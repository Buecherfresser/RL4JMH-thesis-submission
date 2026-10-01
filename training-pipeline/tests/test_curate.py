"""Unit tests for the curated-class snippet filter."""

from __future__ import annotations

import json

import pytest

from jmhgen.data.curate import curate_file, filter_snippets, load_good_classes


def _snip(fqcn: str) -> dict:
    return {"id": fqcn, "source": f"class {fqcn.rsplit('.', 1)[-1]} {{}}", "project": "demo"}


class TestFilterSnippets:
    def test_keeps_only_curated_in_curated_order(self) -> None:
        rows = [_snip("a.B"), _snip("a.C"), _snip("a.D")]
        result = filter_snippets(rows, ["a.D", "a.B"])
        assert [r["id"] for r in result.kept] == ["a.D", "a.B"]  # curated order, not input order
        assert result.matched == ["a.D", "a.B"]
        assert result.missing == []

    def test_reports_missing_curated_classes(self) -> None:
        rows = [_snip("a.B")]
        result = filter_snippets(rows, ["a.B", "a.Nope"])
        assert [r["id"] for r in result.kept] == ["a.B"]
        assert result.missing == ["a.Nope"]

    def test_accepts_benchmark_sample_rows(self) -> None:
        # Rows may be wrapped as {"snippet": {...}} (BenchmarkSample encoding).
        rows = [{"snippet": _snip("a.B")}, {"snippet": _snip("a.C")}]
        result = filter_snippets(rows, ["a.C"])
        assert len(result.kept) == 1
        assert result.kept[0]["snippet"]["id"] == "a.C"


class TestLoadGoodClasses:
    def test_dedups_preserving_order(self, tmp_path) -> None:
        path = tmp_path / "good.yaml"
        path.write_text("classes:\n  - a.B\n  - a.C\n  - a.B\n", encoding="utf-8")
        assert load_good_classes(path) == ["a.B", "a.C"]

    def test_rejects_empty(self, tmp_path) -> None:
        path = tmp_path / "good.yaml"
        path.write_text("classes: []\n", encoding="utf-8")
        with pytest.raises(ValueError):
            load_good_classes(path)


class TestCurateFile:
    def test_writes_filtered_jsonl(self, tmp_path) -> None:
        snippets = tmp_path / "snips.jsonl"
        snippets.write_text(
            "\n".join(json.dumps(_snip(f)) for f in ["a.B", "a.C", "a.D"]) + "\n",
            encoding="utf-8",
        )
        good = tmp_path / "good.yaml"
        good.write_text("classes:\n  - a.C\n  - a.B\n", encoding="utf-8")
        out = tmp_path / "out.good.jsonl"

        result = curate_file(snippets, good, out)

        assert result.report["kept_snippets"] == 2
        written = [json.loads(line) for line in out.read_text().splitlines() if line.strip()]
        assert [r["id"] for r in written] == ["a.C", "a.B"]
