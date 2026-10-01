"""``jmh-extract-snippets`` — turn a Java repo into a prompt-only RFT/eval snippet corpus.

Pipeline:

1. ensure the repo is checked out (shallow-clone at ``--ref`` if missing) and record its HEAD SHA
   for provenance;
2. walk its ``src/main/java`` sources and keep every benchmarkable public class as a
   :class:`~jmhgen.data.schema.CodeSnippet` (:func:`jmhgen.data.snippets_repo.extract_snippets`);
3. write ``<output>`` (a JSONL of snippets, loadable as the RFT ``dataset_path``) and a sibling
   ``<output stem>.report.json``.

Examples::

    # Apache Commons Lang (Maven, zero runtime deps): pure static utilities, ideal subjects.
    jmh-extract-snippets commons-lang \
        --url https://github.com/apache/commons-lang.git \
        --ref rel/commons-lang-3.18.0

    # RoaringBitmap (Gradle): performance-by-design data structures.
    jmh-extract-snippets roaringbitmap \
        --url https://github.com/RoaringBitmap/RoaringBitmap.git --ref 1.6.13 \
        --include-package org.roaringbitmap

Then provision its SUT classpath once (``jmh-provision-classpath``) and point an RFT config's
``dataset_path`` / ``project_classpaths`` at the outputs.
"""

from __future__ import annotations

import json
from pathlib import Path
from typing import Annotated, Any

import typer

from jmhgen.data.snippets_repo import (
    DEFAULT_SOURCE_GLOBS,
    ensure_checkout,
    extract_snippets,
)


def _write_jsonl(path: Path, rows: list[dict[str, Any]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8") as handle:
        for row in rows:
            handle.write(json.dumps(row, ensure_ascii=False))
            handle.write("\n")


def run(
    project: Annotated[
        str, typer.Argument(help="Project id (stamped on snippets; matches project_classpaths).")
    ],
    source: Annotated[
        Path | None, typer.Option(help="Existing checkout root. Skips cloning when given.")
    ] = None,
    url: Annotated[
        str | None, typer.Option(help="Git URL to shallow-clone when --source is absent.")
    ] = None,
    ref: Annotated[
        str | None, typer.Option(help="Tag/branch to pin the clone to (recorded for provenance).")
    ] = None,
    checkout_root: Annotated[
        Path, typer.Option(help="Where to clone (clone lands in <root>/<project>).")
    ] = Path("data/external-src"),
    no_clone: Annotated[
        bool, typer.Option("--no-clone", help="Require an existing checkout; never clone.")
    ] = False,
    output: Annotated[
        Path | None, typer.Option(help="Output JSONL (default data/snippets/<project>.jsonl).")
    ] = None,
    source_glob: Annotated[
        list[str] | None,
        typer.Option(help=f"Source glob (repeatable). Default: {list(DEFAULT_SOURCE_GLOBS)}."),
    ] = None,
    include_package: Annotated[
        list[str] | None, typer.Option(help="Keep only these package prefixes (repeatable).")
    ] = None,
    exclude_package: Annotated[
        list[str] | None, typer.Option(help="Drop these package prefixes (repeatable).")
    ] = None,
    max_chars: Annotated[
        int, typer.Option(help="Drop classes whose source exceeds this many chars (0 = no cap).")
    ] = 0,
    include_abstract: Annotated[
        bool, typer.Option("--include-abstract", help="Keep abstract classes (off by default).")
    ] = False,
    limit: Annotated[
        int, typer.Option(help="Keep only the first N snippets, FQCN-sorted (0 = all).")
    ] = 0,
    license: Annotated[
        str | None, typer.Option(help="SPDX license id, recorded in each snippet's metadata.")
    ] = None,
) -> None:
    """Extract benchmarkable subject classes from PROJECT into a snippet JSONL + report."""
    checkout: Path
    sha: str | None
    if source is not None:
        try:
            checkout, sha = ensure_checkout(url or "", ref, source, clone=False)
        except (FileNotFoundError, RuntimeError):
            checkout, sha = source, None  # not a git checkout; provenance SHA stays unknown
    elif url is not None:
        try:
            checkout, sha = ensure_checkout(url, ref, checkout_root / project, clone=not no_clone)
        except (FileNotFoundError, RuntimeError) as exc:
            typer.echo(f"error: {exc}", err=True)
            raise typer.Exit(code=1) from exc
    else:
        typer.echo("error: pass --source <checkout> or --url <git-url>", err=True)
        raise typer.Exit(code=2)

    result = extract_snippets(
        checkout,
        project,
        url=url,
        commit=sha,
        license=license,
        source_globs=tuple(source_glob) if source_glob else DEFAULT_SOURCE_GLOBS,
        include_packages=tuple(include_package or ()),
        exclude_packages=tuple(exclude_package or ()),
        max_chars=max_chars,
        include_abstract=include_abstract,
        limit=limit,
    )

    out_path = output or Path("data/snippets") / f"{project}.jsonl"
    _write_jsonl(out_path, [s.to_dict() for s in result.snippets])
    report_path = out_path.with_suffix(".report.json")
    report_path.write_text(json.dumps(result.report, indent=2) + "\n", encoding="utf-8")

    typer.echo(json.dumps(result.report, indent=2))
    typer.echo(f"\nwrote {len(result.snippets)} snippet(s) to {out_path}")
    if not result.snippets:
        typer.echo("error: no benchmarkable classes found; check globs / filters.", err=True)
        raise typer.Exit(code=1)


def main() -> None:
    typer.run(run)


if __name__ == "__main__":
    main()
