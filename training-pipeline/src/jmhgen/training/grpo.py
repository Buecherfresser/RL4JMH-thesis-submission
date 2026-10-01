"""GRPO (Group Relative Policy Optimization) stage — online RL with verifiable JMH rewards.

Online RL loop (implemented with ``trl.GRPOTrainer``):

  1. For each prompt (a curated subject class rendered under the ``jmhbench`` contract), roll
     out a group of ``config.group_size`` benchmarks with vLLM.
  2. Score each rollout **synchronously in-process** via the same
     :class:`~jmhgen.runner.client.LocalRunnerClient` + composite reward path RFT uses
     (compile [+ run] + regex anti-pattern) — see :mod:`jmhgen.rewards.grpo_adapter`.
  3. ``GRPOTrainer`` turns the group rewards into group-relative advantages and takes a
     clipped policy-gradient step (Dr.GRPO token-level loss, clip-higher, optional KL).

The numerical recipe (``finetune_mode``/``precision``/``lora``) is inherited from
:class:`~jmhgen.config.schema.StageConfig` and loaded by the shared
:func:`jmhgen.models.load_model_and_tokenizer`, so the policy is constructed identically to
SFT/RFT (a prerequisite for the per-stage comparison).

Generation runs through vLLM 0.23, whose CUDA-13 stack supports Gemma 4 and Transformers 5.
The default is ``vllm_mode="colocate"`` so a single-GPU host can safely time-share training
and rollout generation; TRL server mode requires a distinct CUDA device for the server.
See ``configs/grpo/jmh-rl.yaml``.
"""

from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any

import yaml

from jmhgen.config.schema import GRPOConfig
from jmhgen.data.prompts import render_prompt_messages
from jmhgen.data.schema import CodeSnippet, load_snippets
from jmhgen.training.base import stage_main
from jmhgen.utils.logging import get_logger

logger = get_logger("jmhgen.training.grpo")

# Fallback if configs/grpo/rl-corpus.yaml is missing (matches that file's ``existing`` block).
_ORIGINAL_RL_PROJECTS_FALLBACK = frozenset(
    {
        "commons-numbers",
        "commons-statistics",
        "commons-codec",
        "commons-text",
        "jackson-core",
        "roaringbitmap",
    }
)
_RL_CORPUS_MANIFEST = Path("configs/grpo/rl-corpus.yaml")


def original_rl_projects(manifest: Path = _RL_CORPUS_MANIFEST) -> frozenset[str]:
    """Project ids for the original six-library GRPO corpus."""
    if manifest.is_file():
        data = yaml.safe_load(manifest.read_text(encoding="utf-8")) or {}
        ids = {
            entry["id"]
            for entry in data.get("existing", [])
            if isinstance(entry, dict) and isinstance(entry.get("id"), str)
        }
        if ids:
            return frozenset(ids)
    return _ORIGINAL_RL_PROJECTS_FALLBACK


def _simple_name(fqcn: str) -> str:
    return fqcn.rsplit(".", 1)[-1]


def _package_of(fqcn: str) -> str:
    return fqcn.rsplit(".", 1)[0] if "." in fqcn else ""


def _snippet_package(snippet: CodeSnippet) -> str:
    meta_pkg = snippet.metadata.get("package")
    if isinstance(meta_pkg, str) and meta_pkg:
        return meta_pkg
    return _package_of(snippet.id)


