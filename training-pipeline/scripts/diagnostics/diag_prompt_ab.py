"""A/B two system prompts under the exact training sampling params, compile-check both.

Tests the run-7 finding that 46 % of compile failures involve @State: hard rule 3 lists all six
JMH config annotations in one breath, but @State is the only @Target(TYPE) one of the six.
Arm A is the shipped JMHBENCH_SYSTEM; arm B splits rule 3 by annotation target and promotes the
TimeUnit import + Blackhole-is-a-parameter to hard rules.

Same prompts, same seeds, same sampling for both arms -- the only difference is the system turn.

  python diag_prompt_ab.py <config.yaml> <n_prompts> <k> <base_url> <model> <out.jsonl>
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

CFG, N_PROMPTS, K, BASE, MODEL, OUT = (
    sys.argv[1], int(sys.argv[2]), int(sys.argv[3]), sys.argv[4], sys.argv[5], sys.argv[6]
)
WORKERS = int(os.environ.get("WORKERS", "12"))

cfg = GRPOConfig.from_yaml(CFG)
from jmhgen.data.prompts import JMHBENCH_SYSTEM  # noqa: E402
from jmhgen.training.grpo import build_prompt_records  # noqa: E402

# --- arm B: the only edit is rules 3 and 4 -------------------------------------------------
_OLD_R3 = (
    "3. Declare all JMH configuration annotations: @State, @BenchmarkMode, @Fork, @Warmup, "
    "@Measurement and @OutputTimeUnit. WHICH value you pick is free (@Fork/@Warmup/@Measurement "
    "are overridden at runtime; the scope is up to you; the mode may be AverageTime or "
    "Throughput) -- but each one still needs an explicit value in parentheses, and none may be "
    "omitted. @State especially: it has no default, so write @State(Scope.Thread) or "
    "@State(Scope.Benchmark) -- a bare @State does not compile.\n"
)
_NEW_R3 = (
    "3. Declare the JMH configuration annotations. They do NOT all go in the same place:\n"
    "   3a. @State is @Target(TYPE). It is legal ONLY on a class declaration -- put it on the "
    "line directly above `public class ...` and NOWHERE else. On a @Benchmark method or on a "
    "field it is a compile error (\"annotation interface not applicable to this kind of "
    "declaration\"). It also has no default value, so a bare @State is a compile error "
    "(\"missing a default value for the element 'value'\"). Write exactly "
    "@State(Scope.Thread) or @State(Scope.Benchmark).\n"
    "   3b. @BenchmarkMode, @Fork, @Warmup, @Measurement and @OutputTimeUnit are legal on the "
    "class or on a method. Declare all five, each with an explicit value in parentheses; none "
    "may be omitted. WHICH value you pick is free (@Fork/@Warmup/@Measurement are overridden at "
    "runtime; the mode may be AverageTime or Throughput).\n"
    "   3c. If you write @OutputTimeUnit(TimeUnit.MICROSECONDS) you MUST also write "
    "`import java.util.concurrent.TimeUnit;`. Without that import the file does not compile.\n"
)
_OLD_R4 = (
    "4. Consume the result (RETU): either return it from the @Benchmark method or call "
    "bh.consume(x). A void @Benchmark must take a Blackhole bh parameter. Returning is fully "
    "valid.\n"
)
_NEW_R4 = (
    "4. Consume the result (RETU): either return it from the @Benchmark method or call "
    "bh.consume(x). Blackhole is injected as a method PARAMETER -- write "
    "`public void m(Blackhole bh)` and then `bh.consume(x)`. `consume` is an instance method, so "
    "`Blackhole.consume(x)` on the type does not compile. A void @Benchmark must take a Blackhole "
    "bh parameter. Returning is fully valid.\n"
)
# VARIANT=b   : rules 3a-3c AND the rule-4 rewrite (the first thing measured)
# VARIANT=bprime: rules 3a-3c only. The rule-4 rewrite spells out `Blackhole.consume(x)` as the
#   thing NOT to do, and measurably *raised* its incidence 4.4 % -> 7.3 % (none of which compile)
#   -- negative-example priming. bprime drops it to isolate the part that worked.
VARIANT = os.environ.get("VARIANT", "b").lower()
SYSTEM_B = JMHBENCH_SYSTEM.replace(_OLD_R3, _NEW_R3)
assert _NEW_R3 in SYSTEM_B, "rule 3 substitution failed -- prompt text drifted"
if VARIANT == "b":
    SYSTEM_B = SYSTEM_B.replace(_OLD_R4, _NEW_R4)
    assert _NEW_R4 in SYSTEM_B, "rule 4 substitution failed -- prompt text drifted"
elif VARIANT != "bprime":
    raise SystemExit(f"VARIANT must be 'b' or 'bprime', got {VARIANT!r}")
print(f"[ab] variant={VARIANT}: arm A {len(JMHBENCH_SYSTEM)} chars, arm B {len(SYSTEM_B)} chars")

records = build_prompt_records(cfg)
rng = random.Random(20260810)
rng.shuffle(records)
sample = records[:N_PROMPTS]
print(f"[ab] {len(records)} prompts in corpus, sampling {len(sample)} x {K} x 2 arms")

classpaths = {
    p: load_classpath(f) for p, f in cfg.project_classpaths.items() if os.path.isfile(f)
}


def generate(job):
    arm, rec = job
    msgs = list(rec["prompt"])
    if arm == "B":
        msgs = [{"role": "system", "content": SYSTEM_B}] + [
            m for m in msgs if m["role"] != "system"
        ]
    body = {
        "model": MODEL,
        "messages": msgs,
        "n": K,
        "temperature": cfg.rollout_temperature,
        "top_p": cfg.top_p,
        "top_k": cfg.top_k,
        "max_tokens": cfg.max_completion_length,
        "seed": 20260810,
        "chat_template_kwargs": cfg.chat_template_kwargs or {},
    }
    r = requests.post(f"{BASE}/v1/chat/completions", json=body, timeout=3600)
    r.raise_for_status()
    return arm, rec, r.json()["choices"]


jobs = [(arm, rec) for arm in ("A", "B") for rec in sample]
t0 = time.time()
with ThreadPoolExecutor(max_workers=16) as pool:
    gens = list(pool.map(generate, jobs))
print(f"[ab] generation done in {time.time() - t0:.0f}s")

# Compile-check only (need_run=False), so this never touches JMH's /tmp/jmh.lock and is safe to
# parallelise. One runner (and therefore one Maven work root) per worker.
options = cfg.runner.jmh.to_options()
clients = [LocalRunnerClient(cfg.runner.build_runner()) for _ in range(WORKERS)]
# Maven prints javac diagnostics as `[ERROR] /path/File.java:[12,5] message` on **stdout** with
# no literal "error:" -- the same pattern grpo_adapter._JAVAC_DIAGNOSTIC_RE uses. The `error:`
# form only appears when javac is invoked directly, so keep it as a fallback.
ERR = re.compile(r"^\[ERROR\]\s+\S+\.java:\[\d+,\d+\]\s+(.*)$", re.M)
ERR2 = re.compile(r"error:\s*(.+)")

units = []
for arm, rec, choices in gens:
    for i, ch in enumerate(choices):
        units.append((arm, rec, i, ch))


def check(item):
    idx, (arm, rec, i, ch) = item
    client = clients[idx % WORKERS]
    text = ch["message"].get("content") or ""
    raw = ch["message"].get("reasoning_content") or ""
    parsed = parse_completion(
        text, fallback_package=rec["package"], fallback_class=rec["class_name"]
    )
    row = {
        "arm": arm,
        "snippet_id": rec["snippet_id"],
        "project": rec["project"],
        "finish_reason": ch.get("finish_reason"),
        "reasoning_chars": len(raw),
        "content_chars": len(text),
        "parse_ok": parsed is not None,
        "compiled": False,
    }
    if parsed is None:
        return row
    spec = build_benchmark_spec(
        parsed.java_source,
        parsed.class_name,
        parsed.package or rec["package"],
        classpaths.get(rec["project"], ()),
    )
    ev = client.evaluate(
        RewardRequest(spec=spec, options=options, need_run=False, request_id=f"ab-{arm}-{idx}")
    ).evaluation
    blob = (ev.compile.stderr or "") + "\n" + (ev.compile.stdout or "")
    errs = ERR.findall(blob) or ERR2.findall(blob)
    row["compiled"] = ev.compiled
    row["error_kind"] = ev.compile.error_kind.value
    row["errors"] = errs[:20]
    row["source"] = parsed.java_source
    runner = getattr(client, "runner", None)
    if runner is not None and hasattr(runner, "cleanup"):
        runner.cleanup(spec)
    return row


t0 = time.time()
with ThreadPoolExecutor(max_workers=WORKERS) as pool:
    rows = list(pool.map(check, enumerate(units)))
print(f"[ab] compile-check done in {time.time() - t0:.0f}s ({len(rows)} units, {WORKERS} workers)")

with open(OUT, "w") as fh:
    for row in rows:
        fh.write(json.dumps(row) + "\n")

SIGS = {
    "@State misplaced": "annotation interface not applicable",
    "@State bare": "is missing a default value for the element",
    "TimeUnit import": "must be an enum constant",
    "Blackhole static": "cannot be referenced from a static context",
    "cannot find symbol": "cannot find symbol",
}
print()
for arm in ("A", "B"):
    a = [r for r in rows if r["arm"] == arm]
    c = sum(1 for r in a if r["compiled"])
    f = [r for r in a if not r["compiled"]]
    print(f"=== arm {arm}: {len(a)} rollouts, compiled {c} ({100 * c / len(a):.1f}%), "
          f"parse_ok {sum(1 for r in a if r['parse_ok'])}, "
          f"mean content {sum(r['content_chars'] for r in a) / len(a):.0f} chars")
    for name, sig in SIGS.items():
        n = sum(1 for r in f if any(sig in e for e in r.get("errors", [])))
        print(f"      {name:<20} {n:>4}  ({100 * n / max(len(f), 1):.1f}% of failures, "
              f"{100 * n / len(a):.1f}% of rollouts)")

# Paired by (snippet, k-index): same prompt, same seed, only the system turn differs.
pa = {(r["snippet_id"], i): r for i, r in enumerate(x for x in rows if x["arm"] == "A")}
print("\n[ab] top raw javac errors per arm:")
for arm in ("A", "B"):
    cnt = collections.Counter()
    for r in rows:
        if r["arm"] == arm and not r["compiled"]:
            for e in r.get("errors", [])[:6]:
                cnt[e[:100]] += 1
    print(f"  --- {arm} ---")
    for e, c in cnt.most_common(10):
        print(f"    {c:>5}  {e}")
