"""Extract prompt-only :class:`CodeSnippet` corpora from a Java repo checkout.

RFT samples *fresh* benchmarks for arbitrary subject classes, so — unlike
:mod:`jmhgen.data.scrape_external`, which pairs an existing human benchmark to the class it
exercises — this module needs no benchmark target at all. It walks a repo's production sources
(``src/main/java``) and emits each compilable, benchmarkable class as a prompt-only snippet
(``id`` = fully-qualified class name, ``source`` = the whole file).

The output JSONL is consumed directly as the RFT ``dataset_path`` (:func:`load_snippets`), so any
cloned library (Apache Commons Lang, RoaringBitmap, ...) becomes an RFT corpus once its SUT
classpath is provisioned with :mod:`jmhgen.data.classpath`. Filtering is deliberately *inclusive*:
the verify phase compiles and runs every candidate, so it — not this extractor — is the precision
gate. We only drop sources that can never be a self-contained subject (interfaces, annotations,
package-private or abstract types, files with no public method) so we do not waste samples on them.
"""

from __future__ import annotations

import re
from collections.abc import Iterable, Iterator
from dataclasses import dataclass
from pathlib import Path
from typing import Any

from jmhgen.data.schema import CodeSnippet
from jmhgen.utils.logging import get_logger
from jmhgen.utils.subprocess import run_command

logger = get_logger("jmhgen.data.snippets_repo")

# Production Java lives under a ``src/main/java`` source set; benchmark/test sets are excluded.
DEFAULT_SOURCE_GLOBS: tuple[str, ...] = ("**/src/main/java/**/*.java",)
_MAIN_SRC_MARKER = "/src/main/java/"
# Directory segments that mark a benchmark source set nested under ``src/main/java`` (Kafka's
# ``jmh-benchmarks`` module is the canonical case); subjects living there are never production code.
_EXCLUDE_DIR_SEGMENTS = frozenset({"jmh", "jmh-benchmarks"})

_PACKAGE_RE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)
# First top-level ``public`` type. Java allows exactly one public top-level type (named after the
# file), so the first match is the primary subject. ``@interface`` (annotation) is captured only so
# it can be dropped. The modifier run is captured to detect ``abstract``.
_PUBLIC_TYPE_RE = re.compile(
    r"\bpublic\s+((?:(?:final|abstract|sealed|non-sealed|static|strictfp)\s+)*)"
    r"(@interface|interface|class|enum|record)\s+(\w+)",
    re.MULTILINE,
)
# A public method/constructor signature with a body: ``public ... name(...) {`` (optionally
# ``throws ...``). The ``[^\n=;{}]*`` between ``public`` and ``(`` rejects field declarations
# (which contain ``=`` or end in ``;`` before any ``(``). Heuristic by design — the verify phase is
# the real gate, so over-inclusion is cheap and under-inclusion (a dropped good subject) is not.
_PUBLIC_METHOD_RE = re.compile(r"\bpublic\b[^\n=;{}]*\([^;{}]*\)\s*(?:throws[^{;]*)?\{")

_SKIPPABLE_FILES = frozenset({"package-info.java", "module-info.java"})
_NON_SUBJECT_KINDS = frozenset({"interface", "@interface"})


@dataclass(frozen=True, slots=True)
class PrimaryType:
    """The primary (``public`` top-level) type declared in a Java file."""

    kind: str  # one of: class, enum, record, interface, @interface
    name: str
    is_abstract: bool


@dataclass(frozen=True, slots=True)
class ExtractResult:
    """Extracted snippets plus a JSON-serialisable report (counts, skip reasons, length stats)."""

    snippets: list[CodeSnippet]
    report: dict[str, Any]


def parse_package(text: str) -> str:
    match = _PACKAGE_RE.search(text)
    return match.group(1) if match else ""


def parse_primary_type(text: str) -> PrimaryType | None:
    """Return the first ``public`` top-level type, or ``None`` if the file declares none."""
    match = _PUBLIC_TYPE_RE.search(text)
    if match is None:
        return None
    modifiers, kind, name = match.group(1), match.group(2), match.group(3)
    return PrimaryType(kind=kind, name=name, is_abstract="abstract" in modifiers.split())


def has_public_method(text: str) -> bool:
    """True iff the source declares at least one public method/constructor with a body."""
    return _PUBLIC_METHOD_RE.search(text) is not None


def _package_from_path(rel_path: str) -> str:
    idx = rel_path.find(_MAIN_SRC_MARKER)
    if idx < 0:
        return ""
    tail = rel_path[idx + len(_MAIN_SRC_MARKER) :]
    return ".".join(tail.split("/")[:-1])


def iter_main_sources(repo_root: str | Path, globs: Iterable[str]) -> Iterator[Path]:
    """Yield unique ``*.java`` files under any of the source globs, sorted for determinism."""
    root = Path(repo_root)
    seen: set[Path] = set()
    found: list[Path] = []
    for pattern in globs:
        for path in root.glob(pattern):
            if path.is_file() and path not in seen:
                seen.add(path)
                found.append(path)
    return iter(sorted(found))


def _matches_any_prefix(package: str, prefixes: tuple[str, ...]) -> bool:
    return any(package == p or package.startswith(f"{p}.") for p in prefixes)


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
        "p50": ordered[len(ordered) // 2],
        "p90": pct(90),
        "p99": pct(99),
        "max": ordered[-1],
    }


