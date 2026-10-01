"""Unit tests for the external benchmark scraper (synthetic repo; no network or JVM needed)."""

from __future__ import annotations

from pathlib import Path

import pytest

from jmhgen.data.scrape_external import (
    RepoSpec,
    build_index,
    detect_benchmark_class,
    is_jmh_benchmark,
    resolve_target,
    scrape_repo,
    strip_benchmark_suffix,
)


def _write(path: Path, text: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text, encoding="utf-8")


def _src(package: str, name: str) -> str:
    return f"package {package};\n\npublic class {name} {{\n    int v;\n}}\n"


def _bench(package: str, name: str, *, imports: tuple[str, ...] = (), body: str = "") -> str:
    import_block = "".join(f"import {imp};\n" for imp in imports)
    return (
        f"package {package};\n\n"
        "import org.openjdk.jmh.annotations.*;\n"
        f"{import_block}\n"
        "@BenchmarkMode(Mode.Throughput)\n"
        f"public class {name} {{\n"
        f"{body}"
        "    @Benchmark\n"
        "    public int run() { return 1; }\n"
        "}\n"
    )


@pytest.fixture
def repo(tmp_path: Path) -> Path:
    """A synthetic monorepo exercising every resolution path and skip reason."""
    root = tmp_path / "repo"

    # naming: one global LruCache, benchmark in a different package that *names* it.
    _write(root / "core/src/main/java/com/ex/cache/LruCache.java", _src("com.ex.cache", "LruCache"))
    _write(
        root / "jmh-benchmarks/src/main/java/com/ex/jmh/LruCacheBenchmark.java",
        _bench("com.ex.jmh", "LruCacheBenchmark", body="    LruCache cache;\n"),
    )

    # import: two Status classes exist; the explicit import disambiguates to the api one.
    _write(root / "api/src/main/java/com/ex/api/Status.java", _src("com.ex.api", "Status"))
    _write(root / "net/src/main/java/com/ex/net/Status.java", _src("com.ex.net", "Status"))
    _write(
        root / "api/src/jmh/java/com/ex/api/StatusBenchmark.java",
        _bench("com.ex.api", "StatusBenchmark", imports=("com.ex.api.Status",)),
    )

    # same_package: two Widget classes; the benchmark's own package picks the local one.
    _write(root / "api/src/main/java/com/ex/api/Widget.java", _src("com.ex.api", "Widget"))
    _write(root / "net/src/main/java/com/ex/net/Widget.java", _src("com.ex.net", "Widget"))
    _write(
        root / "api/src/jmh/java/com/ex/api/WidgetBenchmark.java",
        _bench("com.ex.api", "WidgetBenchmark"),
    )

    # naming_pkg: two Codec classes, no import; benchmark names Codec and shares net's prefix.
    _write(root / "net/src/main/java/com/ex/net/Codec.java", _src("com.ex.net", "Codec"))
    _write(root / "util/src/main/java/com/ex/util/Codec.java", _src("com.ex.util", "Codec"))
    _write(
        root / "net/src/jmh/java/com/ex/net/bench/CodecBenchmark.java",
        _bench("com.ex.net.bench", "CodecBenchmark", body="    Codec codec;\n"),
    )

    # no_reference: a unique global Token class exists, but the benchmark never names it.
    _write(root / "util/src/main/java/com/ex/util/Token.java", _src("com.ex.util", "Token"))
    _write(
        root / "jmh-benchmarks/src/main/java/com/ex/jmh/TokenBenchmark.java",
        _bench("com.ex.jmh", "TokenBenchmark"),
    )

    # no_source_class: benchmark target has no production class anywhere.
    _write(
        root / "api/src/jmh/java/com/ex/api/WriteBenchmark.java",
        _bench("com.ex.api", "WriteBenchmark"),
    )

    # not_jmh: a helper in the jmh tree without any @Benchmark method.
    _write(
        root / "api/src/jmh/java/com/ex/api/PerfHelper.java",
        "package com.ex.api;\n\npublic class PerfHelper {\n    int v;\n}\n",
    )

    return root


def test_strip_benchmark_suffix() -> None:
    suffixes = ("Benchmarks", "Benchmark", "Perf")
    assert strip_benchmark_suffix("LruCacheBenchmark", suffixes) == "LruCache"
    assert strip_benchmark_suffix("FooBenchmarks", suffixes) == "Foo"
    assert strip_benchmark_suffix("RangePerf", suffixes) == "Range"
    assert strip_benchmark_suffix("Benchmark", ("Benchmark",)) is None  # nothing left over
    assert strip_benchmark_suffix("PlainClass", ("Benchmark",)) is None


def test_is_jmh_benchmark_excludes_benchmark_mode_only() -> None:
    assert is_jmh_benchmark("@Benchmark\npublic void m() {}")
    assert is_jmh_benchmark("@Benchmark public void m() {}")
    # @BenchmarkMode alone (no method-level @Benchmark) must not count.
    assert not is_jmh_benchmark("@BenchmarkMode(Mode.Throughput)\nclass X {}")


