"""Integration tests for the Maven JMH runner.

These shell out to a real Maven + JVM toolchain and are skipped when it is unavailable.
The first run may download JMH and Maven plugins into ``~/.m2`` (needs network).
"""

from __future__ import annotations

from pathlib import Path

import pytest

from jmhgen.rewards import default_cheap_reward
from jmhgen.runner.client import LocalRunnerClient, RewardRequest
from jmhgen.runner.maven import MavenJmhRunner
from jmhgen.runner.types import BenchmarkSpec, ErrorKind, JmhOptions

_MAVEN_AVAILABLE = MavenJmhRunner().is_available()

pytestmark = [
    pytest.mark.integration,
    pytest.mark.skipif(not _MAVEN_AVAILABLE, reason="requires mvn + java on PATH"),
]

_FAST_OPTIONS = JmhOptions(
    warmup_iterations=0,
    measurement_iterations=1,
    forks=1,
    warmup_time="1s",
    measurement_time="1s",
)


def _hello_spec(source: str) -> BenchmarkSpec:
    return BenchmarkSpec(source=source, class_name="HelloBenchmark", package="jmhgen.testbench")


def test_compile_and_run_hello(hello_benchmark_source: str, tmp_path: Path) -> None:
    runner = MavenJmhRunner(work_root=tmp_path)
    spec = _hello_spec(hello_benchmark_source)

    compiled = runner.compile(spec)
    assert compiled.success, compiled.stdout[-2000:]
    assert compiled.artifact_path is not None
    assert Path(compiled.artifact_path).exists()

    result = runner.run(spec, _FAST_OPTIONS)
    assert result.success, result.stderr[-2000:]
    assert len(result.stats) == 1
    stat = result.stats[0]
    assert "HelloBenchmark.sumLoop" in stat.benchmark
    assert stat.score > 0


def test_compile_error_is_reported(tmp_path: Path) -> None:
    runner = MavenJmhRunner(work_root=tmp_path)
    broken = BenchmarkSpec(
        source="package jmhgen.testbench; public class Broken { this is not java }",
        class_name="Broken",
        package="jmhgen.testbench",
    )
    compiled = runner.compile(broken)
    assert not compiled.success
    assert compiled.error_kind is ErrorKind.COMPILE_ERROR


def test_local_client_yields_full_reward(hello_benchmark_source: str, tmp_path: Path) -> None:
    client = LocalRunnerClient(MavenJmhRunner(work_root=tmp_path))
    response = client.evaluate(
        RewardRequest(spec=_hello_spec(hello_benchmark_source), options=_FAST_OPTIONS)
    )
    reward = default_cheap_reward()(response.evaluation)
    assert reward.value == pytest.approx(1.0)
