"""Instruction templates that turn a (Java class -> benchmark) sample into chat messages.

The dataset is stored prompt-agnostically (see :mod:`jmhgen.data.build_sft`); the instruction
text is applied here at render time. Swapping in a different prompt therefore only requires
re-rendering the messages file, not re-extracting the verified benchmarks.

Two families of templates live here:

* the simple single-instruction templates (``canonical``, ``llm2jmh_original``) render a
  ``[user, assistant]`` pair where the user message is one instruction block followed by the
  Java class under test;
* the ``jmhbench`` template renders a ``[system, user, assistant]`` triple that matches the
  inference-time contract of the JMH-Bench model: a system prompt carrying the (work in
  progress) benchmark rules, a user prompt naming the subject and embedding its source, and a
  raw (unfenced) Java target. Its rules are mirrored by :mod:`jmhgen.data.conformance` so the
  rendered targets stay consistent with what the prompt asks for.
"""

from __future__ import annotations

from typing import Any

from jmhgen.data.schema import BenchmarkSample, CodeSnippet

# Default instruction: clean, positive-only (we train on verified benchmarks, never SKIP).
CANONICAL = (
    "You are an expert Java performance engineer. Given a Java class, write a single, "
    "self-contained JMH (Java Microbenchmark Harness) benchmark class that measures the "
    "performance of its most relevant logic.\n\n"
    "Requirements:\n"
    "- Output exactly one public benchmark class as Java source, in the same package as the "
    "class under test.\n"
    "- Follow JMH best practices: annotate the class with @State, expose @Benchmark methods, "
    "initialise inputs in a @Setup method, and return or consume results so the JIT cannot "
    "eliminate them as dead code.\n"
    "- Keep the benchmark deterministic and free of external I/O.\n"
    "- Respond with the benchmark source inside a single ```java code block and nothing else."
)

# Best-effort reconstruction of the original LLM2JMH instruction (keeps the SKIP branch).
LLM2JMH_ORIGINAL = (
    "You are given a Java class. Decide whether it contains performance-critical logic that "
    "is worth measuring with a JMH microbenchmark.\n\n"
    "- If it does, write a complete, compilable JMH benchmark for it and respond with the Java "
    "source inside a single ```java code block.\n"
    "- If it does not (for example a pure interface, a data holder, or a thin delegation layer "
    "with no isolatable workload), respond with the single word SKIP followed by a short "
    "justification.\n\n"
    "Follow JMH best practices and keep the benchmark self-contained and deterministic."
)

# Simple templates: name -> single instruction block (rendered as the user message).
INSTRUCTION_TEMPLATES: dict[str, str] = {
    "canonical": CANONICAL,
    "llm2jmh_original": LLM2JMH_ORIGINAL,
}

_USER_TEMPLATE = "{instruction}\n\nJava class under test:\n\n```java\n{java_source}\n```"

