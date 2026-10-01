"""Recompile saved candidates at different concurrency levels.

Run 1 (23 % compile) had one rank compiling; run 2 (16 %) had two data-parallel ranks sharing
one Maven work dir and local repo; a serial diagnostic on the run-2 adapter scored 36 %. If the
gap is contention rather than policy, the same sources will compile at different rates here.
Also dumps the raw javac stderr/stdout, which grpo_adapter.py currently discards.
"""
from __future__ import annotations

import collections
import json
import os
import sys
from concurrent.futures import ThreadPoolExecutor

from jmhgen.config.schema import GRPOConfig
from jmhgen.data.classpath import load_classpath
from jmhgen.rewards.grpo_adapter import build_benchmark_spec
from jmhgen.runner.client import LocalRunnerClient, RewardRequest

CFG = sys.argv[1]
SRC = sys.argv[2]
WORKERS = int(sys.argv[3])
OUT = sys.argv[4]

cfg = GRPOConfig.from_yaml(CFG)
classpaths = {
    p: load_classpath(f) for p, f in cfg.project_classpaths.items() if os.path.isfile(f)
}
options = cfg.runner.jmh.to_options()

rows = [json.loads(l) for l in open(SRC) if json.loads(l).get("source")]
print(f"[recompile] {len(rows)} candidates, workers={WORKERS}")

clients = [LocalRunnerClient(cfg.runner.build_runner()) for _ in range(WORKERS)]


def work(item):
    idx, row = item
    client = clients[idx % WORKERS]
    spec = build_benchmark_spec(
        row["source"],
        row["source"].split("class ")[1].split()[0] if "class " in row["source"] else "B",
        "",
        classpaths.get(row["project"], ()),
    )
    ev = client.evaluate(
        RewardRequest(spec=spec, options=options, need_run=False, request_id=f"rc-{idx}")
    ).evaluation
    out = {
        "snippet_id": row["snippet_id"],
        "project": row["project"],
        "was_compiled": row.get("compiled"),
        "now_compiled": ev.compiled,
        "error_kind": ev.compile.error_kind.value,
        "stderr": (ev.compile.stderr or "")[-4000:],
        "stdout": (ev.compile.stdout or "")[-4000:],
    }
    runner = getattr(client, "runner", None)
    if runner is not None and hasattr(runner, "cleanup"):
        runner.cleanup(spec)
    return out


with ThreadPoolExecutor(max_workers=WORKERS) as pool:
    res = list(pool.map(work, enumerate(rows)))

with open(OUT, "w") as fh:
    for r in res:
        fh.write(json.dumps(r) + "\n")

n = len(res)
now = sum(1 for r in res if r["now_compiled"])
was = sum(1 for r in res if r["was_compiled"])
flip_up = sum(1 for r in res if r["now_compiled"] and not r["was_compiled"])
flip_dn = sum(1 for r in res if not r["now_compiled"] and r["was_compiled"])
print(f"[recompile] workers={WORKERS}  serial-baseline {was}/{n} ({100*was/n:.1f}%)  now {now}/{n} ({100*now/n:.1f}%)")
print(f"[recompile] flipped fail->ok {flip_up}, ok->fail {flip_dn}  (nondeterminism = {flip_up + flip_dn})")
print("[recompile] with stderr text:", sum(1 for r in res if r["stderr"].strip()))
print("[recompile] with stdout text:", sum(1 for r in res if r["stdout"].strip()))
for r in res:
    if not r["now_compiled"] and (r["stderr"].strip() or r["stdout"].strip()):
        print("\n=== sample failure", r["snippet_id"], r["error_kind"])
        print("STDERR:", r["stderr"][-1500:])
        print("STDOUT:", r["stdout"][-1500:])
        break
