#!/usr/bin/env bash
# Sync jmhgen to bsc-gcp and bootstrap the unified mutation-RFT environment.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
REMOTE="${JMHGEN_REMOTE_HOST:-bsc-gcp}"
REMOTE_DIR="${JMHGEN_REMOTE_DIR:-~/jmhgen-rft}"
MANIFEST="${JMHGEN_MUTATION_MANIFEST:-configs/rft/mutation-projects.yaml}"

if [[ -z "${VLLM_API_KEY:-}" ]]; then
  echo "error: set VLLM_API_KEY in the environment before deploying" >&2
  exit 1
fi

echo "==> rsync repo -> ${REMOTE}:${REMOTE_DIR}/"
rsync -az --delete -e ssh \
  --exclude .venv \
  --exclude .git \
  --exclude outputs \
  --exclude __pycache__ \
  --exclude .mypy_cache \
  --exclude .pytest_cache \
  --exclude .ruff_cache \
  --exclude data/llm2jmh-* \
  --exclude data/rxjava-src \
  --exclude 'data/external-src/' \
  --exclude 'data/classpaths/*-mutants.*' \
  --exclude 'data/rft-*' \
  --exclude .env \
  "${ROOT}/" "${REMOTE}:${REMOTE_DIR}/"

echo "==> sync mutation project artifacts from ${MANIFEST}"
ssh "${REMOTE}" "mkdir -p ${REMOTE_DIR}/data/external-src ${REMOTE_DIR}/data/classpaths"
while IFS=$'\t' read -r project_id source classpath_jar make_mutants_id mutation_sites; do
  echo "    -> ${project_id}"
  rsync -az -e ssh "${ROOT}/${source}/" "${REMOTE}:${REMOTE_DIR}/${source}/"
  rsync -az -e ssh \
    "${ROOT}/data/classpaths/${classpath_jar}" \
    "${REMOTE}:${REMOTE_DIR}/data/classpaths/"
  cp_basename="${classpath_jar%.jar}.cp"
  ssh "${REMOTE}" "printf '%s\n' '${classpath_jar}' > ${REMOTE_DIR}/data/classpaths/${cp_basename}"
done < <(
  cd "${ROOT}" && uv run python - "${MANIFEST}" <<'PY'
import sys
from jmhgen.config.mutation_manifest import load_mutation_manifest

manifest = load_mutation_manifest(sys.argv[1])
for project in manifest.projects:
    d = project.deploy
    print(
        "\t".join(
            [
                project.id,
                d.source,
                d.classpath_jar,
                d.make_mutants_id,
                d.mutation_sites,
            ]
        )
    )
PY
)

echo "==> write remote .env (chmod 600)"
ssh "${REMOTE}" "printf 'VLLM_API_KEY=%s\n' '${VLLM_API_KEY}' > ${REMOTE_DIR}/.env && chmod 600 ${REMOTE_DIR}/.env && mkdir -p ${REMOTE_DIR}/logs"

echo "==> install uv + Python deps (dev extra only — no GPU train on CPU VM)"
ssh "${REMOTE}" "bash -lc '
  set -euo pipefail
  export PATH=\"\$HOME/.local/bin:\$PATH\"
  if ! command -v uv >/dev/null 2>&1; then
    curl -LsSf https://astral.sh/uv/install.sh | sh
  fi
  cd ${REMOTE_DIR}
  uv sync --extra dev
'"

echo "==> ensure snippet corpora (gitignored; regenerated on demand)"
ssh "${REMOTE}" "bash -lc '
  set -euo pipefail
  export PATH=\"\$HOME/.local/bin:\$PATH\"
  cd ${REMOTE_DIR}
  while IFS=$\"\\t\" read -r project_id source _jar make_mutants_id mutation_sites; do
    snippets=\"data/projects/\${project_id}/snippets.jsonl\"
    if [[ ! -s \"\${snippets}\" ]]; then
      echo \"regenerating \${snippets}\"
      uv run jmh-make-mutants \"\${make_mutants_id}\" \
        --source \"\${source}\" \
        --sites \"\${mutation_sites}\"
    fi
  done < <(uv run python - \"${MANIFEST}\" <<\"PY\"
import sys
from jmhgen.config.mutation_manifest import load_mutation_manifest

manifest = load_mutation_manifest(sys.argv[1])
for project in manifest.projects:
    d = project.deploy
    print(\"\\t\".join([project.id, d.source, d.classpath_jar, d.make_mutants_id, d.mutation_sites]))
PY
)
'"

echo "==> probe RunPod vLLM from bsc-gcp"
ssh "${REMOTE}" "bash -lc '
  set -a; source ${REMOTE_DIR}/.env; set +a
  curl -fsS -H \"Authorization: Bearer \$VLLM_API_KEY\" \
    https://euepml9hwo0l1y-8000.proxy.runpod.net/v1/models | head -c 200
  echo
'"

echo "Deploy complete. Start the unified job with: scripts/bsc-gcp/start-background.sh"
