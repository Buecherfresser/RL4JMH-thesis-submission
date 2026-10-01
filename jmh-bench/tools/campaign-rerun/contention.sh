#!/bin/bash
# Review contention across all 18 machines: is the monitor up, how many CPU
# spikes from other people has it seen, and what is happening right now.
cd "$(dirname "$0")"
xargs -P 5 -I{} ./_contention-one.sh {} < assignment.txt | sort
