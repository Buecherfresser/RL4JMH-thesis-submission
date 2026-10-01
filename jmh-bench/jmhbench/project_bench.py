"""Project mutation-track pipeline.

Evaluates a harness on a whole vendored real-world project (held out from the
synthetic/real tracks). The harness produces a JMH benchmark *suite*; we then
measure its **performance mutation score**: the fraction of the project's fixed
performance mutants whose activation makes some benchmark measurably slower.

Pipeline (build once, then cheap reruns):

1. Materialise the project: copy the pristine SUT, apply ``mutations.patch``
   (which bakes in the dormant guards), drop the generated suite in, render the
   JMH pom, and ``mvn package`` once -> a single shaded ``benchmarks.jar``.
2. Baseline run (no mutant armed) -> per-benchmark sample distributions.
3. Coverage pass: run each benchmark once in *record* mode -> a map of which
   mutant ids each benchmark actually reaches.
4. Coverage-guided selective detection: for each covered mutant, arm only it
   (``-Djmhbench.mutant=<id>``) and rerun only the benchmark(s) that reach it;
   the mutant is *killed* if any such benchmark slows past the global threshold
   (Welch's t-test, ``min_slowdown`` + ``alpha``), reusing
   :func:`jmhbench.stats.detect_regression`.

This module is deliberately independent of the per-task runner
(:mod:`jmhbench.runner`).
"""

from __future__ import annotations

import logging
import random
import re
import shutil
import subprocess
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import asdict, dataclass, field
from pathlib import Path
from typing import Callable

from jmhbench.build import compiler_heap, mvn_package
from jmhbench.config import JmhSettings, RunConfig
from jmhbench.harness import Harness, HarnessOutput, ProjectTask, RawModelOutput
from jmhbench.jmh_run import BenchmarkResult, JmhRun, run_jmh
from jmhbench.proc import run_jvm
from jmhbench.project import apply_patch, force_package, install_harness_output
from jmhbench.project_compile import (
    compile_single_benchmark,
    filter_compiling_suite,
    filter_runnable_suite,
    tail_diagnostics,
)
from jmhbench.project_layout import component_resolver
from jmhbench.adapters._llm_common import (
    harness_supports_compile_fix,
    render_project_class_fix_prompt,
)
from jmhbench.hostinfo import collect as collect_host_info, short_label as host_label
from jmhbench.stats import detect_regression, robust_rsd

log = logging.getLogger("jmhbench.project_bench")

_TEMPLATE = (
    Path(__file__).resolve().parent.parent / "java-runner" / "project-template" / "pom.xml"
)

# Default repair prompt when ``--compile-check`` is enabled during generation.
_PROJECT_CLASS_FIX_PROMPT = (
    Path(__file__).resolve().parent.parent / "prompts" / "project_class_fix.j2"
)

_PACKAGE_RE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)
_CANONICAL_BENCH_PACKAGE = "bench.generated"

# ---- class-by-class harness input -----------------------------------------

#: Fallback when a project declares no ``harness_input.include_packages``: every
#: component is eligible. Projects narrow this to their public API packages
#: (Commons Compress, for instance, skips the ``harmony``/``pack200`` plumbing
#: and the ``changes`` API) in their own ``project.yaml``.
_DEFAULT_INPUT_PACKAGES: tuple[str, ...] = ()

#: Fallback simple-name filter: none, i.e. every public concrete class is a
#: candidate. Projects supply a filter naming the concrete public entry points a
#: benchmark can realistically drive.
_DEFAULT_NAME_FILTER = None

#: A public, concrete (non-abstract) top-level class or enum declaration.
_PUBLIC_CONCRETE_RE = re.compile(
    r"^\s*public\s+(?:final\s+)?(?:class|enum)\s+(\w+)", re.MULTILINE
)

# Progress callbacks (wired by the CLI to rich console output; ``None`` = quiet).
#: ``(completed, total, ClassView, entry_dict)`` after each per-class generation.
GenProgress = Callable[[int, int, "ClassView", dict], None]
#: ``(human_readable_phase_message)`` at each benchmarking phase boundary.
PhaseProgress = Callable[[str], None]
#: ``(done, total_covered, MutantResult, component)`` after each covered mutant.
DetectProgress = Callable[[int, int, "MutantResult", str], None]

# Safety bound: most kills land on the first (highest-coverage) benchmark, so
# capping how many covering benchmarks we rerun per mutant keeps total runtime
# predictable even for pathological suites, at negligible recall cost.
_MAX_DETECT_ATTEMPTS = 6

_NO_CLASS_MSG = "model output has no class declaration"

# Fast, timing-irrelevant settings for the coverage pass: we only need each
# benchmark to execute once so its MutationSwitch.tick(id) calls fire.
_COVERAGE_SETTINGS = JmhSettings(
    forks=1,
    warmup_iterations=0,
    warmup_time_seconds=0.1,
    measurement_iterations=1,
    measurement_time_seconds=0.1,
    timeout_seconds=120,
)


@dataclass
class BenchmarkVerdict:
    """One (mutant, benchmark) detection outcome.

    Mutation *sensitivity* is the share of the benchmarks covering a mutated
    method that detect it, so it needs a verdict per pair. Recording only the
    first killer collapses that metric onto the kill count; these records are
    what let it be computed instead of bounded.
    """

    benchmark: str
    """Identity of the measured row: ``fqn``, or ``fqn[p=v,...]`` with ``@Param``.

    A ``@Param``-ised method is several distinct measurements sharing one name,
    and pairing them by name alone divides a 64-byte baseline by a 4096-byte
    armed run (EVALUATION_ISSUES.md B3)."""

    method: str = ""
    """The bare ``@Benchmark`` FQN, without parameters -- what JMH's ``-l`` and
    the ``include`` regex operate on."""

    params: dict[str, str] = field(default_factory=dict)
    hits: int = 0
    detected: bool = False
    effect_size: float | None = None
    p_value: float | None = None
    base_samples: list[float] = field(default_factory=list)
    mutant_samples: list[float] = field(default_factory=list)
    base_samples_by_fork: list[list[float]] = field(default_factory=list)
    mutant_samples_by_fork: list[list[float]] = field(default_factory=list)
    mode: str = ""
    """The baseline's JMH mode. Persisted per pair so a bug size can be oriented
    without a lookup that may miss (EVALUATION_ISSUES.md B6)."""

    error: str | None = None
    timed_out: bool = False
    """The armed run exceeded its wall-clock budget.

    Whether this is a kill or an error depends on the baseline, and that has
    changed twice. It was originally scored as a kill on the premise that "this
    benchmark's baseline completed within the same budget", which was false:
    the baseline ran uncapped while the mutant rerun inherited a fixed
    per-invocation cap, so one slow high-coverage benchmark auto-killed every
    mutant it covered on attempt 1 with no t-test run (EVALUATION_ISSUES.md B2).
    It was then demoted to an unconditional error, which over-corrected -- a
    mutant that blows a budget the baseline met is the strongest slowdown
    evidence there is, and dropping it removes the mutant from the denominator
    too.

    ``per_benchmark_baseline`` removed the asymmetry that made the original
    premise false: both arms are now measured one benchmark per invocation, and
    the armed run is given ``5 x baseline_wall + 60s``. A timeout under that
    budget is a >5x slowdown against a 1.10 kill threshold. So the verdict is
    now conditional -- see :attr:`timeout_kill`."""

    timeout_kill: bool = False
    """The armed run timed out **and** this benchmark's baseline did not.

    The baseline condition is the whole point: without a completed baseline
    there is no evidence the mutant caused the timeout rather than the benchmark
    simply being slower than the cap, so those stay errors. With one, the armed
    run exceeded ``5 x`` a wall time the baseline actually met, and it is scored
    as a kill.

    Counted separately as ``mutants_killed_by_timeout`` so the stricter
    errors-only score stays recoverable from any scorecard without a rerun."""

    wall_seconds: float = 0.0
    budget_seconds: float = 0.0
    sampled_for_sensitivity: bool = False
    """True when this pair belongs to the uniform random sample.

    The kill probe walks covering benchmarks most-hit-first and stops on the
    first detection, so the pairs it happens to visit are a biased draw and an
    unbiased sensitivity cannot be computed from them. The uniform sample is
    drawn separately and is the set the sensitivity metric should use.
    """


