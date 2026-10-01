"""Supervised fine-tuning (SFT) stage.

Fine-tunes ``config.base_model`` on (snippet -> benchmark) demonstrations using
``trl.SFTTrainer``. The dataset is the conversational ``messages`` file produced by
``jmh-build-sft`` (``data/sft/sft.jsonl`` + an optional ``sft.val.jsonl`` sibling).

By default the prompt (the Java class under test) is masked and the loss is computed only on
the assistant turn (the benchmark) — we split each ``messages`` record into a
prompt/completion pair and rely on TRL's ``completion_only_loss``. The policy itself is
constructed by :func:`jmhgen.models.load_model_and_tokenizer`, so the precision/PEFT recipe
(``finetune_mode`` = full/lora/qlora) is shared with RFT and GRPO.
"""

from __future__ import annotations

from pathlib import Path
from typing import Any

from jmhgen.config.schema import SFTConfig
from jmhgen.training.base import stage_main
from jmhgen.utils.logging import get_logger

logger = get_logger("jmhgen.training.sft")


def _to_prompt_completion(example: dict[str, Any]) -> dict[str, Any]:
    """Split a conversational ``messages`` record into a (prompt, completion) pair.

    The final message (the assistant's benchmark) becomes the completion; everything before
    it is the prompt. This lets TRL mask the prompt tokens via ``completion_only_loss``
    without depending on ``{% generation %}`` markers in the chat template.
    """
    messages = example["messages"]
    return {"prompt": messages[:-1], "completion": messages[-1:]}


def train(config: SFTConfig) -> None:
    """Run supervised fine-tuning with ``trl.SFTTrainer`` over the demonstration dataset."""
    from datasets import load_dataset
    from trl import SFTConfig as TrlSFTConfig
    from trl import SFTTrainer

    from jmhgen.models.loader import load_model_and_tokenizer

    train_path = Path(config.dataset_path)
    if not train_path.exists():
        raise FileNotFoundError(
            f"SFT dataset not found at {train_path}; build it with `jmh-build-sft` first."
        )
    val_path = train_path.parent / f"{train_path.stem}.val.jsonl"

    train_ds = load_dataset("json", data_files=str(train_path), split="train")
    eval_ds = (
        load_dataset("json", data_files=str(val_path), split="train") if val_path.exists() else None
    )
    logger.info(
        "loaded %d train / %s val samples from %s",
        len(train_ds),
        len(eval_ds) if eval_ds is not None else 0,
        train_path,
    )

    if config.completion_only_loss:
        train_ds = train_ds.map(_to_prompt_completion, remove_columns=train_ds.column_names)
        if eval_ds is not None:
            eval_ds = eval_ds.map(_to_prompt_completion, remove_columns=eval_ds.column_names)

    model, tokenizer = load_model_and_tokenizer(config)

    args = TrlSFTConfig(
        output_dir=config.output_dir,
        num_train_epochs=config.num_epochs,
        learning_rate=config.learning_rate,
        per_device_train_batch_size=config.per_device_batch_size,
        per_device_eval_batch_size=config.per_device_batch_size,
        gradient_accumulation_steps=config.gradient_accumulation_steps,
        max_length=config.max_seq_len,
        packing=config.packing,
        completion_only_loss=config.completion_only_loss,
        loss_type=config.loss_type,
        activation_offloading=config.activation_offloading,
        gradient_checkpointing=config.gradient_checkpointing,
        gradient_checkpointing_kwargs=(
            {"use_reentrant": False} if config.gradient_checkpointing else None
        ),
        bf16=config.precision == "bf16",
        fp16=config.precision == "fp16",
        warmup_ratio=config.warmup_ratio,
        logging_steps=config.logging_steps,
        save_strategy="epoch",
        eval_strategy="epoch" if eval_ds is not None else "no",
        report_to=config.report_to,
        seed=config.seed,
    )

    trainer = SFTTrainer(
        model=model,
        args=args,
        train_dataset=train_ds,
        eval_dataset=eval_ds,
        processing_class=tokenizer,
    )

    logger.info("starting SFT (%s, %.2f epochs)", config.finetune_mode, config.num_epochs)
    trainer.train()

    trainer.save_model(config.output_dir)
    tokenizer.save_pretrained(config.output_dir)
    logger.info("saved SFT checkpoint to %s", config.output_dir)


def main() -> None:
    stage_main(
        stage="SFT",
        config_cls=SFTConfig,
        default_config_path="configs/sft/default.yaml",
        train_fn=train,
    )


if __name__ == "__main__":
    main()
