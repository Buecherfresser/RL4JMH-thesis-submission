"""Reports for the project mutation track (performance mutation score)."""

from __future__ import annotations

import json
import math
from collections import defaultdict
from datetime import datetime
from pathlib import Path

from jmhbench.adapters._llm_common import render_raw_output_md
from jmhbench.harness import ProjectTask
from jmhbench.project_bench import ProjectBenchResult


def write_project_report(
    out_dir: Path,
    result: ProjectBenchResult,
    task: ProjectTask,
    config_summary: dict,
) -> str:
    """Write scorecard.json, summary.md and the generated suite; return summary."""
    out_dir.mkdir(parents=True, exist_ok=True)

    if result.raw_outputs:
        # Per-class generation: concatenate every call's prompt/reply.
        raw_rel = "model_output.md"
        blocks = []
        for i, raw in enumerate(result.raw_outputs):
            blocks.append(f"# Class generation {i}\n")
            blocks.append(render_raw_output_md(result.instance_id, raw, {}))
        (out_dir / raw_rel).write_text("\n\n---\n\n".join(blocks))
        result.raw_output_file = raw_rel
    elif result.raw_output is not None:
        raw_rel = "model_output.md"
        (out_dir / raw_rel).write_text(
            render_raw_output_md(result.instance_id, result.raw_output, result.generation_metadata)
        )
        result.raw_output_file = raw_rel

    if result.benchmark_source:
        (out_dir / "benchmark.java").write_text(result.benchmark_source)
    # Persist the full generated suite (primary + every per-class file) so a run
    # is self-contained and reproducible.
    if result.extra_sources:
        gen_dir = out_dir / "generated"
        for rel_path, src in result.extra_sources.items():
            dst = gen_dir / rel_path
            dst.parent.mkdir(parents=True, exist_ok=True)
            dst.write_text(src)

    scorecard = {
        "harness": result.harness,
        "project": task.instance_id,
        "version": task.version,
        "timestamp": datetime.utcnow().isoformat() + "Z",
        # Which machine produced these numbers, captured before the first
        # benchmark ran. Bug size is a within-pair ratio so raw speed cancels,
        # but the host's noise floor does not: bug size is 1 - U of a confidence
        # interval, so a noisier box yields fewer kills at the same true effect.
        # Two arms measured on hosts with different floors are not comparable
        # even though every individual ratio is valid (EVALUATION_ISSUES.md B5).
        "host": result.host or None,
        "config": config_summary,
        "result": result.to_dict(),
    }
    (out_dir / "scorecard.json").write_text(
        json.dumps(
            _json_safe(scorecard),
            indent=2,
            default=str,
            # A bare ``NaN`` token is not JSON: Python reads it back, jq and
            # every strict parser reject the whole file. ``_json_safe`` maps
            # non-finite floats to null first, so this cannot raise
            # (EVALUATION_ISSUES.md C5).
            allow_nan=False,
        )
    )

    summary = _render_markdown(result, task, config_summary)
    (out_dir / "summary.md").write_text(summary)
    return summary


def _json_safe(obj):
    """Replace NaN / +-Inf with null so the scorecard is standard JSON."""
    if isinstance(obj, float):
        return obj if math.isfinite(obj) else None
    if isinstance(obj, dict):
        return {k: _json_safe(v) for k, v in obj.items()}
    if isinstance(obj, (list, tuple)):
        return [_json_safe(v) for v in obj]
    return obj


def _component_index(task: ProjectTask) -> dict[int, str]:
    return {m.id: m.component for m in task.mutants}


