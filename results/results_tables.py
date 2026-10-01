#!/usr/bin/env python3
"""Chapter 4 (Results) tables — generated exclusively from data_for_thesis.

Data root (external SSD):  /Volumes/SamsungSSD/jmh-thesis/data_for_thesis
    campaign-rerun-2026-08-31_spin-250ns/<model>-<project>/out/*/report/scorecard.json
    campaign-rerun-2026-08_thread-sleep-mutation/<model>-<project>/out/*/report/scorecard.json
    llm4jmh-4model-replanted-intersection-2026-09-02/<project>/benchmark/bug-HWO.json
    llm4jmh-4model-replanted-intersection-2026-09-02/<project>/coverage/summary_<arm>.json
    llm4jmh-4model-replanted-intersection-2026-09-02/<project>/coverage/selected_methods.4model.json

Usage
    python3 results_tables.py extract  --data <root> --out <dir>   # one compact JSON per cell (slow, once)
    python3 results_tables.py tables   --data <root> --out <dir>   # LaTeX tables + numbers.json (fast)

Only the standard library is used. Nothing outside --data is read.
"""
from __future__ import annotations

import argparse
import glob
import json
import math
import os
import statistics
import sys
from collections import defaultdict
from pathlib import Path

# --------------------------------------------------------------------------- constants
CAMPAIGNS = {
    "spin": "campaign-rerun-2026-08-31_spin-250ns",
    "sleep": "campaign-rerun-2026-08_thread-sleep-mutation",
}
LLM4JMH_DIR = "llm4jmh-4model-replanted-intersection-2026-09-02"

# arm id in the folder name -> (short label, model id as recorded in class_generations)
MODELS = [  # display order
    ("gemma-base",        r"Gemma-4 E2B-it (base)"),
    ("gemma-rft",         r"Gemma-4 E2B + RFT"),
    ("gemma-run9-ck1600", r"Gemma-4 E2B + GRPO (ours)"),
    ("gemma-e4b-base",    r"Gemma-4 E4B-it"),
    ("gpt-oss-120b",      r"GPT-OSS-120B"),
    ("dsv4",              r"DeepSeek-V4 Flash"),
]
MODELS.append(("gpt-5.6-luna", r"GPT-5.6 Luna"))  # planned replacement arm; picked up automatically once its cells exist
MODEL_LABEL = dict(MODELS)
MODEL_ORDER = [m for m, _ in MODELS]
MODEL_SHORT = {  # column heads for wide matrices
    "gemma-base": "E2B-it", "gemma-rft": "E2B+RFT", "gemma-run9-ck1600": r"\textbf{E2B+GRPO}",
    "gemma-e4b-base": "E4B-it", "gpt-oss-120b": "GPT-OSS", "dsv4": "DSV4", "gpt-5.6-luna": "Luna",
}


def arms(cells: dict) -> list[str]:
    """Arms present in a campaign, known ones in display order, unknown folder prefixes appended."""
    present = {m for (m, p) in cells}
    return [m for m in MODEL_ORDER if m in present] + sorted(present - set(MODEL_ORDER))


def arm_label(m: str) -> str:
    return MODEL_LABEL.get(m, tex_escape(m))


def short(m: str) -> str:
    return MODEL_SHORT.get(m, tex_escape(m))


def filtered(cells: dict, exclude: set[str]) -> dict:
    return {k: v for k, v in cells.items() if k[0] not in exclude}
PROJECTS = ["commons-compress", "decimal4j", "fastfilter", "hppc", "jodd-util", "snakeyaml"]

LLM4JMH_PROJECTS = [("rxjava", "RxJava"), ("eclipse-collections", "Eclipse Collections"), ("zipkin", "Zipkin")]
LLM4JMH_ARMS = [("gptoss", "GPT-OSS-120B"), ("gemini", "Gemini 2.0 Flash"), ("qwen", "Qwen2.5-Coder-32B"),
                ("llm2jmh", "E2B+GRPO (ours)")]
LLM4JMH_ARM_LABEL = {a: (r"\textbf{" + n + "}" if a == "llm2jmh" else n) for a, n in LLM4JMH_ARMS}
DELTAS = [0.01, 0.05, 0.10, 0.50, 0.90]

# "Suites as built" — transcribed from README.md / _docs/INTERSECTION_STATUS.md of the
# LLM4JMH folder (the build logs are only archived as .tgz). files_in, compiled, benchmarks.
# Targets that could actually be planted, per README ("Targets that could not be planted": RxJava 29 of 30,
# Eclipse 26 of 30 — generated primitive collections without a source file —, Zipkin 30 of 30).
LLM4JMH_PLANTED = {"rxjava": 29, "eclipse-collections": 26, "zipkin": 30}
LLM4JMH_DENOM = "planted"  # planted | sampled (all 30) | alldata (upstream Table-4 convention: data in every arm)
LLM4JMH_SUITES = {
    "rxjava":              {"llm2jmh": (179, 179, 292), "gptoss": (443, 443, 700), "gemini": (439, 439, 782), "qwen": (133, 133, 148)},
    "eclipse-collections": {"llm2jmh": (307, 307, 655), "gptoss": (174, 174, 1091), "gemini": (177, 177, 834), "qwen": (55, 55, 189)},
    "zipkin":              {"llm2jmh": (84, 46, 117),   "gptoss": (51, 50, 173),   "gemini": (39, 39, 126),   "qwen": (12, 12, 20)},
}


# --------------------------------------------------------------------------- helpers
def wilson(k: int, n: int, z: float = 1.96) -> tuple[float, float]:
    if n == 0:
        return (float("nan"), float("nan"))
    p = k / n
    denom = 1 + z * z / n
    centre = p + z * z / (2 * n)
    half = z * math.sqrt(p * (1 - p) / n + z * z / (4 * n * n))
    return ((centre - half) / denom, (centre + half) / denom)


def pct(x: float, nd: int = 1) -> str:
    if x is None or (isinstance(x, float) and math.isnan(x)):
        return "--"
    return f"{100 * x:.{nd}f}"


def quantile(xs: list[float], q: float) -> float:
    if not xs:
        return float("nan")
    s = sorted(xs)
    pos = (len(s) - 1) * q
    lo, hi = math.floor(pos), math.ceil(pos)
    if lo == hi:
        return s[lo]
    return s[lo] + (s[hi] - s[lo]) * (pos - lo)


def fx(x: float) -> str:
    """Slowdown factor with adaptive precision: 1.016, 30.3, 526, 2.7e6 -> $2.7\\times10^{6}$."""
    if x is None or math.isnan(x):
        return "--"
    if x < 10:
        return f"{x:.3f}"
    if x < 1000:
        return f"{x:.1f}" if x < 100 else f"{x:.0f}"
    e = int(math.floor(math.log10(x)))
    return f"${x / 10**e:.1f}\\times 10^{{{e}}}$"


def num(x, nd=0):
    if x is None:
        return "--"
    if nd == 0:
        return f"{int(round(x)):,}".replace(",", r"{,}")
    return f"{x:,.{nd}f}".replace(",", r"{,}")


def tex_escape(s: str) -> str:
    return s.replace("_", r"\_").replace("%", r"\%").replace("&", r"\&")


def table(env_lines: list[str], caption: str, label: str, colspec: str, header: str,
          resize: bool = False, notes: str | None = None, small: bool = True) -> str:
    out = ["\\begin{table}[htbp]", "\\centering"]
    if small:
        out.append("\\small")
    out.append(f"\\caption{{{caption}}}")
    out.append(f"\\label{{{label}}}")
    if resize:
        out.append("\\resizebox{\\textwidth}{!}{%")
    out.append(f"\\begin{{tabular}}{{{colspec}}}")
    out.append("\\toprule")
    out.append(header + r" \\")
    out.append("\\midrule")
    out.extend(env_lines)
    out.append("\\bottomrule")
    out.append("\\end{tabular}")
    if resize:
        out.append("}")
    if notes:
        out.append(f"\\par\\smallskip\\footnotesize {notes}")
    out.append("\\end{table}")
    return "\n".join(out) + "\n"


# --------------------------------------------------------------------------- extract
def find_scorecards(root: Path, campaign_dir: str):
    for cell in sorted((root / campaign_dir).iterdir()):
        if not cell.is_dir() or cell.name.startswith("_"):
            continue
        hits = glob.glob(str(cell / "out" / "*" / "report" / "scorecard.json"))
        yield cell.name, (Path(hits[0]) if hits else None)


def split_cell(name: str) -> tuple[str, str]:
    for p in PROJECTS:
        if name.endswith("-" + p):
            return name[: -(len(p) + 1)], p
    raise ValueError(name)


