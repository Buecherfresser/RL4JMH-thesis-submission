"""Offline tests for the Qwen TRL vLLM serve adapter (no GPU / no TRL import required)."""

from __future__ import annotations

from jmhgen.serving.trl_vllm_serve import (
    _LANGUAGE_MODEL_ONLY_FLAG,
    consume_language_model_only,
)


def test_consume_language_model_only_strips_flag() -> None:
    remaining, enabled = consume_language_model_only(
        ["--model", "Qwen/Qwen3.5-4B", _LANGUAGE_MODEL_ONLY_FLAG, "--port", "8000"]
    )
    assert enabled is True
    assert remaining == ["--model", "Qwen/Qwen3.5-4B", "--port", "8000"]


def test_consume_language_model_only_detects_absence() -> None:
    remaining, enabled = consume_language_model_only(["--model", "Qwen/Qwen3.5-4B"])
    assert enabled is False
    assert remaining == ["--model", "Qwen/Qwen3.5-4B"]
