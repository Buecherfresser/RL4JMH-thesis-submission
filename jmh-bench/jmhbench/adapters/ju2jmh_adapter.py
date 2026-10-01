"""Wrap the ju2jmh CLI as a JMH-Bench harness.

ju2jmh (Chalmers; https://github.com/alniniclas/junit-to-jmh) converts JUnit 4
tests into JMH benchmarks. We invoke it as a subprocess on each task whose
``junit_test_path`` is set.

The ju2jmh JAR location is resolved in this order:

1. constructor kwarg ``jar``  (e.g. ``-o jar=/path/to/ju2jmh.jar``)
2. ``JU2JMH_JAR`` environment variable
3. ``./tools/ju2jmh.jar`` relative to the repo

We pass the task's JUnit test (compiled against the SUT) plus the SUT sources
to ju2jmh and collect the produced benchmark file. If ju2jmh fails or the task
has no JUnit test, generation reports failure with a useful message — this is
the *expected* behaviour for harnesses that need inputs not all tasks provide.
"""

from __future__ import annotations

import os
import re
import shutil
import subprocess
from pathlib import Path

from jmhbench.harness import HarnessOutput, Task


_REPO_ROOT = Path(__file__).resolve().parent.parent.parent


def _resolve_jar(explicit: str | None) -> Path | None:
    candidates = [
        explicit,
        os.environ.get("JU2JMH_JAR"),
        str(_REPO_ROOT / "tools" / "ju2jmh.jar"),
    ]
    for c in candidates:
        if c and Path(c).exists():
            return Path(c)
    return None


class Ju2JmhHarness:
    name = "ju2jmh"

    def __init__(self, jar: str | None = None, **_: object) -> None:
        self.jar = _resolve_jar(jar)

    def generate(self, task: Task, workdir: Path) -> HarnessOutput:
        if not task.junit_test_path:
            raise RuntimeError(
                f"Task {task.instance_id} has no JUnit test; ju2jmh requires one."
            )
        if self.jar is None:
            raise RuntimeError(
                "ju2jmh.jar not found. Place it at ./tools/ju2jmh.jar, set $JU2JMH_JAR, "
                "or pass `-o jar=/path/to/ju2jmh.jar`."
            )

        workdir.mkdir(parents=True, exist_ok=True)
        out_dir = workdir / "ju2jmh-out"
        out_dir.mkdir(exist_ok=True)

        # ju2jmh expects: --test-src, --class-dir, --output-dir, then test class names.
        # We first compile the SUT + test into a staging dir, then run ju2jmh.
        stage = workdir / "stage"
        stage.mkdir(exist_ok=True)
        sut_dir = task.sut_dir
        test_path = task.task_dir / task.junit_test_path

        # Compile test + SUT classes for ju2jmh's class-dir argument.
        sources = list(sut_dir.rglob("*.java")) + [test_path]
        classes = stage / "classes"
        classes.mkdir(exist_ok=True)
        # JUnit 4 is required on the classpath; we resolve it via Maven Local.
        junit_jar = _find_junit4_jar()
        if junit_jar is None:
            raise RuntimeError(
                "Could not locate junit-4.13.2.jar; run `mvn dependency:get -Dartifact=junit:junit:4.13.2` first."
            )
        cp = f"{junit_jar}"
        compile_cmd = ["javac", "-d", str(classes), "-cp", cp, *map(str, sources)]
        cr = subprocess.run(compile_cmd, capture_output=True, text=True)
        if cr.returncode != 0:
            raise RuntimeError(f"ju2jmh pre-compile failed: {cr.stderr}")

        # Derive the JUnit test FQN.
        test_fqn = _extract_test_fqn(test_path.read_text(), test_path.stem)

        ju2jmh_cmd = [
            "java",
            "-jar",
            str(self.jar),
            "--test-src", str(sut_dir),
            "--class-dir", str(classes),
            "--output-dir", str(out_dir),
            test_fqn,
        ]
        jr = subprocess.run(ju2jmh_cmd, capture_output=True, text=True)
        if jr.returncode != 0:
            raise RuntimeError(f"ju2jmh failed (exit {jr.returncode}): {jr.stderr}")

        produced = list(out_dir.rglob("*.java"))
        if not produced:
            raise RuntimeError("ju2jmh produced no output Java files")

        primary = produced[0]
        source = primary.read_text()
        extras = {p.name: p.read_text() for p in produced if p != primary}
        return HarnessOutput(
            benchmark_source=source,
            extra_sources=extras,
            metadata={"jar": str(self.jar), "test_fqn": test_fqn},
        )


_PACKAGE_RE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)


def _extract_test_fqn(source: str, class_name: str) -> str:
    m = _PACKAGE_RE.search(source)
    return f"{m.group(1)}.{class_name}" if m else class_name


def _find_junit4_jar() -> Path | None:
    home = Path.home() / ".m2" / "repository" / "junit" / "junit" / "4.13.2"
    candidate = home / "junit-4.13.2.jar"
    if candidate.exists():
        return candidate
    # Fallback: walk ~/.m2 for any junit-4*.jar
    m2 = Path.home() / ".m2"
    if m2.exists():
        for p in m2.rglob("junit-4*.jar"):
            return p
    return None
