#!/bin/bash
# Install and start the contention monitor on every machine.
#   ./deploy-monitor.sh            install + start where not already running
#   ./deploy-monitor.sh restart    replace running monitors (after editing monitor.py)
cd "$(dirname "$0")"
xargs -P 5 -I{} ./_deploy-one.sh {} "${1:-keep}" < assignment.txt | sort
