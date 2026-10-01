"""A thin, timeout-enforcing wrapper around :func:`subprocess.run`.

Centralizing process execution here keeps the runner backends small and gives us a single
place to later add sandboxing (cgroups/containers, network isolation, resource limits) for
the untrusted, LLM-generated Java code that the reward workers will execute.
"""

from __future__ import annotations

import subprocess
import time
from collections.abc import Mapping, Sequence
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True, slots=True)
class ProcResult:
    """Result of a finished (or timed-out) subprocess."""

    returncode: int | None
    stdout: str
    stderr: str
    duration_s: float
    timed_out: bool

    @property
    def ok(self) -> bool:
        return self.returncode == 0 and not self.timed_out


def _as_text(stream: str | bytes | None) -> str:
    if stream is None:
        return ""
    if isinstance(stream, bytes):
        return stream.decode("utf-8", errors="replace")
    return stream


def run_command(
    cmd: Sequence[str],
    *,
    cwd: str | Path | None = None,
    timeout_s: float | None = None,
    env: Mapping[str, str] | None = None,
) -> ProcResult:
    """Run ``cmd``, capturing stdout/stderr, and never raise on a non-zero exit.

    A timeout is reported via ``ProcResult.timed_out`` (with ``returncode=None``) rather
    than as an exception, so callers can map it cleanly onto an ``ErrorKind.TIMEOUT``.
    """
    start = time.monotonic()
    try:
        # Bytes, NOT text=True. Maven/javac/JMH echo fragments of the subject project's own
        # sources and resources, and nothing guarantees those are UTF-8: a latin-1 byte in a
        # comment or a resource name is enough for text=True to raise UnicodeDecodeError from
        # inside subprocess.communicate(). That exception surfaces in a reward worker thread,
        # propagates through TRL's _calculate_rewards, and takes the whole distributed run down --
        # it killed GRPO run 9 at step 208 on byte 0xa4 (2026-08-12). Decoding here with
        # errors="replace" degrades one diagnostic string instead, and matches what the
        # TimeoutExpired path below has always done.
        completed = subprocess.run(  # noqa: S603 - command is constructed by trusted callers
            list(cmd),
            cwd=str(cwd) if cwd is not None else None,
            capture_output=True,
            timeout=timeout_s,
            env=dict(env) if env is not None else None,
            check=False,
        )
        return ProcResult(
            returncode=completed.returncode,
            stdout=_as_text(completed.stdout),
            stderr=_as_text(completed.stderr),
            duration_s=time.monotonic() - start,
            timed_out=False,
        )
    except subprocess.TimeoutExpired as exc:
        return ProcResult(
            returncode=None,
            stdout=_as_text(exc.stdout),
            stderr=_as_text(exc.stderr),
            duration_s=time.monotonic() - start,
            timed_out=True,
        )
