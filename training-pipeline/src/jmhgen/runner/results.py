"""Parsing of JMH JSON output and the robust RSD stability metric.

JMH emits (via ``-rf json``) a top-level JSON array; each element describes one
benchmark method and carries a ``primaryMetric`` object with the aggregate ``score`` and,
when available, ``rawData`` (a list of per-fork lists of per-iteration measurements).
"""

from __future__ import annotations

import json
from collections.abc import Sequence
from statistics import median

from jmhgen.runner.types import BenchmarkStat

# Consistency constant making the MAD a consistent estimator of the standard deviation
# for normally distributed data. See the robust RSD definition in the thesis proposal.
_MAD_TO_STD = 1.4826


def robust_rsd(values: Sequence[float]) -> float | None:
    """Robust relative standard deviation.

    ``robust_rsd = 1.4826 * median(|x_i - x_med|) / x_med``

    Returns ``None`` when undefined (no data, or a zero/negative median that would make
    the ratio meaningless). A higher value indicates a less stable benchmark.
    """
    if not values:
        return None
    med = median(values)
    if med <= 0:
        return None
    mad = median(abs(x - med) for x in values)
    return _MAD_TO_STD * mad / med


def _flatten_raw_data(primary_metric: dict) -> tuple[float, ...]:
    """Flatten JMH ``rawData`` (list of per-fork lists) into a single measurement tuple."""
    raw = primary_metric.get("rawData")
    if not raw:
        return ()
    flat: list[float] = []
    for fork in raw:
        for measurement in fork:
            flat.append(float(measurement))
    return tuple(flat)


def parse_jmh_json(text: str) -> tuple[BenchmarkStat, ...]:
    """Parse JMH JSON output into a tuple of :class:`BenchmarkStat`.

    Raises:
        ValueError: if ``text`` is not valid JMH JSON (e.g. empty or malformed output,
            which typically means the run crashed before producing results).
    """
    try:
        data = json.loads(text)
    except json.JSONDecodeError as exc:  # pragma: no cover - exercised via run() error path
        raise ValueError(f"invalid JMH JSON output: {exc}") from exc

    if not isinstance(data, list):
        raise ValueError("expected JMH JSON output to be a top-level array")

    stats: list[BenchmarkStat] = []
    for entry in data:
        primary = entry.get("primaryMetric", {})
        raw = _flatten_raw_data(primary)
        stats.append(
            BenchmarkStat(
                benchmark=entry.get("benchmark", "<unknown>"),
                mode=entry.get("mode", "<unknown>"),
                score=float(primary.get("score", "nan")),
                score_error=float(primary.get("scoreError", "nan")),
                unit=primary.get("scoreUnit", ""),
                raw_measurements=raw,
                robust_rsd=robust_rsd(raw),
            )
        )
    return tuple(stats)