def extract_cell(sc_path: Path) -> dict:
    d = json.load(open(sc_path))
    r, cfg, host = d["result"], d["config"], d.get("host", {})
    gens = r.get("class_generations", [])
    ok = [g for g in gens if g.get("ok")]
    pairs = []  # (mutant_id, benchmark, effect_size, p_value, detected, hits)
    per_mutant = []
    for m in r["mutant_results"]:
        per_mutant.append({"id": m["id"], "status": m["status"], "n_covering": len(m.get("covered_by") or []),
                           "effect_size": m.get("effect_size"), "n_measured": m.get("n_measured"),
                           "n_timeouts": m.get("n_timeouts")})
        for det in m.get("detections") or []:
            pairs.append([m["id"], det.get("benchmark"), det.get("effect_size"), det.get("p_value"),
                          bool(det.get("detected")), det.get("hits")])
    rsd = list((r.get("per_benchmark_rsd") or {}).values())
    base_ns = {}  # benchmark -> baseline cost per invocation in ns (thrpt in ops/s -> 1e9/score)
    for b in (r.get("base_run") or {}).get("benchmarks") or []:
        sc, unit = b.get("score"), (b.get("unit") or "")
        if sc and sc > 0 and unit.startswith("ops/"):
            per = {"ops/s": 1e9, "ops/ms": 1e6, "ops/us": 1e3, "ops/ns": 1.0}.get(unit)
            if per:
                base_ns[b.get("name") or b.get("method")] = per / sc
    return {
        "baseline_ns_per_op": base_ns,
        "project": r["instance_id"], "version": d.get("version"),
        "mutant_op": cfg.get("mutant_op") or "sleep", "mutant_tokens": cfg.get("mutant_tokens"),
        "preset": cfg.get("preset"), "alpha": cfg.get("alpha"), "min_slowdown": cfg.get("min_slowdown"),
        "max_detect_attempts": cfg.get("max_detect_attempts"), "wall_budget_seconds": r.get("wall_budget_seconds"),
        "jmh": cfg.get("jmh"), "generation_config": {k: v for k, v in cfg.get("generation_config", {}).items()
                                                     if k != "harness_kwargs"} |
                                {"harness_kwargs": {k: v for k, v in cfg.get("generation_config", {}).get("harness_kwargs", {}).items()
                                                    if k in ("temperature", "enable_thinking", "max_tokens")}},
        "model_ids": sorted({g.get("model") for g in gens if g.get("model")}),
        "host": {"hostname": host.get("hostname"), "cpu": (host.get("cpu") or {}).get("model") or (host.get("cpu") or {}).get("brand"),
                 "cpu_raw": host.get("cpu"), "memory_gib": host.get("memory_gib"), "kernel": host.get("release"),
                 "jdk": host.get("jdk")},
        "classes_total": r["classes_total"], "classes_succeeded": r["classes_succeeded"],
        "first_attempt_ok": sum(1 for g in ok if g.get("compile_check_attempts") == 1),
        "attempts_hist": {str(a): sum(1 for g in ok if g.get("compile_check_attempts") == a) for a in (1, 2, 3)},
        "classes_compiled": r.get("classes_compiled"), "classes_filtered": r.get("classes_filtered"),
        "classes_runtime_filtered": r.get("classes_runtime_filtered"),
        "n_runtime_dropped": len(r.get("runtime_filter_dropped") or []),
        "n_compile_dropped": len(r.get("compile_filter_dropped") or []),
        "n_benchmarks": r["n_benchmarks"], "n_baseline_failures": len(r.get("baseline_failures") or []),
        "prompt_tokens": sum(g.get("prompt_tokens") or 0 for g in gens),
        "completion_tokens": sum(g.get("completion_tokens") or 0 for g in gens),
        "completion_tokens_ok": [g.get("completion_tokens") or 0 for g in ok],
        "generation_seconds": (r.get("generation_metadata") or {}).get("generation_seconds"),
        "mutant_count": r["mutant_count"], "mutants_covered": r["mutants_covered"], "mutants_killed": r["mutants_killed"],
        "mutants_killed_by_timeout": r.get("mutants_killed_by_timeout"), "mutants_errored": r.get("mutants_errored"),
        "mutation_score": r["mutation_score"], "coverage_rate": r.get("coverage_rate"),
        "stability_rsd_percent": r.get("stability_rsd_percent"), "per_benchmark_rsd": rsd,
        "duration_seconds": r.get("duration_seconds"),
        "per_mutant": per_mutant, "pairs": pairs,
    }


def cmd_extract(root: Path, out: Path, only: str | None, force: bool = False):
    cache = out / "_cells"
    cache.mkdir(parents=True, exist_ok=True)
    for key, cdir in CAMPAIGNS.items():
        if only and key != only:
            continue
        for name, sc in find_scorecards(root, cdir):
            dst = cache / f"{key}__{name}.json"
            if dst.exists() and not force:
                continue
            if sc is None:
                print(f"[{key}] {name}: no scorecard — skipped", file=sys.stderr)
                continue
            model, project = split_cell(name)
            rec = extract_cell(sc)
            rec.update({"campaign": key, "model": model, "cell": name, "source": str(sc.relative_to(root))})
            json.dump(rec, open(dst, "w"))
            print(f"[{key}] {name}: ok", file=sys.stderr)


# --------------------------------------------------------------------------- load
def load_cells(out: Path) -> dict[str, dict[tuple[str, str], dict]]:
    cells: dict[str, dict[tuple[str, str], dict]] = {k: {} for k in CAMPAIGNS}
    for f in sorted((out / "_cells").glob("*.json")):
        rec = json.load(open(f))
        cells[rec["campaign"]][(rec["model"], rec["project"])] = rec
    return cells


# --------------------------------------------------------------------------- tables (campaign)
def t_eval_projects(spin: dict) -> tuple[str, dict]:
    rows, nums = [], {}
    for p in PROJECTS:
        recs = [r for (m, pp), r in spin.items() if pp == p]
        tot = {r["classes_total"] for r in recs}
        mut = {r["mutant_count"] for r in recs}
        ver = {r["version"] for r in recs}
        assert len(tot) == 1 and len(mut) == 1, (p, tot, mut)
        nums[p] = {"version": ver.pop() if len(ver) == 1 else sorted(ver), "classes_total": tot.copy().pop(), "mutants": mut.copy().pop(), "arms": len(recs)}
        rows.append(f"\\texttt{{{tex_escape(p)}}} & {tex_escape(str(nums[p]['version']))} & {nums[p]['classes_total']} & {nums[p]['mutants']} \\\\")
    tc, tm = sum(v["classes_total"] for v in nums.values()), sum(v["mutants"] for v in nums.values())
    rows.append("\\midrule")
    rows.append(f"\\textbf{{Total}} & & \\textbf{{{tc}}} & \\textbf{{{tm}}} \\\\")
    tex = table(rows, "Held-out evaluation projects of the JMH-Bench project track.", "tab:eval-projects",
                "@{}llrr@{}", r"\textbf{Project} & \textbf{Version} & \textbf{Candidate classes} & \textbf{Planted mutants}",
                notes="Candidate classes are the prefiltered SUT classes each model is prompted with (\\autoref{sec:task-paradigm}).")
    return tex, nums | {"total_classes": tc, "total_mutants": tm}


def t_eval_models(spin: dict) -> tuple[str, dict]:
    rows, nums = [], {}
    for m in arms(spin):
        recs = [r for (mm, p), r in spin.items() if mm == m]
        ids = sorted({i for r in recs for i in r["model_ids"]})
        gc = recs[0]["generation_config"]
        hk = gc.get("harness_kwargs", {})
        nums[m] = {"model_ids": ids, "harness": gc.get("harness"), "temperature": hk.get("temperature"),
                   "thinking": hk.get("enable_thinking"), "max_tokens": hk.get("max_tokens"),
                   "compile_check_retries": gc.get("compile_check_retries")}
        rows.append(f"{arm_label(m)} & \\texttt{{{tex_escape(', '.join(ids))}}} & {tex_escape(gc.get('harness',''))} \\\\")
    hk0 = nums[arms(spin)[0]]
    tex = table(rows, "Model arms of the project-track evaluation.", "tab:eval-models", "@{}lll@{}",
                r"\textbf{Arm} & \textbf{Model identifier} & \textbf{Serving}", resize=False,
                notes=f"All arms: temperature {hk0['temperature']}, thinking mode {hk0['thinking']}, "
                      f"max.\\ {num(int(hk0['max_tokens']))} output tokens, at most {hk0['compile_check_retries']} compile repairs per class. "
                      r"\texttt{openai-zero-shot} = local vLLM server; \texttt{openrouter} = hosted API.")
    return tex, nums


def t_eval_setup(spin: dict, sleep: dict) -> tuple[str, dict]:
    recs = list(spin.values())
    j = recs[0]["jmh"]
    hosts = sorted({r["host"]["hostname"] for r in recs if r["host"]["hostname"]})
    cpus = sorted({str(r["host"]["cpu"]) for r in recs})
    jdks = sorted({json.dumps(r["host"]["jdk"], sort_keys=True) for r in recs})
    mem = sorted({r["host"]["memory_gib"] for r in recs})
    kern = sorted({r["host"]["kernel"] for r in recs})
    jdk0 = recs[0]["host"]["jdk"] or {}
    vs = (jdk0.get("version_string") or "").splitlines()
    jdk_str = vs[0].replace("openjdk version", "OpenJDK").replace('"', "") if vs else json.dumps(jdk0)
    aff = sorted({(r["host"]["cpu_raw"] or {}).get("affinity_count") for r in recs} - {None})
    affcls = sorted({str((r["host"]["cpu_raw"] or {}).get("affinity_perf_class")) for r in recs} - {"None"})
    ops = {"spin": (recs[0]["mutant_op"], recs[0]["mutant_tokens"]), "sleep": (list(sleep.values())[0]["mutant_op"] if sleep else None, None)}
    rows = [
        r"\multicolumn{2}{@{}l}{\textit{Benchmark hosts}} \\",
        f"Machines & {len(hosts)} identical nodes, one (arm, project) cell per node \\\\",
        f"CPU & {tex_escape(cpus[0]) if len(cpus)==1 else tex_escape('; '.join(cpus))} \\\\",
        (f"Pinned logical CPUs & {aff[0]} of 12" + (f" (performance class {tex_escape(affcls[0])})" if len(affcls) == 1 else "") + r" \\") if len(aff) == 1 else f"Pinned logical CPUs & {tex_escape(str(aff))} \\\\",
        f"RAM & {mem[0]:.1f}\\,GiB \\\\" if len(mem) == 1 else f"RAM & {tex_escape(str(mem))} \\\\",
        f"Kernel & \\texttt{{{tex_escape(kern[0])}}} \\\\" if len(kern) == 1 else f"Kernel & {len(kern)} variants \\\\",
        f"JDK & {tex_escape(str(jdk_str))} \\\\",
        r"\midrule",
        r"\multicolumn{2}{@{}l}{\textit{JMH preset (\texttt{campaign})}} \\",
        f"Forks & {j['forks']} \\\\",
        f"Warm-up / measurement iterations & {j['warmup_iterations']} $\\times$ {j['warmup_time_seconds']:g}\\,s / {j['measurement_iterations']} $\\times$ {j['measurement_time_seconds']:g}\\,s \\\\",
        f"Mode / threads / heap / GC & {j['mode']} / {j['threads']} / \\texttt{{{j['heap']}}} / \\texttt{{{tex_escape(j['gc_collector'])}}} \\\\",
        f"Per-benchmark budget / iteration timeout & {j['timeout_seconds']:g}\\,s / {j['iteration_timeout_seconds']:g}\\,s \\\\",
        r"\midrule",
        r"\multicolumn{2}{@{}l}{\textit{Regression detection}} \\",
        f"Welch one-sided $\\alpha$ / min.\\ slowdown & {recs[0]['alpha']} / $\\geq {recs[0]['min_slowdown']:.2f}\\times$ \\\\",
        f"Covering benchmarks re-run per mutant & $\\leq {recs[0]['max_detect_attempts']}$ \\\\",
        f"Mutation operator (main) & \\texttt{{{ops['spin'][0]}}}, {ops['spin'][1]} steps ($\\approx$250\\,ns) \\\\",
        f"Mutation operator (comparison) & \\texttt{{{ops['sleep'][0]}}} (\\texttt{{Thread.sleep(0,1)}}) \\\\",
    ]
    tex = table(rows, "Measurement configuration of the project-track campaigns.", "tab:eval-setup", "@{}ll@{}",
                r"\textbf{Parameter} & \textbf{Value}")
    nums = {"hosts": hosts, "cpus": cpus, "jdk": jdk0, "memory_gib": mem, "kernels": kern, "jmh": j,
            "alpha": recs[0]["alpha"], "min_slowdown": recs[0]["min_slowdown"], "max_detect_attempts": recs[0]["max_detect_attempts"], "ops": ops}
    return tex, nums


