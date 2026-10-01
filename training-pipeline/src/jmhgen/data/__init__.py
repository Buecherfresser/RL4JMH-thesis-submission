"""Dataset schema, loaders, and the SFT dataset builder (code snippet -> JMH benchmark)."""

from jmhgen.data.build_sft import (
    BuildResult,
    DistilledItem,
    build_dataset,
    build_sample,
    iter_run_locked,
    select_final_source,
)
from jmhgen.data.conformance import (
    CONFORMANCE_TEMPLATES,
    is_conformant,
    violations,
)
from jmhgen.data.schema import (
    BenchmarkSample,
    CodeSnippet,
    load_benchmark_samples,
    load_snippets,
)
from jmhgen.data.scrape_external import (
    JavaClass,
    JavaIndex,
    RepoResult,
    RepoSpec,
    Resolution,
    build_index,
    resolve_target,
    scrape_repo,
)

__all__ = [
    "CONFORMANCE_TEMPLATES",
    "BenchmarkSample",
    "BuildResult",
    "CodeSnippet",
    "DistilledItem",
    "JavaClass",
    "JavaIndex",
    "RepoResult",
    "RepoSpec",
    "Resolution",
    "build_dataset",
    "build_index",
    "build_sample",
    "is_conformant",
    "iter_run_locked",
    "load_benchmark_samples",
    "load_snippets",
    "resolve_target",
    "scrape_repo",
    "select_final_source",
    "violations",
]
