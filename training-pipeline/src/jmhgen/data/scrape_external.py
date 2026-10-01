"""Scrape (source class -> human-written JMH benchmark) pairs from public Git repos.

The distilled gpt-oss-120b tree (see :mod:`jmhgen.data.build_sft`) gives us *model-generated*
demonstrations. This module mines the complementary *human expert* signal: JMH benchmarks that
library maintainers wrote and committed alongside their production code (Kafka, gRPC, ...).

The task shape is identical to the SFT builder's — ``(Java class under test -> JMH benchmark
class)`` — so the records are schema-compatible and can be merged with, or trained on instead
of, the distilled set.

The hard part is *pairing*: a benchmark file (e.g. ``LRUCacheBenchmark.java``) lives in a
``src/jmh`` or ``jmh-benchmarks`` source set, while the class it exercises (``LRUCache.java``)
lives in some module's ``src/main/java``. We resolve the target class conservatively, recording
*how* each pair was resolved (``resolved_via``) so the precision can be audited:

* ``import``         — the benchmark imports a class whose simple name matches ``<Foo>``;
* ``javadoc``        — a ``{@link Foo}`` / ``@see`` reference names it;
* ``same_package``   — exactly one ``Foo`` lives in the benchmark's own package;
* ``naming_wildcard``— exactly one ``Foo`` sits under a wildcard-imported package;
* ``naming_pkg``     — several ``Foo`` exist; the one sharing the longest package prefix with
                       the benchmark wins (and the benchmark names ``Foo``);
* ``naming``         — exactly one production ``Foo.java`` exists *and* the benchmark names it.

The weak (name-only) tiers additionally require the benchmark to *reference* ``Foo`` as a
word-boundary token, so a benchmark named after a method or concept (e.g. ``CheckpointBench``,
which exercises ``ReplicaManager`` and never mentions a ``Checkpoint`` class) is *skipped*
rather than mispaired to a coincidentally-named class. A wrong (class -> benchmark) pair is
worse than a missing one.
"""

from __future__ import annotations

import re
from collections.abc import Iterator
from dataclasses import dataclass
from pathlib import Path
from typing import Any

from jmhgen.data.schema import BenchmarkSample, CodeSnippet

# Where benchmark sources typically live (Gradle ``src/jmh`` set or a dedicated module).
DEFAULT_BENCHMARK_GLOBS: tuple[str, ...] = (
    "**/src/jmh/**/*.java",
    "**/jmh-benchmarks/**/*.java",
    "**/jmh/**/*.java",
)
# Class-name suffixes that mark a benchmark and yield the target simple name when stripped.
# Longest-first so ``Benchmarks`` is tried before ``Benchmark``.
DEFAULT_BENCHMARK_SUFFIXES: tuple[str, ...] = ("Benchmarks", "Benchmark", "Perf", "Bench")

_MAIN_SRC_MARKER = "/src/main/java/"
# Directory segments that mark a benchmark source set. Classes living under these are never the
# "class under test" (they are benchmarks or benchmark helpers), so they are kept out of the
# production index even when the module nests them under ``src/main/java`` (e.g. Kafka's
# ``jmh-benchmarks`` module).
_BENCHMARK_DIR_SEGMENTS = frozenset({"jmh", "jmh-benchmarks"})

_PACKAGE_RE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)
_IMPORT_RE = re.compile(r"^\s*import\s+(?:static\s+)?([\w.]+(?:\.\*)?)\s*;", re.MULTILINE)
_PUBLIC_CLASS_RE = re.compile(r"\bpublic\s+(?:final\s+|abstract\s+)*class\s+(\w+)")
_ANY_CLASS_RE = re.compile(r"\b(?:public\s+|final\s+|abstract\s+)*class\s+(\w+)")
# ``@Benchmark`` the method annotation, but not ``@BenchmarkMode`` (negative lookahead on a
# following letter).
_HAS_BENCHMARK_RE = re.compile(r"@Benchmark(?![A-Za-z])")
_LINK_RE = re.compile(r"\{@link(?:plain)?\s+([\w.#]+)")
_SEE_RE = re.compile(r"@see\s+([\w.][\w.]*)")


