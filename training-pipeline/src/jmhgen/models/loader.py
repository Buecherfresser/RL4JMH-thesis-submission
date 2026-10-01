"""Model and tokenizer loading.

Kept behind a thin function so every stage loads the policy identically. Heavy imports
(``transformers``, ``torch``, ``peft``) are done lazily inside the function so the core
runner/reward package stays importable on machines without the ``train`` extra installed
(e.g. CPU reward workers).

The ``finetune_mode`` on the config selects the numerical recipe:

* ``full``  — full fine-tuning, all weights trainable in ``precision``;
* ``lora``  — LoRA adapters on a ``precision`` (e.g. bf16) base, base weights frozen;
* ``qlora`` — LoRA adapters on a 4-bit NF4 base (bitsandbytes), the cheapest option.

The same function is reused by SFT/RFT/GRPO so the policy is constructed identically across
stages (a prerequisite for a valid per-stage comparison).
"""

from __future__ import annotations

import os
from typing import TYPE_CHECKING, Any

from jmhgen.utils.logging import get_logger

if TYPE_CHECKING:  # pragma: no cover - type-only import
    from jmhgen.config.schema import StageConfig

logger = get_logger("jmhgen.models")

# Gemma 4 multimodal checkpoints expose vision/audio projections that carry the same leaf names as
# the text decoder ("q_proj", ...) -- as Gemma4ClippableLinear wrappers on the nested E2B/E4B line,
# and as ordinary Linear under model.embed_vision / model.embed_audio on the unified 12B/31B line.
# PEFT 0.19 evaluates exclude_modules too late, so short target names match the towers before
# exclusion applies. _resolve_lora_target_modules therefore expands them to full text-decoder paths.
_FAMILY_MODEL_PREFIXES = {
    "gemma": "google/gemma-4",
    "qwen": "qwen/qwen3.",
}


def _uses_short_lora_targets(target_modules: list[str]) -> bool:
    return all("." not in name and not name.startswith("r") for name in target_modules)


def _model_has_text_decoder_namespace(model: Any) -> bool:
    """True when the text stack sits under a ``language_model`` namespace.

    This is the real precondition for expanding short LoRA suffixes into full paths: it means the
    checkpoint is a multimodal wrapper whose vision/audio towers also contain ``q_proj``-style
    leaves, so bare suffixes would match the towers too.

    It used to test for ``Gemma4ClippableLinear`` instead, which is only how the *nested* Gemma 4
    checkpoints (E2B/E4B) wrap their tower projections. ``gemma4_unified`` (the 12B/31B dense line)
    has ordinary Linear towers under ``model.embed_vision`` / ``model.embed_audio``, so that test
    was False, no expansion happened, and validation then rejected the bare suffixes with
    "LoRA target modules were not found in the loaded model". Keying on the namespace covers both
    families.
    """
    return any("language_model" in name for name, _ in model.named_modules())


def _gemma4_text_decoder_lora_targets(model: Any, suffixes: list[str]) -> list[str]:
    """Full module paths for text-decoder linear layers only (skip vision/audio towers)."""
    import torch

    suffix_set = set(suffixes)
    # isinstance, not an exact class-name match: subclassed Linear layers are still valid LoRA
    # targets, and the towers are already excluded by the ``language_model`` path filter rather
    # than by their type.
    return [
        name
        for name, module in model.named_modules()
        if "language_model" in name
        and name.rsplit(".", 1)[-1] in suffix_set
        and isinstance(module, torch.nn.Linear)
    ]


def _resolve_lora_target_modules(model: Any, target_modules: list[str]) -> list[str]:
    if _uses_short_lora_targets(target_modules) and _model_has_text_decoder_namespace(model):
        resolved = _gemma4_text_decoder_lora_targets(model, target_modules)
        if not resolved:
            raise ValueError(
                "Gemma 4 text-decoder LoRA targets could not be resolved under "
                "'language_model'; check target_modules."
            )
        logger.info(
            "resolved %d Gemma 4 text-decoder LoRA targets (skipped vision/audio towers)",
            len(resolved),
        )
        return resolved
    return list(target_modules)


def _validate_lora_target_modules(model: Any, target_modules: list[str]) -> None:
    """Raise before PEFT setup when a configured LoRA target is absent from the model."""
    available = {name for name, _ in model.named_modules()}
    missing = [name for name in target_modules if name not in available]
    if missing:
        raise ValueError(
            "LoRA target modules were not found in the loaded model: "
            f"{', '.join(missing)}. Check the model-family preset."
        )


