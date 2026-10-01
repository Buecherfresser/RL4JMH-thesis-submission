"""Rejection fine-tuning (RFT) stage.

Offline, reward-filtered self-training (a.k.a. STaR / RFT / ReST^EM): sample candidate
benchmarks from the base policy, keep the verified ones, and SFT on them. The accepted set is
**self-generated, verified, and keeps the verbatim thinking trace**, which is the fix for the
SFT regression (that run was trained on answer-only targets and unlearned good reasoning).

Four cached phases, each re-runnable independently (delete an artifact under ``work_dir`` to
force a rebuild):

  1. generate — sample ``samples_per_prompt`` candidates per snippet from the vLLM server
     (thinking on), parse out the Java source; write ``generations.jsonl``.
  2. verify   — compile/run each candidate through a ``RunnerClient`` against the SUT
     classpath, score with the composite reward, and gate on conformance; write
     ``scored.jsonl``.
  3. build    — per prompt, dedup + cap accepted completions and drop empty prompts; write the
     TRL ``messages`` dataset (``sft.jsonl`` / ``sft.val.jsonl``) with the verbatim trace as
     the assistant turn.
  4. train    — SFT on the accepted set, always from ``base_model`` (ReST^EM), reusing the SFT
     trainer so the per-stage recipe matches.

The reward step is exactly where a ``RemoteRunnerClient`` would later offload evaluation onto
a CPU fleet; nothing in this loop depends on the runner being local.
"""

from __future__ import annotations

import json
import os
import random
import re
from collections import defaultdict
from collections.abc import Iterator
from pathlib import Path
from typing import Any

from jmhgen.config.schema import RFTConfig, SFTConfig
from jmhgen.data.classpath import load_classpath
from jmhgen.data.conformance import violations
from jmhgen.data.prompts import render_prompt_messages
from jmhgen.data.schema import BenchmarkSample, CodeSnippet, load_snippets
from jmhgen.generate.parse import parse_completion
from jmhgen.generate.vllm_client import SamplingParams, VLLMChatClient
from jmhgen.mutation import MutantSpec, load_mutants, mutants_by_fqcn, score_benchmark_mutations
from jmhgen.rewards import build_composite_reward
from jmhgen.rewards.grpo_adapter import build_benchmark_spec
from jmhgen.runner.base import JmhRunner
from jmhgen.runner.client import LocalRunnerClient, RewardRequest
from jmhgen.runner.types import BenchmarkSpec, EvaluationResult, JmhOptions
from jmhgen.training.base import prepare_output_dir, set_seed
from jmhgen.utils.logging import get_logger

logger = get_logger("jmhgen.training.rft")

_COMMENT_RE = re.compile(r"//[^\n]*|/\*.*?\*/", re.DOTALL)
_COMPILE_LOG_TAIL_CHARS = 2000


def _compile_log_tail(evaluation: EvaluationResult) -> str:
    """Last N chars of Maven compile stdout+stderr for scored.jsonl diagnostics."""
    text = (evaluation.compile.stdout or "") + (evaluation.compile.stderr or "")
    if len(text) <= _COMPILE_LOG_TAIL_CHARS:
        return text
    return text[-_COMPILE_LOG_TAIL_CHARS:]


# -- small IO helpers -----------------------------------------------------------------------


def _read_jsonl(path: Path) -> Iterator[dict[str, Any]]:
    with path.open(encoding="utf-8") as handle:
        for line in handle:
            line = line.strip()
            if line:
                yield json.loads(line)


