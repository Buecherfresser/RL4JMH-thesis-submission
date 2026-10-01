"""Per-candidate performance-mutation scoring.

The candidate benchmark targets one subject class, so scoring is scoped to the mutants planted
in that class. Coverage-guided mode mirrors JMH-Bench: a short record pass establishes which
individual ``@Benchmark`` methods reach each mutant, then armed detection reruns only those
methods. This separates an unreachable mutant from a reachable-but-undetected one.
"""

from __future__ import annotations

import dataclasses
from dataclasses import dataclass, field
from typing import Any

from jmhgen.mutation.coverage import benchmark_method_filter, record_coverage
from jmhgen.mutation.detect import detect_regression
from jmhgen.mutation.registry import MutantSpec
from jmhgen.runner.base import JmhRunner
from jmhgen.runner.types import BenchmarkSpec, BenchmarkStat, ErrorKind, JmhOptions
from jmhgen.utils.logging import get_logger

logger = get_logger("jmhgen.mutation.score")

# Curated classes are intentionally small. Zero means score every in-class mutant; a positive
# limit remains available as a future safety override for an unexpectedly large corpus.
DEFAULT_MAX_MUTANTS_PER_CANDIDATE = 0


@dataclass(frozen=True, slots=True)
class MutationScore:
    """Outcome of scoring one candidate benchmark against its class's mutants.

    ``score`` is the reward value under the selected ``mode`` (see :class:`MutationConfig`).
    ``sampled_kill_rate`` is always ``killed / attempted``; ``graded_score`` is the descriptive
    full-class rate ``killed / total``. They match when all in-class mutants are scored.
    """

    # None when the target class has no mutants (mutation is undefined for this candidate).
    score: float | None
    total: int
    killed: int
    attempted: int
    mode: str = "graded"
    graded_score: float | None = None
    sampled_kill_rate: float | None = None
    coverage_rate: float | None = None
    conditional_kill_rate: float | None = None
    not_covered: int = 0
    covered_not_killed: int = 0
    killed_timeout: int = 0
    killed_statistical: int = 0
    verdicts: list[dict[str, Any]] = field(default_factory=list)
    error: str | None = None

    def to_dict(self) -> dict[str, Any]:
        return {
            "score": self.score,
            "graded_score": self.graded_score,
            "mode": self.mode,
            "total": self.total,
            "killed": self.killed,
            "attempted": self.attempted,
            "sampled_kill_rate": self.sampled_kill_rate,
            "coverage_rate": self.coverage_rate,
            "conditional_kill_rate": self.conditional_kill_rate,
            "not_covered": self.not_covered,
            "covered_not_killed": self.covered_not_killed,
            "killed_timeout": self.killed_timeout,
            "killed_statistical": self.killed_statistical,
            "verdicts": self.verdicts,
            "error": self.error,
        }


def _baseline_samples(stats: tuple[BenchmarkStat, ...]) -> dict[str, tuple[list[float], str]]:
    """Map each baseline benchmark method -> (samples, mode), keeping only usable ones."""
    out: dict[str, tuple[list[float], str]] = {}
    for stat in stats:
        samples = list(stat.raw_measurements)
        if len(samples) >= 2:
            out[stat.benchmark] = (samples, stat.mode)
    return out


def _armed_options(
    base: JmhOptions,
    spec: BenchmarkSpec,
    arm_property: str,
    mutant_id: int,
    *,
    benchmark_method: str | None = None,
) -> JmhOptions:
    jvm_args = (*base.jvm_args, f"-D{arm_property}={mutant_id}")
    return dataclasses.replace(
        base,
        jvm_args=jvm_args,
        benchmark_filter=(
            benchmark_method_filter(spec, benchmark_method)
            if benchmark_method is not None
            else base.benchmark_filter or spec.fully_qualified_name
        ),
    )


def _detect_in_run(
    rerun_stats: tuple[BenchmarkStat, ...],
    baseline: dict[str, tuple[list[float], str]],
    *,
    alpha: float,
    min_slowdown: float,
) -> tuple[bool, float | None, float | None]:
    """Return whether any comparable benchmark stat detects a regression."""
    best_effect: float | None = None
    best_p: float | None = None
    for stat in rerun_stats:
        if stat.benchmark not in baseline:
            continue
        base_samples, base_mode = baseline[stat.benchmark]
        test = detect_regression(
            base_samples,
            list(stat.raw_measurements),
            mode=base_mode,
            alpha=alpha,
            min_slowdown=min_slowdown,
        )
        if best_effect is None or (
            test.effect_size == test.effect_size and test.effect_size > best_effect
        ):
            best_effect = test.effect_size
            best_p = test.p_value
        if test.detected:
            return True, test.effect_size, test.p_value
    return False, best_effect, best_p


