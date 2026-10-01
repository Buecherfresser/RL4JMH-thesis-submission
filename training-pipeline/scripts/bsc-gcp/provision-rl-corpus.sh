#!/usr/bin/env bash
# Run GRPO RL corpus mutant provisioning on bsc-gcp (CPU VM).
#
# Mutants-first strategy (the path mutation GRPO actually needs):
#   1. rsync repo + dependency jars for the few projects that need them
#   2. prepare_rl_sources.py  — clone all + rebuild *-merged trees on the VM
#   3. provision_rl_mutants.sh — javac or Maven/Gradle patched jars (continues on failure)
#   4. sync_grpo_corpus_configs.py — point YAML at projects that have *-mutants.cp
#
#   scripts/bsc-gcp/provision-rl-corpus.sh              # full mutants pass
#   scripts/bsc-gcp/provision-rl-corpus.sh --smoke      # 3-project smoke test only
#   scripts/bsc-gcp/provision-rl-corpus.sh --status
#   scripts/bsc-gcp/provision-rl-corpus.sh --pull-only
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
REMOTE="${JMHGEN_REMOTE_HOST:-bsc-gcp}"
REMOTE_DIR="${JMHGEN_REMOTE_DIR:-~/jmhgen-rft}"
REMOTE_LOG="${REMOTE_DIR}/logs/provision-rl-corpus.log"
REMOTE_PID="${REMOTE_DIR}/logs/provision-rl-corpus.pid"
SMOKE_IDS="commons-codec,hdrhistogram,jheaps,hash4j,xz-java,json-java,flatbuffers"

MODE="${1:-run}"

ssh_remote() {
  ssh "${REMOTE}" "bash -lc $(printf '%q' "$1")"
}

