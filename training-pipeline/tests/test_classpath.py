"""Unit tests for SUT classpath provisioning helpers."""

from __future__ import annotations

import os
from pathlib import Path

import pytest

from jmhgen.cli.provision_mutants import _auto_dep_classpath
from jmhgen.data.classpath import (
    bundle_classpath,
    collect_jars,
    load_classpath,
    provision_classpath,
    write_classpath_file,
)


def _touch(path: Path) -> Path:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(b"")
    return path


def test_collect_jars_matches_globs_and_dedupes(tmp_path: Path) -> None:
    _touch(tmp_path / "build" / "libs" / "rxjava.jar")
    _touch(tmp_path / "build" / "libs" / "notes.txt")
    _touch(tmp_path / "deps" / "reactive-streams-1.0.jar")
    jars = collect_jars([tmp_path], ["build/libs/*.jar", "deps/*.jar", "build/libs/*.jar"])
    names = sorted(p.name for p in jars)
    assert names == ["reactive-streams-1.0.jar", "rxjava.jar"]
    assert len(jars) == len(set(jars))  # no duplicates despite the repeated glob


def test_write_and_load_roundtrip(tmp_path: Path) -> None:
    jar_a = _touch(tmp_path / "a.jar")
    jar_b = _touch(tmp_path / "b.jar")
    cp_file = write_classpath_file(tmp_path / "rxjava.cp", [jar_a, jar_b])
    assert cp_file.read_text(encoding="utf-8").splitlines() == ["a.jar", "b.jar"]
    entries = load_classpath(cp_file)
    assert entries == (str(jar_a.resolve()), str(jar_b.resolve()))


def test_load_classpath_relocates_missing_absolute_to_sibling_jar(tmp_path: Path) -> None:
    jar = _touch(tmp_path / "commons-lang-mutants.jar")
    cp_file = tmp_path / "commons-lang-mutants.cp"
    cp_file.write_text(
        "/nonexistent/host/data/classpaths/commons-lang-mutants.jar\n",
        encoding="utf-8",
    )
    assert load_classpath(cp_file) == (str(jar.resolve()),)


def test_load_classpath_supports_pathsep(tmp_path: Path) -> None:
    cp_file = tmp_path / "x.cp"
    cp_file.write_text(os.pathsep.join(["/x/a.jar", "/x/b.jar"]) + "\n", encoding="utf-8")
    assert load_classpath(cp_file) == ("/x/a.jar", "/x/b.jar")


def test_provision_gathers_jars_without_build(tmp_path: Path) -> None:
    source = tmp_path / "proj"
    _touch(source / "build" / "libs" / "rxjava.jar")
    extra = _touch(tmp_path / "reactive-streams.jar")
    out = provision_classpath(
        "rxjava",
        output=tmp_path / "out" / "rxjava.cp",
        source=source,
        extra_jars=[extra],
    )
    entries = load_classpath(out)
    assert any(e.endswith("rxjava.jar") for e in entries)
    assert any(e.endswith("reactive-streams.jar") for e in entries)


def test_provision_raises_when_no_jars(tmp_path: Path) -> None:
    with pytest.raises(RuntimeError, match="no jars found"):
        provision_classpath("empty", output=tmp_path / "e.cp", source=tmp_path / "empty")


def test_bundle_classpath_copies_runtime_jars_and_rewrites_relative(tmp_path: Path) -> None:
    # A .cp pointing at absolute jars under a "checkout", mixing runtime + source/test jars.
    checkout = tmp_path / "external-src" / "proj" / "target"
    main = _touch(checkout / "proj-1.0.jar")
    dep = _touch(checkout / "dependency" / "dep-2.0.jar")
    _touch(checkout / "proj-1.0-sources.jar")
    _touch(checkout / "proj-1.0-tests.jar")
    cp_dir = tmp_path / "classpaths"
    cp_file = write_classpath_file(cp_dir / "proj.cp", [main, dep, checkout / "proj-1.0-sources.jar"])

    bundle_classpath(cp_file, cp_dir / "lib" / "proj")

    # Rewritten entries are relative and resolve to the bundled copies; sources/tests dropped.
    lines = cp_file.read_text(encoding="utf-8").splitlines()
    assert sorted(lines) == ["lib/proj/dep-2.0.jar", "lib/proj/proj-1.0.jar"]
    entries = load_classpath(cp_file)
    assert all(os.path.isfile(e) for e in entries)
    assert (cp_dir / "lib" / "proj" / "proj-1.0.jar").is_file()


def test_bundle_classpath_is_idempotent(tmp_path: Path) -> None:
    checkout = tmp_path / "src"
    main = _touch(checkout / "proj-1.0.jar")
    cp_dir = tmp_path / "classpaths"
    cp_file = write_classpath_file(cp_dir / "proj.cp", [main])
    lib = cp_dir / "lib" / "proj"

    bundle_classpath(cp_file, lib)
    first = cp_file.read_text(encoding="utf-8")
    bundle_classpath(cp_file, lib)  # re-run over already-bundled relative paths
    assert cp_file.read_text(encoding="utf-8") == first
    assert load_classpath(cp_file) == (str((lib / "proj-1.0.jar").resolve()),)


def test_auto_dep_classpath_reads_plain_cp(tmp_path: Path) -> None:
    """The javac mutant backend falls back to the project's provisioned <project>.cp."""
    jar_a = _touch(tmp_path / "lib" / "joda-time" / "joda-time-2.14.2.jar")
    jar_b = _touch(tmp_path / "lib" / "joda-time" / "joda-convert-1.9.2.jar")
    write_classpath_file(tmp_path / "joda-time.cp", [jar_a, jar_b])

    deps = _auto_dep_classpath("joda-time", tmp_path)

    assert sorted(Path(d).name for d in deps) == [
        "joda-convert-1.9.2.jar",
        "joda-time-2.14.2.jar",
    ]


def test_auto_dep_classpath_drops_vanished_entries(tmp_path: Path) -> None:
    """A native build can leak absolute paths into a deleted scratch checkout (commons-csv)."""
    live = _touch(tmp_path / "lib" / "commons-csv" / "commons-codec-1.22.0.jar")
    (tmp_path / "commons-csv.cp").write_text(
        f"{live}\n/tmp/jmhgen-native-mutants-gone/checkout/target/commons-csv-1.15.0.jar\n",
        encoding="utf-8",
    )

    deps = _auto_dep_classpath("commons-csv", tmp_path)

    assert [Path(d).name for d in deps] == ["commons-codec-1.22.0.jar"]


def test_auto_dep_classpath_absent_cp_is_empty(tmp_path: Path) -> None:
    assert _auto_dep_classpath("never-provisioned", tmp_path) == []
