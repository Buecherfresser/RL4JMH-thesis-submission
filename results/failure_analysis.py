#!/usr/bin/env python3
"""Failure analysis for Chapter 4 — where do the arms lose mutants, and why do benchmarks fail to compile?

Inputs (all read-only)
  --data       data_for_thesis root: campaign-rerun-2026-08-31_spin-250ns/<arm>-<project>/{out/*/report/scorecard.json, bundle/model_output.md}
  --dataset    JMH-Bench dataset root (Codebases/JMH-Bench/dataset/projects): <project>/mutants.yaml (id -> class, method), <project>/src (class LOC)
  --catalogue  JMH_RL_candidate_projects_v3.xlsx: project -> Domain
Outputs (--out):
  figures_ch4/fig_failure_analysis.{pdf,png}    coverage/kill by role of the mutated method, per arm (single panel)
  tables_ch4/tab_compile_errors.tex             condensed javac diagnostics per arm (chapter)
  tables_appendix/tab_compile_errors_full.tex   full category breakdown
  tables_appendix/tab_failure_decomposition.tex, tab_mutant_kinds.tex, tab_class_features.tex
  failure_analysis.md, failure_analysis.json
"""
from __future__ import annotations

import argparse
import collections
import glob
import json
import re
import statistics
from pathlib import Path

import yaml

CAMPAIGN = "campaign-rerun-2026-08-31_spin-250ns"
PROJECTS = ["commons-compress", "decimal4j", "fastfilter", "hppc", "jodd-util", "snakeyaml"]
CATALOGUE_NAME = {"commons-compress": "Apache Commons Compress", "decimal4j": "decimal4j", "fastfilter": "FastFilter",
                  "hppc": "HPPC", "jodd-util": "jodd-util", "snakeyaml": "SnakeYAML"}
ARMS = [("gemma-base", "E2B-it (base)"), ("gemma-rft", "E2B+RFT"), ("gemma-run9-ck1600", r"\textbf{E2B+GRPO}"),
        ("gemma-e4b-base", "E4B-it"), ("gpt-oss-120b", "GPT-OSS-120B")]
ARM_LABEL = dict(ARMS)
OURS = "gemma-run9-ck1600"

# ----------------------------------------------------------------------------- mutant kinds (method role)
KIND_ORDER = ["accessor", "lifecycle", "factory", "iteration", "operation"]
KIND_LABEL = {"accessor": "accessor / getter", "lifecycle": "lifecycle (close, reset, …)", "factory": "factory / constructor",
              "iteration": "iteration / views", "operation": "core operation"}


def mutant_kind(m: dict) -> str:
    name, sig, cls = m["method"], m.get("signature", ""), m["fqcn"].rsplit(".", 1)[-1].split("$")[-1]
    if name in ("close", "finish", "release", "clear", "reset", "flush", "checkOpen"):
        return "lifecycle"
    if name == cls or name in ("from", "of", "valueOf", "by", "zero", "newInstance", "construct", "build", "unscaled", "self") \
            or name.startswith(("create", "from", "new")):
        return "factory"
    if name in ("forEach", "values", "keys", "iterator", "descendingIterator", "cursor") or name.startswith("for"):
        return "iteration"
    if (name.startswith(("get", "is", "has")) and "(" in sig and sig.split("(", 1)[1].startswith(")")) \
            or name in ("size", "ramBytesUsed", "capacity", "position", "scale", "isChecked", "getPrecision"):
        return "accessor"
    return "operation"


