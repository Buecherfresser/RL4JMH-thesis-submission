#!/usr/bin/env bash
# Sync code/config/scripts to the HPI scratch tree used by Qwen/Gemma GRPO jobs.
#
# Requires SSH alias ``hpi2`` (or set REMOTE=user@host).
# Does NOT use --delete and skips huge data/ checkouts so classpaths stay intact.
#
#   ./scripts/cluster/sync-to-hpi2.sh
#   REMOTE=zongxiong.chen@rx01.hpc.sci.hpi.de ./scripts/cluster/sync-to-hpi2.sh
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
REMOTE="${REMOTE:-hpi2}"
DEST="${DEST:-/sc/scratch/zongxiong.chen/jonas/JMH_Training_Pipeline}"

RSYNC_EXCLUDES=(
  --exclude '.venv'
  --exclude '.venv-*'
  --exclude '.git'
  --exclude 'outputs/'
  --exclude '.cache/'
  --exclude '.uv-cache/'
  --exclude '__pycache__/'
  --exclude '.pytest_cache/'
  --exclude '*.pyc'
  --exclude 'data/external-src/'
  --exclude 'data/rxjava-src/'
  --exclude 'data/classpaths/lib/'
  --exclude 'logs/'
  --exclude 'vendor/'
)

echo "rsync (safe, no --delete) ${ROOT}/ -> ${REMOTE}:${DEST}/"
rsync -az "${RSYNC_EXCLUDES[@]}" \
  "${ROOT}/" "${REMOTE}:${DEST}/"

echo "synced. On the login node:"
echo "  cd ${DEST}"
echo "  sbatch scripts/cluster/smoke-qwen-grpo.sbatch"
echo "  # then: sbatch scripts/cluster/run-grpo-qwen35-4b.sbatch"
