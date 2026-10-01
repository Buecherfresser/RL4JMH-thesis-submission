#!/usr/bin/env python3
"""Summarise a live GRPO training log (TRL's per-step dicts) without waiting for the run to end.

`scripts/analyse_grpo_run.py` reads `metrics/` after the fact; this reads the raw stdout log of a
run still in flight, which is what you want when deciding whether to let it continue.

The number to watch is **productive steps** -- those with grad_norm > 0. A step with
reward_std == 0 legitimately yields no gradient (all rollouts in the group scored the same).
A step with reward_std > 0 and grad_norm ~= 0 is the pathology in training-run-failures.md §9.

  python scripts/tail_grpo_log.py <logfile> [--last N]
"""

from __future__ import annotations

import argparse
import re
import statistics
from pathlib import Path

STEP_RE = re.compile(r"\{[^{}]*grad_norm[^{}]*\}")

# run 1 (LRZ 5723231) reference values, for "is this normal?"
REF = {"productive_frac": 0.55, "grad_mean": 1.16e-03, "reward_mean": 0.165}


def field(blob: str, key: str) -> float | None:
    m = re.search(rf"'{re.escape(key)}': '?([-0-9.e+]+)'?", blob)
    return float(m.group(1)) if m else None


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("logfile", type=Path)
    ap.add_argument("--last", type=int, default=0, help="only consider the last N steps")
    args = ap.parse_args()

    blobs = STEP_RE.findall(args.logfile.read_text(errors="replace"))
    if args.last:
        blobs = blobs[-args.last:]
    if not blobs:
        print("no step dicts found yet")
        return 1

    g = [field(b, "grad_norm") for b in blobs]
    r = [field(b, "reward") for b in blobs]
    sd = [field(b, "reward_std") for b in blobs]
    ln = [field(b, "completions/mean_length") for b in blobs]
    st = [field(b, "step_time") for b in blobs]
    n = len(blobs)

    cl = [field(b, "completions/clipped_ratio") for b in blobs]
    prod = [i for i, x in enumerate(g) if x and x > 0]

    # A zero gradient has three distinct causes and only one of them is a bug:
    #   reward_std == 0        -> every rollout in the group scored alike; no advantage. Normal.
    #   clipped_ratio == 1.0   -> every completion hit max_completion_length and
    #                             mask_truncated_completions zeroed them all. Configured, but
    #                             wasteful -- the step cost full rollout time for nothing.
    #   neither of the above   -> §9 gradient suppression. Pathological.
    truncated = [i for i in range(n) if (sd[i] or 0) > 0 and not (g[i] or 0) > 0 and (cl[i] or 0) >= 1.0]
    suspect = [i for i in range(n) if (sd[i] or 0) > 0 and not (g[i] or 0) > 0 and (cl[i] or 0) < 1.0]

    print(f"steps logged        : {n}")
    print(f"productive (grad>0) : {len(prod)}/{n} = {len(prod)/n:.0%}   [run 1: {REF['productive_frac']:.0%}]")
    if prod:
        gp = [g[i] for i in prod]
        rp = [r[i] for i in prod if r[i] is not None]
        print(f"grad_norm (productive): mean={statistics.mean(gp):.3g} max={max(gp):.3g}"
              f"   [run 1 mean {REF['grad_mean']:.2e}]")
        if rp:
            print(f"reward (productive)   : mean={statistics.mean(rp):.3g} max={max(rp):.3g}")
    rr = [x for x in r if x is not None]
    if rr:
        print(f"reward (all steps)    : mean={statistics.mean(rr):.3g}   [run 1 epoch {REF['reward_mean']}]")
    if ln and all(x is not None for x in ln):
        print(f"completion length     : mean={statistics.mean(ln):.0f} max={max(ln):.0f}")
    if st and all(x is not None for x in st):
        print(f"step_time             : mean={statistics.mean(st):.1f}s")

    if truncated:
        print(f"\nnote: {len(truncated)}/{n} step(s) lost their gradient to full truncation "
              f"(clipped_ratio 1.0, all completions masked). Expected with "
              f"mask_truncated_completions, but each one is a wasted rollout — if the rate "
              f"climbs, raise max_completion_length or cap runaway generation.")
    if suspect:
        print(f"\n!! {len(suspect)} step(s) had reward variance and no gradient, without being "
              f"truncated -- the §9 gradient-suppression signature. Check "
              f"vllm_importance_sampling_correction.")
    else:
        print("\nok: no gradient suppression (every unclipped step with reward variance learned)")

    if len(rr) >= 8:
        half = len(rr) // 2
        print(f"trend: first half {statistics.mean(rr[:half]):.3g} -> second half {statistics.mean(rr[half:]):.3g}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