def _render_markdown(result: ProjectBenchResult, task: ProjectTask, config: dict) -> str:
    comp_of = _component_index(task)
    status_of = {m["id"]: m["status"] for m in result.mutant_results}

    # Per-component tallies.
    per_comp_total: dict[str, int] = defaultdict(int)
    per_comp_cov: dict[str, int] = defaultdict(int)
    per_comp_kill: dict[str, int] = defaultdict(int)
    for m in task.mutants:
        comp = comp_of.get(m.id, "?")
        per_comp_total[comp] += 1
        st = status_of.get(m.id)
        if st in ("killed", "covered_not_killed"):
            per_comp_cov[comp] += 1
        if st == "killed":
            per_comp_kill[comp] += 1

    score_pct = f"{result.mutation_score:.1%}"
    cov_pct = f"{result.coverage_rate:.1%}"
    not_covered = result.mutant_count - result.mutants_covered
    covered_not_killed = result.mutants_covered - result.mutants_killed

    lines = [
        f"# JMH-Bench project mutation run - {result.harness}",
        "",
        f"- Project: **{task.display_name} {task.version}** (`{task.instance_id}`)",
        f"- Timestamp: `{datetime.utcnow().isoformat()}Z`",
        f"- Config: `{config}`",
        "",
        "## Headline",
        "",
        "| Dimension | Value |",
        "|---|---|",
        f"| Generation succeeded | {'yes' if result.generated else 'no'} |",
        f"| Harness input mode | {result.input_mode} |",
    ]
    if result.input_mode == "per_class":
        lines.append(
            f"| SUT classes benchmarked | {result.classes_succeeded}/{result.classes_total} |"
        )
    if result.classes_filtered:
        lines.append(
            f"| Classes dropped (compile-and-filter) | {result.classes_filtered} |"
        )
    if result.classes_runtime_filtered:
        lines.append(
            f"| Classes dropped (runtime-and-filter) | {result.classes_runtime_filtered} |"
        )
    if result.classes_compiled:
        lines.append(f"| Classes compiled into suite | {result.classes_compiled} |")
    lines += [
        f"| Compiles | {'yes' if result.compiles else 'no'} |",
        f"| Executes (JMH ran) | {'yes' if result.executes else 'no'} |",
        f"| Benchmarks generated | {result.n_benchmarks} |",
        f"| **Performance mutation score** | **{score_pct}** ({result.mutants_killed}/{result.mutant_count}) |",
        f"| Mutant coverage | {cov_pct} ({result.mutants_covered}/{result.mutant_count}) |",
        f"| Covered but not killed | {covered_not_killed} |",
        f"| Not covered | {not_covered} |",
        f"| Stability RSD (base) | `{result.stability_rsd_percent}` % |",
    ]
    # The injected-latency operator decides what the score can mean: under
    # `sleep` an armed mutant costs ~1.2 ms a hit, so anything covered is killed
    # and the score is coverage by another name. A reader comparing two runs has
    # to see which operator produced each.
    mutant_op = config.get("mutant_op") if isinstance(config, dict) else None
    if mutant_op:
        tokens = config.get("mutant_tokens")
        detail = f"`{mutant_op}`" + (f" x {tokens} tokens" if mutant_op == "spin" and tokens else "")
        if mutant_op == "sleep":
            detail += " — ~1.2 ms/hit, far above the kill threshold"
        lines.append(f"| Injected-latency operator | {detail} |")
    lines.append("")

    if result.generation_error:
        lines += [f"> Generation error: `{result.generation_error}`", ""]
    if result.compile_error and not result.compiles:
        lines += ["> Compilation failed (tail):", "", "```", result.compile_error[-1500:], "```", ""]
    if result.compile_filter_dropped:
        lines += [
            "## Dropped at compile-and-filter",
            "",
            "| package | file |",
            "|---|---|",
        ]
        for d in result.compile_filter_dropped[:40]:
            lines.append(f"| `{d.get('package', '?')}` | `{d.get('file', '?')}` |")
        if len(result.compile_filter_dropped) > 40:
            lines.append(f"| … | +{len(result.compile_filter_dropped) - 40} more |")
        lines.append("")
    if result.runtime_filter_dropped:
        lines += [
            "## Dropped at runtime-and-filter",
            "",
            "| package | file |",
            "|---|---|",
        ]
        for d in result.runtime_filter_dropped[:40]:
            lines.append(f"| `{d.get('package', '?')}` | `{d.get('file', '?')}` |")
        if len(result.runtime_filter_dropped) > 40:
            lines.append(f"| … | +{len(result.runtime_filter_dropped) - 40} more |")
        lines.append("")
    if result.execution_error and not result.executes:
        lines += [f"> Execution error: `{result.execution_error}`", ""]

    lines += [
        "The performance mutation score is the fraction of the project's fixed,",
        "independent performance mutants that the generated benchmark suite detects:",
        "a mutant is *killed* when arming it makes some covering benchmark slow past",
        "the global threshold (>=" + f"{(config.get('min_slowdown', 1.10) - 1) * 100:.0f}% slower & "
        f"p<{config.get('alpha', 0.05)}). It rewards broad, realistic coverage of the",
        "real API rather than a single hand-picked method.",
        "",
        "## By component",
        "",
        "| Component | Killed | Covered | Total |",
        "|---|---|---|---|",
    ]
    for comp in sorted(per_comp_total):
        lines.append(
            f"| `{comp}` | {per_comp_kill[comp]} | {per_comp_cov[comp]} | {per_comp_total[comp]} |"
        )

    killed = [m for m in result.mutant_results if m["status"] == "killed"]
    if killed:
        lines += [
            "",
            "## Killed mutants",
            "",
            "| id | component | killed by | slowdown |",
            "|---|---|---|---|",
        ]
        for m in sorted(killed, key=lambda x: x["id"]):
            eff = m.get("effect_size")
            eff_s = f"{eff:.2f}x" if isinstance(eff, (int, float)) else "-"
            killer = (m.get("killed_by") or "").split(".")[-1]
            lines.append(f"| {m['id']} | `{comp_of.get(m['id'], '?')}` | `{killer}` | {eff_s} |")

    # Every kill now carries an effect size: a timed-out armed run is an error,
    # not a detection, so the "-" that used to hide timeout kills in this column
    # can no longer be produced (EVALUATION_ISSUES.md B2).
    errored = [m for m in result.mutant_results if m["status"] == "error"]
    if errored:
        lines += [
            "",
            "## Unmeasured mutants",
            "",
            "Every covering benchmark for these mutants failed or exceeded its",
            "wall-clock budget, so no t-test was run. They are **not** surviving",
            "mutants and they are **not** kills; they are missing measurements.",
            "",
            "| id | component | covering | timed out | error |",
            "|---|---|---|---|---|",
        ]
        for m in sorted(errored, key=lambda x: x["id"]):
            err = (m.get("error") or "").replace("|", "/")[:80]
            lines.append(
                f"| {m['id']} | `{comp_of.get(m['id'], '?')}` | {len(m.get('covered_by') or [])} "
                f"| {m.get('n_timeouts', 0)} | {err} |"
            )

    if result.baseline_failures:
        lines += [
            "",
            "## Benchmarks excluded at the baseline",
            "",
            f"Budget: {result.wall_budget_seconds:.0f}s per benchmark, applied to the",
            "baseline and to every armed rerun alike. A benchmark that cannot be",
            "measured within it is dropped from the suite rather than left to",
            "time out on every mutant it covers.",
            "",
            "| benchmark | over budget | wall (s) | error |",
            "|---|---|---|---|",
        ]
        for f in result.baseline_failures[:50]:
            err = str(f.get("error") or "").replace("|", "/")[:80]
            name = str(f.get("benchmark", "")).split(".")[-1]
            lines.append(
                f"| `{name}` | {'yes' if f.get('timed_out') else 'no'} "
                f"| {f.get('wall_seconds', 0)} | {err} |"
            )
        if len(result.baseline_failures) > 50:
            lines.append(f"| ... {len(result.baseline_failures) - 50} more | | | |")

    if result.coverage_failures:
        lines += [
            "",
            f"## Coverage record failures ({len(result.coverage_failures)})",
            "",
            "These benchmarks' record runs did not complete, so their coverage is",
            "**unknown**, not empty. They contribute no covering pairs.",
            "",
            "| benchmark | error |",
            "|---|---|",
        ]
        for f in result.coverage_failures[:50]:
            err = str(f.get("error") or "").replace("|", "/")[:80]
            lines.append(f"| `{str(f.get('benchmark', '')).split('.')[-1]}` | {err} |")

    if result.host:
        cpu = result.host.get("cpu") or {}
        jdk = result.host.get("jdk") or {}
        load = result.host.get("load_average") or {}
        jvm = (config.get("jmh") or {})
        lines += [
            "",
            "## Measurement host",
            "",
            "| field | value |",
            "|---|---|",
            f"| hostname | `{result.host.get('hostname')}` |",
            f"| CPU | {cpu.get('model')} |",
            f"| sockets x cores x threads | {cpu.get('sockets')} x {cpu.get('physical_cores')} "
            f"x {cpu.get('logical_cpus')} |",
            f"| SMT / governor / turbo | {cpu.get('smt')} / {cpu.get('governor')} / {cpu.get('turbo')} |",
            f"| RAM | {result.host.get('memory_gib')} GiB |",
            f"| kernel | `{result.host.get('kernel')}` |",
            f"| load at start (1/5/15m) | {load.get('1m')} / {load.get('5m')} / {load.get('15m')} |",
            f"| virtualisation | {result.host.get('virtualisation') or 'none detected'} |",
            f"| JDK | `{(jdk.get('version_string') or '').splitlines()[0] if jdk.get('version_string') else '?'}` |",
            f"| pinned JVM | heap={jvm.get('heap')} gc={jvm.get('gc_collector')} "
            f"threads={jvm.get('threads')} mode={jvm.get('mode')} |",
        ]

    return "\n".join(lines) + "\n"
