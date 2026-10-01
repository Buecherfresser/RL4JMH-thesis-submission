"""Maven compile wrapper used by the runner."""

from __future__ import annotations

import os
import re
import shutil
import subprocess
import sys
from dataclasses import dataclass
from functools import lru_cache
from pathlib import Path

#: Maximum heap for the Maven JVM -- which is javac's heap too, because
#: maven-compiler-plugin runs the compiler in-process by default.
#:
#: This was ``-Xmx512m`` and it was the only compiler-heap setting in the tree:
#: no CLI flag, never exported by ``run_eval.sh``, not recorded anywhere. At
#: that size javac dies in ``Symtab.defineClass`` while resolving imports on a
#: large generated suite -- GPT-OSS-120B's decimal4j run had 110 benchmark
#: classes on top of a 175-class SUT and crashed with
#: ``java.lang.OutOfMemoryError: Java heap space``, scoring the model 0/50 on a
#: project it had actually produced a suite for. hppc failed the same way.
#:
#: The default is a fixed value rather than a fraction of the machine's RAM on
#: purpose: whether a suite compiles must not depend on which box built it, or
#: "compiles" stops being a property of the model (EVALUATION_ISSUES.md B5).
DEFAULT_COMPILER_HEAP = "4g"

_COMPILER_HEAP: str | None = None

_XMX_RE = re.compile(r"(?:^|\s)-Xmx\S+")


def set_compiler_heap(value: str | None) -> None:
    """Override the Maven/javac max heap for this process (e.g. from a CLI flag)."""
    global _COMPILER_HEAP
    _COMPILER_HEAP = value.strip() if value and value.strip() else None


def compiler_heap() -> str:
    """The max heap the build will use, in ``-Xmx`` suffix form (e.g. ``4g``).

    Precedence: explicit :func:`set_compiler_heap` (CLI), then
    ``JMHBENCH_COMPILER_HEAP``, then :data:`DEFAULT_COMPILER_HEAP`.
    """
    if _COMPILER_HEAP:
        return _COMPILER_HEAP
    env = os.environ.get("JMHBENCH_COMPILER_HEAP", "").strip()
    return env or DEFAULT_COMPILER_HEAP


def maven_opts() -> str:
    """``MAVEN_OPTS`` for a build, with a max heap guaranteed to be present.

    An existing ``MAVEN_OPTS`` is preserved -- it may carry proxy or module
    settings that have nothing to do with heap -- but a ``-Xmx`` is appended if
    it does not already specify one. Leaving it absent would hand javac the
    JVM's default heap, which is a quarter of the machine's RAM and therefore
    machine-dependent.
    """
    existing = os.environ.get("MAVEN_OPTS", "").strip()
    if existing and _XMX_RE.search(existing):
        return existing
    heap = f"-Xmx{compiler_heap()}"
    return f"{existing} {heap}".strip() if existing else heap


def is_oom(diagnostics: str) -> bool:
    """Did this build fail because the *compiler* ran out of heap?"""
    text = diagnostics or ""
    return "OutOfMemoryError" in text and (
        "Java heap space" in text
        or "GC overhead limit" in text
        or "jdk.compiler" in text
    )


def oom_hint() -> str:
    return (
        f"javac ran out of heap (MAVEN_OPTS was '{maven_opts()}'). This is a build-machine "
        f"limit, not a defect in the generated suite: a large suite on top of a large SUT "
        f"needs more than this. Raise it with `--compiler-heap 8g`, or "
        f"`JMHBENCH_COMPILER_HEAP=8g`, and re-run."
    )


@dataclass
class BuildResult:
    success: bool
    stdout: str
    stderr: str
    returncode: int
    oom: bool = False
    """The compiler itself ran out of heap.

    A build-machine limit, not a property of the generated suite: it must not be
    reported as "the model's benchmarks do not compile"."""

    @property
    def diagnostics(self) -> str:
        return self.stderr or self.stdout


