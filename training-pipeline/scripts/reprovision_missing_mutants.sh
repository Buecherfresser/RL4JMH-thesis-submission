#!/usr/bin/env bash
# Re-provision patched SUT jars for every RL corpus project that lacks a usable one.
#
# Unlike scripts/repair_failed_mutants.sh this does **not** re-plant mutants: the existing
# mutations.patch / mutants.yaml / snippets.jsonl stay untouched, so the hidden registry keeps
# matching the prompts already in data/snippets/. It only re-runs the build, which is what the
# two fixes below actually changed:
#
#   * jmh-provision-mutants now falls back to data/classpaths/<id>.cp for the javac dependency
#     classpath (previously javac ran with no -cp at all -- the largest cause of corpus loss).
#   * jmhgen.mutation.patch applies with --ignore-whitespace (commit 190faa0), which 10
#     projects' patches need in order to apply at all.
#
# A ".cp" whose entries no longer resolve (a native build can leak paths into a deleted /tmp
# scratch checkout) counts as missing and is retried; the stale file is kept as <id>-mutants.cp.stale.
#
#   ./scripts/reprovision_missing_mutants.sh              # every unusable project
#   ./scripts/reprovision_missing_mutants.sh commons-csv  # just these
set -uo pipefail

cd "$(dirname "$0")/.."
export PATH="${HOME}/.local/bin:${PATH}"
RUN="uv run"
CORPUS="${JMH_RL_CORPUS:-configs/grpo/rl-corpus.yaml}"
CP_DIR="data/classpaths"
PER_PROJECT_TIMEOUT_S="${PER_PROJECT_TIMEOUT_S:-1800}"

# Projects with no usable patched classpath: no .cp, no .jar, or unresolvable entries.
list_unusable() {
  $RUN python - <<'PY'
import os
import re
from pathlib import Path

import yaml

CP = Path("data/classpaths")
corpus = yaml.safe_load(Path(os.environ.get("JMH_RL_CORPUS") or "configs/grpo/rl-corpus.yaml").read_text())
ids = [e["id"] for e in (corpus.get("existing") or [])] + [
    e["id"] for e in (corpus.get("projects") or [])
]

unusable = []
for pid in ids:
    cp = CP / f"{pid}-mutants.cp"
    if not cp.exists() or not (CP / f"{pid}-mutants.jar").exists():
        unusable.append(pid)
        continue
    entries = [e.strip() for e in re.split(r"[:\n]", cp.read_text()) if e.strip()]
    if any(not (CP / e).exists() for e in entries):
        unusable.append(pid)
print(" ".join(unusable))
PY
}

if [[ $# -gt 0 ]]; then
  TARGETS=("$@")
else
  # shellcheck disable=SC2207
  TARGETS=($(list_unusable))
fi

echo "=== reprovision start $(date -Is) ==="
echo "targets (${#TARGETS[@]}): ${TARGETS[*]}"
echo

OK=()
FAILED=()
for id in "${TARGETS[@]}"; do
  echo "======================= [$id] ======================="
  if [[ ! -f "data/projects/${id}/project.yaml" ]]; then
    echo "  skip: no project.yaml"
    FAILED+=("$id")
    continue
  fi

  # Keep a stale classpath around rather than deleting it outright.
  for ext in cp jar; do
    if [[ -f "${CP_DIR}/${id}-mutants.${ext}" ]]; then
      mv "${CP_DIR}/${id}-mutants.${ext}" "${CP_DIR}/${id}-mutants.${ext}.stale"
    fi
  done

  timeout "${PER_PROJECT_TIMEOUT_S}" $RUN jmh-provision-mutants "$id" --build auto
  rc=$?

  if [[ $rc -eq 0 && -f "${CP_DIR}/${id}-mutants.cp" ]]; then
    echo "== [$id] OK =="
    OK+=("$id")
    rm -f "${CP_DIR}/${id}-mutants.cp.stale" "${CP_DIR}/${id}-mutants.jar.stale"
  else
    [[ $rc -eq 124 ]] && echo "== [$id] TIMEOUT after ${PER_PROJECT_TIMEOUT_S}s =="
    echo "== [$id] FAILED (rc=$rc) =="
    FAILED+=("$id")
    # Restore whatever was there before, so a retry starts from the same state.
    for ext in cp jar; do
      if [[ -f "${CP_DIR}/${id}-mutants.${ext}.stale" ]]; then
        mv "${CP_DIR}/${id}-mutants.${ext}.stale" "${CP_DIR}/${id}-mutants.${ext}"
      fi
    done
  fi
  echo
done

echo "=== reprovision summary $(date -Is) ==="
echo "recovered (${#OK[@]}): ${OK[*]:-}"
echo "still failing (${#FAILED[@]}): ${FAILED[*]:-}"
echo "total mutant cps: $(ls "${CP_DIR}"/*-mutants.cp 2>/dev/null | wc -l)"

$RUN python scripts/build_mutation_corpus.py || true
