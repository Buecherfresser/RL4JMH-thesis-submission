"""Metrics for the campaign rerun, including the LLM4JMH set.

LLM4JMH definitions are reproduced from its replication package
(``Scripts/src/analysis_bug_sizes.py``, ``analysis_rciw.py``) so the numbers are
comparable to the paper rather than merely similar:

**Bug size** -- bootstrap the ratio ``mean(buggy) / mean(normal)`` 10k times at
99% (percentiles 0.5 / 99.5) and report ``1 - upper``. Throughput mode, so a
slower mutant gives a ratio below 1 and a positive bug size. A bug size <= 0
means the interval still admits "no regression".

**RCIW** -- relative confidence interval width, ``(U - L) / mean`` from a 99%
bootstrap CI of a benchmark's own iteration scores. LLM4JMH walks prefixes of
length 2..n to show how precision improves with iterations.

One deliberate addition. LLM4JMH reads ``rawData[-1]``: a single fork, because
its runs use ``-f 1``. This campaign used 5 forks, so both estimators are
computed here:

* ``flat``         -- pool all 50 samples, matching LLM4JMH's shape.
* ``hierarchical`` -- resample forks first, then iterations within the drawn
  forks. Iterations inside a fork are autocorrelated and fork-to-fork variance
  is the dominant component, so the flat interval is anti-conservative
  (EVALUATION_ISSUES B4). Reporting both quantifies that gap instead of
  assuming it.
"""
from __future__ import annotations

import json
import math
from dataclasses import dataclass
from pathlib import Path

import numpy as np

BOOT = 10_000
CONF = 99.0
MODELS = ["dsv4", "gemma-run9-ck1600", "gpt-oss-120b"]
MODEL_LABEL = {"dsv4": "DeepSeek-V4-Flash",
               "gemma-run9-ck1600": "Gemma-4 E2B (run9 ck1600)",
               "gpt-oss-120b": "GPT-OSS-120B"}
PROJECTS = ["commons-compress", "decimal4j", "fastfilter", "hppc", "jodd-util", "snakeyaml"]


# ---------------------------------------------------------------- bootstrapping

def _boot_ratio_flat(base: np.ndarray, mut: np.ndarray, rng, iters=BOOT) -> np.ndarray:
    """LLM4JMH's estimator: i.i.d. resample of pooled samples, both arms."""
    b = rng.choice(base, (iters, base.size), replace=True).mean(axis=1)
    m = rng.choice(mut, (iters, mut.size), replace=True).mean(axis=1)
    return m / b


def _boot_ratio_hier(base_f: list[list[float]], mut_f: list[list[float]], rng,
                     iters=BOOT) -> np.ndarray:
    """Two-level resample: forks with replacement, then iterations within them.

    Fully vectorised -- the obvious Python loop over `iters` costs ~50k inner
    operations per detection, which does not scale to the campaign's ~500.
    Ragged forks are truncated to the shortest so the draw stays rectangular;
    under a fixed preset every fork has the same iteration count anyway.
    """
    def stack(forks):
        fs = [np.asarray(f, dtype=float) for f in forks if len(f)]
        if not fs:
            return None
        n = min(f.size for f in fs)
        return np.vstack([f[:n] for f in fs])       # (F, I)

    B, M = stack(base_f), stack(mut_f)
    if B is None or M is None:
        return np.array([])

    def draw(a):
        F, I = a.shape
        fi = rng.integers(0, F, (iters, F))          # which forks
        ii = rng.integers(0, I, (iters, F, I))       # which iterations in each
        vals = a[fi[:, :, None], ii]                 # (iters, F, I)
        return vals.mean(axis=2).mean(axis=1)        # fork means, then overall

    db, dm = draw(B), draw(M)
    with np.errstate(divide="ignore", invalid="ignore"):
        r = dm / db
    return r[np.isfinite(r)]


def bug_size(base_f, mut_f, method="flat", rng=None, iters=BOOT) -> float | None:
    """``1 - upper`` of the 99% CI on mean(mutant)/mean(base). None if unusable."""
    rng = rng or np.random.default_rng(0)
    if method == "hierarchical":
        ratios = _boot_ratio_hier(base_f, mut_f, rng, iters)
    else:
        base = np.asarray([x for f in base_f for x in f], dtype=float)
        mut = np.asarray([x for f in mut_f for x in f], dtype=float)
        if base.size < 2 or mut.size < 2 or base.mean() <= 0:
            return None
        ratios = _boot_ratio_flat(base, mut, rng, iters)
    if ratios.size == 0:
        return None
    lo = (100 - CONF) / 2
    upper = float(np.percentile(ratios, 100 - lo))
    return 1.0 - upper


def rciw(samples: np.ndarray, rng, iters=BOOT) -> float | None:
    """(U - L) / mean from a 99% bootstrap CI of the mean."""
    s = np.asarray(samples, dtype=float)
    if s.size < 2 or s.mean() == 0:
        return None
    means = rng.choice(s, (iters, s.size), replace=True).mean(axis=1)
    lo = (100 - CONF) / 2
    L, U = np.percentile(means, [lo, 100 - lo])
    return float((U - L) / s.mean())


def rciw_curve(samples: np.ndarray, rng, iters=2000) -> list[float]:
    """RCIW over prefixes 2..n, as LLM4JMH reports it."""
    s = np.asarray(samples, dtype=float)
    return [v for k in range(2, s.size + 1)
            if (v := rciw(s[:k], rng, iters)) is not None]


def wilson(k: int, n: int, z: float = 1.96) -> tuple[float, float]:
    """Wilson score interval - the CI the existing thesis tables already use."""
    if n == 0:
        return (0.0, 0.0)
    p = k / n
    d = 1 + z * z / n
    c = p + z * z / (2 * n)
    m = z * math.sqrt(p * (1 - p) / n + z * z / (4 * n * n))
    return ((c - m) / d, (c + m) / d)


# ------------------------------------------------------------------- loading

@dataclass
class Cell:
    model: str
    project: str
    result: dict

    @property
    def key(self) -> str:
        return f"{self.model}/{self.project}"


def load_cells(root: Path, prefer_rescored: bool = True) -> list[Cell]:
    """Every archived cell, newest scoring first."""
    cells = []
    for d in sorted(root.glob("*/out/*/report")):
        f = d / ("scorecard.rescored.json" if prefer_rescored else "scorecard.json")
        if not f.exists():
            f = d / "scorecard.json"
        if not f.exists():
            continue
        cell = d.parent.name
        model = next((m for m in MODELS if cell.startswith(m)), None)
        if model is None:
            continue
        cells.append(Cell(model, cell[len(model) + 1:], json.load(open(f))["result"]))
    return cells


def detections(result: dict):
    """Yield (mutant, detection) for detections carrying per-fork samples."""
    for m in result.get("mutant_results") or []:
        for det in m.get("detections") or []:
            if det.get("base_samples_by_fork") and det.get("mutant_samples_by_fork"):
                yield m, det
