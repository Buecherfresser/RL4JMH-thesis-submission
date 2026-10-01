"""Unit tests for the SFT dataset builder (synthetic fixture; no real data or JVM needed)."""

from __future__ import annotations

import json
from pathlib import Path

import pytest

from jmhgen.data import (
    build_dataset,
    build_sample,
    iter_run_locked,
    load_benchmark_samples,
    select_final_source,
)
from jmhgen.data.build_sft import RUN_LOCK_SUFFIX
from jmhgen.data.conformance import is_conformant, violations
from jmhgen.data.prompts import render_messages
from jmhgen.data.schema import BenchmarkSample, CodeSnippet


def _bench(class_name: str, param: str) -> str:
    return (
        "package com.ex;\n\n"
        "import org.openjdk.jmh.annotations.*;\n\n"
        "@State(Scope.Thread)\n"
        f"public class {class_name} {{\n"
        f'    @Param({{"{param}"}})\n'
        "    public int n;\n"
        "    @Benchmark\n"
        "    public int run() { return n; }\n"
        "}\n"
    )


def _java(class_name: str) -> str:
    return f"package com.ex;\n\npublic class {class_name} {{\n    int v;\n}}\n"


def _write(path: Path, text: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text, encoding="utf-8")


@pytest.fixture
def distilled(tmp_path: Path) -> Path:
    """A synthetic distilled tree under ``com/ex`` exercising every stage combination."""
    root = tmp_path / "distilled"
    pkg = root / "com" / "ex"

    # Foo: needed a compile fix -> comp source must win over the (buggy) syn source.
    _write(pkg / f"Foo{RUN_LOCK_SUFFIX}", "lock")
    _write(pkg / "Foo.stg_r0_stg_syn_syn_out_0", _bench("FooBenchmark", "1_000"))
    _write(pkg / "Foo.stg_r0_stg_comp_syn_out_0", _bench("FooBenchmark", "1000"))

    # Bar: clean first try -> syn source is used.
    _write(pkg / f"Bar{RUN_LOCK_SUFFIX}", "lock")
    _write(pkg / "Bar.stg_r0_stg_syn_syn_out_0", _bench("BarBenchmark", "100"))

    # Run1: run-stage repair present -> run source wins over comp and syn.
    _write(pkg / f"Run1{RUN_LOCK_SUFFIX}", "lock")
    _write(pkg / "Run1.stg_r0_stg_syn_syn_out_0", _bench("Run1Benchmark", "1"))
    _write(pkg / "Run1.stg_r0_stg_comp_syn_out_0", _bench("Run1Benchmark", "2"))
    _write(pkg / "Run1.stg_r0_stg_run_syn_out_0", _bench("Run1Benchmark", "3"))

    # Baz: run-locked but its Java source is absent (simulates commit drift).
    _write(pkg / f"Baz{RUN_LOCK_SUFFIX}", "lock")
    _write(pkg / "Baz.stg_r0_stg_syn_syn_out_0", _bench("BazBenchmark", "5"))

    # Qux: synthesized but NOT run-locked -> must be excluded entirely.
    _write(pkg / "Qux.stg_r0_stg_syn_syn_out_0", _bench("QuxBenchmark", "9"))

    return root


@pytest.fixture
def java_root(tmp_path: Path) -> Path:
    root = tmp_path / "java"
    for name in ("Foo", "Bar", "Run1"):  # deliberately no Baz.java
        _write(root / "src" / "main" / "java" / "com" / "ex" / f"{name}.java", _java(name))
    return root


def test_iter_run_locked_selects_only_locked(distilled: Path) -> None:
    items = list(iter_run_locked(distilled))
    assert [it.fqcn for it in items] == [
        "com.ex.Bar",
        "com.ex.Baz",
        "com.ex.Foo",
        "com.ex.Run1",
    ]
    assert all(it.package == "com.ex" for it in items)


def test_select_final_source_precedence(distilled: Path) -> None:
    by_name = {it.class_name: it for it in iter_run_locked(distilled)}

    foo_src, foo_stage = select_final_source(by_name["Foo"])
    assert foo_stage == "comp"
    assert '"1000"' in foo_src and '"1_000"' not in foo_src

    bar_src, bar_stage = select_final_source(by_name["Bar"])
    assert bar_stage == "syn"

    run_src, run_stage = select_final_source(by_name["Run1"])
    assert run_stage == "run"
    assert '"3"' in run_src