# ----------------------------------------------------------------------------- javac diagnostic categories
CATS = [  # first match wins
    ("jmh", r"Blackhole|org\.openjdk\.jmh|enum annotation value must be an enum constant|@Setup|@Benchmark|@State|annotation (interface|type) not applicable|should be public"),
    ("symbol", r"cannot find symbol"),
    ("import_compress", r"package org\.apache\.commons\.compress[\w.]* does not exist"),
    ("import", r"package [\w.]+ does not exist|cannot access"),
    ("signature", r"cannot be applied to given types|no suitable (method|constructor) found|reference to \S+ is ambiguous|cannot infer type|incompatible types: inference"),
    ("types", r"incompatible types|bad operand|cannot be converted|inconvertible types|does not take parameters"),
    ("exception", r"unreported exception|is never thrown in body"),
    ("access", r"has private access|has protected access|is not public|not visible|is not accessible|cannot be accessed"),
    ("abstract", r"is abstract; cannot be instantiated|is not abstract and does not override|does not override or implement|cannot override|cannot inherit from final"),
    ("static", r"cannot be referenced from a static context"),
    ("final_redef", r"cannot assign a value to final|is already defined|might not have been initialized|might already have been assigned|must be final or effectively final"),
    ("syntax", r"expected|illegal start|reached end of file|unclosed|not a statement|orphaned|missing return statement|invalid method declaration|integer number too large|try-with-resources"),
]
CAT_LABEL = {"jmh": "JMH API or annotation misuse", "symbol": "cannot find symbol (unknown class, method or variable)",
             "import_compress": r"import of \texttt{org.apache.commons.compress} in another project", "import": "other package does not exist",
             "signature": "wrong arity, argument types or ambiguous overload", "types": "incompatible types", "exception": "checked exception not handled",
             "access": "member not accessible", "abstract": "abstract / final / override misuse", "static": "instance member from static context",
             "final_redef": "final, duplicate or uninitialised variable", "syntax": "syntax, missing return", "other": "other"}
DIAG_RE = re.compile(r"^\[ERROR\]\s+(?P<file>\S+\.java):\[(?P<line>\d+),(?P<col>\d+)\]\s+(?P<msg>.*)$")
SYMBOL_RE = re.compile(r"^\s*symbol:\s+(?P<kind>class|method|variable|constructor)\s+(?P<name>\S+)")


def categorize(msg: str) -> str:
    for cat, pat in CATS:
        if re.search(pat, msg):
            return cat
    return "other"


def parse_repair_prompts(md_path: Path):
    """Yield (generation_index, [diagnostics]) for every archived generation whose final prompt was a repair prompt.
    A diagnostic = dict(cat, msg, symbol_kind). Duplicates (Maven prints each error twice) are removed."""
    s = md_path.read_text(errors="replace")
    parts = re.split(r"^# Class generation (\d+)\s*$", s, flags=re.M)
    for idx, body in zip(parts[1::2], parts[2::2]):
        prompt = body.split("## Thinking", 1)[0].split("## Response", 1)[0]
        if "did not compile" not in prompt:
            continue
        seen, diags = set(), []
        lines = prompt.splitlines()
        for i, line in enumerate(lines):
            m = DIAG_RE.match(line.strip())
            if not m:
                continue
            key = (m["file"].rsplit("/", 1)[-1], m["line"], m["col"], m["msg"].strip())
            if key in seen:
                continue
            seen.add(key)
            sk = None
            for j in range(i + 1, min(i + 3, len(lines))):
                sm = SYMBOL_RE.match(lines[j])
                if sm:
                    sk = sm["kind"]
                    break
            diags.append({"cat": categorize(m["msg"]), "msg": m["msg"].strip()[:160], "symbol_kind": sk})
        if diags:
            yield int(idx), diags


# ----------------------------------------------------------------------------- helpers
def wilson(k, n, z=1.96):
    if n == 0:
        return (float("nan"), float("nan"))
    p = k / n
    d = 1 + z * z / n
    c = p + z * z / (2 * n)
    h = z * ((p * (1 - p) / n + z * z / (4 * n * n)) ** 0.5)
    return ((c - h) / d, (c + h) / d)


def pct(x, nd=1):
    return "--" if x is None or x != x else f"{100 * x:.{nd}f}"


def table(rows, caption, label, colspec, header, notes=None, resize=True):
    out = ["\\begin{table}[htbp]", "\\centering", "\\small", f"\\caption{{{caption}}}", f"\\label{{{label}}}"]
    if resize:
        out.append("\\resizebox{\\textwidth}{!}{%")
    out += [f"\\begin{{tabular}}{{{colspec}}}", "\\toprule", header + " \\\\", "\\midrule", *rows, "\\bottomrule", "\\end{tabular}"]
    if resize:
        out.append("}")
    if notes:
        out.append(f"\\par\\smallskip\\footnotesize {notes}")
    out.append("\\end{table}")
    return "\n".join(out) + "\n"


def tex(s: str) -> str:
    return s.replace("_", r"\_").replace("%", r"\%").replace("&", r"\&").replace("$", r"\$")


