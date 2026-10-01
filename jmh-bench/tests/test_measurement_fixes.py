#!/usr/bin/env python3
"""Regression tests for the campaign blockers in EVALUATION_ISSUES.md B1-B5.

Usage::

    .venv/bin/python tests/test_measurement_fixes.py

The JMH-level checks need a built benchmarks.jar. Point ``JMHBENCH_SMOKE_JAR_DIR``
at a directory containing ``target/benchmarks.jar`` to enable them; without it
those cases are skipped and the pure-logic cases still run.

Each case names the issue it pins. No third-party test runner, so it can run
from the same interpreter as the harness.
"""

from __future__ import annotations

import json
import math
import os
import subprocess
import sys
import tempfile
import re
from dataclasses import dataclass, field
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from jmhbench import jmh_run  # noqa: E402
from jmhbench.config import JmhSettings, RunConfig  # noqa: E402
from jmhbench.jmh_run import BenchmarkResult, JmhRun, result_key  # noqa: E402
from jmhbench import project_bench as pb  # noqa: E402

FAILURES: list[str] = []
SKIPPED: list[str] = []
PASSES = 0


def check(name: str, condition: bool, detail: str = "") -> None:
    global PASSES
    if condition:
        PASSES += 1
        print(f"[PASS] {name}")
    else:
        FAILURES.append(f"{name}: {detail}")
        print(f"[FAIL] {name}  {detail}")


def skip(name: str, why: str) -> None:
    SKIPPED.append(name)
    print(f"[SKIP] {name}  ({why})")


# ---------------------------------------------------------------------------
# B4 -- the flags that were never emitted, and the fork structure
# ---------------------------------------------------------------------------

def test_b4_command_line() -> None:
    """-bm / -tu / -gc / -t and a pinned JVM must all reach the command line."""
    captured: dict = {}

    def fake_run_jvm(cmd, **kwargs):
        captured["cmd"] = cmd
        captured["timeout"] = kwargs.get("timeout")
        raise FileNotFoundError("stop here -- we only want the argv")

    with tempfile.TemporaryDirectory() as td:
        project = Path(td)
        (project / "target").mkdir()
        (project / "target" / "benchmarks.jar").write_bytes(b"")
        real = jmh_run.run_jvm
        jmh_run.run_jvm = fake_run_jvm
        try:
            jmh_run.run_jmh(project, JmhSettings.preset("campaign"), tag="t",
                            jvm_args=["-Djmhbench.mutant=7"])
        finally:
            jmh_run.run_jvm = real

    cmd = captured.get("cmd") or []
    joined = " ".join(cmd)
    for flag, value in (("-bm", "thrpt"), ("-tu", "s"), ("-gc", "true"), ("-t", "1")):
        ok = flag in cmd and cmd[cmd.index(flag) + 1] == value
        check(f"B4 {flag} {value} is emitted", ok, joined)

    check("B4 forks raised to 5", "-f" in cmd and cmd[cmd.index("-f") + 1] == "5", joined)
    check(
        "B4 measurement iterations raised to 10",
        "-i" in cmd and cmd[cmd.index("-i") + 1] == "10", joined,
    )

    append = cmd[cmd.index("-jvmArgsAppend") + 1] if "-jvmArgsAppend" in cmd else ""
    for flag in ("-Xms2g", "-Xmx2g", "-XX:+UseG1GC"):
        check(f"B4 {flag} is pinned on the forked JVM", flag in append, append)
    check(
        "B4 the caller's mutant property still reaches the fork",
        "-Djmhbench.mutant=7" in append, append,
    )


def test_b4_preset_reaches_target() -> None:
    campaign = JmhSettings.preset("campaign")
    default = JmhSettings.preset("default")
    check(
        "B4 the campaign preset measures far more than the default",
        campaign.measured_seconds() >= 15 * default.measured_seconds(),
        f"campaign={campaign.measured_seconds()}s default={default.measured_seconds()}s",
    )
    check(
        "B4 the campaign preset uses several forks, not one deep fork",
        campaign.forks >= 5, f"forks={campaign.forks}",
    )
    llm = JmhSettings.preset("llm4jmh")
    check(
        "B4 an llm4jmh-exact preset exists (-f 1 -wi 5 -w 500ms -i 30 -r 1000ms)",
        (llm.forks, llm.warmup_iterations, llm.warmup_time_seconds,
         llm.measurement_iterations, llm.measurement_time_seconds) == (1, 5, 0.5, 30, 1.0),
        f"{llm}",
    )


