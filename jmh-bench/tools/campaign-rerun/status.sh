#!/bin/bash
# Status of the 18-machine campaign rerun. Read-only; safe to run any time.
# Concurrency is capped at 5: the ProxyJump host resets connections past ~6.
cd "$(dirname "$0")"

cat > /tmp/.jmhb_one.sh <<'INNER'
#!/bin/bash
read -r h model proj _ <<< "$1"
o=$(ssh -o BatchMode=yes -o ConnectTimeout=25 "$h" '
  cell=$(basename $(ls -d /var/tmp/jmhb/out/*/ 2>/dev/null | head -1) 2>/dev/null)
  log=/var/tmp/jmhb/out/$cell/run.log
  sc=/var/tmp/jmhb/out/$cell/report/scorecard.json
  pid=$(pgrep -f "jmhbench project-bench" | head -1)
  if [ -f "$sc" ]; then
    python3 -c "
import json
d=json.load(open(\"$sc\"))[\"result\"]
# per-fork samples live on each *detection*, not on the mutant record itself
nf=0
for m in (d.get(\"mutant_results\") or []):
    for det in (m.get(\"detections\") or []):
        nf=max(nf, len(det.get(\"base_samples_by_fork\") or []))
cpu=(d.get(\"host\") or {}).get(\"cpu\") or {}
pin=\"pinned\" if cpu.get(\"affinity_uniform_perf\") else \"MIXED-CORE\"
print(\"DONE   killed=%s/%s  score=%.3f  n_forks=%s  %s\"%(
    d.get(\"mutants_killed\"),d.get(\"mutants_covered\"),d.get(\"mutation_score\") or 0,nf or \"?\",pin))"
  elif [ -n "$pid" ]; then
    el=$(ps -o etimes= -p $pid | tr -d " ")
    # most recent stage marker, whichever stage it is
    st=$(grep -oE "baseline [0-9]+/[0-9]+|mutant [0-9]+/[0-9]+|arming [0-9]+/[0-9]+|coverage [0-9]+/[0-9]+" $log 2>/dev/null | tail -1)
    if [ -z "$st" ]; then
      st=$(grep -oE "^  . (materialising|building|runtime smoke probe|runtime-and-filter|JMH baseline run)[^—]*" $log 2>/dev/null | tail -1 | sed "s/^  . //;s/ *$//")
      st="${st:-starting}"
    fi
    awk -v s="$st" -v e="$el" "BEGIN{printf \"RUN    %-22s %5.1fh elapsed\", s, e/3600}"
  else
    echo "STOPPED  $(tail -2 $log 2>/dev/null | tr "\n" " " | tail -c 100)"
  fi' 2>&1 | tail -1)
printf "%-9s %-18s %-17s %s\n" "$h" "$model" "$proj" "$o"
INNER
chmod +x /tmp/.jmhb_one.sh
xargs -P 5 -I{} /tmp/.jmhb_one.sh {} < assignment.txt | sort
