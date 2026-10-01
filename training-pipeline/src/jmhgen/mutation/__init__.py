"""Performance-mutation testing as a verifiable reward signal (ported from JMH-Bench).

A subject library gets a fixed set of dormant performance mutants baked into its public
methods by :mod:`jmhgen.mutation.generate` (one ``mutations.patch`` + ground-truth
``mutants.yaml``). A candidate benchmark is then scored by how many of its target class's
mutants it detects when each is armed at run time (:mod:`jmhgen.mutation.score`), using the
same statistical detection rule real perf gates use (:mod:`jmhgen.mutation.detect`).

Public surface:
    * Generation: :func:`generate_mutants` (heuristic), :func:`generate_from_sites` (curated),
      :class:`SiteSpec`, :class:`MutantPlan`, :class:`Site`, :func:`is_benchmarkable_type`.
    * Registry: :class:`MutantSpec`, :func:`load_mutants`, :func:`mutants_by_fqcn`.
    * Patch: :func:`apply_patch`, :func:`revert_patch`, :class:`PatchError`.
    * Detection: :func:`detect_regression`, :class:`RegressionTest`.
    * Scoring: :func:`score_benchmark_mutations`, :class:`MutationScore`.
"""

from jmhgen.mutation.detect import RegressionTest, detect_regression
from jmhgen.mutation.generate import (
    MutantPlan,
    Site,
    SiteSpec,
    generate_from_sites,
    generate_mutants,
    is_benchmarkable_type,
    resolve_sites,
)
from jmhgen.mutation.patch import PatchError, apply_patch, revert_patch
from jmhgen.mutation.registry import MutantSpec, load_mutants, mutants_by_fqcn
from jmhgen.mutation.score import MutationScore, score_benchmark_mutations

__all__ = [
    "MutantPlan",
    "MutantSpec",
    "MutationScore",
    "PatchError",
    "RegressionTest",
    "Site",
    "SiteSpec",
    "apply_patch",
    "detect_regression",
    "generate_from_sites",
    "generate_mutants",
    "is_benchmarkable_type",
    "load_mutants",
    "mutants_by_fqcn",
    "resolve_sites",
    "revert_patch",
    "score_benchmark_mutations",
]
