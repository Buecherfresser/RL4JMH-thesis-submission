#!/usr/bin/env python3
"""Score a GRPO config's held-out prompts against a served model, base vs checkpoint.

This is the out-of-sample half of a GRPO run. Everything runs 1-8 report is in-sample -- the
reward at step N is measured on the prompts being trained on at step N -- so none of it separates
"learned to write JMH benchmarks" from "memorised these subjects". This script scores the
``holdout_prompts`` that :func:`jmhgen.training.grpo.split_holdout` withheld, so the same number
can be taken from the base model and from a checkpoint and subtracted.

It deliberately does NOT use TRL's eval loop. That path was measured on run 9 (2026-08-11) at
~22 s/rollout against training's 3.2 s/rollout, and it parse-failed 28 of 32 completions where
training on the identical corpus and weights parse-failed 0 of 16 -- it measures the eval path,
not the policy. Here, generation goes through the same ``VLLMChatClient`` the RFT/JMH-Bench paths
use and scoring goes through the same ``build_grpo_reward_fn`` the trainer uses, so a delta against
the training curve is meaningful.

The split is recomputed from the config rather than read from ``holdout_prompts.json`` so this
works on a run that has not started yet; pass --holdout-json to pin it to what a run actually used
(and the script fails loudly if the two disagree).

Serve a checkpoint first, exactly as scripts/cluster/eval-run8.sh does::

    vllm serve google/gemma-4-E2B-it --enable-lora \
        --lora-modules run9=outputs/grpo-mutation70-gemma-rtx-run9/checkpoint-1600 \
        --max-lora-rank 16 --max-model-len 65536 --gpu-memory-utilization 0.85 --port 8032

    # baseline: the base model is served under its own name by the same server
    uv run python scripts/score_holdout.py configs/grpo/mutation70-gemma-rtx-run9.yaml \
        --model google/gemma-4-E2B-it --label base   --out artifacts/run9-holdout

    uv run python scripts/score_holdout.py configs/grpo/mutation70-gemma-rtx-run9.yaml \
        --model run9 --label ck1600 --out artifacts/run9-holdout
"""

from __future__ import annotations

import argparse
import json
import statistics
import sys
import time
from pathlib import Path
from typing import Any

ROOT = Path(__file__).resolve().parents[1]
if str(ROOT / "src") not in sys.path:
    sys.path.insert(0, str(ROOT / "src"))

import yaml  # noqa: E402

from jmhgen.config.schema import GRPOConfig  # noqa: E402
from jmhgen.generate.vllm_client import SamplingParams, VLLMChatClient  # noqa: E402
from jmhgen.rewards.grpo_adapter import build_grpo_reward_fn  # noqa: E402
from jmhgen.training.grpo import build_prompt_records, split_holdout  # noqa: E402
from jmhgen.utils.logging import get_logger  # noqa: E402

logger = get_logger("jmhgen.score_holdout")

_COMPONENTS = ("compile", "runtime", "anti_pattern", "rsd", "mutation")


class _Collector:
    """Minimal stand-in for ``GrpoMetricsCollector``: keeps the per-rollout scalars in memory.

    ``build_grpo_reward_fn`` only ever calls ``record_rollout`` and ``finish_reward_batch`` on the
    object it is handed, so implementing those two is enough to capture the funnel (parsed ->
    compiled -> ran -> mutation-eligible) that makes a bare mean reward interpretable.
    """

    def __init__(self) -> None:
        self.rollouts: list[dict[str, Any]] = []

    def record_rollout(self, **event: Any) -> None:
        components = event.get("reward_components") or {}
        flat = {
            name: (components.get(name) or {}).get("value")
            for name in _COMPONENTS
            if isinstance(components.get(name), dict)
        }
        detail = (components.get("mutation") or {}).get("detail")
        self.rollouts.append(
            {
                "project": event.get("project"),
                "snippet_id": event.get("snippet_id"),
                "parse_ok": bool(event.get("parse_ok")),
                "compiled": bool(event.get("compiled")),
                "ran": bool(event.get("ran")),
                "reward": float(event.get("reward") or 0.0),
                "duration_s": float(event.get("duration_s") or 0.0),
                "components": flat,
                "mutation_score": (detail or {}).get("score")
                if isinstance(detail, dict)
                else None,
            }
        )

    def finish_reward_batch(self, count: int) -> None:  # noqa: D401 - protocol method
        return None


def _mean(values: list[float]) -> float | None:
    return statistics.fmean(values) if values else None