@dataclass
class MutantResult:
    id: int
    status: str  # killed | covered_not_killed | not_covered | error
    covered_by: list[str] = field(default_factory=list)
    killed_by: str | None = None
    effect_size: float | None = None
    p_value: float | None = None
    error: str | None = None
    detections: list[BenchmarkVerdict] = field(default_factory=list)
    n_timeouts: int = 0
    """Covering benchmarks whose armed run blew its budget. Not kills (B2)."""
    n_measured: int = 0
    """Covering benchmarks that produced a usable t-test."""
    probe_truncated: bool = False
    """The kill probe hit ``max_detect_attempts`` before exhausting coverage."""

    def to_dict(self) -> dict:
        return asdict(self)


@dataclass
class ProjectBenchResult:
    instance_id: str
    harness: str

    generated: bool = False
    generation_error: str | None = None
    generation_metadata: dict = field(default_factory=dict)

    compiles: bool = False
    compile_error: str | None = None

    executes: bool = False
    execution_error: str | None = None
    base_run: dict | None = None
    stability_rsd_percent: float | None = None
    stability_rsd_source: str = ""
    per_benchmark_rsd: dict[str, float] = field(default_factory=dict)

    #: Machine identity, captured before the first benchmark runs. The only
    #: item on the fix list that cannot be retrofitted (EVALUATION_ISSUES.md B5).
    host: dict = field(default_factory=dict)

    #: Per-benchmark baseline wall time, keyed by @Benchmark FQN. Sizes the
    #: mutant arm's budget so both arms are capped the same way (B2).
    baseline_wall_seconds: dict[str, float] = field(default_factory=dict)
    #: Benchmarks whose *baseline* failed or timed out: excluded from the suite
    #: rather than left to auto-kill every mutant they cover (B2).
    baseline_failures: list[dict] = field(default_factory=list)
    #: Benchmarks whose coverage record run failed. These silently produced an
    #: empty coverage set, invisible in the scorecard (EVALUATION_ISSUES.md C5).
    coverage_failures: list[dict] = field(default_factory=list)
    #: Wall-clock budget applied to every single-benchmark JMH invocation.
    wall_budget_seconds: float = 0.0
    mutants_errored: int = 0
    #: Mutants killed *only* because every armed run timed out while the
    #: baseline did not. Recorded separately so the stricter errors-only score
    #: is recoverable as ``mutants_killed - mutants_killed_by_timeout``.
    mutants_killed_by_timeout: int = 0

    #: Max heap given to Maven -- and so to javac, which runs in-process.
    compiler_heap: str = ""
    #: The build failed because *javac* exhausted its heap. A build-machine
    #: limit, not "the model's benchmarks do not compile": at the old 512m
    #: default this alone cost GPT-OSS-120B two whole projects.
    compile_oom: bool = False

    n_benchmarks: int = 0
    mutant_count: int = 0
    mutants_covered: int = 0
    mutants_killed: int = 0
    mutation_score: float = 0.0
    coverage_rate: float = 0.0

    mutant_results: list[dict] = field(default_factory=list)
    coverage_map: dict[str, list[int]] = field(default_factory=dict)

    duration_seconds: float = 0.0
    benchmark_source: str | None = None

    # Class-by-class generation bookkeeping (empty for digest / single-shot).
    input_mode: str = "per_class"
    classes_total: int = 0
    classes_succeeded: int = 0
    class_generations: list[dict] = field(default_factory=list)
    generation_interrupted: bool = False

    # Compile-and-filter (bench): classes dropped because they did not compile.
    classes_compiled: int = 0
    classes_filtered: int = 0
    compile_filter_dropped: list[dict] = field(default_factory=list)

    # Runtime-and-filter (bench): classes dropped because JMH smoke probe failed.
    classes_runtime_filtered: int = 0
    runtime_filter_dropped: list[dict] = field(default_factory=list)

    raw_output: RawModelOutput | None = field(default=None, repr=False)
    raw_output_file: str | None = None
    raw_outputs: list[RawModelOutput] = field(default_factory=list, repr=False)
    extra_sources: dict[str, str] = field(default_factory=dict, repr=False)

    def to_dict(self) -> dict:
        data = asdict(self)
        data.pop("raw_output", None)
        data.pop("raw_outputs", None)
        data.pop("extra_sources", None)
        return data


# ---------------------------------------------------------------------------
# Materialisation + build
# ---------------------------------------------------------------------------


def _render_dependencies(dependencies: list[dict[str, str]]) -> str:
    blocks: list[str] = []
    for dep in dependencies:
        scope = dep.get("scope")
        scope_xml = f"            <scope>{scope}</scope>\n" if scope else ""
        blocks.append(
            "        <dependency>\n"
            f"            <groupId>{dep['groupId']}</groupId>\n"
            f"            <artifactId>{dep['artifactId']}</artifactId>\n"
            f"            <version>{dep['version']}</version>\n"
            f"{scope_xml}"
            "        </dependency>"
        )
    return "\n".join(blocks)


def _ensure_benchmark_package(source: str) -> str:
    """Ensure the generated suite declares a package so it can be placed on disk.

    Unlike the per-task normaliser, this keeps whatever imports the harness wrote
    (the SUT is ``org.apache.commons.compress.*``, imported explicitly), and only
    injects a default package when none is present.
    """
    if _PACKAGE_RE.search(source):
        return source
    return f"package {_CANONICAL_BENCH_PACKAGE};\n\n{source.lstrip()}"


def materialise_project(task: ProjectTask, workdir: Path) -> Path:
    """Copy the SUT, apply the mutation patch, and write the JMH pom."""
    project_dir = workdir / "project"
    if project_dir.exists():
        shutil.rmtree(project_dir)
    project_dir.mkdir(parents=True)

    src_main = project_dir / "src" / "main" / "java"
    src_main.parent.mkdir(parents=True, exist_ok=True)
    shutil.copytree(task.src_root, src_main)

    apply_patch(project_dir, task.patch_path)

    artifact = re.sub(r"[^a-zA-Z0-9_-]", "-", task.instance_id)
    pom = _TEMPLATE.read_text()
    pom = pom.replace("__ARTIFACT__", artifact)
    pom = pom.replace("__DEPENDENCIES__", _render_dependencies(task.dependencies))
    (project_dir / "pom.xml").write_text(pom)
    return project_dir


# ---------------------------------------------------------------------------
# Generation
# ---------------------------------------------------------------------------


