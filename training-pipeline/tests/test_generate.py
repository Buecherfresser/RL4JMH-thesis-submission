"""Unit tests for completion parsing and the (mocked) RFT generate phase."""

from __future__ import annotations

import json
from pathlib import Path

from jmhgen.config.schema import RFTConfig
from jmhgen.data.schema import CodeSnippet
from jmhgen.generate.parse import ensure_package_declaration, parse_completion, strip_thinking
from jmhgen.generate.vllm_client import Completion, SamplingParams

_GOOD_JAVA = (
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


class TestStripThinking:
    def test_removes_well_formed_block(self) -> None:
        assert strip_thinking("<think>reason</think>answer") == "answer"

    def test_recovers_after_stray_close_tag(self) -> None:
        # The open tag was stripped upstream; the answer is after the last close tag.
        assert strip_thinking("leftover reasoning</think>\nanswer").strip() == "answer"

    def test_drops_dangling_open_block(self) -> None:
        assert strip_thinking("<think>truncated reasoning with no close") == ""


class TestParseCompletion:
    def test_fenced_after_thinking(self) -> None:
        text = f"<think>call Foo.compute</think>\n```java\n{_GOOD_JAVA}\n```"
        parsed = parse_completion(text)
        assert parsed is not None
        assert parsed.class_name == "FooBenchmark"
        assert parsed.package == "com.ex"
        assert parsed.fully_qualified_name == "com.ex.FooBenchmark"
        assert "class FooBenchmark" in parsed.java_source
        assert "<think>" not in parsed.java_source

    def test_raw_unfenced(self) -> None:
        text = f"<think>x</think>\n{_GOOD_JAVA}\n"
        parsed = parse_completion(text)
        assert parsed is not None
        assert parsed.java_source.strip().endswith("}")

    def test_separated_reasoning_content(self) -> None:
        # When the server splits reasoning out, the content is already the answer.
        parsed = parse_completion(_GOOD_JAVA, reasoning_content="some reasoning")
        assert parsed is not None
        assert parsed.class_name == "FooBenchmark"

    def test_returns_none_without_java(self) -> None:
        assert parse_completion("<think>...</think>\nI cannot generate one.") is None

    def test_fallback_names_when_missing(self) -> None:
        # A benchmark with no package keyword falls back to the provided defaults.
        body = (
            "@State(Scope.Thread)\npublic class B {\n"
            "    @Benchmark\n    public int m() { return Foo.compute(); }\n}"
        )
        parsed = parse_completion(body, fallback_package="com.ex", fallback_class="X")
        assert parsed is not None
        assert parsed.package == "com.ex"  # inferred package absent -> fallback
        assert parsed.class_name == "B"
        assert parsed.java_source.startswith("package com.ex;")


class TestEnsurePackageDeclaration:
    def test_noop_when_package_present(self) -> None:
        src = "package com.ex;\npublic class X {}"
        assert ensure_package_declaration(src, "com.ex") == src

    def test_injects_when_missing(self) -> None:
        src = "import foo.Bar;\npublic class X {}"
        out = ensure_package_declaration(src, "com.ex")
        assert out.startswith("package com.ex;\n\nimport foo.Bar;")

    def test_noop_for_empty_package(self) -> None:
        src = "public class X {}"
        assert ensure_package_declaration(src, "") == src


class _FakeClient:
    """Stands in for VLLMChatClient: returns canned completions, ignores the network."""

    last_max_concurrency: int | None = None

    def __init__(self, *_args: object, **kwargs: object) -> None:
        type(self).last_max_concurrency = kwargs.get("max_concurrency")  # type: ignore[assignment]

    def complete_many(
        self, prompts: list[list[dict[str, str]]], params: SamplingParams
    ) -> list[list[Completion]]:
        content = f"<think>reason about Foo</think>\n```java\n{_GOOD_JAVA}\n```"
        return [[Completion(content=content)] for _ in prompts]


def test_phase_generate_writes_parsed_rows(tmp_path: Path, monkeypatch) -> None:
    import jmhgen.training.rft as rft

    snippet = CodeSnippet(
        id="com.ex.Foo",
        source="package com.ex;\npublic class Foo { static int compute(int n){return n;} }",
        project="rxjava",
        metadata={"package": "com.ex"},
    )
    dataset = tmp_path / "snippets.jsonl"
    dataset.write_text(json.dumps(snippet.to_dict()) + "\n", encoding="utf-8")

    monkeypatch.setattr(rft, "VLLMChatClient", _FakeClient)
    config = RFTConfig(
        dataset_path=str(dataset),
        work_dir=str(tmp_path / "rft"),
        samples_per_prompt=1,
    )
    gen_path = rft.phase_generate(config)
    rows = [json.loads(line) for line in gen_path.read_text().splitlines()]

    assert len(rows) == 1
    assert rows[0]["parse_ok"] is True
    assert rows[0]["snippet_id"] == "com.ex.Foo"
    assert "class FooBenchmark" in rows[0]["java_source"]
    assert rows[0]["assistant_text"].lstrip().startswith("<think>")


def test_phase_generate_uses_configured_max_concurrency(tmp_path: Path, monkeypatch) -> None:
    import jmhgen.training.rft as rft
    from jmhgen.config.schema import InferenceConfig, RFTConfig

    snippet = CodeSnippet(
        id="com.ex.Foo",
        source="package com.ex;\npublic class Foo { static int compute(int n){return n;} }",
        project="rxjava",
        metadata={"package": "com.ex"},
    )
    dataset = tmp_path / "snippets.jsonl"
    dataset.write_text(json.dumps(snippet.to_dict()) + "\n", encoding="utf-8")

    monkeypatch.setattr(rft, "VLLMChatClient", _FakeClient)
    config = RFTConfig(
        dataset_path=str(dataset),
        work_dir=str(tmp_path / "rft"),
        samples_per_prompt=1,
        inference=InferenceConfig(max_concurrency=3),
    )
    rft.phase_generate(config)
    assert _FakeClient.last_max_concurrency == 3


def test_inference_config_accepts_max_parallel_requests_alias(tmp_path: Path) -> None:
    from jmhgen.config.schema import RFTConfig

    cfg_yaml = tmp_path / "rft.yaml"
    cfg_yaml.write_text(
        "dataset_path: data/x.jsonl\ninference:\n  max_parallel_requests: 4\n",
        encoding="utf-8",
    )
    config = RFTConfig.from_yaml(cfg_yaml)
    assert config.inference.max_concurrency == 4


def test_phase_generate_skips_oversized_snippets(tmp_path: Path, monkeypatch) -> None:
    import jmhgen.training.rft as rft

    small = CodeSnippet(
        id="com.ex.Small",
        source="package com.ex;\npublic class Small { static int f(){return 1;} }",
        project="rxjava",
        metadata={"package": "com.ex"},
    )
    big = CodeSnippet(
        id="com.ex.Big",
        source="package com.ex;\npublic class Big {" + "int x;" * 500 + "}",
        project="rxjava",
        metadata={"package": "com.ex"},
    )
    dataset = tmp_path / "snippets.jsonl"
    with dataset.open("w", encoding="utf-8") as handle:
        handle.write(json.dumps(small.to_dict()) + "\n")
        handle.write(json.dumps(big.to_dict()) + "\n")

    monkeypatch.setattr(rft, "VLLMChatClient", _FakeClient)
    config = RFTConfig(
        dataset_path=str(dataset),
        work_dir=str(tmp_path / "rft"),
        samples_per_prompt=1,
        max_source_chars=200,
    )
    gen_path = rft.phase_generate(config)
    rows = [json.loads(line) for line in gen_path.read_text().splitlines()]

    # The oversized subject is dropped (not truncated); only the small one is sampled.
    assert [r["snippet_id"] for r in rows] == ["com.ex.Small"]


def test_phase_generate_is_cached(tmp_path: Path, monkeypatch) -> None:
    import jmhgen.training.rft as rft

    dataset = tmp_path / "snippets.jsonl"
    snippet = CodeSnippet(id="com.ex.Foo", source="class Foo {}", project="rxjava")
    dataset.write_text(json.dumps(snippet.to_dict()) + "\n", encoding="utf-8")
    work = tmp_path / "rft"
    work.mkdir()
    cached = work / "generations.jsonl"
    cached.write_text('{"cached": true}\n', encoding="utf-8")

    def _boom(*_a: object, **_k: object) -> None:
        raise AssertionError("client must not be constructed when generations are cached")

    monkeypatch.setattr(rft, "VLLMChatClient", _boom)
    config = RFTConfig(dataset_path=str(dataset), work_dir=str(work))
    assert rft.phase_generate(config) == cached


class TestExtractsAnswerNotDraft:
    """Gemma 4's reasoning arrives as undelimited prose, so the parser must not take a draft."""

    def test_prefers_last_fenced_block(self) -> None:
        draft = _GOOD_JAVA.replace("FooBenchmark", "DraftBenchmark")
        text = (
            "Let me analyse the class. A first attempt:\n"
            f"```java\n{draft}\n```\n"
            "That is wrong, it never uses @Setup. Redoing it:\n"
            f"```java\n{_GOOD_JAVA}\n```"
        )
        parsed = parse_completion(text)
        assert parsed is not None
        assert parsed.class_name == "FooBenchmark"
        assert "DraftBenchmark" not in parsed.java_source

    def test_skips_subject_source_quoted_in_reasoning(self) -> None:
        # The model quotes the class under test (its own package line) before answering.
        text = (
            "package com.sut;\npublic class Foo { public static int compute(int n) "
            "{ return n; } }\n\nNow the benchmark:\n\n" + _GOOD_JAVA
        )
        parsed = parse_completion(text)
        assert parsed is not None
        assert parsed.package == "com.ex"
        assert "class Foo {" not in parsed.java_source

    def test_never_returns_a_block_with_fence_remnants(self) -> None:
        parsed = parse_completion(f"```java\n{_GOOD_JAVA}\n```")
        assert parsed is not None
        assert "```" not in parsed.java_source
