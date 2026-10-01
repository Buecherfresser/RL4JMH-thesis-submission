"""SpotJMHBugs integration — the authoritative JMH anti-pattern checker.

SpotJMHBugs (Costa et al., TSE 2019) is the SpotBugs plugin that the original
"What's Wrong With My Benchmark Results?" study built and that the LLM4JMH
paper uses verbatim. It analyses **bytecode**, so it catches patterns a
regex on source code cannot (e.g. an unconsumed method return survives
syntactic warnings about ``void`` benchmarks).

This module is a thin Python wrapper that:

1. Locates the vendored SpotBugs + SpotJMHBugs install (populated by
   ``tools/install_spotjmhbugs.sh``).
2. Runs SpotBugs against ``target/classes`` of a built JMH project.
3. Parses the XML report, filters to the ``JMH_*`` patterns, and maps each
   detector onto Costa's five-category taxonomy: ``RETU`` / ``LOOP`` /
   ``FINAL`` / ``INVO`` / ``FORK``.

Caveats:

* The plugin reports a number of false positives (LLM4JMH report Cohen's
  κ ≈ 0.91 against human review). Treat single hits as advisory, not as
  ground truth.
* ``JMH_BENCHMARK_METHOD_FOUND`` is a no-op detector that always fires
  whenever an ``@Benchmark`` method exists; we silently drop it.
"""

from __future__ import annotations

import dataclasses
import subprocess
import xml.etree.ElementTree as ET
from pathlib import Path

from jmhbench.build import java_executable, resolve_java_home

# Costa et al., TSE 2019 Table 1.
COSTA_CATEGORIES = ["RETU", "LOOP", "FINAL", "INVO", "FORK"]

# Each SpotJMHBugs bug pattern maps to exactly one Costa category. The
# mapping follows the detector source + paper Section 2.
_DETECTOR_TO_CATEGORY: dict[str, str] = {
    # RETU — dead-code-elimination from unused returns.
    "JMH_IGNORED_METHOD_RETURN": "RETU",
    "JMH_IGNORED_STATIC_METHOD_RETURN": "RETU",
    "JMH_IGNORED_STATIC_PRIMITIVE_METHOD_RETURN": "RETU",
    "JMH_DEAD_STORE_VARIABLE": "RETU",
    "JMH_UNSINKED_VARIABLE": "RETU",
    # LOOP — accumulation inside a benchmark loop.
    "JMH_LOOP_INSIDE_BENCHMARK": "LOOP",
    "JMH_UNSAFELOOP_INSIDE_BENCHMARK": "LOOP",
    # FINAL — final primitive used as benchmark input.
    "JMH_STATE_FINAL_PRIMITIVE": "FINAL",
    "JMH_STATE_FINAL_STATIC_PRIMITIVE": "FINAL",
    # INVO — fixture run on every invocation / SingleShot misuse.
    "JMH_FIXTURE_USING_INVOCATION_SCOPE": "INVO",
    "JMH_BENCHMARKMODE_SINGLESHOT": "INVO",
    # FORK — Costa et al. specifically warn about @Fork(0); SpotJMHBugs ships
    # NotForkedBenchmarkDetector with the JMH_NOTFORKED_BENCHMARK pattern.
    "JMH_NOTFORKED_BENCHMARK": "FORK",
}

# Informational detector — drops out, never counted.
_IGNORED_DETECTORS = {"JMH_BENCHMARK_METHOD_FOUND"}

# Costa et al. classify per-invocation fixtures (INVO) as a bad practice only for
# *short-running* benchmarks — "ones that typically run for less than a
# millisecond". SpotJMHBugs' JMH_FIXTURE_USING_INVOCATION_SCOPE detector fires
# unconditionally, so we runtime-gate it against this threshold (see
# ``gate_invo_by_runtime``).
INVO_SHORT_RUNNING_NS = 1_000_000  # 1 ms
_INVOCATION_FIXTURE_DETECTOR = "JMH_FIXTURE_USING_INVOCATION_SCOPE"


