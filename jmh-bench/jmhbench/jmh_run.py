"""Invoke a built JMH benchmark JAR and parse its JSON results."""

from __future__ import annotations

import json
import re
import subprocess
from dataclasses import dataclass, field
from pathlib import Path

from jmhbench.config import JmhSettings
from jmhbench.proc import run_jvm


def result_key(benchmark: str, params: dict[str, str] | None) -> str:
    """Identity of one JMH result row: the method *plus* its ``@Param`` values.

    JMH emits one JSON entry per ``@Param`` combination, all sharing the same
    ``benchmark`` name and distinguished only by a ``params`` object. Keying on
    the name alone silently pairs a 64-byte baseline against a 4096-byte armed
    run -- arbitrary in magnitude, biased in whichever direction the parameter
    ordering points, and it looks like a large significant effect
    (EVALUATION_ISSUES.md B3).

    The key is a display string rather than a tuple so it can flow unchanged
    through coverage maps, verdict records and the scorecard JSON.
    """
    if not params:
        return benchmark
    inner = ",".join(f"{k}={params[k]}" for k in sorted(params))
    return f"{benchmark}[{inner}]"


@dataclass
class BenchmarkResult:
    """One @Benchmark method's measurements after a JMH run.

    One *row* of JMH's JSON: a ``@Param``-ised method contributes several of
    these, one per parameter combination.
    """

    benchmark: str
    mode: str
    score: float
    score_error: float
    unit: str
    samples: list[float] = field(default_factory=list)
    """All iteration scores, flattened across forks. Used by the stats module."""

    params: dict[str, str] = field(default_factory=dict)
    """The ``@Param`` values this row was measured at; empty when there are none."""

    samples_by_fork: list[list[float]] = field(default_factory=list)
    """The same scores with the fork boundary intact, ``[fork][iteration]``.

    ``samples`` flattens this, which erases the level a hierarchical bootstrap
    needs: iterations within a fork are autocorrelated and fork-to-fork variance
    is the dominant component, so a test that treats the flattened list as one
    i.i.d. sample is anti-conservative (EVALUATION_ISSUES.md B4)."""

    @property
    def key(self) -> str:
        """``benchmark`` for a plain method, ``benchmark[p=v,...]`` with params."""
        return result_key(self.benchmark, self.params)

    @property
    def mean(self) -> float:
        return self.score


@dataclass
class JmhRun:
    success: bool
    results: list[BenchmarkResult]
    stdout: str
    stderr: str
    error: str | None = None

    def by_name(self, fragment: str) -> list[BenchmarkResult]:
        return [r for r in self.results if fragment in r.benchmark]

    def by_key(self, key: str) -> BenchmarkResult | None:
        """Exact lookup on ``(benchmark, params)`` identity."""
        return next((r for r in self.results if r.key == key), None)


def _jmh_duration(seconds: float) -> str:
    """JMH accepts integer durations with a unit suffix; convert from seconds."""
    if seconds >= 1.0 and abs(seconds - round(seconds)) < 1e-6:
        return f"{int(round(seconds))}s"
    ms = max(1, int(round(seconds * 1000)))
    return f"{ms}ms"


def _raw_by_fork(raw: list) -> list[list[float]]:
    """JMH's rawData is List[List[float]] keyed [fork][iteration]."""
    out: list[list[float]] = []
    if not raw:
        return out
    for fork in raw:
        if isinstance(fork, list):
            out.append([float(x) for x in fork])
        else:
            out.append([float(fork)])
    return out


def _histogram_by_fork(raw: list) -> list[list[float]]:
    """``Mode.SampleTime`` writes ``rawDataHistogram``, not ``rawData``.

    The shape is ``[fork][iteration][[value, count], ...]``. Reading only
    ``rawData`` returned an empty sample list, which dropped the benchmark from
    coverage with no warning while it still counted in ``n_benchmarks``
    (EVALUATION_ISSUES.md B4). Each iteration is collapsed to its count-weighted
    mean, which is the per-iteration score the other modes report.
    """
    out: list[list[float]] = []
    if not raw:
        return out
    for fork in raw:
        if not isinstance(fork, list):
            continue
        per_iteration: list[float] = []
        for iteration in fork:
            if not isinstance(iteration, list):
                continue
            total = count = 0.0
            for bucket in iteration:
                if isinstance(bucket, list) and len(bucket) >= 2:
                    value, n = float(bucket[0]), float(bucket[1])
                    total += value * n
                    count += n
            if count > 0:
                per_iteration.append(total / count)
        if per_iteration:
            out.append(per_iteration)
    return out


