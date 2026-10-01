#!/usr/bin/env bash
# Start a GRPO rollout server on a dedicated GPU (separate venv from the trainer).
# Do not use this on a one-GPU host: TRL server mode requires different CUDA devices for the
# server and trainer. Single-GPU runs should use vllm_mode=colocate instead.
# Run inside tmux so it survives SSH disconnects:
#   tmux new -s vllm
#   export HF_TOKEN=hf_...
#   ./scripts/remote/start-vllm.sh 2>&1 | tee "${JMHGEN_VOLUME:-/workspace}/logs/vllm.log"
#
# Serve modes:
#   VLLM_SERVE_MODE=openai  — JMH-Bench / RFT / pure inference (/v1/…)
#   VLLM_SERVE_MODE=trl     — GRPO weight-sync + private rollout endpoints (/health/, /generate/)
set -euo pipefail

VOLUME_ROOT="${JMHGEN_VOLUME:-/workspace}"
PROFILE="${JMHGEN_MODEL_PROFILE:-gemma}"
export PATH="${HOME}/.local/bin:${PATH}"
export UV_CACHE_DIR="${VOLUME_ROOT}/.uv-cache"
export UV_LINK_MODE=copy
export HF_HOME="${VOLUME_ROOT}/.cache/huggingface"
export TRANSFORMERS_CACHE="${HF_HOME}"
export XDG_CACHE_HOME="${VOLUME_ROOT}/.cache"

case "${PROFILE}" in
  gemma)
    PROFILE_PROJECT="profiles/gemma-cu130"
    PROFILE_SUFFIX="gemma-cu130"
    DEFAULT_MODEL="google/gemma-4-E2B-it"
    ;;
  qwen)
    PROFILE_PROJECT="profiles/qwen-cu128"
    PROFILE_SUFFIX="qwen-cu128"
    DEFAULT_MODEL="Qwen/Qwen3.5-4B"
    ;;
  qwen-cu130)
  # Released vLLM 0.23.0 on CUDA 13. Deliberately a SEPARATE profile from `qwen`: that one
  # installs the hand-built cu128 wheel, so pointing it at the cu130 venv would `uv sync`
  # cu128 torch straight into it and break the environment.
    PROFILE_PROJECT="profiles/qwen-cu130"
    PROFILE_SUFFIX="qwen-cu130"
    DEFAULT_MODEL="Qwen/Qwen3.5-4B"
    ;;
  *)
    echo "[start-vllm] unsupported JMHGEN_MODEL_PROFILE=${PROFILE}; use gemma, qwen or qwen-cu130" >&2
    exit 2
    ;;
esac

VLLM_VENV="${VLLM_VENV:-${VOLUME_ROOT}/.venv-${PROFILE_SUFFIX}}"
# The venv's bin must be on PATH, not just its python: FlashInfer's JIT shells out to `ninja`,
# which lives at $VLLM_VENV/bin/ninja. Running the venv interpreter by absolute path leaves that
# directory off PATH, and the JIT then dies inside the EngineCore with
#   FileNotFoundError: [Errno 2] No such file or directory: 'ninja'
# where TRL's server never surfaces it -- it just sits at "Waiting for application startup."
if [[ -d "${VLLM_VENV}/bin" ]]; then
  export PATH="${VLLM_VENV}/bin:${PATH}"
fi
MODEL="${VLLM_MODEL:-${DEFAULT_MODEL}}"
PORT="${VLLM_PORT:-8000}"
GPU_UTIL="${VLLM_GPU_MEMORY_UTILIZATION:-0.35}"
MAX_LEN="${VLLM_MAX_MODEL_LEN:-16384}"
# ``openai`` is for JMH-Bench/RFT; ``trl`` retains GRPO's private rollout and
# weight-sync API. Both modes use this same profile venv and vLLM wheel.
SERVE_MODE="${VLLM_SERVE_MODE:-openai}"

mkdir -p "${VOLUME_ROOT}/logs" "${UV_CACHE_DIR}" "${HF_HOME}"

export UV_PROJECT_ENVIRONMENT="${VLLM_VENV}"
export JMHGEN_MODEL_PROFILE="${PROFILE}"
if [[ "${PROFILE}" == "qwen" ]]; then
  export VLLM_MAIN_CUDA_VERSION=12.8
  if command -v nvcc >/dev/null && nvcc --version | grep -q 'release 12\.8'; then
    export CUDA_HOME="${CUDA_HOME:-$(dirname "$(dirname "$(command -v nvcc)")")}"
  fi
  if [[ -n "${CUDA_HOME:-}" && -x "${CUDA_HOME}/bin/nvcc" ]]; then
    # FlashInfer JIT otherwise falls back to /usr/local/cuda, which does not
    # exist on hpi2. Keep its generated Ninja files in the profile cache so a
    # stale build from another CUDA toolkit cannot be reused.
    export FLASHINFER_NVCC="${FLASHINFER_NVCC:-${CUDA_HOME}/bin/nvcc}"
    export FLASHINFER_WORKSPACE_BASE="${FLASHINFER_WORKSPACE_BASE:-${VLLM_CACHE_ROOT:-${VOLUME_ROOT}/.cache/vllm}/flashinfer}"
  fi
  for curand_include in "${VLLM_VENV}"/lib/python*/site-packages/nvidia/curand/include; do
    if [[ -d "${curand_include}" ]]; then
      # The hpi2 CUDA module has nvcc but not the curand development header.
      # vLLM's FlashInfer sampler JIT needs the header bundled with Torch.
      export CPATH="${curand_include}${CPATH:+:${CPATH}}"
      break
    fi
  done
