"""Filter an extracted snippet corpus down to a hand-curated set of benchmarkable classes.

``jmh-extract-snippets`` is deliberately inclusive (every public, concrete class with a public
method). For an online-RL prompt set we instead want only the "good" subjects -- classes with a
genuinely hot, ``once-per-call`` public method a sensible JMH benchmark would exercise -- so the
reward signal is informative and the group-relative baseline does not degenerate on
unbenchmarkable junk.

The curated set is a per-project ``good_classes.yaml`` (a list of fully-qualified class names,
hand-picked by reading the source). This module loads that list and filters a snippets JSONL to
it, reporting any curated FQCN that has no matching snippet (a typo or a class dropped by
extraction) so the corpus never silently shrinks.
"""

from __future__ import annotations

import json
from collections.abc import Iterable
from dataclasses import dataclass
from pathlib import Path
from typing import Any

import yaml


def load_good_classes(path: str | Path) -> list[str]:
    """Load the curated FQCN list from a ``good_classes.yaml`` (its ``classes:`` key)."""
    data = yaml.safe_load(Path(path).read_text(encoding="utf-8")) or {}
    classes = data.get("classes")
    if not isinstance(classes, list) or not classes:
        raise ValueError(f"{path}: expected a non-empty 'classes:' list")
    # Preserve order, drop duplicates.
    seen: set[str] = set()
    ordered: list[str] = []
    for entry in classes:
        fqcn = str(entry).strip()
        if fqcn and fqcn not in seen:
            seen.add(fqcn)
            ordered.append(fqcn)
    return ordered


@dataclass(frozen=True)
class CurateResult:
    """Outcome of filtering a snippet corpus by a curated class list."""

    kept: list[dict[str, Any]]
    matched: list[str]
    missing: list[str]

    @property
    def report(self) -> dict[str, Any]:
        return {
            "curated": len(self.matched) + len(self.missing),
            "matched": len(self.matched),
            "kept_snippets": len(self.kept),
            "missing": self.missing,
        }


def filter_snippets(
    rows: Iterable[dict[str, Any]], good_classes: Iterable[str]
) -> CurateResult:
    """Keep only ``rows`` whose snippet ``id`` (FQCN) is in ``good_classes``.

    Returns the kept rows (in curated order), the FQCNs that matched a snippet, and the curated
    FQCNs that matched nothing (surfaced so the caller can flag a stale curation list).
    """
    by_id: dict[str, dict[str, Any]] = {}
    for row in rows:
        snippet = row.get("snippet", row)
        sid = snippet.get("id")
        if isinstance(sid, str):
            by_id.setdefault(sid, row)

    kept: list[dict[str, Any]] = []
    matched: list[str] = []
    missing: list[str] = []
    for fqcn in good_classes:
        row = by_id.get(fqcn)
        if row is None:
            missing.append(fqcn)
        else:
            kept.append(row)
            matched.append(fqcn)
    return CurateResult(kept=kept, matched=matched, missing=missing)


def _read_jsonl(path: str | Path) -> list[dict[str, Any]]:
    rows: list[dict[str, Any]] = []
    with Path(path).open(encoding="utf-8") as handle:
        for line in handle:
            line = line.strip()
            if line:
                rows.append(json.loads(line))
    return rows


def curate_file(
    snippets_path: str | Path,
    good_classes_path: str | Path,
    output_path: str | Path,
) -> CurateResult:
    """Filter ``snippets_path`` by ``good_classes_path`` and write the kept rows to ``output``."""
    rows = _read_jsonl(snippets_path)
    good = load_good_classes(good_classes_path)
    result = filter_snippets(rows, good)
    out = Path(output_path)
    out.parent.mkdir(parents=True, exist_ok=True)
    with out.open("w", encoding="utf-8") as handle:
        for row in result.kept:
            handle.write(json.dumps(row, ensure_ascii=False))
            handle.write("\n")
    return result
