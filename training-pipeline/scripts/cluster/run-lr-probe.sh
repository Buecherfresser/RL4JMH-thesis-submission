#!/usr/bin/env bash
# Launch the four-arm learning-rate probe, one GPU per arm, all at the same moment.
#
# 3e-6 was chosen for run 2, when the reload_weights bug meant every rollout came from the BASE
# model and gradients were ~20x smaller than they are now. It has never been tuned in the regime
# the pipeline actually runs in. This measures whether it is leaving learning on the table.
#
# Arms are identical except for the LR: same seed, same corpus, same prompt order, same reward,
# started together so they share background load. From base, not from a checkpoint -- that is the
# condition the final run starts in, and resuming would restore optimiser state and silently
# override the LR under test.
#
# Each arm needs its OWN torch.distributed rendezvous port. They are single-process runs, but
# TRL/accelerate still stands up a c10d TCPStore, and four arms on the default 29500 means three
# die instantly with EADDRINUSE.
set -u

ROOT=/opt/jmh/JMH_Training_Pipeline
VENV=/opt/jmh/.venv-gemma-cu130
ARMS=(${LR_ARMS:-3e-6 6e-6 1.2e-5 2.4e-5})
BASE_PORT=${BASE_PORT:-29510}
PREFIX=${PREFIX:-lr-probe}
# Hard stop, so a probe cannot quietly hold four GPUs for its full 300-step horizon.
STOP_AFTER_MIN=${STOP_AFTER_MIN:-0}

cd "$ROOT" || exit 1
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64
export PATH="$JAVA_HOME/bin:/opt/jmh/.local/bin:$PATH"
export MAVEN_OPTS=-Dmaven.repo.local=/opt/jmh/.m2/repository
export HF_HOME=/opt/jmh/.cache/huggingface
export XDG_CACHE_HOME=/opt/jmh/.cache
export VLLM_CACHE_ROOT=/opt/jmh/.cache/vllm
export TMPDIR=/opt/jmh/tmp
export PYTORCH_CUDA_ALLOC_CONF=expandable_segments:True
export NCCL_P2P_DISABLE=1
export VLLM_USE_FLASHINFER_SAMPLER=0
export PYTHONUNBUFFERED=1
export PYTHONPATH=src

for i in "${!ARMS[@]}"; do
  arm="${ARMS[$i]}"
  session="lrp_$(echo "$arm" | tr . _)"
  port=$((BASE_PORT + i))
  log="/opt/jmh/logs/${PREFIX}-${arm}.log"
  tmux kill-session -t "$session" 2>/dev/null
  tmux new-session -d -s "$session" \
    "cd $ROOT && MASTER_ADDR=127.0.0.1 MASTER_PORT=$port RANK=0 WORLD_SIZE=1 LOCAL_RANK=0 \
     CUDA_VISIBLE_DEVICES=$i $VENV/bin/python -m jmhgen.training.grpo \
     --config configs/grpo/${PREFIX}-${arm}.yaml --corpus full 2>&1 | tee $log"
  echo "arm ${arm}: GPU ${i}, port ${port}, session ${session}, log ${log}"
done

sleep 4
echo "--- sessions ---"
tmux ls | grep lrp_ || echo "NONE STARTED"

if [[ "$STOP_AFTER_MIN" != "0" ]]; then
  # Detached watchdog: the arms are configured for 300 steps so their LR decay matches the
  # previous probe, but this round is a fixed-duration experiment. No checkpoints are written,
  # so stopping mid-flight costs nothing -- the per-step telemetry is already on disk.
  nohup bash -c "sleep $((STOP_AFTER_MIN * 60)); for a in ${ARMS[*]}; do tmux kill-session -t lrp_\$(echo \$a | tr . _) 2>/dev/null; done; echo stopped-at-\$(date -Iseconds)" \
    > /opt/jmh/logs/${PREFIX}-watchdog.log 2>&1 &
  echo "watchdog: sessions killed after ${STOP_AFTER_MIN} min"
fi