def _disable_vllm_reload_weights(trainer: Any, config: GRPOConfig) -> None:
    """Stop TRL from reloading the base checkpoint over the policy we just synced into vLLM.

    In colocate + sleep mode, ``GRPOTrainer`` does::

        sync_weights()                       # merges the LoRA, pushes the policy into vLLM
        generate()
          -> llm.wake_up(tags=["weights"])
          -> llm.collective_rpc("reload_weights")   # re-reads the ORIGINAL checkpoint

    ``reload_weights`` is TRL's workaround for vllm#29341, but it runs *after* the sync and
    silently reverts it, so every rollout is sampled from the base model instead of the policy
    being trained. Measured directly on this box (2026-08-09), reading the weights back out of
    the worker with ``collective_rpc``::

        base checkpoint sum          54.561256
        after sync_weights        3932109.250000
        after sleep+wake          3932109.250000   <- sleep/wake alone is harmless
        after reload_weights         54.561256     <- back to base

    That accounts for runs 1-5 showing a training-time compile rate pinned near the base
    model's with no trend, while their saved adapters served far better.

    Disabling sleep mode also avoids it, but then vLLM keeps ~38 GiB pinned through the
    optimiser step and the trainer OOMs (run 6 died at step 203 doing exactly that). Since
    sleep/wake on its own preserves the synced weights, the narrow fix is to skip only this
    call. TRL guards it with ``except NotImplementedError``, so raising that is a supported
    no-op path rather than a hack around the control flow.
    """
    if not config.use_vllm or config.vllm_mode != "colocate" or not config.vllm_enable_sleep_mode:
        return
    llm = getattr(getattr(trainer, "vllm_generation", None), "llm", None)
    if llm is None:
        logger.warning(
            "could not reach the colocated vLLM handle; the reload_weights clobber is NOT "
            "patched and rollouts may come from the base model. Check TRL's internals."
        )
        return

    original = llm.collective_rpc
    state: dict[str, Any] = {"skipped": 0, "first": None, "verdict": None}

    def _collective_rpc(method: Any, *args: Any, **kwargs: Any) -> Any:
        if method == "reload_weights":
            state["skipped"] += 1
            if state["skipped"] == 1:
                logger.info("skipping vLLM reload_weights (it would revert the synced policy)")
            # This is the one instant in the step where the fingerprint can be taken: TRL has
            # just called wake_up(tags=["weights"]) and has not yet generated or slept, so the
            # parameters are resident. Reading them after generate() returns faults, because
            # sleep mode has already released the memory.
            _verify_vllm_policy_sync(original, state)
            raise NotImplementedError("jmhgen: reload_weights would discard the synced policy")
        return original(method, *args, **kwargs)

    llm.collective_rpc = _collective_rpc
    logger.info("patched colocated vLLM: reload_weights disabled, rollouts follow the policy")


_SYNC_PROBE_GENERATIONS = 6


def _probe_vllm_weight_sum(collective_rpc: Any) -> tuple[str, float] | None:
    """Read one large attention weight back out of the colocated vLLM worker.

    Returns ``(param_name, float64 sum)``, or ``None`` if the read failed. The sum is a cheap
    fingerprint: the run-7 bug moved it between 54.561256 (base) and 3932109.25 (synced policy),
    so any drift at all is unambiguous.

    Only safe while the weights are resident -- see the call site. ``collective_rpc`` must be
    vLLM's *unwrapped* method, or this recurses through the reload_weights guard.
    """

    def _read(worker: Any) -> tuple[str, float]:
        params = dict(worker.model_runner.model.named_parameters())
        name = next(
            (
                k
                for pat in (
                    "layers.0.self_attn.qkv_proj.weight",
                    "layers.0.self_attn.q_proj.weight",
                    "layers.0.self_attn.o_proj.weight",
                )
                for k in params
                if k.endswith(pat)
            ),
            None,
        )
        if name is None:
            name = sorted(k for k, v in params.items() if v.dim() == 2 and v.numel() > 100_000)[0]
        return name, float(params[name].detach().float().sum().item())

    try:
        return collective_rpc(_read)[0]
    except Exception as exc:  # noqa: BLE001 - a diagnostic must never take the run down
        logger.warning("could not read weights back out of the vLLM worker: %s", exc)
        return None