def test_detect_benchmark_class_prefers_stem() -> None:
    text = "public class Helper {}\npublic class FooBenchmark {}\n"
    assert detect_benchmark_class(text, "FooBenchmark") == "FooBenchmark"
    assert detect_benchmark_class("public class OnlyOne {}", "Other") == "OnlyOne"


def test_build_index_keys(repo: Path) -> None:
    index = build_index(repo)
    assert "com.ex.cache.LruCache" in index.by_fqcn
    assert {c.package for c in index.by_simple["Status"]} == {"com.ex.api", "com.ex.net"}
    # benchmark sources (src/jmh) are not indexed as production classes.
    assert "LruCacheBenchmark" not in index.by_simple


def test_resolve_naming_unique(repo: Path) -> None:
    index = build_index(repo)
    text = (repo / "jmh-benchmarks/src/main/java/com/ex/jmh/LruCacheBenchmark.java").read_text()
    res = resolve_target(text, "LruCacheBenchmark", index)
    assert res.method == "naming"
    assert res.target is not None and res.target.fqcn == "com.ex.cache.LruCache"


def test_resolve_via_import(repo: Path) -> None:
    index = build_index(repo)
    text = (repo / "api/src/jmh/java/com/ex/api/StatusBenchmark.java").read_text()
    res = resolve_target(text, "StatusBenchmark", index)
    assert res.method == "import"
    assert res.target is not None and res.target.fqcn == "com.ex.api.Status"


def test_resolve_via_same_package(repo: Path) -> None:
    index = build_index(repo)
    text = (repo / "api/src/jmh/java/com/ex/api/WidgetBenchmark.java").read_text()
    res = resolve_target(text, "WidgetBenchmark", index)
    assert res.method == "same_package"
    assert res.target is not None and res.target.fqcn == "com.ex.api.Widget"
    assert res.candidate_count == 2


def test_resolve_via_package_proximity(repo: Path) -> None:
    index = build_index(repo)
    text = (repo / "net/src/jmh/java/com/ex/net/bench/CodecBenchmark.java").read_text()
    res = resolve_target(text, "CodecBenchmark", index)
    assert res.method == "naming_pkg"
    assert res.target is not None and res.target.fqcn == "com.ex.net.Codec"
    assert res.candidate_count == 2


def test_resolve_no_source_class(repo: Path) -> None:
    index = build_index(repo)
    text = (repo / "api/src/jmh/java/com/ex/api/WriteBenchmark.java").read_text()
    res = resolve_target(text, "WriteBenchmark", index)
    assert res.target is None
    assert res.reason == "no_source_class"


def test_resolve_skips_unreferenced_target(repo: Path) -> None:
    index = build_index(repo)
    text = (repo / "jmh-benchmarks/src/main/java/com/ex/jmh/TokenBenchmark.java").read_text()
    res = resolve_target(text, "TokenBenchmark", index)
    assert res.target is None
    assert res.reason == "no_reference"


def test_scrape_repo_pairs_and_report(repo: Path) -> None:
    spec = RepoSpec(name="demo", url="https://example/demo.git", project="demo", commit="abc123")
    result = scrape_repo(repo, spec)

    paired = {s.snippet.id: s for s in result.samples}
    assert set(paired) == {
        "com.ex.cache.LruCache",
        "com.ex.api.Status",
        "com.ex.api.Widget",
        "com.ex.net.Codec",
    }

    lru = paired["com.ex.cache.LruCache"]
    assert lru.benchmark_source.startswith("package com.ex.jmh;")
    assert lru.snippet.project == "demo"
    assert lru.metadata["repo_commit"] == "abc123"
    assert lru.metadata["source"] == "external"
    assert lru.metadata["benchmark_fqcn"] == "com.ex.jmh.LruCacheBenchmark"
    assert lru.metadata["resolved_via"] == "naming"

    report = result.report
    assert report["resolved"] == 4
    assert report["jmh_benchmark_files"] == 6  # Write + Token count but do not resolve
    assert report["skipped_reasons"].get("no_source_class") == 1
    assert report["skipped_reasons"].get("no_reference") == 1
    assert report["skipped_reasons"].get("not_jmh") == 1
    assert report["by_method"] == {
        "naming": 1,
        "import": 1,
        "same_package": 1,
        "naming_pkg": 1,
    }


def test_scrape_repo_records_roundtrip(repo: Path) -> None:
    from jmhgen.data.schema import BenchmarkSample

    spec = RepoSpec(name="demo", url="u", project="demo", commit="c")
    result = scrape_repo(repo, spec)
    for sample in result.samples:
        assert BenchmarkSample.from_dict(sample.to_dict()).to_dict() == sample.to_dict()
