#!/usr/bin/env bash
# Capture the Python stack of the Qwen TRL-serve stall. Short job: the freeze is deterministic
# and happens ~3.5 min in, so 45 min of walltime is plenty and backfills far better than 8 h.
#
# The stall (jobs 2384888, 2385467) is always the same line, then silence for hours:
#   INFO [v1/engine/core.py:116] Initializing a V1 LLM engine (v0.23.1rc1.dev1187+gdc9f845dd)
# GPU stays at 4 MiB, so it never reaches model loading. OpenAI mode does the same work in 104 s.
#
# WHY THE PREVIOUS ATTEMPT CAUGHT NOTHING: job 2385467 ran
#   py-spy record --subprocesses -- bash start-vllm.sh
# py-spy needs a *Python* process to sample; its direct target was bash, so it found no
# interpreter and wrote no samples. Here py-spy is invoked as `dump --pid <python pid>` from
# THIS script, which is an ancestor of the serve process -- that satisfies ptrace_scope=1.
# (`srun --overlap ... py-spy dump` failed with Permission Denied precisely because a separate
# job step is a sibling, not an ancestor.)
#
#   sbatch scripts/cluster/diag-qwen-stack.sbatch
#SBATCH --job-name=jmh-qwen-stack
#SBATCH --partition=aisc-batch
#SBATCH --account=aisc
#SBATCH --qos=aisc
#SBATCH --nodes=1
#SBATCH --gres=gpu:h100:1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64G
#SBATCH --time=45:00
#SBATCH --output=/sc/scratch/zongxiong.chen/jonas/logs/qwen-stack_%j.out
#SBATCH --error=/sc/scratch/zongxiong.chen/jonas/logs/qwen-stack_%j.err

set -uo pipefail
export PYTHONUNBUFFERED=1

JMH_ROOT="${JMH_ROOT:-/sc/scratch/zongxiong.chen/jonas}"
REPO="${JMH_ROOT}/JMH_Training_Pipeline"
PYSPY="${JMH_ROOT}/tools/pyspy/bin/py-spy"
DIAG="${JMH_ROOT}/logs/qwen-stack-${SLURM_JOB_ID}"
mkdir -p "${DIAG}"

cd "${REPO}" || exit 1
# shellcheck source=/dev/null
source "${REPO}/scripts/cluster/env.hpi.sh"

export CUDA_HOME="${CUDA_HOME:-/sc/projects/sci-aisc/cuda-12.8}"
export PATH="${CUDA_HOME}/bin:${PATH}"
export VLLM_MAIN_CUDA_VERSION=12.8
export JMHGEN_MODEL_PROFILE=qwen
export JMHGEN_VOLUME="${JMH_ROOT}"
export REPO_DIR="${REPO}"
export VLLM_SERVE_MODE=trl
export VLLM_VENV="${JMH_ROOT}/.venv-qwen-cu128"
unset UV_PROJECT_ENVIRONMENT

# The configuration that gets furthest: in-process engine + triton GDN + DEBUG logging.
export VLLM_ENABLE_V1_MULTIPROCESSING=0
export VLLM_LOGGING_LEVEL=DEBUG
export JMHGEN_GDN_PREFILL_BACKEND=triton
export VLLM_MODEL=Qwen/Qwen3.5-4B
export VLLM_PORT=8000
export VLLM_MAX_MODEL_LEN=32768
export VLLM_GPU_MEMORY_UTILIZATION=0.90
export CUDA_VISIBLE_DEVICES=0

if [[ -z "${HF_TOKEN:-}" && -f "${HOME}/.cache/huggingface/token" ]]; then
  HF_TOKEN="$(tr -d '[:space:]' < "${HOME}/.cache/huggingface/token")"
  export HF_TOKEN
fi
[[ -z "${HF_TOKEN:-}" ]] && { echo "FATAL: no HF_TOKEN" >&2; exit 1; }

echo "=== node=$(hostname) job=${SLURM_JOB_ID} diag=${DIAG} ==="
nvidia-smi --query-gpu=index,name,driver_version --format=csv,noheader
"${PYSPY}" --version || { echo "FATAL: py-spy missing at ${PYSPY}" >&2; exit 2; }

VLLM_LOG="${DIAG}/vllm.log"
STACKS="${DIAG}/stacks.txt"
READY_URL="http://127.0.0.1:${VLLM_PORT}/health/"

_descendants() {
  local pid="$1" kid
  for kid in $(cat /proc/"${pid}"/task/*/children 2>/dev/null); do
    _descendants "${kid}"
    printf '%s\n' "${kid}"
  done
}
# Only our own tree: this account is shared and other users' vLLM runs under the same uid.
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
trap 'stop_tree "${VLLM_PID:-}"' EXIT

echo "=== launching TRL serve ==="
bash "${REPO}/scripts/remote/start-vllm.sh" >"${VLLM_LOG}" 2>&1 &
VLLM_PID=$!

dump_stacks() {
  local tag="$1" pid found=0
  {
    echo "###################### ${tag} ($(date -u +%H:%M:%SZ)) ######################"
    for pid in $(_descendants "${VLLM_PID}") "${VLLM_PID}"; do
      # py-spy only makes sense on a Python interpreter -- that was the bug last time.
      grep -qa "python" "/proc/${pid}/cmdline" 2>/dev/null || continue
      found=1
      echo "--- pid=${pid} cmd=$(tr -d '\0' < "/proc/${pid}/cmdline" 2>/dev/null | cut -c1-110)"
      echo "    state=$(awk '{print $3}' "/proc/${pid}/stat" 2>/dev/null) wchan=$(cat "/proc/${pid}/wchan" 2>/dev/null)"
      timeout 60 "${PYSPY}" dump --pid "${pid}" --nonblocking 2>&1 | head -60
    done
    [[ "${found}" == "0" ]] && echo "(no python processes found under ${VLLM_PID})"
  } >>"${STACKS}" 2>&1
}

READY=0
for i in $(seq 1 120); do            # 20 min
  if ! kill -0 "${VLLM_PID}" 2>/dev/null; then
    echo "serve exited early after ~$((i * 10))s"; break
  fi
  if curl -sf "${READY_URL}" >/dev/null 2>&1; then
    echo "*** READY after ~$((i * 10))s -- the stall is gone ***"; READY=1; break
  fi
  # First dump at 3 min (just past the known freeze point), then every 2 min.
  if (( i == 18 || (i > 18 && (i - 18) % 12 == 0) )); then
    echo "--- t=$((i * 10))s: log=$(wc -l < "${VLLM_LOG}") lines; dumping stacks"
    dump_stacks "t=$((i * 10))s"
  fi
  sleep 10
done

echo "=== vLLM log tail ==="
grep -vE "UserWarning|is_vllm_available" "${VLLM_LOG}" | tail -12 | cut -c1-190

if [[ "${READY}" != "1" && -s "${STACKS}" ]]; then
  echo
  echo "############ THE BLOCKING FRAME (last dump, deepest frames) ############"
  awk '/^######/{buf=""} {buf=buf $0 "\n"} END{print buf}' "${STACKS}" | tail -45
fi
echo "=== DONE; full stacks in ${STACKS} ==="
ls -la "${DIAG}"
