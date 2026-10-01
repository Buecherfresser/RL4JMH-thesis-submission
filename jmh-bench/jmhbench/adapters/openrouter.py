"""OpenRouter zero-shot harness.

OpenRouter exposes ~hundreds of LLMs (Anthropic, OpenAI, Google, Meta,
Mistral, xAI, DeepSeek, …) behind a single OpenAI-compatible chat-
completions endpoint, so benchmarking a new model is just a config knob.

Configuration is via ``-o key=value`` flags on the CLI or environment
variables:

- ``model`` (or ``OPENROUTER_MODEL``): full model slug, e.g.
  ``anthropic/claude-sonnet-4`` or ``openai/gpt-5-codex``. Defaults to
  ``openai/gpt-4o-mini``.
- ``api_key`` (or ``OPENROUTER_API_KEY``): credential.
- ``base_url`` (or ``OPENROUTER_BASE_URL``): override endpoint; defaults
  to ``https://openrouter.ai/api/v1``.
- ``temperature``: default ``0.2``.
- ``max_tokens``: default ``4096``.
- ``http_referer`` (or ``OPENROUTER_HTTP_REFERER``): optional
  ``HTTP-Referer`` header OpenRouter uses to attribute requests and
  apply fairer rate-limits. Defaults to this benchmark's repo URL.
- ``x_title`` (or ``OPENROUTER_X_TITLE``): optional ``X-Title`` header.
  Defaults to ``"JMH-Bench"``.
- ``provider``: optional JSON or comma-separated string of preferred
  upstream providers (forwarded as ``extra_body={"provider": ...}``).
- ``enable_thinking``: when truthy, enable OpenRouter reasoning tokens
  (``extra_body={"reasoning": {"enabled": true}}``). For Gemini and other
  thinking models on OpenRouter. Use ``-o extra_body='{"reasoning": …}'`` to
  override (e.g. ``{"effort": "high"}``).
- ``prompt_template``: path to a Jinja2 template, default
  ``prompts/zero_shot.j2``.
- ``system_prompt``: path to the system message, default
  ``prompts/system_zero_shot.txt``.
- ``max_retries``: how many times to retry a request that fails with a
  rate-limit (HTTP 429) error, default ``5``. Backoff is exponential with
  jitter (``retry_base_delay`` doubled each attempt, capped at
  ``retry_max_delay``), and any ``Retry-After`` header is honoured. Useful
  with ``--parallel`` when the upstream provider is temporarily rate-limited.
- ``retry_base_delay`` / ``retry_max_delay``: first backoff (default ``2`` s)
  and cap on the exponential term (default ``30`` s).

Example::

    export OPENROUTER_API_KEY=sk-or-v1-...
    jmhbench run --harness openrouter -o model=anthropic/claude-sonnet-4

The harness reuses the ``openai`` SDK (already an optional dependency)
because OpenRouter is OpenAI-compatible — no extra Python dep required.
"""

from __future__ import annotations

import json
import os
from pathlib import Path

from jmhbench.adapters._llm_common import (
    RetrySettings,
    build_chat_messages,
    build_raw_output,
    call_with_retry,
    coerce_bool,
    extract_openai_message,
    load_system_prompt,
    parse_model_reply,
    render_prompt,
)
from jmhbench.harness import HarnessOutput, Task

_DEFAULT_BASE_URL = "https://openrouter.ai/api/v1"
_DEFAULT_REFERER = "https://github.com/jmhbench"
_DEFAULT_TITLE = "JMH-Bench"


