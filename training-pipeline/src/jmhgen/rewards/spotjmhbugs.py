"""SpotJMHBugs integration — authoritative JMH anti-pattern detection (Costa et al., TSE 2019).

SpotJMHBugs is the SpotBugs plugin from the original "What's Wrong With My Benchmark
Results?" study. It analyses **bytecode**, so it catches patterns a regex on source cannot
(e.g. an unconsumed method return that survives syntactic ``void``-benchmark checks).

This module is a thin wrapper (ported from JMH-Bench ``jmhbench/spotjmhbugs.py``) that:

1. Locates the vendored SpotBugs + SpotJMHBugs install (``tools/install_spotjmhbugs.sh``).
2. Runs SpotBugs against ``target/classes`` of a built Maven JMH project.
3. Parses the XML report, filters to ``JMH_*`` patterns, and maps each detector onto Costa's
   five-category taxonomy: ``RETU`` / ``LOOP`` / ``FINAL`` / ``INVO`` / ``FORK``.

When the vendored install is missing, :func:`is_available` returns ``False`` and
:class:`~jmhgen.rewards.antipattern.AntiPatternReward` falls back to the regex pre-screen.
"""

from __future__ import annotations

import os
import xml.etree.ElementTree as ET
from dataclasses import dataclass, field
from pathlib import Path

from jmhgen.utils.logging import get_logger
from jmhgen.utils.subprocess import run_command

logger = get_logger("jmhgen.rewards.spotjmhbugs")

# Costa et al., TSE 2019 Table 1.
COSTA_CATEGORIES: tuple[str, ...] = ("RETU", "LOOP", "FINAL", "INVO", "FORK")

_DETECTOR_TO_CATEGORY: dict[str, str] = {
    "JMH_IGNORED_METHOD_RETURN": "RETU",
    "JMH_IGNORED_STATIC_METHOD_RETURN": "RETU",
    "JMH_IGNORED_STATIC_PRIMITIVE_METHOD_RETURN": "RETU",
    "JMH_DEAD_STORE_VARIABLE": "RETU",
    "JMH_UNSINKED_VARIABLE": "RETU",
    "JMH_LOOP_INSIDE_BENCHMARK": "LOOP",
    "JMH_UNSAFELOOP_INSIDE_BENCHMARK": "LOOP",
    "JMH_STATE_FINAL_PRIMITIVE": "FINAL",
    "JMH_STATE_FINAL_STATIC_PRIMITIVE": "FINAL",
    "JMH_FIXTURE_USING_INVOCATION_SCOPE": "INVO",
    "JMH_BENCHMARKMODE_SINGLESHOT": "INVO",
    "JMH_NOTFORKED_BENCHMARK": "FORK",
}

_IGNORED_DETECTORS = frozenset({"JMH_BENCHMARK_METHOD_FOUND"})

# Costa et al. classify per-invocation fixtures (INVO) as bad only for short-running ops.
INVO_SHORT_RUNNING_NS = 1_000_000  # 1 ms
_INVOCATION_FIXTURE_DETECTOR = "JMH_FIXTURE_USING_INVOCATION_SCOPE"

_TIME_UNIT_NS: dict[str, float] = {
    "ns": 1.0,
    "us": 1e3,
    "µs": 1e3,
    "ms": 1e6,
    "s": 1e9,
    "m": 6e10,
}


@dataclass(frozen=True, slots=True)
class SpotJmhBugsResult:
    """Outcome of a SpotJMHBugs run against one Maven project's ``target/classes``."""

    available: bool
    violations: dict[str, list[str]] = field(default_factory=dict)
    raw_detectors: dict[str, int] = field(default_factory=dict)
    error: str | None = None

    @property
    def total(self) -> int:
        return sum(len(v) for v in self.violations.values())

    @property
    def categories_hit(self) -> int:
        return sum(1 for hits in self.violations.values() if hits)

    @property
    def clean(self) -> bool:
        return self.total == 0

    def to_dict(self) -> dict[str, object]:
        return {
            "available": self.available,
            "violations": {k: list(v) for k, v in self.violations.items()},
            "raw_detectors": dict(self.raw_detectors),
            "total": self.total,
            "categories_hit": self.categories_hit,
            "error": self.error,
        }


def _repo_root() -> Path:
    here = Path(__file__).resolve()
    for parent in here.parents:
        if (parent / "pyproject.toml").exists():
            return parent
    return here.parents[3]


def _vendor_base() -> Path:
    override = os.environ.get("JMHGEN_SPOTBUGS_ROOT")
    if override:
        return Path(override)
    return _repo_root() / "vendor" / "spotbugs"


