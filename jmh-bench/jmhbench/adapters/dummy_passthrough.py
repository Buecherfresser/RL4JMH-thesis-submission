"""Reference passthrough harness.

Reads the hand-authored "gold" benchmark shipped with the task itself and
returns it verbatim. The intent is to provide an *upper-bound* baseline:
if the gold benchmark scores poorly, the task itself is broken.

Looks for ``<task_dir>/reference/*Benchmark.java`` (the first match is the
primary benchmark; any other ``.java`` files under ``reference/`` are
forwarded as ``extra_sources`` so @State classes can live in separate files).
"""

from __future__ import annotations

from pathlib import Path

from jmhbench.harness import HarnessOutput, Task


class DummyPassthroughHarness:
    name = "dummy-passthrough"

    # The gold suite is returned whole, so the project track must NOT drive this
    # harness class-by-class (it would just duplicate the same file N times).
    source_driven = False

    def __init__(self, **_: object) -> None:
        # All kwargs are accepted and ignored so the CLI -o flag stays uniform.
        pass

    def generate(self, task: Task, workdir: Path) -> HarnessOutput:
        ref_dir = task.task_dir / "reference"
        if not ref_dir.exists():
            raise FileNotFoundError(
                f"Task {task.instance_id} has no reference/ folder; "
                "dummy-passthrough requires a gold benchmark."
            )
        candidates = sorted(ref_dir.rglob("*Benchmark.java"))
        if not candidates:
            raise FileNotFoundError(
                f"Task {task.instance_id} has no reference/*Benchmark.java file"
            )
        primary = candidates[0]
        source = primary.read_text()
        extras: dict[str, str] = {}
        for f in ref_dir.rglob("*.java"):
            if f == primary:
                continue
            extras[f.name] = f.read_text()
        return HarnessOutput(
            benchmark_source=source,
            extra_sources=extras,
            metadata={"source": "reference"},
        )
