#!/usr/bin/env bash
# Download Linux x86_64 / py3.12 wheels locally (fast network) for remote VMs
# where pip hits PyPI timeouts. Used automatically by tools/remote_run_eval.sh.
set -euo pipefail

REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT="${REMOTE_WHEEL_DIR:-$REPO/.remote-wheels/py312}"
STAMP="$OUT/.stamp"
PY_VER="${REMOTE_PYTHON_VERSION:-3.12}"
PLATFORM="${REMOTE_WHEEL_PLATFORM:-manylinux2014_x86_64}"

deps_hash() {
  md5sum "$REPO/pyproject.toml" 2>/dev/null | awk '{print $1}' \
    || shasum -a 256 "$REPO/pyproject.toml" | awk '{print $1}'
}

if [[ -f "$STAMP" && "$(cat "$STAMP")" == "$(deps_hash)-$PY_VER-$PLATFORM" ]]; then
  echo "wheel cache up to date: $OUT"
  exit 0
fi

# shellcheck source=/dev/null
source "$REPO/.venv/bin/activate"

mkdir -p "$OUT"
echo ">> downloading linux wheels to $OUT (py${PY_VER}, ${PLATFORM})"

# Resolve all runtime deps for .[openai] targeting the remote platform.
if ! pip download -d "$OUT" --extra openai "$REPO" \
    --platform "$PLATFORM" \
    --python-version "$PY_VER" \
    --implementation cp \
    --abi "cp${PY_VER//./}"; then
  echo ">> retrying with manylinux_2_17_x86_64 ..."
  pip download -d "$OUT" --extra openai "$REPO" \
    --platform manylinux_2_17_x86_64 \
    --python-version "$PY_VER" \
    --implementation cp \
    --abi "cp${PY_VER//./}"
fi

echo "$(deps_hash)-$PY_VER-$PLATFORM" > "$STAMP"
echo ">> cached $(find "$OUT" -maxdepth 1 \( -name '*.whl' -o -name '*.tar.gz' \) | wc -l | tr -d ' ') artifacts"
