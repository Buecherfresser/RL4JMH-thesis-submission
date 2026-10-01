"""The in-run weight-sync certificate must distinguish a live sync from the runs 1-6 revert."""

from __future__ import annotations

import pytest

torch = pytest.importorskip("torch", reason="grpo module imports lazily but needs the train extra")

from jmhgen.training.grpo import _verify_vllm_policy_sync  # noqa: E402


class _Worker:
    def __init__(self, value: float) -> None:
        self.model_runner = type(
            "MR", (), {"model": type("M", (), {"named_parameters": lambda s: iter(())})()}
        )()
        self.value = value


def _rpc_returning(values: list[float]):
    """Stand in for llm.collective_rpc: hands back the next fingerprint each call."""
    calls = {"n": 0}

    def rpc(fn):  # noqa: ANN001
        v = values[min(calls["n"], len(values) - 1)]
        calls["n"] += 1
        return [("layers.0.self_attn.q_proj.weight", v)]

    return rpc


def _drive(values: list[float], generations: int) -> dict:
    state: dict = {"skipped": 0, "first": None, "verdict": None}
    rpc = _rpc_returning(values)
    for _ in range(generations):
        state["skipped"] += 1
        _verify_vllm_policy_sync(rpc, state)
    return state


def test_moving_weights_are_verified() -> None:
    # Zero-init LoRA: generation 1 equals base, then the optimiser moves it.
    state = _drive([54.56, 54.56, 3932109.25], 3)
    assert state["verdict"] == "ok"


def test_frozen_weights_are_reported_broken() -> None:
    # The runs 1-6 signature: reload_weights reverts every sync, so it never moves.
    state = _drive([54.56], 7)
    assert state["verdict"] == "broken"


def test_verdict_is_sticky_and_stops_probing() -> None:
    state = _drive([54.56, 99.0, 12345.0, 54.56], 4)
    assert state["verdict"] == "ok"  # a later coincidental match must not un-verify it


def test_unreadable_worker_does_not_raise() -> None:
    def rpc(fn):  # noqa: ANN001
        raise RuntimeError("CUDA error: an illegal memory access was encountered")

    state: dict = {"skipped": 1, "first": None, "verdict": None}
    _verify_vllm_policy_sync(rpc, state)  # must swallow: a probe may never kill a run
    assert state["verdict"] == "unavailable"
