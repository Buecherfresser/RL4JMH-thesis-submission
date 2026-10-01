"""Create local PNG diagnostics from scalar GRPO telemetry."""

from __future__ import annotations

import argparse
import csv
import json
from pathlib import Path
from statistics import fmean
from typing import Any


def read_csv_rows(path: Path) -> list[dict[str, float | None]]:
    """Read a scalar metrics CSV, preserving missing values as ``None``."""
    if not path.exists():
        return []
    rows: list[dict[str, float | None]] = []
    with path.open(newline="", encoding="utf-8") as handle:
        for raw in csv.DictReader(handle):
            row: dict[str, float | None] = {}
            for key, value in raw.items():
                try:
                    row[key] = float(value) if value not in (None, "") else None
                except ValueError:
                    row[key] = None
            rows.append(row)
    return rows


def diagnostics_summary(
    step_rows: list[dict[str, float | None]], component_rows: list[dict[str, float | None]]
) -> dict[str, Any]:
    """Build a compact report usable without opening any graph."""
    def values(rows: list[dict[str, float | None]], key: str) -> list[float]:
        return [value for row in rows if (value := row.get(key)) is not None]

    components = {
        key.removesuffix("_mean"): fmean(component_values)
        for key in (component_rows[0].keys() if component_rows else ())
        if key.endswith("_mean")
        and not key.startswith("weighted_")
        and (component_values := values(component_rows, key))
    }
    return {
        "steps": len(step_rows),
        "mean_reward": fmean(values(step_rows, "reward")) if values(step_rows, "reward") else None,
        "mean_reward_std": (
            fmean(values(step_rows, "reward_std")) if values(step_rows, "reward_std") else None
        ),
        "zero_std_step_frac": (
            fmean(values(step_rows, "zero_std_frac"))
            if values(step_rows, "zero_std_frac")
            else None
        ),
        "mean_parse_fail_frac": (
            fmean(values(step_rows, "parse_fail_frac"))
            if values(step_rows, "parse_fail_frac")
            else None
        ),
        "mean_compile_frac": (
            fmean(values(step_rows, "compile_frac"))
            if values(step_rows, "compile_frac")
            else None
        ),
        "mean_run_frac": (
            fmean(values(step_rows, "run_frac")) if values(step_rows, "run_frac") else None
        ),
        "mutation": {
            key.removeprefix("mutation_"): (
                fmean(values(step_rows, key)) if values(step_rows, key) else None
            )
            for key in (
                "mutation_score_mean",
                "mutation_attempted_mean",
                "mutation_killed_mean",
                "mutation_coverage_rate_mean",
                "mutation_sampled_kill_rate_mean",
                "mutation_not_covered_frac",
                "mutation_covered_not_killed_frac",
                "mutation_timeout_kill_frac",
                "mutation_statistical_kill_frac",
            )
        },
        "component_means": components,
    }


