#!/usr/bin/env bash
# Overnight Qwen TRL-serve diagnosis + opportunistic smoke.
#
# State of play after 7 failed attempts (see training-run-failures.md §5):
#   * OpenAI mode (`vllm serve`) reaches readiness in 104 s with identical model args.
#   * TRL mode never started. With VLLM_ENABLE_V1_MULTIPROCESSING=1 the log stops after
#     15 lines, before any EngineCore output.
#   * With VLLM_ENABLE_V1_MULTIPROCESSING=0 (engine in-process, job 2384888) it got further
#     than ever: it reached "[core.py:116] Initializing a V1 LLM engine" and then sat for 28
#     minutes. So the stall is now *after* engine init -- executor/worker creation, device
#     init or NCCL -- not in imports.
#   * Ruled out: FlashInfer GDN JIT (OpenAI mode hits the same JIT and starts), humming's CUDA
#     scan / nvrtc build (measured: 0.1 s each, binary already cached in $HOME/.humming),
#     TRL's DP master port (get_open_port(), and DP paths are guarded on dp_size > 1).
#
# This job answers "where exactly?" and "is it hung or merely slow?" in one shot:
#   1. vLLM runs UNDER py-spy, so py-spy is an ancestor and ptrace_scope permits sampling
#      (`srun --overlap py-spy dump` against a sibling gets Permission Denied).
#   2. The readiness wait is 2 h, not 30 min -- if this is pathological slowness rather than a
#      deadlock, we find out instead of killing it at minute 30.
#   3. If the server does come up, it proceeds to the 4-prompt smoke, so a success overnight
#      yields a fully validated Qwen reward path rather than just a green light.
#
#   sbatch scripts/cluster/night-qwen-serve.sbatch
#SBATCH --job-name=jmh-qwen-night
#SBATCH --partition=aisc-batch
#SBATCH --account=aisc
#SBATCH --qos=aisc
#SBATCH --nodes=1
#SBATCH --gres=gpu:h100:2
#SBATCH --cpus-per-task=24
#SBATCH --mem=256G
#SBATCH --time=8:00:00
#SBATCH --output=/sc/scratch/zongxiong.chen/jonas/logs/qwen-night_%j.out
#SBATCH --error=/sc/scratch/zongxiong.chen/jonas/logs/qwen-night_%j.err

set -uo pipefail
export PYTHONUNBUFFERED=1

JMH_ROOT="${JMH_ROOT:-/sc/scratch/zongxiong.chen/jonas}"
REPO="${JMH_ROOT}/JMH_Training_Pipeline"
PYSPY="${JMH_ROOT}/tools/pyspy/bin/py-spy"
DIAG="${JMH_ROOT}/logs/qwen-night-${SLURM_JOB_ID}"
mkdir -p "${DIAG}"

cd "${REPO}" || exit 1
# shellcheck source=/dev/null
source "${REPO}/scripts/cluster/env.hpi.sh"

export CUDA_HOME="${CUDA_HOME:-/sc/projects/sci-aisc/cuda-12.8}"
export PATH="${CUDA_HOME}/bin:${PATH}"
export VLLM_MAIN_CUDA_VERSION=12.8
export JMHGEN_MODEL_PROFILE=qwen
export JMHGEN_VOLUME="${JMH_ROOT}"
export JMHGEN_SCRATCH="${JMH_ROOT}"
export REPO_DIR="${REPO}"
export VLLM_SERVE_MODE=trl
export VLLM_VENV="${JMH_ROOT}/.venv-qwen-cu128"
unset UV_PROJECT_ENVIRONMENT

# The two settings that got us furthest, plus DEBUG so the child actually talks.
export VLLM_ENABLE_V1_MULTIPROCESSING=0
export VLLM_LOGGING_LEVEL=DEBUG
export JMHGEN_GDN_PREFILL_BACKEND="${JMHGEN_GDN_PREFILL_BACKEND:-triton}"

export VLLM_MODEL="${VLLM_MODEL:-Qwen/Qwen3.5-4B}"
export VLLM_PORT="${VLLM_PORT:-8000}"
export VLLM_MAX_MODEL_LEN="${VLLM_MAX_MODEL_LEN:-32768}"
export VLLM_GPU_MEMORY_UTILIZATION="${VLLM_GPU_MEMORY_UTILIZATION:-0.90}"

export GRPO_CONFIG="${GRPO_CONFIG:-configs/grpo/mutation70-qwen.yaml}"
export GRPO_CORPUS="${GRPO_CORPUS:-full}"
export GRPO_LOG_TAG="${GRPO_LOG_TAG:-qwen-night}"
export SMOKE_FIRST=1
export SMOKE_STEPS=2
export RUN_FULL=0

if [[ -z "${HF_TOKEN:-}" && -f "${HOME}/.cache/huggingface/token" ]]; then
  HF_TOKEN="$(tr -d '[:space:]' < "${HOME}/.cache/huggingface/token")"
  export HF_TOKEN
fi
[[ -z "${HF_TOKEN:-}" ]] && { echo "FATAL: no HF_TOKEN" >&2; exit 1; }