@dataclass(frozen=True, slots=True)
class JavaClass:
    """A production Java class located in a repo's ``src/main/java`` tree."""

    path: Path  # absolute path on disk
    rel_path: str  # path relative to the repo root (POSIX)
    package: str
    simple_name: str

    @property
    def fqcn(self) -> str:
        return f"{self.package}.{self.simple_name}" if self.package else self.simple_name


@dataclass(frozen=True, slots=True)
class JavaIndex:
    """Lookup tables over a repo's production classes, by simple name and by FQCN."""

    by_simple: dict[str, list[JavaClass]]
    by_fqcn: dict[str, JavaClass]


@dataclass(frozen=True, slots=True)
class Resolution:
    """Outcome of resolving a benchmark to the class it exercises."""

    target: JavaClass | None
    method: str
    reason: str | None  # set iff ``target is None`` (the skip reason)
    candidate_count: int


def parse_package(text: str) -> str:
    match = _PACKAGE_RE.search(text)
    return match.group(1) if match else ""


def parse_imports(text: str) -> list[str]:
    return _IMPORT_RE.findall(text)


def parse_javadoc_refs(text: str) -> list[str]:
    """Return ``{@link ...}`` and ``@see ...`` targets (with any ``#member`` suffix kept)."""
    return [*_LINK_RE.findall(text), *_SEE_RE.findall(text)]


def is_jmh_benchmark(text: str) -> bool:
    """True iff the source carries at least one ``@Benchmark`` method annotation."""
    return _HAS_BENCHMARK_RE.search(text) is not None


def detect_benchmark_class(text: str, fallback: str) -> str:
    """Pick the benchmark's primary class name, preferring the one matching the file stem."""
    public = _PUBLIC_CLASS_RE.findall(text)
    if fallback in public:
        return fallback
    if public:
        return public[0]
    any_class = _ANY_CLASS_RE.findall(text)
    return any_class[0] if any_class else fallback


def strip_benchmark_suffix(name: str, suffixes: tuple[str, ...]) -> str | None:
    """Strip a benchmark suffix to recover the target simple name (``FooBenchmark`` -> ``Foo``)."""
    for suffix in suffixes:
        if name.endswith(suffix) and len(name) > len(suffix):
            return name[: -len(suffix)]
    return None


def _package_from_path(rel_path: str) -> str:
    idx = rel_path.find(_MAIN_SRC_MARKER)
    if idx < 0:
        return ""
    tail = rel_path[idx + len(_MAIN_SRC_MARKER) :]
    parts = tail.split("/")[:-1]  # drop the file name
    return ".".join(parts)


def build_index(repo_root: str | Path) -> JavaIndex:
    """Index every production ``*.java`` (under ``src/main/java``) by simple name and FQCN."""
    root = Path(repo_root)
    by_simple: dict[str, list[JavaClass]] = {}
    by_fqcn: dict[str, JavaClass] = {}
    for path in root.rglob("*.java"):
        rel_path = path.relative_to(root).as_posix()
        if _MAIN_SRC_MARKER not in f"/{rel_path}":
            continue
        if path.name in {"package-info.java", "module-info.java"}:
            continue
        dir_segments = rel_path.split("/")[:-1]
        if _BENCHMARK_DIR_SEGMENTS.intersection(dir_segments):
            continue
        try:
            text = path.read_text(encoding="utf-8")
        except (OSError, UnicodeDecodeError):
            continue
        package = parse_package(text) or _package_from_path(rel_path)
        java = JavaClass(path=path, rel_path=rel_path, package=package, simple_name=path.stem)
        by_simple.setdefault(java.simple_name, []).append(java)
        by_fqcn.setdefault(java.fqcn, java)
    return JavaIndex(by_simple=by_simple, by_fqcn=by_fqcn)


def _common_prefix_len(a: str, b: str) -> int:
    n = 0
    for x, y in zip(a.split("."), b.split("."), strict=False):
        if x != y:
            break
        n += 1
    return n


