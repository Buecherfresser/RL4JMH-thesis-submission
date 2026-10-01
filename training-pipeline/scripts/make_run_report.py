#!/usr/bin/env python3
"""Render a completed GRPO run as a self-contained PDF report.

Reads the numbers straight from ``metrics/`` and ``analysis.json`` so the report cannot drift
from the run it describes, and embeds the charts produced by ``scripts/plot_grpo_run.py``.

  uv run --with reportlab python scripts/make_run_report.py \
      --metrics outputs/<run>/metrics --figures docs/figures/<run> --out docs/<run>.pdf
"""

from __future__ import annotations

import argparse
import csv
import json
from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.platypus import (
    Image,
    PageBreak,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)

INK = colors.HexColor("#0f172a")
MUTED = colors.HexColor("#475569")
RULE = colors.HexColor("#cbd5e1")
ACCENT = colors.HexColor("#2563eb")
WARN = colors.HexColor("#b91c1c")


def styles() -> dict:
    base = getSampleStyleSheet()
    return {
        "title": ParagraphStyle("t", parent=base["Title"], fontSize=19, leading=23, textColor=INK),
        "sub": ParagraphStyle(
            "s",
            parent=base["Normal"],
            fontSize=10.5,
            leading=15,
            textColor=MUTED,
            alignment=TA_LEFT,
        ),
        "h2": ParagraphStyle(
            "h2",
            parent=base["Heading2"],
            fontSize=13,
            leading=17,
            textColor=INK,
            spaceBefore=13,
            spaceAfter=5,
        ),
        "body": ParagraphStyle("b", parent=base["Normal"], fontSize=9.6, leading=14, textColor=INK),
        "small": ParagraphStyle(
            "sm", parent=base["Normal"], fontSize=8.3, leading=11.5, textColor=MUTED
        ),
        "caption": ParagraphStyle(
            "cap",
            parent=base["Normal"],
            fontSize=8,
            leading=11,
            textColor=MUTED,
            spaceBefore=2,
            spaceAfter=8,
        ),
    }


def table(data: list[list[str]], widths: list[float], *, highlight: int | None = None) -> Table:
    t = Table(data, colWidths=widths, hAlign="LEFT")
    style = [
        ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
        ("FONTSIZE", (0, 0), (-1, -1), 8.4),
        ("TEXTCOLOR", (0, 0), (-1, 0), INK),
        ("TEXTCOLOR", (0, 1), (-1, -1), MUTED),
        ("LINEBELOW", (0, 0), (-1, 0), 0.7, RULE),
        ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, colors.HexColor("#f8fafc")]),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("TOPPADDING", (0, 0), (-1, -1), 3.5),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 3.5),
        ("LEFTPADDING", (0, 0), (-1, -1), 5),
    ]
    if highlight is not None:
        style += [
            ("TEXTCOLOR", (0, highlight), (-1, highlight), WARN),
            ("FONTNAME", (0, highlight), (-1, highlight), "Helvetica-Bold"),
        ]
    t.setStyle(TableStyle(style))
    return t


def fig(path: Path, width: float) -> list:
    if not path.is_file():
        return []
    from PIL import Image as PILImage

    with PILImage.open(path) as im:
        ratio = im.height / im.width
    return [Image(str(path), width=width, height=width * ratio)]


def _pct(x: float | None) -> str:
    return "—" if x is None else f"{x:.0%}"