echo "=== node=$(hostname) job=${SLURM_JOB_ID} diag=${DIAG} ==="
nvidia-smi --query-gpu=index,name,memory.total,driver_version --format=csv,noheader
echo "VLLM_ENABLE_V1_MULTIPROCESSING=${VLLM_ENABLE_V1_MULTIPROCESSING} gdn=${JMHGEN_GDN_PREFILL_BACKEND}"

VLLM_LOG="${DIAG}/vllm.log"
STACKS="${DIAG}/stacks.raw"
READY_URL="http://127.0.0.1:${VLLM_PORT}/health/"

# Only ever signal our own PID and its descendants: this account is shared, and other people's
# vLLM processes run under the same uid on this node.
_descendants() {
  local pid="$1" kid
  for kid in $(cat /proc/"${pid}"/task/*/children 2>/dev/null); do
    _descendants "${kid}"
    printf '%s\n' "${kid}"
  done
}
stop_tree() {
  local pid="${1:-}" kids
  [[ -z "${pid}" ]] && return 0
  kill -0 "${pid}" 2>/dev/null || return 0
  kids="$(_descendants "${pid}")"
  kill -TERM ${kids} "${pid}" 2>/dev/null || true
  for _ in $(seq 1 15); do
    kill -0 "${pid}" 2>/dev/null || return 0
    sleep 2
  done
  kids="$(_descendants "${pid}")"
  kill -KILL ${kids} "${pid}" 2>/dev/null || true
  return 0
}
cleanup() { stop_tree "${SPY_PID:-}"; }
trap cleanup EXIT

echo "=== starting vLLM under py-spy (samples land in ${STACKS}) ==="
if [[ -x "${PYSPY}" ]]; then
  CUDA_VISIBLE_DEVICES=0 "${PYSPY}" record --subprocesses --format raw --rate 1 \
    --duration 25200 --output "${STACKS}" -- \
    bash "${REPO}/scripts/remote/start-vllm.sh" >"${VLLM_LOG}" 2>&1 &
else
  echo "WARNING: py-spy missing at ${PYSPY}; running without profiling" >&2
  CUDA_VISIBLE_DEVICES=0 bash "${REPO}/scripts/remote/start-vllm.sh" >"${VLLM_LOG}" 2>&1 &
fi
SPY_PID=$!

# 2 h of patience (720 x 10 s), versus the 30 min that killed every previous attempt.
READY=0
for i in $(seq 1 720); do
  if ! kill -0 "${SPY_PID}" 2>/dev/null; then
    echo "vLLM exited early after ~$((i * 10))s"; break
  fi
  if curl -sf "${READY_URL}" >/dev/null 2>&1; then
    echo "*** vLLM READY after ~$((i * 10))s ***"; READY=1; break
  fi
  # Every 5 min: how far did the log get, and is the engine burning CPU or blocked?
  if (( i % 30 == 0 )); then
    echo "--- t=$((i * 10))s: log=$(wc -l < "${VLLM_LOG}") lines; last:"
    grep -vE "UserWarning|is_vllm_available" "${VLLM_LOG}" | tail -2 | cut -c1-160
    ps -o pid,etimes,times,stat,wchan:20,cmd -u "$USER" 2>/dev/null \
      | grep -E "trl_vllm|EngineCore|spawn_main" | grep -v grep | cut -c1-150
    nvidia-smi --query-gpu=index,memory.used,utilization.gpu --format=csv,noheader
  fi
  sleep 10
done

if [[ "${READY}" == "1" ]]; then
  echo "=== server up: running the 4-prompt smoke on GPU 1 ==="
  CUDA_VISIBLE_DEVICES=1 "${REPO}/scripts/remote/run-grpo.sh"
  echo "smoke exited ${?}"
  stop_tree "${SPY_PID}"
else
  echo "=== NOT READY after 2 h — collecting the stall profile ==="
  echo "--- vLLM log tail ---"
  grep -vE "UserWarning|is_vllm_available" "${VLLM_LOG}" | tail -40 | cut -c1-190
  # SIGTERM lets py-spy flush its samples; only then force the tree down.
  kill -TERM "${SPY_PID}" 2>/dev/null || true
  for _ in $(seq 1 30); do kill -0 "${SPY_PID}" 2>/dev/null || break; sleep 2; done
  stop_tree "${SPY_PID}"
  if [[ -s "${STACKS}" ]]; then
    echo "--- hottest leaf frames (where it actually sat) ---"
    sed 's/^process [0-9]*:"[^"]*";//; s/process [0-9]*:"[^"]*";//g' "${STACKS}" \
      | sed 's/ [0-9]*$//' | awk -F';' '{print $NF}' | sort | uniq -c | sort -rn | head -20
    echo "--- deepest sample ---"
    sed 's/^process [0-9]*:"[^"]*";//; s/process [0-9]*:"[^"]*";//g' "${STACKS}" \
      | tail -1 | tr ';' '\n' | tail -30
  else
    echo "no samples written to ${STACKS}"
  fi
fi

echo "=== DONE; artifacts in ${DIAG} ==="
ls -la "${DIAG}"