def _closest_by_package(cands: list[JavaClass], bench_pkg: str) -> JavaClass | None:
    """Return the candidate sharing the longest package prefix with the benchmark, if unique."""
    scored = [(_common_prefix_len(c.package, bench_pkg), c) for c in cands]
    best = max(score for score, _ in scored)
    top = [c for score, c in scored if score == best]
    return top[0] if best > 0 and len(top) == 1 else None


def _references_token(text: str, simple_name: str) -> bool:
    """True iff ``simple_name`` appears as a word-boundary token (a real type reference)."""
    return re.search(rf"\b{re.escape(simple_name)}\b", text) is not None


def resolve_target(
    text: str,
    benchmark_class: str,
    index: JavaIndex,
    suffixes: tuple[str, ...] = DEFAULT_BENCHMARK_SUFFIXES,
) -> Resolution:
    """Resolve the production class a benchmark exercises (see module docstring for the ladder)."""
    target = strip_benchmark_suffix(benchmark_class, suffixes)
    if not target:
        return Resolution(None, "", "no_benchmark_suffix", 0)

    imports = parse_imports(text)
    wildcard_pkgs = [imp[:-2] for imp in imports if imp.endswith(".*")]
    bench_pkg = parse_package(text)

    # 1. An explicit import naming the target wins outright (exact FQCN).
    for imp in imports:
        if imp.endswith(".*"):
            continue
        if imp.rsplit(".", 1)[-1] == target and imp in index.by_fqcn:
            return Resolution(index.by_fqcn[imp], "import", None, 1)

    # 2. A qualified Javadoc reference to the target.
    for ref in parse_javadoc_refs(text):
        ref = ref.split("#", 1)[0]
        if not ref or ref.rsplit(".", 1)[-1] != target:
            continue
        if "." in ref and ref in index.by_fqcn:
            return Resolution(index.by_fqcn[ref], "javadoc", None, 1)

    cands = index.by_simple.get(target, [])
    if not cands:
        return Resolution(None, "", "no_source_class", 0)

    # 3. A class in the benchmark's own package with the same base name is the subject.
    same_pkg = [c for c in cands if c.package == bench_pkg]
    if len(same_pkg) == 1:
        return Resolution(same_pkg[0], "same_package", None, len(cands))

    # 4. A class sitting under a wildcard-imported package.
    in_wildcard = [c for c in cands if c.package in wildcard_pkgs]
    if len(in_wildcard) == 1:
        return Resolution(in_wildcard[0], "naming_wildcard", None, len(cands))

    # The remaining (name-only) tiers require the benchmark to actually name the class, so a
    # benchmark named after a method/concept is not paired to a coincidentally-named class.
    if not _references_token(text, target):
        return Resolution(None, "", "no_reference", len(cands))

    closest = _closest_by_package(cands, bench_pkg)
    if closest is not None and len(cands) > 1:
        return Resolution(closest, "naming_pkg", None, len(cands))
    if len(cands) == 1:
        return Resolution(cands[0], "naming", None, 1)

    return Resolution(None, "", "ambiguous", len(cands))


@dataclass(frozen=True, slots=True)
class RepoSpec:
    """Identity of a scraped repo, threaded into every record's metadata for provenance."""

    name: str
    url: str
    project: str
    commit: str | None = None
    license: str | None = None


def iter_benchmark_files(
    repo_root: str | Path, globs: tuple[str, ...] = DEFAULT_BENCHMARK_GLOBS
) -> Iterator[Path]:
    """Yield unique ``*.java`` files under any of the benchmark-source globs, sorted."""
    root = Path(repo_root)
    seen: set[Path] = set()
    found: list[Path] = []
    for pattern in globs:
        for path in root.glob(pattern):
            if path.is_file() and path not in seen:
                seen.add(path)
                found.append(path)
    return iter(sorted(found))


