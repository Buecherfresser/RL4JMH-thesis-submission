#!/usr/bin/env bash
# Overnight provisioning of the Tier A candidate projects on lrz-cpu.
#
# Input:  configs/grpo/rl-corpus-tierA.yaml (282 unverified repos, scripts/build_tier_corpus.py)
# Output: for each project that survives -- data/projects/<id>/{project,mutants,good_classes}.yaml,
#         data/classpaths/<id>.cp and <id>-mutants.cp, data/snippets/<id>.good.jsonl.
#
# Nothing here touches configs/grpo/rl-corpus.yaml or the GRPO training configs. Tier A is a
# paper judgement -- none of these repos has ever been cloned or built -- so a project only
# earns promotion into the training corpus once it has a working mutant classpath. Promote with
#   uv run python scripts/promote_tier_projects.py --dry-run
#
# JDK 17, same policy as provision-corpus-lrz-cpu.sbatch: every SUT jar this produces stays at
# class-file major <= 61 so any javac >= 17 can link against it. Gradle builds that declare
# their own toolchain can still override this -- the promote script re-checks the ceiling.
#
# Array, not one job: 282 projects x (clone + plant + Maven build) does not fit a night in
# sequence. Each task owns a disjoint slice and its own Maven local repository, because
# concurrent writes to one ~/.m2 corrupt artifacts.
#
#   sbatch scripts/cluster/prepare-tierA-lrz-cpu.sbatch
#   sbatch --array=0-5%3 scripts/cluster/prepare-tierA-lrz-cpu.sbatch   # throttle to 3 at once
#SBATCH --job-name=jmh-tierA
#SBATCH --partition=lrz-cpu
#SBATCH --qos=cpu
#SBATCH --array=0-5
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=32G
#SBATCH --time=16:00:00
#SBATCH --output=/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh/logs/tierA_%A_%a.out
#SBATCH --error=/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh/logs/tierA_%A_%a.out

set -uo pipefail
export PYTHONUNBUFFERED=1

JMH_ROOT="${JMH_ROOT:-/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh}"
REPO="${REPO:-${JMH_ROOT}/JMH_Training_Pipeline}"
cd "${REPO}" || exit 1
# shellcheck source=/dev/null
source "${REPO}/scripts/cluster/env.lrz.sh"

export JAVA_HOME="${JMH_ROOT}/tools/jdk-17"
export PATH="${JAVA_HOME}/bin:${JMH_ROOT}/tools/maven/bin:${HOME}/.local/bin:${PATH}"
export UV_PROJECT_ENVIRONMENT="${JMH_ROOT}/.venv-gemma-cu130"
export UV_NO_SYNC=1
export UV_OFFLINE=1
PY="${UV_PROJECT_ENVIRONMENT}/bin/python"

TASK="${SLURM_ARRAY_TASK_ID:-0}"
NTASKS="${JMH_TIERA_SHARDS:-6}"
export JMH_RL_CORPUS="configs/grpo/rl-corpus-tierA.yaml"
# Per-shard so parallel tasks never rewrite the same file. Merged into the real corpus only
# at promotion time.
export JMH_RL_MERGED="data/snippets/tierA-shard${TASK}.merged.jsonl"
# Per-shard Maven repository: one shared ~/.m2 under six concurrent writers corrupts artifacts.
export MAVEN_OPTS="-Dmaven.repo.local=${JMH_ROOT}/.m2-tierA/shard${TASK}"
mkdir -p "${JMH_ROOT}/.m2-tierA/shard${TASK}"
export PER_PROJECT_TIMEOUT_S=900

LOGDIR="${JMH_ROOT}/logs/tierA_${SLURM_ARRAY_JOB_ID:-0}"
mkdir -p "${LOGDIR}"

echo "=== node=$(hostname) job=${SLURM_ARRAY_JOB_ID:-?} shard=${TASK}/${NTASKS} start=$(date -Is) ==="
javac -version 2>&1
echo "corpus=${JMH_RL_CORPUS}  m2=${JMH_ROOT}/.m2-tierA/shard${TASK}"