def _plot(metrics_dir: Path, out_dir: Path) -> list[Path]:
    """Write the plot suite. Imported lazily so normal training needs no matplotlib."""
    import matplotlib

    matplotlib.use("Agg")
    import matplotlib.pyplot as plt

    step_rows = read_csv_rows(metrics_dir / "step_metrics.csv")
    component_rows = read_csv_rows(metrics_dir / "reward_components.csv")
    if not step_rows:
        raise ValueError(f"no step metrics found in {metrics_dir}")

    out_dir.mkdir(parents=True, exist_ok=True)
    steps = [row.get("step") for row in step_rows]
    paths: list[Path] = []

    fig, axes = plt.subplots(2, 3, figsize=(15, 8), constrained_layout=True)
    plots = (
        ("Reward", ("reward", "reward_std")),
        ("Optimizer", ("loss", "grad_norm")),
        ("Learning rate", ("learning_rate",)),
        ("Completion truncation", ("clipped_ratio",)),
        ("Execution rates", ("parse_fail_frac", "compile_frac", "run_frac")),
        ("Step duration (s)", ("step_time",)),
    )
    for axis, (title, keys) in zip(axes.flat, plots, strict=True):
        for key in keys:
            values = [row.get(key) for row in step_rows]
            if any(value is not None for value in values):
                axis.plot(steps, values, label=key)
        axis.set_title(title)
        axis.set_xlabel("Optimizer step")
        if axis.lines:
            axis.legend()
        axis.grid(alpha=0.2)
    path = out_dir / "training_metrics.png"
    fig.savefig(path, dpi=160)
    plt.close(fig)
    paths.append(path)

    mutation_keys = (
        "mutation_score_mean",
        "mutation_coverage_rate_mean",
        "mutation_sampled_kill_rate_mean",
        "mutation_attempted_mean",
        "mutation_killed_mean",
        "mutation_not_covered_frac",
        "mutation_covered_not_killed_frac",
        "mutation_timeout_kill_frac",
        "mutation_statistical_kill_frac",
    )
    if any(
        row.get(key) is not None for row in step_rows for key in mutation_keys
    ):
        fig, axes = plt.subplots(2, 2, figsize=(12, 8), constrained_layout=True)
        mutation_plots = (
            (
                "Mutation reward and rates",
                (
                    "mutation_score_mean",
                    "mutation_coverage_rate_mean",
                    "mutation_sampled_kill_rate_mean",
                ),
            ),
            ("Mutants per rollout", ("mutation_attempted_mean", "mutation_killed_mean")),
            (
                "Coverage outcomes",
                ("mutation_not_covered_frac", "mutation_covered_not_killed_frac"),
            ),
            (
                "Kill mechanism",
                ("mutation_timeout_kill_frac", "mutation_statistical_kill_frac"),
            ),
        )
        for axis, (title, keys) in zip(axes.flat, mutation_plots, strict=True):
            for key in keys:
                values = [row.get(key) for row in step_rows]
                if any(value is not None for value in values):
                    axis.plot(steps, values, label=key.removeprefix("mutation_"))
            axis.set_title(title)
            axis.set_xlabel("Optimizer step")
            if axis.lines:
                axis.legend()
            axis.grid(alpha=0.2)
        path = out_dir / "mutation_progress.png"
        fig.savefig(path, dpi=160)
        plt.close(fig)
        paths.append(path)

    if component_rows:
        fig, axis = plt.subplots(figsize=(11, 5), constrained_layout=True)
        component_steps = [row.get("step") for row in component_rows]
        for key in component_rows[0]:
            if key.endswith("_mean") and not key.startswith("weighted_") and key != "step":
                values = [row.get(key) for row in component_rows]
                if any(value is not None for value in values):
                    axis.plot(component_steps, values, label=key.removesuffix("_mean"))
        axis.set_title("Unweighted reward components")
        axis.set_xlabel("Optimizer step")
        axis.set_ylabel("Mean component reward")
        axis.set_ylim(-0.05, 1.05)
        axis.legend(ncol=2)
        axis.grid(alpha=0.2)
        path = out_dir / "component_progress.png"
        fig.savefig(path, dpi=160)
        plt.close(fig)
        paths.append(path)

    fig, axes = plt.subplots(1, 2, figsize=(11, 4), constrained_layout=True)
    rewards = [row.get("reward") for row in step_rows if row.get("reward") is not None]
    axes[0].hist(rewards, bins=min(20, max(1, len(rewards))))
    axes[0].set_title("Reward distribution")
    axes[0].set_xlabel("Mean group reward")
    axes[0].set_ylabel("Steps")
    axes[1].scatter(
        [row.get("reward") for row in step_rows],
        [row.get("reward_std") for row in step_rows],
        c=[row.get("zero_std_frac") or 0.0 for row in step_rows],
    )
    axes[1].set_title("Reward diversity")
    axes[1].set_xlabel("Mean group reward")
    axes[1].set_ylabel("Within-group reward standard deviation")
    path = out_dir / "reward_distribution.png"
    fig.savefig(path, dpi=160)
    plt.close(fig)
    paths.append(path)
    return paths


def main() -> None:
    parser = argparse.ArgumentParser(description="Plot scalar GRPO diagnostics.")
    parser.add_argument("--metrics-dir", required=True, type=Path)
    parser.add_argument("--out-dir", type=Path, default=None)
    args = parser.parse_args()
    out_dir = args.out_dir or args.metrics_dir
    step_rows = read_csv_rows(args.metrics_dir / "step_metrics.csv")
    component_rows = read_csv_rows(args.metrics_dir / "reward_components.csv")
    paths = _plot(args.metrics_dir, out_dir)
    summary = diagnostics_summary(step_rows, component_rows)
    summary["plots"] = [path.name for path in paths]
    (out_dir / "diagnostics_summary.json").write_text(
        json.dumps(summary, indent=2) + "\n", encoding="utf-8"
    )
    print(f"wrote {len(paths)} GRPO diagnostic plot(s) to {out_dir}")


if __name__ == "__main__":
    main()
