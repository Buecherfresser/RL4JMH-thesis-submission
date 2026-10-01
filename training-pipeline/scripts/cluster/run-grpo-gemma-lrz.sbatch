#!/usr/bin/env bash
# Gemma 4 E2B — coverage-guided 70%-mutation GRPO on LRZ AI Systems (1x H100 94GB).
#
# Single GPU with vLLM in colocate mode. That is not just a memory decision: on
# lrz-hgx-h100-94x4 a 1-GPU request waits ~5.6 h at the median while a 2-GPU request
# waits ~47 h (5 days of sacct history), so colocate is worth roughly two days of queue.
#
# Submit from the login node:
#   cd /dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh/JMH_Training_Pipeline
#   sbatch scripts/cluster/run-grpo-gemma-lrz.sbatch
#
# Smoke only:
#   sbatch --export=ALL,RUN_FULL=0 scripts/cluster/run-grpo-gemma-lrz.sbatch
#
# NOTE ON THE 2-DAY CAP: the config sets resume_from_checkpoint: auto, so re-submitting this
# job continues from the newest checkpoint-* under output_dir (save_steps: 25) instead of
# restarting. HF Trainer restores optimiser state, LR schedule and step count. At ~93 s/step,
# 744 steps is ~19 h and fits the wall; the resume exists for crashes (job 5721357 OOM'd at
# step 108/744) and for chaining if a future corpus does overrun 48 h.
#
# Monitor:
#   squeue -u "$USER"
#   tail -f .../jmh/logs/grpo-gemma-lrz_<jobid>.out
#   tail -f .../jmh/logs/grpo-gemma-4-E2B-it/run-grpo.status
#SBATCH --job-name=jmh-grpo-gemma
#SBATCH --partition=lrz-hgx-h100-94x4
#SBATCH --qos=gpu
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --gres=gpu:1
# The reward compiles and JMH-runs every rollout, so this job is genuinely CPU-hungry
# alongside the GPU. 16 of the node's 96 threads is proportionate to 1 of its 4 GPUs.
#SBATCH --cpus-per-task=16
#SBATCH --mem=120G
# QOS gpu caps walltime at 2 days.
#SBATCH --time=2-00:00:00
#SBATCH --output=/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh/logs/grpo-gemma-lrz_%j.out
#SBATCH --error=/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh/logs/grpo-gemma-lrz_%j.err

set -uo pipefail

# Slurm buffers stdout to a file; re-exec once with line buffering so tail -f is useful.
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
export GRPO_CONFIG="${GRPO_CONFIG:-configs/grpo/mutation70-gemma.yaml}"
# The prompt set is pre-filtered to mutation-scorable projects, so 'full' is correct:
# it means "do not re-narrow to the original six libraries".
export GRPO_CORPUS="${GRPO_CORPUS:-full}"
export SMOKE_FIRST="${SMOKE_FIRST:-1}"
export SMOKE_STEPS="${SMOKE_STEPS:-2}"
export RUN_FULL="${RUN_FULL:-1}"
# Gemma is cu130; never let a Qwen cu128 venv leak in from the submitting shell.
unset CUDA_HOME VLLM_MAIN_CUDA_VERSION
export UV_PROJECT_ENVIRONMENT="${UV_PROJECT_ENVIRONMENT:-${JMH_ROOT}/.venv-gemma-cu130}"

if [[ "${UV_PROJECT_ENVIRONMENT}" == *qwen* ]]; then
  echo "FATAL: refusing a Qwen venv for the Gemma run: ${UV_PROJECT_ENVIRONMENT}" >&2
  exit 2
fi

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
  echo "FATAL: HF_TOKEN not set; google/gemma-4-E2B-it is a gated repo." >&2
  echo "  printf 'HF_TOKEN=hf_...\\n' > ${JMH_ROOT}/secrets.env && chmod 600 ${JMH_ROOT}/secrets.env" >&2
  exit 1
fi

echo "=== Gemma 4 E2B GRPO on LRZ AI Systems ==="
echo "node=$(hostname) job=${SLURM_JOB_ID:-local} config=${GRPO_CONFIG} corpus=${GRPO_CORPUS}"
echo "venv=${UV_PROJECT_ENVIRONMENT}"
echo "TMPDIR=${TMPDIR}"
nvidia-smi --query-gpu=index,name,driver_version,memory.total,compute_cap --format=csv,noheader

# The gemma profile pins torch 2.11.0+cu130, which refuses to initialise on a CUDA-12 driver.
DRIVER_CUDA="$(nvidia-smi 2>/dev/null | sed -n 's/.*CUDA Version: \([0-9.]*\).*/\1/p' | head -1)"
echo "driver CUDA=${DRIVER_CUDA:-unknown}"
case "${DRIVER_CUDA}" in
  1[3-9].*|[2-9][0-9].*) echo "driver CUDA ${DRIVER_CUDA} OK for cu130" ;;
  *)
    echo "FATAL: gemma-cu130 needs driver CUDA >= 13.0, node reports '${DRIVER_CUDA}'." >&2
    exit 2
    ;;
esac

if [[ ! -x "${JAVA_HOME}/bin/java" ]] || ! command -v mvn >/dev/null 2>&1; then
  echo "FATAL: the JMH reward needs java + mvn on PATH (JAVA_HOME=${JAVA_HOME})." >&2
  echo "  run scripts/cluster/install-jdk-maven.sh on the login node first." >&2
  exit 2
fi
java -version 2>&1 | head -1

if [[ ! -x "${UV_PROJECT_ENVIRONMENT}/bin/python" ]]; then
  echo "FATAL: Gemma venv missing at ${UV_PROJECT_ENVIRONMENT}" >&2
  echo "  UV_PROJECT_ENVIRONMENT=${UV_PROJECT_ENVIRONMENT} uv sync --inexact --project profiles/gemma-cu130" >&2
  exit 2
fi

# Gate on the preconditions that silently produced an all-zero reward on hpi2 (job 2343896):
# a prompt-set project with no classpath scores 0 on every rollout, so GRPO sees no gradient
# and a 2-day allocation is wasted. --reward-smoke additionally compiles, JMH-runs and
# mutation-scores one known-good benchmark on THIS node before the policy is ever loaded.
echo "=== preflight: classpath / mutant-registry / toolchain / reward guards ==="
if ! "${UV_PROJECT_ENVIRONMENT}/bin/python" scripts/preflight_grpo.py \
      "${GRPO_CONFIG}" --corpus "${GRPO_CORPUS}" --reward-smoke; then
  echo "FATAL: preflight failed — not spending GPU time on a run that cannot learn." >&2
  exit 3
fi

echo "=== handing off to run-grpo.sh (smoke -> full) ==="
exec "${REPO}/scripts/remote/run-grpo.sh"
