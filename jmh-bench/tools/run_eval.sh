#!/usr/bin/env bash
#
# run_eval.sh — evaluate an OpenAI-compatible model across a matrix of
# temperature x thinking settings, for one or more benchmark tracks.
#
#   Phase 1: generate a JMH suite for every (track, temp, thinking) combo
#            against the API in .env (upfront — all bundles first).
#   Phase 2: benchmark each generated bundle, one at a time. This runs either
#            over SSH on a dedicated box (REMOTE=<host>) or right here on this
#            machine (REMOTE=local — e.g. when the whole script runs on the VM).
#
# Running everything on one machine (REMOTE=local) lets you kick it off on the
# benchmarking VM and close your laptop: generation only needs outbound HTTPS to
# the API, benchmarking needs the JDK + Maven that are already on the VM.
#
# Nothing about the model/endpoint is hardcoded — credentials come from .env
# (HARNESS plus OPENAI_* or OPENROUTER_*), so when the API or key changes you
# only edit .env. The whole matrix is driven by the config block
# below; override any of it from the environment, e.g.:
#
#   TEMPS="0 0.7 1" THINKING=true TRACKS=synthetic tools/run_eval.sh
#   PROJECTS="snakeyaml hppc" TRACKS=project tools/run_eval.sh  # subset of the corpus
#   REMOTE=local PARALLEL=100 tools/run_eval.sh   # all on this box, high fan-out
#   SKIP_GEN=1 tools/run_eval.sh                   # re-benchmark existing bundles
#   RUN_DIR=reports/my-eval tools/run_eval.sh
#
set -euo pipefail

REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO"
# shellcheck source=/dev/null
source "$REPO/.venv/bin/activate"
# Preserve explicit env overrides: sourcing .env with set -a would otherwise
# clobber REMOTE=local passed by remote_run_eval.sh ( .env often has REMOTE=<ssh-host> ).
_RUN_EVAL_ENV_REMOTE="${REMOTE-}"
_RUN_EVAL_ENV_SKIP_GEN="${SKIP_GEN-}"
_RUN_EVAL_ENV_SKIP_BENCH="${SKIP_BENCH-}"
_RUN_EVAL_ENV_RUN_DIR="${RUN_DIR-}"
set -a
# shellcheck source=/dev/null
source "$REPO/.env"
set +a
[[ -n "${_RUN_EVAL_ENV_REMOTE}" ]] && REMOTE="$_RUN_EVAL_ENV_REMOTE"
[[ -n "${_RUN_EVAL_ENV_SKIP_GEN}" ]] && SKIP_GEN="$_RUN_EVAL_ENV_SKIP_GEN"
[[ -n "${_RUN_EVAL_ENV_SKIP_BENCH}" ]] && SKIP_BENCH="$_RUN_EVAL_ENV_SKIP_BENCH"
[[ -n "${_RUN_EVAL_ENV_RUN_DIR}" ]] && RUN_DIR="$_RUN_EVAL_ENV_RUN_DIR"

# ---- config (all overridable from the environment) -------------------------
HARNESS="${HARNESS:-openai-zero-shot}"  # jmhbench harness: openai-zero-shot | openrouter
TRACKS="${TRACKS:-project synthetic}"   # which tracks to evaluate
# Project track only: the vendored projects to fan the matrix out over. Every
# (temp, thinking) cell is generated and benchmarked once per project, so the
# mutation score is averaged over the whole corpus rather than one library.
PROJECTS="${PROJECTS:-commons-compress snakeyaml hppc jodd-util decimal4j fastfilter}"
TEMPS="${TEMPS:-0 1}"                    # sampling temperatures
THINKING="${THINKING:-true false}"       # enable_thinking values
PARALLEL="${PARALLEL:-32}"               # concurrent generation calls
MAX_TOKENS="${MAX_TOKENS:-8192}"         # completion cap (input+output must fit model context)
COMPILE_CHECK="${COMPILE_CHECK:-true}"   # project track: compile+repair each class
                                         # (also builds via Maven -> see COMPILER_HEAP below)

