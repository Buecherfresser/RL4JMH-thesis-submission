#!/usr/bin/env bash
# Start the unified mutation RFT job (all projects, one sequential tmux session).
set -euo pipefail

REMOTE="${JMHGEN_REMOTE_HOST:-bsc-gcp}"
REMOTE_DIR="${JMHGEN_REMOTE_DIR:-~/jmhgen-rft}"
SESSION="${JMHGEN_TMUX_SESSION:-jmhgen-rft}"
MANIFEST="${JMHGEN_MUTATION_MANIFEST:-configs/rft/mutation-projects.yaml}"
PHASES="${JMHGEN_RFT_PHASES:-generate,verify,build}"
LOG="${REMOTE_DIR}/logs/rft-$(date +%Y%m%d-%H%M%S).log"

ssh "${REMOTE}" bash -s -- "${REMOTE_DIR}" "${SESSION}" "${MANIFEST}" "${PHASES}" "${LOG}" <<'EOF'
set -euo pipefail
REMOTE_DIR="$1"
SESSION="$2"
MANIFEST="$3"
PHASES="$4"
LOG="$5"
cd "${REMOTE_DIR}"
if tmux has-session -t "${SESSION}" 2>/dev/null; then
  echo "tmux session ${SESSION} already running"
  tmux capture-pane -pt "${SESSION}" -S -30
  exit 0
fi
RUN="set -euo pipefail; cd ${REMOTE_DIR}; export PATH=\"\$HOME/.local/bin:\$PATH\"; set -a; source .env; set +a; uv run python scripts/run_rft_phases.py --manifest ${MANIFEST} --bsc-gcp --phases ${PHASES} 2>&1 | tee -a ${LOG}"
tmux new-session -d -s "${SESSION}" "bash -lc $(printf %q "${RUN}")"
echo "started tmux session ${SESSION}"
echo "log: ${LOG}"
EOF

echo
echo "Unified job: ${MANIFEST} (generate all → verify all → build all; phases=${PHASES})"
echo "Attach:  ssh ${REMOTE} -t tmux attach -t ${SESSION}"
echo "Tail:    ssh ${REMOTE} tail -f ${LOG}"
echo "Status:  scripts/bsc-gcp/status.sh"
