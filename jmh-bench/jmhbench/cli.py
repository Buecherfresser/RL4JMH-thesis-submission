"""Command-line entry point for JMH-Bench."""

from __future__ import annotations

import json
import logging
import sys
import tempfile
import threading
from pathlib import Path

import click
from dotenv import find_dotenv, load_dotenv
from rich.console import Console
from rich.table import Table

from jmhbench.adapters._llm_common import default_llm4jmh_prompt_path
from jmhbench.bundle import load_generation_bundle, write_generation_bundle
from jmhbench.build import (
    DEFAULT_COMPILER_HEAP,
    compiler_heap as effective_compiler_heap,
    set_compiler_heap,
)
from jmhbench.config import JmhSettings, RunConfig
from jmhbench.dataset import default_dataset_dir, discover_tasks, filter_tasks
from jmhbench.harness import discover_harnesses, load_harness
from jmhbench.proc import reap_orphaned_jmh_forks
from jmhbench.report import (
    build_run_dir_name,
    resolve_run_label,
    write_run_summary,
    write_task_result,
)
from jmhbench.runner import bench_task, generate_task, generate_tasks, run_task

console = Console()


def _load_env_files() -> list[str]:
    """Load `.env` files into the process environment.

    Searches from CWD up to the filesystem root (``find_dotenv``) so the
    user gets the same behaviour as most Python tooling, and also loads a
    sibling ``.env`` next to this module's repo root as a fallback when
    invoked from outside the repo.

    Returns the list of paths that were actually loaded, in the order
    applied. Existing environment variables always win.
    """
    loaded: list[str] = []
    discovered = find_dotenv(usecwd=True)
    if discovered and load_dotenv(discovered, override=False):
        loaded.append(discovered)

    repo_env = Path(__file__).resolve().parent.parent / ".env"
    if repo_env.exists() and str(repo_env) not in loaded:
        if load_dotenv(repo_env, override=False):
            loaded.append(str(repo_env))
    return loaded


_LOADED_ENV_FILES = _load_env_files()


def _parse_kv(items: tuple[str, ...]) -> dict[str, str]:
    out: dict[str, str] = {}
    for item in items:
        if "=" not in item:
            raise click.BadParameter(f"Expected key=value, got: {item!r}")
        k, v = item.split("=", 1)
        out[k.strip()] = v.strip()
    return out


def _apply_prompt_options(
    harness_kwargs: dict[str, str],
    prompt_path: Path | None,
    llm4jmh: bool,
    no_system_prompt: bool,
) -> None:
    if llm4jmh:
        harness_kwargs.setdefault("prompt_template", str(default_llm4jmh_prompt_path()))
        harness_kwargs.setdefault("system_prompt", "none")
    if prompt_path is not None:
        harness_kwargs["prompt_template"] = str(prompt_path)
    if no_system_prompt:
        harness_kwargs["system_prompt"] = "none"


@click.group()
@click.option("--verbose", "-v", is_flag=True, help="Enable info-level logging.")
def main(verbose: bool) -> None:
    """JMH-Bench: evaluate harnesses at generating JMH microbenchmarks."""
    level = logging.INFO if verbose else logging.WARNING
    logging.basicConfig(level=level, format="%(asctime)s %(name)s %(levelname)s %(message)s")
    if verbose and _LOADED_ENV_FILES:
        for path in _LOADED_ENV_FILES:
            logging.getLogger("jmhbench").info("loaded env file: %s", path)


@main.command("list-harnesses")
def list_harnesses() -> None:
    """Show all installed harness adapters."""
    table = Table(title="Installed harnesses")
    table.add_column("Name")
    table.add_column("Module")
    for name, cls in sorted(discover_harnesses().items()):
        module = getattr(cls, "__module__", "?")
        cls_name = getattr(cls, "__qualname__", getattr(cls, "__name__", repr(cls)))
        table.add_row(name, f"{module}.{cls_name}")
    console.print(table)


@main.command("list-tasks")
@click.option("--dataset-dir", type=click.Path(path_type=Path), default=None)
@click.option(
    "--track",
    type=click.Choice(["synthetic", "real", "all"], case_sensitive=False),
    default="synthetic",
    show_default=True,
    help="Dataset track to list.",
)
def list_tasks(dataset_dir: Path | None, track: str) -> None:
    """List all tasks discovered in the dataset directory."""
    if dataset_dir is not None:
        tasks = discover_tasks(dataset_dir)
    else:
        tasks = discover_tasks(track=track)
    if not tasks:
        console.print("[yellow]No tasks discovered.[/]")
        return
    table = Table(title=f"Tasks ({len(tasks)}, track={track})")
    table.add_column("instance_id")
    table.add_column("track")
    table.add_column("target")
    table.add_column("regressions")
    table.add_column("tags")
    for t in tasks:
        table.add_row(
            t.instance_id,
            t.track,
            f"{t.target_class}",
            str(len(t.regressions)),
            ",".join(t.tags),
        )
    console.print(table)


@main.command("run")
@click.option("--harness", "harness_name", default=None, help="Registered harness name.")
@click.option(
    "--predictions",
    "predictions_dir",
    type=click.Path(path_type=Path),
    default=None,
    help="Evaluate pre-generated benchmark files from this directory (no in-process harness call).",
)
@click.option(
    "--name",
    "run_name",
    default=None,
    help="Label for the run when using --predictions (defaults to the directory name).",
)
@click.option("--option", "-o", "options", multiple=True, help="key=value passed to harness constructor.")
@click.option(
    "--prompt",
    "prompt_path",
    type=click.Path(exists=True, path_type=Path),
    default=None,
    help="User prompt template file (Jinja2 or {src_code} placeholders).",
)
@click.option(
    "--llm4jmh",
    is_flag=True,
    help="Use prompts/llm4jmh.txt as the user prompt with no system message.",
)
@click.option(
    "--no-system-prompt",
    is_flag=True,
    help="Omit the system message from the chat request.",
)
@click.option("--tasks", "task_ids", multiple=True, help="Run only these instance IDs.")
@click.option("--tag", "tags", multiple=True, help="Run only tasks matching any of these tags.")
@click.option("--dataset-dir", type=click.Path(path_type=Path), default=None)
@click.option(
    "--track",
    type=click.Choice(["synthetic", "real", "all"], case_sensitive=False),
    default="synthetic",
    show_default=True,
    help="Dataset track: synthetic (hand-built v1), real (OSS-inspired regressions), or all.",
)
@click.option(
    "--preset",
    type=click.Choice(["quick", "default", "strong", "campaign", "llm4jmh"], case_sensitive=False),
    default="default",
    show_default=True,
    help="JMH iteration settings.",
)
@click.option("--quick", is_flag=True, help="Shortcut for --preset quick.")
@click.option("--strong", is_flag=True, help="Shortcut for --preset strong.")
@click.option("--skip-jmh", is_flag=True, help="Compile + static-check only (no JMH execution).")
@click.option("--alpha", default=0.05, show_default=True, help="Significance level (regression flagged only when p < alpha).")
@click.option(
    "--min-slowdown",
    default=1.10,
    show_default=True,
    help="Minimum slowdown ratio (regressed/base) that counts as a regression, e.g. 1.10 = +10%.",
)
@click.option("--max-tasks", type=int, default=None)
@click.option(
    "--out",
    "out_dir",
    type=click.Path(path_type=Path),
    default=None,
    help="Report directory (defaults to reports/<model>_<YYYY-MM-DD_HH-MM-SS>).",
)
@click.option("--workdir", type=click.Path(path_type=Path), default=None, help="Scratch dir (default: temp).")
def run_cmd(
    harness_name: str | None,
    predictions_dir: Path | None,
    run_name: str | None,
    options: tuple[str, ...],
    prompt_path: Path | None,
    llm4jmh: bool,
    no_system_prompt: bool,
    task_ids: tuple[str, ...],
    tags: tuple[str, ...],
    dataset_dir: Path | None,
    track: str,
    preset: str,
    quick: bool,
    strong: bool,
    skip_jmh: bool,
    alpha: float,
    min_slowdown: float,
    max_tasks: int | None,
    out_dir: Path | None,
    workdir: Path | None,
) -> None:
    """Run a harness over the dataset and write a report."""
    _execute(
        harness_name=harness_name,
        predictions_dir=predictions_dir,
        run_name=run_name,
        options=options,
        prompt_path=prompt_path,
        llm4jmh=llm4jmh,
        no_system_prompt=no_system_prompt,
        task_ids=task_ids,
        tags=tags,
        dataset_dir=dataset_dir,
        track=track,
        preset=preset,
        quick=quick,
        strong=strong,
        skip_jmh=skip_jmh,
        alpha=alpha,
        min_slowdown=min_slowdown,
        max_tasks=max_tasks,
        out_dir=out_dir,
        workdir=workdir,
        fpr_replicates=0,
        out_dir_prefix=None,
    )


