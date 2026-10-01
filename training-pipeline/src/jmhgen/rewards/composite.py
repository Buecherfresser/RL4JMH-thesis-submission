"""Composite reward: the weighted sum ``R(c, b) = sum_i w_i * r_i(c, b)``.

Implemented signals, each a :class:`~jmhgen.rewards.base.Reward`:

    * ``compile``  - compiles/packages, tau < 1s
    * ``runtime``  - JMH ran to completion, tau ~ seconds
    * ``anti``     - anti-pattern compliance (Costa et al.), static, tau < 1s
    * ``rsd``      - measurement stability via robust RSD, needs a run
    * ``mutation`` - performance-mutation detection rate, needs a run per armed mutant so its
      score is precomputed in the RFT verify phase and read back from the evaluation metadata

Because the run-dependent signals dominate reward latency, they are exactly what a
``RemoteRunnerClient`` would offload onto a CPU worker fleet.
"""

from __future__ import annotations

from collections.abc import Iterable
from typing import TYPE_CHECKING

from jmhgen.rewards.antipattern import AntiPatternReward, Backend
from jmhgen.rewards.base import Reward, RewardResult
from jmhgen.rewards.compile import CompileReward
from jmhgen.rewards.mutation import MutationReward
from jmhgen.rewards.rsd import RsdReward
from jmhgen.rewards.runtime import RuntimeReward
from jmhgen.runner.types import EvaluationResult
from jmhgen.utils.logging import get_logger

if TYPE_CHECKING:  # pragma: no cover - type-only import (avoids a config<->rewards cycle)
    from jmhgen.config.schema import RewardWeights

logger = get_logger("jmhgen.rewards.composite")


class CompositeReward:
    """Weighted sum of component rewards.

    Args:
        components: pairs of ``(reward, weight)``. Weights are importance factors and are
            not normalized, mirroring the proposal's definition.
    """

    name = "composite"

    def __init__(self, components: Iterable[tuple[Reward, float]]) -> None:
        self.components: list[tuple[Reward, float]] = list(components)

    def __call__(self, evaluation: EvaluationResult) -> RewardResult:
        total = 0.0
        component_details: dict[str, dict] = {}
        for reward, weight in self.components:
            result = reward(evaluation)
            total += weight * result.value
            component_details[result.name] = {
                "value": result.value,
                "weight": weight,
                "weighted": weight * result.value,
                "detail": result.detail,
            }
        return RewardResult(
            name=self.name,
            value=total,
            detail={"total": total, "components": component_details},
        )


def default_cheap_reward(
    compile_weight: float = 0.5, runtime_weight: float = 0.5
) -> CompositeReward:
    """A composite of the two cheap, sub-second signals available today.

    This is a sensible default for the early training stages and for smoke-testing the
    reward path before the expensive signals are wired in.
    """
    return CompositeReward(
        [
            (CompileReward(), compile_weight),
            (RuntimeReward(), runtime_weight),
        ]
    )


# Reward components that are documented extension points but not implemented yet; a non-zero
# weight on any of them is silently dropped (with a warning) rather than scored as 0. Empty now
# that the mutation signal is wired in (its score is precomputed in the RFT verify phase).
_UNIMPLEMENTED_SIGNALS: tuple[str, ...] = ()


def build_composite_reward(
    weights: RewardWeights,
    *,
    rsd_good: float = 0.05,
    rsd_bad: float = 0.25,
    anti_pattern_backend: Backend = "auto",
) -> CompositeReward:
    """Build a :class:`CompositeReward` from configured :class:`RewardWeights`.

    Wires in every implemented signal carrying weight (compile, runtime, anti-pattern, RSD,
    mutation). A non-zero weight on a not-yet-implemented signal is warned about and ignored, so
    a stale config cannot silently zero-out the reward. Falls back to the cheap default when no
    implemented signal carries weight. ``rsd_good``/``rsd_bad`` set the RSD->[0,1] mapping (see
    :class:`~jmhgen.rewards.rsd.RsdReward`); the mutation score is computed upstream and read
    from the evaluation metadata (see :class:`~jmhgen.rewards.mutation.MutationReward`).

    ``anti_pattern_backend`` selects the anti-pattern implementation ("auto"/"regex"/"spotjmh").
    Online RL wants "regex" — the static pre-screen adds no per-rollout JVM/SpotBugs latency.
    """
    for name in _UNIMPLEMENTED_SIGNALS:
        if getattr(weights, name, 0.0):
            logger.warning(
                "reward weight %s=%.3f is set but %s is not implemented yet; ignoring it",
                name,
                getattr(weights, name),
                name,
            )

    components: list[tuple[Reward, float]] = []
    if weights.compile:
        components.append((CompileReward(), weights.compile))
    if weights.runtime:
        components.append((RuntimeReward(), weights.runtime))
    if weights.anti_pattern:
        components.append((AntiPatternReward(backend=anti_pattern_backend), weights.anti_pattern))
    if weights.rsd:
        components.append((RsdReward(good=rsd_good, bad=rsd_bad), weights.rsd))
    if weights.mutation:
        components.append((MutationReward(), weights.mutation))
    if not components:
        logger.warning("no implemented reward weight is non-zero; using default cheap reward")
        return default_cheap_reward()
    return CompositeReward(components)
