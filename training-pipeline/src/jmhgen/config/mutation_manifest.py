"""Load the unified performance-mutation RFT project manifest."""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path

import yaml


@dataclass(frozen=True, slots=True)
class MutationProjectDeploy:
    source: str
    classpath_jar: str
    make_mutants_id: str
    mutation_sites: str


@dataclass(frozen=True, slots=True)
class MutationProject:
    id: str
    display_name: str
    config: str
    bsc_gcp_config: str
    deploy: MutationProjectDeploy

    def config_path(self, *, bsc_gcp: bool = False) -> str:
        return self.bsc_gcp_config if bsc_gcp else self.config


@dataclass(frozen=True, slots=True)
class MutationManifest:
    name: str
    projects: tuple[MutationProject, ...]
    train_config: str | None = None


def load_mutation_manifest(path: str | Path) -> MutationManifest:
    """Parse ``configs/rft/mutation-projects.yaml``."""
    data = yaml.safe_load(Path(path).read_text(encoding="utf-8"))
    if not isinstance(data, dict):
        raise ValueError(f"invalid mutation manifest (expected mapping): {path}")
    projects_raw = data.get("projects")
    if not isinstance(projects_raw, list) or not projects_raw:
        raise ValueError(f"mutation manifest must list at least one project: {path}")

    projects: list[MutationProject] = []
    for raw in projects_raw:
        if not isinstance(raw, dict):
            raise ValueError(f"invalid project entry in {path}: {raw!r}")
        deploy_raw = raw.get("deploy") or {}
        if not isinstance(deploy_raw, dict):
            raise ValueError(f"invalid deploy block for project {raw.get('id')!r} in {path}")
        projects.append(
            MutationProject(
                id=str(raw["id"]),
                display_name=str(raw.get("display_name") or raw["id"]),
                config=str(raw["config"]),
                bsc_gcp_config=str(raw["bsc_gcp_config"]),
                deploy=MutationProjectDeploy(
                    source=str(deploy_raw["source"]),
                    classpath_jar=str(deploy_raw["classpath_jar"]),
                    make_mutants_id=str(deploy_raw["make_mutants_id"]),
                    mutation_sites=str(deploy_raw["mutation_sites"]),
                ),
            )
        )
    train_raw = data.get("train") or {}
    if train_raw and not isinstance(train_raw, dict):
        raise ValueError(f"invalid train block in {path}: {train_raw!r}")
    train_config = train_raw.get("config") if train_raw else None
    return MutationManifest(
        name=str(data.get("name") or "mutation-rft"),
        projects=tuple(projects),
        train_config=str(train_config) if train_config else None,
    )