def test_b4_sample_time_not_dropped() -> None:
    """Mode.SampleTime writes rawDataHistogram; reading only rawData lost it."""
    hist = [[[[10.0, 3], [20.0, 1]], [[30.0, 2], [50.0, 2]]]]
    got = jmh_run._histogram_by_fork(hist)
    check(
        "B4 rawDataHistogram is parsed into per-iteration scores",
        got == [[12.5, 40.0]], f"got {got}",
    )
    with tempfile.TemporaryDirectory() as td:
        p = Path(td) / "r.json"
        p.write_text(json.dumps([{
            "benchmark": "a.B.c", "mode": "sample",
            "primaryMetric": {"score": 12.5, "scoreUnit": "us/op", "rawDataHistogram": hist},
        }]))
        parsed = jmh_run._parse_results(p)
    check(
        "B4 a SampleTime benchmark is no longer silently sample-less",
        len(parsed) == 1 and len(parsed[0].samples) == 2,
        f"samples={parsed[0].samples if parsed else None}",
    )


def test_b4_fork_structure_preserved() -> None:
    raw = [[1.0, 2.0], [3.0, 4.0], [5.0, 6.0]]
    with tempfile.TemporaryDirectory() as td:
        p = Path(td) / "r.json"
        p.write_text(json.dumps([{
            "benchmark": "a.B.c", "mode": "thrpt",
            "primaryMetric": {"score": 3.5, "scoreUnit": "ops/s", "rawData": raw},
        }]))
        r = jmh_run._parse_results(p)[0]
    check("B4 the flat sample is unchanged", r.samples == [1, 2, 3, 4, 5, 6], f"{r.samples}")
    check(
        "B4 the fork boundary survives alongside it",
        r.samples_by_fork == raw, f"{r.samples_by_fork}",
    )
    summary = pb._summarise_results([r], ok=True, error=None)
    entry = summary["benchmarks"][0]
    check(
        "B4 samples_by_fork is persisted to the scorecard",
        entry["samples_by_fork"] == raw and entry["n_forks"] == 3, f"{entry}",
    )


# ---------------------------------------------------------------------------
# B3 -- @Param cross-pairing
# ---------------------------------------------------------------------------

def _result(name: str, params: dict, score: float, samples: list[float]) -> BenchmarkResult:
    return BenchmarkResult(
        benchmark=name, mode="thrpt", score=score, score_error=0.0, unit="ops/s",
        samples=samples, params=params, samples_by_fork=[list(samples)],
    )


def test_b3_param_keys_are_distinct() -> None:
    small = _result("a.B.c", {"size": "64"}, 1000.0, [1000.0, 1010.0, 990.0])
    large = _result("a.B.c", {"size": "4096"}, 10.0, [10.0, 10.1, 9.9])
    plain = _result("a.B.d", {}, 5.0, [5.0, 5.1, 4.9])

    check(
        "B3 param combinations get distinct keys",
        small.key != large.key and small.key == "a.B.c[size=64]", f"{small.key} {large.key}",
    )
    check("B3 a param-free benchmark keeps its bare name", plain.key == "a.B.d", plain.key)

    run = JmhRun(success=True, results=[small, large, plain], stdout="", stderr="")
    index = pb._index_results(run)
    check(
        "B3 the baseline index keeps every combination",
        set(index) == {"a.B.c[size=64]", "a.B.c[size=4096]", "a.B.d"}, f"{sorted(index)}",
    )
    # The old behaviour, for contrast: a dict comprehension on the name kept the
    # LAST combination and n_benchmarks undercounted by one.
    old = {r.benchmark: r for r in run.results}
    check(
        "B3 name-keying really did collapse the combinations (premise check)",
        len(old) == 2 and old["a.B.c"].score == 10.0, f"{ {k: v.score for k, v in old.items()} }",
    )
    check(
        "B3 n_benchmarks now counts measurements, not method names",
        len(index) == 3 and len(old) == 2, f"{len(index)} vs {len(old)}",
    )
    check(
        "B3 key ordering is stable regardless of dict order",
        result_key("x", {"b": "2", "a": "1"}) == result_key("x", {"a": "1", "b": "2"}),
    )


