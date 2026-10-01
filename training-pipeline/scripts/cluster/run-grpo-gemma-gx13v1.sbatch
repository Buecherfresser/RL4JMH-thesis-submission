#!/usr/bin/env bash
# Gemma 4 E2B GRPO on HPI — CUDA-13 node gx13v1 only (4x H100).
#
# Other aisc-batch H100s ship driver CUDA 12.8 and reject torch+cu130
# ("found version 12080"). gx13v1 is the CUDA 13+ node for this stack.
#
# Uses the Gemma profile + repo .venv (cu130). Does NOT touch the Qwen
# cu128 venv or CUDA 12.8 toolkit.
#
# Submit from the login node (rx01 / hpi2):
#   cd /sc/scratch/zongxiong.chen/jonas/JMH_Training_Pipeline
#   sbatch scripts/cluster/run-grpo-gemma-gx13v1.sbatch
#   # or:
#   sbatch /sc/scratch/zongxiong.chen/jonas/scripts/run-grpo-gemma-gx13v1.sbatch
#
# Smoke only:
#   RUN_FULL=0 sbatch scripts/cluster/run-grpo-gemma-gx13v1.sbatch
#
# Monitor:
#   squeue -u "$USER" -w gx13v1
#   tail -f /sc/scratch/zongxiong.chen/jonas/logs/grpo-gemma_%j.out
#   tail -f /sc/scratch/zongxiong.chen/jonas/logs/grpo-gemma-4-E2B-it/run-grpo.status
#SBATCH --job-name=jmh-grpo-gemma
#SBATCH --partition=aisc-batch
#SBATCH --account=aisc
#SBATCH --qos=aisc
#SBATCH --nodes=1
#SBATCH --nodelist=gx13v1
#SBATCH --gres=gpu:h100:1
#SBATCH --cpus-per-task=16
#SBATCH --mem=128G
#SBATCH --time=2-00:00:00
#SBATCH --output=/sc/scratch/zongxiong.chen/jonas/logs/grpo-gemma_%j.out
#SBATCH --error=/sc/scratch/zongxiong.chen/jonas/logs/grpo-gemma_%j.err

set -euo pipefail

# Slurm redirects stdout to a file (fully buffered); re-exec once with line buffering.
if [[ -z "${_JMH_GEMMA_REEXEC:-}" ]] && command -v stdbuf >/dev/null 2>&1; then
  export _JMH_GEMMA_REEXEC=1
  exec stdbuf -oL -eL bash "$0" "$@"
fi
export PYTHONUNBUFFERED=1
export UV_LINK_MODE=copy

JMH_ROOT="${JMH_ROOT:-/sc/scratch/zongxiong.chen/jonas}"
REPO="${JMH_ROOT}/JMH_Training_Pipeline"
LOG_DIR="${JMH_ROOT}/logs"
# Verified Gemma 4 stack (torch 2.11.0+cu130 / vllm 0.23.0 / trl 1.7.1).
# Do NOT point this at .venv-qwen-cu128.
GEMMA_VENV="${UV_PROJECT_ENVIRONMENT:-${REPO}/.venv}"

cd "${REPO}"
# shellcheck source=/dev/null
source "${REPO}/scripts/cluster/env.hpi.sh"

# Gemma / CUDA-13 profile only. Clear any Qwen leftovers from the shell.
export JMHGEN_MODEL_PROFILE=gemma
export JMHGEN_VOLUME="${JMH_ROOT}"
export JMHGEN_SCRATCH="${JMH_ROOT}"
export REPO_DIR="${REPO}"
# Default to the 70%-mutation config on the mutation-scorable corpus (744 prompts / 40
# projects) rather than jmh-rl.yaml. jmh-rl.yaml declares corpus: original but lists only 1 of
# those 6 libraries in project_classpaths, so 5/6 of its rollouts cannot compile -- that is the
# bug behind the 2026-07-17 "reward diversity validation failed" run (see
# training-run-failures.md §1). Leaving it as the default here is a trap.
export GRPO_CONFIG="${GRPO_CONFIG:-configs/grpo/mutation70-gemma.yaml}"
# 'full' = do not re-narrow to the original six libraries; the prompt file is already
# pre-filtered to projects whose mutants can actually be scored.
export GRPO_CORPUS="${GRPO_CORPUS:-full}"
export SMOKE_FIRST="${SMOKE_FIRST:-1}"
export SMOKE_STEPS="${SMOKE_STEPS:-2}"
export RUN_FULL="${RUN_FULL:-1}"
export UV_PROJECT_ENVIRONMENT="${GEMMA_VENV}"
unset CUDA_HOME VLLM_MAIN_CUDA_VERSION || true

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

mkdir -p "${LOG_DIR}"

echo "=== Gemma 4 E2B GRPO on gx13v1 ==="
echo "node=$(hostname) job=${SLURM_JOB_ID:-local} config=${GRPO_CONFIG}"
echo "UV_PROJECT_ENVIRONMENT=${UV_PROJECT_ENVIRONMENT}"
echo "JMHGEN_MODEL_PROFILE=${JMHGEN_MODEL_PROFILE}"

if [[ "$(hostname -s)" != "gx13v1" ]]; then
  echo "FATAL: expected gx13v1 (CUDA 13+), got $(hostname -s)." >&2
  exit 2
fi

if [[ "${UV_PROJECT_ENVIRONMENT}" == *qwen* ]]; then
  echo "FATAL: refusing Qwen venv for Gemma run: ${UV_PROJECT_ENVIRONMENT}" >&2
  exit 2
fi

