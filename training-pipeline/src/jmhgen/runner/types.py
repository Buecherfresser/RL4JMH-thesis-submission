"""Typed, frozen, JSON-serializable value objects exchanged with the JMH runner.

These types are intentionally free of any runtime dependency on a concrete backend
(Maven, javac, ...) or on *where* the runner executes (in-process vs. a remote CPU
fleet). Keeping them as plain, immutable, JSON-serializable dataclasses is what makes
a reward evaluation a relocatable unit of work: it can be logged, replayed, and shipped
across a process or machine boundary unchanged.
"""

from __future__ import annotations

import dataclasses
from dataclasses import dataclass, field
from enum import Enum, StrEnum
from typing import Any


class ErrorKind(StrEnum):
    """Coarse classification of why a compile/run step did not succeed."""

    NONE = "none"
    COMPILE_ERROR = "compile_error"
    RUNTIME_ERROR = "runtime_error"
    NOT_COMPILED = "not_compiled"
    TIMEOUT = "timeout"
    TOOLCHAIN_MISSING = "toolchain_missing"
    INTERNAL = "internal"


def _to_jsonable(value: Any) -> Any:
    """Recursively convert dataclasses/enums/tuples into JSON-friendly primitives."""
    if dataclasses.is_dataclass(value) and not isinstance(value, type):
        return {f.name: _to_jsonable(getattr(value, f.name)) for f in dataclasses.fields(value)}
    if isinstance(value, Enum):
        return value.value
    if isinstance(value, (list, tuple)):
        return [_to_jsonable(v) for v in value]
    if isinstance(value, dict):
        return {k: _to_jsonable(v) for k, v in value.items()}
    return value


@dataclass(frozen=True, slots=True)
class BenchmarkSpec:
    """A self-contained description of a benchmark to compile and run.

    Attributes:
        source: Full Java source of the generated benchmark file.
        class_name: Simple class name of the public benchmark class (e.g. ``MyBenchmark``).
        package: Java package of the benchmark (``""`` for the default package).
        extra_classpath: Absolute paths (jars or class directories) for the
            code-under-test and its dependencies. Added to the compile classpath and,
            at run time, appended to the JMH launch classpath.
        project_id: Optional identifier of a pre-provisioned project on a remote worker.
            Lets a future ``RemoteRunnerClient`` reference a baked-in classpath instead of
            shipping the whole project with every reward request.
    """

    source: str
    class_name: str
    package: str = ""
    extra_classpath: tuple[str, ...] = ()
    project_id: str | None = None

    @property
    def fully_qualified_name(self) -> str:
        return f"{self.package}.{self.class_name}" if self.package else self.class_name

    def to_dict(self) -> dict[str, Any]:
        return _to_jsonable(self)

    @classmethod
    def from_dict(cls, data: dict[str, Any]) -> BenchmarkSpec:
        return cls(
            source=data["source"],
            class_name=data["class_name"],
            package=data.get("package", ""),
            extra_classpath=tuple(data.get("extra_classpath", ())),
            project_id=data.get("project_id"),
        )


@dataclass(frozen=True, slots=True)
class JmhOptions:
    """JMH execution parameters.

    ``timeout_s`` is the overall wall-clock budget for the *launch* (enforced by the
    subprocess wrapper); ``per_iteration_timeout_s`` maps to JMH's own ``-to`` flag.
    Defaults are deliberately small so reward evaluation stays cheap during training;
    faithful evaluation runs should override them via config.
    """

    warmup_iterations: int = 3
    measurement_iterations: int = 5
    forks: int = 1
    warmup_time: str = "1s"
    measurement_time: str = "1s"
    timeout_s: float = 600.0
    per_iteration_timeout_s: float | None = None
    fail_on_error: bool = True
    jvm_args: tuple[str, ...] = ()
    benchmark_filter: str | None = None

    def to_dict(self) -> dict[str, Any]:
        return _to_jsonable(self)

    @classmethod
    def from_dict(cls, data: dict[str, Any]) -> JmhOptions:
        known = {f.name for f in dataclasses.fields(cls)}
        kwargs = {k: v for k, v in data.items() if k in known}
        if "jvm_args" in kwargs:
            kwargs["jvm_args"] = tuple(kwargs["jvm_args"])
        return cls(**kwargs)


@dataclass(frozen=True, slots=True)
class CompileResult:
    """Outcome of compiling (and packaging) a benchmark."""

    success: bool
    duration_s: float
    stdout: str = ""
    stderr: str = ""
    artifact_path: str | None = None
    project_dir: str | None = None
    error_kind: ErrorKind = ErrorKind.NONE

    def to_dict(self) -> dict[str, Any]:
        return _to_jsonable(self)

    @classmethod
    def from_dict(cls, data: dict[str, Any]) -> CompileResult:
        return cls(
            success=data["success"],
            duration_s=data["duration_s"],
            stdout=data.get("stdout", ""),
            stderr=data.get("stderr", ""),
            artifact_path=data.get("artifact_path"),
            project_dir=data.get("project_dir"),
            error_kind=ErrorKind(data.get("error_kind", ErrorKind.NONE.value)),
        )


@dataclass(frozen=True, slots=True)
class BenchmarkStat:
    """Parsed primary metric for a single benchmark method."""

    benchmark: str
    mode: str
    score: float
    score_error: float
    unit: str
    raw_measurements: tuple[float, ...] = ()
    robust_rsd: float | None = None

    def to_dict(self) -> dict[str, Any]:
        return _to_jsonable(self)

    @classmethod
    def from_dict(cls, data: dict[str, Any]) -> BenchmarkStat:
        return cls(
            benchmark=data["benchmark"],
            mode=data["mode"],
            score=data["score"],
            score_error=data["score_error"],
            unit=data["unit"],
            raw_measurements=tuple(data.get("raw_measurements", ())),
            robust_rsd=data.get("robust_rsd"),
        )


@dataclass(frozen=True, slots=True)
class RunResult:
    """Outcome of executing a compiled benchmark via JMH."""

    success: bool
    duration_s: float
    stats: tuple[BenchmarkStat, ...] = ()
    stdout: str = ""
    stderr: str = ""
    raw_json: str | None = None
    error_kind: ErrorKind = ErrorKind.NONE

    def to_dict(self) -> dict[str, Any]:
        return _to_jsonable(self)

    @classmethod
    def from_dict(cls, data: dict[str, Any]) -> RunResult:
        return cls(
            success=data["success"],
            duration_s=data["duration_s"],
            stats=tuple(BenchmarkStat.from_dict(s) for s in data.get("stats", ())),
            stdout=data.get("stdout", ""),
            stderr=data.get("stderr", ""),
            raw_json=data.get("raw_json"),
            error_kind=ErrorKind(data.get("error_kind", ErrorKind.NONE.value)),
        )


@dataclass(frozen=True, slots=True)
class EvaluationResult:
    """Bundle of a compile step and an optional run step for one benchmark spec."""

    compile: CompileResult
    run: RunResult | None = None
    metadata: dict[str, Any] = field(default_factory=dict)

    @property
    def compiled(self) -> bool:
        return self.compile.success

    @property
    def ran(self) -> bool:
        return self.run is not None and self.run.success

    def to_dict(self) -> dict[str, Any]:
        return _to_jsonable(self)

    @classmethod
    def from_dict(cls, data: dict[str, Any]) -> EvaluationResult:
        run = data.get("run")
        return cls(
            compile=CompileResult.from_dict(data["compile"]),
            run=RunResult.from_dict(run) if run is not None else None,
            metadata=dict(data.get("metadata", {})),
        )
