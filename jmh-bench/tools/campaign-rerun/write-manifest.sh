#!/bin/bash
# Regenerate MANIFEST.txt on the archive root. Kept as a script rather than an
# inline heredoc: nested command substitution inside a heredoc silently emitted
# literal $var text the one time it was done that way.
set -uo pipefail
cd "$(dirname "$0")"
R=${R:-/Volumes/SamsungSSD/jmh-thesis}
D="$R/campaign-rerun-2026-08"
[ -d "$D" ] || { echo "no archive root at $D"; exit 1; }
M="$R/MANIFEST.txt"

{
  echo "Generated: $(date -Iseconds)"
  echo
  echo "CELLS ARCHIVED"
  for c in "$D"/*/; do
    b=$(basename "$c"); [ "$b" = "_archives" ] && continue
    nj=$(find "$c" -name 'jmh-results-*.json' 2>/dev/null | wc -l | tr -d ' ')
    nb=$(find "$c/bundle" -name '*.java' 2>/dev/null | wc -l | tr -d ' ')
    ng=$(find "$c"/out/*/report/generated -name '*.java' 2>/dev/null | wc -l | tr -d ' ')
    p=""; [ -f "$c/PARTIAL.txt" ] && p=" PARTIAL"
    printf "  %-38s %6s raw json  %4s bundle .java  %4s benchmarked%s\n" "$b" "$nj" "$nb" "$ng" "$p"
  done
  echo
  echo "NOT YET ARCHIVED"
  n=0
  cat assignment.txt assignment-base.txt 2>/dev/null | while read -r h m pr rest; do
    [ -n "${h:-}" ] || continue
    [ -d "$D/$m-$pr" ] || printf "  %-38s (%s)\n" "$m-$pr" "$h"
  done
  echo
  echo "LOCAL"
  (cd "$R/local" 2>/dev/null && du -sh * 2>/dev/null | sed 's/^/  /')
} > "$M"
echo "wrote $M"
