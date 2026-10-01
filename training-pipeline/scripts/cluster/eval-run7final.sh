#!/usr/bin/env bash
# Full JMH-Bench matrix for run 7's FINAL weights (checkpoint-1300) at MAX_TOKENS=16384.
#
# Only this model needs running: base was already measured at 16384 under identical settings in
# reports/matrix16k/base, so that directory is the comparison arm.
#
# Training is stopped, so unlike the earlier matrix this gets the whole machine: no core pinning
# and no competing JMH, which is what the benchmark phase actually needs (JMH measures
# wall-clock). The private mount namespace is kept anyway - it costs nothing and guarantees the
# JMH lock cannot collide if anything else on the box starts a benchmark.
#
# JDK 21 throughout: generation's compile-check and the benchmark phase then agree, and JDK 25
# silently skips JMH's annotation processor (which produced empty BenchmarkList runs).
set -u

BASE=/opt/jmh/JMH-Bench
OUT="$BASE/reports/matrix16k/run7final"
JDK=/usr/lib/jvm/java-21-openjdk-amd64

cd "$BASE" || exit 1
sed -e "s|^OPENAI_BASE_URL=.*|OPENAI_BASE_URL=http://127.0.0.1:8031/v1|" \
    -e "s|^OPENAI_MODEL=.*|OPENAI_MODEL=run7final|" \
    -e "s|^JAVA_HOME=.*|JAVA_HOME=$JDK|" \
    -e "s|^JAVA_HOME_HINT=.*|JAVA_HOME_HINT=$JDK|" \
    -e "s|/usr/lib/jvm/java-25-openjdk-amd64/bin|$JDK/bin|" \
    /tmp/env.run7 > .env
echo "=== env ==="; grep -E "OPENAI_MODEL|OPENAI_BASE_URL|JAVA_HOME=" .env
echo "=== start $(date -Iseconds) ==="
unshare --mount --propagation private sh -c \
  "mount -t tmpfs tmpfs /tmp && TRACKS='project synthetic' TEMPS='0 1' THINKING='true false' MAX_TOKENS=16384 REMOTE=local PARALLEL=32 RUN_DIR=$OUT tools/run_eval.sh"
echo "=== done $(date -Iseconds) ==="
echo "RUN7FINAL MATRIX COMPLETE"
