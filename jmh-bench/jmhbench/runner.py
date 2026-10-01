"""Per-task execution pipeline."""

from __future__ import annotations

import logging
import threading
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import asdict, dataclass, field
from pathlib import Path
from typing import Callable

from jmhbench.adapters._llm_common import LlmSkipped
from jmhbench import spotjmhbugs
from jmhbench.build import mvn_compile_only, mvn_package
from jmhbench.config import RunConfig
from jmhbench.harness import Harness, HarnessOutput, RawModelOutput, Task
from jmhbench.jmh_run import JmhRun, run_jmh
from jmhbench.project import (
    apply_patch,
    install_harness_output,
    materialise_project,
    normalize_benchmark_package,
    revert_patch,
)
from jmhbench.static_check import StaticCheckResult, check as static_check
from jmhbench.stats import RegressionTest, detect_regression, robust_rsd

log = logging.getLogger("jmhbench.runner")


@dataclass
class TaskResult:
    instance_id: str
    harness: str

    generated: bool = False
    generation_error: str | None = None
    generation_metadata: dict = field(default_factory=dict)

    compiles: bool = False
    compile_error: str | None = None

    static_check: dict = field(default_factory=dict)
    """Regex pre-screen on the generated source (cheap; runs before compile)."""

    spotjmhbugs: dict = field(default_factory=dict)
    """SpotJMHBugs (Costa et al., TSE 2019) bytecode analysis. Authoritative
    when present; falls back to the regex pre-screen above. Empty dict when
    the vendor tree wasn't installed via tools/install_spotjmhbugs.sh."""

    executes: bool = False
    execution_error: str | None = None
    base_run: dict | None = None

    regression_results: dict[str, dict] = field(default_factory=dict)
    """Per-regression: {detected: bool, p_value, effect_size, mode, ...}"""

    fpr_results: dict[str, dict] = field(default_factory=dict)
    """Per-replicate (FPR mode only): {detected: bool, p_value, ...}.
    Any ``detected: true`` here counts as a false positive."""

    stability_rsd_percent: float | None = None
    """Robust relative standard deviation of the base run (see
    :func:`jmhbench.stats.robust_rsd`)."""
    duration_seconds: float = 0.0

    benchmark_source: str | None = None

    raw_output: RawModelOutput | None = field(default=None, repr=False)
    raw_output_file: str | None = None
    """Relative path (under the run dir) to the persisted model-output Markdown."""

    extra_sources: dict[str, str] = field(default_factory=dict, repr=False)
    """Additional generated Java sources (name -> source). Carried from the
    generation phase to the benchmarking phase; not serialised into reports."""

    def to_dict(self) -> dict:
        data = asdict(self)
        data.pop("raw_output", None)
        data.pop("extra_sources", None)
        return data


def _summarise_jmh_run(run: JmhRun) -> dict:
    return {
        "ok": run.success,
        "error": run.error,
        "benchmarks": [
            {
                "name": r.benchmark,
                "mode": r.mode,
                "score": r.score,
                "score_error": r.score_error,
                "unit": r.unit,
                "n_samples": len(r.samples),
            }
            for r in run.results
        ],
    }


def run_task(task: Task, harness: Harness, config: RunConfig, workdir: Path) -> TaskResult:
    """Generate *and* benchmark a task in one pass (combined ``run``/``fpr`` mode).

    Equivalent to :func:`generate_task` followed by :func:`bench_task`. The two
    phases are also exposed separately so generation (LLM calls; no JVM) can be
    decoupled from benchmarking (compile + JMH; quiet machine) — see the
    ``generate`` / ``bench`` CLI commands.
    """
    result = generate_task(task, harness, workdir)
    if not result.generated:
        return result
    return bench_task(task, result, config, workdir)


