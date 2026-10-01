"""Compile helpers for the project mutation track.

Two use-cases:

* **Generation-time compile check** — compile a single freshly generated
  benchmark against the *pristine* SUT (no mutation patch) and optionally feed
  Maven diagnostics back to the LLM for repair.
* **Bench-time compile-and-filter** — when the merged suite fails to build,
  drop the offending benchmark files iteratively and continue with whatever
  compiles.
* **Bench-time runtime-and-filter** — smoke-run each benchmark class with JMH
  and drop ones that throw during ``@Setup`` / execution before the full run.
"""

from __future__ import annotations

import re
import shutil
import tempfile
from dataclasses import dataclass, field
from pathlib import Path

from jmhbench.build import BuildResult, mvn_compile_only, mvn_package
from jmhbench.config import JmhSettings
from jmhbench.harness import HarnessOutput, ProjectTask
from jmhbench.project import install_harness_output

# javac errors cite ``bench/generated/cNNN/FooBenchmark.java``
_BENCH_FILE_RE = re.compile(
    r"bench/generated/(c\d+/[A-Za-z0-9_]+Benchmark\.java)"
)
# JMH stack traces cite ``bench.generated.cNNN.FooBenchmark``
_BENCH_JMH_RE = re.compile(
    r"bench\.generated\.(c\d+)\.([A-Za-z0-9_]+Benchmark)"
)
# JMH annotation-processor failures often cite ``.../cNNN/jmh_generated/...``
_BENCH_PKG_RE = re.compile(r"bench/generated/(c\d+)/")

_DIAG_TAIL = 3000

# Minimal JMH config for the runtime smoke probe: no warmup, one short
# measurement. Enough to run @Setup + one invocation and surface a throw.
_SMOKE_SETTINGS = JmhSettings(
    forks=1,
    warmup_iterations=0,
    warmup_time_seconds=0.1,
    measurement_iterations=1,
    measurement_time_seconds=0.2,
    timeout_seconds=60,
    iteration_timeout_seconds=30,
)


@dataclass
class FilterResult:
    """Outcome of compile-and-filter on a generated suite."""

    output: HarnessOutput
    dropped: list[dict] = field(default_factory=list)
    """Per dropped file: rel path, reason snippet, package index when known."""

    rounds: int = 0


def tail_diagnostics(diagnostics: str, limit: int = _DIAG_TAIL) -> str:
    text = (diagnostics or "").strip()
    return text[-limit:] if len(text) > limit else text


def parse_failing_benchmark_refs(diagnostics: str) -> set[str]:
    """Return ``cNNN/ClassBenchmark.java`` paths (or bare ``cNNN``) from Maven output."""
    refs: set[str] = set()
    for m in _BENCH_FILE_RE.finditer(diagnostics):
        refs.add(m.group(1))
    for m in _BENCH_PKG_RE.finditer(diagnostics):
        refs.add(m.group(1))  # package index only — drop whole class
    return refs


def _rel_key(source: str, rel_for) -> str:
    return rel_for(source)


def _keys_for_ref(
    ref: str,
    primary_key: str,
    extra_sources: dict[str, str],
) -> list[str]:
    """Map a compiler ref (``c042/...`` or ``c042``) to suite dict keys."""
    hits: list[str] = []
    if ref.endswith(".java"):
        for key in (primary_key, *extra_sources):
            if key.endswith(ref) or ref in key:
                hits.append(key)
    else:
        needle = f"/{ref}/"
        for key in (primary_key, *extra_sources):
            if needle in key.replace("\\", "/"):
                hits.append(key)
    return hits


def _drop_keys(
    output: HarnessOutput,
    keys: set[str],
    rel_for,
    reason: str,
) -> tuple[HarnessOutput, list[dict]]:
    """Remove *keys* from the suite (primary or extra) and promote a new primary if needed."""
    dropped: list[dict] = []
    primary_key = _rel_key(output.benchmark_source, rel_for)
    extra = dict(output.extra_sources)
    primary = output.benchmark_source
    reason_tail = tail_diagnostics(reason, 500)

    for key in keys:
        pkg_m = re.search(r"/(c\d+)/", key.replace("\\", "/"))
        dropped.append({
            "file": key,
            "package": pkg_m.group(1) if pkg_m else None,
            "reason": reason_tail,
        })
        if key == primary_key:
            primary = ""
        elif key in extra:
            del extra[key]

    if not primary and extra:
        first_key = sorted(extra.keys())[0]
        primary = extra.pop(first_key)

    return (
        HarnessOutput(
            benchmark_source=primary,
            extra_sources=extra,
            metadata=dict(output.metadata),
            raw_output=output.raw_output,
        ),
        dropped,
    )


