#!/bin/bash
read -r h model proj _ <<< "$1"
out=$(ssh -o BatchMode=yes -o ConnectTimeout=25 "$h" '
M=/var/tmp/jmhb/monitor; P=$M/monitor.pid
up=down; [ -f $P ] && kill -0 "$(cat $P)" 2>/dev/null && up=up
# head -1: grep -c exits 1 on a zero count, so a `|| echo 0` fallback would
# append a second line and corrupt the record separator below.
sp=$(grep -c SPIKE $M/events.jsonl 2>/dev/null | head -1); sp=${sp:-0}
worst=$(grep SPIKE $M/events.jsonl 2>/dev/null | python3 -c "
import sys,json
b=None
for l in sys.stdin:
    try: d=json.loads(l)
    except Exception: continue
    if b is None or d[\"other_cpu_pct\"]>b[\"other_cpu_pct\"]: b=d
print(f\"{b[\"other_cpu_pct\"]}%@{b[\"t\"][11:16]} {b.get(\"by_slice\") or \"\"}\" if b else \"-\")" 2>/dev/null | head -1)
printf "%s|%s|%s|%s\n" "$up" "$sp" "${worst:--}" "$(cat $M/latest.json 2>/dev/null)"' 2>/dev/null | tail -1)
python3 - "$h" "$model" "$proj" "$out" <<'PY'
import sys, json
h, model, proj, line = sys.argv[1:5]
parts = line.split("|", 3)
if len(parts) < 4:
    print(f"{h:<9} {model:<18} {proj:<17} NO DATA ({line[:40]})"); raise SystemExit
up, sp, worst, last = parts
try: d = json.loads(last)
except Exception: d = {}
print(f"{h:<9} {model:<18} {proj:<17} mon={up:<4} spikes={sp:<3} "
      f"ours={d.get('our_cpu_pct','?'):>6}% other={d.get('other_cpu_pct','?'):>5}% "
      f"users={','.join(d.get('users') or []) or '-':<9} worst={worst}")
PY
