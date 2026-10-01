#!/usr/bin/env bash
# Install a prebuilt Qwen-compatible vLLM wheel into the already-synced CUDA-12.8 profile.
#
# Preferred (no local compile):
#   VLLM_WHEEL=/path/to/vllm-…cu128….whl \
#     UV_PROJECT_ENVIRONMENT=… JMHGEN_MODEL_PROFILE=qwen \
#     scripts/remote/install-qwen-vllm.sh
#
# Auto-picks the newest wheel under $JMH_ROOT/artifacts/vllm-cu128-h100/ when
# VLLM_WHEEL is unset. Falls back to a source build of
# profiles/qwen-cu128/vllm-revision.txt only when no wheel is found
# (needs CUDA 12.8 nvcc). Override with FORCE_VLLM_REBUILD=1.
#
# After a wheel install we pin torch/torchvision/torchaudio from the PyTorch
# cu128 index so the wheel's native ABI matches (see MANIFEST.json).
set -euo pipefail

REPO_DIR="${REPO_DIR:-$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)}"
PROFILE="${JMHGEN_MODEL_PROFILE:-qwen}"
VLLM_REF="$(<"${REPO_DIR}/profiles/qwen-cu128/vllm-revision.txt")"
JMH_ROOT="${JMH_ROOT:-$(dirname "${REPO_DIR}")}"
RUSTUP_HOME="${RUSTUP_HOME:-${JMH_ROOT}/tools/rustup}"
CARGO_HOME="${CARGO_HOME:-${JMH_ROOT}/tools/cargo}"
ARTIFACT_DIR="${VLLM_WHEEL_DIR:-${JMH_ROOT}/artifacts/vllm-cu128-h100}"
PYTORCH_CU128_INDEX="${PYTORCH_CU128_INDEX:-https://download.pytorch.org/whl/cu128}"

if [[ "${PROFILE}" != "qwen" ]]; then
  echo "[install-qwen-vllm] JMHGEN_MODEL_PROFILE must be qwen." >&2
  exit 2
fi
if [[ -z "${UV_PROJECT_ENVIRONMENT:-}" ]]; then
  echo "[install-qwen-vllm] set UV_PROJECT_ENVIRONMENT first." >&2
  exit 2
fi

export VLLM_MAIN_CUDA_VERSION="${VLLM_MAIN_CUDA_VERSION:-12.8}"
export RUSTUP_HOME CARGO_HOME
export PATH="${CARGO_HOME}/bin:${PATH}"
VENV_PY="${UV_PROJECT_ENVIRONMENT}/bin/python"

pick_wheel() {
  if [[ -n "${VLLM_WHEEL:-}" && -f "${VLLM_WHEEL}" ]]; then
    printf '%s\n' "${VLLM_WHEEL}"
    return 0
  fi
  if [[ -d "${ARTIFACT_DIR}" ]]; then
    local wheel
    wheel="$(ls -1t "${ARTIFACT_DIR}"/vllm-*.whl 2>/dev/null | head -1 || true)"
    if [[ -n "${wheel}" ]]; then
      printf '%s\n' "${wheel}"
      return 0
    fi
  fi
  return 1
}

# Read torch / torchvision / torchaudio pins from MANIFEST.json next to the wheel.
# Defaults match the known-good cu128 H100 build.
torch_pins_from_manifest() {
  local wheel="$1"
  local manifest
  manifest="$(dirname "${wheel}")/MANIFEST.json"
  local torch_v="2.11.0" vision_v="0.26.0" audio_v="2.11.0"
  if [[ -f "${manifest}" ]]; then
    # MANIFEST may say "2.11.0+cu128" — strip the local version tag for pip.
    torch_v="$(python3 -c "import json,sys; t=json.load(open(sys.argv[1])).get('torch','2.11.0'); print(t.split('+')[0])" "${manifest}")"
  fi
  printf '%s %s %s\n' "${torch_v}" "${vision_v}" "${audio_v}"
}

pin_torch_cu128() {
  local torch_v="$1" vision_v="$2" audio_v="$3"
  echo "[install-qwen-vllm] pinning cu128 torch stack: torch==${torch_v} torchvision==${vision_v} torchaudio==${audio_v}"
  uv pip install --python "${UV_PROJECT_ENVIRONMENT}" \
    --index-url "${PYTORCH_CU128_INDEX}" \
    --force-reinstall \
    "torch==${torch_v}" \
    "torchvision==${vision_v}" \
    "torchaudio==${audio_v}"
}

