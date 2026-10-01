"""``jmh-build-sft`` — build the SFT dataset from the distilled gpt-oss-120b tree.

Pipeline:

1. filter the distilled tree to run-verified benchmark classes and resolve, for each, the
   final verified benchmark source plus the original ``<Class>.java`` from a local RxJava
   checkout (:func:`jmhgen.data.build_dataset`);
2. gate on the Java-source resolution rate (guards the ~85%-confident commit);
3. write the prompt-agnostic ``records.jsonl`` and a ``report.json``;
4. render TRL conversational messages with the chosen template and write a deterministic
   train/val split. For templates that carry hard rules (e.g. ``jmhbench``) only targets that
   satisfy them are rendered, so the prompt never contradicts the trained output.

Pass ``--from-records PATH`` to skip the build and re-render an existing ``records.jsonl``
(the Java sources are embedded there). This is the fast path for swapping the prompt template
or tweaking the work-in-progress conformance rules without re-resolving the source checkout.
"""

from __future__ import annotations

import json
import random
from collections import Counter
from pathlib import Path
from typing import Annotated, Any

import typer

from jmhgen.config.schema import SftDataConfig
from jmhgen.data.build_sft import build_dataset
from jmhgen.data.conformance import CONFORMANCE_TEMPLATES, violations
from jmhgen.data.prompts import TEMPLATES, render_messages
from jmhgen.data.schema import BenchmarkSample, load_benchmark_samples


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


def _partition_conformant(
    samples: list[BenchmarkSample],
) -> tuple[list[BenchmarkSample], list[BenchmarkSample], dict[str, Any]]:
    """Split ``samples`` into (conformant, non_conformant) and a report of why some failed."""
    conformant: list[BenchmarkSample] = []
    dropped: list[BenchmarkSample] = []
    reason_counts: Counter[str] = Counter()
    dropped_ids: list[dict[str, Any]] = []
    for sample in samples:
        reasons = violations(sample)
        if reasons:
            dropped.append(sample)
            dropped_ids.append({"id": sample.snippet.id, "reasons": reasons})
            for reason in reasons:
                reason_counts[reason.split(":", 1)[0]] += 1
        else:
            conformant.append(sample)
    report = {
        "checked": len(samples),
        "conformant": len(conformant),
        "non_conformant": len(dropped),
        "reason_counts": dict(sorted(reason_counts.items())),
        "non_conformant_ids": dropped_ids,
    }
    return conformant, dropped, report


def _records_report(samples: list[BenchmarkSample]) -> dict[str, Any]:
    """Reconstruct a build-style report when re-rendering from an existing records file."""
    stage_counts: Counter[str] = Counter()
    for sample in samples:
        stage_counts[str(sample.metadata.get("stage"))] += 1
    return {
        "java_source_commit": next(
            (s.metadata.get("java_source_commit") for s in samples if s.metadata), None
        ),
        "total_run_locked": len(samples),
        "resolved": len(samples),
        "missing_java_source_count": 0,
        "missing_syn_out_count": 0,
        "missing_java_source": [],
        "missing_syn_out": [],
        "stage_counts": dict(sorted(stage_counts.items())),
        "with_fork_scores": sum(1 for s in samples if s.metadata.get("fork_scores")),
        "source": "records",
    }


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