def _write_jsonl(path: Path, rows: list[dict[str, Any]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8") as handle:
        for row in rows:
            handle.write(json.dumps(row, ensure_ascii=False))
            handle.write("\n")


def _exists_nonempty(path: Path) -> bool:
    return path.exists() and path.stat().st_size > 0


def _simple_name(fqcn: str) -> str:
    return fqcn.rsplit(".", 1)[-1]


def _normalize_java(source: str) -> str:
    """Whitespace/comment-insensitive key for de-duplicating distinct solutions."""
    return re.sub(r"\s+", "", _COMMENT_RE.sub("", source))


def _assistant_text(content: str, reasoning: str | None) -> str:
    """The verbatim training target: thinking + answer.

    With no reasoning parser, ``content`` already carries the inline thinking. When the server
    split it out, we re-join reasoning + content so the thinking is not lost from the target.
    """
    if reasoning:
        return f"{reasoning}\n{content}"
    return content


def _artifacts(config: RFTConfig) -> dict[str, Path]:
    work = Path(config.work_dir)
    return {
        "generations": work / "generations.jsonl",
        "scored": work / "scored.jsonl",
        "dataset": work / "sft.jsonl",
        "val": work / "sft.val.jsonl",
        "report": work / "report.json",
    }


# -- phase 1: generate ----------------------------------------------------------------------


def phase_generate(config: RFTConfig) -> Path:
    """Sample candidates from the policy and parse them into Java sources."""
    paths = _artifacts(config)
    gen_path = paths["generations"]
    if _exists_nonempty(gen_path):
        logger.info("using cached generations at %s", gen_path)
        return gen_path

    snippets = list(load_snippets(config.dataset_path))
    if config.max_source_chars > 0:
        kept_snippets = [s for s in snippets if len(s.source) <= config.max_source_chars]
        skipped = len(snippets) - len(kept_snippets)
        if skipped:
            too_big = sorted(
                (s for s in snippets if len(s.source) > config.max_source_chars),
                key=lambda s: len(s.source),
                reverse=True,
            )
            sample_ids = ", ".join(f"{s.id} ({len(s.source)} chars)" for s in too_big[:5])
            logger.warning(
                "skipping %d/%d snippet(s) over max_source_chars=%d (won't fit the context "
                "window): %s%s",
                skipped,
                len(snippets),
                config.max_source_chars,
                sample_ids,
                "" if skipped <= 5 else ", ...",
            )
        snippets = kept_snippets
    if config.limit_prompts > 0:
        snippets = snippets[: config.limit_prompts]
    if not snippets:
        raise SystemExit(f"[RFT] no snippets found in {config.dataset_path}")

    inf = config.inference
    api_key = os.environ.get(inf.api_key_env) or None
    client = VLLMChatClient(
        base_url=inf.base_url,
        model=inf.model,
        api_key=api_key,
        timeout_s=inf.timeout_s,
        max_retries=inf.max_retries,
        retry_backoff_s=inf.retry_backoff_s,
        max_concurrency=inf.max_concurrency,
    )
    params = SamplingParams(
        n=config.samples_per_prompt,
        temperature=config.sampling_temperature,
        top_p=inf.top_p,
        top_k=inf.top_k,
        max_tokens=inf.max_tokens,
        enable_thinking=inf.enable_thinking,
        chat_template_kwargs=dict(inf.chat_template_kwargs),
        extra_body=dict(inf.extra_body),
    )

    prompts = [render_prompt_messages(s, "jmhbench") for s in snippets]
    logger.info(
        "sampling %d prompt(s) x n=%d at T=%.2f from %s (max_concurrency=%d)",
        len(prompts),
        params.n,
        params.temperature,
        inf.base_url,
        inf.max_concurrency,
    )
    results = client.complete_many(prompts, params)

    rows: list[dict[str, Any]] = []
    warned_reasoning = False
    for snippet, completions in zip(snippets, results, strict=True):
        fallback_class = f"{_simple_name(snippet.id)}Benchmark"
        fallback_pkg = str(snippet.metadata.get("package") or "")
        for idx, comp in enumerate(completions):
            if comp.reasoning and not warned_reasoning:
                logger.warning(
                    "server returned separated reasoning_content; re-joining reasoning + "
                    "content for the training target. Disable the reasoning parser to keep "
                    "the verbatim thinking inline."
                )
                warned_reasoning = True
            parsed = parse_completion(
                comp.content,
                reasoning_content=comp.reasoning,
                fallback_package=fallback_pkg,
                fallback_class=fallback_class,
            )
            rows.append(
                {
                    "snippet_id": snippet.id,
                    "project": snippet.project,
                    "sample_index": idx,
                    "assistant_text": _assistant_text(comp.content, comp.reasoning),
                    "reasoning": comp.reasoning,
                    "finish_reason": comp.finish_reason,
                    "parse_ok": parsed is not None,
                    "java_source": parsed.java_source if parsed else None,
                    "package": parsed.package if parsed else None,
                    "class_name": parsed.class_name if parsed else None,
                }
            )

    _write_jsonl(gen_path, rows)
    parsed_ok = sum(1 for r in rows if r["parse_ok"])
    logger.info("wrote %d generation(s) (%d parsed) to %s", len(rows), parsed_ok, gen_path)
    return gen_path


# -- phase 2: verify ------------------------------------------------------------------------


def phase_verify(config: RFTConfig) -> Path:
    """Compile/run each parsed candidate and score + gate it on conformance."""
    paths = _artifacts(config)
    scored_path = paths["scored"]
    if _exists_nonempty(scored_path):
        logger.info("using cached scored candidates at %s", scored_path)
        return scored_path
    if not _exists_nonempty(paths["generations"]):
        raise SystemExit("[RFT] no generations.jsonl; run the generate phase first")

    classpaths: dict[str, tuple[str, ...]] = {}
    missing_classpaths: list[str] = []
    for project, cp_file in config.project_classpaths.items():
        path = Path(cp_file)
        if not path.is_file():
            missing_classpaths.append(f"{project} -> {cp_file}")
            continue
        classpaths[project] = load_classpath(path)
        logger.info("loaded %d classpath entries for project %s", len(classpaths[project]), project)
    if missing_classpaths:
        preview = "\n  - ".join(missing_classpaths[:20])
        extra = (
            f"\n  - ... and {len(missing_classpaths) - 20} more"
            if len(missing_classpaths) > 20
            else ""
        )
        logger.warning(
            "\n"
            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n"
            "WARNING: skipping %d missing project classpath file(s).\n"
            "Candidates for those projects will compile without SUT jars (likely rejected).\n"
            "Provision with jmh-provision-classpath / sync data/classpaths/ before full RFT.\n"
            "  - %s%s\n"
            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!",
            len(missing_classpaths),
            preview,
            extra,
        )

    runner = config.runner.build_runner()
    if not runner.is_available():
        raise SystemExit(
            f"[RFT] verification needs '{runner.mvn_executable}' and "
            f"'{runner.java_executable}' on PATH"
        )
    client = LocalRunnerClient(runner)
    options = config.runner.jmh.to_options()
    reward_fn = build_composite_reward(
        config.reward_weights,
        rsd_good=config.rsd_good,
        rsd_bad=config.rsd_bad,
    )
    mutants_by_project = _load_project_mutants(config)

    out: list[dict[str, Any]] = []
    rows = list(_read_jsonl(paths["generations"]))
    for i, row in enumerate(rows):
        if not row.get("parse_ok"):
            out.append(
                {
                    **row,
                    "compiled": False,
                    "ran": False,
                    "composite_reward": 0.0,
                    "robust_rsd": None,
                    "conformance_ok": False,
                    "conformance_reasons": ["parse_failed"],
                    "accepted": False,
                }
            )
            continue

        cp = classpaths.get(row.get("project") or "", ())
        pkg = str(row.get("package") or "")
        spec = build_benchmark_spec(row["java_source"], row["class_name"], pkg, cp)
        request = RewardRequest(
            spec=spec,
            options=options,
            need_run=config.need_run,
            request_id=f"{row['snippet_id']}#{row['sample_index']}",
        )
        evaluation = client.evaluate(request).evaluation
        _score_mutations_into(config, runner, spec, options, evaluation, row, mutants_by_project)
        reward = reward_fn(evaluation)
        reward_components = reward.detail.get("components", {})

        sample = BenchmarkSample(
            snippet=CodeSnippet(id=row["snippet_id"], source="", project=row.get("project")),
            benchmark_source=row["java_source"],
        )
        reasons = violations(sample)
        conformance_ok = not reasons
        accepted = (reward.value >= config.accept_threshold) and conformance_ok

        out.append(
            {
                **row,
                "compiled": evaluation.compiled,
                "ran": evaluation.ran,
                "composite_reward": reward.value,
                "reward_components": {
                    name: info.get("value") for name, info in reward_components.items()
                },
                "anti_pattern_source": (
                    (reward_components.get("anti_pattern") or {}).get("detail") or {}
                ).get("source"),
                "robust_rsd": _best_rsd(evaluation),
                "mutation": evaluation.metadata.get("mutation"),
                "compile_error_kind": evaluation.compile.error_kind.value,
                "compile_log_tail": _compile_log_tail(evaluation),
                "run_error_kind": evaluation.run.error_kind.value if evaluation.run else None,
                "conformance_ok": conformance_ok,
                "conformance_reasons": reasons,
                "accepted": accepted,
            }
        )
        if hasattr(runner, "cleanup"):
            runner.cleanup(spec)
        if (i + 1) % 50 == 0:
            logger.info("verified %d/%d candidates", i + 1, len(rows))

    _write_jsonl(scored_path, out)
    n_acc = sum(1 for r in out if r["accepted"])
    logger.info(
        "scored %d candidate(s): %d compiled, %d accepted (%.1f%%) -> %s",
        len(out),
        sum(1 for r in out if r["compiled"]),
        n_acc,
        100.0 * n_acc / max(1, len(out)),
        scored_path,
    )
    return scored_path


def _best_rsd(evaluation: Any) -> float | None:
    if evaluation.run is None:
        return None
    rsds = [s.robust_rsd for s in evaluation.run.stats if s.robust_rsd is not None]
    return min(rsds) if rsds else None


def _load_project_mutants(config: RFTConfig) -> dict[str, dict[str, list[MutantSpec]]]:
    """Load each project's mutant registry, grouped by subject class (only if mutation is on)."""
    if config.reward_weights.mutation <= 0:
        return {}
    by_project: dict[str, dict[str, list[MutantSpec]]] = {}
    for project, registry_path in config.project_mutants.items():
        grouped = mutants_by_fqcn(load_mutants(registry_path))
        by_project[project] = grouped
        logger.info(
            "loaded %d mutant(s) across %d class(es) for project %s",
            sum(len(v) for v in grouped.values()),
            len(grouped),
            project,
        )
    return by_project


def _score_mutations_into(
    config: RFTConfig,
    runner: JmhRunner,
    spec: BenchmarkSpec,
    options: JmhOptions,
    evaluation: EvaluationResult,
    row: dict[str, Any],
    mutants_by_project: dict[str, dict[str, list[MutantSpec]]],
) -> None:
    """Compute the mutation score for a runnable candidate and stash it in the metadata.

    No-op unless the mutation weight is on, the candidate actually ran, and the registry has
    mutants planted in this candidate's subject class. The score is read back by
    :class:`~jmhgen.rewards.mutation.MutationReward` during composite scoring.
    """
    if config.reward_weights.mutation <= 0 or not evaluation.ran or evaluation.run is None:
        return
    per_class = mutants_by_project.get(row.get("project") or "")
    if not per_class:
        return
    class_mutants = per_class.get(row["snippet_id"])
    if not class_mutants:
        return
    mutation_score = score_benchmark_mutations(
        runner,
        spec,
        options,
        evaluation.run.stats,
        class_mutants,
        alpha=config.mutation.alpha,
        min_slowdown=config.mutation.min_slowdown,
        arm_property=config.mutation.arm_property,
        max_mutants=config.mutation.max_mutants_per_candidate,
        mode=config.mutation.mode,
        coverage_guidance=config.mutation.coverage_guidance,
        coverage_options=config.mutation.coverage_jmh.to_options(),
        record_property=config.mutation.record_property,
        record_file_property=config.mutation.record_file_property,
        max_benchmarks_per_mutant=config.mutation.max_benchmarks_per_mutant,
        coverage_weight=config.mutation.coverage_weight,
    )
    evaluation.metadata["mutation"] = mutation_score.to_dict()


# -- phase 3: build dataset -----------------------------------------------------------------


def phase_build(config: RFTConfig) -> int:
    """Filter accepted candidates into a TRL messages dataset. Returns the train-sample count."""
    paths = _artifacts(config)
    if not _exists_nonempty(paths["scored"]):
        raise SystemExit("[RFT] no scored.jsonl; run the verify phase first")

    scored = list(_read_jsonl(paths["scored"]))
    snippets = {s.id: s for s in load_snippets(config.dataset_path)}

    groups: dict[str, list[dict[str, Any]]] = defaultdict(list)
    for row in scored:
        groups[row["snippet_id"]].append(row)

    kept: list[tuple[str, CodeSnippet, str]] = []  # (snippet_id, snippet, assistant_text)
    prompts_with_data = 0
    dropped_all_pass = 0
    for sid, rows in groups.items():
        accepted = [r for r in rows if r.get("accepted")]
        if not accepted:
            continue
        if config.drop_all_pass and len(accepted) == len(rows):
            dropped_all_pass += 1
            continue
        if config.dedup:
            seen: set[str] = set()
            deduped: list[dict[str, Any]] = []
            for r in accepted:
                key = _normalize_java(r["java_source"])
                if key not in seen:
                    seen.add(key)
                    deduped.append(r)
            accepted = deduped
        accepted = accepted[: config.max_keep_per_prompt]
        snippet = snippets.get(sid)
        if snippet is None:
            logger.warning("scored snippet %s not found in %s; skipping", sid, config.dataset_path)
            continue
        prompts_with_data += 1
        for r in accepted:
            kept.append((sid, snippet, r["assistant_text"]))

    train, val = _split_by_prompt(kept, config.val_fraction, config.seed)
    _write_jsonl(paths["dataset"], [_to_messages_row(s, text) for _sid, s, text in train])
    if val:
        _write_jsonl(paths["val"], [_to_messages_row(s, text) for _sid, s, text in val])
    elif paths["val"].exists():
        paths["val"].unlink()

    accepted_total = sum(1 for r in scored if r.get("accepted"))
    acceptance_rate = round(accepted_total / max(1, len(scored)), 4)
    report: dict[str, Any] = {
        "snippets_total": len(snippets),
        "prompts_sampled": len(groups),
        "candidates_total": len(scored),
        "candidates_parsed": sum(1 for r in scored if r.get("parse_ok")),
        "candidates_compiled": sum(1 for r in scored if r.get("compiled")),
        "candidates_ran": sum(1 for r in scored if r.get("ran")),
        "candidates_accepted": accepted_total,
        "acceptance_rate": acceptance_rate,
        "prompts_with_accepted": prompts_with_data,
        "prompts_dropped_all_pass": dropped_all_pass,
        "max_keep_per_prompt": config.max_keep_per_prompt,
        "dedup": config.dedup,
        "train_samples": len(train),
        "val_samples": len(val),
        "inspection": [
            {"snippet_id": sid, "assistant_text_head": text[:600]} for sid, _s, text in kept[:3]
        ],
    }
    paths["report"].write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    logger.info(
        "built RFT dataset: %d train / %d val from %d/%d prompts (acceptance %.1f%%) -> %s",
        len(train),
        len(val),
        prompts_with_data,
        len(groups),
        100.0 * acceptance_rate,
        paths["dataset"],
    )
    return len(train)


def _to_messages_row(snippet: CodeSnippet, assistant_text: str) -> dict[str, Any]:
    messages = render_prompt_messages(snippet, "jmhbench")
    messages.append({"role": "assistant", "content": assistant_text})
    return {"messages": messages}


def _split_by_prompt(
    kept: list[tuple[str, CodeSnippet, str]], val_fraction: float, seed: int
) -> tuple[list[tuple[str, CodeSnippet, str]], list[tuple[str, CodeSnippet, str]]]:
    """Hold out a fraction of *prompts* (not rows) so no prompt straddles train/val."""
    if val_fraction <= 0 or len(kept) < 2:
        return kept, []
    prompt_ids = list(dict.fromkeys(sid for sid, _s, _t in kept))
    if len(prompt_ids) < 2:
        return kept, []
    random.Random(seed).shuffle(prompt_ids)
    n_val = max(1, int(round(len(prompt_ids) * val_fraction)))
    val_ids = set(prompt_ids[:n_val])
    train = [item for item in kept if item[0] not in val_ids]
    val = [item for item in kept if item[0] in val_ids]
    return train, val


# -- phase 4: train -------------------------------------------------------------------------


def _to_sft_config(config: RFTConfig) -> SFTConfig:
    """Project the RFT config onto an SFTConfig for the train step (always from base_model)."""
    return SFTConfig(
        base_model=config.base_model,
        output_dir=config.output_dir,
        dataset_path=str(_artifacts(config)["dataset"]),
        seed=config.seed,
        max_seq_len=config.max_seq_len,
        learning_rate=config.learning_rate,
        per_device_batch_size=config.per_device_batch_size,
        gradient_accumulation_steps=config.gradient_accumulation_steps,
        finetune_mode=config.finetune_mode,
        precision=config.precision,
        gradient_checkpointing=config.gradient_checkpointing,
        attn_implementation=config.attn_implementation,
        loss_type=config.loss_type,
        activation_offloading=config.activation_offloading,
        lora=config.lora,
        hf_token_env=config.hf_token_env,
        trust_remote_code=config.trust_remote_code,
        num_epochs=config.num_epochs,
        packing=config.packing,
        completion_only_loss=config.completion_only_loss,
        warmup_ratio=config.warmup_ratio,
        logging_steps=config.logging_steps,
        report_to=config.report_to,
    )


def phase_train(config: RFTConfig) -> None:
    from jmhgen.training.sft import train as sft_train

    logger.info("training on accepted RFT set from base model %s", config.base_model)
    sft_train(_to_sft_config(config))


def train(config: RFTConfig) -> None:
    """Run rejection fine-tuning end to end (sample -> verify -> filter -> SFT)."""
    phase_generate(config)
    phase_verify(config)
    n_train = phase_build(config)
    if n_train == 0:
        raise SystemExit(
            "[RFT] 0 accepted training samples. Inspect data/rft/report.json: check the "
            "vLLM thinking toggle, the SUT classpaths (project_classpaths), and the accept "
            "threshold / reward weights."
        )
    phase_train(config)


def main() -> None:
    import argparse

    from jmhgen.config.mutation_manifest import load_mutation_manifest

    parser = argparse.ArgumentParser(description="Run the RFT training stage.")
    group = parser.add_mutually_exclusive_group()
    group.add_argument(
        "--config", default="configs/rft/default.yaml", help="Path to a single YAML config."
    )
    group.add_argument(
        "--manifest",
        default=None,
        help="Unified mutation manifest — runs every project sequentially in one job.",
    )
    parser.add_argument(
        "--bsc-gcp",
        action="store_true",
        help="With --manifest, use each project's bsc_gcp_config instead of config.",
    )
    parser.add_argument(
        "--max-concurrency",
        "--max-parallel-requests",
        type=int,
        default=None,
        metavar="N",
        dest="max_concurrency",
        help=(
            "Override inference.max_concurrency: max parallel vLLM chat/completions requests "
            "during the generate phase (also settable as inference.max_concurrency or "
            "inference.max_parallel_requests in the YAML)."
        ),
    )
    args = parser.parse_args()

    def _apply_concurrency(config: RFTConfig) -> RFTConfig:
        if args.max_concurrency is None:
            return config
        if args.max_concurrency < 1:
            raise SystemExit("--max-concurrency must be >= 1")
        return config.model_copy(
            update={
                "inference": config.inference.model_copy(
                    update={"max_concurrency": args.max_concurrency}
                )
            }
        )

    if args.manifest:
        from jmhgen.training.manifest_job import run_manifest as run_manifest_job

        manifest = load_mutation_manifest(args.manifest)
        if args.max_concurrency is not None:
            logger.warning(
                "[RFT] --max-concurrency is not yet applied to cross-project manifest runs; "
                "set inference.max_concurrency in each bsc_gcp_config instead."
            )
        try:
            run_manifest_job(
                manifest,
                ("generate", "verify", "build", "train"),
                bsc_gcp=args.bsc_gcp,
                order="cross-project",
            )
        except NotImplementedError as exc:
            raise SystemExit(f"[RFT] not implemented yet: {exc}") from None
        return

    config = _apply_concurrency(RFTConfig.from_yaml(args.config))
    set_seed(config.seed)
    output_dir = prepare_output_dir(config.output_dir)
    logger.info("[RFT] loaded config from %s (output_dir=%s)", args.config, output_dir)

    try:
        train(config)
    except NotImplementedError as exc:
        raise SystemExit(f"[RFT] not implemented yet: {exc}") from None


if __name__ == "__main__":
    main()
