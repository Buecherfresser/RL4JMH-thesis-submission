"""Unit tests for SpotJMHBugs integration (no vendored SpotBugs required)."""

from __future__ import annotations

from pathlib import Path

import pytest

from jmhgen.rewards.antipattern import AntiPatternReward
from jmhgen.rewards.spotjmhbugs import (
    COSTA_CATEGORIES,
    gate_invo_by_runtime,
    score_from_result,
)
from jmhgen.runner.types import (
    BenchmarkStat,
    CompileResult,
    ErrorKind,
    EvaluationResult,
    RunResult,
)

_SAMPLE_XML = """<?xml version="1.0" encoding="UTF-8"?>
<BugCollection version="4.0" release="4.9.6">
  <BugInstance type="JMH_IGNORED_METHOD_RETURN" priority="1"/>
  <BugInstance type="JMH_LOOP_INSIDE_BENCHMARK" priority="1"/>
  <BugInstance type="JMH_BENCHMARK_METHOD_FOUND" priority="3"/>
  <BugInstance type="DLS_DEAD_LOCAL_STORE" priority="2"/>
</BugCollection>
"""


def test_parse_report_maps_detectors_to_costa_categories(tmp_path: Path) -> None:
    report = tmp_path / "report.xml"
    report.write_text(_SAMPLE_XML, encoding="utf-8")
    from jmhgen.rewards import spotjmhbugs as sj

    result = sj._parse_report(report)
    assert result.available is True
    assert "JMH_IGNORED_METHOD_RETURN" in result.violations["RETU"]
    assert "JMH_LOOP_INSIDE_BENCHMARK" in result.violations["LOOP"]
    assert result.categories_hit == 2
    assert result.total == 2
    assert "JMH_BENCHMARK_METHOD_FOUND" not in result.raw_detectors


def test_score_from_result_grades_by_costa_category() -> None:
    clean = {
        "available": True,
        "categories_hit": 0,
        "violations": {c: [] for c in COSTA_CATEGORIES},
    }
    one_hit = {
        "available": True,
        "categories_hit": 1,
        "violations": {
            "RETU": ["JMH_IGNORED_METHOD_RETURN"],
            "LOOP": [],
            "FINAL": [],
            "INVO": [],
            "FORK": [],
        },
    }
    assert score_from_result(clean) == pytest.approx(1.0)
    assert score_from_result(one_hit) == pytest.approx(0.8)


def test_gate_invo_drops_slow_running_fixture_flag() -> None:
    base = {
        "available": True,
        "violations": {
            "INVO": ["JMH_FIXTURE_USING_INVOCATION_SCOPE"],
            "RETU": [],
            "LOOP": [],
            "FINAL": [],
            "FORK": [],
        },
        "total": 1,
        "categories_hit": 1,
    }
    gated = gate_invo_by_runtime(dict(base), per_op_ns=2_000_000)
    assert gated["violations"]["INVO"] == []
    assert gated["total"] == 0
    assert gated.get("invo_runtime_gated") is True

    unchanged = gate_invo_by_runtime(dict(base), per_op_ns=500_000)
    assert unchanged["total"] == 1


def test_anti_pattern_reward_uses_spotjmh_when_project_dir_present(monkeypatch) -> None:
    monkeypatch.setattr("jmhgen.rewards.antipattern.spotjmh_available", lambda: True)

    class _FakeSj:
        available = True
        error = None

        def to_dict(self) -> dict[str, object]:
            return {
                "available": True,
                "violations": {c: [] for c in COSTA_CATEGORIES},
                "raw_detectors": {},
                "total": 0,
                "categories_hit": 0,
                "error": None,
            }

    monkeypatch.setattr("jmhgen.rewards.antipattern.run_spotjmh", lambda *_a, **_k: _FakeSj())

    evaluation = EvaluationResult(
        compile=CompileResult(
            success=True,
            duration_s=1.0,
            project_dir="/tmp/bench-abc",
            error_kind=ErrorKind.NONE,
        ),
        run=RunResult(
            success=True,
            duration_s=1.0,
            stats=(BenchmarkStat("x.B.m", "avgt", 2.0, 0.0, "ns/op", robust_rsd=0.01),),
            error_kind=ErrorKind.NONE,
        ),
        metadata={"source": "public class B {}", "extra_classpath": []},
    )
    result = AntiPatternReward()(evaluation)
    assert result.value == pytest.approx(1.0)
    assert result.detail["source"] == "spotjmhbugs"


def test_anti_pattern_reward_falls_back_to_regex_when_spotjmh_missing(monkeypatch) -> None:
    monkeypatch.setattr("jmhgen.rewards.antipattern.spotjmh_available", lambda: False)
    source = (
        "@State(Scope.Thread)\n@BenchmarkMode(Mode.AverageTime)\n"
        "@Fork(1)\n@Warmup(iterations = 1)\n@Measurement(iterations = 1)\n"
        "public class B {\n"
        "    private int n;\n"
        "    @Setup\n    public void s() { n = 1; }\n"
        "    @Benchmark\n    public int m() { return Foo.f(n); }\n}"
    )
    evaluation = EvaluationResult(
        compile=CompileResult(success=True, duration_s=1.0, error_kind=ErrorKind.NONE),
        metadata={"source": source},
    )
    result = AntiPatternReward()(evaluation)
    assert result.detail["source"] == "regex"
    assert result.value == pytest.approx(1.0)