@dataclass
class ClassView:
    """A single SUT class presented to a source-driven harness.

    Duck-types just enough of :class:`~jmhbench.harness.Task` /
    :class:`ProjectTask` for the existing adapters (which only read
    ``sut_source`` and, in the prompt, ``target_class``). Extra fields
    (``api_digest``, ``bench_class_name``) are consumed by ``project_class.j2``.
    """

    instance_id: str
    target_class: str  # fully-qualified name of the SUT class being benchmarked
    simple_name: str
    component: str
    bench_class_name: str
    api_digest: str
    project_dir: Path
    _source: str

    @property
    def sut_source(self) -> str:
        return self._source

    @property
    def task_dir(self) -> Path:
        return self.project_dir

    @property
    def junit_test_source(self) -> str | None:
        return None


def _select_sut_classes(task: ProjectTask, max_classes: int | None = None) -> list[ClassView]:
    """Pick the SUT classes to feed a source-driven harness one at a time.

    Honours ``task.harness_input`` (``include_packages``, ``name_filter``,
    explicit ``classes``, ``max_classes``); with none of those set, every public
    concrete class in the library is a candidate. Deterministic order.
    """
    cfg = task.harness_input or {}
    src_root = task.src_root
    digest = task.sut_source  # api_surface.md

    explicit = cfg.get("classes")
    include = tuple(cfg.get("include_packages") or _DEFAULT_INPUT_PACKAGES)
    name_filter = cfg.get("name_filter", _DEFAULT_NAME_FILTER)
    name_re = re.compile(name_filter) if name_filter else None
    cap = max_classes if max_classes is not None else cfg.get("max_classes")
    component_of = component_resolver(src_root, cfg)

    views: list[ClassView] = []
    for path in sorted(src_root.rglob("*.java")):
        rel = path.relative_to(src_root).as_posix()
        if rel.endswith("package-info.java") or rel.endswith("module-info.java"):
            continue
        component = component_of(rel)
        top = component.split("/")[0]
        if include and top not in include:
            continue

        fqcn = rel[:-5].replace("/", ".") if rel.endswith(".java") else rel.replace("/", ".")
        if explicit is not None and fqcn not in explicit:
            continue

        source = path.read_text(encoding="utf-8", errors="replace")
        m = _PUBLIC_CONCRETE_RE.search(source)
        if m is None:  # abstract / interface / package-private: not a direct target
            continue
        simple = m.group(1)
        if explicit is None and name_re is not None and not name_re.search(simple):
            continue

        views.append(
            ClassView(
                instance_id=f"{task.instance_id}:{fqcn}",
                target_class=fqcn,
                simple_name=simple,
                component=component,
                bench_class_name=f"{simple}Benchmark",
                api_digest=digest,
                project_dir=task.project_dir,
                _source=source,
            )
        )

    if cap is not None:
        views = views[: int(cap)]
    return views


def _accumulate_usage(into: dict, meta: dict) -> None:
    for key in ("prompt_tokens", "completion_tokens", "total_tokens"):
        val = meta.get(key)
        if isinstance(val, (int, float)):
            into[key] = into.get(key, 0) + val


@dataclass
class _ClassOutcome:
    """Result of generating a benchmark for one SUT class."""

    entry: dict  # serialisable per-class record (target_class, package, ok, tokens, ...)
    source: str | None  # package-forced benchmark source, or None on failure
    raw: RawModelOutput | None


def _generate_one_class(
    task: ProjectTask,
    harness: Harness,
    view: "ClassView",
    workdir: Path,
    index: int,
    *,
    compile_check: bool = False,
    compile_check_retries: int = 2,
    fix_prompt_template: str | None = None,
) -> _ClassOutcome:
    """Generate a benchmark for a single SUT class (one or more harness calls)."""
    pkg = f"{_CANONICAL_BENCH_PACKAGE}.c{index:03d}"
    entry: dict = {
        "index": index,
        "target_class": view.target_class,
        "simple_name": view.simple_name,
        "component": view.component,
        "package": pkg,
        "ok": False,
    }
    use_compile_check = compile_check and harness_supports_compile_fix(harness)
    if compile_check and not use_compile_check:
        entry["compile_check_skipped"] = "harness does not support compile-fix prompts"

    class_dir = workdir / f"class-{index:03d}"
    raw: RawModelOutput | None = None
    src: str | None = None
    last_errors: str | None = None
    max_attempts = (compile_check_retries + 1) if use_compile_check else 1

    try:
        for attempt in range(max_attempts):
            attempt_dir = class_dir / (f"attempt-{attempt}" if attempt else "initial")
            if attempt == 0:
                out = harness.generate(view, attempt_dir)
            else:
                fix_prompt = render_project_class_fix_prompt(
                    view,
                    src or "",
                    last_errors or "",
                    template_path=fix_prompt_template,
                )
                out = harness.generate_from_prompt(fix_prompt, attempt_dir)  # type: ignore[attr-defined]

            candidate = force_package(out.benchmark_source, pkg)
            if not _PUBLIC_CONCRETE_RE.search(candidate) and "class" not in candidate:
                # Treat an unparseable reply as a failed attempt, not a fatal
                # error. Raising here abandoned the class immediately, so an
                # extraction failure cost it every remaining repair round while
                # a compile failure got all of them -- the two were not being
                # judged on equal terms.
                last_errors = _NO_CLASS_MSG
                src = candidate
                entry["compile_fix_attempts"] = attempt
                continue

            meta = out.metadata or {}
            if meta.get("model"):
                entry["model"] = meta["model"]
            for key in ("prompt_tokens", "completion_tokens", "total_tokens", "generation_seconds"):
                if isinstance(meta.get(key), (int, float)):
                    entry[key] = entry.get(key, 0) + meta[key]

            if out.raw_output is not None:
                raw = out.raw_output

            if not use_compile_check:
                src = candidate
                entry["ok"] = True
                break

            build = compile_single_benchmark(
                task, candidate, attempt_dir / "compile-check",
            )
            if build.success:
                src = candidate
                entry["ok"] = True
                entry["compile_check_attempts"] = attempt + 1
                break

            last_errors = tail_diagnostics(build.diagnostics)
            src = candidate  # keep for next fix prompt
            entry["compile_fix_attempts"] = attempt
            log.warning(
                "class %s compile-check failed (attempt %d/%d)",
                view.target_class, attempt + 1, max_attempts,
            )

        if not entry.get("ok"):
            stage = "extraction" if last_errors == _NO_CLASS_MSG else "compile-check"
            entry["error"] = (
                f"{stage} failed after {max_attempts} attempt(s): "
                f"{(last_errors or 'unknown error').splitlines()[0][:120]}"
            )
            entry["compile_error"] = last_errors
            return _ClassOutcome(entry=entry, source=None, raw=raw)

        return _ClassOutcome(entry=entry, source=src, raw=raw)
    except Exception as exc:  # noqa: BLE001 - one bad class shouldn't sink the run
        entry["error"] = f"{type(exc).__name__}: {exc}"
        log.warning("class %s failed: %s", view.target_class, entry["error"])
        return _ClassOutcome(entry=entry, source=None, raw=raw)


