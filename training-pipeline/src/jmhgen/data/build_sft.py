"""Build the SFT dataset: (original Java class -> final verified JMH benchmark).

The distilled tree from gpt-oss-120b stores, per Java class under test, the raw model trial,
one or more synthesized benchmark sources (one per repair stage), and stage "lock" markers.
This module turns the *run-verified* subset into clean :class:`BenchmarkSample` records:

* filter   -> a class is kept iff it has a ``*.stg_r0_stg_run_run_lock_0`` marker;
* target   -> the benchmark source from the latest repair stage that produced one
              (``run`` > ``comp`` > ``syn``), since an earlier stage may still contain the
              bug a later stage fixed;
* context  -> the full original ``<Class>.java`` source, resolved from a local RxJava
              checkout by package path.

The result is prompt-agnostic; instruction text is applied later in :mod:`jmhgen.data.prompts`.
"""

from __future__ import annotations

import json
import re
import statistics
from collections.abc import Iterator
from dataclasses import dataclass
from pathlib import Path
from typing import Any

from jmhgen.data.schema import BenchmarkSample, CodeSnippet

RUN_LOCK_SUFFIX = ".stg_r0_stg_run_run_lock_0"

# Precedence: the latest pipeline stage that emitted a benchmark source wins.
_SYN_OUT_BY_STAGE: tuple[tuple[str, str], ...] = (
    ("run", ".stg_r0_stg_run_syn_out_0"),
    ("comp", ".stg_r0_stg_comp_syn_out_0"),
    ("syn", ".stg_r0_stg_syn_syn_out_0"),
)

_CLASS_RE = re.compile(r"\b(?:public\s+|final\s+|abstract\s+)*class\s+(\w+)")


@dataclass(frozen=True, slots=True)
class DistilledItem:
    """A run-verified entry in the distilled tree, keyed by the class under test."""

    class_name: str  # e.g. "Maybe"
    package: str  # e.g. "io.reactivex.rxjava3.core"
    directory: Path  # the package directory inside the distilled tree

    @property
    def fqcn(self) -> str:
        return f"{self.package}.{self.class_name}" if self.package else self.class_name


def iter_run_locked(distilled_root: str | Path) -> Iterator[DistilledItem]:
    """Yield every class that carries a run-lock marker, sorted by fully-qualified name."""
    root = Path(distilled_root)
    items: list[DistilledItem] = []
    for lock in root.rglob(f"*{RUN_LOCK_SUFFIX}"):
        class_name = lock.name[: -len(RUN_LOCK_SUFFIX)]
        package = ".".join(lock.parent.relative_to(root).parts)
        items.append(DistilledItem(class_name=class_name, package=package, directory=lock.parent))
    yield from sorted(items, key=lambda item: item.fqcn)


def select_final_source(item: DistilledItem) -> tuple[str, str]:
    """Return ``(benchmark_source, stage)`` for the latest stage that produced a source."""
    for stage, suffix in _SYN_OUT_BY_STAGE:
        candidate = item.directory / f"{item.class_name}{suffix}"
        if candidate.is_file():
            return candidate.read_text(encoding="utf-8"), stage
    raise FileNotFoundError(f"no synthesized benchmark source for {item.fqcn} in {item.directory}")


def java_source_path(java_source_root: str | Path, package: str, class_name: str) -> Path:
    """Map a package + class name to ``<root>/src/main/java/<pkg path>/<Class>.java``."""
    pkg_parts = package.split(".") if package else []
    return Path(java_source_root, "src", "main", "java", *pkg_parts, f"{class_name}.java")


def infer_benchmark_class(benchmark_source: str, fallback: str) -> str:
    match = _CLASS_RE.search(benchmark_source)
    return match.group(1) if match else fallback


def load_fork_scores(fork_dir: str | Path | None, benchmark_fqcn: str) -> list[dict[str, Any]]:
    """Collect measured JMH scores for ``benchmark_fqcn`` from the fork-results directory.

    Returns one entry per ``<fqcn>.<method>.json`` file (empty when ``fork_dir`` is unset or no
    results exist, which is the common case since the fork run covers only a subset).
    """
    if fork_dir is None:
        return []
    scores: list[dict[str, Any]] = []
    for result_file in sorted(Path(fork_dir).glob(f"{benchmark_fqcn}.*.json")):
        try:
            payload = json.loads(result_file.read_text(encoding="utf-8"))
        except json.JSONDecodeError:
            continue
        for entry in payload:
            primary = entry.get("primaryMetric") or {}
            scores.append(
                {
                    "benchmark": entry.get("benchmark"),
                    "mode": entry.get("mode"),
                    "score": primary.get("score"),
                    "unit": primary.get("scoreUnit"),
                }
            )
    return scores