def score_benchmark_mutations(
    runner: JmhRunner,
    spec: BenchmarkSpec,
    base_options: JmhOptions,
    baseline_stats: tuple[BenchmarkStat, ...],
    mutants: list[MutantSpec],
    *,
    alpha: float = 0.05,
    min_slowdown: float = 1.10,
    arm_property: str = "jmhbench.mutant",
    max_mutants: int = DEFAULT_MAX_MUTANTS_PER_CANDIDATE,
    mode: str = "graded",
    coverage_guidance: bool = False,
    coverage_options: JmhOptions | None = None,
    record_property: str = "jmhbench.record",
    record_file_property: str = "jmhbench.record.file",
    max_benchmarks_per_mutant: int = 6,
    coverage_weight: float = 0.30,
) -> MutationScore:
    """Score one candidate against its target class's mutants.

    ``mutants`` are the registry entries whose ``fqcn`` is the candidate's subject class.
    ``baseline_stats`` come from the verify phase's un-armed run of the same ``spec``. ``mode``
    selects the reward mapping: ``"graded"`` (strict kill rate), ``"gate"`` (any-kill binary),
    or ``"coverage_aware"``. In coverage-guided mode, only methods reaching a mutant are armed.
    """
    total = len(mutants)
    if total == 0:
        return MutationScore(score=None, total=0, killed=0, attempted=0, mode=mode)

    baseline = _baseline_samples(baseline_stats)
    if not baseline:
        return MutationScore(
            score=0.0, total=total, killed=0, attempted=0, mode=mode, error="no_usable_baseline"
        )

    attempted = total if max_mutants == 0 else min(total, max_mutants)
    selected_mutants = mutants[:attempted]
    coverage_by_mutant: dict[int, list[tuple[str, int]]] = {}
    if coverage_guidance:
        if coverage_options is None:
            return MutationScore(
                score=0.0,
                total=total,
                killed=0,
                attempted=attempted,
                mode=mode,
                error="missing_coverage_options",
            )
        coverage_by_mutant = record_coverage(
            runner,
            spec,
            dataclasses.replace(
                coverage_options,
                jvm_args=(*base_options.jvm_args, *coverage_options.jvm_args),
            ),
            record_property=record_property,
            record_file_property=record_file_property,
        )

    killed = 0
    covered = 0
    not_covered = 0
    covered_not_killed = 0
    killed_timeout = 0
    killed_statistical = 0
    verdicts: list[dict[str, Any]] = []
    for mutant in selected_mutants:
        covering = coverage_by_mutant.get(mutant.id)
        if coverage_guidance and not covering:
            not_covered += 1
            verdicts.append({"id": mutant.id, "method": mutant.method, "status": "not_covered"})
            continue
        covered += 1
        methods = (
            [method for method, _hits in covering[:max_benchmarks_per_mutant]]
            if coverage_guidance
            else [None]
        )
        mutant_killed = False
        timeout = False
        best_effect: float | None = None
        best_p: float | None = None
        last_error: str | None = None
        killed_by: str | None = None
        for method in methods:
            options = _armed_options(
                base_options, spec, arm_property, mutant.id, benchmark_method=method
            )
            rerun = runner.run(spec, options)
            if rerun.error_kind == ErrorKind.TIMEOUT:
                mutant_killed = True
                timeout = True
                killed_by = method
                break
            if not rerun.success:
                last_error = rerun.error_kind.value
                continue
            detected, effect, p_value = _detect_in_run(
                rerun.stats, baseline, alpha=alpha, min_slowdown=min_slowdown
            )
            if best_effect is None or (effect is not None and effect > best_effect):
                best_effect, best_p = effect, p_value
            if detected:
                mutant_killed = True
                killed_by = method
                break

        if mutant_killed:
            killed += 1
            if timeout:
                killed_timeout += 1
                status = "killed_timeout"
            else:
                killed_statistical += 1
                status = "killed_statistical"
        else:
            covered_not_killed += 1
            status = "covered_not_killed"
        verdict = {
            "id": mutant.id,
            "method": mutant.method,
            "status": status,
            "covered_by": [method for method, _hits in covering] if covering else [],
            "killed_by": killed_by,
            "effect_size": best_effect,
            "p_value": best_p,
        }
        if timeout:
            verdict["reason"] = "timeout"
        if last_error is not None:
            verdict["error"] = last_error
        verdicts.append(verdict)

    sampled_kill_rate = killed / attempted if attempted else 0.0
    graded = killed / total
    coverage_rate = covered / attempted if coverage_guidance and attempted else None
    conditional_kill_rate = killed / covered if coverage_guidance and covered else 0.0
    if mode == "gate":
        score = 1.0 if killed > 0 else 0.0
    elif mode == "coverage_aware":
        score = round(
            coverage_weight * (coverage_rate or 0.0)
            + (1.0 - coverage_weight) * conditional_kill_rate,
            4,
        )
    else:
        score = sampled_kill_rate
    return MutationScore(
        score=score,
        graded_score=graded,
        sampled_kill_rate=sampled_kill_rate,
        coverage_rate=coverage_rate,
        conditional_kill_rate=conditional_kill_rate,
        mode=mode,
        total=total,
        killed=killed,
        attempted=attempted,
        not_covered=not_covered,
        covered_not_killed=covered_not_killed,
        killed_timeout=killed_timeout,
        killed_statistical=killed_statistical,
        verdicts=verdicts,
    )
