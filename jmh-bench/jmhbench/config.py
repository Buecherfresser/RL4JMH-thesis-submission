"""Runtime configuration for the runner."""

from __future__ import annotations

import os

from dataclasses import dataclass


@dataclass
class JmhSettings:
    """Knobs passed to the JMH command line."""

    forks: int = 1
    warmup_iterations: int = 2
    warmup_time_seconds: float = 1.0
    measurement_iterations: int = 3
    measurement_time_seconds: float = 1.0
    timeout_seconds: int = 120
    """Wallclock cap for a single JMH invocation (harness-enforced).

    When this fires the harness kills the JMH launcher *and its forked JVMs*
    (see :func:`jmhbench.proc.run_jvm`)."""

    iteration_timeout_seconds: int = 60
    """Per-iteration cap handed to JMH itself via ``-to``.

    JMH aborts (and tries to cleanly shut down the fork for) any single
    warmup/measurement iteration that exceeds this, so a stuck benchmark is
    caught *before* the coarser wall-clock ``timeout_seconds`` escalates to a
    process-group kill. Keep it below ``timeout_seconds``."""

    # ---- measurement shape: pinned, not inherited from the machine ---------
    #
    # Everything below used to be *unset*, which does not mean "default" -- it
    # means "whatever this box and this model's annotations decided". Two hosts
    # then run different JVM configurations on the same jar, and the machine
    # leaks into the measurement rather than cancelling out of the within-pair
    # ratio (EVALUATION_ISSUES.md B4 / section 5).

    mode: str | None = "thrpt"
    """JMH benchmark mode, emitted as ``-bm``. ``None`` leaves it to each
    class's ``@BenchmarkMode``.

    Left unset the suite is mode-heterogeneous: ratios still survive because a
    pair shares a mode, but ``Mode.SampleTime`` classes are silently dropped
    (JMH writes ``rawDataHistogram`` instead of ``rawData``) while still
    counting in ``n_benchmarks``. LLM4JMH pin ``thrpt``."""

    time_unit: str | None = "s"
    """JMH time unit, emitted as ``-tu``."""

    force_gc: bool = True
    """Emit ``-gc true``: force a GC between iterations.

    Without it one GC pause landing in one of a handful of measured iterations
    moves the mean by tens of percent, against a 10% detection gate."""

    threads: int | None = 1
    """Emit ``-t``. ``None`` leaves it to ``@Threads``.

    ``@Threads(Threads.MAX)`` resolves to ``availableProcessors()``, so the same
    class is an 8-way contention measurement on one box and a 64-way one on
    another."""

    heap: str | None = "2g"
    """Pin ``-Xms``/``-Xmx`` on the forked JVMs. ``None`` leaves heap sizing to
    the JVM, which picks roughly a quarter of the machine's RAM."""

    gc_collector: str | None = "-XX:+UseG1GC"
    """Name the collector explicitly on the forked JVMs.

    Unset, the JVM picks by machine class: a small or single-core box gets
    SerialGC where a large one gets G1, which is a different program running the
    same jar. G1 is the JDK 17 default on server-class hardware, so naming it
    pins current behaviour rather than changing it."""

    per_benchmark_baseline: bool = True
    """Run the baseline one benchmark per JMH invocation, like the mutant arm.

    A whole-suite baseline runs uncapped (it can legitimately take hours) while
    each mutant rerun inherits a per-invocation cap, so a benchmark slower than
    that cap passes the baseline and times out on every armed run. Measuring
    both arms the same way removes the asymmetry and yields a per-benchmark
    baseline wall time to size the mutant budget from
    (EVALUATION_ISSUES.md B2)."""

    fork_overhead_seconds: float = 20.0
    """Wall-clock headroom per fork for JVM start-up and class loading."""

    setup_allowance_seconds: float = 120.0
    """Wall-clock headroom per invocation for ``@Setup``/``@TearDown``."""

    # ---- the injected-latency operator (project mutation track) -------------

    mutant_op: str = "sleep"
    """Operator an *armed* ``MutationSwitch.tick`` runs: ``sleep``, ``spin``,
    ``nanotime`` or ``consumecpu``. Emitted as ``-Djmhbench.mutant.op``.

    ``sleep`` is ``Thread.sleep(0, 1)``, the original operator: ~1.2 ms per hit
    on Linux. Against a 10% kill threshold that is four orders of magnitude of
    headroom, so essentially every *covered* mutant dies and the mutation score
    collapses onto coverage -- in the 2026-08 campaign 536 of 576 covered
    mutants were killed (7 of the other 40 were measurement errors), the median
    killed mutant was 7,664x slower, and the largest was 7.6e7x.

    ``spin`` and ``consumecpu`` burn ``mutant_spin_tokens`` steps of a 64-bit
    burn loop instead, so the injected latency lands in the range a real
    regression occupies and a mutant is killed only by a benchmark tight enough
    to resolve it. ``consumecpu`` is JMH's own ``Blackhole.consumeCPU`` ported
    verbatim and is the one to use for new measurement: the loop counter in its
    step makes the recurrence non-affine, so a token costs the same no matter how
    C2 unrolls the loop (1.894 ns/token on a Zen 4c core at 3.7 GHz, stable to
    0.02% across JVMs). ``spin`` is the earlier, affine variant, kept unchanged
    so runs already measured with it stay reproducible (1.317 ns/token on the
    same core). ``nanotime`` injects a single ``System.nanoTime()`` read
    (~10-25 ns), a fixed operator that mirrors a real one (a clock read added to
    a hot path).

    Left at ``sleep`` so previously measured runs stay reproducible; new
    measurement should pin it explicitly. It is recorded in the run's
    provenance either way."""

    mutant_spin_tokens: int = 64
    """Burn-loop steps per hit when ``mutant_op`` is ``spin`` or ``consumecpu``.
    Emitted as ``-Djmhbench.mutant.tokens``.

    Roughly one nanosecond each, but that is hardware, not a contract:
    calibrate on the measurement host with
    ``tools/calibrate_mutant_op.py``. Below ~16 tokens the cost disappears into
    the call's own overhead."""

    MUTANT_OPS = ("sleep", "spin", "nanotime", "consumecpu")

    #: Operators that burn ``mutant_spin_tokens`` steps and so need calibrating.
    MUTANT_BURN_OPS = ("spin", "consumecpu")

    def measured_seconds(self) -> float:
        """JMH's own measured time per benchmark, from the preset alone."""
        return self.forks * (
            self.warmup_iterations * self.warmup_time_seconds
            + self.measurement_iterations * self.measurement_time_seconds
        )

    def wall_budget_seconds(self) -> float:
        """Wall-clock budget for one benchmark, applied to *both* arms.

        ``timeout_seconds`` is kept as a floor so an explicitly-raised cap is
        never silently lowered."""
        budget = (
            self.measured_seconds()
            + self.forks * self.fork_overhead_seconds
            + self.setup_allowance_seconds
        )
        return float(max(budget, self.timeout_seconds))

    def jvm_args(self) -> list[str]:
        """Forked-JVM flags implied by the pinned settings."""
        args: list[str] = []
        if self.heap:
            args += [f"-Xms{self.heap}", f"-Xmx{self.heap}"]
        if self.gc_collector:
            args.append(self.gc_collector)

        # Java resolves ``java.io.tmpdir`` to /tmp and ignores $TMPDIR, so a SUT
        # that creates temp files writes them outside whatever scratch the run
        # was given -- and JMH's forked JVMs inherit that. jodd-util does exactly
        # this and never deletes them: one campaign cell wrote ~4M jodd-*.tmp
        # files and exhausted the *inodes* of /tmp (a 16G tmpfs) while it still
        # showed bytes free. Benchmarks then died with
        # ``java.io.IOException: No space left on device``, one cell scored 0/50
        # on a suite that was fine, and two more lost benchmarks silently.
        # Honouring $TMPDIR keeps that on the scratch volume where it belongs.
        tmpdir = os.environ.get("TMPDIR", "").strip()
        if tmpdir and not any(a.startswith("-Djava.io.tmpdir=") for a in args):
            args.append(f"-Djava.io.tmpdir={tmpdir}")

        # The operator goes to *every* arm, not just the armed rerun. It is inert
        # with no mutant armed (``id == ARMED`` folds away at -1), so the baseline
        # and coverage runs measure exactly what they did before -- but passing it
        # uniformly means the flag cannot drift between the two arms of a ratio.
        if self.mutant_op not in self.MUTANT_OPS:
            raise ValueError(
                f"mutant_op must be one of {'|'.join(self.MUTANT_OPS)}, got {self.mutant_op!r}"
            )
        args.append(f"-Djmhbench.mutant.op={self.mutant_op}")
        if self.mutant_op in self.MUTANT_BURN_OPS:
            if self.mutant_spin_tokens < 1:
                raise ValueError(
                    f"mutant_spin_tokens must be >= 1, got {self.mutant_spin_tokens}"
                )
            args.append(f"-Djmhbench.mutant.tokens={self.mutant_spin_tokens}")
        return args

    @classmethod
    def preset(cls, name: str) -> "JmhSettings":
        if name == "quick":
            return cls(
                forks=1,
                warmup_iterations=1,
                warmup_time_seconds=0.5,
                measurement_iterations=2,
                measurement_time_seconds=0.5,
                timeout_seconds=60,
                iteration_timeout_seconds=30,
            )
        if name == "default":
            return cls()
        if name == "campaign":
            # The reported campaign. Five forks rather than one deep fork:
            # iterations inside a fork are autocorrelated and exclude
            # fork-to-fork variance, which is the dominant component, so a
            # t-test over one fork is anti-conservative and deepening that fork
            # does not fix it (EVALUATION_ISSUES.md B4).
            return cls(
                forks=5,
                warmup_iterations=5,
                warmup_time_seconds=1.0,
                measurement_iterations=10,
                measurement_time_seconds=1.0,
                timeout_seconds=900,
                iteration_timeout_seconds=120,
            )
        if name == "llm4jmh":
            # Chen et al.'s exact invocation:
            #   -f 1 -wi 5 -w 500ms -i 30 -r 1000ms -bm thrpt -tu s -gc true
            # Kept for replication only; prefer `campaign` for new measurement,
            # since all 30 iterations come from a single fork.
            return cls(
                forks=1,
                warmup_iterations=5,
                warmup_time_seconds=0.5,
                measurement_iterations=30,
                measurement_time_seconds=1.0,
                timeout_seconds=900,
                iteration_timeout_seconds=120,
            )
        if name == "strong":
            return cls(
                forks=2,
                warmup_iterations=5,
                warmup_time_seconds=1.0,
                measurement_iterations=5,
                measurement_time_seconds=1.0,
                timeout_seconds=300,
                iteration_timeout_seconds=120,
            )
        raise ValueError(f"Unknown JMH preset: {name!r}")

    #: Preset names accepted on the command line, in increasing cost order.
    PRESETS = ("quick", "default", "strong", "campaign", "llm4jmh")