def _verify_vllm_policy_sync(collective_rpc: Any, state: dict[str, Any]) -> None:
    """Certify, from inside the run, that vLLM samples from the policy and not from base.

    Runs 1-6 all silently sampled from the base model (see
    :func:`_disable_vllm_reload_weights`) and *nothing in the training telemetry could see it* --
    the compile rate simply sat at base's with no trend for 834 steps at a time. That failure
    must never again be diagnosable only in hindsight, so the run now certifies itself.

    Called at the ``reload_weights`` interception, once per generation. The LoRA is
    zero-initialised, so the very first fingerprint *is* the base value and equality there is
    expected; once the optimiser has moved the adapter, a fingerprint still equal to the first
    one means the sync is being reverted.
    """
    if state.get("verdict") is not None or state["skipped"] > _SYNC_PROBE_GENERATIONS:
        return
    probed = _probe_vllm_weight_sum(collective_rpc)
    if probed is None:
        state["verdict"] = "unavailable"
        return
    name, value = probed
    if state["first"] is None:
        state["first"] = value
        logger.info(
            "[weight-sync] generation 1: %s sum=%.6f (LoRA is zero-init, so this is base)",
            name,
            value,
        )
        return
    drifted = value != state["first"]
    logger.info(
        "[weight-sync] generation %d: %s sum=%.6f (%s generation-1 value %.6f)",
        state["skipped"],
        name,
        value,
        "MOVED from" if drifted else "still equal to",
        state["first"],
    )
    if drifted:
        state["verdict"] = "ok"
        logger.info(
            "[weight-sync] VERIFIED: the weights inside vLLM track the policy — rollouts are "
            "on-policy."
        )
    elif state["skipped"] >= _SYNC_PROBE_GENERATIONS:
        state["verdict"] = "broken"
        logger.error(
            "\n"
            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n"
            "WEIGHT SYNC BROKEN: %s has not moved in %d generations. vLLM is almost\n"
            "certainly still serving the BASE model, so every rollout is off-policy and\n"
            "the run will not learn -- this is the runs 1-6 failure.\n"
            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!",
            name,
            _SYNC_PROBE_GENERATIONS,
        )


def build_prompt_records(config: GRPOConfig) -> list[dict[str, Any]]:
    """Render the curated snippet corpus into TRL GRPO prompt records.

    Each record has the conversational ``prompt`` (``[system, user]`` under the ``jmhbench``
    contract, the exact inference-time shape) plus the metadata columns the reward function
    needs to route a rollout to the right SUT classpath and name a fallback class:
    ``project``, ``snippet_id``, ``class_name`` (benchmark class), ``package``, ``source``.
    """
    snippets = list(load_snippets(config.dataset_path))
    if not snippets:
        raise SystemExit(f"[GRPO] no snippets found in {config.dataset_path}")

    if config.corpus == "original":
        allowed = original_rl_projects()
        before = len(snippets)
        snippets = [s for s in snippets if (s.project or "") in allowed]
        logger.info(
            "corpus=original: kept %d/%d snippet(s) from projects %s",
            len(snippets),
            before,
            ", ".join(sorted(allowed)),
        )
    elif config.corpus == "full":
        logger.info("corpus=full: using all %d snippet(s) from %s", len(snippets), config.dataset_path)
    else:
        raise SystemExit(f"[GRPO] unknown corpus={config.corpus!r}; use 'original' or 'full'")

    if config.exclude_projects:
        excluded = frozenset(config.exclude_projects)
        before = len(snippets)
        snippets = [s for s in snippets if (s.project or "") not in excluded]
        logger.info(
            "exclude_projects: dropped %d/%d snippet(s) from %s",
            before - len(snippets),
            before,
            ", ".join(sorted(excluded)),
        )

    if config.max_source_chars > 0:
        kept = [s for s in snippets if len(s.source) <= config.max_source_chars]
        skipped = len(snippets) - len(kept)
        if skipped:
            logger.warning(
                "skipping %d/%d snippet(s) over max_source_chars=%d (won't fit the rollout "
                "context window)",
                skipped,
                len(snippets),
                config.max_source_chars,
            )
        snippets = kept
    if config.limit_prompts > 0:
        snippets = snippets[: config.limit_prompts]
    if not snippets:
        raise SystemExit(
            "[GRPO] every snippet was filtered out; relax max_source_chars / corpus / limit_prompts"
        )

    records: list[dict[str, Any]] = []
    for snippet in snippets:
        records.append(
            {
                "prompt": render_prompt_messages(snippet, "jmhbench"),
                "project": snippet.project or "",
                "snippet_id": snippet.id,
                "class_name": f"{_simple_name(snippet.id)}Benchmark",
                "package": _snippet_package(snippet),
                "source": snippet.source,
            }
        )
    logger.info("built %d GRPO prompt(s) from %s", len(records), config.dataset_path)
    return records


