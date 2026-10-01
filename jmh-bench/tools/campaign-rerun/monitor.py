#!/usr/bin/env python3
"""Per-machine contention sampler for the campaign rerun.

Answers one question once a minute: how much CPU on this box is *not* ours,
and who is causing it. Runs on the benchmark machine rather than being polled
over SSH -- 18 hosts x 1/min through the ProxyJump would trip its connection
limit, and polling would stop the moment the laptop sleeps.

Accounting is done with **cgroup v2 counters, not a /proc process scan**. The
scan approach undercounts badly here: JMH forks a fresh JVM per fork (5 per
invocation, ~20s each), so most of our JVMs are born and reaped between two
60s samples and appear in neither snapshot. Measured side by side, a per-PID
scan reported 6-25% of a core while the cgroup reported 159%. The same blind
spot would hide precisely the short interfering job this monitor exists to
catch. `cpu.stat` accumulates descendants including exited ones, so nothing is
missed regardless of process lifetime.

  ours   = delta of usage_usec on the harness's own session scope
  total  = delta of busy jiffies from /proc/stat
  other  = total - ours          <- everything not caused by this run

Percentages are top-style: 100 == one core saturated, so a 12-thread box tops
out at 1200.

Writes to /var/tmp/jmhb/monitor/:
  events.jsonl   append-only, spikes + hourly heartbeats (rotated at 5 MB)
  latest.json    last sample, overwritten -- cheap liveness check
  monitor.pid    this process's pid
"""
from __future__ import annotations

import json
import os
import time
from pathlib import Path

OUT = Path("/var/tmp/jmhb/monitor")
CG = Path("/sys/fs/cgroup")
INTERVAL = 60
SPIKE_PCT = 30.0          # of one core; below this is daemon noise
PINNED = {1, 2, 3, 5, 7, 8, 9, 11}
HEARTBEAT_EVERY = 60      # samples -> hourly
MAX_EVENTS_BYTES = 5 * 1024 * 1024
CLK = os.sysconf("SC_CLK_TCK")
OUR_UID = os.getuid()


def read_int(path: Path, key: str) -> int | None:
    try:
        for line in path.read_text().splitlines():
            if line.startswith(key):
                return int(line.split()[1])
    except (OSError, ValueError, IndexError):
        pass
    return None


def cpu_totals() -> tuple[int, dict[int, int], dict[int, int]]:
    """Busy jiffies machine-wide, plus (busy, total) per cpu."""
    total_busy = 0
    busy: dict[int, int] = {}
    tot: dict[int, int] = {}
    try:
        for line in Path("/proc/stat").read_text().splitlines():
            if not line.startswith("cpu"):
                break
            parts = line.split()
            vals = [int(x) for x in parts[1:]]
            idle = vals[3] + (vals[4] if len(vals) > 4 else 0)
            if parts[0] == "cpu":
                total_busy = sum(vals) - idle
            else:
                n = int(parts[0][3:])
                busy[n] = sum(vals) - idle
                tot[n] = sum(vals)
    except (OSError, ValueError, IndexError):
        pass
    return total_busy, busy, tot


def find_harness() -> tuple[int, Path] | None:
    """(pid, cgroup cpu.stat path) of the running harness, or None."""
    for entry in os.scandir("/proc"):
        if not entry.name.isdigit():
            continue
        try:
            cmd = Path(f"/proc/{entry.name}/cmdline").read_bytes().replace(b"\0", b" ").decode()
            if "jmhbench" in cmd and "project-bench" in cmd:
                rel = Path(f"/proc/{entry.name}/cgroup").read_text().strip().split(":")[-1]
                return int(entry.name), CG / rel.lstrip("/") / "cpu.stat"
        except (OSError, ValueError, IndexError):
            continue
    return None


def slice_usage() -> dict[str, int]:
    """usage_usec per user slice and for system.slice, for attribution."""
    out: dict[str, int] = {}
    try:
        for d in (CG / "user.slice").iterdir():
            if d.name.startswith("user-") and d.name.endswith(".slice"):
                v = read_int(d / "cpu.stat", "usage_usec")
                if v is not None:
                    uid = d.name[5:-6]
                    if uid != str(OUR_UID):
                        out[f"uid{uid}"] = v
    except OSError:
        pass
    v = read_int(CG / "system.slice" / "cpu.stat", "usage_usec")
    if v is not None:
        out["system.slice"] = v
    return out


