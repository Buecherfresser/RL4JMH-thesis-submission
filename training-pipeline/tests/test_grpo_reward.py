"""Unit tests for the GRPO reward adapter (no JVM/torch: the runner client is faked)."""

from __future__ import annotations

from jmhgen.config.schema import GRPOConfig, RewardWeights
from jmhgen.rewards.grpo_adapter import build_grpo_reward_fn
from jmhgen.runner.client import RewardRequest, RewardResponse
from jmhgen.runner.types import CompileResult, ErrorKind, EvaluationResult, RunResult
from jmhgen.training.grpo_metrics import GrpoMetricsCollector

# A clean, conformant benchmark (all JMH config annotations, @Setup inputs, returned result):
# regex anti-pattern scan should find zero smells -> r_anti == 1.0.
_CLEAN_BENCH = (
    "package com.ex;\n"
    "import org.openjdk.jmh.annotations.*;\n"
    "import java.util.concurrent.TimeUnit;\n"
    "@State(Scope.Thread)\n"
    "@BenchmarkMode(Mode.AverageTime)\n"
    "@OutputTimeUnit(TimeUnit.NANOSECONDS)\n"
    "@Fork(1)\n@Warmup(iterations = 5)\n@Measurement(iterations = 5)\n"
    "public class FooBenchmark {\n"
    "    private int n;\n"
    "    @Setup\n    public void setup() { n = 7; }\n"
    "    @Benchmark\n    public Object measure() { return Foo.compute(n); }\n"
    "}"
)


class _FakeClient:
    """A RunnerClient stub whose compile/run outcome is dictated per request.

    ``compiled``/``ran`` are fixed for the whole batch; ``metadata['source']`` is populated
    from the request spec exactly as ``LocalRunnerClient`` does, so the regex anti-pattern
    reward can score the parsed source.
    """

    def __init__(self, *, compiled: bool = True, ran: bool = True) -> None:
        self.compiled = compiled
        self.ran = ran
        self.requests: list[RewardRequest] = []

    def evaluate(self, request: RewardRequest) -> RewardResponse:
        self.requests.append(request)
        compile_result = CompileResult(
            success=self.compiled,
            duration_s=1.0,
            error_kind=ErrorKind.NONE if self.compiled else ErrorKind.COMPILE_ERROR,
            project_dir="/tmp/fake",
        )
        run_result = None
        if request.need_run and self.compiled:
            run_result = RunResult(
                success=self.ran,
                duration_s=1.0,
                error_kind=ErrorKind.NONE if self.ran else ErrorKind.RUNTIME_ERROR,
            )
        return RewardResponse(
            evaluation=EvaluationResult(
                compile=compile_result,
                run=run_result,
                metadata={"source": request.spec.source},
            ),
            request_id=request.request_id,
        )

    def evaluate_batch(self, requests: list[RewardRequest]) -> list[RewardResponse]:
        return [self.evaluate(r) for r in requests]


def _config(**overrides) -> GRPOConfig:
    base = {
        "reward_weights": RewardWeights(compile=0.34, runtime=0.33, anti_pattern=0.33),
        "need_run": True,
    }
    base.update(overrides)
    return GRPOConfig(**base)


def _completion(text: str) -> list[dict[str, str]]:
    """A conversational completion the way TRL returns it for a chat prompt."""
    return [{"role": "assistant", "content": text}]