def _generate_per_class(
    task: ProjectTask,
    harness: Harness,
    workdir: Path,
    result: ProjectBenchResult,
    max_classes: int | None,
    *,
    parallel: int = 1,
    on_progress: GenProgress | None = None,
    compile_check: bool = False,
    compile_check_retries: int = 2,
    fix_prompt_template: str | None = None,
) -> ProjectBenchResult:
    """Feed the harness one SUT class at a time and merge the per-class
    benchmark files into a single unique-package suite.

    Generation of distinct classes is independent, so up to *parallel* harness
    calls run concurrently; package indices stay tied to selection order so the
    merged suite layout is deterministic regardless of completion order.
    """
    views = _select_sut_classes(task, max_classes)
    result.input_mode = "per_class"
    result.classes_total = len(views)
    workers = max(1, parallel)
    log.info(
        "[%s] per-class generation over %d SUT class(es) (parallel=%d, compile_check=%s)",
        task.instance_id, len(views), workers, compile_check,
    )

    gen_kwargs = dict(
        compile_check=compile_check,
        compile_check_retries=compile_check_retries,
        fix_prompt_template=fix_prompt_template,
    )

    outcomes: dict[int, _ClassOutcome] = {}
    completed = 0

    if workers == 1:
        try:
            for i, view in enumerate(views):
                oc = _generate_one_class(task, harness, view, workdir, i, **gen_kwargs)
                outcomes[i] = oc
                completed += 1
                if on_progress is not None:
                    on_progress(completed, len(views), view, oc.entry)
        except KeyboardInterrupt:
            result.generation_interrupted = True
    else:
        executor = ThreadPoolExecutor(max_workers=workers)
        futures = {
            executor.submit(
                _generate_one_class, task, harness, v, workdir, i, **gen_kwargs,
            ): i
            for i, v in enumerate(views)
        }
        try:
            for future in as_completed(futures):
                i = futures[future]
                outcomes[i] = future.result()
                completed += 1
                if on_progress is not None:
                    on_progress(completed, len(views), views[i], outcomes[i].entry)
            executor.shutdown(wait=True)
        except KeyboardInterrupt:
            result.generation_interrupted = True
            for future in futures:
                future.cancel()
            executor.shutdown(wait=False, cancel_futures=True)

    # Assemble in selection order so package indices are stable and reproducible.
    sources: list[str] = []
    usage: dict[str, float] = {}
    for i in range(len(views)):
        oc = outcomes.get(i)
        if oc is None:  # not finished (interrupted) — skip
            continue
        result.class_generations.append(oc.entry)
        if oc.source is not None:
            sources.append(oc.source)
            _accumulate_usage(usage, oc.entry)
            if oc.raw is not None:
                result.raw_outputs.append(oc.raw)

    result.classes_succeeded = len(sources)
    if not sources:
        result.generation_error = (
            f"per-class generation produced no usable benchmark (0/{len(views)} classes)"
        )
        return result

    result.generated = True
    result.benchmark_source = sources[0]
    result.extra_sources = {f"{_rel_for(src)}": src for src in sources[1:]}
    result.generation_metadata.update(
        {
            "input_mode": "per_class",
            "parallel": workers,
            "compile_check": compile_check,
            "compile_check_retries": compile_check_retries if compile_check else 0,
            "classes_total": len(views),
            "classes_succeeded": len(sources),
            "classes_failed": len(result.class_generations) - len(sources),
            **usage,
        }
    )
    return result


def _rel_for(source: str) -> str:
    """Relative ``pkg/Class.java`` path for an already package-declared source."""
    pkg_m = _PACKAGE_RE.search(source)
    cls_m = _PUBLIC_CONCRETE_RE.search(source) or re.search(r"\bclass\s+(\w+)", source)
    pkg = pkg_m.group(1) if pkg_m else _CANONICAL_BENCH_PACKAGE
    cls = cls_m.group(1) if cls_m else "Benchmark"
    return "/".join(pkg.split(".")) + f"/{cls}.java"


def _generate_single_shot(
    task: ProjectTask,
    harness: Harness,
    workdir: Path,
    result: ProjectBenchResult,
) -> ProjectBenchResult:
    """One harness call using the API digest (non-source-driven harnesses, or
    explicit ``mode: digest``)."""
    result.input_mode = "digest"
    try:
        output = harness.generate(task, workdir / "harness")
        output.benchmark_source = _ensure_benchmark_package(output.benchmark_source)
        result.generated = True
        result.generation_metadata = dict(output.metadata)
        result.generation_metadata["input_mode"] = "digest"
        result.benchmark_source = output.benchmark_source
        result.raw_output = output.raw_output
        result.extra_sources = dict(output.extra_sources)
    except Exception as exc:  # noqa: BLE001 - surfaced in the report
        result.generation_error = f"{type(exc).__name__}: {exc}"
    return result


def generate_project(
    task: ProjectTask,
    harness: Harness,
    workdir: Path,
    *,
    input_mode: str | None = None,
    max_classes: int | None = None,
    parallel: int = 1,
    on_progress: GenProgress | None = None,
    compile_check: bool = False,
    compile_check_retries: int = 2,
    fix_prompt_template: str | None = None,
) -> ProjectBenchResult:
    """Produce a JMH suite for *task*.

    Source-driven harnesses (LLMs) default to **class-by-class** generation: the
    harness is called once per real SUT class (up to *parallel* concurrently)
    and the per-class benchmark files are merged into one suite. Non-source-driven
    harnesses (dummy-passthrough, predictions), or ``input_mode="digest"``, take a
    single digest-based call.
    """
    started = time.time()
    workdir.mkdir(parents=True, exist_ok=True)
    result = ProjectBenchResult(instance_id=task.instance_id, harness=harness.name)

    mode = input_mode or task.harness_input.get("mode", "per_class")
    source_driven = getattr(harness, "source_driven", True)
    log.info(
        "[%s] generating via harness=%s (mode=%s, source_driven=%s)",
        task.instance_id, harness.name, mode, source_driven,
    )

    fix_tpl = fix_prompt_template or task.harness_input.get("fix_prompt_template")
    if fix_tpl:
        fix_tpl = str(fix_tpl)

    if mode == "per_class" and source_driven:
        _generate_per_class(
            task, harness, workdir, result, max_classes,
            parallel=parallel, on_progress=on_progress,
            compile_check=compile_check,
            compile_check_retries=compile_check_retries,
            fix_prompt_template=fix_tpl,
        )
    else:
        _generate_single_shot(task, harness, workdir, result)

    elapsed = time.time() - started
    result.generation_metadata["generation_seconds"] = round(elapsed, 3)
    result.duration_seconds += elapsed
    return result


# ---------------------------------------------------------------------------
# Benchmarking + scoring
# ---------------------------------------------------------------------------


def _index_results(run: JmhRun) -> dict[str, BenchmarkResult]:
    """Index by ``(benchmark, params)`` identity, never by name alone.

    A dict comprehension keyed on ``r.benchmark`` silently kept the **last**
    ``@Param`` combination while the mutant side's ``next(...)`` scan kept the
    **first**, so the two arms of a pair could be different measurements
    entirely (EVALUATION_ISSUES.md B3).
    """
    return {r.key: r for r in run.results}


def _summarise_results(results: list[BenchmarkResult], *, ok: bool, error: str | None) -> dict:
    return {
        "ok": ok,
        "error": error,
        "benchmarks": [
            {
                # ``name`` is the pairing key: the method plus its @Param values.
                "name": r.key,
                "method": r.benchmark,
                "params": dict(r.params),
                "mode": r.mode,
                "score": r.score,
                "unit": r.unit,
                "n_samples": len(r.samples),
                # The individual iteration measurements, not just their mean.
                # Bootstrap bug size and per-benchmark RSD are both defined on
                # the sample, and neither can be reconstructed from a score.
                "samples": list(r.samples),
                # ...and the same sample with the fork boundary intact, which a
                # hierarchical bootstrap needs and the flat list destroys (B4).
                "samples_by_fork": [list(f) for f in r.samples_by_fork],
                "n_forks": len(r.samples_by_fork),
            }
            for r in results
        ],
    }


