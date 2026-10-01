#!/usr/bin/env bash
# Does STOCK `trl vllm-serve` start Qwen3.5 on released vLLM 0.23.0 (CUDA 13)?
#
# This is the test that decides the Qwen rollout stack. Background:
#   * The hand-built wheel (0.23.1rc1.dev1187+gdc9f845dd) exists to support CUDA 12.8, which is
#     what most aisc-batch H100s ship. Released vLLM is cu13-only.
#   * But TRL 1.7.1 supports vLLM 0.14.0-0.23.0, so that wheel is OUT of range, and
#     `trl vllm-serve` has never started on it in 8 attempts -- it stalls immediately after
#     "Initializing a V1 LLM engine" while OpenAI mode serves the same model in 104 s.
#   * vLLM 0.23.0 already has Qwen3.5 (models/qwen3_5.py, the registry entry, GDN linear
#     attention under layers/mamba/gdn, and --language-model-only), on the same torch 2.11.0.
#
# So: same model, supported TRL/vLLM pair, no custom wheel, and -- for this first run -- NO
# jmhgen shim. If it starts, the whole custom-wheel path can be retired. If it only starts with
# --language-model-only (set LANGUAGE_MODEL_ONLY=1), we reinstate the small shim, which is now
# a patch on a supported stack rather than a workaround on an unsupported one.
#
#   sbatch scripts/cluster/test-qwen-cu130-serve.sbatch
#   NODELIST=gx13v1,gxNN sbatch scripts/cluster/test-qwen-cu130-serve.sbatch
#SBATCH --job-name=jmh-qwen130
#SBATCH --partition=aisc-batch
#SBATCH --account=aisc
#SBATCH --qos=aisc
#SBATCH --nodes=1
#SBATCH --gres=gpu:h100:1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64G
#SBATCH --time=40:00
#SBATCH --output=/sc/scratch/zongxiong.chen/jonas/logs/qwen130_%j.out
#SBATCH --error=/sc/scratch/zongxiong.chen/jonas/logs/qwen130_%j.err

set -uo pipefail
export PYTHONUNBUFFERED=1

JMH_ROOT="${JMH_ROOT:-/sc/scratch/zongxiong.chen/jonas}"
REPO="${JMH_ROOT}/JMH_Training_Pipeline"
VENV="${JMH_ROOT}/.venv-qwen-cu130"
PYSPY="${JMH_ROOT}/tools/pyspy/bin/py-spy"
DIAG="${JMH_ROOT}/logs/qwen130-${SLURM_JOB_ID}"
mkdir -p "${DIAG}"

cd "${REPO}" || exit 1
# shellcheck source=/dev/null
source "${REPO}/scripts/cluster/env.hpi.sh"

# cu130 must NOT inherit the cu128 toolkit settings.
unset CUDA_HOME VLLM_MAIN_CUDA_VERSION
export VLLM_LOGGING_LEVEL=DEBUG
export CUDA_VISIBLE_DEVICES=0
# The venv's bin on PATH so any JIT that shells out to ninja can find it.
export PATH="${VENV}/bin:${PATH}"

if [[ -z "${HF_TOKEN:-}" && -f "${HOME}/.cache/huggingface/token" ]]; then
  HF_TOKEN="$(tr -d '[:space:]' < "${HOME}/.cache/huggingface/token")"
  export HF_TOKEN
fi
[[ -z "${HF_TOKEN:-}" ]] && { echo "FATAL: no HF_TOKEN" >&2; exit 1; }

echo "=== node=$(hostname) job=${SLURM_JOB_ID} ==="
nvidia-smi --query-gpu=name,driver_version --format=csv,noheader
DRIVER_CUDA="$(nvidia-smi 2>/dev/null | sed -n 's/.*CUDA Version: \([0-9.]*\).*/\1/p' | head -1)"
echo "driver CUDA=${DRIVER_CUDA}"
case "${DRIVER_CUDA}" in
  1[3-9].*|[2-9][0-9].*) echo "OK for cu130" ;;
  *) echo "FATAL: vLLM 0.23.0 is cu13-only; this node reports CUDA ${DRIVER_CUDA}." >&2; exit 2 ;;
esac