def generate_task(task: Task, harness: Harness, workdir: Path) -> TaskResult:
    """Phase 1: invoke the harness to produce a JMH benchmark source.

    Performs *no* compilation or JMH execution, so it needs neither a JDK nor
    Maven — only whatever the harness itself requires (e.g. an API key). The
    returned :class:`TaskResult` carries the generated source (plus raw model
    output and any extra sources) for the benchmarking phase to consume.
    """
    started = time.time()
    workdir.mkdir(parents=True, exist_ok=True)
    result = TaskResult(instance_id=task.instance_id, harness=harness.name)

    log.info("[%s] generating benchmark via harness=%s", task.instance_id, harness.name)
    try:
        output: HarnessOutput = harness.generate(task, workdir / "harness")
        # Normalise the package up front so the SUT layout (package name +
        # whether the SUT is imported) never decides a harness's score. The
        # untouched model text is still preserved verbatim in `raw_output`.
        output.benchmark_source = normalize_benchmark_package(output.benchmark_source)
        result.generated = True
        result.generation_metadata = dict(output.metadata)
        result.benchmark_source = output.benchmark_source
        result.raw_output = output.raw_output
        result.extra_sources = dict(output.extra_sources)
    except LlmSkipped as exc:
        result.generation_error = f"SKIP: {exc.reason[:500]}"
        result.generation_metadata = {"skipped": True}
    except Exception as exc:
        result.generation_error = f"{type(exc).__name__}: {exc}"
    elapsed = time.time() - started
    result.generation_metadata["generation_seconds"] = round(elapsed, 3)
    result.duration_seconds += elapsed
    return result


GenerationProgress = Callable[[int, int, Task, TaskResult], None]


def generate_tasks(
    tasks: list[Task],
    harness: Harness,
    scratch: Path,
    *,
    parallel: int = 1,
    on_progress: GenerationProgress | None = None,
) -> tuple[list[TaskResult], bool]:
    """Generate benchmarks for *tasks*, optionally in parallel.

    Returns ``(results, interrupted)``. *results* follow *tasks* order; tasks
    that were not started or did not finish before interruption are omitted.
    """
    if not tasks:
        return [], False

    workers = max(1, parallel)
    if workers == 1:
        return _generate_tasks_sequential(tasks, harness, scratch, on_progress=on_progress)

    results_by_id: dict[str, TaskResult] = {}
    interrupted = False
    completed = 0
    lock = threading.Lock()

    def _one(task: Task) -> TaskResult:
        try:
            return generate_task(task, harness, scratch / task.instance_id)
        except Exception as exc:
            log.exception("[%s] unhandled generation error", task.instance_id)
            result = TaskResult(instance_id=task.instance_id, harness=harness.name)
            result.generation_error = f"{type(exc).__name__}: {exc}"
            return result

    executor = ThreadPoolExecutor(max_workers=workers)
    futures = {executor.submit(_one, task): task for task in tasks}
    try:
        for future in as_completed(futures):
            task = futures[future]
            try:
                result = future.result()
            except Exception as exc:
                log.exception("[%s] future failed", task.instance_id)
                result = TaskResult(instance_id=task.instance_id, harness=harness.name)
                result.generation_error = f"{type(exc).__name__}: {exc}"
            with lock:
                completed += 1
                results_by_id[task.instance_id] = result
                if on_progress is not None:
                    on_progress(completed, len(tasks), task, result)
    except KeyboardInterrupt:
        interrupted = True
        for future in futures:
            future.cancel()
        executor.shutdown(wait=False, cancel_futures=True)
    else:
        executor.shutdown(wait=True)

    ordered = [results_by_id[t.instance_id] for t in tasks if t.instance_id in results_by_id]
    return ordered, interrupted


def _generate_tasks_sequential(
    tasks: list[Task],
    harness: Harness,
    scratch: Path,
    *,
    on_progress: GenerationProgress | None = None,
) -> tuple[list[TaskResult], bool]:
    results: list[TaskResult] = []
    interrupted = False
    try:
        for i, task in enumerate(tasks, 1):
            try:
                result = generate_task(task, harness, scratch / task.instance_id)
            except Exception as exc:
                log.exception("[%s] unhandled generation error", task.instance_id)
                result = TaskResult(instance_id=task.instance_id, harness=harness.name)
                result.generation_error = f"{type(exc).__name__}: {exc}"
            results.append(result)
            if on_progress is not None:
                on_progress(i, len(tasks), task, result)
    except KeyboardInterrupt:
        interrupted = True
    return results, interrupted


