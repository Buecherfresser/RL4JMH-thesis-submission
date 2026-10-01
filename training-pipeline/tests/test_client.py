"""Tests for the runner client seam: local dispatch + serializable request/response contract.

Uses a fake in-memory runner so the seam can be tested without a JVM toolchain. This is
exactly the property that lets reward execution move to a remote worker later.
"""

from __future__ import annotations

import json

from jmhgen.runner.client import (
    LocalRunnerClient,
    RewardRequest,
    RewardResponse,
)
from jmhgen.runner.types import (
    BenchmarkSpec,
    BenchmarkStat,
    CompileResult,
    ErrorKind,
    JmhOptions,
    RunResult,
)


class FakeRunner:
    """A JmhRunner that returns canned results and records calls."""

    def __init__(self, *, compile_ok: bool = True, run_ok: bool = True) -> None:
        self.compile_ok = compile_ok
        self.run_ok = run_ok
        self.compiled: list[str] = []
        self.ran: list[str] = []

    def compile(self, spec: BenchmarkSpec) -> CompileResult:
        self.compiled.append(spec.fully_qualified_name)
        return CompileResult(
            success=self.compile_ok,
            duration_s=0.1,
            error_kind=ErrorKind.NONE if self.compile_ok else ErrorKind.COMPILE_ERROR,
        )

    def run(self, spec: BenchmarkSpec, options: JmhOptions) -> RunResult:
        self.ran.append(spec.fully_qualified_name)
        stat = BenchmarkStat(
            benchmark=f"{spec.fully_qualified_name}.m",
            mode="avgt",
            score=1.0,
            score_error=0.0,
            unit="ns/op",
            raw_measurements=(1.0, 1.0),
            robust_rsd=0.0,
        )
        return RunResult(
            success=self.run_ok,
            duration_s=0.2,
            stats=(stat,) if self.run_ok else (),
            error_kind=ErrorKind.NONE if self.run_ok else ErrorKind.RUNTIME_ERROR,
        )


def _spec() -> BenchmarkSpec:
    return BenchmarkSpec(source="class B {}", class_name="B", package="com.example")


class TestLocalRunnerClient:
    def test_compile_and_run(self) -> None:
        runner = FakeRunner()
        client = LocalRunnerClient(runner)
        response = client.evaluate(RewardRequest(spec=_spec()))
        assert response.evaluation.compiled
        assert response.evaluation.ran
        assert runner.ran == ["com.example.B"]

    def test_skips_run_when_compile_fails(self) -> None:
        runner = FakeRunner(compile_ok=False)
        client = LocalRunnerClient(runner)
        response = client.evaluate(RewardRequest(spec=_spec()))
        assert not response.evaluation.compiled
        assert response.evaluation.run is None
        assert runner.ran == []

    def test_compile_only_request(self) -> None:
        runner = FakeRunner()
        client = LocalRunnerClient(runner)
        response = client.evaluate(RewardRequest(spec=_spec(), need_run=False))
        assert response.evaluation.compiled
        assert response.evaluation.run is None
        assert runner.ran == []

    def test_batch(self) -> None:
        client = LocalRunnerClient(FakeRunner())
        responses = client.evaluate_batch([RewardRequest(spec=_spec()) for _ in range(3)])
        assert len(responses) == 3
        assert all(r.evaluation.ran for r in responses)


class TestSerializationContract:
    def test_request_round_trip_through_json(self) -> None:
        request = RewardRequest(
            spec=BenchmarkSpec(
                source="class B {}",
                class_name="B",
                package="com.example",
                extra_classpath=("/lib/a.jar", "/lib/b.jar"),
                project_id="proj-1",
            ),
            options=JmhOptions(forks=2, jvm_args=("-Xmx1g",)),
            need_run=True,
            request_id="req-42",
        )
        restored = RewardRequest.from_dict(json.loads(json.dumps(request.to_dict())))
        assert restored == request

    def test_response_round_trip_through_json(self) -> None:
        client = LocalRunnerClient(FakeRunner())
        response = client.evaluate(RewardRequest(spec=_spec(), request_id="req-1"))
        restored = RewardResponse.from_dict(json.loads(json.dumps(response.to_dict())))
        assert restored == response