def _flatten_raw(raw: list) -> list[float]:
    """Backwards-compatible flat view of ``rawData``."""
    return [x for fork in _raw_by_fork(raw) for x in fork]


def _jmh_env() -> dict[str, str]:
    """Augment the subprocess env with the resolved JAVA_HOME / PATH."""
    import os
    from jmhbench.build import resolve_java_home

    env = os.environ.copy()
    home = resolve_java_home()
    if home:
        env["JAVA_HOME"] = home
        env["PATH"] = f"{home}/bin{os.pathsep}{env.get('PATH', '')}"
    return env


def _jmh_failure_hint(returncode: int, stdout: str, stderr: str) -> str:
    """Turn JMH's non-zero exit into something actionable."""
    blob = f"{stdout}\n{stderr}"
    lower = blob.lower()
    if "no matching benchmarks" in lower:
        return "JMH found no benchmarks in the JAR — check that the produced source has `@Benchmark` methods and the right package."
    if "unsupportedclassversion" in lower or "has been compiled by a more recent version" in lower:
        return "JDK version mismatch — the JAR was built with a newer JDK than the one running JMH. Align JAVA_HOME."
    if "could not find or load main class" in lower:
        return "JMH's main class missing from the shaded JAR. Re-run with a clean Maven cache."

    last_benchmark: str | None = None
    for line in blob.splitlines():
        stripped = line.strip()
        if stripped.startswith("# Benchmark:"):
            last_benchmark = stripped.removeprefix("# Benchmark:").strip()

    for line in reversed(blob.splitlines()):
        stripped = line.strip()
        if not stripped:
            continue
        if re.search(r"(?:Exception|Error):\s", stripped) and "Thread.run" not in stripped:
            where = f" at {last_benchmark}" if last_benchmark else ""
            return f"JMH exited with code {returncode}{where}: {stripped[:300]}"

    if last_benchmark:
        return f"JMH exited with code {returncode} at {last_benchmark}"

    tail = (stderr or stdout or "").strip().splitlines()[-1:] or [""]
    return f"JMH exited with code {returncode}: {tail[0][:200]}"


def _parse_results(json_path: Path) -> list[BenchmarkResult]:
    if not json_path.exists():
        return []
    text = json_path.read_text().strip()
    if not text:
        return []
    data = json.loads(text)
    out: list[BenchmarkResult] = []
    for entry in data:
        primary = entry.get("primaryMetric", {})
        by_fork = _raw_by_fork(primary.get("rawData", []))
        if not by_fork:
            by_fork = _histogram_by_fork(primary.get("rawDataHistogram", []))
        params = {str(k): str(v) for k, v in (entry.get("params") or {}).items()}
        out.append(
            BenchmarkResult(
                benchmark=entry["benchmark"],
                mode=entry.get("mode", "thrpt"),
                score=float(primary.get("score", 0.0)),
                score_error=float(primary.get("scoreError", 0.0)) if primary.get("scoreError") not in (None, "NaN") else 0.0,
                unit=primary.get("scoreUnit", ""),
                samples=[x for fork in by_fork for x in fork],
                params=params,
                samples_by_fork=by_fork,
            )
        )
    return out


_USE_SETTINGS_TIMEOUT = object()


