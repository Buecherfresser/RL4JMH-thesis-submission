"""Markdown export of a run's telemetry -- the same numbers the dashboard shows, pasteable.

Deliberately opinionated about what goes at the top: the health checks that have silently
invalidated past runs (weight sync, the JMH lock, zero-advantage steps) come before the reward
curve, because a beautiful reward curve on rollouts sampled from the base model is what runs 1-6
looked like.
"""

from __future__ import annotations

import statistics as st
from typing import Any

from jmhgen.dashboard.load import RunData
from jmhgen.dashboard.series import (
    COMPONENTS,
    failure_table,
    project_table,
    realised_weights,
    summary,
)

NOMINAL_HINT = {
    "compile": 0.10,
    "runtime": 0.10,
    "anti_pattern": 0.05,
    "rsd": 0.05,
    "mutation": 0.70,
}


def _pct(value: Any, digits: int = 1) -> str:
    return "—" if not isinstance(value, (int, float)) else f"{100 * value:.{digits}f} %"


def _num(value: Any, digits: int = 4) -> str:
    return "—" if not isinstance(value, (int, float)) else f"{value:.{digits}f}"


def _health(data: RunData, s: dict[str, Any]) -> list[tuple[str, str, str]]:
    """(check, value, verdict). Thresholds come from measured run-7/run-8 behaviour."""
    out: list[tuple[str, str, str]] = []

    roc = s["run_over_compile"]
    out.append((
        "ran / compiled",
        _num(roc, 2),
        "ok" if roc is None or roc >= 0.85 else
        "WARN — compiled benchmarks are not being measured (JMH lock? runtime errors?)",
    ))
    if not s.get("jmh_lock_recorded"):
        out.append((
            "rollouts lost to the JMH lock",
            "not recorded",
            "UNKNOWN — this run predates run_diagnostics, so a lost lock is indistinguishable "
            "from a benchmark that threw",
        ))
    else:
        out.append((
            "rollouts lost to the JMH lock",
            str(s["jmh_lock_losses"]),
            "ok" if not s["jmh_lock_losses"] else
            "FAIL — set a per-invocation java.io.tmpdir; these are false-negative zeros",
        ))
    z = s["zero_std_frac_last"]
    out.append((
        "zero-advantage steps (last 50)",
        _pct(z),
        "ok" if z is None or z < 0.5 else
        "WARN — over half of recent steps produced no gradient at all",
    ))
    el = s["mutation_eligible_frac"]
    out.append((
        "rollouts reaching the mutation term",
        _pct(el),
        "ok" if el is None or el >= 0.15 else
        "WARN — the 0.70-weight signal is gated behind a small fraction of rollouts",
    ))
    ms = s["mutation_score_mean"]
    out.append((
        "mutation score (eligible)",
        _num(ms, 3),
        "ok" if ms is None or ms >= 0.5 else
        "WARN — check whether concurrent JMH is perturbing the slowdown test",
    ))
    return out


