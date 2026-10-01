"""Runtime-success reward (``r_runtime``: the benchmark executed without error)."""

from __future__ import annotations

from jmhgen.rewards.base import RewardResult
from jmhgen.runner.types import EvaluationResult


class RuntimeReward:
    """Rewards a benchmark that runs to completion and produces at least one result."""

    name = "runtime"

    def __init__(self, on_success: float = 1.0, on_failure: float = 0.0) -> None:
        self.on_success = on_success
        self.on_failure = on_failure

    def __call__(self, evaluation: EvaluationResult) -> RewardResult:
        ok = evaluation.ran
        if evaluation.run is not None:
            error_kind = evaluation.run.error_kind.value
            num_benchmarks = len(evaluation.run.stats)
        else:
            # Not executed (e.g. compile failed, or a compile-only request).
            error_kind = "not_run"
            num_benchmarks = 0
        return RewardResult(
            name=self.name,
            value=self.on_success if ok else self.on_failure,
            detail={
                "success": ok,
                "error_kind": error_kind,
                "num_benchmarks": num_benchmarks,
            },
        )