def bench_task(task: Task, result: TaskResult, config: RunConfig, workdir: Path) -> TaskResult:
    """Phase 2: compile, static-check and benchmark an already-generated task.

    Consumes a :class:`TaskResult` produced by :func:`generate_task` (the
    benchmark source must already be present and package-normalised). Needs a
    JDK 17+ and Maven, but no harness/API access. Mutates and returns *result*.
    """
    started = time.time()
    if not result.generated or not result.benchmark_source:
        # Nothing was generated (error/skip) — there is nothing to benchmark.
        return result

    workdir.mkdir(parents=True, exist_ok=True)
    output = HarnessOutput(
        benchmark_source=result.benchmark_source,
        extra_sources=dict(result.extra_sources or {}),
    )

    # ----- 1. Materialise project ------------------------------------
    log.info("[%s] materialising project", task.instance_id)
    project_dir = materialise_project(task, workdir)

    # ----- 3. Install + static check ----------------------------------
    try:
        install_harness_output(project_dir, output)
    except Exception as exc:
        result.compile_error = f"layout: {exc}"
        result.duration_seconds += time.time() - started
        return result

    sc: StaticCheckResult = static_check(output.benchmark_source)
    result.static_check = sc.to_dict()

    # ----- 4. Compile + package --------------------------------------
    log.info("[%s] compiling", task.instance_id)
    build = mvn_package(project_dir)
    result.compiles = build.success
    if not build.success:
        result.compile_error = build.diagnostics[-2000:]
        result.duration_seconds += time.time() - started
        return result

    # ----- 4b. SpotJMHBugs (authoritative anti-pattern detection) ------
    # Costa et al.'s plugin needs bytecode, so it runs after compile. The
    # regex pre-screen above stays in `result.static_check` for context;
    # SpotJMHBugs feeds the scorecard when available.
    if spotjmhbugs.is_available():
        log.info("[%s] running SpotJMHBugs", task.instance_id)
        sj = spotjmhbugs.run(project_dir)
        result.spotjmhbugs = sj.to_dict()

    if config.skip_jmh:
        result.duration_seconds += time.time() - started
        return result

    # ----- 5. JMH base run -------------------------------------------
    log.info("[%s] running JMH base", task.instance_id)
    base_run = run_jmh(project_dir, config.jmh, tag="base")
    result.executes = base_run.success
    result.base_run = _summarise_jmh_run(base_run)
    if not base_run.success:
        result.execution_error = base_run.error or "jmh-base-failed"
        result.duration_seconds += time.time() - started
        return result

    primary = base_run.results[0]
    result.stability_rsd_percent = robust_rsd(primary.samples)

    # Runtime-gate INVO: a per-invocation fixture is only a Costa et al. bad
    # practice for short-running (<1ms) benchmarks, so drop the flag once we
    # know the measured op is long enough to justify it.
    if result.spotjmhbugs:
        per_op_ns = spotjmhbugs._per_op_nanos(primary.score, primary.unit, primary.mode)
        result.spotjmhbugs = spotjmhbugs.gate_invo_by_runtime(result.spotjmhbugs, per_op_ns)

    # ----- 6a. FPR replicates ----------------------------------------
    #
    # In FPR mode we don't apply any regression patches. Instead we re-run
    # the same shaded JAR ``N`` more times and apply the regression test
    # baseline-vs-replicate. The SUT bytecode is identical, so any
    # ``detected: true`` is a false positive caused by either an
    # antipattern (DCE, constant folding, ...) or insufficient iterations.
    if config.fpr_replicates > 0:
        # Mirror the real detection rule (same min-slowdown + alpha) so the
        # measured FPR reflects the detector's actual operating point: how
        # often pure noise clears the +min_slowdown, p<alpha bar.
        min_slowdown = config.min_slowdown
        for i in range(1, config.fpr_replicates + 1):
            replicate_tag = f"fpr-{i}"
            log.info("[%s] FPR replicate %d/%d", task.instance_id, i, config.fpr_replicates)
            replicate = _rerun_with_classpath(project_dir, config, tag=replicate_tag)
            if not replicate.success:
                result.fpr_results[replicate_tag] = {
                    "detected": False,
                    "error": replicate.error or "jmh-failed",
                }
                continue
            matched = _match_primary(base_run, replicate)
            if matched is None:
                result.fpr_results[replicate_tag] = {
                    "detected": False,
                    "error": "no-matching-benchmark",
                }
                continue
            base_r, rep_r = matched
            test = detect_regression(
                base_r.samples,
                rep_r.samples,
                mode=base_r.mode,
                alpha=config.alpha,
                min_slowdown=min_slowdown,
            )
            result.fpr_results[replicate_tag] = test.to_dict() | {
                "base_score": base_r.score,
                "replicate_score": rep_r.score,
                "unit": base_r.unit,
            }
        result.duration_seconds += time.time() - started
        return result

    # ----- 6b. Regression runs ---------------------------------------
    for reg in task.regressions:
        patch_path = task.task_dir / reg.patch
        log.info("[%s] applying regression %s", task.instance_id, reg.id)
        try:
            apply_patch(project_dir, patch_path)
        except Exception as exc:
            result.regression_results[reg.id] = {
                "detected": False,
                "error": f"apply_patch: {exc}",
            }
            continue

        recompile = mvn_compile_only(project_dir)
        if not recompile.success:
            result.regression_results[reg.id] = {
                "detected": False,
                "error": "recompile-failed",
                "diagnostics": recompile.diagnostics[-1000:],
            }
            revert_patch(project_dir, patch_path)
            continue

        # JMH's shaded JAR loads benchmark classes from inside the JAR but
        # the SUT class is on the classpath, so just re-running it picks up
        # the recompiled .class via the JVM classpath. We pass the JAR plus
        # target/classes explicitly.
        regressed = _rerun_with_classpath(project_dir, config, tag=f"reg-{reg.id}")
        revert_patch(project_dir, patch_path)

        if not regressed.success:
            result.regression_results[reg.id] = {
                "detected": False,
                "error": regressed.error or "jmh-failed",
            }
            continue

        matched = _match_primary(base_run, regressed)
        if matched is None:
            result.regression_results[reg.id] = {"detected": False, "error": "no-matching-benchmark"}
            continue
        base_r, reg_r = matched
        test: RegressionTest = detect_regression(
            base_r.samples,
            reg_r.samples,
            mode=base_r.mode,
            alpha=config.alpha,
            min_slowdown=config.min_slowdown,
        )
        result.regression_results[reg.id] = test.to_dict() | {
            "base_score": base_r.score,
            "regressed_score": reg_r.score,
            "unit": base_r.unit,
            "min_slowdown": config.min_slowdown,
            "alpha": config.alpha,
        }

    result.duration_seconds += time.time() - started
    return result


