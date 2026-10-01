# JMH-Bench + GRPO training pipeline

Code and data for the BSc thesis *Reinforcement Learning with Verifiable Execution Rewards for
Synthesizing Regression-Detecting Microbenchmarks*.

| Path | Contents |
| --- | --- |
| `jmh-bench/` | The benchmark and scorer (`jmhbench`): 6 held-out Java projects with 350 performance mutants |
| `jmh-bench/thesis-bundles/` | The benchmark suites evaluated in the thesis: 6 models × 6 projects, T=0, thinking on |
| `training-pipeline/` | SFT / RFT / GRPO training (`jmhgen`), configs, mutant patches for the 246 training libraries, and the run-9 prompt corpus |
| `results/` | The measured results (one record per model × project) and the scripts that build the thesis tables |

Trained models (LoRA adapters on `google/gemma-4-E2B-it`):

- GRPO run 9, the model evaluated in the thesis: [`bookxd/gemma-4-E2B-it-jmh-grpo-mutation70-run9`](https://huggingface.co/bookxd/gemma-4-E2B-it-jmh-grpo-mutation70-run9) (the repo root holds step 1600)
- GRPO run 8, the conservative run: [`bookxd/gemma-4-E2B-it-jmh-grpo-mutation70-run8`](https://huggingface.co/bookxd/gemma-4-E2B-it-jmh-grpo-mutation70-run8)
- RFT baseline: [`bookxd/gemma-4-e2b-rft-mutation`](https://huggingface.co/bookxd/gemma-4-e2b-rft-mutation)

Replication data: [`bookxd/jmh-grpo-replication-data`](https://huggingface.co/datasets/bookxd/jmh-grpo-replication-data)
holds the built training jars run 9 used (1.2 GB) and the complete raw data of the three
evaluation campaigns (`spin`, `sleep` and the LLM4JMH replication).

## Requirements

- Python 3.12 and [uv](https://docs.astral.sh/uv/)
- Maven 3.9 and JDK 17 for JMH-Bench. JDK 23 and newer silently skip JMH's annotation
  processor, which produces benchmark jars with no benchmarks in them.
- For training: JDK 25 (what run 9 used), Linux, 4 × 96 GB GPUs, CUDA 13. For generation: 1 GPU.

## Reproduce

### 0. Rebuild the thesis tables from the measured results (seconds)

```bash
cd results
python3 results_tables.py tables --data data --out . --exclude dsv4
uv run --with pyyaml --with matplotlib python failure_analysis.py --from-json --out .
```

This writes the chapter 4 tables to `tables_ch4/` and the appendix tables to `tables_appendix/`.
DeepSeek-V4 Flash (`dsv4`) appears only in the appendix.

### 1. Set up JMH-Bench and score the reference suite (CPU, about 20 min)

```bash
cd jmh-bench
uv sync --extra openai --extra dev
uv run pytest
uv run jmhbench project-bench --harness dummy-passthrough --project fastfilter --quick
```

### 2. Re-measure a thesis result (CPU, several hours per project)

```bash
uv run jmhbench project-bench thesis-bundles/gemma-e2b-grpo-run9/hppc --project hppc \
  --preset campaign --mutant-op spin --mutant-tokens 200 \
  --sensitivity-sample 0 --max-detect-attempts 6 --compiler-heap 4g --out out/run9-hppc
```

Use `--mutant-op sleep` for the sleep-operator comparison. The thesis ran each project on its own
Linux machine (AMD Ryzen 5 PRO 8500GE), pinned to one CPU core type with
`taskset -c 1,2,3,5,7,8,9,11`. Mutation scores depend on the hardware, so expect results close
to the thesis numbers, not identical ones.

### 3. Generate suites with the trained model (1 GPU)

vLLM comes from the training environment (step 4: `profiles/gemma-cu130`).

```bash
hf download bookxd/gemma-4-E2B-it-jmh-grpo-mutation70-run9 --local-dir run9 \
  --exclude 'checkpoints/*' --exclude 'evaluation/*' --exclude 'metrics/*'
vllm serve google/gemma-4-E2B-it --enable-lora --lora-modules run9ck1600=run9 --max-model-len 65536

# in jmh-bench/, once the server is up
OPENAI_BASE_URL=http://localhost:8000/v1 OPENAI_API_KEY=none \
uv run jmhbench project-gen --harness openai-zero-shot --project hppc \
  -o model=run9ck1600 -o temperature=0 -o enable_thinking=true -o max_tokens=16384 \
  --compile-check --compile-check-retries 2 --parallel 8 --out gen/run9-hppc
```

Then score `gen/run9-hppc` the same way as in step 2.

### 4. Train GRPO run 9 (4 GPUs, about 21 h)

```bash
cd training-pipeline
uv sync --extra dev && uv run pytest
UV_PROJECT_ENVIRONMENT=.venv-gemma-cu130 uv sync --project profiles/gemma-cu130 --locked
./tools/install_spotjmhbugs.sh

# The jars run 9 trained on (1.2 GB)
hf download bookxd/jmh-grpo-replication-data --repo-type dataset \
  --include 'classpaths/*' --local-dir data

CFG=configs/grpo/mutation70-gemma-rtx-run9.yaml
uv run python scripts/preflight_grpo.py $CFG --reward-smoke
HF_TOKEN=... .venv-gemma-cu130/bin/accelerate launch --num_processes 4 --mixed_precision bf16 \
  -m jmhgen.training.grpo --config $CFG --corpus full
```

To rebuild the jars from source instead, clone each library at its pinned commit and build it:

```bash
for c in configs/grpo/rl-corpus.yaml configs/grpo/rl-corpus-tierA.verified.yaml; do
  export JMH_RL_CORPUS=$c
  uv run python scripts/prepare_rl_sources.py
  ./scripts/provision_rl_projects.sh
  KEEP_PATCHES=1 ./scripts/provision_rl_mutants.sh   # build the committed mutants, don't re-plant
done
```

Retry a library that fails on JDK 25 with JDK 17. Six libraries (`commons-codec`, `objectlayout`,
`gson`, `avro`, `cactoos`, `geometry-api-java`) build on neither, so a rebuild is incomplete;
the downloaded jars are complete.

The adapter is written to `outputs/grpo-mutation70-gemma-rtx-run9/`. The config's header
explains every setting. The other stages (SFT, RFT) are covered in `training-pipeline/README.md`.

## License

MIT (see `LICENSE`). The vendored projects under `jmh-bench/dataset/projects/` keep their own
licenses.

`consumeCpu` in `jmh-bench/tools/make_project_mutants.py` and `make_compress_mutants.py` is
adapted from `Blackhole.consumeCPU` in [OpenJDK JMH](https://github.com/openjdk/jmh) 1.37 (GPLv2
with Classpath Exception).
