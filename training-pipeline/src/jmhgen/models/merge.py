"""Merge a trained LoRA adapter into its base model at the *state-dict* level.

Why not just ``peft``'s ``merge_and_unload()`` + ``save_pretrained``?

Gemma 4 uses **layer-wise KV sharing** (``text_config.num_kv_shared_layers``): the last
``N`` decoder layers reuse the key/value projections of an earlier layer, so the modern
``transformers`` ``Gemma4`` modules never *instantiate* ``k_proj`` / ``v_proj`` / ``k_norm``
parameters for those shared layers. The official base checkpoint ships those tensors anyway,
but a ``from_pretrained`` -> ``save_pretrained`` round-trip drops them (they are not module
parameters). vLLM's ``Gemma4`` loader, on the other hand, *does* create ``k_norm`` for every
layer and refuses to start when the checkpoint is missing them — which is exactly the
``ValueError: Following weights were not initialized from checkpoint: {... k_norm ...}`` that a
naive PEFT merge produces for the KV-shared layers.

This merger sidesteps the round-trip entirely. It reads the **full** base ``state_dict``
straight from the base safetensors (every tensor, including the KV-shared ones), adds the LoRA
delta ``scaling * (B @ A)`` to the targeted linear weights, and re-serialises the complete
tensor set. KV-shared tensors are copied through verbatim (they are never LoRA targets), so the
merged checkpoint has the exact tensor inventory of the base model — which is what strict
loaders such as vLLM expect.

Only standard LoRA is supported (no DoRA / rank- or alpha-patterns); the merger refuses
adapters that use those features rather than silently producing wrong weights.
"""

from __future__ import annotations

import argparse
import json
import math
import os
import shutil
from pathlib import Path
from typing import TYPE_CHECKING, Any

from jmhgen.utils.logging import get_logger

if TYPE_CHECKING:  # pragma: no cover - type-only import
    import torch

logger = get_logger("jmhgen.models.merge")

# PEFT serialises adapter weights under this prefix; stripping it yields the base param path.
_PEFT_PREFIX = "base_model.model."
_LORA_A = ".lora_A.weight"
_LORA_B = ".lora_B.weight"

# Aux files copied straight from the base checkpoint (architecture / generation / processor).
_BASE_AUX_FILES = (
    "config.json",
    "generation_config.json",
    "preprocessor_config.json",
    "processor_config.json",
)
# Aux files copied from the adapter dir (the tokenizer/chat template saved alongside training).
_ADAPTER_AUX_FILES = (
    "tokenizer.json",
    "tokenizer_config.json",
    "tokenizer.model",
    "special_tokens_map.json",
    "chat_template.jinja",
)


def _resolve_base_safetensors(base_model: str, token: str | None) -> list[Path]:
    """Return local paths to every safetensors shard of ``base_model`` (dir or hub id)."""
    local = Path(base_model)
    if local.is_dir():
        index = local / "model.safetensors.index.json"
        if index.exists():
            weight_map = json.loads(index.read_text())["weight_map"]
            return sorted({local / name for name in weight_map.values()})
        single = local / "model.safetensors"
        if not single.exists():
            raise FileNotFoundError(f"No model.safetensors(.index.json) under {local}")
        return [single]

    from huggingface_hub import hf_hub_download
    from huggingface_hub.errors import EntryNotFoundError

    try:
        index_path = Path(hf_hub_download(base_model, "model.safetensors.index.json", token=token))
        weight_map = json.loads(index_path.read_text())["weight_map"]
        return [
            Path(hf_hub_download(base_model, name, token=token))
            for name in sorted(set(weight_map.values()))
        ]
    except EntryNotFoundError:
        return [Path(hf_hub_download(base_model, "model.safetensors", token=token))]


def _load_full_base_state_dict(base_model: str, token: str | None) -> dict[str, torch.Tensor]:
    """Load every tensor of the base checkpoint (incl. KV-shared, otherwise-"unused" ones)."""
    from safetensors.torch import load_file

    state_dict: dict[str, torch.Tensor] = {}
    for shard in _resolve_base_safetensors(base_model, token):
        state_dict.update(load_file(str(shard)))
    logger.info("loaded %d base tensors from %s", len(state_dict), base_model)
    return state_dict


def _lora_scaling(adapter_config: dict[str, Any]) -> float:
    """LoRA inference scaling: ``alpha / sqrt(r)`` under rsLoRA, else ``alpha / r``."""
    r = int(adapter_config["r"])
    alpha = float(adapter_config["lora_alpha"])
    if adapter_config.get("use_rslora", False):
        return alpha / math.sqrt(r)
    return alpha / r


def _assert_supported(adapter_config: dict[str, Any]) -> None:
    if adapter_config.get("use_dora", False):
        raise NotImplementedError("DoRA adapters are not supported by this merger.")
    if adapter_config.get("rank_pattern") or adapter_config.get("alpha_pattern"):
        raise NotImplementedError(
            "Per-module rank_pattern/alpha_pattern adapters are not supported by this merger."
        )
    if adapter_config.get("peft_type", "LORA") != "LORA":
        raise NotImplementedError(
            f"Only peft_type=LORA is supported, got {adapter_config.get('peft_type')!r}."
        )


def _collect_lora_pairs(
    adapter_weights: dict[str, torch.Tensor],
) -> dict[str, dict[str, torch.Tensor]]:
    """Group adapter tensors into ``{base_param_path: {"A": tensor, "B": tensor}}``."""
    pairs: dict[str, dict[str, torch.Tensor]] = {}
    for key, tensor in adapter_weights.items():
        if key.endswith(_LORA_A):
            module = key[: -len(_LORA_A)]
            slot = "A"
        elif key.endswith(_LORA_B):
            module = key[: -len(_LORA_B)]
            slot = "B"
        else:
            raise NotImplementedError(f"Unsupported adapter tensor (not plain LoRA): {key}")
        if not module.startswith(_PEFT_PREFIX):
            raise ValueError(f"Adapter key does not start with {_PEFT_PREFIX!r}: {key}")
        base_param = f"{module[len(_PEFT_PREFIX) :]}.weight"
        pairs.setdefault(base_param, {})[slot] = tensor
    return pairs


