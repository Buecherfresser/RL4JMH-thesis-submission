#!/usr/bin/env bash
# Isolate the Qwen TRL server-mode hang on hpi2.
#
# Symptom (jobs 2343911, 2362641, 2384618): `trl vllm-serve` reaches
# "Waiting for application startup." and stays there. Inside the allocation the
# EngineCore process is alive and burns 100% of one core, but allocates 0 MiB of
# GPU memory and reads 0 bytes of the cached weights. The parent blocks in
# unix_stream_data_wait, waiting for the worker's {"status": "ready"}.
#
# Already ruled out: FlashInfer JIT (sampling.so built 2026-07-17, cache warm, no
# nvcc/ninja running), missing weights (8.8 GB fully cached, no .incomplete),
# stale HF locks.
#
# IMPORTANT vs scripts/cluster/diag-qwen-trl.sbatch: that script's step [1/2] runs
# vLLM from a heredoc (`python - <<PY`). vLLM forces the `spawn` start method once
# CUDA is initialised, and spawn re-imports __main__ -- which for a heredoc is the
# literal path '<stdin>'. That is the FileNotFoundError in qwen-diag_2362677.err:
# a false negative that says nothing about the real hang. Every Python step here
# runs from a REAL FILE so the test is valid.
#
#   sbatch scripts/cluster/diag-qwen-trl-serve.sbatch
#
# Four independent, time-boxed probes; none aborts the others, so one job yields
# all four data points:
#   [1] plain vLLM.LLM, production-sized args   -> is engine init itself healthy?
#   [2] stock `trl vllm-serve`, no custom worker -> is TRL itself the problem?
#   [3] our jmhgen worker under py-spy           -> WHERE does it hang (stack)?
#SBATCH --job-name=jmh-qwen-diag2
#SBATCH --partition=aisc-batch
#SBATCH --account=aisc
#SBATCH --qos=aisc
#SBATCH --nodes=1
#SBATCH --gres=gpu:h100:1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64G
#SBATCH --time=1:00:00
#SBATCH --output=/sc/scratch/zongxiong.chen/jonas/logs/qwen-diag2_%j.out
#SBATCH --error=/sc/scratch/zongxiong.chen/jonas/logs/qwen-diag2_%j.err

set -uo pipefail
export PYTHONUNBUFFERED=1
# The production runs logged only 14 lines before hanging. DEBUG is the point of this job.
export VLLM_LOGGING_LEVEL=DEBUG
export UV_LINK_MODE=copy

JMH_ROOT="${JMH_ROOT:-/sc/scratch/zongxiong.chen/jonas}"
REPO="${JMH_ROOT}/JMH_Training_Pipeline"
VENV="${JMH_ROOT}/.venv-qwen-cu128"
PYSPY="${JMH_ROOT}/tools/pyspy/bin/py-spy"
DIAG_DIR="${JMH_ROOT}/logs/qwen-diag2-${SLURM_JOB_ID}"
mkdir -p "${DIAG_DIR}"

cd "${REPO}" || exit 1
# shellcheck source=/dev/null
source "${REPO}/scripts/cluster/env.hpi.sh"

export CUDA_HOME="${CUDA_HOME:-/sc/projects/sci-aisc/cuda-12.8}"
export PATH="${CUDA_HOME}/bin:${PATH}"
export VLLM_MAIN_CUDA_VERSION=12.8
export JMHGEN_MODEL_PROFILE=qwen
export JMHGEN_VOLUME="${JMH_ROOT}"
export UV_PROJECT_ENVIRONMENT="${VENV}"
export VLLM_VENV="${VENV}"
export CUDA_VISIBLE_DEVICES=0

if [[ -z "${HF_TOKEN:-}" && -f "${HOME}/.cache/huggingface/token" ]]; then
  HF_TOKEN="$(tr -d '[:space:]' < "${HOME}/.cache/huggingface/token")"
  export HF_TOKEN