# ---- project-track measurement settings -----------------------------------
# These used to be left unset, which meant the campaign silently ran the JMH
# `default` preset (-f 1 -i 3, no forced GC, unpinned JVM) and the truncated
# kill probe with no sensitivity sample -- i.e. the reported numbers came from
# the cheap smoke-test configuration (EVALUATION_ISSUES.md B1 / B4).
#
#   PRESET=campaign  -f 5 -wi 5 -w 1s -i 10 -r 1s, plus -bm thrpt -tu s -gc true
#                    -t 1 and a pinned 2g heap + named collector on both arms.
#                    Five forks rather than one deep fork: iterations inside a
#                    fork are autocorrelated and exclude fork-to-fork variance.
#   PRESET=llm4jmh   Chen et al.'s exact invocation, for replication only.
#
# SENSITIVITY_SAMPLE=all measures every covering benchmark per mutant, which is
# what turns mutation sensitivity from a bound into a measurement. It is the
# expensive knob: cost scales with the suite's fan-out, not with the mutant
# count. Set it to an integer for a uniform sample, or 0 for bounds only.
PRESET="${PRESET:-campaign}"
SENSITIVITY_SAMPLE="${SENSITIVITY_SAMPLE:-all}"
MAX_DETECT_ATTEMPTS="${MAX_DETECT_ATTEMPTS:-6}"

# What an armed mutant injects. `sleep` is Thread.sleep(0,1) -- ~1.2 ms a hit on
# the benchmarking boxes, four orders of magnitude above the 10% kill threshold,
# so every *covered* mutant is killed and the mutation score restates coverage.
# It stays the default because the 2026-08 campaign was measured under it.
# MUTANT_OP=spin injects MUTANT_TOKENS steps of an LCG instead (~1 ns each), a
# latency the benchmark has to actually resolve. Calibrate the token count on the
# benchmarking box, under the same core pinning:
#   uv run python tools/calibrate_mutant_op.py --target 100
MUTANT_OP="${MUTANT_OP:-sleep}"
MUTANT_TOKENS="${MUTANT_TOKENS:-64}"

# Max heap for the Maven JVM -- and so for javac, which maven-compiler-plugin
# runs in-process. This was hardcoded to 512m inside build.py and never exported
# from here, which is why GPT-OSS-120B's decimal4j and hppc runs both died with
# `java.lang.OutOfMemoryError: Java heap space` in javac and were scored 0/50 on
# projects they had in fact produced compilable suites for. Exported so it also
# reaches the *remote* benchmarking box, whose environment this script does not
# otherwise control.
COMPILER_HEAP="${COMPILER_HEAP:-4g}"
export JMHBENCH_COMPILER_HEAP="$COMPILER_HEAP"

REMOTE="${REMOTE:-bsc-gcp}"              # ssh host for benchmarking, or "local" to bench here
REMOTE_BASE="${REMOTE_BASE:-~/jmhbench-remote}"  # remote repo checkout (venv + JDK)
JAVA_HOME_HINT="${JAVA_HOME_HINT:-/usr/lib/jvm/java-21-openjdk-amd64}"  # used on remote & for local bench/compile-check

# Make the JDK discoverable for local compile-check / local benchmarking.
if [[ -z "${JAVA_HOME:-}" && -d "$JAVA_HOME_HINT" ]]; then
  export JAVA_HOME="$JAVA_HOME_HINT"
  export PATH="$JAVA_HOME/bin:$PATH"
fi

# Keep scratch off a small/RAM-backed /tmp: Maven target dirs, shaded JMH jars
# and JMH's forked-JVM temp files can easily exceed a tmpfs /tmp and fail with
# "No space left on device". TMPDIR is inherited by child java/mvn processes
# (java.io.tmpdir), so this covers both jmhbench's scratch and JMH's forks.
TMPDIR="${TMPDIR_OVERRIDE:-$REPO/tmp}"
export TMPDIR
mkdir -p "$TMPDIR"

SKIP_GEN="${SKIP_GEN:-}"                 # set to 1 to skip local generation
SKIP_BENCH="${SKIP_BENCH:-}"            # set to 1 to skip remote benchmarking

POLL_SECS="${POLL_SECS:-15}"
MAX_WAIT_SECS="${MAX_WAIT_SECS:-1800}"

RUN_DIR="${RUN_DIR:-$REPO/reports/eval_$(date +%Y-%m-%d_%H-%M-%S)}"
GEN_DIR="$RUN_DIR/bundles"
REPORT_DIR="$RUN_DIR/reports"
LOG="$RUN_DIR/run.log"
mkdir -p "$GEN_DIR" "$REPORT_DIR"

FAILED=()

log() { echo "[$(date -Iseconds)] $*" | tee -a "$LOG" >&2; }
die() { log "ERROR: $*"; exit 1; }