install_from_wheel() {
  local wheel="$1"
  echo "[install-qwen-vllm] installing prebuilt wheel: ${wheel}"
  # Wheel install may pull a non-cu128 torch from PyPI; we re-pin from the
  # cu128 index immediately after so the native ABI matches the wheel.
  uv pip install --python "${UV_PROJECT_ENVIRONMENT}" --force-reinstall "${wheel}"
  read -r TORCH_V VISION_V AUDIO_V <<<"$(torch_pins_from_manifest "${wheel}")"
  pin_torch_cu128 "${TORCH_V}" "${VISION_V}" "${AUDIO_V}"
}

ensure_rust() {
  mkdir -p "${RUSTUP_HOME}" "${CARGO_HOME}"
  if [[ ! -x "${CARGO_HOME}/bin/rustup" ]]; then
    echo "[install-qwen-vllm] installing rustup into ${CARGO_HOME}"
    curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs \
      | sh -s -- -y --no-modify-path --default-toolchain stable
  fi
  # GPFS often drops execute bits on extracted toolchain binaries (same class of issue as JDK).
  chmod -R u+x "${CARGO_HOME}/bin" "${RUSTUP_HOME}/toolchains" 2>/dev/null || true
  if ! rustc --version >/dev/null 2>&1; then
    rustup default stable
    chmod -R u+x "${RUSTUP_HOME}/toolchains" 2>/dev/null || true
  fi
  rustc --version
  cargo --version
}

install_from_source() {
  if ! command -v nvcc >/dev/null || ! nvcc --version | grep -q 'release 12\.8'; then
    echo "[install-qwen-vllm] CUDA 12.8 nvcc is required to build the pinned vLLM extension." >&2
    exit 2
  fi
  export CUDA_HOME="${CUDA_HOME:-$(dirname "$(dirname "$(command -v nvcc)")")}"
  if [[ ! -f "${CUDA_HOME}/include/cublas_v2.h" ]]; then
    echo "[install-qwen-vllm] ${CUDA_HOME}/include/cublas_v2.h missing; use a prebuilt wheel instead." >&2
    exit 2
  fi
  ensure_rust

  # --no-build-isolation reuses the profile's cu128 torch, so build deps must already be present.
  # Keep setuptools in the range vLLM's pyproject allows (<81).
  uv pip install --python "${UV_PROJECT_ENVIRONMENT}" \
    "setuptools>=77.0.3,<81" \
    "setuptools-scm>=8.0" \
    "setuptools-rust>=1.9.0" \
    "cmake>=3.26.1" \
    ninja \
    wheel \
    jinja2 \
    "packaging>=24.2"

  uv pip install --python "${UV_PROJECT_ENVIRONMENT}" \
    --no-build-isolation \
    "vllm @ git+https://github.com/vllm-project/vllm.git@${VLLM_REF}"
}

WHEEL=""
if [[ "${FORCE_VLLM_REBUILD:-0}" != "1" ]] && WHEEL="$(pick_wheel)"; then
  install_from_wheel "${WHEEL}"
else
  if [[ "${FORCE_VLLM_REBUILD:-0}" == "1" ]]; then
    echo "[install-qwen-vllm] FORCE_VLLM_REBUILD=1 — building from source @ ${VLLM_REF}"
  else
    echo "[install-qwen-vllm] no prebuilt wheel under ${ARTIFACT_DIR}; building from source @ ${VLLM_REF}"
  fi
  install_from_source
fi

# Verify with the venv interpreter directly — do NOT use `uv run --project` here;
# that re-syncs against the lock and can replace the ABI-matched torch.
"${VENV_PY}" - <<'PY'
import torch, vllm
import vllm._C_stable_libtorch  # CUDA builds

print(
    f"[install-qwen-vllm] torch={torch.__version__} cuda={torch.version.cuda} "
    f"vllm={vllm.__version__}"
)
if "cu128" not in torch.__version__:
    raise SystemExit(f"expected torch *+cu128*, got {torch.__version__}")
PY
