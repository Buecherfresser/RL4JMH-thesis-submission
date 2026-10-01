"""``jmh-provision-mutants`` — build a project's *patched* SUT classpath for the mutation reward.

Reads ``data/projects/<project>/project.yaml`` (written alongside ``mutations.patch`` /
``mutants.yaml``), applies the mutation patch, builds the library, and writes a ``.cp`` file.

Build backends (``--build``):

* ``javac`` — plain javac (fast; zero-dep libraries)
* ``native`` — Maven/Gradle on a throwaway copy of ``data/external-src/<project>``
* ``auto`` — try javac first; on failure fall back to native when a build file exists

Example::

    jmh-provision-mutants commons-lang
    jmh-provision-mutants gson --build native
    jmh-provision-mutants guava --build auto
"""

from __future__ import annotations

from pathlib import Path
from typing import Annotated, Literal

import typer
import yaml

from jmhgen.data.classpath import bundle_classpath, load_classpath
from jmhgen.mutation.provision import (
    detect_native_build,
    provision_mutated_classpath,
    provision_mutated_classpath_native,
)


def _auto_dep_classpath(project: str, cp_dir: Path) -> list[str]:
    """Dependency jars for the javac backend, taken from ``<cp_dir>/<project>.cp``.

    ``jmh-provision-classpath`` already resolves every jar a project needs to compile, but
    ``project.yaml`` only carries a hand-written ``dependencies`` list (populated for 3 of ~100
    projects). Without this, javac runs with no ``-cp`` at all and every library with a
    third-party import fails with ``cannot find symbol`` — the single largest cause of corpus
    loss. Entries that no longer exist on disk are dropped: a native build can leave absolute
    paths into a deleted scratch checkout behind (commons-csv did).

    The plain ``.cp`` also holds the project's *own unpatched* jar. That is safe and wanted:
    :func:`provision_mutated_classpath` writes the patched jar **first**, so it shadows every
    class it defines, while classes the source tree never had (codegen output) stay resolvable.
    """
    cp_file = cp_dir / f"{project}.cp"
    if not cp_file.exists():
        return []
    entries = list(load_classpath(cp_file))
    live = [e for e in entries if Path(e).exists()]
    if len(live) != len(entries):
        typer.echo(
            f"warning: {cp_file} has {len(entries) - len(live)} unresolved entr(ies); "
            "using the rest",
            err=True,
        )
    return live


def run(
    project: Annotated[str, typer.Argument(help="Project id under data/projects/<project>/.")],
    projects_dir: Annotated[Path, typer.Option(help="Where project dirs live.")] = Path(
        "data/projects"
    ),
    source: Annotated[
        Path | None, typer.Option(help="Override the src/main/java root from project.yaml.")
    ] = None,
    checkout: Annotated[
        Path | None,
        typer.Option(help="Git checkout root for native Maven/Gradle builds."),
    ] = None,
    output: Annotated[
        Path | None, typer.Option(help="Output .cp (default data/classpaths/<project>-mutants.cp).")
    ] = None,
    dep_jar: Annotated[
        list[str] | None, typer.Option(help="Extra dependency jar on the classpath (repeatable).")
    ] = None,
    auto_deps: Annotated[
        bool,
        typer.Option(
            help="Fall back to data/classpaths/<project>.cp when project.yaml declares no "
            "dependencies (the javac backend has no other dependency source)."
        ),
    ] = True,
    java_release: Annotated[
        int | None, typer.Option(help="Override the --release used to compile the SUT.")
    ] = None,
    build: Annotated[
        Literal["auto", "javac", "native"],
        typer.Option(help="Build backend: javac, native (Maven/Gradle), or auto."),
    ] = "auto",
) -> None:
    """Build the patched SUT jar for PROJECT and write its classpath file."""
    project_dir = projects_dir / project
    spec_file = project_dir / "project.yaml"
    if not spec_file.exists():
        typer.echo(f"error: no project.yaml at {spec_file}", err=True)
        raise typer.Exit(code=1)
    data = yaml.safe_load(spec_file.read_text(encoding="utf-8")) or {}

    src_main = source or Path(data.get("source_dir", ""))
    if not src_main or not Path(src_main).exists():
        typer.echo(
            f"error: source_dir {src_main!r} not found; check project.yaml or pass --source",
            err=True,
        )
        raise typer.Exit(code=1)
    patch_path = project_dir / data.get("mutations_patch", "mutations.patch")
    deps = [str(d) for d in data.get("dependencies", [])] + list(dep_jar or [])
    if not deps and auto_deps:
        deps = _auto_dep_classpath(project, Path("data/classpaths"))
        if deps:
            typer.echo(f"using {len(deps)} dependency jar(s) from data/classpaths/{project}.cp")
    release = java_release if java_release is not None else int(data.get("java_release", 17))
    javac_args = [str(a) for a in data.get("javac_args", [])]
    out_cp = output or Path("data/classpaths") / f"{project}-mutants.cp"
    checkout_root = checkout or Path("data/external-src") / project

    def _javac() -> Path:
        return provision_mutated_classpath(
            src_main,
            patch_path,
            out_cp,
            java_release=release,
            dep_classpath=deps,
            javac_args=javac_args,
        )

    def _native() -> Path:
        if not checkout_root.exists():
            raise FileNotFoundError(f"checkout not found: {checkout_root}")
        return provision_mutated_classpath_native(
            checkout_root,
            src_main,
            patch_path,
            out_cp,
        )

    try:
        if build == "javac":
            written = _javac()
        elif build == "native":
            written = _native()
        else:
            # auto: javac first when it is likely to work; otherwise native.
            native = detect_native_build(checkout_root) if checkout_root.exists() else None
            try:
                written = _javac()
            except (FileNotFoundError, RuntimeError) as javac_exc:
                if native is None:
                    raise
                typer.echo(
                    f"javac backend failed ({javac_exc}); falling back to {native[0]}",
                    err=True,
                )
                written = _native()
    except (FileNotFoundError, RuntimeError) as exc:
        typer.echo(f"error: {exc}", err=True)
        raise typer.Exit(code=1) from exc

    # Bundle jars next to the .cp so rsync of data/classpaths is enough on remotes.
    try:
        bundle_classpath(written, Path("data/classpaths") / "lib" / f"{project}-mutants")
    except RuntimeError as exc:
        typer.echo(f"warning: could not bundle jars: {exc}", err=True)

    entries = load_classpath(written)
    typer.echo(f"wrote {len(entries)} classpath entr(ies) to {written}")
    for entry in entries[:12]:
        typer.echo(f"  {entry}")
    if len(entries) > 12:
        typer.echo(f"  ... +{len(entries) - 12} more")


def main() -> None:
    typer.run(run)


if __name__ == "__main__":
    main()