def main() -> None:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("config", type=Path, help="the GRPO config whose holdout should be scored")
    ap.add_argument("--model", required=True, help="served model or --lora-modules name")
    ap.add_argument("--label", required=True, help="name for this arm, e.g. base / ck1600")
    ap.add_argument("--base-url", default="http://127.0.0.1:8032/v1")
    ap.add_argument("--api-key", default="EMPTY")
    ap.add_argument("--out", type=Path, default=None, help="directory for the JSON report")
    ap.add_argument(
        "--group-size",
        type=int,
        default=None,
        help="completions per prompt (default: the config's group_size, so the number is "
        "directly comparable to the training reward)",
    )
    ap.add_argument("--limit", type=int, default=0, help="score only the first N held-out prompts")
    ap.add_argument("--max-concurrency", type=int, default=16)
    ap.add_argument("--timeout-s", type=float, default=1800.0)
    ap.add_argument(
        "--holdout-json",
        type=Path,
        default=None,
        help="a run's holdout_prompts.json; verifies the recomputed split matches what it trained on",
    )
    args = ap.parse_args()

    config = GRPOConfig.model_validate(yaml.safe_load(args.config.read_text(encoding="utf-8")))
    if config.holdout_prompts <= 0:
        raise SystemExit(f"[holdout] {args.config} sets holdout_prompts: 0 — nothing was withheld")

    records = build_prompt_records(config)
    _, holdout = split_holdout(records, config)

    if args.holdout_json is not None:
        pinned = json.loads(args.holdout_json.read_text(encoding="utf-8"))["snippet_ids"]
        got = [r["snippet_id"] for r in holdout]
        if pinned != got:
            raise SystemExit(
                "[holdout] the split recomputed from the config does not match "
                f"{args.holdout_json}: {len(set(pinned) ^ set(got))} id(s) differ. The corpus or "
                "the filters changed since the run; score against the pinned ids instead."
            )
        logger.info("split verified against %s (%d ids)", args.holdout_json, len(pinned))

    if args.limit > 0:
        holdout = holdout[: args.limit]
    group_size = args.group_size or config.group_size

    client = VLLMChatClient(
        base_url=args.base_url,
        model=args.model,
        api_key=args.api_key,
        timeout_s=args.timeout_s,
        max_concurrency=args.max_concurrency,
    )
    # Identical to the rollout sampling params, so this measures the same policy the reward saw.
    params = SamplingParams(
        n=group_size,
        temperature=config.rollout_temperature,
        top_p=config.top_p,
        top_k=config.top_k,
        max_tokens=config.max_completion_length,
        chat_template_kwargs=dict(config.chat_template_kwargs),
    )
    logger.info(
        "scoring %d held-out prompt(s) x G=%d from %s as %r",
        len(holdout),
        group_size,
        args.base_url,
        args.model,
    )

    t0 = time.time()
    generations = client.complete_many([r["prompt"] for r in holdout], params)
    gen_s = time.time() - t0

    collector = _Collector()
    reward_fn = build_grpo_reward_fn(config, metrics=collector)

    # Flatten to the (prompts, completions, **columns) contract TRL uses: G completions per prompt,
    # with every other dataset column repeated alongside.
    prompts: list[Any] = []
    completions: list[Any] = []
    columns: dict[str, list[Any]] = {k: [] for k in holdout[0] if k != "prompt"}
    for record, group in zip(holdout, generations, strict=True):
        texts = [c.content for c in group] or [""]
        for text in texts:
            prompts.append(record["prompt"])
            completions.append(text)
            for key in columns:
                columns[key].append(record[key])

    t1 = time.time()
    rewards = reward_fn(prompts, completions, **columns)
    reward_s = time.time() - t1

    rolls = collector.rollouts
    n = len(rolls) if (rolls := rolls) else 0
    eligible = [r["mutation_score"] for r in rolls if isinstance(r["mutation_score"], (int, float))]
    per_prompt: dict[str, list[float]] = {}
    for r in rolls:
        per_prompt.setdefault(str(r["snippet_id"]), []).append(r["reward"])

    report = {
        "label": args.label,
        "model": args.model,
        "config": str(args.config),
        "holdout_seed": config.holdout_seed,
        "n_prompts": len(holdout),
        "n_projects": len({r["project"] for r in holdout}),
        "group_size": group_size,
        "n_rollouts": n,
        "reward_mean": _mean([float(x) for x in rewards]),
        "reward_mean_per_prompt": _mean([statistics.fmean(v) for v in per_prompt.values()]),
        "parse_frac": (sum(r["parse_ok"] for r in rolls) / n) if n else None,
        "compile_frac": (sum(r["compiled"] for r in rolls) / n) if n else None,
        "run_frac": (sum(r["ran"] for r in rolls) / n) if n else None,
        "mutation_eligible_frac": (len(eligible) / n) if n else None,
        "mutation_score_mean": _mean([float(x) for x in eligible]),
        "component_means": {
            name: _mean(
                [
                    float(r["components"][name])
                    for r in rolls
                    if isinstance(r["components"].get(name), (int, float))
                ]
            )
            for name in _COMPONENTS
        },
        "generation_seconds": round(gen_s, 1),
        "reward_seconds": round(reward_s, 1),
    }

    print(json.dumps(report, indent=2, sort_keys=True))
    if args.out is not None:
        args.out.mkdir(parents=True, exist_ok=True)
        (args.out / f"holdout-{args.label}.json").write_text(
            json.dumps(report, indent=2, sort_keys=True) + "\n", encoding="utf-8"
        )
        (args.out / f"holdout-{args.label}-rollouts.jsonl").write_text(
            "".join(json.dumps(r, sort_keys=True) + "\n" for r in rolls), encoding="utf-8"
        )
        logger.info("wrote %s", args.out / f"holdout-{args.label}.json")


if __name__ == "__main__":
    main()