[[ -x "${VENV}/bin/trl" ]] || { echo "FATAL: ${VENV} not built" >&2; exit 2; }
"${VENV}/bin/python" -c "import torch,vllm,trl;print('torch',torch.__version__,'vllm',vllm.__version__,'trl',trl.__version__)" || exit 2

LOG="${DIAG}/serve.log"
PORT=8000
READY_URL="http://127.0.0.1:${PORT}/health/"

_descendants() {
  local pid="$1" kid
  for kid in $(cat /proc/"${pid}"/task/*/children 2>/dev/null); do
    _descendants "${kid}"; printf '%s\n' "${kid}"
  done
}
# Shared account: only ever signal our own tree, never a pattern match on "vllm".
stop_tree() {
  local pid="${1:-}" kids
  [[ -z "${pid}" ]] && return 0
  kill -0 "${pid}" 2>/dev/null || return 0
  kids="$(_descendants "${pid}")"
  kill -TERM ${kids} "${pid}" 2>/dev/null || true
  for _ in $(seq 1 15); do kill -0 "${pid}" 2>/dev/null || return 0; sleep 2; done
  kids="$(_descendants "${pid}")"
  kill -KILL ${kids} "${pid}" 2>/dev/null || true
  return 0
}
trap 'stop_tree "${SERVE_PID:-}"' EXIT

EXTRA=()
if [[ "${LANGUAGE_MODEL_ONLY:-0}" == "1" ]]; then
  echo "=== using the jmhgen shim (--language-model-only) ==="
  "${VENV}/bin/python" -m jmhgen.serving.trl_vllm_serve \
    --model Qwen/Qwen3.5-4B --max-model-len 32768 --gpu-memory-utilization 0.90 \
    --enforce-eager true --port "${PORT}" --language-model-only >"${LOG}" 2>&1 &
else
  echo "=== STOCK trl vllm-serve, no shim (multimodal towers will load) ==="
  "${VENV}/bin/trl" vllm-serve \
    --model Qwen/Qwen3.5-4B --max-model-len 32768 --gpu-memory-utilization 0.90 \
    --enforce-eager true --port "${PORT}" "${EXTRA[@]}" >"${LOG}" 2>&1 &
fi
SERVE_PID=$!

READY=0
for i in $(seq 1 150); do          # 25 min; the working OpenAI-mode start took 104 s
  if ! kill -0 "${SERVE_PID}" 2>/dev/null; then
    echo "serve EXITED after ~$((i * 10))s"; break
  fi
  if curl -sf "${READY_URL}" >/dev/null 2>&1; then
    echo "*** READY after ~$((i * 10))s ***"; READY=1; break
  fi
  if (( i % 6 == 0 )); then
    echo "--- t=$((i * 10))s log=$(wc -l < "${LOG}") lines"
    grep -vE "UserWarning|is_vllm_available" "${LOG}" | tail -2 | cut -c1-150
    nvidia-smi --query-gpu=memory.used,utilization.gpu --format=csv,noheader
  fi
  sleep 10
done

if [[ "${READY}" == "1" ]]; then
  echo "=== SUCCESS: stock TRL serve works on vLLM 0.23.0 ==="
  curl -s "${READY_URL}"; echo
  curl -s "http://127.0.0.1:${PORT}/get_world_size/" 2>/dev/null; echo
else
  echo "=== NOT READY — capturing where it sat ==="
  # py-spy must be an ancestor (this script is); target only real Python processes.
  if [[ -x "${PYSPY}" ]]; then
    for pid in $(_descendants "${SERVE_PID}") "${SERVE_PID}"; do
      grep -qa python "/proc/${pid}/cmdline" 2>/dev/null || continue
      echo "--- pid=${pid} state=$(awk '{print $3}' /proc/${pid}/stat 2>/dev/null) wchan=$(cat /proc/${pid}/wchan 2>/dev/null)"
      timeout 60 "${PYSPY}" dump --pid "${pid}" --nonblocking 2>&1 | head -40
    done
  fi
fi

echo "=== serve log tail ==="
grep -vE "UserWarning|is_vllm_available" "${LOG}" | tail -25 | cut -c1-190
echo "=== DONE; artifacts in ${DIAG} ==="