fi
for curand_include in "${VENV}"/lib/python*/site-packages/nvidia/curand/include; do
  [[ -d "${curand_include}" ]] && export CPATH="${curand_include}${CPATH:+:${CPATH}}" && break
done
export FLASHINFER_NVCC="${CUDA_HOME}/bin/nvcc"
export FLASHINFER_WORKSPACE_BASE="${JMH_ROOT}/.cache/vllm/flashinfer"

echo "=== node=$(hostname) job=${SLURM_JOB_ID} diag_dir=${DIAG_DIR} ==="
nvidia-smi --query-gpu=index,name,memory.total,driver_version --format=csv,noheader
echo "py-spy: $([[ -x ${PYSPY} ]] && "${PYSPY}" --version || echo MISSING)"

# Snapshot GPU + our process tree while something is (not) starting up.
snapshot() {
  local tag="$1"
  echo "--- snapshot ${tag}: gpu ---"
  nvidia-smi --query-gpu=index,memory.used,utilization.gpu --format=csv,noheader
  echo "--- snapshot ${tag}: our python/EngineCore procs ---"
  ps -u "$USER" -o pid,ppid,etime,stat,rss,wchan:20,cmd \
    | grep -E "vllm|EngineCore|spawn_main|trl_vllm" | grep -v grep | cut -c1-150
}

###############################################################################
echo
echo "############ [1] plain vLLM.LLM from a REAL FILE, production-sized ############"
# max_model_len/gpu_util match configs/grpo/mutation70-qwen.yaml, not a toy 2048/0.5 --
# the hang may well be size-dependent, so the toy settings could hide it.
# Everything that constructs an LLM MUST sit inside the __main__ guard. vLLM forces `spawn`,
# and spawn re-imports this module in the child; a top-level LLM(...) therefore trips
# multiprocessing's _check_not_importing_main ("An attempt has been made to start a new process
# before the current process has finished its bootstrapping phase") and the probe fails for a
# reason that has nothing to do with the hang under investigation.
cat > "${DIAG_DIR}/probe_plain_llm.py" <<'PYFILE'
import time


def main() -> None:
    import torch
    from vllm import LLM

    print(f"torch={torch.__version__} cuda={torch.version.cuda}", flush=True)
    print("cuda_available", torch.cuda.is_available(), flush=True)
    print("device", torch.cuda.get_device_name(0), flush=True)
    started = time.time()
    llm = LLM(
        model="Qwen/Qwen3.5-4B",
        max_model_len=32768,
        gpu_memory_utilization=0.90,
        enforce_eager=True,
        language_model_only=True,
    )
    print(f"LLM ready in {time.time() - started:.1f}s", flush=True)
    out = llm.generate(["Say hi in one word."], use_tqdm=False)
    print("generated:", out[0].outputs[0].text[:200], flush=True)
    print("PROBE1_OK", flush=True)


if __name__ == "__main__":
    main()
PYFILE
timeout 900 "${VENV}/bin/python" "${DIAG_DIR}/probe_plain_llm.py" \
  >"${DIAG_DIR}/1-plain-llm.log" 2>&1
echo "[1] exit=$? (124 = timed out)"
tail -30 "${DIAG_DIR}/1-plain-llm.log"
snapshot "after-1"

###############################################################################
echo
echo "############ [2] STOCK trl vllm-serve (no jmhgen worker, no text-only) ############"
# If stock TRL also hangs, the custom worker is exonerated and the fault is in
# TRL 1.7.1 against this vLLM build. If stock TRL works, our worker is the fault.
#
# Use the `trl` CONSOLE SCRIPT (as scripts/remote/start-vllm.sh does). `python -m trl.cli` is
# NOT a valid entry point -- "'trl.cli' is a package and cannot be directly executed" -- and it
# exits instantly, which the health loop below then misreports as a hang. Guard against that
# class of mistake by treating an early exit as INCONCLUSIVE, never as evidence.
timeout 600 "${VENV}/bin/trl" vllm-serve \
  --model Qwen/Qwen3.5-4B --max-model-len 8192 \
  --gpu-memory-utilization 0.60 --enforce-eager true --port 8001 \
  >"${DIAG_DIR}/2-stock-trl.log" 2>&1 &
