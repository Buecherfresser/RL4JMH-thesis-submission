"""Statistical helpers for regression detection and stability scoring."""

from __future__ import annotations

import math
import statistics
from dataclasses import dataclass

from scipy import stats  # type: ignore[import-untyped]

#: Consistency constant that makes ``1.4826 * MAD`` an unbiased estimator of the
#: standard deviation for normally distributed data (it equals ``1 / Φ⁻¹(3/4)``).
_MAD_TO_SD = 1.4826


@dataclass
class RegressionTest:
    detected: bool
    p_value: float
    effect_size: float
    """Slowdown ratio: regressed_mean / base_mean for throughput-style metrics
    is inverted to be 'larger means slower' irrespective of mode."""

    mode: str
    method: str = "welch_t"

    def to_dict(self) -> dict:
        return {
            "detected": self.detected,
            "p_value": self.p_value,
            "effect_size": self.effect_size,
            "mode": self.mode,
            "method": self.method,
        }


def robust_rsd(samples: list[float]) -> float:
    """Robust relative standard deviation (RSD), in percent.

    Defined as in LLM4JMH (Chen et al.)::

        RSD = 1.4826 * median(|Xi - X̃|) / X̃

    where ``Xi`` is the i-th execution-time measurement and ``X̃`` their median.
    The numerator is ``1.4826 * MAD`` — a median-absolute-deviation based,
    outlier-resistant estimate of the standard deviation — so this is an
    outlier-resistant analogue of the coefficient of variation. JIT warmup
    spikes and GC pauses make microbenchmark samples heavy-tailed, so the
    median/MAD form is far less sensitive to a handful of stray iterations than
    the mean/standard-deviation CV.

    Returns NaN for fewer than 2 samples or a zero median.
    """
    if len(samples) < 2:
        return float("nan")
    median = statistics.median(samples)
    if median == 0:
        return float("nan")
    mad = statistics.median([abs(x - median) for x in samples])
    return _MAD_TO_SD * mad / abs(median) * 100.0


def detect_regression(
    base_samples: list[float],
    regressed_samples: list[float],
    mode: str,
    alpha: float = 0.05,
    min_slowdown: float = 1.10,
) -> RegressionTest:
    """Return a regression-detection verdict.

    *mode* is the JMH mode string (``"thrpt"``, ``"avgt"``, ``"sample"``, etc.).
    For throughput, "slower" means *lower* score; for everything else,
    "slower" means *higher* score. We normalise so ``effect_size > 1`` always
    indicates a slowdown.

    A regression is flagged only when *both* a practical and a statistical
    bar are cleared, mirroring how real perf gates work (e.g. Criterion.rs's
    noise threshold + significance, asv's step detection): the measured
    slowdown must be at least *min_slowdown* (a fixed relative effect, e.g.
    ``1.10`` for +10%) *and* significant at *alpha* (e.g. ``p < 0.05``). The
    threshold is deliberately global, not per-task: a detector never knows a
    regression's magnitude in advance.
    """
    if len(base_samples) < 2 or len(regressed_samples) < 2:
        return RegressionTest(False, float("nan"), float("nan"), mode)

    base_mean = sum(base_samples) / len(base_samples)
    reg_mean = sum(regressed_samples) / len(regressed_samples)
    if base_mean == 0 or reg_mean == 0:
        return RegressionTest(False, float("nan"), float("nan"), mode)

    if mode.startswith("thrpt"):
        effect = base_mean / reg_mean
        # Welch's t-test: regressed has lower throughput
        t = stats.ttest_ind(base_samples, regressed_samples, equal_var=False, alternative="greater")
    else:  # avgt, sample, ss
        effect = reg_mean / base_mean
        t = stats.ttest_ind(regressed_samples, base_samples, equal_var=False, alternative="greater")

    p_value = float(t.pvalue) if not math.isnan(float(t.pvalue)) else 1.0
    detected = bool(p_value < alpha and effect >= min_slowdown)
    return RegressionTest(detected=detected, p_value=p_value, effect_size=float(effect), mode=mode)