def split_holdout(
    records: list[dict[str, Any]], config: GRPOConfig
) -> tuple[list[dict[str, Any]], list[dict[str, Any]]]:
    """Split ``records`` into (train, holdout) with a deterministic seeded shuffle.

    Returns ``(records, [])`` when ``holdout_prompts`` is 0, so every pre-existing config keeps
    training on the full prompt set.

    The shuffle is seeded from ``holdout_seed`` alone and applied to the already-filtered list, so
    the same config always yields the same split -- including after a resume, where a different
    split would silently leak held-out prompts into training for the remainder of the run. The
    holdout is drawn from the shuffle rather than from the head of the file because the corpus is
    grouped by project: a head/tail slice would hold out whole projects and measure cross-project
    transfer instead of held-out-subject generalisation.
    """
    n = config.holdout_prompts
    if n <= 0:
        return records, []
    if n >= len(records):
        raise SystemExit(
            f"[GRPO] holdout_prompts={n} leaves no training prompts (only {len(records)} "
            "survived filtering); lower it or relax max_source_chars/exclude_projects"
        )

    import random

    order = list(range(len(records)))
    random.Random(config.holdout_seed).shuffle(order)
    holdout_idx = frozenset(order[:n])
    holdout = [records[i] for i in sorted(holdout_idx)]
    train = [records[i] for i in range(len(records)) if i not in holdout_idx]

    holdout_projects = {r["project"] for r in holdout}
    logger.info(
        "holdout: %d prompt(s) from %d project(s) withheld (seed=%d); training on %d",
        len(holdout),
        len(holdout_projects),
        config.holdout_seed,
        len(train),
    )
    return train, holdout


