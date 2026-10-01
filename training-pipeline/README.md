# jmhgen

Training pipeline for an LLM that writes JMH microbenchmarks, trained with verifiable,
execution-based rewards. Stages: SFT, rejection fine-tuning (RFT) and GRPO, all on
`google/gemma-4-E2B-it` with LoRA.

The reward compiles and runs each generated benchmark and scores it on:

| Term | Weight (GRPO run 9) |
| --- | --- |
| compiles | 0.10 |
| runs | 0.10 |
| no JMH anti-patterns (SpotJMHBugs) | 0.05 |
| stable measurements (robust RSD) | 0.05 |
| detects injected performance mutants | 0.70 |

## Install

Requires Python 3.11+, [uv](https://docs.astral.sh/uv/), a JDK and Maven. Training needs Linux
with CUDA 13 GPUs.

```bash
uv sync --extra dev                                   # core: runner, rewards, data tools
UV_PROJECT_ENVIRONMENT=.venv-gemma-cu130 \
  uv sync --project profiles/gemma-cu130 --locked     # training: torch, TRL, vLLM
./tools/install_spotjmhbugs.sh                        # anti-pattern reward
```

Keep the two environments separate. `profiles/qwen-cu128` is an alternative stack for
Qwen3.5-4B.

## Usage

```bash
# Score a single benchmark with the reward
uv run jmh-run tests/resources/HelloBenchmark.java

# SFT on distilled (class, benchmark) pairs
.venv-gemma-cu130/bin/jmh-train-sft --config configs/sft/default.yaml

# RFT: train on the verified samples in data/rft-mutation/
.venv-gemma-cu130/bin/python scripts/run_rft_phases.py \
  --manifest configs/rft/mutation-projects.yaml --phases train

# GRPO (run 9: 4 GPUs, about 21 h)
.venv-gemma-cu130/bin/accelerate launch --num_processes 4 --mixed_precision bf16 \
  -m jmhgen.training.grpo --config configs/grpo/mutation70-gemma-rtx-run9.yaml --corpus full
```

GRPO needs a patched jar for every training library. See the repository root README for how
to build them, and run `scripts/preflight_grpo.py <config>` before a long run.

## Data

- `data/projects/<id>/`: one directory per training library, holding the pinned version, the
  mutant patch (`mutations.patch`) and the mutant registry (`mutants.yaml`)
- `data/snippets/rl-mutation-all.good.jsonl`: the GRPO prompt corpus (4,739 classes)
- `data/sft/`, `data/rft-*/`: SFT and RFT datasets

## Layout

```
src/jmhgen/runner/    compile and run a benchmark with Maven + JMH
src/jmhgen/rewards/   reward terms
src/jmhgen/mutation/  plant mutants and build patched jars
src/jmhgen/training/  SFT, RFT and GRPO entry points
configs/              one YAML per experiment
scripts/              corpus provisioning and analysis
scripts/cluster/      launchers for the machines used in the thesis (host-specific)
```

## Tests

```bash
uv run pytest
```

## License

MIT
