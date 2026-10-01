"""Dataset schema shared across all stages.

The same representation flows through SFT, RFT, and GRPO so that prompts and targets are
formatted identically everywhere (a prerequisite for a valid per-stage comparison).

Records are persisted as JSONL. Each line is the :meth:`BenchmarkSample.to_dict` (or
:meth:`CodeSnippet.to_dict`) encoding, so the loaders below are the exact inverse of what
the dataset builder writes.
"""

from __future__ import annotations

import json
from collections.abc import Iterator
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any


@dataclass(frozen=True, slots=True)
class CodeSnippet:
    """A unit of source code under test, ``c in C`` in the proposal's notation."""

    id: str
    source: str
    language: str = "java"
    project: str | None = None
    path: str | None = None
    metadata: dict[str, Any] = field(default_factory=dict)

    def to_dict(self) -> dict[str, Any]:
        return {
            "id": self.id,
            "source": self.source,
            "language": self.language,
            "project": self.project,
            "path": self.path,
            "metadata": dict(self.metadata),
        }

    @classmethod
    def from_dict(cls, data: dict[str, Any]) -> CodeSnippet:
        return cls(
            id=data["id"],
            source=data["source"],
            language=data.get("language", "java"),
            project=data.get("project"),
            path=data.get("path"),
            metadata=dict(data.get("metadata") or {}),
        )


@dataclass(frozen=True, slots=True)
class BenchmarkSample:
    """A (snippet -> benchmark) pair, optionally annotated with a reward.

    ``benchmark_source is None`` encodes the SKIP decision (the policy's ``_|_`` output:
    no benchmark generated for this snippet).
    """

    snippet: CodeSnippet
    benchmark_source: str | None
    reward: float | None = None
    metadata: dict[str, Any] = field(default_factory=dict)

    @property
    def is_skip(self) -> bool:
        return self.benchmark_source is None

    def to_dict(self) -> dict[str, Any]:
        return {
            "snippet": self.snippet.to_dict(),
            "benchmark_source": self.benchmark_source,
            "reward": self.reward,
            "metadata": dict(self.metadata),
        }

    @classmethod
    def from_dict(cls, data: dict[str, Any]) -> BenchmarkSample:
        return cls(
            snippet=CodeSnippet.from_dict(data["snippet"]),
            benchmark_source=data.get("benchmark_source"),
            reward=data.get("reward"),
            metadata=dict(data.get("metadata") or {}),
        )


def _iter_json_lines(path: str | Path) -> Iterator[dict[str, Any]]:
    with Path(path).open(encoding="utf-8") as handle:
        for line_no, raw in enumerate(handle, start=1):
            line = raw.strip()
            if not line:
                continue
            try:
                obj = json.loads(line)
            except json.JSONDecodeError as exc:  # pragma: no cover - defensive
                raise ValueError(f"{path}:{line_no}: invalid JSON ({exc})") from exc
            if not isinstance(obj, dict):
                raise ValueError(f"{path}:{line_no}: expected a JSON object, got {type(obj)}")
            yield obj


def load_snippets(path: str | Path) -> Iterator[CodeSnippet]:
    """Load code snippets from a JSONL file.

    Each line is either a bare :class:`CodeSnippet` encoding or a :class:`BenchmarkSample`
    encoding (in which case its ``snippet`` field is returned), so the same dataset file can
    be consumed for snippet-only and (snippet, benchmark) use cases.
    """
    for obj in _iter_json_lines(path):
        snippet_obj = obj.get("snippet", obj)
        yield CodeSnippet.from_dict(snippet_obj)


def load_benchmark_samples(path: str | Path) -> Iterator[BenchmarkSample]:
    """Load (snippet, benchmark) demonstrations for SFT/RFT from a JSONL file."""
    for obj in _iter_json_lines(path):
        yield BenchmarkSample.from_dict(obj)
