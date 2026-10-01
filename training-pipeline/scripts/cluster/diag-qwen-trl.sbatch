#!/usr/bin/env bash
# Diagnose Qwen TRL hang: plain vLLM LLM() then TRL serve with verbose logs.
#SBATCH --job-name=jmh-qwen-diag
#SBATCH --partition=aisc-batch
#SBATCH --account=aisc
#SBATCH --qos=aisc
#SBATCH --nodes=1
#SBATCH --gres=gpu:h100:1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64G
#SBATCH --time=0:45:00
#SBATCH --output=/sc/scratch/zongxiong.chen/jonas/logs/qwen-diag_%j.out
#SBATCH --error=/sc/scratch/zongxiong.chen/jonas/logs/qwen-diag_%j.err

set -euo pipefail
export PYTHONUNBUFFERED=1
export VLLM_LOGGING_LEVEL=INFO
export UV_LINK_MODE=copy

JMH_ROOT="${JMH_ROOT:-/sc/scratch/zongxiong.chen/jonas}"
REPO="${JMH_ROOT}/JMH_Training_Pipeline"
VENV="${JMH_ROOT}/.venv-qwen-cu128"
cd "${REPO}"
# shellcheck source=/dev/null
source "${REPO}/scripts/cluster/env.hpi.sh"

export CUDA_HOME="${CUDA_HOME:-/sc/projects/sci-aisc/cuda-12.8}"
export PATH="${CUDA_HOME}/bin:${PATH}"
export VLLM_MAIN_CUDA_VERSION=12.8
export JMHGEN_MODEL_PROFILE=qwen
export JMHGEN_VOLUME="${JMH_ROOT}"
export UV_PROJECT_ENVIRONMENT="${VENV}"
export VLLM_VENV="${VENV}"
export CUDA_VISIBLE_DEVICES=0

if [[ -z "${HF_TOKEN:-}" && -f "${HOME}/.cache/huggingface/token" ]]; then
  HF_TOKEN="$(tr -d '[:space:]' < "${HOME}/.cache/huggingface/token")"
  export HF_TOKEN
fi

echo "=== node=$(hostname) job=${SLURM_JOB_ID} ==="
nvidia-smi --query-gpu=index,name,memory.total --format=csv,noheader
for curand_include in "${VENV}"/lib/python*/site-packages/nvidia/curand/include; do
  [[ -d "${curand_include}" ]] && export CPATH="${curand_include}${CPATH:+:${CPATH}}" && break
done
export FLASHINFER_NVCC="${CUDA_HOME}/bin/nvcc"
export FLASHINFER_WORKSPACE_BASE="${JMH_ROOT}/.cache/vllm/flashinfer"

echo "=== [1/2] plain vLLM.LLM language_model_only (no TRL) ==="
"${VENV}/bin/python" - <<'PY'
import os, time, torch
from vllm import LLM

print(f"torch={torch.__version__} cuda={torch.version.cuda}", flush=True)
print("cuda_available", torch.cuda.is_available(), "device", torch.cuda.get_device_name(0), flush=True)
t0 = time.time()
llm = LLM(
    model="Qwen/Qwen3.5-4B",
    max_model_len=2048,
    gpu_memory_utilization=0.5,
    enforce_eager=True,
    language_model_only=True,
)
print(f"LLM ready in {time.time()-t0:.1f}s", flush=True)
out = llm.generate(["Say hi in one word."], use_tqdm=False)
print("gen", out[0].outputs[0].text[:200], flush=True)
del llm
print("plain LLM OK", flush=True)
PY

echo "=== [2/2] TRL text-only serve readiness (60s probe) ==="
export VLLM_SERVE_MODE=trl
export VLLM_MODEL=Qwen/Qwen3.5-4B
export VLLM_PORT=8000
export VLLM_MAX_MODEL_LEN=2048
export VLLM_GPU_MEMORY_UTILIZATION=0.5
LOG="${JMH_ROOT}/logs/qwen-diag-vllm-${SLURM_JOB_ID}.log"
"${REPO}/scripts/remote/start-vllm.sh" >"${LOG}" 2>&1 &
PID=$!
for i in $(seq 1 36); do
  if ! kill -0 "${PID}" 2>/dev/null; then
    echo "FATAL: serve exited"; tail -80 "${LOG}"; exit 1
  fi
  if curl -sf http://127.0.0.1:8000/health/ >/dev/null; then
    echo "TRL health OK after ~$((i*10))s"
    curl -s http://127.0.0.1:8000/health/; echo
    kill "${PID}" 2>/dev/null || true
    wait "${PID}" 2>/dev/null || true
    echo "=== DIAG PASSED ==="
    exit 0
  fi
  echo "  wait ${i}/36 ..."
  tail -3 "${LOG}" || true
  sleep 10
done
echo "FATAL: TRL serve timeout"; tail -120 "${LOG}"; kill "${PID}" 2>/dev/null || true; exit 1
