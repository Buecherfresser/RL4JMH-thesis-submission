"""Coverage recording for generated JMH benchmarks with planted performance mutants."""

from __future__ import annotations

import dataclasses
import re
import tempfile
from collections import defaultdict
from pathlib import Path

from jmhgen.data.conformance import _benchmark_methods
from jmhgen.runner.base import JmhRunner
from jmhgen.runner.types import BenchmarkSpec, JmhOptions


def benchmark_method_names(source: str) -> list[str]:
    """Return generated ``@Benchmark`` method names in their source order."""
    return [method.name for method in _benchmark_methods(source)]


def benchmark_method_filter(spec: BenchmarkSpec, method: str) -> str:
    """Build an exact JMH include regex for one generated benchmark method."""
    return re.escape(f"{spec.fully_qualified_name}.{method}") + "$"


def read_record_file(path: Path) -> dict[int, int]:
    """Parse MutationSwitch's bounded ``<mutant-id> <hit-count>`` output."""
    if not path.exists():
        return {}
    hits: dict[int, int] = {}
    for raw_line in path.read_text(encoding="utf-8", errors="replace").splitlines():
        fields = raw_line.split()
        if not fields:
            continue
        try:
            mutant_id = int(fields[0])
            count = int(fields[1]) if len(fields) > 1 else 1
        except ValueError:
            continue
        if mutant_id > 0 and count > 0:
            hits[mutant_id] = count
    return hits


def record_coverage(
    runner: JmhRunner,
    spec: BenchmarkSpec,
    options: JmhOptions,
    *,
    record_property: str,
    record_file_property: str,
) -> dict[int, list[tuple[str, int]]]:
    """Record which individual benchmark methods reach each planted mutant.

    The switch records process-global counts, so every ``@Benchmark`` method runs in its own
    short JMH process. The returned list for each mutant is ranked by hit count descending and
    method name ascending, matching JMH-Bench's deterministic selection policy.
    """
    covered_by: dict[int, list[tuple[str, int]]] = defaultdict(list)
    with tempfile.TemporaryDirectory(prefix="jmhgen-coverage-") as temp_dir:
        directory = Path(temp_dir)
        for index, method in enumerate(benchmark_method_names(spec.source)):
            record_file = directory / f"coverage-{index}.txt"
            record_args = (
                *options.jvm_args,
                f"-D{record_property}=true",
                f"-D{record_file_property}={record_file}",
            )
            record_options = dataclasses.replace(
                options,
                jvm_args=record_args,
                benchmark_filter=benchmark_method_filter(spec, method),
            )
            runner.run(spec, record_options)
            for mutant_id, hits in read_record_file(record_file).items():
                covered_by[mutant_id].append((method, hits))
    return {
        mutant_id: sorted(methods, key=lambda item: (-item[1], item[0]))
        for mutant_id, methods in covered_by.items()
    }
