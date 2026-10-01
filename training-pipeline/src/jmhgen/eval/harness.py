"""Evaluation harness — scaffold.

Computes the thesis's evaluation metrics on a held-out set, reusing the *same* runner and
reward definitions as training so that numbers are comparable across the base model and
each trained checkpoint:

    * anti-pattern frequency (Costa et al.)
    * robust RSD distribution
    * mutation-detection rate
    * real-world regression detection (e.g. Apache Flink)
    * false-positive rate under repeated execution

These reuse :class:`~jmhgen.runner.client.RunnerClient` for faithful (high-fidelity)
execution; see :mod:`jmhgen.rewards` for the reward components they build on.
"""

from __future__ import annotations

from collections.abc import Iterable
from dataclasses import dataclass, field
from typing import Any

from jmhgen.data.schema import CodeSnippet
from jmhgen.runner.client import RunnerClient


@dataclass(frozen=True, slots=True)
class EvaluationReport:
    """Aggregate metrics for one model over an evaluation set."""

    model_name: str
    num_snippets: int
    metrics: dict[str, Any] = field(default_factory=dict)


class EvaluationHarness:
    """Run a generated-benchmark suite through the metrics above.

    Args:
        client: runner client used for faithful execution of generated benchmarks.
    """

    def __init__(self, client: RunnerClient) -> None:
        self.client = client

    def evaluate(self, model_name: str, snippets: Iterable[CodeSnippet]) -> EvaluationReport:
        """Generate + execute + score benchmarks for ``snippets``.

        TODO: drive generation with the trained policy, evaluate each benchmark via
        ``self.client``, and aggregate the metrics below into an :class:`EvaluationReport`.
        """
        raise NotImplementedError("evaluation harness is not implemented yet")

    def anti_pattern_frequency(self, benchmark_source: str) -> dict[str, int]:
        """TODO: detect the five JMH bad practices from Costa et al. via static analysis."""
        raise NotImplementedError

    def false_positive_rate(self, snippet: CodeSnippet, benchmark_source: str) -> float:
        """TODO: estimate FPR via repeated execution against an unchanged snippet."""
        raise NotImplementedError
