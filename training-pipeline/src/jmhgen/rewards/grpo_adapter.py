"""Adapter that turns the JMH reward stack into a ``trl.GRPOTrainer`` reward function.

``GRPOTrainer`` calls a reward function with the batch of rollouts and expects a list of
scalar rewards (one per completion). This module bridges that contract to the exact same
``LocalRunnerClient`` + :func:`~jmhgen.rewards.composite.build_composite_reward` path used by
RFT verification, so a candidate is scored identically whether it is filtered offline (RFT)
or reinforced online (GRPO):

    parse the completion -> build a ``BenchmarkSpec`` on the SUT classpath ->
    ``LocalRunnerClient.evaluate`` (compile [+ run]) -> ``CompositeReward`` -> scalar.

The rewards are computed **synchronously in the trainer process** (no async CPU fleet), but a
group's completions may be scored on a thread pool -- see ``config.reward_workers``. The JMH
build/run is the wall-clock bottleneck (measured: half a GRPO step), so keep the group size and
the JMH options (``config.runner.jmh``) small regardless. Nothing here imports torch/trl, so it
stays importable on a CPU-only reward box and is unit-testable without a JVM (inject a fake
``RunnerClient``, which also pins the serial path).
"""

from __future__ import annotations

import re
import tempfile
import time
from collections.abc import Callable, Sequence
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path
from typing import TYPE_CHECKING, Any

from jmhgen.data.classpath import load_classpath
from jmhgen.generate.parse import ensure_package_declaration, parse_completion
from jmhgen.generate.repair import repair_java_source
from jmhgen.mutation import MutantSpec, load_mutants, mutants_by_fqcn, score_benchmark_mutations
from jmhgen.rewards.composite import build_composite_reward
from jmhgen.runner.client import LocalRunnerClient, RewardRequest, RunnerClient
from jmhgen.runner.types import BenchmarkSpec, EvaluationResult, JmhOptions
from jmhgen.utils.logging import get_logger

if TYPE_CHECKING:  # pragma: no cover - type-only import (avoids a config<->rewards cycle)
    from jmhgen.config.schema import GRPOConfig

logger = get_logger("jmhgen.rewards.grpo")


def build_benchmark_spec(
    java_source: str,
    class_name: str,
    package: str,
    extra_classpath: Sequence[str],
) -> BenchmarkSpec:
    """Assemble the :class:`BenchmarkSpec` a candidate is compiled/run under.

    Shared by RFT verification and GRPO reward so both stages score a candidate identically:
    the model's package declaration is completed from metadata when missing (the Maven runner
    lays sources out by package and JMH's processor rejects a package-less file), and the
    code-under-test classpath is attached verbatim.
    """
    pkg = package or ""
    source = ensure_package_declaration(java_source, pkg)
    return BenchmarkSpec(
        source=source,
        class_name=class_name,
        package=pkg,
        extra_classpath=tuple(extra_classpath),
    )


_JAVAC_DIAGNOSTIC_RE = re.compile(r"^\[ERROR\]\s+\S+\.java:\[\d+,\d+\]\s+(.*)$", re.MULTILINE)
_MAX_DIAGNOSTICS = 12


def _compile_diagnostics(evaluation: EvaluationResult) -> tuple[str, ...]:
    """Pull the javac messages out of a failed compile, in source order.

    Maven puts them on stdout; stderr only carries JVM warnings. Deduplicated and capped so a
    file with 200 cascading syntax errors cannot dominate the metrics file.
    """
    if evaluation.compiled:
        return ()
    blob = f"{evaluation.compile.stdout or ''}\n{evaluation.compile.stderr or ''}"
    seen = dict.fromkeys(m.strip() for m in _JAVAC_DIAGNOSTIC_RE.findall(blob))
    return tuple(list(seen)[:_MAX_DIAGNOSTICS])


_RUN_DIAGNOSTIC_RES = (
    # JMH's own refusals. The lock one is the reason this function exists: run 7 recorded 987
    # rollouts as a bare ``runtime_error`` with no way to tell a real benchmark failure from
    # "another JMH had /tmp/jmh.lock", and the two demand opposite responses.
    re.compile(r"^ERROR:\s*(org\.openjdk\.jmh\.runner\.RunnerException:.*)$", re.MULTILINE),
    re.compile(r"^(<failure>.*)$", re.MULTILINE),
    # The benchmark itself threw: JMH reports the cause on its own line.
    re.compile(r"^(?:Caused by:\s*)?([A-Za-z_.$]+(?:Exception|Error)(?::.*)?)$", re.MULTILINE),
)
_MAX_RUN_DIAGNOSTICS = 4