def _execute(
    *,
    harness_name: str | None,
    predictions_dir: Path | None,
    run_name: str | None,
    options: tuple[str, ...],
    prompt_path: Path | None,
    llm4jmh: bool,
    no_system_prompt: bool,
    task_ids: tuple[str, ...],
    tags: tuple[str, ...],
    dataset_dir: Path | None,
    track: str,
    preset: str,
    quick: bool,
    strong: bool,
    skip_jmh: bool,
    alpha: float,
    min_slowdown: float,
    max_tasks: int | None,
    out_dir: Path | None,
    workdir: Path | None,
    fpr_replicates: int,
    out_dir_prefix: str | None,
) -> None:
    if quick:
        preset = "quick"
    if strong:
        preset = "strong"

    if predictions_dir is not None:
        if harness_name and harness_name != "predictions":
            console.print("[red]--predictions cannot be combined with a different --harness.[/]")
            sys.exit(2)
        harness_name = "predictions"

    if harness_name is None:
        console.print("[red]Provide --harness <name> or --predictions <dir>.[/]")
        sys.exit(2)

    harness_kwargs = _parse_kv(options)
    _apply_prompt_options(harness_kwargs, prompt_path, llm4jmh, no_system_prompt)
    if predictions_dir is not None:
        harness_kwargs["predictions_dir"] = str(predictions_dir)
        if run_name:
            harness_kwargs["name"] = run_name
    jmh = JmhSettings.preset(preset)
    config = RunConfig(
        harness_name=harness_name,
        harness_kwargs=harness_kwargs,
        jmh=jmh,
        alpha=alpha,
        min_slowdown=min_slowdown,
        max_tasks=max_tasks,
        skip_jmh=skip_jmh,
        fpr_replicates=fpr_replicates,
    )

    if dataset_dir is not None:
        tasks = discover_tasks(dataset_dir)
        source_desc = str(dataset_dir)
    else:
        tasks = discover_tasks(track=track)
        source_desc = f"track={track}"
    if not tasks:
        console.print(f"[red]No tasks found ({source_desc}).[/]")
        sys.exit(2)
    tasks = filter_tasks(tasks, list(task_ids) or None, list(tags) or None)
    if max_tasks:
        tasks = tasks[:max_tasks]
    if not tasks:
        console.print("[red]No tasks match the given filters.[/]")
        sys.exit(2)

    try:
        harness = load_harness(harness_name, **harness_kwargs)
    except KeyError as exc:
        console.print(f"[red]{exc}[/]")
        sys.exit(2)
    except (RuntimeError, ValueError) as exc:
        console.print(f"[red]Failed to load harness '{harness_name}': {exc}[/]")
        sys.exit(2)

    run_label = resolve_run_label(harness, harness_name, harness_kwargs)
    out_dir = out_dir or Path("reports") / build_run_dir_name(
        run_label, prefix=out_dir_prefix
    )
    out_dir.mkdir(parents=True, exist_ok=True)

    use_temp = workdir is None
    if use_temp:
        scratch = Path(tempfile.mkdtemp(prefix="jmhbench-"))
    else:
        scratch = workdir
        scratch.mkdir(parents=True, exist_ok=True)

    mode_label = (
        f"FPR mode (replicates={fpr_replicates})"
        if fpr_replicates > 0
        else "regression-detection mode"
    )
    console.rule(f"Running {len(tasks)} task(s) with [bold]{harness_name}[/] — {mode_label}")
    console.print(
        f"preset=[cyan]{preset}[/]  detect=[cyan]≥{(min_slowdown - 1) * 100:.0f}% slower & p<{alpha}[/]  "
        f"workdir=[dim]{scratch}[/]  out=[dim]{out_dir}[/]"
    )

    config_summary = {
        "harness": harness_name,
        "harness_kwargs": harness_kwargs,
        "preset": preset,
        "alpha": alpha,
        "min_slowdown": min_slowdown,
        "jmh": jmh.__dict__,
        "dataset_dir": str(dataset_dir) if dataset_dir is not None else None,
        "track": None if dataset_dir is not None else track,
        "fpr_replicates": fpr_replicates,
        "planned_tasks": len(tasks),
    }

    per_task_dir = out_dir / "per_task"
    per_task_dir.mkdir(parents=True, exist_ok=True)

    results = []
    interrupted = False
    try:
        for i, task in enumerate(tasks, 1):
            console.print(f"[{i}/{len(tasks)}] [bold]{task.instance_id}[/]")
            task_workdir = scratch / task.instance_id
            try:
                r = run_task(task, harness, config, task_workdir)
            except Exception as exc:
                console.print(f"  [red]unhandled error: {type(exc).__name__}: {exc}[/]")
                continue
            results.append(r)
            _emit_task_summary(r)
            write_task_result(per_task_dir, r)
            write_run_summary(out_dir, harness_name, results, config_summary)
    except KeyboardInterrupt:
        interrupted = True
        console.print("\n[yellow]Interrupted — saving partial results…[/]")

    if not results:
        console.print("[yellow]No task results to report.[/]")
        sys.exit(130 if interrupted else 0)

    card = write_run_summary(
        out_dir,
        harness_name,
        results,
        config_summary,
        interrupted=interrupted,
    )

    console.rule("Scorecard" + (" (partial)" if interrupted else ""))
    console.print(json.dumps(card.to_dict()["rates"], indent=2))
    console.print(f"Report written to [green]{out_dir}[/]")
    if interrupted:
        sys.exit(130)


@main.command("fpr")
@click.option("--harness", "harness_name", default=None, help="Registered harness name.")
@click.option(
    "--predictions",
    "predictions_dir",
    type=click.Path(path_type=Path),
    default=None,
    help="Evaluate pre-generated benchmark files from this directory (no in-process harness call).",
)
@click.option(
    "--name",
    "run_name",
    default=None,
    help="Label for the run when using --predictions (defaults to the directory name).",
)
@click.option(
    "--replicates",
    "fpr_replicates",
    type=click.IntRange(min=1),
    default=1,
    show_default=True,
    help="Unchanged reruns per task, each compared to the base with the same "
    "p<alpha & min-slowdown test. Default 1 mirrors the regression run (one "
    "verdict per task); raise it only to estimate a finer per-replicate rate.",
)
@click.option("--option", "-o", "options", multiple=True, help="key=value passed to harness constructor.")
@click.option(
    "--prompt",
    "prompt_path",
    type=click.Path(exists=True, path_type=Path),
    default=None,
    help="User prompt template file (Jinja2 or {src_code} placeholders).",
)
@click.option(
    "--llm4jmh",
    is_flag=True,
    help="Use prompts/llm4jmh.txt as the user prompt with no system message.",
)
@click.option(
    "--no-system-prompt",
    is_flag=True,
    help="Omit the system message from the chat request.",
)
@click.option("--tasks", "task_ids", multiple=True, help="Run only these instance IDs.")
@click.option("--tag", "tags", multiple=True, help="Run only tasks matching any of these tags.")
@click.option("--dataset-dir", type=click.Path(path_type=Path), default=None)
@click.option(
    "--track",
    type=click.Choice(["synthetic", "real", "all"], case_sensitive=False),
    default="synthetic",
    show_default=True,
    help="Dataset track.",
)
@click.option(
    "--preset",
    type=click.Choice(["quick", "default", "strong", "campaign", "llm4jmh"], case_sensitive=False),
    default="default",
    show_default=True,
    help="JMH iteration settings.",
)
@click.option("--quick", is_flag=True, help="Shortcut for --preset quick.")
@click.option("--strong", is_flag=True, help="Shortcut for --preset strong.")
@click.option("--alpha", default=0.05, show_default=True, help="Significance level (regression flagged only when p < alpha).")
@click.option(
    "--min-slowdown",
    default=1.10,
    show_default=True,
    help="Minimum slowdown ratio (regressed/base) that counts as a regression, e.g. 1.10 = +10%.",
)
@click.option("--max-tasks", type=int, default=None)
@click.option(
    "--out",
    "out_dir",
    type=click.Path(path_type=Path),
    default=None,
    help="Report directory (defaults to reports/fpr_<model>_<YYYY-MM-DD_HH-MM-SS>).",
)
@click.option("--workdir", type=click.Path(path_type=Path), default=None, help="Scratch dir (default: temp).")
def fpr_cmd(
    harness_name: str | None,
    predictions_dir: Path | None,
    run_name: str | None,
    fpr_replicates: int,
    options: tuple[str, ...],
    prompt_path: Path | None,
    llm4jmh: bool,
    no_system_prompt: bool,
    task_ids: tuple[str, ...],
    tags: tuple[str, ...],
    dataset_dir: Path | None,
    track: str,
    preset: str,
    quick: bool,
    strong: bool,
    alpha: float,
    min_slowdown: float,
    max_tasks: int | None,
    out_dir: Path | None,
    workdir: Path | None,
) -> None:
    """Measure the false-positive rate of a harness.

    Generates a benchmark per task as usual, then runs it ``--replicates``
    additional times against the *unmodified* SUT. Any time the
    regression-detection test fires it counts as a false positive: the
    SUT bytecode is identical between runs, so any positive is noise
    caused by an antipattern (DCE, constant folding, ...) or by too few
    iterations.

    No regression patches are applied in this mode.
    """
    _execute(
        harness_name=harness_name,
        predictions_dir=predictions_dir,
        run_name=run_name,
        options=options,
        prompt_path=prompt_path,
        llm4jmh=llm4jmh,
        no_system_prompt=no_system_prompt,
        task_ids=task_ids,
        tags=tags,
        dataset_dir=dataset_dir,
        track=track,
        preset=preset,
        quick=quick,
        strong=strong,
        skip_jmh=False,
        alpha=alpha,
        min_slowdown=min_slowdown,
        max_tasks=max_tasks,
        out_dir=out_dir,
        workdir=workdir,
        fpr_replicates=fpr_replicates,
        out_dir_prefix="fpr",
    )