def materialise_pristine_project(task: ProjectTask, workdir: Path) -> Path:
    """Copy the SUT and write the JMH pom, **without** applying ``mutations.patch``."""
    from jmhbench.project_bench import _render_dependencies, _TEMPLATE  # noqa: PLC0415

    project_dir = workdir / "project"
    if project_dir.exists():
        shutil.rmtree(project_dir)
    project_dir.mkdir(parents=True)
    src_main = project_dir / "src" / "main" / "java"
    src_main.parent.mkdir(parents=True, exist_ok=True)
    shutil.copytree(task.src_root, src_main)
    artifact = re.sub(r"[^a-zA-Z0-9_-]", "-", task.instance_id)
    pom = _TEMPLATE.read_text()
    pom = pom.replace("__ARTIFACT__", artifact)
    pom = pom.replace("__DEPENDENCIES__", _render_dependencies(task.dependencies))
    (project_dir / "pom.xml").write_text(pom)
    return project_dir


def compile_benchmark_output(
    task: ProjectTask,
    output: HarnessOutput,
    *,
    workdir: Path | None = None,
    apply_patch: bool = False,
    package_only: bool = False,
    timeout: int = 180,
) -> BuildResult:
    """Try to compile *output* against the project SUT.

    *apply_patch*: when ``True`` (bench-time), apply ``mutations.patch`` first.
    *package_only*: when ``True``, run ``mvn package`` (shaded jar); otherwise
    ``mvn compile`` (faster — used during generation compile-check).
    """
    if not output.benchmark_source:
        return BuildResult(success=False, stdout="", stderr="empty benchmark source", returncode=1)

    use_temp = workdir is None
    root = Path(tempfile.mkdtemp(prefix="jmhbench-pcompile-")) if use_temp else workdir
    root.mkdir(parents=True, exist_ok=True)
    try:
        if apply_patch:
            from jmhbench.project_bench import materialise_project  # noqa: PLC0415

            project_dir = materialise_project(task, root)
        else:
            project_dir = materialise_pristine_project(task, root)
        install_harness_output(project_dir, output)
        if package_only:
            return mvn_package(project_dir, timeout=timeout)
        return mvn_compile_only(project_dir, timeout=timeout)
    finally:
        if use_temp and root.exists():
            shutil.rmtree(root, ignore_errors=True)


def compile_single_benchmark(
    task: ProjectTask,
    source: str,
    workdir: Path,
    *,
    timeout: int = 120,
) -> BuildResult:
    """Compile one benchmark class against the pristine SUT (generation check)."""
    return compile_benchmark_output(
        task,
        HarnessOutput(benchmark_source=source),
        workdir=workdir,
        apply_patch=False,
        package_only=False,
        timeout=timeout,
    )


def filter_compiling_suite(
    task: ProjectTask,
    output: HarnessOutput,
    workdir: Path,
    *,
    rel_for,
    package_only: bool = True,
    timeout: int = 600,
    max_rounds: int = 30,
) -> FilterResult:
    """Drop benchmark files until the suite compiles, or nothing is left."""
    dropped_all: list[dict] = []
    current = HarnessOutput(
        benchmark_source=output.benchmark_source,
        extra_sources=dict(output.extra_sources),
        metadata=dict(output.metadata),
        raw_output=output.raw_output,
    )
    compile_root = workdir / "compile-filter"
    compile_root.mkdir(parents=True, exist_ok=True)

    for round_i in range(1, max_rounds + 1):
        if not current.benchmark_source:
            return FilterResult(output=current, dropped=dropped_all, rounds=round_i - 1)

        build = compile_benchmark_output(
            task,
            current,
            workdir=compile_root / f"round-{round_i}",
            apply_patch=True,
            package_only=package_only,
            timeout=timeout,
        )
        if build.success:
            return FilterResult(output=current, dropped=dropped_all, rounds=round_i - 1)

        refs = parse_failing_benchmark_refs(build.diagnostics)
        if not refs:
            # Non-benchmark failure (pom, SUT, …) — cannot filter our way out.
            return FilterResult(output=current, dropped=dropped_all, rounds=round_i)

        primary_key = _rel_key(current.benchmark_source, rel_for)
        keys: set[str] = set()
        for ref in refs:
            keys.update(_keys_for_ref(ref, primary_key, current.extra_sources))

        if not keys:
            return FilterResult(output=current, dropped=dropped_all, rounds=round_i)

        reason = build.diagnostics
        current, dropped = _drop_keys(current, keys, rel_for, reason)
        dropped_all.extend(dropped)

    return FilterResult(output=current, dropped=dropped_all, rounds=max_rounds)


