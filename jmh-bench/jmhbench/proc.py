"""Subprocess helpers that keep JMH's forked JVMs from orphaning.

Why this module exists
----------------------
JMH runs each benchmark in a *forked* JVM (``org.openjdk.jmh.runner.ForkedMain``).
That fork is a **grandchild** of this harness::

    jmhbench (python)  →  java -jar benchmarks.jar (JMH launcher)  →  java … ForkedMain (the fork)

``subprocess.run(..., timeout=...)`` only sends ``SIGKILL`` to its *direct*
child (the launcher) when a timeout fires. The launcher dies instantly without
getting a chance to tell its fork to stop, so the fork is reparented to ``init``
(``PPID 1``). If the benchmarked code is stuck in a non-terminating / hot loop
(common with model-generated SUTs and regression mutants), the fork never
notices its dead parent and spins forever, pinning a core long after the run
ended.

The fix is two-fold:

* :func:`run_jvm` launches the JMH launcher as a **process-group leader**
  (``start_new_session=True`` → ``setsid``) and, on timeout or interrupt,
  signals the *entire group* (``SIGTERM`` then ``SIGKILL``). Forks inherit the
  group, so they die with the launcher instead of leaking.
* :func:`reap_orphaned_jmh_forks` is a safety net: it kills any ``ForkedMain``
  JVM that has already been orphaned (``PPID 1``) — e.g. left behind by an
  older build, or by a harness that was itself killed before it could clean up.
"""

from __future__ import annotations

import os
import signal
import subprocess
from dataclasses import dataclass

#: Marker class name of a JMH forked benchmark JVM. A process carrying this on
#: its command line with ``PPID == 1`` is, by definition, an orphaned fork.
FORKED_MAIN_MARKER = "org.openjdk.jmh.runner.ForkedMain"


@dataclass
class ProcResult:
    """The subset of :class:`subprocess.CompletedProcess` callers rely on."""

    returncode: int
    stdout: str
    stderr: str


def _signal_group(proc: subprocess.Popen, sig: int) -> None:
    """Send *sig* to the whole process group led by *proc* (best effort).

    Falls back to signalling just the leader when process groups aren't
    available (non-POSIX) or the group has already gone away.
    """
    if proc.poll() is not None:
        return
    try:
        if os.name == "posix":
            os.killpg(os.getpgid(proc.pid), sig)
            return
    except (ProcessLookupError, PermissionError, OSError):
        pass
    try:
        proc.send_signal(sig)
    except (ProcessLookupError, OSError):
        pass


def _kill_group_and_drain(proc: subprocess.Popen, grace: float = 5.0) -> tuple[str, str]:
    """Escalate ``SIGTERM`` → ``SIGKILL`` against *proc*'s group and collect output.

    Draining via :meth:`subprocess.Popen.communicate` (rather than
    :meth:`wait`) avoids dead-locking when the child has filled a stdout/stderr
    pipe buffer.
    """
    _signal_group(proc, signal.SIGTERM)
    try:
        return proc.communicate(timeout=grace)
    except subprocess.TimeoutExpired:
        _signal_group(proc, signal.SIGKILL)
        try:
            return proc.communicate(timeout=grace)
        except subprocess.TimeoutExpired:
            return "", ""


def run_jvm(
    cmd: list[str],
    *,
    cwd: os.PathLike[str] | str | None = None,
    env: dict[str, str] | None = None,
    timeout: float | None = None,
    text: bool = True,
) -> ProcResult:
    """Run *cmd* (a JVM launch) in its own process group, group-killing on exit.

    Behaves like :func:`subprocess.run` with ``capture_output=True`` for the
    happy path, but guarantees that a wall-clock timeout — or a
    ``KeyboardInterrupt`` / signal delivered to the harness — tears down the
    launcher **and every JVM it forked**, instead of leaving orphans behind.

    Raises :class:`subprocess.TimeoutExpired` (after the group is reaped) so
    existing callers keep working unchanged.

    Decoding is ``errors="replace"``. A benchmarked SUT can write arbitrary
    bytes to stdout -- a Latin-1 string, a raw buffer -- and strict UTF-8 turns
    that into a ``UnicodeDecodeError`` that escapes as an unhandled exception
    and kills the whole run. That is not hypothetical: it destroyed
    gemma/jodd-util 8.3h in, at baseline 240/411, on a single 0xef byte. The
    measurements are read from JMH's JSON result file, never from this text, so
    substituting a replacement character costs nothing.
    """
    popen_kwargs: dict = {}
    if os.name == "posix":
        # New session ⇒ the launcher becomes a process-group leader and JMH's
        # forks inherit the group, so we can signal them all at once.
        popen_kwargs["start_new_session"] = True

    proc = subprocess.Popen(
        cmd,
        cwd=cwd,
        env=env,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=text,
        errors="replace" if text else None,
        **popen_kwargs,
    )
    try:
        stdout, stderr = proc.communicate(timeout=timeout)
    except subprocess.TimeoutExpired:
        stdout, stderr = _kill_group_and_drain(proc)
        raise subprocess.TimeoutExpired(cmd, timeout, output=stdout, stderr=stderr)
    except BaseException:
        # KeyboardInterrupt / SIGTERM to the harness: don't leak the group.
        _kill_group_and_drain(proc)
        raise
    return ProcResult(returncode=proc.returncode, stdout=stdout or "", stderr=stderr or "")


def _iter_processes() -> list[tuple[int, int, str]]:
    """Return ``(pid, ppid, args)`` for every visible process (best effort)."""
    try:
        out = subprocess.run(
            ["ps", "-eo", "pid=,ppid=,args="],
            capture_output=True,
            text=True,
            errors="replace",
            timeout=15,
        ).stdout
    except (OSError, subprocess.SubprocessError):
        return []
    rows: list[tuple[int, int, str]] = []
    for line in out.splitlines():
        parts = line.split(maxsplit=2)
        if len(parts) < 3:
            continue
        try:
            pid, ppid = int(parts[0]), int(parts[1])
        except ValueError:
            continue
        rows.append((pid, ppid, parts[2]))
    return rows


def find_orphaned_jmh_forks() -> list[tuple[int, str]]:
    """List ``(pid, args)`` of orphaned JMH forks (``ForkedMain`` with PPID 1).

    A healthy fork has the JMH launcher as its parent, so ``PPID == 1`` is a
    reliable, side-effect-free signal that the fork has been leaked.
    """
    return [
        (pid, args)
        for pid, ppid, args in _iter_processes()
        if ppid == 1 and FORKED_MAIN_MARKER in args
    ]


def reap_orphaned_jmh_forks(*, log=None) -> int:
    """SIGKILL any orphaned JMH forks. Returns how many were signalled.

    Intended as a startup/shutdown safety net for forks that escaped the
    process-group teardown (e.g. left by a previous run whose harness was
    itself killed). Only ever targets ``ForkedMain`` processes with ``PPID 1``.
    """
    orphans = find_orphaned_jmh_forks()
    killed = 0
    for pid, args in orphans:
        try:
            os.kill(pid, signal.SIGKILL)
            killed += 1
            if log is not None:
                log.warning("reaped orphaned JMH fork pid=%d (%s)", pid, args[:120])
        except (ProcessLookupError, PermissionError, OSError):
            continue
    return killed