def _apply_lora(
    state_dict: dict[str, torch.Tensor],
    pairs: dict[str, dict[str, torch.Tensor]],
    scaling: float,
) -> None:
    """In-place add ``scaling * (B @ A)`` to each targeted base weight (computed in fp32)."""
    import torch

    for base_param, ab in sorted(pairs.items()):
        if "A" not in ab or "B" not in ab:
            raise ValueError(f"Incomplete LoRA pair for {base_param}: have {sorted(ab)}")
        if base_param not in state_dict:
            raise KeyError(f"LoRA target {base_param} not found in base state dict.")
        weight = state_dict[base_param]
        delta = (ab["B"].to(torch.float32) @ ab["A"].to(torch.float32)) * scaling
        if delta.shape != weight.shape:
            raise ValueError(
                f"Delta shape {tuple(delta.shape)} != base weight {tuple(weight.shape)} "
                f"for {base_param}."
            )
        state_dict[base_param] = (weight.to(torch.float32) + delta).to(weight.dtype)
    logger.info("merged %d LoRA-targeted weights (scaling=%.4f)", len(pairs), scaling)


def _copy_aux_files(
    output_dir: Path, base_model: str, adapter_dir: Path, token: str | None, aux_from: Path | None
) -> None:
    """Populate ``output_dir`` with config/tokenizer/processor files (no weights)."""
    if aux_from is not None:
        for path in sorted(aux_from.iterdir()):
            if (
                path.is_file()
                and path.suffix != ".safetensors"
                and path.name != "model.safetensors.index.json"
            ):
                shutil.copy2(path, output_dir / path.name)
        logger.info("copied auxiliary files from %s", aux_from)
        return

    for name in _ADAPTER_AUX_FILES:
        src = adapter_dir / name
        if src.exists():
            shutil.copy2(src, output_dir / name)

    base_local = Path(base_model)
    if base_local.is_dir():
        for name in _BASE_AUX_FILES:
            src = base_local / name
            if src.exists():
                shutil.copy2(src, output_dir / name)
    else:
        from huggingface_hub import hf_hub_download
        from huggingface_hub.errors import EntryNotFoundError

        for name in _BASE_AUX_FILES:
            try:
                downloaded = hf_hub_download(base_model, name, token=token)
            except EntryNotFoundError:
                continue
            shutil.copy2(downloaded, output_dir / name)
    logger.info("copied auxiliary files from base (%s) + adapter (%s)", base_model, adapter_dir)


def merge_lora_adapter(
    *,
    base_model: str,
    adapter_dir: str | Path,
    output_dir: str | Path,
    hf_token_env: str = "HF_TOKEN",
    aux_from: str | Path | None = None,
) -> Path:
    """Merge the LoRA adapter in ``adapter_dir`` into ``base_model`` and write ``output_dir``.

    The merged ``model.safetensors`` keeps the **complete** base tensor inventory (including the
    Gemma 4 KV-shared ``k_proj``/``v_proj``/``k_norm`` tensors that a PEFT round-trip drops), with
    the LoRA delta applied to the targeted linear weights. Returns the output directory.
    """
    from safetensors.torch import save_file

    adapter_path = Path(adapter_dir)
    out = Path(output_dir)
    out.mkdir(parents=True, exist_ok=True)
    token = os.environ.get(hf_token_env) or None

    adapter_config = json.loads((adapter_path / "adapter_config.json").read_text())
    _assert_supported(adapter_config)
    config_base = adapter_config.get("base_model_name_or_path")
    if config_base and config_base != base_model:
        logger.warning("adapter was trained on %s but merging into %s", config_base, base_model)

    state_dict = _load_full_base_state_dict(base_model, token)
    n_base = len(state_dict)

    from safetensors.torch import load_file

    adapter_weights = load_file(str(adapter_path / "adapter_model.safetensors"))
    pairs = _collect_lora_pairs(adapter_weights)
    _apply_lora(state_dict, pairs, _lora_scaling(adapter_config))

    if len(state_dict) != n_base:
        raise RuntimeError("merge changed the tensor count; refusing to write.")

    save_file(state_dict, str(out / "model.safetensors"), metadata={"format": "pt"})
    logger.info("wrote %d tensors to %s", len(state_dict), out / "model.safetensors")

    _copy_aux_files(out, base_model, adapter_path, token, Path(aux_from) if aux_from else None)
    logger.info("merged model written to %s", out)
    return out


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Merge a LoRA adapter into its base model (KV-share-safe)."
    )
    parser.add_argument("--base", default="google/gemma-4-E2B-it", help="Base model dir or hub id.")
    parser.add_argument("--adapter", default="outputs/sft", help="Adapter (PEFT) directory.")
    parser.add_argument(
        "--out", default="outputs/sft-merged-fixed", help="Output directory for merged weights."
    )
    parser.add_argument(
        "--aux-from",
        default=None,
        help="Optional dir to copy config/tokenizer/processor files from (e.g. a prior merge).",
    )
    parser.add_argument("--hf-token-env", default="HF_TOKEN", help="Env var holding the HF token.")
    args = parser.parse_args()

    merge_lora_adapter(
        base_model=args.base,
        adapter_dir=args.adapter,
        output_dir=args.out,
        hf_token_env=args.hf_token_env,
        aux_from=args.aux_from,
    )


if __name__ == "__main__":
    main()
