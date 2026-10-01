"""Discovery and loading of project mutation-track instances.

A *project* is a vendored, real-world Java library used as a held-out subject
under test. It lives at ``dataset/projects/<name>/`` and ships:

* ``project.yaml``    - metadata (version, dependencies, mutation config, ...)
* ``mutants.yaml``    - the hidden ground-truth registry of 100 mutants
* ``mutations.patch`` - the single patch that bakes the dormant mutants in
* ``api_surface.md``  - the curated public-API digest shown to the harness
* ``src/main/java/``  - the pristine subject-under-test sources

This is intentionally separate from :mod:`jmhbench.dataset` (the per-task
synthetic/real tracks): the project track has its own model, runner and report.
"""

from __future__ import annotations

from pathlib import Path

import yaml

from jmhbench.harness import MutantSpec, ProjectTask, Provenance


def _repo_root() -> Path:
    return Path(__file__).resolve().parent.parent


def projects_dir() -> Path:
    return _repo_root() / "dataset" / "projects"


def load_project_task(project_dir: Path) -> ProjectTask:
    """Load a :class:`ProjectTask` from ``<project_dir>/project.yaml``."""
    project_dir = project_dir.resolve()
    spec_file = project_dir / "project.yaml"
    if not spec_file.exists():
        raise FileNotFoundError(f"Missing project.yaml in {project_dir}")
    data = yaml.safe_load(spec_file.read_text()) or {}

    instance_id = data.get("name") or project_dir.name
    provenance = None
    if isinstance(data.get("provenance"), dict):
        provenance = Provenance.model_validate(data["provenance"])

    mutants = _load_mutants(project_dir, data.get("mutants_registry", "mutants.yaml"))

    # YAML happily parses ``version: 1.10`` as the float 1.1; coerce everything
    # back to strings so Maven coordinates survive verbatim.
    dependencies = [
        {k: str(v) for k, v in dep.items()} for dep in data.get("dependencies", [])
    ]

    return ProjectTask(
        instance_id=instance_id,
        display_name=data.get("display_name", instance_id),
        version=str(data.get("version", "unknown")),
        source_dir=data.get("source_dir", "src/main/java"),
        mutations_patch=data.get("mutations_patch", "mutations.patch"),
        api_surface_path=data.get("api_surface", "api_surface.md"),
        dependencies=dependencies,
        mutation=dict(data.get("mutation", {})),
        harness_input=dict(data.get("harness_input", {})),
        mutants=mutants,
        provenance=provenance,
        project_dir=project_dir,
    )


def _load_mutants(project_dir: Path, registry_name: str) -> list[MutantSpec]:
    registry = project_dir / registry_name
    if not registry.exists():
        return []
    data = yaml.safe_load(registry.read_text()) or {}
    return [MutantSpec.model_validate(m) for m in data.get("mutants", [])]


def discover_projects() -> list[ProjectTask]:
    """Return every project task found under ``dataset/projects/``."""
    base = projects_dir()
    if not base.exists():
        return []
    out: list[ProjectTask] = []
    for entry in sorted(base.iterdir()):
        if entry.is_dir() and (entry / "project.yaml").exists():
            out.append(load_project_task(entry))
    return out


def get_project_task(name: str) -> ProjectTask:
    """Load a single project task by directory name (e.g. ``commons-compress``)."""
    project_dir = projects_dir() / name
    if not (project_dir / "project.yaml").exists():
        available = ", ".join(p.instance_id for p in discover_projects()) or "<none>"
        raise KeyError(f"No project '{name}' under {projects_dir()}. Available: {available}")
    return load_project_task(project_dir)