fi

# The Qwen vLLM wheel is installed after the locked profile sync because it
# must match this host's CUDA ABI. ``--inexact`` keeps that wheel instead of
# treating it as an extraneous package and downloading it again at every start.
uv sync --inexact --project "${PROFILE_PROJECT}"

# After sync, never let `uv run --project` re-resolve — it can replace the
# cu128 torch that matches the prebuilt vLLM wheel. Use --no-sync / venv bins.
VENV_PY="${VLLM_VENV}/bin/python"

# Qwen: prefer the prebuilt cu128 wheel under artifacts/. Only fall back to a
# source build (needs full CUDA 12.8 toolkit) when no wheel is available.
# Override: FORCE_VLLM_REBUILD=1
if [[ "${PROFILE}" == "qwen" ]]; then
  if [[ "${FORCE_VLLM_REBUILD:-0}" == "1" ]] \
    || ! "${VENV_PY}" -c "import vllm" >/dev/null 2>&1; then
    ARTIFACT_DIR="${VLLM_WHEEL_DIR:-${VOLUME_ROOT}/artifacts/vllm-cu128-h100}"
    HAVE_WHEEL=0
    if [[ -n "${VLLM_WHEEL:-}" && -f "${VLLM_WHEEL}" ]]; then
      HAVE_WHEEL=1
    elif ls "${ARTIFACT_DIR}"/vllm-*.whl >/dev/null 2>&1; then
      HAVE_WHEEL=1
    fi
    if [[ "${HAVE_WHEEL}" != "1" ]]; then
      if ! command -v nvcc >/dev/null || ! nvcc --version | grep -q 'release 12\.8'; then
        echo "[start-vllm] Qwen rebuild needs a prebuilt wheel under ${ARTIFACT_DIR} or CUDA 12.8 nvcc." >&2
        exit 2
      fi
      export CUDA_HOME="${CUDA_HOME:-$(dirname "$(dirname "$(command -v nvcc)")")}"
    fi
    echo "[start-vllm] installing Qwen vLLM into ${VLLM_VENV}"
    REPO_DIR="$(pwd)" scripts/remote/install-qwen-vllm.sh
  else
    echo "[start-vllm] vLLM already importable — skipping install (FORCE_VLLM_REBUILD=1 to force)"
  fi
fi
"${VENV_PY}" - <<'PY'
import torch, vllm
import vllm._C_stable_libtorch  # CUDA builds

print(f"[start-vllm] torch={torch.__version__} cuda={torch.version.cuda} vllm={vllm.__version__}")
PY

export CUDA_VISIBLE_DEVICES="${CUDA_VISIBLE_DEVICES:-0}"
COMMON_ARGS=(
  --gpu-memory-utilization "${GPU_UTIL}"
  --max-model-len "${MAX_LEN}"
  --port "${PORT}"
)
QWEN_OPENAI_ARGS=()
# Both qwen profiles: Qwen3.5 needs text-only init regardless of which vLLM build.
if [[ "${PROFILE}" == qwen* ]]; then
  QWEN_OPENAI_ARGS=(
    --language-model-only
    --reasoning-parser qwen3
  )
fi

# --no-sync: keep the wheel-matched torch; do not re-apply the lock on serve.
case "${SERVE_MODE}" in
  openai)
    # JMH-Bench/RFT use the standard OpenAI-compatible /v1 API. vLLM owns
    # these options directly, unlike TRL 1.7.1's restricted argument parser.
    exec uv run --no-sync --project "${PROFILE_PROJECT}" \
      vllm serve "${MODEL}" "${COMMON_ARGS[@]}" \
      --enforce-eager \
      "${QWEN_OPENAI_ARGS[@]}"
    ;;
  trl)
    # GRPO server mode must keep TRL's private generation/log-probability and
    # weight-update endpoints. The version-pinned adapter adds the otherwise
    # unsupported vLLM text-only option to TRL 1.7.1's worker.
    # TRL 1.7.1's vllm-serve parser has no --language-model-only (verified against the
    # installed TRL), so BOTH qwen profiles need the jmhgen shim to reach vLLM with it.
    if [[ "${PROFILE}" == qwen* ]]; then
      exec uv run --no-sync --project "${PROFILE_PROJECT}" \
        python -m jmhgen.serving.trl_vllm_serve \
        --model "${MODEL}" "${COMMON_ARGS[@]}" \
        --enforce-eager true \
        --language-model-only
    fi
    exec uv run --no-sync --project "${PROFILE_PROJECT}" \
      trl vllm-serve \
      --model "${MODEL}" "${COMMON_ARGS[@]}" \
      --enforce-eager true
    ;;
  *)
    echo "[start-vllm] unsupported VLLM_SERVE_MODE=${SERVE_MODE}; use openai or trl" >&2
    exit 2
    ;;
esac
