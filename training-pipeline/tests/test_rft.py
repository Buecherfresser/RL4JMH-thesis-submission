"""Unit tests for the RFT loop: helpers, the (mocked) verify phase, and dataset building."""

from __future__ import annotations

import json
from pathlib import Path

import pytest

import jmhgen.training.rft as rft
from jmhgen.config.schema import RewardWeights, RFTConfig
from jmhgen.data.schema import CodeSnippet
from jmhgen.rewards import build_composite_reward
from jmhgen.runner.client import RewardRequest, RewardResponse
from jmhgen.runner.maven import MavenJmhRunner
from jmhgen.runner.types import (
    BenchmarkStat,
    CompileResult,
    ErrorKind,
    EvaluationResult,
    RunResult,
)

_CONFORMANT = (
    "package com.ex;\n"
    "import org.openjdk.jmh.annotations.*;\n"
    "import org.openjdk.jmh.infra.Blackhole;\n"
    "import java.util.concurrent.TimeUnit;\n"
    "@State(Scope.Benchmark)\n"
    "@BenchmarkMode(Mode.AverageTime)\n"
    "@OutputTimeUnit(TimeUnit.NANOSECONDS)\n"
    "@Fork(1)\n@Warmup(iterations = 1)\n@Measurement(iterations = 3)\n"
    "public class FooBenchmark {\n"
    "    private int n;\n"
    "    @Setup\n    public void setup() { n = 7; }\n"
    "    @Benchmark\n    public Object measure() { return Foo.compute(n); }\n"
    "}"
)

# Compiles/runs (in the mock) but is non-conformant: missing config, void unused, never names Bar.
_NONCONFORMANT = (
    "package com.ex;\n"
    "import org.openjdk.jmh.annotations.*;\n"
    "@State(Scope.Thread)\n@BenchmarkMode(Mode.Throughput)\n"
    "@OutputTimeUnit(java.util.concurrent.TimeUnit.SECONDS)\n"
    "public class BarBenchmark {\n"
    "    @Benchmark\n    public void run() { helper(); }\n}"
)


# -- pure helpers ---------------------------------------------------------------------------


def test_normalize_java_ignores_comments_and_whitespace() -> None:
    a = "class A {\n  int x;  // a\n}"
    b = "class A{int x;/* b */}"
    assert rft._normalize_java(a) == rft._normalize_java(b)


def test_assistant_text_joins_separated_reasoning() -> None:
    assert rft._assistant_text("ANSWER", None) == "ANSWER"
    assert rft._assistant_text("ANSWER", "THINK") == "THINK\nANSWER"


def test_split_by_prompt_holds_out_whole_prompts() -> None:
    snip = CodeSnippet(id="com.ex.Foo", source="x")
    kept = [(f"id{i}", snip, f"t{i}") for i in range(10)]
    train, val = rft._split_by_prompt(kept, val_fraction=0.2, seed=1)
    assert len(val) == 2
    assert len(train) == 8
    train_ids = {i for i, _s, _t in train}
    val_ids = {i for i, _s, _t in val}
    assert train_ids.isdisjoint(val_ids)  # no prompt straddles the split


# -- reward builder -------------------------------------------------------------------------


def _eval(compiled: bool, ran: bool) -> EvaluationResult:
    compile_result = CompileResult(
        success=compiled,
        duration_s=1.0,
        error_kind=ErrorKind.NONE if compiled else ErrorKind.COMPILE_ERROR,
    )
    run_result = (
        RunResult(
            success=ran,
            duration_s=1.0,
            stats=(BenchmarkStat("x.B.m", "avgt", 1.0, 0.0, "ns/op"),) if ran else (),
            error_kind=ErrorKind.NONE if ran else ErrorKind.RUNTIME_ERROR,
        )
        if compiled
        else None
    )
    return EvaluationResult(compile=compile_result, run=run_result)


def test_build_composite_reward_uses_weights() -> None:
    reward = build_composite_reward(RewardWeights(compile=0.5, runtime=0.5))
    assert reward(_eval(True, True)).value == pytest.approx(1.0)
    assert reward(_eval(True, False)).value == pytest.approx(0.5)


def test_build_composite_reward_wires_mutation() -> None:
    # Mutation is now implemented: a weight on it produces a real component that reads the
    # precomputed score from the evaluation metadata (populated by the verify phase).
    reward = build_composite_reward(RewardWeights(compile=0.0, runtime=0.0, mutation=1.0))
    evaluation = _eval(True, True)
    evaluation.metadata["mutation"] = {"score": 1.0, "total": 2, "killed": 2}
    scored = reward(evaluation)
    assert scored.value == pytest.approx(1.0)
    assert "mutation" in scored.detail["components"]


def test_build_composite_reward_falls_back_when_no_weight() -> None:
    # No signal carries weight -> fall back to the cheap default rather than silently zeroing
    # the reward out.
    reward = build_composite_reward(RewardWeights(compile=0.0, runtime=0.0))
    assert reward(_eval(True, True)).value == pytest.approx(1.0)


# -- verify phase (mocked runner client) ----------------------------------------------------


class _FakeClient:
    """Pretends every parsed candidate compiles and runs, so acceptance is decided by the
    reward threshold and the conformance gate alone."""

    def __init__(self, _runner: object) -> None:
        pass

    def evaluate(self, request: RewardRequest) -> RewardResponse:
        return RewardResponse(evaluation=_eval(True, True), request_id=request.request_id)


def _write_generations(work: Path, rows: list[dict[str, object]]) -> None:
    work.mkdir(parents=True, exist_ok=True)
    with (work / "generations.jsonl").open("w", encoding="utf-8") as handle:
        for row in rows:
            handle.write(json.dumps(row) + "\n")


