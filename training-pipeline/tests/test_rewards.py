"""Unit tests for the reward layer (no JVM required: results are constructed directly)."""

from __future__ import annotations

import pytest

from jmhgen.config.schema import RewardWeights
from jmhgen.rewards import (
    AntiPatternReward,
    CompileReward,
    CompositeReward,
    RsdReward,
    RuntimeReward,
    build_composite_reward,
    check_anti_patterns,
    default_cheap_reward,
)
from jmhgen.runner.types import (
    BenchmarkStat,
    CompileResult,
    ErrorKind,
    EvaluationResult,
    RunResult,
)

# A clean, conformant benchmark: @State + @Setup inputs, all JMH config annotations, a returned
# result (no DCE risk), no constant folding, no final-literal locals.
_CLEAN_BENCH = (
    "package com.ex;\n"
    "import org.openjdk.jmh.annotations.*;\n"
    "import java.util.concurrent.TimeUnit;\n"
    "@State(Scope.Thread)\n"
    "@BenchmarkMode(Mode.AverageTime)\n"
    "@OutputTimeUnit(TimeUnit.NANOSECONDS)\n"
    "@Fork(1)\n@Warmup(iterations = 5)\n@Measurement(iterations = 5)\n"
    "public class FooBenchmark {\n"
    "    private int n;\n"
    "    @Setup\n    public void setup() { n = 7; }\n"
    "    @Benchmark\n    public Object measure() { return Foo.compute(n); }\n"
    "}"
)


def _stat(robust_rsd: float | None = None, name: str = "x.B.m") -> BenchmarkStat:
    return BenchmarkStat(
        benchmark=name, mode="avgt", score=1.0, score_error=0.0, unit="ns/op", robust_rsd=robust_rsd
    )


def _compiled(ok: bool) -> CompileResult:
    return CompileResult(
        success=ok,
        duration_s=1.0,
        error_kind=ErrorKind.NONE if ok else ErrorKind.COMPILE_ERROR,
        artifact_path="/tmp/benchmarks.jar" if ok else None,
    )


def _ran(ok: bool) -> RunResult:
    return RunResult(
        success=ok,
        duration_s=2.0,
        stats=(_stat(),) if ok else (),
        error_kind=ErrorKind.NONE if ok else ErrorKind.RUNTIME_ERROR,
    )


class TestCompileReward:
    def test_success(self) -> None:
        result = CompileReward()(EvaluationResult(compile=_compiled(True)))
        assert result.value == 1.0
        assert result.detail["success"] is True

    def test_failure(self) -> None:
        result = CompileReward()(EvaluationResult(compile=_compiled(False)))
        assert result.value == 0.0
        assert result.detail["error_kind"] == ErrorKind.COMPILE_ERROR.value


class TestRuntimeReward:
    def test_success(self) -> None:
        evaluation = EvaluationResult(compile=_compiled(True), run=_ran(True))
        result = RuntimeReward()(evaluation)
        assert result.value == 1.0
        assert result.detail["num_benchmarks"] == 1

    def test_failure_when_run_errored(self) -> None:
        evaluation = EvaluationResult(compile=_compiled(True), run=_ran(False))
        assert RuntimeReward()(evaluation).value == 0.0

    def test_failure_when_not_run(self) -> None:
        evaluation = EvaluationResult(compile=_compiled(False), run=None)
        result = RuntimeReward()(evaluation)
        assert result.value == 0.0
        assert result.detail["error_kind"] == "not_run"


class TestCompositeReward:
    def test_weighted_sum(self) -> None:
        composite = CompositeReward([(CompileReward(), 0.3), (RuntimeReward(), 0.7)])
        evaluation = EvaluationResult(compile=_compiled(True), run=_ran(True))
        result = composite(evaluation)
        assert result.value == pytest.approx(0.3 * 1.0 + 0.7 * 1.0)
        assert set(result.detail["components"]) == {"compile", "runtime"}

    def test_partial_credit_compile_only(self) -> None:
        composite = CompositeReward([(CompileReward(), 0.4), (RuntimeReward(), 0.6)])
        evaluation = EvaluationResult(compile=_compiled(True), run=_ran(False))
        assert composite(evaluation).value == pytest.approx(0.4)

    def test_zero_when_nothing_works(self) -> None:
        composite = default_cheap_reward()
        evaluation = EvaluationResult(compile=_compiled(False), run=None)
        assert composite(evaluation).value == 0.0

    def test_default_cheap_reward_weights(self) -> None:
        composite = default_cheap_reward(compile_weight=0.5, runtime_weight=0.5)
        evaluation = EvaluationResult(compile=_compiled(True), run=_ran(True))
        assert composite(evaluation).value == pytest.approx(1.0)


# All five anti-patterns: void @Benchmark w/o Blackhole (dce_risk), no @State/@Param + a
# static-final literal (constant_folding AND final_local_input -- "final int" matches inside
# "static final int", exactly as JMH-Bench does), missing config, no @BenchmarkMode.
_SMELLY_BENCH = (
    "public class B {\n"
    "    static final int SIZE = 100;\n"
    "    @Benchmark\n    public void run() { compute(SIZE); }\n"
    "}"
)

# Exactly two patterns: missing_jmh_config + wrong_mode (it has @State, returns a value).
_PARTIAL_BENCH = (
    "@State(Scope.Thread)\n"
    "public class B {\n"
    "    private int n;\n"
    "    @Setup\n    public void s() { n = readInput(); }\n"
    "    @Benchmark\n    public int m() { return Foo.f(n); }\n}"
)


