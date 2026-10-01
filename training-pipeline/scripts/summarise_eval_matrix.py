#!/usr/bin/env python3
"""Summarise a JMH-Bench eval matrix run: generation yield per config, and scores if benched.

Reads the RUN_DIR produced by ``tools/run_eval.sh`` (bundles/<slug>/generation.json, and
reports/<slug>/summary.md once phase 2 has run) and prints one row per matrix cell.

The project track reports a *compile-check* yield: how many of the 97 SUT classes produced a
benchmark that compiles, split by whether it took the LLM repair loop to get there. First-shot
rate is the honest measure of the model; the post-repair rate is what the bundle actually
contains.

  uv run python scripts/summarise_eval_matrix.py <RUN_DIR> [--json out.json]
"""

from __future__ import annotations

import argparse
import collections
import json
import re
from pathlib import Path


def project_stats(gen: dict) -> dict | None:
    result = gen.get("result") or gen
    classes = result.get("class_generations") or []
    if not classes:
        return None
    attempts: collections.Counter = collections.Counter()
    for entry in classes:
        attempts[entry.get("compile_check_attempts") if entry.get("ok") else "F"] += 1
    total = len(classes)
    compiled = sum(v for k, v in attempts.items() if k != "F")
    return {
        "total": total,
        "first_shot": attempts.get(1, 0),
        "compiled": compiled,
        "never": attempts.get("F", 0),
        "by_attempt": {str(k): v for k, v in sorted(attempts.items(), key=lambda x: str(x[0]))},
    }


def synthetic_stats(gen: dict) -> dict:
    """Synthetic bundles have no compile-check loop -- yield is just 'did the model emit code'."""
    cfg = gen.get("config", {})
    tasks = gen.get("tasks") or []
    generated = sum(1 for t in tasks if (t.get("generated") if isinstance(t, dict) else False))
    return {
        "planned_tasks": cfg.get("planned_tasks"),
        "tasks": len(tasks),
        "generated": generated or len(tasks),
        "track": cfg.get("track"),
    }


_SUMMARY_PATTERNS = {
    "composite": r"\*\*Composite pass\*\* \| \*\*(\d+)%\*\* \| (\d+)/(\d+)",
    "compiles": r"\| Compiles \| (\d+)% \| (\d+)/(\d+)",
    "executes": r"\| Executes \(JMH ran\) \| (\d+)% \| (\d+)/(\d+)",
    "regression": r"\| Regression detection \| (\d+)% \| (\d+)/(\d+)",
}


def summary_scores(report_dir: Path) -> dict:
    """Scores for one matrix cell.

    Project cells write scorecard.json (mutation score, coverage); synthetic cells write
    summary.md with the four rate tables. Read whichever exists rather than guessing from
    the slug -- a half-finished run should still report what it has.
    """
    out: dict = {}

    card = report_dir / "scorecard.json"
    if card.is_file():
        result = json.loads(card.read_text(encoding="utf-8")).get("result", {})
        for key in ("mutation_score", "coverage_rate", "mutants_covered", "mutants_killed",
                    "mutant_count", "n_benchmarks", "classes_compiled", "classes_total",
                    "stability_rsd_percent"):
            if key in result:
                out[key] = result[key]
        covered = out.get("mutants_covered") or 0
        if covered:
            out["conditional_kill"] = out.get("mutants_killed", 0) / covered

    summary = report_dir / "summary.md"
    if summary.is_file():
        text = summary.read_text(encoding="utf-8", errors="replace")
        for key, pattern in _SUMMARY_PATTERNS.items():
            m = re.search(pattern, text)
            if m:
                out[key] = {"pct": int(m.group(1)), "n": int(m.group(2)), "of": int(m.group(3))}
    return out


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("run_dir", type=Path)
    ap.add_argument("--json", type=Path, default=None)
    args = ap.parse_args()

    bundles = sorted((args.run_dir / "bundles").glob("*/generation.json"))
    if not bundles:
        print(f"no bundles under {args.run_dir}/bundles")
        return 1

    rows: dict = {}
    print(f"{'config':<26} {'first-shot':>12} {'after repair':>14} {'never':>6}  scored")
    print("-" * 78)
    for path in bundles:
        slug = path.parent.name
        gen = json.loads(path.read_text(encoding="utf-8"))
        proj = project_stats(gen)
        report_dir = args.run_dir / "reports" / slug
        scores = summary_scores(report_dir) if report_dir.is_dir() else {}
        rows[slug] = {"project": proj, "synthetic": None if proj else synthetic_stats(gen), "scores": scores}

        if "composite" in scores:
            c = scores["composite"]
            score_s = f"composite {c['pct']}% ({c['n']}/{c['of']})"
        elif "mutation_score" in scores:
            ck = scores.get("conditional_kill")
            score_s = (f"mutation {scores['mutation_score']:.0%} "
                       f"({scores.get('mutants_killed')}/{scores.get('mutant_count')})"
                       + (f"  cov {scores.get('coverage_rate', 0):.0%}"
                          f"  cond-kill {ck:.0%}" if ck is not None else ""))
        else:
            score_s = "(not benched)"

        if proj:
            t, f, ok, nv = proj["total"], proj["first_shot"], proj["compiled"], proj["never"]
            print(f"{slug:<26} {f:>4}/{t} {f/t:>5.1%} {ok:>5}/{t} {ok/t:>6.1%} {nv:>6}  {score_s}")
        else:
            syn = rows[slug]["synthetic"]
            gen_s = f"{syn['generated']}/{syn['planned_tasks']} gen"
            print(f"{slug:<26} {gen_s:>12} {'n/a':>14} {'—':>6}  {score_s}")

    if args.json:
        args.json.write_text(json.dumps(rows, indent=2), encoding="utf-8")
        print(f"\nwrote {args.json}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
