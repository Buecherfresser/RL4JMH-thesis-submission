"""Unit tests for the repo -> snippet extractor (synthetic repo; no network or JVM needed)."""

from __future__ import annotations

from pathlib import Path

import pytest

from jmhgen.data.schema import CodeSnippet
from jmhgen.data.snippets_repo import (
    PrimaryType,
    ensure_checkout,
    extract_snippets,
    has_public_method,
    parse_primary_type,
)


def _write(path: Path, text: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text, encoding="utf-8")


def _klass(package: str, name: str, *, body: str = "    public int run() { return 1; }\n") -> str:
    return f"package {package};\n\npublic class {name} {{\n{body}}}\n"


@pytest.fixture
def repo(tmp_path: Path) -> Path:
    """A synthetic repo exercising the keep path and every skip reason."""
    root = tmp_path / "repo"
    main = "core/src/main/java"

    # keep: a public class with a public method.
    _write(root / f"{main}/com/ex/util/StringHelper.java", _klass("com.ex.util", "StringHelper"))
    # keep: an enum with a method.
    _write(
        root / f"{main}/com/ex/util/Color.java",
        "package com.ex.util;\n\npublic enum Color {\n  RED, GREEN;\n"
        "  public int code() { return ordinal(); }\n}\n",
    )

    # skip: interface, annotation, abstract class.
    _write(
        root / f"{main}/com/ex/util/Marker.java",
        "package com.ex.util;\n\npublic interface Marker {\n  void m();\n}\n",
    )
    _write(
        root / f"{main}/com/ex/util/MyAnno.java",
        "package com.ex.util;\n\npublic @interface MyAnno {\n  String value();\n}\n",
    )
    _write(
        root / f"{main}/com/ex/util/AbstractThing.java",
        "package com.ex.util;\n\npublic abstract class AbstractThing {\n"
        "  public abstract int f();\n  public int g() { return 2; }\n}\n",
    )

    # skip: public class with no public method (data holder), package-private class.
    _write(
        root / f"{main}/com/ex/util/DataOnly.java",
        "package com.ex.util;\n\npublic class DataOnly {\n  public int value;\n}\n",
    )
    _write(
        root / f"{main}/com/ex/util/Internal.java",
        "package com.ex.util;\n\nclass Internal {\n  public int m() { return 1; }\n}\n",
    )

    # skip silently: package-info / module-info are not counted as scanned files.
    _write(root / f"{main}/com/ex/util/package-info.java", "package com.ex.util;\n")

    # skip: a benchmark source set nested under src/main/java.
    _write(
        root / "jmh-benchmarks/src/main/java/com/ex/jmh/Bench.java",
        _klass("com.ex.jmh", "Bench"),
    )

    # keep, but outside the include prefix used in one test.
    _write(root / f"{main}/com/other/External.java", _klass("com.other", "External"))
    return root


def test_parse_primary_type() -> None:
    assert parse_primary_type("public class Foo {}") == PrimaryType("class", "Foo", False)
    assert parse_primary_type("public final class Foo {}") == PrimaryType("class", "Foo", False)
    assert parse_primary_type("public abstract class Foo {}") == PrimaryType("class", "Foo", True)
    assert parse_primary_type("public enum E { A }") == PrimaryType("enum", "E", False)
    assert parse_primary_type("public record R(int a) {}") == PrimaryType("record", "R", False)
    assert parse_primary_type("public interface I {}") == PrimaryType("interface", "I", False)
    assert parse_primary_type("public @interface A {}") == PrimaryType("@interface", "A", False)
    # No public top-level type.
    assert parse_primary_type("class PackagePrivate { public int m() {} }") is None


def test_has_public_method() -> None:
    assert has_public_method("public int run() { return 1; }")
    assert has_public_method("public static String of(int a, int b) { return null; }")
    assert has_public_method("public void f() throws IOException {}")
    # A field, an abstract signature, and a class header are not method bodies.
    assert not has_public_method("public static final int X = 42;")
    assert not has_public_method("public abstract int f();")
    assert not has_public_method("public class Foo {")