def _rerun_with_classpath(project_dir: Path, config: RunConfig, tag: str) -> JmhRun:
    """Re-run JMH after recompile, prepending target/classes so the patched SUT wins."""
    import subprocess

    jar = project_dir / "target" / "benchmarks.jar"
    classes = project_dir / "target" / "classes"
    json_out = project_dir / f"jmh-results-{tag}.json"
    if json_out.exists():
        json_out.unlink()

    from jmhbench.build import java_executable, resolve_java_home
    from jmhbench.jmh_run import _jmh_duration, _jmh_env, _jmh_failure_hint, _parse_results
    from jmhbench.proc import run_jvm

    if resolve_java_home() is None:
        return JmhRun(success=False, results=[], stdout="", stderr="", error="No JDK 17+ found (set JAVA_HOME).")

    cmd = [
        java_executable(),
        "-cp",
        f"{classes}:{jar}",
        "org.openjdk.jmh.Main",
        "-f", str(config.jmh.forks),
        "-wi", str(config.jmh.warmup_iterations),
        "-w", _jmh_duration(config.jmh.warmup_time_seconds),
        "-i", str(config.jmh.measurement_iterations),
        "-r", _jmh_duration(config.jmh.measurement_time_seconds),
        "-to", _jmh_duration(config.jmh.iteration_timeout_seconds),
        "-rf", "json",
        "-rff", str(json_out),
        "-foe", "true",
    ]
    try:
        proc = run_jvm(cmd, cwd=project_dir, timeout=config.jmh.timeout_seconds, env=_jmh_env())
    except FileNotFoundError:
        return JmhRun(success=False, results=[], stdout="", stderr="", error=f"`java` not found at {cmd[0]!r}; set JAVA_HOME.")
    except subprocess.TimeoutExpired:
        return JmhRun(success=False, results=[], stdout="", stderr="", error="timeout")

    if proc.returncode != 0:
        return JmhRun(success=False, results=[], stdout=proc.stdout, stderr=proc.stderr, error=_jmh_failure_hint(proc.returncode, proc.stdout, proc.stderr))
    results = _parse_results(json_out)
    if not results:
        return JmhRun(success=False, results=[], stdout=proc.stdout, stderr=proc.stderr, error="JMH exited cleanly but produced no parseable results")
    return JmhRun(success=True, results=results, stdout=proc.stdout, stderr=proc.stderr, error=None)


def _match_primary(base: JmhRun, regressed: JmhRun):
    """Find the same benchmark in both runs (by full name); fall back to first."""
    if not base.results or not regressed.results:
        return None
    base_first = base.results[0]
    for r in regressed.results:
        if r.benchmark == base_first.benchmark:
            return base_first, r
    return base_first, regressed.results[0]
