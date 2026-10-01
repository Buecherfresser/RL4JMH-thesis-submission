# Campaign rerun — 18-machine operations

Re-benchmarking 3 models x 6 projects (t0 bundles) on `nc-2-*.kosmos`, one cell
per machine, to capture per-iteration JMH samples with the fork boundary intact.

`assignment.txt` is the host -> cell map and the input to every script here.

## Commands

| | |
|---|---|
| `./status.sh` | progress per cell: stage, benchmarks done, elapsed |
| `./contention.sh` | is anyone else using the machines |
| `./collect.sh [dest]` | pull finished results (default `./collected/`) |
| `./deploy-monitor.sh [restart]` | install/start the contention monitor |
| `./rescore.py <scorecards> [--write]` | apply the timeout-as-kill policy to existing results |

`collect.sh` is re-runnable — it skips unfinished cells, so run it each day as
more land.

## Scoring: timeouts as kills

An armed run that exceeds its wall-clock budget counts as a **kill**, but only
when that benchmark's baseline did *not* also time out.

With a completed baseline the armed run blew `5 x baseline_wall + 60s` — a >5x
slowdown against a 1.10 kill threshold, and the strongest regression evidence
available. Without one there is no evidence the mutant caused the timeout rather
than the benchmark simply being slower than the cap, so those stay errors.
Scoring them as errors removes the mutant from the numerator *and* the
denominator, which understated any model whose benchmarks are slow.

`jmhbench/project_bench.py` implements this at measurement time and records
`mutants_killed_by_timeout`, so the stricter errors-only score is always
recoverable as `mutants_killed - mutants_killed_by_timeout`.

`rescore.py` applies the same rule to scorecards measured before that change,
so the campaign need not be re-run. It recovers the baseline condition from the
stored detection error, which reads `... budget (baseline Ms)` when the baseline
wall time was known and omits the parenthetical when it was not. It never edits
in place; `--write` emits `scorecard.rescored.json` beside each input.

Only `dsv4/fastfilter` is affected so far: 0.700 -> 0.840 (7 mutants promoted),
which reproduces the pre-campaign reference value exactly.

## Contention monitor

`monitor.py` runs *on* each machine, samples once a minute, and logs to
`/var/tmp/jmhb/monitor/` (`latest.json`, `events.jsonl`). It records a `SPIKE`
event whenever CPU **not caused by this run** exceeds 30% of one core, plus an
hourly heartbeat. It exits by itself ~30 min after the harness finishes.

Percentages are top-style: `100` = one core saturated, so a 12-thread box tops
out at 1200. Expect `ours` ≈ 66% steady-state (one benchmark thread plus JIT),
briefly ~160% during Maven builds.

Two things it does deliberately, both of which the obvious implementation gets
wrong:

- **Accounting is cgroup-based, not a `/proc` process scan.** JMH forks a fresh
  JVM per fork (5 per invocation, ~20s each), so most of our JVMs are born and
  reaped between two 60s samples and appear in neither snapshot. Measured side
  by side, a per-PID scan reported 6–25% of a core where the cgroup reported
  159%. The same blind spot would hide the short interfering job the monitor
  exists to catch.
- **Liveness is a pid file, not `pgrep -f monitor.py`.** The launch command line
  itself contains `monitor.py`, so any `-f` pattern matches the shell doing the
  checking and reports "already running" on every host, running or not.

## Run configuration (for reference)

Each cell runs, pinned to the 4 Zen 4c cores:

```
taskset -c 1,2,3,5,7,8,9,11 jmhbench project-bench <bundle> \
  --preset campaign --sensitivity-sample 0 --max-detect-attempts 6 --compiler-heap 4g
```

`JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64` — Java 21 on these images is
JRE-only. Everything (repo copy, venv, Maven repo, output) lives under
`/var/tmp/jmhb/`; `$HOME` is a shared NFS mount with a 50 GB hard quota.

Per-machine `NOTES.md` in `/var/tmp/jmhb/out/<cell>/` covers the pre-flight,
the core-heterogeneity finding and the frequency strategy.
