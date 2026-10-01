#!/usr/bin/env python3
"""Fail fast on the GRPO misconfigurations that silently waste a whole training run.

Motivating incident (hpi2, 2026-07-17, job 2343896): ``configs/grpo/jmh-rl.yaml`` declared
``corpus: original`` — the six curated libraries — but its ``project_classpaths`` map listed
only ``roaringbitmap`` of those six. Every rollout for the other five compiled against no SUT
jar, so all 16 rollouts scored compile=0, reward_std=0 and grad_norm=0. The run consumed a GPU
and produced no gradient. Nothing failed loudly; the trainer happily reported reward 0.

The trainer only *warns* about missing classpaths. On a cluster where the queue wait is
measured in hours, a warning buried in a log is not good enough — these are preconditions.

  uv run python scripts/preflight_grpo.py configs/grpo/mutation70-gemma.yaml
  uv run python scripts/preflight_grpo.py configs/grpo/mutation70-gemma.yaml --reward-smoke

Exits non-zero on the first category that fails, so an sbatch can gate on it.
"""

from __future__ import annotations

import argparse
import json
import re
import struct
import subprocess
import sys
import zipfile
from pathlib import Path

import yaml

from jmhgen.config.schema import GRPOConfig
from jmhgen.training.grpo import build_prompt_records

# A subject class carrying a planted mutant, used by --reward-smoke to prove the whole
# compile -> JMH run -> mutation-arming path works on THIS machine before training starts.
SMOKE_PROJECT = "commons-codec"
SMOKE_SNIPPET_ID = "org.apache.commons.codec.language.Soundex"
SMOKE_CLASS_NAME = "SoundexBenchmark"
SMOKE_PACKAGE = "org.apache.commons.codec.language"
SMOKE_COMPLETION = """```java
package org.apache.commons.codec.language;

import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.language.Soundex;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
public class SoundexBenchmark {

    private Soundex soundex;
    private String[] names;

    @Setup
    public void setup() {
        soundex = new Soundex();
        names = new String[] {"Robert", "Rupert", "Ashcraft", "Tymczak", "Pfister"};
    }

    @Benchmark
    public void encodeNames(Blackhole bh) {
        for (final String name : names) {
            bh.consume(soundex.soundex(name));
        }
    }
}
```
"""


# Reward terms whose value is a measurement, not a capability. A 0 here means "this benchmark
# happened to time badly on this machine", which is a legitimate rollout outcome and must not
# fail the gate. Every other weighted term scoring 0 on a benchmark we know is correct means the
# machinery for that term is broken. r_rsd is scored from the JMH timings themselves, so the
# runtime assertion already proves its machinery works; on a shared node an 8-core box routinely
# exceeds rsd_bad=0.25 and scores 0.
_ADVISORY_TERMS = {
    "rsd": "timing variance above rsd_bad on a shared node; runtime>0 already proves the machinery"
}


def _classpath_entries(cp_file: Path) -> list[str]:
    """Entries of a ``.cp`` file. Provisioning writes ':'- or newline-separated paths."""
    raw = cp_file.read_text(encoding="utf-8")
    return [entry.strip() for entry in re.split(r"[:\n]", raw) if entry.strip()]


def check_classpaths(config: GRPOConfig, projects: set[str]) -> list[str]:
    """Every project in the prompt set needs a classpath whose entries all exist."""
    problems: list[str] = []
    for project in sorted(projects):
        cp_name = config.project_classpaths.get(project)
        if not cp_name:
            problems.append(f"{project}: no project_classpaths entry (rollouts cannot compile)")
            continue
        cp_file = Path(cp_name)
        if not cp_file.is_file():
            problems.append(f"{project}: classpath file missing: {cp_name}")
            continue
        root = cp_file.parent
        unresolved = [e for e in _classpath_entries(cp_file) if not (root / e).exists()]
        if unresolved:
            problems.append(
                f"{project}: {len(unresolved)} unresolved classpath entry/ies, "
                f"first: {unresolved[0]}"
            )
    return problems


def check_mutants(config: GRPOConfig, projects: set[str]) -> list[str]:
    """With mutation weighted, every project needs a non-empty hidden registry."""
    problems: list[str] = []
    for project in sorted(projects):
        registry = config.project_mutants.get(project)
        if not registry:
            problems.append(f"{project}: no project_mutants entry but mutation weight > 0")
            continue
        path = Path(registry)
        if not path.is_file():
            problems.append(f"{project}: mutants registry missing: {registry}")
            continue
        data = yaml.safe_load(path.read_text(encoding="utf-8")) or {}
        if not (data.get("mutants") or []):
            problems.append(f"{project}: mutants registry has no mutants: {registry}")
    return problems


