#!/usr/bin/env bash
# Short HPI smoke for the Qwen GRPO stack.
# Installs the pinned vLLM once, serves it on GPU0 (TRL mode), runs a 1-step GRPO smoke on GPU1.
#
#   sbatch scripts/cluster/smoke-qwen-grpo.sbatch
#
# Watch logs with:
#   tail -f /sc/scratch/zongxiong.chen/jonas/logs/qwen-smoke_<jobid>.out
#SBATCH --job-name=jmh-qwen-smoke
#SBATCH --partition=aisc-batch
#SBATCH --account=aisc
#SBATCH --qos=aisc
#SBATCH --nodes=1
#SBATCH --gres=gpu:h100:2
#SBATCH --cpus-per-task=16
#SBATCH --mem=128G
#SBATCH --time=3:00:00
#SBATCH --output=/sc/scratch/zongxiong.chen/jonas/logs/qwen-smoke_%j.out
#SBATCH --error=/sc/scratch/zongxiong.chen/jonas/logs/qwen-smoke_%j.err

set -euo pipefail
# Slurm redirects stdout to a file (fully buffered); re-exec once with line buffering.
if [[ -z "${_JMH_SMOKE_REEXEC:-}" ]] && command -v stdbuf >/dev/null 2>&1; then
  export _JMH_SMOKE_REEXEC=1
  exec stdbuf -oL -eL bash "$0" "$@"
fi
export PYTHONUNBUFFERED=1
export UV_LINK_MODE=copy

JMH_ROOT="${JMH_ROOT:-/sc/scratch/zongxiong.chen/jonas}"
REPO="${JMH_ROOT}/JMH_Training_Pipeline"
LOG_DIR="${JMH_ROOT}/logs"
VENV="${JMH_ROOT}/.venv-qwen-cu128"

echo "=== smoke start $(date -Is) ==="

cd "${REPO}"
# shellcheck source=/dev/null
source "${REPO}/scripts/cluster/env.hpi.sh"

export CUDA_HOME="${CUDA_HOME:-/sc/projects/sci-aisc/cuda-12.8}"
export PATH="${CUDA_HOME}/bin:${JMH_ROOT}/tools/cargo/bin:${PATH}"
export RUSTUP_HOME="${JMH_ROOT}/tools/rustup"
export CARGO_HOME="${JMH_ROOT}/tools/cargo"
export VLLM_MAIN_CUDA_VERSION=12.8
export JMHGEN_MODEL_PROFILE=qwen
export JMHGEN_VOLUME="${JMH_ROOT}"
export JMHGEN_SCRATCH="${JMH_ROOT}"
export REPO_DIR="${REPO}"
export UV_PROJECT_ENVIRONMENT="${VENV}"
export VLLM_VENV="${VENV}"
export VLLM_SERVE_MODE=trl

export VLLM_MODEL="${VLLM_MODEL:-Qwen/Qwen3.5-4B}"
export VLLM_PORT="${VLLM_PORT:-8000}"
export VLLM_MAX_MODEL_LEN="${VLLM_MAX_MODEL_LEN:-8192}"
export VLLM_GPU_MEMORY_UTILIZATION="${VLLM_GPU_MEMORY_UTILIZATION:-0.50}"

if [[ -z "${HF_TOKEN:-}" && -f "${HOME}/.cache/huggingface/token" ]]; then
  HF_TOKEN="$(tr -d '[:space:]' < "${HOME}/.cache/huggingface/token")"
  export HF_TOKEN
fi
if [[ -z "${HF_TOKEN:-}" ]]; then
  echo "FATAL: HF_TOKEN missing" >&2
  exit 1
fi

chmod u+x "${CARGO_HOME}/bin/"* 2>/dev/null || true
if [[ -d "${RUSTUP_HOME}/toolchains" ]]; then
  find "${RUSTUP_HOME}/toolchains" -maxdepth 3 -type f \( -name rustc -o -name cargo -o -name rustup \) \
    -exec chmod u+x {} + 2>/dev/null || true
