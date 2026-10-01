#!/usr/bin/env bash
# Qwen3.5-4B — coverage-guided 70%-mutation GRPO on LRZ AI Systems (2x H100 94GB).
#
# Two GPUs, because Qwen needs TRL *server* mode: vLLM serves rollouts on GPU 0 and the trainer
# runs on GPU 1. Colocate is not an option for Qwen3.5 (scripts/remote/run-grpo.sh refuses it,
# and it is known not to work). That costs queue time -- 2-GPU requests on this partition wait
# ~47 h at the median versus ~5.6 h for 1 GPU -- but there is no single-GPU alternative.
#
# Stack: RELEASED vLLM 0.23.0 (cu130), not the hand-built cu128 wheel. 0.23.0 already has
# Qwen3.5 (models/qwen3_5.py, the registry entry, GDN linear attention, --language-model-only)
# on the same torch 2.11.0, and it is inside TRL 1.7.1's supported range -- whereas the custom
# 0.23.1rc1.dev1187 wheel is outside it and never once started a TRL server in 8 attempts.
# The catch is that released vLLM is CUDA-13 only (nvidia-cutlass-dsl[cu13];
# _C.abi3.so links libcudart.so.13), which is fine here: every lrz-hgx-h100-94x4 node we have
# measured runs driver 580.x / CUDA 13.0. On hpi2 only gx13v1 would qualify.
#
#   sbatch scripts/cluster/run-grpo-qwen-lrz.sbatch
#   sbatch --export=ALL,RUN_FULL=0 scripts/cluster/run-grpo-qwen-lrz.sbatch   # smoke only
#SBATCH --job-name=jmh-grpo-qwen
#SBATCH --partition=lrz-hgx-h100-94x4
#SBATCH --qos=gpu
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --gres=gpu:2
# The reward compiles and JMH-runs every rollout, so this is CPU-hungry alongside the GPUs.
#SBATCH --cpus-per-task=24
#SBATCH --mem=240G
#SBATCH --time=2-00:00:00
#SBATCH --output=/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh/logs/grpo-qwen-lrz_%j.out
#SBATCH --error=/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh/logs/grpo-qwen-lrz_%j.err

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

# qwen-cu130, NOT qwen: the plain `qwen` profile maps to profiles/qwen-cu128 and would
# uv-sync cu128 torch into this cu130 venv, breaking it.
export JMHGEN_MODEL_PROFILE=qwen-cu130
export JMHGEN_VOLUME="${JMH_ROOT}"
export JMHGEN_SCRATCH="${JMH_ROOT}"
export REPO_DIR="${REPO}"
export GRPO_CONFIG="${GRPO_CONFIG:-configs/grpo/mutation70-qwen.yaml}"
export GRPO_CORPUS="${GRPO_CORPUS:-full}"
export SMOKE_FIRST="${SMOKE_FIRST:-1}"
export SMOKE_STEPS="${SMOKE_STEPS:-2}"
export RUN_FULL="${RUN_FULL:-1}"
# GRPO needs TRL's private rollout + weight-sync endpoints, not the OpenAI API.
export VLLM_SERVE_MODE=trl
# cu130 build: there is no CUDA-12.8 toolkit involved here, so make sure none leaks in from
# the submitting shell (it would send start-vllm.sh down the prebuilt-cu128-wheel path).
unset CUDA_HOME VLLM_MAIN_CUDA_VERSION
export VLLM_VENV="${JMH_ROOT}/.venv-qwen-cu130"
export UV_PROJECT_ENVIRONMENT="${VLLM_VENV}"
if [[ "${UV_PROJECT_ENVIRONMENT}" == *cu128* ]]; then
  echo "FATAL: refusing the cu128 venv for the cu130 Qwen run: ${UV_PROJECT_ENVIRONMENT}" >&2
  exit 2
fi

export VLLM_MODEL="${VLLM_MODEL:-Qwen/Qwen3.5-4B}"
export VLLM_PORT="${VLLM_PORT:-8000}"
export VLLM_MAX_MODEL_LEN="${VLLM_MAX_MODEL_LEN:-32768}"
export VLLM_GPU_MEMORY_UTILIZATION="${VLLM_GPU_MEMORY_UTILIZATION:-0.90}"

if [[ -z "${HF_TOKEN:-}" ]]; then
  for token_file in "${HOME}/.cache/huggingface/token" "${JMH_ROOT}/.cache/huggingface/token"; do
    [[ -f "${token_file}" ]] && { HF_TOKEN="$(tr -d '[:space:]' < "${token_file}")"; export HF_TOKEN; break; }
  done
fi
[[ -z "${HF_TOKEN:-}" ]] && { echo "FATAL: HF_TOKEN not set." >&2; exit 1; }

echo "=== Qwen3.5-4B GRPO on LRZ (released vLLM 0.23.0 / cu130) ==="
echo "node=$(hostname) job=${SLURM_JOB_ID:-local} config=${GRPO_CONFIG} corpus=${GRPO_CORPUS}"
echo "venv=${UV_PROJECT_ENVIRONMENT}"
nvidia-smi --query-gpu=index,name,driver_version,memory.total --format=csv,noheader

DRIVER_CUDA="$(nvidia-smi 2>/dev/null | sed -n 's/.*CUDA Version: \([0-9.]*\).*/\1/p' | head -1)"
echo "driver CUDA=${DRIVER_CUDA:-unknown}"
case "${DRIVER_CUDA}" in
  1[3-9].*|[2-9][0-9].*) echo "driver CUDA ${DRIVER_CUDA} OK for cu130" ;;
  *) echo "FATAL: released vLLM 0.23.0 is cu13-only; node reports '${DRIVER_CUDA}'." >&2; exit 2 ;;
esac

[[ -x "${UV_PROJECT_ENVIRONMENT}/bin/python" ]] || {
  echo "FATAL: Qwen cu130 venv missing at ${UV_PROJECT_ENVIRONMENT}" >&2
  echo "  UV_PROJECT_ENVIRONMENT=${UV_PROJECT_ENVIRONMENT} uv sync --inexact --project profiles/qwen-cu130" >&2
  exit 2
}
"${UV_PROJECT_ENVIRONMENT}/bin/python" -c "import torch,vllm,trl;print('torch',torch.__version__,'vllm',vllm.__version__,'trl',trl.__version__)" || exit 2

if [[ ! -x "${JAVA_HOME}/bin/java" ]] || ! command -v mvn >/dev/null 2>&1; then
  echo "FATAL: the JMH reward needs java + mvn on PATH (JAVA_HOME=${JAVA_HOME})." >&2
  exit 2
fi

# Same guards as Gemma: a prompt-set project with no classpath, an unresolvable classpath entry,
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
VLLM_LOG="${LOG_DIR}/vllm-qwen-lrz-${SLURM_JOB_ID:-local}.log"
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

echo "=== starting vLLM (TRL mode) on GPU 0 -> ${VLLM_LOG} ==="
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