def logged_in() -> list[str]:
    try:
        return sorted({ln.split()[0] for ln in os.popen("who").read().splitlines() if ln.split()})
    except Exception:
        return []


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    # A pid file, not a pgrep pattern: any `pgrep -f monitor.py` check runs from
    # a command line that itself contains "monitor.py", so it matches itself and
    # claims a monitor is already up on every host, running or not.
    (OUT / "monitor.pid").write_text(str(os.getpid()))
    events = OUT / "events.jsonl"

    harness = find_harness()
    our_stat = harness[1] if harness else None
    prev_ours = read_int(our_stat, "usage_usec") if our_stat else None
    prev_busy_tot, prev_busy, prev_cputot = cpu_totals()
    prev_slices = slice_usage()
    prev_t = time.monotonic()
    tick = 0
    gone = 0

    while True:
        time.sleep(INTERVAL)
        tick += 1
        now = time.monotonic()
        elapsed = now - prev_t
        prev_t = now
        if elapsed <= 0:
            continue

        # Re-resolve if the harness restarted or its scope changed.
        rebound = False
        if harness is None or not Path(f"/proc/{harness[0]}").exists():
            before = harness[0] if harness else None
            harness = find_harness()
            our_stat = harness[1] if harness else None
            prev_ours = None
            rebound = harness is not None and harness[0] != before

        busy_tot, busy, cputot = cpu_totals()
        slices = slice_usage()

        # top-style percentages: 100 == one core for the whole interval
        total_pct = 100.0 * ((busy_tot - prev_busy_tot) / CLK) / elapsed

        cur_ours = read_int(our_stat, "usage_usec") if our_stat else None
        if cur_ours is not None and prev_ours is not None:
            ours_pct = 100.0 * ((cur_ours - prev_ours) / 1e6) / elapsed
        else:
            ours_pct = 0.0

        other_pct = max(0.0, total_pct - ours_pct)

        by: dict[str, float] = {}
        for k, v in slices.items():
            d = v - prev_slices.get(k, v)
            if d > 0:
                p = 100.0 * (d / 1e6) / elapsed
                if p >= 1.0:
                    by[k] = round(p, 1)

        pin_b = sum(busy[c] - prev_busy.get(c, 0) for c in PINNED if c in busy)
        pin_t = sum(cputot[c] - prev_cputot.get(c, 0) for c in PINNED if c in cputot)
        pinned_pct = round(100.0 * pin_b / pin_t, 1) if pin_t > 0 else 0.0

        sample = {
            "t": time.strftime("%Y-%m-%dT%H:%M:%S%z"),
            "other_cpu_pct": round(other_pct, 1),
            "our_cpu_pct": round(ours_pct, 1),
            "total_cpu_pct": round(total_pct, 1),
            "pinned_busy_pct": pinned_pct,
            "load1": float(open("/proc/loadavg").read().split()[0]),
            "users": logged_in(),
            "by_slice": dict(sorted(by.items(), key=lambda x: -x[1])[:4]),
            "harness_up": harness is not None,
        }
        (OUT / "latest.json").write_text(json.dumps(sample))

        # The sample straddling a restart has no previous counter for the new
        # scope, so all of our own CPU lands in `other` and looks like a spike.
        # Observed as a phantom 131% right after relaunching a crashed cell.
        if rebound:
            sample["note"] = "harness rebound; ours not yet measurable this sample"
            (OUT / "latest.json").write_text(json.dumps(sample))
            prev_busy_tot, prev_busy, prev_cputot = busy_tot, busy, cputot
            prev_slices = slices
            prev_ours = cur_ours
            continue

        rec = None
        if other_pct >= SPIKE_PCT:
            rec = dict(sample, event="SPIKE")
        elif tick % HEARTBEAT_EVERY == 0:
            rec = dict(sample, event="heartbeat")
        if rec is not None:
            try:
                if events.exists() and events.stat().st_size > MAX_EVENTS_BYTES:
                    events.rename(OUT / "events.jsonl.1")
            except OSError:
                pass
            with events.open("a") as fh:
                fh.write(json.dumps(rec) + "\n")

        # Stop ~30 min after the harness disappears so the sampler does not
        # linger forever on a shared machine.
        if harness is not None and find_harness() is None:
            gone += 1
            if gone >= 30:
                with events.open("a") as fh:
                    fh.write(json.dumps({"t": sample["t"], "event": "harness-gone, monitor exiting"}) + "\n")
                return
        else:
            gone = 0

        prev_busy_tot, prev_busy, prev_cputot = busy_tot, busy, cputot
        prev_slices = slices
        prev_ours = cur_ours


if __name__ == "__main__":
    main()