def parse_failing_jmh_refs(diagnostics: str) -> set[str]:
    """Return ``cNNN/ClassBenchmark.java`` paths from JMH stderr/stdout."""
    refs: set[str] = set()
    for m in _BENCH_JMH_RE.finditer(diagnostics):
        refs.add(f"{m.group(1)}/{m.group(2)}.java")
    return refs


def _suite_package_indices(output: HarnessOutput, rel_for) -> list[str]:
    indices: set[str] = set()
    primary_key = _rel_key(output.benchmark_source, rel_for)
    for key in (primary_key, *output.extra_sources):
        m = re.search(r"/(c\d+)/", key.replace("\\", "/"))
        if m:
            indices.add(m.group(1))
    return sorted(indices)


def filter_runnable_suite(
    task: ProjectTask,
    output: HarnessOutput,
    workdir: Path,
    *,
    rel_for,
    smoke_settings: JmhSettings | None = None,
    timeout: int = 600,
) -> FilterResult:
    """Drop benchmark classes that fail a per-class JMH smoke probe.

    Each class is run alone with a *minimal* JMH config (no warmup, a single
    short measurement) just to trigger ``@Setup`` and one invocation. Classes
    that throw before producing a result are dropped; the surviving suite is
    returned so the real baseline run does not abort on the first bad class.
    """
    from jmhbench.jmh_run import run_jmh  # noqa: PLC0415
    from jmhbench.project_bench import materialise_project  # noqa: PLC0415

    smoke = smoke_settings or _SMOKE_SETTINGS
    current = HarnessOutput(
        benchmark_source=output.benchmark_source,
        extra_sources=dict(output.extra_sources),
        metadata=dict(output.metadata),
        raw_output=output.raw_output,
    )
    if not current.benchmark_source:
        return FilterResult(output=current)

    probe_root = workdir / "probe"
    project_dir = materialise_project(task, probe_root)
    install_harness_output(project_dir, current)
    build = mvn_package(project_dir, timeout=timeout)
    if not build.success:
        return FilterResult(output=current)

    primary_key = _rel_key(current.benchmark_source, rel_for)
    failing_keys: set[str] = set()
    reasons: dict[str, str] = {}

    for pkg in _suite_package_indices(current, rel_for):
        include = f"bench\\.generated\\.{pkg}\\."
        run = run_jmh(project_dir, smoke, tag=f"probe-{pkg}", include=include)
        if run.success:
            continue
        diag = f"{run.stdout}\n{run.stderr}"
        if "no matching benchmarks" in diag.lower():
            continue
        refs = parse_failing_jmh_refs(diag)
        keys: set[str] = set()
        if refs:
            for ref in refs:
                keys.update(_keys_for_ref(ref, primary_key, current.extra_sources))
        else:
            keys.update(_keys_for_ref(pkg, primary_key, current.extra_sources))
        reason = run.error or tail_diagnostics(diag, 500)
        for key in keys:
            failing_keys.add(key)
            reasons[key] = reason

    dropped_all: list[dict] = []
    if failing_keys:
        combined = "\n---\n".join(dict.fromkeys(reasons.values()))[:2000]
        current, dropped = _drop_keys(current, failing_keys, rel_for, combined)
        dropped_all.extend(dropped)

    return FilterResult(output=current, dropped=dropped_all)