def _locate_spotbugs() -> Path | None:
    base = _vendor_base()
    if not base.exists():
        return None
    for candidate in sorted(base.glob("spotbugs-*")):
        bin_path = candidate / "bin" / "spotbugs"
        if bin_path.is_file():
            return candidate
    return None


def is_available() -> bool:
    return _locate_spotbugs() is not None


def _build_auxclasspath(project_dir: Path, extra_classpath: tuple[str, ...] = ()) -> str:
    pieces: list[str] = []
    classes = project_dir / "target" / "classes"
    jar = project_dir / "target" / "benchmarks.jar"
    if classes.exists():
        pieces.append(str(classes))
    if jar.exists():
        pieces.append(str(jar))
    pieces.extend(str(p) for p in extra_classpath if p)
    m2 = Path.home() / ".m2" / "repository" / "org" / "openjdk" / "jmh"
    if m2.exists():
        for hit in m2.rglob("jmh-core-*.jar"):
            pieces.append(str(hit))
            break
    return os.pathsep.join(pieces)


def _parse_report(report: Path) -> SpotJmhBugsResult:
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
            continue
        violations[category].append(kind)
    return SpotJmhBugsResult(available=True, violations=violations, raw_detectors=raw)


def run(
    project_dir: str | Path,
    *,
    extra_classpath: tuple[str, ...] = (),
    timeout_s: float = 60.0,
) -> SpotJmhBugsResult:
    """Run SpotJMHBugs against ``project_dir/target/classes``."""
    sb_root = _locate_spotbugs()
    if sb_root is None:
        return SpotJmhBugsResult(
            available=False,
            error="vendored SpotBugs not installed; run `./tools/install_spotjmhbugs.sh`",
        )

    root = Path(project_dir)
    classes = root / "target" / "classes"
    if not classes.exists():
        return SpotJmhBugsResult(
            available=True,
            error=f"target/classes not found at {classes}",
        )

    report_path = root / "spotjmhbugs-report.xml"
    if report_path.exists():
        report_path.unlink()

    spotbugs_bin = sb_root / "bin" / "spotbugs"
    cmd = [
        str(spotbugs_bin),
        "-textui",
        "-xml:withMessages",
        "-output",
        str(report_path),
    ]
    aux = _build_auxclasspath(root, extra_classpath)
    if aux:
        cmd.extend(["-auxclasspath", aux])
    cmd.append(str(classes))

    proc = run_command(cmd, cwd=root, timeout_s=timeout_s)
    if not report_path.exists():
        return SpotJmhBugsResult(
            available=True,
            error=(
                f"SpotBugs produced no report (rc={proc.returncode}): "
                f"{proc.stderr[-300:].strip()}"
            ),
        )
    return _parse_report(report_path)


def per_op_nanos(score: float, unit: str, mode: str | None = None) -> float | None:
    """Convert a JMH primary metric to nanoseconds per operation."""
    if not unit or score <= 0:
        return None
    parts = unit.replace(" ", "").split("/")
    if len(parts) != 2:
        return None
    left, right = parts
    if left == "ops":
        factor = _TIME_UNIT_NS.get(right)
        return factor / score if factor is not None else None
    if right == "op":
        factor = _TIME_UNIT_NS.get(left)
        return score * factor if factor is not None else None
    return None


def gate_invo_by_runtime(result: dict[str, object], per_op_ns: float | None) -> dict[str, object]:
    """Drop a runtime-justified INVO flag (see JMH-Bench ``gate_invo_by_runtime``)."""
    if not result or not result.get("available"):
        return result
    violations = dict(result.get("violations") or {})
    invo = list(violations.get("INVO") or [])
    if _INVOCATION_FIXTURE_DETECTOR not in invo:
        return result
    if per_op_ns is None or per_op_ns < INVO_SHORT_RUNNING_NS:
        return result
    remaining = [d for d in invo if d != _INVOCATION_FIXTURE_DETECTOR]
    new_violations = {**violations, "INVO": remaining}
    new_total = sum(len(v) for v in new_violations.values())
    categories_hit = sum(1 for hits in new_violations.values() if hits)
    return {
        **result,
        "violations": new_violations,
        "total": new_total,
        "categories_hit": categories_hit,
        "invo_runtime_gated": True,
    }


def score_from_result(result: dict[str, object]) -> float:
    """Map a SpotJMHBugs result dict to ``r_anti`` in ``[0, 1]`` (Costa category grading)."""
    if not result.get("available") or result.get("error"):
        return 0.0
    hit = int(result.get("categories_hit") or 0)
    return max(0.0, 1.0 - hit / len(COSTA_CATEGORIES))
