"""Shared helpers for LLM-based harness adapters."""

from __future__ import annotations

import logging
import random
import re
import time
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Callable, TypeVar

from jinja2 import Template

from jmhbench.harness import RawModelOutput, Task

log = logging.getLogger(__name__)

_DEFAULT_TEMPLATE = Path(__file__).resolve().parent.parent.parent / "prompts" / "zero_shot.j2"
_DEFAULT_SYSTEM_PROMPT = (
    Path(__file__).resolve().parent.parent.parent / "prompts" / "system_zero_shot.txt"
)
_LLM4JMH_PROMPT = Path(__file__).resolve().parent.parent.parent / "prompts" / "llm4jmh.txt"


def default_llm4jmh_prompt_path() -> Path:
    return _LLM4JMH_PROMPT


def load_system_prompt(path: str | None = None) -> str | None:
    """Return system prompt text, or ``None`` when disabled via ``system_prompt=none``."""
    if path is not None and path.strip().lower() in {"", "none", "off", "false"}:
        return None
    src = Path(path) if path else _DEFAULT_SYSTEM_PROMPT
    return src.read_text().strip()


def load_prompt(path: str | None = None) -> Template:
    src = Path(path).read_text() if path else _DEFAULT_TEMPLATE.read_text()
    return Template(src, keep_trailing_newline=True)


def render_prompt(task: Task, template_path: str | None = None) -> str:
    template_text = (
        Path(template_path).read_text()
        if template_path
        else _DEFAULT_TEMPLATE.read_text()
    )
    if "{src_code}" in template_text and "{{" not in template_text:
        return template_text.format(src_code=task.sut_source)
    return Template(template_text, keep_trailing_newline=True).render(
        task=task,
        src_code=task.sut_source,
    )


class LlmSkipped(Exception):
    """Raised when the model decides no benchmark is needed (LLM4JMH SKIP)."""

    def __init__(self, reason: str) -> None:
        self.reason = reason
        super().__init__(reason)


def is_skip_response(reply: str) -> bool:
    stripped = reply.strip()
    if not stripped:
        return False
    first_line = stripped.splitlines()[0].strip()
    return first_line.upper().startswith("SKIP")


def parse_model_reply(reply: str) -> str:
    """Extract Java from a model reply, or raise ``LlmSkipped``."""
    if is_skip_response(reply):
        raise LlmSkipped(reply.strip())
    return extract_java(reply)


# A fence opens with three or more backticks and closes with at least as many
# (CommonMark). Matching the opener with a backreference is what makes nesting
# work: a model that wraps ```java in an outer ```` block closes the inner fence
# with three backticks, which must not terminate the outer one. The old pattern
# hard-coded three backticks and non-greedily matched the first ``` it saw, so a
# ````-wrapped reply extracted to a single stray backtick and the caller then
# reported "model output has no class declaration" for a perfectly good class.
_FENCE_RE = re.compile(
    r"(?P<fence>`{3,})[ \t]*(?P<lang>[A-Za-z0-9_+#.-]*)[ \t]*\r?\n"
    r"(?P<body>.*?)"
    r"(?:\r?\n[ \t]*(?P=fence)`*[ \t]*(?:\n|$)|\Z)",
    re.DOTALL,
)

_JAVA_HINT_RE = re.compile(r"(?m)^\s*(?:package\s+[\w.]+\s*;|import\s+|@\w+|(?:public|final|abstract)\s+.*\bclass\b|class\s+\w+)")

_MAX_FENCE_DEPTH = 3


def _looks_like_java(text: str) -> bool:
    return bool(_JAVA_HINT_RE.search(text))


def extract_java(reply: str, _depth: int = 0) -> str:
    """Strip code fences if the model wrapped its answer in markdown.

    Prefers the first fenced block that actually looks like Java, unwrapping
    nested fences on the way, so a reply whose real answer sits one layer deep
    is recovered instead of being scored as a generation failure.
    """
    blocks: list[str] = []
    for match in _FENCE_RE.finditer(reply):
        body = match.group("body").strip()
        if not body:
            continue
        if body.startswith("```") and _depth < _MAX_FENCE_DEPTH:
            body = extract_java(body, _depth + 1).strip()
        blocks.append(body)

    for body in blocks:
        if _looks_like_java(body):
            return body + "\n"
    if blocks:
        return blocks[0] + "\n"
    return reply.strip() + "\n"


_THINK_OPEN = re.escape("<" + "think" + ">")
_THINK_CLOSE = re.escape("</" + "think" + ">")
_THINK_TAG_RE = re.compile(
    rf"{_THINK_OPEN}(.*?){_THINK_CLOSE}",
    re.DOTALL | re.IGNORECASE,
)

