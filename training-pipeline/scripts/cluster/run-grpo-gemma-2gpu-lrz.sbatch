#!/usr/bin/env bash
# Gemma 4 E2B - run 2: coverage-guided 70%-mutation GRPO on LRZ (2x H100 94GB), TRL server mode.
#
# Run 1 (job 5723231) was one GPU with vLLM colocated. That worked, but capped the run twice
# over: vLLM held ~30 % of the card, and the trainer still OOM'd in backward at step 108 of
# job 5721357. Server mode puts vLLM on GPU 0 and gives GPU 1 entirely to the trainer, which
# is what pays for the 40960-token window this config needs.
#
# What run 2 changes and why (all measured, see docs/evaluation-gemma-grpo-run1.md):
#   * max_completion_length 4096 -> 16384. Run 1 logged clipped_ratio mean 0.10 / max 0.80,
#     so ~1 rollout in 10 was cut off mid-class and could not compile by construction.
#   * thinking ON during rollouts. The JMH-Bench matrix scored the run-1 adapter at 18 % vs
#     10 % mutation and 34 % vs 21 % first-shot compile purely on that flag -- but run 1
#     trained with it off, so training and serving disagreed on the single biggest lever.
#   * LR 1e-6 -> 3e-6, max_source_chars 24000 -> 48000 (834 prompts, up from 744).
#
# Same cu130 stack as the Gemma run-1 job: profiles/gemma-cu130 + released vLLM. Every
# lrz-hgx-h100-94x4 node measured so far runs driver 580.x / CUDA 13.0.
#
# Stage 1 is ONE epoch (~834 steps). resume_from_checkpoint: auto means a follow-up job
# continues rather than restarting, so a second epoch is a resubmit, not a rerun.
#
#   sbatch scripts/cluster/run-grpo-gemma-2gpu-lrz.sbatch
#   sbatch --export=ALL,RUN_FULL=0 scripts/cluster/run-grpo-gemma-2gpu-lrz.sbatch   # smoke only
#SBATCH --job-name=jmh-grpo-gemma2
#SBATCH --partition=lrz-hgx-h100-94x4
#SBATCH --qos=gpu
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --gres=gpu:2
# The reward compiles and JMH-runs every rollout, so this is CPU-hungry alongside the GPUs.
#SBATCH --cpus-per-task=24
#SBATCH --mem=240G
#SBATCH --time=2-00:00:00
#SBATCH --output=/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh/logs/grpo-gemma2-lrz_%j.out
#SBATCH --error=/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh/logs/grpo-gemma2-lrz_%j.err

set -uo pipefail

if [[ -z "${_JMH_LRZ_REEXEC:-}" ]] && command -v stdbuf >/dev/null 2>&1; then
  export _JMH_LRZ_REEXEC=1
  exec stdbuf -oL -eL bash "$0" "$@"
fi
export PYTHONUNBUFFERED=1

JMH_ROOT="${JMH_ROOT:-/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh}"
REPO="${REPO:-${JMH_ROOT}/JMH_Training_Pipeline}"

cd "${REPO}" || exit 1
# shellcheck source=/dev/null
source "${REPO}/scripts/cluster/env.lrz.sh"

export JMHGEN_MODEL_PROFILE=gemma
export JMHGEN_VOLUME="${JMH_ROOT}"
export JMHGEN_SCRATCH="${JMH_ROOT}"
export REPO_DIR="${REPO}"
export GRPO_CONFIG="${GRPO_CONFIG:-configs/grpo/mutation70-gemma-2gpu.yaml}"
export GRPO_CORPUS="${GRPO_CORPUS:-full}"
export SMOKE_FIRST="${SMOKE_FIRST:-1}"
export SMOKE_STEPS="${SMOKE_STEPS:-2}"
export RUN_FULL="${RUN_FULL:-1}"
# GRPO needs TRL's private rollout + weight-sync endpoints, not the OpenAI API.
export VLLM_SERVE_MODE=trl
# cu130 build: make sure no CUDA-12.8 toolkit leaks in from the submitting shell (it would
# send start-vllm.sh down the prebuilt-cu128-wheel path).
unset CUDA_HOME VLLM_MAIN_CUDA_VERSION
export VLLM_VENV="${JMH_ROOT}/.venv-gemma-cu130"
export UV_PROJECT_ENVIRONMENT="${VLLM_VENV}"
if [[ "${UV_PROJECT_ENVIRONMENT}" == *cu128* ]]; then
  echo "FATAL: refusing a cu128 venv for the cu130 Gemma run: ${UV_PROJECT_ENVIRONMENT}" >&2
  exit 2
fi

export VLLM_MODEL="${VLLM_MODEL:-google/gemma-4-E2B-it}"
export VLLM_PORT="${VLLM_PORT:-8000}"
# Must match vllm_max_model_length in the config (20480 prompt + 16384 completion).
export VLLM_MAX_MODEL_LEN="${VLLM_MAX_MODEL_LEN:-40960}"
export VLLM_GPU_MEMORY_UTILIZATION="${VLLM_GPU_MEMORY_UTILIZATION:-0.90}"

if [[ -z "${HF_TOKEN:-}" ]]; then
  for token_file in "${HOME}/.cache/huggingface/token" "${JMH_ROOT}/.cache/huggingface/token"; do
    [[ -f "${token_file}" ]] && { HF_TOKEN="$(tr -d '[:space:]' < "${token_file}")"; export HF_TOKEN; break; }
  done
fi
[[ -z "${HF_TOKEN:-}" ]] && { echo "FATAL: HF_TOKEN not set." >&2; exit 1; }

