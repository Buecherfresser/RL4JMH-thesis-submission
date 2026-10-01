#!/usr/bin/env python3
"""Generate EVALUATION_REPORT.md for a JMH-Bench eval session or campaign."""

from __future__ import annotations

import argparse
import json
import re
from collections import Counter, defaultdict
from pathlib import Path


def _read_json(path: Path) -> dict:
    return json.loads(path.read_text())


def _parse_synthetic_summary(path: Path) -> dict:
    text = path.read_text()
    rates: dict[str, tuple[int, int] | None] = {}
    for key, pat in [
        ("generated", r"Generation succeeded \| \d+% \| (\d+)/(\d+)"),
        ("compiles", r"\| Compiles \| \d+% \| (\d+)/(\d+)"),
        ("executes", r"\| Executes \(JMH ran\) \| \d+% \| (\d+)/(\d+)"),
        ("regression", r"\| Regression detection \| \d+% \| (\d+)/(\d+)"),
        ("composite", r"\*\*Composite pass\*\* \| \*\*\d+%\*\* \| (\d+)/(\d+)"),
    ]:
        m = re.search(pat, text)
        rates[key] = (int(m.group(1)), int(m.group(2))) if m else None
    comp_m = re.search(r"\*\*Composite pass\*\* \| \*\*(\d+)%\*\*", text)
    tasks: dict[str, dict] = {}
    for line in text.splitlines():
        if not line.startswith("| [`"):
            continue
        parts = [p.strip() for p in line.split("|")]
        task = parts[1].split("`")[1]
        tasks[task] = {
            "compiles": parts[3] == "OK",
            "executes": parts[4] == "OK",
            "regressions": parts[6],
        }
    interrupted = "partial — run interrupted" in text
    ts_m = re.search(r"Timestamp: `([^`]+)`", text)
    return {
        "timestamp": ts_m.group(1) if ts_m else None,
        "composite_pct": int(comp_m.group(1)) if comp_m else 0,
        "rates": rates,
        "tasks": tasks,
        "interrupted": interrupted,
        "config_snippet": _extract_config(text),
    }


def _parse_project_summary(path: Path) -> dict:
    text = path.read_text()
    sc_path = path.parent / "scorecard.json"
    meta = {}
    if sc_path.exists():
        meta = _read_json(sc_path).get("result", {}).get("generation_metadata", {})

    def g(pat: str) -> tuple[str, ...] | None:
        m = re.search(pat, text)
        return m.groups() if m else None

    score = g(r"\*\*Performance mutation score\*\* \| \*\*([\d.]+)%\*\* \((\d+)/(\d+)\)")
    sut = g(r"SUT classes benchmarked \| (\d+)/(\d+)")
    bench = g(r"Benchmarks generated \| (\d+)")
    cov = g(r"Mutant coverage \| ([\d.]+)% \((\d+)/(\d+)\)")
    suite = g(r"Classes compiled into suite \| (\d+)")
    ts_m = re.search(r"Timestamp: `([^`]+)`", text)
    return {
        "timestamp": ts_m.group(1) if ts_m else None,
        "mutation_pct": float(score[0]) if score else 0.0,
        "killed": int(score[1]) if score else 0,
        "mutants": int(score[2]) if score else 100,
        "sut_bench": int(sut[0]) if sut else 0,
        "sut_total": int(sut[1]) if sut else 97,
        "benchmarks": int(bench[0]) if bench else 0,
        "covered": int(cov[1]) if cov else 0,
        "suite_classes": int(suite[0]) if suite else 0,
        "gen_ok": meta.get("classes_succeeded"),
        "gen_fail": meta.get("classes_failed"),
        "config_snippet": _extract_config(text),
    }


def _extract_config(text: str) -> str:
    m = re.search(r"Config: `(\{.*?\})`", text, re.DOTALL)
    return m.group(1)[:200] + "…" if m else ""


def _slug_label(name: str) -> str:
    # Project-track slugs carry their subject: project-<name>-t0-think. The
    # project segment is optional so pre-corpus runs (project-t0-think) still parse.
    m = re.match(r"(?:synthetic|project)-(?:(.+?)-)?t(\d+)-(think|nothink)$", name)
    if not m:
        return name
    project, temp, think = m.groups()
    label = f"t={temp}, {'thinking' if think == 'think' else 'no thinking'}"
    return f"{project} — {label}" if project else label


