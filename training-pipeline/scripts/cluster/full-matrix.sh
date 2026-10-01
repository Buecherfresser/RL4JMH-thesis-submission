#!/usr/bin/env bash
# Full JMH-Bench matrix (2 tracks x 2 temps x 2 thinking = 8 cells) for the run-7 checkpoint
# and for the base model, at MAX_TOKENS=16384 to match the completion cap training uses.
#
# The single-cell run at 8192 was not comparable to earlier results and may have been
# truncating: thinking alone accounted for a median 4.5k chars (max 8k) of the response.
#
# Each model's whole matrix runs inside a private mount namespace, so it gets its own /tmp and
# therefore its own JMH lock and never collides with the training run's JMH. Both are pinned to
# cores 0-23 while training stays on 24-95, so the benchmark phase is not fighting the trainer
# for CPU. JDK 21 throughout: generation's compile-check and the benchmark phase then agree, so
# a class that passes the check also builds in phase 2 (JDK 25 silently skips JMH's annotation
# processor, which is what produced empty BenchmarkList runs).
set -u

BASE=/opt/jmh/JMH-Bench
OUT="$BASE/reports/matrix16k"
JDK=/usr/lib/jvm/java-21-openjdk-amd64

for MODEL in run7 base; do
  cd "$BASE" || exit 1
  cp "/tmp/env.$MODEL" .env
  sed -i "s|^JAVA_HOME=.*|JAVA_HOME=$JDK|; s|^JAVA_HOME_HINT=.*|JAVA_HOME_HINT=$JDK|; s|/usr/lib/jvm/java-25-openjdk-amd64/bin|$JDK/bin|" .env
  echo "=== MODEL=$MODEL start $(date -Iseconds) ==="
  taskset -c 0-23 unshare --mount --propagation private sh -c \
    "mount -t tmpfs tmpfs /tmp && TRACKS='project synthetic' TEMPS='0 1' THINKING='true false' MAX_TOKENS=16384 REMOTE=local PARALLEL=32 RUN_DIR=$OUT/$MODEL tools/run_eval.sh"
  echo "=== MODEL=$MODEL done $(date -Iseconds) ==="
done
echo "FULL MATRIX COMPLETE"
