"""Unit tests for the performance-mutation layer (no JVM: the runner is mocked).

Covers the statistical detector, the deterministic patch/registry generator (+ a real
``git apply`` round-trip when git is present), the registry loader, the per-candidate scorer
against a fake runner, and the ``r_mutation`` reward mapping.
"""

from __future__ import annotations

import shutil
import subprocess
from pathlib import Path

import pytest

from jmhgen.config.schema import RewardWeights
from jmhgen.mutation import (
    MutantSpec,
    SiteSpec,
    detect_regression,
    generate_from_sites,
    generate_mutants,
    is_benchmarkable_type,
    load_mutants,
    mutants_by_fqcn,
    score_benchmark_mutations,
)
from jmhgen.mutation.detect import (
    regularized_incomplete_beta,
    student_t_sf,
    welch_ttest_greater,
)
from jmhgen.rewards import MutationReward, build_composite_reward
from jmhgen.runner.types import (
    BenchmarkSpec,
    BenchmarkStat,
    CompileResult,
    ErrorKind,
    EvaluationResult,
    JmhOptions,
    RunResult,
)

_FOO = (
    "package com.ex;\n"
    "public class Foo {\n"
    "    public int compute(int a, int b) {\n"
    "        return a + b;\n"
    "    }\n"
    "    public int scale(int a, int b) {\n"
    "        return a * b;\n"
    "    }\n"
    "}\n"
)

_BAR = (
    "package com.ex;\n"
    "public class Bar {\n"
    "    public int add(final int k) { return k; }\n"
    "    public int add(final int a, final int b) { return a + b; }\n"
    "    public long wide(\n"
    "            final long x,\n"
    "            final long y) {\n"
    "        return x + y;\n"
    "    }\n"
    "}\n"
)

_IFACE = "package com.ex;\npublic interface Iface {\n    int go(int x);\n}\n"

_ABSTRACT = (
    "package com.ex;\n"
    "public abstract class Base {\n"
    "    public abstract int go(int x);\n"
    "    public int helper(int x) { return x; }\n"
    "}\n"
)


# --------------------------------------------------------------------------- detector


class TestDetector:
    def test_incomplete_beta_symmetry(self) -> None:
        assert regularized_incomplete_beta(1.0, 1.0, 0.5) == pytest.approx(0.5)
        assert regularized_incomplete_beta(2.0, 2.0, 0.5) == pytest.approx(0.5)

    def test_student_t_sf(self) -> None:
        assert student_t_sf(0.0, 10.0) == pytest.approx(0.5)
        assert student_t_sf(30.0, 8.0) < 1e-4
        # Symmetry: P(T > t) + P(T > -t) == 1.
        assert student_t_sf(1.5, 12.0) + student_t_sf(-1.5, 12.0) == pytest.approx(1.0)

    def test_welch_matches_known_p(self) -> None:
        # Two clearly separated, low-variance samples => tiny p, positive t.
        t, df, p = welch_ttest_greater([5.0, 5.1, 4.9, 5.0], [1.0, 1.1, 0.9, 1.0])
        assert t > 0 and df > 0 and p < 1e-3

    def test_avgt_slowdown_detected(self) -> None:
        base = [1.0, 1.02, 0.98, 1.0, 1.0]
        armed = [1.6, 1.62, 1.58, 1.6, 1.6]  # ~60% slower (avgt: higher == slower)
        result = detect_regression(base, armed, mode="avgt")
        assert result.detected is True
        assert result.effect_size > 1.1

    def test_no_change_not_detected(self) -> None:
        base = [1.0, 1.02, 0.98, 1.0, 1.0]
        armed = [1.0, 0.99, 1.01, 1.0, 1.02]
        assert detect_regression(base, armed, mode="avgt").detected is False

    def test_throughput_slowdown_detected(self) -> None:
        # thrpt: slower == LOWER score.
        base = [1000.0, 1010.0, 990.0, 1000.0]
        armed = [500.0, 505.0, 495.0, 500.0]
        result = detect_regression(base, armed, mode="thrpt")
        assert result.detected is True
        assert result.effect_size == pytest.approx(base_mean(base) / base_mean(armed), rel=0.05)

    def test_too_few_samples(self) -> None:
        assert detect_regression([1.0], [2.0], mode="avgt").detected is False


