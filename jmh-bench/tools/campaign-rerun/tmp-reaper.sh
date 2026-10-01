#!/bin/bash
# Delete abandoned SUT temp files from the run's scratch tmpdir.
#
# jodd-util creates temp files and never removes them: ~4M jodd-*.tmp in one
# 8h cell. With -Djava.io.tmpdir they now land on /var/tmp (21.4M inodes)
# instead of /tmp (4M, and RAM-backed), but a 28h cell still projects to ~10M
# files, so the headroom is not comfortable enough to leave unattended.
#
# Only files not modified in the last 20 minutes are removed. A JMH invocation
# is ~120s, so nothing a running benchmark is still using is old enough to
# qualify -- and on Linux unlinking a file another process holds open is safe
# regardless, since the inode survives until the last descriptor closes.
#
# Exits once the harness is gone, so it does not linger on a shared machine.
TMPD=${1:-/var/tmp/jmhb/tmp}
while true; do
  sleep 600
  find "$TMPD" -maxdepth 1 -type f -name '*.tmp' -mmin +20 -delete 2>/dev/null
  pgrep -f "[j]mhbench project-bench" >/dev/null || { sleep 60; \
    pgrep -f "[j]mhbench project-bench" >/dev/null || exit 0; }
done
