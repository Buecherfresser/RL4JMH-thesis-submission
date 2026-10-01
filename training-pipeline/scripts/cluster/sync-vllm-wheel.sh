#!/usr/bin/env bash
# Sync a built vLLM wheel from the rental VM -> local SSD -> HPI.
#
#   ./scripts/cluster/sync-vllm-wheel.sh
#
# Env overrides:
#   VAST_HOST / VAST_PORT / VAST_KEY
#   LOCAL_DIR   default: /Volumes/SamsungSSD/ml-artifacts/vllm-cu128-h100
#   HPI_DIR     default: /sc/scratch/zongxiong.chen/jonas/artifacts/vllm-cu128-h100
set -euo pipefail

VAST_HOST="${VAST_HOST:-66.92.198.250}"
VAST_PORT="${VAST_PORT:-11086}"
VAST_KEY="${VAST_KEY:-$HOME/.ssh/id_ed25519}"
REMOTE_DIST="${REMOTE_DIST:-/workspace/vllm-build/dist}"
LOCAL_DIR="${LOCAL_DIR:-/Volumes/SamsungSSD/ml-artifacts/vllm-cu128-h100}"
HPI_HOST="${HPI_HOST:-hpi2}"
HPI_DIR="${HPI_DIR:-/sc/scratch/zongxiong.chen/jonas/artifacts/vllm-cu128-h100}"

mkdir -p "${LOCAL_DIR}"
echo "==> pull from vast ${VAST_HOST}:${REMOTE_DIST}"
rsync -avz --progress \
  -e "ssh -p ${VAST_PORT} -i ${VAST_KEY}" \
  "root@${VAST_HOST}:${REMOTE_DIST}/" \
  "${LOCAL_DIR}/"

echo "==> push to ${HPI_HOST}:${HPI_DIR}"
ssh "${HPI_HOST}" "mkdir -p '${HPI_DIR}'"
rsync -avz --progress \
  "${LOCAL_DIR}/" \
  "${HPI_HOST}:${HPI_DIR}/"

echo "==> done"
ls -lh "${LOCAL_DIR}"
ssh "${HPI_HOST}" "ls -lh '${HPI_DIR}'"
