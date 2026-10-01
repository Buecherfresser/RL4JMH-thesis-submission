"""Diagnose why GRPO run-2 rollouts fail to compile.

Regenerates rollouts with the trained adapter under the exact training sampling params,
compiles them through the same reward path, and keeps the javac diagnostics that
grpo_adapter.py currently throws away.
"""
from __future__ import annotations

import collections
import json
import os
import random
import re
import sys
import time
from concurrent.futures import ThreadPoolExecutor

import requests

from jmhgen.config.schema import GRPOConfig
from jmhgen.data.classpath import load_classpath
from jmhgen.generate.parse import parse_completion
from jmhgen.rewards.grpo_adapter import build_benchmark_spec
from jmhgen.runner.client import LocalRunnerClient, RewardRequest

CFG = sys.argv[1]
N_PROMPTS = int(sys.argv[2])
K = int(sys.argv[3])
BASE = sys.argv[4]
MODEL = sys.argv[5]
OUT = sys.argv[6]

cfg = GRPOConfig.from_yaml(CFG)
from jmhgen.training.grpo import build_prompt_records  # noqa: E402  (needs cfg import first)

records = build_prompt_records(cfg)
rng = random.Random(20260808)
rng.shuffle(records)
sample = records[:N_PROMPTS]
print(f"[diag] {len(records)} prompts in corpus, sampling {len(sample)} x {K} completions")

classpaths = {}
for project, cp_file in cfg.project_classpaths.items():
    if os.path.isfile(cp_file):
        classpaths[project] = load_classpath(cp_file)


def generate(rec):
    body = {
        "model": MODEL,
        "messages": rec["prompt"],
        "n": K,
        "temperature": cfg.rollout_temperature,
        "top_p": cfg.top_p,
        "top_k": cfg.top_k,
        "max_tokens": cfg.max_completion_length,
        "chat_template_kwargs": cfg.chat_template_kwargs or {},
    }
    r = requests.post(f"{BASE}/v1/chat/completions", json=body, timeout=1800)
    r.raise_for_status()
    return r.json()["choices"]


t0 = time.time()
with ThreadPoolExecutor(max_workers=8) as pool:
    gens = list(pool.map(generate, sample))
print(f"[diag] generation done in {time.time() - t0:.0f}s")

client = LocalRunnerClient(cfg.runner.build_runner())
options = cfg.runner.jmh.to_options()

ERR = re.compile(r"^.*?\.java:\[?\d+[,:]\d*\]?\s*error:\s*(.*)$", re.M)
ERR2 = re.compile(r"error:\s*(.+)")

rows = []
finish = collections.Counter()
for rec, choices in zip(sample, gens):
    for i, ch in enumerate(choices):
        finish[ch.get("finish_reason")] += 1
        text = ch["message"].get("content") or ""
        raw = ch["message"].get("reasoning_content") or ""
        parsed = parse_completion(
            text,
            fallback_package=rec["package"],
            fallback_class=rec["class_name"],
        )
        row = {
            "snippet_id": rec["snippet_id"],
            "project": rec["project"],
            "finish_reason": ch.get("finish_reason"),
            "has_reasoning": bool(raw),
            "reasoning_chars": len(raw),
            "content_chars": len(text),
            "parse_ok": parsed is not None,
            "raw_tail": text[-400:],
        }
        if parsed is None:
            rows.append(row)
            continue
        spec = build_benchmark_spec(
            parsed.java_source,
            parsed.class_name,
            parsed.package or rec["package"],
            classpaths.get(rec["project"], ()),
        )
        ev = client.evaluate(
            RewardRequest(spec=spec, options=options, need_run=False, request_id=f"diag-{i}")
        ).evaluation
        blob = (ev.compile.stderr or "") + "\n" + (ev.compile.stdout or "")
        errs = ERR.findall(blob) or ERR2.findall(blob)
        row["compiled"] = ev.compiled
        row["error_kind"] = ev.compile.error_kind.value
        row["errors"] = errs[:20]
        row["n_errors"] = len(errs)
        row["source"] = parsed.java_source
        rows.append(row)
        runner = getattr(client, "runner", None)
        if runner is not None and hasattr(runner, "cleanup"):
            runner.cleanup(spec)

with open(OUT, "w") as fh:
    for row in rows:
        fh.write(json.dumps(row) + "\n")

n = len(rows)
comp = sum(1 for r in rows if r.get("compiled"))
print(f"\n[diag] {n} rollouts, compiled {comp} ({100 * comp / n:.1f}%)")
print("[diag] finish_reason:", dict(finish))
print("[diag] has_reasoning:", sum(1 for r in rows if r["has_reasoning"]), "/", n)


def norm(e):
    e = re.sub(r"[A-Za-z0-9_.$<>]{3,}", "X", e)
    return e[:90]


cnt = collections.Counter()
for r in rows:
    for e in r.get("errors", [])[:6]:
        cnt[norm(e)] += 1
print("\n[diag] top normalised javac errors:")
for e, c in cnt.most_common(25):
    print(f"  {c:>5}  {e}")

raw = collections.Counter()
for r in rows:
    for e in r.get("errors", [])[:6]:
        raw[e[:110]] += 1
print("\n[diag] top raw javac errors:")
for e, c in raw.most_common(25):
    print(f"  {c:>5}  {e}")
