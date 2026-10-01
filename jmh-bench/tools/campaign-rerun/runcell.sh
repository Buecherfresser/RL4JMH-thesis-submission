#!/bin/bash
# Re-benchmark one (model, project) cell.
# Args: MODEL_KEY PROJECT CAMPAIGN_PATH [PRESET] [TIMESTAMP]
# TIMESTAMP defaults to the Aug-12 rerun campaign; the base-model campaign is
# 2026-08-11_11-50-00, so it has to be passed explicitly. If it names a
# directory that has no bundle for this project, fall back to globbing the
# campaign for one -- `base` carries an older timestamp whose bundles are not
# split per project.
#
# MUTANT_OP / MUTANT_TOKENS in the environment pick the injected-latency
# operator. Unset means `sleep` -- what the 2026-08 rerun measured -- so an
# in-flight campaign is unaffected. For a run at nanosecond scale, calibrate
# tokens on the measurement host first:
#   MUTANT_OP=spin MUTANT_TOKENS=$(...calibrate_mutant_op.py...) ./runcell.sh ...
set -uo pipefail
MODEL=$1; PROJECT=$2; CAMPAIGN=$3; PRESET=${4:-campaign}; TS=${5:-2026-08-12_22-40-00}
MUTANT_OP=${MUTANT_OP:-sleep}
MUTANT_TOKENS=${MUTANT_TOKENS:-64}

ROOT=/var/tmp/jmhb
REPO=$ROOT/repo
CELL=$MODEL-$PROJECT
OUT=$ROOT/out/$CELL
mkdir -p "$OUT" "$ROOT/tmp" "$ROOT/scratch"

# java-21 on this image is a JRE (no javac, no jar) and `java` on PATH points at
# it, so build.py's JAVA_HOME fallback would pick a runtime with no compiler.
# java-17 is the only complete JDK here and matches the pom's release=17.
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$ROOT/bin:$PATH
export MAVEN_OPTS="-Dmaven.repo.local=$ROOT/m2"
export TMPDIR=$ROOT/tmp

BUNDLE=$REPO/reports/$CAMPAIGN/$TS/bundles/project-$PROJECT-t0-think
if [ ! -d "$BUNDLE" ]; then
  BUNDLE=$(ls -d "$REPO/reports/$CAMPAIGN"/*/bundles/project-"$PROJECT"-t0-think 2>/dev/null | head -1)
fi
[ -d "$BUNDLE" ] || { echo "MISSING BUNDLE for $CAMPAIGN / $PROJECT (ts=$TS)"; exit 2; }

# Pin to the 4 Zen 4c cores (perf 144). The 2 Zen 4 cores (perf 196) measure
# 1.36x faster; an unpinned baseline and its mutant can land on different core
# types and differ by that much from scheduling alone, against a 1.10 kill
# threshold. Affinity is inherited by every JVM JMH forks.
PIN=1,2,3,5,7,8,9,11

cd "$REPO"
echo "=== $CELL start $(date -Iseconds) host=$(hostname -s) preset=$PRESET pin=$PIN mutant-op=$MUTANT_OP/$MUTANT_TOKENS"
echo "    bundle=$BUNDLE"
taskset -c $PIN "$ROOT/venv/bin/jmhbench" project-bench "$BUNDLE" \
  --project "$PROJECT" \
  --out "$OUT/report" \
  --workdir "$ROOT/scratch/$CELL" \
  --preset "$PRESET" \
  --sensitivity-sample 0 \
  --max-detect-attempts 6 \
  --mutant-op "$MUTANT_OP" \
  --mutant-tokens "$MUTANT_TOKENS" \
  --compiler-heap 4g
rc=$?
echo "=== $CELL done $(date -Iseconds) rc=$rc"
exit $rc