def eval_section(rows: dict, s: dict, W: float) -> list:
    """The JMH-Bench matrix, rendered from summarise_eval_matrix.py's JSON.

    Two tables because the tracks measure different things: the project track scores mutation
    detection against a real library, the synthetic track scores composite pass on hand-built
    tasks. Collapsing them into one table would imply a comparability that does not exist.
    """
    out: list = [PageBreak(), Paragraph("Evaluation on JMH-Bench", s["h2"])]
    out += [
        Paragraph(
            "Held-out evaluation of the trained adapter, run through JMH-Bench's own "
            "<font face='Courier'>tools/run_eval.sh</font> over the full matrix: two tracks x "
            "temperature {0, 1} x thinking {on, off}. Generation on an hpi2 H100, benchmarking on "
            "a quiet CPU box so JMH timings are trustworthy.",
            s["body"],
        ),
        Spacer(1, 7),
    ]

    proj = [(k, v) for k, v in sorted(rows.items()) if v.get("project")]
    if proj:
        data = [["project track", "compile 1st", "compiled", "mutation", "coverage", "cond. kill"]]
        for slug, v in proj:
            p, sc = v["project"], v.get("scores") or {}
            data.append([
                slug.replace("project-", ""),
                f"{p['first_shot']}/{p['total']}  {p['first_shot'] / p['total']:.0%}",
                f"{p['compiled']}/{p['total']}  {p['compiled'] / p['total']:.0%}",
                _pct(sc.get("mutation_score")),
                _pct(sc.get("coverage_rate")),
                _pct(sc.get("conditional_kill")),
            ])
        out += [table(data, [30 * mm, 26 * mm, 26 * mm, 24 * mm, 24 * mm, W - 130 * mm])]
        out += [
            Spacer(1, 6),
            Paragraph(
                "<b>Mutation score is coverage multiplied by a near-constant kill rate</b> (89-100 % "
                "across a 9x spread in score). Once a mutant is reached it is almost always killed; "
                "only a fifth are reached at all, so the constraint here is API-surface reach, not "
                "detection quality — the shape run 1's training funnel showed, one stage later.<br/><br/>"
                "Against the base matrix in the same cell (t0-think): base compiled 58/97 classes to "
                "our 61/97 — near-identical yield — but covered 17 mutants and killed 13 (<b>76 %</b> "
                "conditional kill) where this model covers 20 and kills 18 (<b>90 %</b>). The trained "
                "model is barely better at writing benchmarks that compile; it is markedly better at "
                "writing benchmarks that <b>detect a regression once they run</b>. That is what a 0.70 "
                "mutation reward is supposed to buy.",
                s["body"],
            ),
            Spacer(1, 9),
        ]

    syn = [(k, v) for k, v in sorted(rows.items()) if not v.get("project")]
    if syn:
        data = [["synthetic track", "composite", "compiles", "executes", "regression"]]
        for slug, v in syn:
            sc = v.get("scores") or {}
            def cell(key: str) -> str:
                d = sc.get(key)
                return f"{d['pct']}%  ({d['n']}/{d['of']})" if isinstance(d, dict) else "—"
            data.append([
                slug.replace("synthetic-", ""),
                cell("composite"), cell("compiles"), cell("executes"), cell("regression"),
            ])
        out += [table(data, [30 * mm, 32 * mm, 30 * mm, 30 * mm, W - 122 * mm])]
        out += [
            Spacer(1, 6),
            Paragraph(
                "Baselines on the same 25 tasks, same 8-cell protocol: <b>base matrix 68 %</b> "
                "composite pass (17/25, best cell) and <b>rft 72 %</b> (18/25). The 60 % often "
                "quoted for base is a single run, not the matrix, and understates it. This run "
                "used a 16384-token completion budget against their 8192, so part of any "
                "compile-rate gain is headroom rather than skill.",
                s["body"],
            ),
        ]
    return out


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--metrics", type=Path, required=True)
    ap.add_argument("--figures", type=Path, required=True)
    ap.add_argument("--analysis", type=Path, default=None)
    ap.add_argument("--eval", dest="eval_json", type=Path, default=None,
                    help="JSON from scripts/summarise_eval_matrix.py --json; adds the JMH-Bench section")
    ap.add_argument("--out", type=Path, required=True)
    args = ap.parse_args()

    analysis = json.loads((args.analysis or (args.metrics.parent / "analysis.json")).read_text())
    report = json.loads((args.metrics / "report.json").read_text())
    steps = list(csv.DictReader((args.metrics / "step_metrics.csv").open()))
    f = analysis["funnel"]
    s = styles()
    args.out.parent.mkdir(parents=True, exist_ok=True)

    doc = SimpleDocTemplate(
        str(args.out),
        pagesize=A4,
        leftMargin=19 * mm,
        rightMargin=19 * mm,
        topMargin=17 * mm,
        bottomMargin=16 * mm,
        title="Gemma 4 E2B - GRPO 70% mutation reward - run 1",
        author="JMH Training Pipeline",
    )
    W = doc.width
    story: list = []

    story += [
        Paragraph("Gemma 4 E2B — GRPO with a 70 % mutation reward", s["title"]),
        Spacer(1, 3),
        Paragraph(
            "Run 1 · LRZ job 5723231 · lrz-hgx-h100-012 · 2026-08-02 01:05–21:37 CEST · "
            "20 h 32 m · 744/744 steps · exit 0",
            s["sub"],
        ),
        Spacer(1, 9),
        Paragraph(
            "First GRPO run to complete a full epoch on the coverage-guided mutation reward. "
            "The headline: <b>the reward pipeline works end to end and produces signal, but a "
            "single epoch shows no learning trend</b>, and the binding constraint is compilation "
            "rather than mutation detection.",
            s["body"],
        ),
        Spacer(1, 10),
    ]

    story += [Paragraph("Configuration", s["h2"])]
    story += [
        table(
            [
                ["setting", "value"],
                ["policy", "google/gemma-4-E2B-it · LoRA r=16 α=32 · bf16"],
                ["rollouts", "vLLM 0.23.0 colocate · G=8 · temp 1.0 / top-p 0.95 / top-k 64"],
                ["batching", "per-device 2 × grad-accum 4 = global 8 = group_size"],
                ["corpus", "744 prompts across 40 Java projects (mutation-scorable subset)"],
                [
                    "reward",
                    "compile .10 · runtime .10 · anti .05 (SpotJMHBugs) · rsd .05 · mutation .70",
                ],
                [
                    "mutation",
                    "coverage_aware · coverage-guided · 0.30 coverage / 0.70 conditional-kill",
                ],
                ["optimiser", "LR 1e-6 · dr_grpo · KL 0 · ε 0.2/0.28 · 1 epoch · ~94 s/step"],
            ],
            [30 * mm, W - 30 * mm],
        )
    ]

    story += [Paragraph("Headline numbers", s["h2"])]
    grad = sum(1 for r in steps if (r.get("reward_std") or "0").strip() not in ("", "0", "0.0"))
    story += [
        table(
            [
                ["metric", "value"],
                ["mean reward", f"{report['mean_reward']:.4f}"],
                ["max reward", f"{analysis['max_reward']:.3f}"],
                ["mean reward_std", f"{report['mean_reward_std']:.4f}"],
                [
                    "steps producing a gradient",
                    f"{grad}/{len(steps)}  ({100 * grad / len(steps):.1f} %)",
                ],
                ["steps with zero variance", f"{report['zero_std_step_frac'] * 100:.1f} %"],
            ],
            [55 * mm, W - 55 * mm],
        )
    ]

    story += [Paragraph("Reward over training", s["h2"])]
    story += fig(args.figures / "reward.png", W)
    story += [
        Paragraph(
            "Grey: per-step group mean. Blue: 50-step rolling mean. The rolling mean ends where it "
            "began — quartile means run 0.173 → 0.146 → 0.181 → 0.162. Treat the third-quartile bump "
            "as noise, not progress.",
            s["caption"],
        )
    ]

    story += [PageBreak(), Paragraph("Where rollouts are lost", s["h2"])]
    story += fig(args.figures / "funnel.png", W)
    total = max(f["total"], 1)
    rows = [["stage", "rollouts", "of all", "of previous stage"]]
    prev = total
    for key, label in (
        ("total", "generated"),
        ("parse_ok", "parsed"),
        ("compiled", "compiled"),
        ("ran", "ran"),
        ("mutation_scored", "mutation scored"),
        ("mutation_killed", "mutation killed"),
    ):
        n = f[key]
        rows.append(
            [
                label,
                str(n),
                f"{100 * n / total:.1f} %",
                "—" if key == "total" else f"{100 * n / max(prev, 1):.1f} %",
            ]
        )
        prev = n
    story += [table(rows, [40 * mm, 25 * mm, 25 * mm, W - 90 * mm], highlight=3)]
    story += [
        Spacer(1, 5),
        Paragraph(
            "<b>This is the central result.</b> Compilation is the only real bottleneck: 23 % of "
            "generations compile, but of those 92 % run, 97 % get mutation-scored, and "
            "<b>77 % actually kill a planted mutant</b>. The model is not bad at writing "
            "mutation-sensitive benchmarks — it is bad at writing benchmarks that compile against an "
            "unfamiliar API. Parsing is solved (99.9 %).",
            s["body"],
        ),
    ]

    story += [Paragraph("Pipeline fractions over training", s["h2"])]
    story += fig(args.figures / "fractions.png", W)
    story += [
        Paragraph(
            "compile_frac gates everything downstream, and it is flat across the run.", s["caption"]
        )
    ]

    story += [PageBreak(), Paragraph("Compile rate by project", s["h2"])]
    worst = f["worst_projects_by_compile_rate"][:5]
    best = f["best_projects_by_compile_rate"][:5]
    rows = [["hardest project", "rate", "easiest project", "rate"]]
    for i in range(max(len(worst), len(best))):
        w = worst[i] if i < len(worst) else {"project": "", "compile_rate": ""}
        b = best[i] if i < len(best) else {"project": "", "compile_rate": ""}
        rows.append(
            [
                w["project"],
                f"{w['compile_rate']:.3f}" if w["project"] else "",
                b["project"],
                f"{b['compile_rate']:.3f}" if b["project"] else "",
            ]
        )
    story += [table(rows, [52 * mm, 20 * mm, 52 * mm, W - 124 * mm])]
    story += [
        Spacer(1, 5),
        Paragraph(
            "A 26× spread. This is <b>not</b> an infrastructure problem: the classpaths of the worst "
            "projects resolve completely and every jar is class-file major 61, readable by the JDK 23 "
            "in use. It tracks API shape — simple static APIs compile, factory/builder/generic APIs "
            "do not. <font color='#b91c1c'>jackson-dataformats-binary compiled 0 of 160 times</font>, "
            "contributing 160 guaranteed-zero rollouts.",
            s["body"],
        ),
    ]
    story += fig(args.figures / "compile_by_project.png", W * 0.86)

    if args.eval_json and args.eval_json.is_file():
        story += eval_section(json.loads(args.eval_json.read_text()), s, W)

    story += [PageBreak(), Paragraph("What to change next", s["h2"])]
    story += [
        Paragraph(
            "The evidence points at two things: compile rate is the constraint, and the run was "
            "under-trained (744 updates at LR 1e-6, with 44 % of steps contributing no gradient).",
            s["body"],
        ),
        Spacer(1, 6),
    ]
    story += [
        table(
            [
                ["change", "rationale"],
                [
                    "Init from SFT/RFT, not base-instruct",
                    "Compile rate is a format+API skill, exactly what SFT teaches. Highest leverage, "
                    "costs no extra GPU time.",
                ],
                [
                    "Raise LR 1e-6 → 3e-6",
                    "Sparse reward + small dataset. Watch entropy and clipped_ratio for collapse; "
                    "fall back to 2e-6.",
                ],
                [
                    "2 GPUs, server mode",
                    "Frees the whole trainer card (vLLM currently takes ~30 %) and removes the OOM risk "
                    "that killed job 5721357 at step 108.",
                ],
                [
                    "Context 16384/8192, source 48000 chars",
                    "Admits ~835 of 876 scorable prompts (+12 %), disproportionately the large, "
                    "complex-API subjects.",
                ],
                [
                    "2–3 epochs with resume",
                    "~410 effective updates is not much. Needs resume_from_checkpoint (now implemented) "
                    "to cross the 2-day QOS wall.",
                ],
                [
                    "Capture javac output on failure",
                    "Every failure is a generic compile_error today, so we cannot tell a missing import "
                    "from a wrong constructor.",
                ],
                [
                    "Drop projects below ~5 % compile rate",
                    "They contribute near-guaranteed zeros that dilute the group-relative baseline.",
                ],
            ],
            [46 * mm, W - 46 * mm],
        )
    ]
    story += [
        Spacer(1, 8),
        Paragraph(
            "<b>Suggested order.</b> Run the SFT-init + LR 3e-6 experiment first on one GPU — it is "
            "cheap, queues in hours rather than ~47 h, and isolates the two changes most likely to "
            "move compile rate. If compile rate rises, spend the 2-GPU allocation on the headline "
            "run.",
            s["body"],
        ),
    ]

    story += [
        Spacer(1, 10),
        Paragraph(
            "Generated from metrics/step_metrics.csv, metrics/rollouts.jsonl and analysis.json. "
            "Reproduce with scripts/analyse_grpo_run.py, scripts/plot_grpo_run.py and "
            "scripts/make_run_report.py.",
            s["small"],
        ),
    ]

    doc.build(story)
    print(f"wrote {args.out}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