class TestBuildGrpoRewardFn:
    def test_clean_compiled_ran_scores_full(self) -> None:
        client = _FakeClient(compiled=True, ran=True)
        reward_fn = build_grpo_reward_fn(_config(), client=client)
        rewards = reward_fn(
            prompts=[[{"role": "user", "content": "x"}]],
            completions=[_completion(f"```java\n{_CLEAN_BENCH}\n```")],
            project=["commons-lang"],
            snippet_id=["com.ex.Foo"],
            class_name=["FooBenchmark"],
            package=["com.ex"],
        )
        assert len(rewards) == 1
        assert rewards[0] == 1.0  # 0.34 + 0.33 + 0.33 * 1.0 (clean)

    def test_unparseable_completion_scores_zero(self) -> None:
        client = _FakeClient(compiled=True, ran=True)
        reward_fn = build_grpo_reward_fn(_config(), client=client)
        rewards = reward_fn(
            completions=["I could not write a benchmark for this class."],
            snippet_id=["com.ex.Foo"],
        )
        assert rewards == [0.0]
        # An unparseable rollout must never reach the (expensive) runner.
        assert client.requests == []

    def test_compile_failure_does_not_earn_static_anti_pattern_reward(self) -> None:
        client = _FakeClient(compiled=False, ran=False)
        reward_fn = build_grpo_reward_fn(_config(), client=client)
        rewards = reward_fn(
            completions=[_completion(f"```java\n{_CLEAN_BENCH}\n```")],
            snippet_id=["com.ex.Foo"],
            package=["com.ex"],
        )
        # A clean-looking source is irrelevant if the generated benchmark does not compile.
        assert rewards[0] == 0.0

    def test_batch_and_plain_string_completions(self) -> None:
        client = _FakeClient(compiled=True, ran=True)
        reward_fn = build_grpo_reward_fn(_config(need_run=False), client=client)
        rewards = reward_fn(
            completions=[
                f"```java\n{_CLEAN_BENCH}\n```",  # plain string form
                "no java here",
            ],
            snippet_id=["com.ex.Foo", "com.ex.Bar"],
            package=["com.ex", "com.ex"],
        )
        assert len(rewards) == 2
        assert rewards[1] == 0.0
        # need_run=False -> compile-only; clean source still earns compile + anti_pattern.
        assert abs(rewards[0] - (0.34 + 0.33)) < 1e-9

    def test_metrics_collector_receives_component_rewards(self, tmp_path) -> None:
        collector = GrpoMetricsCollector(tmp_path)
        client = _FakeClient(compiled=True, ran=True)
        reward_fn = build_grpo_reward_fn(_config(), client=client, metrics=collector)
        reward_fn(
            completions=[_completion(f"```java\n{_CLEAN_BENCH}\n```")],
            project=["commons-lang"],
            snippet_id=["com.ex.Foo"],
            class_name=["FooBenchmark"],
            package=["com.ex"],
        )
        collector.flush_step(1, {"reward": 1.0, "reward_std": 0.0})
        event = (tmp_path / "rollouts.jsonl").read_text()
        assert '"compile": 1.0' in event
        assert '"runtime": 1.0' in event
        assert '"anti_pattern": 1.0' in event

    def test_missing_classpath_files_are_skipped(self, tmp_path, caplog) -> None:
        present = tmp_path / "present.cp"
        present.write_text("a.jar\n", encoding="utf-8")
        missing = tmp_path / "missing.cp"
        client = _FakeClient(compiled=True, ran=True)
        with caplog.at_level("WARNING", logger="jmhgen.rewards.grpo"):
            reward_fn = build_grpo_reward_fn(
                _config(
                    project_classpaths={
                        "ok-project": str(present),
                        "trove": str(missing),
                    }
                ),
                client=client,
            )
        assert any("skipping 1 missing project classpath" in r.message for r in caplog.records)
        assert any("trove" in r.message for r in caplog.records)
        rewards = reward_fn(
            completions=[_completion(f"```java\n{_CLEAN_BENCH}\n```")],
            project=["ok-project"],
            snippet_id=["com.ex.Foo"],
            package=["com.ex"],
        )
        assert rewards[0] == 1.0
        assert client.requests[0].spec.extra_classpath == (str((tmp_path / "a.jar").resolve()),)

    def test_reward_workers_preserves_completion_order(self, tmp_path) -> None:
        """Parallel scoring must return rewards positionally aligned with ``completions``.

        TRL pairs rewards with completions by index and the group-relative advantage is computed
        over that order, so a thread pool that returned results in completion order would
        silently scramble which rollout gets credited.
        """
        import threading
        import time

        seen: list[str] = []
        lock = threading.Lock()

        class _SlowOrderedClient(_FakeClient):
            """Sleeps in reverse index order, so an order-losing pool would visibly reorder."""

            def evaluate(self, request: RewardRequest) -> RewardResponse:
                index = int(str(request.request_id).rsplit("#", 1)[-1])
                time.sleep(0.05 * (4 - index))
                with lock:
                    seen.append(request.spec.source)
                return super().evaluate(request)

        # Each completion gets a distinct field name and a distinct compile outcome, so a pool
        # that returned results in *completion* order rather than *index* order would flip the
        # rewards and the assertion below would fail.
        sources = [_CLEAN_BENCH.replace("int n;", f"int n{i};") for i in range(5)]
        pool = [_SlowOrderedClient(compiled=i % 2 == 0, ran=i % 2 == 0) for i in range(5)]
        reward_fn = build_grpo_reward_fn(_config(reward_workers=5), clients=pool)
        rewards = reward_fn(
            completions=[_completion(f"```java\n{s}\n```") for s in sources],
            snippet_id=["com.ex.Foo"] * 5,
            package=["com.ex"] * 5,
        )
        # Completion i is scored by client i, which compiles iff i is even.
        assert rewards == [1.0, 0.0, 1.0, 0.0, 1.0]
        # And the pool really did run out of order: the sleeps are longest for the lowest index.
        assert seen == list(reversed(sources))

    def test_reward_workers_defaults_to_serial(self) -> None:
        assert _config().reward_workers == 1

    def test_injected_client_forces_serial_path(self) -> None:
        """``reward_workers`` must not silently share one injected client across threads."""
        client = _FakeClient(compiled=True, ran=True)
        reward_fn = build_grpo_reward_fn(_config(reward_workers=8), client=client)
        rewards = reward_fn(
            completions=[_completion(f"```java\n{_CLEAN_BENCH}\n```")] * 3,
            snippet_id=["com.ex.Foo"] * 3,
            package=["com.ex"] * 3,
        )
        assert rewards == [1.0, 1.0, 1.0]
        assert len(client.requests) == 3