def base_mean(xs: list[float]) -> float:
    return sum(xs) / len(xs)


# --------------------------------------------------------------------------- generator


class TestGenerator:
    def _tree(self, tmp_path):  # type: ignore[no-untyped-def]
        src = tmp_path / "src" / "main" / "java" / "com" / "ex"
        src.mkdir(parents=True)
        (src / "Foo.java").write_text(_FOO, encoding="utf-8")
        return tmp_path / "src" / "main" / "java"

    def test_plan_structure(self, tmp_path) -> None:  # type: ignore[no-untyped-def]
        src_main = self._tree(tmp_path)
        plan = generate_mutants(src_main, "com.ex", count=2)
        assert len(plan.selected) == 2
        assert "MutationSwitch.tick(1)" in plan.patch
        assert "MutationSwitch.tick(2)" in plan.patch
        assert "com/ex/jmhbench/MutationSwitch.java" in plan.patch
        mutants = load_mutants_from_text(plan.registry_yaml, tmp_path)
        assert {m.fqcn for m in mutants} == {"com.ex.Foo"}
        assert {m.method for m in mutants} == {"compute", "scale"}

    def test_deterministic(self, tmp_path) -> None:  # type: ignore[no-untyped-def]
        src_main = self._tree(tmp_path)
        first = generate_mutants(src_main, "com.ex", count=2)
        second = generate_mutants(src_main, "com.ex", count=2)
        assert first.patch == second.patch
        assert first.registry_yaml == second.registry_yaml

    def test_too_few_sites_raises(self, tmp_path) -> None:  # type: ignore[no-untyped-def]
        src_main = self._tree(tmp_path)
        with pytest.raises(ValueError):
            generate_mutants(src_main, "com.ex", count=99)

    @pytest.mark.skipif(shutil.which("git") is None, reason="git required for apply round-trip")
    def test_patch_applies(self, tmp_path) -> None:  # type: ignore[no-untyped-def]
        src_main = self._tree(tmp_path)
        plan = generate_mutants(src_main, "com.ex", count=2)
        patch_file = tmp_path / "mutations.patch"
        patch_file.write_text(plan.patch, encoding="utf-8")

        project = tmp_path / "proj"
        (project / "src" / "main" / "java").mkdir(parents=True)
        shutil.copytree(src_main, project / "src" / "main" / "java", dirs_exist_ok=True)
        subprocess.run(["git", "init", "-q"], cwd=project, check=True)
        rc = subprocess.run(
            [
                "git",
                "apply",
                "--unsafe-paths",
                "--whitespace=nowarn",
                "--directory=src/main/java",
                str(patch_file),
            ],
            cwd=project,
            capture_output=True,
            text=True,
        )
        assert rc.returncode == 0, rc.stderr
        switch = project / "src/main/java/com/ex/jmhbench/MutationSwitch.java"
        foo = (project / "src/main/java/com/ex/Foo.java").read_text()
        assert switch.exists()
        assert "com.ex.jmhbench.MutationSwitch.tick(1)" in foo


