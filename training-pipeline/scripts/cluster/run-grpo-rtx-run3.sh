#!/usr/bin/env bash
# GRPO run 3 on the 8x RTX PRO 6000 Blackwell box (rtx-jonas). No Slurm here -- this is a
# single bare-metal host, so the script is the scheduler: run it under tmux and it owns the GPUs.
#
# Differs from run-grpo-rtx.sh only in the default config and in pinning CUDA_VISIBLE_DEVICES.
# The pin matters now: GPUs 2-6 are busy with the JMH-Bench eval and the DeepSeek server, and
# accelerate would otherwise grab the first --num_processes cards it sees.
#
# Multi-GPU **colocate**, not server mode. `trl vllm-serve` hangs at "Waiting for application
# startup" for every model on every cluster we have tried (training-run-failures.md 5), so
# colocate is the only proven rollout path. Under accelerate each rank gets one GPU and runs
# its own colocated vLLM, which is data parallelism: every rank holds a full copy of the model.
# Fine for E2B (~11 GiB of bf16 weights against 95.6 GiB of VRAM); it will NOT scale to a 31B
# model without sharding the trainer (FSDP/ZeRO-3) and vLLM (vllm_tensor_parallel_size > 1).
#
#   tmux new -s grpo
#   scripts/cluster/run-grpo-rtx.sh                       # 2 GPUs, full run
#   NUM_GPUS=2 SMOKE_STEPS=3 SMOKE_ONLY=1 scripts/cluster/run-grpo-rtx.sh   # quick sanity
set -uo pipefail
export PYTHONUNBUFFERED=1

JMH_ROOT="${JMH_ROOT:-/opt/jmh}"
REPO="${REPO:-${JMH_ROOT}/JMH_Training_Pipeline}"
VENV="${VENV:-${JMH_ROOT}/.venv-gemma-cu130}"
GRPO_CONFIG="${GRPO_CONFIG:-configs/grpo/mutation70-gemma-rtx-run3.yaml}"
GRPO_CORPUS="${GRPO_CORPUS:-full}"
NUM_GPUS="${NUM_GPUS:-2}"
SMOKE_STEPS="${SMOKE_STEPS:-0}"      # >0 : cap total steps (num_iterations) for a sanity run
SMOKE_ONLY="${SMOKE_ONLY:-0}"
SKIP_PREFLIGHT="${SKIP_PREFLIGHT:-0}"

cd "${REPO}" || exit 1

# --- toolchain ---------------------------------------------------------------------------
# JDK 25 reads the class-file major 67 mutant jars (commons-codec) that broke javac 17 on LRZ;
# `javac --release 17` still emits the bytecode the runner config asks for.
export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-25-openjdk-amd64}"
export PATH="${JAVA_HOME}/bin:${JMH_ROOT}/.local/bin:/root/.local/bin:${PATH}"
# -Xmx1g bounds Maven's OWN heap. Without it Maven inherits the JVM default max heap of 1/4 of
# physical RAM (~15.5 GiB here), and the reward runs up to `reward_workers x NUM_GPUS` of them at
# once -- 16 unbounded JVMs against ~35 GiB of usable RAM. Compiling one benchmark file needs
# nowhere near 1 GiB. The JMH *forks* are capped separately, per-config, via runner.jmh.jvm_args.
export MAVEN_OPTS="-Dmaven.repo.local=${JMH_ROOT}/.m2/repository -Xmx1g"
export HF_HOME="${JMH_ROOT}/.cache/huggingface"
export XDG_CACHE_HOME="${JMH_ROOT}/.cache"
export TMPDIR="${JMH_ROOT}/tmp"
mkdir -p "${TMPDIR}" "${JMH_ROOT}/logs" "${JMH_ROOT}/.m2/repository"
# The run-1 OOM (job 5721357, step 108) was a fragmented heap, not a true out-of-memory.
export PYTORCH_CUDA_ALLOC_CONF="${PYTORCH_CUDA_ALLOC_CONF:-expandable_segments:True}"
# NCCL P2P is OFF by default here, and that is not cargo-culting: with it on, the first
# cross-rank collective deadlocks. Both ranks sit in state R burning 100 % CPU (22:53 of CPU
# over 22:19 wall) with the log frozen immediately after "NCCL version 2.28.9+cuda13.0", no
# GPU memory movement and no progress -- a classic NCCL busy-wait spin.
#
# Cause: AMD IOMMU is active (82 groups, no `iommu=pt` on the kernel cmdline) and these cards
# sit behind a PCIe switch. `nvidia-smi topo -p2p r` reports OK because it probes *capability*;
# it does not prove ACS lets the peer DMA through. Disabling P2P makes NCCL stage through host
# memory instead.
#
# The cost is ~nil for this workload: LoRA r=16 gradients are a few MB per all-reduce, not GB.
# It WOULD matter for tensor-parallel on a 31B model -- the real fix there is booting with
# `iommu=pt`, which needs a GRUB change and a reboot. See docs/rtx-blackwell-box.md.
export NCCL_P2P_DISABLE="${NCCL_P2P_DISABLE:-1}"
export NCCL_DEBUG="${NCCL_DEBUG:-WARN}"
# Only cards 0 and 1 are free; 2-5 hold the DeepSeek server and 6 the JMH-Bench eval vLLM.
export CUDA_VISIBLE_DEVICES="${CUDA_VISIBLE_DEVICES:-0,1}"
# vLLM's FlashInfer top-k/top-p sampler JIT-compiles and needs nvcc, which is not at the
# default /usr/local/cuda on this box. The native sampler is numerically equivalent for our
# sampling params; this is what /root/serve-jmh-gemma.sh already does.
export VLLM_USE_FLASHINFER_SAMPLER="${VLLM_USE_FLASHINFER_SAMPLER:-0}"
export VLLM_CACHE_ROOT="${VLLM_CACHE_ROOT:-${JMH_ROOT}/.cache/vllm}"

