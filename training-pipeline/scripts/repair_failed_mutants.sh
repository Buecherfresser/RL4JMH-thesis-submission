#!/usr/bin/env bash
# Replant + provision only projects that lack a *-mutants.cp (skip successes).
#
# Tries progressively smaller mutant counts and JDK 11 then 21 for Gradle-era builds.
#
#   ./scripts/repair_failed_mutants.sh
#   ./scripts/repair_failed_mutants.sh agrona trove netty
set -euo pipefail

cd "$(dirname "$0")/.."
export PATH="${HOME}/.local/bin:${PATH}"
RUN="uv run"
CORPUS="configs/grpo/rl-corpus.yaml"
CP_DIR="data/classpaths"
LOG_DIR="logs"
mkdir -p "$LOG_DIR" "$CP_DIR"

JAVA8="${HOME}/.local/jdk/jdk8"
JAVA11="/usr/lib/jvm/java-11-openjdk-amd64"
JAVA21="/usr/lib/jvm/java-21-openjdk-amd64"
[[ -x "${JAVA8}/bin/java" ]] || JAVA8=""
[[ -d "$JAVA11" ]] || JAVA11=""
[[ -d "$JAVA21" ]] || JAVA21="${JAVA_HOME:-}"

COUNTS=(50 30 15 8)

list_missing() {
  $RUN python -c "
import yaml
from pathlib import Path
c = yaml.safe_load(Path('${CORPUS}').read_text())
ids = [e['id'] for e in (c.get('existing') or [])] + [e['id'] for e in (c.get('projects') or [])]
cp = Path('${CP_DIR}')
missing = [i for i in ids if not (cp / f'{i}-mutants.cp').exists()]
print(' '.join(missing))
"
}

pkg_root_for() {
  local id="$1"
  $RUN python -c "
import yaml
from pathlib import Path
print(yaml.safe_load(Path('data/projects/${id}/project.yaml').read_text())['package_root'])
"
}

source_dir_for() {
  local id="$1"
  $RUN python -c "
import yaml
from pathlib import Path
print(yaml.safe_load(Path('data/projects/${id}/project.yaml').read_text())['source_dir'])
"
}

prefer_checkout_source() {
  # For native builds, prefer planting against the real module tree when merged patches flake.
  local id="$1"
  local checkout="data/external-src/${id}"
  local merged
  merged="$(source_dir_for "$id")"
  if [[ -d "$checkout" ]]; then
    # Prefer guava-style or standard src/main/java under the primary module folder.
    if [[ -d "$checkout/${id}/src/main/java" ]]; then
      echo "$checkout/${id}/src/main/java"
      return
    fi
    if [[ -d "$checkout/src/main/java" ]]; then
      echo "$checkout/src/main/java"
      return
    fi
    # Guava flat src
    if [[ -d "$checkout/${id}/src" ]] && [[ ! -d "$checkout/${id}/src/main" ]]; then
      echo "$checkout/${id}/src"
      return
    fi
  fi
  echo "$merged"
}

try_provision() {
  local id="$1"
  local java_home="${2:-}"
  if [[ -n "$java_home" && -x "$java_home/bin/java" ]]; then
    echo "  JAVA_HOME=$java_home"
    env JAVA_HOME="$java_home" PATH="$java_home/bin:$PATH" \
      $RUN jmh-provision-mutants "$id" --build auto && return 0
  else
    $RUN jmh-provision-mutants "$id" --build auto && return 0
  fi
  return 1
}

repair_one() {
  local id="$1"
  local out_cp="${CP_DIR}/${id}-mutants.cp"
  if [[ -f "$out_cp" ]]; then
    echo "== [$id] skip (already have ${out_cp}) =="
    return 0
  fi
  if [[ ! -f "data/projects/${id}/project.yaml" ]]; then
    echo "== [$id] ERROR: no project.yaml ==" >&2
    return 1
  fi

  echo "== [$id] prepare sources =="
  $RUN python scripts/prepare_rl_sources.py --only "$id" || true

  local src pkg
  src="$(prefer_checkout_source "$id")"
  pkg="$(pkg_root_for "$id")"
  if [[ ! -d "$src" ]]; then
    src="$(source_dir_for "$id")"
  fi
  if [[ ! -d "$src" ]]; then
    echo "== [$id] ERROR: no source tree ==" >&2
    return 1
  fi
  echo "  source=$src package_root=$pkg"

  # Point project.yaml at the source we actually plant against.
  $RUN python -c "
from pathlib import Path
import yaml
p = Path('data/projects/${id}/project.yaml')
d = yaml.safe_load(p.read_text()) or {}
d['source_dir'] = '${src}'
p.write_text(yaml.safe_dump(d, sort_keys=False), encoding='utf-8')
"

  local count
  for count in "${COUNTS[@]}"; do
    echo "== [$id] plant count=${count} =="
    rm -f "data/projects/${id}/mutations.patch"
    if ! $RUN jmh-make-mutants "$id" --source "$src" \
      --package-root "$pkg" --count "$count" --allow-partial; then
      echo "  plant failed at count=$count"
      continue
    fi

    # JDK8 first (ancient sources using '_' as a name), then 11, then 21.
    if [[ -n "$JAVA8" ]] && try_provision "$id" "$JAVA8"; then
      echo "== [$id] OK (jdk8, count=$count) =="
      return 0
    fi
    if [[ -n "$JAVA11" ]] && try_provision "$id" "$JAVA11"; then
      echo "== [$id] OK (jdk11, count=$count) =="
      return 0
    fi
    if [[ -n "$JAVA21" ]] && try_provision "$id" "$JAVA21"; then
      echo "== [$id] OK (jdk21, count=$count) =="
      return 0
    fi
    if try_provision "$id" ""; then
      echo "== [$id] OK (default jdk, count=$count) =="
      return 0
    fi
    echo "  provision failed at count=$count; trying fewer mutants"
  done

  echo "== [$id] FAILED after all attempts ==" >&2
  return 1
}

if [[ $# -gt 0 ]]; then
  TARGETS=("$@")
else
  # shellcheck disable=SC2207
  TARGETS=($(list_missing))
fi

echo "repair targets (${#TARGETS[@]}): ${TARGETS[*]}"
ok=0
fail=0
failed_ids=()
for id in "${TARGETS[@]}"; do
  if repair_one "$id"; then
    ok=$((ok + 1))
  else
    fail=$((fail + 1))
    failed_ids+=("$id")
  fi
done

echo "repair summary: ok=$ok failed=$fail"
if ((${#failed_ids[@]})); then
  echo "still failing: ${failed_ids[*]}"
fi
ls "${CP_DIR}"/*-mutants.cp 2>/dev/null | wc -l | awk '{print "total mutant cps:", $1}'

$RUN python scripts/sync_grpo_corpus_configs.py || true
