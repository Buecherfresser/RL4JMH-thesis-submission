"""The injected-latency operator toggle (``--mutant-op`` / ``--mutant-tokens``).

An armed mutant used to inject ``Thread.sleep(0, 1)`` unconditionally: ~1.2 ms a
hit on Linux, against a 1.10 kill threshold. These pin the toggle that replaces
it, the invariants that keep old measurement reproducible (``sleep`` stays the
default), and the ones that keep new measurement honest (the flag reaches every
arm; a bad operator name fails instead of silently falling back).

The Java-level cases need a JDK and are skipped without one.
"""
from __future__ import annotations

import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

import pytest

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "tools"))

from jmhbench.config import JmhSettings  # noqa: E402

REPO = Path(__file__).resolve().parents[1]


# --- the Python side: what reaches the JVM ---------------------------------


def test_sleep_is_still_the_default():
    """Reruns of the 2026-08 campaign must reproduce, so the default cannot move."""
    assert JmhSettings().mutant_op == "sleep"
    assert "-Djmhbench.mutant.op=sleep" in JmhSettings.preset("campaign").jvm_args()


def test_spin_emits_its_token_count():
    args = JmhSettings(mutant_op="spin", mutant_spin_tokens=256).jvm_args()
    assert "-Djmhbench.mutant.op=spin" in args
    assert "-Djmhbench.mutant.tokens=256" in args


def test_consumecpu_emits_its_token_count():
    args = JmhSettings(mutant_op="consumecpu", mutant_spin_tokens=256).jvm_args()
    assert "-Djmhbench.mutant.op=consumecpu" in args
    assert "-Djmhbench.mutant.tokens=256" in args


def test_tokens_are_not_emitted_for_the_other_operators():
    for op in ("sleep", "nanotime"):
        args = JmhSettings(mutant_op=op, mutant_spin_tokens=256).jvm_args()
        assert not any(a.startswith("-Djmhbench.mutant.tokens") for a in args), op


def test_a_bad_operator_is_refused_before_the_jvm_sees_it():
    with pytest.raises(ValueError, match="sleep\\|spin\\|nanotime"):
        JmhSettings(mutant_op="thread.sleep").jvm_args()
    with pytest.raises(ValueError, match="must be >= 1"):
        JmhSettings(mutant_op="spin", mutant_spin_tokens=0).jvm_args()


def test_the_operator_is_recorded_in_the_provenance():
    """``jmh.__dict__`` is what lands in scorecard.json's config block."""
    d = JmhSettings(mutant_op="spin", mutant_spin_tokens=110).__dict__
    assert d["mutant_op"] == "spin"
    assert d["mutant_spin_tokens"] == 110


# --- the dataset side: every vendored patch carries the toggle -------------


def _switch_sources() -> dict[str, str]:
    """The MutationSwitch.java each mutations.patch adds, by project."""
    out = {}
    for patch_path in sorted((REPO / "dataset" / "projects").glob("*/mutations.patch")):
        patch = patch_path.read_text(encoding="utf-8")
        starts = [m.start() for m in re.finditer(r"^diff --git ", patch, re.MULTILINE)]
        for i, start in enumerate(starts):
            end = starts[i + 1] if i + 1 < len(starts) else len(patch)
            block = patch[start:end]
            if "MutationSwitch.java" not in block.splitlines()[0]:
                continue
            body = block.split("@@ -0,0", 1)[1].split("\n", 1)[1]
            out[patch_path.parent.name] = "\n".join(
                ln[1:] for ln in body.splitlines() if ln.startswith("+")
            )
    return out


def test_every_project_switch_understands_the_toggle():
    sources = _switch_sources()
    assert len(sources) == 6, sorted(sources)
    for project, src in sources.items():
        assert "jmhbench.mutant.op" in src, project
        assert "jmhbench.mutant.tokens" in src, project
        assert "private static void spin(final long tokens)" in src, project
        assert "private static void consumeCpu(final long tokens)" in src, project
        # The legacy operator has to stay reachable, or old runs are unreproducible.
        assert "Thread.sleep(0, 1)" in src, project