# ---------------------------------------------------------------------------
# B1 / B2 -- the detection probe
# ---------------------------------------------------------------------------

@dataclass
class _Mutant:
    id: int
    component: str = "core"


@dataclass
class _Task:
    instance_id: str = "proj"
    mutant_count: int = 1
    mutants: list = field(default_factory=list)


def _method_from_include(include: str | None) -> str:
    """Undo ``_include_regex``: it escapes the FQN and anchors it with ``$``."""
    return (include or "").removesuffix("$").replace("\\", "")


def _detect(scripted, *, sensitivity_sample=0, max_attempts=6, n_bench=8,
            baseline_wall=None, mutant_ids=(1,)):
    """Drive _detect_mutants with run_jmh stubbed by a per-method script."""
    keys = [f"a.B.bench{i}" for i in range(n_bench)]
    baseline = {
        k: _result(k, {}, 100.0, [100.0, 101.0, 99.0, 100.5]) for k in keys
    }
    coverage = {k: {mid: n_bench - i for mid in mutant_ids} for i, k in enumerate(keys)}
    task = _Task(mutant_count=len(mutant_ids), mutants=[_Mutant(i) for i in mutant_ids])
    config = RunConfig(
        harness_name="x", harness_kwargs={}, jmh=JmhSettings.preset("quick"),
        sensitivity_sample=sensitivity_sample, max_detect_attempts=max_attempts,
    )
    calls: list[str] = []

    def fake_run_jmh(project_dir, settings, tag="base", **kwargs):
        method = _method_from_include(kwargs.get("include"))
        calls.append(method)
        return scripted(method)

    real = pb.run_jmh
    pb.run_jmh = fake_run_jmh
    try:
        results = pb._detect_mutants(
            Path("/nonexistent"), task, config, baseline, coverage,
            baseline_wall=baseline_wall or {},
        )
    finally:
        pb.run_jmh = real
    return results, calls


def _slow(method: str, factor: float) -> JmhRun:
    base = [100.0, 101.0, 99.0, 100.5]
    return JmhRun(
        success=True, stdout="", stderr="",
        results=[_result(method, {}, 100.0 / factor, [x / factor for x in base])],
    )


def test_b2_timeout_is_not_a_kill() -> None:
    timeout = JmhRun(success=False, results=[], stdout="", stderr="", error="timeout")
    results, calls = _detect(lambda m: timeout)
    m = results[0]
    check(
        "B2 a mutant whose every armed run timed out is not killed",
        m.status != "killed", f"status={m.status}",
    )
    check("B2 it is reported as an error, not a survivor", m.status == "error", m.status)
    check("B2 the timeouts are counted", m.n_timeouts > 0, f"{m.n_timeouts}")
    check(
        "B2 no detection verdict is manufactured from a timeout",
        all(not d.detected for d in m.detections), "a timed-out pair is marked detected",
    )
    check(
        "B2 every timed-out pair is flagged as such",
        all(d.timed_out for d in m.detections), "timed_out not set",
    )
    check(
        "B2 no fake effect size accompanies a timeout",
        all(d.effect_size is None for d in m.detections),
    )


def test_b2_timeout_does_not_mask_a_real_measurement() -> None:
    """The old probe broke on the first timeout, so nothing else was measured."""
    def script(method: str) -> JmhRun:
        if method.endswith("bench0"):   # the highest-hit benchmark, tried first
            return JmhRun(success=False, results=[], stdout="", stderr="", error="timeout")
        if method.endswith("bench1"):
            return _slow(method, 2.0)   # a real 2x slowdown
        return _slow(method, 1.0)

    results, calls = _detect(script)
    m = results[0]
    check(
        "B2 a timeout on the first benchmark no longer ends the probe",
        len(calls) > 1, f"calls={calls}",
    )
    check(
        "B2 the real slowdown behind the timeout is found and kills the mutant",
        m.status == "killed" and (m.killed_by or "").endswith("bench1"),
        f"status={m.status} killed_by={m.killed_by}",
    )
    check(
        "B2 the kill carries a genuine effect size",
        m.effect_size is not None and m.effect_size > 1.5, f"{m.effect_size}",
    )


