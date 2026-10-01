"""``jmh-make-mutants`` — bake a fixed performance-mutation set into a subject library.

Ported from JMH-Bench ``tools/make_compress_mutants.py`` and generalised to any package root.
For a checked-out library it writes three artefacts under ``data/projects/<project>/``:

* ``mutations.patch`` — the single unified diff that adds ``MutationSwitch`` and inserts the
  dormant ``MutationSwitch.tick(<id>)`` guards (apply with ``git apply --directory=src/main/java``);
* ``mutants.yaml`` — the hidden ground-truth registry (never shown to the policy);
* ``snippets.jsonl`` — a prompt corpus of exactly the mutated subject classes, so the RFT
  ``dataset_path`` lines up with the classes that carry a mutation reward signal.

Examples::

    # Apache Commons Lang (already checked out under data/external-src/commons-lang).
    jmh-make-mutants commons-lang \
        --source data/external-src/commons-lang \
        --package-root org.apache.commons.lang3

    # fastutil (sources extracted under data/external-src/fastutil).
    jmh-make-mutants fastutil \
        --source data/external-src/fastutil \
        --package-root it.unimi.dsi.fastutil --count 100
"""

from __future__ import annotations

import json
from pathlib import Path
from typing import Annotated

import typer
import yaml

from jmhgen.data.schema import CodeSnippet
from jmhgen.mutation.generate import (
    MutantPlan,
    SiteSpec,
    generate_from_sites,
    generate_mutants,
    sites_yaml,
)


def _resolve_src_main(source: Path) -> Path:
    """Return the ``src/main/java`` root, accepting either it or the checkout root."""
    if source.name == "java" and source.parent.name == "main":
        return source
    candidate = source / "src" / "main" / "java"
    if candidate.exists():
        return candidate
    if source.exists():
        return source
    raise typer.BadParameter(f"no src/main/java under {source} (and {source} is not itself a root)")


def _snippets_for_sites(src_main: Path, rel_paths: list[str], project: str) -> list[CodeSnippet]:
    """Build one prompt snippet per unique mutated source file.

    The FQCN is derived from the file path (not by parsing the source), so it matches the
    registry's path-based ``fqcn`` verbatim — the mutation reward keys on that equality, and
    parsing the source is fooled by ``public class`` examples inside javadoc.
    """
    snippets: list[CodeSnippet] = []
    seen: set[str] = set()
    for rel in rel_paths:
        if rel in seen:
            continue
        seen.add(rel)
        path = src_main / rel
        try:
            text = path.read_text(encoding="utf-8")
        except OSError:
            continue
        fqcn = rel[:-5].replace("/", ".") if rel.endswith(".java") else rel.replace("/", ".")
        name = fqcn.rsplit(".", 1)[-1]
        package = fqcn.rsplit(".", 1)[0] if "." in fqcn else ""
        snippets.append(
            CodeSnippet(
                id=fqcn,
                source=text,
                language="java",
                project=project,
                path=rel,
                metadata={
                    "package": package,
                    "class_name": name,
                    "kind": "class",
                    "repo": project,
                    "mutated": True,
                },
            )
        )
    snippets.sort(key=lambda s: s.id)
    return snippets


def _load_site_specs(path: Path) -> tuple[str | None, list[SiteSpec]]:
    """Parse a curated ``mutation_sites.yaml`` into (package_root, specs)."""
    data = yaml.safe_load(path.read_text(encoding="utf-8")) or {}
    raw_sites = data.get("sites") or []
    specs: list[SiteSpec] = []
    for entry in raw_sites:
        fqcn = entry.get("class")
        method = entry.get("method")
        if not fqcn or not method:
            raise typer.BadParameter(f"each site needs 'class' and 'method': got {entry!r}")
        specs.append(
            SiteSpec(
                fqcn=str(fqcn),
                method=str(method),
                match=(str(entry["match"]) if entry.get("match") is not None else None),
                note=(str(entry["note"]) if entry.get("note") is not None else None),
            )
        )
    return data.get("package_root"), specs


