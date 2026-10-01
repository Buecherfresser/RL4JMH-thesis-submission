"""Turn a :class:`~jmhgen.dashboard.load.RunData` into the chart series and tables the UI needs.

The panel set is chosen from what actually went wrong in runs 1-8, not from what is easy to plot:

* **Weight sync** -- runs 1-6 sampled every rollout from the BASE model and no in-run telemetry
  could see it. `grad_norm` and reward trend are the proxies; the run log carries the certificate.
* **Zero-advantage steps** -- 42 % of run-7 steps had `frac_reward_zero_std == 1`, i.e. the whole
  step's wall clock bought no gradient. That is a first-class panel, not a footnote.
* **Realised vs nominal reward weights** -- mutation is nominally 0.70 but gated behind
  compile+run, so it realised as 0.55 while compile realised at 2x its nominal weight. The
  weighted-component split is the only way to see what the policy is actually being paid for.
* **ran / compiled** -- the JMH lock silently zeroed 40 % of compiled rollouts in run 7. A ratio
  below ~0.9 means benchmarks that compiled are not being scored.
* **Funnel** -- parse -> compile -> run -> mutation-eligible, because only ~12-20 % of rollouts
  ever reach the term carrying 70 % of the reward weight.
"""

from __future__ import annotations

import statistics as st
from collections.abc import Sequence
from typing import Any

from jmhgen.dashboard.load import RunData

COMPONENTS = ("compile", "runtime", "anti_pattern", "rsd", "mutation")


def _f(value: Any) -> float | None:
    return value if isinstance(value, (int, float)) else None