@lru_cache(maxsize=1)
def resolve_java_home(minimum_version: int = 17) -> str | None:
    """Best-effort JAVA_HOME discovery so users don't have to export it.

    Order of precedence:
    1. ``$JAVA_HOME`` if already set and contains a ``bin/java``.
    2. macOS: ``/usr/libexec/java_home -v <minimum_version>`` (only if a JDK
       satisfying the version constraint is installed).
    3. ``java`` on PATH; derive ``JAVA_HOME`` from its real path.

    Returns ``None`` if nothing usable is found; callers may then surface a
    helpful error to the user.
    """
    env_home = os.environ.get("JAVA_HOME")
    if env_home and (Path(env_home) / "bin" / "java").exists():
        return env_home

    if sys.platform == "darwin":
        try:
            proc = subprocess.run(
                ["/usr/libexec/java_home", "-v", str(minimum_version)],
                capture_output=True,
                text=True,
                errors="replace",
                timeout=5,
            )
            if proc.returncode == 0:
                candidate = proc.stdout.strip()
                if candidate and (Path(candidate) / "bin" / "java").exists():
                    return candidate
        except (OSError, subprocess.TimeoutExpired):
            pass

    java_bin = shutil.which("java")
    if java_bin:
        real = Path(java_bin).resolve()
        if real.parent.name == "bin":
            return str(real.parent.parent)

    return None


def java_executable() -> str:
    """Path to the ``java`` binary the rest of the codebase should invoke."""
    home = resolve_java_home()
    if home:
        return str(Path(home) / "bin" / "java")
    return "java"


def _mvn_env() -> dict[str, str]:
    env = os.environ.copy()
    # Not setdefault: an inherited MAVEN_OPTS with no -Xmx used to win outright
    # and silently hand javac a machine-dependent default heap.
    env["MAVEN_OPTS"] = maven_opts()
    home = resolve_java_home()
    if home:
        env["JAVA_HOME"] = home
        env["PATH"] = f"{home}/bin{os.pathsep}{env.get('PATH', '')}"
    return env


def mvn_package(project_dir: Path, timeout: int = 180) -> BuildResult:
    """Run `mvn -q -DskipTests package` and capture output."""
    cmd = ["mvn", "-q", "-B", "-DskipTests", "-Dmaven.test.skip=true", "package"]
    try:
        result = subprocess.run(
            cmd,
            cwd=project_dir,
            capture_output=True,
            text=True,
            errors="replace",
            timeout=timeout,
            env=_mvn_env(),
        )
    except subprocess.TimeoutExpired as exc:
        return BuildResult(
            success=False,
            stdout=(exc.stdout or b"").decode(errors="replace") if isinstance(exc.stdout, bytes) else (exc.stdout or ""),
            stderr=f"Maven build timed out after {timeout}s",
            returncode=-1,
        )
    return _finish(result)


def _finish(result: subprocess.CompletedProcess) -> BuildResult:
    """Wrap a completed Maven run, flagging a compiler OOM explicitly.

    The hint is **appended** to *stderr*, because callers keep only the last
    few KB of diagnostics (``tail_diagnostics`` returns ``text[-limit:]``). A
    javac heap crash emits thousands of stack-trace lines after the message, so
    the tail that survives contains neither the banner nor the words
    "OutOfMemoryError" -- which is how the original failure reached the report
    as the unreadable fragment "duplicates. Include your program, the following
    diagnostic, ...". Putting the hint last guarantees it is what survives.
    """
    if result.returncode != 0:
        blob = f"{result.stdout or ''}\n{result.stderr or ''}"
        if is_oom(blob):
            return BuildResult(
                success=False,
                stdout=result.stdout,
                stderr=f"{result.stderr or ''}\n\n{oom_hint()}",
                returncode=result.returncode,
                oom=True,
            )
    return BuildResult(
        success=result.returncode == 0,
        stdout=result.stdout,
        stderr=result.stderr,
        returncode=result.returncode,
    )


def mvn_compile_only(project_dir: Path, timeout: int = 90) -> BuildResult:
    """Compile without the (slow) shade-plugin step. Used after applying a regression patch
    when the benchmark JAR has already been built."""
    cmd = ["mvn", "-q", "-B", "compile"]
    try:
        result = subprocess.run(
            cmd,
            cwd=project_dir,
            capture_output=True,
            text=True,
            errors="replace",
            timeout=timeout,
            env=_mvn_env(),
        )
    except subprocess.TimeoutExpired:
        return BuildResult(success=False, stdout="", stderr=f"Maven compile timed out after {timeout}s", returncode=-1)
    return _finish(result)