if [[ ! -x "${UV_PROJECT_ENVIRONMENT}/bin/python" ]]; then
  echo "FATAL: Gemma venv missing at ${UV_PROJECT_ENVIRONMENT}" >&2
  echo "Create it with:" >&2
  echo "  UV_PROJECT_ENVIRONMENT=${REPO}/.venv uv sync --project profiles/gemma-cu130 --locked" >&2
  exit 2
fi

nvidia-smi --query-gpu=index,name,driver_version,memory.total --format=csv,noheader

# Fail fast if this node is still on a CUDA-12.8 driver (header: "CUDA Version: XX.Y").
DRIVER_CUDA="$(nvidia-smi 2>/dev/null | sed -n 's/.*CUDA Version: \([0-9.]*\).*/\1/p' | head -1)"
echo "nvidia-smi CUDA Version=${DRIVER_CUDA:-unknown}"
python3 - <<'PY' "${DRIVER_CUDA}"
import sys
raw = (sys.argv[1] or "").strip()
if not raw:
    raise SystemExit("FATAL: could not read CUDA Version from nvidia-smi")
try:
    major, minor = (int(x) for x in raw.split(".")[:2])
except ValueError as exc:
    raise SystemExit(f"FATAL: could not parse driver CUDA version {raw!r}") from exc
if (major, minor) < (13, 0):
    raise SystemExit(
        f"FATAL: gx13v1 driver reports CUDA {raw}; Gemma needs CUDA >= 13.0 "
        "(torch 2.11.0+cu130)."
    )
print(f"driver CUDA {raw} OK for cu130")
PY

# Some of gx13v1's GPUs currently refuse to create a CUDA context: nvidia-smi is happy, the
# device is Default mode, non-MIG, 0 MiB used and ECC-clean, torch.cuda.is_available() is True,
# and then the first allocation dies with
#   CUDA error: CUDA-capable device(s) is/are busy or unavailable  (cudaErrorDevicesUnavailable)
# Jobs 2387025 and 2387090 both lost that coin flip. It is not specific to us -- another user's
# array job on the same node alternates FAILED(13s)/COMPLETED(22s) depending on which GPU it
# lands on. Existing contexts keep working, so long-running jobs there look healthy.
#
# Until the node is fixed, probe the context here and requeue onto a fresh allocation instead of
# burning the submission. Bounded so a genuinely broken stack cannot loop forever.
MAX_CUDA_REQUEUES="${MAX_CUDA_REQUEUES:-6}"
RESTARTS="${SLURM_RESTART_COUNT:-0}"
if ! "${UV_PROJECT_ENVIRONMENT}/bin/python" - <<'PY'
import sys

import torch

try:
    x = torch.randn(64, 64, device="cuda", dtype=torch.bfloat16)
    print(f"cuda context OK (matmul {float((x @ x).sum().item()):.1f})")
except Exception as exc:  # noqa: BLE001 - any CUDA init failure means this GPU is unusable
    print(f"CUDA_CONTEXT_UNAVAILABLE: {type(exc).__name__}: {exc}", file=sys.stderr)
    sys.exit(75)
PY
then
  if (( RESTARTS < MAX_CUDA_REQUEUES )) && command -v scontrol >/dev/null 2>&1; then
    echo "GPU on $(hostname) cannot create a CUDA context; requeueing" \
         "(attempt $((RESTARTS + 1))/${MAX_CUDA_REQUEUES})" >&2
    scontrol requeue "${SLURM_JOB_ID}" && exit 0
  fi
  echo "FATAL: no usable GPU after ${RESTARTS} requeue(s) — gx13v1 likely needs admin attention." >&2
  exit 75
fi

echo "=== verifying Gemma cu130 stack (not Qwen cu128) ==="
UV_PROJECT_ENVIRONMENT="${UV_PROJECT_ENVIRONMENT}" \
  uv run --project profiles/gemma-cu130 python - <<'PY'
import torch
import transformers
import trl
import vllm

print(f"torch={torch.__version__} cuda={torch.version.cuda}")
print(f"transformers={transformers.__version__}")
print(f"trl={trl.__version__}")
print(f"vllm={vllm.__version__}")

assert "cu130" in torch.__version__, torch.__version__
assert torch.version.cuda and torch.version.cuda.startswith("13"), torch.version.cuda
assert not torch.__version__.endswith("+cu128"), torch.__version__
assert vllm.__version__.startswith("0.23"), vllm.__version__
assert trl.__version__.startswith("1.7"), trl.__version__
assert torch.cuda.is_available(), "CUDA unavailable"
print("device", torch.cuda.get_device_name(0), "bf16", torch.cuda.is_bf16_supported())
# Smoke a tiny matmul so a driver mismatch fails before the long GRPO sync.
x = torch.randn(64, 64, device="cuda", dtype=torch.bfloat16)
y = x @ x
print("matmul_ok", float(y.sum().item()))
PY

# Gate on the preconditions that silently produce an all-zero reward: a prompt-set project with
# no classpath, an unresolvable classpath entry, a SUT jar newer than javac can read, or a
# weighted reward term that is structurally dead. --reward-smoke additionally compiles,
# JMH-runs and mutation-scores one known-good benchmark on THIS node before the policy loads.
echo "=== preflight: classpath / mutant-registry / toolchain / reward guards ==="
if ! "${UV_PROJECT_ENVIRONMENT}/bin/python" scripts/preflight_grpo.py \
      "${GRPO_CONFIG}" --corpus "${GRPO_CORPUS}" --reward-smoke; then
  echo "FATAL: preflight failed — not spending GPU time on a run that cannot learn." >&2
  exit 3
fi

echo "=== starting colocated GRPO (${GRPO_CONFIG} / gemma-cu130) ==="
exec "${REPO}/scripts/remote/run-grpo.sh"