class TestCuratedGenerator:
    def _tree(self, tmp_path):  # type: ignore[no-untyped-def]
        src = tmp_path / "src" / "main" / "java" / "com" / "ex"
        src.mkdir(parents=True)
        (src / "Foo.java").write_text(_FOO, encoding="utf-8")
        (src / "Bar.java").write_text(_BAR, encoding="utf-8")
        (src / "Iface.java").write_text(_IFACE, encoding="utf-8")
        (src / "Base.java").write_text(_ABSTRACT, encoding="utf-8")
        return tmp_path / "src" / "main" / "java"

    def test_resolves_curated_sites(self, tmp_path) -> None:  # type: ignore[no-untyped-def]
        src_main = self._tree(tmp_path)
        specs = [SiteSpec("com.ex.Foo", "compute"), SiteSpec("com.ex.Foo", "scale")]
        plan = generate_from_sites(src_main, "com.ex", specs)
        assert len(plan.selected) == 2
        assert "MutationSwitch.tick(1)" in plan.patch
        assert "MutationSwitch.tick(2)" in plan.patch
        mutants = load_mutants_from_text(plan.registry_yaml, tmp_path)
        # order is preserved from the curated list
        assert [m.method for m in mutants] == ["compute", "scale"]

    def test_overload_match_disambiguates(self, tmp_path) -> None:  # type: ignore[no-untyped-def]
        src_main = self._tree(tmp_path)
        specs = [SiteSpec("com.ex.Bar", "add", match="(final int k)")]
        plan = generate_from_sites(src_main, "com.ex", specs)
        mutant = load_mutants_from_text(plan.registry_yaml, tmp_path)[0]
        assert mutant.method == "add"
        assert "(final int k)" in mutant.signature
        assert "final int a" not in mutant.signature

    def test_multiline_signature_resolves(self, tmp_path) -> None:  # type: ignore[no-untyped-def]
        src_main = self._tree(tmp_path)
        plan = generate_from_sites(src_main, "com.ex", [SiteSpec("com.ex.Bar", "wide")])
        assert "MutationSwitch.tick(1)" in plan.patch
        assert load_mutants_from_text(plan.registry_yaml, tmp_path)[0].method == "wide"

    def test_unresolved_method_raises(self, tmp_path) -> None:  # type: ignore[no-untyped-def]
        src_main = self._tree(tmp_path)
        with pytest.raises(ValueError, match="could not resolve"):
            generate_from_sites(src_main, "com.ex", [SiteSpec("com.ex.Foo", "nope")])

    def test_missing_class_raises(self, tmp_path) -> None:  # type: ignore[no-untyped-def]
        src_main = self._tree(tmp_path)
        with pytest.raises(ValueError, match="not found"):
            generate_from_sites(src_main, "com.ex", [SiteSpec("com.ex.Ghost", "x")])

    def test_rejects_non_benchmarkable(self, tmp_path) -> None:  # type: ignore[no-untyped-def]
        src_main = self._tree(tmp_path)
        with pytest.raises(ValueError, match="not benchmarkable"):
            generate_from_sites(src_main, "com.ex", [SiteSpec("com.ex.Iface", "go")])
        with pytest.raises(ValueError, match="not benchmarkable"):
            generate_from_sites(src_main, "com.ex", [SiteSpec("com.ex.Base", "helper")])

    def test_is_benchmarkable_type(self) -> None:
        assert is_benchmarkable_type(_FOO, "Foo") is True
        assert is_benchmarkable_type(_IFACE, "Iface") is False
        assert is_benchmarkable_type(_ABSTRACT, "Base") is False


def load_mutants_from_text(text: str, tmp_path):  # type: ignore[no-untyped-def]
    path = tmp_path / "mutants.yaml"
    path.write_text(text, encoding="utf-8")
    return load_mutants(path)


# --------------------------------------------------------------------------- registry


class TestRegistry:
    def test_group_by_fqcn(self) -> None:
        mutants = [
            MutantSpec(1, "a/B.java", "a.B", "a", "m1", "sig", 1),
            MutantSpec(2, "a/B.java", "a.B", "a", "m2", "sig", 2),
            MutantSpec(3, "a/C.java", "a.C", "a", "m", "sig", 3),
        ]
        grouped = mutants_by_fqcn(mutants)
        assert set(grouped) == {"a.B", "a.C"}
        assert [m.id for m in grouped["a.B"]] == [1, 2]

    def test_load_missing_returns_empty(self, tmp_path) -> None:  # type: ignore[no-untyped-def]
        assert load_mutants(tmp_path / "nope.yaml") == []


# --------------------------------------------------------------------------- scorer