def _build_trl_config(config: GRPOConfig, *, has_holdout: bool = False) -> Any:
    """Map :class:`GRPOConfig` onto ``trl.GRPOConfig`` (imported lazily; needs the train extra)."""
    from trl import GRPOConfig as TrlGRPOConfig

    # >0 num_iterations means a fixed number of optimiser steps (overrides epochs); else epochs.
    max_steps = config.num_iterations if config.num_iterations > 0 else -1

    # Only pass lr_scheduler_kwargs when a floor is set: HF validates the kwargs against the
    # chosen scheduler's signature, so handing min_lr to "linear" is a hard error rather than
    # a silently ignored field.
    scheduler_kwargs: dict[str, Any] = {}
    if config.lr_scheduler_min_lr is not None:
        if "min_lr" not in config.lr_scheduler_type:
            raise SystemExit(
                f"[GRPO] lr_scheduler_min_lr={config.lr_scheduler_min_lr} needs a *_with_min_lr "
                f"scheduler, but lr_scheduler_type={config.lr_scheduler_type!r}"
            )
        scheduler_kwargs["min_lr"] = config.lr_scheduler_min_lr

    # Evaluation is off unless a holdout exists AND a cadence was asked for; otherwise HF would
    # stand up an eval dataloader for an empty set.
    eval_every = config.holdout_eval_every if has_holdout else 0
    eval_args: dict[str, Any] = {"eval_strategy": "no"}
    if eval_every > 0:
        eval_args = {
            "eval_strategy": "steps",
            "eval_steps": eval_every,
            "eval_on_start": config.holdout_eval_on_start,
            "per_device_eval_batch_size": config.per_device_eval_batch_size,
        }
        if config.num_generations_eval is not None:
            eval_args["num_generations_eval"] = config.num_generations_eval

    return TrlGRPOConfig(
        lr_scheduler_type=config.lr_scheduler_type,
        **({"lr_scheduler_kwargs": scheduler_kwargs} if scheduler_kwargs else {}),
        **eval_args,
        output_dir=config.output_dir,
        # rollouts
        num_generations=config.group_size,
        temperature=config.rollout_temperature,
        top_p=config.top_p,
        top_k=config.top_k,
        max_completion_length=config.max_completion_length,
        # Only pass it when non-empty: TRL's default is None, and handing it {} would still
        # render the template with an explicit empty mapping on older TRL versions.
        **({"chat_template_kwargs": config.chat_template_kwargs} if config.chat_template_kwargs else {}),
        # Only override what the config actually sets; None means "keep TRL's default".
        **{
            k: v
            for k, v in (
                ("vllm_importance_sampling_correction", config.vllm_importance_sampling_correction),
                ("vllm_importance_sampling_mode", config.vllm_importance_sampling_mode),
                ("vllm_importance_sampling_cap", config.vllm_importance_sampling_cap),
            )
            if v is not None
        },
        # objective / DAPO stabilisers
        beta=config.kl_coeff,
        epsilon=config.epsilon,
        epsilon_high=config.epsilon_high,
        loss_type=config.grpo_loss_type,
        scale_rewards=config.scale_rewards,
        mask_truncated_completions=config.mask_truncated_completions,
        num_iterations=config.mu,
        # optimisation / length
        learning_rate=config.learning_rate,
        per_device_train_batch_size=config.per_device_batch_size,
        gradient_accumulation_steps=config.gradient_accumulation_steps,
        num_train_epochs=config.num_epochs,
        max_steps=max_steps,
        warmup_ratio=config.warmup_ratio,
        gradient_checkpointing=config.gradient_checkpointing,
        gradient_checkpointing_kwargs=(
            {"use_reentrant": False} if config.gradient_checkpointing else None
        ),
        bf16=config.precision == "bf16",
        fp16=config.precision == "fp16",
        # generation backend (vLLM)
        use_vllm=config.use_vllm,
        vllm_mode=config.vllm_mode,
        vllm_gpu_memory_utilization=config.vllm_gpu_memory_utilization,
        vllm_max_model_length=config.vllm_max_model_length,
        vllm_tensor_parallel_size=config.vllm_tensor_parallel_size,
        vllm_enable_sleep_mode=config.vllm_enable_sleep_mode,
        vllm_server_base_url=config.vllm_server_base_url,
        vllm_server_host=config.vllm_server_host,
        vllm_server_port=config.vllm_server_port,
        vllm_server_timeout=config.vllm_server_timeout,
        # logging / bookkeeping
        logging_steps=config.logging_steps,
        save_strategy="steps" if config.save_steps > 0 else "no",
        save_steps=config.save_steps if config.save_steps > 0 else 500,
        report_to=config.report_to,
        seed=config.seed,
        log_completions=config.log_completions,
    )


def resolve_resume_checkpoint(config: GRPOConfig) -> str | None:
    """Resolve ``config.resume_from_checkpoint`` to a concrete checkpoint dir, or ``None``.

    ``None`` (the default) means never resume, so every existing config keeps its current
    behaviour. ``"auto"`` picks the highest-numbered ``checkpoint-<step>`` under ``output_dir``
    and quietly starts fresh when the directory holds none — that is what makes it safe to
    leave enabled in a job script that may be the first run or the fifth.
    """
    setting = (config.resume_from_checkpoint or "").strip()
    if not setting:
        return None

    if setting != "auto":
        path = Path(setting)
        if not path.is_dir():
            raise SystemExit(f"[GRPO] resume_from_checkpoint={setting!r} is not a directory")
        logger.info("resuming from %s", path)
        return str(path)

    output_dir = Path(config.output_dir)
    checkpoints = [
        candidate
        for candidate in output_dir.glob("checkpoint-*")
        if candidate.is_dir() and candidate.name.removeprefix("checkpoint-").isdigit()
    ]
    if not checkpoints:
        logger.info("resume_from_checkpoint=auto: no checkpoint in %s, starting fresh", output_dir)
        return None
    newest = max(checkpoints, key=lambda p: int(p.name.removeprefix("checkpoint-")))
    logger.info("resume_from_checkpoint=auto: resuming from %s", newest)
    return str(newest)