class OpenRouterHarness:
    name = "openrouter"
    source_driven = True

    def __init__(
        self,
        model: str | None = None,
        api_key: str | None = None,
        base_url: str | None = None,
        temperature: str | float = 0.2,
        max_tokens: str | int = 4096,
        http_referer: str | None = None,
        x_title: str | None = None,
        provider: str | None = None,
        enable_thinking: str | bool = False,
        extra_body: str | dict | None = None,
        prompt_template: str | None = None,
        system_prompt: str | None = None,
        max_retries: str | int | None = None,
        retry_base_delay: str | float | None = None,
        retry_max_delay: str | float | None = None,
        **_: object,
    ) -> None:
        try:
            from openai import OpenAI  # type: ignore[import-not-found]
        except ImportError as exc:
            raise RuntimeError(
                "The 'openai' extra is required: `pip install jmhbench[openai]` "
                "(OpenRouter uses the OpenAI-compatible API)."
            ) from exc

        resolved_key = api_key or os.environ.get("OPENROUTER_API_KEY")
        if not resolved_key:
            raise RuntimeError(
                "OpenRouter requires an API key. Set OPENROUTER_API_KEY or pass "
                "-o api_key=sk-or-v1-..."
            )

        self.model = model or os.environ.get("OPENROUTER_MODEL", "openai/gpt-4o-mini")
        self.temperature = float(temperature)
        self.max_tokens = int(max_tokens)
        self.prompt_template = prompt_template
        self.system_prompt = load_system_prompt(system_prompt)
        self._retry = RetrySettings.from_kwargs(max_retries, retry_base_delay, retry_max_delay)

        self._default_headers = {
            "HTTP-Referer": http_referer or os.environ.get("OPENROUTER_HTTP_REFERER", _DEFAULT_REFERER),
            "X-Title": x_title or os.environ.get("OPENROUTER_X_TITLE", _DEFAULT_TITLE),
        }
        self._extra_body = _build_extra_body(provider, extra_body, enable_thinking)

        self.client = OpenAI(
            api_key=resolved_key,
            base_url=base_url or os.environ.get("OPENROUTER_BASE_URL", _DEFAULT_BASE_URL),
            default_headers=self._default_headers,
        )

    def generate(self, task: Task, workdir: Path) -> HarnessOutput:
        prompt = render_prompt(task, self.prompt_template)
        return self.generate_from_prompt(prompt, workdir)

    def generate_from_prompt(self, prompt: str, workdir: Path) -> HarnessOutput:
        """Call the model with an explicit user prompt (used for compile-fix retries)."""
        workdir.mkdir(parents=True, exist_ok=True)
        (workdir / "prompt.txt").write_text(prompt)

        kwargs: dict = dict(
            model=self.model,
            temperature=self.temperature,
            max_tokens=self.max_tokens,
            messages=build_chat_messages(self.system_prompt, prompt),
        )
        if self._extra_body:
            kwargs["extra_body"] = self._extra_body

        completion = call_with_retry(
            lambda: self.client.chat.completions.create(**kwargs),
            self._retry,
            label=self.model,
        )
        message = completion.choices[0].message
        thinking, reply = extract_openai_message(message)
        source = parse_model_reply(reply)
        (workdir / "raw_reply.txt").write_text(reply)
        (workdir / "benchmark.java").write_text(source)

        meta: dict = {"model": self.model, "provider": "openrouter"}
        upstream = getattr(completion, "model", None)
        if upstream and upstream != self.model:
            meta["routed_to"] = upstream
        usage = getattr(completion, "usage", None)
        if usage is not None:
            meta["prompt_tokens"] = getattr(usage, "prompt_tokens", None)
            meta["completion_tokens"] = getattr(usage, "completion_tokens", None)
            meta["total_tokens"] = getattr(usage, "total_tokens", None)
        return HarnessOutput(
            benchmark_source=source,
            metadata=meta,
            raw_output=build_raw_output(prompt, thinking, reply),
        )


def _build_extra_body(
    provider: str | None,
    extra_body: str | dict | None = None,
    enable_thinking: str | bool = False,
) -> dict:
    """Assemble OpenRouter ``extra_body`` from provider, explicit JSON, and thinking."""
    body: dict = _provider_extra(provider)
    if extra_body:
        if isinstance(extra_body, str):
            try:
                parsed = json.loads(extra_body)
            except json.JSONDecodeError as exc:
                raise ValueError(f"Could not parse extra_body JSON: {exc}") from exc
            if not isinstance(parsed, dict):
                raise ValueError("extra_body must be a JSON object.")
            body.update(parsed)
        else:
            body.update(extra_body)

    if coerce_bool(enable_thinking):
        reasoning = dict(body.get("reasoning") or {})
        reasoning.setdefault("enabled", True)
        body["reasoning"] = reasoning

    return body


def _provider_extra(provider: str | None) -> dict:
    """Translate the ``-o provider=...`` knob into OpenRouter's request body.

    Accepts either a JSON blob (``-o provider='{"order":["anthropic"]}'``)
    or a comma-separated allow-list (``-o provider=anthropic,fireworks``).
    """
    if not provider:
        return {}
    raw = provider.strip()
    if raw.startswith("{"):
        try:
            return {"provider": json.loads(raw)}
        except json.JSONDecodeError as exc:
            raise ValueError(f"Could not parse provider JSON: {exc}") from exc
    order = [p.strip() for p in raw.split(",") if p.strip()]
    if not order:
        return {}
    return {"provider": {"order": order}}