# Interleaved slice (i % NTASKS): the corpus is ordered cheapest-first, so contiguous blocks
# would hand one shard every large project.
IDS=$("${PY}" - <<PYEOF
import yaml
from pathlib import Path
c = yaml.safe_load(Path("${JMH_RL_CORPUS}").read_text())
ids = [e["id"] for e in (c.get("projects") or [])]
print(" ".join(ids[${TASK}::${NTASKS}]))
PYEOF
)
N=$(echo "${IDS}" | wc -w)
echo "shard ${TASK}: ${N} project(s)"
if [[ -z "${IDS// /}" ]]; then echo "nothing to do"; exit 0; fi

# --- 1. clone + plant 50 mutants -------------------------------------------------------
# Resumable: bootstrap skips a project that already has mutation_sites.yaml.
echo; echo "###### shard ${TASK}: BOOTSTRAP ######"
IDS_CSV=$(echo "${IDS}" | tr ' ' ',')
timeout 7h "${PY}" scripts/bootstrap_rl_corpus.py --only "${IDS_CSV}" 2>&1 \
  | tee "${LOGDIR}/shard${TASK}-1-bootstrap.log" \
  | grep -E "^== \[|ERROR|planted|ok=|failed=|summary" | tail -150
echo "BOOTSTRAP_RC=${PIPESTATUS[0]}"

# --- 2. snippets + dependency classpaths ------------------------------------------------
echo; echo "###### shard ${TASK}: CLASSPATHS ######"
# shellcheck disable=SC2086
timeout 6h ./scripts/provision_rl_projects.sh ${IDS} 2>&1 \
  | tee "${LOGDIR}/shard${TASK}-2-classpaths.log" \
  | grep -E "^== \[|WARNING|failures" | tail -150
echo "CLASSPATH_RC=${PIPESTATUS[0]}"

# --- 3. patched SUT jars -----------------------------------------------------------------
echo; echo "###### shard ${TASK}: MUTANT JARS ######"
# shellcheck disable=SC2086
timeout 5h ./scripts/reprovision_missing_mutants.sh ${IDS} 2>&1 \
  | tee "${LOGDIR}/shard${TASK}-3-mutants.log" \
  | grep -E "^== \[|^recovered|^still failing|^total" | tail -150
echo "MUTANT_RC=${PIPESTATUS[0]}"

# --- 4. shard summary ---------------------------------------------------------------------
echo; echo "###### shard ${TASK}: SUMMARY ######"
"${PY}" - <<PYEOF
import json, struct, zipfile
from pathlib import Path
ids = "${IDS}".split()
CP, PROJ, SNIP = Path("data/classpaths"), Path("data/projects"), Path("data/snippets")
rows, scorable = [], 0
for pid in ids:
    planted = (PROJ / pid / "mutants.yaml").exists()
    dep_cp = (CP / f"{pid}.cp").exists()
    mut_cp = (CP / f"{pid}-mutants.cp").exists() and (CP / f"{pid}-mutants.jar").exists()
    good = SNIP / f"{pid}.good.jsonl"
    n_prompts = sum(1 for _ in good.open()) if good.exists() else 0
    major = 0
    jar = CP / f"{pid}-mutants.jar"
    if jar.exists():
        try:
            with zipfile.ZipFile(jar) as z:
                for n in [x for x in z.namelist() if x.endswith(".class")][:20]:
                    b = z.read(n)[:8]
                    if len(b) >= 8 and b[:4] == b"\xca\xfe\xba\xbe":
                        major = max(major, struct.unpack(">H", b[6:8])[0])
        except Exception:
            pass
    if mut_cp and planted:
        scorable += 1
    rows.append(dict(id=pid, planted=planted, dep_cp=dep_cp, mutant_cp=mut_cp,
                     prompts=n_prompts, major=major))
Path("${LOGDIR}/shard${TASK}-status.json").write_text(json.dumps(rows, indent=1))
print(f"shard ${TASK}: {len(ids)} attempted")
print(f"  planted mutants : {sum(r['planted'] for r in rows)}")
print(f"  dependency cp   : {sum(r['dep_cp'] for r in rows)}")
print(f"  MUTATION-SCORABLE: {scorable}")
print(f"  prompts          : {sum(r['prompts'] for r in rows)}")
over = [f"{r['id']}={r['major']}" for r in rows if r["major"] > 61]
print(f"  above major 61   : {' '.join(over) if over else 'none'}")
PYEOF

echo "=== shard ${TASK} DONE $(date -Is) ==="
