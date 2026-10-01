"""Maven-backed :class:`JmhRunner` — the faithful baseline backend.

It drops the generated benchmark into a templated Maven project, builds the canonical JMH
uber-jar with the shade plugin (this is exactly how real JMH projects build), and executes
it. This mirrors LLM4JMH's toolchain and maximizes reproducibility at the cost of per-call
overhead.

A lightweight ``javac`` + ``java org.openjdk.jmh.Main`` backend (much lower latency, better
for high-volume training rewards) is a planned sibling implementation of the same
:class:`JmhRunner` protocol; the throughput-vs-fidelity trade-off is studied in the thesis.
"""

from __future__ import annotations

import hashlib
import os
import re
import shutil
import tempfile
from importlib.resources import files
from pathlib import Path

import jinja2

from jmhgen.runner.results import parse_jmh_json
from jmhgen.runner.types import (
    BenchmarkSpec,
    CompileResult,
    ErrorKind,
    JmhOptions,
    RunResult,
)
from jmhgen.utils.logging import get_logger
from jmhgen.utils.subprocess import run_command

logger = get_logger(__name__)

_UBERJAR_NAME = "benchmarks"
_JMH_MAIN_CLASS = "org.openjdk.jmh.Main"
_NON_ALNUM = re.compile(r"[^A-Za-z0-9]+")


