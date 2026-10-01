#!/usr/bin/env python3
"""Measure what a burn-loop token actually costs on *this* machine.

The ``spin`` and ``consumecpu`` mutation operators burn N steps of a 64-bit
recurrence per armed hit. A step is roughly a nanosecond, but that is a property
of the core, not a contract: it moves with clock speed, with the core type on a
hybrid CPU, and with the governor. Since the whole point of the operator is to
inject a latency of a chosen size, the token count has to be calibrated where
the measurement runs.

This tool times the operator's real source -- it lifts the method straight out
of the ``MutationSwitch`` template in ``make_project_mutants.py``, so what it
measures cannot drift from what a mutant injects -- then reports the token count
closest to each target latency.

    uv run python tools/calibrate_mutant_op.py
    uv run python tools/calibrate_mutant_op.py --op spin
    uv run python tools/calibrate_mutant_op.py --target 50 --target 200

It also mirrors the *shape* of the real call, which matters as much as the
source. In ``MutationSwitch`` the token count is a ``static final`` read from a
system property, so the JIT sees a compile-time-constant trip count; timing a
runtime ``tokens`` parameter instead lets C2 make different unrolling decisions
and can report a different cost than the mutant pays. So this harness declares
``TOKENS`` the same way and forks one JVM per token count. That costs about a
second of JVM startup per rung -- worth it, because on the affine ``spin``
recurrence the two shapes disagree by 2.7%, and on a plain ``t = t * A + B``
they disagree by 2x (C2 composes that one across unrolled iterations; see the
note on ``consumeCpu`` in the switch template).

Feed the recommended count to ``jmhbench project-bench --mutant-op consumecpu
--mutant-tokens <n>``. Pin the same machine: a count calibrated on one host is
not valid on another, and on a hybrid CPU it is not valid across core types
either, so pin affinity (``taskset``) the way the measurement run does.
"""

from __future__ import annotations

import argparse
import re
import shutil
import statistics
import subprocess
import sys
import tempfile
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from make_project_mutants import _SWITCH_TEMPLATE  # noqa: E402

DEFAULT_TOKENS = (1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096)
DEFAULT_TARGETS = (25.0, 50.0, 100.0, 250.0, 1000.0)

#: ``--op`` value -> the method to lift out of the switch template and time.
OPS = {"consumecpu": "consumeCpu", "spin": "spin"}

_HARNESS = """
public class SpinCal {

    /**
     * Declared exactly as MutationSwitch declares it: a static final read from a
     * system property, so the JIT sees a constant trip count -- the same thing an
     * armed mutant's burn loop sees. One JVM per token count, therefore.
     */
    private static final long TOKENS = Long.getLong("jmhbench.mutant.tokens", 0L);

__SPIN__

    private static volatile long consumed = 0x5DEECE66DL;

    /** Mirrors MutationSwitch.slow(): the token count is a constant at the call. */
    private static void slow() {
        __CALL__(TOKENS);
    }

    private static double time(final int reps, final int rounds) {
        for (int i = 0; i < 300_000; i++) {
            slow();
        }
        long best = Long.MAX_VALUE;
        for (int r = 0; r < rounds; r++) {
            final long s = System.nanoTime();
            for (int i = 0; i < reps; i++) {
                slow();
            }
            best = Math.min(best, System.nanoTime() - s);
        }
        return (double) best / reps;
    }

    public static void main(final String[] args) {
        final int reps = Integer.parseInt(args[0]);
        final int rounds = Integer.parseInt(args[1]);
        System.out.printf("%d\\t%.4f%n", TOKENS, time(reps, rounds));
    }
}
"""


def _op_source(method: str) -> str:
    """Lift one operator's method out of the MutationSwitch template, verbatim."""
    m = re.search(
        rf"\n(    private static void {method}\(final long tokens\) \{{.*?\n    \}}\n)",
        _SWITCH_TEMPLATE,
        re.DOTALL,
    )
    if not m:
        raise SystemExit(
            f"cannot find {method}() in the MutationSwitch template "
            f"— did the operator move?"
        )
    return m.group(1).rstrip("\n")


def _run(
    method: str, tokens: tuple[int, ...], reps: int, rounds: int, passes: int
) -> dict[int, list[float]]:
    """Time each token count in its own JVM; return every pass's reading."""
    if shutil.which("java") is None:
        raise SystemExit("no `java` on PATH; set JAVA_HOME/PATH to a JDK 17+ install")
    tmp = Path(tempfile.mkdtemp(prefix="jmhbench-spincal-"))
    try:
        src = tmp / "SpinCal.java"
        src.write_text(
            _HARNESS.replace("__SPIN__", _op_source(method)).replace("__CALL__", method),
            encoding="utf-8",
        )
        out: dict[int, list[float]] = {n: [] for n in tokens}
        for p in range(passes):
            for n in tokens:
                cmd = [
                    "java", "-XX:+UseG1GC", f"-Djmhbench.mutant.tokens={n}",
                    str(src), str(reps), str(rounds),
                ]
                proc = subprocess.run(cmd, capture_output=True, text=True)
                if proc.returncode != 0:
                    raise SystemExit(
                        f"calibration run failed at {n} tokens:\n"
                        f"{proc.stdout}\n{proc.stderr}"
                    )
                got, ns = proc.stdout.strip().split("\t")
                assert int(got) == n, f"harness reported {got} tokens, asked for {n}"
                out[n].append(float(ns))
            if passes > 1:
                print(f"  pass {p + 1}/{passes} done", file=sys.stderr)
        return out
    finally:
        shutil.rmtree(tmp, ignore_errors=True)


