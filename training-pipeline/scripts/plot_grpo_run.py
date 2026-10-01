#!/usr/bin/env python3
"""Plot a completed GRPO run: reward, the rollout funnel, and per-project compile rates.

Deliberately separate from ``jmh-plot-grpo-metrics``: that one needs the ``viz`` extra inside
the training venv, which is not installed on the clusters (the LRZ run logged
"diagnostic plotting failed" for exactly that reason). This runs anywhere the metrics files
have been copied to.

  uv run --extra viz python scripts/plot_grpo_run.py outputs/<run>/metrics --out-dir <dir>
"""

from __future__ import annotations

import argparse
import csv
import json
from collections import Counter
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt  # noqa: E402

BLUE, GREY, GREEN, ORANGE = "#2563eb", "#94a3b8", "#059669", "#ea580c"


def _f(v: str | None) -> float | None:
    try:
        return float((v or "").strip())
    except ValueError:
        return None


def _rolling(vals: list[float], window: int) -> list[float]:
    out, acc = [], []
    for v in vals:
        acc.append(v)
        if len(acc) > window:
            acc.pop(0)
        out.append(sum(acc) / len(acc))
    return out


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("metrics", type=Path)
    ap.add_argument("--out-dir", type=Path, required=True)
    ap.add_argument("--title", default="Gemma 4 E2B — GRPO (70% mutation reward)")
    args = ap.parse_args()
    args.out_dir.mkdir(parents=True, exist_ok=True)

    steps = list(csv.DictReader((args.metrics / "step_metrics.csv").open()))
    rollouts = [
        json.loads(line)
        for line in (args.metrics / "rollouts.jsonl").read_text(errors="replace").splitlines()
        if line.strip()
    ]
    x = list(range(1, len(steps) + 1))

    # 1. Reward per step + rolling mean. The raw series is dominated by all-zero groups, so the
    #    rolling mean is what carries any trend.
    reward = [(_f(r.get("reward")) or 0.0) for r in steps]
    fig, ax = plt.subplots(figsize=(9, 3.6))
    ax.plot(x, reward, lw=0.6, color=GREY, label="reward (per step)")
    ax.plot(x, _rolling(reward, 50), lw=2.0, color=BLUE, label="rolling mean (50)")
    ax.set_xlabel("optimiser step")
    ax.set_ylabel("mean group reward")
    ax.set_title(f"{args.title} — reward")
    ax.legend(frameon=False, fontsize=8)
    ax.spines[["top", "right"]].set_visible(False)
    fig.tight_layout()
    fig.savefig(args.out_dir / "reward.png", dpi=160)
    plt.close(fig)

    # 2. Pipeline fractions. compile_frac is the binding constraint, so show it against the
    #    mutation terms it gates.
    fig, ax = plt.subplots(figsize=(9, 3.6))
    for key, colour, label in (
        ("compile_frac", BLUE, "compiled"),
        ("run_frac", GREEN, "ran"),
        ("mutation_eligible_frac", ORANGE, "mutation-eligible"),
    ):
        series = [(_f(r.get(key)) or 0.0) for r in steps]
        ax.plot(x, _rolling(series, 50), lw=1.8, color=colour, label=label)
    ax.set_ylim(0, 1)
    ax.set_xlabel("optimiser step")
    ax.set_ylabel("fraction of rollouts (rolling mean, 50)")
    ax.set_title(f"{args.title} — pipeline fractions")
    ax.legend(frameon=False, fontsize=8)
    ax.spines[["top", "right"]].set_visible(False)
    fig.tight_layout()
    fig.savefig(args.out_dir / "fractions.png", dpi=160)
    plt.close(fig)

    # 3. Funnel: where rollouts are lost.
    parsed = [r for r in rollouts if r.get("parse_ok")]
    compiled = [r for r in parsed if r.get("compiled")]
    ran = [r for r in compiled if r.get("ran")]
    scored = [
        r
        for r in ran
        if isinstance(r.get("mutation"), dict) and r["mutation"].get("score") is not None
    ]
    killed = [r for r in scored if (r["mutation"].get("killed") or 0) > 0]
    names = ["generated", "parsed", "compiled", "ran", "mutation\nscored", "mutation\nkilled"]
    counts = [len(rollouts), len(parsed), len(compiled), len(ran), len(scored), len(killed)]
    fig, ax = plt.subplots(figsize=(7.5, 3.6))
    bars = ax.bar(names, counts, color=[GREY, GREY, BLUE, GREEN, ORANGE, "#dc2626"])
    for bar, c in zip(bars, counts, strict=True):
        ax.text(
            bar.get_x() + bar.get_width() / 2,
            c,
            f"{c}\n{100 * c / max(counts[0], 1):.0f}%",
            ha="center",
            va="bottom",
            fontsize=8,
        )
    ax.set_ylabel("rollouts")
    ax.set_ylim(0, max(counts) * 1.22)
    ax.set_title(f"{args.title} — rollout funnel (n={len(rollouts)})")
    ax.spines[["top", "right"]].set_visible(False)
    fig.tight_layout()
    fig.savefig(args.out_dir / "funnel.png", dpi=160)
    plt.close(fig)

    # 4. Per-project compile rate -- the spread is the actionable part.
    per = Counter(r.get("project", "?") for r in rollouts)
    ok = Counter(r.get("project", "?") for r in compiled)
    rows = sorted(((p, ok.get(p, 0) / n, n) for p, n in per.items() if n >= 20), key=lambda t: t[1])
    fig, ax = plt.subplots(figsize=(8, max(4.0, 0.22 * len(rows))))
    ax.barh(
        [r[0] for r in rows],
        [r[1] for r in rows],
        color=[BLUE if r[1] >= 0.23 else "#dc2626" for r in rows],
    )
    ax.axvline(
        len(compiled) / max(len(rollouts), 1),
        color="black",
        ls="--",
        lw=1,
        label=f"overall {100 * len(compiled) / max(len(rollouts), 1):.0f}%",
    )
    ax.set_xlabel("compile rate")
    ax.set_title(f"{args.title} — compile rate by project")
    ax.tick_params(axis="y", labelsize=7)
    ax.legend(frameon=False, fontsize=8)
    ax.spines[["top", "right"]].set_visible(False)
    fig.tight_layout()
    fig.savefig(args.out_dir / "compile_by_project.png", dpi=160)
    plt.close(fig)

    print(f"wrote 4 charts to {args.out_dir}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