def agg_generation(cells: dict, m: str) -> dict:
    recs = [r for (mm, p), r in cells.items() if mm == m]
    tot = sum(r["classes_total"] for r in recs)
    suc = sum(r["classes_succeeded"] for r in recs)
    first = sum(r["first_attempt_ok"] for r in recs)
    comp = sum(r["classes_compiled"] or 0 for r in recs)
    rt = sum(r["n_runtime_dropped"] for r in recs)
    nb = sum(r["n_benchmarks"] for r in recs)
    ctoks = [t for r in recs for t in r["completion_tokens_ok"]]
    bf = sum(r["n_baseline_failures"] for r in recs)
    # classes_compiled is the harness's "classes compiled into suite" AFTER the runtime smoke filter
    # (classes_succeeded - classes_runtime_filtered in every cell), i.e. the classes actually measured.
    return {"projects": len(recs), "classes_total": tot, "succeeded": suc, "first_attempt": first, "compiled": comp,
            "runtime_dropped": rt, "benchmarks": nb, "baseline_failures": bf, "bench_per_class": nb / max(1, comp),
            "completion_tokens_median": statistics.median(ctoks) if ctoks else None,
            "prompt_tokens": sum(r["prompt_tokens"] for r in recs), "completion_tokens": sum(r["completion_tokens"] for r in recs),
            "generation_seconds": sum(r["generation_seconds"] or 0 for r in recs),
            "wilson_succeeded": wilson(suc, tot), "wilson_compiled": wilson(comp, tot)}


def t_generation(spin: dict) -> tuple[str, dict]:
    rows, nums = [], {}
    for m in arms(spin):
        a = agg_generation(spin, m)
        nums[m] = a
        lo, hi = a["wilson_succeeded"]
        rows.append(
            f"{arm_label(m)} & {a['succeeded']}/{a['classes_total']} & {pct(a['succeeded']/a['classes_total'])} [{pct(lo,0)}, {pct(hi,0)}] "
            f"& {pct(a['first_attempt']/a['classes_total'])} & {a['runtime_dropped']} & {a['compiled']} ({pct(a['compiled']/a['classes_total'],0)}) "
            f"& {num(a['benchmarks'])} & {a['baseline_failures']} & {a['bench_per_class']:.1f} \\\\")
    tex = table(rows, "Generation and compilation outcomes over the six held-out projects (514 candidate classes).",
                "tab:generation", "@{}lrrrrrrrr@{}",
                r"\textbf{Arm} & \textbf{Generated} & \textbf{\% [95\,\% CI]} & \textbf{1st try \%} & \textbf{RT drop} & \textbf{Measured} & \textbf{Bench.} & \textbf{Excl.} & \textbf{/class}",
                resize=True,
                notes=r"Generated: classes whose benchmark passed the compile check within $\leq 2$ repairs; 1st try: passed without repair; "
                      r"RT drop: classes removed by the runtime smoke filter; Measured: classes in the final suite (generated $-$ RT drop); "
                      r"Bench.: measured \texttt{@Benchmark} methods; Excl.: benchmarks excluded at the baseline (error, OOM or over budget); /class: Bench.\ per measured class. Wilson 95\,\% intervals.")
    return tex, nums


def t_generation_by_project(spin: dict) -> tuple[str, dict]:
    rows, nums = [], defaultdict(dict)
    models = arms(spin)
    head = r"\textbf{Project} & " + " & ".join(short(m) for m in models)
    for p in PROJECTS:
        cells = []
        for m in models:
            r = spin.get((m, p))
            if r is None:
                cells.append("--"); continue
            nums[p][m] = {"succeeded": r["classes_succeeded"], "total": r["classes_total"], "compiled": r["classes_compiled"]}
            cells.append(f"{r['classes_succeeded']}/{r['classes_total']} ({pct(r['classes_succeeded']/r['classes_total'],0)})")
        rows.append(f"\\texttt{{{tex_escape(p)}}} & " + " & ".join(cells) + r" \\")
    rows.append("\\midrule")
    tot_cells = []
    for m in models:
        a = agg_generation(spin, m)
        tot_cells.append(f"\\textbf{{{a['succeeded']}/{a['classes_total']} ({pct(a['succeeded']/a['classes_total'],0)})}}")
    rows.append(r"\textbf{All} & " + " & ".join(tot_cells) + r" \\")
    tex = table(rows, "Generated classes per project and arm (generated/candidates, per cent).", "tab:generation-by-project",
                "@{}l" + "r" * len(models) + "@{}", head, resize=len(models) >= 6)
    return tex, nums


def agg_mutation(cells: dict, m: str) -> dict:
    recs = [r for (mm, p), r in cells.items() if mm == m]
    tot = sum(r["mutant_count"] for r in recs)
    cov = sum(r["mutants_covered"] for r in recs)
    kil = sum(r["mutants_killed"] for r in recs)
    to = sum(r["mutants_killed_by_timeout"] or 0 for r in recs)
    scores = [r["mutation_score"] for r in recs]
    return {"projects": len(recs), "mutants": tot, "covered": cov, "killed": kil, "killed_by_timeout": to,
            "coverage": cov / tot if tot else None, "kill_given_covered": kil / cov if cov else None,
            "score_pooled": kil / tot if tot else None, "score_mean": statistics.mean(scores) if scores else None,
            "score_ci": wilson(kil, tot), "kgc_ci": wilson(kil, cov) if cov else (float('nan'), float('nan'))}


def t_mutation_matrix(cells: dict, op: str, label: str, caption: str) -> tuple[str, dict]:
    models = arms(cells)
    head = r"\textbf{Project} & " + " & ".join(short(m) for m in models)
    rows, nums = [], defaultdict(dict)
    for p in PROJECTS:
        c = []
        for m in models:
            r = cells.get((m, p))
            if r is None:
                c.append("--"); continue
            nums[p][m] = {"killed": r["mutants_killed"], "covered": r["mutants_covered"], "total": r["mutant_count"], "score": r["mutation_score"]}
            c.append(f"{r['mutants_killed']}/{r['mutants_covered']} ({pct(r['mutation_score'],0)})")
        rows.append(f"\\texttt{{{tex_escape(p)}}} & " + " & ".join(c) + r" \\")
    rows.append("\\midrule")
    pooled = []
    for m in models:
        a = agg_mutation(cells, m)
        nums["pooled"][m] = a
        pooled.append(f"\\textbf{{{a['killed']}/{a['covered']} ({pct(a['score_pooled'],0)})}}")
    rows.append(r"\textbf{All (350)} & " + " & ".join(pooled) + r" \\")
    tex = table(rows, caption, label, "@{}l" + "r" * len(models) + "@{}", head, resize=len(models) >= 6,
                notes=r"Cell format: killed/covered (mutation score in \% of all planted mutants of the project: 50, commons-compress 100).")
    return tex, nums


def t_mutation_rq1(spin: dict) -> tuple[str, dict]:
    """RQ1: coverage, conditional kill and mutation score per arm, CPU-burn operator, pooled over the 350 mutants."""
    rows, nums = [], {}
    for m in arms(spin):
        a = agg_mutation(spin, m)
        nums[m] = a
        clo, chi = wilson(a["covered"], a["mutants"])
        klo, khi = a["kgc_ci"]
        lo, hi = a["score_ci"]
        rows.append(f"{arm_label(m)} & {a['covered']}/{a['mutants']} & {pct(a['coverage'])} [{pct(clo,0)}, {pct(chi,0)}] "
                    f"& {a['killed']}/{a['covered']} & {pct(a['kill_given_covered'])} [{pct(klo,0)}, {pct(khi,0)}] "
                    f"& \\textbf{{{pct(a['score_pooled'])}}} [{pct(lo,0)}, {pct(hi,0)}] & {pct(a['score_mean'])} \\\\")
    tex = table(rows, "Mutation coverage and detection on the six held-out projects (CPU-burn operator, 350 planted mutants).",
                "tab:mutation-rq1", "@{}lrrrrrr@{}",
                r"\textbf{Arm} & \textbf{Covered} & \textbf{Coverage \%} & \textbf{Killed} & \textbf{Killed $\mid$ covered \%} & \textbf{Mutation score \%} & \textbf{Mean/proj.}",
                resize=True,
                notes=r"Covered: mutants reached by at least one benchmark of the arm (recording pass); Killed $\mid$ covered: kill rate among covered mutants (the conditional-kill term of $r_{\mathrm{mutation}}$); Mutation score: killed / 350. Wilson 95\,\% intervals in brackets; Mean/proj.\ is the unweighted mean of the six per-project scores.")
    return tex, nums