@main.command("generate")
@click.option("--harness", "harness_name", required=True, help="Registered harness name.")
@click.option("--option", "-o", "options", multiple=True, help="key=value passed to harness constructor.")
@click.option(
    "--prompt",
    "prompt_path",
    type=click.Path(exists=True, path_type=Path),
    default=None,
    help="User prompt template file (Jinja2 or {src_code} placeholders).",
)
@click.option("--llm4jmh", is_flag=True, help="Use prompts/llm4jmh.txt as the user prompt with no system message.")
@click.option("--no-system-prompt", is_flag=True, help="Omit the system message from the chat request.")
@click.option("--tasks", "task_ids", multiple=True, help="Generate only for these instance IDs.")
@click.option("--tag", "tags", multiple=True, help="Generate only for tasks matching any of these tags.")
@click.option("--dataset-dir", type=click.Path(path_type=Path), default=None)
@click.option(
    "--track",
    type=click.Choice(["synthetic", "real", "all"], case_sensitive=False),
    default="synthetic",
    show_default=True,
    help="Dataset track: synthetic (hand-built v1), real (OSS-inspired regressions), or all.",
)
@click.option("--max-tasks", type=int, default=None)
@click.option(
    "--out",
    "out_dir",
    type=click.Path(path_type=Path),
    default=None,
    help="Bundle directory (defaults to reports/gen_<model>_<YYYY-MM-DD_HH-MM-SS>).",
)
@click.option("--workdir", type=click.Path(path_type=Path), default=None, help="Scratch dir (default: temp).")
@click.option(
    "--parallel",
    type=click.IntRange(min=1),
    default=5,
    show_default=True,
    help="Maximum concurrent harness/LLM calls during generation.",
)
def generate_cmd(
    harness_name: str,
    options: tuple[str, ...],
    prompt_path: Path | None,
    llm4jmh: bool,
    no_system_prompt: bool,
    task_ids: tuple[str, ...],
    tags: tuple[str, ...],
    dataset_dir: Path | None,
    track: str,
    max_tasks: int | None,
    out_dir: Path | None,
    workdir: Path | None,
    parallel: int,
) -> None:
    """Phase 1: generate benchmarks and write a portable bundle (no JMH).

    Only the harness runs — no JDK or Maven is needed. The resulting bundle is
    self-contained (it embeds the task definitions it was generated against), so
    it can be copied to a quiet benchmarking machine and fed to ``jmhbench bench``.
    """
    harness_kwargs = _parse_kv(options)
    _apply_prompt_options(harness_kwargs, prompt_path, llm4jmh, no_system_prompt)

    if dataset_dir is not None:
        tasks = discover_tasks(dataset_dir)
        source_desc = str(dataset_dir)
    else:
        tasks = discover_tasks(track=track)
        source_desc = f"track={track}"
    if not tasks:
        console.print(f"[red]No tasks found ({source_desc}).[/]")
        sys.exit(2)
    tasks = filter_tasks(tasks, list(task_ids) or None, list(tags) or None)
    if max_tasks:
        tasks = tasks[:max_tasks]
    if not tasks:
        console.print("[red]No tasks match the given filters.[/]")
        sys.exit(2)

    try:
        harness = load_harness(harness_name, **harness_kwargs)
    except KeyError as exc:
        console.print(f"[red]{exc}[/]")
        sys.exit(2)
    except (RuntimeError, ValueError) as exc:
        console.print(f"[red]Failed to load harness '{harness_name}': {exc}[/]")
        sys.exit(2)

    run_label = resolve_run_label(harness, harness_name, harness_kwargs)
    out_dir = out_dir or Path("reports") / build_run_dir_name(run_label, prefix="gen")
    out_dir.mkdir(parents=True, exist_ok=True)

    use_temp = workdir is None
    scratch = Path(tempfile.mkdtemp(prefix="jmhbench-gen-")) if use_temp else workdir
    scratch.mkdir(parents=True, exist_ok=True)

    console.rule(f"Generating {len(tasks)} benchmark(s) with [bold]{harness_name}[/]")
    workers_label = "sequential" if parallel == 1 else f"up to {parallel} parallel"
    console.print(
        f"workdir=[dim]{scratch}[/]  bundle=[dim]{out_dir}[/]  "
        f"workers=[cyan]{workers_label}[/]"
    )

    config_summary = {
        "harness": harness_name,
        "harness_kwargs": harness_kwargs,
        "dataset_dir": str(dataset_dir) if dataset_dir is not None else None,
        "track": None if dataset_dir is not None else track,
        "planned_tasks": len(tasks),
        "parallel": parallel,
    }

    progress_lock = threading.Lock()

    def _on_progress(completed: int, total: int, task, result) -> None:
        with progress_lock:
            console.print(f"[{completed}/{total}] [bold]{task.instance_id}[/]")
            _emit_generation_summary(result)

    results, interrupted = generate_tasks(
        tasks,
        harness,
        scratch,
        parallel=parallel,
        on_progress=_on_progress,
    )
    if interrupted:
        console.print("\n[yellow]Interrupted — writing partial bundle…[/]")

    if not results:
        console.print("[yellow]Nothing generated; no bundle written.[/]")
        sys.exit(130 if interrupted else 0)

    write_generation_bundle(
        out_dir,
        harness_name=harness_name,
        run_label=run_label,
        config_summary=config_summary,
        results=results,
        tasks=tasks,
    )
    n_ok = sum(1 for r in results if r.generated)
    console.rule("Generation complete" + (" (partial)" if interrupted else ""))
    console.print(f"Generated [green]{n_ok}[/]/{len(results)} benchmark(s).")
    console.print(f"Bundle written to [green]{out_dir}[/]")
    console.print(f"Next: [cyan]jmhbench bench {out_dir}[/]")
    if interrupted:
        sys.exit(130)