def bin_series(
    xs: Sequence[float], ys: Sequence[float | None], bins: int = 120
) -> list[list[float]]:
    """Downsample to at most ``bins`` points by averaging, so a 5000-step run stays plottable."""
    pairs = [(x, y) for x, y in zip(xs, ys, strict=False) if y is not None]
    if not pairs:
        return []
    if len(pairs) <= bins:
        return [[x, y] for x, y in pairs]
    size = len(pairs) / bins
    out: list[list[float]] = []
    for i in range(bins):
        chunk = pairs[int(i * size) : int((i + 1) * size)] or None
        if not chunk:
            continue
        out.append([chunk[len(chunk) // 2][0], sum(p[1] for p in chunk) / len(chunk)])
    return out


def rolling(values: Sequence[float | None], window: int) -> list[float | None]:
    """Trailing mean over the last ``window`` non-null values; keeps None where nothing is known."""
    out: list[float | None] = []
    buf: list[float] = []
    for v in values:
        if v is not None:
            buf.append(v)
            if len(buf) > window:
                buf.pop(0)
        out.append(sum(buf) / len(buf) if buf else None)
    return out


def build_charts(data: RunData, bins: int = 120, smooth: int = 25) -> dict[str, Any]:
    """Every time series the dashboard plots, already binned for the browser."""
    steps = data.merged_steps()
    x = [row["step"] for row in steps]

    def series(key: str, *, smoothed: bool = True) -> dict[str, Any]:
        raw = [_f(row.get(key)) for row in steps]
        out: dict[str, Any] = {"raw": bin_series(x, raw, bins)}
        if smoothed:
            out["smooth"] = bin_series(x, rolling(raw, smooth), bins)
        return out

    charts: dict[str, Any] = {
        "reward": series("reward"),
        "reward_std": series("reward_std"),
        "zero_std_frac": series("zero_std_frac"),
        "compile_frac": series("compile_frac"),
        "run_frac": series("run_frac"),
        "parse_fail_frac": series("parse_fail_frac"),
        "mutation_eligible_frac": series("mutation_eligible_frac"),
        "mutation_score_mean": series("mutation_score_mean"),
        "mutation_coverage_rate_mean": series("mutation_coverage_rate_mean"),
        "step_time": series("step_time"),
        "grad_norm": series("grad_norm"),
        "learning_rate": series("learning_rate", smoothed=False),
        "loss": series("loss"),
        "epoch": series("epoch", smoothed=False),
        "rollouts": series("rollouts", smoothed=False),
    }

    # ran/compiled: compiled rollouts that never produced a measurement. Run 7 sat at 0.59
    # because of the JMH lock; a healthy run is ~1.0.
    ratio = [
        (c and r is not None and c > 0) and (r / c) or None
        for c, r in ((_f(row.get("compile_frac")), _f(row.get("run_frac"))) for row in steps)
    ]
    charts["run_over_compile"] = {
        "raw": bin_series(x, ratio, bins),
        "smooth": bin_series(x, rolling(ratio, smooth), bins),
    }

    # Trainer-side completion length, which only exists in the rollout summaries.
    tm_x, tm_len, tm_ent, tm_clip, tm_max = [], [], [], [], []
    for row in data.summaries:
        metrics = row.get("trainer_metrics") or {}
        step = row.get("step")
        if step is None or not metrics:
            continue
        tm_x.append(step)
        tm_len.append(_f(metrics.get("completions/mean_length")))
        tm_ent.append(_f(metrics.get("entropy")))
        tm_clip.append(_f(metrics.get("completions/clipped_ratio")))
        tm_max.append(_f(metrics.get("completions/max_length")))
    charts["completion_length"] = {"raw": bin_series(tm_x, tm_len, bins),
                                   "smooth": bin_series(tm_x, rolling(tm_len, smooth), bins)}
    charts["entropy"] = {"raw": bin_series(tm_x, tm_ent, bins),
                         "smooth": bin_series(tm_x, rolling(tm_ent, smooth), bins)}
    charts["clipped_ratio"] = {"raw": bin_series(tm_x, tm_clip, bins)}
    # The honest truncation signal: the longest completion in a step, in tokens. Compare it to
    # max_completion_length by eye. `clipped_ratio` above cannot do this job -- see the UI note.
    charts["max_completion_length"] = {"raw": bin_series(tm_x, tm_max, bins),
                                       "smooth": bin_series(tm_x, rolling(tm_max, smooth), bins)}

    # Reward composition: raw component means and the weighted contributions that actually move
    # the policy. The gap between them is the "realised vs nominal" story.
    comp_x = [row["step"] for row in data.components if row.get("step") is not None]
    for name in COMPONENTS:
        for prefix in ("", "weighted_"):
            key = f"{prefix}{name}_mean"
            vals = [_f(row.get(key)) for row in data.components]
            charts[key] = {"raw": bin_series(comp_x, rolling(vals, smooth), bins)}

    if data.gpu:
        gx = [row.get("t") for row in data.gpu]
        for key in ("util", "mem_used_frac", "power_w", "temp_c"):
            if any(row.get(key) is not None for row in data.gpu):
                ys = [_f(r.get(key)) for r in data.gpu]
                charts[f"gpu_{key}"] = {"raw": bin_series(gx, ys, bins)}
    return charts


def realised_weights(data: RunData) -> list[dict[str, Any]]:
    """Share of total realised reward each component contributed, vs its nominal weight.

    Run 7: mutation nominal 0.70 realised 0.55, compile nominal 0.10 realised 0.20. The policy is
    paid mostly for clearing the compile+run gate, which is why it learned to write shorter
    benchmarks -- see docs/grpo-gemma-mutation70-run7.md.
    """
    totals = dict.fromkeys(COMPONENTS, 0.0)
    for row in data.components:
        for name in COMPONENTS:
            value = _f(row.get(f"weighted_{name}_mean"))
            if value:
                totals[name] += value
    grand = sum(totals.values())
    return [
        {
            "component": name,
            "realised_share": (totals[name] / grand) if grand else None,
            "total": totals[name],
        }
        for name in COMPONENTS
    ]


def summary(data: RunData) -> dict[str, Any]:
    """Headline numbers, plus the health checks worth failing a run over."""
    steps = data.merged_steps()
    rollouts = data.rollouts
    n = len(rollouts) or 1
    compiled = sum(1 for r in rollouts if r.get("compiled"))
    ran = sum(1 for r in rollouts if r.get("ran"))
    eligible = [
        r for r in rollouts
        if isinstance(r.get("mutation"), dict) and r["mutation"].get("score") is not None
    ]
    # `run_diagnostics` only exists in runs from 2026-08-10 on. Without it a lock loss is
    # indistinguishable from a benchmark that threw -- which is exactly the blind spot that cost
    # run 7 ~24 pp of its compiled rollouts -- so report "unknown", never a reassuring zero.
    lock_recorded = any("run_diagnostics" in r for r in rollouts)
    lock = (
        sum(1 for r in rollouts for d in (r.get("run_diagnostics") or []) if "jmh.lock" in str(d))
        if lock_recorded
        else None
    )

    def last(key: str, window: int = 50) -> float | None:
        vals = [_f(row.get(key)) for row in steps[-window:]]
        vals = [v for v in vals if v is not None]
        return sum(vals) / len(vals) if vals else None

    def first(key: str, window: int = 50) -> float | None:
        vals = [_f(row.get(key)) for row in steps[:window]]
        vals = [v for v in vals if v is not None]
        return sum(vals) / len(vals) if vals else None

    step_times = [_f(row.get("step_time")) for row in steps]
    step_times = [v for v in step_times if v is not None]
    return {
        "run": data.name,
        "steps": len(steps),
        "last_step": steps[-1]["step"] if steps else None,
        "epoch": steps[-1].get("epoch") if steps else None,
        "rollouts": len(rollouts),
        "compile_frac": compiled / n,
        "run_frac": ran / n,
        "run_over_compile": ran / compiled if compiled else None,
        "mutation_eligible_frac": len(eligible) / n,
        "mutation_score_mean": (
            st.mean([r["mutation"]["score"] for r in eligible]) if eligible else None
        ),
        "jmh_lock_losses": lock,
        "jmh_lock_recorded": lock_recorded,
        "reward_first": first("reward"),
        "reward_last": last("reward"),
        "compile_first": first("compile_frac"),
        "compile_last": last("compile_frac"),
        "zero_std_frac_last": last("zero_std_frac"),
        "step_time_mean": st.mean(step_times) if step_times else None,
        "step_time_last": st.mean(step_times[-50:]) if step_times else None,
        "wall_clock_h": sum(step_times) / 3600 if step_times else None,
        "prompts_per_step": (
            steps[-1].get("rollouts") / 8 if steps and steps[-1].get("rollouts") else None
        ),
        "has_completions": bool(data.completions),
        "has_gpu": bool(data.gpu),
    }


def project_table(data: RunData) -> list[dict[str, Any]]:
    """Per-project yield and cost -- which subjects earn their wall clock."""
    by: dict[str, dict[str, Any]] = {}
    for r in data.rollouts:
        key = str(r.get("project") or "?")
        acc = by.setdefault(key, {"project": key, "n": 0, "compiled": 0, "ran": 0,
                                  "eligible": 0, "reward": 0.0, "seconds": 0.0})
        acc["n"] += 1
        acc["compiled"] += bool(r.get("compiled"))
        acc["ran"] += bool(r.get("ran"))
        mutation = r.get("mutation")
        if isinstance(mutation, dict) and mutation.get("score") is not None:
            acc["eligible"] += 1
        acc["reward"] += _f(r.get("reward")) or 0.0
        acc["seconds"] += _f(r.get("duration_s")) or 0.0
    rows = []
    for acc in by.values():
        n = acc["n"] or 1
        rows.append({
            **acc,
            "compile_frac": acc["compiled"] / n,
            "run_frac": acc["ran"] / n,
            "reward_mean": acc["reward"] / n,
            "seconds_per_compiled": acc["seconds"] / acc["compiled"] if acc["compiled"] else None,
        })
    return sorted(rows, key=lambda r: r["compile_frac"])


def failure_table(data: RunData, limit: int = 25) -> dict[str, list[dict[str, Any]]]:
    """Compile and run failure taxonomies, most common first."""
    compile_counts: dict[str, int] = {}
    run_counts: dict[str, int] = {}
    failures = 0
    for r in data.rollouts:
        if not r.get("compiled"):
            failures += 1
            for d in r.get("compile_diagnostics") or []:
                compile_counts[str(d)] = compile_counts.get(str(d), 0) + 1
        for d in r.get("run_diagnostics") or []:
            run_counts[str(d)] = run_counts.get(str(d), 0) + 1
    return {
        "compile": [
            {"message": k, "count": v, "share_of_failures": v / failures if failures else None}
            for k, v in sorted(compile_counts.items(), key=lambda kv: -kv[1])[:limit]
        ],
        "run": [
            {"message": k, "count": v}
            for k, v in sorted(run_counts.items(), key=lambda kv: -kv[1])[:limit]
        ],
    }