def t_mutation_summary(spin: dict, sleep: dict) -> tuple[str, dict]:
    """RQ2: sleep vs CPU burn side by side, for the arms measured under both operators."""
    rows, nums = [], {}
    def delta(x):
        return f"${100 * x:+.0f}$"
    models = [m for m in arms(spin) if any(mm == m for (mm, p) in sleep)]
    for m in models:
        s_, b_ = agg_mutation(sleep, m), agg_mutation(spin, m)
        nums[f"{m}|sleep"], nums[f"{m}|spin"] = s_, b_
        rows.append(f"{arm_label(m)} & {pct(s_['coverage'])} & {pct(b_['coverage'])} "
                    f"& {pct(s_['kill_given_covered'])} & {pct(b_['kill_given_covered'])} & {delta(b_['kill_given_covered'] - s_['kill_given_covered'])} "
                    f"& \\textbf{{{pct(s_['score_pooled'])}}} & \\textbf{{{pct(b_['score_pooled'])}}} & {delta(b_['score_pooled'] - s_['score_pooled'])} \\\\")
    head = (r"\textbf{Arm} & \multicolumn{2}{c}{\textbf{Coverage \%}} & \multicolumn{3}{c}{\textbf{Killed $\mid$ covered \%}} & \multicolumn{3}{c}{\textbf{Mutation score \%}} \\" + "\n"
            + " & sleep & CPU burn & sleep & CPU burn & $\Delta$ & sleep & CPU burn & $\Delta$")
    tex = table(rows, "Effect of the mutation operator on the same generated suites: inherited sleep ($\\approx$1.2\\,ms per hit) versus CPU burn ($\\approx$250\\,ns), pooled over the 350 mutants.",
                "tab:mutation-summary", "@{}lrrrrrrrr@{}", head, resize=True,
                notes=r"Only arms measured under both operators. Small coverage differences arise from benchmarks excluded at the baseline in one run but not the other; $\Delta$ in percentage points. Per-project killed/covered counts: \autoref{tab:mutation-spin} and \autoref{tab:mutation-sleep} (appendix).")
    return tex, nums


def t_operator_effect(spin: dict, sleep: dict) -> tuple[str, dict]:
    rows, nums = [], {}
    def pairs_of(cells, m):
        return [p for (mm, pp), r in cells.items() if mm == m for p in r["pairs"] if p[2] is not None and p[2] > 0]
    models = arms(spin)
    for m in models + ["__all__"]:
        for op, cells in (("spin", spin), ("sleep", sleep)):
            if m == "__all__":
                if op == "spin":
                    if rows and rows[-1].startswith(r"\addlinespace"):
                        rows.pop()
                    rows.append(r"\midrule")
                ps = [p for mm in models for p in pairs_of(cells, mm)]
                lab = r"\textbf{All arms}"
            else:
                if not any(mm == m for (mm, p) in cells):
                    continue
                ps = pairs_of(cells, m)
                lab = arm_label(m)
            if not ps:
                continue
            es = [p[2] for p in ps]
            det = sum(1 for p in ps if p[4])
            n = len(es)
            below = sum(1 for e in es if e < 1.02) / n
            band = sum(1 for e in es if 1.02 <= e <= 2.0) / n
            above = sum(1 for e in es if e > 2.0) / n
            nums[f"{m}|{op}"] = {"pairs": n, "detected": det, "median": quantile(es, .5), "p75": quantile(es, .75), "p95": quantile(es, .95),
                                 "max": max(es), "lt102": below, "band": band, "gt2": above}
            opname = "CPU burn" if op == "spin" else "sleep"
            first = lab if op == "spin" else ""
            rows.append(f"{first} & {opname} & {num(n)} & {pct(det/n)} & {fx(quantile(es,.5))} & {fx(quantile(es,.75))} & {fx(quantile(es,.95))} "
                        f"& {pct(below,0)} & {pct(band,0)} & {pct(above,0)} \\\\")
        if m != "__all__":
            rows.append(r"\addlinespace[2pt]")
    tex = table(rows, "Measured (mutant, benchmark) pairs: detection rate and slowdown distribution per mutation operator.",
                "tab:operator-effect", "@{}llrrrrrrrr@{}",
                r"\textbf{Arm} & \textbf{Operator} & \textbf{Pairs} & \textbf{Det.\ \%} & \textbf{Median} & \textbf{p75} & \textbf{p95} & \textbf{$<$1.02} & \textbf{[1.02, 2]} & \textbf{$>$2}",
                resize=True,
                notes=r"Slowdown = baseline throughput / armed throughput of a covering benchmark. The last three columns give the share of pairs (\%) below the 2\,\% band, inside the targeted [2\,\%, 100\,\%] band, and above it.")
    return tex, nums


def t_stability(spin: dict) -> tuple[str, dict]:
    rows, nums = [], {}
    for m in arms(spin):
        rs = [x for (mm, p), r in spin.items() if mm == m for x in r["per_benchmark_rsd"] if x is not None]
        if not rs:
            continue
        n = len(rs)
        le5 = sum(1 for x in rs if x <= 5.0) / n
        le1 = sum(1 for x in rs if x <= 1.0) / n
        gt25 = sum(1 for x in rs if x >= 25.0) / n
        med = statistics.median(rs)
        p90 = quantile(rs, .9)
        nums[m] = {"benchmarks": n, "median_rsd": med, "p90_rsd": p90, "share_le1": le1, "share_le5": le5, "share_ge25": gt25,
                   "per_project_median": {p: r["stability_rsd_percent"] for (mm, p), r in spin.items() if mm == m}}
        rows.append(f"{arm_label(m)} & {num(n)} & {med:.2f} & {p90:.2f} & {pct(le1)} & {pct(le5)} & {pct(gt25)} \\\\")
    tex = table(rows, "Measurement stability of the generated benchmarks (baseline runs, robust RSD per benchmark, in \\%).",
                "tab:stability", "@{}lrrrrrr@{}",
                r"\textbf{Arm} & \textbf{Bench.} & \textbf{Median} & \textbf{p90} & \textbf{$\leq 1\,\%$} & \textbf{$\leq 5\,\%$} & \textbf{$\geq 25\,\%$}",
                resize=True,
                notes=r"$\leq 5\,\%$ and $\geq 25\,\%$ are the thresholds at which $r_{\mathrm{RSD}}$ is 1 and 0 (\autoref{sec:reward-stability}); $\leq 1\,\%$ is LLM4JMH's stable band.")
    return tex, nums


def t_cost(spin: dict) -> tuple[str, dict]:
    rows, nums = [], {}
    for m in arms(spin):
        a = agg_generation(spin, m)
        hours = sum((r["duration_seconds"] or 0) for (mm, p), r in spin.items() if mm == m) / 3600
        mut = agg_mutation(spin, m)
        nums[m] = {"generation_seconds": a["generation_seconds"], "prompt_tokens": a["prompt_tokens"], "completion_tokens": a["completion_tokens"],
                   "bench_hours": hours, "hours_per_killed": hours / mut["killed"] if mut["killed"] else None}
        rows.append(f"{arm_label(m)} & {a['generation_seconds']/60:.0f} & {a['prompt_tokens']/1e6:.2f} & {a['completion_tokens']/1e6:.2f} "
                    f"& {hours:.0f} & {hours/mut['killed']:.2f} \\\\")
    tex = table(rows, "Generation and measurement cost over the six held-out projects (CPU-burn campaign).", "tab:cost",
                "@{}lrrrrr@{}",
                r"\textbf{Arm} & \textbf{Gen.\ min} & \textbf{Prompt Mtok} & \textbf{Compl.\ Mtok} & \textbf{Bench.\ h} & \textbf{h/kill}",
                notes=r"Bench.\ h: summed wall-clock of the six measurement cells (baseline + recording + armed re-runs), one cell per machine.")
    return tex, nums


# --------------------------------------------------------------------------- tables (LLM4JMH)
def load_llm4jmh(root: Path) -> dict:
    base = root / LLM4JMH_DIR
    out = {}
    for pid, _ in LLM4JMH_PROJECTS:
        bug = json.load(open(base / pid / "benchmark" / "bug-HWO.json"))
        sel = json.load(open(base / pid / "coverage" / "selected_methods.4model.json"))
        cov = {}
        for arm, _ in LLM4JMH_ARMS:
            f = base / pid / "coverage" / f"summary_{arm}.json"
            if f.exists():
                s = json.load(open(f))  # keys: project, branch, benchmarks_profiled, lines, covers
                covers = s.get("covers", {})
                cov[arm] = {"methods": len(covers), "benchmarks": s.get("benchmarks_profiled"),
                            "benchmarks_covering": len({b for bb in covers.values() for b in bb})}
        # intersection size straight from the report file
        inter = None
        rep = base / pid / "coverage" / "intersection_report.4model.txt"
        if rep.exists():
            for line in rep.read_text().splitlines():
                if line.startswith("intersection:"):
                    inter = int(line.split(":")[1].split()[0])
        out[pid] = {"bug": bug, "selected": sel, "coverage": cov, "intersection": inter}
    return out


def _llm4jmh_denominator(pid: str, bug: dict) -> tuple[int, str]:
    if LLM4JMH_DENOM == "sampled":
        return len(bug), "all sampled targets"
    if LLM4JMH_DENOM == "alldata":
        return sum(1 for v in bug.values() if all(v.get(a) for a, _ in LLM4JMH_ARMS)), "targets with data in every arm"
    return LLM4JMH_PLANTED[pid], "planted mutants"