# ----------------------------------------------------------------------------- loading
def load_domains(xlsx: Path) -> dict:
    import openpyxl
    wb = openpyxl.load_workbook(xlsx, read_only=True, data_only=True)
    ws = wb["Candidate Projects"]
    rows = list(ws.iter_rows(values_only=True))
    hdr = rows[2]
    recs = [dict(zip(hdr, r)) for r in rows[3:] if r[1]]
    by_name = {str(r["Project"]): r for r in recs}
    return {p: str(by_name[CATALOGUE_NAME[p]]["Domain"]) for p in PROJECTS if CATALOGUE_NAME[p] in by_name}


def load_mutants(dataset: Path) -> dict:
    out = {}
    for p in PROJECTS:
        d = yaml.safe_load(open(dataset / p / "mutants.yaml"))
        out[p] = {m["id"]: m | {"kind": mutant_kind(m), "top": m["fqcn"].split("$")[0]} for m in d["mutants"]}
    return out


def class_features(dataset: Path, project: str, fqcn: str) -> dict:
    path = dataset / project / "src" / "main" / "java" / (fqcn.replace(".", "/") + ".java")
    if not path.exists():
        return {"loc": None, "generic": None, "public_methods": None}
    src = path.read_text(errors="replace")
    loc = sum(1 for ln in src.splitlines() if ln.strip() and not ln.strip().startswith(("//", "*", "/*")))
    simple = fqcn.rsplit(".", 1)[-1]
    generic = bool(re.search(r"\b(class|enum|interface)\s+" + re.escape(simple) + r"\s*<", src))
    pub = len(re.findall(r"^\s*public\s+(?!class|interface|enum|static\s+final\s+\w+\s+[A-Z_]+\s*=)[\w<>\[\],\s?]+\s+\w+\s*\(", src, flags=re.M))
    return {"loc": loc, "generic": generic, "public_methods": pub}


def load_cells(data: Path):
    cells = {}
    for arm, _ in ARMS:
        for p in PROJECTS:
            hits = glob.glob(str(data / CAMPAIGN / f"{arm}-{p}" / "out" / "*" / "report" / "scorecard.json"))
            if not hits:
                continue
            d = json.load(open(hits[0]))
            r = d["result"]
            gens = r["class_generations"]
            by_class = {g["target_class"]: g for g in gens}
            dropped_pkgs = {x["package"] for x in (r.get("runtime_filter_dropped") or [])}
            in_suite = {g["target_class"] for g in gens if g.get("ok") and g["package"].rsplit(".", 1)[-1] not in dropped_pkgs}
            mut = {m["id"]: m for m in r["mutant_results"]}
            cells[(arm, p)] = {"gens": by_class, "in_suite": in_suite, "mutants": mut,
                               "bundle_md": data / CAMPAIGN / f"{arm}-{p}" / "bundle" / "model_output.md",
                               "n_ok": r["classes_succeeded"], "n_total": r["classes_total"]}
    return cells


# ----------------------------------------------------------------------------- analyses
OUTCOMES = ["not_candidate", "not_generated", "runtime_dropped", "not_reached", "covered_not_killed", "killed"]
OUTCOME_LABEL = {"not_candidate": "class not prompted", "not_generated": "class not generated", "runtime_dropped": "class dropped at runtime",
                 "not_reached": "class benchmarked, method not reached", "covered_not_killed": "reached, not killed", "killed": "killed"}
OUTCOME_SHORT = {"not_candidate": "not prompted", "not_generated": "not generated", "runtime_dropped": "RT drop",
                 "not_reached": "not reached", "covered_not_killed": "not killed", "killed": "killed"}


def decompose(cell, mutants):
    out = collections.Counter()
    for mid, m in mutants.items():
        res = cell["mutants"].get(mid)
        if res is None:
            continue
        top = m["top"]
        if res["status"] == "killed":
            o = "killed"
        elif res["status"] == "covered_not_killed":
            o = "covered_not_killed"
        else:
            g = cell["gens"].get(top)
            if g is None:
                o = "not_candidate"
            elif not g.get("ok"):
                o = "not_generated"
            elif top not in cell["in_suite"]:
                o = "runtime_dropped"
            else:
                o = "not_reached"
        out[o] += 1
    return out