def _summarise(run: JmhRun) -> dict:
    return _summarise_results(run.results, ok=run.success, error=run.error)


def _list_benchmarks(project_dir: Path, timeout: int = 120) -> list[str]:
    """Enumerate benchmark FQNs via ``-l`` without executing them.

    Used to size the baseline wall-clock timeout to the surviving suite: the
    baseline runs *every* benchmark in one JMH process, so its budget must scale
    with the benchmark count rather than the fixed per-invocation cap.
    """
    from jmhbench.build import java_executable  # noqa: PLC0415
    from jmhbench.jmh_run import _jmh_env  # noqa: PLC0415

    jar = (project_dir / "target" / "benchmarks.jar").resolve()
    if not jar.exists():
        return []
    try:
        proc = run_jvm(
            [java_executable(), "-jar", str(jar), "-l"],
            cwd=str(project_dir.resolve()),
            timeout=timeout,
            env=_jmh_env(),
        )
    except (subprocess.TimeoutExpired, OSError):
        return []
    names: list[str] = []
    for line in proc.stdout.splitlines():
        s = line.strip()
        if s.startswith(_CANONICAL_BENCH_PACKAGE):
            names.append(s)
    return names


def _read_record_file(path: Path) -> dict[int, int]:
    """Parse a coverage dump of ``<mutant-id> <hit-count>`` lines."""
    if not path.exists():
        return {}
    counts: dict[int, int] = {}
    for line in path.read_text().splitlines():
        parts = line.split()
        if len(parts) == 2 and parts[0].isdigit() and parts[1].isdigit():
            counts[int(parts[0])] = int(parts[1])
        elif len(parts) == 1 and parts[0].isdigit():  # tolerate id-only dumps
            counts[int(parts[0])] = 1
    return counts


def _include_regex(benchmark_name: str) -> str:
    return re.escape(benchmark_name) + "$"


def _record_coverage(
    project_dir: Path,
    benchmark_names: list[str],
    on_cover: Callable[[int, int, str, int], None] | None = None,
) -> tuple[dict[str, dict[int, int]], list[dict]]:
    """Run each benchmark once in record mode -> per-benchmark mutant hit counts.

    The hit count is how many times a benchmark reached each mutant during the
    (short) record run. A high count means the mutant is on that benchmark's
    measured hot path; a low count usually means it was only touched during
    @Setup. Detection uses this to try the most promising benchmark first.

    *on_cover* (if given) is called ``(i+1, total, benchmark, n_mutants_hit)``
    after each benchmark's record run for progress reporting.

    Returns ``(coverage, failures)``; *failures* names the benchmarks whose
    record run did not complete, whose coverage is therefore unknown rather
    than empty.
    """
    coverage: dict[str, dict[int, int]] = {}
    failures: list[dict] = []
    total = len(benchmark_names)
    for i, name in enumerate(benchmark_names):
        record_file = (project_dir / f"coverage-{i}.txt").resolve()
        if record_file.exists():
            record_file.unlink()
        run = run_jmh(
            project_dir,
            _COVERAGE_SETTINGS,
            tag=f"cov-{i}",
            jvm_args=[
                "-Djmhbench.record=true",
                f"-Djmhbench.record.file={record_file}",
            ],
            include=_include_regex(name),
        )
        if not run.success:
            # A failed record run yields an empty coverage set, which is
            # indistinguishable from "this benchmark reaches no mutant" and used
            # to leave only a log line behind (EVALUATION_ISSUES.md C5).
            log.warning("[coverage] benchmark %s failed: %s", name, run.error)
            coverage[name] = {}
            failures.append({
                "benchmark": name,
                "error": run.error,
                "timed_out": run.error == "timeout",
            })
        else:
            coverage[name] = _read_record_file(record_file)
        if on_cover is not None:
            on_cover(i + 1, total, name, len(coverage[name]))
    return coverage, failures


