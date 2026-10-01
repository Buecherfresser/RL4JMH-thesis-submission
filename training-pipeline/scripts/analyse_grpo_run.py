#!/usr/bin/env python3
"""Summarise a completed GRPO run from its ``metrics/`` directory.

Answers the questions that decide what to change next, rather than just restating the mean
reward: how often each stage of the reward pipeline actually fired, *why* rollouts dropped out
(parse vs compile vs run vs mutation-ineligible), and whether any of it trended over the run.

  uv run python scripts/analyse_grpo_run.py outputs/<run>/metrics [--json out.json]
"""

from __future__ import annotations

import argparse
import csv
import json
import statistics
from collections import Counter
from pathlib import Path
from typing import Any


def _f(value: str | None) -> float | None:
    try:
        return float((value or "").strip())
    except ValueError:
        return None


def load_steps(metrics: Path) -> list[dict[str, str]]:
    path = metrics / "step_metrics.csv"
    return list(csv.DictReader(path.open())) if path.is_file() else []


def load_rollouts(metrics: Path) -> list[dict[str, Any]]:
    path = metrics / "rollouts.jsonl"
    if not path.is_file():
        return []
    out = []
    for line in path.read_text(errors="replace").splitlines():
        line = line.strip()
        if line:
            try:
                out.append(json.loads(line))
            except json.JSONDecodeError:
                continue
    return out


def quartiles(steps: list[dict[str, str]], keys: list[str]) -> list[dict[str, Any]]:
    size = max(len(steps) // 4, 1)
    rows = []
    for start in range(0, len(steps), size):
        chunk = steps[start : start + size]
        row: dict[str, Any] = {"window": f"{start + 1}-{start + len(chunk)}"}
        for key in keys:
            vals = [v for v in (_f(r.get(key)) for r in chunk) if v is not None]
            row[key] = round(sum(vals) / len(vals), 4) if vals else None
        rows.append(row)
    return rows


def rollout_funnel(rollouts: list[dict[str, Any]]) -> dict[str, Any]:
    """Where rollouts drop out. Each stage gates the next, so these are cumulative survivors."""
    total = len(rollouts)
    parsed = [r for r in rollouts if r.get("parse_ok")]
    compiled = [r for r in parsed if r.get("compiled")]
    ran = [r for r in compiled if r.get("ran")]
    scored = [
        r
        for r in ran
        if isinstance(r.get("mutation"), dict) and r["mutation"].get("score") is not None
    ]
    killed = [r for r in scored if (r["mutation"].get("killed") or 0) > 0]

    compile_errs = Counter(
        r.get("compile_error_kind") or "unknown" for r in parsed if not r.get("compiled")
    )
    run_errs = Counter(r.get("run_error_kind") or "unknown" for r in compiled if not r.get("ran"))
    mut_errs = Counter(
        (r.get("mutation") or {}).get("error", "none")
        for r in ran
        if not (isinstance(r.get("mutation"), dict) and r["mutation"].get("score") is not None)
    )
    per_project = Counter(r.get("project", "?") for r in rollouts)
    compiled_by_project = Counter(r.get("project", "?") for r in compiled)
    worst = sorted(
        ((p, compiled_by_project.get(p, 0) / n, n) for p, n in per_project.items() if n >= 20),
        key=lambda t: t[1],
    )

    return {
        "total": total,
        "parse_ok": len(parsed),
        "compiled": len(compiled),
        "ran": len(ran),
        "mutation_scored": len(scored),
        "mutation_killed": len(killed),
        "compile_error_kinds": compile_errs.most_common(6),
        "run_error_kinds": run_errs.most_common(6),
        "mutation_gap_reasons": mut_errs.most_common(6),
        "worst_projects_by_compile_rate": [
            {"project": p, "compile_rate": round(rate, 3), "rollouts": n}
            for p, rate, n in worst[:8]
        ],
        "best_projects_by_compile_rate": [
            {"project": p, "compile_rate": round(rate, 3), "rollouts": n}
            for p, rate, n in worst[-5:][::-1]
        ],
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("metrics", type=Path)
    parser.add_argument("--json", type=Path, default=None)
    args = parser.parse_args()

    steps = load_steps(args.metrics)
    rollouts = load_rollouts(args.metrics)
    keys = [
        "reward",
        "reward_std",
        "grad_norm",
        "compile_frac",
        "run_frac",
        "mutation_eligible_frac",
        "mutation_score_mean",
        "mutation_coverage_rate_mean",
        "mutation_sampled_kill_rate_mean",
    ]
    keys = [k for k in keys if steps and k in steps[0]]

    rewards = [v for v in (_f(r.get("reward")) for r in steps) if v is not None]
    summary: dict[str, Any] = {
        "steps": len(steps),
        "mean_reward": round(statistics.fmean(rewards), 4) if rewards else None,
        "max_reward": round(max(rewards), 4) if rewards else None,
        "steps_with_gradient": sum(1 for r in steps if (_f(r.get("reward_std")) or 0) > 0),
        "quartiles": quartiles(steps, keys),
        "funnel": rollout_funnel(rollouts),
    }

    print(
        f"steps={summary['steps']}  mean_reward={summary['mean_reward']}  "
        f"max={summary['max_reward']}  steps_with_gradient={summary['steps_with_gradient']}"
    )
    print("\n--- quartile trends ---")
    hdr = ["window", *keys]
    print(" ".join(f"{h[:20]:>20}" for h in hdr))
    for row in summary["quartiles"]:
        print(" ".join(f"{str(row.get(h, '-')):>20}" for h in hdr))

    f = summary["funnel"]
    print("\n--- rollout funnel (each stage gates the next) ---")
    total = max(f["total"], 1)
    for stage in ("total", "parse_ok", "compiled", "ran", "mutation_scored", "mutation_killed"):
        print(f"  {stage:>18}: {f[stage]:>6}  ({100 * f[stage] / total:5.1f}%)")
    print(f"\n  compile error kinds : {f['compile_error_kinds']}")
    print(f"  run error kinds     : {f['run_error_kinds']}")
    print(f"  mutation gap reasons: {f['mutation_gap_reasons']}")
    print("\n  worst projects by compile rate:")
    for row in f["worst_projects_by_compile_rate"]:
        print(f"    {row['project']:<28} {row['compile_rate']:.3f}  (n={row['rollouts']})")
    print("  best projects by compile rate:")
    for row in f["best_projects_by_compile_rate"]:
        print(f"    {row['project']:<28} {row['compile_rate']:.3f}  (n={row['rollouts']})")

    if args.json:
        args.json.write_text(json.dumps(summary, indent=2), encoding="utf-8")
        print(f"\nwrote {args.json}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