# Human-readable slug for one matrix cell, e.g. "synthetic-t0-think" or
# "project-snakeyaml-t0-think" (the project track carries its subject in the slug).
slug_for() {
  local track=$1 project=$2 temp=$3 think=$4 th
  [[ "$think" == "true" ]] && th="think" || th="nothink"
  if [[ "$track" == "project" ]]; then
    echo "${track}-${project}-t${temp}-${th}"
  else
    echo "${track}-t${temp}-${th}"
  fi
}

# Emit one "track<TAB>project<TAB>temp<TAB>thinking" line per matrix cell. The
# project track fans out over $PROJECTS; the other tracks have no project
# dimension and carry "-" instead.
cells() {
  local track temp think project
  for track in $TRACKS; do
    for temp in $TEMPS; do
      for think in $THINKING; do
        if [[ "$track" == "project" ]]; then
          for project in $PROJECTS; do
            printf '%s\t%s\t%s\t%s\n' "$track" "$project" "$temp" "$think"
          done
        else
          printf '%s\t%s\t%s\t%s\n' "$track" "-" "$temp" "$think"
        fi
      done
    done
  done
}

wait_for_api() {
  local base_url api_key model
  if [[ "$HARNESS" == "openrouter" ]]; then
    base_url="${OPENROUTER_BASE_URL:-https://openrouter.ai/api/v1}"
    api_key="${OPENROUTER_API_KEY:-}"
    model="${OPENROUTER_MODEL:-?}"
  else
    base_url="${OPENAI_BASE_URL:-}"
    api_key="${OPENAI_API_KEY:-}"
    model="${OPENAI_MODEL:-?}"
  fi
  log "Waiting for API at ${base_url} (harness=${HARNESS} model=${model}) ..."
  local elapsed=0 code
  while (( elapsed < MAX_WAIT_SECS )); do
    code=$(curl -s -o /dev/null -w "%{http_code}" \
      -H "Authorization: Bearer ${api_key}" \
      "${base_url%/}/models" || true)
    if [[ "$code" == "200" ]]; then
      log "API ready."
      return 0
    fi
    log "  not ready (HTTP $code), retry in ${POLL_SECS}s ..."
    sleep "$POLL_SECS"
    elapsed=$((elapsed + POLL_SECS))
  done
  die "API did not become ready within ${MAX_WAIT_SECS}s"
}

# ---- phase 1: local generation --------------------------------------------
generate_one() {
  local track=$1 project=$2 temp=$3 think=$4 slug out
  slug="$(slug_for "$track" "$project" "$temp" "$think")"
  out="$GEN_DIR/$slug"
  if [[ -f "$out/generation.json" ]]; then
    log "gen: $slug already generated — skipping"
    return 0
  fi
  log "gen: $slug (track=$track temp=$temp thinking=$think) -> $out"
  local -a opts=(
    --harness "$HARNESS"
    -o "temperature=$temp"
    -o "enable_thinking=$think"
    -o "max_tokens=$MAX_TOKENS"
    --parallel "$PARALLEL"
    --out "$out"
  )
  case "$track" in
    project)
      [[ "$COMPILE_CHECK" == "true" ]] && opts+=(--compile-check)
      jmhbench project-gen --project "$project" "${opts[@]}" ;;
    synthetic|real|all)
      jmhbench generate --track "$track" "${opts[@]}" ;;
    *) die "unknown track: $track" ;;
  esac
}