# Gemma 3n "thought" channel emitted when the chat template's ``enable_thinking``
# flag is set: ``<|channel>thought\n ...reasoning... <channel|>`` followed by the
# answer. Note the asymmetric delimiters (``<|channel>`` open vs ``<channel|>`` close).
_CHANNEL_THOUGHT_RE = re.compile(
    r"<\|channel>thought\s*\n?(.*?)<channel\|>",
    re.DOTALL,
)

# Residual special tokens that can survive when a server returns text with
# ``skip_special_tokens=false`` (needed to expose the channel delimiters above).
_RESIDUAL_SPECIAL_RE = re.compile(
    r"<\|?(?:start_of_turn|end_of_turn|eos|bos|think)\|?>"
)


def split_embedded_thinking(text: str) -> tuple[str | None, str]:
    """Pull ``...`` blocks out of the visible reply when providers inline them."""
    thinking_parts: list[str] = []

    def _collect(match: re.Match[str]) -> str:
        captured = match.group(1).strip()
        if captured:
            thinking_parts.append(captured)
        return ""

    stripped = _CHANNEL_THOUGHT_RE.sub(_collect, text)
    stripped = _THINK_TAG_RE.sub(_collect, stripped)
    stripped = _RESIDUAL_SPECIAL_RE.sub("", stripped).strip()
    thinking = "\n\n".join(thinking_parts) if thinking_parts else None
    return thinking, stripped


def extract_openai_message(message: Any) -> tuple[str | None, str]:
    """Return ``(thinking, content)`` from an OpenAI-compatible chat message."""
    thinking_parts: list[str] = []
    for attr in ("reasoning_content", "reasoning"):
        value = getattr(message, attr, None)
        if value:
            thinking_parts.append(str(value).strip())

    content = message.content or ""
    if not thinking_parts and content:
        embedded, content = split_embedded_thinking(content)
        if embedded:
            thinking_parts.append(embedded)

    thinking = "\n\n".join(thinking_parts) if thinking_parts else None
    return thinking, content


def extract_anthropic_content(content_blocks: Any) -> tuple[str | None, str]:
    """Return ``(thinking, content)`` from Anthropic ``Message.content`` blocks."""
    thinking_parts: list[str] = []
    text_parts: list[str] = []
    for block in content_blocks:
        block_type = getattr(block, "type", None)
        if block_type == "thinking":
            value = getattr(block, "thinking", None) or getattr(block, "text", "")
            if value:
                thinking_parts.append(str(value).strip())
        elif block_type == "text":
            value = getattr(block, "text", "")
            if value:
                text_parts.append(str(value))
        else:
            value = getattr(block, "text", "")
            if value:
                text_parts.append(str(value))

    content = "".join(text_parts)
    thinking = "\n\n".join(thinking_parts) if thinking_parts else None
    if not thinking and content:
        thinking, content = split_embedded_thinking(content)
    return thinking, content


_TRUTHY = {"1", "true", "yes", "on", "y", "t"}


def coerce_bool(value: object) -> bool:
    """Interpret CLI-style ``-o key=value`` strings (and real bools) as booleans."""
    if isinstance(value, bool):
        return value
    return str(value).strip().lower() in _TRUTHY


def build_chat_messages(system_prompt: str | None, user_prompt: str) -> list[dict[str, str]]:
    messages: list[dict[str, str]] = []
    if system_prompt:
        messages.append({"role": "system", "content": system_prompt})
    messages.append({"role": "user", "content": user_prompt})
    return messages


def build_raw_output(prompt: str, thinking: str | None, content: str) -> RawModelOutput:
    return RawModelOutput(prompt=prompt, thinking=thinking, content=content)


_PROJECT_CLASS_FIX_PROMPT = (
    Path(__file__).resolve().parent.parent.parent / "prompts" / "project_class_fix.j2"
)


def render_project_class_fix_prompt(
    task: Task,
    previous_source: str,
    compile_errors: str,
    template_path: str | None = None,
) -> str:
    """Build a repair prompt after a generation-time compile failure."""
    src = Path(template_path).read_text() if template_path else _PROJECT_CLASS_FIX_PROMPT.read_text()
    return Template(src, keep_trailing_newline=True).render(
        task=task,
        previous_source=previous_source.rstrip(),
        compile_errors=compile_errors.strip(),
    )


def harness_supports_compile_fix(harness: object) -> bool:
    """True when *harness* can accept a free-form repair prompt (LLM adapters)."""
    return getattr(harness, "source_driven", True) and callable(
        getattr(harness, "generate_from_prompt", None)
    )


# ---------------------------------------------------------------------------
# Rate-limit retry with backoff (shared by every LLM adapter)
# ---------------------------------------------------------------------------

_RETRY_AFTER_CAP = 120.0  # never honour an absurd Retry-After value