def build_sample(
    item: DistilledItem,
    java_source_root: str | Path,
    fork_dir: str | Path | None = None,
    commit: str | None = None,
) -> BenchmarkSample:
    """Assemble a :class:`BenchmarkSample` for one run-verified item.

    Raises :class:`FileNotFoundError` if either the synthesized benchmark source or the
    original Java class source is missing.
    """
    benchmark_source, stage = select_final_source(item)
    source_path = java_source_path(java_source_root, item.package, item.class_name)
    java_source = source_path.read_text(encoding="utf-8")

    benchmark_class = infer_benchmark_class(benchmark_source, f"{item.class_name}Benchmark")
    benchmark_fqcn = f"{item.package}.{benchmark_class}" if item.package else benchmark_class
    fork_scores = load_fork_scores(fork_dir, benchmark_fqcn)

    snippet = CodeSnippet(
        id=item.fqcn,
        source=java_source,
        language="java",
        project="rxjava",
        path=str(source_path.relative_to(java_source_root)),
        metadata={"package": item.package, "class_name": item.class_name},
    )
    return BenchmarkSample(
        snippet=snippet,
        benchmark_source=benchmark_source,
        reward=None,
        metadata={
            "stage": stage,
            "benchmark_class": benchmark_class,
            "benchmark_fqcn": benchmark_fqcn,
            "java_source_commit": commit,
            "fork_scores": fork_scores,
        },
    )


@dataclass(frozen=True, slots=True)
class BuildResult:
    """Outcome of a dataset build: the samples plus a JSON-serializable report."""

    samples: list[BenchmarkSample]
    report: dict[str, Any]


def _length_stats(values: list[int]) -> dict[str, Any]:
    if not values:
        return {"count": 0}
    ordered = sorted(values)

    def pct(p: float) -> int:
        idx = min(len(ordered) - 1, int(round(p / 100.0 * (len(ordered) - 1))))
        return ordered[idx]

    return {
        "count": len(ordered),
        "min": ordered[0],
        "p50": int(statistics.median(ordered)),
        "p90": pct(90),
        "p99": pct(99),
        "max": ordered[-1],
    }


def build_dataset(
    distilled_root: str | Path,
    java_source_root: str | Path,
    fork_dir: str | Path | None = None,
    commit: str | None = None,
) -> BuildResult:
    """Build all run-verified samples and a report describing coverage and sizes.

    Items whose synthesized source or Java source cannot be resolved are recorded in the
    report (``missing_java_source`` / ``missing_syn_out``) and skipped rather than aborting,
    so the caller can decide whether the resolution rate is acceptable.
    """
    samples: list[BenchmarkSample] = []
    missing_java_source: list[str] = []
    missing_syn_out: list[str] = []
    stage_counts: dict[str, int] = {}
    java_lengths: list[int] = []
    bench_lengths: list[int] = []

    items = list(iter_run_locked(distilled_root))
    for item in items:
        try:
            sample = build_sample(item, java_source_root, fork_dir=fork_dir, commit=commit)
        except FileNotFoundError as exc:
            # Distinguish a missing Java source (commit drift) from a missing synth source.
            if "synthesized benchmark source" in str(exc):
                missing_syn_out.append(item.fqcn)
            else:
                missing_java_source.append(item.fqcn)
            continue
        samples.append(sample)
        stage = str(sample.metadata.get("stage"))
        stage_counts[stage] = stage_counts.get(stage, 0) + 1
        java_lengths.append(len(sample.snippet.source))
        bench_lengths.append(len(sample.benchmark_source or ""))

    report: dict[str, Any] = {
        "java_source_commit": commit,
        "total_run_locked": len(items),
        "resolved": len(samples),
        "missing_java_source_count": len(missing_java_source),
        "missing_syn_out_count": len(missing_syn_out),
        "missing_java_source": missing_java_source,
        "missing_syn_out": missing_syn_out,
        "stage_counts": stage_counts,
        "with_fork_scores": sum(1 for s in samples if s.metadata.get("fork_scores")),
        "java_source_chars": _length_stats(java_lengths),
        "benchmark_source_chars": _length_stats(bench_lengths),
    }
    return BuildResult(samples=samples, report=report)
