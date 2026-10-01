"""``jmh-run`` — compile and execute a single JMH benchmark, then print its rewards.

This is the user-facing demonstration of the runner: it ties together the Maven backend,
the local runner client, and the cheap composite reward into one command.
"""

from __future__ import annotations

import json
import re
from pathlib import Path
from typing import Annotated, Any

import typer

from jmhgen.config.schema import RunnerConfig
from jmhgen.rewards import default_cheap_reward
from jmhgen.runner.client import LocalRunnerClient, RewardRequest
from jmhgen.runner.types import BenchmarkSpec, JmhOptions

_PACKAGE_RE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)
_CLASS_RE = re.compile(r"\b(?:public\s+|final\s+|abstract\s+)*class\s+(\w+)")


def _infer_package(source: str) -> str:
    match = _PACKAGE_RE.search(source)
    return match.group(1) if match else ""


def _infer_class_name(source: str, fallback: str) -> str:
    match = _CLASS_RE.search(source)
    return match.group(1) if match else fallback


def _summarize(reward_value: float, evaluation: Any) -> dict[str, Any]:
    run = evaluation.run
    return {
        "compiled": evaluation.compiled,
        "ran": evaluation.ran,
        "composite_reward": reward_value,
        "compile": {
            "success": evaluation.compile.success,
            "error_kind": evaluation.compile.error_kind.value,
            "duration_s": round(evaluation.compile.duration_s, 3),
        },
        "run": None
        if run is None
        else {
            "success": run.success,
            "error_kind": run.error_kind.value,
            "duration_s": round(run.duration_s, 3),
            "benchmarks": [
                {
                    "benchmark": s.benchmark,
                    "mode": s.mode,
                    "score": s.score,
                    "unit": s.unit,
                    "robust_rsd": s.robust_rsd,
                }
                for s in run.stats
            ],
        },
    }


def run(
    java_file: Annotated[Path, typer.Argument(help="Path to the .java benchmark file.")],
    package: Annotated[
        str | None, typer.Option(help="Java package (inferred from source if omitted).")
    ] = None,
    class_name: Annotated[
        str | None, typer.Option(help="Benchmark class name (inferred if omitted).")
    ] = None,
    classpath: Annotated[
        list[str] | None,
        typer.Option(help="Extra classpath entry for code-under-test (repeatable)."),
    ] = None,
    config: Annotated[
        Path | None, typer.Option(help="Runner config YAML (defaults to built-in values).")
    ] = None,
    execute: Annotated[
        bool, typer.Option("--run/--no-run", help="Execute after compiling.")
    ] = True,
    forks: Annotated[int | None, typer.Option(help="Override JMH forks.")] = None,
    warmup_iterations: Annotated[int | None, typer.Option(help="Override JMH warmups.")] = None,
    measurement_iterations: Annotated[
        int | None, typer.Option(help="Override JMH measurement iterations.")
    ] = None,
) -> None:
    """Compile and (optionally) run JAVA_FILE as a JMH benchmark and print reward info."""
    source = java_file.read_text(encoding="utf-8")
    resolved_package = package if package is not None else _infer_package(source)
    resolved_class = class_name or _infer_class_name(source, java_file.stem)

    runner_config = RunnerConfig.from_yaml(config) if config else RunnerConfig()
    runner = runner_config.build_runner()
    if not runner.is_available():
        typer.echo(
            f"error: need '{runner.mvn_executable}' and '{runner.java_executable}' on PATH",
            err=True,
        )
        raise typer.Exit(code=2)

    options = runner_config.jmh.to_options()
    overrides: dict[str, Any] = {}
    if forks is not None:
        overrides["forks"] = forks
    if warmup_iterations is not None:
        overrides["warmup_iterations"] = warmup_iterations
    if measurement_iterations is not None:
        overrides["measurement_iterations"] = measurement_iterations
    if overrides:
        options = JmhOptions.from_dict({**options.to_dict(), **overrides})

    spec = BenchmarkSpec(
        source=source,
        class_name=resolved_class,
        package=resolved_package,
        extra_classpath=tuple(classpath or ()),
    )

    typer.echo(f"compiling {spec.fully_qualified_name} ...", err=True)
    client = LocalRunnerClient(runner)
    response = client.evaluate(RewardRequest(spec=spec, options=options, need_run=execute))
    evaluation = response.evaluation
    reward = default_cheap_reward()(evaluation)

    typer.echo(json.dumps(_summarize(reward.value, evaluation), indent=2))

    ok = evaluation.compiled and (evaluation.ran or not execute)
    if not ok:
        tail = (evaluation.run.stderr if evaluation.run else evaluation.compile.stderr)[-1500:]
        if tail.strip():
            typer.echo("\n--- diagnostics (tail) ---", err=True)
            typer.echo(tail, err=True)
    raise typer.Exit(code=0 if ok else 1)


def main() -> None:
    typer.run(run)


if __name__ == "__main__":
    main()