class _FakeRunner:
    """A JmhRunner stand-in: returns per-mutant armed samples keyed off the arm jvm-arg."""

    def __init__(
        self,
        armed: dict[int, list[float]],
        *,
        base: list[float],
        mode: str = "avgt",
        name: str = "com.ex.FooBenchmark.measure",
        timeout_ids: frozenset[int] = frozenset(),
        fail_ids: frozenset[int] = frozenset(),
    ) -> None:
        self.armed = armed
        self.base = base
        self.mode = mode
        self.name = name
        self.timeout_ids = timeout_ids
        self.fail_ids = fail_ids

    def is_available(self) -> bool:
        return True

    def compile(self, spec: BenchmarkSpec) -> CompileResult:
        return CompileResult(success=True, duration_s=0.0, artifact_path="/tmp/benchmarks.jar")

    def _armed_id(self, options: JmhOptions) -> int | None:
        for arg in options.jvm_args:
            if arg.startswith("-Djmhbench.mutant="):
                return int(arg.split("=", 1)[1])
        return None

    def run(self, spec: BenchmarkSpec, options: JmhOptions) -> RunResult:
        mid = self._armed_id(options)
        if mid in self.timeout_ids:
            return RunResult(success=False, duration_s=0.0, error_kind=ErrorKind.TIMEOUT)
        if mid in self.fail_ids:
            return RunResult(success=False, duration_s=0.0, error_kind=ErrorKind.RUNTIME_ERROR)
        samples = self.armed.get(mid, self.base) if mid is not None else self.base
        stat = BenchmarkStat(
            benchmark=self.name,
            mode=self.mode,
            score=base_mean(samples),
            score_error=0.0,
            unit="ns/op",
            raw_measurements=tuple(samples),
        )
        return RunResult(success=True, duration_s=0.0, stats=(stat,))


class _CoverageFakeRunner(_FakeRunner):
    """Fake runner that writes MutationSwitch record files per benchmark method."""

    def __init__(
        self,
        armed: dict[tuple[int, str], list[float]],
        *,
        base: list[float],
        coverage: dict[str, dict[int, int]],
        timeout: frozenset[tuple[int, str]] = frozenset(),
    ) -> None:
        super().__init__({}, base=base)
        self.coverage = coverage
        self.armed_by_method = armed
        self.timeout = timeout
        self.calls: list[JmhOptions] = []

    @staticmethod
    def _method(options: JmhOptions) -> str:
        benchmark_filter = options.benchmark_filter or ""
        for name in ("measureFast", "measureSlow"):
            if name in benchmark_filter:
                return name
        return "measureFast"

    def run(self, spec: BenchmarkSpec, options: JmhOptions) -> RunResult:
        self.calls.append(options)
        method = self._method(options)
        record_file = next(
            (
                Path(arg.split("=", 1)[1])
                for arg in options.jvm_args
                if arg.startswith("-Djmhbench.record.file=")
            ),
            None,
        )
        if record_file is not None:
            record_file.write_text(
                "".join(
                    f"{mutant_id} {hits}\n" for mutant_id, hits in self.coverage[method].items()
                )
            )
            return RunResult(success=True, duration_s=0.0)

        mutant_id = self._armed_id(options)
        if mutant_id is not None and (mutant_id, method) in self.timeout:
            return RunResult(success=False, duration_s=0.0, error_kind=ErrorKind.TIMEOUT)
        samples = self.armed_by_method.get((mutant_id or 0, method), self.base)
        return RunResult(
            success=True,
            duration_s=0.0,
            stats=(
                BenchmarkStat(
                    benchmark=f"com.ex.FooBenchmark.{method}",
                    mode="avgt",
                    score=base_mean(samples),
                    score_error=0.0,
                    unit="ns/op",
                    raw_measurements=tuple(samples),
                ),
            ),
        )


def _spec() -> BenchmarkSpec:
    return BenchmarkSpec(source="// bench", class_name="FooBenchmark", package="com.ex")


def _baseline_stats(base: list[float], name: str = "com.ex.FooBenchmark.measure") -> tuple:
    return (
        BenchmarkStat(
            benchmark=name,
            mode="avgt",
            score=base_mean(base),
            score_error=0.0,
            unit="ns/op",
            raw_measurements=tuple(base),
        ),
    )


def _coverage_spec() -> BenchmarkSpec:
    return BenchmarkSpec(
        source=(
            "package com.ex;\n"
            "import org.openjdk.jmh.annotations.Benchmark;\n"
            "public class FooBenchmark {\n"
            "  @Benchmark public int measureFast() { return 1; }\n"
            "  @Benchmark public int measureSlow() { return 2; }\n"
            "}\n"
        ),
        class_name="FooBenchmark",
        package="com.ex",
    )


