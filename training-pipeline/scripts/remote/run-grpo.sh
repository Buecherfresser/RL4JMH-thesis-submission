#!/usr/bin/env bash
# Detached single-GPU GRPO orchestrator. vLLM runs in TRL colocate mode inside the trainer;
# this script runs a smoke validation and then the full run. Launch it in tmux so the work
# survives laptop / SSH disconnects.
#
# Usage (on the pod):
#   export HF_TOKEN=hf_...
#   cd /workspace/jmhgen-rft
#   tmux new -d -s grpo './scripts/remote/run-grpo.sh'   # fully detached
#   # or run in the foreground: ./scripts/remote/run-grpo.sh
#
# Layout it creates (model-namespaced so Gemma + Qwen can run in parallel):
#   $VOLUME/logs/grpo-<model>/train.smoke.log
#   $VOLUME/logs/grpo-<model>/train.log
#   $VOLUME/logs/grpo-<model>/run-grpo.status
#   $VOLUME/logs/grpo-<model>/smoke.yaml
#
# Env toggles:
#   SMOKE_FIRST=1 (default)  run a 4-prompt smoke, then the full run if it passes
#   SMOKE_STEPS=2 (default)  optimiser steps in the smoke run
#   SMOKE_FIRST=0            skip the smoke, go straight to the full run
#   RUN_FULL=0               stop after the smoke run
#   GRPO_CONFIG=<path>       trainer config (default configs/grpo/jmh-rl.yaml)
#   GRPO_LOG_TAG=<slug>      override log folder name (default: base_model basename)
set -uo pipefail

VOLUME_ROOT="${JMHGEN_VOLUME:-/workspace}"
SCRATCH_ROOT="${JMHGEN_SCRATCH:-/root/jmhgen-scratch}"
REPO_DIR="${REPO_DIR:-/workspace/jmhgen-rft}"
CONFIG="${GRPO_CONFIG:-configs/grpo/jmh-rl.yaml}"
PROFILE="${JMHGEN_MODEL_PROFILE:-gemma}"
# original = six curated libraries; full = expanded RL corpus (see jmh-train-grpo --corpus).
CORPUS="${GRPO_CORPUS:-original}"
SMOKE_FIRST="${SMOKE_FIRST:-1}"
SMOKE_STEPS="${SMOKE_STEPS:-2}"
RUN_FULL="${RUN_FULL:-1}"
GRPO_CLI_ARGS=(--config "${CONFIG}" --corpus "${CORPUS}")

export PATH="${HOME}/.local/bin:${PATH}"
export HF_HOME="${VOLUME_ROOT}/.cache/huggingface"
case "${PROFILE}" in
  gemma)
    PROFILE_PROJECT="profiles/gemma-cu130"
    PROFILE_SUFFIX="gemma-cu130"
    ;;
  qwen)
    PROFILE_PROJECT="profiles/qwen-cu128"
    PROFILE_SUFFIX="qwen-cu128"
    ;;
  qwen-cu130)
  # Released vLLM 0.23.0 on CUDA 13. Deliberately a SEPARATE profile from `qwen`: that one
  # installs the hand-built cu128 wheel, so pointing it at the cu130 venv would `uv sync`
  # cu128 torch straight into it and break the environment.
    PROFILE_PROJECT="profiles/qwen-cu130"
    PROFILE_SUFFIX="qwen-cu130"
    ;;
  *)
    echo "unsupported JMHGEN_MODEL_PROFILE=${PROFILE}; use gemma, qwen or qwen-cu130" >&2
    exit 2
    ;;
esac

# The model cache and outputs persist on the network volume. Venv/package/JIT caches are
# deliberately local because uv installs and Triton/Inductor compilation are I/O intensive.  They
# are profile-namespaced so CUDA 12.8 and CUDA 13 extensions never overwrite one another.
export JMHGEN_MODEL_PROFILE="${PROFILE}"
export UV_PROJECT_ENVIRONMENT="${UV_PROJECT_ENVIRONMENT:-${SCRATCH_ROOT}/.venv-${PROFILE_SUFFIX}}"
export UV_CACHE_DIR="${UV_CACHE_DIR:-${SCRATCH_ROOT}/.uv-cache}"
export XDG_CACHE_HOME="${XDG_CACHE_HOME:-${SCRATCH_ROOT}/.cache/${PROFILE_SUFFIX}}"
export TRITON_CACHE_DIR="${TRITON_CACHE_DIR:-${SCRATCH_ROOT}/.cache/${PROFILE_SUFFIX}/triton}"
export TORCHINDUCTOR_CACHE_DIR="${TORCHINDUCTOR_CACHE_DIR:-${SCRATCH_ROOT}/.cache/${PROFILE_SUFFIX}/torchinductor}"
export VLLM_CACHE_ROOT="${VLLM_CACHE_ROOT:-${SCRATCH_ROOT}/.cache/${PROFILE_SUFFIX}/vllm}"
export TMPDIR="${TMPDIR:-${SCRATCH_ROOT}/tmp}"