def test_extract_keeps_subjects_and_drops_non_subjects(repo: Path) -> None:
    result = extract_snippets(repo, "demo", url="u", commit="c0ffee", license="Apache-2.0")

    ids = {s.id for s in result.snippets}
    assert ids == {"com.ex.util.StringHelper", "com.ex.util.Color", "com.other.External"}

    reasons = result.report["skip_reasons"]
    assert reasons["interface"] == 1
    assert reasons["annotation"] == 1
    assert reasons["abstract"] == 1
    assert reasons["no_public_method"] == 1  # DataOnly
    assert reasons["no_public_type"] == 1  # Internal (package-private)
    assert reasons["benchmark_source_set"] == 1  # jmh-benchmarks/...
    # package-info.java is not counted as a scanned file.
    assert result.report["files_scanned"] == 9
    assert result.report["kept"] == 3


def test_extract_snippet_fields(repo: Path) -> None:
    result = extract_snippets(repo, "demo", url="u", commit="c0ffee", license="Apache-2.0")
    helper = next(s for s in result.snippets if s.id == "com.ex.util.StringHelper")

    assert helper.project == "demo"
    assert helper.language == "java"
    assert helper.path.endswith("com/ex/util/StringHelper.java")
    assert helper.metadata["package"] == "com.ex.util"
    assert helper.metadata["class_name"] == "StringHelper"
    assert helper.metadata["kind"] == "class"
    assert helper.metadata["repo_commit"] == "c0ffee"
    assert helper.metadata["license"] == "Apache-2.0"
    assert helper.source.startswith("package com.ex.util;")
    # Round-trips through the on-disk schema (it is loaded back as the RFT dataset).
    assert CodeSnippet.from_dict(helper.to_dict()).to_dict() == helper.to_dict()


def test_include_and_exclude_package_filters(repo: Path) -> None:
    included = extract_snippets(repo, "demo", include_packages=("com.ex",))
    assert {s.id for s in included.snippets} == {
        "com.ex.util.StringHelper",
        "com.ex.util.Color",
    }
    assert included.report["skip_reasons"]["package_not_included"] == 1  # com.other.External

    excluded = extract_snippets(repo, "demo", exclude_packages=("com.ex.util",))
    assert {s.id for s in excluded.snippets} == {"com.other.External"}
    assert excluded.report["skip_reasons"]["package_excluded"] == 2


def test_include_abstract_flag(repo: Path) -> None:
    result = extract_snippets(repo, "demo", include_abstract=True)
    assert "com.ex.util.AbstractThing" in {s.id for s in result.snippets}
    assert "abstract" not in result.report["skip_reasons"]


def test_max_chars_and_limit(tmp_path: Path) -> None:
    root = tmp_path / "r"
    base = "x/src/main/java/p"
    for name in ("A", "B", "C"):
        _write(root / f"{base}/{name}.java", _klass("p", name))

    capped = extract_snippets(root, "demo", max_chars=10)
    assert capped.snippets == []
    assert capped.report["skip_reasons"]["oversize"] == 3

    limited = extract_snippets(root, "demo", limit=2)
    assert [s.id for s in limited.snippets] == ["p.A", "p.B"]  # FQCN-sorted, first two


def test_dedup_keeps_one_per_fqcn(tmp_path: Path) -> None:
    root = tmp_path / "r"
    _write(root / "m1/src/main/java/p/Dup.java", _klass("p", "Dup"))
    _write(root / "m2/src/main/java/p/Dup.java", _klass("p", "Dup"))

    result = extract_snippets(root, "demo")
    assert [s.id for s in result.snippets] == ["p.Dup"]
    assert result.report["skip_reasons"]["duplicate_fqcn"] == 1


def test_ensure_checkout_requires_existing_when_no_clone(tmp_path: Path) -> None:
    with pytest.raises(FileNotFoundError):
        ensure_checkout("https://example/x.git", None, tmp_path / "missing", clone=False)
