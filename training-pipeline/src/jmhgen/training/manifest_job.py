"""Run a unified mutation-RFT manifest (multi-project job orchestration)."""

from __future__ import annotations

import json
from collections.abc import Iterable, Sequence
from pathlib import Path
from typing import Literal

from jmhgen.config.mutation_manifest import MutationManifest
from jmhgen.config.schema import RFTConfig
from jmhgen.training import rft
from jmhgen.training.base import prepare_output_dir

ManifestPhaseOrder = Literal["cross-project", "per-project"]


def run_single_phase(config: RFTConfig, phase: str, *, label: str | None = None) -> None:
    """Run one cached RFT phase for a single project config."""
    prefix = f"[RFT:{label}] " if label else "[RFT] "
    if phase == "generate":
        path = rft.phase_generate(config)
        print(f"{prefix}generate -> {path}", flush=True)
    elif phase == "verify":
        path = rft.phase_verify(config)
        print(f"{prefix}verify -> {path}", flush=True)
    elif phase == "build":
        n = rft.phase_build(config)
        print(f"{prefix}build -> {n} training sample(s)", flush=True)
    elif phase == "train":
        prepare_output_dir(config.output_dir)
        rft.phase_train(config)
        print(f"{prefix}train complete", flush=True)
    else:
        raise SystemExit(f"unknown phase: {phase!r}")


def run_phases(
    config: RFTConfig, phases: Iterable[str], *, label: str | None = None
) -> None:
    """Run several phases for one project (generate -> verify -> build)."""
    for phase in phases:
        run_single_phase(config, phase, label=label)
    report = config.work_dir + "/report.json"
    prefix = f"[RFT:{label}] " if label else "[RFT] "
    print(f"{prefix}done (see {report})", flush=True)


def _jsonl_count(path: Path) -> int:
    """Count non-empty JSONL rows without parsing large message payloads."""
    if not path.exists():
        raise SystemExit(f"[RFT] expected built dataset at {path}; run the build phase first")
    with path.open(encoding="utf-8") as fh:
        return sum(1 for line in fh if line.strip())


def _merge_jsonl(inputs: Sequence[Path], output: Path) -> int:
    """Concatenate project JSONL files into the manifest-level train artifact."""
    output.parent.mkdir(parents=True, exist_ok=True)
    total = 0
    with output.open("w", encoding="utf-8") as dst:
        for source in inputs:
            if not source.exists():
                raise SystemExit(
                    f"[RFT] expected built dataset at {source}; run the build phase first"
                )
            with source.open(encoding="utf-8") as src:
                for raw in src:
                    line = raw.rstrip("\n")
                    if not line.strip():
                        continue
                    dst.write(line + "\n")
                    total += 1
    return total


def prepare_manifest_train_dataset(
    project_configs: Sequence[RFTConfig], train_config: RFTConfig
) -> tuple[int, int]:
    """Merge every project's accepted RFT samples into one manifest-level SFT set."""
    train_work = Path(train_config.work_dir)
    train_path = train_work / "sft.jsonl"
    val_path = train_work / "sft.val.jsonl"
    report_path = train_work / "report.json"

    train_inputs = [Path(config.work_dir) / "sft.jsonl" for config in project_configs]
    val_inputs = [Path(config.work_dir) / "sft.val.jsonl" for config in project_configs]
    train_count = _merge_jsonl(train_inputs, train_path)
    val_count = _merge_jsonl([path for path in val_inputs if path.exists()], val_path)
    if train_count == 0:
        raise SystemExit("[RFT] merged manifest train dataset has 0 samples")

    project_rows = []
    for config, train_input, val_input in zip(
        project_configs, train_inputs, val_inputs, strict=True
    ):
        project_rows.append(
            {
                "work_dir": config.work_dir,
                "train_path": str(train_input),
                "train_samples": _jsonl_count(train_input),
                "val_path": str(val_input),
                "val_samples": _jsonl_count(val_input) if val_input.exists() else 0,
            }
        )
    report_path.write_text(
        json.dumps(
            {
                "train_samples": train_count,
                "val_samples": val_count,
                "projects": project_rows,
            },
            indent=2,
        )
        + "\n",
        encoding="utf-8",
    )
    return train_count, val_count


def run_manifest_train(
    manifest: MutationManifest,
    project_configs: Sequence[RFTConfig],
) -> None:
    """Train one adapter for the whole manifest from merged project RFT samples."""
    if not manifest.train_config:
        for project, config in zip(manifest.projects, project_configs, strict=True):
            print(
                f"[RFT] --- {project.display_name} ({project.id}) :: train ---",
                flush=True,
            )
            run_single_phase(config, "train", label=project.id)
        return

    train_config = RFTConfig.from_yaml(manifest.train_config)
    n_train, n_val = prepare_manifest_train_dataset(project_configs, train_config)
    print(
        f"[RFT] --- merged manifest train :: {n_train} train / {n_val} val sample(s) ---",
        flush=True,
    )
    prepare_output_dir(train_config.output_dir)
    rft.phase_train(train_config)
    print("[RFT:merged] train complete", flush=True)


def run_manifest(
    manifest: MutationManifest,
    phases: Sequence[str],
    *,
    bsc_gcp: bool,
    order: ManifestPhaseOrder = "cross-project",
) -> None:
    """Run a multi-project RFT job as one sequential worker.

    ``cross-project`` (default) runs each phase across all projects before the next
    phase — e.g. generate commons-lang, generate fastutil, then verify both, then build
    both. This lets you turn off the GPU after every ``generate`` phase finishes.

    ``per-project`` runs the full phase list for one project before moving to the next.
    """
    phase_list = tuple(phases)
    if not phase_list:
        raise SystemExit("[RFT] manifest job requires at least one phase")

    projects = manifest.projects

    def load_config(project) -> RFTConfig:  # noqa: ANN001
        return RFTConfig.from_yaml(project.config_path(bsc_gcp=bsc_gcp))

    has_train = "train" in phase_list
    project_phase_list = tuple(phase for phase in phase_list if phase != "train")

    if order == "per-project":
        for index, project in enumerate(projects, start=1):
            print(
                f"[RFT] === {manifest.name}: project {index}/{len(projects)} "
                f"{project.display_name} ({project.id}) ===",
                flush=True,
            )
            if project_phase_list:
                run_phases(load_config(project), project_phase_list, label=project.id)
        if has_train:
            run_manifest_train(manifest, [load_config(project) for project in projects])
        return

    for phase_index, phase in enumerate(project_phase_list, start=1):
        print(
            f"[RFT] === {manifest.name}: phase {phase_index}/{len(project_phase_list)} "
            f"{phase} across {len(projects)} project(s) ===",
            flush=True,
        )
        for project in projects:
            print(
                f"[RFT] --- {project.display_name} ({project.id}) :: {phase} ---",
                flush=True,
            )
            run_single_phase(load_config(project), phase, label=project.id)
    if has_train:
        run_manifest_train(manifest, [load_config(project) for project in projects])
