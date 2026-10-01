#!/usr/bin/env bash
# Provision the GRPO (online RL) prompt corpus for every library in
# configs/grpo/rl-corpus.yaml: extract snippets, build SUT classpaths, curate good classes,
# then merge into data/snippets/rl-merged.good.jsonl.
#
# Needs: git, a JDK, Maven (Gradle for some projects), and network on first run.
# Plant mutants first via scripts/bootstrap_rl_corpus.py so good_classes.yaml exists.
#
#   ./scripts/provision_rl_projects.sh                 # all projects with good_classes.yaml
#   ./scripts/provision_rl_projects.sh commons-codec   # a single project
#
# Projects whose SUT build fails are skipped (WARNING) so one broken library does not abort
# the whole corpus merge. Snippet curation can still succeed without a classpath.
set -euo pipefail

cd "$(dirname "$0")/.."

RUN="uv run"
SNIPPETS_DIR="data/snippets"
CP_DIR="data/classpaths"
PROJECTS_DIR="data/projects"
# Both overridable: a candidate-tier run must not read the training corpus nor rewrite the
# shared merge target (parallel array tasks would race on it).
CORPUS="${JMH_RL_CORPUS:-configs/grpo/rl-corpus.yaml}"
MERGED="${JMH_RL_MERGED:-${SNIPPETS_DIR}/rl-merged.good.jsonl}"

# Release 17, not 11. Forcing 11 *downgrades* the compiler and breaks any library using a
# post-11 language feature -- parsii fails with "pattern matching in instanceof is not
# supported in -source 11", and it is a common failure across the candidate tiers. 17 matches
# runner.java_release and the class-file ceiling the corpus targets.
MVN_BUILD='mvn -q -DskipTests package dependency:copy-dependencies -DincludeScope=runtime -Dmaven.compiler.release=17 -Dmaven.compiler.source=17 -Dmaven.compiler.target=17'
# Second attempt with no compiler override at all: projects that need a newer release than 17,
# or that pin their own source/target in the POM, build correctly on their own terms.
MVN_BUILD_ASIS='mvn -q -DskipTests package dependency:copy-dependencies -DincludeScope=runtime'
MVN_GLOBS=(--jar-glob '**/target/*.jar' --jar-glob '**/target/dependency/*.jar')
GRADLE_BUILD='./gradlew jar -x test'
GRADLE_GLOBS=(--jar-glob '**/build/libs/*.jar')

corpus_field() {  # <id> <field>
  local id="$1" field="$2"
  $RUN python -c "
import yaml
from pathlib import Path
c = yaml.safe_load(Path('${CORPUS}').read_text())
entries = list(c.get('existing') or []) + list(c.get('projects') or [])
for e in entries:
    if e['id'] == '${id}':
        v = e.get('${field}')
        print('' if v is None else v)
        break
else:
    raise SystemExit('unknown project: ${id}')
"
}

list_all_ids() {
  $RUN python -c "
import yaml
from pathlib import Path
c = yaml.safe_load(Path('${CORPUS}').read_text())
ids = [e['id'] for e in (c.get('existing') or [])] + [e['id'] for e in (c.get('projects') or [])]
print(' '.join(ids))
"
}

detect_build() {  # <source-dir> -> maven|gradle|none
  local src="$1"
  local forced="${2:-}"
  if [[ "$forced" == "gradle" || "$forced" == "maven" ]]; then
    echo "$forced"
    return
  fi
  if [[ -f "$src/pom.xml" ]]; then
    echo maven
  elif [[ -f "$src/build.gradle" || -f "$src/build.gradle.kts" || -f "$src/settings.gradle" || -f "$src/settings.gradle.kts" ]]; then
    echo gradle
  else
    echo none
  fi
}

bundle_cp() {
  local id="$1"
  if [[ ! -f "${CP_DIR}/${id}.cp" ]]; then
    echo "== [$id] skip bundle (no ${CP_DIR}/${id}.cp) =="
    return 1
  fi
  echo "== [$id] bundle classpath jars =="
  $RUN python -c \
    "from jmhgen.data.classpath import bundle_classpath as b; b('${CP_DIR}/${id}.cp', '${CP_DIR}/lib/${id}')"
  echo "  bundled $(wc -l < "${CP_DIR}/${id}.cp" | tr -d ' ') jar(s) -> ${CP_DIR}/lib/${id}"
}

curate() {
  local id="$1"
  local good="${PROJECTS_DIR}/${id}/good_classes.yaml"
  if [[ ! -f "$good" ]]; then
    echo "== [$id] skip curate (no good_classes.yaml) =="
    return 0
  fi
  if [[ ! -f "${SNIPPETS_DIR}/${id}.jsonl" ]]; then
    echo "== [$id] skip curate (no snippets jsonl) =="
    return 0
  fi
  echo "== [$id] curate good classes =="
  $RUN jmh-curate-snippets \
    --snippets "${SNIPPETS_DIR}/${id}.jsonl" \
    --good-classes "$good" \
    --output "${SNIPPETS_DIR}/${id}.good.jsonl"
}

