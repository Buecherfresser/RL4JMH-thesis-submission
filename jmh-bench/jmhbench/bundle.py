"""Portable *generation bundles* that decouple generation from benchmarking.

A generation bundle is the artifact produced by ``jmhbench generate`` and
consumed by ``jmhbench bench``. It captures everything the benchmarking phase
needs and *nothing it doesn't* — in particular no API keys and no model access
— so the two halves of a run can happen on different machines:

    laptop  $ jmhbench generate --harness openrouter -o model=... --out bundle/
    laptop  $ rsync -a bundle/ bench-box:/runs/bundle/
    bench   $ jmhbench bench /runs/bundle/        # quiet machine, JDK + Maven
    laptop  $ rsync -a bench-box:/runs/bundle-report/ ./report/

Layout (self-contained — the bench machine needs no dataset checkout)::

    bundle/
    ├── generation.json          # manifest: config + per-task generation outcome
    ├── per_task/
    │   ├── <id>.java            # generated benchmark source (package-normalised)
    │   └── <id>.raw.md          # raw model output, when the harness exposed it
    └── dataset/
        ├── tasks/<id>/...       # embedded task definitions (SUT, regressions)
        └── tasks_real/<id>/...  # so the bench phase resolves them locally

The manifest records each task's generation result (succeeded / errored /
skipped, metadata, token counts, ...) so the final scorecard reflects the
*real* generation outcome rather than treating every prepared file as a pass.
"""

from __future__ import annotations

import json
import shutil
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path

from jmhbench.adapters._llm_common import render_raw_output_md
from jmhbench.dataset import TRACKS, discover_tasks
from jmhbench.harness import Task
from jmhbench.runner import TaskResult

MANIFEST_NAME = "generation.json"
PER_TASK_DIR = "per_task"
DATASET_DIR = "dataset"


def write_generation_bundle(
    bundle_dir: Path,
    *,
    harness_name: str,
    run_label: str,
    config_summary: dict,
    results: list[TaskResult],
    tasks: list[Task],
) -> dict:
    """Persist a generation bundle to *bundle_dir* and return the manifest dict.

    *results* are the per-task outcomes from :func:`jmhbench.runner.generate_task`;
    *tasks* the corresponding :class:`Task` objects (their definitions are copied
    into the bundle so the benchmarking phase is fully self-contained).
    """
    bundle_dir.mkdir(parents=True, exist_ok=True)
    per_task = bundle_dir / PER_TASK_DIR
    per_task.mkdir(parents=True, exist_ok=True)
    dataset_root = bundle_dir / DATASET_DIR

    task_by_id = {t.instance_id: t for t in tasks}
    entries: dict[str, dict] = {}

    for r in results:
        entry: dict = {
            "instance_id": r.instance_id,
            "harness": r.harness,
            "generated": r.generated,
            "generation_error": r.generation_error,
            "generation_metadata": r.generation_metadata,
            "duration_seconds": r.duration_seconds,
            "benchmark_source": None,
            "raw_output_file": None,
            # Extra @State / helper sources are usually empty for LLM harnesses;
            # store them inline so the bundle stays a flat, greppable tree.
            "extra_sources": dict(r.extra_sources or {}),
        }
        if r.benchmark_source:
            rel = f"{PER_TASK_DIR}/{r.instance_id}.java"
            (bundle_dir / rel).write_text(r.benchmark_source)
            entry["benchmark_source"] = rel
        if r.raw_output is not None:
            rel = f"{PER_TASK_DIR}/{r.instance_id}.raw.md"
            (bundle_dir / rel).write_text(
                render_raw_output_md(r.instance_id, r.raw_output, r.generation_metadata)
            )
            entry["raw_output_file"] = rel

        task = task_by_id.get(r.instance_id)
        if task is not None:
            _embed_task(dataset_root, task)
        entries[r.instance_id] = entry

    manifest = {
        "harness": harness_name,
        "run_label": run_label,
        "timestamp": datetime.utcnow().isoformat() + "Z",
        "config": config_summary,
        "tasks": entries,
    }
    (bundle_dir / MANIFEST_NAME).write_text(json.dumps(manifest, indent=2, default=str))
    return manifest


def _embed_task(dataset_root: Path, task: Task) -> None:
    """Copy a task's on-disk definition into the bundle, preserving its track."""
    sub = TRACKS.get(task.track, TRACKS["synthetic"])
    dst = dataset_root / sub / task.instance_id
    if dst.exists():
        shutil.rmtree(dst)
    dst.parent.mkdir(parents=True, exist_ok=True)
    shutil.copytree(task.task_dir, dst)


@dataclass
class LoadedBundle:
    """A generation bundle read back from disk for the benchmarking phase."""

    dir: Path
    manifest: dict
    tasks: list[Task]
    """Tasks discovered from the embedded dataset, ordered as in the manifest."""

    @property
    def run_label(self) -> str:
        return self.manifest.get("run_label") or self.manifest.get("harness") or "bench"

    @property
    def harness_name(self) -> str:
        return self.manifest.get("harness") or "predictions"

    def entry(self, instance_id: str) -> dict:
        return self.manifest.get("tasks", {}).get(instance_id, {})

    def task_result(self, instance_id: str) -> TaskResult:
        """Reconstruct the generation-phase :class:`TaskResult` for a task.

        The benchmarking fields are left empty for :func:`jmhbench.runner.bench_task`
        to fill in. ``raw_output_file`` is left pointing at the bundle-relative
        path; the caller is responsible for copying that file into the final
        report directory (see the ``bench`` CLI command).
        """
        e = self.entry(instance_id)
        r = TaskResult(instance_id=instance_id, harness=e.get("harness", self.harness_name))
        r.generated = bool(e.get("generated"))
        r.generation_error = e.get("generation_error")
        meta = dict(e.get("generation_metadata") or {})
        if "generation_seconds" not in meta:
            gen_secs = float(e.get("duration_seconds") or 0.0)
            if gen_secs:
                meta["generation_seconds"] = gen_secs
        r.generation_metadata = meta
        r.extra_sources = e.get("extra_sources") or {}
        r.duration_seconds = float(e.get("duration_seconds") or 0.0)
        src_rel = e.get("benchmark_source")
        if src_rel and (self.dir / src_rel).exists():
            r.benchmark_source = (self.dir / src_rel).read_text()
        raw_rel = e.get("raw_output_file")
        if raw_rel and (self.dir / raw_rel).exists():
            r.raw_output_file = raw_rel
        return r


def load_generation_bundle(bundle_dir: Path) -> LoadedBundle:
    """Read a generation bundle written by :func:`write_generation_bundle`."""
    bundle_dir = Path(bundle_dir).expanduser().resolve()
    manifest_path = bundle_dir / MANIFEST_NAME
    if not manifest_path.exists():
        raise FileNotFoundError(
            f"Not a generation bundle (missing {MANIFEST_NAME}): {bundle_dir}"
        )
    manifest = json.loads(manifest_path.read_text())

    discovered: dict[str, Task] = {}
    dataset_root = bundle_dir / DATASET_DIR
    if dataset_root.exists():
        # Track is inferred from the sub-directory name, mirroring the repo's
        # dataset/ layout, so embedded `tasks/` and `tasks_real/` keep their
        # synthetic / real labels.
        for sub in TRACKS.values():
            d = dataset_root / sub
            if d.exists():
                for t in discover_tasks(d):
                    discovered.setdefault(t.instance_id, t)

    ordered = [discovered[i] for i in manifest.get("tasks", {}) if i in discovered]
    return LoadedBundle(dir=bundle_dir, manifest=manifest, tasks=ordered)