class TestCheckAntiPatterns:
    def test_clean_source_has_no_violations(self) -> None:
        result = check_anti_patterns(_CLEAN_BENCH)
        assert result.clean
        assert result.total == 0

    def test_smelly_source_flags_all_patterns(self) -> None:
        result = check_anti_patterns(_SMELLY_BENCH)
        assert all(result.violations[p] for p in result.violations)
        assert result.total == 5

    def test_partial_source_flags_two(self) -> None:
        result = check_anti_patterns(_PARTIAL_BENCH)
        assert result.violations["missing_jmh_config"] is True
        assert result.violations["wrong_mode"] is True
        assert result.violations["dce_risk"] is False
        assert result.violations["constant_folding"] is False
        assert result.total == 2

    def test_no_benchmark_method_is_missing_config(self) -> None:
        result = check_anti_patterns("public class B {}")
        assert result.violations["missing_jmh_config"] is True
        assert result.total == 1

    def test_final_local_literal_detected(self) -> None:
        source = (
            "@State(Scope.Thread)\n@BenchmarkMode(Mode.AverageTime)\n"
            "@Fork(1)\n@Warmup(iterations = 1)\n@Measurement(iterations = 1)\n"
            "public class B {\n"
            "    @Benchmark\n    public int m() { final int x = 42; return Foo.f(x); }\n}"
        )
        assert check_anti_patterns(source).violations["final_local_input"] is True


class TestAntiPatternReward:
    def test_clean_scores_one(self) -> None:
        evaluation = EvaluationResult(compile=_compiled(True), metadata={"source": _CLEAN_BENCH})
        result = AntiPatternReward()(evaluation)
        assert result.value == pytest.approx(1.0)
        assert result.detail["total"] == 0

    def test_graded_by_violation_count(self) -> None:
        evaluation = EvaluationResult(compile=_compiled(True), metadata={"source": _PARTIAL_BENCH})
        # 2 of 5 patterns -> 1 - 2/5 = 0.6.
        assert AntiPatternReward()(evaluation).value == pytest.approx(0.6)

    def test_all_patterns_scores_zero(self) -> None:
        evaluation = EvaluationResult(compile=_compiled(True), metadata={"source": _SMELLY_BENCH})
        assert AntiPatternReward()(evaluation).value == pytest.approx(0.0)

    def test_missing_source_uses_fallback(self) -> None:
        evaluation = EvaluationResult(compile=_compiled(True))
        assert AntiPatternReward(on_missing_source=0.0)(evaluation).value == 0.0


def _ran_with_rsd(*rsds: float | None) -> RunResult:
    stats = tuple(_stat(robust_rsd=r, name=f"x.B.m{i}") for i, r in enumerate(rsds))
    return RunResult(success=True, duration_s=1.0, stats=stats, error_kind=ErrorKind.NONE)


class TestRsdReward:
    def test_stable_scores_one(self) -> None:
        evaluation = EvaluationResult(compile=_compiled(True), run=_ran_with_rsd(0.02))
        assert RsdReward(good=0.05, bad=0.25)(evaluation).value == pytest.approx(1.0)

    def test_noisy_scores_zero(self) -> None:
        evaluation = EvaluationResult(compile=_compiled(True), run=_ran_with_rsd(0.30))
        assert RsdReward(good=0.05, bad=0.25)(evaluation).value == 0.0

    def test_linear_between_thresholds(self) -> None:
        evaluation = EvaluationResult(compile=_compiled(True), run=_ran_with_rsd(0.15))
        # (0.25 - 0.15) / (0.25 - 0.05) = 0.5.
        assert RsdReward(good=0.05, bad=0.25)(evaluation).value == pytest.approx(0.5)

    def test_aggregates_worst_method(self) -> None:
        evaluation = EvaluationResult(compile=_compiled(True), run=_ran_with_rsd(0.02, 0.20))
        # max(0.02, 0.20) = 0.20 -> (0.25 - 0.20) / 0.20 = 0.25.
        assert RsdReward(good=0.05, bad=0.25)(evaluation).value == pytest.approx(0.25)

    def test_missing_rsd_uses_fallback(self) -> None:
        evaluation = EvaluationResult(compile=_compiled(True), run=_ran_with_rsd(None))
        assert RsdReward(on_missing=0.0)(evaluation).value == 0.0

    def test_no_run_uses_fallback(self) -> None:
        evaluation = EvaluationResult(compile=_compiled(False), run=None)
        assert RsdReward(on_missing=0.0)(evaluation).value == 0.0

    def test_requires_good_below_bad(self) -> None:
        with pytest.raises(ValueError):
            RsdReward(good=0.3, bad=0.1)


class TestBuildCompositeRewardWiring:
    def test_all_four_signals_sum_to_one(self) -> None:
        weights = RewardWeights(compile=0.25, runtime=0.25, anti_pattern=0.25, rsd=0.25)
        reward = build_composite_reward(weights, rsd_good=0.05, rsd_bad=0.25)
        evaluation = EvaluationResult(
            compile=_compiled(True),
            run=_ran_with_rsd(0.01),
            metadata={"source": _CLEAN_BENCH},
        )
        result = reward(evaluation)
        assert result.value == pytest.approx(1.0)
        assert set(result.detail["components"]) == {"compile", "runtime", "anti_pattern", "rsd"}
