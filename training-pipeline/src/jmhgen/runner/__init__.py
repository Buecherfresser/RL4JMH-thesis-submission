"""JMH Runner: the verifiable-reward engine that compiles and executes benchmarks.

Public surface:
    * Value types: :class:`BenchmarkSpec`, :class:`JmhOptions`, :class:`CompileResult`,
      :class:`RunResult`, :class:`BenchmarkStat`, :class:`EvaluationResult`, :class:`ErrorKind`.
    * Backend interface: :class:`JmhRunner` (+ :class:`MavenJmhRunner`).
    * Placement seam: :class:`RunnerClient` (+ :class:`LocalRunnerClient`,
      :class:`RemoteRunnerClient`) and the :class:`RewardRequest` / :class:`RewardResponse`
      contract.
    * Helpers: :func:`parse_jmh_json`, :func:`robust_rsd`.
"""

from jmhgen.runner.base import JmhRunner
from jmhgen.runner.client import (
    LocalRunnerClient,
    RemoteRunnerClient,
    RewardRequest,
    RewardResponse,
    RunnerClient,
)
from jmhgen.runner.maven import MavenJmhRunner
from jmhgen.runner.results import parse_jmh_json, robust_rsd
from jmhgen.runner.types import (
    BenchmarkSpec,
    BenchmarkStat,
    CompileResult,
    ErrorKind,
    EvaluationResult,
    JmhOptions,
    RunResult,
)

__all__ = [
    "BenchmarkSpec",
    "BenchmarkStat",
    "CompileResult",
    "ErrorKind",
    "EvaluationResult",
    "JmhOptions",
    "JmhRunner",
    "LocalRunnerClient",
    "MavenJmhRunner",
    "RemoteRunnerClient",
    "RewardRequest",
    "RewardResponse",
    "RunResult",
    "RunnerClient",
    "parse_jmh_json",
    "robust_rsd",
]
