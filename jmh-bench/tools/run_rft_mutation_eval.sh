#!/usr/bin/env bash
# Generate + bench all 4 temp/thinking combos for project + synthetic tracks.
# Generation runs locally against RunPod vLLM; benchmarking runs sequentially on bsc-gcp.
set -euo pipefail

REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO"
# shellcheck source=/dev/null
source "$REPO/.venv/bin/activate"
set -a
# shellcheck source=/dev/null
source "$REPO/.env"
set +a

LOG="$REPO/reports/rft-mutation-eval.log"
mkdir -p "$REPO/reports"

REMOTE="bsc-gcp"
REMOTE_BASE="~/jmhbench-remote"
PARALLEL="${PARALLEL:-8}"
POLL_SECS="${POLL_SECS:-15}"
MAX_WAIT_SECS="${MAX_WAIT_SECS:-3600}"

# The injected-latency operator (see tools/run_eval.sh for the why). `sleep` is
# what every run so far measured under; MUTANT_OP=spin with a host-calibrated
# MUTANT_TOKENS injects nanoseconds instead of ~1.2 ms.
MUTANT_OP="${MUTANT_OP:-sleep}"
MUTANT_TOKENS="${MUTANT_TOKENS:-64}"

log() { echo "[$(date -Iseconds)] $*" | tee -a "$LOG" >&2; }

wait_for_api() {
  log "Waiting for RunPod vLLM at $OPENAI_BASE_URL ..."
  local elapsed=0
  while (( elapsed < MAX_WAIT_SECS )); do
    local code
    code=$(curl -s -o /tmp/jmhbench_models.json -w "%{http_code}" \
      -H "Authorization: Bearer ${OPENAI_API_KEY}" \
      "${OPENAI_BASE_URL%/}/models" || true)
    if [[ "$code" == "200" ]] && grep -q '"data"' /tmp/jmhbench_models.json 2>/dev/null; then
      log "vLLM ready (model: ${OPENAI_MODEL})"
      return 0
    fi
    log "  not ready (HTTP $code), retry in ${POLL_SECS}s ..."
    sleep "$POLL_SECS"
    elapsed=$((elapsed + POLL_SECS))
  done
  die "RunPod vLLM did not become ready within ${MAX_WAIT_SECS}s"
}

die() { log "ERROR: $*"; exit 1; }

LAST_BUNDLE=""

run_project_gen() {
  local tag="$1" temp="$2" thinking="$3"
  local out="$REPO/reports/rft-mutation_projgen_${tag}"
  log "=== project-gen: $tag (temp=$temp thinking=$thinking) -> $out ==="
  local -a opts=(-o "temperature=$temp" -o "model=${OPENAI_MODEL}")
  if [[ "$thinking" == "true" ]]; then
    opts+=(-o enable_thinking=true)
  else
    opts+=(-o enable_thinking=false)
  fi
  jmhbench project-gen \
    --harness openai-zero-shot \
    "${opts[@]}" \
    --parallel "$PARALLEL" \
    --compile-check \
    --out "$out"
  LAST_BUNDLE="$out"
}

run_synthetic_gen() {
  local tag="$1" temp="$2" thinking="$3"
  local out="$REPO/reports/rft-mutation_gen_${tag}"
  log "=== generate (synthetic): $tag (temp=$temp thinking=$thinking) -> $out ==="
  local -a opts=(-o "temperature=$temp" -o "model=${OPENAI_MODEL}")
  if [[ "$thinking" == "true" ]]; then
    opts+=(-o enable_thinking=true)
  else
    opts+=(-o enable_thinking=false)
  fi
  jmhbench generate \
    --harness openai-zero-shot \
    "${opts[@]}" \
    --parallel "$PARALLEL" \
    --out "$out"
  LAST_BUNDLE="$out"
}

bench_project_on_remote() {
  local bundle="$1"
  local name
  name="$(basename "$bundle")"
  local remote_run="$REMOTE_BASE/runs/$name"
  local remote_scratch="$REMOTE_BASE/runs/bench-scratch-$name"
  local remote_log="$REMOTE_BASE/runs/${name}-bench.log"

  log "=== project-bench on $REMOTE: $name ==="
  ssh "$REMOTE" "mkdir -p $REMOTE_BASE/runs"
  rsync -az "$bundle/" "$REMOTE:$remote_run/"

  ssh "$REMOTE" "cd $REMOTE_BASE && source .venv/bin/activate && \
    export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 && \
    export PATH=\$JAVA_HOME/bin:\$PATH && \
    jmhbench project-bench '$remote_run' --workdir '$remote_scratch' \
    --mutant-op '$MUTANT_OP' --mutant-tokens '$MUTANT_TOKENS' \
    2>&1 | tee '$remote_log'"
}

bench_synthetic_on_remote() {
  local bundle="$1"
  local name
  name="$(basename "$bundle")"
  local remote_bundle="$REMOTE_BASE/runs/$name/bundle"
  local remote_report="$REMOTE_BASE/runs/$name/report"

  log "=== bench (synthetic) on $REMOTE: $name ==="
  ssh "$REMOTE" "mkdir -p '$remote_bundle' '$remote_report'"
  rsync -az "$bundle/" "$REMOTE:$remote_bundle/"
  ssh "$REMOTE" "cd $REMOTE_BASE && source .venv/bin/activate && \
    export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 && \
    export PATH=\$JAVA_HOME/bin:\$PATH && \
    jmhbench bench '$remote_bundle' --out '$remote_report'"
  mkdir -p "$REPO/reports/${name}-bench"
  rsync -az "$REMOTE:$remote_report/" "$REPO/reports/${name}-bench/"
}

# --- main -------------------------------------------------------------------
wait_for_api

COMBOS=(
  "t0-think:0:true"
  "t0-nothink:0:false"
  "t1-think:1:true"
  "t1-nothink:1:false"
)

PROJECT_BUNDLES=()
SYN_BUNDLES=()

for combo in "${COMBOS[@]}"; do
  IFS=: read -r tag temp thinking <<< "$combo"
  run_project_gen "$tag" "$temp" "$thinking"
  PROJECT_BUNDLES+=("$LAST_BUNDLE")
done

for combo in "${COMBOS[@]}"; do
  IFS=: read -r tag temp thinking <<< "$combo"
  run_synthetic_gen "$tag" "$temp" "$thinking"
  SYN_BUNDLES+=("$LAST_BUNDLE")
done

log "=== All generation complete. Starting sequential remote benchmarking ==="

for bundle in "${PROJECT_BUNDLES[@]}"; do
  bench_project_on_remote "$bundle"
done

for bundle in "${SYN_BUNDLES[@]}"; do
  bench_synthetic_on_remote "$bundle"
done

log "=== All runs finished ==="