def _javac_max_class_version() -> tuple[int, str] | None:
    """Highest class-file major version the javac on PATH can read (44 + feature release)."""
    try:
        proc = subprocess.run(
            ["javac", "-version"], capture_output=True, text=True, timeout=60, check=False
        )
    except (OSError, subprocess.SubprocessError):
        return None
    raw = f"{proc.stdout} {proc.stderr}"
    match = re.search(r"javac (\d+)", raw)
    if not match:
        return None
    feature = int(match.group(1))
    return 44 + feature, raw.strip()


def _jar_max_class_version(jar: Path) -> int:
    """Highest class-file major version inside a jar, ignoring multi-release overlays."""
    highest = 0
    with zipfile.ZipFile(jar) as archive:
        for name in archive.namelist():
            if not name.endswith(".class") or "META-INF/versions/" in name:
                continue
            with archive.open(name) as member:
                head = member.read(8)
            if len(head) >= 8 and head[:4] == b"\xca\xfe\xba\xbe":
                highest = max(highest, struct.unpack(">H", head[6:8])[0])
    return highest


def check_class_versions(config: GRPOConfig, projects: set[str]) -> list[str]:
    """Reject SUT jars that this javac cannot read.

    Provisioning builds each mutant jar with whatever JDK the provisioning host had, so the
    corpus is not uniform: as of 2026-07-13 commons-codec is major 67 (JDK 23) and
    jackson-databind is major 65 (JDK 21) while the other 46 are major 61 (JDK 17). A javac
    older than a jar's major version cannot link against it at all -- it fails with
    "bad class file ... class file has wrong version 67.0, should be 61.0", so every rollout
    for that project scores 0. This is invisible until a rollout for that specific project
    happens to be sampled, which is exactly the kind of silent zero this script exists to stop.
    """
    limit = _javac_max_class_version()
    if limit is None:
        return ["could not determine javac version to validate SUT class-file versions"]
    max_major, version_line = limit
    print(f"  javac reads class-file major <= {max_major} ({version_line.splitlines()[0]})")

    problems: list[str] = []
    for project in sorted(projects):
        cp_name = config.project_classpaths.get(project)
        if not cp_name:
            continue  # already reported by check_classpaths
        cp_file = Path(cp_name)
        if not cp_file.is_file():
            continue
        root = cp_file.parent
        for entry in _classpath_entries(cp_file):
            jar = root / entry
            if not jar.is_file() or jar.suffix != ".jar":
                continue
            try:
                found = _jar_max_class_version(jar)
            except (OSError, zipfile.BadZipFile) as exc:
                problems.append(f"{project}: unreadable jar {entry}: {exc}")
                continue
            if found > max_major:
                problems.append(
                    f"{project}: {entry} has class-file major {found} "
                    f"(needs JDK >= {found - 44}); this javac reads <= {max_major}"
                )
                break
    return problems


def check_toolchain(config: GRPOConfig) -> list[str]:
    """The reward compiles and runs every rollout, so Maven + a JDK must be on PATH."""
    runner = config.runner.build_runner()
    if not runner.is_available():
        return [
            f"reward toolchain unavailable: needs '{runner.mvn_executable}' and "
            f"'{runner.java_executable}' on PATH"
        ]
    return []


