#!/bin/bash
# Copy the local thesis working data onto the SSD.
#
# COPIES, never deletes: verify the SSD side before removing anything locally.
#
# Two forms on purpose. The destination is exFAT, which stores no POSIX
# permission bits and no symlinks, and allocates 128K per file -- so a faithful
# .git tree cannot survive a plain copy, and 66k small files would balloon from
# 1.5G to ~7.8G. The tarballs are therefore the canonical archive; the extracted
# trees exist only so the raw data can be read without unpacking.
set -uo pipefail
SRC=/Users/jonas/BSc_Thesis
DEST=${DEST:-/Volumes/SamsungSSD/jmh-thesis/local}
[ -d /Volumes/SamsungSSD ] || { echo "SSD not mounted"; exit 1; }
mkdir -p "$DEST"

EXCL=(--exclude '.venv' --exclude '__pycache__' --exclude '.pytest_cache'
      --exclude '.DS_Store' --exclude '*.egg-info' --exclude '.mypy_cache')

echo "== 1/4  jmh-bench repo -> tarball (canonical: keeps .git, perms) =="
tar czf "$DEST/jmh-bench-repo.tar.gz" \
  --exclude '.venv' --exclude '__pycache__' --exclude '.pytest_cache' \
  --exclude '.DS_Store' --exclude '*.egg-info' --exclude '.mypy_cache' \
  -C "$SRC/Codebases" JMH-Bench
echo "   $(du -sh "$DEST/jmh-bench-repo.tar.gz" | cut -f1)"

echo "== 2/4  evaluation -> tarball + extracted =="
tar czf "$DEST/evaluation.tar.gz" "${EXCL[@]}" -C "$SRC" evaluation
rsync -a "${EXCL[@]}" "$SRC/evaluation/" "$DEST/evaluation/"

echo "== 3/4  jmh-bench reports -> extracted (raw campaign outputs) =="
rsync -a "${EXCL[@]}" "$SRC/Codebases/JMH-Bench/reports/" "$DEST/jmh-bench-reports/"

echo "== 4/4  jmh-bench code + dataset -> extracted (no .git) =="
rsync -a "${EXCL[@]}" --exclude '.git' --exclude 'reports' \
  "$SRC/Codebases/JMH-Bench/" "$DEST/jmh-bench-code/"

echo
echo "== verification: file counts source vs destination =="
for pair in "evaluation:$SRC/evaluation:$DEST/evaluation" \
            "reports:$SRC/Codebases/JMH-Bench/reports:$DEST/jmh-bench-reports"; do
  name=${pair%%:*}; rest=${pair#*:}; s=${rest%%:*}; d=${rest#*:}
  sc=$(find "$s" -type f ! -name '.DS_Store' 2>/dev/null | grep -v -e __pycache__ -e '\.pyc$' | wc -l | tr -d ' ')
  dc=$(find "$d" -type f ! -name '.DS_Store' 2>/dev/null | grep -v -e __pycache__ -e '\.pyc$' | wc -l | tr -d ' ')
  [ "$sc" = "$dc" ] && v="OK" || v="MISMATCH"
  printf "  %-12s src=%-7s dst=%-7s %s\n" "$name" "$sc" "$dc" "$v"
done
echo "  tarball integrity:"
for t in "$DEST/jmh-bench-repo.tar.gz" "$DEST/evaluation.tar.gz"; do
  if tar tzf "$t" >/dev/null 2>&1; then echo "    OK  $(basename "$t")"; else echo "    CORRUPT $(basename "$t")"; fi
done