[[ -f "${JMH_ROOT}/secrets.env" ]] && . "${JMH_ROOT}/secrets.env"
[[ -z "${HF_TOKEN:-}" ]] && { echo "FATAL: HF_TOKEN not set (expected ${JMH_ROOT}/secrets.env)" >&2; exit 1; }

echo "=== host=$(hostname) gpus=${NUM_GPUS} (CUDA_VISIBLE_DEVICES=${CUDA_VISIBLE_DEVICES}) config=${GRPO_CONFIG} corpus=${GRPO_CORPUS} ==="
nvidia-smi --query-gpu=index,name,memory.total,memory.used --format=csv,noheader | head -"${NUM_GPUS}"
"${VENV}/bin/python" -c "import torch,vllm,trl;print(f'torch {torch.__version__} | vllm {vllm.__version__} | trl {trl.__version__} | gpus {torch.cuda.device_count()}')" || exit 2
java -version 2>&1 | head -1
mvn -v 2>&1 | head -1

# --- preflight ---------------------------------------------------------------------------
# Four classes of silent-zero reward failure are only catchable statically; see
# training-run-failures.md. Never skip this on a fresh machine.
if [[ "${SKIP_PREFLIGHT}" != "1" ]]; then
  echo "=== preflight ==="
  "${VENV}/bin/python" scripts/preflight_grpo.py "${GRPO_CONFIG}" \
      --corpus "${GRPO_CORPUS}" --reward-smoke || {
    echo "FATAL: preflight failed — not starting a run that cannot learn." >&2; exit 3; }
fi

CONFIG_TO_RUN="${GRPO_CONFIG}"
if (( SMOKE_STEPS > 0 )); then
  CONFIG_TO_RUN="${TMPDIR}/smoke-$(basename "${GRPO_CONFIG}")"
  "${VENV}/bin/python" - "${GRPO_CONFIG}" "${CONFIG_TO_RUN}" "${SMOKE_STEPS}" <<'PY'
import sys, yaml
src, dst, steps = sys.argv[1], sys.argv[2], int(sys.argv[3])
cfg = yaml.safe_load(open(src))
cfg.update(limit_prompts=4 * steps, num_iterations=steps, save_steps=0,
           logging_steps=1, output_dir=cfg["output_dir"] + "-smoke",
           resume_from_checkpoint=None)   # never resume a smoke; it would return instantly
yaml.safe_dump(cfg, open(dst, "w"), sort_keys=False)
print(f"smoke config -> {dst} ({steps} steps)")
PY
fi

echo "=== launching GRPO on ${NUM_GPUS} GPU(s) (colocate) ==="
LOG="${JMH_ROOT}/logs/grpo-rtx-run3-$(date -u +%Y%m%d-%H%M%S).log"
echo "log: ${LOG}"
"${VENV}/bin/accelerate" launch \
  --num_processes "${NUM_GPUS}" \
  --num_machines 1 \
  --mixed_precision bf16 \
  --dynamo_backend no \
  -m jmhgen.training.grpo \
  --config "${CONFIG_TO_RUN}" --corpus "${GRPO_CORPUS}" 2>&1 | tee "${LOG}"
rc="${PIPESTATUS[0]}"
echo "GRPO exited ${rc}"
(( SMOKE_ONLY == 1 )) && echo "(smoke only — not continuing to the full run)"
exit "${rc}"