@dataclass
class RunConfig:
    """Full configuration for a benchmark run."""

    harness_name: str
    harness_kwargs: dict
    jmh: JmhSettings
    alpha: float = 0.05
    """Significance threshold (``p <`` *alpha*) for the regression-detection test.

    ``0.05`` matches common perf tooling (e.g. Criterion.rs) and is powered at
    the default JMH preset. Stricter levels (``0.01``) need more samples — pair
    them with ``--strong`` or a higher ``measurement_iterations``."""

    min_slowdown: float = 1.10
    """Global minimum slowdown ratio (regressed/base) that counts as a regression.

    ``1.10`` means "at least 10% slower". Combined with *alpha*, this is the
    realistic detection rule applied to every task: a fixed practical effect
    size plus statistical significance, rather than a per-task expected
    magnitude. A detector cannot know a regression's size ahead of time."""

    max_tasks: int | None = None
    skip_jmh: bool = False
    """If True, skip the JMH execution phase (compile + static check only).
    Useful for fast smoke tests."""

    sensitivity_sample: int = 0
    """Covering benchmarks probed per mutant for the sensitivity metric.

    ``0`` off, ``> 0`` a uniform random sample of that size, ``-1`` **every**
    covering benchmark (the CLI spells this ``--sensitivity-sample all``). Only
    ``-1`` makes mutation sensitivity a point estimate rather than a bound: a
    fixed *k* still gives full coverage only where *k* exceeds the largest
    fan-out, and there is no way to know that in advance
    (EVALUATION_ISSUES.md B1).

    The kill probe walks covering benchmarks most-hit-first and stops at the
    first detection, which is the cheap way to decide *whether* a mutant dies
    but a biased, truncated draw. Sensitivity -- the share of covering
    benchmarks that detect the mutant -- needs a sample that is neither ranked
    nor truncated, so it is collected separately. ``0`` keeps the run at its
    historical cost and leaves sensitivity computable only as a bound.

    Cost is roughly this many extra JMH invocations per covered mutant, minus
    whatever the kill probe already measured."""

    max_detect_attempts: int = 6
    """Covering benchmarks the *kill* probe walks, most-hit-first, before giving
    up. ``0`` or negative means no cap.

    This bounds the mutation *score*'s cost, not sensitivity: the probe stops at
    the first detection anyway. When ``sensitivity_sample`` is ``-1`` every
    covering benchmark is measured regardless, so this cap no longer truncates
    anything the sensitivity metric needs."""

    fpr_replicates: int = 0
    """If > 0, the runner is in *false-positive-rate* mode: instead of
    applying each regression patch, it runs the JMH benchmark ``fpr_replicates``
    additional times against the unmodified SUT and applies the same
    regression-detection statistical test. Any positive detection is a
    false positive, since the SUT bytecode is unchanged between runs."""
