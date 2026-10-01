"""Decompose the ~22.6 s fixed term of a GRPO step into sync_weights / generate / fwd-bwd.

The run-7 regression says step_time = 22.6 s + 1.47 x eval, and separate probes ruled out vLLM
sleep mode (~2 s) and token generation (a 3x shorter completion did not help) as the cause. That
leaves the per-step weight sync (merge the LoRA into 5.1 B params, push into vLLM) and the four
grad-accum fwd/bwd passes -- untested, and they point at different fixes.

TRL already wraps the interesting blocks in `profiling_context`, but only reports them to
wandb/mlflow/trackio; `report_to: none` throws them away. This patches ProfilingContext to keep
the durations, times the reward function and compute_loss on top, and prints a per-step table.

  python probe_timing.py <config.yaml>
"""
from __future__ import annotations

import collections
import statistics as st
import sys
import time

import trl.extras.profiling as _prof

DUR: dict[str, list[float]] = collections.defaultdict(list)
STEP_MARK = {"t": None, "n": 0}

_orig_enter = _prof.ProfilingContext.__enter__
_orig_exit = _prof.ProfilingContext.__exit__


def _enter(self):
    self._probe_t0 = time.perf_counter()
    return _orig_enter(self)


def _exit(self, *a):
    dt = time.perf_counter() - getattr(self, "_probe_t0", time.perf_counter())
    DUR[self.name].append(dt)
    return _orig_exit(self, *a)


_prof.ProfilingContext.__enter__ = _enter
_prof.ProfilingContext.__exit__ = _exit

from trl import GRPOTrainer  # noqa: E402

_orig_loss = GRPOTrainer.compute_loss
_orig_gas = GRPOTrainer._generate_and_score_completions


def _loss(self, *a, **k):
    t = time.perf_counter()
    out = _orig_loss(self, *a, **k)
    DUR["compute_loss(fwd+bwd_fwd)"].append(time.perf_counter() - t)
    return out


def _gas(self, *a, **k):
    t = time.perf_counter()
    out = _orig_gas(self, *a, **k)
    dt = time.perf_counter() - t
    DUR["_generate_and_score_completions"].append(dt)

    n = STEP_MARK["n"] = STEP_MARK["n"] + 1
    now = time.perf_counter()
    wall = None if STEP_MARK["t"] is None else now - STEP_MARK["t"]
    STEP_MARK["t"] = now
    print(f"\n===== generation batch {n}"
          + (f" (wall since previous: {wall:.1f}s)" if wall else "") + " =====", flush=True)
    for name in sorted(DUR):
        v = DUR[name]
        print(f"  {name:<38} n={len(v):3d} last {v[-1]:7.2f}s  mean {st.mean(v):7.2f}s  "
              f"total {sum(v):8.1f}s", flush=True)
    return out


GRPOTrainer.compute_loss = _loss
GRPOTrainer._generate_and_score_completions = _gas

from jmhgen.config.schema import GRPOConfig  # noqa: E402
from jmhgen.training.grpo import train  # noqa: E402

cfg = GRPOConfig.from_yaml(sys.argv[1])
print(f"[probe] {cfg.output_dir}  max_steps={cfg.num_iterations}  "
      f"accum={cfg.gradient_accumulation_steps}  G={cfg.group_size}", flush=True)
train(cfg)

print("\n===== SUMMARY =====")
for name in sorted(DUR):
    v = DUR[name]
    v_sorted = sorted(v)
    print(f"{name:<40} n={len(v):3d} mean {st.mean(v):7.2f}s  median "
          f"{v_sorted[len(v) // 2]:7.2f}s  total {sum(v):8.1f}s")