def validate_model_profile(config: StageConfig) -> None:
    """Confirm model-id and selected GPU dependency profile agree before loading weights."""
    model_id = config.base_model.lower()
    expected_prefix = _FAMILY_MODEL_PREFIXES[config.model_family]
    if not model_id.startswith(expected_prefix):
        raise ValueError(
            f"model_family={config.model_family!r} requires a model id beginning with "
            f"{expected_prefix!r}, got {config.base_model!r}."
        )

    active_profile = os.environ.get("JMHGEN_MODEL_PROFILE")
    if active_profile and active_profile != config.model_family:
        raise RuntimeError(
            f"Config requests the {config.model_family!r} profile but JMHGEN_MODEL_PROFILE is "
            f"{active_profile!r}. Activate the matching isolated environment under profiles/."
        )


def load_model_and_tokenizer(config: StageConfig) -> tuple[Any, Any]:
    """Load the base policy model and its tokenizer for ``config.base_model``.

    Returns ``(model, tokenizer)`` where ``model`` is already wrapped with LoRA adapters for
    the ``lora``/``qlora`` modes. Requires the matching isolated GPU profile under ``profiles/``.
    """
    import torch
    from transformers import AutoModelForCausalLM, AutoTokenizer

    validate_model_profile(config)
    mode = config.finetune_mode
    dtype = {
        "bf16": torch.bfloat16,
        "fp16": torch.float16,
        "fp32": torch.float32,
    }[config.precision]
    token = os.environ.get(config.hf_token_env) or None

    logger.info(
        "loading %s (mode=%s, precision=%s, attn=%s)",
        config.base_model,
        mode,
        config.precision,
        config.attn_implementation,
    )

    tokenizer = AutoTokenizer.from_pretrained(
        config.base_model,
        trust_remote_code=config.trust_remote_code,
        token=token,
    )
    if tokenizer.pad_token is None:
        tokenizer.pad_token = tokenizer.eos_token
    tokenizer.padding_side = "right"

    model_kwargs: dict[str, Any] = {
        "dtype": dtype,
        "attn_implementation": config.attn_implementation,
        "trust_remote_code": config.trust_remote_code,
        "token": token,
    }

    if mode == "qlora":
        from transformers import BitsAndBytesConfig

        compute_dtype = torch.bfloat16 if config.precision == "fp32" else dtype
        model_kwargs["quantization_config"] = BitsAndBytesConfig(
            load_in_4bit=True,
            bnb_4bit_quant_type="nf4",
            bnb_4bit_compute_dtype=compute_dtype,
            bnb_4bit_use_double_quant=True,
        )
        # 4-bit weights must materialize on the GPU; pin them to the current device so a
        # single-GPU Trainer run does not try to offload to CPU.
        if torch.cuda.is_available():
            model_kwargs["device_map"] = {"": torch.cuda.current_device()}

    model = AutoModelForCausalLM.from_pretrained(config.base_model, **model_kwargs)
    # Caching is incompatible with gradient checkpointing / training; re-enabled at inference.
    model.config.use_cache = False

    if mode in ("lora", "qlora"):
        from peft import LoraConfig, get_peft_model

        if mode == "qlora":
            from peft import prepare_model_for_kbit_training

            model = prepare_model_for_kbit_training(
                model, use_gradient_checkpointing=config.gradient_checkpointing
            )

        target_modules = _resolve_lora_target_modules(model, list(config.lora.target_modules))
        _validate_lora_target_modules(model, target_modules)
        lora_config = LoraConfig(
            r=config.lora.r,
            lora_alpha=config.lora.alpha,
            lora_dropout=config.lora.dropout,
            target_modules=target_modules,
            exclude_modules=list(config.lora.exclude_modules) or None,
            bias=config.lora.bias,
            task_type="CAUSAL_LM",
        )
        model = get_peft_model(model, lora_config)
        # Required so gradients flow back through a frozen (and possibly checkpointed) base.
        if config.gradient_checkpointing and hasattr(model, "enable_input_require_grads"):
            model.enable_input_require_grads()
        if hasattr(model, "print_trainable_parameters"):
            model.print_trainable_parameters()

    return model, tokenizer