mkdir -p \
  "${UV_CACHE_DIR}" "${TRITON_CACHE_DIR}" \
  "${TORCHINDUCTOR_CACHE_DIR}" "${VLLM_CACHE_ROOT}" "${TMPDIR}"
cd "${REPO_DIR}"

# Prefer config base_model (e.g. gemma-4-E2B-it / Qwen3.5-4B) so concurrent runs don't share logs.
CONFIG_PATH="${CONFIG}"
if [[ ! -f "${CONFIG_PATH}" && -f "${REPO_DIR}/${CONFIG}" ]]; then
  CONFIG_PATH="${REPO_DIR}/${CONFIG}"
fi
if [[ -n "${GRPO_LOG_TAG:-}" ]]; then
  MODEL_SLUG="${GRPO_LOG_TAG}"
elif [[ -f "${CONFIG_PATH}" ]]; then
  MODEL_SLUG="$(
    sed -n 's/^base_model:[[:space:]]*//p' "${CONFIG_PATH}" | head -1 \
      | sed 's/#.*//; s/[[:space:]]*$//; s|.*/||; s/[^A-Za-z0-9._+-]/-/g'
  )"
fi
MODEL_SLUG="${MODEL_SLUG:-${PROFILE}}"
LOG_DIR="${VOLUME_ROOT}/logs/grpo-${MODEL_SLUG}"
STATUS="${LOG_DIR}/run-grpo.status"
SMOKE_LOG="${LOG_DIR}/train.smoke.log"
TRAIN_LOG="${LOG_DIR}/train.log"
SMOKE_CONFIG="${LOG_DIR}/smoke.yaml"
SMOKE_OUTPUT_DIR="outputs/grpo-${MODEL_SLUG}-smoke"
mkdir -p "${LOG_DIR}"

if [[ "${PROFILE}" == "qwen" ]]; then
  export VLLM_MAIN_CUDA_VERSION=12.8
  ARTIFACT_DIR="${VLLM_WHEEL_DIR:-${VOLUME_ROOT}/artifacts/vllm-cu128-h100}"
  HAVE_WHEEL=0
  if [[ -n "${VLLM_WHEEL:-}" && -f "${VLLM_WHEEL}" ]]; then
    HAVE_WHEEL=1
  elif ls "${ARTIFACT_DIR}"/vllm-*.whl >/dev/null 2>&1; then
    HAVE_WHEEL=1
  fi
  if command -v nvcc >/dev/null && nvcc --version | grep -q 'release 12\.8'; then
    export CUDA_HOME="${CUDA_HOME:-$(dirname "$(dirname "$(command -v nvcc)")")}"
  elif [[ "${HAVE_WHEEL}" != "1" ]]; then
    echo "[run-grpo] Qwen needs a prebuilt wheel under ${ARTIFACT_DIR} or CUDA 12.8 nvcc." >&2
    exit 2
  fi
fi
if ! uv sync --inexact --project "${PROFILE_PROJECT}"; then
  echo "[run-grpo] failed to sync ${PROFILE} dependency profile." >&2
  exit 1
fi
if [[ "${PROFILE}" == "qwen" ]]; then
  VENV_PY="${UV_PROJECT_ENVIRONMENT}/bin/python"
  if [[ "${FORCE_VLLM_REBUILD:-0}" == "1" ]] \
    || ! "${VENV_PY}" -c "import vllm" >/dev/null 2>&1; then
    REPO_DIR="${REPO_DIR}" scripts/remote/install-qwen-vllm.sh
  else
    echo "[run-grpo] vLLM already importable — skipping install"
  fi
fi
if ! uv run --no-sync --project "${PROFILE_PROJECT}" python - <<'PY'
import torch
import vllm

print(f"[run-grpo] torch={torch.__version__} cuda={torch.version.cuda} vllm={vllm.__version__}")
PY
then
  echo "[run-grpo] selected ${PROFILE} profile failed to import torch/vLLM." >&2
  exit 1
fi

log() { echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] $*" | tee -a "${STATUS}"; }

log "profile=${PROFILE} model_slug=${MODEL_SLUG} log_dir=${LOG_DIR}"

metrics_dir() {
  uv run --no-sync --project "${PROFILE_PROJECT}" python - "${CONFIG}" <<'PY'
import sys
from pathlib import Path

import yaml

config = yaml.safe_load(Path(sys.argv[1]).read_text()) or {}
print(config.get("diagnostics_dir") or Path(config.get("output_dir", "outputs/grpo")) / "metrics")
PY
}

plot_diagnostics() {
  local dir
  dir="$(metrics_dir)"
  if [[ ! -s "${dir}/step_metrics.csv" ]]; then
    log "GRPO diagnostics unavailable: no step metrics at ${dir}."
    return 0
  fi
  if uv run --no-sync --project "${PROFILE_PROJECT}" --extra viz jmh-plot-grpo-metrics --metrics-dir "${dir}" --out-dir "${dir}"; then
    log "wrote GRPO diagnostic plots to ${dir}."
  else
    log "WARNING: GRPO training succeeded but diagnostic plotting failed; metrics remain at ${dir}."
  fi
}

