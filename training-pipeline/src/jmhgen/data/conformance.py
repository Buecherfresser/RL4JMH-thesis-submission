"""Static conformance checks tying a benchmark target to the ``jmhbench`` prompt rules.

The point of this module is the constraint *"there must be no difference between the prompt
we train on and the output we train on"*: if the system prompt asserts a hard rule, every
assistant target rendered under that prompt must satisfy it, otherwise we are teaching the
model to contradict its own instructions.

The rules are **work in progress** (see :data:`jmhgen.data.prompts.JMHBENCH_SYSTEM`). They
are encoded here as one small, independent function per rule so they are cheap to tweak,
drop, or add as the spec firms up. Each check returns ``None`` when the target satisfies the
rule, or a short machine-readable reason string when it does not. :func:`violations`
aggregates them; the dataset renderer uses it to keep the rendered SFT view consistent with
the current rule set (the full, prompt-agnostic ``records.jsonl`` is never pruned).

The checks are deliberately *static* (regex over the source). They are sound enough to catch
the structural mismatches that matter for prompt/output consistency (missing JMH config,
unused results, non-injectable parameters, never naming the subject); the semantic
anti-patterns that need real dataflow analysis (LOOP / FINAL / INVO) are intentionally left
out for now and documented as a follow-up rather than approximated unreliably.
"""

from __future__ import annotations

import re
from collections.abc import Callable

from jmhgen.data.schema import BenchmarkSample

# Templates whose prompt carries hard rules that the rendered targets must satisfy. The
# dataset renderer consults this to decide whether to gate the rendered view on conformance.
CONFORMANCE_TEMPLATES: frozenset[str] = frozenset({"jmhbench"})

# Class-level JMH configuration annotations the prompt requires to be present (values are
# free; only presence is checked). Keep this list in sync with JMHBENCH_SYSTEM.
REQUIRED_CONFIG_ANNOTATIONS: tuple[str, ...] = (
    "State",
    "BenchmarkMode",
    "Fork",
    "Warmup",
    "Measurement",
    "OutputTimeUnit",
)

# Parameter types JMH can inject into a @Benchmark method besides a @State object.
JMH_INFRA_PARAM_TYPES: frozenset[str] = frozenset(
    {"Blackhole", "Control", "BenchmarkParams", "ThreadParams", "IterationParams"}
)


def _has_annotation(source: str, name: str) -> bool:
    return re.search(r"@" + name + r"\b", source) is not None


def _simple_name(fqcn: str) -> str:
    return fqcn.rsplit(".", 1)[-1]


def _state_type_names(source: str) -> set[str]:
    """Names of classes annotated ``@State`` in ``source`` (injectable as parameters)."""
    names: set[str] = set()
    for match in re.finditer(r"@State\b", source):
        decl = re.search(r"\bclass\s+(\w+)", source[match.end() : match.end() + 200])
        if decl:
            names.add(decl.group(1))
    return names


def _parameter_type(param: str) -> str:
    """Best-effort extraction of a parameter's type token (drops modifiers/annotations)."""
    cleaned = re.sub(r"^\s*(final\s+|@\w+(\s*\([^)]*\))?\s+)+", "", param.strip())
    cleaned = re.sub(r"<[^>]*>", "", cleaned)
    tokens = cleaned.split()
    if len(tokens) >= 2:
        return tokens[-2]
    return tokens[0] if tokens else ""


class _BenchmarkMethod:
    __slots__ = ("return_type", "name", "params")

    def __init__(self, return_type: str, name: str, params: list[str]) -> None:
        self.return_type = return_type
        self.name = name
        self.params = params


def _benchmark_methods(source: str) -> list[_BenchmarkMethod]:
    """Parse ``@Benchmark`` method signatures (return type, name, parameter list)."""
    methods: list[_BenchmarkMethod] = []
    for marker in re.finditer(r"@Benchmark\b", source):
        idx = marker.end()
        # Skip any method-level annotations (possibly with arguments) before the signature.
        while True:
            ann = re.match(r"\s*@\w+(\s*\([^)]*\))?", source[idx:])
            if not ann:
                break
            idx += ann.end()
        sig = re.match(
            r"\s*((?:public|protected|private|final|static|synchronized|abstract|strictfp)\s+)*"
            r"([\w.$<>\[\],\s\?]+?)\s+(\w+)\s*\(([^)]*)\)",
            source[idx:],
        )
        if not sig:
            continue
        params = [p.strip() for p in sig.group(4).split(",") if p.strip()]
        methods.append(_BenchmarkMethod(sig.group(2).strip(), sig.group(3), params))
    return methods


# --- individual rules -------------------------------------------------------------------
# Each takes the benchmark source and the subject's simple class name, and returns a reason
# string when the rule is violated, else None.


def _rule_jmh_config(source: str, sut: str) -> str | None:
    missing = [a for a in REQUIRED_CONFIG_ANNOTATIONS if not _has_annotation(source, a)]
    return f"missing_jmh_config:{','.join(missing)}" if missing else None


def _rule_has_benchmark(source: str, sut: str) -> str | None:
    return None if _benchmark_methods(source) else "no_benchmark_method"


def _rule_result_consumed(source: str, sut: str) -> str | None:
    """RETU: a result must be returned, or a void benchmark must take a ``Blackhole``."""
    for method in _benchmark_methods(source):
        if method.return_type == "void" and not any(
            _parameter_type(p) == "Blackhole" for p in method.params
        ):
            return "result_unused"
    return None


def _rule_injectable_params(source: str, sut: str) -> str | None:
    """@Benchmark parameters must be JMH-injectable (Blackhole / infra / a @State object)."""
    allowed = JMH_INFRA_PARAM_TYPES | _state_type_names(source)
    for method in _benchmark_methods(source):
        for param in method.params:
            if _parameter_type(param) not in allowed:
                return "noninjectable_param"
    return None


def _rule_subject_called(source: str, sut: str) -> str | None:
    """The subject class must be named in the body (not just renamed/exercised indirectly)."""
    body = source[source.find("{") :] if "{" in source else source
    return None if re.search(r"\b" + re.escape(sut) + r"\b", body) else "sut_not_called"


# Ordered registry: name -> check. Edit this list to enable/disable rules as the spec evolves.
RULES: tuple[tuple[str, Callable[[str, str], str | None]], ...] = (
    ("jmh_config", _rule_jmh_config),
    ("has_benchmark", _rule_has_benchmark),
    ("result_consumed", _rule_result_consumed),
    ("injectable_params", _rule_injectable_params),
    ("subject_called", _rule_subject_called),
)


def violations(sample: BenchmarkSample) -> list[str]:
    """Return the reasons ``sample`` violates the current rule set (empty == conformant)."""
    source = sample.benchmark_source or ""
    sut = _simple_name(sample.snippet.id)
    reasons: list[str] = []
    for _name, check in RULES:
        reason = check(source, sut)
        if reason is not None:
            reasons.append(reason)
    return reasons


def is_conformant(sample: BenchmarkSample) -> bool:
    return not violations(sample)