def _run_diagnostics(evaluation: EvaluationResult) -> tuple[str, ...]:
    """Summarise why a compiled benchmark failed to run, in a scalar-safe form.

    Deliberately narrow: matched JMH/exception lines only, deduplicated and truncated. Never the
    benchmark source, the prompt, or the full JMH output.
    """
    run = evaluation.run
    if run is None or run.success:
        return ()
    blob = f"{run.stdout or ''}\n{run.stderr or ''}"
    seen: dict[str, None] = {}
    for pattern in _RUN_DIAGNOSTIC_RES:
        for match in pattern.findall(blob):
            seen.setdefault(match.strip()[:200], None)
            if len(seen) >= _MAX_RUN_DIAGNOSTICS:
                return tuple(seen)
    return tuple(seen)


def _completion_text(completion: Any) -> str:
    """Extract the assistant text from a TRL completion (conversational or plain string)."""
    if isinstance(completion, str):
        return completion
    if isinstance(completion, dict):
        return str(completion.get("content") or "")
    if isinstance(completion, (list, tuple)) and completion:
        # Conversational completion: a list of role/content messages; take the last one.
        last = completion[-1]
        if isinstance(last, dict):
            return str(last.get("content") or "")
        return str(last)
    return ""


def _simple_name(fqcn: str) -> str:
    return fqcn.rsplit(".", 1)[-1]


def _load_project_mutants(config: GRPOConfig) -> dict[str, dict[str, list[MutantSpec]]]:
    if config.reward_weights.mutation <= 0:
        return {}
    by_project: dict[str, dict[str, list[MutantSpec]]] = {}
    for project, registry_path in config.project_mutants.items():
        grouped = mutants_by_fqcn(load_mutants(registry_path))
        by_project[project] = grouped
        logger.info(
            "loaded %d mutant(s) across %d class(es) for project %s",
            sum(len(v) for v in grouped.values()),
            len(grouped),
            project,
        )
    return by_project


def _score_mutations_into(
    config: GRPOConfig,
    client: RunnerClient,
    spec: BenchmarkSpec,
    options: JmhOptions,
    evaluation: EvaluationResult,
    *,
    project: str | None,
    snippet_id: str | None,
    mutants_by_project: dict[str, dict[str, list[MutantSpec]]],
) -> None:
    if config.reward_weights.mutation <= 0 or not evaluation.ran or evaluation.run is None:
        return
    if not snippet_id:
        return
    per_class = mutants_by_project.get(project or "")
    if not per_class:
        return
    class_mutants = per_class.get(snippet_id)
    if not class_mutants:
        return
    runner = getattr(client, "runner", None)
    if runner is None:
        return
    mutation_score = score_benchmark_mutations(
        runner,
        spec,
        options,
        evaluation.run.stats,
        class_mutants,
        alpha=config.mutation.alpha,
        min_slowdown=config.mutation.min_slowdown,
        arm_property=config.mutation.arm_property,
        max_mutants=config.mutation.max_mutants_per_candidate,
        mode=config.mutation.mode,
        coverage_guidance=config.mutation.coverage_guidance,
        coverage_options=config.mutation.coverage_jmh.to_options(),
        record_property=config.mutation.record_property,
        record_file_property=config.mutation.record_file_property,
        max_benchmarks_per_mutant=config.mutation.max_benchmarks_per_mutant,
        coverage_weight=config.mutation.coverage_weight,
    )
    evaluation.metadata["mutation"] = mutation_score.to_dict()