if [[ -z "${HF_TOKEN:-}" ]]; then
  log "FATAL: HF_TOKEN not set — export it before launching."; exit 1
fi

if [[ "${PROFILE}" == qwen* ]] && ! uv run --no-sync --project "${PROFILE_PROJECT}" python - "${CONFIG}" <<'PY'
import sys
from pathlib import Path

import yaml

config = yaml.safe_load(Path(sys.argv[1]).read_text()) or {}
if config.get("model_family") != "qwen" or config.get("vllm_mode") != "server":
    raise SystemExit("Qwen GRPO requires model_family: qwen and vllm_mode: server.")
PY
then
  log "FATAL: Qwen configuration does not use the isolated server-mode rollout path."
  exit 2
fi

# 1. Optional smoke validation: rollouts + JMH reward + policy update in one process.
if [[ "${SMOKE_FIRST}" == "1" ]]; then
  uv run --no-sync --project "${PROFILE_PROJECT}" python - \
    "${CONFIG}" "${SMOKE_CONFIG}" "${SMOKE_STEPS}" "${SMOKE_OUTPUT_DIR}" <<'PY'
import sys, yaml
src, dst, smoke_steps, smoke_output = sys.argv[1], sys.argv[2], int(sys.argv[3]), sys.argv[4]
cfg = yaml.safe_load(open(src))
cfg.update(
    limit_prompts=4,
    num_iterations=smoke_steps,
    save_steps=smoke_steps,
    logging_steps=1,
    output_dir=smoke_output,
    # Never resume the SMOKE. It inherits the parent config, so with
    # resume_from_checkpoint: auto a rerun would find the previous smoke's
    # checkpoint-2, resume at step 2 of 2, return immediately and log no metrics --
    # and the reward-diversity check below would then fail a perfectly good run.
    resume_from_checkpoint=None,
)
# Preserve corpus from the parent config (default original); CLI --corpus still overrides.
yaml.safe_dump(cfg, open(dst, "w"), sort_keys=False)
PY
  log "running smoke (4 prompts, ${SMOKE_STEPS} steps, corpus=${CORPUS}) -> ${SMOKE_LOG}"
  if HF_TOKEN="${HF_TOKEN}" uv run --no-sync --project "${PROFILE_PROJECT}" jmh-train-grpo \
        --config "${SMOKE_CONFIG}" --corpus "${CORPUS}" 2>&1 \
      | tee "${SMOKE_LOG}"; then
    if uv run --no-sync --project "${PROFILE_PROJECT}" python - "${SMOKE_LOG}" <<'PY'
import re
import sys
from pathlib import Path

text = Path(sys.argv[1]).read_text(encoding="utf-8", errors="replace")
number = r"([-+]?(?:\d+(?:\.\d*)?|\.\d+)(?:[eE][-+]?\d+)?)"
def metrics(name):
    pattern = rf"['\"]{re.escape(name)}['\"]\s*:\s*['\"]?" + number
    return [float(value) for value in re.findall(pattern, text)]

rewards = metrics("reward")
stds = metrics("reward_std")
grad_norms = metrics("grad_norm")
clipped_ratios = metrics("completions/clipped_ratio")
if not rewards or max(rewards) <= 0:
    raise SystemExit("smoke produced no positive reward metric")
if not stds or max(stds) <= 0:
    raise SystemExit("smoke produced no positive reward_std metric")
if not grad_norms or max(grad_norms) <= 0:
    raise SystemExit("smoke produced no non-zero gradient")
if not clipped_ratios or min(clipped_ratios) >= 1:
    raise SystemExit("every smoke completion was truncated and masked")
print(
    f"validated smoke metrics: reward={max(rewards):.6g}, "
    f"reward_std={max(stds):.6g}, grad_norm={max(grad_norms):.6g}, "
    f"min_clipped_ratio={min(clipped_ratios):.6g}"
)
PY
    then
      log "smoke run OK with reward diversity and a non-zero policy gradient."
    else
      log "FATAL: smoke completed but reward diversity validation failed."
      exit 1
    fi
  else
    log "FATAL: smoke run failed; see ${SMOKE_LOG}."
    exit 1
  fi
fi

# 2. Full run.
if [[ "${RUN_FULL}" == "1" ]]; then
  log "starting full GRPO run (corpus=${CORPUS}) -> ${TRAIN_LOG}"
  HF_TOKEN="${HF_TOKEN}" uv run --no-sync --project "${PROFILE_PROJECT}" jmh-train-grpo \
    "${GRPO_CLI_ARGS[@]}" 2>&1 | tee "${TRAIN_LOG}"
  rc=${PIPESTATUS[0]}
  if [[ "${rc}" == "0" ]]; then
    plot_diagnostics
  fi
  log "full GRPO run exited with code ${rc}"
  exit "${rc}"
else
  log "RUN_FULL=0 — stopping after smoke."
fi