def analyse(data: Path, dataset: Path, catalogue: Path) -> tuple[dict, list[str]]:
    """Read the raw data and return (numbers, markdown lines). Everything rendered later comes from `numbers`."""
    domains = load_domains(catalogue)
    mutants = load_mutants(dataset)
    cells = load_cells(data)
    numbers: dict = {"domains": domains, "projects": PROJECTS}
    md = ["# Failure analysis (auto-generated by evaluation/failure_analysis.py)", ""]

    # 1. decomposition per (arm, project) and pooled per arm
    dec = {}
    for arm, _ in ARMS:
        tot = collections.Counter()
        for p in PROJECTS:
            if (arm, p) in cells:
                dec[f"{arm}|{p}"] = dict(decompose(cells[(arm, p)], mutants[p]))
                tot.update(dec[f"{arm}|{p}"])
        if tot:
            dec[arm] = dict(tot)
    numbers["decomposition"] = dec
    md.append("## 1. Mutant outcome decomposition (CPU-burn campaign)\n")
    md.append("| arm / project | " + " | ".join(OUTCOME_LABEL[o] for o in OUTCOMES) + " |")
    md.append("|---|" + "---|" * len(OUTCOMES))
    for k, v in dec.items():
        n = sum(v.values())
        md.append(f"| {k} | " + " | ".join(f"{v.get(o,0)} ({100*v.get(o,0)/n:.0f} %)" for o in OUTCOMES) + " |")
    md.append("")

    # 2. mutant kinds
    kinds = {}
    for arm, _ in ARMS:
        for p in PROJECTS:
            cell = cells.get((arm, p))
            if not cell:
                continue
            for mid, m in mutants[p].items():
                res = cell["mutants"].get(mid)
                if not res:
                    continue
                k = kinds.setdefault(f"{arm}|{m['kind']}", collections.Counter())
                k["n"] += 1
                k["covered"] += res["status"] != "not_covered"
                k["killed"] += res["status"] == "killed"
    numbers["kinds"] = {k: dict(v) for k, v in kinds.items()}
    numbers["kind_n"] = dict(collections.Counter(m["kind"] for p in PROJECTS for m in mutants[p].values()))
    numbers["kind_by_project"] = {p: dict(collections.Counter(m["kind"] for m in mutants[p].values())) for p in PROJECTS}
    md.append("## 2. Mutant kinds\n")
    for p in PROJECTS:
        md.append(f"- {p} ({domains.get(p)}): " + ", ".join(f"{k} {v}" for k, v in sorted(numbers['kind_by_project'][p].items(), key=lambda x: -x[1])))
    md.append("")

    # 3. compile diagnostics from repair prompts
    cats_by_arm, repaired, diags_per_class, symbol_kinds = {}, collections.Counter(), collections.defaultdict(list), collections.defaultdict(collections.Counter)
    examples = collections.defaultdict(list)
    for (arm, p), cell in cells.items():
        if not cell["bundle_md"].exists():
            continue
        for idx, diags in parse_repair_prompts(cell["bundle_md"]):
            repaired[arm] += 1
            diags_per_class[arm].append(len(diags))
            c = cats_by_arm.setdefault(arm, collections.Counter())
            for dg in diags:
                c[dg["cat"]] += 1
                if dg["cat"] == "symbol" and dg["symbol_kind"]:
                    symbol_kinds[arm][dg["symbol_kind"]] += 1
                if len(examples[dg["cat"]]) < 3:
                    examples[dg["cat"]].append(f"{p}: {dg['msg']}")
    terminal = {arm: sum(1 for p in PROJECTS if (arm, p) in cells for g in cells[(arm, p)]["gens"].values() if not g.get("ok")) for arm, _ in ARMS}
    cat_order = [c for c, _ in CATS] + ["other"]
    numbers["compile"] = {
        "categories": {f"{arm}|{cat}": cats_by_arm.get(arm, collections.Counter())[cat] for arm, _ in ARMS for cat in cat_order},
        "repaired_classes": dict(repaired), "terminal_failures": terminal,
        "median_diags_per_class": {a: (statistics.median(diags_per_class[a]) if diags_per_class[a] else None) for a, _ in ARMS},
        "symbol_kinds": {k: dict(v) for k, v in symbol_kinds.items()},
    }
    md.append("## 3. Compile diagnostics in repair prompts\n")
    md.append("| category | " + " | ".join(a for a, _ in ARMS) + " |")
    md.append("|---|" + "---|" * len(ARMS))
    for cat in cat_order:
        md.append(f"| {cat} | " + " | ".join(str(cats_by_arm.get(a, collections.Counter())[cat]) for a, _ in ARMS) + " |")
    md.append("| repaired classes | " + " | ".join(str(repaired[a]) for a, _ in ARMS) + " |")
    md.append("| terminal failures | " + " | ".join(str(terminal[a]) for a, _ in ARMS) + " |")
    md.append("")
    md.append("examples per category:")
    for cat, ex in examples.items():
        for e in ex:
            md.append(f"- [{cat}] {e}")
    md.append("")

    # 4. class features
    feats = {(p, cls): class_features(dataset, p, cls) for p in PROJECTS for cls in cells[(OURS, p)]["gens"]}
    locs = sorted(f["loc"] for f in feats.values() if f["loc"])
    q1, q2 = locs[len(locs) // 3], locs[2 * len(locs) // 3]

    def loc_bin(l):
        return "n/a" if l is None else ("small" if l < q1 else ("medium" if l < q2 else "large"))
    cf = {"loc_terciles": [q1, q2]}
    for g in ("small", "medium", "large", "generic", "nongeneric"):
        members = [(p, c) for (p, c), f in feats.items() if (loc_bin(f["loc"]) == g) or (g == "generic" and f["generic"]) or (g == "nongeneric" and f["generic"] is False)]
        cf[f"n|{g}"] = len(members)
        for arm, _ in ARMS:
            ok = sum(1 for (p, c) in members if (arm, p) in cells and cells[(arm, p)]["gens"].get(c, {}).get("ok"))
            n = sum(1 for (p, c) in members if (arm, p) in cells and c in cells[(arm, p)]["gens"])
            cf[f"{arm}|{g}"] = {"ok": ok, "n": n}
    numbers["class_features"] = cf

    # 5. GRPO-only vs GPT-OSS-only kills
    only = {"grpo_only": [], "gptoss_only": []}
    for p in PROJECTS:
        for mid, m in mutants[p].items():
            a = cells[(OURS, p)]["mutants"][mid]["status"] == "killed"
            b = cells[("gpt-oss-120b", p)]["mutants"][mid]["status"] == "killed"
            name = f"{p}: {m['fqcn'].rsplit('.',1)[-1]}.{m['method']} [{m['kind']}]"
            if a and not b:
                only["grpo_only"].append(name)
            if b and not a:
                only["gptoss_only"].append(name)
    numbers["only"] = {k: len(v) for k, v in only.items()}
    md.append("## 5. GRPO-only vs GPT-OSS-only kills\n")
    md.append(f"- killed by GRPO only: {len(only['grpo_only'])}; by GPT-OSS only: {len(only['gptoss_only'])}")
    md.append("- GRPO only: " + "; ".join(only["grpo_only"]))
    md.append("- GPT-OSS only: " + "; ".join(only["gptoss_only"]))
    return numbers, md


# ----------------------------------------------------------------------------- rendering (from numbers only)
OUTCOME_COLOR = {"not_candidate": "#e8e8e8", "not_generated": "#c4c4c4", "runtime_dropped": "#9a9a9a",
                 "not_reached": "#a9c4de", "covered_not_killed": "#6f95bd", "killed": "#1f4e79"}
FIG_OUTCOME = {"not_candidate": "class not prompted", "not_generated": "class not generated", "runtime_dropped": "dropped at runtime",
               "not_reached": "method not reached", "covered_not_killed": "reached, not killed", "killed": "killed"}
ARM_COLOR = {"gemma-base": "#b0b0b0", "gemma-rft": "#7f7f7f", "gemma-run9-ck1600": "#1f4e79", "gemma-e4b-base": "#4d4d4d", "gpt-oss-120b": "#c2662d"}
ARM_PLAIN = {"gemma-base": "E2B-it", "gemma-rft": "E2B+RFT", "gemma-run9-ck1600": "E2B+GRPO", "gemma-e4b-base": "E4B-it", "gpt-oss-120b": "GPT-OSS"}
SHORT_HEAD = r"\textbf{base} & \textbf{RFT} & \textbf{GRPO} & \textbf{E4B} & \textbf{GPT-OSS}"


def fig_failure(numbers: dict, out: Path):
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt
    plt.rcParams.update({"font.family": "sans-serif", "font.size": 8, "axes.titlesize": 8.5, "axes.labelsize": 8,
                         "xtick.labelsize": 7.5, "ytick.labelsize": 7.5, "legend.fontsize": 7, "legend.frameon": False,
                         "axes.spines.top": False, "axes.spines.right": False, "axes.linewidth": 0.7, "pdf.fonttype": 42})
    kinds = numbers["kinds"]
    kind_n = numbers.get("kind_n") or {k: max((v["n"] for key, v in kinds.items() if key.endswith("|" + k)), default=0) for k in KIND_ORDER}
    # Single panel: coverage (light) and kill rate (solid) by role of the mutated method, per arm.
    # The outcome decomposition per arm / project is quoted in the text and tabulated in the appendix.
    fig, ax = plt.subplots(figsize=(6.3, 2.9))
    arms_c = [a for a, _ in ARMS]
    w = 0.8 / len(arms_c)
    for i, a in enumerate(arms_c):
        xs = [j + (i - (len(arms_c) - 1) / 2) * w for j in range(len(KIND_ORDER))]
        cov = [100 * kinds[f"{a}|{k}"]["covered"] / kinds[f"{a}|{k}"]["n"] if f"{a}|{k}" in kinds else 0 for k in KIND_ORDER]
        kil = [100 * kinds[f"{a}|{k}"]["killed"] / kinds[f"{a}|{k}"]["n"] if f"{a}|{k}" in kinds else 0 for k in KIND_ORDER]
        ax.bar(xs, cov, width=w * 0.95, color=ARM_COLOR[a], alpha=0.3, linewidth=0)
        ax.bar(xs, kil, width=w * 0.95, color=ARM_COLOR[a], label=ARM_PLAIN[a])
        for x, c, k in zip(xs, cov, kil):
            if c >= 8:
                ax.text(x, c + 1.5, f"{k:.0f}", ha="center", va="bottom", fontsize=6,
                        color="#8a8a8a" if a == "gemma-base" else ARM_COLOR[a])
    ax.set_xticks(range(len(KIND_ORDER)))
    fig_kind = {"accessor": "accessor /\ngetter", "lifecycle": "lifecycle\n(close, reset, …)", "factory": "factory /\nconstructor",
                "iteration": "iteration /\nview", "operation": "core\noperation"}
    ax.set_xticklabels([f"{fig_kind[k]}\n(n = {kind_n[k]})" for k in KIND_ORDER])
    ax.tick_params(axis="x", length=0)
    ax.set_ylabel("% of mutants of that kind")
    ax.set_ylim(0, 80)
    ax.set_yticks([0, 20, 40, 60, 80])
    ax.legend(ncol=5, loc="lower left", bbox_to_anchor=(0, 1.0), handlelength=1.0, columnspacing=1.4, borderaxespad=0)
    out.mkdir(parents=True, exist_ok=True)
    fig.savefig(out / "fig_failure_analysis.pdf", bbox_inches="tight")
    fig.savefig(out / "fig_failure_analysis.png", bbox_inches="tight", dpi=170)
    plt.close(fig)


def render(numbers: dict, out: Path):
    (out / "tables_ch4").mkdir(parents=True, exist_ok=True)
    (out / "tables_appendix").mkdir(parents=True, exist_ok=True)
    dec, kinds, domains = numbers["decomposition"], numbers["kinds"], numbers["domains"]
    kind_n = numbers.get("kind_n") or {k: max((v["n"] for key, v in kinds.items() if key.endswith("|" + k)), default=0) for k in KIND_ORDER}
    fig_failure(numbers, out / "figures_ch4")

    # appendix: decomposition table
    rows = []
    head = r"\textbf{Arm / project} & \textbf{Domain} & " + " & ".join(f"\\textbf{{{OUTCOME_SHORT[o]}}}" for o in OUTCOMES) + r" & \textbf{Score}"
    for arm, lab in ARMS:
        if arm not in dec:
            continue
        c = dec[arm]; n = sum(c.get(o, 0) for o in OUTCOMES)
        rows.append(f"{lab} & all six & " + " & ".join(pct(c.get(o, 0) / n, 0) for o in OUTCOMES) + f" & {pct(c.get('killed',0)/n)} \\\\")
    for arm, lab in ((OURS, "E2B+GRPO"), ("gpt-oss-120b", "GPT-OSS-120B")):
        rows.append("\\midrule")
        for p in PROJECTS:
            c = dec[f"{arm}|{p}"]; n = sum(c.get(o, 0) for o in OUTCOMES)
            rows.append(f"\\quad {lab}: \\texttt{{{tex(p)}}} & {tex(domains.get(p, '?'))} & " + " & ".join(pct(c.get(o, 0) / n, 0) for o in OUTCOMES) + f" & {pct(c.get('killed',0)/n)} \\\\")
    t1 = table(rows, "Where the planted mutants are lost (share of mutants, \\%): pooled per arm over the six projects, and per project for the GRPO model and GPT-OSS-120B.",
               "tab:failure-decomposition", "@{}ll" + "r" * len(OUTCOMES) + "r@{}", head,
               notes=r"Outcomes are exclusive and read left to right: not prompted = the mutant's class was never a candidate (package-private or nested type); not generated = the benchmark for its class failed the compile check; RT drop = removed by the runtime smoke filter; not reached = class benchmarked but no benchmark executed the mutated method; not killed = reached, slowdown below the kill rule. Domains from the project catalogue (\texttt{JMH\_RL\_candidate\_projects\_v3.xlsx}).")
    (out / "tables_appendix" / "tab_failure_decomposition.tex").write_text(t1)

    # appendix: kinds table
    rows = []
    head = r"\textbf{Mutant kind} & \textbf{n} & " + " & ".join(f"\\multicolumn{{2}}{{c}}{{{lab}}}" for _, lab in ARMS) + r" \\" + "\n" + " & & " + " & ".join("cov. & killed" for _ in ARMS)
    for kd in KIND_ORDER:
        cellsk = []
        for arm, _ in ARMS:
            k = kinds.get(f"{arm}|{kd}", {})
            cellsk.append(f"{pct(k['covered']/k['n'],0) if k.get('n') else '--'} & {pct(k['killed']/k['n'],0) if k.get('n') else '--'}")
        rows.append(f"{KIND_LABEL[kd]} & {kind_n[kd]} & " + " & ".join(cellsk) + " \\\\")
    t2 = table(rows, "Coverage and kill rate (\\% of mutants of that kind) by the role of the mutated method, per arm (numbers behind \\autoref{fig:failure-analysis}).",
               "tab:mutant-kinds", "@{}lr" + "rr" * len(ARMS) + "@{}", head,
               notes=r"Kinds assigned from the mutated method's name and signature (\texttt{failure\_analysis.py}): accessors are argument-less getters; lifecycle = \texttt{close}, \texttt{finish}, \texttt{release}, \texttt{reset}, \texttt{clear}; factory = constructors and static factories (\texttt{from}, \texttt{valueOf}, \texttt{construct}, \dots); iteration = \texttt{forEach}, \texttt{values}, iterators; everything else is a core operation.")
    (out / "tables_appendix" / "tab_mutant_kinds.tex").write_text(t2)

    # compile tables
    comp = numbers["compile"]
    cat_order = [c for c, _ in CATS] + ["other"]
    totals = {arm: sum(comp["categories"].get(f"{arm}|{c}", 0) for c in cat_order) for arm, _ in ARMS}

    def share_row(label, cats):
        vals = []
        for arm, _ in ARMS:
            tot = totals[arm]
            vals.append(pct(sum(comp["categories"].get(f"{arm}|{x}", 0) for x in cats) / tot, 0) if tot else "--")
        return f"{label} & " + " & ".join(vals) + " \\\\"

    footer = ["\\midrule",
              r"diagnostics analysed & " + " & ".join(str(totals[a]) for a, _ in ARMS) + " \\\\",
              r"classes repaired & " + " & ".join(str(comp["repaired_classes"].get(a, 0)) for a, _ in ARMS) + " \\\\",
              r"classes failed after 3 attempts & " + " & ".join(str(comp["terminal_failures"].get(a, 0)) for a, _ in ARMS) + " \\\\"]
    condensed = [
        ("unknown symbol (class, method, field)", ["symbol"]),
        (r"\texttt{commons.compress} import (prompt template)", ["import_compress"]),
        ("JMH API or annotation misuse", ["jmh"]),
        ("wrong arity or argument types", ["signature"]),
        ("incompatible types", ["types"]),
        ("instance member from static context", ["static"]),
        ("syntax, missing return", ["syntax"]),
        ("other", ["access", "abstract", "exception", "final_redef", "import", "other"]),
    ]
    rows = [share_row(lab, cats) for lab, cats in condensed] + footer
    t3 = table(rows, "Why generated benchmarks do not compile: javac diagnostics returned to the model in repair prompts (share of diagnostics per arm, \\%), and the number of classes that still failed after two repairs.",
               "tab:compile-errors", "@{}l" + "r" * len(ARMS) + "@{}", r"\textbf{javac diagnostic} & " + SHORT_HEAD, resize=False,
               notes=r"Diagnostics come from the archived repair prompts, i.e.\ the errors of the attempt \emph{before} a successful repair (about two per repaired class); the archives keep no diagnostics for classes that failed all three attempts. The \texttt{org.apache.commons.compress} imports follow the prompt template, which tells the model for every project that the subject under test lives under that package (\texttt{project\_class.j2}). ``Other'' pools access, abstract-type, exception, final-redefinition and remaining import errors; full breakdown: \autoref{tab:compile-errors-full} (appendix).")
    (out / "tables_ch4" / "tab_compile_errors.tex").write_text(t3)
    rows = [share_row(CAT_LABEL[c], [c]) for c in cat_order] + footer
    t3f = table(rows, "Javac diagnostics in repair prompts, full category breakdown (share of diagnostics per arm, \\%).",
                "tab:compile-errors-full", "@{}l" + "r" * len(ARMS) + "@{}", r"\textbf{javac diagnostic} & " + SHORT_HEAD)
    (out / "tables_appendix" / "tab_compile_errors_full.tex").write_text(t3f)

    # appendix: class features
    cf = numbers["class_features"]
    q1, q2 = cf["loc_terciles"]
    rows = []
    for g, glab in (("small", f"LOC $<$ {q1}"), ("medium", f"{q1} $\\leq$ LOC $<$ {q2}"), ("large", f"LOC $\\geq$ {q2}"),
                    ("generic", "generic type parameters"), ("nongeneric", "no type parameters")):
        vals = []
        for arm, _ in ARMS:
            e = cf.get(f"{arm}|{g}", {})
            vals.append(pct(e["ok"] / e["n"]) if e.get("n") else "--")
        n = cf.get(f"n|{g}", cf.get(f"{OURS}|{g}", {}).get("n", ""))
        rows.append(f"{glab} & {n} & " + " & ".join(vals) + " \\\\")
    t4 = table(rows, "Generation success (\\% of candidate classes with a compiling benchmark) by size and genericity of the SUT class, per arm.",
               "tab:class-features", "@{}lr" + "r" * len(ARMS) + "@{}", r"\textbf{Class group} & \textbf{n} & " + SHORT_HEAD, resize=False,
               notes=r"LOC = non-blank, non-comment lines of the SUT source; terciles over the 514 candidates. Generic = the class declaration carries type parameters.")
    (out / "tables_appendix" / "tab_class_features.tex").write_text(t4)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--data", help="data_for_thesis root (omit or pass --from-json to render from a saved failure_analysis.json)")
    ap.add_argument("--dataset", help="JMH-Bench dataset/projects root")
    ap.add_argument("--catalogue", help="JMH_RL_candidate_projects_v3.xlsx")
    ap.add_argument("--out", required=True)
    ap.add_argument("--from-json", action="store_true", help="skip the analysis; render figure and tables from <out>/failure_analysis.json")
    a = ap.parse_args()
    out = Path(a.out)
    out.mkdir(parents=True, exist_ok=True)
    jpath = out / "failure_analysis.json"
    if a.from_json or not (a.data and Path(a.data).exists()):
        if not jpath.exists():
            raise SystemExit("no data root reachable and no saved failure_analysis.json to render from")
        print(f"rendering from {jpath}")
        numbers = json.load(open(jpath))
    else:
        numbers, md = analyse(Path(a.data), Path(a.dataset), Path(a.catalogue))
        (out / "failure_analysis.md").write_text("\n".join(md))
        json.dump(numbers, open(jpath, "w"), indent=1, default=str)
    render(numbers, out)
    print(f"wrote figures_ch4/fig_failure_analysis.{{pdf,png}}, tables_ch4/tab_compile_errors.tex and 4 appendix tables to {out}")


if __name__ == "__main__":
    main()
