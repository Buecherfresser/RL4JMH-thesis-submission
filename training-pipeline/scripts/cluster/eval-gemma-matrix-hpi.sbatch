#!/usr/bin/env bash
# Phase 1 of the GRPO-Gemma evaluation: serve base + LoRA on an aisc H100 and drive
# JMH-Bench's own `tools/run_eval.sh` over the full matrix
#
#     tracks {project, synthetic} x temp {0, 1} x thinking {on, off}  =  8 configs
#
# This replaces the hand-rolled single-track generate loop that used to live in
# eval-gemma-bundle-hpi.sbatch. run_eval.sh is the script every previous eval in this project
# used (base, sft, rft, simpleRL), so using it is what makes the numbers comparable -- same
# slugs, same MAX_TOKENS=8192, same compile-check on the project track, same report layout.
#
# Benchmarking is deliberately NOT done here (SKIP_BENCH=1): JMH measures wall-clock time and a
# shared GPU node is the worst possible place to do that. Phase 2 runs the bundles on a quiet
# CPU box -- see the "Next" banner printed at the end.
#
#   sbatch scripts/cluster/eval-gemma-matrix-hpi.sbatch
#   sbatch --export=ALL,TRACKS=synthetic,MAX_CLASSES=5 scripts/cluster/eval-gemma-matrix-hpi.sbatch  # smoke
#SBATCH --job-name=jmh-eval-matrix
#SBATCH --partition=aisc-batch
#SBATCH --account=aisc
#SBATCH --qos=aisc
#SBATCH --nodes=1
#SBATCH --gres=gpu:h100:1
# gx13v1 has a GPU that cannot create CUDA contexts (training-run-failures.md 7).
#SBATCH --exclude=gx13v1
#SBATCH --cpus-per-task=16
#SBATCH --mem=128G
#SBATCH --time=12:00:00
#SBATCH --output=/sc/scratch/zongxiong.chen/jonas/logs/eval-matrix_%j.out
#SBATCH --error=/sc/scratch/zongxiong.chen/jonas/logs/eval-matrix_%j.err

set -uo pipefail
export PYTHONUNBUFFERED=1

JMH_ROOT="${JMH_ROOT:-/sc/scratch/zongxiong.chen/jonas}"
BENCH="${BENCH:-${JMH_ROOT}/JMH-Bench}"
ADAPTER="${ADAPTER:-${JMH_ROOT}/adapters/gemma4-e2b-grpo-mutation70}"
# cu128 venv: inference only, and it runs on the nine CUDA-12.8 aisc nodes instead of just
# gx13v1 (the one node with the faulty GPU).
SERVE_VENV="${SERVE_VENV:-${JMH_ROOT}/.venv-qwen-cu128}"
PORT="${PORT:-8077}"
MODEL_NAME="${MODEL_NAME:-gemma4-e2b-grpo-mutation70}"
BASE_MODEL="${BASE_MODEL:-google/gemma-4-E2B-it}"
MAX_MODEL_LEN="${MAX_MODEL_LEN:-65536}"

# run_eval.sh matrix knobs. The matrix shape matches every previous eval in this project, but
# MAX_TOKENS is deliberately raised from the usual 8192: on a whole H100 the KV cache is free,
# and truncated completions were a known failure mode (a benchmark cut off mid-class cannot
# compile, which reads as a model failure when it is really a budget failure). This trades a
# little comparability against base/sft/rft for a cleaner read on generation quality.
export TRACKS="${TRACKS:-project synthetic}"
export TEMPS="${TEMPS:-0 1}"
export THINKING="${THINKING:-true false}"
export MAX_TOKENS="${MAX_TOKENS:-16384}"
export PARALLEL="${PARALLEL:-16}"
export COMPILE_CHECK="${COMPILE_CHECK:-true}"
export SKIP_BENCH=1
RUN_STAMP="${RUN_STAMP:-$(date -u +%Y-%m-%d_%H-%M-%S)}"
export RUN_DIR="${RUN_DIR:-${BENCH}/reports/gemma4-e2b/grpo-mutation70/${RUN_STAMP}}"
MAX_CLASSES="${MAX_CLASSES:-}"

export HF_HOME="${JMH_ROOT}/.cache/huggingface"
export XDG_CACHE_HOME="${JMH_ROOT}/.cache"
export VLLM_CACHE_ROOT="${JMH_ROOT}/.cache/vllm"
export TMPDIR_OVERRIDE="${JMH_ROOT}/tmp"     # run_eval.sh honours this; keeps Maven off /tmp
export TMPDIR="${TMPDIR_OVERRIDE}"
mkdir -p "${TMPDIR}" "${JMH_ROOT}/logs"

echo "=== node=$(hostname) job=${SLURM_JOB_ID} ==="
nvidia-smi --query-gpu=name,driver_version,memory.total --format=csv,noheader
DRIVER_CUDA="$(nvidia-smi 2>/dev/null | sed -n 's/.*CUDA Version: \([0-9.]*\).*/\1/p' | head -1)"
echo "driver CUDA=${DRIVER_CUDA}"
case "${DRIVER_CUDA}" in
  12.8|12.9|1[3-9].*|[2-9][0-9].*) echo "driver CUDA ${DRIVER_CUDA} OK for cu128 inference" ;;
  *) echo "FATAL: need driver CUDA >= 12.8, node reports '${DRIVER_CUDA}'." >&2; exit 2 ;;
