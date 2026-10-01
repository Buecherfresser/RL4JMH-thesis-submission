"""Aggregate per-task results into a run-level scorecard."""

from __future__ import annotations

from dataclasses import dataclass, field

from jmhbench.runner import TaskResult


@dataclass
class Scorecard:
    n_tasks: int
    n_generated: int
    n_compiles: int
    n_executes: int
    n_no_antipatterns: int
    antipattern_counts: dict[str, int] = field(default_factory=dict)
    """Per-pattern: number of tasks where the pattern was violated."""

    antipattern_source: str = "regex"
    """Which checker produced ``antipattern_counts`` for this run:
    ``spotjmhbugs`` (Costa et al. bytecode plugin) or ``regex`` (source-level
    pre-screen). Stays ``regex`` if the vendored SpotBugs install was
    missing for the whole run."""

    regression_total: int = 0
    regression_detected: int = 0

    fpr_replicate_total: int = 0
    """Total FPR replicates (per-task * replicates). 0 when not in FPR mode."""
    fpr_false_positives: int = 0
    """Replicates where the regression test fired despite identical SUT bytecode."""
    fpr_tasks_with_any_fp: int = 0
    """Number of tasks where at least one replicate produced a false positive."""

    composite_passes: int = 0
    """Tasks that hit (compiles AND executes AND ≤1 antipattern AND all regressions detected)."""

    median_stability_rsd: float | None = None

    def to_dict(self) -> dict:
        rates: dict[str, float] = {
            "generated": _safe_rate(self.n_generated, self.n_tasks),
            "compiles": _safe_rate(self.n_compiles, self.n_tasks),
            "executes": _safe_rate(self.n_executes, self.n_tasks),
            "clean": _safe_rate(self.n_no_antipatterns, self.n_tasks),
            "regression_detection": _safe_rate(self.regression_detected, self.regression_total),
            "composite": _safe_rate(self.composite_passes, self.n_tasks),
        }
        if self.fpr_replicate_total > 0:
            rates["fpr_per_replicate"] = _safe_rate(
                self.fpr_false_positives, self.fpr_replicate_total
            )
            rates["fpr_per_task"] = _safe_rate(self.fpr_tasks_with_any_fp, self.n_tasks)
        return {
            "n_tasks": self.n_tasks,
            "n_generated": self.n_generated,
            "n_compiles": self.n_compiles,
            "n_executes": self.n_executes,
            "n_no_antipatterns": self.n_no_antipatterns,
            "antipattern_counts": self.antipattern_counts,
            "antipattern_source": self.antipattern_source,
            "regression_total": self.regression_total,
            "regression_detected": self.regression_detected,
            "fpr_replicate_total": self.fpr_replicate_total,
            "fpr_false_positives": self.fpr_false_positives,
            "fpr_tasks_with_any_fp": self.fpr_tasks_with_any_fp,
            "composite_passes": self.composite_passes,
            "median_stability_rsd": self.median_stability_rsd,
            "rates": rates,
        }


def _safe_rate(num: int, denom: int) -> float:
    return 0.0 if denom == 0 else round(num / denom, 4)


def aggregate(results: list[TaskResult]) -> Scorecard:
    from statistics import median

    # Prefer SpotJMHBugs (Costa et al. plugin) whenever any task has its
    # output; fall back to the regex pre-screen run by static_check.
    use_spotjmhbugs = any(
        getattr(r, "spotjmhbugs", None) and r.spotjmhbugs.get("available")
        for r in results
    )

    def _ap_total(r: TaskResult) -> int:
        if use_spotjmhbugs and r.spotjmhbugs and r.spotjmhbugs.get("available"):
            return int(r.spotjmhbugs.get("total", 0))
        sc = r.static_check or {}
        return int(sc.get("total", 0)) if sc else 0

    def _ap_categories_hit(r: TaskResult) -> list[str]:
        """Return the set of *categories* (Costa codes or regex keys) that
        the chosen checker flagged for this task."""
        if use_spotjmhbugs and r.spotjmhbugs and r.spotjmhbugs.get("available"):
            return [cat for cat, hits in (r.spotjmhbugs.get("violations") or {}).items() if hits]
        return [name for name, hit in ((r.static_check or {}).get("violations") or {}).items() if hit]

    card = Scorecard(
        n_tasks=len(results),
        n_generated=sum(1 for r in results if r.generated),
        n_compiles=sum(1 for r in results if r.compiles),
        n_executes=sum(1 for r in results if r.executes),
        n_no_antipatterns=sum(1 for r in results if _ap_total(r) == 0),
    )
    card.antipattern_source = "spotjmhbugs" if use_spotjmhbugs else "regex"

    ap_counts: dict[str, int] = {}
    for r in results:
        for cat in _ap_categories_hit(r):
            ap_counts[cat] = ap_counts.get(cat, 0) + 1
    card.antipattern_counts = ap_counts

    for r in results:
        for reg_id, info in r.regression_results.items():
            card.regression_total += 1
            if info.get("detected"):
                card.regression_detected += 1

    for r in results:
        if not r.fpr_results:
            continue
        any_fp = False
        for info in r.fpr_results.values():
            card.fpr_replicate_total += 1
            if info.get("detected"):
                card.fpr_false_positives += 1
                any_fp = True
        if any_fp:
            card.fpr_tasks_with_any_fp += 1

    for r in results:
        all_reg_ok = all(info.get("detected") for info in r.regression_results.values())
        if r.compiles and r.executes and _ap_total(r) <= 1 and all_reg_ok and r.regression_results:
            card.composite_passes += 1

    rsds = [r.stability_rsd_percent for r in results if r.stability_rsd_percent is not None]
    card.median_stability_rsd = float(median(rsds)) if rsds else None
    return card
