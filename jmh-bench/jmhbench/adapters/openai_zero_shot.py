"""OpenAI-compatible zero-shot harness.

Works with any OpenAI-compatible chat-completions endpoint: the official
OpenAI API, Azure OpenAI, vLLM, Ollama (with the openai-compat shim), etc.

Configuration is via keyword arguments (passed as ``-o key=value`` on the
CLI) or environment variables:

- ``model`` (or ``OPENAI_MODEL``): model name, default ``gpt-4o-mini``.
- ``base_url`` (or ``OPENAI_BASE_URL``): override endpoint.
- ``api_key`` (or ``OPENAI_API_KEY``): credential.
- ``temperature``: default ``0.2``.
- ``max_tokens``: default ``4096``.
- ``prompt_template``: path to a Jinja2 template, default ``prompts/zero_shot.j2``.
- ``system_prompt``: path to the system message, default ``prompts/system_zero_shot.txt``.
- ``enable_thinking``: when truthy, ask the server to render the chat template
  with ``enable_thinking=true`` (forwarded as
  ``extra_body={"chat_template_kwargs": {"enable_thinking": true}}``). For the
  Gemma 3n SFT model this injects the ``<|think|>`` token so the model reasons
  before answering. Also sets ``skip_special_tokens=false`` so the
  ``<|channel>thought ... <channel|>`` delimiters survive and the reasoning can
  be split out of the final answer.
- ``extra_body``: optional JSON object merged into the request body, for any
  other vLLM/OpenAI-compatible knob (e.g.
  ``-o extra_body='{"chat_template_kwargs": {"enable_thinking": true}}'`` or
  ``-o extra_body='{"reasoning_effort": "high"}'``). Explicit keys here win over
  the ones implied by ``enable_thinking``.
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


class OpenAIZeroShotHarness:
    name = "openai-zero-shot"
    source_driven = True

    def __init__(
        self,
        model: str | None = None,
        base_url: str | None = None,
        api_key: str | None = None,
        temperature: str | float = 0.2,
        max_tokens: str | int = 4096,
        prompt_template: str | None = None,
        system_prompt: str | None = None,
        enable_thinking: str | bool = False,
        extra_body: str | dict | None = None,
        max_retries: str | int | None = None,
        retry_base_delay: str | float | None = None,
        retry_max_delay: str | float | None = None,
        **_: object,
    ) -> None:
        try:
            from openai import OpenAI  # type: ignore[import-not-found]
        except ImportError as exc:
            raise RuntimeError(
                "The 'openai' extra is required: `pip install jmhbench[openai]`."
            ) from exc

        self.model = model or os.environ.get("OPENAI_MODEL", "gpt-4o-mini")
        self.temperature = float(temperature)
        self.max_tokens = int(max_tokens)
        self.prompt_template = prompt_template
        self.system_prompt = load_system_prompt(system_prompt)
        self.extra_body = _build_extra_body(extra_body, enable_thinking)
        self._retry = RetrySettings.from_kwargs(max_retries, retry_base_delay, retry_max_delay)

        self.client = OpenAI(
            api_key=api_key or os.environ.get("OPENAI_API_KEY"),
            base_url=base_url or os.environ.get("OPENAI_BASE_URL"),
        )

    def generate(self, task: Task, workdir: Path) -> HarnessOutput:
        prompt = render_prompt(task, self.prompt_template)
        return self.generate_from_prompt(prompt, workdir)

    def generate_from_prompt(self, prompt: str, workdir: Path) -> HarnessOutput:
        workdir.mkdir(parents=True, exist_ok=True)
        (workdir / "prompt.txt").write_text(prompt)

        create_kwargs: dict = dict(
            model=self.model,
            temperature=self.temperature,
            max_tokens=self.max_tokens,
            messages=build_chat_messages(self.system_prompt, prompt),
        )
        if self.extra_body:
            create_kwargs["extra_body"] = self.extra_body

        completion = call_with_retry(
            lambda: self.client.chat.completions.create(**create_kwargs),
            self._retry,
            label=self.model,
        )
        message = completion.choices[0].message
        thinking, reply = extract_openai_message(message)
        source = parse_model_reply(reply)
        (workdir / "raw_reply.txt").write_text(reply)
        (workdir / "benchmark.java").write_text(source)

        usage = getattr(completion, "usage", None)
        meta = {"model": self.model}
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
    extra_body: str | dict | None, enable_thinking: str | bool
) -> dict:
    """Assemble the OpenAI ``extra_body`` from explicit JSON and the thinking flag.

    Explicit keys from ``extra_body`` always win over the defaults implied by
    ``enable_thinking`` so power users can fully control the request.
    """
    body: dict = {}
    if extra_body:
        if isinstance(extra_body, str):
            try:
                parsed = json.loads(extra_body)
            except json.JSONDecodeError as exc:
                raise ValueError(f"Could not parse extra_body JSON: {exc}") from exc
            if not isinstance(parsed, dict):
                raise ValueError("extra_body must be a JSON object.")
            body = parsed
        else:
            body = dict(extra_body)

    if coerce_bool(enable_thinking):
        ctk = dict(body.get("chat_template_kwargs") or {})
        ctk.setdefault("enable_thinking", True)
        body["chat_template_kwargs"] = ctk
        # Keep the channel delimiters so reasoning can be split from the answer.
        body.setdefault("skip_special_tokens", False)

    return body