def run_reward_smoke(config: GRPOConfig) -> list[str]:
    """Score a known-good benchmark and require every *weighted* term to actually fire.

    "reward > 0" is far too weak a gate. On the LRZ login node this benchmark scored 0.15:
    it compiled, but the JMH run never completed, so both the runtime term and the whole
    0.70 mutation term were 0. That run would have trained for two days against a reward
    whose dominant component is structurally dead. So assert per-component: any term with
    a non-zero weight must be non-zero for a benchmark we know is correct.
    """
    import tempfile

    from jmhgen.rewards.grpo_adapter import build_grpo_reward_fn
    from jmhgen.training.grpo_metrics import GrpoMetricsCollector

    if SMOKE_PROJECT not in config.project_classpaths:
        return [f"--reward-smoke needs {SMOKE_PROJECT} in project_classpaths; skipping"]

    with tempfile.TemporaryDirectory(prefix="jmh-preflight-") as tmp:
        collector = GrpoMetricsCollector(Path(tmp), failure_sample_limit=5)
        reward_fn = build_grpo_reward_fn(config, metrics=collector)
        rewards = reward_fn(
            prompts=[[{"role": "user", "content": "benchmark it"}]],
            completions=[[{"role": "assistant", "content": SMOKE_COMPLETION}]],
            project=[SMOKE_PROJECT],
            snippet_id=[SMOKE_SNIPPET_ID],
            class_name=[SMOKE_CLASS_NAME],
            package=[SMOKE_PACKAGE],
        )
        rollouts = Path(tmp) / "rollouts.jsonl"
        records = (
            [
                json.loads(line)
                for line in rollouts.read_text(encoding="utf-8").splitlines()
                if line.strip()
            ]
            if rollouts.is_file()
            else []
        )

    print(f"  reward for a known-good benchmark: {rewards}")
    if not rewards or rewards[0] <= 0:
        return [
            "a known-good benchmark scored 0 — the reward harness is broken on this machine, "
            "so every rollout would score 0 and GRPO would see no gradient"
        ]
    if not records:
        return ["reward smoke produced no rollout diagnostics; cannot verify components"]

    record = records[0]
    components = record.get("components") or {}
    print(f"  components: {components}")
    print(f"  compiled={record.get('compiled')} ran={record.get('ran')}")
    if record.get("mutation"):
        print(f"  mutation: {record['mutation']}")

    problems: list[str] = []
    weights = config.reward_weights.model_dump()
    for term, weight in weights.items():
        if weight <= 0:
            continue
        value = components.get(term)
        if value is None:
            problems.append(f"reward term '{term}' has weight {weight} but was not computed")
        elif value <= 0 and term in _ADVISORY_TERMS:
            # Not a capability, so 0 is a legitimate measurement, not a broken machine.
            print(f"  NOTE: reward term '{term}' scored 0 here ({_ADVISORY_TERMS[term]})")
        elif value <= 0:
            problems.append(
                f"reward term '{term}' has weight {weight} but scored {value} on a "
                f"known-good benchmark — that term is dead on this machine"
            )
    if weights.get("mutation", 0) > 0 and not (record.get("mutation") or {}).get("attempted"):
        problems.append(
            "mutation weight > 0 but no mutant was attempted — coverage/arming is not working "
            "here, so the dominant reward term would be constant 0"
        )
    return problems


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("config", help="GRPO YAML config")
    parser.add_argument("--corpus", choices=("original", "full"), default=None)
    parser.add_argument(
        "--reward-smoke",
        action="store_true",
        help="also compile+run+mutation-score one known-good benchmark (needs mvn + JDK)",
    )
    args = parser.parse_args()

    config = GRPOConfig(**yaml.safe_load(Path(args.config).read_text(encoding="utf-8")))
    if args.corpus:
        config.corpus = args.corpus

    records = build_prompt_records(config)
    projects = {r["project"] for r in records if r["project"]}
    print(f"config           : {args.config}")
    print(f"corpus           : {config.corpus}")
    print(f"prompts          : {len(records)} across {len(projects)} project(s)")
    print(f"mutation weight  : {config.reward_weights.mutation}")

    failures: list[tuple[str, list[str]]] = []
    for label, problems in (
        ("project classpaths", check_classpaths(config, projects)),
        (
            "mutant registries",
            check_mutants(config, projects) if config.reward_weights.mutation > 0 else [],
        ),
        ("reward toolchain", check_toolchain(config)),
        ("SUT class-file versions", check_class_versions(config, projects)),
    ):
        if problems:
            failures.append((label, problems))
        else:
            print(f"OK  {label}")

    if not failures and args.reward_smoke:
        problems = run_reward_smoke(config)
        if problems:
            failures.append(("reward smoke", problems))
        else:
            print("OK  reward smoke")

    if failures:
        print("\nPREFLIGHT FAILED", file=sys.stderr)
        for label, problems in failures:
            print(f"\n[{label}] {len(problems)} problem(s):", file=sys.stderr)
            for problem in problems[:25]:
                print(f"  - {problem}", file=sys.stderr)
            if len(problems) > 25:
                print(f"  - ... and {len(problems) - 25} more", file=sys.stderr)
        return 1

    print("\nPREFLIGHT OK")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