#: ``spin``'s step, byte for byte as it was first measured. Runs already scored
#: with ``op=spin`` are only reproducible while this line is untouched.
_SPIN_STEP = "t += t * 0x5DEECE66DL + 0xBL;"

#: ``consumecpu``'s step: JMH 1.37 ``Blackhole.consumeCPU``, ported verbatim.
_CONSUMECPU_STEP = "t += (t * 0x5DEECE66DL + 0xBL + i) & 0xFFFFFFFFFFFFL;"

#: The trap. ``t = t * A + B`` is affine, so over k steps it has the closed form
#: ``t * A^k + C``; C2 composes it across unrolled iterations and emits one
#: multiply-add per two tokens. The burn loop then costs half of what the
#: calibration measured, and nothing in the run reports the discrepancy.
_AFFINE_STEP = "t = t * 0x5DEECE66DL + 0xBL;"


def test_both_burn_steps_are_frozen():
    for project, src in _switch_sources().items():
        assert _SPIN_STEP in src, project
        assert _CONSUMECPU_STEP in src, project


def test_no_switch_uses_the_foldable_affine_step():
    """Guard against "simplifying" a burn loop into a JIT-collapsible recurrence."""
    for project, src in _switch_sources().items():
        assert _AFFINE_STEP not in src, (
            f"{project}: the affine step folds under C2 (one multiply-add per two "
            f"tokens), halving the injected latency behind the calibration's back"
        )


def test_the_vendored_patches_match_the_generator():
    """`refresh_mutation_switch.py --check` is the drift guard; run it."""
    proc = subprocess.run(
        [sys.executable, str(REPO / "tools" / "refresh_mutation_switch.py"), "--check"],
        capture_output=True, text=True, cwd=REPO,
    )
    assert proc.returncode == 0, proc.stdout + proc.stderr


def test_hunk_headers_count_their_own_lines():
    """A wrong `@@ -0,0 +1,N @@` makes `git apply` reject the whole patch."""
    for patch_path in sorted((REPO / "dataset" / "projects").glob("*/mutations.patch")):
        patch = patch_path.read_text(encoding="utf-8")
        starts = [m.start() for m in re.finditer(r"^diff --git ", patch, re.MULTILINE)]
        for i, start in enumerate(starts):
            end = starts[i + 1] if i + 1 < len(starts) else len(patch)
            block = patch[start:end]
            if "MutationSwitch.java" not in block.splitlines()[0]:
                continue
            m = re.search(r"@@ -0,0 \+1,(\d+) @@", block)
            assert m, patch_path
            declared = int(m.group(1))
            body = block.split("@@ -0,0", 1)[1].split("\n", 1)[1]
            actual = sum(1 for ln in body.splitlines() if ln.startswith("+"))
            assert declared == actual, f"{patch_path}: header {declared} != {actual} lines"


# --- the Java side: the operators actually cost what they claim ------------


def _compile_switch(tmp: Path) -> Path:
    src = _switch_sources()["hppc"]
    java = tmp / "src" / "com" / "carrotsearch" / "hppc" / "jmhbench" / "MutationSwitch.java"
    java.parent.mkdir(parents=True)
    java.write_text(src + "\n", encoding="utf-8")
    driver = tmp / "src" / "Drive.java"
    driver.write_text(
        """
import com.carrotsearch.hppc.jmhbench.MutationSwitch;

public class Drive {
    public static void main(String[] a) {
        int reps = Integer.parseInt(a[0]);
        for (int i = 0; i < 200_000; i++) MutationSwitch.tick(1);
        long best = Long.MAX_VALUE;
        for (int r = 0; r < 5; r++) {
            long s = System.nanoTime();
            for (int i = 0; i < reps; i++) MutationSwitch.tick(1);
            best = Math.min(best, System.nanoTime() - s);
        }
        System.out.printf("%.4f%n", (double) best / reps);
    }
}
""",
        encoding="utf-8",
    )
    classes = tmp / "classes"
    subprocess.run(
        ["javac", "-nowarn", "-d", str(classes), str(java), str(driver)],
        check=True, capture_output=True, text=True,
    )
    return classes


