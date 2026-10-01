"""Reward interface.

A reward maps the *outcome* of evaluating a benchmark (an
:class:`~jmhgen.runner.types.EvaluationResult`) to a scalar signal. This matches the
thesis's ``r_i : C x B -> R``: the candidate benchmark ``b`` for snippet ``c`` has already
been compiled/executed by the runner, and the reward scores that observed behavior.
"""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import Any, Protocol, runtime_checkable

from jmhgen.runner.types import EvaluationResult


@dataclass(frozen=True, slots=True)
class RewardResult:
    """A single reward signal plus a detail payload for logging/analysis."""

    name: str
    value: float
    detail: dict[str, Any] = field(default_factory=dict)


@runtime_checkable
class Reward(Protocol):
    """Callable that scores an :class:`EvaluationResult`.

    Conventionally returns a value in ``[0, 1]`` so that composite weights are
    interpretable as importance factors.
    """

    name: str

    def __call__(self, evaluation: EvaluationResult) -> RewardResult: ...
