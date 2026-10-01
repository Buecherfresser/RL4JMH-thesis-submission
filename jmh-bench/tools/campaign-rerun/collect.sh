#!/bin/bash
# Fetch finished results off the machines into ./collected/<cell>/.
# Re-runnable: rsync only pulls what changed, and unfinished cells are skipped.
#
# ssh is invoked with -n throughout: without it ssh drains stdin, eats the rest
# of assignment.txt, and the loop silently processes only the first host.
cd "$(dirname "$0")"
DEST=${1:-./collected}
mkdir -p "$DEST"
while read -r h model proj _; do
  [ -n "$h" ] || continue
  cell="$model-$proj"
  if ssh -n -o BatchMode=yes -o ConnectTimeout=20 "$h" \
       "test -f /var/tmp/jmhb/out/$cell/report/scorecard.json" 2>/dev/null; then
    mkdir -p "$DEST/$cell"
    if rsync -az -e "ssh -o BatchMode=yes" \
         --exclude 'report.tmpfull-*' \
         "$h:/var/tmp/jmhb/out/$cell/" "$DEST/$cell/"; then
      echo "PULLED  $cell  <- $h"
    else
      echo "RSYNC-FAILED  $cell  <- $h"
    fi
  else
    echo "skip    $cell  ($h - not finished)"
  fi
done < assignment.txt