def _skip_vllm_unmodelled_tower_params(trainer: Any, config: GRPOConfig) -> None:
    """Let weight sync tolerate HF parameters that vLLM's model class does not implement.

    ``GRPOTrainer`` syncs the policy into vLLM by walking **every** HF named parameter and calling
    ``load_weights`` once per tensor. On the unified Gemma 4 line (12B/31B,
    ``Gemma4UnifiedForConditionalGeneration``) the HF checkpoint carries multimodal embedder
    parameters that vLLM 0.23's ``gemma4_unified`` does not model -- concretely
    ``embed_vision.pos_embedding`` -- and vLLM raises::

        ValueError: There is no module or parameter named 'embed_vision.pos_embedding' in
        Gemma4UnifiedForConditionalGeneration.

    That kills the run on the first generation. The nested E2B/E4B line never hits it, which is why
    runs 1-9 did not need this.

    Skipping those tensors is a genuine no-op here: LoRA targets only text-decoder projections (see
    ``_gemma4_text_decoder_lora_targets``), so the towers are frozen and vLLM already holds exactly
    these values from its own checkpoint load at init.

    The filter is deliberately narrow, because the failure mode it borders on is the worst one this
    pipeline has had. Silently dropping a *text* weight would mean sampling from the base model
    while the logs look healthy -- that was runs 1-6. So:

      * only ``ValueError``s whose message is vLLM's "no module or parameter named" are caught;
      * anything under ``language_model`` re-raises rather than being skipped;
      * the skipped set is logged once, by name, so it is auditable.

    ``_verify_vllm_policy_sync`` remains the backstop: it fingerprints an attention weight inside
    the vLLM worker and prints VERIFIED / BROKEN regardless of what this filter did.
    """
    generation = getattr(trainer, "vllm_generation", None)
    if generation is None or config.vllm_mode != "colocate":
        return
    original_push = generation._push_param_to_vllm
    skipped: set[str] = set()

    def push(name: str, param: Any) -> None:
        try:
            original_push(name, param)
        except ValueError as exc:
            if "no module or parameter named" not in str(exc):
                raise
            if "language_model" in name:
                # A text weight that cannot reach vLLM means the rollouts are not on-policy.
                raise
            if name not in skipped:
                skipped.add(name)
                logger.warning(
                    "[weight-sync] vLLM does not model %r; skipping it (frozen tower parameter, "
                    "already correct in the vLLM copy). Total skipped so far: %d",
                    name,
                    len(skipped),
                )

    generation._push_param_to_vllm = push


def drop_overlong_prompts(
    records: list[dict[str, Any]], config: GRPOConfig, tokenizer: Any
) -> list[dict[str, Any]]:
    """Drop prompts whose rendered length exceeds the context budget vLLM enforces.

    ``max_prompt_length`` is not enforced under TRL >= 1.0 and ``max_source_chars`` is measured in
    characters, which does not bound tokens: Java classes built around dense constant tables
    tokenise at roughly 1.1 chars/token instead of ~3.5. ``PDF417Common`` is 45692 chars -- inside a
    48000-char limit -- and 42010 tokens. One such prompt raises ``VLLMValidationError`` inside
    generation, which is not caught per-rollout the way reward failures are, so it takes the whole
    run down. Run 10 died at step 84 on exactly this, twice, and reducing the completion cap did not
    help because the overflow was entirely prompt-side (identical 42010 both times).

    The budget defaults to ``vllm_max_model_length - max_completion_length``: the prompt has to leave
    room for the completion in the same window.

    Caller contract: pass the TRAINING records only, after :func:`split_holdout`. Filtering the full
    list before the split would change the split's input and therefore re-draw the holdout, which on
    a resume silently moves held-out subjects into training.
    """
    budget = config.max_prompt_tokens or (config.vllm_max_model_length - config.max_completion_length)
    if budget <= 0:
        return records

    kept: list[dict[str, Any]] = []
    dropped: list[tuple[str, int]] = []
    for record in records:
        rendered = tokenizer.apply_chat_template(
            record["prompt"],
            tokenize=False,
            add_generation_prompt=True,
            **(config.chat_template_kwargs or {}),
        )
        # NB: len(apply_chat_template(..., tokenize=True)) is NOT a token count -- it returns a
        # BatchEncoding, so len() gives the number of keys (2). Tokenise the rendered string.
        n_tokens = len(tokenizer(rendered, add_special_tokens=False).input_ids)
        if n_tokens > budget:
            dropped.append((record["snippet_id"], n_tokens))
        else:
            kept.append(record)

    if dropped:
        logger.warning(
            "dropping %d/%d training prompt(s) over max_prompt_tokens=%d: %s",
            len(dropped),
            len(records),
            budget,
            ", ".join(f"{sid} ({n} tok)" for sid, n in sorted(dropped, key=lambda x: -x[1])[:10]),
        )
    else:
        logger.info("all %d training prompt(s) fit the %d-token budget", len(records), budget)
    return kept


