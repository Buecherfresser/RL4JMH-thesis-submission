# Source on the LRZ AI Systems login node or inside an sbatch job:
#   source scripts/cluster/env.lrz.sh
#
# LRZ differs from HPI in three ways that matter here:
#   * No `module`, no system CUDA toolkit. The H100 nodes run driver 580.x / CUDA 13.0, so the
#     gemma-cu130 profile's torch wheels work natively — no container, no nvcc, no source build.
#   * $HOME is a 100 GB DSS quota; bulk data belongs in the DSS container (700 GB personal).
#   * Slurm sets neither $SCRATCH nor $SLURM_TMPDIR. Compute nodes do have a local 1.7 TB
#     /raid, which is what the JMH reward wants: every rollout is a Maven build plus forked
#     JVMs, i.e. thousands of small files that would crawl on GPFS.
JMH_ROOT="${JMH_ROOT:-/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh}"
REPO="${REPO:-${JMH_ROOT}/JMH_Training_Pipeline}"

# Optional secrets (HF_TOKEN, etc.): chmod 600 $JMH_ROOT/secrets.env
if [[ -f "${JMH_ROOT}/secrets.env" ]]; then
  # shellcheck source=/dev/null
  source "${JMH_ROOT}/secrets.env"
fi

export HF_HOME="${JMH_ROOT}/.cache/huggingface"
export UV_CACHE_DIR="${JMH_ROOT}/.uv-cache"
export XDG_CACHE_HOME="${JMH_ROOT}/.cache"
export TRITON_CACHE_DIR="${JMH_ROOT}/.cache/triton"
export TORCHINDUCTOR_CACHE_DIR="${JMH_ROOT}/.cache/torchinductor"
export VLLM_CACHE_ROOT="${JMH_ROOT}/.cache/vllm"
# GPFS drops execute bits and hard-links badly during uv installs.
export UV_LINK_MODE=copy
# GRPO alternates long rollouts with backward passes, which fragments the caching allocator:
# job 5721357 died at step 108/744 with 16.19 GiB "reserved but unallocated" while asking for
# a 16 GiB block. Expandable segments let the allocator grow a segment instead of needing one
# contiguous free block.
export PYTORCH_CUDA_ALLOC_CONF="${PYTORCH_CUDA_ALLOC_CONF:-expandable_segments:True}"

# Node-local build scratch. /raid exists on the GPU nodes but not on the login node.
JMH_LOCAL_SCRATCH=""
if [[ -n "${SLURM_JOB_ID:-}" ]]; then
  for candidate in /raid /tmp; do
    if [[ -d "${candidate}" && -w "${candidate}" ]]; then
      JMH_LOCAL_SCRATCH="${candidate}/jmh-${SLURM_JOB_ID}"
      break
    fi
  done
fi
if [[ -n "${JMH_LOCAL_SCRATCH}" ]] && mkdir -p "${JMH_LOCAL_SCRATCH}" 2>/dev/null; then
  export TMPDIR="${JMH_LOCAL_SCRATCH}/tmp"
  export JMH_LOCAL_SCRATCH
else
  export TMPDIR="${JMH_ROOT}/tmp"
fi

# JDK 23, not 17. The mutant corpus is not uniform: provisioning built each jar with
# whatever JDK its host had, so commons-codec is class-file major 67 (JDK 23) and
# jackson-databind is 65 (JDK 21) while the other 46 projects are 61 (JDK 17). A javac
# older than a jar cannot link against it -- "bad class file ... wrong version 67.0,
# should be 61.0" -- so under JDK 17 every rollout for those two projects scores 0.
# javac 23 reads all of them and still emits release-17 bytecode (runner.java_release).
# Fall back to jdk-17 only if 23 is not installed; preflight_grpo.py fails the job in
# that case rather than letting the zeros pass silently.
if [[ -x "${JMH_ROOT}/tools/jdk-23/bin/javac" ]]; then
  export JAVA_HOME="${JMH_ROOT}/tools/jdk-23"
else
  export JAVA_HOME="${JMH_ROOT}/tools/jdk-17"
fi
export PATH="${JAVA_HOME}/bin:${JMH_ROOT}/tools/maven/bin:${HOME}/.local/bin:${PATH}"
export MAVEN_OPTS="-Dmaven.repo.local=${JMH_ROOT}/.m2/repository"

mkdir -p \
  "${HF_HOME}" "${UV_CACHE_DIR}" "${XDG_CACHE_HOME}" "${TMPDIR}" \
  "${JMH_ROOT}/.m2/repository" "${JMH_ROOT}/logs"
