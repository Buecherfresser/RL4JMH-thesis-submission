#!/usr/bin/env bash
#
# remote_bench.sh — run the *benchmarking* phase of a decoupled JMH-Bench run on
# a remote (quiet, dedicated) machine.
#
# Generation bundles produced by `jmhbench generate` are self-contained: they
# embed the task definitions, so the benchmarking host needs only `jmhbench`
# installed (editable, so java-runner/ ships with it), a JDK 17+ and Maven — no
# API keys, no dataset checkout, no model access.
#
# This script:
#   1. rsyncs a local bundle directory up to the remote,
#   2. runs `jmhbench bench` there (results written to a known --out path),
#   3. rsyncs the resulting report back to your machine.
#
# Usage:
#   tools/remote_bench.sh <bundle_dir> <user@host> [remote_base_dir] [-- <bench args...>]
#
# Examples:
#   # default preset, results land in ./<bundle>-report/
#   tools/remote_bench.sh reports/gen_claude_2026-... bench-box
#
#   # strong preset + tighter alpha, custom remote dir
#   tools/remote_bench.sh reports/gen_claude_2026-... me@bench-box /scratch/jmh \
#       -- --strong --alpha 0.01
#
#   # false-positive-rate mode
#   tools/remote_bench.sh reports/gen_claude_2026-... bench-box -- --replicates 5
#
# Configuration via environment variables:
#   REMOTE_SETUP   shell snippet run on the remote before jmhbench, e.g.
#                  "cd ~/JMH-Bench && source .venv/bin/activate"
#                  (default: empty — assumes jmhbench is on PATH)
#   JMHBENCH       the jmhbench command on the remote (default: "jmhbench")
#   LOCAL_OUT      local directory to receive the report
#                  (default: ./<bundle_name>-report)
set -euo pipefail

die() { echo "error: $*" >&2; exit 1; }

# --- parse args -------------------------------------------------------------
BENCH_ARGS=()
POSITIONAL=()
while [[ $# -gt 0 ]]; do
  if [[ "$1" == "--" ]]; then
    shift
    BENCH_ARGS=("$@")
    break
  fi
  POSITIONAL+=("$1")
  shift
done

[[ ${#POSITIONAL[@]} -ge 2 ]] || die "usage: $0 <bundle_dir> <user@host> [remote_base_dir] [-- <bench args...>]"

BUNDLE_DIR="${POSITIONAL[0]%/}"
REMOTE="${POSITIONAL[1]}"
REMOTE_BASE="${POSITIONAL[2]:-jmhbench-runs}"

[[ -f "$BUNDLE_DIR/generation.json" ]] || \
  die "$BUNDLE_DIR is not a generation bundle (no generation.json). Run 'jmhbench generate' first."

NAME="$(basename "$BUNDLE_DIR")"
REMOTE_BUNDLE="$REMOTE_BASE/$NAME/bundle"
REMOTE_REPORT="$REMOTE_BASE/$NAME/report"
LOCAL_OUT="${LOCAL_OUT:-./$NAME-report}"
JMHBENCH="${JMHBENCH:-jmhbench}"
REMOTE_SETUP="${REMOTE_SETUP:-}"

echo ">> bundle : $BUNDLE_DIR"
echo ">> remote : $REMOTE:$REMOTE_BUNDLE"
echo ">> report : $LOCAL_OUT"

# --- 1. ship the bundle up --------------------------------------------------
ssh "$REMOTE" "mkdir -p '$REMOTE_BUNDLE' '$REMOTE_REPORT'"
rsync -az --delete "$BUNDLE_DIR/" "$REMOTE:$REMOTE_BUNDLE/"

# --- 2. benchmark on the remote ---------------------------------------------
# Build the remote command; REMOTE_SETUP (if any) runs first so a venv can be
# activated / the repo entered before jmhbench is invoked.
REMOTE_CMD="${REMOTE_SETUP:+$REMOTE_SETUP && }$JMHBENCH bench '$REMOTE_BUNDLE' --out '$REMOTE_REPORT'"
for a in "${BENCH_ARGS[@]:-}"; do
  [[ -n "$a" ]] && REMOTE_CMD+=" $(printf '%q' "$a")"
done
echo ">> running: $REMOTE_CMD"
ssh "$REMOTE" "$REMOTE_CMD"

# --- 3. pull the report back ------------------------------------------------
mkdir -p "$LOCAL_OUT"
rsync -az "$REMOTE:$REMOTE_REPORT/" "$LOCAL_OUT/"

echo ">> done. report at: $LOCAL_OUT"
