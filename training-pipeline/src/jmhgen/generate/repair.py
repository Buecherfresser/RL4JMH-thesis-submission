"""Deterministic repairs for the mechanical defects that dominate GRPO compile failures.

Measured over 152 failing run-2 rollouts (985 javac diagnostics), the leading *root causes*
were not API knowledge gaps but three fixed, mechanical mistakes:

    11.5 %  `an enum annotation value must be an enum constant`
            -> @OutputTimeUnit(TimeUnit.…) with no `import java.util.concurrent.TimeUnit;`
     ~9 %   `non-static method consume(...) cannot be referenced from a static context`
            -> `Blackhole.consume(x)` written against the type instead of the injected instance
     ~7 %   `illegal character: '`'` and friends
            -> markdown fence remnants left in the extracted source

None of these is the skill the reward is meant to teach (writing a mutation-sensitive benchmark
against an unfamiliar API), and each is a guaranteed zero that also flattens the group-relative
advantage. Repairing them turns those rollouts back into signal.

The repairs are deliberately conservative: each is a no-op unless its exact defect signature is
present, and none of them invents an API call on the subject under test. Applied on the GRPO
reward path only (``repair_completions``), so the RFT/eval scoring path stays byte-identical and
JMH-Bench numbers remain comparable across runs.
"""

from __future__ import annotations

import re
from dataclasses import dataclass

# Fully-qualified names for the symbols we know how to import, keyed by the bare name that has
# to appear in the source before we add anything.
_KNOWN_IMPORTS: dict[str, str] = {
    "TimeUnit": "java.util.concurrent.TimeUnit",
    "Blackhole": "org.openjdk.jmh.infra.Blackhole",
    "Control": "org.openjdk.jmh.infra.Control",
}
# The JMH annotations live behind one wildcard; adding it is safer than guessing which of
# @State/@Fork/@Warmup/... the model actually used.
_JMH_ANNOTATIONS_IMPORT = "org.openjdk.jmh.annotations.*"
_JMH_ANNOTATION_RE = re.compile(
    r"@(?:State|Benchmark|BenchmarkMode|Fork|Warmup|Measurement|OutputTimeUnit|Setup|TearDown"
    r"|Param|Group|GroupThreads|Threads|CompilerControl)\b"
)

_PACKAGE_RE = re.compile(r"^\s*package\s+[\w.]+\s*;[ \t]*$", re.MULTILINE)
_IMPORT_RE = re.compile(r"^\s*import\s+(?:static\s+)?([\w.*]+)\s*;", re.MULTILINE)
_FENCE_LINE_RE = re.compile(r"^[ \t]*```[A-Za-z]*[ \t]*$", re.MULTILINE)
# `Blackhole.consume(...)` / `Blackhole.consumeCPU(...)` called on the type. Only rewritten when
# the enclosing file actually has a Blackhole parameter to call it on.
_STATIC_BLACKHOLE_RE = re.compile(r"\bBlackhole\s*\.\s*(consume)\s*\(")
_BLACKHOLE_PARAM_RE = re.compile(r"\bBlackhole\s+(\w+)\s*[,)]")
# A @Benchmark method signature: the annotation, any further annotations, modifiers, a return
# type, the name, and the parameter list we may need to extend.
_BENCHMARK_METHOD_RE = re.compile(
    r"@Benchmark\b\s*"
    r"(?:@\w+(?:\([^)]*\))?\s*)*"
    r"(?:public|protected|private)?\s*(?:static\s+|final\s+)*"
    r"[\w.$]+(?:\s*<[^{;]*?>)?(?:\s*\[\s*\])*\s+"
    r"\w+\s*\((?P<params>[^)]*)\)"
)


@dataclass(frozen=True, slots=True)
class RepairReport:
    """What :func:`repair_java_source` changed, for diagnostics."""

    source: str
    applied: tuple[str, ...]

    @property
    def changed(self) -> bool:
        return bool(self.applied)


def _strip_fence_lines(source: str) -> tuple[str, bool]:
    """Drop stray ``` lines the extractor left behind (they are never valid Java)."""
    if "```" not in source:
        return source, False
    cleaned = _FENCE_LINE_RE.sub("", source)
    # An inline fence remnant (not on its own line) means the block is genuinely mangled; leave
    # it alone rather than guessing where the Java ends.
    return cleaned, cleaned != source


