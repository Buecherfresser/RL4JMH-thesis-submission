"""Provision and load a subject-under-test (SUT) classpath for the reward backend.

RFT trains on real Java classes, so a generated benchmark for ``io.reactivex.rxjava3.core.Maybe``
only compiles if RxJava (and its runtime deps) are on the classpath. The Maven runner already
accepts arbitrary jars/class-dirs via :attr:`~jmhgen.runner.types.BenchmarkSpec.extra_classpath`;
this module's job is to *produce* that list once per project and persist it as a ``.cp`` file
that the RFT loop loads per snippet (keyed by :attr:`CodeSnippet.project`).

The build itself is project-specific (RxJava is Gradle, others Maven), so the provisioner is
deliberately generic: optionally run a build command, then gather the resulting jars (the
project artifact plus any dependency jars from given roots/globs) into one classpath file.
"""

from __future__ import annotations

import os
import shutil
from collections.abc import Iterable
from pathlib import Path

from jmhgen.utils.logging import get_logger
from jmhgen.utils.subprocess import run_command

logger = get_logger("jmhgen.data.classpath")

# Default places a Gradle/Maven build drops its artifacts and resolved dependency jars.
DEFAULT_JAR_GLOBS: tuple[str, ...] = (
    "build/libs/*.jar",
    "**/build/libs/*.jar",
    "target/*.jar",
    "**/target/*.jar",
)


def collect_jars(roots: Iterable[str | Path], globs: Iterable[str]) -> list[Path]:
    """Return existing ``.jar`` files under ``roots`` matching any ``globs`` (deduped, ordered)."""
    seen: set[Path] = set()
    jars: list[Path] = []
    glob_list = list(globs)
    for root in roots:
        root_path = Path(root)
        if not root_path.exists():
            logger.warning("classpath root does not exist: %s", root_path)
            continue
        for pattern in glob_list:
            for match in sorted(root_path.glob(pattern)):
                resolved = match.resolve()
                if resolved.suffix == ".jar" and resolved.is_file() and resolved not in seen:
                    seen.add(resolved)
                    jars.append(resolved)
    return jars


def write_classpath_file(path: str | Path, jars: Iterable[str | Path]) -> Path:
    """Write one jar path per line to ``path`` (creating parent dirs).

    Paths are stored relative to the ``.cp`` file's directory when possible so the same file
    works after rsync to a remote host with a different filesystem root.
    """
    out = Path(path)
    out.parent.mkdir(parents=True, exist_ok=True)
    cp_dir = out.parent.resolve()
    lines: list[str] = []
    for jar in jars:
        resolved = Path(jar).resolve()
        try:
            lines.append(str(resolved.relative_to(cp_dir)))
        except ValueError:
            lines.append(str(resolved))
    out.write_text("\n".join(lines) + ("\n" if lines else ""), encoding="utf-8")
    return out


def resolve_classpath_entry(entry: str, cp_dir: Path) -> Path:
    """Resolve one classpath entry from a ``.cp`` file against ``cp_dir``."""
    candidate = Path(entry)
    if candidate.is_absolute():
        if candidate.is_file():
            return candidate.resolve()
        # Relocated deployment: the jar often sits beside the ``.cp`` file under a new root.
        sibling = cp_dir / candidate.name
        if sibling.is_file():
            return sibling.resolve()
        return candidate.resolve()
    return (cp_dir / candidate).resolve()


def load_classpath(path: str | Path) -> tuple[str, ...]:
    """Load classpath entries from a ``.cp`` file (newline- or ``os.pathsep``-separated)."""
    cp_file = Path(path).resolve()
    cp_dir = cp_file.parent
    text = cp_file.read_text(encoding="utf-8")
    entries: list[str] = []
    for line in text.replace(os.pathsep, "\n").splitlines():
        entry = line.strip()
        if entry:
            entries.append(str(resolve_classpath_entry(entry, cp_dir)))
    return tuple(entries)


# Build artifacts that should never end up on a benchmark compile/run classpath: source jars
# (no .class files) and test jars (test-only classes, occasionally with conflicting deps).
_NON_RUNTIME_JAR_SUFFIXES: tuple[str, ...] = (
    "-sources.jar",
    "-tests.jar",
    "-test-sources.jar",
    "-javadoc.jar",
)


def bundle_classpath(
    cp_file: str | Path,
    lib_dir: str | Path,
    *,
    exclude_suffixes: Iterable[str] = _NON_RUNTIME_JAR_SUFFIXES,
) -> Path:
    """Copy a ``.cp``'s jars beside it and rewrite the file with **relative** paths.

    ``jmh-provision-classpath`` records absolute jar paths under a huge project checkout
    (``data/external-src``), which neither survive an rsync to a pod with a different filesystem
    root nor are worth shipping. Bundling copies just the runtime jars into ``lib_dir`` (kept
    under the ``.cp`` file's directory, e.g. ``data/classpaths/lib/<project>``) and rewrites the
    ``.cp`` to point at them relatively, so syncing ``data/classpaths`` alone is enough for the
    reward runner on any host. Source/test/javadoc jars are dropped. Idempotent.
    """
    entries = load_classpath(cp_file)
    lib = Path(lib_dir)
    lib.mkdir(parents=True, exist_ok=True)
    suffixes = tuple(exclude_suffixes)

    kept: list[Path] = []
    for entry in entries:
        src = Path(entry)
        if any(src.name.endswith(s) for s in suffixes) or not src.is_file():
            continue
        dst = lib / src.name
        if src.resolve() != dst.resolve():
            shutil.copy2(src, dst)
        kept.append(dst)

    if not kept:
        raise RuntimeError(f"no runtime jars to bundle from {cp_file} (all filtered out?)")
    return write_classpath_file(cp_file, kept)


def provision_classpath(
    project: str,
    *,
    output: str | Path,
    source: str | Path | None = None,
    build_cmd: list[str] | None = None,
    jar_roots: Iterable[str | Path] | None = None,
    jar_globs: Iterable[str] | None = None,
    extra_jars: Iterable[str | Path] | None = None,
    build_timeout_s: float = 1800.0,
) -> Path:
    """Build (optionally) and gather a project's classpath into ``output``.

    Returns the path to the written ``.cp`` file. Raises if no jars were found, since an empty
    classpath would silently make every RFT compile fail with a misleading error.
    """
    if build_cmd:
        if source is None:
            raise ValueError("source must be set when build_cmd is provided")
        logger.info("building %s: %s (cwd=%s)", project, " ".join(build_cmd), source)
        proc = run_command(build_cmd, cwd=source, timeout_s=build_timeout_s)
        if not proc.ok:
            raise RuntimeError(
                f"build command for {project} failed (rc={proc.returncode}, "
                f"timed_out={proc.timed_out}):\n{proc.stderr[-2000:]}"
            )

    roots: list[str | Path] = list(jar_roots or [])
    if source is not None:
        roots.insert(0, source)
    jars = collect_jars(roots, jar_globs or DEFAULT_JAR_GLOBS)
    for jar in extra_jars or ():
        resolved = Path(jar).resolve()
        if resolved not in jars:
            jars.append(resolved)

    if not jars:
        raise RuntimeError(
            f"no jars found for {project}; check --source / --jar-root / --jar-glob "
            f"(searched roots={roots})"
        )

    out = write_classpath_file(output, jars)
    logger.info("wrote %d classpath entries for %s to %s", len(jars), project, out)
    return out