def render(data: RunData, *, top_projects: int = 12, top_failures: int = 15) -> str:
    s = summary(data)
    steps = data.merged_steps()
    lines: list[str] = []
    add = lines.append

    add(f"# GRPO telemetry — `{s['run']}`")
    add("")
    add(f"{s['steps']} logged steps · {s['rollouts']} rollouts · "
        f"epoch {_num(s['epoch'], 2)} · {_num(s['wall_clock_h'], 2)} h of step time")
    add("")

    add("## Health")
    add("")
    add("| check | value | verdict |")
    add("|---|---|---|")
    for check, value, verdict in _health(data, s):
        add(f"| {check} | {value} | {verdict} |")
    add("")
    add("> Weight sync is **not** checkable from these files — it is certified in the run log "
        "(`[weight-sync] VERIFIED`). Runs 1–6 sampled every rollout from the base model and no "
        "metric here could see it.")
    add("")

    add("## Headline")
    add("")
    add("| metric | first 50 steps | last 50 steps |")
    add("|---|---|---|")
    add(f"| reward | {_num(s['reward_first'])} | {_num(s['reward_last'])} |")
    add(f"| compile_frac | {_pct(s['compile_first'])} | {_pct(s['compile_last'])} |")
    add(f"| step time | — | {_num(s['step_time_last'], 1)} s |")
    add("")

    add("## Reward composition")
    add("")
    add("Realised share is what the policy is actually paid for, after gating: the mutation term "
        "only pays on rollouts that compiled *and* ran, so its realised share is well below its "
        "nominal weight while compile's is above.")
    add("")
    add("| component | mean | realised share of reward | nominal weight |")
    add("|---|---|---|---|")
    realised = {r["component"]: r for r in realised_weights(data)}
    for name in COMPONENTS:
        vals = [row.get(f"{name}_mean") for row in data.components]
        vals = [v for v in vals if isinstance(v, (int, float))]
        add(f"| {name} | {_num(st.mean(vals), 4) if vals else '—'} | "
            f"{_pct(realised[name]['realised_share'])} | {NOMINAL_HINT.get(name, '—')} |")
    add("")

    add("## Funnel")
    add("")
    add("| stage | share of rollouts |")
    add("|---|---|")
    unparsed = sum(1 for r in data.rollouts if not r.get("parse_ok"))
    add(f"| parsed | {_pct(1 - unparsed / max(len(data.rollouts), 1))} |")
    add(f"| compiled | {_pct(s['compile_frac'])} |")
    add(f"| ran | {_pct(s['run_frac'])} |")
    add(f"| mutation-eligible | {_pct(s['mutation_eligible_frac'])} |")
    add("")

    if steps:
        add("## Trend (10 blocks)")
        add("")
        add("| steps | reward | compile | run/comp | mutation | zero-adv | step time |")
        add("|---|---|---|---|---|---|---|")
        n = len(steps)
        for i in range(10):
            chunk = steps[i * n // 10 : (i + 1) * n // 10]
            if not chunk:
                continue

            def mean(key: str, rows: list[dict[str, Any]] = chunk) -> float | None:
                vals = [v for r in rows if isinstance((v := r.get(key)), (int, float))]
                return st.mean(vals) if vals else None

            c, r = mean("compile_frac"), mean("run_frac")
            add(f"| {int(chunk[0]['step'])}–{int(chunk[-1]['step'])} | {_num(mean('reward'))} | "
                f"{_pct(c)} | {_num(r / c, 2) if c and r is not None else '—'} | "
                f"{_num(mean('mutation_score_mean'), 3)} | {_pct(mean('zero_std_frac'), 0)} | "
                f"{_num(mean('step_time'), 1)} s |")
        add("")

    failures = failure_table(data, limit=top_failures)
    if failures["compile"]:
        add("## Compile failures")
        add("")
        add("| count | share of failures | javac diagnostic |")
        add("|---|---|---|")
        for row in failures["compile"]:
            add(f"| {row['count']} | {_pct(row['share_of_failures'])} | {row['message'][:110]} |")
        add("")
    if failures["run"]:
        add("## Run failures")
        add("")
        add("| count | diagnostic |")
        add("|---|---|")
        for row in failures["run"]:
            add(f"| {row['count']} | {row['message'][:110]} |")
        add("")

    projects = project_table(data)
    if projects:
        add(f"## Per-project yield (worst {top_projects} by compile rate)")
        add("")
        add("| project | rollouts | compile | ran | reward | s / compiled |")
        add("|---|---|---|---|---|---|")
        for row in projects[:top_projects]:
            add(f"| {row['project']} | {row['n']} | {_pct(row['compile_frac'])} | "
                f"{_pct(row['run_frac'])} | {_num(row['reward_mean'], 3)} | "
                f"{_num(row['seconds_per_compiled'], 1)} |")
        add("")

    if data.gpu:
        add("## GPU")
        add("")
        add("| gpu | mean util | mean mem | mean power |")
        add("|---|---|---|---|")
        by_gpu: dict[Any, list[dict[str, Any]]] = {}
        for row in data.gpu:
            by_gpu.setdefault(row.get("gpu"), []).append(row)
        for gpu in sorted(by_gpu, key=lambda g: (g is None, g)):
            rows = by_gpu[gpu]

            def mean(key: str, rs: list[dict[str, Any]] = rows) -> float | None:
                vals = [v for r in rs if isinstance((v := r.get(key)), (int, float))]
                return st.mean(vals) if vals else None

            add(f"| {int(gpu) if isinstance(gpu, (int, float)) else '?'} | "
                f"{_pct(mean('util'), 0)} | {_pct(mean('mem_used_frac'), 0)} | "
                f"{_num(mean('power_w'), 0)} W |")
        add("")

    if not data.completions:
        add("> No generations were captured. Set `capture_completions_every` in the GRPO config "
            "to record a bounded sample of rollout sources for side-by-side inspection.")
        add("")
    return "\n".join(lines)