extract_snippets() {
  local id="$1" url="$2" ref="$3" pkg="$4"
  echo "== [$id] extract snippets =="
  if [[ -f "${SNIPPETS_DIR}/${id}.jsonl" ]]; then
    echo "  cached ${SNIPPETS_DIR}/${id}.jsonl"
    return 0
  fi
  local ref_args=()
  [[ -n "$ref" ]] && ref_args=(--ref "$ref")
  $RUN jmh-extract-snippets "$id" --url "$url" "${ref_args[@]}" --include-package "$pkg"
}

provision_classpath() {
  local id="$1" build="$2"
  echo "== [$id] provision classpath ($build) =="
  if [[ -f "${CP_DIR}/${id}.cp" ]]; then
    echo "  cached ${CP_DIR}/${id}.cp"
    return 0
  fi
  case "$build" in
    maven)
      if $RUN jmh-provision-classpath "$id" \
        --source "data/external-src/${id}" \
        --build-cmd "$MVN_BUILD" \
        "${MVN_GLOBS[@]}"; then
        return 0
      fi
      echo "== [$id] retrying maven with the project's own compiler settings =="
      $RUN jmh-provision-classpath "$id" \
        --source "data/external-src/${id}" \
        --build-cmd "$MVN_BUILD_ASIS" \
        "${MVN_GLOBS[@]}"
      ;;
    gradle)
      local cmd="$GRADLE_BUILD"
      if [[ "$id" == "roaringbitmap" ]]; then
        cmd='./gradlew :RoaringBitmap:jar'
      fi
      $RUN jmh-provision-classpath "$id" \
        --source "data/external-src/${id}" \
        --build-cmd "$cmd" \
        "${GRADLE_GLOBS[@]}"
      ;;
    *)
      echo "== [$id] ERROR: no pom.xml / build.gradle under data/external-src/${id} ==" >&2
      return 1
      ;;
  esac
}

provision_one() {
  local id="$1"
  local github ref pkg build_field build src
  github="$(corpus_field "$id" github)"
  ref="$(corpus_field "$id" ref)"
  pkg="$(corpus_field "$id" package_root)"
  build_field="$(corpus_field "$id" build)"
  if [[ -z "$pkg" && -f "${PROJECTS_DIR}/${id}/project.yaml" ]]; then
    # Candidate-tier corpora (scripts/build_tier_corpus.py) carry only id + github: the
    # spreadsheet cannot know a package root. bootstrap_rl_corpus.py detects it from the
    # checkout and records it here, so prefer that over refusing to provision.
    pkg="$($RUN python -c "
import yaml
from pathlib import Path
d = yaml.safe_load(Path('${PROJECTS_DIR}/${id}/project.yaml').read_text()) or {}
print(d.get('package_root') or '')
")"
  fi
  if [[ -z "$github" || -z "$pkg" ]]; then
    echo "unknown or incomplete corpus entry: $id" >&2
    return 2
  fi

  # jsr166 is planted from the local JDK src.zip — no git checkout / Maven build.
  if [[ "$(corpus_field "$id" source_style)" == "jdk_srczip" ]]; then
    echo "== [$id] skip classpath build (jdk_srczip; mutants jar comes from provision_rl_mutants.sh) =="
    curate "$id" || true
    return 0
  fi

  src="data/external-src/${id}"
  if [[ ! -d "$src" ]]; then
    echo "== [$id] ERROR: missing checkout $src (run bootstrap_rl_corpus.py) ==" >&2
    return 1
  fi

  extract_snippets "$id" "https://github.com/${github}.git" "$ref" "$pkg"
  build="$(detect_build "$src" "$build_field")"
  if ! provision_classpath "$id" "$build"; then
    echo "== [$id] WARNING: classpath build failed ==" >&2
    curate "$id" || true
    return 1
  fi
  bundle_cp "$id"
  curate "$id"
}

if [[ $# -gt 0 ]]; then
  TARGETS=("$@")
else
  # shellcheck disable=SC2207
  TARGETS=($(list_all_ids))
fi

FAILED=()
for t in "${TARGETS[@]}"; do
  if [[ ! -f "${PROJECTS_DIR}/${t}/good_classes.yaml" ]]; then
    echo "== [$t] skip (no good_classes.yaml; run bootstrap_rl_corpus.py first) =="
    continue
  fi
  # Do NOT use `cmd || echo` here: that disables set -e inside functions (bash gotcha),
  # which previously let bundle_cp run after a failed Maven build.
  set +e
  provision_one "$t"
  rc=$?
  set -e
  if [[ $rc -ne 0 ]]; then
    FAILED+=("$t")
    echo "== [$t] WARNING: provision failed (rc=$rc); continuing ==" >&2
  fi
done

echo "== merge curated corpora -> ${MERGED} =="
: > "$MERGED"
# shellcheck disable=SC2207
ALL=($(list_all_ids))
for t in "${ALL[@]}"; do
  good="${SNIPPETS_DIR}/${t}.good.jsonl"
  if [[ -f "$good" ]]; then
    cat "$good" >> "$MERGED"
    echo "  + $(wc -l < "$good" | tr -d ' ') from ${t}"
  fi
done
echo "merged prompt corpus: $(wc -l < "$MERGED" | tr -d ' ') snippets -> ${MERGED}"

if [[ ${#FAILED[@]} -gt 0 ]]; then
  echo "classpath build failures (${#FAILED[@]}): ${FAILED[*]}" >&2
  exit 1
fi
