#!/usr/bin/env bash
# Phase 2 of the GRPO-Gemma evaluation: benchmark the bundles that
# `eval-gemma-matrix-hpi.sbatch` generated on hpi2.
#
# Run this from your laptop. It pulls the run directory off hpi2 and then hands the bundles to
# JMH-Bench's own tools/run_eval.sh in SKIP_GEN mode, which rsyncs each bundle to the bench box,
# runs `jmhbench bench` / `project-bench` there one at a time, and pulls the report back.
#
# Why a quiet box and not the GPU node: JMH decides "regression" by whether an armed mutant is
# >=1.10x slower than baseline at p < 0.05. A neighbouring job stealing cores either buries a real
# regression or manufactures one. Benchmarking is also strictly sequential for the same reason --
# run_eval.sh does one bundle at a time on purpose.
#
#   scripts/cluster/bench-gemma-matrix.sh /sc/scratch/.../reports/gemma4-e2b/grpo-mutation70/<stamp>
#   BENCH_HOST=bsc-gcp scripts/cluster/bench-gemma-matrix.sh <remote_run_dir>
set -euo pipefail

REMOTE_RUN_DIR="${1:?usage: $0 <RUN_DIR on hpi2> }"
# fsc04: 12 cores / 30 GB. bsc-gcp: 4 cores / 15 GB. Prefer fsc04 unless it is busy -- more cores
# means the Maven builds finish sooner, and JMH pins its forks regardless.
BENCH_HOST="${BENCH_HOST:-fsc04.local}"
JMHB="${JMHB:-${HOME}/BSc_Thesis/Codebases/JMH-Bench}"
LOCAL_RUN="${LOCAL_RUN:-${JMHB}/reports/gemma4-e2b/grpo-mutation70/$(basename "${REMOTE_RUN_DIR}")}"

[[ -d "${JMHB}" ]] || { echo "FATAL: JMH-Bench not at ${JMHB}" >&2; exit 2; }

echo "=== bench host check: ${BENCH_HOST} ==="
ssh -o BatchMode=yes -o ConnectTimeout=30 "${BENCH_HOST}" \
  'echo "  host=$(hostname) cpus=$(nproc) load=$(cut -d" " -f1 /proc/loadavg)"
   [ -x ~/jmhbench-remote/.venv/bin/jmhbench ] || { echo "  FATAL: ~/jmhbench-remote venv missing" >&2; exit 2; }
   java -version 2>&1 | head -1 | sed "s/^/  /"'

echo "=== pulling bundles: hpi2:${REMOTE_RUN_DIR} -> ${LOCAL_RUN} ==="
mkdir -p "${LOCAL_RUN}"
rsync -az "hpi2:${REMOTE_RUN_DIR}/" "${LOCAL_RUN}/"
echo "bundles pulled:"
ls "${LOCAL_RUN}/bundles" 2>/dev/null | sed 's/^/  /'

echo "=== benchmarking on ${BENCH_HOST} (sequential, one bundle at a time) ==="
cd "${JMHB}"
# REMOTE / SKIP_GEN / RUN_DIR survive run_eval.sh's `source .env` -- it preserves those three
# explicitly, which is exactly why they are passed as env rather than edited into .env.
SKIP_GEN=1 REMOTE="${BENCH_HOST}" RUN_DIR="${LOCAL_RUN}" bash tools/run_eval.sh

echo
echo "=== reports ==="
for s in "${LOCAL_RUN}"/reports/*/summary.md; do
  [[ -f "${s}" ]] || continue
  echo "--- $(basename "$(dirname "${s}")") ---"
  grep -E "Composite pass|\| Compiles|\| Executes|Regression detection|mutation score" "${s}" | head -5
done
echo
echo "Cross-variant report:  cd ${JMHB} && uv run python tools/generate_evaluation_report.py"
