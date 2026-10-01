#!/usr/bin/env bash
# Plant performance mutants for every GRPO RL corpus project and build patched SUT jars.
#
# Reads configs/grpo/rl-corpus.yaml (existing + projects). For each id that has
# data/projects/<id>/mutation_sites.yaml + project.yaml, runs jmh-make-mutants --sites and
# jmh-provision-mutants (plain javac — does not need a Maven/Gradle SUT build).
#
# Source trees must already exist (see scripts/prepare_rl_sources.py).
#
#   ./scripts/provision_rl_mutants.sh                 # all corpus projects with sites
#   ./scripts/provision_rl_mutants.sh commons-codec   # one project
#   KEEP_PATCHES=1 ./scripts/provision_rl_mutants.sh  # build the committed patches as-is
#
# Re-planting from mutation_sites.yaml is not stable across generator versions: on the pinned
# sources it moves or drops mutants in some projects (objectlayout plants 44 of 50). The
# committed mutations.patch + mutants.yaml are the set a training run actually used, so
# KEEP_PATCHES=1 skips jmh-make-mutants wherever both already exist.
#
# Continues on per-project failure (does not abort the whole corpus).
set -euo pipefail

cd "$(dirname "$0")/.."
RUN="uv run"
CORPUS="${JMH_RL_CORPUS:-configs/grpo/rl-corpus.yaml}"
CP_DIR="data/classpaths"

list_all_ids() {
  $RUN python -c "
import yaml
from pathlib import Path
c = yaml.safe_load(Path('${CORPUS}').read_text())
ids = [e['id'] for e in (c.get('existing') or [])] + [e['id'] for e in (c.get('projects') or [])]
print(' '.join(ids))
"
}

source_dir_for() {
  local id="$1"
  $RUN python -c "
import yaml
from pathlib import Path
p = yaml.safe_load(Path('data/projects/${id}/project.yaml').read_text())
print(p['source_dir'])
"
}

make_mutants() {
  local id="$1" src="$2"
  echo "== [$id] make mutants =="
  if $RUN jmh-make-mutants "$id" --source "$src" \
    --sites "data/projects/$id/mutation_sites.yaml"; then
    return 0
  fi
  echo "== [$id] curated sites failed; re-planting heuristically ==" >&2
  local pkg
  pkg="$($RUN python -c "
import yaml
from pathlib import Path
print(yaml.safe_load(Path('data/projects/${id}/project.yaml').read_text())['package_root'])
")"
  $RUN jmh-make-mutants "$id" --source "$src" \
    --package-root "$pkg" --count 50 --allow-partial
}

provision_mutants() {
  local id="$1"
  echo "== [$id] provision patched SUT jar =="
  # Prefer native Maven/Gradle when available; auto falls back from javac.
  $RUN jmh-provision-mutants "$id" --build auto
}

provision_one() {
  local id="$1"
  local sites="data/projects/${id}/mutation_sites.yaml"
  local meta="data/projects/${id}/project.yaml"
  local out_cp="${CP_DIR}/${id}-mutants.cp"

  if [[ ! -f "$sites" ]]; then
    echo "== [$id] skip (no mutation_sites.yaml) =="
    return 0
  fi
  if [[ ! -f "$meta" ]]; then
    echo "== [$id] ERROR: missing project.yaml ==" >&2
    return 1
  fi
  if [[ -f "$out_cp" ]]; then
    echo "== [$id] skip (cached ${out_cp}) =="
    return 0
  fi

  local src
  src="$(source_dir_for "$id")"
  if [[ ! -d "$src" ]]; then
    echo "== [$id] ERROR: source_dir missing: $src (run prepare_rl_sources.py) ==" >&2
    return 1
  fi

  if [[ "${KEEP_PATCHES:-0}" == "1" && -f "data/projects/${id}/mutations.patch" \
        && -f "data/projects/${id}/mutants.yaml" ]]; then
    echo "== [$id] keeping committed mutations.patch =="
  else
    make_mutants "$id" "$src"
  fi
  provision_mutants "$id"
}

if [[ $# -gt 0 ]]; then
  TARGETS=("$@")
else
  # shellcheck disable=SC2207
  TARGETS=($(list_all_ids))
fi

FAILED=()
OK=0
SKIPPED=0
for t in "${TARGETS[@]}"; do
  set +e
  provision_one "$t"
  rc=$?
  set -e
  if [[ $rc -ne 0 ]]; then
    FAILED+=("$t")
    echo "== [$t] WARNING: mutant provision failed (rc=$rc); continuing ==" >&2
  elif [[ -f "${CP_DIR}/${t}-mutants.cp" ]]; then
    OK=$((OK + 1))
  else
    SKIPPED=$((SKIPPED + 1))
  fi
done

echo "RL mutation classpaths: ok=${OK} skipped=${SKIPPED} failed=${#FAILED[@]}"
if [[ ${#FAILED[@]} -gt 0 ]]; then
  echo "mutant provision failures (${#FAILED[@]}): ${FAILED[*]}" >&2
  exit 1
fi
echo "RL mutation classpaths ready (point configs/grpo/jmh-rl-full.yaml project_classpaths here)."
