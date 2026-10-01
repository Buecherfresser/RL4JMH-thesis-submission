"""Task discovery and loading from the on-disk dataset."""

from __future__ import annotations

from pathlib import Path

import yaml

from jmhbench.harness import Task

DATASET_DIR_ENV = "JMHBENCH_DATASET_DIR"

# Available tracks. A *track* is a subdirectory of ``dataset/`` that contains
# task folders. ``synthetic`` is the hand-built v1 set; ``real`` ships tasks
# whose regressions mirror documented OSS performance commits.
TRACKS: dict[str, str] = {
    "synthetic": "tasks",
    "real": "tasks_real",
}


def _repo_root() -> Path:
    return Path(__file__).resolve().parent.parent


def default_dataset_dir() -> Path:
    """Default dataset directory (the *synthetic* track, for backwards compat).

    Order of resolution:
    1. ``$JMHBENCH_DATASET_DIR`` if set
    2. ``./dataset/tasks`` relative to the repo root
    """
    import os

    env = os.environ.get(DATASET_DIR_ENV)
    if env:
        return Path(env).expanduser().resolve()
    return _repo_root() / "dataset" / "tasks"


def dataset_dirs_for_track(track: str) -> list[Path]:
    """Return the dataset directories to scan for *track*.

    ``track`` may be a single track name (``synthetic``, ``real``) or
    ``all`` to union every known track. If ``$JMHBENCH_DATASET_DIR`` is set
    the environment override wins for the ``synthetic`` track only.
    """
    if track == "all":
        return [_repo_root() / "dataset" / sub for sub in TRACKS.values()]
    if track not in TRACKS:
        raise ValueError(
            f"Unknown track '{track}'. Available: {', '.join(sorted([*TRACKS.keys(), 'all']))}"
        )
    if track == "synthetic":
        return [default_dataset_dir()]
    return [_repo_root() / "dataset" / TRACKS[track]]


def load_task(task_dir: Path, *, track: str | None = None) -> Task:
    spec_file = task_dir / "task.yaml"
    if not spec_file.exists():
        raise FileNotFoundError(f"Missing task.yaml in {task_dir}")
    data = yaml.safe_load(spec_file.read_text()) or {}
    data["task_dir"] = task_dir
    if track is not None and "track" not in data:
        data["track"] = track
    return Task.model_validate(data)


def _infer_track(dataset_dir: Path) -> str:
    name = dataset_dir.name
    for track, sub in TRACKS.items():
        if sub == name:
            return track
    return "synthetic"


def discover_tasks(
    dataset_dir: Path | None = None,
    *,
    track: str | None = None,
) -> list[Task]:
    """Discover tasks.

    Either pass an explicit ``dataset_dir`` (legacy) or a ``track`` name
    (``synthetic`` / ``real`` / ``all``). Passing both is an error.
    """
    if dataset_dir is not None and track is not None:
        raise ValueError("Pass dataset_dir or track, not both")
    if dataset_dir is not None:
        dirs = [dataset_dir]
    elif track is not None:
        dirs = dataset_dirs_for_track(track)
    else:
        dirs = [default_dataset_dir()]

    tasks: list[Task] = []
    seen: set[str] = set()
    for ddir in dirs:
        if not ddir.exists():
            continue
        inferred_track = _infer_track(ddir)
        for entry in sorted(ddir.iterdir()):
            if entry.is_dir() and (entry / "task.yaml").exists():
                task = load_task(entry, track=inferred_track)
                if task.instance_id in seen:
                    continue
                seen.add(task.instance_id)
                tasks.append(task)
    return tasks


def filter_tasks(tasks: list[Task], ids: list[str] | None, tags: list[str] | None) -> list[Task]:
    out = tasks
    if ids:
        wanted = set(ids)
        out = [t for t in out if t.instance_id in wanted]
    if tags:
        wanted_tags = set(tags)
        out = [t for t in out if wanted_tags.intersection(t.tags)]
    return out