def _add_missing_imports(source: str) -> tuple[str, list[str]]:
    """Insert the standard JMH imports the file uses but never imported."""
    imported = set(_IMPORT_RE.findall(source))
    wanted: list[str] = []

    if _JMH_ANNOTATION_RE.search(source) and not any(
        i == _JMH_ANNOTATIONS_IMPORT or i.startswith("org.openjdk.jmh.annotations.")
        for i in imported
    ):
        wanted.append(_JMH_ANNOTATIONS_IMPORT)

    for bare, fqn in _KNOWN_IMPORTS.items():
        if not re.search(rf"\b{bare}\b", source):
            continue
        if fqn in imported:
            continue
        # A wildcard on the same package already covers it.
        package = fqn.rsplit(".", 1)[0]
        if f"{package}.*" in imported:
            continue
        # The model may have declared its own type with that name; do not shadow it.
        if re.search(rf"\b(?:class|interface|enum|record)\s+{bare}\b", source):
            continue
        wanted.append(fqn)

    if not wanted:
        return source, []

    block = "".join(f"import {fqn};\n" for fqn in wanted)
    existing = list(_IMPORT_RE.finditer(source))
    if existing:
        at = existing[0].start()
    else:
        package = _PACKAGE_RE.search(source)
        at = package.end() + 1 if package else 0
        block = ("\n" if package else "") + block + "\n"
    return source[:at] + block + source[at:], wanted


def _fix_static_blackhole(source: str) -> tuple[str, bool]:
    """Rewrite ``Blackhole.consume(x)`` to the injected instance, when there is one."""
    if not _STATIC_BLACKHOLE_RE.search(source):
        return source, False
    param = _BLACKHOLE_PARAM_RE.search(source)
    if param is None:
        return source, False
    name = param.group(1)
    fixed = _STATIC_BLACKHOLE_RE.sub(rf"{name}.\1(", source)
    return fixed, fixed != source


def _method_body_span(source: str, open_brace: int) -> int | None:
    """Return the offset just past the ``}`` closing the block that opens at ``open_brace``."""
    depth = 0
    for i in range(open_brace, len(source)):
        char = source[i]
        if char == "{":
            depth += 1
        elif char == "}":
            depth -= 1
            if depth == 0:
                return i + 1
    return None


def _inject_blackhole_param(source: str) -> tuple[str, bool]:
    """Give a ``@Benchmark`` method the ``Blackhole`` parameter its body already assumes.

    Handles the residue of :func:`_fix_static_blackhole`: the model calls ``Blackhole.consume``
    from a method that has no Blackhole to call it on, so there is nothing to rewrite onto.
    JMH injects a ``Blackhole`` into any ``@Benchmark`` method that declares one, so adding the
    parameter is the documented fix rather than a workaround. Measured on 147 failing run-2
    rollouts, 9 compiled with nothing else changed.

    Only rewrites a method whose body actually contains the static call, and never touches a
    method that already has a Blackhole parameter.
    """
    changed = False
    # Work backwards so earlier offsets stay valid as we splice.
    for match in reversed(list(_BENCHMARK_METHOD_RE.finditer(source))):
        params = match.group("params")
        if "Blackhole" in params:
            continue
        brace = source.find("{", match.end())
        if brace == -1:
            continue
        end = _method_body_span(source, brace)
        if end is None:
            continue
        body = source[brace:end]
        if not _STATIC_BLACKHOLE_RE.search(body):
            continue
        name = "bh" if not re.search(r"\bbh\b", body) else "jmhBlackhole"
        new_params = f"{params.strip()}, Blackhole {name}" if params.strip() else f"Blackhole {name}"
        new_body = _STATIC_BLACKHOLE_RE.sub(rf"{name}.\1(", body)
        source = (
            source[: match.start("params")]
            + new_params
            + source[match.end("params") : brace]
            + new_body
            + source[end:]
        )
        changed = True
    return source, changed


# WITHDRAWN: a bare-@State repair (@State -> @State(Scope.Thread)) shipped in run 3 and was
# actively destructive. Measured over 8341 run-3 rollouts: it fired on 45 % of them, and those
# compiled at 3.5 % against 31.9 % for rollouts it did not touch, with 89.7 % of them hitting
# "annotation interface not applicable to this kind of declaration" -- it was pasting a
# type-only annotation onto non-type declarations. The offline check on 239 saved candidates
# missed this because only 19 triggered it and all 19 were already failing for other reasons.
# Lesson: validate a repair on the rate it FIRES, not only on the conversions it wins.


def repair_java_source(source: str) -> RepairReport:
    """Apply every repair whose defect signature is present; otherwise return ``source`` as-is."""
    applied: list[str] = []

    source, stripped = _strip_fence_lines(source)
    if stripped:
        applied.append("fence_lines")

    source, added = _add_missing_imports(source)
    if added:
        applied.append("imports:" + ",".join(added))

    source, blackholed = _fix_static_blackhole(source)
    if blackholed:
        applied.append("static_blackhole")

    source, injected = _inject_blackhole_param(source)
    if injected:
        applied.append("blackhole_param")
        # The injected parameter needs the import even if nothing else did.
        source, added_after = _add_missing_imports(source)
        if added_after:
            applied.append("imports:" + ",".join(added_after))

    return RepairReport(source=source, applied=tuple(applied))