def _coverage_baseline() -> tuple[BenchmarkStat, ...]:
    return tuple(
        BenchmarkStat(
            benchmark=f"com.ex.FooBenchmark.{method}",
            mode="avgt",
            score=1.0,
            score_error=0.0,
            unit="ns/op",
            raw_measurements=(1.0,) * 5,
        )
        for method in ("measureFast", "measureSlow")
    )


def _mutants(n: int) -> list[MutantSpec]:
    return [
        MutantSpec(i, "com/ex/Foo.java", "com.ex.Foo", "(root)", f"m{i}", "sig", i)
        for i in range(1, n + 1)
    ]


class TestScorer:
    def test_partial_kill(self) -> None:
        base = [1.0, 1.0, 1.0, 1.0, 1.0]
        # Mutant 1 slows things down; mutant 2 does not.
        runner = _FakeRunner({1: [2.0] * 5, 2: [1.0, 1.01, 0.99, 1.0, 1.0]}, base=base)
        score = score_benchmark_mutations(
            runner, _spec(), JmhOptions(), _baseline_stats(base), _mutants(2)
        )
        assert score.total == 2
        assert score.killed == 1
        assert score.score == pytest.approx(0.5)

    def test_timeout_counts_as_kill(self) -> None:
        base = [1.0] * 5
        runner = _FakeRunner({}, base=base, timeout_ids=frozenset({1}))
        score = score_benchmark_mutations(
            runner, _spec(), JmhOptions(), _baseline_stats(base), _mutants(1)
        )
        assert score.killed == 1
        assert score.score == pytest.approx(1.0)
        assert score.verdicts[0]["reason"] == "timeout"

    def test_no_target_mutants_is_none(self) -> None:
        base = [1.0] * 5
        runner = _FakeRunner({}, base=base)
        score = score_benchmark_mutations(runner, _spec(), JmhOptions(), _baseline_stats(base), [])
        assert score.score is None
        assert score.total == 0

    def test_no_usable_baseline(self) -> None:
        runner = _FakeRunner({}, base=[1.0] * 5)
        score = score_benchmark_mutations(runner, _spec(), JmhOptions(), (), _mutants(2))
        assert score.score == 0.0
        assert score.error == "no_usable_baseline"

    def test_respects_max_mutants(self) -> None:
        base = [1.0] * 5
        runner = _FakeRunner({i: [2.0] * 5 for i in range(1, 6)}, base=base)
        score = score_benchmark_mutations(
            runner, _spec(), JmhOptions(), _baseline_stats(base), _mutants(5), max_mutants=2
        )
        # The strict training rate uses only the two armed mutants; full-class coverage remains
        # descriptive metadata.
        assert score.total == 5
        assert score.attempted == 2
        assert score.killed == 2
        assert score.score == pytest.approx(1.0)
        assert score.sampled_kill_rate == pytest.approx(1.0)
        assert score.graded_score == pytest.approx(0.4)

    def test_gate_mode_any_kill_is_one(self) -> None:
        base = [1.0] * 5
        # Mutant 1 slows down, mutant 2 does not: graded would be 0.5, gate is 1.0.
        runner = _FakeRunner({1: [2.0] * 5, 2: [1.0, 1.01, 0.99, 1.0, 1.0]}, base=base)
        score = score_benchmark_mutations(
            runner, _spec(), JmhOptions(), _baseline_stats(base), _mutants(2), mode="gate"
        )
        assert score.killed == 1
        assert score.mode == "gate"
        assert score.graded_score == pytest.approx(0.5)
        assert score.score == pytest.approx(1.0)

    def test_gate_mode_no_kill_is_zero(self) -> None:
        base = [1.0] * 5
        runner = _FakeRunner({1: [1.0, 1.01, 0.99, 1.0, 1.0]}, base=base)
        score = score_benchmark_mutations(
            runner, _spec(), JmhOptions(), _baseline_stats(base), _mutants(1), mode="gate"
        )
        assert score.killed == 0
        assert score.score == pytest.approx(0.0)
        assert score.graded_score == pytest.approx(0.0)

    def test_coverage_guidance_records_statuses_and_selects_methods(self) -> None:
        runner = _CoverageFakeRunner(
            {
                (1, "measureFast"): [2.0] * 5,
                (2, "measureFast"): [1.0] * 5,
                (2, "measureSlow"): [1.0] * 5,
            },
            base=[1.0] * 5,
            coverage={
                "measureFast": {1: 100, 2: 1},
                "measureSlow": {2: 10},
            },
        )
        score = score_benchmark_mutations(
            runner,
            _coverage_spec(),
            JmhOptions(),
            _coverage_baseline(),
            _mutants(3),
            coverage_guidance=True,
            coverage_options=JmhOptions(warmup_iterations=0, measurement_iterations=1),
            mode="coverage_aware",
            max_benchmarks_per_mutant=1,
        )

        assert score.attempted == 3
        assert score.killed == 1
        assert score.not_covered == 1
        assert score.covered_not_killed == 1
        assert score.killed_statistical == 1
        assert score.killed_timeout == 0
        assert score.coverage_rate == pytest.approx(2 / 3)
        assert score.conditional_kill_rate == pytest.approx(1 / 2)
        assert score.sampled_kill_rate == pytest.approx(1 / 3)
        assert score.score == pytest.approx(0.55)
        statuses = {verdict["id"]: verdict["status"] for verdict in score.verdicts}
        assert statuses == {1: "killed_statistical", 2: "covered_not_killed", 3: "not_covered"}

        armed_filters = [
            options.benchmark_filter
            for options in runner.calls
            if any(arg.startswith("-Djmhbench.mutant=") for arg in options.jvm_args)
        ]
        assert len(armed_filters) == 2
        assert all(
            benchmark_filter
            and ("measureFast" in benchmark_filter or "measureSlow" in benchmark_filter)
            for benchmark_filter in armed_filters
        )

    def test_coverage_guidance_treats_timeout_as_separate_kill(self) -> None:
        runner = _CoverageFakeRunner(
            {},
            base=[1.0] * 5,
            coverage={"measureFast": {1: 10}, "measureSlow": {}},
            timeout=frozenset({(1, "measureFast")}),
        )
        score = score_benchmark_mutations(
            runner,
            _coverage_spec(),
            JmhOptions(),
            _coverage_baseline(),
            _mutants(1),
            coverage_guidance=True,
            coverage_options=JmhOptions(),
        )
        assert score.killed == 1
        assert score.killed_timeout == 1
        assert score.killed_statistical == 0
        assert score.verdicts[0]["status"] == "killed_timeout"


