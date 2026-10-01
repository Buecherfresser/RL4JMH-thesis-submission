#!/bin/bash
# Add bundle/ (the model's original generated .java) and REPRODUCIBILITY.txt to
# archived cells that predate those being part of archive-raw.sh.
#
# Purely local: reads the repo and writes to the SSD, so it works for cells whose
# machine has since been wiped and reassigned.
#
# No associative arrays -- macOS ships bash 3.2, which does not have them.
set -uo pipefail
cd "$(dirname "$0")"
DEST=${DEST:-/Volumes/SamsungSSD/jmh-thesis/campaign-rerun-2026-08}
REPO=$(cd ../.. && pwd)

campaign_of() {
  case "$1" in
    dsv4)              echo "reports/deepseek-v4-flash-0731/project/aug12" ;;
    gemma-run9-ck1600) echo "reports/gemma4-e2b/project/aug12-run9-ck1600" ;;
    gpt-oss-120b)      echo "reports/gpt-oss-120b/project/aug12" ;;
    gemma-base)        echo "reports/gemma4-e2b/project/base" ;;
  esac
}

for d in "$DEST"/*/; do
  cell=$(basename "$d"); [ "$cell" = "_archives" ] && continue
  if [ -f "$d/REPRODUCIBILITY.txt" ] && [ -d "$d/bundle" ]; then echo "ok       $cell"; continue; fi
  # longest matching prefix, so gemma-run9-ck1600 wins over a shorter gemma-*
  model=""
  for m in dsv4 gemma-run9-ck1600 gpt-oss-120b gemma-base; do
    case "$cell" in "$m"-*) [ ${#m} -gt ${#model} ] && model=$m ;; esac
  done
  [ -n "$model" ] || { echo "SKIP     $cell (unknown model prefix)"; continue; }
  proj=${cell#${model}-}
  camp=$(campaign_of "$model")
  src=$(ls -d "$REPO/$camp"/*/bundles/project-"$proj"-t0-think 2>/dev/null | head -1)
  [ -n "$src" ] || { echo "SKIP     $cell (no bundle in repo)"; continue; }
  mkdir -p "$d/bundle"; rsync -a "$src/" "$d/bundle/"
  nb=$(find "$d/bundle" -name '*.java' | wc -l | tr -d ' ')
  ng=$(find "$d/out/$cell/report/generated" -name '*.java' 2>/dev/null | wc -l | tr -d ' ')
  nj=$(find "$d" -name 'jmh-results-*.json' | wc -l | tr -d ' ')
  cat > "$d/REPRODUCIBILITY.txt" <<TXT
cell: $cell
campaign: $camp ($(basename "$(dirname "$(dirname "$src")")"))

bundle/                     $nb .java  -- the model's original output, unfiltered
out/$cell/report/generated/ $ng .java  -- what was actually benchmarked, after
                                          the compile + runtime filters
out/$cell/report/scorecard.json        -- per-fork samples for every pair
raw JMH invocation results  $nj json

The filtered-out classes are the difference between the two .java counts; the
scorecard lists them under compile_filter_dropped and runtime_filter_dropped.
The materialised build tree is not kept: it regenerates from bundle/ plus the
vendored SUT in local/jmh-bench-code/dataset/projects/$proj/.
TXT
  echo "BACKFILL $cell  ($nb bundle .java, $ng benchmarked .java, $nj raw json)"
done