def run(
    config: Annotated[
        Path | None, typer.Option(help="SftDataConfig YAML (defaults to built-in values).")
    ] = None,
    distilled_root: Annotated[
        Path | None, typer.Option(help="Override the distilled gpt-oss-120b root.")
    ] = None,
    java_source_root: Annotated[
        Path | None, typer.Option(help="Override the local RxJava checkout root.")
    ] = None,
    fork_dir: Annotated[
        Path | None, typer.Option(help="Override the JMH fork-results directory.")
    ] = None,
    output_dir: Annotated[
        Path | None, typer.Option(help="Override the dataset output directory.")
    ] = None,
    template: Annotated[
        str | None, typer.Option(help=f"Instruction template: one of {sorted(TEMPLATES)}.")
    ] = None,
    from_records: Annotated[
        Path | None,
        typer.Option(
            help="Skip the build and re-render this existing records.jsonl instead "
            "(Java sources are embedded there).",
        ),
    ] = None,
    enforce_conformance: Annotated[
        bool | None,
        typer.Option(
            "--enforce-conformance/--no-enforce-conformance",
            help="For rule-bearing templates, render only targets that satisfy the rules.",
        ),
    ] = None,
    allow_missing: Annotated[
        bool, typer.Option("--allow-missing", help="Skip unresolved classes instead of aborting.")
    ] = False,
) -> None:
    """Build the SFT dataset and write records/messages/report under the output directory."""
    cfg = SftDataConfig.from_yaml(config) if config else SftDataConfig()
    if distilled_root is not None:
        cfg = cfg.model_copy(update={"distilled_root": str(distilled_root)})
    if java_source_root is not None:
        cfg = cfg.model_copy(update={"java_source_root": str(java_source_root)})
    if fork_dir is not None:
        cfg = cfg.model_copy(update={"fork_dir": str(fork_dir)})
    if output_dir is not None:
        cfg = cfg.model_copy(update={"output_dir": str(output_dir)})
    if template is not None:
        cfg = cfg.model_copy(update={"template": template})
    if enforce_conformance is not None:
        cfg = cfg.model_copy(update={"enforce_conformance": enforce_conformance})
    if allow_missing:
        cfg = cfg.model_copy(update={"require_all_resolved": False})

    if cfg.template not in TEMPLATES:
        typer.echo(
            f"error: unknown template {cfg.template!r}; choose {sorted(TEMPLATES)}", err=True
        )
        raise typer.Exit(code=2)

    out_dir = Path(cfg.output_dir)
    if from_records is not None:
        all_samples = list(load_benchmark_samples(from_records))
        base_report: dict[str, Any] = _records_report(all_samples)
    else:
        result = build_dataset(
            distilled_root=cfg.distilled_root,
            java_source_root=cfg.java_source_root,
            fork_dir=cfg.fork_dir,
            commit=cfg.java_source_commit,
        )
        all_samples = result.samples
        base_report = dict(result.report)

    samples, dropped_oversize = _apply_size_cap(all_samples, cfg.max_java_source_chars)

    # For rule-bearing templates, the rendered (trained-on) view is gated on conformance so the
    # prompt never asks for something the target violates. records.jsonl stays the full set.
    conformance_report: dict[str, Any] | None = None
    renderable = samples
    if cfg.template in CONFORMANCE_TEMPLATES:
        conformant, _dropped, conformance_report = _partition_conformant(samples)
        if cfg.enforce_conformance:
            renderable = conformant

    train, val = _split(renderable, cfg.val_fraction, cfg.seed)

    report = base_report
    report.update(
        {
            "template": cfg.template,
            "enforce_conformance": cfg.enforce_conformance,
            "max_java_source_chars": cfg.max_java_source_chars,
            "dropped_oversize_count": len(dropped_oversize),
            "dropped_oversize": dropped_oversize,
            "written": {"records": len(samples), "train": len(train), "val": len(val)},
        }
    )
    if conformance_report is not None:
        report["conformance"] = conformance_report

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
    else:
        # Avoid leaving a stale val split from a previous (rule-free) build alongside new data.
        stale_val = out_dir / "sft.val.jsonl"
        if stale_val.exists():
            stale_val.unlink()
    (out_dir / "report.json").write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")

    typer.echo(json.dumps(report, indent=2))

    unresolved = report["missing_java_source_count"] + report["missing_syn_out_count"]
    if unresolved and cfg.require_all_resolved:
        typer.echo(
            f"\nerror: {unresolved} run-locked class(es) could not be resolved against "
            f"commit {cfg.java_source_commit!r}. Inspect report.json, fix the commit, or rerun "
            f"with --allow-missing.",
            err=True,
        )
        raise typer.Exit(code=1)


def main() -> None:
    typer.run(run)


if __name__ == "__main__":
    main()
