"""Materialise a per-task Maven JMH project on disk."""

from __future__ import annotations

import re
import shutil
import subprocess
from pathlib import Path

from jmhbench.harness import HarnessOutput, Task

_TEMPLATE_DIR = Path(__file__).resolve().parent.parent / "java-runner" / "template"


class ProjectError(RuntimeError):
    """Raised when a project operation (compile, patch, etc.) fails."""


def materialise_project(task: Task, workdir: Path) -> Path:
    """Create a fresh Maven project for *task* under *workdir*/'project'.

    Returns the project root. Idempotent: nukes any previous contents.
    """
    project_dir = workdir / "project"
    if project_dir.exists():
        shutil.rmtree(project_dir)
    project_dir.mkdir(parents=True)

    pom_template = (_TEMPLATE_DIR / "pom.xml").read_text()
    artifact = re.sub(r"[^a-zA-Z0-9_-]", "-", task.instance_id)
    (project_dir / "pom.xml").write_text(pom_template.replace("__ARTIFACT__", artifact))

    src_main = project_dir / "src" / "main" / "java"
    src_main.mkdir(parents=True)
    if task.sut_dir.exists():
        for f in task.sut_dir.rglob("*.java"):
            rel = f.relative_to(task.sut_dir)
            dst = src_main / rel
            dst.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(f, dst)

    if task.junit_test_path:
        junit_src = task.task_dir / task.junit_test_path
        if junit_src.exists():
            test_dir = project_dir / "src" / "test" / "java"
            test_dir.mkdir(parents=True, exist_ok=True)
            shutil.copy2(junit_src, test_dir / junit_src.name)

    return project_dir


_PACKAGE_RE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)
#: A type declaration at the start of a line. Anchoring matters: an unanchored
#: `class (\w+)` also matches prose inside a Javadoc block ("... covers class
#: introspection ..."), which would name the emitted file after the comment
#: instead of the benchmark and break `javac`'s public-class/file-name rule.
#: Javadoc continuation lines start with `*`, so requiring the modifiers or the
#: keyword itself at the line start is enough to skip them.
_CLASS_RE = re.compile(
    r"^[ \t]*(?:(?:public|final|abstract|sealed|non-sealed|strictfp)\s+)*"
    r"(?:class|enum|record)\s+(\w+)",
    re.MULTILINE,
)

# A whole leading `package ...;` line, including its trailing newline, so we can
# strip or splice around it without touching the rest of the source.
_PACKAGE_LINE_RE = re.compile(r"^[ \t]*package\s+[\w.]+\s*;[ \t]*\r?\n?", re.MULTILINE)

#: Canonical package every generated benchmark is rewritten into. Harnesses are
#: never judged on guessing JMH-Bench's directory layout, so the package name is
#: normalised here rather than demanded in the prompt.
CANONICAL_BENCH_PACKAGE = "bench.generated"
#: Package the subject-under-test always lives in (e.g. ``bench.Buffer``).
SUT_PACKAGE = "bench"


def normalize_benchmark_package(
    source: str,
    package: str = CANONICAL_BENCH_PACKAGE,
    sut_package: str = SUT_PACKAGE,
) -> str:
    """Force a generated benchmark into *package* so every harness is judged on
    JMH content, not on knowing JMH-Bench's layout.

    Any declared package (whatever it is) is rewritten to *package*; a missing
    package declaration is injected. Because the subject-under-test lives in
    *sut_package* — a different package — references that compiled under the
    harness's original package (e.g. same-package simple names) would otherwise
    break, so we also ensure the SUT is importable by simple name via
    ``import <sut_package>.*;``. Idempotent: a benchmark already in *package*
    with the SUT importable is returned unchanged.
    """
    existing = _PACKAGE_RE.search(source)
    current = existing.group(1) if existing else None

    if current != package:
        body = _PACKAGE_LINE_RE.sub("", source, count=1) if existing else source
        body = body.lstrip("\n")
        source = f"package {package};\n\n{body}"

    return _ensure_simple_name_import(source, sut_package)


