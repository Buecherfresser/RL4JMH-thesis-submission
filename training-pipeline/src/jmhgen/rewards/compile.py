"""Compilation-success reward (``r_compile``, the cheapest verifiable signal)."""

from __future__ import annotations

from jmhgen.rewards.base import RewardResult
from jmhgen.runner.types import EvaluationResult


class CompileReward:
    """Rewards a benchmark that compiles (and packages) successfully."""

    name = "compile"

    def __init__(self, on_success: float = 1.0, on_failure: float = 0.0) -> None:
        self.on_success = on_success
        self.on_failure = on_failure

    def __call__(self, evaluation: EvaluationResult) -> RewardResult:
        ok = evaluation.compiled
        return RewardResult(
            name=self.name,
            value=self.on_success if ok else self.on_failure,
            detail={
                "success": ok,
                "error_kind": evaluation.compile.error_kind.value,
                "duration_s": evaluation.compile.duration_s,
            },
        )
