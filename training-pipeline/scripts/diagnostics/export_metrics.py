"""Export one tidy per-step CSV of every GRPO reward/metric logged for a run.

Three sources are merged:

* ``metrics/step_metrics.csv``     - reward, funnel (compile/run/mutation) and optimiser scalars
* ``metrics/reward_components.csv`` - per-component reward means/stds (raw and weight-applied)
* the console log                  - the TRL metrics that never reach the CSVs, notably
                                     ``entropy`` and the ``completions/*`` length stats

step_metrics.csv carries one row per data-parallel rank per step, so rows are aggregated to one
row per step: counts (``rollouts``) are summed, everything else averaged. A resumed run can log
the same step twice; aggregating by step absorbs that too.
"""
from __future__ import annotations

import ast
import csv
import re
import sys
from collections import defaultdict

METRICS_DIR, CONSOLE_LOG, OUT = sys.argv[1], sys.argv[2], sys.argv[3]

SUM_COLS = {"rollouts"}


def read_csv_by_step(path):
    rows = defaultdict(list)
    with open(path) as fh:
        for row in csv.DictReader(fh):
            try:
                rows[int(float(row["step"]))].append(row)
            except (KeyError, ValueError):
                continue
    return rows


def aggregate(rows, skip=("step",)):
    """Mean across ranks, except counts which are summed. Blank/NaN cells are ignored."""
    out = {}
    keys = [k for k in rows[0] if k not in skip]
    for k in keys:
        vals = []
        for r in rows:
            v = (r.get(k) or "").strip()
            if v in ("", "nan", "None"):
                continue
            try:
                vals.append(float(v))
            except ValueError:
                pass
        if not vals:
            out[k] = ""
        elif k in SUM_COLS:
            out[k] = sum(vals)
        else:
            out[k] = sum(vals) / len(vals)
    return out


# --- the TRL dicts printed to the console: pick up what the CSVs do not carry ---------------
CONSOLE_KEYS = [
    "entropy",
    "num_tokens",
    "completions/mean_length",
    "completions/min_length",
    "completions/max_length",
    "completions/mean_terminated_length",
    "completions/clipped_ratio",
    "clip_ratio/region_mean",
]
console = defaultdict(list)
order = 0
with open(CONSOLE_LOG, errors="ignore") as fh:
    blob = fh.read()
# The dicts are printed one per logging step, in order; epoch lets us line them up with steps.
for m in re.finditer(r"\{'loss':.*?\}", blob, flags=re.S):
    try:
        d = ast.literal_eval(m.group(0))
    except (ValueError, SyntaxError):
        continue
    order += 1
    console[order] = d

step_rows = read_csv_by_step(f"{METRICS_DIR}/step_metrics.csv")
comp_rows = read_csv_by_step(f"{METRICS_DIR}/reward_components.csv")
steps = sorted(step_rows)

# Console dicts are emitted once per step in step order; map them onto the sorted step list.
console_by_step = {}
for i, s in enumerate(steps, start=1):
    if i in console:
        console_by_step[s] = console[i]

base_keys = [k for k in next(iter(step_rows.values()))[0] if k != "step"]
comp_keys = [k for k in next(iter(comp_rows.values()))[0] if k != "step"] if comp_rows else []
header = ["step"] + base_keys + comp_keys + [k.replace("/", "_") for k in CONSOLE_KEYS]

with open(OUT, "w", newline="") as fh:
    w = csv.writer(fh)
    w.writerow(header)
    for s in steps:
        agg = aggregate(step_rows[s])
        comp = aggregate(comp_rows[s]) if s in comp_rows else {}
        c = console_by_step.get(s, {})
        row = [s]
        row += [agg.get(k, "") for k in base_keys]
        row += [comp.get(k, "") for k in comp_keys]
        for k in CONSOLE_KEYS:
            v = c.get(k, "")
            try:
                v = float(v)
            except (TypeError, ValueError):
                pass
            row.append(v)
        w.writerow(row)

print(f"wrote {OUT}: {len(steps)} steps x {len(header)} columns")
print("steps %d..%d" % (steps[0], steps[-1]))
print("columns:", ", ".join(header))