def force_package(source: str, package: str) -> str:
    """Rewrite (or inject) the leading ``package`` declaration to *package*.

    Unlike :func:`normalize_benchmark_package` this does **not** splice an
    ``import <sut>.*;`` line: the project mutation track lives in a different
    world (the SUT is ``org.apache.commons.compress.*`` and the harness imports
    it by fully-qualified name), and it lets each per-class benchmark be placed
    in its own unique sub-package so identically-named generated classes never
    collide on disk. Idempotent.
    """
    existing = _PACKAGE_RE.search(source)
    if existing is not None and existing.group(1) == package:
        return source
    body = _PACKAGE_LINE_RE.sub("", source, count=1) if existing else source
    body = body.lstrip("\n")
    return f"package {package};\n\n{body}"


def _ensure_simple_name_import(source: str, pkg: str) -> str:
    """Splice ``import <pkg>.*;`` after the package line unless the source already
    imports a type from *pkg* (a wildcard or an explicit ``pkg.SomeClass``)."""
    already = re.search(
        rf"^\s*import\s+{re.escape(pkg)}\.(?:\*|[A-Z]\w*)\s*;",
        source,
        re.MULTILINE,
    )
    if already:
        return source
    return _PACKAGE_LINE_RE.sub(
        lambda m: f"{m.group(0)}import {pkg}.*;\n",
        source,
        count=1,
    )


def _emit_java_file(project_dir: Path, source: str, fallback_path: str | None = None) -> Path:
    """Write *source* under src/main/java/<pkg>/<Class>.java.

    Falls back to *fallback_path* (relative path) when the parser can't find
    a package + public class.
    """
    src_root = project_dir / "src" / "main" / "java"
    pkg_match = _PACKAGE_RE.search(source)
    cls_match = _CLASS_RE.search(source)
    if pkg_match and cls_match:
        pkg = pkg_match.group(1)
        cls = cls_match.group(1)
        dst = src_root / Path(*pkg.split(".")) / f"{cls}.java"
    elif fallback_path:
        dst = src_root / fallback_path
    else:
        raise ProjectError(
            "Cannot place generated Java source: no `package` + class declaration found "
            "and no fallback path supplied."
        )
    dst.parent.mkdir(parents=True, exist_ok=True)
    dst.write_text(source)
    return dst


def install_harness_output(project_dir: Path, output: HarnessOutput) -> list[Path]:
    """Drop the harness's generated sources into the Maven layout.

    The primary benchmark is expected to already be package-normalised by the
    runner (see :func:`normalize_benchmark_package`); files are placed on disk
    according to their declared package.
    """
    written: list[Path] = []
    written.append(_emit_java_file(project_dir, output.benchmark_source))
    for rel_path, src in output.extra_sources.items():
        try:
            written.append(_emit_java_file(project_dir, src, fallback_path=rel_path))
        except ProjectError:
            dst = project_dir / "src" / "main" / "java" / rel_path
            dst.parent.mkdir(parents=True, exist_ok=True)
            dst.write_text(src)
            written.append(dst)
    return written


def apply_patch(project_dir: Path, patch_path: Path) -> None:
    """Apply a unified diff to the SUT inside *project_dir*.

    Patches are written relative to the task's `src/main/java` root; we apply
    them with `git apply --unsafe-paths --directory=src/main/java`.
    """
    if not patch_path.exists():
        raise ProjectError(f"Patch file not found: {patch_path}")
    result = subprocess.run(
        [
            "git",
            "apply",
            "--unsafe-paths",
            "--whitespace=nowarn",
            "--directory=src/main/java",
            str(patch_path.resolve()),
        ],
        cwd=project_dir,
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        raise ProjectError(
            f"Failed to apply patch {patch_path.name}: {result.stderr.strip()}"
        )


def revert_patch(project_dir: Path, patch_path: Path) -> None:
    """Inverse of :func:`apply_patch`."""
    if not patch_path.exists():
        return
    subprocess.run(
        [
            "git",
            "apply",
            "--reverse",
            "--unsafe-paths",
            "--whitespace=nowarn",
            "--directory=src/main/java",
            str(patch_path.resolve()),
        ],
        cwd=project_dir,
        capture_output=True,
        text=True,
        check=False,
    )