@main.command("bench")
@click.argument("bundle_dir", type=click.Path(exists=True, path_type=Path))
@click.option(
    "--preset",
    type=click.Choice(["quick", "default", "strong", "campaign", "llm4jmh"], case_sensitive=False),
    default="default",
    show_default=True,
    help="JMH iteration settings.",
)
@click.option("--quick", is_flag=True, help="Shortcut for --preset quick.")
@click.option("--strong", is_flag=True, help="Shortcut for --preset strong.")
@click.option("--skip-jmh", is_flag=True, help="Compile + static-check only (no JMH execution).")
@click.option("--alpha", default=0.05, show_default=True, help="Significance level (regression flagged only when p < alpha).")
@click.option(
    "--min-slowdown",
    default=1.10,
    show_default=True,
    help="Minimum slowdown ratio (regressed/base) that counts as a regression, e.g. 1.10 = +10%.",
)
@click.option(
    "--replicates",
    "fpr_replicates",
    type=click.IntRange(min=0),
    default=0,
    show_default=True,
    help="If > 0, run in false-positive-rate mode: this many unchanged reruns "
    "per task instead of applying regression patches.",
)
@click.option("--tasks", "task_ids", multiple=True, help="Benchmark only these instance IDs from the bundle.")
@click.option("--tag", "tags", multiple=True, help="Benchmark only bundle tasks matching any of these tags.")
@click.option("--max-tasks", type=int, default=None)
@click.option(
    "--out",
    "out_dir",
    type=click.Path(path_type=Path),
    default=None,
    help="Report directory (defaults to reports/[fpr_]<model>_<YYYY-MM-DD_HH-MM-SS>).",
)
@click.option("--workdir", type=click.Path(path_type=Path), default=None, help="Scratch dir (default: temp).")
def bench_cmd(
    bundle_dir: Path,
    preset: str,
    quick: bool,
    strong: bool,
    skip_jmh: bool,
    alpha: float,
    min_slowdown: float,
    fpr_replicates: int,
    task_ids: tuple[str, ...],
    tags: tuple[str, ...],
    max_tasks: int | None,
    out_dir: Path | None,
    workdir: Path | None,
) -> None:
    """Phase 2: benchmark a generation bundle and write the scorecard.

    Compiles, static-checks and runs JMH for every benchmark in BUNDLE_DIR (the
    output of ``jmhbench generate``). Needs a JDK 17+ and Maven but no harness or
    API access, so it can run unattended on a dedicated, quiet machine.
    """
    if quick:
        preset = "quick"
    if strong:
        preset = "strong"

    try:
        bundle = load_generation_bundle(bundle_dir)
    except (FileNotFoundError, ValueError) as exc:
        console.print(f"[red]{exc}[/]")
        sys.exit(2)

    tasks = filter_tasks(bundle.tasks, list(task_ids) or None, list(tags) or None)
    if max_tasks:
        tasks = tasks[:max_tasks]
    if not tasks:
        console.print("[red]No bundle tasks match the given filters.[/]")
        sys.exit(2)

    jmh = JmhSettings.preset(preset)
    config = RunConfig(
        harness_name=bundle.harness_name,
        harness_kwargs={},
        jmh=jmh,
        alpha=alpha,
        min_slowdown=min_slowdown,
        max_tasks=max_tasks,
        skip_jmh=skip_jmh,
        fpr_replicates=fpr_replicates,
    )

    prefix = "fpr" if fpr_replicates > 0 else None
    out_dir = out_dir or Path("reports") / build_run_dir_name(bundle.run_label, prefix=prefix)
    out_dir.mkdir(parents=True, exist_ok=True)

    use_temp = workdir is None
    scratch = Path(tempfile.mkdtemp(prefix="jmhbench-bench-")) if use_temp else workdir
    scratch.mkdir(parents=True, exist_ok=True)

    mode_label = (
        f"FPR mode (replicates={fpr_replicates})"
        if fpr_replicates > 0
        else "regression-detection mode"
    )
    console.rule(
        f"Benchmarking {len(tasks)} task(s) from [bold]{bundle.run_label}[/] — {mode_label}"
    )
    console.print(
        f"preset=[cyan]{preset}[/]  detect=[cyan]≥{(min_slowdown - 1) * 100:.0f}% slower & p<{alpha}[/]  "
        f"bundle=[dim]{bundle.dir}[/]  out=[dim]{out_dir}[/]"
    )

    config_summary = {
        "harness": bundle.harness_name,
        "from_bundle": str(bundle.dir),
        "generation_config": bundle.manifest.get("config", {}),
        "preset": preset,
        "alpha": alpha,
        "min_slowdown": min_slowdown,
        "jmh": jmh.__dict__,
        "fpr_replicates": fpr_replicates,
        "planned_tasks": len(tasks),
    }

    per_task_dir = out_dir / "per_task"
    per_task_dir.mkdir(parents=True, exist_ok=True)

    # Sweep forks orphaned by an earlier crashed/killed run before we pile more
    # JVMs onto the box — otherwise their stale CPU load skews fresh timings.
    swept = reap_orphaned_jmh_forks()
    if swept:
        console.print(f"[yellow]Reaped {swept} orphaned JMH fork(s) left by a previous run.[/]")

    results = []
    interrupted = False
    try:
        for i, task in enumerate(tasks, 1):
            console.print(f"[{i}/{len(tasks)}] [bold]{task.instance_id}[/]")
            r = bundle.task_result(task.instance_id)
            _copy_raw_output(bundle.dir, out_dir, r)
            try:
                r = bench_task(task, r, config, scratch / task.instance_id)
            except Exception as exc:
                console.print(f"  [red]unhandled error: {type(exc).__name__}: {exc}[/]")
                continue
            results.append(r)
            _emit_task_summary(r, bench_only=True)
            write_task_result(per_task_dir, r)
            write_run_summary(out_dir, bundle.harness_name, results, config_summary)
    except KeyboardInterrupt:
        interrupted = True
        console.print("\n[yellow]Interrupted — saving partial results…[/]")
    finally:
        # Belt-and-suspenders: kill anything that still escaped the per-run
        # process-group teardown (double-forked workers, etc.).
        reap_orphaned_jmh_forks()

    if not results:
        console.print("[yellow]No task results to report.[/]")
        sys.exit(130 if interrupted else 0)

    card = write_run_summary(
        out_dir,
        bundle.harness_name,
        results,
        config_summary,
        interrupted=interrupted,
    )

    console.rule("Scorecard" + (" (partial)" if interrupted else ""))
    console.print(json.dumps(card.to_dict()["rates"], indent=2))
    console.print(f"Report written to [green]{out_dir}[/]")
    if interrupted:
        sys.exit(130)


def _copy_raw_output(bundle_dir: Path, out_dir: Path, r) -> None:
    """Carry a bundle's raw model-output Markdown into the bench report so the
    summary's ``[raw]`` link resolves."""
    if not r.raw_output_file:
        return
    src = bundle_dir / r.raw_output_file
    if not src.exists():
        r.raw_output_file = None
        return
    dst = out_dir / r.raw_output_file
    dst.parent.mkdir(parents=True, exist_ok=True)
    import shutil

    shutil.copy2(src, dst)


def _generation_tps_suffix(r) -> str | None:
    meta = r.generation_metadata or {}
    tokens = meta.get("completion_tokens") or meta.get("output_tokens")
    if not tokens:
        return None
    seconds = meta.get("generation_seconds") or r.duration_seconds
    if not seconds:
        return None
    return f"tps={tokens / seconds:.1f}"


def _emit_generation_summary(r) -> None:
    if r.generated:
        status = "OK"
    elif r.generation_error and r.generation_error.startswith("SKIP"):
        status = "SKIP"
    else:
        status = "FAIL"
    parts = [f"gen={status}"]
    tps = _generation_tps_suffix(r)
    if tps:
        parts.append(tps)
    if r.generation_error:
        parts.append(f"err={r.generation_error[:70]}")
    console.print("    " + "  ".join(parts))


def _emit_task_summary(r, *, bench_only: bool = False) -> None:
    ap = (r.static_check or {}).get("total", "-")
    parts: list[str] = []
    if bench_only:
        if not r.generated:
            parts.append("bundled=FAIL")
    else:
        parts.append(f"gen={'OK' if r.generated else 'FAIL'}")
        tps = _generation_tps_suffix(r)
        if tps:
            parts.append(tps)
    parts += [
        f"compile={'OK' if r.compiles else 'FAIL'}",
        f"exec={'OK' if r.executes else '-'}",
        f"ap={ap}",
    ]
    if r.fpr_results:
        fps = sum(1 for v in r.fpr_results.values() if v.get("detected"))
        parts.append(f"fp={fps}/{len(r.fpr_results)}")
    else:
        reg_ok = sum(1 for v in r.regression_results.values() if v.get("detected"))
        parts.append(f"reg={reg_ok}/{len(r.regression_results)}")
    if r.stability_rsd_percent is not None:
        parts.append(f"rsd={r.stability_rsd_percent:.1f}%")
    if r.generation_error:
        parts.append(f"err={r.generation_error[:60]}")
    elif r.compile_error and not r.compiles:
        parts.append("compile-err")
    console.print("    " + "  ".join(parts))


