"""Anthropic Claude zero-shot harness."""

from __future__ import annotations

import os
from pathlib import Path

from jmhbench.adapters._llm_common import (
    RetrySettings,
    build_raw_output,
    call_with_retry,
    extract_anthropic_content,
    load_system_prompt,
    parse_model_reply,
    render_prompt,
)
from jmhbench.harness import HarnessOutput, Task


class AnthropicZeroShotHarness:
    name = "anthropic-zero-shot"
    source_driven = True

    def __init__(
        self,
        model: str | None = None,
        api_key: str | None = None,
        temperature: str | float = 0.2,
        max_tokens: str | int = 4096,
        prompt_template: str | None = None,
        system_prompt: str | None = None,
        max_retries: str | int | None = None,
        retry_base_delay: str | float | None = None,
        retry_max_delay: str | float | None = None,
        **_: object,
    ) -> None:
        try:
            from anthropic import Anthropic  # type: ignore[import-not-found]
        except ImportError as exc:
            raise RuntimeError(
                "The 'anthropic' extra is required: `pip install jmhbench[anthropic]`."
            ) from exc

        self.model = model or os.environ.get("ANTHROPIC_MODEL", "claude-3-5-sonnet-latest")
        self.temperature = float(temperature)
        self.max_tokens = int(max_tokens)
        self.prompt_template = prompt_template
        self.system_prompt = load_system_prompt(system_prompt)
        self._retry = RetrySettings.from_kwargs(max_retries, retry_base_delay, retry_max_delay)
        self.client = Anthropic(api_key=api_key or os.environ.get("ANTHROPIC_API_KEY"))

    def generate(self, task: Task, workdir: Path) -> HarnessOutput:
        prompt = render_prompt(task, self.prompt_template)
        return self.generate_from_prompt(prompt, workdir)

    def generate_from_prompt(self, prompt: str, workdir: Path) -> HarnessOutput:
        workdir.mkdir(parents=True, exist_ok=True)
        (workdir / "prompt.txt").write_text(prompt)
        msg = call_with_retry(
            lambda: self.client.messages.create(
                model=self.model,
                max_tokens=self.max_tokens,
                temperature=self.temperature,
                system=self.system_prompt or "",
                messages=[{"role": "user", "content": prompt}],
            ),
            self._retry,
            label=self.model,
        )
        thinking, reply = extract_anthropic_content(msg.content)
        source = parse_model_reply(reply)
        (workdir / "raw_reply.txt").write_text(reply)
        (workdir / "benchmark.java").write_text(source)
        meta = {
            "model": self.model,
            "input_tokens": getattr(getattr(msg, "usage", None), "input_tokens", None),
            "output_tokens": getattr(getattr(msg, "usage", None), "output_tokens", None),
        }
        return HarnessOutput(
            benchmark_source=source,
            metadata=meta,
            raw_output=build_raw_output(prompt, thinking, reply),
        )
