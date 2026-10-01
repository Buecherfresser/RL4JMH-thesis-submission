"""Tests for cross-project manifest scheduling."""

from __future__ import annotations

from pathlib import Path

from jmhgen.config.mutation_manifest import (
    MutationManifest,
    MutationProject,
    MutationProjectDeploy,
    load_mutation_manifest,
)
from jmhgen.training import manifest_job


def test_run_manifest_cross_project_order(monkeypatch) -> None:
    manifest = load_mutation_manifest("configs/rft/mutation-projects.yaml")
    calls: list[tuple[str, str]] = []

    def fake_single_phase(config, phase, *, label=None):  # noqa: ANN001
        calls.append((label or "?", phase))

    monkeypatch.setattr(manifest_job, "run_single_phase", fake_single_phase)

    def fake_from_yaml(_path):  # noqa: ANN001
        class _Cfg:
            work_dir = "x"

        return _Cfg()

    monkeypatch.setattr("jmhgen.config.schema.RFTConfig.from_yaml", fake_from_yaml)

    manifest_job.run_manifest(
        manifest,
        ("generate", "verify"),
        bsc_gcp=True,
        order="cross-project",
    )
    assert calls == [
        ("commons-lang", "generate"),
        ("fastutil", "generate"),
        ("commons-lang", "verify"),
        ("fastutil", "verify"),
    ]


def test_run_manifest_per_project_order(monkeypatch) -> None:
    manifest = load_mutation_manifest("configs/rft/mutation-projects.yaml")
    calls: list[tuple[str, str]] = []

    def fake_run_phases(config, phases, *, label=None):  # noqa: ANN001
        for phase in phases:
            calls.append((label or "?", phase))

    monkeypatch.setattr(manifest_job, "run_phases", fake_run_phases)

    def fake_from_yaml(_path):  # noqa: ANN001
        class _Cfg:
            work_dir = "x"

        return _Cfg()

    monkeypatch.setattr("jmhgen.config.schema.RFTConfig.from_yaml", fake_from_yaml)

    manifest_job.run_manifest(
        manifest,
        ("generate", "verify"),
        bsc_gcp=False,
        order="per-project",
    )
    assert calls == [
        ("commons-lang", "generate"),
        ("commons-lang", "verify"),
        ("fastutil", "generate"),
        ("fastutil", "verify"),
    ]


def test_manifest_train_merges_project_datasets(monkeypatch, tmp_path: Path) -> None:
    commons = tmp_path / "commons"
    fastutil = tmp_path / "fastutil"
    merged = tmp_path / "merged"
    commons.mkdir()
    fastutil.mkdir()
    (commons / "sft.jsonl").write_text('{"id":"c1"}\n{"id":"c2"}\n', encoding="utf-8")
    (commons / "sft.val.jsonl").write_text('{"id":"cv"}\n', encoding="utf-8")
    (fastutil / "sft.jsonl").write_text('{"id":"f1"}\n', encoding="utf-8")
    (fastutil / "sft.val.jsonl").write_text('{"id":"fv"}\n', encoding="utf-8")

    manifest = MutationManifest(
        name="test-rft",
        train_config="merged.yaml",
        projects=(
            _project("commons-lang", "commons.yaml"),
            _project("fastutil", "fastutil.yaml"),
        ),
    )
    configs = {
        "commons.yaml": _Config(work_dir=str(commons), output_dir=str(tmp_path / "unused-c")),
        "fastutil.yaml": _Config(work_dir=str(fastutil), output_dir=str(tmp_path / "unused-f")),
        "merged.yaml": _Config(work_dir=str(merged), output_dir=str(tmp_path / "out")),
    }
    trained: list[str] = []

    monkeypatch.setattr(
        "jmhgen.config.schema.RFTConfig.from_yaml",
        lambda path: configs[path],
    )
    monkeypatch.setattr(
        manifest_job.rft,
        "phase_train",
        lambda config: trained.append(config.output_dir),
    )
    monkeypatch.setattr(manifest_job, "prepare_output_dir", lambda _path: None)

    manifest_job.run_manifest(manifest, ("train",), bsc_gcp=False)

    assert (merged / "sft.jsonl").read_text(encoding="utf-8").splitlines() == [
        '{"id":"c1"}',
        '{"id":"c2"}',
        '{"id":"f1"}',
    ]
    assert (merged / "sft.val.jsonl").read_text(encoding="utf-8").splitlines() == [
        '{"id":"cv"}',
        '{"id":"fv"}',
    ]
    assert trained == [str(tmp_path / "out")]


def _project(project_id: str, config: str) -> MutationProject:
    return MutationProject(
        id=project_id,
        display_name=project_id,
        config=config,
        bsc_gcp_config=config,
        deploy=MutationProjectDeploy(
            source="source",
            classpath_jar="mutants.jar",
            make_mutants_id=project_id,
            mutation_sites="sites.yaml",
        ),
    )


class _Config:
    def __init__(self, *, work_dir: str, output_dir: str) -> None:
        self.work_dir = work_dir
        self.output_dir = output_dir
