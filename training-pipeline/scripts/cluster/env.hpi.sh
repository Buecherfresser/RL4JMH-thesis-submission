# Source on hpi2 (login or inside sbatch): source scripts/cluster/env.hpi.sh
#
# Do NOT use the name SCRATCH here — Slurm sets $SCRATCH to a per-job temp dir
# (e.g. /scratch/2340597) on compute nodes, which breaks path defaults.
#
# Do NOT pin UV_PROJECT_ENVIRONMENT here. run-grpo.sh / start-vllm.sh pick a
# profile-specific venv (gemma-cu130 vs qwen-cu128). The shared repo .venv is
# often a CUDA-13 Gemma stack and will fail on H100 driver 12.8.
JMH_ROOT="${JMH_ROOT:-/sc/scratch/zongxiong.chen/jonas}"
REPO="${JMH_ROOT}/JMH_Training_Pipeline"

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
# Prefer Slurm's per-job local scratch when present (exec-friendly, faster than GPFS).
if [[ -n "${SCRATCH:-}" && -d "${SCRATCH}" ]]; then
  export TMPDIR="${SCRATCH}/tmp"
else
  export TMPDIR="${JMH_ROOT}/tmp"
fi

# Prefer JDK 23 over 17: the mutant corpus is not uniform. Provisioning built each jar with
# whatever JDK its host had, so commons-codec is class-file major 67 (JDK 23) and
# jackson-databind is 65 (JDK 21) while the other 46 projects are 61 (JDK 17). javac 17 cannot
# link against a major-67 jar at all ("bad class file ... wrong version 67.0, should be 61.0"),
# so under JDK 17 every rollout for those two projects silently scores 0 -- 40 of the 858
# mutation70-qwen prompts. javac 23 reads all of them and still targets runner.java_release.
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
