"""Offline validation for the Qwen CUDA-profile presets."""

from __future__ import annotations

from pathlib import Path

from jmhgen.config.schema import GRPOConfig, RFTConfig, SFTConfig

_ROOT = Path(__file__).resolve().parents[1]


def test_qwen_sft_preset_selects_qwen_family() -> None:
    config = SFTConfig.from_yaml(_ROOT / "configs/sft/qwen35-4b.yaml")

    assert config.model_family == "qwen"
    assert config.base_model == "Qwen/Qwen3.5-4B"
    assert "in_proj" in config.lora.target_modules


def test_qwen_rft_preset_uses_separate_output_and_work_dirs() -> None:
    config = RFTConfig.from_yaml(_ROOT / "configs/rft/qwen35-4b.yaml")

    assert config.model_family == "qwen"
    assert config.inference.model == config.base_model
    assert config.output_dir.startswith("outputs/qwen35-4b/")
    assert config.work_dir.startswith("data/qwen35-4b/")


def test_qwen_grpo_preset_requires_server_rollouts() -> None:
    config = GRPOConfig.from_yaml(_ROOT / "configs/grpo/qwen35-4b.yaml")

    assert config.model_family == "qwen"
    assert config.vllm_mode == "server"
    assert config.vllm_server_base_url is not None
    assert config.completion_eos_token_id is None
    assert config.vllm_max_model_length == 32768
    assert config.max_prompt_length + config.max_completion_length <= config.vllm_max_model_length


def test_qwen_grpo_preset_uses_original_six_classpaths() -> None:
    config = GRPOConfig.from_yaml(_ROOT / "configs/grpo/qwen35-4b.yaml")
    expected = {
        "commons-numbers",
        "commons-statistics",
        "commons-codec",
        "commons-text",
        "jackson-core",
        "roaringbitmap",
    }
    # scripts/sync_grpo_corpus_configs.py writes every corpus project's classpath into each
    # config, so the map is a superset; `corpus: original` is what restricts training to the six.
    assert config.corpus == "original"
    assert expected <= set(config.project_classpaths)
