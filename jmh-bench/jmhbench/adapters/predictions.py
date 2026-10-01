"""File-based predictions harness.

Lets any external harness — including ones written in Java, Rust, or
hosted as an API — be evaluated by JMH-Bench. The user pre-generates
benchmark sources offline and points the runner at a directory:

    my_predictions/
    ├── arraylist_contains_001/
    │   ├── Benchmark.java        # required; primary benchmark source
    │   ├── ExtraState.java       # optional; copied as an extra source
    │   └── meta.json             # optional; merged into result metadata
    ├── string_concat_001/
    │   └── Benchmark.java
    └── ...

Two flat layouts are also accepted for convenience:

    my_predictions/
    ├── arraylist_contains_001.java
    └── string_concat_001.java

The harness then performs zero work at "generation" time: it simply reads
the prepared file. Mirrors SWT-Bench's ``--predictions_path`` mode.
"""

from __future__ import annotations

import json
from pathlib import Path

from jmhbench.harness import HarnessOutput, Task


class PredictionsHarness:
    """Read pre-generated JMH sources from disk; no model call at runtime."""

    name = "predictions"

    # Predictions are read from disk per task/project, not generated from SUT
    # source, so the project track must not drive this harness class-by-class.
    source_driven = False

    def __init__(self, predictions_dir: str | Path, name: str | None = None, **_: object) -> None:
        self.dir = Path(predictions_dir).expanduser().resolve()
        if not self.dir.exists():
            raise FileNotFoundError(f"--predictions dir not found: {self.dir}")
        # Allow the user to label the run (defaults to the dir name).
        self.name = name or f"predictions:{self.dir.name}"

    def generate(self, task: Task, workdir: Path) -> HarnessOutput:
        task_dir = self.dir / task.instance_id
        flat_file = self.dir / f"{task.instance_id}.java"

        if task_dir.is_dir():
            return self._load_from_dir(task_dir)
        if flat_file.exists():
            return HarnessOutput(
                benchmark_source=flat_file.read_text(),
                metadata={"source": str(flat_file)},
            )

        raise FileNotFoundError(
            f"No prediction found for task {task.instance_id} in {self.dir}. "
            f"Expected either {task_dir}/ with a *.java file inside, or {flat_file}."
        )

    @staticmethod
    def _load_from_dir(task_dir: Path) -> HarnessOutput:
        java_files = sorted(task_dir.rglob("*.java"))
        if not java_files:
            raise FileNotFoundError(f"{task_dir} contains no .java files")

        # Pick the primary file: prefer something *Benchmark.java, else the first.
        primary = next(
            (f for f in java_files if f.stem.endswith("Benchmark")),
            java_files[0],
        )
        extras: dict[str, str] = {}
        for f in java_files:
            if f == primary:
                continue
            extras[f.name] = f.read_text()

        meta: dict = {"source": str(primary)}
        meta_path = task_dir / "meta.json"
        if meta_path.exists():
            try:
                meta.update(json.loads(meta_path.read_text()))
            except Exception:  # pragma: no cover - tolerate broken meta
                pass

        return HarnessOutput(
            benchmark_source=primary.read_text(),
            extra_sources=extras,
            metadata=meta,
        )
