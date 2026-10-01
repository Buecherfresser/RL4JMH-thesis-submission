"""Inference-side helpers for sampling and parsing benchmark candidates.

This package holds the pieces RFT (and, later, GRPO / the eval harness) need *around* the
verifiable-reward backend: a thin client that samples completions from an OpenAI-compatible
server (vLLM), and a parser that splits a thinking-model completion into the reasoning trace
(kept verbatim for the training target) and the Java source (extracted for the reward).
"""

from jmhgen.generate.parse import (
    ParsedBenchmark,
    ensure_package_declaration,
    parse_completion,
    strip_thinking,
)

__all__ = ["ParsedBenchmark", "ensure_package_declaration", "parse_completion", "strip_thinking"]