class MavenJmhRunner:
    """Compile and execute JMH benchmarks through Apache Maven.

    Working directories are derived deterministically from the benchmark content, so a
    :meth:`compile` followed by :meth:`run` for the same spec reuses the built artifact
    instead of packaging twice.
    """

    def __init__(
        self,
        *,
        jmh_version: str = "1.37",
        java_release: int = 17,
        mvn_executable: str = "mvn",
        java_executable: str = "java",
        compiler_plugin_version: str = "3.13.0",
        shade_plugin_version: str = "3.6.0",
        group_id: str = "jmhgen.generated",
        work_root: str | Path | None = None,
        keep_work_dir: bool = False,
    ) -> None:
        self.jmh_version = jmh_version
        self.java_release = java_release
        self.mvn_executable = mvn_executable
        self.java_executable = java_executable
        self.compiler_plugin_version = compiler_plugin_version
        self.shade_plugin_version = shade_plugin_version
        self.group_id = group_id
        default_root = Path(tempfile.gettempdir()) / "jmhgen_work"
        self.work_root = Path(work_root) if work_root else default_root
        self.keep_work_dir = keep_work_dir
        self._template = jinja2.Template(
            files("jmhgen.runner").joinpath("templates/pom.xml.j2").read_text(encoding="utf-8"),
            autoescape=False,
            keep_trailing_newline=True,
        )

    # -- availability ---------------------------------------------------------------

    def is_available(self) -> bool:
        """Whether both the Maven and Java executables are resolvable on ``PATH``."""
        return (
            shutil.which(self.mvn_executable) is not None
            and shutil.which(self.java_executable) is not None
        )

    # -- public API -----------------------------------------------------------------

    def compile(self, spec: BenchmarkSpec) -> CompileResult:
        if not self.is_available():
            return CompileResult(
                success=False,
                duration_s=0.0,
                stderr=f"toolchain missing: need '{self.mvn_executable}' and "
                f"'{self.java_executable}' on PATH",
                error_kind=ErrorKind.TOOLCHAIN_MISSING,
            )

        project = self._prepare_project(spec)
        cmd = [self.mvn_executable, "-B", "-ntp", "clean", "package", "-DskipTests"]
        proc = run_command(cmd, cwd=project, timeout_s=_compile_timeout(spec))
        jar = self._artifact_path(project)

        if proc.timed_out:
            return CompileResult(
                success=False,
                duration_s=proc.duration_s,
                stdout=proc.stdout,
                stderr=proc.stderr,
                error_kind=ErrorKind.TIMEOUT,
            )

        success = proc.returncode == 0 and jar.exists()
        return CompileResult(
            success=success,
            duration_s=proc.duration_s,
            stdout=proc.stdout,
            stderr=proc.stderr,
            artifact_path=str(jar) if jar.exists() else None,
            project_dir=str(project),
            error_kind=ErrorKind.NONE if success else ErrorKind.COMPILE_ERROR,
        )

    def run(self, spec: BenchmarkSpec, options: JmhOptions) -> RunResult:
        if not self.is_available():
            return RunResult(
                success=False,
                duration_s=0.0,
                stderr="toolchain missing",
                error_kind=ErrorKind.TOOLCHAIN_MISSING,
            )

        project = self._prepare_project(spec)
        jar = self._artifact_path(project)
        if not jar.exists():
            compiled = self.compile(spec)
            if not compiled.success:
                return RunResult(
                    success=False,
                    duration_s=compiled.duration_s,
                    stdout=compiled.stdout,
                    stderr=compiled.stderr,
                    error_kind=ErrorKind.NOT_COMPILED,
                )

        results_file = project / "jmh-result.json"
        # JMH takes a global lock at ``$java.io.tmpdir/jmh.lock`` and, when it cannot acquire it,
        # ABORTS -- it does not queue:
        #
        #   RunnerException: Another JMH instance might be running. Unable to acquire the JMH
        #   lock (/tmp/jmh.lock), exiting.
        #
        # Java ignores $TMPDIR on Linux, so without this every JMH process on the host (both
        # training ranks, plus any concurrent eval) shared /tmp and one of each overlapping pair
        # died instantly. In run 7 that is the signature of 1003 rollouts -- 40.7 % of everything
        # that compiled -- recorded as ``runtime_error`` with a duration clamped to 3.9-5.5 s,
        # and the rate rose with the compile rate rather than falling. Those are compilable
        # benchmarks scored 0 on runtime, rsd and mutation: a false negative in the reward.
        #
        # A private directory per invocation (not per project dir: two identical completions in
        # one group hash to the same project) gives every JMH its own uncontended lock. Measured
        # on rtx-jonas: two concurrent JMH runs sharing /tmp -> one fails in 0.10 s; with separate
        # java.io.tmpdir -> 1.9 s and 2.0 s against a 2.0 s solo baseline, i.e. full parallelism.
        jmh_tmp = Path(tempfile.mkdtemp(prefix="jmhtmp-", dir=project))
        try:
            cmd = self._build_run_command(spec, options, jar, results_file, jmh_tmp)
            proc = run_command(cmd, cwd=project, timeout_s=options.timeout_s)
        finally:
            shutil.rmtree(jmh_tmp, ignore_errors=True)

        if proc.timed_out:
            return RunResult(
                success=False,
                duration_s=proc.duration_s,
                stdout=proc.stdout,
                stderr=proc.stderr,
                error_kind=ErrorKind.TIMEOUT,
            )

        raw_json = results_file.read_text(encoding="utf-8") if results_file.exists() else None
        stats: tuple = ()
        if raw_json:
            try:
                stats = parse_jmh_json(raw_json)
            except ValueError as exc:
                logger.warning("failed to parse JMH results: %s", exc)

        success = proc.returncode == 0 and len(stats) > 0
        return RunResult(
            success=success,
            duration_s=proc.duration_s,
            stats=stats,
            stdout=proc.stdout,
            stderr=proc.stderr,
            raw_json=raw_json,
            error_kind=ErrorKind.NONE if success else ErrorKind.RUNTIME_ERROR,
        )

    # -- internals ------------------------------------------------------------------

    def _project_dir(self, spec: BenchmarkSpec) -> Path:
        digest = hashlib.sha1(self._fingerprint(spec).encode("utf-8")).hexdigest()[:16]
        return self.work_root / f"bench-{digest}"

    def _fingerprint(self, spec: BenchmarkSpec) -> str:
        config = (
            self.jmh_version,
            self.java_release,
            self.compiler_plugin_version,
            self.shade_plugin_version,
            self.group_id,
        )
        return "\u0000".join(
            (spec.fully_qualified_name, spec.source, *spec.extra_classpath, *map(str, config))
        )

    def _artifact_path(self, project: Path) -> Path:
        return project / "target" / f"{_UBERJAR_NAME}.jar"

    def _prepare_project(self, spec: BenchmarkSpec) -> Path:
        project = self._project_dir(spec)
        src_dir = project / "src" / "main" / "java"
        if spec.package:
            src_dir = src_dir / Path(*spec.package.split("."))
        src_dir.mkdir(parents=True, exist_ok=True)

        (project / "pom.xml").write_text(self._render_pom(spec), encoding="utf-8")
        (src_dir / f"{spec.class_name}.java").write_text(spec.source, encoding="utf-8")
        return project

    def _render_pom(self, spec: BenchmarkSpec) -> str:
        system_dependencies = []
        for i, path in enumerate(spec.extra_classpath):
            stem = _NON_ALNUM.sub("-", Path(path).stem).strip("-") or "dep"
            system_dependencies.append(
                {
                    "group_id": "jmhgen.local",
                    "artifact_id": f"cut-{i}-{stem}",
                    "version": "1.0",
                    "path": str(Path(path).resolve()),
                }
            )
        return self._template.render(
            group_id=self.group_id,
            artifact_id=spec.class_name.lower(),
            project_version="1.0",
            java_release=self.java_release,
            jmh_version=self.jmh_version,
            uberjar_name=_UBERJAR_NAME,
            compiler_plugin_version=self.compiler_plugin_version,
            shade_plugin_version=self.shade_plugin_version,
            system_dependencies=system_dependencies,
        )

    def _build_run_command(
        self,
        spec: BenchmarkSpec,
        options: JmhOptions,
        jar: Path,
        results_file: Path,
        jmh_tmp: Path | None = None,
    ) -> list[str]:
        classpath = os.pathsep.join([str(jar), *spec.extra_classpath])
        benchmark_filter = options.benchmark_filter or spec.fully_qualified_name
        cmd = [
            self.java_executable,
            # Must precede the main class. See run() for why this is load-bearing.
            *([f"-Djava.io.tmpdir={jmh_tmp}"] if jmh_tmp is not None else []),
            "-cp",
            classpath,
            _JMH_MAIN_CLASS,
            benchmark_filter,
            "-rf",
            "json",
            "-rff",
            str(results_file),
            "-wi",
            str(options.warmup_iterations),
            "-i",
            str(options.measurement_iterations),
            "-f",
            str(options.forks),
            "-w",
            options.warmup_time,
            "-r",
            options.measurement_time,
            "-foe",
            "true" if options.fail_on_error else "false",
        ]
        if options.per_iteration_timeout_s is not None:
            cmd += ["-to", f"{options.per_iteration_timeout_s:g}s"]
        if options.jvm_args:
            cmd += ["-jvmArgsAppend", " ".join(options.jvm_args)]
        return cmd

    def cleanup(self, spec: BenchmarkSpec) -> None:
        """Remove the on-disk working directory for ``spec`` (unless ``keep_work_dir``)."""
        if self.keep_work_dir:
            return
        shutil.rmtree(self._project_dir(spec), ignore_errors=True)


def _compile_timeout(spec: BenchmarkSpec) -> float:
    """Generous default budget for a Maven package (first run may download dependencies)."""
    return 900.0
