"""Package layout helpers shared by the project mutation track.

A project-track subject is a vendored real-world library, and both the harness
input selector (:mod:`jmhbench.project_bench`) and the mutant generator
(``tools/make_project_mutants.py``) need to answer the same question: *which
component of the library does this source file belong to?*

"Component" is the package segment directly below the library's own base
package — ``archivers`` for ``org/apache/commons/compress/archivers/zip/…``,
``scanner`` for ``org/yaml/snakeyaml/scanner/…``. The base package is derived
from the tree itself (its longest common package prefix), so nothing here is
tied to a particular library.

Some libraries are deep enough that the first segment is too coarse to be a
useful grouping — Commons Compress puts every archive and compression format
under ``archivers/<fmt>`` and ``compressors/<fmt>``. Those top segments are
listed in ``harness_input.component_groups`` and get a two-segment label.
"""

from __future__ import annotations

from collections.abc import Sequence
from functools import lru_cache
from pathlib import Path


@lru_cache(maxsize=None)
def base_package(src_root: Path) -> str:
    """Longest common package prefix in *src_root*, as a ``/``-joined path.

    Returns ``""`` for a tree whose sources sit in the default package or share
    no common prefix, in which case every file's first segment is its component.
    """
    prefix: list[str] | None = None
    for path in src_root.rglob("*.java"):
        segments = path.relative_to(src_root).as_posix().split("/")[:-1]
        if not segments:
            return ""
        if prefix is None:
            prefix = segments
            continue
        i = 0
        while i < len(prefix) and i < len(segments) and prefix[i] == segments[i]:
            i += 1
        prefix = prefix[:i]
        if not prefix:
            return ""
    return "/".join(prefix or ())


def component_of(
    rel_path: str,
    base: str,
    group_prefixes: Sequence[str] = (),
) -> str:
    """Component label for *rel_path* (relative to the source root).

    Files sitting directly in the base package are ``"(root)"``. A file whose
    first segment is in *group_prefixes* gets a two-segment label
    (``archivers/zip``) so format-per-package libraries stay legible.
    """
    tail = rel_path[len(base) + 1 :] if base and rel_path.startswith(base + "/") else rel_path
    parts = tail.split("/")
    if len(parts) == 1:
        return "(root)"
    if parts[0] in group_prefixes and len(parts) >= 3:
        return f"{parts[0]}/{parts[1]}"
    return parts[0]


def component_resolver(src_root: Path, harness_input: dict | None = None):
    """Return ``rel_path -> component`` for the project rooted at *src_root*."""
    base = base_package(Path(src_root))
    groups = tuple((harness_input or {}).get("component_groups") or ())
    return lambda rel_path: component_of(rel_path, base, groups)
