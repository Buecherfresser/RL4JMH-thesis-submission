#!/usr/bin/env python3
"""Apply the timeout-as-kill policy to already-measured scorecards.

Policy (decided 2026-08-27): an armed run that exceeds its wall-clock budget
counts as a **kill**, but only when that benchmark's baseline did not also time
out. With a completed baseline the armed run blew ``5 x baseline_wall + 60s``,
which is a >5x slowdown against a 1.10 kill threshold; without one there is no
evidence the mutant caused the timeout rather than the benchmark simply being
slower than the cap.

`jmhbench/project_bench.py` now implements this at measurement time. This script
applies the same rule to scorecards produced *before* that change, so the
campaign does not have to be re-run to adopt it.

The baseline condition is recoverable from the stored data: the harness writes
the detection error as ``armed run exceeded its Ns budget (baseline Ms)`` when
the baseline wall time was known, and omits the parenthetical when it was not.

Prints old and new side by side and never overwrites in place; pass --write to
emit <scorecard>.rescored.json next to each input.
"""
from __future__ import annotations

import argparse
import copy
import glob
import json
import os
import sys

BASELINE_KNOWN = "(baseline "


def timeout_attributable(det: dict) -> bool:
    """Did this detection time out with a baseline that did not?"""
    return bool(det.get("timed_out")) and BASELINE_KNOWN in (det.get("error") or "")


def rescore(result: dict) -> dict:
    """Recompute kill/cover counts under the policy. Returns a summary dict."""
    mr = result.get("mutant_results") or []
    promoted = []
    for m in mr:
        if m.get("status") != "error":
            continue
        dets = m.get("detections") or []
        if any(timeout_attributable(d) for d in dets):
            m["status"] = "killed"
            m["killed_by_timeout"] = True
            promoted.append(m.get("id"))

    total = result.get("mutant_count") or 1
    killed = sum(1 for m in mr if m.get("status") == "killed")
    covered = sum(1 for m in mr if m.get("status") in ("killed", "covered_not_killed"))
    result["mutants_killed"] = killed
    result["mutants_covered"] = covered
    result["mutants_errored"] = sum(1 for m in mr if m.get("status") == "error")
    result["mutants_killed_by_timeout"] = len(promoted)
    result["mutation_score"] = round(killed / total, 4)
    result["coverage_rate"] = round(covered / total, 4)
    return {"promoted": promoted, "killed": killed, "covered": covered,
            "score": result["mutation_score"]}


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("paths", nargs="+", help="scorecard.json files or globs")
    ap.add_argument("--write", action="store_true",
                    help="write <name>.rescored.json beside each input")
    args = ap.parse_args()

    files: list[str] = []
    for p in args.paths:
        files.extend(sorted(glob.glob(p)) or ([p] if os.path.exists(p) else []))
    if not files:
        print("no scorecards matched", file=sys.stderr)
        return 1

    print(f"{'cell':38s} {'old':>6s} {'new':>6s} {'promoted':>9s}  ids")
    print("-" * 78)
    changed = 0
    for f in files:
        doc = json.load(open(f))
        result = doc["result"]
        before = result.get("mutation_score") or 0
        summary = rescore(copy.deepcopy(result) if not args.write else result)
        cell = os.path.basename(os.path.dirname(os.path.dirname(f)))
        mark = "" if not summary["promoted"] else "  <-- changed"
        print(f"{cell:38s} {before:6.3f} {summary['score']:6.3f} "
              f"{len(summary['promoted']):9d}  {summary['promoted'] or ''}{mark}")
        if summary["promoted"]:
            changed += 1
        if args.write:
            out = f.replace(".json", ".rescored.json")
            json.dump(doc, open(out, "w"), indent=2)
    print(f"\n{changed} of {len(files)} scorecards changed")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
