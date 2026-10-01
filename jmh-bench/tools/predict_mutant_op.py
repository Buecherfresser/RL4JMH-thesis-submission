#!/usr/bin/env python3
"""Estimate what a campaign would have scored under a cheaper mutation operator.

Rerunning a campaign to pick ``--mutant-tokens`` costs what the campaign cost
(the 2026-08 rerun: 372 measurement-hours). It does not have to be rerun,
because every scorecard already carries the raw per-fork samples of both arms of
every detection, and the injected latency is the only thing that changes.

For one (mutant, benchmark) detection with mean throughputs ``base`` and
``armed``, the armed run spent an extra ``dt = 1/armed - 1/base`` seconds per
op. Under the ``sleep`` operator one hit costs ~1.2 ms, so the site was hit
``h = dt / 1.2ms`` times per op -- and that hit count is a property of the
benchmark, not of the operator. Swapping in an operator that costs ``d`` instead
gives a predicted per-op cost of ``base_op + h*d``, hence

    slowdown(d) = 1 + h * d / base_op

which is re-tested against the same kill gate. Two caveats, both in the
optimistic direction for large ``d`` and pessimistic for small: the effect-size
gate is re-applied but the t-test is not (variance at the new effect size is not
knowable from these samples, so a noise floor of ``3 x`` the baseline RSD stands
in for it), and mutants that were never *covered* stay uncovered -- this
predicts the kill rate among covered mutants, not new coverage.

    uv run python tools/predict_mutant_op.py /path/to/campaign-dir
    uv run python tools/predict_mutant_op.py CAMPAIGN --latency 50 --latency 250 --per-cell
"""

from __future__ import annotations

import argparse
import json
import statistics
from pathlib import Path

#: Cost of one ``Thread.sleep(0, 1)`` hit, the operator the samples were taken
#: under. Measured back out of the data itself (--show-sleep-cost).
SLEEP_NS = 1.212e6

DEFAULT_LATENCIES = (10.0, 25.0, 50.0, 100.0, 250.0, 1_000.0, 10_000.0)


def _mean(xs: list[float]) -> float:
    return sum(xs) / len(xs)


def _detections(campaign: Path) -> list[dict]:
    """One row per covered mutant: its strongest measured detection."""
    rows = []
    for sc in sorted(campaign.glob("*/out/*/report/scorecard.json")):
        cell = sc.relative_to(campaign).parts[0]
        result = json.loads(sc.read_text())["result"]
        for mutant in result.get("mutant_results") or []:
            best = None
            for det in mutant.get("detections") or []:
                base, armed = det.get("base_samples") or [], det.get("mutant_samples") or []
                if not base or not armed:
                    continue
                b, a = _mean(base), _mean(armed)
                if b <= 0 or a <= 0 or a >= b:
                    continue
                base_op_ns = 1e9 / b
                dt_ns = 1e9 / a - 1e9 / b
                cand = {
                    "cell": cell,
                    "id": mutant["id"],
                    "base_op_ns": base_op_ns,
                    "dt_ns": dt_ns,
                    "hits_per_op": dt_ns / SLEEP_NS,
                    "slowdown": b / a,
                    "rsd": statistics.stdev(base) / b * 100 if len(base) > 1 else 0.0,
                    "killed": mutant["status"] == "killed",
                }
                if best is None or cand["dt_ns"] / cand["base_op_ns"] > best["dt_ns"] / best["base_op_ns"]:
                    best = cand
            if best:
                rows.append(best)
    return rows


def _kills(rows: list[dict], latency_ns: float, min_slowdown: float) -> list[bool]:
    out = []
    for r in rows:
        effect_pct = r["hits_per_op"] * latency_ns / r["base_op_ns"] * 100.0
        gate = max((min_slowdown - 1.0) * 100.0, 3.0 * r["rsd"])
        out.append(effect_pct >= gate)
    return out


def main() -> int:
    ap = argparse.ArgumentParser(
        description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter
    )
    ap.add_argument("campaign", type=Path,
                    help="directory of cells, each with out/*/report/scorecard.json")
    ap.add_argument("--latency", type=float, action="append", metavar="NS",
                    help="injected latency to model, in ns (repeatable)")
    ap.add_argument("--min-slowdown", type=float, default=1.10,
                    help="kill gate, as in the run itself (default 1.10)")
    ap.add_argument("--per-cell", action="store_true",
                    help="also break the prediction down by cell")
    args = ap.parse_args()
    latencies = tuple(args.latency) if args.latency else DEFAULT_LATENCIES

    rows = _detections(args.campaign)
    if not rows:
        print(f"no detections with raw samples under {args.campaign}")
        return 1

    cells = sorted({r["cell"] for r in rows})
    print(f"{len(rows)} measured detections across {len(cells)} cell(s)\n")

    hpo = sorted(r["hits_per_op"] for r in rows)
    print(f"implied hits per op: median {statistics.median(hpo):.2f}, "
          f"p90 {hpo[int(0.9 * len(hpo))]:.1f}, max {hpo[-1]:.3g}")
    ops = sorted(r["base_op_ns"] for r in rows)
    print(f"baseline op cost:    median {statistics.median(ops):.0f} ns, "
          f"p10 {ops[int(0.1 * len(ops))]:.1f} ns, p90 {ops[int(0.9 * len(ops))]:.0f} ns")
    print("\nA fixed injected latency is only detectable on benchmarks whose op cost is "
          "within\nan order of magnitude of it, which is what spreads the predictions below.\n")

    print(f"{'latency':>12} {'predicted kills':>17} {'of measured':>13} {'median slowdown':>17}")
    for d in latencies:
        kills = _kills(rows, d, args.min_slowdown)
        n = sum(kills)
        sl = statistics.median(
            1 + r["hits_per_op"] * d / r["base_op_ns"] for r in rows
        )
        label = f"{d / 1000:.0f} us" if d >= 1000 else f"{d:.0f} ns"
        print(f"{label:>12} {n:>17} {n / len(rows):>12.0%} {sl:>16.3g}x")

    actual = sum(1 for r in rows if r["killed"])
    print(f"\n{'sleep (actual)':>12} {actual:>17} {actual / len(rows):>12.0%}")

    if args.per_cell:
        print(f"\n{'cell':<38}" + "".join(
            f"{(f'{d / 1000:.0f}us' if d >= 1000 else f'{d:.0f}ns'):>9}" for d in latencies
        ) + f"{'sleep':>9}")
        for cell in cells:
            sub = [r for r in rows if r["cell"] == cell]
            line = f"{cell:<38}"
            for d in latencies:
                line += f"{sum(_kills(sub, d, args.min_slowdown)):>9}"
            line += f"{sum(1 for r in sub if r['killed']):>9}"
            print(line)
        print(f"\n(counts are mutants killed out of the {len(rows)} covered ones measured; "
              "uncovered mutants stay uncovered)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
