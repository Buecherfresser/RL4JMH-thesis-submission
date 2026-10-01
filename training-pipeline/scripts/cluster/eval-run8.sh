#!/usr/bin/env bash
# Full JMH-Bench matrix (2 tracks x 4 cells) for a run-8 checkpoint at MAX_TOKENS=16384.
#
# Serves the checkpoint as a LoRA module on one free card, runs the 8 cells, stops the server.
# base at 16384 in reports/matrix16k/base is the comparison arm and is NOT re-run; run7final is
# the other arm already on disk, so a run-8 number is directly comparable to both.
#
# Assumes training has STOPPED. The box is RAM-bound (62 GiB) and run 7 was OOM-killed at step
# 938 by exactly this combination -- an eval server plus 32-way generation plus Maven/JMH on top
# of a live training run. queue-eval-after-training.sh is what enforces the ordering; if you run
# this by hand, check nothing is training first.
#
# JDK 21 throughout: generation's compile-check and the benchmark phase then agree, and JDK 25
# silently skips JMH's annotation processor (which produces empty BenchmarkList runs).
#
#   CKPT=/opt/jmh/.../checkpoint-1300 LABEL=run8 scripts/cluster/eval-run8.sh
set -u

BASE=/opt/jmh/JMH-Bench
TRAIN=/opt/jmh/JMH_Training_Pipeline
VENV=/opt/jmh/.venv-gemma-cu130
JDK=/usr/lib/jvm/java-21-openjdk-amd64

LABEL="${LABEL:-run8}"
OUT="${OUT:-$BASE/reports/matrix16k/$LABEL}"
PORT="${PORT:-8032}"
EVAL_GPU="${EVAL_GPU:-4}"
OUTDIR="${OUTDIR:-$TRAIN/outputs/grpo-mutation70-gemma-rtx-run8}"

# Default to the newest checkpoint. NOTE: newest is not necessarily best -- run 7's ck700 beat
# its final ck1300 on the synthetic composite (78 vs 75) and produced 35 % more benchmarks. Set
# CKPT explicitly to evaluate a different one.
if [[ -z "${CKPT:-}" ]]; then
  CKPT=$(ls -d "$OUTDIR"/checkpoint-* 2>/dev/null \
         | sed 's/.*checkpoint-//' | sort -n | tail -1 \
         | xargs -I{} echo "$OUTDIR/checkpoint-{}")
fi
[[ -d "${CKPT:-}" ]] || { echo "FATAL: no checkpoint found (CKPT=${CKPT:-unset})" >&2; exit 1; }

echo "=== eval $LABEL ==="
echo "checkpoint : $CKPT"
echo "out        : $OUT"
echo "server     : GPU $EVAL_GPU, port $PORT"

# A stale idle server still pins host RAM and VRAM; the eval needs both.
for pid in $(pgrep -f 'vllm serve.*--lora-modules' || true); do
  echo "stopping stale vLLM server pid=$pid"; kill "$pid" 2>/dev/null
done
sleep 10

export CUDA_VISIBLE_DEVICES="$EVAL_GPU"
export NCCL_P2P_DISABLE=1          # AMD IOMMU in Translated mode: peer copies silently move zeros
export HF_HOME=/opt/jmh/.cache/huggingface
export XDG_CACHE_HOME=/opt/jmh/.cache
export VLLM_CACHE_ROOT=/opt/jmh/.cache/vllm
export TMPDIR=/opt/jmh/tmp
export VLLM_USE_FLASHINFER_SAMPLER=0   # its JIT fails on this box's nvcc/cccl skew; fallback is fine
[[ -f /opt/jmh/secrets.env ]] && . /opt/jmh/secrets.env

echo "=== starting vLLM $(date -Iseconds) ==="
"$VENV/bin/vllm" serve google/gemma-4-E2B-it \
  --served-model-name google/gemma-4-E2B-it \
  --enable-lora --lora-modules "$LABEL=$CKPT" \
  --max-lora-rank 16 --max-model-len 65536 \
  --gpu-memory-utilization 0.85 \
  --host 127.0.0.1 --port "$PORT" > "/opt/jmh/logs/eval-$LABEL-server.log" 2>&1 &
SERVER_PID=$!
trap 'kill $SERVER_PID 2>/dev/null' EXIT

for i in $(seq 1 120); do
  curl -sf -m 5 "http://127.0.0.1:$PORT/v1/models" >/dev/null && { echo "server ready after ${i}0s"; break; }
  kill -0 "$SERVER_PID" 2>/dev/null || { echo "FATAL: server died, see /opt/jmh/logs/eval-$LABEL-server.log" >&2; exit 2; }
  sleep 10
done
curl -sf -m 5 "http://127.0.0.1:$PORT/v1/models" >/dev/null || { echo "FATAL: server never came up" >&2; exit 2; }

cd "$BASE" || exit 1
sed -e "s|^OPENAI_BASE_URL=.*|OPENAI_BASE_URL=http://127.0.0.1:$PORT/v1|" \
    -e "s|^OPENAI_MODEL=.*|OPENAI_MODEL=$LABEL|" \
    -e "s|^JAVA_HOME=.*|JAVA_HOME=$JDK|" \
    -e "s|^JAVA_HOME_HINT=.*|JAVA_HOME_HINT=$JDK|" \
    -e "s|/usr/lib/jvm/java-25-openjdk-amd64/bin|$JDK/bin|" \
    /tmp/env.run7 > .env
echo "=== env ==="; grep -E "OPENAI_MODEL|OPENAI_BASE_URL|JAVA_HOME=" .env

echo "=== matrix start $(date -Iseconds) ==="
# Private mount namespace with its own tmpfs /tmp: guarantees JMH's global $java.io.tmpdir/jmh.lock
# cannot collide with anything else on the box. (The training pipeline now sets an explicit
# per-invocation java.io.tmpdir, but JMH-Bench is a separate codebase and still relies on this.)
unshare --mount --propagation private sh -c \
  "mount -t tmpfs tmpfs /tmp && TRACKS='project synthetic' TEMPS='0 1' THINKING='true false' MAX_TOKENS=16384 REMOTE=local PARALLEL=32 RUN_DIR=$OUT tools/run_eval.sh"
rc=$?
echo "=== matrix done $(date -Iseconds) rc=$rc ==="
kill "$SERVER_PID" 2>/dev/null
[[ $rc -eq 0 ]] && echo "RUN8 MATRIX COMPLETE: $OUT" || echo "RUN8 MATRIX FAILED rc=$rc"
exit "$rc"