@main.command("list-projects")
def list_projects_cmd() -> None:
    """List vendored projects available for the mutation track."""
    from jmhbench.projects import discover_projects

    projects = discover_projects()
    if not projects:
        console.print("[yellow]No projects found under dataset/projects/.[/]")
        return
    table = Table(title=f"Projects ({len(projects)})")
    table.add_column("name")
    table.add_column("version")
    table.add_column("mutants")
    table.add_column("dependencies")
    for p in projects:
        table.add_row(p.instance_id, p.version, str(p.mutant_count), str(len(p.dependencies)))
    console.print(table)


_PROJECT_SUITE_PROMPT = (
    Path(__file__).resolve().parent.parent / "prompts" / "project_suite.j2"
)
_PROJECT_CLASS_PROMPT = (
    Path(__file__).resolve().parent.parent / "prompts" / "project_class.j2"
)
# Per-class (or digest) suite generation produces a full Java file with many
# @Benchmark methods + @State setup — the generic LLM harness default of 4096
# completion tokens truncates large classes (e.g. ArchiveStreamFactory).
_PROJECT_DEFAULT_MAX_TOKENS = 16384


def _build_project_harness(
    *,
    harness_name: str,
    predictions_dir: Path | None,
    run_name: str | None,
    options: tuple[str, ...],
    prompt_path: Path | None,
    no_system_prompt: bool,
    effective_mode: str,
    task,
):
    """Construct the harness for a project run (shared by project-gen/-bench)."""
    harness_kwargs = _parse_kv(options)
    # Per-class mode shows one real SUT class per call (project_class.j2); digest
    # mode shows the whole API surface in one call (project_suite.j2). dummy /
    # predictions ignore the prompt entirely.
    default_prompt = _PROJECT_CLASS_PROMPT if effective_mode == "per_class" else _PROJECT_SUITE_PROMPT
    harness_kwargs.setdefault("prompt_template", str(prompt_path or default_prompt))
    # Generous completion budget unless overridden via -o max_tokens=… or
    # harness_input.max_tokens in project.yaml.
    default_max_tokens = task.harness_input.get("max_tokens", _PROJECT_DEFAULT_MAX_TOKENS)
    harness_kwargs.setdefault("max_tokens", str(default_max_tokens))
    if no_system_prompt:
        harness_kwargs["system_prompt"] = "none"
    if predictions_dir is not None:
        harness_kwargs["predictions_dir"] = str(predictions_dir)
        if run_name:
            harness_kwargs["name"] = run_name

    try:
        harness = load_harness(harness_name, **harness_kwargs)
    except KeyError as exc:
        console.print(f"[red]{exc}[/]")
        sys.exit(2)
    except (RuntimeError, ValueError) as exc:
        console.print(f"[red]Failed to load harness '{harness_name}': {exc}[/]")
        sys.exit(2)
    return harness, harness_kwargs


def _make_project_gen_progress():
    """A thread-safe ``on_progress`` callback that logs each per-class result."""
    lock = threading.Lock()

    def cb(completed: int, total: int, view, entry: dict) -> None:
        with lock:
            if entry.get("ok"):
                bits = []
                attempts = entry.get("compile_check_attempts")
                if attempts and attempts > 1:
                    bits.append(f"compile-fix×{attempts}")
                ct = entry.get("completion_tokens")
                secs = entry.get("generation_seconds")
                if ct:
                    bits.append(f"{int(ct)} tok")
                if ct and secs:
                    bits.append(f"{ct / secs:.0f} tok/s")
                suffix = ("  [dim]" + " · ".join(bits) + "[/]") if bits else ""
                console.print(
                    f"[{completed}/{total}] [green]gen OK[/]   "
                    f"[bold]{view.simple_name}[/] [dim]{view.component}[/]{suffix}"
                )
            else:
                err = (entry.get("error") or entry.get("compile_error") or "").splitlines()[0][:90]
                console.print(
                    f"[{completed}/{total}] [red]gen FAIL[/] "
                    f"[bold]{view.simple_name}[/] [dim]{view.component}[/]  [red]{err}[/]"
                )

    return cb


def _mutant_op_label(jmh: JmhSettings) -> str:
    """How the armed operator reads in a one-line run header."""
    if jmh.mutant_op in JmhSettings.MUTANT_BURN_OPS:
        return f"{jmh.mutant_op} x{jmh.mutant_spin_tokens}"
    if jmh.mutant_op == "sleep":
        return "sleep (~1.2ms/hit)"
    return jmh.mutant_op


def _project_phase(msg: str) -> None:
    console.print(f"  [cyan]›[/] {msg}")


def _project_cover_progress(done: int, total: int, name: str, n_hit: int) -> None:
    # Coverage runs are the quiet, repetitive part — only surface the last
    # segment of the benchmark name to keep lines short.
    short = name.rsplit(".", 1)[-1]
    console.print(f"    [dim][cover {done}/{total}][/] {short} → {n_hit} mutant(s)")


def _project_detect_progress(done: int, total: int, mres, component: str) -> None:
    eff = mres.effect_size
    pct = f"+{(eff - 1) * 100:.0f}%" if (eff is not None and eff == eff) else None
    if mres.status == "killed":
        detail = pct or "timeout"
        killer = (mres.killed_by or "").rsplit(".", 1)[-1]
        via = f" via {killer}" if killer else ""
        console.print(
            f"  [detect {done}/{total}] [green]KILL[/]     "
            f"mutant {mres.id:>3} [dim]{component}[/] ({detail}{via})"
        )
    else:
        detail = f"max {pct}" if pct else "no signal"
        console.print(
            f"  [detect {done}/{total}] [yellow]survived[/] "
            f"mutant {mres.id:>3} [dim]{component}[/] ({detail})"
        )


def _emit_project_scorecard(result) -> None:
    console.print(
        json.dumps(
            {
                "generated": result.generated,
                "input_mode": result.input_mode,
                "classes": f"{result.classes_succeeded}/{result.classes_total}"
                if result.input_mode == "per_class"
                else None,
                "classes_compiled": result.classes_compiled or None,
                "classes_filtered": result.classes_filtered or None,
                "classes_runtime_filtered": result.classes_runtime_filtered or None,
                "compiles": result.compiles,
                "executes": result.executes,
                "n_benchmarks": result.n_benchmarks,
                "mutation_score": result.mutation_score,
                "mutants_killed": result.mutants_killed,
                "mutants_covered": result.mutants_covered,
                # Mutants whose every covering benchmark errored or blew its
                # budget: missing measurements, not survivors and not kills (B2).
                "mutants_errored": result.mutants_errored or None,
                "mutant_count": result.mutant_count,
                "coverage_rate": result.coverage_rate,
                "baseline_excluded": len(result.baseline_failures) or None,
                "coverage_failures": len(result.coverage_failures) or None,
                "stability_rsd_percent": result.stability_rsd_percent,
                "stability_rsd_source": result.stability_rsd_source or None,
                "compiler_heap": result.compiler_heap or None,
                "compile_oom": result.compile_oom or None,
                "host": (result.host or {}).get("hostname"),
            },
            indent=2,
        )
    )