def test_b2_mutant_budget_scales_with_the_baseline() -> None:
    seen: dict = {}

    def script(method: str) -> JmhRun:
        return _slow(method, 1.0)

    keys = [f"a.B.bench{i}" for i in range(2)]
    baseline = {k: _result(k, {}, 100.0, [100.0, 101.0, 99.0, 100.5]) for k in keys}
    coverage = {k: {1: 5 - i} for i, k in enumerate(keys)}
    task = _Task(mutant_count=1, mutants=[_Mutant(1)])
    config = RunConfig(harness_name="x", harness_kwargs={},
                       jmh=JmhSettings.preset("quick"), max_detect_attempts=0)

    def fake_run_jmh(project_dir, settings, tag="base", **kwargs):
        method = _method_from_include(kwargs.get("include"))
        seen[method] = kwargs.get("wall_timeout")
        return script(method)

    real = pb.run_jmh
    pb.run_jmh = fake_run_jmh
    try:
        pb._detect_mutants(Path("/x"), task, config, baseline, coverage,
                           baseline_wall={"a.B.bench0": 400.0})
    finally:
        pb.run_jmh = real

    budget = JmhSettings.preset("quick").wall_budget_seconds()
    check(
        "B2 a slow-but-healthy benchmark gets a budget scaled to its own baseline",
        seen.get("a.B.bench0", 0) >= 5 * 400.0,
        f"budget for a 400s baseline was {seen.get('a.B.bench0')}s",
    )
    check(
        "B2 a benchmark with no recorded baseline falls back to the flat budget",
        seen.get("a.B.bench1") == budget, f"{seen.get('a.B.bench1')} != {budget}",
    )


def test_b1_probe_covers_everything_with_sensitivity_all() -> None:
    results, calls = _detect(lambda m: _slow(m, 1.0), n_bench=12, max_attempts=6)
    check(
        "B1 the kill probe alone still stops at max_detect_attempts",
        len(set(calls)) == 6, f"visited {len(set(calls))} of 12",
    )
    check(
        "B1 a truncated probe says so",
        results[0].probe_truncated, "probe_truncated not set",
    )

    results, calls = _detect(
        lambda m: _slow(m, 1.0), n_bench=12, max_attempts=6, sensitivity_sample=-1
    )
    check(
        "B1 --sensitivity-sample all measures every covering benchmark",
        len(set(calls)) == 12, f"visited {len(set(calls))} of 12",
    )
    m = results[0]
    check(
        "B1 every covering pair carries a verdict",
        len(m.detections) == 12, f"{len(m.detections)} verdicts",
    )
    check(
        "B1 every pair is flagged as part of the sensitivity sample",
        all(d.sampled_for_sensitivity for d in m.detections),
    )
    check(
        "B1 a full sweep is not reported as truncated",
        not m.probe_truncated,
    )
    check(
        "B1 sensitivity is now computable: detecting pairs / covering pairs",
        m.n_measured == 12, f"n_measured={m.n_measured}",
    )


def test_b1_fixed_k_is_still_a_bound() -> None:
    _results, calls = _detect(
        lambda m: _slow(m, 1.0), n_bench=12, max_attempts=6, sensitivity_sample=4
    )
    check(
        "B1 a fixed k under the fan-out leaves pairs unmeasured (why 'all' exists)",
        len(set(calls)) < 12, f"visited {len(set(calls))}",
    )


def test_b1_no_double_payment() -> None:
    """The sensitivity sweep must not re-run what the kill probe measured."""
    _results, calls = _detect(
        lambda m: _slow(m, 1.0), n_bench=8, max_attempts=8, sensitivity_sample=-1
    )
    check(
        "B1 each covering benchmark is run exactly once",
        len(calls) == len(set(calls)) == 8, f"{len(calls)} calls for {len(set(calls))} benchmarks",
    )


def test_c5_nan_effect_does_not_poison_best_effect() -> None:
    """A leading NaN used to discard every later valid effect size."""
    def script(method: str) -> JmhRun:
        if method.endswith("bench0"):
            return JmhRun(success=True, stdout="", stderr="",
                          results=[_result(method, {}, float("nan"),
                                           [float("nan")] * 4)])
        return _slow(method, 1.05)   # real but below the 10% gate

    results, _calls = _detect(script, n_bench=4, max_attempts=4)
    eff = results[0].effect_size
    check(
        "C5 a NaN on the first probe does not swallow the later effect sizes",
        eff is not None and not math.isnan(eff) and eff > 1.0,
        f"best_effect={eff}",
    )