@dataclass
class RetrySettings:
    """Backoff policy for transient rate-limit (HTTP 429) errors.

    Configured per-harness from ``-o`` flags: ``max_retries`` (attempts *after*
    the first call), ``retry_base_delay`` (first backoff, doubled each time) and
    ``retry_max_delay`` (cap for the exponential term). A provider-supplied
    ``Retry-After`` header always wins when present.
    """

    max_retries: int = 5
    base_delay: float = 2.0
    max_delay: float = 30.0

    @classmethod
    def from_kwargs(
        cls,
        max_retries: str | int | None = None,
        retry_base_delay: str | float | None = None,
        retry_max_delay: str | float | None = None,
    ) -> "RetrySettings":
        return cls(
            max_retries=int(max_retries) if max_retries is not None else 5,
            base_delay=float(retry_base_delay) if retry_base_delay is not None else 2.0,
            max_delay=float(retry_max_delay) if retry_max_delay is not None else 30.0,
        )


def _status_code(exc: Exception) -> int | None:
    code = getattr(exc, "status_code", None)
    if isinstance(code, int):
        return code
    resp = getattr(exc, "response", None)
    code = getattr(resp, "status_code", None)
    return code if isinstance(code, int) else None


def is_rate_limit_error(exc: Exception) -> bool:
    """True for HTTP 429s across SDKs (openai/anthropic ``RateLimitError`` etc.).

    Detected without importing provider-specific exception classes: by status
    code (429) or by the exception class name, so the shared helper stays free
    of optional SDK imports.
    """
    if _status_code(exc) == 429:
        return True
    return "ratelimit" in type(exc).__name__.lower()


def _retry_after_seconds(exc: Exception) -> float | None:
    """Read a ``Retry-After`` header (seconds) from an SDK error, if any."""
    resp = getattr(exc, "response", None)
    headers = getattr(resp, "headers", None)
    if not headers:
        return None
    value = None
    try:
        value = headers.get("retry-after") or headers.get("Retry-After")
    except AttributeError:
        return None
    if not value:
        return None
    try:
        return float(value)
    except (TypeError, ValueError):
        return None


_T = TypeVar("_T")


def call_with_retry(
    fn: Callable[[], _T],
    settings: RetrySettings,
    *,
    label: str = "",
) -> _T:
    """Call *fn*, retrying rate-limit (429) errors with backoff + jitter.

    Non-rate-limit errors propagate immediately. The backoff is exponential
    (``base_delay * 2**n``, capped at ``max_delay``) with up to 25% jitter,
    unless the provider sent a ``Retry-After`` header (honoured up to a 120s
    cap). Blocking ``sleep`` is fine here: adapters run inside the harness's
    worker thread, so one stalled call doesn't hold up the others.
    """
    attempt = 0
    while True:
        try:
            return fn()
        except Exception as exc:  # noqa: BLE001 - re-raised unless it's a 429
            if attempt >= settings.max_retries or not is_rate_limit_error(exc):
                raise
            attempt += 1
            delay = _retry_after_seconds(exc)
            if delay is not None:
                delay = min(delay, _RETRY_AFTER_CAP)
            else:
                delay = min(settings.max_delay, settings.base_delay * (2 ** (attempt - 1)))
                delay += random.uniform(0, max(0.1, delay * 0.25))
            suffix = f" [{label}]" if label else ""
            log.warning(
                "rate-limited%s — backing off %.1fs then retry %d/%d",
                suffix, delay, attempt, settings.max_retries,
            )
            time.sleep(delay)


def _fence_length(text: str) -> int:
    """Return a fence length (>=3) longer than any run of backticks inside *text*."""
    max_run = 0
    run = 0
    for ch in text:
        if ch == "`":
            run += 1
            max_run = max(max_run, run)
        else:
            run = 0
    return max(3, max_run + 1)


def _markdown_fenced_block(text: str) -> list[str]:
    """Wrap *text* in a markdown code fence that survives nested ``` markers."""
    fence = "`" * _fence_length(text)
    return [fence, text.rstrip(), fence]


def render_raw_output_md(
    instance_id: str,
    raw: RawModelOutput,
    metadata: dict[str, Any] | None = None,
) -> str:
    """Format stored model I/O as a Markdown file for manual inspection."""
    lines = [f"# Model output — `{instance_id}`", ""]
    if metadata:
        lines += ["## Metadata", ""]
        for key in sorted(metadata):
            lines.append(f"- **{key}**: `{metadata[key]}`")
        lines.append("")
    lines += ["## Prompt", ""] + _markdown_fenced_block(raw.prompt) + [""]
    if raw.thinking:
        lines += ["## Thinking", ""] + _markdown_fenced_block(raw.thinking) + [""]
    lines += ["## Response", ""] + _markdown_fenced_block(raw.content) + [""]
    return "\n".join(lines)
