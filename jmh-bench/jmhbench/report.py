"""Emit JSON and Markdown reports for a run."""

from __future__ import annotations

import json
import re
from datetime import datetime
from pathlib import Path

from jmhbench.adapters._llm_common import render_raw_output_md
from jmhbench.runner import TaskResult
from jmhbench.scoring import Scorecard, aggregate


def sanitize_run_label(label: str) -> str:
    """Make a model/harness name safe for a report directory segment."""
    s = label.replace("/", "_").replace("\\", "_").replace(":", "-")
    s = re.sub(r"[^\w.\-+]", "-", s)
    s = re.sub(r"-+", "-", s).strip("-")
    return s or "run"


def resolve_run_label(harness: object, harness_name: str, harness_kwargs: dict) -> str:
    """Pick a human-readable label for the report directory.

    Prefers the resolved model name (after env/default fallback in the harness),
    then an explicit predictions run name, then the harness id.
    """
    model = getattr(harness, "model", None)
    if model:
        return sanitize_run_label(str(model))
    if harness_kwargs.get("name"):
        return sanitize_run_label(str(harness_kwargs["name"]))
    return sanitize_run_label(harness_name)


def format_run_timestamp(when: datetime | None = None) -> str:
    """UTC timestamp formatted for report directory names."""
    return (when or datetime.utcnow()).strftime("%Y-%m-%d_%H-%M-%S")


def build_run_dir_name(
    label: str,
    *,
    prefix: str | None = None,
    when: datetime | None = None,
) -> str:
    """Build ``<label>_<YYYY-MM-DD_HH-MM-SS>`` or ``<prefix>_<label>_<timestamp>``."""
    ts = format_run_timestamp(when)
    safe_label = sanitize_run_label(label)
    if prefix:
        return f"{sanitize_run_label(prefix)}_{safe_label}_{ts}"
    return f"{safe_label}_{ts}"


def write_task_result(per_task_dir: Path, r: TaskResult) -> None:
    """Persist one task's artifacts under ``per_task/``."""
    per_task_dir.mkdir(parents=True, exist_ok=True)
    task_dict = r.to_dict()
    if r.raw_output is not None:
        raw_rel = f"per_task/{r.instance_id}.raw.md"
        (per_task_dir / f"{r.instance_id}.raw.md").write_text(
            render_raw_output_md(r.instance_id, r.raw_output, r.generation_metadata)
        )
        task_dict["raw_output_file"] = raw_rel
        r.raw_output_file = raw_rel
    (per_task_dir / f"{r.instance_id}.json").write_text(
        json.dumps(task_dict, indent=2, default=str)
    )
    if r.benchmark_source:
        (per_task_dir / f"{r.instance_id}.java").write_text(r.benchmark_source)


def write_run_summary(
    run_dir: Path,
    harness_name: str,
    results: list[TaskResult],
    config_summary: dict,
    *,
    interrupted: bool = False,
) -> Scorecard:
    """Refresh aggregate ``results.json`` and ``summary.md`` for a run."""
    run_dir.mkdir(parents=True, exist_ok=True)
    card = aggregate(results)
    (run_dir / "results.json").write_text(
        json.dumps(
            {
                "harness": harness_name,
                "timestamp": datetime.utcnow().isoformat() + "Z",
                "interrupted": interrupted,
                "config": config_summary,
                "summary": card.to_dict(),
                "tasks": [r.to_dict() for r in results],
            },
            indent=2,
            default=str,
        )
    )
    (run_dir / "summary.md").write_text(
        _render_markdown(
            harness_name,
            card,
            results,
            config_summary,
            interrupted=interrupted,
        )
    )
    return card


def write_run(
    run_dir: Path,
    harness_name: str,
    results: list[TaskResult],
    config_summary: dict,
) -> Scorecard:
    run_dir.mkdir(parents=True, exist_ok=True)
    per_task_dir = run_dir / "per_task"
    per_task_dir.mkdir(exist_ok=True)
    for r in results:
        write_task_result(per_task_dir, r)
    return write_run_summary(run_dir, harness_name, results, config_summary)