@main.command("project-gen")
@click.option("--harness", "harness_name", default=None, help="Registered harness name.")
@click.option("--name", "run_name", default=None, help="Run label for the bundle.")
@click.option(
    "--project",
    "project_name",
    default="commons-compress",
    show_default=True,
    help="Vendored project name under dataset/projects/.",
)
@click.option("--option", "-o", "options", multiple=True, help="key=value passed to harness constructor.")
@click.option(
    "--prompt",
    "prompt_path",
    type=click.Path(exists=True, path_type=Path),
    default=None,
    help="User prompt template (defaults to prompts/project_class.j2 in per-class "
    "mode, prompts/project_suite.j2 in digest mode).",
)
@click.option("--no-system-prompt", is_flag=True, help="Omit the system message from the chat request.")
@click.option(
    "--input-mode",
    type=click.Choice(["per-class", "digest"], case_sensitive=False),
    default=None,
    help="How the SUT is shown to a source-driven harness. 'per-class' (default "
    "for LLM harnesses) feeds one real class at a time; 'digest' makes a single "
    "call with the API-surface digest. Overrides project.yaml's harness_input.mode.",
)
@click.option(
    "--max-classes",
    type=int,
    default=None,
    help="Cap the number of SUT classes fed in per-class mode (handy for smoke runs).",
)
@click.option(
    "--parallel",
    type=click.IntRange(min=1),
    default=1,
    show_default=True,
    help="Number of concurrent harness calls during per-class generation.",
)
@click.option(
    "--compile-check",
    is_flag=True,
    help="After each class, compile against the pristine SUT; on failure send "
    "Maven errors back to the LLM for repair (requires JDK 17+ and Maven).",
)
@click.option(
    "--compile-check-retries",
    type=click.IntRange(min=0),
    default=2,
    show_default=True,
    help="LLM repair attempts per class when --compile-check is enabled.",
)
@click.option(
    "--out",
    "out_dir",
    type=click.Path(path_type=Path),
    default=None,
    help="Bundle directory (defaults to reports/projgen_<project>_<model>_<timestamp>).",
)
@click.option("--workdir", type=click.Path(path_type=Path), default=None, help="Scratch dir (default: temp).")
def project_gen_cmd(
    harness_name: str | None,
    run_name: str | None,
    project_name: str,
    options: tuple[str, ...],
    prompt_path: Path | None,
    no_system_prompt: bool,
    input_mode: str | None,
    max_classes: int | None,
    parallel: int,
    compile_check: bool,
    compile_check_retries: int,
    out_dir: Path | None,
    workdir: Path | None,
) -> None:
    """Phase 1 (project track): generate a JMH suite and write a portable bundle.

    Per-class generation can issue up to ``--parallel`` concurrent model calls.
    With ``--compile-check``, each class is compiled against the pristine SUT
    immediately after generation; Maven diagnostics are fed back to the LLM for
    up to ``--compile-check-retries`` repair attempts (requires JDK + Maven).
    """
    from jmhbench.project_bench import generate_project
    from jmhbench.project_bundle import write_project_bundle
    from jmhbench.projects import get_project_task

    if harness_name is None:
        console.print("[red]Provide --harness <name>.[/]")
        sys.exit(2)

    try:
        task = get_project_task(project_name)
    except KeyError as exc:
        console.print(f"[red]{exc}[/]")
        sys.exit(2)

    effective_mode = (
        input_mode.replace("-", "_") if input_mode else task.harness_input.get("mode", "per_class")
    )
    harness, harness_kwargs = _build_project_harness(
        harness_name=harness_name,
        predictions_dir=None,
        run_name=run_name,
        options=options,
        prompt_path=prompt_path,
        no_system_prompt=no_system_prompt,
        effective_mode=effective_mode,
        task=task,
    )

    run_label = resolve_run_label(harness, harness_name, harness_kwargs)
    out_dir = out_dir or Path("reports") / build_run_dir_name(
        f"{project_name}_{run_label}", prefix="projgen"
    )
    out_dir.mkdir(parents=True, exist_ok=True)

    scratch = Path(tempfile.mkdtemp(prefix="jmhbench-projgen-")) if workdir is None else workdir
    scratch.mkdir(parents=True, exist_ok=True)

    source_driven = getattr(harness, "source_driven", True)
    shown_mode = effective_mode if source_driven else "digest"
    cap_note = f" (capped at {max_classes})" if (max_classes and shown_mode == "per_class") else ""
    compile_check = compile_check or bool(task.harness_input.get("compile_check"))
    if compile_check_retries == 2 and task.harness_input.get("compile_check_retries") is not None:
        compile_check_retries = int(task.harness_input["compile_check_retries"])

    workers_label = "sequential" if parallel == 1 else f"up to {parallel} parallel"
    compile_note = "  compile-check=[cyan]on[/]" if compile_check else ""

    console.rule(f"Project generation: [bold]{task.display_name} {task.version}[/] with {harness_name}")
    console.print(
        f"input=[cyan]{shown_mode}[/]{cap_note}  workers=[cyan]{workers_label}[/]{compile_note}  "
        f"workdir=[dim]{scratch}[/]  bundle=[dim]{out_dir}[/]"
    )

    config_summary = {
        "harness": harness_name,
        "harness_kwargs": {k: v for k, v in harness_kwargs.items() if k != "predictions_dir"},
        "project": task.instance_id,
        "version": task.version,
        "input_mode": shown_mode,
        "max_classes": max_classes,
        "parallel": parallel,
        "compile_check": compile_check,
        "compile_check_retries": compile_check_retries if compile_check else 0,
    }

    on_progress = _make_project_gen_progress()
    try:
        result = generate_project(
            task, harness, scratch / task.instance_id,
            input_mode=effective_mode, max_classes=max_classes,
            parallel=parallel, on_progress=on_progress,
            compile_check=compile_check,
            compile_check_retries=compile_check_retries,
        )
    except KeyboardInterrupt:
        console.print("\n[yellow]Interrupted during generation.[/]")
        sys.exit(130)
    except Exception as exc:  # noqa: BLE001
        console.print(f"[red]unhandled error: {type(exc).__name__}: {exc}[/]")
        sys.exit(1)

    if not result.generated:
        console.print(f"[yellow]Nothing generated; no bundle written.[/] ({result.generation_error})")
        sys.exit(1)

    write_project_bundle(
        out_dir, result=result, task=task, config_summary=config_summary, run_label=run_label
    )

    partial = result.generation_interrupted
    console.rule("Generation complete" + (" (partial)" if partial else ""))
    if result.input_mode == "per_class":
        console.print(
            f"Generated [green]{result.classes_succeeded}[/]/{result.classes_total} class benchmark(s)."
        )
    else:
        console.print("Generated suite (digest mode).")
    console.print(f"Bundle written to [green]{out_dir}[/]")
    console.print(f"Next: [cyan]jmhbench project-bench {out_dir}[/]")
    if partial:
        sys.exit(130)


def _parse_sensitivity_sample(raw: str) -> int:
    """``all`` / ``-1`` -> every covering benchmark; otherwise a sample size.

    A fixed *k* only gives full coverage where *k* exceeds the largest fan-out,
    and there was no sentinel for "all" (EVALUATION_ISSUES.md B1).
    """
    text = (raw or "").strip().lower()
    if text in ("all", "-1", "full"):
        return -1
    try:
        value = int(text)
    except ValueError:
        raise ValueError(
            f"--sensitivity-sample expects an integer or 'all', got {raw!r}"
        ) from None
    return -1 if value < 0 else value


