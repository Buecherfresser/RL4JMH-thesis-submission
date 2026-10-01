#!/usr/bin/env bash
# Recover mutation-scorable projects for the GRPO corpus on an LRZ CPU node.
#
# Why a cluster job: 36 of the still-unusable projects have no dependency classpath at all
# (`data/classpaths/<id>.cp` was never built for them), so the javac mutant backend runs with
# no -cp and every third-party import fails. Building those classpaths means a real Maven or
# Gradle build per project against a cold ~/.m2 -- hours of CPU, which is what lrz-cpu is for.
#
# JDK POLICY -- pinned to 17 on purpose, do not "upgrade" this.
#   A SUT jar can only be linked against by a javac at least as new as the jar. The corpus is
#   already ragged (majors 52/53/55/61/65/67), and the two jars above 61 -- commons-codec (67)
#   and jackson-databind (65) -- are exactly why scripts/cluster/env.lrz.sh has to force JDK 23
#   at rollout time. Building everything new under JDK 17 caps every jar this job produces at
#   major 61, which any javac >= 17 can read, on any host. The javac backend already targets
#   --release 17 (project.yaml java_release); pinning JAVA_HOME is what also constrains the
#   native Maven/Gradle fallback, which is where the drift came from. The cost is that
#   projects whose build demands JDK 21+ will fail here -- an accepted trade for a uniform corpus.
#
#   sbatch scripts/cluster/provision-corpus-lrz-cpu.sbatch
#SBATCH --job-name=jmh-provision-corpus
#SBATCH --partition=lrz-cpu
#SBATCH --qos=cpu
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=32G
#SBATCH --time=10:00:00
#SBATCH --output=/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh/logs/provision-corpus_%j.out
#SBATCH --error=/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh/logs/provision-corpus_%j.out

set -uo pipefail
export PYTHONUNBUFFERED=1

JMH_ROOT="${JMH_ROOT:-/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh}"
REPO="${REPO:-${JMH_ROOT}/JMH_Training_Pipeline}"
cd "${REPO}" || exit 1
# shellcheck source=/dev/null
source "${REPO}/scripts/cluster/env.lrz.sh"

# Override env.lrz.sh's JDK 23 preference -- see JDK POLICY above.
export JAVA_HOME="${JMH_ROOT}/tools/jdk-17"
export PATH="${JAVA_HOME}/bin:${JMH_ROOT}/tools/maven/bin:${HOME}/.local/bin:${PATH}"
# uv must not try to re-resolve the environment on a compute node.
export UV_PROJECT_ENVIRONMENT="${JMH_ROOT}/.venv-gemma-cu130"
export UV_NO_SYNC=1
export UV_OFFLINE=1
# One project must not eat the whole wall clock.
export PER_PROJECT_TIMEOUT_S=1500

PY="${UV_PROJECT_ENVIRONMENT}/bin/python"
# Full per-phase output. The summary below is tail-truncated for readability; these keep
# every error so a failed phase can actually be diagnosed afterwards.
PHASE_LOGS="${JMH_ROOT}/logs/provision-corpus_${SLURM_JOB_ID}"
mkdir -p "${PHASE_LOGS}"
echo "per-phase logs: ${PHASE_LOGS}"

banner() { echo; echo "############ $* ############"; echo; }

echo "=== node=$(hostname) job=${SLURM_JOB_ID} cpus=${SLURM_CPUS_PER_TASK:-?} start=$(date -Is) ==="
echo "JAVA_HOME=${JAVA_HOME}"
java -version 2>&1 | head -1
javac -version 2>&1
mvn -v 2>&1 | head -1
echo "TMPDIR=${TMPDIR}"
df -h "${TMPDIR}" 2>/dev/null | tail -1
df -h "${JMH_ROOT}" 2>/dev/null | tail -1
echo "mutant cps at start: $(ls data/classpaths/*-mutants.cp 2>/dev/null | wc -l)"
echo "plain  cps at start: $(ls data/classpaths/*.cp 2>/dev/null | grep -vc mutants)"

banner "PHASE 0 -- outbound network gate"
# Every phase below needs GitHub (checkouts) and Maven Central (dependency resolution), and
# this cluster's ~/.m2 is nearly cold. If compute nodes are walled off, fail in seconds
# rather than burning a 10-hour allocation discovering it project by project.
NET_OK=1
curl -sS -o /dev/null -m 25 -w "  github.com http=%{http_code}\n" https://github.com || NET_OK=0
curl -sS -o /dev/null -m 25 -w "  repo.maven.apache.org http=%{http_code}\n" \
  https://repo.maven.apache.org/maven2/ || NET_OK=0
_probe=$(mktemp -d)
if timeout 120 git clone --depth 1 --filter=blob:none -q \
     https://github.com/stleary/JSON-java.git "${_probe}/p" >/dev/null 2>&1; then
  echo "  git clone OK"
else
  echo "  git clone FAILED"
  NET_OK=0
fi
rm -rf "${_probe}"
if [[ "${NET_OK}" -ne 1 ]]; then
  echo
  echo "ABORT: lrz-cpu compute nodes cannot reach GitHub / Maven Central."
  echo "       Provisioning needs both. Run this on a networked host, or pre-populate"
  echo "       ${JMH_ROOT}/.m2/repository and data/external-src from the login node."
  exit 2
fi

# The prompt corpus is an input, not a scratch file: keep a copy so a bad merge is recoverable.
cp -f data/snippets/rl-merged.good.jsonl "data/snippets/rl-merged.good.jsonl.bak-${SLURM_JOB_ID}" 2>/dev/null || true

