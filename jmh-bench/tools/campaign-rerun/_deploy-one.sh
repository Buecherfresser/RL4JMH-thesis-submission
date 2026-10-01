#!/bin/bash
# helper for deploy-monitor.sh -- install+start the monitor on one host.
# $2 = "restart" to replace a running monitor (use after changing monitor.py).
# Liveness is a pid file, not `pgrep -f`: the launch command line itself
# contains "monitor.py", so any -f pattern matches the checking shell and
# reports "already running" on every host whether or not one is up.
read -r h _ <<< "$1"
MODE=${2:-keep}
DIR="$(cd "$(dirname "$0")" && pwd)"
scp -q -o BatchMode=yes -o ConnectTimeout=20 "$DIR/monitor.py" "$h":/var/tmp/monitor.py 2>/dev/null
r=$(ssh -o BatchMode=yes -o ConnectTimeout=20 "$h" "
P=/var/tmp/jmhb/monitor/monitor.pid
alive(){ [ -f \$P ] && kill -0 \"\$(cat \$P)\" 2>/dev/null; }
if [ '$MODE' = restart ] && alive; then kill \"\$(cat \$P)\" 2>/dev/null; sleep 1; fi
if alive; then
  echo \"already-running pid=\$(cat \$P)\"
else
  mkdir -p /var/tmp/jmhb/monitor
  setsid nohup python3 /var/tmp/monitor.py >/var/tmp/jmhb/monitor/monitor.err 2>&1 </dev/null &
  disown; sleep 2
  alive && echo \"started pid=\$(cat \$P)\" || echo \"FAILED \$(tail -2 /var/tmp/jmhb/monitor/monitor.err 2>/dev/null)\"
fi" 2>/dev/null | tail -1)
printf "%-9s %s\n" "$h" "${r:-UNREACHABLE}"