def t_llm4jmh_detection(L: dict) -> tuple[str, dict]:
    """Mutant-level detection as in LLM4JMH's notebook ("percentage of detected bugs"): a mutant is detected by an
    arm when any covering benchmark has bug_size >= delta. Denominator per LLM4JMH_DENOM."""
    rows, nums = [], {}
    ncol = 3 + len(DELTAS)
    for pid, pname in LLM4JMH_PROJECTS:
        bug = L[pid]["bug"]
        denom, denom_name = _llm4jmh_denominator(pid, bug)
        anydata = sum(1 for v in bug.values() if any(v.get(a) for a, _ in LLM4JMH_ARMS))
        rows.append(f"\\multicolumn{{{ncol}}}{{@{{}}l}}{{\\textit{{{pname}}} ({len(bug)} sampled targets, "
                    f"{LLM4JMH_PLANTED[pid]} planted, {anydata} with measurements; denominator {denom})}} \\\\")
        for arm, _ in LLM4JMH_ARMS:
            withdata = sum(1 for v in bug.values() if v.get(arm))
            pairs = sum(len(v.get(arm, [])) for v in bug.values())
            det = {d: sum(1 for v in bug.values() if any(b >= d for b in v.get(arm, []))) for d in DELTAS}
            nums[f"{pid}|{arm}"] = {"with_data": withdata, "pairs": pairs, "detected": det, "denominator": denom, "sampled": len(bug)}
            rows.append(f"\\quad {LLM4JMH_ARM_LABEL[arm]} & {withdata} & {pairs} & "
                        + " & ".join(pct(det[d] / denom) for d in DELTAS) + " \\\\")
        if pid != LLM4JMH_PROJECTS[-1][0]:
            rows.append("\\addlinespace")
    head = ("\\textbf{Arm} & \\textbf{Mutants w/ data} & \\textbf{Pairs} & "
            + " & ".join(f"$\\delta={int(d * 100)}\\,\\%$" for d in DELTAS))
    tex = table(rows, "LLM4JMH replication on replanted intersection mutants: share of the planted mutants per project "
                      "detected at bug-size threshold $\\delta$.",
                "tab:llm4jmh-detection", "@{}lrr" + "r" * len(DELTAS) + "@{}", head, resize=True,
                notes="Bug size $= 1 -$ upper bound of the 99\\,\\% bootstrap CI (10\\,000 resamples) of the armed/clean "
                      "throughput ratio, computed with LLM4JMH's own script; a mutant counts as detected when any covering "
                      "benchmark of the arm reaches bug size $\\geq \\delta$ (LLM4JMH's \\emph{detected bugs} metric). "
                      "Denominator: " + denom_name + "; mutants without a measured pair count as not detected. "
                      "Pair-level rates and the upstream denominator convention: \\autoref{tab:llm4jmh-conventions}.")
    return tex, nums


def t_llm4jmh_conventions(L: dict) -> tuple[str, dict]:
    """The two other aggregations found in LLM4JMH's notebook: (i) pair-level 'killed mutants' = share of
    (mutant, benchmark) bug sizes >= delta; (ii) mutant-level with denominator = targets that have data in every arm."""
    rows, nums = [], {}
    ds = [0.01, 0.10, 0.50]
    ncol = 1 + 2 * len(ds)
    for pid, pname in LLM4JMH_PROJECTS:
        bug = L[pid]["bug"]
        common = [k for k, v in bug.items() if all(v.get(a) for a, _ in LLM4JMH_ARMS)]
        rows.append(f"\\multicolumn{{{ncol}}}{{@{{}}l}}{{\\textit{{{pname}}} ({len(common)} targets with data in every arm)}} \\\\")
        for arm, _ in LLM4JMH_ARMS:
            allb = [b for v in bug.values() for b in v.get(arm, [])]
            pair = {d: (sum(1 for b in allb if b >= d) / len(allb) if allb else float("nan")) for d in ds}
            mut = {d: (sum(1 for k in common if any(b >= d for b in bug[k].get(arm, []))) / len(common) if common else float("nan")) for d in ds}
            nums[f"{pid}|{arm}"] = {"pair_level": pair, "mutant_level_common": mut, "pairs": len(allb), "common": len(common)}
            rows.append(f"\\quad {LLM4JMH_ARM_LABEL[arm]} & " + " & ".join(pct(pair[d]) for d in ds)
                        + " & " + " & ".join(pct(mut[d]) for d in ds) + " \\\\")
        if pid != LLM4JMH_PROJECTS[-1][0]:
            rows.append("\\addlinespace")
    dcols = " & ".join(f"\\multicolumn{{1}}{{c}}{{$\\delta={int(d * 100)}\\,\\%$}}" for d in ds)
    head = ("\\textbf{Arm} & " + dcols + " & " + dcols + " \\\\\n"
            " & \\multicolumn{3}{c}{pairs killed (\\%)} & \\multicolumn{3}{c}{mutants detected (\\%), common targets}")
    tex = table(rows, "LLM4JMH replication under the two aggregation conventions of the original notebook: share of "
                      "(mutant, benchmark) pairs with bug size $\\geq \\delta$ (``killed mutants''), and share of mutants "
                      "detected with the denominator restricted to targets measured in every arm (Table~4 convention).",
                "tab:llm4jmh-conventions", "@{}l" + "r" * (2 * len(ds)) + "@{}", head, resize=True)
    return tex, nums


def t_llm4jmh_coverage(L: dict) -> tuple[str, dict]:
    rows, nums = [], {}
    arms = [a for a, _ in LLM4JMH_ARMS]
    head = r"\textbf{Project} & " + " & ".join(f"\\multicolumn{{2}}{{c}}{{\\textbf{{{n}}}}}" for _, n in LLM4JMH_ARMS) + r" & \textbf{Intersection} \\" + "\n" + \
           " & " + " & ".join(r"bench. & methods" for _ in LLM4JMH_ARMS) + " & methods"
    for pid, pname in LLM4JMH_PROJECTS:
        cov, inter = L[pid]["coverage"], L[pid]["intersection"]
        nums[pid] = {"coverage": cov, "intersection": inter, "selected": len(L[pid]["selected"])}
        cells = [f"{num(cov[a]['benchmarks'])} & {num(cov[a]['methods'])}" if a in cov else "-- & --" for a in arms]
        rows.append(f"{pname} & " + " & ".join(cells) + f" & {num(inter) if inter else '--'} \\\\")
    tex = table(rows, "Coverage breadth on the LLM4JMH projects: profiled \\texttt{@Benchmark} methods, SUT methods they execute (JaCoCo), and size of the four-arm intersection from which 30 mutation targets were sampled (seed 42).",
                "tab:llm4jmh-coverage", "@{}l" + "rr" * len(arms) + "r@{}", head, resize=True)
    return tex, nums


def t_llm4jmh_suites() -> tuple[str, dict]:
    rows = []
    for pid, pname in LLM4JMH_PROJECTS:
        cells = []
        for arm, _ in LLM4JMH_ARMS:
            fi, co, nb = LLM4JMH_SUITES[pid][arm]
            cells.append(f"{co}/{fi} & {nb}")
        rows.append(f"{pname} & " + " & ".join(cells) + r" \\")
    head = r"\textbf{Project} & " + " & ".join(f"\\multicolumn{{2}}{{c}}{{\\textbf{{{n}}}}}" for _, n in LLM4JMH_ARMS) + r" \\" + "\n" + \
           " & " + " & ".join(r"compiled/files & bench." for _ in LLM4JMH_ARMS)
    tex = table(rows, "Suites as built on the LLM4JMH projects: compiled/generated benchmark files and \\texttt{@Benchmark} methods per arm.",
                "tab:llm4jmh-suites", "@{}l" + "rr" * len(LLM4JMH_ARMS) + "@{}", head, resize=True,
                notes=r"Baseline suites are the published LLM4JMH outputs (no repair loop possible); the GRPO arm used the repair loop on RxJava and Eclipse Collections but not on Zipkin (vLLM server unavailable). Transcribed from the dataset README.")
    return tex, {"suites": LLM4JMH_SUITES}


# --------------------------------------------------------------------------- figures
ARM_COLOR = {  # ours in a saturated accent, Gemma family in greys, large/proprietary models in warm tones
    "gemma-base": "#b0b0b0", "gemma-rft": "#7f7f7f", "gemma-run9-ck1600": "#1f4e79", "gemma-e4b-base": "#4d4d4d",
    "gpt-oss-120b": "#c2662d", "dsv4": "#8c6d31", "gpt-5.6-luna": "#d9a441",
}
_FALLBACK = ["#5b8db8", "#9c6ade", "#3a8f6e", "#b5474a"]


def plain_short(m: str) -> str:
    return MODEL_SHORT.get(m, m).replace("\\textbf{", "").replace("}", "").replace("\\_", "_")


def arm_color(m: str) -> str:
    if m in ARM_COLOR:
        return ARM_COLOR[m]
    return _FALLBACK[hash(m) % len(_FALLBACK)]


def _mpl():
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt
    plt.rcParams.update({
        "font.family": "sans-serif", "font.size": 8, "axes.titlesize": 8.5, "axes.labelsize": 8,
        "xtick.labelsize": 7.5, "ytick.labelsize": 7.5, "legend.fontsize": 7.2, "legend.frameon": False,
        "axes.spines.top": False, "axes.spines.right": False, "axes.linewidth": 0.7,
        "xtick.major.width": 0.6, "ytick.major.width": 0.6, "pdf.fonttype": 42, "figure.dpi": 150,
    })
    return plt


def _ecdf(xs):
    xs = sorted(xs)
    n = len(xs)
    return xs, [(i + 1) / n for i in range(n)]


def _save(fig, out: Path, name: str):
    out.mkdir(parents=True, exist_ok=True)
    fig.savefig(out / f"{name}.pdf", bbox_inches="tight")
    fig.savefig(out / f"{name}.png", bbox_inches="tight", dpi=170)


