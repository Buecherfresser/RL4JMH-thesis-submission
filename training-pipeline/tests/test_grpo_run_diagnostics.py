"""Run-failure diagnostics must name the JMH lock, because run 7 could not."""

from __future__ import annotations

from jmhgen.rewards.grpo_adapter import _run_diagnostics
from jmhgen.runner.types import CompileResult, ErrorKind, EvaluationResult, RunResult

_LOCK = (
    "ERROR: org.openjdk.jmh.runner.RunnerException: ERROR: Another JMH instance might be "
    "running. Unable to acquire the JMH lock (/tmp/jmh.lock), exiting. Use "
    "-Djmh.ignoreLock=true to forcefully continue.\n"
    "\tat org.openjdk.jmh.runner.Runner.run(Runner.java:210)\n"
)
_THREW = (
    "# Run progress: 0.00% complete\n"
    "<failure>\n"
    "java.lang.NullPointerException: Cannot invoke \"p.Foo.bar()\" because \"this.f\" is null\n"
    "\tat p.FooBenchmark.measure(FooBenchmark.java:21)\n"
)


def _ev(stdout: str, *, success: bool = False) -> EvaluationResult:
    return EvaluationResult(
        compile=CompileResult(success=True, duration_s=1.0, error_kind=ErrorKind.NONE),
        run=RunResult(
            success=success,
            duration_s=1.0,
            stdout=stdout,
            error_kind=ErrorKind.NONE if success else ErrorKind.RUNTIME_ERROR,
        ),
    )


def test_lock_failure_is_named() -> None:
    diags = _run_diagnostics(_ev(_LOCK))
    assert any("Unable to acquire the JMH lock" in d for d in diags)


def test_benchmark_exception_is_named_and_distinguishable_from_the_lock() -> None:
    diags = _run_diagnostics(_ev(_THREW))
    assert any("NullPointerException" in d for d in diags)
    assert not any("jmh.lock" in d for d in diags)


def test_successful_run_yields_nothing() -> None:
    assert _run_diagnostics(_ev(_THREW, success=True)) == ()


def test_no_run_yields_nothing() -> None:
    ev = EvaluationResult(
        compile=CompileResult(success=False, duration_s=1.0, error_kind=ErrorKind.COMPILE_ERROR),
        run=None,
    )
    assert _run_diagnostics(ev) == ()


def test_output_is_bounded() -> None:
    noisy = "\n".join(f"java.lang.IllegalStateException: distinct {i}" for i in range(50))
    diags = _run_diagnostics(_ev(noisy))
    assert 0 < len(diags) <= 4
    assert all(len(d) <= 200 for d in diags)
