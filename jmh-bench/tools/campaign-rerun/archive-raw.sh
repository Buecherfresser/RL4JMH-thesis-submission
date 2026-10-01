#!/bin/bash
# Archive raw campaign results off the machines onto the SSD.
#
#   ./archive-raw.sh [assignment-file] [--force]
#
# Re-runnable: only cells with a finished scorecard.json are pulled, and a cell
# already on the SSD is skipped unless --force. Run again as the rest finish.
#
# Tars on the remote first: the payload is thousands of small JMH JSON files,
# which transfer far slower one-by-one than as a single stream.
#
# What is kept, and why each piece is needed to recompute a metric from scratch:
#
#   bundle/                     the model's ORIGINAL generated .java, unfiltered,
#                               plus model_output.md -- the raw LLM response
#   out/*/report/generated/     the sources that were actually BENCHMARKED, i.e.
#                               after the compile and runtime filters dropped
#                               classes; which ones were dropped, and why, is in
#                               the scorecard's compile_filter_dropped /
#                               runtime_filter_dropped
#   out/*/report/scorecard.json per-fork samples for every base/mutant pair
#   scratch/**/jmh-results-*.json  raw JMH output for every single invocation
#   monitor/                    CPU contention during the run
#   ARCHIVE-HOSTINFO.txt        host, CPU model, per-core perf classes
#
# Deliberately NOT kept: the materialised project under scratch/ (SUT copy, pom,
# target/). It is large and fully regenerable from bundle + the vendored dataset,
# both of which are archived.
set -uo pipefail
cd "$(dirname "$0")"
ASSIGN=assignment.txt; FORCE=""
for a in "$@"; do case "$a" in --force) FORCE=1;; *) ASSIGN="$a";; esac; done
DEST=${DEST:-/Volumes/SamsungSSD/jmh-thesis/campaign-rerun-2026-08}
REPO=${REPO:-$(cd ../.. && pwd)}

[ -f "$ASSIGN" ] || { echo "no assignment file: $ASSIGN"; exit 1; }
parent=$(dirname "$DEST")
[ -d "$parent" ] || { echo "destination parent missing: $parent"; exit 1; }
mkdir -p "$DEST/_archives"