def fig_mutation_rq1(spin: dict, out: Path):
    """(a) pooled mutant outcome per arm out of 350; (b) mutation score per project and arm."""
    plt = _mpl()
    models = arms(spin)
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(6.3, 2.75), gridspec_kw={"width_ratios": [1.05, 1.5], "wspace": 0.38})
    # (a)
    ys = list(range(len(models)))[::-1]
    for y, m in zip(ys, models):
        a = agg_mutation(spin, m)
        k, c, t = a["killed"], a["covered"], a["mutants"]
        ax1.barh(y, k, color=arm_color(m), height=0.62)
        ax1.barh(y, c - k, left=k, color=arm_color(m), alpha=0.35, height=0.62)
        ax1.barh(y, t - c, left=c, color="#efefef", height=0.62, edgecolor="#d0d0d0", linewidth=0.4)
        lo, hi = a["score_ci"]
        ax1.errorbar(k, y, xerr=[[k - lo * t], [hi * t - k]], fmt="none", ecolor="black", elinewidth=0.7, capsize=1.6)
        ax1.text(t + 4, y, f"{100*k/t:.0f} %", va="center", ha="left", fontsize=7.2)
        ax1.text(t + 4, y - 0.27, f"cov. {100*c/t:.0f} %", va="center", ha="left", fontsize=5.9, color="#666666")
    ax1.set_yticks(ys)
    ax1.set_yticklabels([plain_short(m) for m in models])
    ax1.set_xlim(0, 350 * 1.26)
    ax1.set_xticks([0, 100, 200, 300])
    ax1.set_xlabel("planted mutants (350)")
    ax1.set_title("(a) outcome of the planted mutants", loc="left")
    # (b)
    w = 0.8 / len(models)
    for i, m in enumerate(models):
        xs = [j + (i - (len(models) - 1) / 2) * w for j in range(len(PROJECTS))]
        vals = [100 * spin[(m, p)]["mutation_score"] if (m, p) in spin else 0 for p in PROJECTS]
        covs = [100 * spin[(m, p)]["coverage_rate"] if (m, p) in spin else 0 for p in PROJECTS]
        ax2.bar(xs, covs, width=w * 0.95, color=arm_color(m), alpha=0.3, linewidth=0)
        ax2.bar(xs, vals, width=w * 0.95, color=arm_color(m), label=plain_short(m))
    ax2.set_xticks(range(len(PROJECTS)))
    ax2.set_xticklabels(PROJECTS, rotation=20, ha="right")
    ax2.set_ylabel("% of the project's mutants")
    ax2.set_ylim(0, 100)
    ax2.set_title("(b) coverage (light) and mutation score (solid)", loc="left")
    ax2.legend(ncol=1, loc="upper left", bbox_to_anchor=(1.0, 1.02), handlelength=1.0, borderaxespad=0)
    _save(fig, out, "fig_mutation_rq1")
    plt.close(fig)


def fig_stability_cdf(spin: dict, out: Path):
    plt = _mpl()
    models = arms(spin)
    fig, ax = plt.subplots(figsize=(6.3, 2.4))
    for m in models:
        rs = [max(x, 1e-3) for (mm, p), r in spin.items() if mm == m for x in r["per_benchmark_rsd"] if x is not None]
        xs, ys = _ecdf(rs)
        ax.step(xs, ys, where="post", color=arm_color(m), lw=1.5 if m == "gemma-run9-ck1600" else 1.0,
                label=f"{plain_short(m)} (n={len(rs):,})")
    for x, t in ((1, "1 %"), (5, "5 %  ($r_{\\mathrm{RSD}}=1$)"), (25, "25 %  ($r_{\\mathrm{RSD}}=0$)")):
        ax.axvline(x, color="#999999", lw=0.6, ls="--")
        ax.text(x * 1.06, 0.04, t, fontsize=6.8, color="#666666", rotation=90, va="bottom", ha="left")
    ax.set_xscale("log")
    ax.set_xlim(0.01, 100)
    ax.set_ylim(0, 1.0)
    ax.set_xlabel("robust RSD of the baseline run per benchmark (%)")
    ax.set_ylabel("share of benchmarks")
    ax.legend(loc="upper left", ncol=1)
    _save(fig, out, "fig_stability_cdf")
    plt.close(fig)


def _excess_ecdf(ratios, xmin):
    """ECDF of relative slowdown r-1 for a log axis: pairs with r-1 <= xmin (no measurable slowdown, or faster)
    form the starting level at the left edge."""
    ex = sorted(r - 1.0 for r in ratios)
    n = len(ex)
    base = sum(1 for e in ex if e <= xmin) / n
    xs = [xmin] + [e for e in ex if e > xmin]
    ys = [base] + [(i + 1) / n for i in range(n) if ex[i] > xmin]
    return xs, ys, base


def fig_effect_cdf(spin: dict, sleep: dict, out: Path):
    """Relative slowdown (armed/baseline time - 1) on a log axis: 0.001 = 0.1 %, 1 = a factor of two.
    The compressed jump at ratio 1 of the plain-ratio plot spreads over two decades here."""
    plt = _mpl()
    xmin = 1e-3
    fig, ax = plt.subplots(figsize=(6.3, 2.6))
    ax.axvspan(0.02, 1.0, color="#dfe8f1", lw=0, label="targeted band: 2 % – 100 %")
    ax.axvline(0.10, color="#c2662d", lw=0.8, ls="--", label="kill threshold: 10 %")
    for m in arms(spin):  # thin per-arm lines, CPU burn
        es = [p[2] for (mm, pp), r in spin.items() if mm == m for p in r["pairs"] if p[2] and p[2] > 0]
        if es:
            xs, ys, _ = _excess_ecdf(es, xmin)
            ax.step(xs, ys, where="post", color=arm_color(m), lw=0.6, alpha=0.55)
    for cells, name, col in ((spin, "CPU burn", "#1f4e79"), (sleep, "sleep", "#7f7f7f")):
        es = [p[2] for r in cells.values() for p in r["pairs"] if p[2] and p[2] > 0]
        if not es:
            continue
        xs, ys, base = _excess_ecdf(es, xmin)
        ax.step(xs, ys, where="post", color=col, lw=1.8,
                label=f"{name}, all arms (n={len(es):,} pairs; {100*base:.0f} % show ≤ 0.1 %)")
    ax.set_xscale("log")
    ax.set_xlim(xmin, 1e8)
    ax.set_ylim(0, 1)
    ax.set_xticks([1e-3, 1e-2, 1e-1, 1, 1e1, 1e2, 1e3, 1e4, 1e5, 1e6, 1e7, 1e8])
    ax.set_xticklabels(["0.1 %", "1 %", "10 %", "×2", "×11", "×101", "$10^3$", "$10^4$", "$10^5$", "$10^6$", "$10^7$", "$10^8$"])
    ax.set_xlabel("relative slowdown of a covering benchmark (armed / baseline time − 1)")
    ax.set_ylabel("share of (mutant, benchmark) pairs")
    h, l = ax.get_legend_handles_labels()
    order = [i for i, x in enumerate(l) if "all arms" in x] + [i for i, x in enumerate(l) if "all arms" not in x]
    ax.legend([h[i] for i in order], [l[i] for i in order], loc="lower right", fontsize=6.9)
    _save(fig, out, "fig_effect_cdf")
    plt.close(fig)


def fig_llm4jmh_delta(L: dict, out: Path):
    plt = _mpl()
    fig, axes = plt.subplots(1, 3, figsize=(6.3, 2.2), sharey=True, gridspec_kw={"wspace": 0.12})
    grid = [i / 100 for i in range(1, 100)]
    colors = {"gptoss": "#c2662d", "gemini": "#4d4d4d", "qwen": "#b0b0b0", "llm2jmh": "#1f4e79"}
    for ax, (pid, pname) in zip(axes, LLM4JMH_PROJECTS):
        bug = L[pid]["bug"]
        n = len(bug)
        for arm, aname in LLM4JMH_ARMS:
            denom, _ = _llm4jmh_denominator(pid, bug)
            ys = [100 * sum(1 for v in bug.values() if any(b >= d for b in v.get(arm, []))) / denom for d in grid]
            ax.step(grid, ys, where="post", color=colors[arm], lw=1.7 if arm == "llm2jmh" else 1.0, label=aname)
        # two-line title: "Eclipse Collections (26 mutants)" is wider than one panel and ran into the Zipkin title
        ax.set_title(f"{pname}\n({_llm4jmh_denominator(pid, bug)[0]} mutants)", loc="left", linespacing=1.1)
        ax.set_xlim(0, 1)
        ax.set_ylim(0, 100)
        ax.set_xticks([0, 0.25, 0.5, 0.75, 1.0])
        ax.set_xticklabels(["0", ".25", ".5", ".75", "1"])
        ax.set_xlabel("bug-size threshold $\\delta$")
        ax.grid(axis="y", color="#eeeeee", lw=0.5)
    axes[0].set_ylabel("mutants detected (%)")
    axes[0].legend(loc="lower left", fontsize=6.6, handlelength=1.4, labelspacing=0.3)
    _save(fig, out, "fig_llm4jmh_delta")
    plt.close(fig)


def cmd_figures(root: Path, out: Path, exclude: set[str]):
    cells = load_cells(out)
    spin, sleep = filtered(cells["spin"], exclude), filtered(cells["sleep"], exclude)
    if not spin:
        sys.exit("no extracted cells — run `extract` first")
    fdir = out / "figures_ch4"
    fig_mutation_rq1(spin, fdir)
    fig_stability_cdf(spin, fdir)
    fig_effect_cdf(spin, sleep, fdir)
    try:
        fig_llm4jmh_delta(load_llm4jmh(root), fdir)
    except FileNotFoundError as e:
        print(f"warning: LLM4JMH data not reachable ({e.filename}); fig_llm4jmh_delta not regenerated", file=sys.stderr)
    print(f"wrote 4 figures (pdf+png) to {fdir}; excluded arms: {sorted(exclude) or 'none'}")


# --------------------------------------------------------------------------- macros (single source of truth for numbers quoted in prose)
MACRO_ARM = {"gemma-base": "Base", "gemma-rft": "RFT", "gemma-run9-ck1600": "GRPO", "gemma-e4b-base": "EFourB",
             "gpt-oss-120b": "GPTOSS", "dsv4": "DSV", "gpt-5.6-luna": "Luna"}
MACRO_PROJECT = {"commons-compress": "Compress", "decimal4j": "Decimal", "fastfilter": "Fastfilter", "hppc": "Hppc",
                 "jodd-util": "Jodd", "snakeyaml": "Snakeyaml"}


def _ns(x: float) -> str:
    if x < 1e3:
        return f"{x:.1f}\\,ns"
    if x < 1e6:
        return f"{x/1e3:.1f}\\,$\\mu$s"
    return f"{x/1e6:.1f}\\,ms"


