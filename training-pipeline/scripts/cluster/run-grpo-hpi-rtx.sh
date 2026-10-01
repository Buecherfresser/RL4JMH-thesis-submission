#!/usr/bin/env bash
# Slurm batch wrapper for GRPO on HPI — RTX Pro 6000 (gpu-batch).
#
# Submit:
#   cd /sc/scratch/zongxiong.chen/jonas/JMH_Training_Pipeline
#   sbatch scripts/cluster/run-grpo-hpi-rtx.sh
#
# Verify your account can use gpu-batch:  saccount
# If submit fails on --account, try dropping it or ask sc-helpdesk@hpi.de.
#
# Monitor:
#   tail -f /sc/scratch/zongxiong.chen/jonas/logs/grpo_<jobid>.out
#SBATCH --job-name=jmh-grpo-rtx
#SBATCH --partition=gpu-batch
#SBATCH --account=aisc
#SBATCH --nodes=1
#SBATCH --gres=gpu:rtx_pro_6000:1
#SBATCH --cpus-per-task=16
#SBATCH --mem=128G
#SBATCH --time=2-00:00:00
#SBATCH --output=/sc/scratch/zongxiong.chen/jonas/logs/grpo_%j.out
#SBATCH --error=/sc/scratch/zongxiong.chen/jonas/logs/grpo_%j.err

set -euo pipefail

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
nvidia-smi --query-gpu=index,name,driver_version,memory.total --format=csv,noheader 2>/dev/null || true

exec "${REPO}/scripts/remote/run-grpo.sh"
