#!/usr/bin/env bash
# Run the GRPO preflight + reward smoke on an LRZ *compute* node, no GPU.
#
# Why: on the login node the known-good benchmark scored only 0.15 -- it compiled, but the
# JMH run never completed, so the runtime term and the entire 0.70 mutation term were 0.
# Either the login node forbids/starves JMH's forked JVMs, or the reward is broken on LRZ
# generally. The GPU queue is hours long, so settle it on lrz-cpu, which has idle nodes and
# backfills in seconds. The reward is pure CPU work, so this is a faithful test.
#
#   sbatch scripts/cluster/preflight-lrz-cpu.sbatch
#SBATCH --job-name=jmh-preflight-cpu
#SBATCH --partition=lrz-cpu
#SBATCH --qos=cpu
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=32G
#SBATCH --time=00:25:00
#SBATCH --output=/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh/logs/preflight-cpu_%j.out
#SBATCH --error=/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh/logs/preflight-cpu_%j.out

set -uo pipefail
export PYTHONUNBUFFERED=1

JMH_ROOT="${JMH_ROOT:-/dss/dssfs04/lwp-dss-0002/pr28qe/pr28qe-dss-0001/go68bef2/jmh}"
REPO="${REPO:-${JMH_ROOT}/JMH_Training_Pipeline}"
cd "${REPO}" || exit 1
# shellcheck source=/dev/null
source "${REPO}/scripts/cluster/env.lrz.sh"
export UV_PROJECT_ENVIRONMENT="${JMH_ROOT}/.venv-gemma-cu130"
PY="${UV_PROJECT_ENVIRONMENT}/bin/python"

echo "=== node=$(hostname) job=${SLURM_JOB_ID} cpus=${SLURM_CPUS_PER_TASK:-?} ==="
echo "JAVA_HOME=${JAVA_HOME}"
java -version 2>&1 | head -1
javac -version
echo "TMPDIR=${TMPDIR} (local scratch: ${JMH_LOCAL_SCRATCH:-none})"
df -h "${TMPDIR}" 2>/dev/null | tail -1
ulimit -a | grep -Ei "processes|files|memory" || true

echo
echo "=== preflight + reward smoke on a compute node ==="
"${PY}" scripts/preflight_grpo.py configs/grpo/mutation70-gemma.yaml \
  --corpus full --reward-smoke 2>&1 | grep -vE "loaded [0-9]+ (classpath|mutant)"
echo "PREFLIGHT_RC=${PIPESTATUS[0]}"

echo
echo "=== if the JMH run failed, show why ==="
"${PY}" - <<'PYEOF' 2>&1 | tail -40
import sys, yaml
from pathlib import Path

sys.path.insert(0, "scripts")
from preflight_grpo import SMOKE_CLASS_NAME, SMOKE_COMPLETION, SMOKE_PACKAGE

from jmhgen.config.schema import GRPOConfig
from jmhgen.data.classpath import load_classpath
from jmhgen.generate.parse import parse_completion
from jmhgen.rewards.grpo_adapter import build_benchmark_spec

cfg = GRPOConfig(**yaml.safe_load(Path("configs/grpo/mutation70-gemma.yaml").read_text()))
cp = load_classpath(Path(cfg.project_classpaths["commons-codec"]))
parsed = parse_completion(SMOKE_COMPLETION, fallback_package=SMOKE_PACKAGE,
                          fallback_class=SMOKE_CLASS_NAME)
spec = build_benchmark_spec(parsed.java_source, SMOKE_CLASS_NAME, SMOKE_PACKAGE, cp)
runner = cfg.runner.build_runner()

compiled = runner.compile(spec)
print("compile:", compiled.success, "in", round(compiled.duration_s, 2), "s")
if not compiled.success:
    print((compiled.stdout or "")[-2500:])
    raise SystemExit(0)

run = runner.run(spec, cfg.runner.jmh.to_options())
print("run success:", run.success, "duration:", round(run.duration_s, 2),
      "error_kind:", getattr(run, "error_kind", None))
for attr in ("stdout", "stderr"):
    val = getattr(run, attr, "")
    if val:
        print(f"\n--- run {attr} tail ---")
        print(val[-3000:])
PYEOF
echo "=== DONE ==="