def _tick_cost_ns(classes: Path, reps: int, *props: str) -> float:
    proc = subprocess.run(
        ["java", "-XX:+UseG1GC", "-cp", str(classes),
         "-Djmhbench.mutant=1", *props, "Drive", str(reps)],
        capture_output=True, text=True,
    )
    assert proc.returncode == 0, proc.stderr
    return float(proc.stdout.strip().splitlines()[-1])


@pytest.fixture(scope="module")
def classes():
    if shutil.which("javac") is None or shutil.which("java") is None:
        pytest.skip("no JDK on PATH")
    tmp = Path(tempfile.mkdtemp(prefix="jmhbench-optest-"))
    try:
        yield _compile_switch(tmp)
    finally:
        shutil.rmtree(tmp, ignore_errors=True)


def test_spin_is_not_optimised_away(classes):
    """The volatile read plus the never-taken store keep the loop live."""
    small = _tick_cost_ns(classes, 300_000, "-Djmhbench.mutant.op=spin",
                          "-Djmhbench.mutant.tokens=64")
    large = _tick_cost_ns(classes, 300_000, "-Djmhbench.mutant.op=spin",
                          "-Djmhbench.mutant.tokens=2048")
    assert large > 8 * small, f"64 tokens {small:.1f} ns, 2048 tokens {large:.1f} ns"


def test_spin_lands_in_the_nanosecond_range(classes):
    """The whole point: three or four orders of magnitude below the sleep."""
    spin = _tick_cost_ns(classes, 300_000, "-Djmhbench.mutant.op=spin",
                         "-Djmhbench.mutant.tokens=64")
    assert 5.0 < spin < 500.0, f"{spin:.1f} ns for 64 tokens"


def test_consumecpu_is_not_optimised_away(classes):
    """The loop counter in the step is what keeps C2 from composing it."""
    small = _tick_cost_ns(classes, 300_000, "-Djmhbench.mutant.op=consumecpu",
                          "-Djmhbench.mutant.tokens=64")
    large = _tick_cost_ns(classes, 300_000, "-Djmhbench.mutant.op=consumecpu",
                          "-Djmhbench.mutant.tokens=2048")
    assert large > 8 * small, f"64 tokens {small:.1f} ns, 2048 tokens {large:.1f} ns"


def test_consumecpu_lands_in_the_nanosecond_range(classes):
    cost = _tick_cost_ns(classes, 300_000, "-Djmhbench.mutant.op=consumecpu",
                         "-Djmhbench.mutant.tokens=64")
    assert 5.0 < cost < 500.0, f"{cost:.1f} ns for 64 tokens"


def test_the_unarmed_path_stays_free(classes):
    """An unarmed tick must cost the same whatever the operator is."""
    proc = subprocess.run(
        ["java", "-XX:+UseG1GC", "-cp", str(classes),
         "-Djmhbench.mutant=999", "-Djmhbench.mutant.op=spin",
         "-Djmhbench.mutant.tokens=4096", "Drive", "300000"],
        capture_output=True, text=True,
    )
    assert proc.returncode == 0, proc.stderr
    assert float(proc.stdout.strip().splitlines()[-1]) < 5.0


def test_a_bad_operator_name_fails_in_the_jvm_too(classes):
    proc = subprocess.run(
        ["java", "-XX:+UseG1GC", "-cp", str(classes),
         "-Djmhbench.mutant=1", "-Djmhbench.mutant.op=bogus", "Drive", "1000"],
        capture_output=True, text=True,
    )
    assert proc.returncode != 0
    assert "jmhbench.mutant.op must be one of spin|sleep|nanotime" in proc.stderr
