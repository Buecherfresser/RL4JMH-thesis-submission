"""A small OpenAI-compatible chat client for sampling benchmark candidates from vLLM.

Generation is decoupled from training and verification (the same way the eval harness
decouples generation from scoring): the policy is served by a vLLM ``--host`` process
(e.g. on RunPod) exposing the OpenAI ``/v1/chat/completions`` route, and this client just
fans requests out at it. Keeping it on the standard library (``urllib`` + a thread pool)
means the reward/CPU workers never need to install an HTTP SDK.

Thinking: we sample with thinking **on** and keep the verbatim assistant ``content`` as the
training target, so the fine-tuned policy retains the base model's reasoning format. Run the
server **without** a reasoning parser so the thinking stays inline in ``content``; if a parser
is active (``reasoning_content`` comes back populated) the client surfaces it separately and
the caller is warned, because an answer-only target is exactly what regressed the SFT model.
"""

from __future__ import annotations

import json
import time
import urllib.error
import urllib.request
from concurrent.futures import ThreadPoolExecutor
from dataclasses import dataclass, field
from typing import Any

from jmhgen.utils.logging import get_logger

logger = get_logger("jmhgen.generate.vllm")


@dataclass(frozen=True, slots=True)
class SamplingParams:
    """Sampling configuration for one chat-completions request.

    Defaults follow Gemma's recommended decoding (temperature 1.0 / top_p 0.95 / top_k 64).
    ``n`` is the number of candidates per prompt (``samples_per_prompt`` in the RFT config).
    """

    n: int = 8
    temperature: float = 1.0
    top_p: float = 0.95
    top_k: int = 64
    max_tokens: int = 6000
    enable_thinking: bool = True
    # Forwarded to the server's chat-template renderer (vLLM ``chat_template_kwargs``). The
    # exact key that turns on Gemma's thinking lives here so it can be tuned without code
    # changes; ``enable_thinking`` seeds a sensible default.
    chat_template_kwargs: dict[str, Any] = field(default_factory=dict)
    # Escape hatch for any other top-level request field the server understands.
    extra_body: dict[str, Any] = field(default_factory=dict)

    def to_body(self) -> dict[str, Any]:
        body: dict[str, Any] = {
            "n": self.n,
            "temperature": self.temperature,
            "top_p": self.top_p,
            "max_tokens": self.max_tokens,
        }
        if self.top_k:
            body["top_k"] = self.top_k  # vLLM extension to the OpenAI schema
        ctk = dict(self.chat_template_kwargs)
        if self.enable_thinking:
            ctk.setdefault("enable_thinking", True)
        if ctk:
            body["chat_template_kwargs"] = ctk
        body.update(self.extra_body)
        return body


@dataclass(frozen=True, slots=True)
class Completion:
    """One sampled candidate: verbatim ``content`` plus optional separated ``reasoning``."""

    content: str
    reasoning: str | None = None
    finish_reason: str | None = None


class VLLMChatClient:
    """Minimal threaded client for an OpenAI-compatible ``/v1/chat/completions`` endpoint."""

    def __init__(
        self,
        base_url: str,
        model: str,
        *,
        api_key: str | None = None,
        timeout_s: float = 600.0,
        max_retries: int = 3,
        retry_backoff_s: float = 2.0,
        max_concurrency: int = 8,
    ) -> None:
        self.endpoint = base_url.rstrip("/") + "/chat/completions"
        self.model = model
        self.api_key = api_key
        self.timeout_s = timeout_s
        self.max_retries = max_retries
        self.retry_backoff_s = retry_backoff_s
        self.max_concurrency = max(1, max_concurrency)

    def _post(self, payload: dict[str, Any]) -> dict[str, Any]:
        data = json.dumps(payload).encode("utf-8")
        headers = {
            "Content-Type": "application/json",
            # RunPod's Cloudflare front rejects urllib's default User-Agent (403 / error 1010).
            "User-Agent": "jmhgen/0.1 (OpenAI-compatible vLLM client)",
        }
        if self.api_key:
            headers["Authorization"] = f"Bearer {self.api_key}"
        last_error: Exception | None = None
        for attempt in range(self.max_retries):
            request = urllib.request.Request(  # noqa: S310 - trusted, operator-configured URL
                self.endpoint, data=data, headers=headers, method="POST"
            )
            try:
                with urllib.request.urlopen(request, timeout=self.timeout_s) as response:
                    raw = response.read().decode("utf-8")
                parsed: dict[str, Any] = json.loads(raw)
                return parsed
            except urllib.error.HTTPError as exc:
                body = exc.read().decode("utf-8", errors="replace")
                last_error = exc
                # 4xx other than 429 are deterministic; do not waste retries on them.
                if exc.code != 429 and 400 <= exc.code < 500:
                    raise RuntimeError(f"vLLM request failed ({exc.code}): {body}") from exc
                logger.warning("vLLM %s (attempt %d): %s", exc.code, attempt + 1, body[:300])
            except (urllib.error.URLError, TimeoutError, ConnectionError) as exc:
                last_error = exc
                logger.warning("vLLM connection error (attempt %d): %s", attempt + 1, exc)
            time.sleep(self.retry_backoff_s * (attempt + 1))
        raise RuntimeError(f"vLLM request failed after {self.max_retries} retries: {last_error}")

    def complete(self, messages: list[dict[str, str]], params: SamplingParams) -> list[Completion]:
        """Sample ``params.n`` candidates for a single prompt."""
        payload: dict[str, Any] = {"model": self.model, "messages": messages, **params.to_body()}
        response = self._post(payload)
        completions: list[Completion] = []
        for choice in response.get("choices", []):
            message = choice.get("message") or {}
            reasoning = message.get("reasoning_content")
            completions.append(
                Completion(
                    content=message.get("content") or "",
                    reasoning=reasoning if reasoning else None,
                    finish_reason=choice.get("finish_reason"),
                )
            )
        return completions

    def complete_many(
        self, prompts: list[list[dict[str, str]]], params: SamplingParams
    ) -> list[list[Completion]]:
        """Sample candidates for many prompts concurrently (order preserved)."""
        results: list[list[Completion]] = [[] for _ in prompts]
        with ThreadPoolExecutor(max_workers=self.max_concurrency) as pool:
            futures = {
                pool.submit(self.complete, messages, params): i
                for i, messages in enumerate(prompts)
            }
            for future in futures:
                index = futures[future]
                try:
                    results[index] = future.result()
                except Exception as exc:  # noqa: BLE001 - isolate one prompt's failure
                    logger.error("generation failed for prompt %d: %s", index, exc)
                    results[index] = []
        return results