STOCK_PID=$!
# healthy | hung | inconclusive -- an early exit means the invocation was wrong, which is NOT
# evidence about the hang. Conflating the two is how this probe previously produced a confident
# but false "stock TRL also hangs".
STOCK_VERDICT=hung
for i in $(seq 1 55); do
  if ! kill -0 "${STOCK_PID}" 2>/dev/null; then
    STOCK_VERDICT=inconclusive
    echo "[2] serve exited early -- INCONCLUSIVE (bad invocation, not a hang); log:"
    tail -15 "${DIAG_DIR}/2-stock-trl.log"
    break
  fi
  if curl -sf http://127.0.0.1:8001/health/ >/dev/null 2>&1; then
    STOCK_VERDICT=healthy
    echo "[2] STOCK TRL HEALTHY after ~$((i * 10))s -> the jmhgen worker is the fault"
    break
  fi
  (( i % 6 == 0 )) && echo "  [2] waiting ${i}/55 ..." && tail -3 "${DIAG_DIR}/2-stock-trl.log"
  sleep 10
done
echo "[2] verdict=${STOCK_VERDICT}"
if [[ "${STOCK_VERDICT}" == "hung" ]]; then
  echo "[2] STOCK TRL ALSO HANGS -> fault is TRL/vLLM, not the jmhgen worker"
  snapshot "stock-hung"
  [[ -x "${PYSPY}" ]] && "${PYSPY}" dump --pid "${STOCK_PID}" --nonblocking \
    >"${DIAG_DIR}/2-stock-pyspy.txt" 2>&1
fi
kill "${STOCK_PID}" 2>/dev/null; wait "${STOCK_PID}" 2>/dev/null
tail -40 "${DIAG_DIR}/2-stock-trl.log"

###############################################################################
echo
echo "############ [3] jmhgen worker under py-spy (capture the hang stack) ############"
# py-spy must be an ANCESTOR of the target: ptrace_scope=1 refuses siblings, which
# is why `srun --overlap ... py-spy dump` got Permission Denied against the live job.
# Running the server *under* py-spy record makes it traceable.
if [[ -x "${PYSPY}" ]]; then
  timeout 660 "${PYSPY}" record --subprocesses --format raw --rate 5 \
    --duration 600 --output "${DIAG_DIR}/3-jmhgen-stacks.txt" -- \
    "${VENV}/bin/python" -m jmhgen.serving.trl_vllm_serve \
      --model Qwen/Qwen3.5-4B --max-model-len 32768 \
      --gpu-memory-utilization 0.90 --enforce-eager true --port 8002 \
      --language-model-only \
    >"${DIAG_DIR}/3-jmhgen-serve.log" 2>&1 &
  SPY_PID=$!
  for i in $(seq 1 60); do
    if curl -sf http://127.0.0.1:8002/health/ >/dev/null 2>&1; then
      echo "[3] jmhgen worker HEALTHY after ~$((i * 10))s (hang did not reproduce)"; break
    fi
    kill -0 "${SPY_PID}" 2>/dev/null || { echo "[3] exited"; break; }
    (( i % 6 == 0 )) && echo "  [3] waiting ${i}/60 ..." && snapshot "3-t${i}"
    sleep 10
  done
  kill "${SPY_PID}" 2>/dev/null; wait "${SPY_PID}" 2>/dev/null
  echo "--- [3] serve log tail ---"; tail -50 "${DIAG_DIR}/3-jmhgen-serve.log"
  echo "--- [3] hottest stacks (where it spins) ---"
  sort "${DIAG_DIR}/3-jmhgen-stacks.txt" 2>/dev/null | uniq -c | sort -rn | head -25
else
  echo "[3] SKIPPED: py-spy missing at ${PYSPY}"
fi

echo
echo "=== DIAG COMPLETE; artifacts in ${DIAG_DIR} ==="
ls -la "${DIAG_DIR}"