esac
[[ -d "${ADAPTER}" ]]                    || { echo "FATAL: adapter missing at ${ADAPTER}" >&2; exit 2; }
[[ -x "${SERVE_VENV}/bin/python" ]]      || { echo "FATAL: serve venv missing" >&2; exit 2; }
[[ -x "${BENCH}/.venv/bin/jmhbench" ]]   || { echo "FATAL: jmhbench missing" >&2; exit 2; }
[[ -f "${BENCH}/tools/run_eval.sh" ]]    || { echo "FATAL: run_eval.sh missing" >&2; exit 2; }
[[ -d "${BENCH}/dataset/projects" ]]     || { echo "FATAL: project dataset missing" >&2; exit 2; }

# FlashInfer JIT-compiles its sampler at engine start; it needs `ninja` (venv bin/) and a
# complete CUDA toolkit. The site toolkit has nvcc but no curand.h, so the JIT cannot succeed
# here -- and it is only a fast path. Turn it off. (training-run-failures.md 8)
export PATH="${SERVE_VENV}/bin:${PATH}"
export VLLM_USE_FLASHINFER_SAMPLER=0
export FLASHINFER_WORKSPACE_BASE="${VLLM_CACHE_ROOT}/flashinfer"

VLLM_LOG="${JMH_ROOT}/logs/eval-matrix-vllm-${SLURM_JOB_ID}.log"
ENV_BAK="${BENCH}/.env.bak.${SLURM_JOB_ID}"

_descendants() {
  local pid="$1" kid
  for kid in $(cat /proc/"${pid}"/task/*/children 2>/dev/null); do
    _descendants "${kid}"; printf '%s\n' "${kid}"
  done
}
# Shared account: never pattern-match on "vllm" -- other users run their own on these nodes.
cleanup() {
  # Always put the checkout's .env back, even if the job is cancelled mid-run.
  [[ -f "${ENV_BAK}" ]] && mv -f "${ENV_BAK}" "${BENCH}/.env"
  local pid="${VLLM_PID:-}" kids
  [[ -z "${pid}" ]] && return 0
  kill -0 "${pid}" 2>/dev/null || return 0
  kids="$(_descendants "${pid}")"
  kill -TERM ${kids} "${pid}" 2>/dev/null || true
  for _ in $(seq 1 15); do kill -0 "${pid}" 2>/dev/null || return 0; sleep 2; done
  kids="$(_descendants "${pid}")"
  kill -KILL ${kids} "${pid}" 2>/dev/null || true
  return 0
}
trap cleanup EXIT

echo "=== serving ${BASE_MODEL} + LoRA(${MODEL_NAME}) on :${PORT} (len=${MAX_MODEL_LEN}) ==="
# No --enforce-eager: the matrix is ~500-1000 generations, so CUDA graphs are worth the
# extra ~60 s of startup several times over.
"${SERVE_VENV}/bin/vllm" serve "${BASE_MODEL}" \
  --served-model-name "${BASE_MODEL}" \
  --enable-lora --lora-modules "${MODEL_NAME}=${ADAPTER}" \
  --max-lora-rank 16 \
  --max-model-len "${MAX_MODEL_LEN}" --gpu-memory-utilization 0.85 \
  --port "${PORT}" >"${VLLM_LOG}" 2>&1 &
VLLM_PID=$!

READY=0
for i in $(seq 1 150); do   # 25 min
  kill -0 "${VLLM_PID}" 2>/dev/null || { echo "FATAL: vLLM exited"; tail -40 "${VLLM_LOG}" >&2; exit 1; }
  if curl -sf "http://127.0.0.1:${PORT}/v1/models" >/dev/null 2>&1; then
    echo "*** vLLM ready after ~$((i*10))s ***"; READY=1; break
  fi
  (( i % 6 == 0 )) && { echo "  waiting (${i}/150)"; tail -2 "${VLLM_LOG}" | cut -c1-150; }
  sleep 10
done
[[ "${READY}" == "1" ]] || { echo "FATAL: vLLM not ready in 25 min" >&2; tail -60 "${VLLM_LOG}" >&2; exit 1; }

# run_eval.sh reads the endpoint from $REPO/.env and re-exports it with `set -a`, so exported
# shell vars would be clobbered -- the file itself has to point at our server. Back it up and
# restore in the EXIT trap so the checkout is left exactly as found.
cp -p "${BENCH}/.env" "${ENV_BAK}"
{
  grep -vE '^(HARNESS|OPENAI_BASE_URL|OPENAI_API_KEY|OPENAI_MODEL|REMOTE)=' "${ENV_BAK}"
  echo "HARNESS=openai-zero-shot"
  echo "OPENAI_BASE_URL=http://127.0.0.1:${PORT}/v1"
  echo "OPENAI_API_KEY=dummy"
  echo "OPENAI_MODEL=${MODEL_NAME}"
  echo "REMOTE=local"
} > "${BENCH}/.env.tmp.${SLURM_JOB_ID}"
mv -f "${BENCH}/.env.tmp.${SLURM_JOB_ID}" "${BENCH}/.env"

[[ -n "${MAX_CLASSES}" ]] && export MAX_CLASSES

echo "=== run_eval.sh: tracks='${TRACKS}' temps='${TEMPS}' thinking='${THINKING}' max_tokens=${MAX_TOKENS} ==="
echo "    RUN_DIR=${RUN_DIR}"
cd "${BENCH}" || exit 1
bash tools/run_eval.sh
rc=$?
echo "run_eval.sh exited ${rc}"

echo "=== bundles ==="
for d in "${RUN_DIR}"/bundles/*; do
  [[ -d "${d}" ]] || continue
  printf '%-26s generation.json=%s\n' "$(basename "${d}")" \
    "$([[ -f ${d}/generation.json ]] && echo yes || echo NO)"
done
echo
echo "Next -- phase 2 on a quiet CPU box (JMH timings must not share a GPU node):"
echo "  scripts/cluster/bench-gemma-matrix.sh ${RUN_DIR}"
exit "${rc}"