banner "PHASE 1 -- checkouts pinned to each project.yaml provenance.tag"
# Without pinning, a fresh clone lands on today's default-branch tip and mutations.patch
# stops applying. data/external-src is empty on this host, so every project is a fresh clone.
"${PY}" scripts/prepare_rl_sources.py 2>&1 | tee "${PHASE_LOGS}/phase1-prepare.log" | grep -vE "^  \\$ git" | tail -120
echo "PHASE1_RC=${PIPESTATUS[0]}"
echo "checkouts now: $(ls data/external-src 2>/dev/null | wc -l)"

banner "PHASE 2 -- dependency classpaths for projects that have none"
MISSING_CP=$("${PY}" - <<'PYEOF'
from pathlib import Path
import yaml
CP = Path("data/classpaths")
c = yaml.safe_load(Path("configs/grpo/rl-corpus.yaml").read_text())
ids = [e["id"] for e in (c.get("existing") or [])] + [e["id"] for e in (c.get("projects") or [])]
print(" ".join(i for i in ids if not (CP / f"{i}.cp").exists()))
PYEOF
)
echo "targets ($(echo "${MISSING_CP}" | wc -w)): ${MISSING_CP}"
if [[ -n "${MISSING_CP// /}" ]]; then
  # shellcheck disable=SC2086
  timeout 5h ./scripts/provision_rl_projects.sh ${MISSING_CP} 2>&1 | tee "${PHASE_LOGS}/phase2-classpaths.log" | grep -E "^== \\[|WARNING|ERROR|failures" | tail -200
  echo "PHASE2_RC=${PIPESTATUS[0]}"
fi
echo "plain cps now: $(ls data/classpaths/*.cp 2>/dev/null | grep -vc mutants)"

banner "PHASE 3 -- patched SUT jars for every unusable project"
timeout 3h ./scripts/reprovision_missing_mutants.sh 2>&1 | tee "${PHASE_LOGS}/phase3-mutants.log" | grep -E "^== \\[|^recovered|^still failing|^total" | tail -200
echo "PHASE3_RC=${PIPESTATUS[0]}"

banner "PHASE 4 -- rebuild the jars above class-file major 61"
# Last, so a failure here cannot cost the recoveries above; the script restores the previous
# jar when a rebuild fails. Under JDK 17 these come back at major 61 and stop needing
# env.lrz.sh's JDK 23 workaround.
OVER=$("${PY}" - <<'PYEOF'
import struct, zipfile
from pathlib import Path
out = []
for jar in sorted(Path("data/classpaths").glob("*-mutants.jar")):
    try:
        with zipfile.ZipFile(jar) as z:
            names = [n for n in z.namelist() if n.endswith(".class")][:40]
            for n in names:
                b = z.read(n)[:8]
                if len(b) >= 8 and b[:4] == b"\xca\xfe\xba\xbe" and struct.unpack(">H", b[6:8])[0] > 61:
                    out.append(jar.name.removesuffix("-mutants.jar"))
                    break
    except Exception:
        pass
print(" ".join(out))
PYEOF
)
echo "over-ceiling: ${OVER:-none}"
if [[ -n "${OVER// /}" ]]; then
  # shellcheck disable=SC2086
  timeout 45m ./scripts/reprovision_missing_mutants.sh ${OVER} 2>&1 | tee "${PHASE_LOGS}/phase4-ceiling.log" | grep -E "^== \\[|^recovered|^still failing" | tail -60
fi

banner "PHASE 5 -- rebuild the prompt set and wire the configs"
"${PY}" scripts/build_mutation_corpus.py 2>&1 | tail -25
"${PY}" scripts/sync_grpo_corpus_configs.py 2>&1 | tail -15

banner "PHASE 6 -- results"
echo "mutant cps: $(ls data/classpaths/*-mutants.cp 2>/dev/null | wc -l)"
echo "plain  cps: $(ls data/classpaths/*.cp 2>/dev/null | grep -vc mutants)"
echo
echo "--- class-file major distribution (JDK consistency) ---"
"${PY}" - <<'PYEOF'
import collections, struct, zipfile
from pathlib import Path
JDK = {52: "8", 53: "9", 55: "11", 61: "17", 65: "21", 67: "23"}
dist, over = collections.Counter(), []
for jar in sorted(Path("data/classpaths").glob("*-mutants.jar")):
    try:
        with zipfile.ZipFile(jar) as z:
            majors = set()
            for n in [x for x in z.namelist() if x.endswith(".class")][:40]:
                b = z.read(n)[:8]
                if len(b) >= 8 and b[:4] == b"\xca\xfe\xba\xbe":
                    majors.add(struct.unpack(">H", b[6:8])[0])
            for m in majors:
                dist[m] += 1
            if majors and max(majors) > 61:
                over.append(f"{jar.name.removesuffix('-mutants.jar')}={max(majors)}")
    except Exception as exc:
        print(f"  unreadable {jar.name}: {exc}")
for m, n in sorted(dist.items()):
    print(f"  major {m} (JDK {JDK.get(m, '?')}): {n} jar(s)")
print("  ABOVE CEILING 61:", " ".join(over) if over else "none -- every jar reads on javac >= 17")
PYEOF

echo
echo "--- preflight on the training configs ---"
for cfg in configs/grpo/mutation70-gemma.yaml configs/grpo/mutation70-qwen.yaml; do
  echo "== ${cfg} =="
  "${PY}" scripts/preflight_grpo.py "${cfg}" --corpus full 2>&1 | tail -9
done

echo
echo "=== DONE $(date -Is) ==="
