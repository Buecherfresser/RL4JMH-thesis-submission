"""Performance-regression detection (ported from JMH-Bench ``jmhbench/stats.py``).

A mutant is *killed* when arming it makes a benchmark measurably slower than the
baseline. "Measurably" is the same global rule real perf gates use (e.g. Criterion.rs,
asv): the observed slowdown must clear a fixed practical effect (``min_slowdown``) **and**
be statistically significant (``p < alpha``) under Welch's unequal-variance t-test. The
threshold is deliberately not per-mutant — a detector never knows a regression's magnitude
in advance.

The t-test p-value is computed with a self-contained regularized incomplete beta function
so the reward workers stay dependency-light (no SciPy on the JVM-only reward path).
"""

from __future__ import annotations

import math
from dataclasses import dataclass
from statistics import fmean


@dataclass(frozen=True, slots=True)
class RegressionTest:
    """Verdict for one (baseline, armed) sample comparison."""

    detected: bool
    p_value: float
    # Slowdown ratio, normalised so ``> 1`` always means "slower" regardless of JMH mode.
    effect_size: float
    mode: str
    method: str = "welch_t"

    def to_dict(self) -> dict[str, object]:
        return {
            "detected": self.detected,
            "p_value": self.p_value,
            "effect_size": self.effect_size,
            "mode": self.mode,
            "method": self.method,
        }


# -- regularized incomplete beta (Numerical Recipes ``betai``) -------------------------------


def _betacf(a: float, b: float, x: float) -> float:
    max_iter = 200
    eps = 3.0e-12
    fpmin = 1.0e-300
    qab = a + b
    qap = a + 1.0
    qam = a - 1.0
    c = 1.0
    d = 1.0 - qab * x / qap
    if abs(d) < fpmin:
        d = fpmin
    d = 1.0 / d
    h = d
    for m in range(1, max_iter + 1):
        m2 = 2 * m
        aa = m * (b - m) * x / ((qam + m2) * (a + m2))
        d = 1.0 + aa * d
        if abs(d) < fpmin:
            d = fpmin
        c = 1.0 + aa / c
        if abs(c) < fpmin:
            c = fpmin
        d = 1.0 / d
        h *= d * c
        aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2))
        d = 1.0 + aa * d
        if abs(d) < fpmin:
            d = fpmin
        c = 1.0 + aa / c
        if abs(c) < fpmin:
            c = fpmin
        d = 1.0 / d
        delta = d * c
        h *= delta
        if abs(delta - 1.0) < eps:
            break
    return h


def regularized_incomplete_beta(a: float, b: float, x: float) -> float:
    """The regularized incomplete beta ``I_x(a, b)`` in ``[0, 1]``."""
    if x <= 0.0:
        return 0.0
    if x >= 1.0:
        return 1.0
    ln_beta = math.lgamma(a + b) - math.lgamma(a) - math.lgamma(b)
    front = math.exp(ln_beta + a * math.log(x) + b * math.log(1.0 - x))
    if x < (a + 1.0) / (a + b + 2.0):
        return front * _betacf(a, b, x) / a
    return 1.0 - front * _betacf(b, a, 1.0 - x) / b


def student_t_sf(t: float, df: float) -> float:
    """Upper-tail probability ``P(T > t)`` for a Student-t with ``df`` degrees of freedom."""
    if df <= 0.0:
        return float("nan")
    x = df / (df + t * t)
    half_two_tailed = 0.5 * regularized_incomplete_beta(df / 2.0, 0.5, x)
    return half_two_tailed if t > 0.0 else 1.0 - half_two_tailed


def _sample_variance(values: list[float], mean: float) -> float:
    if len(values) < 2:
        return 0.0
    return sum((v - mean) ** 2 for v in values) / (len(values) - 1)


def welch_ttest_greater(a: list[float], b: list[float]) -> tuple[float, float, float]:
    """One-sided Welch's t-test of ``mean(a) > mean(b)``.

    Returns ``(t_statistic, degrees_of_freedom, p_value)``. The p-value is the probability
    of observing a t at least this large under H0 (means equal).
    """
    na, nb = len(a), len(b)
    if na < 2 or nb < 2:
        return float("nan"), float("nan"), 1.0
    mean_a, mean_b = fmean(a), fmean(b)
    var_a = _sample_variance(a, mean_a)
    var_b = _sample_variance(b, mean_b)
    se2_a = var_a / na
    se2_b = var_b / nb
    denom = se2_a + se2_b
    if denom <= 0.0:
        # Zero variance on both sides: a difference in means is a certain effect.
        return (float("inf"), float(na + nb - 2), 0.0) if mean_a > mean_b else (0.0, 1.0, 1.0)
    t = (mean_a - mean_b) / math.sqrt(denom)
    df_num = denom * denom
    df_den = (se2_a * se2_a) / (na - 1) + (se2_b * se2_b) / (nb - 1)
    df = df_num / df_den if df_den > 0.0 else float(na + nb - 2)
    return t, df, student_t_sf(t, df)


def detect_regression(
    base_samples: list[float],
    armed_samples: list[float],
    mode: str,
    alpha: float = 0.05,
    min_slowdown: float = 1.10,
) -> RegressionTest:
    """Decide whether ``armed_samples`` is a significant slowdown over ``base_samples``.

    *mode* is the JMH mode string (``"thrpt"``, ``"avgt"``, ``"sample"``, ``"ss"``). For
    throughput "slower" means a *lower* score; for the time-based modes it means a *higher*
    score. The effect size is normalised so ``> 1`` always denotes a slowdown.
    """
    if len(base_samples) < 2 or len(armed_samples) < 2:
        return RegressionTest(False, float("nan"), float("nan"), mode)

    base_mean = fmean(base_samples)
    armed_mean = fmean(armed_samples)
    if base_mean == 0.0 or armed_mean == 0.0:
        return RegressionTest(False, float("nan"), float("nan"), mode)

    if mode.startswith("thrpt"):
        effect = base_mean / armed_mean
        _, _, p_value = welch_ttest_greater(base_samples, armed_samples)
    else:  # avgt, sample, ss: higher score == slower
        effect = armed_mean / base_mean
        _, _, p_value = welch_ttest_greater(armed_samples, base_samples)

    if math.isnan(p_value):
        p_value = 1.0
    detected = bool(p_value < alpha and effect >= min_slowdown)
    return RegressionTest(detected=detected, p_value=p_value, effect_size=float(effect), mode=mode)