def _render_markdown(
    harness: str,
    card: Scorecard,
    results: list[TaskResult],
    config: dict,
    *,
    interrupted: bool = False,
) -> str:
    s = card.to_dict()
    rates = s["rates"]
    fpr_mode = s["fpr_replicate_total"] > 0
    lines = [
        f"# JMH-Bench {'FPR ' if fpr_mode else ''}run — {harness}",
        "",
        f"- Timestamp: `{datetime.utcnow().isoformat()}Z`",
        f"- Tasks: **{s['n_tasks']}**"
        + (" _(partial — run interrupted)_" if interrupted else ""),
        f"- Mode: {'**False-positive-rate**' if fpr_mode else 'Regression detection'}",
        f"- Config: `{config}`",
        "",
        "## Headline numbers",
        "",
        "| Dimension | Rate | Pass / Total |",
        "|---|---|---|",
        f"| Generation succeeded | {rates['generated']:.0%} | {s['n_generated']}/{s['n_tasks']} |",
        f"| Compiles | {rates['compiles']:.0%} | {s['n_compiles']}/{s['n_tasks']} |",
        f"| Executes (JMH ran) | {rates['executes']:.0%} | {s['n_executes']}/{s['n_tasks']} |",
        f"| Zero anti-patterns | {rates['clean']:.0%} | {s['n_no_antipatterns']}/{s['n_tasks']} |",
    ]
    if fpr_mode:
        reps_per_task = max((len(r.fpr_results) for r in results), default=0)
        if reps_per_task <= 1:
            # One unchanged comparison per task — symmetric with the regression
            # run (one verdict per task), so a single rate is unambiguous.
            lines.append(
                f"| **False positives** | **{rates['fpr_per_replicate']:.1%}** "
                f"| {s['fpr_false_positives']}/{s['fpr_replicate_total']} |"
            )
        else:
            lines += [
                f"| **False positives (per replicate)** | **{rates['fpr_per_replicate']:.1%}** "
                f"| {s['fpr_false_positives']}/{s['fpr_replicate_total']} |",
                f"| Tasks with any false positive | {rates['fpr_per_task']:.0%} "
                f"| {s['fpr_tasks_with_any_fp']}/{s['n_tasks']} |",
            ]
    else:
        lines += [
            f"| Regression detection | {rates['regression_detection']:.0%} "
            f"| {s['regression_detected']}/{s['regression_total']} |",
            f"| **Composite pass** | **{rates['composite']:.0%}** "
            f"| {s['composite_passes']}/{s['n_tasks']} |",
        ]
    ap_source = s.get("antipattern_source", "regex")
    ap_source_label = {
        "spotjmhbugs": "SpotJMHBugs (Costa et al., TSE 2019 bytecode plugin)",
        "regex": "regex pre-screen (source-level)",
    }.get(ap_source, ap_source)
    lines += [
        "",
        f"Median stability RSD (base run): `{s['median_stability_rsd']}` %",
        "",
        f"## Anti-pattern breakdown ({ap_source_label})",
        "",
        "| Pattern | Tasks violating |",
        "|---|---|",
    ]
    for ap, count in sorted(s["antipattern_counts"].items()):
        lines.append(f"| `{ap}` | {count} |")
    if not s["antipattern_counts"]:
        lines.append("| _(none)_ | 0 |")

    if fpr_mode:
        lines += [
            "",
            "## Per-task results (FPR)",
            "",
            "| Task | Generated | Compiles | Executes | Antipatterns | False positives | RSD % |",
            "|---|---|---|---|---|---|---|",
        ]
        for r in results:
            fp_total = len(r.fpr_results) or 0
            fp_hit = sum(1 for info in r.fpr_results.values() if info.get("detected"))
            ap_total = _per_task_ap_total(r, ap_source)
            rsd = f"{r.stability_rsd_percent:.1f}" if r.stability_rsd_percent is not None else "-"
            lines.append(
                f"| {_task_cell(r)} "
                f"| {'OK' if r.generated else 'FAIL'} "
                f"| {'OK' if r.compiles else 'FAIL'} "
                f"| {'OK' if r.executes else '-'} "
                f"| {ap_total} "
                f"| {fp_hit}/{fp_total} "
                f"| {rsd} |"
            )
    else:
        lines += [
            "",
            "## Per-task results",
            "",
            "| Task | Generated | Compiles | Executes | Antipatterns | Regressions | RSD % |",
            "|---|---|---|---|---|---|---|",
        ]
        for r in results:
            reg_total = len(r.regression_results) or 0
            reg_ok = sum(1 for info in r.regression_results.values() if info.get("detected"))
            ap_total = _per_task_ap_total(r, ap_source)
            rsd = f"{r.stability_rsd_percent:.1f}" if r.stability_rsd_percent is not None else "-"
            lines.append(
                f"| {_task_cell(r)} "
                f"| {'OK' if r.generated else 'FAIL'} "
                f"| {'OK' if r.compiles else 'FAIL'} "
                f"| {'OK' if r.executes else '-'} "
                f"| {ap_total} "
                f"| {reg_ok}/{reg_total} "
                f"| {rsd} |"
            )
    return "\n".join(lines) + "\n"


def _task_cell(r: TaskResult) -> str:
    """Render the leftmost cell as `id` linked to its JSON record and, when
    the harness produced source, a second link to the generated `.java`."""
    links = [f"[`{r.instance_id}`](per_task/{r.instance_id}.json)"]
    if r.benchmark_source:
        links.append(f"[java](per_task/{r.instance_id}.java)")
    if r.raw_output_file:
        links.append(f"[raw]({r.raw_output_file})")
    return " ".join(links)


def _per_task_ap_total(r, ap_source: str) -> str | int:
    """Anti-pattern count to show per task, sourced consistently with the
    scorecard's chosen checker."""
    if ap_source == "spotjmhbugs":
        sj = getattr(r, "spotjmhbugs", {}) or {}
        if sj.get("available"):
            return int(sj.get("total", 0))
    sc = getattr(r, "static_check", {}) or {}
    return sc.get("total", "-")
