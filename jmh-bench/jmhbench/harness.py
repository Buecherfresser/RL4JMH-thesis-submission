"""Harness adapter interface and registry.

A *harness* is anything that can take a JMH-Bench task and produce a JMH
benchmark source file. This module defines the public protocol that all
adapters (LLM zero-shot, ju2jmh, AutoJMH, custom agents, ...) implement.
"""

from __future__ import annotations

from dataclasses import dataclass, field
from pathlib import Path
from typing import Any, Protocol, runtime_checkable

from pydantic import BaseModel, Field


class RegressionSpec(BaseModel):
    """A single performance-regression mutation applied to the SUT.

    Detection thresholds are intentionally *not* stored per task: regressions
    are judged against a single global rule (see ``RunConfig.min_slowdown`` /
    ``alpha``), because a realistic detector never knows a regression's
    magnitude in advance.
    """

    id: str
    patch: str = Field(description="Path (relative to task dir) to a .patch file")
    description: str | None = None


class Provenance(BaseModel):
    """Real-world attribution for a task whose regression mirrors an OSS commit."""

    project: str
    url: str
    """Canonical URL of the upstream PR / commit / issue."""

    commit: str | None = None
    description: str | None = None


class Task(BaseModel):
    """A JMH-Bench task instance (SWT-Bench-style)."""

    instance_id: str
    target_class: str = Field(description="Fully qualified class name, e.g. bench.ArrayListUtil")
    expected_input_shapes: list[str] = Field(default_factory=list)
    regressions: list[RegressionSpec] = Field(default_factory=list)
    junit_test_path: str | None = None
    tags: list[str] = Field(default_factory=list)
    provenance: Provenance | None = None
    track: str = Field(default="synthetic", description="Dataset track this task belongs to.")

    task_dir: Path = Field(exclude=True)

    @property
    def sut_dir(self) -> Path:
        return self.task_dir / "src" / "main" / "java"

    @property
    def sut_source(self) -> str:
        """Concatenated source of all SUT Java files (handy for LLM prompts)."""
        chunks: list[str] = []
        for p in sorted(self.sut_dir.rglob("*.java")):
            rel = p.relative_to(self.sut_dir)
            chunks.append(f"// ===== {rel} =====\n{p.read_text()}")
        return "\n\n".join(chunks)

    @property
    def junit_test_source(self) -> str | None:
        if not self.junit_test_path:
            return None
        p = self.task_dir / self.junit_test_path
        return p.read_text() if p.exists() else None

    model_config = {"arbitrary_types_allowed": True}


class MutantSpec(BaseModel):
    """One fixed performance mutant in the project mutation track.

    A mutant is a dormant ``MutationSwitch.tick(id)`` call baked into a method of
    the vendored subject-under-test. It is armed at run time via
    ``-Djmhbench.mutant=<id>``; when armed it injects a tiny, fixed latency whose
    operator and magnitude come from ``JmhSettings.mutant_op`` /
    ``mutant_spin_tokens``.
    This registry is the hidden ground truth and is never shown to a harness.
    """

    id: int
    file: str
    fqcn: str
    component: str
    method: str
    signature: str
    line: int


class ProjectTask(BaseModel):
    """A whole-project mutation-track instance (a vendored real-world SUT).

    Unlike :class:`Task` (a tiny synthetic class + its own regression patches),
    a project task points at a large vendored project whose source carries a
    fixed set of independent performance mutants. The harness is judged on what
    fraction of those mutants its generated JMH *suite* detects.

    The attributes a harness reads (``instance_id``, ``task_dir``, ``sut_source``)
    are deliberately duck-typed to match :class:`Task`, so existing adapters
    (dummy-passthrough, predictions, LLM zero-shot) work unchanged. ``sut_source``
    returns the curated public-API digest (``api_surface.md``) rather than the
    whole project, and deliberately never reveals where the mutants live.
    """

    instance_id: str
    display_name: str
    version: str
    source_dir: str = "src/main/java"
    mutations_patch: str = "mutations.patch"
    api_surface_path: str = "api_surface.md"
    dependencies: list[dict[str, str]] = Field(default_factory=list)
    mutation: dict[str, Any] = Field(default_factory=dict)
    harness_input: dict[str, Any] = Field(default_factory=dict)
    """How the SUT is shown to a (source-driven) harness. Keys:

    * ``mode``: ``"per_class"`` (default) feeds the harness one real SUT class at
      a time so it can write a benchmark class per source class; ``"digest"``
      falls back to a single call with :attr:`sut_source` (the API digest).
    * ``include_packages``: component prefixes eligible for per-class input
      (e.g. ``archivers``, ``compressors``, ``utils``, ``parallel``, ``(root)``).
    * ``name_filter``: regex a class's simple name must match to be fed
      (``null``/empty = every public concrete class in scope).
    * ``classes``: explicit FQCN list that overrides discovery.
    * ``max_classes``: hard cap on classes fed (CLI ``--max-classes`` wins).
    * ``max_tokens``: completion-token budget per LLM call (CLI ``-o max_tokens=``
      wins; project-bench default is 16384).
    * ``compile_check``: when true, compile each generated class against the
      pristine SUT during generation and feed Maven errors back to the LLM for
      repair (CLI ``--compile-check`` wins).
    * ``compile_check_retries``: repair attempts per class (default 2).
    * ``compile_filter``: when false, disable bench-time compile-and-filter
      (CLI ``--no-compile-filter`` wins; filtering is on by default).
    * ``runtime_filter``: when false, skip the per-class JMH smoke probe before
      the baseline run (CLI ``--no-runtime-filter`` wins; filtering is on by default)."""

    mutants: list[MutantSpec] = Field(default_factory=list)
    provenance: Provenance | None = None

    # Duck-typed Task-ish fields so harness adapters need no special-casing.
    target_class: str = "org.apache.commons.compress"
    tags: list[str] = Field(default_factory=lambda: ["project", "real-world", "mutation"])
    junit_test_path: None = None
    regressions: list[RegressionSpec] = Field(default_factory=list)
    expected_input_shapes: list[str] = Field(default_factory=list)
    track: str = Field(default="project")

    project_dir: Path = Field(exclude=True)

    @property
    def task_dir(self) -> Path:
        return self.project_dir

    @property
    def src_root(self) -> Path:
        return self.project_dir / self.source_dir

    @property
    def patch_path(self) -> Path:
        return self.project_dir / self.mutations_patch

    @property
    def sut_source(self) -> str:
        """The harness-facing context: the curated public-API digest."""
        return (self.project_dir / self.api_surface_path).read_text()

    @property
    def junit_test_source(self) -> str | None:
        return None

    @property
    def mutant_count(self) -> int:
        return len(self.mutants)

    model_config = {"arbitrary_types_allowed": True}