def build_grpo_reward_fn(
    config: GRPOConfig,
    *,
    client: RunnerClient | None = None,
    clients: Sequence[RunnerClient] | None = None,
    metrics: Any | None = None,
) -> Callable[..., list[float]]:
    """Build the scalar reward function passed to ``trl.GRPOTrainer(reward_funcs=[...])``.

    Loads each project's SUT classpath once, wires the composite reward from
    ``config.reward_weights`` (including RSD/mutation and ``config.anti_pattern_backend``),
    and returns ``reward_fn(prompts, completions, **columns) -> list[float]``. TRL forwards
    each non-``prompt`` dataset column (``project``, ``snippet_id``, ``class_name``, ``package``)
    as a keyword list aligned with ``completions``. Pass ``client`` to inject a fake runner in
    tests; by default a :class:`LocalRunnerClient` over the configured Maven runner is used.
    ``metrics`` is an optional scalar-only collector; it never changes the TRL reward contract.
    """
    classpaths: dict[str, tuple[str, ...]] = {}
    missing_classpaths: list[str] = []
    for project, cp_file in config.project_classpaths.items():
        path = Path(cp_file)
        if not path.is_file():
            missing_classpaths.append(f"{project} -> {cp_file}")
            continue
        classpaths[project] = load_classpath(path)
        logger.info(
            "loaded %d classpath entries for project %s", len(classpaths[project]), project
        )
    if missing_classpaths:
        preview = "\n  - ".join(missing_classpaths[:20])
        extra = (
            f"\n  - ... and {len(missing_classpaths) - 20} more"
            if len(missing_classpaths) > 20
            else ""
        )
        logger.warning(
            "\n"
            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n"
            "WARNING: skipping %d missing project classpath file(s).\n"
            "Rollouts for those projects will compile without SUT jars (likely reward 0).\n"
            "Provision with jmh-provision-classpath / sync data/classpaths/ before full RL.\n"
            "  - %s%s\n"
            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!",
            len(missing_classpaths),
            preview,
            extra,
        )

    # One client per worker. A single injected ``client`` (the common test case) forces the
    # serial path so the fake-runner contract is unchanged; pass ``clients`` to drive the pool
    # with fakes.
    if clients is not None:
        pool_clients = list(clients)
    elif client is not None:
        pool_clients = [client]
    elif int(config.reward_workers) <= 1:
        pool_clients = [LocalRunnerClient(config.runner.build_runner())]
    else:
        workers = int(config.reward_workers)
        root = Path(config.runner.work_root or tempfile.gettempdir()) / "jmhgen_work"
        pool_clients = [
            LocalRunnerClient(config.runner.build_runner(work_root=str(root / f"w{i}")))
            for i in range(workers)
        ]
        logger.info("scoring rollouts on %d worker(s), work roots under %s", workers, root)
    options: JmhOptions = config.runner.jmh.to_options()
    composite = build_composite_reward(
        config.reward_weights,
        rsd_good=config.rsd_good,
        rsd_bad=config.rsd_bad,
        anti_pattern_backend=config.anti_pattern_backend,
    )
    mutants_by_project = _load_project_mutants(config)
    need_run = config.need_run
    repair_completions = config.repair_completions
    if repair_completions:
        logger.info("repairing rollouts before compile (imports / static Blackhole / fences)")

    def reward_fn(
        prompts: list[Any] | None = None,
        completions: list[Any] | None = None,
        **columns: Any,
    ) -> list[float]:
        completions = completions or []
        projects = columns.get("project") or [None] * len(completions)
        snippet_ids = columns.get("snippet_id") or [None] * len(completions)
        class_names = columns.get("class_name") or [None] * len(completions)
        packages = columns.get("package") or [""] * len(completions)

        def score_one(i: int) -> float:
            completion = completions[i]
            client = pool_clients[i % len(pool_clients)]
            started_at = time.perf_counter()
            snippet_id = snippet_ids[i]
            fallback_pkg = str(packages[i] or "")
            fallback_class = class_names[i] or (
                f"{_simple_name(snippet_id)}Benchmark" if snippet_id else "GeneratedBenchmark"
            )
            parsed = parse_completion(
                _completion_text(completion),
                fallback_package=fallback_pkg,
                fallback_class=fallback_class,
            )
            if parsed is None:
                if metrics is not None:
                    metrics.record_rollout(
                        project=str(projects[i] or ""),
                        snippet_id=str(snippet_id or ""),
                        parse_ok=False,
                        compiled=False,
                        ran=False,
                        compile_error_kind=None,
                        run_error_kind=None,
                        reward=0.0,
                        reward_components=None,
                        duration_s=time.perf_counter() - started_at,
                    )
                return 0.0

            java_source = parsed.java_source
            repairs: tuple[str, ...] = ()
            if repair_completions:
                report = repair_java_source(java_source)
                java_source, repairs = report.source, report.applied

            spec = build_benchmark_spec(
                java_source,
                parsed.class_name,
                parsed.package or fallback_pkg,
                classpaths.get(projects[i] or "", ()),
            )
            request = RewardRequest(
                spec=spec,
                options=options,
                need_run=need_run,
                request_id=f"{snippet_id}#{i}" if snippet_id else str(i),
            )
            evaluation = client.evaluate(request).evaluation
            _score_mutations_into(
                config,
                client,
                spec,
                options,
                evaluation,
                project=projects[i],
                snippet_id=snippet_ids[i],
                mutants_by_project=mutants_by_project,
            )
            reward = composite(evaluation)
            if metrics is not None:
                metrics.record_rollout(
                    project=str(projects[i] or ""),
                    snippet_id=str(snippet_id or ""),
                    parse_ok=True,
                    compiled=evaluation.compiled,
                    ran=evaluation.ran,
                    compile_error_kind=evaluation.compile.error_kind.value,
                    run_error_kind=evaluation.run.error_kind.value if evaluation.run else None,
                    reward=float(reward.value),
                    reward_components=reward.detail.get("components"),
                    duration_s=time.perf_counter() - started_at,
                    repairs=repairs,
                    # Maven prints javac diagnostics to STDOUT as
                    # ``[ERROR] /path/File.java:[line,col] message`` -- not stderr, and never
                    # containing the string "error:". Runs 1 and 2 stored only the error *kind*,
                    # so neither could say which compile errors dominated. Keep a bounded tail.
                    compile_diagnostics=_compile_diagnostics(evaluation),
                    # Run 7 could not distinguish a benchmark that threw from one that lost the
                    # JMH lock; both landed as ``runtime_error``. 40.7 % of compiled rollouts
                    # went that way, so the ambiguity was worth ~9 % of the whole run.
                    run_diagnostics=_run_diagnostics(evaluation),
                    # Only stored when config.capture_completions_every is set; the collector
                    # drops it otherwise, so the default metrics dir stays scalar-only.
                    source=java_source,
                )
            # Reclaim the per-candidate Maven work dir so a long RL run doesn't fill the disk.
            runner = getattr(client, "runner", None)
            if runner is not None and hasattr(runner, "cleanup"):
                runner.cleanup(spec)
            return float(reward.value)

        def score_one_guarded(i: int) -> float:
            """``score_one`` with a hard exception boundary around it.

            The reward runs untrusted, model-written Java through Maven and JMH. An exception here
            does not stay local: it propagates out of the worker thread, through TRL's
            ``_calculate_rewards``, and aborts the rank -- which under torchrun kills the whole
            distributed run. GRPO run 9 lost 208 steps that way to a single ``UnicodeDecodeError``
            on non-UTF-8 Maven output (2026-08-12).

            Scoring the rollout 0.0 is the same outcome an uncompilable candidate already gets, so
            the objective is unchanged, and the failure stays visible: it is logged with a
            traceback and recorded under ``run_error_kind="reward_exception"`` so a systematic
            breakage shows up in the funnel instead of masquerading as a policy that stopped
            learning.
            """
            try:
                return score_one(i)
            except Exception:  # noqa: BLE001 - a dead run is strictly worse than a zero reward
                logger.exception(
                    "reward evaluation raised for rollout %d (project=%s snippet=%s); scoring 0.0",
                    i,
                    projects[i],
                    snippet_ids[i],
                )
                if metrics is not None:
                    metrics.record_rollout(
                        project=str(projects[i] or ""),
                        snippet_id=str(snippet_ids[i] or ""),
                        parse_ok=False,
                        compiled=False,
                        ran=False,
                        compile_error_kind=None,
                        run_error_kind="reward_exception",
                        reward=0.0,
                        reward_components=None,
                        duration_s=0.0,
                    )
                return 0.0

        if len(pool_clients) > 1 and len(completions) > 1:
            # Order matters: TRL pairs rewards with completions positionally, and the
            # group-relative advantage is computed over that order.
            with ThreadPoolExecutor(max_workers=len(pool_clients)) as pool:
                rewards = list(pool.map(score_one_guarded, range(len(completions))))
        else:
            rewards = [score_one_guarded(i) for i in range(len(completions))]
        if metrics is not None:
            metrics.finish_reward_batch(len(completions))
        return rewards

    # TRL uses the function name to label the reward column in its logs.
    reward_fn.__name__ = "jmh_composite_reward"
    return reward_fn
