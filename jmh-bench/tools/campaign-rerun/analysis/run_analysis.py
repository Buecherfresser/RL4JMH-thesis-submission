#!/usr/bin/env python3
"""Build every table and figure for the campaign rerun.

Re-runnable: reads whatever cells are archived and regenerates all outputs, so
run it again as the remaining cells land.

  uv run --with numpy --with matplotlib python run_analysis.py [--root DIR] [--out DIR]
"""
from __future__ import annotations

import argparse
import csv
import json
import sys
from pathlib import Path

import numpy as np

sys.path.insert(0, str(Path(__file__).parent))
from metrics import (MODEL_LABEL, MODELS, PROJECTS, Cell, bug_size, detections,
                     load_cells, rciw, rciw_curve, wilson)

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

# Categorical slots 1-3 of the validated palette. Validated for this chart set
# with `validate_palette.js --pairs all --mode light`: all checks pass; the aqua
# carries a sub-3:1 contrast WARN, whose required relief is the legend + direct
# labels on every figure and the CSV/LaTeX table view shipped beside them.
COLOR = {"dsv4": "#2a78d6", "gemma-run9-ck1600": "#eb6834", "gpt-oss-120b": "#1baf7a"}
INK, INK2, GRID = "#0b0b0b", "#52514e", "#d9d8d4"
DELTAS = [0.01, 0.05, 0.10, 0.50, 0.90]

plt.rcParams.update({
    "figure.dpi": 140, "savefig.dpi": 300, "savefig.bbox": "tight",
    "font.size": 9, "axes.titlesize": 10, "axes.labelsize": 9,
    "axes.edgecolor": GRID, "axes.linewidth": 0.8, "axes.labelcolor": INK2,
    "text.color": INK, "xtick.color": INK2, "ytick.color": INK2,
    "xtick.labelsize": 8, "ytick.labelsize": 8,
    "grid.color": GRID, "grid.linewidth": 0.6,
    "legend.frameon": False, "legend.fontsize": 8,
    "figure.facecolor": "white", "axes.facecolor": "white",
})


def style(ax, xgrid=False):
    ax.grid(axis="x" if xgrid else "y", alpha=0.7, zorder=0)
    ax.set_axisbelow(True)
    for s in ("top", "right"):
        ax.spines[s].set_visible(False)


def save(fig, out: Path, name: str):
    for ext in ("pdf", "png"):
        fig.savefig(out / f"{name}.{ext}")
    plt.close(fig)
    print(f"  figure  {name}.pdf/.png")


# ------------------------------------------------------------------ computation

