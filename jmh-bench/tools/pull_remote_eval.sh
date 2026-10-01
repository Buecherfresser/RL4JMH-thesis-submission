#!/usr/bin/env bash
#
# pull_remote_eval.sh — rsync completed eval runs from remote VMs to reports/gemma4-e2b/.
#
# Usage:
#   tools/pull_remote_eval.sh
#   tools/pull_remote_eval.sh fsc01.local:mutation:eval_2026-07-14_10-31-21
#   tools/pull_remote_eval.sh fsc01.local:base:gemma4-e2b/base/2026-08-11_11-50-00
#
# Each spec is HOST:VARIANT:RUN_PATH, where RUN_PATH is either a run directory
# name directly under the remote reports/ (the legacy eval_YYYY-MM-DD_HH-MM-SS
# form) or a path relative to it, for campaigns filed under a model/tag tree.
#
# A run directory holds both tracks; the local layout keeps them apart
# (reports/gemma4-e2b/<track>/<variant>/<timestamp>), so each track's cells are
# selected by name — project-* and synthetic-* — rather than copied wholesale.
#
set -euo pipefail

REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
REMOTE_BASE="${REMOTE_BASE:-jmhbench-remote}"
DEST_ROOT="${DEST_ROOT:-$REPO/reports/gemma4-e2b}"

RSYNC=(rsync -az --progress)
if [[ -n "${SSH_OPTS:-}" ]]; then
  RSYNC+=(-e "ssh $SSH_OPTS")
fi

log() { echo "[$(date -Iseconds)] $*"; }
die() { log "ERROR: $*"; exit 1; }

remote_reports_dir() {
  local host=$1 run_path=$2
  local q="echo \"\$HOME/$REMOTE_BASE/reports/$run_path\""
  if [[ -n "${SSH_OPTS:-}" ]]; then
    ssh $SSH_OPTS "$host" "$q"
  else
    ssh "$host" "$q"
  fi
}

pull_one() {
  local host=$1 variant=$2 run_path=$3
  local remote_path track dest stamp

  remote_path="$(remote_reports_dir "$host" "$run_path")"
  log "checking $host:$remote_path ..."
  if [[ -n "${SSH_OPTS:-}" ]]; then
    ssh $SSH_OPTS "$host" "test -d '$remote_path/reports' || test -d '$remote_path/bundles'"
  else
    ssh "$host" "test -d '$remote_path/reports' || test -d '$remote_path/bundles'"
  fi || die "no results at $host:$remote_path"

  stamp="$(basename "$run_path")"
  stamp="${stamp#eval_}"

  for track in project synthetic; do
    dest="$DEST_ROOT/$track/$variant/$stamp"
    mkdir -p "$dest"
    log "pulling $track -> $dest"
    # --prune-empty-dirs keeps the other track's now-empty bundles/ and reports/
    # skeletons out; scratch/ and tmp/ fall to the trailing catch-all exclude.
    "${RSYNC[@]}" \
      --prune-empty-dirs \
      --include='/run.log' \
      --include='/bench.log' \
      --include='/bundles/' \
      --include="/bundles/${track}-*/***" \
      --include='/reports/' \
      --include="/reports/${track}-*/***" \
      --exclude='*' \
      "$host:$remote_path/" "$dest/"
  done
}

SPECS=("$@")
if (( ${#SPECS[@]} == 0 )); then
  SPECS=(
    "fsc01.local:mutation:eval_2026-07-14_10-31-21"
    "fsc04.local:simpleRL:eval_2026-07-14_11-30-18"
  )
fi

for spec in "${SPECS[@]}"; do
  IFS=: read -r host variant run_path <<< "$spec"
  [[ -n "$host" && -n "$variant" && -n "$run_path" ]] \
    || die "bad spec '$spec' (want HOST:VARIANT:RUN_PATH)"
  pull_one "$host" "$variant" "$run_path"
done

log "done — results under $DEST_ROOT"