@main.command("project-bench")
@click.argument("bundle_dir", required=False, type=click.Path(exists=True, path_type=Path))
@click.option("--harness", "harness_name", default=None, help="Registered harness name.")
@click.option(
    "--predictions",
    "predictions_dir",
    type=click.Path(path_type=Path),
    default=None,
    help="Evaluate a pre-generated suite directory (contains <project>/*.java).",
)
@click.option("--name", "run_name", default=None, help="Run label when using --predictions.")
@click.option(
    "--project",
    "project_name",
    default="commons-compress",
    show_default=True,
    help="Vendored project name under dataset/projects/.",
)
@click.option("--option", "-o", "options", multiple=True, help="key=value passed to harness constructor.")
@click.option(
    "--prompt",
    "prompt_path",
    type=click.Path(exists=True, path_type=Path),
    default=None,
    help="User prompt template (defaults to prompts/project_class.j2 in per-class "
    "mode, prompts/project_suite.j2 in digest mode).",
)
@click.option("--no-system-prompt", is_flag=True, help="Omit the system message from the chat request.")
@click.option(
    "--preset",
    type=click.Choice(["quick", "default", "strong", "campaign", "llm4jmh"], case_sensitive=False),
    default="default",
    show_default=True,
    help="JMH iteration settings.",
)
@click.option("--quick", is_flag=True, help="Shortcut for --preset quick.")
@click.option("--strong", is_flag=True, help="Shortcut for --preset strong.")
@click.option("--skip-jmh", is_flag=True, help="Build only (no JMH execution / detection).")
@click.option(
    "--input-mode",
    type=click.Choice(["per-class", "digest"], case_sensitive=False),
    default=None,
    help="How the SUT is shown to a source-driven harness. "
    "'per-class' (default for LLM harnesses) feeds one real class at a time; "
    "'digest' makes a single call with the API-surface digest. "
    "Overrides project.yaml's harness_input.mode.",
)
@click.option(
    "--max-classes",
    type=int,
    default=None,
    help="Cap the number of SUT classes fed in per-class mode (handy for smoke runs).",
)
@click.option("--alpha", default=0.05, show_default=True, help="Significance level (p < alpha).")
@click.option(
    "--min-slowdown",
    default=1.10,
    show_default=True,
    help="Minimum slowdown ratio counting as a kill, e.g. 1.10 = +10%.",
)
@click.option(
    "--mutant-op",
    type=click.Choice(JmhSettings.MUTANT_OPS),
    default="sleep",
    show_default=True,
    help=(
        "What an armed mutant injects. 'sleep' is Thread.sleep(0,1) (~1.2 ms/hit on "
        "Linux) -- four orders of magnitude above the kill threshold, so every covered "
        "mutant dies and the score collapses onto coverage. 'consumecpu' burns "
        "--mutant-tokens steps of JMH's own Blackhole.consumeCPU (~1.9 ns each) for a "
        "latency a benchmark has to actually resolve -- prefer it for new measurement. "
        "'spin' is the earlier burn loop (~1.3 ns/token), kept so runs measured with it "
        "reproduce. 'nanotime' injects one System.nanoTime() read (~10-25 ns)."
    ),
)
@click.option(
    "--mutant-tokens",
    type=click.IntRange(min=1),
    default=64,
    show_default=True,
    help=(
        "Burn-loop steps per hit under --mutant-op consumecpu or spin: roughly a "
        "nanosecond each, but calibrate on the measurement host "
        "(tools/calibrate_mutant_op.py --op <operator>). Ignored by the other operators."
    ),
)
@click.option(
    "--sensitivity-sample",
    type=str,
    default="0",
    show_default=True,
    help=(
        "Covering benchmarks measured per mutant for the sensitivity metric: 'all' for "
        "every one of them, an integer for a uniform random sample of that size, 0 to skip. "
        "The kill probe stops at the first detection, so without this sensitivity can only "
        "be bounded. Only 'all' makes it a point estimate -- a fixed k gives full coverage "
        "only where k exceeds the largest fan-out, which is not knowable in advance. "
        "Costs roughly this many extra JMH runs per covered mutant."
    ),
)
@click.option(
    "--compiler-heap",
    default=None,
    help=(
        "Max heap for the Maven JVM, which is javac's heap too (e.g. '8g'). "
        f"Default {DEFAULT_COMPILER_HEAP}, or $JMHBENCH_COMPILER_HEAP. A large generated suite "
        "on top of a large SUT exhausts a small heap and javac dies with OutOfMemoryError, "
        "which scores the model 0 on a project it did produce a suite for."
    ),
)
@click.option(
    "--max-detect-attempts",
    type=int,
    default=6,
    show_default=True,
    help=(
        "Covering benchmarks the kill probe walks, most-hit-first, before giving up "
        "(0 = no cap). Bounds the cost of the mutation score only; --sensitivity-sample all "
        "measures every covering benchmark regardless."
    ),
)
@click.option(
    "--parallel",
    type=click.IntRange(min=1),
    default=1,
    show_default=True,
    help="Concurrent harness calls during generation (ignored when benching a bundle).",
)
@click.option(
    "--no-compile-filter",
    is_flag=True,
    help="Fail the run if the merged suite does not compile (default: drop "
    "non-compiling classes and bench the rest).",
)
@click.option(
    "--no-runtime-filter",
    is_flag=True,
    help="Skip the per-class JMH smoke probe before the baseline run (default: "
    "drop classes that throw during @Setup/execution).",
)
@click.option(
    "--compile-check",
    is_flag=True,
    help="During generation, compile each class and feed Maven errors back to "
    "the LLM for repair (combined / project-gen flows only).",
)
@click.option(
    "--compile-check-retries",
    type=click.IntRange(min=0),
    default=2,
    show_default=True,
    help="LLM repair attempts per class when --compile-check is enabled.",
)
@click.option(
    "--out",
    "out_dir",
    type=click.Path(path_type=Path),
    default=None,
    help="Report directory (defaults to reports/proj_<project>_<model>_<timestamp>).",
)
@click.option("--workdir", type=click.Path(path_type=Path), default=None, help="Scratch dir (default: temp).")
def project_bench_cmd(
    bundle_dir: Path | None,
    harness_name: str | None,
    predictions_dir: Path | None,
    run_name: str | None,
    project_name: str,
    options: tuple[str, ...],
    prompt_path: Path | None,
    no_system_prompt: bool,
    preset: str,
    quick: bool,
    strong: bool,
    skip_jmh: bool,
    input_mode: str | None,
    max_classes: int | None,
    alpha: float,
    min_slowdown: float,
    mutant_op: str,
    mutant_tokens: int,
    sensitivity_sample: str,
    max_detect_attempts: int,
    compiler_heap: str | None,
    parallel: int,
    no_compile_filter: bool,
    no_runtime_filter: bool,
    compile_check: bool,
    compile_check_retries: int,
    out_dir: Path | None,
    workdir: Path | None,
) -> None:
    """Project mutation track: score a harness by its performance mutation score.

    The harness generates a JMH benchmark *suite* for a whole vendored,
    held-out project (default: Apache Commons Compress). JMH-Bench builds the
    mutated subject once, records which benchmarks reach which of the project's
    fixed performance mutants, then arms each covered mutant in isolation and
    reruns only the benchmarks that reach it. The score is the fraction of
    mutants killed.

    Pass a BUNDLE_DIR (the output of ``jmhbench project-gen``) to skip generation
    and only benchmark a pre-generated suite — no harness or model access needed.
    Otherwise the suite is generated and benchmarked in one combined run.
    """
    from jmhbench.project_bench import bench_project, run_project
    from jmhbench.project_report import write_project_report
    from jmhbench.projects import get_project_task

    if quick:
        preset = "quick"
    if strong:
        preset = "strong"

    try:
        sensitivity_n = _parse_sensitivity_sample(sensitivity_sample)
    except ValueError as exc:
        console.print(f"[red]{exc}[/]")
        sys.exit(2)

    set_compiler_heap(compiler_heap)
    heap = effective_compiler_heap()

    jmh = JmhSettings.preset(preset)
    jmh.mutant_op = mutant_op
    jmh.mutant_spin_tokens = mutant_tokens
    compile_filter = not no_compile_filter
    runtime_filter = not no_runtime_filter

    # ----- Phase-2-only: benchmark a pre-generated bundle (no harness/model) ---
    if bundle_dir is not None:
        from jmhbench.project_bundle import load_project_bundle

        if harness_name or predictions_dir is not None or options:
            console.print(
                "[yellow]Benching a bundle — ignoring generation options "
                "(--harness/--predictions/-o/--prompt/--input-mode/--parallel).[/]"
            )
        try:
            bundle = load_project_bundle(bundle_dir)
        except (FileNotFoundError, ValueError, KeyError) as exc:
            console.print(f"[red]{exc}[/]")
            sys.exit(2)

        task = bundle.task
        result = bundle.to_result()
        config = RunConfig(
            harness_name=bundle.harness_name,
            harness_kwargs={},
            jmh=jmh,
            alpha=alpha,
            min_slowdown=min_slowdown,
            sensitivity_sample=sensitivity_n,
            max_detect_attempts=max_detect_attempts,
            skip_jmh=skip_jmh,
        )

        out_dir = out_dir or Path("reports") / build_run_dir_name(
            f"{task.instance_id}_{bundle.run_label}", prefix="proj"
        )
        out_dir.mkdir(parents=True, exist_ok=True)
        scratch = Path(tempfile.mkdtemp(prefix="jmhbench-proj-")) if workdir is None else workdir
        scratch.mkdir(parents=True, exist_ok=True)

        cls_note = (
            f"  classes=[cyan]{result.classes_succeeded}/{result.classes_total}[/]"
            if result.input_mode == "per_class"
            else ""
        )
        console.rule(
            f"Project mutation track (bench-only): "
            f"[bold]{task.display_name} {task.version}[/] from {bundle.run_label}"
        )
        console.print(
            f"preset=[cyan]{preset}[/]  input=[cyan]{result.input_mode}[/]{cls_note}  "
            f"mutants=[cyan]{task.mutant_count}[/]  "
            f"detect=[cyan]>={(min_slowdown - 1) * 100:.0f}% slower & p<{alpha}[/]  "
            f"mutant-op=[cyan]{_mutant_op_label(jmh)}[/]  "
            f"javac-heap=[cyan]{heap}[/]  "
            f"bundle=[dim]{bundle.dir}[/]  out=[dim]{out_dir}[/]"
        )

        config_summary = {
            "harness": bundle.harness_name,
            "from_bundle": str(bundle.dir),
            "generation_config": bundle.manifest.get("config", {}),
            "project": task.instance_id,
            "version": task.version,
            "preset": preset,
            "input_mode": result.input_mode,
            "alpha": alpha,
            "min_slowdown": min_slowdown,
            "sensitivity_sample": sensitivity_n,
            "sensitivity_sample_mode": "all" if sensitivity_n < 0 else str(sensitivity_n),
            "max_detect_attempts": max_detect_attempts,
            # The compiler heap belongs in provenance: whether a suite compiled
            # depends on it, and at the old 512m default that alone decided two
            # of GPT-OSS-120B's six projects.
            "compiler_heap": heap,
            "mutant_op": mutant_op,
            "mutant_tokens": mutant_tokens if mutant_op == "spin" else None,
            "jmh": jmh.__dict__,
            "compile_filter": compile_filter,
            "runtime_filter": runtime_filter,
        }

        swept = reap_orphaned_jmh_forks()
        if swept:
            console.print(f"[yellow]Reaped {swept} orphaned JMH fork(s) left by a previous run.[/]")

        try:
            result = bench_project(
                task, result, config, scratch / task.instance_id,
                compile_filter=compile_filter,
                runtime_filter=runtime_filter,
                on_phase=_project_phase,
                on_cover=_project_cover_progress,
                on_detect=_project_detect_progress,
            )
        except Exception as exc:  # noqa: BLE001
            console.print(f"[red]unhandled error: {type(exc).__name__}: {exc}[/]")
            sys.exit(1)
        finally:
            reap_orphaned_jmh_forks()

        _copy_project_raw_output(bundle.dir, out_dir, result)
        write_project_report(out_dir, result, task, config_summary)
        console.rule("Mutation score")
        _emit_project_scorecard(result)
        console.print(f"Report written to [green]{out_dir}[/]")
        return

    # ----- Combined: generate + benchmark in one run -------------------------
    if predictions_dir is not None:
        if harness_name and harness_name != "predictions":
            console.print("[red]--predictions cannot be combined with a different --harness.[/]")
            sys.exit(2)
        harness_name = "predictions"
    if harness_name is None:
        console.print("[red]Provide --harness <name>, --predictions <dir>, or a BUNDLE_DIR to bench.[/]")
        sys.exit(2)

    try:
        task = get_project_task(project_name)
    except KeyError as exc:
        console.print(f"[red]{exc}[/]")
        sys.exit(2)

    # Effective input mode (CLI --input-mode wins over project.yaml's default).
    effective_mode = (
        input_mode.replace("-", "_") if input_mode else task.harness_input.get("mode", "per_class")
    )
    harness, harness_kwargs = _build_project_harness(
        harness_name=harness_name,
        predictions_dir=predictions_dir,
        run_name=run_name,
        options=options,
        prompt_path=prompt_path,
        no_system_prompt=no_system_prompt,
        effective_mode=effective_mode,
        task=task,
    )

    config = RunConfig(
        harness_name=harness_name,
        harness_kwargs=harness_kwargs,
        jmh=jmh,
        alpha=alpha,
        min_slowdown=min_slowdown,
        sensitivity_sample=sensitivity_n,
        max_detect_attempts=max_detect_attempts,
        skip_jmh=skip_jmh,
    )

    run_label = resolve_run_label(harness, harness_name, harness_kwargs)
    out_dir = out_dir or Path("reports") / build_run_dir_name(
        f"{project_name}_{run_label}", prefix="proj"
    )
    out_dir.mkdir(parents=True, exist_ok=True)

    scratch = Path(tempfile.mkdtemp(prefix="jmhbench-proj-")) if workdir is None else workdir
    scratch.mkdir(parents=True, exist_ok=True)

    source_driven = getattr(harness, "source_driven", True)
    shown_mode = effective_mode if source_driven else "digest"
    cap_note = f" (capped at {max_classes})" if (max_classes and shown_mode == "per_class") else ""
    workers_label = "sequential" if parallel == 1 else f"up to {parallel} parallel"

    console.rule(f"Project mutation track: [bold]{task.display_name} {task.version}[/] with {harness_name}")
    console.print(
        f"preset=[cyan]{preset}[/]  input=[cyan]{shown_mode}[/]{cap_note}  "
        f"workers=[cyan]{workers_label}[/]  mutants=[cyan]{task.mutant_count}[/]  "
        f"detect=[cyan]>={(min_slowdown - 1) * 100:.0f}% slower & p<{alpha}[/]  "
        f"mutant-op=[cyan]{_mutant_op_label(jmh)}[/]  "
        f"workdir=[dim]{scratch}[/]  out=[dim]{out_dir}[/]"
    )

    compile_check = compile_check or bool(task.harness_input.get("compile_check"))
    if compile_check_retries == 2 and task.harness_input.get("compile_check_retries") is not None:
        compile_check_retries = int(task.harness_input["compile_check_retries"])

    config_summary = {
        "harness": harness_name,
        "harness_kwargs": {k: v for k, v in harness_kwargs.items() if k != "predictions_dir"},
        "project": task.instance_id,
        "version": task.version,
        "preset": preset,
        "input_mode": shown_mode,
        "max_classes": max_classes,
        "parallel": parallel,
        "compile_check": compile_check,
        "compile_check_retries": compile_check_retries if compile_check else 0,
            "compile_filter": compile_filter,
            "runtime_filter": runtime_filter,
            "alpha": alpha,
        "min_slowdown": min_slowdown,
        "mutant_op": mutant_op,
        "mutant_tokens": mutant_tokens if mutant_op == "spin" else None,
        "jmh": jmh.__dict__,
    }

    swept = reap_orphaned_jmh_forks()
    if swept:
        console.print(f"[yellow]Reaped {swept} orphaned JMH fork(s) left by a previous run.[/]")

    try:
        result = run_project(
            task, harness, config, scratch / task.instance_id,
            input_mode=effective_mode, max_classes=max_classes, parallel=parallel,
            compile_filter=compile_filter,
            runtime_filter=runtime_filter,
            compile_check=compile_check,
            compile_check_retries=compile_check_retries,
            on_progress=_make_project_gen_progress(),
            on_phase=_project_phase,
            on_cover=_project_cover_progress,
            on_detect=_project_detect_progress,
        )
    except Exception as exc:  # noqa: BLE001
        console.print(f"[red]unhandled error: {type(exc).__name__}: {exc}[/]")
        sys.exit(1)
    finally:
        reap_orphaned_jmh_forks()

    write_project_report(out_dir, result, task, config_summary)

    console.rule("Mutation score")
    _emit_project_scorecard(result)
    console.print(f"Report written to [green]{out_dir}[/]")