def compute(cells: list[Cell], iters: int) -> dict:
    rng = np.random.default_rng(20260827)
    per_cell, bugs, rciws, curves = [], [], [], []
    per_model_curves, curve_cap = {}, 80   # even sampling across models

    for c in cells:
        r = c.result
        n = r.get("mutant_count") or 0
        killed, covered = r.get("mutants_killed") or 0, r.get("mutants_covered") or 0
        lo, hi = wilson(killed, n)

        # delta sweep: a mutant counts as killed at delta when some covering
        # benchmark shows >= (1+delta) slowdown at p < 0.05
        dsweep = {}
        for d in DELTAS:
            k = sum(1 for m in (r.get("mutant_results") or [])
                    if any((det.get("effect_size") or 0) >= 1 + d
                           and (det.get("p_value") is not None and det["p_value"] < 0.05)
                           for det in (m.get("detections") or [])))
            dsweep[d] = k / n if n else 0.0

        cell_bugs_flat, cell_bugs_hier, cell_rciw = [], [], []
        seen_bench = set()
        for m, det in detections(r):
            bf, mf = det["base_samples_by_fork"], det["mutant_samples_by_fork"]
            bs = bug_size(bf, mf, "flat", rng, iters)
            bh = bug_size(bf, mf, "hierarchical", rng, iters)
            if bs is not None:
                cell_bugs_flat.append(bs)
                bugs.append({"model": c.model, "project": c.project,
                             "mutant": m.get("id"), "benchmark": det.get("benchmark"),
                             "bug_size_flat": bs, "bug_size_hier": bh,
                             "effect_size": det.get("effect_size"),
                             "p_value": det.get("p_value"),
                             "killed": m.get("status") == "killed"})
            if bh is not None:
                cell_bugs_hier.append(bh)
            key = det.get("benchmark")
            if key not in seen_bench:
                seen_bench.add(key)
                flat = np.asarray([x for f in bf for x in f], dtype=float)
                v = rciw(flat, rng, iters)
                if v is not None:
                    cell_rciw.append(v)
                    rciws.append({"model": c.model, "project": c.project,
                                  "benchmark": key, "rciw": v})
                # Two curves, because they answer different questions.
                # `within` uses one fork, matching LLM4JMH's rawData[-1]: it is
                # the honest "does precision improve with iterations" curve.
                # `pooled` walks the concatenated forks and is NOT a precision
                # curve -- it steps up every time the prefix crosses a fork
                # boundary and picks up between-fork variance. That artefact is
                # the point: it is B4 made visible.
                per_model_curves.setdefault(c.model, 0)
                if per_model_curves[c.model] < curve_cap:
                    per_model_curves[c.model] += 1
                    cheap = max(500, iters // 20)
                    within = rciw_curve(np.asarray(bf[-1], dtype=float), rng, cheap)
                    pooled = rciw_curve(flat, rng, cheap)
                    if within or pooled:
                        curves.append({"model": c.model, "within": within,
                                       "pooled": pooled})

        bl_fail = r.get("baseline_failures") or []
        per_cell.append({
            "model": c.model, "project": c.project,
            "classes_total": r.get("classes_total"), "classes_compiled": r.get("classes_compiled"),
            "runtime_filtered": r.get("classes_runtime_filtered"),
            "n_benchmarks": r.get("n_benchmarks"),
            "mutants_total": n, "mutants_covered": covered, "mutants_killed": killed,
            "mutants_errored": r.get("mutants_errored") or 0,
            "killed_by_timeout": r.get("mutants_killed_by_timeout") or 0,
            "mutation_score": r.get("mutation_score") or 0,
            "score_low95": lo, "score_high95": hi,
            "coverage_rate": r.get("coverage_rate") or 0,
            "baseline_rsd_pct": r.get("stability_rsd_percent"),
            "baseline_failures": len(bl_fail),
            "runtime_errors": sum(1 for b in bl_fail if not b.get("timed_out")),
            "bugsize_flat_median": float(np.median(cell_bugs_flat)) if cell_bugs_flat else None,
            "bugsize_hier_median": float(np.median(cell_bugs_hier)) if cell_bugs_hier else None,
            "rciw_median": float(np.median(cell_rciw)) if cell_rciw else None,
            "duration_h": round((r.get("duration_seconds") or 0) / 3600, 2),
            **{f"delta_{int(d*100)}pct": v for d, v in dsweep.items()},
        })
        print(f"  {c.key:44s} {len(cell_bugs_flat):4} pairs  "
              f"bug={per_cell[-1]['bugsize_flat_median'] or float('nan'):.3f}  "
              f"rciw={per_cell[-1]['rciw_median'] or float('nan'):.4f}")

    return {"per_cell": per_cell, "bugs": bugs, "rciw": rciws, "curves": curves}


# ----------------------------------------------------------------------- output

def write_csv(rows, path: Path, cols=None):
    if not rows:
        return
    cols = cols or list(rows[0])
    with open(path, "w", newline="") as fh:
        w = csv.DictWriter(fh, fieldnames=cols, extrasaction="ignore")
        w.writeheader()
        w.writerows(rows)
    print(f"  table   {path.name}")


def tex_escape(s):
    return str(s).replace("_", r"\_").replace("%", r"\%")


def write_tex(path: Path, caption, label, header, rows, align):
    with open(path, "w") as fh:
        fh.write("% generated by tools/campaign-rerun/analysis/run_analysis.py\n")
        fh.write("\\begin{table}[t]\n\\centering\\small\n")
        fh.write(f"\\caption{{{caption}}}\n\\label{{{label}}}\n")
        fh.write(f"\\begin{{tabular}}{{{align}}}\n\\toprule\n")
        fh.write(" & ".join(header) + " \\\\\n\\midrule\n")
        for r in rows:
            fh.write(" & ".join(str(x) for x in r) + " \\\\\n")
        fh.write("\\bottomrule\n\\end{tabular}\n\\end{table}\n")
    print(f"  table   {path.name}")


def fmt(v, n=3, dash="--"):
    return dash if v is None or (isinstance(v, float) and np.isnan(v)) else f"{v:.{n}f}"


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--root", default="/Volumes/SamsungSSD/jmh-thesis/campaign-rerun-2026-08")
    ap.add_argument("--out", default="/Volumes/SamsungSSD/jmh-thesis/analysis")
    ap.add_argument("--iters", type=int, default=10000)
    a = ap.parse_args()

    out = Path(a.out); (out / "figures").mkdir(parents=True, exist_ok=True)
    (out / "tables").mkdir(parents=True, exist_ok=True)
    cells = load_cells(Path(a.root))
    if not cells:
        print("no cells found", file=sys.stderr); return 1
    print(f"{len(cells)} cells\n")
    D = compute(cells, a.iters)
    pc, bugs, rc, curves = D["per_cell"], D["bugs"], D["rciw"], D["curves"]

    print("\nwriting tables")
    write_csv(pc, out / "tables" / "per_cell_metrics.csv")
    write_csv(bugs, out / "tables" / "bug_sizes.csv")
    write_csv(rc, out / "tables" / "rciw.csv")

    # T1 per-cell summary
    rows = []
    for m in MODELS:
        for p in PROJECTS:
            r = next((x for x in pc if x["model"] == m and x["project"] == p), None)
            if not r:
                continue
            rows.append([tex_escape(MODEL_LABEL[m]), tex_escape(p), r["n_benchmarks"],
                         f'{r["mutants_covered"]}/{r["mutants_total"]}', r["mutants_killed"],
                         f'{r["mutation_score"]:.3f}',
                         f'[{r["score_low95"]:.3f}, {r["score_high95"]:.3f}]',
                         fmt(r["baseline_rsd_pct"], 3)])
    write_tex(out / "tables" / "tab_rerun_summary.tex",
              "Campaign rerun: per-cell mutation results. Wilson 95\\% intervals in brackets. "
              "RSD is the median per-benchmark relative standard deviation of the baseline.",
              "tab:rerun_summary",
              ["Model", "Project", "Bench.", "Cov./Tot.", "Killed", "Score", "95\\% CI", "RSD (\\%)"],
              rows, "llrrrrrr")

    # T2 LLM4JMH metric set, aggregated per model
    rows = []
    for m in MODELS:
        mb = [b["bug_size_flat"] for b in bugs if b["model"] == m]
        mh = [b["bug_size_hier"] for b in bugs if b["model"] == m and b["bug_size_hier"] is not None]
        mr = [x["rciw"] for x in rc if x["model"] == m]
        cs = [x for x in pc if x["model"] == m]
        if not cs:
            continue
        rows.append([tex_escape(MODEL_LABEL[m]), len(cs), sum(c["n_benchmarks"] or 0 for c in cs),
                     len(mb),
                     fmt(np.median(mb)) if mb else "--",
                     f"[{np.percentile(mb,25):.3f}, {np.percentile(mb,75):.3f}]" if mb else "--",
                     fmt(np.median(mh)) if mh else "--",
                     fmt(np.median(mr), 4) if mr else "--",
                     f'{np.mean([c["coverage_rate"] for c in cs]):.3f}',
                     sum(c["runtime_errors"] for c in cs)])
    write_tex(out / "tables" / "tab_llm4jmh_metrics.tex",
              "LLM4JMH metric set on the campaign rerun. Bug size is $1-\\text{upper}$ of a 99\\% "
              "bootstrap CI on $\\text{mean(mutant)}/\\text{mean(base)}$ (10k resamples); "
              "\\emph{flat} pools all samples as LLM4JMH does, \\emph{hier.} resamples forks then "
              "iterations. RCIW is $(U-L)/\\text{mean}$ at 99\\%.",
              "tab:llm4jmh_metrics",
              ["Model", "Cells", "Bench.", "Pairs", "Bug size", "IQR", "Bug (hier.)",
               "RCIW", "Coverage", "Rt. err."],
              rows, "lrrrrrrrrr")

    # T3 delta sweep
    rows = []
    for m in MODELS:
        cs = [x for x in pc if x["model"] == m]
        if not cs:
            continue
        rows.append([tex_escape(MODEL_LABEL[m])] +
                    [f'{np.mean([c[f"delta_{int(d*100)}pct"] for c in cs]):.3f}' for d in DELTAS])
    write_tex(out / "tables" / "tab_rerun_delta_sweep.tex",
              "Mutation score under a degradation-threshold spectrum ($\\delta$), "
              "mean over cells. A mutant counts as detected at $\\delta$ when some covering "
              "benchmark shows $\\geq(1+\\delta)$ slowdown at $p<0.05$.",
              "tab:rerun_delta_sweep",
              ["Model"] + [f"$\\delta={int(d*100)}\\%$" for d in DELTAS], rows, "lrrrrr")

    print("\nwriting figures")
    figs = out / "figures"

    # F1 mutation score, grouped bars
    fig, ax = plt.subplots(figsize=(7.2, 3.2))
    w, xs = 0.26, np.arange(len(PROJECTS))
    for i, m in enumerate(MODELS):
        vals, los, his = [], [], []
        for p in PROJECTS:
            r = next((x for x in pc if x["model"] == m and x["project"] == p), None)
            vals.append(r["mutation_score"] if r else np.nan)
            los.append((r["mutation_score"] - r["score_low95"]) if r else 0)
            his.append((r["score_high95"] - r["mutation_score"]) if r else 0)
            if r is None:   # absent bar would otherwise be read as a score of 0
                ax.text(xs[PROJECTS.index(p)] + (i - 1) * w, 0.015, "pending",
                        rotation=90, ha="center", va="bottom", fontsize=6.5, color=INK2)
        ax.bar(xs + (i - 1) * w, vals, w * 0.88, label=MODEL_LABEL[m],
               color=COLOR[m], zorder=3, linewidth=0)
        ax.errorbar(xs + (i - 1) * w, vals, yerr=[los, his], fmt="none",
                    ecolor=INK2, elinewidth=0.8, capsize=2, zorder=4)
    ax.set_xticks(xs); ax.set_xticklabels(PROJECTS, rotation=12, ha="right")
    ax.set_ylabel("Mutation score"); ax.set_ylim(0, 1.0)
    ax.set_title("Mutation score by project (Wilson 95% CI)", loc="left", color=INK)
    ax.legend(ncol=3, loc="upper left", bbox_to_anchor=(0, 1.22))
    style(ax); save(fig, figs, "fig_mutation_score")

    # F2 bug-size ECDF
    fig, ax = plt.subplots(figsize=(5.4, 3.4))
    for m in MODELS:
        v = np.sort([b["bug_size_flat"] for b in bugs if b["model"] == m])
        if v.size == 0:
            continue
        ax.step(v, np.arange(1, v.size + 1) / v.size, where="post",
                color=COLOR[m], lw=2, label=f"{MODEL_LABEL[m]} (n={v.size})")
    ax.axvline(0, color=INK2, lw=0.8, ls=":")
    ax.annotate("$\\leq 0$: interval still\nadmits no regression", xy=(0, 0.5),
                xytext=(-0.62, 0.62), fontsize=7, color=INK2,
                arrowprops=dict(arrowstyle="->", color=INK2, lw=0.7))
    ax.set_xlabel("Bug size  $1-\\mathrm{upper}_{99\\%}$"); ax.set_ylabel("Cumulative fraction of pairs")
    ax.set_title("Bug size distribution (LLM4JMH estimator)", loc="left", color=INK)
    ax.legend(loc="upper left"); style(ax); save(fig, figs, "fig_bugsize_ecdf")

    # F3 RCIW convergence -- two panels, because concatenated forks are not a
    # precision curve (see the note in compute()).
    fig, axes = plt.subplots(1, 2, figsize=(8.0, 3.3), sharey=True)
    for ax_, kind, ttl in ((axes[0], "within", "Within one fork (LLM4JMH shape)"),
                           (axes[1], "pooled", "Prefix across pooled forks")):
        for m in MODELS:
            cs = [c[kind] for c in curves if c["model"] == m and c.get(kind)]
            if not cs:
                continue
            n = min(len(c) for c in cs)
            arr = np.array([c[:n] for c in cs])
            x = np.arange(2, 2 + n)
            ax_.plot(x, np.median(arr, axis=0), color=COLOR[m], lw=2,
                     label=f"{MODEL_LABEL[m]} (n={len(cs)})")
            ax_.fill_between(x, np.percentile(arr, 25, axis=0),
                             np.percentile(arr, 75, axis=0),
                             color=COLOR[m], alpha=0.13, linewidth=0)
        ax_.set_xlabel("Iterations used"); ax_.set_yscale("log")
        ax_.set_title(ttl, loc="left", color=INK, fontsize=9)
        style(ax_)
    for b in (10, 20, 30, 40):
        axes[1].axvline(b, color=GRID, lw=0.8, ls=":", zorder=1)
    axes[1].text(41, axes[1].get_ylim()[1] * 0.62, "fork\nboundaries",
                 fontsize=6.5, color=INK2, ha="left", va="top")
    axes[0].set_ylabel("RCIW  $(U-L)/\\mathrm{mean}$")
    axes[0].legend(loc="lower left")
    fig.suptitle("Measurement precision vs. iterations (median, IQR band)",
                 x=0.005, y=1.06, ha="left", color=INK, fontsize=10)
    save(fig, figs, "fig_rciw_convergence")

    # F4 flat vs hierarchical bug size
    fig, ax = plt.subplots(figsize=(4.8, 4.4))
    for m in MODELS:
        xs_ = [b["bug_size_flat"] for b in bugs if b["model"] == m and b["bug_size_hier"] is not None]
        ys_ = [b["bug_size_hier"] for b in bugs if b["model"] == m and b["bug_size_hier"] is not None]
        ax.scatter(xs_, ys_, s=13, color=COLOR[m], alpha=0.75,
                   edgecolors="white", linewidths=0.4, label=MODEL_LABEL[m], zorder=3)
    lim = [-0.05, 1.02]
    ax.axvspan(0.2, 0.8, color=GRID, alpha=0.35, zorder=1, linewidth=0)
    ax.plot(lim, lim, color=INK2, lw=0.8, ls="--", zorder=2)
    fa = np.array([b["bug_size_flat"] for b in bugs if b["bug_size_hier"] is not None])
    ha = np.array([b["bug_size_hier"] for b in bugs if b["bug_size_hier"] is not None])
    mid = (fa > 0.2) & (fa < 0.8)
    ax.text(0.5, 1.0, "marginal", ha="center", va="top", fontsize=7, color=INK2)
    ax.set_xlim(lim); ax.set_ylim(lim)
    ax.set_xlabel("Bug size — flat (LLM4JMH)"); ax.set_ylabel("Bug size — hierarchical")
    ax.set_title("Estimators agree except on marginal effects", loc="left", color=INK)
    ax.text(0.03, 0.93,
            f"median |diff| {np.median(np.abs(fa-ha)):.5f}\n"
            f"marginal band (n={int(mid.sum())}): mean {np.mean(fa[mid]-ha[mid]):+.3f}",
            fontsize=7, color=INK2, va="top")
    ax.legend(loc="lower right"); style(ax); save(fig, figs, "fig_bugsize_estimators")

    # F5 delta sweep
    fig, ax = plt.subplots(figsize=(5.4, 3.2))
    for m in MODELS:
        cs = [x for x in pc if x["model"] == m]
        if not cs:
            continue
        ys = [np.mean([c[f"delta_{int(d*100)}pct"] for c in cs]) for d in DELTAS]
        ax.plot([d * 100 for d in DELTAS], ys, marker="o", ms=5, lw=2,
                color=COLOR[m], label=MODEL_LABEL[m])
        ax.annotate(f"{ys[-1]:.2f}", (DELTAS[-1] * 100, ys[-1]), textcoords="offset points",
                    xytext=(6, 0), fontsize=7, color=INK2, va="center")
    ax.set_xscale("log"); ax.set_xticks([1, 5, 10, 50, 90])
    ax.get_xaxis().set_major_formatter(matplotlib.ticker.ScalarFormatter())
    ax.set_xlabel("Degradation threshold $\\delta$ (%)"); ax.set_ylabel("Mutation score")
    # a proportion gets a zero baseline; truncating it exaggerates the gaps
    ax.set_ylim(0, 0.72)
    ax.set_title("Detection vs. degradation threshold", loc="left", color=INK)
    ax.text(1.05, 0.03,
            "curves are near-flat: detected regressions are\nalmost all far above any threshold",
            fontsize=7, color=INK2, va="bottom")
    ax.legend(loc="upper right"); style(ax); save(fig, figs, "fig_delta_sweep")

    json.dump({"cells": len(cells), "detections": len(bugs), "benchmarks_rciw": len(rc)},
              open(out / "analysis_meta.json", "w"), indent=2)
    print(f"\nwrote to {out}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
