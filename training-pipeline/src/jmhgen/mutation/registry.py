"""The hidden ground-truth mutant registry (``mutants.yaml``).

Each :class:`MutantSpec` is one dormant ``MutationSwitch.tick(id)`` call baked into a public
method of a subject class by :mod:`jmhgen.mutation.generate`. The registry maps a mutant id
to its location so the scorer knows which mutants belong to a given subject class. It is the
ground truth for the mutation reward and is never shown to the policy.
"""

from __future__ import annotations

from collections import defaultdict
from dataclasses import dataclass
from pathlib import Path
from typing import Any

import yaml


@dataclass(frozen=True, slots=True)
class MutantSpec:
    """One fixed performance mutant baked into the subject-under-test."""

    id: int
    file: str
    fqcn: str
    component: str
    method: str
    signature: str
    line: int

    def to_dict(self) -> dict[str, Any]:
        return {
            "id": self.id,
            "file": self.file,
            "fqcn": self.fqcn,
            "component": self.component,
            "method": self.method,
            "signature": self.signature,
            "line": self.line,
        }

    @classmethod
    def from_dict(cls, data: dict[str, Any]) -> MutantSpec:
        return cls(
            id=int(data["id"]),
            file=str(data["file"]),
            fqcn=str(data["fqcn"]),
            component=str(data["component"]),
            method=str(data["method"]),
            signature=str(data["signature"]),
            line=int(data["line"]),
        )


def load_mutants(path: str | Path) -> list[MutantSpec]:
    """Load the mutant registry from a ``mutants.yaml`` file (empty list if absent)."""
    registry = Path(path)
    if not registry.exists():
        return []
    data = yaml.safe_load(registry.read_text(encoding="utf-8")) or {}
    return [MutantSpec.from_dict(m) for m in data.get("mutants", [])]


def mutants_by_fqcn(mutants: list[MutantSpec]) -> dict[str, list[MutantSpec]]:
    """Group mutants by their subject class (fully-qualified name)."""
    grouped: dict[str, list[MutantSpec]] = defaultdict(list)
    for mutant in mutants:
        grouped[mutant.fqcn].append(mutant)
    return dict(grouped)
