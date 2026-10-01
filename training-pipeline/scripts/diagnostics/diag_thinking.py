"""A/B the enable_thinking flag on training-time compile rate.

Run 1 trained with thinking off and compiled 23 %; run 2 turned it on and compiled 16 %. A
served, serial replay of the run-2 adapter compiled 36 %, which is far above either. This
generates both arms from the same adapter on the same prompts through the offline vLLM path
(so chat_template_kwargs are definitely applied) and compiles both.
"""
from __future__ import annotations

import collections
import json
import os
import random
import sys

from transformers import AutoTokenizer
from vllm import LLM, SamplingParams
from vllm.lora.request import LoRARequest

from jmhgen.config.schema import GRPOConfig
from jmhgen.data.classpath import load_classpath
from jmhgen.generate.parse import parse_completion
from jmhgen.rewards.grpo_adapter import build_benchmark_spec
from jmhgen.runner.client import LocalRunnerClient, RewardRequest
from jmhgen.training.grpo import build_prompt_records

CFG, ADAPTER, N, K, OUT = sys.argv[1], sys.argv[2], int(sys.argv[3]), int(sys.argv[4]), sys.argv[5]

cfg = GRPOConfig.from_yaml(CFG)
tok = AutoTokenizer.from_pretrained(ADAPTER)
records = build_prompt_records(cfg)
random.Random(777).shuffle(records)
sample = records[:N]

classpaths = {p: load_classpath(f) for p, f in cfg.project_classpaths.items() if os.path.isfile(f)}
options = cfg.runner.jmh.to_options()

llm = LLM(
    model=cfg.base_model,
    enable_lora=True,
    max_lora_rank=cfg.lora.r,
    max_model_len=cfg.vllm_max_model_length,
    gpu_memory_utilization=0.85,
)
sp = SamplingParams(
    n=K,
    temperature=cfg.rollout_temperature,
    top_p=cfg.top_p,
    top_k=cfg.top_k,
    max_tokens=cfg.max_completion_length,
)
lora = LoRARequest("adapter", 1, ADAPTER)
client = LocalRunnerClient(cfg.runner.build_runner())

ARMS = {"thinking_on": {"enable_thinking": True}, "thinking_off": {"enable_thinking": False}}
results = {}
rows = []

for arm, kwargs in ARMS.items():
    prompts = [
        tok.apply_chat_template(r["prompt"], tokenize=False, add_generation_prompt=True, **kwargs)
        for r in sample
    ]
    print(f"\n[{arm}] prompt tail: {prompts[0][-120:]!r}")
    outs = llm.generate(prompts, sp, lora_request=lora)
    n = ok = parse_ok = 0
    lens = []
    for rec, o in zip(sample, outs):
        for c in o.outputs:
            n += 1
            lens.append(len(c.token_ids))
            parsed = parse_completion(
                c.text, fallback_package=rec["package"], fallback_class=rec["class_name"]
            )
            row = {
                "arm": arm,
                "snippet_id": rec["snippet_id"],
                "project": rec["project"],
                "tokens": len(c.token_ids),
                "has_thought_marker": ("<|channel>" in c.text or "<channel|>" in c.text),
                "parse_ok": parsed is not None,
            }
            if parsed is None:
                rows.append(row)
                continue
            parse_ok += 1
            spec = build_benchmark_spec(
                parsed.java_source,
                parsed.class_name,
                parsed.package or rec["package"],
                classpaths.get(rec["project"], ()),
            )
            ev = client.evaluate(
                RewardRequest(spec=spec, options=options, need_run=False, request_id=f"{arm}-{n}")
            ).evaluation
            ok += bool(ev.compiled)
            row["compiled"] = ev.compiled
            row["stdout"] = (ev.compile.stdout or "")[-3000:]
            row["source_head"] = parsed.java_source[:400]
            rows.append(row)
            runner = getattr(client, "runner", None)
            if runner is not None and hasattr(runner, "cleanup"):
                runner.cleanup(spec)
    results[arm] = (n, parse_ok, ok, sum(lens) / len(lens))
    print(f"[{arm}] n={n} parse_ok={parse_ok} compiled={ok} ({100 * ok / n:.1f}%) mean_tokens={sum(lens) / len(lens):.0f}")

with open(OUT, "w") as fh:
    for r in rows:
        fh.write(json.dumps(r) + "\n")

print("\n=== SUMMARY ===")
for arm, (n, p, ok, ml) in results.items():
    print(f"  {arm:<14} compile {ok}/{n} = {100 * ok / n:5.1f}%   parse_ok {100 * p / n:5.1f}%   mean_tokens {ml:.0f}")
print("thought markers seen:", collections.Counter((r["arm"], r["has_thought_marker"]) for r in rows))
