"""The JMH run command must isolate ``java.io.tmpdir`` per invocation.

JMH takes a global lock at ``$java.io.tmpdir/jmh.lock`` and *aborts* when it cannot acquire it
rather than queueing:

    RunnerException: Another JMH instance might be running. Unable to acquire the JMH lock
    (/tmp/jmh.lock), exiting.

Java ignores ``$TMPDIR`` on Linux, so without an explicit ``-Djava.io.tmpdir`` every JMH process
on a host shares ``/tmp`` -- both training ranks plus any concurrent eval -- and one of each
overlapping pair dies instantly. That surfaced as 40.7 % of run 7's compiled rollouts being
recorded as ``runtime_error`` and scored 0 on runtime, rsd and mutation.

These are pure unit tests on the command builder, so unlike ``test_runner_maven.py`` they need
no Maven or JVM.
"""

from __future__ import annotations

from pathlib import Path

from jmhgen.runner.maven import MavenJmhRunner
from jmhgen.runner.types import BenchmarkSpec, JmhOptions

_SPEC = BenchmarkSpec(source="class B {}", class_name="B", package="p")
_OPTIONS = JmhOptions(
    warmup_iterations=0, measurement_iterations=1, forks=1, warmup_time="1s", measurement_time="1s"
)


def _cmd(jmh_tmp: Path | None) -> list[str]:
    runner = MavenJmhRunner()
    return runner._build_run_command(
        _SPEC, _OPTIONS, Path("/w/target/benchmarks.jar"), Path("/w/jmh-result.json"), jmh_tmp
    )


def test_tmpdir_flag_is_passed_to_the_jvm(tmp_path: Path) -> None:
    cmd = _cmd(tmp_path)
    assert f"-Djava.io.tmpdir={tmp_path}" in cmd


def test_tmpdir_flag_precedes_the_main_class(tmp_path: Path) -> None:
    """A -D after the main class is an argument to JMH, not a JVM system property."""
    cmd = _cmd(tmp_path)
    main = next(i for i, a in enumerate(cmd) if a == "org.openjdk.jmh.Main")
    assert cmd.index(f"-Djava.io.tmpdir={tmp_path}") < main
    # ...and before -cp, so it can never be swallowed as part of the classpath value.
    assert cmd.index(f"-Djava.io.tmpdir={tmp_path}") < cmd.index("-cp")


def test_omitted_when_no_tmpdir_given() -> None:
    assert not any(a.startswith("-Djava.io.tmpdir") for a in _cmd(None))


def test_run_uses_a_fresh_tmpdir_per_invocation(tmp_path: Path, monkeypatch) -> None:
    """Two runs of the *same* spec must not share a lock directory.

    Project directories are content-hashed, so two identical completions in one group resolve to
    the same project dir -- the isolation has to be per invocation, not per project.
    """
    runner = MavenJmhRunner(work_root=tmp_path)
    seen: list[str] = []

    def fake_run_command(cmd, cwd=None, timeout_s=None):  # noqa: ANN001, ARG001
        seen.extend(a for a in cmd if a.startswith("-Djava.io.tmpdir="))
        raise RuntimeError("stop after the command is built")

    monkeypatch.setattr(MavenJmhRunner, "is_available", lambda self: True)
    monkeypatch.setattr("jmhgen.runner.maven.run_command", fake_run_command)
    # Pretend the jar is already built so run() goes straight to the JMH invocation.
    monkeypatch.setattr(MavenJmhRunner, "_artifact_path", lambda self, project: Path(__file__))

    for _ in range(2):
        try:
            runner.run(_SPEC, _OPTIONS)
        except RuntimeError:
            pass

    assert len(seen) == 2
    assert seen[0] != seen[1], "both invocations reused one java.io.tmpdir"