def build_sample(
    benchmark_path: Path,
    repo_root: Path,
    repo: RepoSpec,
    target: JavaClass,
    method: str,
    candidate_count: int,
    benchmark_class: str,
    benchmark_package: str,
) -> BenchmarkSample:
    """Assemble a schema-compatible ``BenchmarkSample`` for one resolved (class, benchmark) pair."""
    benchmark_source = benchmark_path.read_text(encoding="utf-8")
    java_source = target.path.read_text(encoding="utf-8")
    benchmark_fqcn = (
        f"{benchmark_package}.{benchmark_class}" if benchmark_package else benchmark_class
    )
    snippet = CodeSnippet(
        id=target.fqcn,
        source=java_source,
        language="java",
        project=repo.project,
        path=target.rel_path,
        metadata={"package": target.package, "class_name": target.simple_name},
    )
    return BenchmarkSample(
        snippet=snippet,
        benchmark_source=benchmark_source,
        reward=None,
        metadata={
            "source": "external",
            "stage": "human",
            "repo": repo.name,
            "repo_url": repo.url,
            "repo_commit": repo.commit,
            "license": repo.license,
            "benchmark_class": benchmark_class,
            "benchmark_fqcn": benchmark_fqcn,
            "benchmark_path": benchmark_path.relative_to(repo_root).as_posix(),
            "resolved_via": method,
            "candidate_count": candidate_count,
            "java_source_commit": repo.commit,
            "fork_scores": [],
        },
    )


@dataclass(frozen=True, slots=True)
class RepoResult:
    """Samples scraped from one repo plus a JSON-serializable per-repo report."""

    samples: list[BenchmarkSample]
    report: dict[str, Any]


def scrape_repo(
    repo_root: str | Path,
    repo: RepoSpec,
    globs: tuple[str, ...] = DEFAULT_BENCHMARK_GLOBS,
    suffixes: tuple[str, ...] = DEFAULT_BENCHMARK_SUFFIXES,
) -> RepoResult:
    """Scrape one repo into (class -> benchmark) samples, skipping anything that can't pair."""
    root = Path(repo_root)
    index = build_index(root)

    samples: list[BenchmarkSample] = []
    by_method: dict[str, int] = {}
    skipped_reasons: dict[str, int] = {}
    skipped: list[dict[str, str]] = []
    candidate_files = 0
    jmh_files = 0

    for bench_path in iter_benchmark_files(root, globs):
        candidate_files += 1
        try:
            text = bench_path.read_text(encoding="utf-8")
        except (OSError, UnicodeDecodeError):
            continue
        rel = bench_path.relative_to(root).as_posix()
        if not is_jmh_benchmark(text):
            skipped_reasons["not_jmh"] = skipped_reasons.get("not_jmh", 0) + 1
            continue
        jmh_files += 1

        benchmark_class = detect_benchmark_class(text, bench_path.stem)
        benchmark_package = parse_package(text)
        resolution = resolve_target(text, benchmark_class, index, suffixes)
        if resolution.target is None:
            reason = resolution.reason or "unresolved"
            skipped_reasons[reason] = skipped_reasons.get(reason, 0) + 1
            skipped.append({"benchmark": rel, "reason": reason})
            continue

        samples.append(
            build_sample(
                benchmark_path=bench_path,
                repo_root=root,
                repo=repo,
                target=resolution.target,
                method=resolution.method,
                candidate_count=resolution.candidate_count,
                benchmark_class=benchmark_class,
                benchmark_package=benchmark_package,
            )
        )
        by_method[resolution.method] = by_method.get(resolution.method, 0) + 1

    report = {
        "repo": repo.name,
        "url": repo.url,
        "commit": repo.commit,
        "license": repo.license,
        "indexed_production_classes": len(index.by_fqcn),
        "candidate_files": candidate_files,
        "jmh_benchmark_files": jmh_files,
        "resolved": len(samples),
        "skipped": jmh_files - len(samples),
        "by_method": by_method,
        "skipped_reasons": skipped_reasons,
        "skipped_benchmarks": skipped,
    }
    return RepoResult(samples=samples, report=report)