def run_jmh(
    project_dir: Path,
    settings: JmhSettings,
    tag: str = "base",
    *,
    jvm_args: list[str] | None = None,
    include: str | None = None,
    wall_timeout: float | None | object = _USE_SETTINGS_TIMEOUT,
    fail_on_error: bool = True,
) -> JmhRun:
    """Execute the shaded benchmarks JAR and parse its JSON output.

    *tag* is appended to the JSON file name so base- and regressed-run outputs
    don't clobber each other.

    *jvm_args* are extra JVM flags (e.g. ``["-Djmhbench.mutant=42"]``). They are
    passed both to the launcher JVM (covers ``forks=0``) and forwarded to JMH's
    forked JVMs via ``-jvmArgsAppend`` (covers ``forks>=1``), so a system
    property arms a mutant regardless of fork mode.

    *include* is an optional JMH benchmark-name regex used to run a single
    benchmark (its argument is matched against fully-qualified benchmark names).

    *wall_timeout* overrides the coarse process-group wall-clock cap: the
    default uses ``settings.timeout_seconds``; pass ``None`` to disable it
    entirely (relying solely on JMH's per-iteration ``-to`` to bound hangs —
    used for whole-suite runs that must not be truncated).

    *fail_on_error* toggles JMH's ``-foe``. The default (``True``) aborts the
    whole invocation as soon as one benchmark throws — right for single-benchmark
    coverage/detection runs. Pass ``False`` for the whole-suite baseline so one
    pathological benchmark (e.g. an unbounded ``@State`` buffer that OOMs) is
    skipped and the run still returns results for every benchmark that succeeded.
    """
    project_dir = project_dir.resolve()
    jar = project_dir / "target" / "benchmarks.jar"
    if not jar.exists():
        return JmhRun(success=False, results=[], stdout="", stderr="", error=f"benchmarks.jar not found at {jar}")

    json_out = project_dir / f"jmh-results-{tag}.json"
    if json_out.exists():
        json_out.unlink()

    from jmhbench.build import java_executable, resolve_java_home
    if resolve_java_home() is None:
        return JmhRun(
            success=False,
            results=[],
            stdout="",
            stderr="",
            error=(
                "No JDK 17+ found. Set JAVA_HOME or install a JDK. On macOS: "
                "`brew install openjdk@17` then "
                "`export JAVA_HOME=$(/usr/libexec/java_home -v 17)`."
            ),
        )

    jvm_args = list(jvm_args or [])
    cmd = [java_executable(), *jvm_args, "-jar", str(jar)]
    cmd += [
        "-f", str(settings.forks),
        "-wi", str(settings.warmup_iterations),
        "-w", _jmh_duration(settings.warmup_time_seconds),
        "-i", str(settings.measurement_iterations),
        "-r", _jmh_duration(settings.measurement_time_seconds),
        "-to", _jmh_duration(settings.iteration_timeout_seconds),
        "-rf", "json",
        "-rff", str(json_out),
        "-foe", "true" if fail_on_error else "false",
    ]
    # Measurement shape, pinned rather than inherited from the machine or from
    # whatever @BenchmarkMode/@Threads the model happened to write. Command-line
    # options override the annotations (EVALUATION_ISSUES.md B4).
    if settings.mode:
        cmd += ["-bm", settings.mode]
    if settings.time_unit:
        cmd += ["-tu", settings.time_unit]
    if settings.force_gc:
        cmd += ["-gc", "true"]
    if settings.threads:
        cmd += ["-t", str(settings.threads)]
    # Heap and collector go to the *forked* JVMs, which are what actually
    # measure; the launcher only orchestrates. Caller-supplied args (mutant
    # arming) are forwarded alongside so a system property still works at
    # forks=0.
    forked_args = settings.jvm_args() + jvm_args
    if forked_args:
        cmd += ["-jvmArgsAppend", " ".join(forked_args)]
    if include:
        cmd.append(include)
    timeout = (
        settings.timeout_seconds
        if wall_timeout is _USE_SETTINGS_TIMEOUT
        else wall_timeout
    )
    try:
        proc = run_jvm(
            cmd,
            cwd=project_dir,
            timeout=timeout,
            env=_jmh_env(),
        )
    except FileNotFoundError:
        return JmhRun(
            success=False,
            results=[],
            stdout="",
            stderr="",
            error=f"`java` not found at {cmd[0]!r}; set JAVA_HOME to a JDK 17+ install.",
        )
    except subprocess.TimeoutExpired as exc:
        return JmhRun(
            success=False,
            results=[],
            stdout=(exc.stdout or "") if isinstance(exc.stdout, str) else "",
            # The effective cap, which may be a per-benchmark budget rather than
            # settings.timeout_seconds. Reporting the wrong number here is how a
            # timeout gets misread as a severe regression.
            stderr=f"JMH timed out after {timeout}s",
            error="timeout",
        )

    if proc.returncode != 0:
        return JmhRun(
            success=False,
            results=[],
            stdout=proc.stdout,
            stderr=proc.stderr,
            error=_jmh_failure_hint(proc.returncode, proc.stdout, proc.stderr),
        )

    results = _parse_results(json_out)
    if not results:
        return JmhRun(
            success=False,
            results=[],
            stdout=proc.stdout,
            stderr=proc.stderr,
            error=(
                f"JMH exited cleanly but produced no parseable results in "
                f"{json_out.name}. stderr tail: {(proc.stderr or '').strip()[-200:]!r}"
            ),
        )
    return JmhRun(
        success=True,
        results=results,
        stdout=proc.stdout,
        stderr=proc.stderr,
        error=None,
    )
