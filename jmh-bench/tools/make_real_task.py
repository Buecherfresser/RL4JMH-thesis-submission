"""Author a real-world task from a self-contained spec.

Each spec is a Python dict with:

    {
      "id": "lucene_pointtree_eager_001",
      "target_class": "bench.NumericComparator",
      "expected_input_shapes": ["..."],
      "tags": ["lucene", "real-world"],
      "provenance": {
        "project": "apache/lucene",
        "url": "https://github.com/apache/lucene/pull/13498",
        "commit": "<sha>",
        "description": "..."
      },
      "sut_files": { "bench/NumericComparator.java": "<source>" },
      "regressed_files": { "bench/NumericComparator.java": "<regressed source>" },
      "regression_id": "eager_construction",
      "regression_description": "Build the prefix tree eagerly in the constructor.",
      "test_class": "NumericComparatorTest",
      "test_source": "<JUnit source>",
      "benchmark_class": "NumericComparatorBenchmark",
      "benchmark_source": "<reference benchmark>",
    }

Run ``python tools/make_real_task.py`` to (re)generate every task whose
spec is listed in :data:`SPECS` below.

The script:
  * writes ``dataset/tasks_real/<id>/task.yaml`` (with provenance & commit)
  * writes ``src/main/java/<sut_file>`` files verbatim
  * writes ``regressions/<id>.patch`` derived from difflib.unified_diff
  * writes ``tests/<class>.java`` and ``reference/<class>.java``
  * verifies that each patch is non-empty
"""

from __future__ import annotations

import difflib
import sys
from pathlib import Path
from typing import Any

REPO = Path(__file__).resolve().parent.parent
ROOT = REPO / "dataset" / "tasks_real"


def _write(path: Path, contents: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(contents)


def _yaml_str(s: str, indent: int = 2) -> str:
    """Render a multi-line string as a YAML literal block."""
    pad = " " * indent
    body = "\n".join(pad + ln if ln else "" for ln in s.rstrip("\n").splitlines())
    return "|\n" + body


def _patch_for(sut: dict[str, str], regressed: dict[str, str]) -> str:
    chunks: list[str] = []
    for rel, original in sut.items():
        modified = regressed.get(rel)
        if modified is None or modified == original:
            continue
        from_label = f"a/{rel}"
        to_label = f"b/{rel}"
        diff = difflib.unified_diff(
            original.splitlines(keepends=True),
            modified.splitlines(keepends=True),
            fromfile=from_label,
            tofile=to_label,
            n=3,
        )
        chunks.append("".join(diff))
    return "".join(chunks)


def emit(spec: dict[str, Any]) -> None:
    task_id: str = spec["id"]
    task_dir = ROOT / task_id

    # SUT
    for rel, src in spec["sut_files"].items():
        _write(task_dir / "src" / "main" / "java" / rel, src)

    # Regression patch
    patch = _patch_for(spec["sut_files"], spec["regressed_files"])
    if not patch.strip():
        raise RuntimeError(f"{task_id}: empty regression patch — regressed == SUT")
    patch_path = task_dir / "regressions" / f"{spec['regression_id']}.patch"
    _write(patch_path, patch)

    # JUnit test
    test_rel = f"tests/{spec['test_class']}.java"
    _write(task_dir / test_rel, spec["test_source"])

    # Reference benchmark
    _write(task_dir / "reference" / f"{spec['benchmark_class']}.java", spec["benchmark_source"])

    # task.yaml
    prov = spec["provenance"]
    yaml_lines: list[str] = [
        f"instance_id: {task_id}",
        f"target_class: {spec['target_class']}",
        "expected_input_shapes:",
    ]
    for shape in spec["expected_input_shapes"]:
        yaml_lines.append(f'  - "{shape}"')
    yaml_lines.append("tags: [" + ", ".join(spec["tags"]) + "]")
    yaml_lines.append("provenance:")
    yaml_lines.append(f'  project: {prov["project"]}')
    yaml_lines.append(f'  url: {prov["url"]}')
    if prov.get("commit"):
        yaml_lines.append(f'  commit: "{prov["commit"]}"')
    if prov.get("description"):
        yaml_lines.append("  description: " + _yaml_str(prov["description"], indent=4))
    yaml_lines.append(f"junit_test_path: {test_rel}")
    yaml_lines.append("regressions:")
    yaml_lines.append(f"  - id: {spec['regression_id']}")
    yaml_lines.append(
        f"    patch: regressions/{spec['regression_id']}.patch"
    )
    yaml_lines.append(f"    description: {spec['regression_description']}")
    yaml_lines.append("")
    _write(task_dir / "task.yaml", "\n".join(yaml_lines))

    print(f"  emitted: {task_id}")


# ---------------------------------------------------------------------------
# Specs live in a sibling module so this file stays focused on plumbing.
# ---------------------------------------------------------------------------

from tools.real_task_specs import SPECS  # noqa: E402


if __name__ == "__main__":
    sys.path.insert(0, str(REPO))
    ROOT.mkdir(parents=True, exist_ok=True)
    for spec in SPECS:
        try:
            emit(spec)
        except Exception as exc:
            print(f"!! failed {spec.get('id', '?')}: {exc}", file=sys.stderr)
            raise
    print(f"done: {len(SPECS)} task(s) emitted")