def test_phase_verify_conformance_gates_acceptance(tmp_path: Path, monkeypatch) -> None:
    work = tmp_path / "rft"
    _write_generations(
        work,
        [
            {
                "snippet_id": "com.ex.Foo",
                "project": "rxjava",
                "sample_index": 0,
                "assistant_text": "t",
                "parse_ok": True,
                "java_source": _CONFORMANT,
                "package": "com.ex",
                "class_name": "FooBenchmark",
            },
            {
                "snippet_id": "com.ex.Bar",
                "project": "rxjava",
                "sample_index": 0,
                "assistant_text": "t",
                "parse_ok": True,
                "java_source": _NONCONFORMANT,
                "package": "com.ex",
                "class_name": "BarBenchmark",
            },
            {
                "snippet_id": "com.ex.Baz",
                "project": "rxjava",
                "sample_index": 0,
                "assistant_text": "t",
                "parse_ok": False,
                "java_source": None,
                "package": None,
                "class_name": None,
            },
        ],
    )
    monkeypatch.setattr(MavenJmhRunner, "is_available", lambda self: True)
    monkeypatch.setattr(rft, "LocalRunnerClient", _FakeClient)

    config = RFTConfig(work_dir=str(work))
    scored_path = rft.phase_verify(config)
    scored_rows = [json.loads(x) for x in scored_path.read_text().splitlines()]
    by_id = {r["snippet_id"]: r for r in scored_rows}

    assert by_id["com.ex.Foo"]["accepted"] is True
    assert by_id["com.ex.Foo"]["compiled"] is True
    # Compiles+runs in the mock (reward 1.0) but the conformance gate rejects it.
    assert by_id["com.ex.Bar"]["accepted"] is False
    assert by_id["com.ex.Bar"]["conformance_ok"] is False
    # Unparseable candidates are never accepted and never sent to the runner.
    assert by_id["com.ex.Baz"]["accepted"] is False
    assert by_id["com.ex.Baz"]["conformance_reasons"] == ["parse_failed"]


# -- build phase (dedup, cap, drop-empty) ---------------------------------------------------


def _write_snippets(path: Path, ids: list[str]) -> None:
    with path.open("w", encoding="utf-8") as handle:
        for cid in ids:
            snippet = CodeSnippet(id=cid, source=f"package com.ex;\npublic class X {{}} // {cid}")
            handle.write(json.dumps(snippet.to_dict()) + "\n")


def _scored_row(sid: str, java: str, text: str, accepted: bool) -> dict[str, object]:
    return {
        "snippet_id": sid,
        "project": "rxjava",
        "assistant_text": text,
        "parse_ok": True,
        "java_source": java,
        "accepted": accepted,
    }


def test_phase_build_dedups_caps_and_drops_empty(tmp_path: Path) -> None:
    work = tmp_path / "rft"
    work.mkdir(parents=True)
    dataset = tmp_path / "snippets.jsonl"
    _write_snippets(dataset, ["com.ex.Foo", "com.ex.Bar"])

    java_a = "class A { @Benchmark void a(){} }"
    java_b = "class B { @Benchmark void b(){} }"
    java_c = "class C { @Benchmark void c(){} }"
    scored = [
        _scored_row("com.ex.Foo", java_a, "TA1", True),
        _scored_row("com.ex.Foo", java_a, "TA2", True),  # duplicate java -> deduped out
        _scored_row("com.ex.Foo", java_b, "TB", True),
        _scored_row("com.ex.Foo", java_c, "TC", False),  # not accepted
        _scored_row("com.ex.Bar", java_c, "TX", False),  # prompt with 0 accepted -> dropped
    ]
    with (work / "scored.jsonl").open("w", encoding="utf-8") as handle:
        for row in scored:
            handle.write(json.dumps(row) + "\n")

    config = RFTConfig(
        dataset_path=str(dataset),
        work_dir=str(work),
        max_keep_per_prompt=2,
        dedup=True,
        val_fraction=0.0,
    )
    n_train = rft.phase_build(config)

    assert n_train == 2  # TA1 (first of dup) + TB; TC rejected; Bar dropped
    dataset_rows = [json.loads(x) for x in (work / "sft.jsonl").read_text().splitlines()]
    assert len(dataset_rows) == 2
    roles = [m["role"] for m in dataset_rows[0]["messages"]]
    assert roles == ["system", "user", "assistant"]
    assistant_texts = {rows["messages"][-1]["content"] for rows in dataset_rows}
    assert assistant_texts == {"TA1", "TB"}

    report = json.loads((work / "report.json").read_text())
    assert report["prompts_with_accepted"] == 1
    assert report["train_samples"] == 2
    assert report["candidates_accepted"] == 3


def test_phase_build_drop_all_pass(tmp_path: Path) -> None:
    work = tmp_path / "rft"
    work.mkdir(parents=True)
    dataset = tmp_path / "snippets.jsonl"
    _write_snippets(dataset, ["com.ex.Foo"])
    with (work / "scored.jsonl").open("w", encoding="utf-8") as handle:
        for i in range(3):
            row = _scored_row("com.ex.Foo", f"class A{i}{{}}", f"T{i}", True)
            handle.write(json.dumps(row) + "\n")

    config = RFTConfig(
        dataset_path=str(dataset),
        work_dir=str(work),
        drop_all_pass=True,
        val_fraction=0.0,
    )
    # Every sample for the only prompt passed -> the prompt is dropped (no learning signal).
    assert rft.phase_build(config) == 0
