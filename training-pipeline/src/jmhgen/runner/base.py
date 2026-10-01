"""The ``JmhRunner`` interface: compile a benchmark, then execute it.

A runner is a *backend* — it decides **how** a benchmark is compiled and executed
(Maven today; a lightweight ``javac`` + ``java org.openjdk.jmh.Main`` backend is a
documented future extension). It says nothing about **where** that happens; placement
is handled one layer up by ``jmhgen.runner.client``.
"""

from __future__ import annotations

from typing import Protocol, runtime_checkable

from jmhgen.runner.types import BenchmarkSpec, CompileResult, JmhOptions, RunResult


@runtime_checkable
class JmhRunner(Protocol):
    """Compile and execute JMH benchmarks.

    Implementations must be side-effect-isolated per call (e.g. their own working
    directory) and must never raise for *expected* failures such as a compile error or a
    benchmark timeout — those are reported through :class:`CompileResult` /
    :class:`RunResult`. Exceptions are reserved for genuinely unexpected, internal faults.
    """

    def compile(self, spec: BenchmarkSpec) -> CompileResult:
        """Compile (and package) the benchmark described by ``spec``."""
        ...

    def run(self, spec: BenchmarkSpec, options: JmhOptions) -> RunResult:
        """Execute the (already compiled) benchmark and return parsed results.

        If the benchmark has not been compiled yet, implementations should compile it
        first (or return a :class:`RunResult` with ``error_kind=NOT_COMPILED``).
        """
        ...