def macros(spin: dict, sleep: dict, eff: dict, gen: dict, mut: dict) -> str:
    L = ["% Auto-generated by evaluation/results_tables.py — numbers quoted in the prose of Ch. 3 and Ch. 4.",
         "% \\input this file in the preamble of main.tex. Do not edit by hand; re-run the script.",
         f"% Arms included: {', '.join(arms(spin))}", ""]
    def cmd(name, val):
        L.append(f"\\newcommand{{\\{name}}}{{{val}}}")
    e = eff["__all__|spin"]
    cmd("EffPairs", num(e["pairs"]))
    cmd("EffDetectedShare", pct(e["detected"] / e["pairs"]))
    cmd("EffMedian", f"{e['median']:.3f}")
    cmd("EffUpperQuartile", f"{e['p75']:.2f}" if e["p75"] < 100 else f"{e['p75']:.0f}")
    cmd("EffNinetyFifth", f"{e['p95']:.0f}")
    cmd("EffMax", num(e["max"]))
    cmd("EffBelowBand", pct(e["lt102"]))
    cmd("EffInBand", pct(e["band"]))
    cmd("EffAboveBand", pct(e["gt2"]))
    if "__all__|sleep" in eff:
        es = eff["__all__|sleep"]
        cmd("EffSleepPairs", num(es["pairs"]))
        cmd("EffSleepMedian", fx(es["median"]))
        cmd("EffSleepBelowBand", pct(es["lt102"]))
        cmd("EffSleepDetectedShare", pct(es["detected"] / es["pairs"]))
    # per project: median slowdown over measured pairs, and median baseline cost per invocation of the
    # covering benchmarks that appear in those pairs (chapter arms); OpTimeAll... = over all benchmarks
    for p in PROJECTS:
        es, ns_cov, ns_all = [], [], []
        for (m, proj), r in spin.items():
            if proj != p:
                continue
            bn = r["baseline_ns_per_op"]
            ns_all += list(bn.values())
            seen = set()
            for pp in r["pairs"]:
                if pp[2] and pp[2] > 0:
                    es.append(pp[2])
                if pp[1] in bn and pp[1] not in seen:
                    seen.add(pp[1])
                    ns_cov.append(bn[pp[1]])
        if es:
            cmd(f"EffMedian{MACRO_PROJECT[p]}", fx(statistics.median(es)))
        if ns_cov:
            cmd(f"OpTime{MACRO_PROJECT[p]}", _ns(statistics.median(ns_cov)))
        if ns_all:
            cmd(f"OpTimeAll{MACRO_PROJECT[p]}", _ns(statistics.median(ns_all)))
    # per-arm headline numbers
    for m in arms(spin):
        k = MACRO_ARM.get(m)
        if not k:
            continue
        g, a = gen[m], mut["pooled"][m]
        cmd(f"GenRate{k}", pct(g["succeeded"] / g["classes_total"]))
        cmd(f"GenCount{k}", str(g["succeeded"]))
        cmd(f"FirstTry{k}", pct(g["first_attempt"] / g["classes_total"]))
        cmd(f"Bench{k}", num(g["benchmarks"]))
        cmd(f"Cov{k}", pct(a["coverage"]))
        cmd(f"Covered{k}", str(a["covered"]))
        cmd(f"Killed{k}", str(a["killed"]))
        cmd(f"KillGivenCov{k}", pct(a["kill_given_covered"]))
        cmd(f"Score{k}", pct(a["score_pooled"]))
        lo, hi = a["score_ci"]
        cmd(f"ScoreCI{k}", f"[{pct(lo)}, {pct(hi)}]")
        sl = [r for (mm, p), r in sleep.items() if mm == m]
        if sl:
            asl = agg_mutation(sleep, m)
            cmd(f"ScoreSleep{k}", pct(asl["score_pooled"]))
            cmd(f"KillGivenCovSleep{k}", pct(asl["kill_given_covered"]))
    return "\n".join(L) + "\n"


# --------------------------------------------------------------------------- skeleton
def _fig(path: str, caption: str, label: str, width: str = r"\textwidth") -> str:
    return (f"\\begin{{figure}}[htbp]\n\\centering\n\\includegraphics[width={width}]{{{path}}}\n"
            f"\\caption{{{caption}}}\n\\label{{{label}}}\n\\end{{figure}}\n")


FIGS = {
    "fig_mutation_rq1": ("Mutation testing on the six held-out projects with the CPU-burn operator. (a) Outcome of the 350 planted mutants per arm: killed (solid), covered but not killed (light), not covered (grey); labels give the pooled mutation score and coverage, the whiskers the Wilson 95\\,\\% interval of the score. (b) Per project: coverage (light bar) and mutation score (solid bar, always contained in the coverage bar).", "fig:mutation-rq1"),
    "fig_stability_cdf": ("Distribution of the robust RSD of the baseline measurement per benchmark and arm (empirical CDF, log scale). Dashed lines mark LLM4JMH's 1\\,\\% stability band and the 5\\,\\% and 25\\,\\% thresholds of $r_{\\mathrm{RSD}}$.", "fig:stability-cdf"),
    "fig_effect_cdf": ("Relative slowdown observed on covering benchmarks when a mutant is armed, for the inherited sleep operator and the CPU-burn operator (empirical CDF over all measured pairs; log axis of armed/baseline time $-$ 1, so 0.1\\,\\% to a factor of $10^{8}$; thin lines: CPU burn per arm). Pairs with no measurable slowdown form the level at the left edge. The shaded band is the targeted 2--100\\,\\% range, the dashed line the 10\\,\\% kill threshold.", "fig:effect-cdf"),
    "fig_failure_analysis": ("Coverage (light) and kill rate (solid) of the planted mutants by the role of the mutated method, per arm; $n$ = mutants of that role across the six projects, the small number above each bar is the kill rate in \\%. Roles are assigned from the mutated method's name and signature (\\autoref{tab:mutant-kinds}, appendix).", "fig:failure-analysis"),
    "fig_llm4jmh_delta": ("LLM4JMH replication: share of the planted intersection mutants detected per arm as a function of the bug-size threshold $\\delta$ (bug size $\\geq \\delta$ on any covering benchmark).", "fig:llm4jmh-delta"),
}


EXTERNAL_CHAPTER = ["tab_compile_errors"]   # from failure_analysis.py
EXTERNAL_APPENDIX = ["tab_failure_decomposition", "tab_mutant_kinds", "tab_compile_errors_full", "tab_class_features"]


def skeleton(tables_dir_rel: str, figs_dir_rel: str, inline: dict[str, str] | None, excluded: list[str]) -> str:
    def T(name):
        if inline is not None and name not in inline:
            return f"% {name}.tex not found — run evaluation/failure_analysis.py first"
        return inline[name] if inline else f"\\input{{{tables_dir_rel}/{name}.tex}}"
    def F(name):
        cap, lab = FIGS[name]
        return _fig(f"{figs_dir_rel}/{name}.pdf", cap, lab)
    excl = ", ".join(excluded) if excluded else "none"
    return rf"""% !TeX root = ../main.tex
% Chapter 4 — Results. Skeleton only: structure, tables, figures; no prose.
% Target length ~3,000 words: setup 450 · RQ1 1,200 · RQ2 550 · RQ3 550 · summary 250.
% Tables, figures and results_macros.tex are generated by evaluation/results_tables.py from
% /Volumes/SamsungSSD/jmh-thesis/data_for_thesis and nothing else. Quote numbers in prose via the
% macros (\EffMedian, \ScoreGRPO, ...) so Ch. 3 and Ch. 4 stay consistent; re-run the script instead
% of editing numbers by hand. Arms excluded from chapter tables/figures: {excl} (kept in the appendix).
% Preamble: booktabs, multirow, graphicx, amsmath, amssymb; \input{{results_macros.tex}}.
\chapter{{Results}}\label{{chapter:results}}

\section{{Research Questions}}\label{{sec:results-rqs}}
\begin{{description}}
  \item[RQ1] Does GRPO post-training with verifiable rewards improve the JMH benchmarks that Gemma-4 E2B generates for held-out projects, and how does the post-trained 2B model compare with its base model, its larger sibling Gemma-4 E4B and large or proprietary models?
  \item[RQ2] How does the mutation operator---the inherited \texttt{{Thread.sleep(0,1)}} versus the calibrated CPU-burn operator---change the measured mutation scores and the ranking of the models?
  \item[RQ3] How does the post-trained model compare with the models studied by LLM4JMH on their projects and under their bug-size metric?
\end{{description}}

\section{{Experimental Setup}}\label{{sec:results-setup}}
% ~450 words. Projects (\autoref{{tab:eval-projects}}); arms and decoding (\autoref{{tab:eval-models}}, appendix);
% hosts, JMH preset, kill rule (\autoref{{tab:eval-setup}}, appendix); metrics by reference to
% \autoref{{sec:reward}}, \autoref{{sec:reward-mutation}}, \autoref{{sec:mutation-detection}}; Wilson 95\,\% intervals.
{T("tab_eval_projects")}

\section{{RQ1: Effect of GRPO Post-Training}}\label{{sec:results-rq1}}
% ~1,200 words.
\subsection{{Generation and Compilation}}\label{{sec:results-generation}}
{T("tab_generation")}
% Per-project breakdown: \autoref{{tab:generation-by-project}} (appendix).
\subsection{{Mutation Coverage and Detection}}\label{{sec:results-mutation}}
{T("tab_mutation_rq1")}
{F("fig_mutation_rq1")}
% Killed/covered counts per project: \autoref{{tab:mutation-spin}} (appendix). Pooled numbers via macros:
% \ScoreBase, \ScoreGRPO, \ScoreEFourB, \ScoreGPTOSS (with \ScoreCI...), \CovGRPO, \KillGivenCovGRPO.
\subsection{{Measurement Stability}}\label{{sec:results-stability}}
{F("fig_stability_cdf")}
% Numbers: \autoref{{tab:stability}} (appendix). Cost: \autoref{{tab:cost}} (appendix).
\subsection{{Failure Analysis}}\label{{sec:results-failures}}
% ~350 words. Figure + table from evaluation/failure_analysis.py (data_for_thesis + JMH-Bench mutants.yaml + project catalogue).
% Text: where the missed mutants are lost (per arm / project, numbers from tab:failure-decomposition);
% figure: which method roles the suites reach; table: why first attempts fail to compile.
{F("fig_failure_analysis")}
{T("tab_compile_errors")}
% Appendix: \autoref{{tab:failure-decomposition}} (outcome decomposition), \autoref{{tab:mutant-kinds}} (numbers behind the figure);
% full diagnostic categories: \autoref{{tab:compile-errors-full}}; class size / genericity: \autoref{{tab:class-features}} (appendix).

\section{{RQ2: Impact of the Mutation Operator}}\label{{sec:results-rq2}}
% ~550 words. Same suites, only the operator changed; the sleep campaign covers four arms.
{T("tab_mutation_summary")}
{F("fig_effect_cdf")}
{T("tab_operator_effect")}
% Per-project sleep matrix: \autoref{{tab:mutation-sleep}} (appendix).

\section{{RQ3: Comparison with LLM4JMH}}\label{{sec:results-rq3}}
% ~550 words. Replanted intersection mutants (30 per project, seed 42), HWO = Thread.sleep(0,1),
% bug size from the 99\,\% bootstrap CI, JMH -f 1 -wi 5 -i 30; JDK per project (RxJava, Eclipse:
% javac 17 / java 21; Zipkin: javac 11 / java 21). Suites as built: \autoref{{tab:llm4jmh-suites}} (appendix).
{T("tab_llm4jmh_coverage")}
{F("fig_llm4jmh_delta")}
{T("tab_llm4jmh_detection")}
% Other aggregation conventions of the LLM4JMH notebook: \autoref{{tab:llm4jmh-conventions}} (appendix).

\section{{Summary}}\label{{sec:results-summary}}
% ~250 words, one paragraph per RQ. Interpretation, threats to validity and future work
% belong to the Discussion chapter.
"""