def test_build_sample_fields_and_fork_scores(distilled: Path, java_root: Path) -> None:
    fork_dir = distilled.parent / "fork"
    _write(
        fork_dir / "com.ex.FooBenchmark.run.json",
        json.dumps(
            [
                {
                    "benchmark": "com.ex.FooBenchmark.run",
                    "mode": "thrpt",
                    "primaryMetric": {"score": 123.4, "scoreUnit": "ops/s"},
                }
            ]
        ),
    )
    item = next(it for it in iter_run_locked(distilled) if it.class_name == "Foo")
    sample = build_sample(item, java_root, fork_dir=fork_dir, commit="deadbeef")

    assert sample.snippet.id == "com.ex.Foo"
    assert sample.snippet.source == _java("Foo")
    assert sample.snippet.path == "src/main/java/com/ex/Foo.java"
    assert sample.metadata["stage"] == "comp"
    assert sample.metadata["benchmark_class"] == "FooBenchmark"
    assert sample.metadata["benchmark_fqcn"] == "com.ex.FooBenchmark"
    assert sample.metadata["java_source_commit"] == "deadbeef"
    assert sample.metadata["fork_scores"] == [
        {"benchmark": "com.ex.FooBenchmark.run", "mode": "thrpt", "score": 123.4, "unit": "ops/s"}
    ]


def test_build_sample_missing_java_source_raises(distilled: Path, java_root: Path) -> None:
    baz = next(it for it in iter_run_locked(distilled) if it.class_name == "Baz")
    with pytest.raises(FileNotFoundError):
        build_sample(baz, java_root)


def test_build_dataset_report(distilled: Path, java_root: Path) -> None:
    result = build_dataset(distilled, java_root, commit="deadbeef")

    assert result.report["total_run_locked"] == 4
    assert result.report["resolved"] == 3
    assert result.report["missing_java_source"] == ["com.ex.Baz"]
    assert result.report["stage_counts"] == {"comp": 1, "syn": 1, "run": 1}
    assert {s.snippet.id for s in result.samples} == {"com.ex.Foo", "com.ex.Bar", "com.ex.Run1"}


def test_render_messages_shape() -> None:
    sample = BenchmarkSample(
        snippet=CodeSnippet(id="com.ex.Foo", source="class Foo {}"),
        benchmark_source="class FooBenchmark {}",
    )
    messages = render_messages(sample, template="canonical")
    assert [m["role"] for m in messages] == ["user", "assistant"]
    assert "```java\nclass Foo {}\n```" in messages[0]["content"]
    assert messages[1]["content"] == "```java\nclass FooBenchmark {}\n```"

    raw = render_messages(sample, fence_assistant=False)
    assert raw[1]["content"] == "class FooBenchmark {}"


def _conformant_bench(class_name: str, sut: str) -> str:
    return (
        "package com.ex;\n\n"
        "import org.openjdk.jmh.annotations.*;\n"
        "import org.openjdk.jmh.infra.Blackhole;\n"
        "import java.util.concurrent.TimeUnit;\n\n"
        "@State(Scope.Benchmark)\n"
        "@BenchmarkMode(Mode.AverageTime)\n"
        "@OutputTimeUnit(TimeUnit.NANOSECONDS)\n"
        "@Fork(1)\n@Warmup(iterations = 1)\n@Measurement(iterations = 3)\n"
        f"public class {class_name} {{\n"
        "    private int n;\n"
        "    @Setup\n    public void setup() { n = 7; }\n"
        "    @Benchmark\n"
        f"    public Object measure() {{ return {sut}.compute(n); }}\n"
        "}\n"
    )


def test_render_messages_jmhbench_shape() -> None:
    sample = BenchmarkSample(
        snippet=CodeSnippet(id="com.ex.Foo", source="package com.ex;\npublic class Foo {}"),
        benchmark_source=_conformant_bench("FooBenchmark", "Foo"),
        metadata={"benchmark_class": "FooBenchmark"},
    )
    messages = render_messages(sample, template="jmhbench", fence_assistant=False)
    assert [m["role"] for m in messages] == ["system", "user", "assistant"]
    # System carries the (WiP) rules; user names the subject and embeds its source.
    assert "Hard rules" in messages[0]["content"]
    assert "Target: `com.ex.Foo`" in messages[1]["content"]
    assert "Subject under test source" in messages[1]["content"]
    # Assistant target is raw (no markdown fence).
    assert not messages[2]["content"].lstrip().startswith("```")
    assert messages[2]["content"] == sample.benchmark_source.rstrip()


