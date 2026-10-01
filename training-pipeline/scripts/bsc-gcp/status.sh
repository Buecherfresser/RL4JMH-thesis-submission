#!/usr/bin/env bash
set -euo pipefail

REMOTE="${JMHGEN_REMOTE_HOST:-bsc-gcp}"
REMOTE_DIR="${JMHGEN_REMOTE_DIR:-~/jmhgen-rft}"
SESSION="${JMHGEN_TMUX_SESSION:-jmhgen-rft}"
MANIFEST="${JMHGEN_MUTATION_MANIFEST:-configs/rft/mutation-projects.yaml}"

ssh "${REMOTE}" "bash -lc '
  echo \"=== tmux ${SESSION} (unified mutation job) ===\"
  if tmux has-session -t ${SESSION} 2>/dev/null; then
    tmux capture-pane -pt ${SESSION} -S -50
  else
    echo \"(not running)\"
  fi
  echo
  echo \"=== work_dir artifacts ===\"
  cd ${REMOTE_DIR}
  uv run python - \"${MANIFEST}\" <<\"PY\"
import sys
from pathlib import Path
from jmhgen.config.mutation_manifest import load_mutation_manifest
from jmhgen.config.schema import RFTConfig

manifest = load_mutation_manifest(sys.argv[1])
for project in manifest.projects:
    cfg = RFTConfig.from_yaml(project.bsc_gcp_config)
    work = Path(cfg.work_dir)
    print(f\"--- {project.id} ({work}) ---\")
    if not work.is_dir():
        print(\"  (no work_dir yet)\")
        continue
    for name in (\"generations.jsonl\", \"scored.jsonl\", \"sft.jsonl\", \"report.json\"):
        path = work / name
        if path.is_file():
            print(f\"  {name}: {path.stat().st_size} bytes\")
        else:
            print(f\"  {name}: (missing)\")
PY
  echo
  echo \"=== latest log ===\"
  ls -t ${REMOTE_DIR}/logs/rft-*.log 2>/dev/null | head -1 | xargs -r tail -20
'"
