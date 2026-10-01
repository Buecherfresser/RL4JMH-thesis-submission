"""Two throughput probes for the GRPO reward path, on real run-7 candidates.

1. `mvn clean package` (+ shade, what the trainer does today) vs a direct javac with the JMH
   annotation processor on the processor path -- for the compile *gate* only.
2. Wall-clock scaling of the current reward path as worker count rises, which is the ceiling on
   parallelising a rollout group inside a rank.

  python bench_compile_path.py <config.yaml> <sources.jsonl> <n>
"""
from __future__ import annotations

import json
import os
import subprocess
import sys
import tempfile
import time
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path

from jmhgen.config.schema import GRPOConfig
from jmhgen.data.classpath import load_classpath
from jmhgen.rewards.grpo_adapter import build_benchmark_spec
from jmhgen.runner.client import LocalRunnerClient, RewardRequest

CFG, SRC, N = sys.argv[1], sys.argv[2], int(sys.argv[3])
cfg = GRPOConfig.from_yaml(CFG)
classpaths = {
    p: load_classpath(f) for p, f in cfg.project_classpaths.items() if os.path.isfile(f)
}
options = cfg.runner.jmh.to_options()

rows = [json.loads(l) for l in open(SRC)]
rows = [r for r in rows if r.get("source")][:N]
print(f"[bench] {len(rows)} candidates")

M2 = os.environ.get("M2_REPO", "/opt/jmh/.m2/repository")
JMH = f"{M2}/org/openjdk/jmh/jmh-core/1.37/jmh-core-1.37.jar"
PROC = f"{M2}/org/openjdk/jmh/jmh-generator-annprocess/1.37/jmh-generator-annprocess-1.37.jar"
JOPT = f"{M2}/net/sf/jopt-simple/jopt-simple/5.0.4/jopt-simple-5.0.4.jar"
CMATH = f"{M2}/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar"
BASE_CP = os.pathsep.join(p for p in (JMH, JOPT, CMATH) if os.path.isfile(p))
print(f"[bench] base cp entries present: {[os.path.basename(p) for p in BASE_CP.split(os.pathsep)]}")
print(f"[bench] processor jar present: {os.path.isfile(PROC)}")


def spec_for(row):
    return build_benchmark_spec(
        row["source"],
        row.get("class_name") or "B",
        row.get("package") or "",
        classpaths.get(row["project"], ()),
    )


def javac_only(row):
    spec = spec_for(row)
    with tempfile.TemporaryDirectory(prefix="javacprobe-") as d:
        d = Path(d)
        src = d / "src"
        pkg = src / Path(*spec.package.split(".")) if spec.package else src
        pkg.mkdir(parents=True, exist_ok=True)
        (pkg / f"{spec.class_name}.java").write_text(spec.source)
        (d / "out").mkdir()
        (d / "gen").mkdir()
        cp = os.pathsep.join([BASE_CP, *spec.extra_classpath])
        cmd = [
            "javac", "-nowarn", "-proc:full",
            "--release", str(cfg.runner.java_release),
            "-processorpath", PROC,
            "-cp", cp,
            "-d", str(d / "out"),
            "-s", str(d / "gen"),
            str(pkg / f"{spec.class_name}.java"),
        ]
        t = time.perf_counter()
        p = subprocess.run(cmd, capture_output=True, text=True, timeout=300)
        return time.perf_counter() - t, p.returncode == 0


client = LocalRunnerClient(cfg.runner.build_runner())


def maven_only(row):
    spec = spec_for(row)
    t = time.perf_counter()
    ev = client.evaluate(
        RewardRequest(spec=spec, options=options, need_run=False, request_id="mvn")
    ).evaluation
    dt = time.perf_counter() - t
    client.runner.cleanup(spec)
    return dt, ev.compiled


print("\n[bench] probe 1: compile gate, serial, same candidates")
for name, fn in (("mvn clean package (+shade)", maven_only), ("javac -proc:full", javac_only)):
    ts, oks = [], 0
    for r in rows:
        try:
            dt, ok = fn(r)
        except Exception as exc:  # noqa: BLE001
            print(f"    {name}: {type(exc).__name__} {exc}")
            continue
        ts.append(dt)
        oks += ok
    if not ts:
        continue
    ts_sorted = sorted(ts)
    print(f"  {name:<28} n={len(ts):3d} mean {sum(ts)/len(ts):6.2f}s  "
          f"median {ts_sorted[len(ts)//2]:5.2f}s  compiled={oks}")

print("\n[bench] probe 2: reward-path wall clock vs workers (compile gate, need_run=False)")
sub = rows[: min(len(rows), 32)]
base = None
for w in (1, 2, 4, 8, 16):
    clients = [LocalRunnerClient(cfg.runner.build_runner()) for _ in range(w)]

    def work(item, _clients=clients, _w=w):
        i, row = item
        c = _clients[i % _w]
        spec = spec_for(row)
        c.evaluate(RewardRequest(spec=spec, options=options, need_run=False, request_id=f"p{i}"))
        c.runner.cleanup(spec)

    t = time.perf_counter()
    with ThreadPoolExecutor(max_workers=w) as pool:
        list(pool.map(work, enumerate(sub)))
    dt = time.perf_counter() - t
    base = base or dt
    print(f"  workers={w:<3} {len(sub)} candidates in {dt:6.1f}s  "
          f"({dt/len(sub):5.2f}s each)  speedup {base/dt:4.2f}x")