@dataclasses.dataclass
class SpotJmhBugsResult:
    available: bool
    """False when the vendored SpotBugs install is missing; callers should
    fall back to the regex pre-screen."""

    violations: dict[str, list[str]] = dataclasses.field(default_factory=dict)
    """Costa category -> list of detector names that fired."""

    raw_detectors: dict[str, int] = dataclasses.field(default_factory=dict)
    """Raw detector -> count, for debugging / FP review."""

    error: str | None = None

    @property
    def total(self) -> int:
        return sum(len(v) for v in self.violations.values())

    @property
    def clean(self) -> bool:
        return self.total == 0

    def to_dict(self) -> dict:
        return {
            "available": self.available,
            "violations": {k: list(v) for k, v in self.violations.items()},
            "raw_detectors": dict(self.raw_detectors),
            "total": self.total,
            "error": self.error,
        }


def _repo_root() -> Path:
    return Path(__file__).resolve().parent.parent


def _locate_spotbugs() -> Path | None:
    """Find the vendored SpotBugs install, or None if not present."""
    base = _repo_root() / "vendor" / "spotbugs"
    if not base.exists():
        return None
    candidates = sorted(base.glob("spotbugs-*"))
    for c in candidates:
        bin_path = c / "bin" / "spotbugs"
        if bin_path.is_file():
            return c
    return None


def is_available() -> bool:
    return _locate_spotbugs() is not None


def run(project_dir: Path, *, timeout: int = 60) -> SpotJmhBugsResult:
    """Run SpotJMHBugs against ``project_dir/target/classes``.

    Returns a result with ``available=False`` if the vendored SpotBugs
    install isn't found; the caller is responsible for choosing whether
    to fall back to a regex check or to fail.
    """
    sb_root = _locate_spotbugs()
    if sb_root is None:
        return SpotJmhBugsResult(
            available=False,
            error="vendored SpotBugs not installed; run `./tools/install_spotjmhbugs.sh`",
        )

    classes = project_dir / "target" / "classes"
    if not classes.exists():
        return SpotJmhBugsResult(
            available=True,
            error=f"target/classes not found at {classes}",
        )

    report_path = project_dir / "spotjmhbugs-report.xml"
    if report_path.exists():
        report_path.unlink()

    aux = _build_auxclasspath(project_dir)
    env = _spotbugs_env()
    spotbugs_bin = sb_root / "bin" / "spotbugs"
    cmd = [
        str(spotbugs_bin),
        "-textui",
        "-xml:withMessages",
        "-output", str(report_path),
    ]
    if aux:
        cmd.extend(["-auxclasspath", aux])
    cmd.append(str(classes))

    try:
        proc = subprocess.run(
            cmd,
            cwd=project_dir,
            capture_output=True,
            text=True,
            timeout=timeout,
            env=env,
        )
    except FileNotFoundError:
        return SpotJmhBugsResult(available=True, error=f"`{spotbugs_bin}` not executable")
    except subprocess.TimeoutExpired:
        return SpotJmhBugsResult(available=True, error=f"SpotBugs timed out after {timeout}s")

    if not report_path.exists():
        return SpotJmhBugsResult(
            available=True,
            error=f"SpotBugs produced no report (rc={proc.returncode}): {proc.stderr[-300:].strip()}",
        )

    return _parse(report_path)


def _build_auxclasspath(project_dir: Path) -> str:
    """Best-effort classpath so BCEL can resolve referenced classes.

    Maven puts the shaded JAR in ``target/benchmarks.jar`` which already
    contains JMH and the SUT; including it plus the SUT's loose
    ``target/classes`` and the local Maven repo entry for ``jmh-core`` is
    enough for the detector to bind everything.
    """
    pieces: list[str] = []
    classes = project_dir / "target" / "classes"
    jar = project_dir / "target" / "benchmarks.jar"
    if classes.exists():
        pieces.append(str(classes))
    if jar.exists():
        pieces.append(str(jar))
    m2 = Path.home() / ".m2" / "repository" / "org" / "openjdk" / "jmh"
    if m2.exists():
        for hit in m2.rglob("jmh-core-*.jar"):
            pieces.append(str(hit))
            break
    return ":".join(pieces)


