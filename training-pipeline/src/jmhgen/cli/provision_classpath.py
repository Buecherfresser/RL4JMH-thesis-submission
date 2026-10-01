"""``jmh-provision-classpath`` — build/gather a project's SUT classpath for RFT rewards.

Run once per project. Examples:

    # RxJava is Gradle: build the jar, then gather it plus the reactive-streams dep.
    jmh-provision-classpath rxjava \
        --source data/rxjava-src \
        --build-cmd "./gradlew :rxjava:jar" \
        --jar-root ~/.gradle/caches \
        --jar-glob "**/reactive-streams-*.jar"

    # Or, if the project is already built, just gather jars (no --build-cmd).
    jmh-provision-classpath rxjava --source data/rxjava-src

The resulting ``data/classpaths/<project>.cp`` is referenced from the RFT config's
``project_classpaths`` map and loaded per snippet by its ``project`` field.
"""

from __future__ import annotations

import shlex
from pathlib import Path
from typing import Annotated

import typer

from jmhgen.data.classpath import DEFAULT_JAR_GLOBS, load_classpath, provision_classpath


def run(
    project: Annotated[str, typer.Argument(help="Project id (matches CodeSnippet.project).")],
    source: Annotated[
        Path | None, typer.Option(help="Project checkout root (build cwd + default jar root).")
    ] = None,
    build_cmd: Annotated[
        str | None, typer.Option(help="Shell-quoted build command run in --source (optional).")
    ] = None,
    jar_root: Annotated[
        list[str] | None, typer.Option(help="Extra root to search for jars (repeatable).")
    ] = None,
    jar_glob: Annotated[
        list[str] | None,
        typer.Option(help=f"Jar glob (repeatable). Default: {list(DEFAULT_JAR_GLOBS)}."),
    ] = None,
    extra_jar: Annotated[
        list[str] | None, typer.Option(help="Explicit jar to include (repeatable).")
    ] = None,
    output: Annotated[
        Path | None, typer.Option(help="Output .cp file (default data/classpaths/<project>.cp).")
    ] = None,
) -> None:
    """Provision the classpath for PROJECT and write it to a ``.cp`` file."""
    out_path = output or Path("data/classpaths") / f"{project}.cp"
    cmd = shlex.split(build_cmd) if build_cmd else None
    written = provision_classpath(
        project,
        output=out_path,
        source=source,
        build_cmd=cmd,
        jar_roots=jar_root or [],
        jar_globs=jar_glob or DEFAULT_JAR_GLOBS,
        extra_jars=extra_jar or [],
    )
    entries = load_classpath(written)
    typer.echo(f"wrote {len(entries)} classpath entries to {written}")
    for entry in entries:
        typer.echo(f"  {entry}")


def main() -> None:
    typer.run(run)


if __name__ == "__main__":
    main()