@dataclass
class RawModelOutput:
    """Full model response for offline analysis (prompt, thinking, final text)."""

    prompt: str
    content: str
    """Model reply before Java extraction (may include markdown fences)."""

    thinking: str | None = None
    """Reasoning / chain-of-thought when the provider exposes it separately."""


@dataclass
class HarnessOutput:
    """What a harness returns: the generated JMH source and any extras."""

    benchmark_source: str
    """Single Java source containing one or more @Benchmark methods. Must include
    a package declaration so the runner can place it on disk correctly."""

    extra_sources: dict[str, str] = field(default_factory=dict)
    """Optional additional Java sources, keyed by 'pkg/Sub/Name.java'."""

    metadata: dict[str, Any] = field(default_factory=dict)
    """Free-form harness metadata (tokens used, latency, etc.) recorded in
    the run report."""

    raw_output: RawModelOutput | None = None
    """When set, the report writer persists this as a per-task Markdown file."""


@runtime_checkable
class Harness(Protocol):
    """Anything implementing this protocol can be plugged in as a harness."""

    name: str

    def generate(self, task: Task, workdir: Path) -> HarnessOutput:
        """Produce a JMH benchmark for *task*.

        *workdir* is a per-task scratch directory the harness may use as it
        pleases (e.g. for caching intermediate artefacts). The runner ignores
        its contents.
        """
        ...


# ---------------------------------------------------------------------------
# Registry: loads harness classes from setuptools entry points.
# ---------------------------------------------------------------------------


_ENTRY_POINT_GROUP = "jmhbench.harnesses"


def discover_harnesses() -> dict[str, type[Harness]]:
    """Return a {name: class} map of all harnesses registered as entry points."""
    from importlib.metadata import entry_points

    out: dict[str, type[Harness]] = {}
    eps = entry_points().select(group=_ENTRY_POINT_GROUP)
    for ep in eps:
        try:
            out[ep.name] = ep.load()
        except Exception as exc:  # pragma: no cover - tolerate broken extras
            out[ep.name] = _BrokenHarness(ep.name, str(exc))  # type: ignore[assignment]
    return out


def load_harness(harness_name: str, /, **kwargs: Any) -> Harness:
    """Instantiate the harness registered under *harness_name*.

    *harness_name* is positional-only so kwargs like ``name=...`` can be
    forwarded to the harness constructor without collision.
    """
    harnesses = discover_harnesses()
    if harness_name not in harnesses:
        available = ", ".join(sorted(harnesses)) or "<none>"
        raise KeyError(f"No harness registered as '{harness_name}'. Available: {available}")
    cls = harnesses[harness_name]
    return cls(**kwargs)  # type: ignore[call-arg]


class _BrokenHarness:
    """Placeholder so `jmhbench list-harnesses` still works when an extra is missing."""

    def __init__(self, name: str, error: str) -> None:
        self.name = name
        self._error = error

    def generate(self, task: Task, workdir: Path) -> HarnessOutput:  # noqa: D401
        raise RuntimeError(
            f"Harness '{self.name}' failed to load: {self._error}. "
            f"Install the relevant extra (e.g. `pip install jmhbench[openai]`)."
        )