# --------------------------------------------------------------------------- reward


class TestMutationReward:
    def test_reads_metadata_score(self) -> None:
        evaluation = EvaluationResult(
            compile=CompileResult(success=True, duration_s=0.0),
            metadata={"mutation": {"score": 0.75, "total": 4, "killed": 3}},
        )
        result = MutationReward()(evaluation)
        assert result.value == pytest.approx(0.75)

    def test_missing_score_uses_on_missing(self) -> None:
        evaluation = EvaluationResult(compile=CompileResult(success=True, duration_s=0.0))
        assert MutationReward(on_missing=0.0)(evaluation).value == 0.0
        # score is None (no in-class mutants) also falls back.
        evaluation2 = EvaluationResult(
            compile=CompileResult(success=True, duration_s=0.0),
            metadata={"mutation": {"score": None, "total": 0}},
        )
        assert MutationReward(on_missing=0.0)(evaluation2).value == 0.0

    def test_composite_wires_mutation(self) -> None:
        weights = RewardWeights(compile=0.5, runtime=0.0, mutation=0.5)
        reward = build_composite_reward(weights)
        evaluation = EvaluationResult(
            compile=CompileResult(success=True, duration_s=0.0),
            run=RunResult(success=True, duration_s=0.0, stats=()),
            metadata={"mutation": {"score": 1.0, "total": 2, "killed": 2}},
        )
        result = reward(evaluation)
        assert "mutation" in result.detail["components"]
        assert result.value == pytest.approx(0.5 * 1.0 + 0.5 * 1.0)
