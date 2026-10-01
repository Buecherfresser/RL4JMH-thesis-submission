"""Mutation-detection reward (``r_mutation``): does the benchmark catch real slowdowns?

Unlike the other signals, the mutation score cannot be read off a single
:class:`~jmhgen.runner.types.EvaluationResult`: it needs one extra JMH run per armed mutant
(see :mod:`jmhgen.mutation.score`). That multi-run work is done in the RFT verify phase, which
owns the runner, and the result is stashed in ``evaluation.metadata["mutation"]``. This reward
is then a thin, pure reader of that precomputed value — exactly mirroring how
:class:`~jmhgen.rewards.antipattern.AntiPatternReward` reads its source/project_dir from the
evaluation metadata, so the ``Reward`` protocol stays a pure function of an evaluation.
"""

from __future__ import annotations

from jmhgen.rewards.base import RewardResult
from jmhgen.runner.types import EvaluationResult


class MutationReward:
    """``r_mutation``: fraction of the subject class's mutants the benchmark detects.

    Reads ``evaluation.metadata["mutation"]`` (a
    :meth:`~jmhgen.mutation.score.MutationScore.to_dict` payload). Falls back to ``on_missing``
    when no score is present or the target class had no mutants (``score is None``), so
    acceptance requires a *verified* detection signal rather than assuming it.
    """

    name = "mutation"

    def __init__(self, on_missing: float = 0.0) -> None:
        self.on_missing = on_missing

    def __call__(self, evaluation: EvaluationResult) -> RewardResult:
        info = (evaluation.metadata or {}).get("mutation")
        if not isinstance(info, dict) or info.get("score") is None:
            detail = {"error": "no_mutation_score", "source": "mutation"}
            if isinstance(info, dict):
                detail.update(info)
            return RewardResult(name=self.name, value=self.on_missing, detail=detail)
        return RewardResult(
            name=self.name, value=float(info["score"]), detail={**info, "source": "mutation"}
        )