echo "=== Gemma 4 E2B GRPO run 2 on LRZ (2 GPUs, TRL server mode, thinking ON) ==="
echo "node=$(hostname) job=${SLURM_JOB_ID:-local} config=${GRPO_CONFIG} corpus=${GRPO_CORPUS}"
echo "venv=${UV_PROJECT_ENVIRONMENT}"
nvidia-smi --query-gpu=index,name,driver_version,memory.total --format=csv,noheader

DRIVER_CUDA="$(nvidia-smi 2>/dev/null | sed -n 's/.*CUDA Version: \([0-9.]*\).*/\1/p' | head -1)"
echo "driver CUDA=${DRIVER_CUDA:-unknown}"
case "${DRIVER_CUDA}" in
  1[3-9].*|[2-9][0-9].*) echo "driver CUDA ${DRIVER_CUDA} OK for cu130" ;;
  *) echo "FATAL: the cu130 stack needs driver CUDA >= 13; node reports '${DRIVER_CUDA}'." >&2; exit 2 ;;
esac

[[ -x "${UV_PROJECT_ENVIRONMENT}/bin/python" ]] || {
  echo "FATAL: Gemma cu130 venv missing at ${UV_PROJECT_ENVIRONMENT}" >&2
  echo "  UV_PROJECT_ENVIRONMENT=${UV_PROJECT_ENVIRONMENT} uv sync --inexact --project profiles/gemma-cu130" >&2
  exit 2
}
"${UV_PROJECT_ENVIRONMENT}/bin/python" -c "import torch,vllm,trl;print('torch',torch.__version__,'vllm',vllm.__version__,'trl',trl.__version__)" || exit 2

if [[ ! -x "${JAVA_HOME}/bin/java" ]] || ! command -v mvn >/dev/null 2>&1; then
  echo "FATAL: the JMH reward needs java + mvn on PATH (JAVA_HOME=${JAVA_HOME})." >&2
  exit 2
fi

# A prompt-set project with no classpath, an unresolvable classpath entry,
# a SUT jar newer than javac, or a structurally dead reward term all yield an all-zero reward
# and a wasted allocation. --reward-smoke exercises compile -> JMH run -> mutation scoring here.
echo "=== preflight ==="
if ! "${UV_PROJECT_ENVIRONMENT}/bin/python" scripts/preflight_grpo.py \
      "${GRPO_CONFIG}" --corpus "${GRPO_CORPUS}" --reward-smoke; then
  echo "FATAL: preflight failed — not spending GPU time on a run that cannot learn." >&2
  exit 3
fi

LOG_DIR="${JMH_ROOT}/logs"
mkdir -p "${LOG_DIR}"
VLLM_LOG="${LOG_DIR}/vllm-gemma2-lrz-${SLURM_JOB_ID:-local}.log"
READY_URL="http://127.0.0.1:${VLLM_PORT}/health/"

_descendants() {
  local pid="$1" kid
  for kid in $(cat /proc/"${pid}"/task/*/children 2>/dev/null); do
    _descendants "${kid}"; printf '%s\n' "${kid}"
  done
}
# Only ever signal our own tree. A bare `kill` + `wait` also deadlocks here: vLLM can ignore
# SIGTERM while blocked, and the job then holds its GPUs for the full walltime doing nothing
# (hpi2 job 2384618 leaked 2 H100s for ~47 h that way).
cleanup() {
  local pid="${VLLM_PID:-}" kids
  [[ -z "${pid}" ]] && return 0
  kill -0 "${pid}" 2>/dev/null || return 0
  echo "stopping vLLM pid=${pid}"
  kids="$(_descendants "${pid}")"
  kill -TERM ${kids} "${pid}" 2>/dev/null || true
  for _ in $(seq 1 15); do kill -0 "${pid}" 2>/dev/null || return 0; sleep 2; done
  echo "vLLM ignored SIGTERM after 30s; escalating to SIGKILL"
  kids="$(_descendants "${pid}")"
  kill -KILL ${kids} "${pid}" 2>/dev/null || true
  return 0
}
trap cleanup EXIT

echo "=== starting vLLM (TRL mode, len=${VLLM_MAX_MODEL_LEN}) on GPU 0 -> ${VLLM_LOG} ==="
CUDA_VISIBLE_DEVICES=0 "${REPO}/scripts/remote/start-vllm.sh" >"${VLLM_LOG}" 2>&1 &
VLLM_PID=$!

# 30 min. A healthy cold start is ~2 min (the working OpenAI-mode run did 104 s); the extra
# headroom covers first-touch weight reads from GPFS.
READY=0
for i in $(seq 1 180); do
  if ! kill -0 "${VLLM_PID}" 2>/dev/null; then
    echo "FATAL: vLLM exited early; see ${VLLM_LOG}" >&2
    grep -vE "UserWarning|is_vllm_available" "${VLLM_LOG}" | tail -60 >&2
    exit 1
  fi
  if curl -sf "${READY_URL}" >/dev/null 2>&1; then
    echo "*** vLLM ready after ~$((i * 10))s ***"; READY=1; break
  fi
  if (( i % 6 == 0 )); then
    echo "  waiting (${i}/180) ..."
    grep -vE "UserWarning|is_vllm_available" "${VLLM_LOG}" | tail -2 | cut -c1-150
  fi
  sleep 10
done
if [[ "${READY}" != "1" ]]; then
  echo "FATAL: vLLM did not become ready within 30 min; see ${VLLM_LOG}" >&2
  grep -vE "UserWarning|is_vllm_available" "${VLLM_LOG}" | tail -80 >&2
  exit 1
fi

echo "=== starting GRPO trainer on GPU 1 ==="
CUDA_VISIBLE_DEVICES=1 "${REPO}/scripts/remote/run-grpo.sh"
rc=$?
echo "GRPO exited with code ${rc}"
exit "${rc}"
