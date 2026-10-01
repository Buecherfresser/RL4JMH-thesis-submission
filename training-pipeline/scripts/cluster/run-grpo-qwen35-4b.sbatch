#!/usr/bin/env bash
# Qwen3.5-4B GRPO on HPI: 2x H100 (vLLM TRL server + trainer).
#
# Submit from the login node (rx01 / hpi2):
#   cd /sc/scratch/zongxiong.chen/jonas/JMH_Training_Pipeline
#   export HF_TOKEN=hf_...   # or rely on secrets.env / HF token file
#   sbatch scripts/cluster/run-grpo-qwen35-4b.sbatch
#
# Smoke only:
#   RUN_FULL=0 sbatch scripts/cluster/run-grpo-qwen35-4b.sbatch
#
# Monitor:
#   squeue -u "$USER"
#   tail -f /sc/scratch/zongxiong.chen/jonas/logs/grpo-qwen_%j.out
#   tail -f /sc/scratch/zongxiong.chen/jonas/logs/grpo-Qwen3.5-4B/run-grpo.status
#SBATCH --job-name=jmh-grpo-qwen35
#SBATCH --partition=aisc-batch
#SBATCH --account=aisc
#SBATCH --qos=aisc
#SBATCH --nodes=1
#SBATCH --gres=gpu:h100:2
#SBATCH --cpus-per-task=24
#SBATCH --mem=256G
#SBATCH --time=2-00:00:00
#SBATCH --output=/sc/scratch/zongxiong.chen/jonas/logs/grpo-qwen_%j.out
#SBATCH --error=/sc/scratch/zongxiong.chen/jonas/logs/grpo-qwen_%j.err

set -euo pipefail

JMH_ROOT="${JMH_ROOT:-/sc/scratch/zongxiong.chen/jonas}"
REPO="${JMH_ROOT}/JMH_Training_Pipeline"
LOG_DIR="${JMH_ROOT}/logs"

cd "${REPO}"
# shellcheck source=/dev/null
source "${REPO}/scripts/cluster/env.hpi.sh"

# Qwen3.5 needs the CUDA-12.8 toolkit (nvcc) to build/run the pinned vLLM extension.
export CUDA_HOME="${CUDA_HOME:-/sc/projects/sci-aisc/cuda-12.8}"
export PATH="${CUDA_HOME}/bin:${PATH}"
export VLLM_MAIN_CUDA_VERSION=12.8
export JMHGEN_MODEL_PROFILE=qwen
export JMHGEN_VOLUME="${JMH_ROOT}"
export JMHGEN_SCRATCH="${JMH_ROOT}"
export REPO_DIR="${REPO}"
# GRPO needs TRL's private rollout and weight-sync endpoints. The shared
# launcher still defaults Qwen OpenAI mode for JMH-Bench benchmarking.
export VLLM_SERVE_MODE=trl
export GRPO_CONFIG="${GRPO_CONFIG:-configs/grpo/qwen35-4b.yaml}"
export GRPO_CORPUS="${GRPO_CORPUS:-original}"
export SMOKE_FIRST="${SMOKE_FIRST:-1}"
export SMOKE_STEPS="${SMOKE_STEPS:-2}"
export RUN_FULL="${RUN_FULL:-1}"
# Let run-grpo.sh / start-vllm.sh choose profile venvs (qwen-cu128). A pre-set
# UV_PROJECT_ENVIRONMENT pointing at the shared Gemma cu130 .venv breaks H100.
unset UV_PROJECT_ENVIRONMENT

# Dedicated rollout GPU: give vLLM most of the card for a 32k KV window.
export VLLM_MODEL="${VLLM_MODEL:-Qwen/Qwen3.5-4B}"
export VLLM_PORT="${VLLM_PORT:-8000}"
export VLLM_MAX_MODEL_LEN="${VLLM_MAX_MODEL_LEN:-32768}"
export VLLM_GPU_MEMORY_UTILIZATION="${VLLM_GPU_MEMORY_UTILIZATION:-0.90}"

if [[ -z "${HF_TOKEN:-}" ]]; then
  for token_file in \
    "${HOME}/.cache/huggingface/token" \
    "${JMH_ROOT}/.cache/huggingface/token"
  do
    if [[ -f "${token_file}" ]]; then
      HF_TOKEN="$(tr -d '[:space:]' < "${token_file}")"
      export HF_TOKEN
      break
    fi
  done
fi
if [[ -z "${HF_TOKEN:-}" ]]; then
  echo "FATAL: HF_TOKEN not set and no Hugging Face token file found." >&2
  exit 1
fi

