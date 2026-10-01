"""``jmh-scrape-external`` — mine (class -> human JMH benchmark) pairs from public repos.

Pipeline (mirrors ``jmh-build-sft`` so the outputs are interchangeable):

1. ensure each configured repo is checked out (shallow-clone if missing) and record its HEAD
   SHA for provenance;
2. index the repo's production classes and pair every ``@Benchmark`` source to the class it
   exercises (:func:`jmhgen.data.scrape_external.scrape_repo`);
3. write the prompt-agnostic ``records.jsonl`` and a ``report.json``;
4. render TRL conversational messages with the chosen instruction template and write a
   deterministic train/val split.

Pairing is conservative: benchmarks whose target class is ambiguous or absent are skipped
(with a reason captured in the report) rather than mispaired.
"""

from __future__ import annotations

import json
import random
from pathlib import Path
from typing import Annotated, Any

import typer

from jmhgen.config.schema import ExternalRepo, ExternalScrapeConfig
from jmhgen.data.prompts import TEMPLATES, render_messages
from jmhgen.data.schema import BenchmarkSample
from jmhgen.data.scrape_external import RepoSpec, scrape_repo
from jmhgen.utils.logging import get_logger
from jmhgen.utils.subprocess import run_command

logger = get_logger(__name__)


def _write_jsonl(path: Path, rows: list[dict[str, Any]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8") as handle:
        for row in rows:
            handle.write(json.dumps(row, ensure_ascii=False))
            handle.write("\n")


def _apply_size_cap(
    samples: list[BenchmarkSample], max_chars: int
) -> tuple[list[BenchmarkSample], list[str]]:
    if max_chars <= 0:
        return samples, []
    kept: list[BenchmarkSample] = []
    dropped: list[str] = []
    for sample in samples:
        if len(sample.snippet.source) > max_chars:
            dropped.append(sample.snippet.id)
        else:
            kept.append(sample)
    return kept, dropped


def _split(
    samples: list[BenchmarkSample], val_fraction: float, seed: int
) -> tuple[list[BenchmarkSample], list[BenchmarkSample]]:
    if val_fraction <= 0 or len(samples) < 2:
        return samples, []
    order = list(range(len(samples)))
    random.Random(seed).shuffle(order)
    n_val = max(1, int(round(len(samples) * val_fraction)))
    val_idx = set(order[:n_val])
    train = [s for i, s in enumerate(samples) if i not in val_idx]
    val = [s for i, s in enumerate(samples) if i in val_idx]
    return train, val


def _length_stats(values: list[int]) -> dict[str, Any]:
    if not values:
        return {"count": 0}
    ordered = sorted(values)

    def pct(p: float) -> int:
        idx = min(len(ordered) - 1, int(round(p / 100.0 * (len(ordered) - 1))))
        return ordered[idx]

    return {
        "count": len(ordered),
        "min": ordered[0],
        "p50": ordered[len(ordered) // 2],
        "p90": pct(90),
        "p99": pct(99),
        "max": ordered[-1],
    }


def _git(args: list[str], cwd: Path | None = None, timeout_s: float = 1800.0) -> str:
    result = run_command(["git", *args], cwd=cwd, timeout_s=timeout_s)
    if not result.ok:
        raise RuntimeError(
            f"git {' '.join(args)} failed (rc={result.returncode}): {result.stderr.strip()}"
        )
    return result.stdout.strip()


def _ensure_checkout(repo: ExternalRepo, checkout_root: Path, clone: bool) -> tuple[Path, str]:
    """Return ``(repo_dir, head_sha)``, shallow-cloning the repo if it is not already present."""
    dest = checkout_root / repo.name
    if not (dest / ".git").exists():
        if not clone:
            raise FileNotFoundError(
                f"{dest} is not a checkout and clone is disabled; clone {repo.url} manually."
            )
        checkout_root.mkdir(parents=True, exist_ok=True)
        clone_args = ["clone", "--depth", "1"]
        if repo.ref:
            clone_args += ["--branch", repo.ref]
        clone_args += [repo.url, str(dest)]
        logger.info("cloning %s -> %s", repo.url, dest)
        _git(clone_args)
    sha = _git(["rev-parse", "HEAD"], cwd=dest)
    return dest, sha


def run(
    config: Annotated[
        Path | None, typer.Option(help="ExternalScrapeConfig YAML (defaults to built-in values).")
    ] = None,
    checkout_root: Annotated[
        Path | None, typer.Option(help="Override where repos are cloned.")
    ] = None,
    output_dir: Annotated[
        Path | None, typer.Option(help="Override the dataset output directory.")
    ] = None,
    template: Annotated[
        str | None, typer.Option(help=f"Instruction template: one of {sorted(TEMPLATES)}.")
    ] = None,
    no_clone: Annotated[
        bool, typer.Option("--no-clone", help="Require existing checkouts; never clone.")
    ] = False,
) -> None:
    """Scrape (class -> human JMH benchmark) pairs and write records/messages/report."""
    cfg = ExternalScrapeConfig.from_yaml(config) if config else ExternalScrapeConfig()
    if checkout_root is not None:
        cfg = cfg.model_copy(update={"checkout_root": str(checkout_root)})
    if output_dir is not None:
        cfg = cfg.model_copy(update={"output_dir": str(output_dir)})
    if template is not None:
        cfg = cfg.model_copy(update={"template": template})
    if no_clone:
        cfg = cfg.model_copy(update={"clone": False})

    if cfg.template not in TEMPLATES:
        typer.echo(
            f"error: unknown template {cfg.template!r}; choose {sorted(TEMPLATES)}", err=True
        )
        raise typer.Exit(code=2)

    globs = tuple(cfg.benchmark_globs)
    suffixes = tuple(cfg.benchmark_suffixes)
    checkout_dir = Path(cfg.checkout_root)

    all_samples: list[BenchmarkSample] = []
    repo_reports: list[dict[str, Any]] = []
    for repo in cfg.repos:
        try:
            repo_dir, sha = _ensure_checkout(repo, checkout_dir, cfg.clone)
        except (FileNotFoundError, RuntimeError) as exc:
            typer.echo(f"error: {exc}", err=True)
            raise typer.Exit(code=1) from exc
        spec = RepoSpec(
            name=repo.name,
            url=repo.url,
            project=repo.project,
            commit=sha,
            license=repo.license,
        )
        logger.info("scraping %s @ %s", repo.name, sha[:12])
        result = scrape_repo(repo_dir, spec, globs=globs, suffixes=suffixes)
        all_samples.extend(result.samples)
        repo_reports.append(result.report)

    samples, dropped_oversize = _apply_size_cap(all_samples, cfg.max_java_source_chars)
    train, val = _split(samples, cfg.val_fraction, cfg.seed)

    by_method: dict[str, int] = {}
    by_repo: dict[str, int] = {}
    for sample in samples:
        method = str(sample.metadata.get("resolved_via"))
        by_method[method] = by_method.get(method, 0) + 1
        repo_name = str(sample.metadata.get("repo"))
        by_repo[repo_name] = by_repo.get(repo_name, 0) + 1

    report: dict[str, Any] = {
        "repos": repo_reports,
        "template": cfg.template,
        "max_java_source_chars": cfg.max_java_source_chars,
        "dropped_oversize_count": len(dropped_oversize),
        "dropped_oversize": dropped_oversize,
        "resolved_total": len(samples),
        "by_repo": by_repo,
        "by_method": by_method,
        "java_source_chars": _length_stats([len(s.snippet.source) for s in samples]),
        "benchmark_source_chars": _length_stats([len(s.benchmark_source or "") for s in samples]),
        "written": {"records": len(samples), "train": len(train), "val": len(val)},
    }

    out_dir = Path(cfg.output_dir)
    out_dir.mkdir(parents=True, exist_ok=True)
    _write_jsonl(out_dir / "records.jsonl", [s.to_dict() for s in samples])
    _write_jsonl(
        out_dir / "sft.jsonl",
        [{"messages": render_messages(s, cfg.template, cfg.fence_assistant)} for s in train],
    )
    if val:
        _write_jsonl(
            out_dir / "sft.val.jsonl",
            [{"messages": render_messages(s, cfg.template, cfg.fence_assistant)} for s in val],
        )
    (out_dir / "report.json").write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")

    typer.echo(json.dumps(report, indent=2))

    if not samples:
        typer.echo("\nerror: no (class -> benchmark) pairs were resolved.", err=True)
        raise typer.Exit(code=1)


def main() -> None:
    typer.run(run)


if __name__ == "__main__":
    main()