def run(
    project: Annotated[str, typer.Argument(help="Project id (stamped on snippets; names output).")],
    source: Annotated[Path, typer.Option(help="Checkout root or its src/main/java directory.")],
    package_root: Annotated[
        str | None,
        typer.Option(help="Base package for the heuristic planter / to override the sites file."),
    ] = None,
    sites: Annotated[
        Path | None,
        typer.Option(help="Curated mutation_sites.yaml; when set, placement is explicit."),
    ] = None,
    count: Annotated[int, typer.Option(help="Number of mutants (heuristic mode only).")] = 100,
    allow_partial: Annotated[
        bool,
        typer.Option(
            "--allow-partial/--strict-count",
            help="Heuristic mode: accept fewer than --count when the library is small.",
        ),
    ] = False,
    include_subpackage: Annotated[
        list[str] | None, typer.Option(help="Keep only these subpackage buckets (repeatable).")
    ] = None,
    exclude_subpackage: Annotated[
        list[str] | None, typer.Option(help="Drop these subpackage buckets (repeatable).")
    ] = None,
    output_dir: Annotated[
        Path | None, typer.Option(help="Output dir (default data/projects/<project>).")
    ] = None,
    snippets: Annotated[
        bool, typer.Option("--snippets/--no-snippets", help="Also emit the mutated-class corpus.")
    ] = True,
) -> None:
    """Generate the mutation patch, registry, and mutated-class snippet corpus for PROJECT."""
    src_main = _resolve_src_main(source)
    plan: MutantPlan
    root: str
    try:
        if sites is not None:
            file_root, specs = _load_site_specs(sites)
            root = package_root or file_root or ""
            if not root:
                raise typer.BadParameter(
                    "no package_root (pass --package-root or set it in the --sites file)"
                )
            plan = generate_from_sites(src_main, root, specs)
        else:
            if not package_root:
                raise typer.BadParameter("--package-root is required for heuristic planting")
            root = package_root
            plan = generate_mutants(
                src_main,
                package_root,
                count=count,
                include_subpackages=tuple(include_subpackage or ()),
                exclude_subpackages=tuple(exclude_subpackage or ()),
                allow_partial=allow_partial,
            )
    except (FileNotFoundError, ValueError) as exc:
        typer.echo(f"error: {exc}", err=True)
        raise typer.Exit(code=1) from exc

    out_dir = output_dir or Path("data/projects") / project
    out_dir.mkdir(parents=True, exist_ok=True)
    (out_dir / "mutations.patch").write_text(plan.patch, encoding="utf-8")
    (out_dir / "mutants.yaml").write_text(plan.registry_yaml, encoding="utf-8")
    # Heuristic planting pins the placement as a re-plantable sites file; curated --sites
    # mode leaves the author-owned mutation_sites.yaml untouched.
    wrote_sites = False
    if sites is None:
        (out_dir / "mutation_sites.yaml").write_text(
            sites_yaml(plan.selected, root, project=project),
            encoding="utf-8",
        )
        wrote_sites = True

    n_snippets = 0
    if snippets:
        rel_paths = [site.rel_path for site in plan.selected]
        corpus = _snippets_for_sites(src_main, rel_paths, project)
        n_snippets = len(corpus)
        with (out_dir / "snippets.jsonl").open("w", encoding="utf-8") as handle:
            for snippet in corpus:
                handle.write(json.dumps(snippet.to_dict(), ensure_ascii=False))
                handle.write("\n")

    typer.echo(
        f"planted {len(plan.selected)} mutant(s) from {plan.total_candidates} candidate site(s)"
    )
    typer.echo("distribution by component:")
    for comp, n in plan.distribution.items():
        typer.echo(f"  {comp:28s} {n}")
    typer.echo(f"\nwrote {out_dir / 'mutations.patch'}")
    typer.echo(f"wrote {out_dir / 'mutants.yaml'}")
    if wrote_sites:
        typer.echo(f"wrote {out_dir / 'mutation_sites.yaml'}")
    if snippets:
        typer.echo(f"wrote {out_dir / 'snippets.jsonl'} ({n_snippets} mutated class(es))")


def main() -> None:
    typer.run(run)


if __name__ == "__main__":
    main()
