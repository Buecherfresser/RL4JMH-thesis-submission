"""Read a GRPO run's metrics directory into plain Python series.

Everything here tolerates files that are being appended to *right now*: a training run writes
`rollouts.jsonl` from four ranks while the dashboard polls it, so a trailing half-written line is
normal and must never raise. Readers therefore skip unparsable lines rather than failing, and the
CSV readers coerce missing values to ``None`` instead of erroring.

Stdlib only, on purpose. This has to run on the training box and on a CPU-only reward box without
adding a dependency to either profile.
"""

from __future__ import annotations

import csv
import json
import math
from collections.abc import Iterator
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any

_COMPONENTS = ("compile", "runtime", "anti_pattern", "rsd", "mutation")


def _number(value: Any) -> float | None:
    if value is None or value == "":
        return None
    if isinstance(value, bool):
        return float(value)
    try:
        result = float(value)
    except (TypeError, ValueError):
        return None
    return result if math.isfinite(result) else None


def read_csv(path: Path) -> list[dict[str, Any]]:
    """Read a metrics CSV; unparsable cells become ``None``, a missing file becomes ``[]``."""
    if not path.is_file():
        return []
    rows: list[dict[str, Any]] = []
    with path.open(newline="", encoding="utf-8", errors="replace") as handle:
        for raw in csv.DictReader(handle):
            row = {key: _number(value) for key, value in raw.items() if key is not None}
            # Drop only genuinely empty rows -- a trailing partial line from a live writer. Do
            # NOT key this on a "step" column: gpu.csv is indexed by wall-clock `t`, and an
            # earlier version of this guard silently discarded every GPU sample.
            if any(value is not None for value in row.values()):
                rows.append(row)
    return rows


def read_jsonl(path: Path, *, limit: int | None = None) -> Iterator[dict[str, Any]]:
    """Yield JSON objects, skipping any line that does not parse.

    A partially flushed final line is expected while a run is live, and a rank can interleave a
    write mid-line; neither is worth crashing a read-only viewer over.
    """
    if not path.is_file():
        return
    seen = 0
    with path.open(encoding="utf-8", errors="replace") as handle:
        for line in handle:
            line = line.strip()
            if not line:
                continue
            try:
                obj = json.loads(line)
            except json.JSONDecodeError:
                continue
            if not isinstance(obj, dict):
                continue
            yield obj
            seen += 1
            if limit is not None and seen >= limit:
                return


@dataclass
class RunData:
    """Everything the dashboard knows about one run directory."""

    run_dir: Path
    name: str
    steps: list[dict[str, Any]] = field(default_factory=list)
    components: list[dict[str, Any]] = field(default_factory=list)
    rollouts: list[dict[str, Any]] = field(default_factory=list)
    summaries: list[dict[str, Any]] = field(default_factory=list)
    gpu: list[dict[str, Any]] = field(default_factory=list)
    completions: list[dict[str, Any]] = field(default_factory=list)

    @property
    def metrics_dir(self) -> Path:
        return self.run_dir / "metrics" if (self.run_dir / "metrics").is_dir() else self.run_dir

    def merged_steps(self) -> list[dict[str, Any]]:
        """One row per optimiser step, averaging the per-rank rows.

        `step_metrics.csv` holds one row per rank per step (four rows per step on a 4-GPU run).
        Rates and means are averaged; `rollouts` is summed because it is a count over ranks.
        """
        by_step: dict[float, list[dict[str, Any]]] = {}
        for row in self.steps:
            step = row.get("step")
            if step is None:
                continue
            by_step.setdefault(step, []).append(row)
        merged: list[dict[str, Any]] = []
        summed = {"rollouts", "num_generations"}
        for step in sorted(by_step):
            group = by_step[step]
            out: dict[str, Any] = {"step": step, "ranks": len(group)}
            for key in group[0]:
                if key == "step":
                    continue
                values = [v for row in group if (v := row.get(key)) is not None]
                if not values:
                    out[key] = None
                elif key in summed:
                    out[key] = sum(values)
                else:
                    out[key] = sum(values) / len(values)
            merged.append(out)
        return merged


def load_run(run_dir: str | Path, *, max_rollouts: int = 400_000) -> RunData:
    """Load one run directory. Accepts either the output dir or its ``metrics`` subdirectory."""
    path = Path(run_dir).expanduser()
    data = RunData(run_dir=path, name=path.name)
    m = data.metrics_dir
    data.steps = read_csv(m / "step_metrics.csv")
    data.components = read_csv(m / "reward_components.csv")
    data.gpu = read_csv(m / "gpu.csv")
    data.rollouts = list(read_jsonl(m / "rollouts.jsonl", limit=max_rollouts))
    data.summaries = list(read_jsonl(m / "rollout_summary.jsonl"))
    data.completions = list(read_jsonl(m / "completions.jsonl"))
    return data


def discover_runs(root: str | Path) -> list[Path]:
    """Find run directories under ``root`` (anything containing a metrics dir with step data)."""
    base = Path(root).expanduser()
    if not base.is_dir():
        return []
    found: list[tuple[float, Path]] = []
    candidates = [base, *sorted(p for p in base.iterdir() if p.is_dir())]
    for candidate in candidates:
        metrics = candidate / "metrics"
        target = metrics if metrics.is_dir() else candidate
        marks = [target / "step_metrics.csv", target / "rollouts.jsonl"]
        present = [m for m in marks if m.is_file()]
        if present:
            found.append((max(m.stat().st_mtime for m in present), candidate))
    # Ordered oldest-first by last write, so the *live* run is last and the UI defaults to it.
    # Alphabetical order would default to whichever run happens to sort last -- on this box that
    # is a smoke test, not the run anyone opened the dashboard to look at.
    return [path for _, path in sorted(found, key=lambda pair: pair[0])]