def _spotbugs_env() -> dict:
    """Make sure SpotBugs is invoked with the resolved JDK on PATH."""
    import os

    env = os.environ.copy()
    home = resolve_java_home()
    if home:
        env["JAVA_HOME"] = home
        env["PATH"] = f"{home}/bin{os.pathsep}{env.get('PATH', '')}"
        env.setdefault("JAVA", java_executable())
    return env


def _parse(report: Path) -> SpotJmhBugsResult:
    """Translate SpotBugs XML into Costa-taxonomy violations."""
    try:
        tree = ET.parse(report)
    except ET.ParseError as exc:
        return SpotJmhBugsResult(available=True, error=f"could not parse SpotBugs XML: {exc}")

    raw: dict[str, int] = {}
    violations: dict[str, list[str]] = {c: [] for c in COSTA_CATEGORIES}
    for bug in tree.getroot().iter("BugInstance"):
        kind = bug.get("type") or ""
        if kind in _IGNORED_DETECTORS:
            continue
        raw[kind] = raw.get(kind, 0) + 1
        category = _DETECTOR_TO_CATEGORY.get(kind)
        if category is None:
            # Generic SpotBugs detector (DLS_DEAD_LOCAL_STORE etc.); ignore.
            continue
        violations[category].append(kind)
    return SpotJmhBugsResult(available=True, violations=violations, raw_detectors=raw)


# JMH time-unit suffixes -> nanoseconds. Covers both ``<time>/op`` (avgt, ss,
# sample) and ``ops/<time>`` (thrpt) primary-metric units.
_TIME_UNIT_NS: dict[str, float] = {
    "ns": 1.0,
    "us": 1e3,
    "µs": 1e3,
    "ms": 1e6,
    "s": 1e9,
    "m": 6e10,
}


def _per_op_nanos(score: float, unit: str, mode: str | None = None) -> float | None:
    """Convert a JMH primary metric to nanoseconds per operation.

    Handles time-per-op units (``ns/op``, ``us/op``, ``ms/op``, ``s/op``) and
    throughput units (``ops/s``, ``ops/ms``, ...). Returns ``None`` when the
    unit can't be parsed or the score is non-positive. ``mode`` is accepted for
    callers' convenience but the unit string alone determines the conversion.
    """
    if not unit or score <= 0:
        return None
    parts = unit.replace(" ", "").split("/")
    if len(parts) != 2:
        return None
    left, right = parts
    if left == "ops":  # throughput: `score` ops per `right` -> ns per op
        factor = _TIME_UNIT_NS.get(right)
        return factor / score if factor is not None else None
    if right == "op":  # average time: `score` <left>-units per op
        factor = _TIME_UNIT_NS.get(left)
        return score * factor if factor is not None else None
    return None


def gate_invo_by_runtime(sj: dict, per_op_ns: float | None) -> dict:
    """Drop a runtime-justified INVO flag from a ``SpotJmhBugsResult`` dict.

    Costa et al. only consider per-invocation fixtures a bad practice for
    short-running (<1 ms) benchmarks, but the
    ``JMH_FIXTURE_USING_INVOCATION_SCOPE`` detector fires unconditionally. When
    the measured op runs at least ``INVO_SHORT_RUNNING_NS``, the Invocation-level
    fixture is justified: we remove just that detector from the INVO category and
    recompute ``total``. ``JMH_BENCHMARKMODE_SINGLESHOT`` is left untouched, and
    short-running or unknown timing (``per_op_ns is None``) keeps the flag.

    Returns a new dict; the input is not mutated.
    """
    if not sj or not sj.get("available"):
        return sj
    violations = sj.get("violations") or {}
    invo = list(violations.get("INVO") or [])
    if _INVOCATION_FIXTURE_DETECTOR not in invo:
        return sj
    if per_op_ns is None or per_op_ns < INVO_SHORT_RUNNING_NS:
        return sj
    remaining = [d for d in invo if d != _INVOCATION_FIXTURE_DETECTOR]
    new_violations = {**violations, "INVO": remaining}
    new_total = sum(len(v) for v in new_violations.values())
    return {**sj, "violations": new_violations, "total": new_total, "invo_runtime_gated": True}