def appendix(tables_dir_rel: str, inline: dict[str, str] | None, excluded: list[str]) -> str:
    def T(name):
        if inline is not None and name not in inline:
            return f"% {name}.tex not found — run evaluation/failure_analysis.py first"
        return inline[name] if inline else f"\\input{{{tables_dir_rel}/{name}.tex}}"
    note = ""
    if excluded:
        note = (f"% Arms excluded from the chapter tables but kept here for completeness: {', '.join(excluded)}.\n"
                "% dsv4 (DeepSeek-V4 Flash): 255 of its 259 failed classes end with `model output has no class\n"
                "% declaration', i.e. the API returned no Java class within the 16,384-token completion budget\n"
                "% (its reasoning traces are 2-3x longer than GPT-OSS's). A truncation artefact, not a capability\n"
                "% result; see the Discussion chapter.\n")
    return rf"""% !TeX root = ../main.tex
% Appendix — additional evaluation data for \autoref{{chapter:results}}. Generated tables only (all arms).
{note}\chapter{{Additional Evaluation Data}}\label{{app:results}}

\section{{Setup Details}}\label{{app:results-setup}}
{T("tab_eval_models")}
{T("tab_eval_setup")}

\section{{Per-Project Results}}\label{{app:results-projects}}
{T("tab_generation_by_project")}
{T("tab_mutation_spin")}
{T("tab_mutation_sleep")}
{T("tab_stability")}

\section{{Failure Analysis}}\label{{app:results-failures}}
{T("tab_failure_decomposition")}
{T("tab_mutant_kinds")}
{T("tab_compile_errors_full")}
{T("tab_class_features")}

\section{{Cost}}\label{{app:results-cost}}
{T("tab_cost")}

\section{{LLM4JMH Replication}}\label{{app:results-llm4jmh}}
{T("tab_llm4jmh_suites")}
{T("tab_llm4jmh_conventions")}
"""


# --------------------------------------------------------------------------- main
CHAPTER_TABLES = ["tab_eval_projects", "tab_generation", "tab_mutation_rq1", "tab_mutation_summary", "tab_operator_effect",
                  "tab_llm4jmh_coverage", "tab_llm4jmh_detection"]
APPENDIX_TABLES = ["tab_eval_models", "tab_eval_setup", "tab_generation_by_project", "tab_mutation_spin", "tab_mutation_sleep",
                   "tab_stability", "tab_cost", "tab_llm4jmh_suites", "tab_llm4jmh_conventions"]


def build_tables(root: Path, spin: dict, sleep: dict) -> tuple[dict[str, str], dict]:
    produced, numbers = {}, {}

    def emit(name, tex, nums):
        produced[name] = tex
        numbers[name] = nums

    emit("tab_eval_projects", *t_eval_projects(spin))
    emit("tab_eval_models", *t_eval_models(spin))
    emit("tab_eval_setup", *t_eval_setup(spin, sleep))
    emit("tab_generation", *t_generation(spin))
    emit("tab_generation_by_project", *t_generation_by_project(spin))
    emit("tab_mutation_spin", *t_mutation_matrix(spin, "spin", "tab:mutation-spin",
         "Mutation detection per project and arm with the CPU-burn operator ($\\approx$250\\,ns per hit)."))
    emit("tab_mutation_sleep", *t_mutation_matrix(sleep, "sleep", "tab:mutation-sleep",
         "Mutation detection per project and arm with the inherited sleep operator (\\texttt{Thread.sleep(0,1)}, $\\approx$1.2\\,ms per hit)."))
    emit("tab_mutation_rq1", *t_mutation_rq1(spin))
    emit("tab_mutation_summary", *t_mutation_summary(spin, sleep))
    emit("tab_operator_effect", *t_operator_effect(spin, sleep))
    emit("tab_stability", *t_stability(spin))
    emit("tab_cost", *t_cost(spin))
    try:
        L = load_llm4jmh(root)
    except FileNotFoundError as e:
        print(f"warning: LLM4JMH data not reachable ({e.filename}); keeping the previously generated LLM4JMH tables", file=sys.stderr)
        L = None
    if L is not None:
        emit("tab_llm4jmh_detection", *t_llm4jmh_detection(L))
        emit("tab_llm4jmh_coverage", *t_llm4jmh_coverage(L))
        emit("tab_llm4jmh_suites", *t_llm4jmh_suites())
        emit("tab_llm4jmh_conventions", *t_llm4jmh_conventions(L))
    return produced, numbers


def cmd_tables(root: Path, out: Path, exclude: set[str]):
    cells = load_cells(out)
    spin_all, sleep_all = cells["spin"], cells["sleep"]
    if not spin_all:
        sys.exit("no extracted cells — run `extract` first")
    excluded = sorted(e for e in exclude if any(m == e for (m, p) in spin_all))

    # chapter tables: excluded arms removed; appendix tables: every arm
    main_tex, main_nums = build_tables(root, filtered(spin_all, exclude), filtered(sleep_all, exclude))
    app_tex, app_nums = build_tables(root, spin_all, sleep_all)

    tdir, adir = out / "tables_ch4", out / "tables_appendix"
    tdir.mkdir(parents=True, exist_ok=True)
    adir.mkdir(parents=True, exist_ok=True)
    for name in CHAPTER_TABLES:
        if name in main_tex:
            (tdir / f"{name}.tex").write_text(main_tex[name])
        elif (tdir / f"{name}.tex").exists():
            main_tex[name] = (tdir / f"{name}.tex").read_text()
    for name in APPENDIX_TABLES:
        if name in app_tex:
            (adir / f"{name}.tex").write_text(app_tex[name])
        elif (adir / f"{name}.tex").exists():
            app_tex[name] = (adir / f"{name}.tex").read_text()

    for name in EXTERNAL_CHAPTER:
        f = tdir / f"{name}.tex"
        if f.exists():
            main_tex[name] = f.read_text()
    for name in EXTERNAL_APPENDIX:
        f = adir / f"{name}.tex"
        if f.exists():
            app_tex[name] = f.read_text()
    (out / "chapter4_results_skeleton.tex").write_text(skeleton("tables_ch4", "figures_ch4", None, excluded))
    (out / "chapter4_results_skeleton_inline.tex").write_text(skeleton("tables_ch4", "figures_ch4", main_tex, excluded))
    spin_m, sleep_m = filtered(spin_all, exclude), filtered(sleep_all, exclude)
    (out / "results_macros.tex").write_text(macros(spin_m, sleep_m, main_nums["tab_operator_effect"],
                                                   main_nums["tab_generation"], main_nums["tab_mutation_spin"]))
    (out / "appendix_results_skeleton.tex").write_text(appendix("tables_appendix", None, excluded))
    (out / "appendix_results_skeleton_inline.tex").write_text(appendix("tables_appendix", app_tex, excluded))
    numbers = {"cells": {k: sorted(f"{m}-{p}" for (m, p) in v) for k, v in cells.items()},
               "excluded_from_chapter": excluded, "chapter": main_nums, "appendix_all_arms": app_nums}
    json.dump(numbers, open(out / "results_ch4_numbers.json", "w"), indent=1, default=str)
    print(f"chapter: {len(CHAPTER_TABLES)} tables -> {tdir}; appendix: {len(APPENDIX_TABLES)} tables -> {adir}; "
          f"excluded from chapter: {excluded or 'none'}")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("stage", choices=["extract", "tables", "figures"])
    ap.add_argument("--data", required=True)
    ap.add_argument("--out", required=True)
    ap.add_argument("--only", choices=list(CAMPAIGNS), default=None, help="extract: restrict to one campaign")
    ap.add_argument("--exclude", default="", help="tables/figures: comma-separated arm prefixes to drop from the chapter tables and figures (kept in the appendix)")
    ap.add_argument("--force", action="store_true", help="extract: overwrite existing per-cell extracts")
    ap.add_argument("--llm4jmh-denominator", choices=["planted", "sampled", "alldata"], default="planted",
                    help="denominator of the LLM4JMH detection table/figure (default: planted mutants)")
    a = ap.parse_args()
    root, out = Path(a.data), Path(a.out)
    out.mkdir(parents=True, exist_ok=True)
    exclude = {e.strip() for e in a.exclude.split(",") if e.strip()}
    global LLM4JMH_DENOM
    LLM4JMH_DENOM = a.llm4jmh_denominator
    if a.stage == "extract":
        cmd_extract(root, out, a.only, a.force)
    elif a.stage == "figures":
        cmd_figures(root, out, exclude)
    else:
        cmd_tables(root, out, exclude)


if __name__ == "__main__":
    main()
