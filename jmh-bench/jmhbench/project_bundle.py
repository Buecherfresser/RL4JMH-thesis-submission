"""Portable *project generation bundles* for the project mutation track.

Mirrors :mod:`jmhbench.bundle` (the per-task track) but for whole-project runs:
``jmhbench project-gen`` writes a bundle, ``jmhbench project-bench <bundle>``
consumes it. The bundle captures the harness-generated JMH suite and the
per-class generation outcomes — and nothing the benchmarking phase doesn't need
(no API keys, no model access) — so generation (needs the model) and
benchmarking (needs a JDK + Maven, ideally a quiet box) can run separately::

    laptop  $ jmhbench project-gen --harness openrouter -o model=... --parallel 8 --out gen/
    laptop  $ rsync -a gen/ bench-box:/runs/gen/
    bench   $ jmhbench project-bench /runs/gen/        # JDK + Maven, no model

Layout::

    bundle/
    ├── generation.json     # manifest: project ref + config + per-class outcomes
    ├── suite/
    │   └── bench/generated/cNNN/<Class>Benchmark.java   # every generated file
    └── model_output.md     # raw model prompts/replies, when available

The subject-under-test is referenced by **name** (``project`` in the manifest),
not embedded: the benchmarking phase re-materialises it from
``dataset/projects/<name>/`` in the repo (the vendored source is large and
already version-controlled). The bench machine therefore needs the same repo
checkout — unlike the per-task bundle, which embeds its tiny task definitions.
"""

from __future__ import annotations

import json
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path

from jmhbench.adapters._llm_common import render_raw_output_md
from jmhbench.harness import ProjectTask
from jmhbench.project_bench import ProjectBenchResult, _rel_for
from jmhbench.projects import get_project_task

MANIFEST_NAME = "generation.json"
SUITE_DIR = "suite"
RAW_OUTPUT_NAME = "model_output.md"


def write_project_bundle(
    bundle_dir: Path,
    *,
    result: ProjectBenchResult,
    task: ProjectTask,
    config_summary: dict,
    run_label: str,
) -> dict:
    """Persist a project generation bundle and return the manifest dict."""
    bundle_dir.mkdir(parents=True, exist_ok=True)
    suite = bundle_dir / SUITE_DIR
    suite.mkdir(parents=True, exist_ok=True)

    files: list[str] = []
    primary_rel: str | None = None
    if result.benchmark_source:
        primary_rel = _rel_for(result.benchmark_source)
        _write(suite / primary_rel, result.benchmark_source)
        files.append(primary_rel)
    for rel, src in (result.extra_sources or {}).items():
        _write(suite / rel, src)
        files.append(rel)

    raw_rel: str | None = None
    if result.raw_outputs:
        blocks = []
        for i, raw in enumerate(result.raw_outputs):
            blocks.append(f"# Class generation {i}\n")
            blocks.append(render_raw_output_md(result.instance_id, raw, {}))
        _write(bundle_dir / RAW_OUTPUT_NAME, "\n\n---\n\n".join(blocks))
        raw_rel = RAW_OUTPUT_NAME
    elif result.raw_output is not None:
        _write(
            bundle_dir / RAW_OUTPUT_NAME,
            render_raw_output_md(result.instance_id, result.raw_output, result.generation_metadata),
        )
        raw_rel = RAW_OUTPUT_NAME

    manifest = {
        "project": task.instance_id,
        "version": task.version,
        "harness": result.harness,
        "run_label": run_label,
        "timestamp": datetime.utcnow().isoformat() + "Z",
        "config": config_summary,
        "input_mode": result.input_mode,
        "classes_total": result.classes_total,
        "classes_succeeded": result.classes_succeeded,
        "generation_interrupted": result.generation_interrupted,
        "generation_error": result.generation_error,
        "generation_metadata": result.generation_metadata,
        "class_generations": result.class_generations,
        "suite": {"primary": primary_rel, "files": files, "raw_output": raw_rel},
    }
    _write(bundle_dir / MANIFEST_NAME, json.dumps(manifest, indent=2, default=str))
    return manifest


def _write(path: Path, text: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text)


@dataclass
class LoadedProjectBundle:
    """A project generation bundle read back for the benchmarking phase."""

    dir: Path
    manifest: dict
    task: ProjectTask

    @property
    def run_label(self) -> str:
        return self.manifest.get("run_label") or self.manifest.get("harness") or "project"

    @property
    def harness_name(self) -> str:
        return self.manifest.get("harness") or "predictions"

    def to_result(self) -> ProjectBenchResult:
        """Reconstruct the generation-phase :class:`ProjectBenchResult`.

        Benchmarking fields stay empty for :func:`jmhbench.project_bench.bench_project`
        to fill in. ``raw_output_file`` points at the bundle-relative Markdown so
        the report writer can carry it into the final report.
        """
        m = self.manifest
        result = ProjectBenchResult(
            instance_id=m.get("project", self.task.instance_id),
            harness=self.harness_name,
        )
        result.input_mode = m.get("input_mode", "per_class")
        result.classes_total = int(m.get("classes_total") or 0)
        result.classes_succeeded = int(m.get("classes_succeeded") or 0)
        result.generation_interrupted = bool(m.get("generation_interrupted"))
        result.generation_error = m.get("generation_error")
        result.generation_metadata = dict(m.get("generation_metadata") or {})
        result.class_generations = list(m.get("class_generations") or [])

        suite = m.get("suite", {})
        primary_rel = suite.get("primary")
        files = suite.get("files", [])
        if primary_rel and (self.dir / SUITE_DIR / primary_rel).exists():
            result.benchmark_source = (self.dir / SUITE_DIR / primary_rel).read_text()
            result.generated = True
        result.extra_sources = {
            rel: (self.dir / SUITE_DIR / rel).read_text()
            for rel in files
            if rel != primary_rel and (self.dir / SUITE_DIR / rel).exists()
        }
        raw_rel = suite.get("raw_output")
        if raw_rel and (self.dir / raw_rel).exists():
            result.raw_output_file = raw_rel
        return result


def load_project_bundle(bundle_dir: Path) -> LoadedProjectBundle:
    """Read a bundle written by :func:`write_project_bundle`."""
    bundle_dir = Path(bundle_dir).expanduser().resolve()
    manifest_path = bundle_dir / MANIFEST_NAME
    if not manifest_path.exists():
        raise FileNotFoundError(
            f"Not a project generation bundle (missing {MANIFEST_NAME}): {bundle_dir}"
        )
    manifest = json.loads(manifest_path.read_text())
    project = manifest.get("project")
    if not project:
        raise ValueError(f"Bundle manifest has no 'project' field: {manifest_path}")
    task = get_project_task(project)
    return LoadedProjectBundle(dir=bundle_dir, manifest=manifest, task=task)