def _route_eval_rollouts_to_holdout(trainer: Any, collector: Any) -> None:
    """Make the metrics collector write held-out rollouts to their own file.

    The reward function is shared between training and evaluation -- TRL's ``prediction_step``
    calls the same ``_prepare_inputs`` -> reward path -- so without this every held-out rollout
    would also land in ``step_metrics.csv``. That would put a row carrying held-out compile/mutation
    rates under a *training* step number, corrupting exactly the curve the run is judged on.

    Wrapping ``evaluate`` rather than using a callback is deliberate: ``TrainerCallback`` has no
    "evaluation is about to start" hook, and the first held-out reward is scored before any
    eval-time callback fires.
    """
    original_evaluate = trainer.evaluate

    def evaluate(*args: Any, **kwargs: Any) -> Any:
        collector.set_eval_mode(True)
        try:
            return original_evaluate(*args, **kwargs)
        finally:
            collector.set_eval_mode(False)

    trainer.evaluate = evaluate


def train(config: GRPOConfig) -> None:
    """Run GRPO end to end: rollout (vLLM) -> JMH reward -> group-relative policy update."""
    from datasets import Dataset
    from trl import GRPOTrainer

    from jmhgen.models.loader import load_model_and_tokenizer
    from jmhgen.rewards.grpo_adapter import build_grpo_reward_fn
    from jmhgen.training.gpu_sampler import GpuSampler
    from jmhgen.training.grpo_metrics import GrpoMetricsCollector, build_grpo_metrics_callback

    records = build_prompt_records(config)
    train_records, holdout_records = split_holdout(records, config)
    train_ds = Dataset.from_list(train_records)
    holdout_ds = Dataset.from_list(holdout_records) if holdout_records else None

    # Fail fast if the reward toolchain is missing: every rollout is scored by compiling and
    # running it, so Maven + a JDK must be on PATH before we spend time loading the policy.
    runner = config.runner.build_runner()
    if not runner.is_available():
        raise SystemExit(
            f"[GRPO] the JMH reward needs '{runner.mvn_executable}' and "
            f"'{runner.java_executable}' on PATH (rewards are computed on this VM)."
        )
    if not config.project_classpaths:
        logger.warning(
            "[GRPO] no project_classpaths set; rollouts that import SUT types will fail to "
            "compile (reward 0). Set project_classpaths for every project in the corpus."
        )

    args = _build_trl_config(config, has_holdout=holdout_ds is not None)
    # Persist the split before the run can spend a GPU-hour on it: a held-out number is only
    # meaningful if the exact prompt set is recoverable afterwards.
    if holdout_records:
        holdout_path = Path(config.output_dir) / "holdout_prompts.json"
        holdout_path.parent.mkdir(parents=True, exist_ok=True)
        holdout_path.write_text(
            json.dumps(
                {
                    "holdout_seed": config.holdout_seed,
                    "dataset_path": config.dataset_path,
                    "n_train": len(train_records),
                    "n_holdout": len(holdout_records),
                    "snippet_ids": [r["snippet_id"] for r in holdout_records],
                },
                indent=2,
            )
            + "\n",
            encoding="utf-8",
        )
        logger.info("wrote the held-out prompt list to %s", holdout_path)
    model, tokenizer = load_model_and_tokenizer(config)
    if config.completion_eos_token_id is not None:
        eos_id = config.completion_eos_token_id
        eos_token = tokenizer.convert_ids_to_tokens(eos_id)
        if eos_token is None:
            raise SystemExit(f"[GRPO] completion_eos_token_id={eos_id} is not in the tokenizer")
        tokenizer.eos_token_id = eos_id
        logger.info("using completion EOS token %d (%s)", eos_id, eos_token)
    # Needs the tokenizer, so it runs here rather than beside split_holdout -- but it is still applied
    # to the training split only, for the reason documented in drop_overlong_prompts.
    train_records = drop_overlong_prompts(train_records, config, tokenizer)
    train_ds = Dataset.from_list(train_records)

    collector = None
    callbacks: list[Any] = []
    if config.diagnostics:
        metrics_dir = Path(config.diagnostics_dir or Path(config.output_dir) / "metrics")
        collector = GrpoMetricsCollector(
            metrics_dir,
            failure_sample_limit=config.diagnostics_failure_sample_limit,
            capture_completions_every=config.capture_completions_every,
            capture_completions_max_chars=config.capture_completions_max_chars,
        )
        callbacks.append(build_grpo_metrics_callback(collector))
        logger.info("writing scalar GRPO diagnostics to %s", metrics_dir)
    reward_fn = build_grpo_reward_fn(config, metrics=collector)

    logger.info(
        "starting GRPO: %d prompts, G=%d, vllm_mode=%s, reward=%s",
        len(train_ds),
        config.group_size,
        config.vllm_mode,
        {
            "compile": config.reward_weights.compile,
            "runtime": config.reward_weights.runtime,
            "anti_pattern": config.reward_weights.anti_pattern,
            "rsd": config.reward_weights.rsd,
            "mutation": config.reward_weights.mutation,
            "anti_pattern_backend": config.anti_pattern_backend,
        },
    )
    trainer = GRPOTrainer(
        model=model,
        reward_funcs=[reward_fn],
        args=args,
        train_dataset=train_ds,
        eval_dataset=holdout_ds,
        processing_class=tokenizer,
        callbacks=callbacks,
    )
    _disable_vllm_reload_weights(trainer, config)
    _skip_vllm_unmodelled_tower_params(trainer, config)
    if holdout_ds is not None and collector is not None:
        _route_eval_rollouts_to_holdout(trainer, collector)
    # Sampled into the same metrics dir so the dashboard can line GPU utilisation up against
    # step time -- the CPU-side JMH reward is ~half of a step, and that shows up here as the
    # cards sitting idle.
    gpu_sampler = None
    if config.diagnostics and config.gpu_sample_seconds > 0:
        gpu_sampler = GpuSampler(metrics_dir, interval_s=config.gpu_sample_seconds).start()
    try:
        trainer.train(resume_from_checkpoint=resolve_resume_checkpoint(config))
    finally:
        if gpu_sampler is not None:
            gpu_sampler.stop()

    trainer.save_model(config.output_dir)
    tokenizer.save_pretrained(config.output_dir)
    logger.info("saved GRPO adapter to %s", config.output_dir)


def _configure_grpo_parser(parser: argparse.ArgumentParser) -> None:
    parser.add_argument(
        "--corpus",
        choices=("original", "full"),
        default=None,
        help=(
            "Subject-library set: 'original' = six curated libraries "
            "(commons-numbers/statistics/codec/text, roaringbitmap, jackson-core); "
            "'full' = expanded RL corpus. Overrides the YAML corpus field when set."
        ),
    )


def _apply_grpo_cli(config: GRPOConfig, args: argparse.Namespace) -> None:
    if args.corpus is not None:
        config.corpus = args.corpus


def main() -> None:
    stage_main(
        stage="GRPO",
        config_cls=GRPOConfig,
        default_config_path="configs/grpo/jmh-rl.yaml",
        train_fn=train,
        configure_parser=_configure_grpo_parser,
        apply_cli=_apply_grpo_cli,
    )


if __name__ == "__main__":
    main()