# ---------------------------------------------------------------------------
# B5 -- machine identity
# ---------------------------------------------------------------------------

def test_b5_host_is_recorded() -> None:
    from jmhbench import hostinfo
    info = hostinfo.collect()
    for key in ("hostname", "platform", "kernel", "cpu", "memory_bytes", "jdk"):
        check(f"B5 {key} is recorded", info.get(key) is not None, f"{key}={info.get(key)!r}")
    cpu = info["cpu"]
    check("B5 the CPU model is identified", bool(cpu.get("model")), f"{cpu}")
    check("B5 the logical CPU count is recorded", bool(cpu.get("logical_cpus")), f"{cpu}")
    check(
        "B5 the JDK build string is recorded, not just a major version",
        bool((info["jdk"] or {}).get("version_string")), f"{info['jdk']}",
    )
    check(
        "B5 load average is captured (it is only meaningful taken at the start)",
        info.get("load_average") is not None,
    )
    check(
        "B5 the whole record is JSON-serialisable",
        isinstance(json.dumps(info), str),
    )
    check("B5 short_label degrades gracefully", hostinfo.short_label(None) == "not recorded")
    check("B5 short_label names the host", info["hostname"] in hostinfo.short_label(info))


# ---------------------------------------------------------------------------
# End to end against a real JMH jar
# ---------------------------------------------------------------------------

def test_live_jmh() -> None:
    jar_dir = os.environ.get("JMHBENCH_SMOKE_JAR_DIR")
    if not jar_dir or not (Path(jar_dir) / "target" / "benchmarks.jar").exists():
        skip("live JMH run", "set JMHBENCH_SMOKE_JAR_DIR to a dir with target/benchmarks.jar")
        return
    run = jmh_run.run_jmh(Path(jar_dir), JmhSettings.preset("quick"), tag="selftest")
    check("live: JMH accepts the pinned command line", run.success, str(run.error))
    if not run.success:
        return
    out = run.stdout or ""
    check(
        "live: -bm overrides the class's @BenchmarkMode",
        "Throughput" in out, "mode line: " + next(
            (l for l in out.splitlines() if "Benchmark mode" in l), "?"
        ),
    )
    check(
        "live: -t overrides @Threads(Threads.MAX)",
        "1 thread" in out, next((l for l in out.splitlines() if "# Threads" in l), "?"),
    )
    check(
        "live: the heap and collector reach the forked JVM",
        "-Xmx2g" in out and "UseG1GC" in out,
        next((l for l in out.splitlines() if "VM options" in l), "?"),
    )
    keys = {r.key for r in run.results}
    parametrised = {k for k in keys if "[" in k}
    check(
        "live: each @Param combination is a distinct result",
        len(parametrised) >= 2, f"{sorted(keys)}",
    )
    by_name = {r.benchmark for r in run.results}
    check(
        "live: name-keying would have collapsed them (premise check)",
        len(by_name) < len(keys), f"{len(by_name)} names for {len(keys)} measurements",
    )
    check(
        "live: every result carries its fork structure",
        all(r.samples_by_fork for r in run.results),
    )
    check(
        "live: all modes agree after -bm (the suite is homogeneous)",
        len({r.mode for r in run.results}) == 1, f"{ {r.mode for r in run.results} }",
    )


def main() -> int:
    print("=== measurement regression tests (EVALUATION_ISSUES.md B1-B5) ===\n")
    for fn in (
        test_b4_command_line,
        test_b4_preset_reaches_target,
        test_b4_sample_time_not_dropped,
        test_b4_fork_structure_preserved,
        test_b3_param_keys_are_distinct,
        test_b2_timeout_is_not_a_kill,
        test_b2_timeout_does_not_mask_a_real_measurement,
        test_b2_mutant_budget_scales_with_the_baseline,
        test_b1_probe_covers_everything_with_sensitivity_all,
        test_b1_fixed_k_is_still_a_bound,
        test_b1_no_double_payment,
        test_c5_nan_effect_does_not_poison_best_effect,
        test_b5_host_is_recorded,
        test_live_jmh,
    ):
        fn()
    print(f"\n{PASSES} passed, {len(FAILURES)} failed, {len(SKIPPED)} skipped.")
    for f in FAILURES:
        print(f"  FAIL {f}")
    return 1 if FAILURES else 0


if __name__ == "__main__":
    sys.exit(main())
