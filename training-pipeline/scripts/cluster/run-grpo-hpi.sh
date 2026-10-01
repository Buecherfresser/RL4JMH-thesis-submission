#!/usr/bin/env bash
# Slurm batch wrapper for GRPO on HPI (rx01 -> aisc-batch H100 by default).
#
# Submit from the login node (not on a GPU interactively):
#   cd /sc/scratch/zongxiong.chen/jonas/JMH_Training_Pipeline
#   export HF_TOKEN=hf_...   # or put it in $JMH_ROOT/secrets.env (chmod 600)
#   sbatch scripts/cluster/run-grpo-hpi.sh
#
# RTX Pro 6000 (gpu-batch; check account with `saccount`):
#   sbatch --partition=gpu-batch --gres=gpu:rtx_pro_6000:1 \
#     --account=aisc scripts/cluster/run-grpo-hpi.sh
#
# Smoke only:
#   RUN_FULL=0 sbatch scripts/cluster/run-grpo-hpi.sh
#
# Full run without smoke:
#   SMOKE_FIRST=0 sbatch scripts/cluster/run-grpo-hpi.sh
#
# Monitor:
#   squeue -u zongxiong.chen
#   tail -f /sc/scratch/zongxiong.chen/jonas/logs/grpo_<jobid>.out
#   tail -f /sc/scratch/zongxiong.chen/jonas/logs/grpo-gemma-4-E2B-it/run-grpo.status
#SBATCH --job-name=jmh-grpo
#SBATCH --partition=aisc-batch
#SBATCH --account=aisc
#SBATCH --qos=aisc
#SBATCH --nodes=1
#SBATCH --gres=gpu:h100:1
#SBATCH --cpus-per-task=16
#SBATCH --mem=128G
#SBATCH --time=2-00:00:00
#SBATCH --output=/sc/scratch/zongxiong.chen/jonas/logs/grpo_%j.out
#SBATCH --error=/sc/scratch/zongxiong.chen/jonas/logs/grpo_%j.err

set -euo pipefail

# Slurm sets $SCRATCH to /scratch/<jobid> on compute nodes — do not use that name.
JMH_ROOT="${JMH_ROOT:-/sc/scratch/zongxiong.chen/jonas}"
REPO="${JMH_ROOT}/JMH_Training_Pipeline"

cd "${REPO}"
# shellcheck source=/dev/null
source "${REPO}/scripts/cluster/env.hpi.sh"

export JMHGEN_VOLUME="${JMH_ROOT}"
export JMHGEN_SCRATCH="${JMH_ROOT}"
export REPO_DIR="${REPO}"
export GRPO_CONFIG="${GRPO_CONFIG:-configs/grpo/jmh-rl.yaml}"
export SMOKE_FIRST="${SMOKE_FIRST:-1}"
export SMOKE_STEPS="${SMOKE_STEPS:-2}"
export RUN_FULL="${RUN_FULL:-1}"

echo "node=$(hostname) job=${SLURM_JOB_ID:-local} config=${GRPO_CONFIG}"
nvidia-smi --query-gpu=index,name,memory.total --format=csv,noheader 2>/dev/null || true

exec "${REPO}/scripts/remote/run-grpo.sh"