def test_render_messages_jmhbench_target_method() -> None:
    sample = BenchmarkSample(
        snippet=CodeSnippet(id="com.ex.Foo", source="package com.ex;\npublic class Foo {}"),
        benchmark_source=_conformant_bench("FooBenchmark", "Foo"),
        metadata={"target_method": "compute"},
    )
    user = render_messages(sample, template="jmhbench", fence_assistant=False)[1]["content"]
    assert "Target: `com.ex.Foo.compute`" in user
    # The prompt carries no free-text task description.
    assert "Task description" not in user


def test_render_messages_jmhbench_encourages_multiple_benchmarks() -> None:
    sample = BenchmarkSample(
        snippet=CodeSnippet(id="com.ex.Foo", source="package com.ex;\npublic class Foo {}"),
        benchmark_source="package com.ex;\npublic class FooBenchmark {}",
    )
    messages = render_messages(sample, template="jmhbench", fence_assistant=False)
    system, user = messages[0]["content"], messages[1]["content"]
    # The system prompt has an explicit coverage rule; the user shape shows >1 @Benchmark.
    assert "Coverage:" in system
    assert "one @Benchmark per behaviour" in user
    assert "measureOne" in user and "measureAnother" in user


def test_conformance_accepts_and_reports() -> None:
    good = BenchmarkSample(
        snippet=CodeSnippet(id="com.ex.Foo", source="x"),
        benchmark_source=_conformant_bench("FooBenchmark", "Foo"),
    )
    assert violations(good) == []
    assert is_conformant(good)

    # Missing @Fork/@Warmup/@Measurement, never names the subject, and drops the void result.
    bad = BenchmarkSample(
        snippet=CodeSnippet(id="com.ex.Bar", source="x"),
        benchmark_source=(
            "package com.ex;\n"
            "import org.openjdk.jmh.annotations.*;\n"
            "@State(Scope.Thread)\n@BenchmarkMode(Mode.Throughput)\n"
            "@OutputTimeUnit(java.util.concurrent.TimeUnit.SECONDS)\n"
            "public class BarBenchmark {\n"
            "    @Benchmark\n    public void run() { helper(); }\n}\n"
        ),
    )
    reasons = violations(bad)
    assert any(r.startswith("missing_jmh_config") for r in reasons)
    assert "result_unused" in reasons
    assert "sut_not_called" in reasons


def test_conformance_allows_state_object_param() -> None:
    sample = BenchmarkSample(
        snippet=CodeSnippet(id="com.ex.Foo", source="x"),
        benchmark_source=(
            "package com.ex;\n"
            "import org.openjdk.jmh.annotations.*;\n"
            "import org.openjdk.jmh.infra.Blackhole;\n"
            "@BenchmarkMode(Mode.Throughput)\n"
            "@OutputTimeUnit(java.util.concurrent.TimeUnit.SECONDS)\n"
            "@Fork(1)\n@Warmup(iterations = 1)\n@Measurement(iterations = 1)\n"
            "public class FooBenchmark {\n"
            "    @State(Scope.Benchmark)\n    public static class St { int n = Foo.seed(); }\n"
            "    @Benchmark\n"
            "    public void run(St st, Blackhole bh) { bh.consume(Foo.use(st.n)); }\n}\n"
        ),
    )
    assert violations(sample) == []


def test_records_roundtrip(distilled: Path, java_root: Path, tmp_path: Path) -> None:
    result = build_dataset(distilled, java_root, commit="deadbeef")
    out = tmp_path / "records.jsonl"
    out.write_text(
        "\n".join(json.dumps(s.to_dict()) for s in result.samples) + "\n", encoding="utf-8"
    )

    loaded = list(load_benchmark_samples(out))
    assert len(loaded) == len(result.samples)
    assert loaded[0].to_dict() == result.samples[0].to_dict()
