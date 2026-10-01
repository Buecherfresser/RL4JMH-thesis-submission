#!/usr/bin/env bash
# Wait for the GRPO training run to stop, then fire the JMH-Bench matrix for SEVERAL checkpoints.
#
# Why a watcher rather than appending the eval to the training command: the training run is
# resumable and gets restarted by hand, so chaining `train && eval` would lose the queued eval on
# every restart. This polls instead, and is safe to leave running across restarts.
#
# The ordering is not cosmetic. The box has 62 GiB of RAM and run 7 was OOM-KILLED at step 938 by
# an eval server + 32-way generation + Maven/JMH stacked on a live training run. Nothing here
# starts until the last trainer process is gone.
#
# WHY MORE THAN ONE CHECKPOINT. Run 8 passes over the same 786 prompts four times, and the
# training telemetry cannot see the kind of overfitting that matters: run 7's ck1300 beat its
# ck700 on *training* compile rate while generating 26 % fewer benchmarks and killing fewer
# mutants on held-out commons-compress. The only way to find where that turn happens is to
# measure more than one point on the curve. Checkpoints are free (saved every 25 steps); the
# eval costs ~1.7 h each on cards that are otherwise idle.
#
# CHECKPOINTS defaults to a mid point, the step that matches run 7's stopping point (so the two
# runs are comparable at equal optimiser updates), and the final weights.
#
#   CHECKPOINTS="1000 1300 final" scripts/cluster/queue-eval-after-training.sh
set -u

TRAIN=/opt/jmh/JMH_Training_Pipeline
OUTDIR="${OUTDIR:-$TRAIN/outputs/grpo-mutation70-gemma-rtx-run8}"
CONSOLE="${CONSOLE:-/opt/jmh/logs/run8-console.log}"
MIN_STEP="${MIN_STEP:-700}"
POLL="${POLL:-60}"
LABEL="${LABEL:-run8}"
CHECKPOINTS="${CHECKPOINTS:-1000 1300 final}"

newest_step() {
  ls -d "$OUTDIR"/checkpoint-* 2>/dev/null | sed 's/.*checkpoint-//' | sort -n | tail -1
}

# Nearest existing checkpoint at or below a target, so a run that stops early still evaluates
# something meaningful instead of skipping the cell.
resolve_step() {
  local want="$1"
  ls -d "$OUTDIR"/checkpoint-* 2>/dev/null | sed 's/.*checkpoint-//' | sort -n \
    | awk -v w="$want" '$1 <= w {c=$1} END {if (c) print c}'
}

echo "=== eval queue armed $(date -Iseconds) ==="
echo "watching    : $OUTDIR"
echo "console     : $CONSOLE"
echo "min step    : $MIN_STEP"
echo "checkpoints : $CHECKPOINTS"
echo "poll        : ${POLL}s"

while pgrep -f 'jmhgen.training.grpo' >/dev/null 2>&1; do
  sleep "$POLL"
done

echo "=== trainer gone $(date -Iseconds) ==="
sleep 60          # let CUDA contexts tear down and host RAM come back before claiming a card

CLEAN=no
grep -q 'GRPO exited 0' "$CONSOLE" 2>/dev/null && CLEAN=yes
NEWEST=$(newest_step)
echo "clean exit  : $CLEAN"
echo "newest ckpt : checkpoint-${NEWEST:-none}"

if [[ -z "$NEWEST" ]]; then
  echo "REFUSING: no checkpoint in $OUTDIR"; exit 1
fi
if [[ "$CLEAN" != yes && "$NEWEST" -lt "$MIN_STEP" ]]; then
  echo "REFUSING: trainer did not exit cleanly and checkpoint-$NEWEST is below MIN_STEP=$MIN_STEP."
  echo "          Nothing has been evaluated. Inspect $CONSOLE, then run eval-run8.sh by hand."
  exit 1
fi

# De-duplicate: if the run stopped early, "1300" and "final" can resolve to the same checkpoint,
# and paying 1.7 h to measure the same weights twice helps nobody.
declare -A SEEN=()
ORDER=()
for want in $CHECKPOINTS; do
  if [[ "$want" == "final" ]]; then step="$NEWEST"; else step=$(resolve_step "$want"); fi
  [[ -z "$step" ]] && { echo "skip $want: no checkpoint at or below it"; continue; }
  [[ -n "${SEEN[$step]:-}" ]] && { echo "skip $want: already covered by checkpoint-$step"; continue; }
  SEEN[$step]=1
  ORDER+=("$step")
done
echo "will evaluate: ${ORDER[*]}"

rc=0
for step in "${ORDER[@]}"; do
  echo "=== matrix for checkpoint-$step $(date -Iseconds) ==="
  CKPT="$OUTDIR/checkpoint-$step" LABEL="${LABEL}-ck${step}" \
    "$TRAIN/scripts/cluster/eval-run8.sh" || { rc=$?; echo "checkpoint-$step FAILED rc=$rc"; }
done
echo "=== all matrices done $(date -Iseconds) rc=$rc ==="
echo "compare with: reports/matrix16k/base and reports/matrix16k/run7final"
exit "$rc"