# nvcc is only required for a source rebuild; the prebuilt wheel under
# artifacts/vllm-cu128-h100/ is enough for normal starts.
ARTIFACT_DIR="${JMH_ROOT}/artifacts/vllm-cu128-h100"
if ! ls "${ARTIFACT_DIR}"/vllm-*.whl >/dev/null 2>&1; then
  if ! command -v nvcc >/dev/null || ! nvcc --version | grep -q 'release 12\.8'; then
    echo "FATAL: need ${ARTIFACT_DIR}/vllm-*.whl or CUDA 12.8 nvcc (CUDA_HOME=${CUDA_HOME})." >&2
    exit 2
  fi
fi

echo "node=$(hostname) job=${SLURM_JOB_ID:-local} config=${GRPO_CONFIG}"
echo "CUDA_HOME=${CUDA_HOME} VLLM_SERVE_MODE=${VLLM_SERVE_MODE}"
nvidia-smi --query-gpu=index,name,memory.total --format=csv,noheader 2>/dev/null || true

mkdir -p "${LOG_DIR}"
VLLM_LOG="${LOG_DIR}/vllm-qwen-${SLURM_JOB_ID:-local}.log"
# TRL mode exposes /health/; OpenAI mode exposes /v1/models. Never mix them.
READY_URL="http://127.0.0.1:${VLLM_PORT}/health/"

# NEVER widen this to pkill -f 'vllm'/'EngineCore': the hpi2 account is shared, so other
# people's jobs run under the same uid on the same node and a pattern match would kill their
# work. Only ever signal VLLM_PID and its own descendants.
_vllm_descendants() {
  local pid="$1" kid
  for kid in $(cat /proc/"${pid}"/task/*/children 2>/dev/null); do
    _vllm_descendants "${kid}"
    printf '%s\n' "${kid}"
  done
}

cleanup() {
  local pid="${VLLM_PID:-}"
  [[ -z "${pid}" ]] && return 0
  kill -0 "${pid}" 2>/dev/null || return 0
  echo "stopping vLLM pid=${pid}"
  # A bare `kill` + `wait` deadlocks here. vLLM's serve process can sit in
  # unix_stream_data_wait with an EngineCore child spinning, ignoring SIGTERM; `wait` then
  # blocks forever and the job holds its GPUs for the full walltime doing nothing. Job
  # 2384618 leaked 2 H100s for ~47h that way on 2026-07-29. So: TERM the tree, wait a
  # bounded time, then SIGKILL and return regardless -- exiting lets Slurm tear down the
  # step cgroup, which reclaims anything still alive.
  local kids
  kids="$(_vllm_descendants "${pid}")"
  kill -TERM ${kids} "${pid}" 2>/dev/null || true
  for _ in $(seq 1 15); do
    kill -0 "${pid}" 2>/dev/null || { echo "vLLM exited cleanly"; return 0; }
    sleep 2
  done
  echo "vLLM ignored SIGTERM after 30s; escalating to SIGKILL"
  kids="$(_vllm_descendants "${pid}")"
  kill -KILL ${kids} "${pid}" 2>/dev/null || true
  return 0
}
trap cleanup EXIT

echo "starting vLLM on GPU 0 (max_model_len=${VLLM_MAX_MODEL_LEN}) -> ${VLLM_LOG}"
CUDA_VISIBLE_DEVICES=0 \
  "${REPO}/scripts/remote/start-vllm.sh" >"${VLLM_LOG}" 2>&1 &
VLLM_PID=$!

echo "waiting for TRL vLLM on :${VLLM_PORT} (${READY_URL})"
for i in $(seq 1 180); do
  if ! kill -0 "${VLLM_PID}" 2>/dev/null; then
    echo "FATAL: vLLM exited early; see ${VLLM_LOG}" >&2
    tail -80 "${VLLM_LOG}" >&2 || true
    exit 1
  fi
  if curl -sf "${READY_URL}" >/dev/null 2>&1; then
    echo "vLLM is ready after ~$((i * 10))s"
    curl -s "${READY_URL}" || true
    echo
    break
  fi
  if (( i % 6 == 0 )); then
    echo "  still waiting (${i}/180) ..."
    tail -5 "${VLLM_LOG}" || true
  fi
  sleep 10
done
if ! curl -sf "${READY_URL}" >/dev/null 2>&1; then
  echo "FATAL: vLLM did not become ready within timeout; see ${VLLM_LOG}" >&2
  tail -120 "${VLLM_LOG}" >&2 || true
  exit 1
fi

echo "starting GRPO trainer on GPU 1"
CUDA_VISIBLE_DEVICES=1 "${REPO}/scripts/remote/run-grpo.sh"
rc=$?
echo "GRPO exited with code ${rc}"
exit "${rc}"