# ---- phase 2: benchmarking (one bundle at a time) -------------------------
bench_one() {
  local track=$1 project=$2 temp=$3 think=$4 slug bundle
  slug="$(slug_for "$track" "$project" "$temp" "$think")"
  bundle="$GEN_DIR/$slug"
  if [[ ! -f "$bundle/generation.json" ]]; then
    log "bench: no bundle for $slug — skipping"
    return 0
  fi
  local local_report="$REPORT_DIR/$slug"
  mkdir -p "$local_report"

  # Local mode: this machine is the benchmarking box (e.g. running on the VM).
  if [[ "$REMOTE" == "local" || -z "$REMOTE" ]]; then
    log "bench (local): $slug (track=$track)"
    case "$track" in
      project) jmhbench project-bench "$bundle" --out "$local_report" \
                 --workdir "$RUN_DIR/scratch/$slug" \
                 --preset "$PRESET" \
                 --sensitivity-sample "$SENSITIVITY_SAMPLE" \
                 --max-detect-attempts "$MAX_DETECT_ATTEMPTS" \
                 --mutant-op "$MUTANT_OP" \
                 --mutant-tokens "$MUTANT_TOKENS" \
                 --compiler-heap "$COMPILER_HEAP" ;;
      *)       jmhbench bench "$bundle" --out "$local_report" ;;
    esac
    log "bench: $slug -> $local_report"
    return 0
  fi

  # Remote mode: ship the bundle, benchmark over SSH, pull the report back.
  local remote_run="$REMOTE_BASE/runs/$slug"
  local remote_bundle="$remote_run/bundle"
  local remote_report="$remote_run/report"
  local remote_scratch="$remote_run/scratch"

  log "bench: $slug on $REMOTE (track=$track)"
  ssh "$REMOTE" "mkdir -p $remote_bundle $remote_report"
  rsync -az --delete "$bundle/" "$REMOTE:$remote_bundle/"

  local bench_cmd
  case "$track" in
    project)
      bench_cmd="jmhbench project-bench $remote_bundle --out $remote_report \
        --workdir $remote_scratch --preset $PRESET \
        --sensitivity-sample $SENSITIVITY_SAMPLE \
        --max-detect-attempts $MAX_DETECT_ATTEMPTS \
        --mutant-op $MUTANT_OP --mutant-tokens $MUTANT_TOKENS \
        --compiler-heap $COMPILER_HEAP" ;;
    *)
      bench_cmd="jmhbench bench $remote_bundle --out $remote_report" ;;
  esac

  ssh "$REMOTE" "cd $REMOTE_BASE && source .venv/bin/activate && \
    export JAVA_HOME=$JAVA_HOME_HINT && export PATH=\$JAVA_HOME/bin:\$PATH && \
    $bench_cmd 2>&1 | tee $remote_run/bench.log"

  rsync -az "$REMOTE:$remote_report/" "$local_report/"
  log "bench: $slug -> $local_report"
}

# ---- drive the matrix ------------------------------------------------------
log "run dir: $RUN_DIR"
log "matrix : harness=$HARNESS tracks=[$TRACKS] temps=[$TEMPS] thinking=[$THINKING] parallel=$PARALLEL max_tokens=$MAX_TOKENS"
[[ "$TRACKS" == *project* ]] && log "measure: preset=$PRESET sensitivity-sample=$SENSITIVITY_SAMPLE max-detect-attempts=$MAX_DETECT_ATTEMPTS compiler-heap=$COMPILER_HEAP"
[[ "$TRACKS" == *project* ]] && log "projects: [$PROJECTS]"
log "cells  : $(cells | wc -l | tr -d ' ')"

if [[ -z "$SKIP_GEN" ]]; then
  wait_for_api
  log "=== phase 1: generation (all bundles upfront) ==="
  while IFS=$'\t' read -r track project temp think; do
    if ! generate_one "$track" "$project" "$temp" "$think"; then
      FAILED+=("gen:$(slug_for "$track" "$project" "$temp" "$think")")
      log "gen FAILED: $(slug_for "$track" "$project" "$temp" "$think")"
    fi
  done < <(cells)
else
  log "SKIP_GEN set — using existing bundles in $GEN_DIR"
fi

if [[ -z "$SKIP_BENCH" ]]; then
  if [[ "$REMOTE" == "local" || -z "$REMOTE" ]]; then
    log "=== phase 2: sequential benchmarking (local) ==="
  else
    # Expand a leading ~ so remote paths quote safely in ssh/rsync commands.
    REMOTE_HOME="$(ssh "$REMOTE" 'echo $HOME')"
    REMOTE_BASE="${REMOTE_BASE/#\~/$REMOTE_HOME}"
    log "=== phase 2: sequential remote benchmarking on $REMOTE ($REMOTE_BASE) ==="
  fi
  while IFS=$'\t' read -r track project temp think; do
    if ! bench_one "$track" "$project" "$temp" "$think"; then
      FAILED+=("bench:$(slug_for "$track" "$project" "$temp" "$think")")
      log "bench FAILED: $(slug_for "$track" "$project" "$temp" "$think")"
    fi
  done < <(cells)
else
  log "SKIP_BENCH set — generation only."
fi

# ---- summary ---------------------------------------------------------------
if (( ${#FAILED[@]} )); then
  log "=== finished with ${#FAILED[@]} failure(s): ${FAILED[*]} ==="
  exit 1
fi
log "=== all runs finished; reports in $REPORT_DIR ==="
