"""Reward functions built on top of the JMH runner's evaluation outcomes."""

from jmhgen.rewards.antipattern import (
    ANTI_PATTERNS,
    AntiPatternResult,
    AntiPatternReward,
    check_anti_patterns,
)
from jmhgen.rewards.base import Reward, RewardResult
from jmhgen.rewards.compile import CompileReward
from jmhgen.rewards.composite import (
    CompositeReward,
    build_composite_reward,
    default_cheap_reward,
)
from jmhgen.rewards.mutation import MutationReward
from jmhgen.rewards.rsd import RsdReward
from jmhgen.rewards.runtime import RuntimeReward

__all__ = [
    "ANTI_PATTERNS",
    "AntiPatternResult",
    "AntiPatternReward",
    "CompileReward",
    "CompositeReward",
    "MutationReward",
    "Reward",
    "RewardResult",
    "RsdReward",
    "RuntimeReward",
    "build_composite_reward",
    "check_anti_patterns",
    "default_cheap_reward",
]
