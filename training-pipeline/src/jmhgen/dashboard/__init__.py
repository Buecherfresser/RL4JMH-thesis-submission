"""Read-only viewer and exporter for GRPO training telemetry.

Stdlib only so it can start on the training box without touching its pinned CUDA venvs.
"""

from jmhgen.dashboard.load import RunData, discover_runs, load_run

__all__ = ["RunData", "discover_runs", "load_run"]