def _recommend(curve: list[tuple[int, float]], target: float) -> str:
    """Interpolate the measured token -> latency curve for one target latency."""
    n_min, ns_min = curve[0]
    if target <= ns_min:
        return f"{n_min:>17}   below the floor ({ns_min:.1f} ns at {n_min} token(s))"
    for (n_lo, ns_lo), (n_hi, ns_hi) in zip(curve, curve[1:], strict=False):
        if ns_lo <= target <= ns_hi:
            # Linear inside the bracket; the brackets are close enough together
            # that the curvature between two of them is small.
            frac = (target - ns_lo) / (ns_hi - ns_lo)
            n = max(1, round(n_lo + frac * (n_hi - n_lo)))
            return f"{n:>17}   {ns_lo:.0f}-{ns_hi:.0f} ns at {n_lo}-{n_hi} tokens"
    n_max, ns_max = curve[-1]
    n = max(1, round(n_max * target / ns_max))
    return f"{n:>17}   extrapolated past {n_max} tokens ({ns_max:.0f} ns)"


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--op", choices=sorted(OPS), default="consumecpu",
                    help="operator to calibrate (default consumecpu)")
    ap.add_argument("--target", type=float, action="append", metavar="NS",
                    help="target injected latency in ns (repeatable; "
                         f"default {', '.join(str(t) for t in DEFAULT_TARGETS)})")
    ap.add_argument("--reps", type=int, default=1_000_000,
                    help="calls per timing round (default 1000000)")
    ap.add_argument("--rounds", type=int, default=7,
                    help="timing rounds; the fastest one wins (default 7)")
    ap.add_argument("--passes", type=int, default=1,
                    help="repeat the whole ladder N times, one JVM per reading, and "
                         "report the spread (default 1). Use 3+ to check that the "
                         "cost per token does not move between JVMs")
    args = ap.parse_args()
    targets = tuple(args.target) if args.target else DEFAULT_TARGETS
    method = OPS[args.op]

    # 0 tokens measures the call itself; subtract it to isolate injected latency.
    ladder = (0,) + DEFAULT_TOKENS
    readings = _run(method, ladder, args.reps, args.rounds, max(1, args.passes))
    floor = min(readings.pop(0))

    print(f"operator: {args.op} ({method}() from the MutationSwitch template), "
          f"constant trip count, one JVM per reading")
    print(f"call overhead (0 tokens): {floor:.2f} ns — subtracted below\n")
    spread_col = f" {'spread':>9}" if args.passes > 1 else ""
    print(f"{'tokens':>8} {'injected latency':>18} {'ns/token':>10}{spread_col}")
    measured: dict[int, float] = {}
    for n in sorted(readings):
        best = min(readings[n]) - floor
        measured[n] = best
        row = f"{n:>8} {best:>15.2f} ns {best / n:>10.3f}"
        if args.passes > 1:
            vals = [v - floor for v in readings[n]]
            rng = max(vals) - min(vals)
            row += f" {rng / statistics.mean(vals) * 100:>8.2f}%"
        print(row)

    # The curve is not a straight line through the origin: at a handful of tokens
    # the recurrence overlaps with the call itself and a token looks cheap, and it
    # only settles near its asymptotic cost once the chain is long enough to
    # dominate. So recommend by interpolating the measured curve rather than by
    # dividing by a fitted slope, which overshoots badly in the middle of the range.
    curve = sorted((n, ns) for n, ns in measured.items() if ns > 0)
    if len(curve) < 2:
        print("\nno usable readings — raise --reps and rerun on an idle machine")
        return 1
    tail = [(n, ns) for n, ns in curve if n >= 512]
    if tail:
        per_token = sum(ns / n for n, ns in tail) / len(tail)
        print(f"\nasymptotic cost: {per_token:.3f} ns per token "
              f"(over {tail[0][0]}..{tail[-1][0]} tokens)")

    print(f"\n{'target':>10} {'--mutant-tokens':>17} {'measured range':>24}")
    for t in targets:
        print(f"{t:>7.0f} ns {_recommend(curve, t)}")

    print(
        "\nCalibrate on the host and core set the measurement uses: on a hybrid CPU "
        "a token costs different amounts on different core types, so run this under "
        "the same `taskset` mask as the campaign."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