fi

echo "=== node=$(hostname) job=${SLURM_JOB_ID} ==="
nvidia-smi --query-gpu=index,name,driver_version,memory.total --format=csv,noheader
nvcc --version 2>/dev/null | grep release || echo "nvcc not required when wheel is present"

mkdir -p "${LOG_DIR}"
VLLM_LOG="${LOG_DIR}/qwen-smoke-vllm-${SLURM_JOB_ID}.log"
TRAIN_LOG="${LOG_DIR}/qwen-smoke-train-${SLURM_JOB_ID}.log"
READY_URL="http://127.0.0.1:${VLLM_PORT}/health/"

cleanup() {
  if [[ -n "${VLLM_PID:-}" ]] && kill -0 "${VLLM_PID}" 2>/dev/null; then
    kill "${VLLM_PID}" 2>/dev/null || true
    wait "${VLLM_PID}" 2>/dev/null || true
  fi
}
trap cleanup EXIT

echo "=== [1/4] sync qwen-cu128 + install pinned vLLM (wheel preferred) ==="
uv sync --inexact --project profiles/qwen-cu128
REPO_DIR="${REPO}" scripts/remote/install-qwen-vllm.sh
uv run --no-sync --project profiles/qwen-cu128 python - <<'PY'
import torch, vllm
print(f"[smoke] torch={torch.__version__} cuda={torch.version.cuda} vllm={vllm.__version__}")
PY

echo "=== [2/4] serve TRL vLLM on GPU0 ==="
CUDA_VISIBLE_DEVICES=0 "${REPO}/scripts/remote/start-vllm.sh" \
  >"${VLLM_LOG}" 2>&1 &
VLLM_PID=$!

echo "waiting for TRL vLLM (${READY_URL}; log: ${VLLM_LOG})"
for i in $(seq 1 90); do
  if ! kill -0 "${VLLM_PID}" 2>/dev/null; then
    echo "FATAL: vLLM exited early" >&2
    tail -120 "${VLLM_LOG}" >&2 || true
    exit 1
  fi
  if curl -sf "${READY_URL}" >/dev/null 2>&1; then
    echo "vLLM ready after ~$((i * 10))s"
    curl -s "${READY_URL}"
    echo
    break
  fi
  if (( i % 6 == 0 )); then
    echo "  still waiting (${i}/90) ..."
    tail -5 "${VLLM_LOG}" || true
  fi
  sleep 10
done
if ! curl -sf "${READY_URL}" >/dev/null 2>&1; then
  echo "FATAL: vLLM timeout" >&2
  tail -120 "${VLLM_LOG}" >&2 || true
  exit 1
fi

echo "=== [3/4] trainer GPU1 sanity ==="
CUDA_VISIBLE_DEVICES=1 uv run --no-sync --project profiles/qwen-cu128 python - <<'PY'
import torch
from jmhgen.config.schema import GRPOConfig

assert torch.cuda.is_available()
print("device", torch.cuda.get_device_name(0), "bf16", torch.cuda.is_bf16_supported())
cfg = GRPOConfig.from_yaml("configs/grpo/qwen35-4b.yaml")
assert cfg.model_family == "qwen" and cfg.vllm_mode == "server"
print("config", cfg.base_model, "max_model_len", cfg.vllm_max_model_length)
print("classpaths", sorted(cfg.project_classpaths))
PY

echo "=== [4/4] GRPO smoke (4 prompts, 1 step, no full run) ==="
export GRPO_CONFIG=configs/grpo/qwen35-4b.yaml
export SMOKE_FIRST=1
export SMOKE_STEPS=1
export RUN_FULL=0
CUDA_VISIBLE_DEVICES=1 "${REPO}/scripts/remote/run-grpo.sh" 2>&1 | tee "${TRAIN_LOG}"

echo "=== SMOKE PASSED ==="