while read -r h model proj camp ts; do
  [ -n "${h:-}" ] || continue
  cell="$model-$proj"
  if [ -d "$DEST/$cell" ] && [ -z "$FORCE" ]; then
    echo "skip     $cell (already on SSD)"; continue
  fi
  if ! ssh -n -o BatchMode=yes -o ConnectTimeout=20 "$h" \
        "test -f /var/tmp/jmhb/out/$cell/report/scorecard.json" 2>/dev/null; then
    echo "pending  $cell ($h - still running or unreachable)"; continue
  fi

  remote_sha=$(ssh -n -o BatchMode=yes -o ConnectTimeout=30 "$h" "
    set -e
    cd /var/tmp/jmhb
    { echo 'cell: $cell'; echo \"host: \$(hostname -f)\"; echo \"archived: \$(date -Iseconds)\"
      echo \"campaign: ${camp:-?} ${ts:-}\"
      echo; echo '## cpu'; lscpu | grep -E 'Model name|^CPU\(s\)|Thread\(s\)|Core\(s\)'
      echo; echo '## per-core CPPC highest_perf (hybrid check)'
      for c in /sys/devices/system/cpu/cpu[0-9]*; do
        printf '%s=%s ' \"\${c##*/cpu}\" \"\$(cat \$c/acpi_cppc/highest_perf 2>/dev/null)\"
      done; echo
      echo; echo '## disk'; df -h /var/tmp /tmp
    } > out/$cell/ARCHIVE-HOSTINFO.txt
    find scratch/$cell -name 'jmh-results-*.json' > /tmp/jsonlist.\$\$ 2>/dev/null || :
    tar czf /var/tmp/$cell-raw.tar.gz out/$cell monitor -T /tmp/jsonlist.\$\$
    rm -f /tmp/jsonlist.\$\$
    sha256sum /var/tmp/$cell-raw.tar.gz | cut -d' ' -f1" 2>/dev/null | tail -1)

  if [ -z "$remote_sha" ]; then echo "FAILED   $cell (remote tar)"; continue; fi
  if ! scp -q -o BatchMode=yes "$h":/var/tmp/"$cell"-raw.tar.gz "$DEST/_archives/" 2>/dev/null; then
    echo "FAILED   $cell (transfer)"; continue
  fi
  local_sha=$(shasum -a 256 "$DEST/_archives/$cell-raw.tar.gz" | cut -d' ' -f1)
  if [ "$remote_sha" != "$local_sha" ]; then
    echo "CHECKSUM MISMATCH $cell"; rm -f "$DEST/_archives/$cell-raw.tar.gz"; continue
  fi
  mkdir -p "$DEST/$cell"
  tar xzf "$DEST/_archives/$cell-raw.tar.gz" -C "$DEST/$cell"

  # The original, pre-filter generated sources live in the repo, not on the
  # machine. Copy them in so each cell directory stands alone.
  src=""
  if [ -n "${camp:-}" ]; then
    if [ -n "${ts:-}" ] && [ -d "$REPO/reports/$camp/$ts/bundles/project-$proj-t0-think" ]; then
      src="$REPO/reports/$camp/$ts/bundles/project-$proj-t0-think"
    else
      src=$(ls -d "$REPO/reports/$camp"/*/bundles/project-"$proj"-t0-think 2>/dev/null | head -1)
    fi
  fi
  nb=0
  if [ -n "$src" ] && [ -d "$src" ]; then
    mkdir -p "$DEST/$cell/bundle"
    rsync -a "$src/" "$DEST/$cell/bundle/"
    nb=$(find "$DEST/$cell/bundle" -name '*.java' | wc -l | tr -d ' ')
  fi

  # completeness gate -- an archive missing sources cannot reproduce anything
  ng=$(find "$DEST/$cell/out/$cell/report/generated" -name '*.java' 2>/dev/null | wc -l | tr -d ' ')
  nj=$(find "$DEST/$cell" -name 'jmh-results-*.json' | wc -l | tr -d ' ')
  mo=$([ -f "$DEST/$cell/bundle/model_output.md" ] || [ -f "$DEST/$cell/out/$cell/report/model_output.md" ] && echo yes || echo NO)
  warn=""
  [ "$nb" -eq 0 ] && warn="$warn NO-BUNDLE-JAVA"
  [ "$ng" -eq 0 ] && warn="$warn NO-BENCHMARKED-JAVA"
  [ "$mo" = NO ] && warn="$warn NO-MODEL-OUTPUT"

  cat > "$DEST/$cell/REPRODUCIBILITY.txt" <<TXT
cell: $cell
campaign: ${camp:-?} ${ts:-}
host: $h
tarball sha256: $local_sha

bundle/                    $nb .java  -- the model's original output, unfiltered
out/$cell/report/generated/ $ng .java  -- what was actually benchmarked, after
                                         the compile + runtime filters
out/$cell/report/scorecard.json        -- per-fork samples for every pair
raw JMH invocation results  $nj json

The filtered-out classes are the difference between the two .java counts; the
scorecard lists them under compile_filter_dropped and runtime_filter_dropped.
The materialised build tree is not kept: it regenerates from bundle/ plus the
vendored SUT in local/jmh-bench-code/dataset/projects/$proj/.
TXT

  sz=$(du -sh "$DEST/$cell" | cut -f1 | tr -d ' ')
  echo "PULLED   $cell  ($sz, $nj raw json, $nb bundle .java, $ng benchmarked .java)${warn:+  ** $warn **}"
  ssh -n -o BatchMode=yes "$h" "rm -f /var/tmp/$cell-raw.tar.gz" 2>/dev/null
done < "$ASSIGN"