# --- jmhbench: system + user contract -------------------------------------------------------
# WORK IN PROGRESS. These rules are the current sensible draft, not a frozen spec. They are
# mirrored by jmhgen.data.conformance.RULES; when you change a load-bearing rule here, update
# the matching check there (and re-render) so the prompt and the targets we train on agree.
JMHBENCH_SYSTEM = (
    "You write compilable JMH benchmarks for JMH-Bench.\n\n"
    "One-line version: emit one compilable Java file that calls the real subject under test "
    "once per @Benchmark, declares the JMH configuration annotations, consumes its result "
    "(return it or pass it to a Blackhole), feeds inputs from @State/@Setup rather than "
    "constants, picks the @Setup level by whether the subject mutates state, and covers the "
    "subject's important public methods with one @Benchmark each.\n\n"
    "Coverage: put multiple @Benchmark methods in the one benchmark class -- add one per "
    "public behaviour worth measuring. A single @Benchmark is fine for a small subject; for a "
    "larger one, measure several representative methods (no need to benchmark trivial getters "
    "or setters).\n\n"
    "Hard rules (load-bearing):\n"
    "1. One self-contained Java file, raw source (no prose). A single markdown fence is "
    "tolerated by the parser, but do not rely on it.\n"
    "2. Compile against the subject: import and call the real subject class by its declared "
    "name and package; never invent or rename it. If the target method is non-static, hold an "
    "instance in a @State field built in @Setup.\n"
    "3. Declare all JMH configuration annotations: @State, @BenchmarkMode, @Fork, @Warmup, "
    "@Measurement and @OutputTimeUnit. WHICH value you pick is free (@Fork/@Warmup/@Measurement "
    "are overridden at runtime; the scope is up to you; the mode may be AverageTime or "
    "Throughput) -- but each one still needs an explicit value in parentheses, and none may be "
    "omitted. @State especially: it has no default, so write @State(Scope.Thread) or "
    "@State(Scope.Benchmark) -- a bare @State does not compile.\n"
    "4. Consume the result (RETU): either return it from the @Benchmark method or call "
    "bh.consume(x). A void @Benchmark must take a Blackhole bh parameter. Returning is fully "
    "valid.\n"
    "5. One call per invocation (LOOP): no accumulation or multi-size loops inside @Benchmark.\n"
    "6. Real inputs, not constants (FINAL): drive inputs from non-final @State fields populated "
    "in @Setup; no static final literals or `final int x = 42` feeding the subject.\n"
    "7. @Benchmark signature: zero parameters, or only JMH-injectable parameters (a Blackhole "
    "and/or @State objects) -- never plain int/String/custom inputs.\n\n"
    "Conditional rule (INVO): pick the @Setup level by mutation. A read-only subject uses "
    "@Setup(Level.Trial) once and reuses it. A mutating subject cycles a Trial-built pool by "
    "index; use @Setup(Level.Invocation) only when rebuilding state is unavoidable and a single "
    "call runs longer than ~1ms (sub-millisecond Level.Invocation is flagged).\n\n"
    "Not enforced (do not over-constrain): an exact Scope.Benchmark, an exact Mode.AverageTime, "
    "a particular @OutputTimeUnit, always using a Blackhole, or specific iteration counts. Only "
    "the presence of the configuration annotations and the rules above are checked."
)

# The user turn. ``target_spec`` is the subject class, optionally suffixed with ``.method``;
# ``sut_source`` is the subject source.
JMHBENCH_USER_TEMPLATE = (
    "Write a JMH microbenchmark class for the target below.\n\n"
    "Target: `{target_spec}`\n"
    "Import and call `{target_class}` exactly -- do not rename or shorten the class.\n"
    "Cover its important public methods: add one @Benchmark per behaviour worth measuring "
    "(one self-contained call each), all in the single benchmark class. A single @Benchmark "
    "is fine for a small subject; measure several representative methods for a larger one.\n\n"
    "Minimal shape to follow (annotation values are examples; only their presence is "
    "required):\n"
    "```java\npackage {package};\n\n"
    "import {target_class};\n"
    "import org.openjdk.jmh.annotations.*;\n"
    "import org.openjdk.jmh.infra.Blackhole;\n"
    "import java.util.concurrent.TimeUnit;\n\n"
    "@State(Scope.Thread)\n"
    "@BenchmarkMode(Mode.Throughput)\n"
    "@OutputTimeUnit(TimeUnit.MICROSECONDS)\n"
    "@Fork(1)\n"
    "@Warmup(iterations = 5)\n"
    "@Measurement(iterations = 5)\n"
    "public class {bench_class} {{\n"
    "    // non-final @State fields, populated in @Setup\n"
    "    @Setup\n"
    "    public void setup() {{ /* build inputs here */ }}\n\n"
    "    @Benchmark\n"
    "    public Object measureOne() {{\n"
    "        return /* one call to {target_class} */;\n"
    "    }}\n\n"
    "    @Benchmark\n"
    "    public Object measureAnother() {{\n"
    "        return /* a different call to {target_class} */;\n"
    "    }}\n"
    "}}\n```\n\n"
    "Subject under test source:\n```java\n{sut_source}\n```\n\n"
    "Output the complete Java file and NOTHING ELSE. Do not wrap it in markdown fences."
)