case "$MODE" in
  --status|status)
    ssh_remote "
      if [[ -f ${REMOTE_PID} ]] && kill -0 \"\$(cat ${REMOTE_PID})\" 2>/dev/null; then
        echo \"RUNNING pid=\$(cat ${REMOTE_PID})\"
      else
        echo \"NOT RUNNING (pid file missing or stale)\"
      fi
      echo '---- log tail ----'
      tail -n 50 ${REMOTE_LOG} 2>/dev/null || echo '(no log yet)'
      echo '---- mutant cps ----'
      ls ${REMOTE_DIR}/data/classpaths/*-mutants.cp 2>/dev/null | wc -l
    "
    exit 0
    ;;
  --pull-only|pull)
    echo "==> pull classpaths + snippets + configs from ${REMOTE}"
    mkdir -p "${ROOT}/data/classpaths" "${ROOT}/data/snippets" "${ROOT}/data/projects"
    rsync -az -e ssh \
      --include='*/' \
      --include='*.cp' \
      --include='*-mutants.jar' \
      --include='lib/***' \
      --exclude='*' \
      "${REMOTE}:${REMOTE_DIR}/data/classpaths/" "${ROOT}/data/classpaths/"
    rsync -az -e ssh \
      "${REMOTE}:${REMOTE_DIR}/data/snippets/" "${ROOT}/data/snippets/" || true
    rsync -az -e ssh \
      "${REMOTE}:${REMOTE_DIR}/configs/grpo/" "${ROOT}/configs/grpo/"
    echo "pull complete"
    exit 0
    ;;
  --smoke|smoke)
    MODE=smoke
    ;;
  run|"")
    MODE=run
    ;;
  *)
    echo "usage: $0 [--status|--pull-only|--smoke]" >&2
    exit 2
    ;;
esac

echo "==> rsync repo -> ${REMOTE}:${REMOTE_DIR}/"
rsync -az --delete -e ssh \
  --exclude .venv \
  --exclude .git \
  --exclude outputs \
  --exclude __pycache__ \
  --exclude .mypy_cache \
  --exclude .pytest_cache \
  --exclude .ruff_cache \
  --exclude 'data/llm2jmh-*' \
  --exclude data/rxjava-src \
  --exclude 'data/external-src/' \
  --exclude 'data/classpaths/' \
  --exclude 'data/rft-*' \
  --exclude .env \
  "${ROOT}/" "${REMOTE}:${REMOTE_DIR}/"

echo "==> sync dependency jars needed by a few mutant builds"
ssh_remote "mkdir -p ${REMOTE_DIR}/data/classpaths/lib"
for proj in commons-text commons-statistics jackson-core; do
  if [[ -d "${ROOT}/data/classpaths/lib/${proj}" ]]; then
    rsync -az -e ssh \
      "${ROOT}/data/classpaths/lib/${proj}/" \
      "${REMOTE}:${REMOTE_DIR}/data/classpaths/lib/${proj}/"
  fi
done
# Also sync any existing local mutant jars so remote can skip them
if ls "${ROOT}/data/classpaths/"*-mutants.cp >/dev/null 2>&1; then
  rsync -az -e ssh \
    "${ROOT}/data/classpaths/"*-mutants.cp \
    "${ROOT}/data/classpaths/"*-mutants.jar \
    "${REMOTE}:${REMOTE_DIR}/data/classpaths/" 2>/dev/null || true
fi

echo "==> bootstrap remote toolchain"
ssh_remote "
  set -euo pipefail
  export PATH=\"\$HOME/.local/bin:\$PATH\"
  if ! command -v uv >/dev/null 2>&1; then
    curl -LsSf https://astral.sh/uv/install.sh | sh
  fi
  cd ${REMOTE_DIR}
  mkdir -p logs data/external-src data/classpaths data/snippets data/projects
  uv sync --extra dev
  java -version
"

if [[ "$MODE" == "smoke" ]]; then
  echo "==> SMOKE: prepare + mutants for ${SMOKE_IDS}"
  ssh_remote "
    set -euo pipefail
    export PATH=\"\$HOME/.local/bin:\$PATH\"
    cd ${REMOTE_DIR}
    IFS=',' read -r -a IDS <<< '${SMOKE_IDS}'
    for id in \"\${IDS[@]}\"; do
      rm -f data/classpaths/\${id}-mutants.cp data/classpaths/\${id}-mutants.jar
    done
    uv run python scripts/prepare_rl_sources.py --only ${SMOKE_IDS}
    ./scripts/provision_rl_mutants.sh \"\${IDS[@]}\"
    echo 'SMOKE_OK'
    ls data/classpaths/*-mutants.cp | wc -l
  "
  echo "Smoke test passed. Re-run without --smoke for the full corpus."
  exit 0
fi

echo "==> start remote mutants-first provision in background"
ssh_remote "
  set -euo pipefail
  export PATH=\"\$HOME/.local/bin:\$PATH\"
  cd ${REMOTE_DIR}
  if [[ -f ${REMOTE_PID} ]] && kill -0 \"\$(cat ${REMOTE_PID})\" 2>/dev/null; then
    echo \"already running pid=\$(cat ${REMOTE_PID})\"
    exit 0
  fi
  nohup bash -lc '
    set -euo pipefail
    export PATH=\"\$HOME/.local/bin:\$PATH\"
    cd ${REMOTE_DIR}
    echo \"=== prepare_rl_sources \$(date -Is) ===\"
    uv run python scripts/prepare_rl_sources.py || {
      echo \"WARNING: prepare_rl_sources exited \$? — continuing with whatever sources exist\"
    }
    echo \"=== provision_rl_mutants \$(date -Is) ===\"
    ./scripts/provision_rl_mutants.sh || {
      echo \"WARNING: provision_rl_mutants exited \$? — syncing whatever succeeded\"
    }
    echo \"=== sync_grpo_corpus_configs \$(date -Is) ===\"
    uv run python scripts/sync_grpo_corpus_configs.py || true
    echo \"=== mutant cp count ===\"
    ls data/classpaths/*-mutants.cp 2>/dev/null | wc -l
    echo \"=== DONE \$(date -Is) ===\"
  ' > ${REMOTE_LOG} 2>&1 &
  echo \$! > ${REMOTE_PID}
  echo \"started pid=\$(cat ${REMOTE_PID}) log=${REMOTE_LOG}\"
"

echo
echo "Remote mutants-first job started on ${REMOTE}."
echo "  Status:  scripts/bsc-gcp/provision-rl-corpus.sh --status"
echo "  Pull:    scripts/bsc-gcp/provision-rl-corpus.sh --pull-only"
echo "  Log:     ssh ${REMOTE} tail -f ${REMOTE_LOG}"
