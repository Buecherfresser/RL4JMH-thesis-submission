"""Background `nvidia-smi` sampler writing `metrics/gpu.csv` alongside the other telemetry.

Why record it in-run rather than watching live: the questions worth answering are retrospective.
"Was the GPU idle while the CPU-side reward ran?" is the whole throughput story of this pipeline
-- run 7 spent ~49 % of every step in Maven/JMH with the cards doing nothing -- and you cannot
see that by looking at `nvidia-smi` after the fact.

Sampling is best-effort by construction: no NVML dependency, no failure path that can touch the
run. If `nvidia-smi` is missing or errors, the thread logs once and stops.
"""

from __future__ import annotations

import csv
import subprocess
import threading
import time
from pathlib import Path

from jmhgen.utils.logging import get_logger

logger = get_logger("jmhgen.training.gpu")

_FIELDS = ("t", "gpu", "util", "mem_used_frac", "mem_used_mb", "power_w", "temp_c")
_QUERY = "index,utilization.gpu,memory.used,memory.total,power.draw,temperature.gpu"


def sample_once(timeout_s: float = 10.0) -> list[dict[str, float]]:
    """One `nvidia-smi` reading per visible GPU. Returns ``[]` on any failure."""
    try:
        proc = subprocess.run(
            ["nvidia-smi", f"--query-gpu={_QUERY}", "--format=csv,noheader,nounits"],
            capture_output=True,
            text=True,
            timeout=timeout_s,
        )
    except (OSError, subprocess.SubprocessError):
        return []
    if proc.returncode != 0:
        return []
    now = time.time()
    rows: list[dict[str, float]] = []
    for line in proc.stdout.splitlines():
        parts = [p.strip() for p in line.split(",")]
        if len(parts) < 6:
            continue
        try:
            index, util, used, total, power, temp = parts[:6]
            used_f, total_f = float(used), float(total)
            rows.append({
                "t": round(now, 3),
                "gpu": float(index),
                "util": float(util) / 100.0,
                "mem_used_frac": (used_f / total_f) if total_f else 0.0,
                "mem_used_mb": used_f,
                "power_w": float(power),
                "temp_c": float(temp),
            })
        except ValueError:
            # "[N/A]" shows up for power on some SKUs; skip the row rather than guess.
            continue
    return rows


class GpuSampler:
    """Appends a `gpu.csv` row per GPU per interval until :meth:`stop` is called."""

    def __init__(self, metrics_dir: str | Path, *, interval_s: float = 10.0) -> None:
        self.path = Path(metrics_dir) / "gpu.csv"
        self.interval_s = max(1.0, float(interval_s))
        self._stop = threading.Event()
        self._thread: threading.Thread | None = None

    def _write_header_if_needed(self) -> None:
        if self.path.exists() and self.path.stat().st_size > 0:
            return
        self.path.parent.mkdir(parents=True, exist_ok=True)
        with self.path.open("w", newline="", encoding="utf-8") as handle:
            csv.DictWriter(handle, fieldnames=_FIELDS).writeheader()

    def _loop(self) -> None:
        while not self._stop.is_set():
            rows = sample_once()
            if not rows:
                logger.warning("gpu sampler: nvidia-smi unavailable, stopping")
                return
            try:
                with self.path.open("a", newline="", encoding="utf-8") as handle:
                    writer = csv.DictWriter(handle, fieldnames=_FIELDS)
                    for row in rows:
                        writer.writerow(row)
            except OSError as exc:
                logger.warning("gpu sampler: write failed (%s), stopping", exc)
                return
            self._stop.wait(self.interval_s)

    def start(self) -> GpuSampler:
        if self._thread is not None:
            return self
        if not sample_once():
            logger.info("gpu sampler: no readable GPUs, not starting")
            return self
        self._write_header_if_needed()
        self._thread = threading.Thread(target=self._loop, name="gpu-sampler", daemon=True)
        self._thread.start()
        logger.info("gpu sampler: writing %s every %.0fs", self.path, self.interval_s)
        return self

    def stop(self) -> None:
        self._stop.set()
        if self._thread is not None:
            self._thread.join(timeout=self.interval_s + 5)
            self._thread = None