# All valid template names (used by the CLIs for validation and help text).
TEMPLATES: tuple[str, ...] = (*INSTRUCTION_TEMPLATES, "jmhbench")


def instruction_for(template: str) -> str:
    """Return the instruction string for a simple ``template`` (raises on an unknown name)."""
    try:
        return INSTRUCTION_TEMPLATES[template]
    except KeyError:
        names = sorted(INSTRUCTION_TEMPLATES)
        raise KeyError(
            f"unknown instruction template {template!r}; choose one of {names}"
        ) from None


def _simple_name(fqcn: str) -> str:
    return fqcn.rsplit(".", 1)[-1]


def _package_of(fqcn: str) -> str:
    return fqcn.rsplit(".", 1)[0] if "." in fqcn else ""


def _render_jmhbench_user(sample: BenchmarkSample) -> str:
    fqcn = sample.snippet.id
    target_method = sample.metadata.get("target_method")
    has_method = isinstance(target_method, str) and bool(target_method)
    target_spec = f"{fqcn}.{target_method}" if has_method else fqcn
    bench_class = sample.metadata.get("benchmark_class") or f"{_simple_name(fqcn)}Benchmark"
    return JMHBENCH_USER_TEMPLATE.format(
        target_spec=target_spec,
        target_class=fqcn,
        package=_package_of(fqcn) or "bench.generated",
        bench_class=bench_class,
        sut_source=sample.snippet.source.rstrip(),
    )


def render_user(sample: BenchmarkSample, template: str = "canonical") -> str:
    if template == "jmhbench":
        return _render_jmhbench_user(sample)
    return _USER_TEMPLATE.format(
        instruction=instruction_for(template),
        java_source=sample.snippet.source.rstrip(),
    )


def render_assistant(sample: BenchmarkSample, fence: bool = True) -> str:
    source = (sample.benchmark_source or "").rstrip()
    return f"```java\n{source}\n```" if fence else source


def render_messages(
    sample: BenchmarkSample,
    template: str = "canonical",
    fence_assistant: bool = True,
) -> list[dict[str, str]]:
    """Render a sample to the TRL conversational format (a list of role/content messages).

    The ``jmhbench`` template prepends a ``system`` message carrying the benchmark rules; the
    simple templates render a plain ``[user, assistant]`` pair.
    """
    messages: list[dict[str, str]] = []
    if template == "jmhbench":
        messages.append({"role": "system", "content": JMHBENCH_SYSTEM})
    messages.append({"role": "user", "content": render_user(sample, template)})
    messages.append({"role": "assistant", "content": render_assistant(sample, fence_assistant)})
    return messages


def render_prompt_messages(
    snippet: CodeSnippet,
    template: str = "jmhbench",
    metadata: dict[str, Any] | None = None,
) -> list[dict[str, str]]:
    """Render the *prompt* (``[system, user]``) for a snippet, with no assistant target.

    This is the inference-time counterpart of :func:`render_messages`: RFT/eval need the
    same system+user contract the model was (or will be) trained under, but the assistant
    turn is what the policy is asked to produce, so it is intentionally omitted here. The
    ``assistant`` turn (a verified, on-policy generation) is appended later by the caller.
    """
    sample = BenchmarkSample(snippet=snippet, benchmark_source=None, metadata=dict(metadata or {}))
    messages: list[dict[str, str]] = []
    if template == "jmhbench":
        messages.append({"role": "system", "content": JMHBENCH_SYSTEM})
    messages.append({"role": "user", "content": render_user(sample, template)})
    return messages
