"""``jmh-curate-snippets`` — filter an extracted snippet corpus to a curated class list.

Takes the inclusive ``data/snippets/<project>.jsonl`` from ``jmh-extract-snippets`` and a
hand-picked ``data/projects/<project>/good_classes.yaml``, and writes the benchmarkable subset
(default ``data/snippets/<project>.good.jsonl``) used as the GRPO prompt corpus. Curated FQCNs
that match no snippet are reported so a stale list is caught early.

Example::

    jmh-curate-snippets \
        --snippets data/snippets/commons-codec.jsonl \
        --good-classes data/projects/commons-codec/good_classes.yaml
"""

from __future__ import annotations

import json
from pathlib import Path
from typing import Annotated

import typer

from jmhgen.data.curate import curate_file


def run(
    snippets: Annotated[
        Path, typer.Option(help="Extracted snippets JSONL (from jmh-extract-snippets).")
    ],
    good_classes: Annotated[
        Path, typer.Option(help="Curated good_classes.yaml with a 'classes:' FQCN list.")
    ],
    output: Annotated[
        Path | None,
        typer.Option(help="Output JSONL (default: <snippets stem>.good.jsonl beside input)."),
    ] = None,
) -> None:
    """Filter SNIPPETS to the classes in GOOD_CLASSES and write the curated corpus."""
    out_path = output or snippets.with_suffix("").with_suffix(".good.jsonl")
    result = curate_file(snippets, good_classes, out_path)
    typer.echo(json.dumps(result.report, indent=2))
    typer.echo(f"\nwrote {len(result.kept)} curated snippet(s) to {out_path}")
    if result.missing:
        typer.echo(
            f"warning: {len(result.missing)} curated class(es) had no snippet "
            f"(typo or dropped by extraction): {', '.join(result.missing[:10])}"
            f"{' ...' if len(result.missing) > 10 else ''}",
            err=True,
        )
    if not result.kept:
        typer.echo("error: no curated snippet matched; check the FQCNs / extraction.", err=True)
        raise typer.Exit(code=1)


def main() -> None:
    typer.run(run)


if __name__ == "__main__":
    main()
