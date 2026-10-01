"""Parse a (thinking) model completion into a Java benchmark source.

A completion from a thinking model looks like ``<think> ... </think>`` (or whatever the
server emits) followed by the Java file. For RFT we need two views of it:

* the **verbatim** assistant text (thinking + answer) is what we train on, so that the
  fine-tuned policy keeps the base model's reasoning format (the SFT run regressed precisely
  because it was trained on answer-only targets);
* the **Java source** is extracted from the answer so the reward backend can compile/run it.

The extraction is deliberately tolerant: the ``jmhbench`` prompt asks for a raw Java file
("Do not wrap it in markdown fences"), but thinking models frequently fence anyway, so we
accept either a fenced block or a raw file and then trim surrounding prose.
"""

from __future__ import annotations

import re
from dataclasses import dataclass

# Reasoning blocks we know how to strip when the server does *not* split them into a separate
# ``reasoning_content`` field. Matched non-greedily and case-insensitively across newlines.
_THINK_BLOCK_RE = re.compile(
    r"<\s*(think|thinking|reasoning)\s*>.*?<\s*/\s*\1\s*>",
    re.DOTALL | re.IGNORECASE,
)
# A dangling open tag with no close (truncated generation): drop everything up to the tag.
_THINK_OPEN_RE = re.compile(r"<\s*(think|thinking|reasoning)\s*>", re.IGNORECASE)
# The matching close tag, used to recover the answer when the open tag was stripped upstream.
_THINK_CLOSE_RE = re.compile(r"<\s*/\s*(think|thinking|reasoning)\s*>", re.IGNORECASE)

_FENCE_RE = re.compile(r"```(?:java|Java)?\s*\n?(.*?)```", re.DOTALL)
_PACKAGE_RE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)
_CLASS_RE = re.compile(r"\b(?:public\s+|final\s+|abstract\s+)*class\s+(\w+)")
# The first line that plausibly starts a Java file (package / import / annotation / class).
_JAVA_START_RE = re.compile(
    r"^\s*(package\s|import\s|@\w|public\s|final\s|abstract\s|class\s)", re.MULTILINE
)


@dataclass(frozen=True, slots=True)
class ParsedBenchmark:
    """The two views of a completion the RFT loop needs."""

    java_source: str
    package: str
    class_name: str

    @property
    def fully_qualified_name(self) -> str:
        return f"{self.package}.{self.class_name}" if self.package else self.class_name


def strip_thinking(text: str) -> str:
    """Remove reasoning blocks from ``text``, returning just the answer portion.

    Handles three shapes: a well-formed ``<think>...</think>`` block, a stripped-open block
    that still carries a stray ``</think>`` (answer is everything after the last close tag),
    and a dangling ``<think>`` with no close (answer is everything before it, which is usually
    empty for a truncated generation).
    """
    cleaned = _THINK_BLOCK_RE.sub("", text)
    closes = list(_THINK_CLOSE_RE.finditer(cleaned))
    if closes:
        cleaned = cleaned[closes[-1].end() :]
    open_match = _THINK_OPEN_RE.search(cleaned)
    if open_match:
        cleaned = cleaned[: open_match.start()]
    return cleaned.strip()


def _extract_java_block(answer: str) -> str | None:
    """Return the most plausible Java file from an answer (fenced or raw), or ``None``.

    Fenced blocks are tried **last-first**. A reasoning model narrates before it answers, and
    that narration routinely contains a draft benchmark in its own fence which the model then
    rejects; taking the first fence scored the scratch work instead of the answer. Gemma 4 makes
    this the common case rather than the exception: vLLM strips its ``<|channel>``/``<channel|>``
    thought markers on detokenise, so :func:`strip_thinking` sees no delimiter to cut at and the
    reasoning arrives as plain prose ahead of the real file. The last fence is the answer.
    """
    fenced = [m.group(1) for m in _FENCE_RE.finditer(answer)]
    candidates = [*reversed(fenced), answer]
    for candidate in candidates:
        end = candidate.rfind("}")
        if end == -1:
            continue
        for start in _java_start_positions(candidate):
            if end <= start:
                continue
            block = candidate[start : end + 1].strip()
            if "class" in block and "@Benchmark" in block and "```" not in block:
                return block
    return None


def _java_start_positions(candidate: str) -> list[int]:
    """Offsets to try as the first character of the Java file, best guess first.

    A Java file has exactly one ``package`` declaration, so when several appear the **last** one
    opens the answer and the earlier ones are the subject class quoted back inside the model's
    reasoning. Starting there drops the quoted prose that would otherwise be glued to the front
    of the file. Everything after is the pre-existing behaviour, kept as a fallback.
    """
    starts: list[int] = []
    packages = [m.start() for m in _PACKAGE_RE.finditer(candidate)]
    if packages:
        starts.append(packages[-1])
    generic = _JAVA_START_RE.search(candidate)
    if generic is not None:
        starts.append(generic.start())
    return list(dict.fromkeys(starts))


def _infer_package(source: str) -> str:
    match = _PACKAGE_RE.search(source)
    return match.group(1) if match else ""


def _infer_class_name(source: str, fallback: str) -> str:
    match = _CLASS_RE.search(source)
    return match.group(1) if match else fallback


def ensure_package_declaration(source: str, package: str) -> str:
    """Prepend ``package …;`` when the model omitted it but metadata supplies one.

    The Maven runner places sources under ``src/main/java/<pkg>/``, yet JMH's annotation
    processor rejects a benchmark with no explicit package declaration in the file itself.
    """
    if not package or _PACKAGE_RE.search(source):
        return source
    return f"package {package};\n\n{source.lstrip()}"


def parse_completion(
    text: str,
    *,
    reasoning_content: str | None = None,
    fallback_package: str = "",
    fallback_class: str = "GeneratedBenchmark",
) -> ParsedBenchmark | None:
    """Parse a completion into a :class:`ParsedBenchmark`, or ``None`` if no Java is found.

    ``text`` is the verbatim assistant content. When the server already split the reasoning
    into ``reasoning_content`` (a reasoning parser is active), ``text`` is treated as the
    answer directly; otherwise the inline thinking block is stripped first.
    """
    answer = text if reasoning_content is not None else strip_thinking(text)
    java = _extract_java_block(answer)
    if java is None:
        return None
    package = _infer_package(java) or fallback_package
    class_name = _infer_class_name(java, fallback_class)
    java = ensure_package_declaration(java, package)
    return ParsedBenchmark(java_source=java, package=package, class_name=class_name)
