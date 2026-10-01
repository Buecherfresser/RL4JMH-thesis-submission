"""Stability reward (``r_RSD``): rewards a benchmark whose measurements are low-variance.

Stability is the robust relative standard deviation of the per-iteration measurements,

    RSD = 1.4826 * median(|x_i - x_med|) / x_med

(a MAD-based, outlier-resistant CV; see :func:`jmhgen.runner.results.robust_rsd`, which the
runner already computes per ``@Benchmark`` method). This reward maps that fraction onto
``[0, 1]``: full reward at/below ``good``, zero at/above ``bad``, linear in between.

A benchmark may now contain several ``@Benchmark`` methods (we encourage covering a class
with multiple benchmarks); by default the reward aggregates by the *worst* (max) RSD across
methods, since a suite is only as trustworthy as its noisiest measurement.
"""

from __future__ import annotations

from statistics import fmean
from typing import Literal

from jmhgen.rewards.base import RewardResult
from jmhgen.runner.types import EvaluationResult

Aggregate = Literal["max", "mean", "min"]


class RsdReward:
    """``r_RSD``: rewards measurement stability via the robust RSD.

    Args:
        good: RSD at/below which the benchmark is considered perfectly stable (reward 1.0).
        bad: RSD at/above which stability is unusable (reward 0.0). Linear in between.
        aggregate: how to combine per-method RSDs (``"max"`` = worst-case, the default).
        on_missing: reward when no RSD is available (no run, or too few measurements). 0.0 by
            default, so acceptance requires *verified* stability rather than assuming it.
    """

    name = "rsd"

    def __init__(
        self,
        good: float = 0.05,
        bad: float = 0.25,
        aggregate: Aggregate = "max",
        on_missing: float = 0.0,
    ) -> None:
        if not 0.0 <= good < bad:
            raise ValueError(f"require 0 <= good < bad, got good={good}, bad={bad}")
        self.good = good
        self.bad = bad
        self.aggregate = aggregate
        self.on_missing = on_missing

    def _map(self, rsd: float) -> float:
        if rsd <= self.good:
            return 1.0
        if rsd >= self.bad:
            return 0.0
        return (self.bad - rsd) / (self.bad - self.good)

    def __call__(self, evaluation: EvaluationResult) -> RewardResult:
        run = evaluation.run
        if run is None:
            return RewardResult(name=self.name, value=self.on_missing, detail={"error": "not_run"})

        per_method = {s.benchmark: s.robust_rsd for s in run.stats if s.robust_rsd is not None}
        if not per_method:
            return RewardResult(
                name=self.name,
                value=self.on_missing,
                detail={"error": "no_rsd", "num_stats": len(run.stats)},
            )

        rsds = list(per_method.values())
        if self.aggregate == "max":
            agg = max(rsds)
        elif self.aggregate == "min":
            agg = min(rsds)
        else:
            agg = fmean(rsds)

        return RewardResult(
            name=self.name,
            value=self._map(agg),
            detail={
                "rsd": agg,
                "aggregate": self.aggregate,
                "good": self.good,
                "bad": self.bad,
                "per_benchmark": per_method,
            },
        )