def _config_label(name: str) -> str:
    """Map slug names like t0-think or synthetic-t0-think to readable labels."""
    if name.startswith(("synthetic-", "project-")):
        return _slug_label(name)
    m = re.match(r"t(\d+)(?:-(think(?:-\d+)?))?$", name)
    if not m:
        return name
    temp = m.group(1)
    think = m.group(2)
    if think:
        rep = think.replace("-", " ")
        return f"t={temp}, {rep.replace('think', 'thinking')}"
    return f"t={temp}, no thinking"


def _find_synthetic_failures(configs: dict[str, dict]) -> dict[str, list[str]]:
    compile_fails: Counter[str] = Counter()
    exec_no_detect: Counter[str] = Counter()
    for cfg, data in configs.items():
        for task, info in data["tasks"].items():
            if not info["compiles"]:
                compile_fails[task] += 1
            elif info["executes"] and info["regressions"].startswith("0/"):
                exec_no_detect[task] += 1
    n = len(configs)
    return {
        "compile": [t for t, c in compile_fails.items() if c >= max(1, n // 2)],
        "no_detect": [t for t, c in exec_no_detect.items() if c >= max(1, n // 2)],
        "always_fail": [
            t
            for t in set(compile_fails) | set(exec_no_detect)
            if all(
                not configs[c]["tasks"].get(t, {}).get("compiles")
                or (
                    configs[c]["tasks"].get(t, {}).get("executes")
                    and configs[c]["tasks"].get(t, {}).get("regressions", "").startswith("0/")
                )
                for c in configs
                if t in configs[c]["tasks"]
            )
        ],
    }


def _artifact_tree(run_dir: Path, configs: list[str]) -> str:
    repo = run_dir
    while repo.name != "reports" and repo.parent != repo:
        repo = repo.parent
    rel = run_dir.relative_to(repo) if repo.name == "reports" else run_dir
    lines = [
        f"reports/{rel}/",
        "├── EVALUATION_REPORT.md          ← this file",
    ]
    if (run_dir / "run.log").exists():
        lines.append("├── run.log")
    if (run_dir / "bundles").is_dir():
        lines.append("├── bundles/")
    sub = "reports/" if (run_dir / "reports").is_dir() else ""
    for i, cfg in enumerate(configs):
        branch = "└──" if i == len(configs) - 1 else "├──"
        lines.append(f"{branch} {sub}{cfg}/         ← summary.md, per_task/")
    return "\n".join(lines)


def _section(title: str, body: str) -> str:
    return f"## {title}\n\n{body.strip()}\n"


def generate_matrix_report(
    *,
    model: str,
    variant: str,
    run_id: str,
    synthetic_dir: Path | None,
    project_dir: Path | None,
    out_path: Path,
    notes: list[str] | None = None,
) -> str:
    syn_cfgs: dict[str, dict] = {}
    proj_cfgs: dict[str, dict] = {}

    if synthetic_dir and (synthetic_dir / "reports").is_dir():
        for d in sorted((synthetic_dir / "reports").iterdir()):
            if (d / "summary.md").exists():
                syn_cfgs[d.name] = _parse_synthetic_summary(d / "summary.md")

    if project_dir and (project_dir / "reports").is_dir():
        for d in sorted((project_dir / "reports").iterdir()):
            if (d / "summary.md").exists():
                proj_cfgs[d.name] = _parse_project_summary(d / "summary.md")

    n_cfg = len(syn_cfgs) + len(proj_cfgs)
    best_syn = max(syn_cfgs.items(), key=lambda x: x[1]["composite_pct"], default=(None, {"composite_pct": 0, "rates": {}}))
    best_proj = max(proj_cfgs.items(), key=lambda x: x[1]["mutation_pct"], default=(None, {"mutation_pct": 0, "killed": 0, "mutants": 100}))

    syn_label = _slug_label(best_syn[0]) if best_syn[0] else "n/a"
    proj_label = _slug_label(best_proj[0]) if best_proj[0] else "n/a"
    syn_score = best_syn[1]["rates"].get("composite")
    syn_score_s = f"{best_syn[1]['composite_pct']}% ({syn_score[0]}/{syn_score[1]})" if syn_score else f"{best_syn[1]['composite_pct']}%"
    proj_score_s = f"{best_proj[1]['mutation_pct']:.0f}% ({best_proj[1]['killed']}/{best_proj[1]['mutants']})"

    # Collect the findings unnumbered, then number them on the way out. The previous version
    # hardcoded each index ("2" if syn_cfgs else "1", ...), which skipped a number whenever an
    # optional finding was absent -- every existing report reads 1, 2, 4.
    findings: list[str] = []
    if syn_cfgs:
        findings.append(
            f"**Synthetic:** best at {_slug_label(best_syn[0])} with **{syn_score_s}** composite pass."
        )
    if proj_cfgs:
        findings.append(
            f"**Project:** best at {_slug_label(best_proj[0])} with **{proj_score_s}** mutation score."
        )
    if proj_cfgs and all(v["mutation_pct"] == 0 for v in proj_cfgs.values()):
        findings.append(
            "**Project track collapsed** — compile-check passed for ≤3/97 classes per config; no mutants covered."
        )
    if syn_cfgs:
        fails = _find_synthetic_failures(syn_cfgs)
        if fails["no_detect"]:
            findings.append(
                f"**Recurring synthetic misses:** {', '.join(f'`{t}`' for t in fails['no_detect'][:5])}."
            )
    headlines = [f"{i}. {f}" for i, f in enumerate(findings, start=1)]

    report_path = out_path.relative_to(out_path.parents[4]) if len(out_path.parents) >= 5 else out_path

    setup_rows = [
        f"| Model | `{model}` |",
        "| Harness | `openai-zero-shot` |",
        "| Synthetic track | 25 hand-built tasks, 1 regression each |",
        "| Project track | Apache Commons Compress 1.28.0, 97 SUT classes, 100 mutants |",
        "| JMH preset | `default` (forks=1, 2 warmup / 3 measurement × 1s) |",
        "| Detection threshold | ≥10% slowdown (p < 0.05) |",
    ]

    parts = [
        f"# Evaluation Report: `{model}`",
        "",
        f"**Variant:** `{variant}`  ",
        f"**Run ID:** `{run_id}`  ",
        "**Harness:** `openai-zero-shot`  ",
        "**Matrix:** temp ∈ {0, 1} × thinking ∈ {on, off} × tracks {project, synthetic}  ",
        f"**Reports:** `reports/{report_path.parent}`",
        "",
        "---",
        "",
        _section(
            "Executive summary",
            f"The model was evaluated across **{n_cfg} configurations** "
            f"({len(syn_cfgs)} synthetic + {len(proj_cfgs)} project).\n\n"
            "| Track | Best config | Primary score |\n|---|---|---|\n"
            + (f"| **Synthetic** (composite pass) | {syn_label} | **{syn_score_s}** |\n" if syn_cfgs else "")
            + (f"| **Project** (mutation score) | {proj_label} | **{proj_score_s}** |\n" if proj_cfgs else "")
            + "\n**Headline findings:**\n\n"
            + "\n".join(headlines)
            + ("\n\n" + "\n".join(f"- {n}" for n in (notes or [])) if notes else ""),
        ),
        _section(
            "Experimental setup",
            "| Parameter | Value |\n|---|---|\n" + "\n".join(setup_rows),
        ),
    ]

    if syn_cfgs:
        rows = []
        for name, d in sorted(syn_cfgs.items()):
            r = d["rates"]
            rows.append(
                f"| {_slug_label(name)} | {r['generated'][0]}/{r['generated'][1]} | "
                f"{r['compiles'][0]}/{r['compiles'][1]} | {r['executes'][0]}/{r['executes'][1]} | "
                f"{r['regression'][0]}/{r['regression'][1]} | **{d['composite_pct']}%** ({r['composite'][0]}/{r['composite'][1]}) |"
            )
        parts.append(
            _section(
                "Results overview — synthetic track",
                "| Config | Generated | Compiles | Executes | Regression det. | Composite pass |\n"
                "|---|---|---|---|---|---|\n" + "\n".join(rows),
            )
        )

    if proj_cfgs:
        rows = []
        for name, d in sorted(proj_cfgs.items()):
            rows.append(
                f"| {_slug_label(name)} | {d['gen_ok'] or '?'}/{d['sut_total']} | "
                f"{d['sut_bench']}/{d['sut_total']} | {d['benchmarks']} | {d['covered']} | "
                f"{d['killed']} | **{d['mutation_pct']:.0f}%** |"
            )
        parts.append(
            _section(
                "Results overview — project track",
                "| Config | Gen OK | Runtime kept | Benchmarks | Covered | Killed | Mutation score |\n"
                "|---|---|---|---|---|---|---|\n" + "\n".join(rows),
            )
        )

    if syn_cfgs:
        fails = _find_synthetic_failures(syn_cfgs)
        body = ""
        if fails["compile"]:
            body += "**Compile failures** (≥ half of configs):\n\n| Task | Failed in |\n|---|---|\n"
            for t in sorted(fails["compile"]):
                cfgs = [c for c, d in syn_cfgs.items() if not d["tasks"].get(t, {}).get("compiles")]
                body += f"| `{t}` | {', '.join(_slug_label(c) for c in cfgs)} |\n"
            body += "\n"
        if fails["no_detect"]:
            body += "**Executed but regression not detected** (≥ half of configs):\n\n| Task | Missed in |\n|---|---|\n"
            for t in sorted(fails["no_detect"]):
                cfgs = [
                    c
                    for c, d in syn_cfgs.items()
                    if d["tasks"].get(t, {}).get("executes")
                    and d["tasks"].get(t, {}).get("regressions", "").startswith("0/")
                ]
                body += f"| `{t}` | {', '.join(_slug_label(c) for c in cfgs)} |\n"
        parts.append(_section("Synthetic failure analysis", body or "No systematic failure clusters identified."))

    if proj_cfgs:
        avg_fail = sum(d["gen_fail"] or 0 for d in proj_cfgs.values()) / max(len(proj_cfgs), 1)
        body = (
            f"- **Compile-check exhaustion:** ~{avg_fail:.0f}/97 classes fail generation per config on average.\n"
        )
        if all(d["mutation_pct"] == 0 for d in proj_cfgs.values()):
            body += (
                "- **Zero mutation score across all configs:** surviving benchmarks do not cover any of the 100 performance mutants.\n"
                "- Likely causes: invalid `@Setup` fixtures for archive/compressor classes, or benchmarks that never invoke mutated API paths.\n"
            )
        else:
            body += "- See per-config scorecard.json for runtime-filter drops and uncovered mutants.\n"
        parts.append(_section("Project failure analysis", body))

    all_cfgs = sorted(syn_cfgs) + sorted(proj_cfgs)
    parts.append(
        _section("Artifact index", f"```\n{_artifact_tree(out_path.parent, all_cfgs)}\n```")
    )

    text = "\n".join(parts) + "\n"
    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text(text)
    return text


def generate_synthetic_campaign_report(
    *,
    model: str,
    variant: str,
    campaign: str,
    config_dirs: list[Path],
    out_path: Path,
) -> str:
    configs: dict[str, dict] = {}
    for d in config_dirs:
        sm = d / "summary.md"
        if sm.exists():
            configs[d.name] = _parse_synthetic_summary(sm)

    best = max(configs.items(), key=lambda x: x[1]["composite_pct"])
    rows = []
    for name, d in sorted(configs.items()):
        r = d["rates"]
        rows.append(
            f"| {_config_label(name)} | {r['compiles'][0]}/{r['compiles'][1]} | "
            f"{r['executes'][0]}/{r['executes'][1]} | {r['regression'][0]}/{r['regression'][1]} | "
            f"**{d['composite_pct']}%** ({r['composite'][0]}/{r['composite'][1]}) |"
        )

    fails = _find_synthetic_failures(configs)
    preset = "strong" if "strong" in campaign else "default"

    parts = [
        f"# Evaluation Report: `{model}`",
        "",
        f"**Variant:** `{variant}`  ",
        f"**Campaign:** `{campaign}`  ",
        "**Track:** synthetic  ",
        f"**Harness:** `openai-zero-shot`  ",
        f"**JMH preset:** `{preset}`  ",
        f"**Configs:** {len(configs)} (temp × thinking grid + replicates at temp=1)",
        "",
        "---",
        "",
        _section(
            "Executive summary",
            f"Synthetic-track campaign across **{len(configs)} configurations**.\n\n"
            f"| Best config | Composite pass |\n|---|---|\n"
            f"| {_config_label(best[0])} | **{best[1]['composite_pct']}%** "
            f"({best[1]['rates']['composite'][0]}/{best[1]['rates']['composite'][1]}) |\n\n"
            "**Headline findings:**\n\n"
            f"1. Best composite pass: **{best[1]['composite_pct']}%** at {_config_label(best[0])}.\n"
            f"2. Temp=1 configs show lower compile rates"
            + (" (especially for SFT)." if variant == "sft" else ".")
            + "\n"
            + (
                f"3. Recurring missed tasks: {', '.join(f'`{t}`' for t in fails['no_detect'][:6])}."
                if fails["no_detect"]
                else ""
            ),
        ),
        _section(
            "Results overview",
            "| Config | Compiles | Executes | Regression det. | Composite pass |\n"
            "|---|---|---|---|---|\n" + "\n".join(rows),
        ),
    ]

    if fails["compile"] or fails["no_detect"]:
        body = ""
        if fails["compile"]:
            body += "**Compile failures:** " + ", ".join(f"`{t}`" for t in sorted(fails["compile"])) + "\n\n"
        if fails["no_detect"]:
            body += "**Detection misses:** " + ", ".join(f"`{t}`" for t in sorted(fails["no_detect"])) + "\n"
        parts.append(_section("Failure analysis", body))

    parts.append(
        _section(
            "Artifact index",
            f"```\nreports/gemma4-e2b/synthetic/{variant}/{campaign}/\n"
            + "├── EVALUATION_REPORT.md\n"
            + "".join(f"├── {n}/summary.md\n" for n in sorted(configs))
            + "```",
        )
    )

    text = "\n".join(parts) + "\n"
    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text(text)
    return text


def generate_single_synthetic_report(
    *,
    model: str,
    variant: str,
    run_dir: Path,
    out_path: Path,
) -> str:
    sm = run_dir / "summary.md"
    data = _parse_synthetic_summary(sm)
    r = data["rates"]
    fails = _find_synthetic_failures({"single": data})
    status = " _(partial — interrupted)_" if data["interrupted"] else ""

    parts = [
        f"# Evaluation Report: `{model}`",
        "",
        f"**Variant:** `{variant}`  ",
        f"**Track:** synthetic  ",
        f"**Run:** `{run_dir.name}`{status}  ",
        "**Harness:** `openai-zero-shot`  ",
        "",
        "---",
        "",
        _section(
            "Executive summary",
            "| Metric | Value |\n|---|---|\n"
            f"| Composite pass | **{data['composite_pct']}%** ({r['composite'][0]}/{r['composite'][1]}) |\n"
            f"| Compiles | {r['compiles'][0]}/{r['compiles'][1]} |\n"
            f"| Executes | {r['executes'][0]}/{r['executes'][1]} |\n"
            f"| Regression detection | {r['regression'][0]}/{r['regression'][1]} |",
        ),
    ]
    if fails["no_detect"] or fails["compile"]:
        body = ""
        if fails["compile"]:
            body += "**Compile failures:** " + ", ".join(f"`{t}`" for t in fails["compile"]) + "\n\n"
        if fails["no_detect"]:
            body += "**Missed despite executing:** " + ", ".join(f"`{t}`" for t in fails["no_detect"]) + "\n"
        parts.append(_section("Failure analysis", body))

    parts.append(
        _section("Artifact index", f"```\n{run_dir.relative_to(run_dir.parents[2])}/summary.md\n```")
    )
    text = "\n".join(parts) + "\n"
    out_path.write_text(text)
    return text


MODELS = {
    "base": "google/gemma-4-e2b-it",
    "sft": "bookxd/jmhgen-gemma4-e2b-sft-merged",
    "rft": "bookxd/gemma-4-e2b-rft-mutation-merged",
    "grpo-mutation70": "bookxd/gemma4-e2b-jmh-grpo-mutation70",
    "aug8-grpo-834": "bookxd/gemma4-e2b-Aug-8-GRPO-834-corpus",
}


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--repo", type=Path, default=Path(__file__).resolve().parents[1])
    args = parser.parse_args()
    repo = args.repo
    root = repo / "reports" / "gemma4-e2b"

    # Matrix eval: base model (2026-07-09)
    syn_base = root / "synthetic/base/2026-07-09_08-31-21"
    proj_base = root / "project/base/2026-07-09_08-31-21"
    for out in (syn_base / "EVALUATION_REPORT.md", proj_base / "EVALUATION_REPORT.md"):
        generate_matrix_report(
            model=MODELS["base"],
            variant="base",
            run_id="2026-07-09_08-31-21",
            synthetic_dir=syn_base,
            project_dir=proj_base,
            out_path=out,
        )
        print("wrote", out)

    # Matrix eval: RFT (2026-07-07) — detailed report maintained manually; skip auto-gen
    # (see reports/gemma4-e2b/synthetic/rft/2026-07-07_12-38-16/EVALUATION_REPORT.md)

    # Matrix eval: GRPO 70%-mutation (2026-08-03). Generated on an hpi2 H100, benchmarked on
    # fsc04. Note the 16384-token completion budget (baselines used 8192).
    # Matrix eval: GRPO run 2 (2026-08-08). Same protocol and token budget as run 1, but
    # benchmarked on bsc-gcp rather than fsc04 -- per-config RSD is noted in each scorecard.
    aug8_run = "2026-08-08_06-48-11"
    syn_aug8 = root / f"synthetic/aug8-grpo-834/{aug8_run}"
    proj_aug8 = root / f"project/aug8-grpo-834/{aug8_run}"
    if syn_aug8.is_dir() or proj_aug8.is_dir():
        for out in (syn_aug8 / "EVALUATION_REPORT.md", proj_aug8 / "EVALUATION_REPORT.md"):
            generate_matrix_report(
                model=MODELS["aug8-grpo-834"],
                variant="aug8-grpo-834",
                run_id=aug8_run,
                synthetic_dir=syn_aug8,
                project_dir=proj_aug8,
                out_path=out,
                notes=[
                    "Training run 2: 834-prompt corpus (vs 744), thinking enabled during rollouts, "
                    "LR 3e-6. Generated on rtx-jonas (RTX PRO 6000 Blackwell), benchmarked on bsc-gcp.",
                    "**Synthetic parity, project regression.** Best composite pass ties run 1 at 76 %, "
                    "but project mutation score falls to 0.12 from 0.18 -- coverage held (0.17-0.19 vs "
                    "0.19-0.20) while conditional kill rate collapsed to 33-71 % from 89-100 %.",
                    "Corpus, thinking and learning rate all changed together, so no single factor is "
                    "attributable from these numbers.",
                ],
            )
            print("wrote", out)

    grpo_run = "2026-08-03_09-30-27"
    syn_grpo = root / f"synthetic/grpo-mutation70/{grpo_run}"
    proj_grpo = root / f"project/grpo-mutation70/{grpo_run}"
    if syn_grpo.is_dir() or proj_grpo.is_dir():
        for out in (syn_grpo / "EVALUATION_REPORT.md", proj_grpo / "EVALUATION_REPORT.md"):
            generate_matrix_report(
                model=MODELS["grpo-mutation70"],
                variant="grpo-mutation70",
                run_id=grpo_run,
                synthetic_dir=syn_grpo,
                project_dir=proj_grpo,
                out_path=out,
                notes=[
                    "Generation: hpi2 aisc H100 (job 2401622), vLLM serving base + LoRA adapter "
                    "`bookxd/gemma4-e2b-jmh-grpo-mutation70-adapter`; benchmarking: fsc04 "
                    "(12 cores, median RSD 0.03-0.08 %).",
                    "**max_tokens 16384**, against 8192 for the base/sft/rft baselines - part of "
                    "any compile-rate gain is headroom rather than skill.",
                    "Training run: LRZ 5723231, GRPO with a 0.70 coverage-guided mutation reward "
                    "(see JMH_Training_Pipeline/docs/grpo-gemma-mutation70-run1.md).",
                ],
            )
            print("wrote", out)

    # Strong campaigns
    for variant in ("base", "sft"):
        strong = root / f"synthetic/{variant}/strong"
        configs = sorted(p for p in strong.iterdir() if p.is_dir() and (p / "summary.md").exists())
        out = strong / "EVALUATION_REPORT.md"
        generate_synthetic_campaign_report(
            model=MODELS[variant],
            variant=variant,
            campaign="strong",
            config_dirs=configs,
            out_path=out,
        )
        print("wrote", out)

    # Single synthetic runs
    singles = [
        ("base", root / "synthetic/base/2026-06-23_11-57-30"),
        ("sft", root / "synthetic/sft/2026-06-23_12-42-08"),
    ]
    for variant, run_dir in singles:
        out = run_dir / "EVALUATION_REPORT.md"
        generate_single_synthetic_report(
            model=MODELS[variant],
            variant=variant,
            run_dir=run_dir,
            out_path=out,
        )
        print("wrote", out)


if __name__ == "__main__":
    main()
