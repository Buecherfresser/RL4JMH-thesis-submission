"""Unit tests for Gemma 4 LoRA target resolution in :mod:`jmhgen.models.loader`."""

from __future__ import annotations

import pytest

from jmhgen.config.schema import StageConfig
from jmhgen.models.loader import (
    _gemma4_text_decoder_lora_targets,
    _model_has_text_decoder_namespace,
    _resolve_lora_target_modules,
    _validate_lora_target_modules,
    validate_model_profile,
)


class _Module:
    """Tiny stand-in for torch.nn.Module; loader helpers only require named_modules()."""

    def __init__(self, **children: object) -> None:
        self.children = children

    def named_modules(self):
        yield "", self
        for name, child in self.children.items():
            yield name, child
            if hasattr(child, "named_modules"):
                for nested_name, nested in child.named_modules():
                    if nested_name:
                        yield f"{name}.{nested_name}", nested


class _Linear:
    pass


_Linear.__name__ = "Linear"


class _ClippableLinear:
    __name__ = "Gemma4ClippableLinear"  # type: ignore[misc]


# Mimic PEFT's view of the wrapper class name via type().__name__ patching.
_ClippableLinear.__name__ = "Gemma4ClippableLinear"


class _FakeGemma4(_Module):
    def __init__(self) -> None:
        super().__init__(
            vision_tower=_Module(q_proj=_ClippableLinear()),
            language_model=_Module(
                layers=_Module(
                    **{"0": _Module(q_proj=_Linear())},
                )
            ),
        )


def test_model_has_text_decoder_namespace() -> None:
    assert _model_has_text_decoder_namespace(_FakeGemma4())


def _torch_gemma4() -> _Module:
    """Like _FakeGemma4, but with real Linear leaves (the resolver checks isinstance)."""
    torch = pytest.importorskip("torch")
    return _Module(
        vision_tower=_Module(q_proj=torch.nn.Linear(1, 1)),
        language_model=_Module(layers=_Module(**{"0": _Module(q_proj=torch.nn.Linear(1, 1))})),
    )


def test_gemma4_text_decoder_lora_targets_skips_vision_tower() -> None:
    model = _torch_gemma4()
    targets = _gemma4_text_decoder_lora_targets(model, ["q_proj"])
    assert targets == ["language_model.layers.0.q_proj"]


def test_resolve_lora_target_modules_expands_short_names_for_gemma4() -> None:
    model = _torch_gemma4()
    resolved = _resolve_lora_target_modules(model, ["q_proj"])
    assert resolved == ["language_model.layers.0.q_proj"]


def test_validate_lora_target_modules_rejects_missing_qwen_target() -> None:
    model = _Module(q_proj=_Linear())

    with pytest.raises(ValueError, match="LoRA target modules"):
        _validate_lora_target_modules(model, ["q_proj", "missing_proj"])


def test_validate_model_profile_rejects_wrong_model_family() -> None:
    with pytest.raises(ValueError, match="model_family='qwen'"):
        validate_model_profile(StageConfig(model_family="qwen", base_model="google/gemma-4-E2B-it"))


def test_validate_model_profile_rejects_wrong_active_environment(monkeypatch) -> None:
    monkeypatch.setenv("JMHGEN_MODEL_PROFILE", "gemma")
    with pytest.raises(RuntimeError, match="JMHGEN_MODEL_PROFILE"):
        validate_model_profile(StageConfig(model_family="qwen", base_model="Qwen/Qwen3.5-4B"))