def _classify(
    text: str,
    rel_path: str,
    include_packages: tuple[str, ...],
    exclude_packages: tuple[str, ...],
    max_chars: int,
    include_abstract: bool,
) -> tuple[str | None, dict[str, Any]]:
    """Classify one file: return ``(skip_reason, meta)``; ``skip_reason is None`` means keep."""
    segments = rel_path.split("/")[:-1]
    if _EXCLUDE_DIR_SEGMENTS.intersection(segments):
        return "benchmark_source_set", {}

    primary = parse_primary_type(text)
    if primary is None:
        return "no_public_type", {}
    if primary.kind in _NON_SUBJECT_KINDS:
        return ("interface" if primary.kind == "interface" else "annotation"), {}
    if primary.is_abstract and not include_abstract:
        return "abstract", {}
    if not has_public_method(text):
        return "no_public_method", {}

    package = parse_package(text) or _package_from_path(rel_path)
    if include_packages and not _matches_any_prefix(package, include_packages):
        return "package_not_included", {}
    if exclude_packages and _matches_any_prefix(package, exclude_packages):
        return "package_excluded", {}
    if max_chars > 0 and len(text) > max_chars:
        return "oversize", {}

    fqcn = f"{package}.{primary.name}" if package else primary.name
    return None, {"fqcn": fqcn, "package": package, "name": primary.name, "kind": primary.kind}


def extract_snippets(
    repo_root: str | Path,
    project: str,
    *,
    url: str | None = None,
    commit: str | None = None,
    license: str | None = None,
    source_globs: Iterable[str] = DEFAULT_SOURCE_GLOBS,
    include_packages: Iterable[str] = (),
    exclude_packages: Iterable[str] = (),
    max_chars: int = 0,
    include_abstract: bool = False,
    limit: int = 0,
) -> ExtractResult:
    """Walk a repo's production sources into prompt-only snippets, dropping non-subjects.

    Each kept file becomes a :class:`CodeSnippet` whose ``id`` is the fully-qualified class name
    (so the ``jmhbench`` prompt imports the subject correctly) and whose ``source`` is the whole
    file. ``project`` is stamped onto every snippet so the RFT verify phase can look the SUT
    classpath up by it. ``limit`` (>0) keeps only the first N snippets (FQCN-sorted) for smoke runs.
    """
    root = Path(repo_root)
    include = tuple(include_packages)
    exclude = tuple(exclude_packages)

    snippets: list[CodeSnippet] = []
    seen_fqcn: set[str] = set()
    skip_reasons: dict[str, int] = {}
    by_package: dict[str, int] = {}
    files_scanned = 0

    for path in iter_main_sources(root, source_globs):
        if path.name in _SKIPPABLE_FILES:
            continue
        files_scanned += 1
        rel_path = path.relative_to(root).as_posix()
        try:
            text = path.read_text(encoding="utf-8")
        except (OSError, UnicodeDecodeError):
            skip_reasons["unreadable"] = skip_reasons.get("unreadable", 0) + 1
            continue

        reason, meta = _classify(text, rel_path, include, exclude, max_chars, include_abstract)
        if reason is not None:
            skip_reasons[reason] = skip_reasons.get(reason, 0) + 1
            continue

        fqcn = str(meta["fqcn"])
        if fqcn in seen_fqcn:
            skip_reasons["duplicate_fqcn"] = skip_reasons.get("duplicate_fqcn", 0) + 1
            continue
        seen_fqcn.add(fqcn)
        package = str(meta["package"])
        by_package[package] = by_package.get(package, 0) + 1
        snippets.append(
            CodeSnippet(
                id=fqcn,
                source=text,
                language="java",
                project=project,
                path=rel_path,
                metadata={
                    "package": package,
                    "class_name": meta["name"],
                    "kind": meta["kind"],
                    "repo": project,
                    "repo_url": url,
                    "repo_commit": commit,
                    "license": license,
                },
            )
        )

    snippets.sort(key=lambda s: s.id)
    if limit > 0:
        snippets = snippets[:limit]

    top_packages = dict(sorted(by_package.items(), key=lambda kv: (-kv[1], kv[0]))[:25])
    report: dict[str, Any] = {
        "project": project,
        "url": url,
        "commit": commit,
        "license": license,
        "source_globs": list(source_globs),
        "include_packages": list(include),
        "exclude_packages": list(exclude),
        "max_chars": max_chars,
        "include_abstract": include_abstract,
        "limit": limit,
        "files_scanned": files_scanned,
        "kept": len(snippets),
        "skipped": files_scanned - len(snippets),
        "skip_reasons": dict(sorted(skip_reasons.items())),
        "top_packages": top_packages,
        "java_source_chars": _length_stats([len(s.source) for s in snippets]),
    }
    return ExtractResult(snippets=snippets, report=report)


def _git(args: list[str], cwd: Path | None = None, timeout_s: float = 1800.0) -> str:
    result = run_command(["git", *args], cwd=cwd, timeout_s=timeout_s)
    if not result.ok:
        raise RuntimeError(
            f"git {' '.join(args)} failed (rc={result.returncode}): {result.stderr.strip()}"
        )
    return result.stdout.strip()


def ensure_checkout(
    url: str, ref: str | None, dest: str | Path, clone: bool = True
) -> tuple[Path, str]:
    """Return ``(checkout_dir, head_sha)``, shallow-cloning ``url`` (at ``ref``) if it is absent."""
    dest_path = Path(dest)
    if not (dest_path / ".git").exists():
        if not clone:
            raise FileNotFoundError(
                f"{dest_path} is not a checkout and clone is disabled; clone {url} manually."
            )
        dest_path.parent.mkdir(parents=True, exist_ok=True)
        clone_args = ["clone", "--depth", "1"]
        if ref:
            clone_args += ["--branch", ref]
        clone_args += [url, str(dest_path)]
        logger.info("cloning %s%s -> %s", url, f" @ {ref}" if ref else "", dest_path)
        _git(clone_args)
    sha = _git(["rev-parse", "HEAD"], cwd=dest_path)
    return dest_path, sha
