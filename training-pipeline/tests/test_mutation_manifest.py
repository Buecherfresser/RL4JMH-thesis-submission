"""Tests for the unified mutation RFT manifest."""

from __future__ import annotations

from pathlib import Path

from jmhgen.config.mutation_manifest import load_mutation_manifest


def test_load_mutation_manifest_lists_both_projects() -> None:
    manifest = load_mutation_manifest("configs/rft/mutation-projects.yaml")
    assert manifest.name == "mutation-rft"
    assert [p.id for p in manifest.projects] == ["commons-lang", "fastutil"]
    assert manifest.projects[0].config_path(bsc_gcp=False).endswith(
        "commons-lang-mutation.yaml"
    )
    assert manifest.projects[1].config_path(bsc_gcp=True).endswith(
        "fastutil-mutation-bsc-gcp.yaml"
    )
    assert manifest.train_config == "configs/rft/mutation-merged.yaml"


def test_deploy_blocks_reference_existing_paths() -> None:
    root = Path(".")
    manifest = load_mutation_manifest("configs/rft/mutation-projects.yaml")
    for project in manifest.projects:
        assert (root / project.config).is_file()
        assert (root / project.bsc_gcp_config).is_file()
        assert (root / project.deploy.mutation_sites).is_file()
    assert manifest.train_config is not None
    assert (root / manifest.train_config).is_file()
