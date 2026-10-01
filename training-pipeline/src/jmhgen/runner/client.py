"""The ``RunnerClient`` seam: *where* reward evaluation happens.

A backend (:class:`~jmhgen.runner.base.JmhRunner`) decides *how* to build and run a
benchmark. A client decides *where*: in-process today (:class:`LocalRunnerClient`), on a
separate CPU fleet tomorrow (:class:`RemoteRunnerClient`). Training code depends only on
this interface and on the JSON-serializable :class:`RewardRequest` / :class:`RewardResponse`
contract, so reward execution can be relocated without any change to the training loop.

This separation is what enables the latency-mitigation research question: JMH rewards are
CPU/JVM-bound and long-tailed, while training is GPU-bound, so the reward stage should be
disaggregatable onto cheap CPU workers.
"""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import Any, Protocol, runtime_checkable

from jmhgen.runner.base import JmhRunner
from jmhgen.runner.types import (
    BenchmarkSpec,
    EvaluationResult,
    JmhOptions,
    _to_jsonable,
)


@dataclass(frozen=True, slots=True)
class RewardRequest:
    """A self-contained, JSON-serializable request to evaluate one benchmark.

    ``need_run=False`` requests a compile-only evaluation (useful for the cheap
    compilation reward without paying for execution).
    """

    spec: BenchmarkSpec
    options: JmhOptions = field(default_factory=JmhOptions)
    need_run: bool = True
    request_id: str | None = None

    def to_dict(self) -> dict[str, Any]:
        return _to_jsonable(self)

    @classmethod
    def from_dict(cls, data: dict[str, Any]) -> RewardRequest:
        return cls(
            spec=BenchmarkSpec.from_dict(data["spec"]),
            options=JmhOptions.from_dict(data.get("options", {})),
            need_run=data.get("need_run", True),
            request_id=data.get("request_id"),
        )


@dataclass(frozen=True, slots=True)
class RewardResponse:
    """A JSON-serializable response bundling the compile/run outcome for a request."""

    evaluation: EvaluationResult
    request_id: str | None = None

    def to_dict(self) -> dict[str, Any]:
        return _to_jsonable(self)

    @classmethod
    def from_dict(cls, data: dict[str, Any]) -> RewardResponse:
        return cls(
            evaluation=EvaluationResult.from_dict(data["evaluation"]),
            request_id=data.get("request_id"),
        )


@runtime_checkable
class RunnerClient(Protocol):
    """Evaluate benchmark(s) somewhere and return the outcome(s)."""

    def evaluate(self, request: RewardRequest) -> RewardResponse: ...

    def evaluate_batch(self, requests: list[RewardRequest]) -> list[RewardResponse]: ...


class LocalRunnerClient:
    """Evaluate benchmarks in-process using a concrete :class:`JmhRunner` backend."""

    def __init__(self, runner: JmhRunner) -> None:
        self.runner = runner

    def evaluate(self, request: RewardRequest) -> RewardResponse:
        compile_result = self.runner.compile(request.spec)
        run_result = None
        if request.need_run and compile_result.success:
            run_result = self.runner.run(request.spec, request.options)
        # Carry the source (and identity) so *static* rewards (e.g. the anti-pattern reward)
        # can score it through the EvaluationResult-only Reward protocol. It is JSON-friendly,
        # so a future RemoteRunnerClient would populate the same field from request.spec.
        return RewardResponse(
            evaluation=EvaluationResult(
                compile=compile_result,
                run=run_result,
                metadata={
                    "source": request.spec.source,
                    "class_name": request.spec.class_name,
                    "package": request.spec.package,
                    "project_dir": compile_result.project_dir,
                    "extra_classpath": list(request.spec.extra_classpath),
                },
            ),
            request_id=request.request_id,
        )

    def evaluate_batch(self, requests: list[RewardRequest]) -> list[RewardResponse]:
        # Serial by design: parallelism belongs to the remote client, which owns a
        # worker pool. Keeping the local client simple makes it ideal for tests/debugging.
        return [self.evaluate(request) for request in requests]


class RemoteRunnerClient:
    """Planned client that dispatches reward evaluation to a remote CPU worker fleet.

    Intended transport: **Ray**. Each worker is a Ray actor wrapping a ``JmhRunner`` and
    holding warm state (pre-resolved code-under-test classpaths, optionally a warm JVM /
    ``mvnd`` pool). A placement group pins these reward actors to CPU nodes while the
    trainer and rollout stay on GPU nodes, so high-latency, long-tail JMH rewards do not
    stall the GPUs.

    Design contract already satisfied by this codebase:
      * Requests/responses are JSON-serializable (:class:`RewardRequest` /
        :class:`RewardResponse`), so they cross the process/machine boundary unchanged.
      * Requests may carry ``spec.project_id`` so a worker can reference a pre-provisioned
        project instead of receiving the whole code-under-test per call.
      * ``evaluate_batch`` is the natural fan-out point onto the actor pool.

    Not implemented yet: this is the documented extension point for the asynchronous,
    off-policy reward serving studied in the thesis. Implementing it must not require any
    change to training code that already depends on :class:`RunnerClient`.
    """

    def __init__(self, address: str | None = None, num_workers: int = 1) -> None:
        self.address = address
        self.num_workers = num_workers

    def evaluate(self, request: RewardRequest) -> RewardResponse:
        raise NotImplementedError(
            "RemoteRunnerClient is a planned Ray-backed extension point; use "
            "LocalRunnerClient for now."
        )

    def evaluate_batch(self, requests: list[RewardRequest]) -> list[RewardResponse]:
        raise NotImplementedError(
            "RemoteRunnerClient is a planned Ray-backed extension point; use "
            "LocalRunnerClient for now."
        )