def _copy_project_raw_output(bundle_dir: Path, out_dir: Path, result) -> None:
    """Carry a bundle's combined model-output Markdown into the bench report."""
    rel = getattr(result, "raw_output_file", None)
    if not rel:
        return
    src = bundle_dir / rel
    if not src.exists():
        result.raw_output_file = None
        return
    dst = out_dir / rel
    dst.parent.mkdir(parents=True, exist_ok=True)
    import shutil

    shutil.copy2(src, dst)


@main.command("score")
@click.argument("run_dir", type=click.Path(exists=True, path_type=Path))
def score_cmd(run_dir: Path) -> None:
    """Print the summary of a previous run."""
    summary = run_dir / "summary.md"
    if summary.exists():
        console.print(summary.read_text())
    else:
        console.print(f"[red]No summary.md at {run_dir}[/]")
        sys.exit(2)


@main.command("new-task")
@click.argument("instance_id")
@click.option("--dataset-dir", type=click.Path(path_type=Path), default=None)
def new_task_cmd(instance_id: str, dataset_dir: Path | None) -> None:
    """Scaffold a fresh task folder under the dataset directory."""
    base = dataset_dir or default_dataset_dir()
    task_dir = base / instance_id
    if task_dir.exists():
        console.print(f"[red]Task already exists: {task_dir}[/]")
        sys.exit(2)
    (task_dir / "src" / "main" / "java" / "bench").mkdir(parents=True)
    (task_dir / "regressions").mkdir()
    (task_dir / "task.yaml").write_text(
        "\n".join(
            [
                f"instance_id: {instance_id}",
                "target_class: bench.Example",
                "tags: [example]",
                "regressions: []",
                "",
            ]
        )
    )
    (task_dir / "src" / "main" / "java" / "bench" / "Example.java").write_text(
        "package bench;\n\npublic final class Example {\n"
        "    public int compute(int n) { return n; }\n}\n"
    )
    console.print(f"Created task at [green]{task_dir}[/]")


if __name__ == "__main__":  # pragma: no cover
    main()