def _run_baseline(
    project_dir: Path,
    config: RunConfig,
    bench_names: list[str],
    phase: Callable[[str], None],
) -> tuple[list[BenchmarkResult], dict[str, float], list[dict], str | None]:
    """Measure the unmutated suite, one benchmark per JMH invocation.

    Returns ``(results, wall_by_method, failures, error)``.

    The whole-suite baseline this replaces ran with ``wall_timeout=None``,
    deliberately -- a full suite can legitimately take hours -- while every
    mutant rerun inherited a fixed per-invocation cap. A benchmark slower than
    that cap therefore passed the baseline and timed out on every armed run,
    and because the kill probe tries the highest-hit-count benchmark first and
    stopped on the first "detection", one slow high-coverage benchmark
    auto-killed every mutant it covered (EVALUATION_ISSUES.md B2).

    Running both arms one benchmark at a time under the *same* budget removes
    the asymmetry, drops the benchmarks that cannot be measured within it, and
    yields the per-benchmark wall time the mutant arm's budget is sized from.
    The extra cost is one launcher JVM start per benchmark; the forks, which
    dominate the runtime, are unchanged.
    """
    budget = config.jmh.wall_budget_seconds()
    results: list[BenchmarkResult] = []
    wall_by_method: dict[str, float] = {}
    failures: list[dict] = []
    total = len(bench_names)
    step = max(1, total // 20)

    for i, name in enumerate(bench_names):
        started = time.time()
        run = run_jmh(
            project_dir,
            config.jmh,
            tag=f"base-{i}",
            include=_include_regex(name),
            wall_timeout=budget,
        )
        elapsed = time.time() - started
        if not run.success or not run.results:
            failures.append({
                "benchmark": name,
                "error": run.error or "no results",
                "timed_out": run.error == "timeout",
                "wall_seconds": round(elapsed, 2),
                "budget_seconds": budget,
            })
        else:
            results.extend(run.results)
            wall_by_method[name] = elapsed
        if (i + 1) % step == 0 or i + 1 == total:
            phase(f"baseline {i + 1}/{total} benchmark(s) — {len(failures)} unusable")

    error = None
    if not results:
        error = (
            f"every one of the {total} benchmark(s) failed the baseline; "
            f"first error: {failures[0]['error'] if failures else 'unknown'}"
        )
    return results, wall_by_method, failures, error


def _detect_mutants(
    project_dir: Path,
    task: ProjectTask,
    config: RunConfig,
    baseline: dict[str, BenchmarkResult],
    coverage: dict[str, dict[int, int]],
    baseline_wall: dict[str, float] | None = None,
    on_detect: DetectProgress | None = None,
) -> list[MutantResult]:
    """Coverage-guided selective rerun: arm one mutant, rerun its covering
    benchmarks, and flag it killed if any of them slows past the threshold.

    *baseline* and *coverage* are both keyed on ``(benchmark, params)`` identity
    (``fqn`` or ``fqn[p=v,...]``), so a ``@Param``-ised method contributes one
    pair per combination and each is compared against its own baseline
    (EVALUATION_ISSUES.md B3).

    Two probes run over the covering set:

    * the **kill probe** walks it most-hit-first and stops at the first
      detection, capped at ``config.max_detect_attempts``. It decides the
      mutation score and is deliberately cheap;
    * the **sensitivity probe** measures ``config.sensitivity_sample`` covering
      benchmarks with no ranking and no early stop -- ``-1`` meaning *all* of
      them. Only the ``-1`` mode makes mutation sensitivity a point estimate;
      any fixed *k* is a bound unless *k* happens to exceed the largest fan-out
      (EVALUATION_ISSUES.md B1).

    A JMH invocation covers every ``@Param`` combination of one method, so one
    run fills in the verdict for all sibling keys at once; results are memoised
    per method to keep the two probes from paying twice.

    *on_detect* (if given) is called ``(done, total_covered, MutantResult,
    component)`` after each *covered* mutant is resolved (uncovered mutants need
    no rerun and are skipped for progress).
    """
    baseline_wall = baseline_wall or {}
    budget = config.jmh.wall_budget_seconds()

    # Which @Benchmark method each pairing key belongs to, and the reverse.
    method_of: dict[str, str] = {key: r.benchmark for key, r in baseline.items()}
    siblings_of: dict[str, list[str]] = {}
    for key, method in method_of.items():
        siblings_of.setdefault(method, []).append(key)

    # Invert coverage: mutant id -> [(key, hit_count)] for keys we have a
    # usable baseline distribution for.
    covered_by: dict[int, list[tuple[str, int]]] = {}
    for key, counts in coverage.items():
        if key not in baseline or len(baseline[key].samples) < 2:
            continue
        for mid, hits in counts.items():
            covered_by.setdefault(mid, []).append((key, hits))

    component_of = {m.id: m.component for m in task.mutants}
    total_covered = len(covered_by)
    done = 0

    max_attempts = config.max_detect_attempts
    sample_n = config.sensitivity_sample

    results: list[MutantResult] = []
    for mutant in task.mutants:
        mid = mutant.id
        ranked = covered_by.get(mid, [])
        if not ranked:
            results.append(MutantResult(id=mid, status="not_covered"))
            continue
        # Most-hit benchmark first; tie-break on name for determinism.
        ranked = sorted(ranked, key=lambda x: (-x[1], x[0]))
        all_keys = [key for key, _ in ranked]
        hits_of = dict(ranked)
        covering = set(all_keys)
        attempts = all_keys if max_attempts <= 0 else all_keys[:max_attempts]

        killed = False
        last_error: str | None = None
        best_effect: float | None = None
        best_p: float | None = None
        killer: str | None = None
        verdicts: dict[str, BenchmarkVerdict] = {}
        runs: dict[str, None] = {}   # methods already rerun for this mutant

        def _blank(key: str) -> BenchmarkVerdict:
            base = baseline.get(key)
            return BenchmarkVerdict(
                benchmark=key,
                method=method_of.get(key, key),
                params=dict(base.params) if base else {},
                hits=hits_of.get(key, 0),
                mode=base.mode if base else "",
                budget_seconds=budget,
            )

        def probe(key: str) -> BenchmarkVerdict:
            """Measure one covering pair against the armed mutant.

            One JMH invocation runs every ``@Param`` combination of the method,
            so all sibling keys in this mutant's covering set are filled in from
            the same run. Memoised, so the sensitivity probe never pays for a
            benchmark the kill probe already measured.
            """
            cached = verdicts.get(key)
            if cached is not None:
                return cached

            method = method_of.get(key, key)
            targets = [k for k in siblings_of.get(method, [key]) if k in covering]
            if key not in targets:
                targets.append(key)

            # Both arms get the same wall-clock budget. Where the baseline for
            # this method is known, allow the armed run a generous multiple of
            # it: a benchmark that genuinely takes 300s must not be scored on
            # whether it fits a cap the baseline never had applied (B2).
            wall = baseline_wall.get(method)
            timeout = max(budget, 5.0 * wall + 60.0) if wall else budget

            started = time.time()
            rerun = run_jmh(
                project_dir,
                config.jmh,
                tag=f"mut-{mid}-{_safe(method)}",
                jvm_args=[f"-Djmhbench.mutant={mid}"],
                include=_include_regex(method),
                wall_timeout=timeout,
            )
            elapsed = time.time() - started

            if rerun.error == "timeout":
                # A kill only if this benchmark's baseline completed. `wall` is
                # populated from baseline_wall, which holds an entry only for a
                # method whose baseline invocation succeeded, so it is exactly
                # the "baseline did not time out" test. Where it is missing the
                # timeout stays an error: nothing distinguishes a mutant-induced
                # slowdown from a benchmark that never fit the cap to begin with.
                attributable = bool(wall)
                for k in targets:
                    v = _blank(k)
                    v.timed_out = True
                    v.timeout_kill = attributable
                    v.detected = attributable
                    v.wall_seconds = elapsed
                    v.budget_seconds = timeout
                    v.error = (
                        f"armed run exceeded its {timeout:.0f}s budget "
                        f"(baseline {wall:.0f}s)" if wall
                        else f"armed run exceeded its {timeout:.0f}s budget"
                    )
                    verdicts[k] = v
                runs[method] = None
                return verdicts[key]

            if not rerun.success:
                for k in targets:
                    v = _blank(k)
                    v.error = rerun.error or "no-matching-benchmark"
                    v.wall_seconds = elapsed
                    v.budget_seconds = timeout
                    verdicts[k] = v
                runs[method] = None
                return verdicts[key]

            measured = {r.key: r for r in rerun.results}
            for k in targets:
                v = _blank(k)
                v.wall_seconds = elapsed
                v.budget_seconds = timeout
                matched = measured.get(k)
                base_r = baseline.get(k)
                if matched is None or base_r is None:
                    v.error = "no-matching-benchmark"
                    verdicts[k] = v
                    continue
                test = detect_regression(
                    base_r.samples,
                    matched.samples,
                    mode=base_r.mode,
                    alpha=config.alpha,
                    min_slowdown=config.min_slowdown,
                )
                v.detected = bool(test.detected)
                v.effect_size = test.effect_size
                v.p_value = test.p_value
                v.mode = base_r.mode
                v.base_samples = list(base_r.samples)
                v.mutant_samples = list(matched.samples)
                v.base_samples_by_fork = [list(f) for f in base_r.samples_by_fork]
                v.mutant_samples_by_fork = [list(f) for f in matched.samples_by_fork]
                verdicts[k] = v
            runs[method] = None
            return verdicts.get(key) or _blank(key)

        def _better(candidate: float | None, current: float | None) -> bool:
            """Is *candidate* a strictly larger, non-NaN effect than *current*?

            Order matters: testing ``current is None`` first let a leading NaN be
            stored, after which every ``x > nan`` comparison is False and every
            later valid effect was discarded -- and the NaN also flipped the
            error branch, dropping ``last_error`` (EVALUATION_ISSUES.md C5).
            """
            if candidate is None or candidate != candidate:  # None or NaN
                return False
            if current is None or current != current:
                return True
            return candidate > current

        # Kill probe: most-hit first, stop at the first detection.
        for name in attempts:
            verdict = probe(name)
            if verdict.timeout_kill:
                # Checked before the effect-size guard below: a timeout has no
                # effect size, so it would otherwise be skipped as an error and
                # never reach the `detected` test.
                killed = True
                killer = name
                last_error = verdict.error or last_error
                break
            if verdict.effect_size is None:
                last_error = verdict.error or last_error
                continue
            if _better(verdict.effect_size, best_effect):
                best_effect = verdict.effect_size
                best_p = verdict.p_value
            if verdict.detected:
                killed = True
                killer = name
                best_effect = verdict.effect_size
                best_p = verdict.p_value
                break

        # Sensitivity probe: unranked, no early stop. ``-1`` means every
        # covering benchmark, which is the only setting under which mutation
        # sensitivity is a measurement rather than an interval (B1).
        if sample_n < 0:
            draw = list(all_keys)
        elif sample_n > 0:
            rng = random.Random(f"{task.instance_id}:{mid}")
            draw = rng.sample(all_keys, min(len(all_keys), sample_n))
        else:
            draw = []
        for name in draw:
            probe(name).sampled_for_sensitivity = True

        detections = [verdicts[key] for key in all_keys if key in verdicts]
        n_timeouts = sum(1 for v in detections if v.timed_out)
        n_measured = sum(1 for v in detections if v.effect_size is not None)
        truncated = len(attempts) < len(all_keys) and not draw

        if killed:
            status = "killed"
        elif n_measured > 0:
            status = "covered_not_killed"
        else:
            # Every covering benchmark errored or blew its budget. That is an
            # infrastructure outcome, not a surviving mutant, and it must not be
            # silently pooled with the mutants that were genuinely measured and
            # not killed.
            status = "error"

        mres = MutantResult(
            id=mid,
            status=status,
            covered_by=all_keys,
            killed_by=killer,
            effect_size=best_effect,
            p_value=best_p,
            error=last_error if best_effect is None else None,
            detections=detections,
            n_timeouts=n_timeouts,
            n_measured=n_measured,
            probe_truncated=truncated,
        )
        results.append(mres)
        done += 1
        if on_detect is not None:
            on_detect(done, total_covered, mres, component_of.get(mid, "?"))
    return results


def _safe(name: str) -> str:
    return re.sub(r"[^a-zA-Z0-9]", "_", name)[-60:]


def bench_project(
    task: ProjectTask,
    result: ProjectBenchResult,
    config: RunConfig,
    workdir: Path,
    *,
    compile_filter: bool = True,
    runtime_filter: bool = True,
    on_phase: PhaseProgress | None = None,
    on_cover: Callable[[int, int, str, int], None] | None = None,
    on_detect: DetectProgress | None = None,
) -> ProjectBenchResult:
    """Build once, baseline, record coverage, then selectively detect mutants.

    The optional ``on_phase``/``on_cover``/``on_detect`` callbacks report
    progress (the CLI wires them to console output); without them the function
    is silent except for ``log`` messages.
    """
    def phase(msg: str) -> None:
        log.info("[%s] %s", task.instance_id, msg)
        if on_phase is not None:
            on_phase(msg)

    started = time.time()
    # Machine identity first, before any load is put on the box: the load
    # average is only meaningful taken at the start, and this is the one item on
    # the fix list that cannot be recovered afterwards (EVALUATION_ISSUES.md B5).
    result.host = collect_host_info()
    log.info("[%s] host: %s", task.instance_id, host_label(result.host))

    if not result.generated or not result.benchmark_source:
        # The model produced nothing for this project, so there is no suite to
        # build, mutate or measure. mutant_count stays 0, which downstream must
        # read as "not measured" rather than as a silent absence.
        result.execution_error = (
            "no benchmark source was generated; nothing was built, mutated or measured"
        )
        return result

    result.mutant_count = task.mutant_count
    workdir.mkdir(parents=True, exist_ok=True)
    output = HarnessOutput(
        benchmark_source=result.benchmark_source,
        extra_sources=dict(result.extra_sources or {}),
    )

    n_files = 1 + len(result.extra_sources or {})
    phase(f"materialising + patching project ({n_files} generated file(s))")
    project_dir = materialise_project(task, workdir)
    try:
        install_harness_output(project_dir, output)
    except Exception as exc:  # noqa: BLE001
        result.compile_error = f"layout: {exc}"
        result.duration_seconds += time.time() - started
        return result

    phase(f"building (mvn package) — compiles the whole SUT once, javac heap {compiler_heap()}")
    result.compiler_heap = compiler_heap()
    build = mvn_package(project_dir, timeout=config.jmh.timeout_seconds + 600)

    if build.oom:
        # Do NOT fall through to compile-and-filter. That pass exists to drop
        # classes the *model* got wrong; a compiler heap exhaustion says nothing
        # about any individual class, and letting the filter respond to it would
        # either waste a full rebuild (best case) or silently drop good classes
        # and score the model on a truncated suite (worse). Stop and say why.
        result.compiles = False
        result.compile_oom = True
        result.classes_compiled = 0
        result.compile_error = tail_diagnostics(build.diagnostics)
        phase(f"build FAILED — javac ran out of heap at {compiler_heap()}; raise --compiler-heap")
        log.error(
            "[%s] compiler OOM at heap=%s — this is a build-machine limit, not a "
            "property of the generated suite",
            task.instance_id, compiler_heap(),
        )
        result.duration_seconds += time.time() - started
        return result

    if not build.success and compile_filter:
        phase("build failed — compile-and-filter: dropping non-compiling classes")
        fr = filter_compiling_suite(
            task,
            output,
            workdir / "compile-filter",
            rel_for=_rel_for,
            package_only=True,
            timeout=config.jmh.timeout_seconds + 600,
        )
        result.compile_filter_dropped = fr.dropped
        result.classes_filtered = len(fr.dropped)
        output = fr.output
        result.benchmark_source = output.benchmark_source
        result.extra_sources = dict(output.extra_sources)

        if not output.benchmark_source:
            result.compiles = False
            result.compile_error = tail_diagnostics(build.diagnostics)
            result.classes_compiled = 0
            phase("build FAILED — all classes filtered out")
            result.duration_seconds += time.time() - started
            return result

        kept = 1 + len(output.extra_sources)
        phase(
            f"compile-and-filter: kept {kept} class(es), "
            f"dropped {len(fr.dropped)} — rebuilding"
        )
        project_dir = materialise_project(task, workdir)
        install_harness_output(project_dir, output)
        build = mvn_package(project_dir, timeout=config.jmh.timeout_seconds + 600)

    result.compiles = build.success
    result.compile_oom = result.compile_oom or build.oom
    result.classes_compiled = (1 + len(result.extra_sources or {})) if build.success else 0
    if not build.success:
        result.compile_error = tail_diagnostics(build.diagnostics)
        phase("build FAILED" + (f" — javac ran out of heap at {compiler_heap()}" if build.oom else ""))
        result.duration_seconds += time.time() - started
        return result

    if config.skip_jmh:
        phase("build OK (skip-jmh: stopping before execution)")
        result.duration_seconds += time.time() - started
        return result

    if runtime_filter:
        phase("runtime smoke probe (per-class JMH)")
        rr = filter_runnable_suite(
            task,
            output,
            workdir / "runtime-filter",
            rel_for=_rel_for,
            timeout=config.jmh.timeout_seconds + 600,
        )
        if rr.dropped:
            result.runtime_filter_dropped = rr.dropped
            result.classes_runtime_filtered = len(rr.dropped)
            output = rr.output
            result.benchmark_source = output.benchmark_source
            result.extra_sources = dict(output.extra_sources)
            if not output.benchmark_source:
                result.executes = False
                result.execution_error = "runtime-and-filter removed all classes"
                phase("runtime-and-filter removed all classes — stopping")
                result.duration_seconds += time.time() - started
                return result
            kept = 1 + len(output.extra_sources)
            phase(
                f"runtime-and-filter: kept {kept} class(es), "
                f"dropped {len(rr.dropped)} — rebuilding"
            )
            project_dir = materialise_project(task, workdir)
            install_harness_output(project_dir, output)
            build = mvn_package(project_dir, timeout=config.jmh.timeout_seconds + 600)
            result.compiles = build.success
            result.classes_compiled = (1 + len(result.extra_sources or {})) if build.success else 0
            if not build.success:
                result.compile_error = tail_diagnostics(build.diagnostics)
                phase("rebuild FAILED after runtime-and-filter")
                result.duration_seconds += time.time() - started
                return result

    bench_names = _list_benchmarks(project_dir)
    budget = config.jmh.wall_budget_seconds()
    result.wall_budget_seconds = budget
    phase(
        f"JMH baseline run (all mutants off) — {len(bench_names)} benchmark(s), "
        f"one invocation each, {budget:.0f}s budget per benchmark "
        f"(per-iteration -to {config.jmh.iteration_timeout_seconds}s)"
    )
    if config.jmh.per_benchmark_baseline and bench_names:
        base_results, wall_by_method, base_failures, base_error = _run_baseline(
            project_dir, config, bench_names, phase
        )
        result.baseline_wall_seconds = {k: round(v, 2) for k, v in wall_by_method.items()}
        result.baseline_failures = base_failures
        result.base_run = _summarise_results(
            base_results, ok=bool(base_results), error=base_error
        )
        baseline = {r.key: r for r in base_results}
    else:
        # Legacy whole-suite baseline, retained for `per_benchmark_baseline=False`.
        # fail_on_error=False so one pathological benchmark does not discard the
        # rest -- a property the per-benchmark path gets for free.
        base_run = run_jmh(
            project_dir, config.jmh, tag="base", wall_timeout=None, fail_on_error=False
        )
        result.base_run = _summarise(base_run)
        baseline = _index_results(base_run)
        wall_by_method = {}
        base_error = base_run.error

    result.executes = bool(baseline)
    if not baseline:
        result.execution_error = base_error or "jmh-base-produced-no-results"
        phase(f"baseline FAILED: {result.execution_error}")
        result.duration_seconds += time.time() - started
        return result

    if result.baseline_failures:
        n_to = sum(1 for f in result.baseline_failures if f.get("timed_out"))
        phase(
            f"baseline: {len(baseline)} measurement(s) from "
            f"{len(bench_names) - len(result.baseline_failures)}/{len(bench_names)} "
            f"benchmark(s) — {len(result.baseline_failures)} unusable "
            f"({n_to} over budget), excluded from coverage and detection"
        )
    # n_benchmarks counts *measurements*: a @Param-ised method contributes one
    # per combination, which is what the detection loop pairs on (B3).
    result.n_benchmarks = len(baseline)

    # Suite-median RSD, not the RSD of whichever benchmark JMH happened to write
    # first -- that scalar was being reported as a suite stability figure.
    per_bench_rsd: dict[str, float] = {}
    for key, r in baseline.items():
        if len(r.samples) >= 2:
            value = robust_rsd(r.samples)
            if value == value:  # not NaN
                per_bench_rsd[key] = round(value, 6)
    result.per_benchmark_rsd = per_bench_rsd
    if per_bench_rsd:
        ordered = sorted(per_bench_rsd.values())
        half = len(ordered) // 2
        result.stability_rsd_percent = (
            ordered[half] if len(ordered) % 2 else (ordered[half - 1] + ordered[half]) / 2
        )
        result.stability_rsd_source = f"median over {len(ordered)} per-benchmark RSDs"
    elif baseline:
        first = next(iter(baseline.values()))
        result.stability_rsd_percent = robust_rsd(first.samples)
        result.stability_rsd_source = "single benchmark (no benchmark had >= 2 samples)"

    # The coverage pass runs per @Benchmark *method*; its record run exercises
    # every @Param combination, so the hit counts apply to each of that method's
    # measurement keys.
    methods = sorted({r.benchmark for r in baseline.values()})
    phase(f"coverage pass over {len(methods)} benchmark method(s)")
    coverage_by_method, cov_failures = _record_coverage(
        project_dir, methods, on_cover=on_cover
    )
    result.coverage_failures = cov_failures
    if cov_failures:
        phase(
            f"coverage: {len(cov_failures)} benchmark(s) failed their record run — "
            "their coverage is unknown, not empty"
        )
    coverage: dict[str, dict[int, int]] = {}
    for key, r in baseline.items():
        counts = coverage_by_method.get(r.benchmark)
        if counts:
            coverage[key] = dict(counts)
    result.coverage_map = {name: sorted(ids) for name, ids in coverage.items()}

    n_covered = len({mid for counts in coverage.values() for mid in counts})
    phase(f"detection: {n_covered}/{task.mutant_count} mutant(s) covered by the suite")
    mutant_results = _detect_mutants(
        project_dir, task, config, baseline, coverage,
        baseline_wall=wall_by_method, on_detect=on_detect,
    )
    result.mutant_results = [m.to_dict() for m in mutant_results]
    result.mutants_killed = sum(1 for m in mutant_results if m.status == "killed")
    # Subtract this from mutants_killed to recover the errors-only score.
    result.mutants_killed_by_timeout = sum(
        1 for m in mutant_results
        if m.status == "killed"
        and any(v.timeout_kill for v in (m.detections or []))
        and not any(v.detected and not v.timed_out for v in (m.detections or []))
    )
    result.mutants_covered = sum(
        1 for m in mutant_results if m.status in ("killed", "covered_not_killed")
    )
    result.mutants_errored = sum(1 for m in mutant_results if m.status == "error")
    total = result.mutant_count or 1
    result.mutation_score = round(result.mutants_killed / total, 4)
    result.coverage_rate = round(result.mutants_covered / total, 4)
    phase(
        f"done: killed {result.mutants_killed}/{result.mutant_count} "
        f"(score {result.mutation_score:.1%})"
    )

    result.duration_seconds += time.time() - started
    return result


def run_project(
    task: ProjectTask,
    harness: Harness,
    config: RunConfig,
    workdir: Path,
    *,
    input_mode: str | None = None,
    max_classes: int | None = None,
    parallel: int = 1,
    compile_filter: bool = True,
    runtime_filter: bool = True,
    compile_check: bool = False,
    compile_check_retries: int = 2,
    fix_prompt_template: str | None = None,
    on_progress: GenProgress | None = None,
    on_phase: PhaseProgress | None = None,
    on_cover: Callable[[int, int, str, int], None] | None = None,
    on_detect: DetectProgress | None = None,
) -> ProjectBenchResult:
    """Generate the suite then benchmark it (combined phase)."""
    result = generate_project(
        task, harness, workdir,
        input_mode=input_mode, max_classes=max_classes,
        parallel=parallel, on_progress=on_progress,
        compile_check=compile_check,
        compile_check_retries=compile_check_retries,
        fix_prompt_template=fix_prompt_template,
    )
    if not result.generated:
        return result
    return bench_project(
        task, result, config, workdir,
        compile_filter=compile_filter,
        runtime_filter=runtime_filter,
        on_phase=on_phase, on_cover=on_cover, on_detect=on_detect,
    )
