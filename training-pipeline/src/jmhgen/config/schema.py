"""Typed configuration (pydantic) for the runner and the training stages.

Keeping configuration in validated, YAML-loadable models (rather than ad-hoc dicts) is a
reproducibility lever: every experiment is fully described by a config file under
``configs/`` that can be versioned alongside results.
"""

from __future__ import annotations

from pathlib import Path
from typing import Any, Literal, TypeVar

import yaml
from pydantic import AliasChoices, BaseModel, Field

from jmhgen.runner.maven import MavenJmhRunner
from jmhgen.runner.types import JmhOptions

_T = TypeVar("_T", bound="YamlModel")


class YamlModel(BaseModel):
    """Base model that can be loaded from a YAML file."""

    model_config = {"extra": "forbid"}

    @classmethod
    def from_yaml(cls: type[_T], path: str | Path) -> _T:
        data = yaml.safe_load(Path(path).read_text(encoding="utf-8")) or {}
        return cls.model_validate(data)


class JmhConfig(YamlModel):
    """JMH execution parameters (mirrors :class:`~jmhgen.runner.types.JmhOptions`)."""

    warmup_iterations: int = 3
    measurement_iterations: int = 5
    forks: int = 1
    warmup_time: str = "1s"
    measurement_time: str = "1s"
    timeout_s: float = 600.0
    per_iteration_timeout_s: float | None = None
    fail_on_error: bool = True
    jvm_args: list[str] = Field(default_factory=list)
    benchmark_filter: str | None = None

    def to_options(self) -> JmhOptions:
        return JmhOptions(
            warmup_iterations=self.warmup_iterations,
            measurement_iterations=self.measurement_iterations,
            forks=self.forks,
            warmup_time=self.warmup_time,
            measurement_time=self.measurement_time,
            timeout_s=self.timeout_s,
            per_iteration_timeout_s=self.per_iteration_timeout_s,
            fail_on_error=self.fail_on_error,
            jvm_args=tuple(self.jvm_args),
            benchmark_filter=self.benchmark_filter,
        )


class RunnerConfig(YamlModel):
    """Configuration for the Maven JMH runner backend."""

    jmh_version: str = "1.37"
    java_release: int = 17
    mvn_executable: str = "mvn"
    java_executable: str = "java"
    compiler_plugin_version: str = "3.13.0"
    shade_plugin_version: str = "3.6.0"
    group_id: str = "jmhgen.generated"
    work_root: str | None = None
    keep_work_dir: bool = False
    jmh: JmhConfig = Field(default_factory=JmhConfig)

    def build_runner(self, work_root: str | None = None) -> MavenJmhRunner:
        """Build the Maven runner. ``work_root`` overrides the configured one.

        The override exists for parallel reward evaluation: project directories are derived from
        a hash of the benchmark source, so two identical completions in the same group would
        otherwise share one directory and race on writing it. One work root per worker keeps
        concurrent evaluations on disjoint paths.
        """
        return MavenJmhRunner(
            jmh_version=self.jmh_version,
            java_release=self.java_release,
            mvn_executable=self.mvn_executable,
            java_executable=self.java_executable,
            compiler_plugin_version=self.compiler_plugin_version,
            shade_plugin_version=self.shade_plugin_version,
            group_id=self.group_id,
            work_root=work_root or self.work_root,
            keep_work_dir=self.keep_work_dir,
        )


class RewardWeights(YamlModel):
    """Importance factors ``w_i`` for the composite reward.

    All signals are implemented (compile, runtime, anti-pattern, RSD, mutation); see
    :mod:`jmhgen.rewards.composite`. The mutation score is expensive (a JMH rerun per armed
    mutant) so it is computed in the RFT verify phase and read back from the evaluation metadata.
    """

    compile: float = 0.5
    runtime: float = 0.5
    anti_pattern: float = 0.0
    rsd: float = 0.0
    mutation: float = 0.0


class MutationConfig(YamlModel):
    """Detection rule for the performance-mutation reward (``r_mutation``).

    A mutant is *killed* when arming it (``-Djmhbench.mutant=<id>``) makes a covering benchmark
    slower than the un-armed baseline past a global bar: at least ``min_slowdown`` (relative
    effect) **and** significant at ``alpha`` (Welch's t-test). The bar is intentionally not
    per-mutant — a detector never knows a regression's magnitude in advance. Coverage-guided
    mode first records the mutant IDs reached by each ``@Benchmark`` method, then arms a mutant
    only against methods that demonstrably reach it.

    ``mode`` selects how kills map to the reward:

    * ``"graded"`` — strict kill rate ``killed / attempted``;
    * ``"gate"`` — binary ``1.0`` if the benchmark kills *any* in-class mutant else ``0.0`` (a
      cheaper "does this benchmark exercise the subject at all" signal).
    * ``"coverage_aware"`` — ``coverage_weight * coverage_rate + (1 - coverage_weight) *
      conditional_kill_rate``. It rewards reaching important code while keeping detection the
      larger term.
    """

    alpha: float = 0.05
    min_slowdown: float = 1.10
    # System property the patched MutationSwitch reads to arm exactly one mutant per JVM.
    arm_property: str = "jmhbench.mutant"
    # ``MutationSwitch`` recording JVM properties. They are part of every generated patch.
    record_property: str = "jmhbench.record"
    record_file_property: str = "jmhbench.record.file"
    # Record coverage before armed detection. Off by default so historical configs retain their
    # exact whole-suite behavior; coverage-focused experiments enable it explicitly.
    coverage_guidance: bool = False
    coverage_jmh: JmhConfig = Field(
        default_factory=lambda: JmhConfig(
            warmup_iterations=0,
            measurement_iterations=1,
            forks=1,
            warmup_time="100ms",
            measurement_time="100ms",
            timeout_s=120.0,
            per_iteration_timeout_s=60.0,
            fail_on_error=False,
        )
    )
    # 0 scores every curated in-class mutant. A positive value is a future safety override.
    max_mutants_per_candidate: int = Field(default=0, ge=0)
    # At most this many highest-hit covering benchmark methods are used for one armed mutant.
    max_benchmarks_per_mutant: int = Field(default=6, ge=1)
    # How a scored mutant result maps to r_mutation.
    mode: Literal["graded", "gate", "coverage_aware"] = "graded"
    coverage_weight: float = Field(default=0.30, ge=0.0, le=1.0)


class LoraSettings(YamlModel):
    """LoRA adapter hyper-parameters (shared by ``lora`` and ``qlora`` modes).

    Defaults target every projection in a Gemma decoder block (attention + MLP), which is
    the standard "apply LoRA everywhere" recipe; keep these fixed across SFT/RFT/GRPO so the
    per-stage comparison is not confounded by a changing adapter footprint.
    """

    r: int = 16
    alpha: int = 32
    dropout: float = 0.05
    target_modules: list[str] = Field(
        default_factory=lambda: [
            "q_proj",
            "k_proj",
            "v_proj",
            "o_proj",
            "gate_proj",
            "up_proj",
            "down_proj",
        ]
    )
    # Skip these subtrees when injecting LoRA (Gemma 4 multimodal towers use
    # Gemma4ClippableLinear, which PEFT cannot target). Empty = no exclusions.
    exclude_modules: list[str] = Field(default_factory=list)
    bias: str = "none"


class StageConfig(YamlModel):
    """Fields shared by every training stage.

    The precision / PEFT knobs live here (not on a single stage) on purpose: the thesis
    compares quality added at each stage, which is only valid if SFT, RFT, and GRPO all use
    the *same* numerical recipe. ``finetune_mode`` defaults to ``lora`` on a bf16 base — one
    method that scales from E2B (24 GB) to the 31B target (a single 80 GB GPU) without a
    base-quantization confound.
    """

    base_model: str = "google/gemma-4-E2B-it"
    # Dependency family selected by the launch environment.  Keep it explicit rather than
    # inferring CUDA wheels from a model id: the Gemma CUDA-13 and Qwen CUDA-12.8 profiles must
    # never be installed in one environment.
    model_family: Literal["gemma", "qwen"] = "gemma"
    output_dir: str = "outputs"
    dataset_path: str = "data/snippets.jsonl"
    seed: int = 42
    max_seq_len: int = 4096
    learning_rate: float = 1e-5
    per_device_batch_size: int = 1
    gradient_accumulation_steps: int = 8

    # Numerical recipe (kept identical across stages). ``full`` = full fine-tuning in
    # ``precision``; ``lora`` = LoRA adapters on a ``precision`` base; ``qlora`` = LoRA on a
    # 4-bit NF4 base (cheapest, adds a quantization confound — report it as a limitation).
    finetune_mode: Literal["full", "lora", "qlora"] = "lora"
    precision: Literal["bf16", "fp16", "fp32"] = "bf16"
    gradient_checkpointing: bool = True
    attn_implementation: str = "eager"
    # TRL SFT loss backend. "chunked_nll" is numerically identical to "nll" but skips the
    # lm_head matmul on ignored (prompt) tokens and computes the cross-entropy in small token
    # chunks, so peak logits memory is O(chunk * vocab) instead of O(seq * vocab). Essential
    # for long-context completion-only training with Gemma's 262k vocabulary.
    loss_type: Literal["nll", "chunked_nll", "dft"] = "nll"
    # Stream saved forward activations to CPU RAM and fetch them back in the backward pass.
    # Gemma-4 E2B carries per-layer input embeddings, so activations cost ~4 GiB per 1k tokens
    # even with gradient checkpointing; offloading trades host-device copies (slower) for a much
    # lower GPU peak so long sequences fit on a single card. Cheap here (tiny RFT dataset).
    activation_offloading: bool = False
    lora: LoraSettings = Field(default_factory=LoraSettings)
    # Name of the env var holding the Hugging Face token (for gated/private checkpoints).
    hf_token_env: str = "HF_TOKEN"
    trust_remote_code: bool = False


class SftDataConfig(YamlModel):
    """Inputs and options for building the SFT dataset from the distilled gpt-oss-120b tree.

    The build is deterministic and prompt-agnostic: the heavy extraction (filtering to
    run-verified classes and resolving the original Java sources) is decoupled from the
    instruction text, which is applied at render time via ``template``.
    """

    distilled_root: str = "data/llm2jmh-gptoss-120b/ollama-gptoss-120b"
    java_source_root: str = "data/rxjava-src"
    java_source_commit: str = "67e949db"
    fork_dir: str | None = "data/llm2jmh-gptoss-120b-ap-spot-fork_2"
    output_dir: str = "data/sft"
    template: str = "canonical"
    fence_assistant: bool = True
    # When the template carries hard rules (e.g. ``jmhbench``), render only targets that
    # satisfy them, so the prompt we train on never contradicts the output we train on. The
    # full, prompt-agnostic ``records.jsonl`` is always written in full regardless; this only
    # gates the rendered ``sft.jsonl``/``sft.val.jsonl`` view. A conformance breakdown is added
    # to ``report.json`` either way. Has no effect for rule-free templates (``canonical``).
    enforce_conformance: bool = True
    # Drop samples whose original Java source exceeds this many characters (0 disables the
    # cap). A rough rule of thumb is ~4 chars per token; large RxJava classes can blow past a
    # 4k-token budget, so this is exposed for tuning once the length distribution is known.
    max_java_source_chars: int = 0
    val_fraction: float = 0.05
    seed: int = 42
    # Abort if any run-locked class cannot be resolved against the Java source checkout
    # (a guard against the ~85%-confident commit being wrong); set False to skip-and-continue.
    require_all_resolved: bool = True


class ExternalRepo(YamlModel):
    """A public Git repo to mine human-written JMH benchmarks from.

    ``ref`` pins the checkout (branch/tag; a bare commit needs a non-shallow clone). The
    resolved HEAD SHA is recorded in every scraped record's metadata for provenance, so the
    pairing can always be reproduced even when ``ref`` tracks a moving branch.
    """

    name: str
    url: str
    project: str
    ref: str | None = None
    license: str | None = None


class ExternalScrapeConfig(YamlModel):
    """Inputs and options for scraping (class -> human JMH benchmark) pairs from public repos.

    The output is schema-identical to the distilled SFT build (``records.jsonl`` +
    TRL ``sft.jsonl``/``sft.val.jsonl`` + ``report.json``), so the human-expert pairs can be
    merged with — or substituted for — the gpt-oss-120b demonstrations.
    """

    repos: list[ExternalRepo] = Field(
        default_factory=lambda: [
            ExternalRepo(
                name="kafka",
                url="https://github.com/apache/kafka.git",
                project="kafka",
                license="Apache-2.0",
            ),
            ExternalRepo(
                name="grpc-java",
                url="https://github.com/grpc/grpc-java.git",
                project="grpc-java",
                license="Apache-2.0",
            ),
        ]
    )
    checkout_root: str = "data/external-src"
    output_dir: str = "data/sft-external"
    # Clone missing repos automatically (shallow). Set False to require a pre-existing checkout.
    clone: bool = True
    template: str = "canonical"
    fence_assistant: bool = True
    # Globs (relative to each repo root) where benchmark sources live; matches are de-duplicated.
    benchmark_globs: list[str] = Field(
        default_factory=lambda: [
            "**/src/jmh/**/*.java",
            "**/jmh-benchmarks/**/*.java",
            "**/jmh/**/*.java",
        ]
    )
    # Class-name suffixes (longest-first) that mark a benchmark and yield the target class name.
    benchmark_suffixes: list[str] = Field(
        default_factory=lambda: ["Benchmarks", "Benchmark", "Perf", "Bench"]
    )
    # Drop pairs whose original Java source exceeds this many characters (0 disables the cap).
    max_java_source_chars: int = 0
    val_fraction: float = 0.05
    seed: int = 42


class SFTConfig(StageConfig):
    """Supervised fine-tuning on (snippet -> benchmark) demonstrations."""

    num_epochs: float = 1.0
    # Packing concatenates short samples for throughput. Off by default because we mask the
    # prompt (completion-only loss) via a prompt/completion split, which is cleaner without
    # packing; the SFT set is small enough that throughput is a non-issue.
    packing: bool = False
    # Compute the loss only on the assistant (benchmark) tokens, not on the Java prompt.
    completion_only_loss: bool = True
    warmup_ratio: float = 0.03
    logging_steps: int = 5
    # Experiment trackers passed to the HF Trainer ("none", "wandb", "tensorboard", ...).
    report_to: str = "none"


class InferenceConfig(YamlModel):
    """Connection + sampling knobs for the vLLM OpenAI-compatible generation server.

    Generation is decoupled from training/verification: the policy is served by a vLLM
    ``--host`` process (e.g. on RunPod) and sampled over HTTP. ``temperature`` and the number
    of samples live on :class:`RFTConfig` (``sampling_temperature`` / ``samples_per_prompt``);
    everything else is here. Run the server **without** a reasoning parser so the verbatim
    thinking stays inline in ``content`` (the training target depends on it).
    """

    base_url: str = "http://localhost:8000/v1"
    model: str = "google/gemma-4-E2B-it"
    # Env var holding the server API key ("EMPTY"/unset is fine for a private vLLM pod).
    api_key_env: str = "VLLM_API_KEY"
    timeout_s: float = 600.0
    max_retries: int = 3
    retry_backoff_s: float = 2.0
    # Max parallel HTTP requests to the vLLM server during ``phase_generate`` (one request per
    # prompt; each request samples ``samples_per_prompt`` completions via ``n=``). Tune against
    # GPU memory and server ``--max-num-seqs``.
    max_concurrency: int = Field(
        default=8,
        validation_alias=AliasChoices("max_concurrency", "max_parallel_requests"),
    )
    top_p: float = 0.95
    top_k: int = 64
    max_tokens: int = 6000
    enable_thinking: bool = True
    # Forwarded to vLLM's chat-template renderer; the exact key that enables Gemma thinking
    # is configurable here (``enable_thinking`` seeds a default in the client).
    chat_template_kwargs: dict[str, Any] = Field(default_factory=dict)
    # Escape hatch for any other top-level request field the server understands.
    extra_body: dict[str, Any] = Field(default_factory=dict)


class RFTConfig(StageConfig):
    """Rejection fine-tuning: sample k, keep high-reward completions, then SFT on them.

    The accepted set is self-generated, verified, and (crucially) keeps the verbatim thinking
    trace, so the SFT step preserves the base model's reasoning format instead of overwriting
    it. Following ReST^EM, the train step always fine-tunes from ``base_model`` (not a prior
    checkpoint). The SFT-recipe fields mirror :class:`SFTConfig` so the per-stage comparison
    is not confounded by a different trainer setup.
    """

    # --- sampling (E-step) ---
    samples_per_prompt: int = 16
    sampling_temperature: float = 1.0
    inference: InferenceConfig = Field(default_factory=InferenceConfig)
    # Cap the number of prompts processed (0 = all); handy for smoke runs.
    limit_prompts: int = 0
    # Skip a snippet whose subject source exceeds this many characters (0 disables the skip).
    # The embedded source must leave room in the server's context window for the prompt
    # boilerplate PLUS inference.max_tokens of output, so very large classes that cannot fit
    # are dropped rather than silently truncated (~4 chars/token rule of thumb).
    max_source_chars: int = 0

    # --- verification / acceptance ---
    # With the default weights this gates on compile+runtime; raise it (e.g. 0.9) once
    # anti_pattern/rsd weights are set so acceptance also requires clean, stable benchmarks.
    accept_threshold: float = 1.0
    # Whether to execute (not just compile) candidates. False => compile-only rewards.
    need_run: bool = True
    reward_weights: RewardWeights = Field(default_factory=RewardWeights)
    # Robust-RSD -> [0,1] mapping for the stability reward (full reward at/below rsd_good,
    # zero at/above rsd_bad, linear between). Only used when reward_weights.rsd > 0.
    rsd_good: float = 0.05
    rsd_bad: float = 0.25
    runner: RunnerConfig = Field(default_factory=RunnerConfig)
    # Map CodeSnippet.project -> a ``.cp`` file from ``jmh-provision-classpath``. A snippet
    # whose project is absent gets an empty classpath (fine for self-contained subjects only).
    # For a mutation-scored run this MUST point at the *patched* SUT jar (jmh-provision-mutants)
    # so the dormant mutants exist at run time.
    project_classpaths: dict[str, str] = Field(default_factory=dict)
    # --- mutation reward (r_mutation) ---
    # Map CodeSnippet.project -> a ``mutants.yaml`` registry from ``jmh-make-mutants``. Required
    # when reward_weights.mutation > 0: the verify phase looks up the mutants planted in each
    # candidate's subject class, arms them one at a time, and reruns to measure detection.
    project_mutants: dict[str, str] = Field(default_factory=dict)
    mutation: MutationConfig = Field(default_factory=MutationConfig)

    # --- dataset construction (filter accepted -> SFT messages) ---
    # Intermediate artifacts (generations/scored/sft jsonl + report) live here.
    work_dir: str = "data/rft"
    # Cap accepted samples kept per prompt so easy prompts don't dominate (ReST^EM).
    max_keep_per_prompt: int = 4
    # Deduplicate kept completions by normalized Java source (distinct paths matter, Yuan 2023).
    dedup: bool = True
    # Optionally drop prompts where every sample passed (low learning signal; Reinforce-Rej).
    drop_all_pass: bool = False
    val_fraction: float = 0.05

    # --- SFT (M-step), kept in sync with SFTConfig ---
    num_epochs: float = 1.0
    packing: bool = False
    completion_only_loss: bool = True
    warmup_ratio: float = 0.03
    logging_steps: int = 5
    report_to: str = "none"


class GRPOConfig(StageConfig):
    """Group Relative Policy Optimization with verifiable JMH rewards (online RL).

    Rollouts are generated by a colocated vLLM engine (``vllm_mode="colocate"``: vLLM shares
    the training GPU), scored **in-process** by the same ``LocalRunnerClient`` +
    ``CompositeReward`` path RFT uses, and turned into group-relative advantages by
    ``trl.GRPOTrainer``. Fields map onto ``trl.GRPOConfig``; the numerical recipe
    (``finetune_mode``/``precision``/``lora``) is inherited from :class:`StageConfig` so the
    per-stage comparison stays valid.
    """

    # --- rollouts (E-step) ---
    # Group size G: candidates sampled per prompt for the group-relative baseline (TRL
    # num_generations). The synchronous JMH reward makes each rollout expensive, so keep this
    # modest; too small also weakens the baseline and worsens advantage collapse.
    group_size: int = 8
    rollout_temperature: float = 1.0
    top_p: float = 0.95
    top_k: int = 64
    max_prompt_length: int = 4096
    max_completion_length: int = 2048
    # Forwarded to TRL's ``chat_template_kwargs`` when rendering rollout prompts. Gemma 4's
    # template accepts exactly two flags -- ``enable_thinking`` (default false) and
    # ``preserve_thinking`` -- and no effort/budget knob, so the only way to buy more reasoning
    # is a larger ``max_completion_length``. Training with thinking off while serving with it on
    # is a train/inference mismatch: the JMH-Bench matrix scored 18 % vs 10 % mutation and
    # 34 % vs 21 % first-shot compile purely on this flag. ``parse_completion`` already calls
    # ``strip_thinking``, so the reward path needs no change.
    chat_template_kwargs: dict[str, Any] = Field(default_factory=dict)
    # TRL corrects for the fact that vLLM's logprobs are not bit-identical to the trainer's.
    # In the default ``sequence_mask`` mode that correction is a **mask**: a completion whose
    # sequence-level ratio strays past ``vllm_importance_sampling_cap`` is dropped from the loss
    # entirely. Divergence compounds with sequence length, so long completions are masked most.
    #
    # Measured on rtx-jonas, 2-step smokes differing only in ``enable_thinking``:
    #     thinking off - completions  271 tok, ratio_mean 0.228, grad_norm 5.6e-04  (healthy)
    #     thinking on  - completions 1026 tok, ratio_mean 0.125, grad_norm 3.2e-08  (dead)
    # Both had healthy reward_std and frac_reward_zero_std 0 -- the reward was fine and the
    # gradient was being discarded downstream. Exposed here so long-completion runs can relax
    # the masking instead of silently training on almost nothing. ``None`` = leave TRL's default.
    vllm_importance_sampling_correction: bool | None = None
    vllm_importance_sampling_mode: str | None = None
    vllm_importance_sampling_cap: float | None = None
    # Optional chat-turn terminator used by TRL's truncated-completion mask. Gemma 4 advertises
    # <eos> (1) on its tokenizer but instruction completions normally end with <turn|> (106).
    completion_eos_token_id: int | None = None
    # Cap prompts processed (0 = all); handy for smoke runs. Applied to the loaded snippets.
    limit_prompts: int = 0
    # Which subject-library set to train on from ``dataset_path`` (usually rl-merged.good.jsonl).
    # ``original`` = the six hand-curated libraries in configs/grpo/rl-corpus.yaml ``existing``;
    # ``full`` = every project present in the merged JSONL (expanded ~99-library corpus).
    # Override on the CLI with ``jmh-train-grpo --corpus {original,full}``.
    corpus: Literal["original", "full"] = "original"
    # Skip a snippet whose subject source exceeds this many chars (0 = keep all). A cheap pre-filter
    # only -- it is NOT a reliable context gate, because chars-per-token is far from constant across
    # Java. Classes dominated by dense constant tables tokenise at ~1.1 chars/token instead of the
    # usual ~3.5: measured on the 12B tokenizer, ``com.google.zxing.pdf417.PDF417Common`` is 45692
    # chars -- comfortably under a 48000 limit -- and **42010 tokens**. Use ``max_prompt_tokens``.
    max_source_chars: int = 0
    # Drop a prompt whose rendered chat-template length exceeds this many tokens. 0 = derive it from
    # ``vllm_max_model_length - max_completion_length``, which is the budget vLLM actually enforces.
    #
    # This exists because ``max_prompt_length`` is not enforced under TRL >= 1.0 and
    # ``max_source_chars`` is in the wrong unit (above), so one pathological class aborts the entire
    # distributed run with ``VLLMValidationError`` mid-training. It killed run 10 twice at step 84,
    # and only 3 of 4547 prompts were responsible.
    #
    # Applied to the TRAINING split only, and only after ``split_holdout``: the holdout is a seeded
    # shuffle of the filtered list, so gating before the split would re-draw it and leak held-out
    # subjects into training on a resume.
    max_prompt_tokens: int = 0

    # --- GRPO objective / DAPO-style stabilisers ---
    # KL penalty toward the reference policy (TRL ``beta``). Recent recipes (LongCat, DAPO)
    # drop the KL term (beta=0) and rely on clipping alone; that is the default here. Kept under
    # the historical name so an existing config's ``kl_coeff`` still validates.
    kl_coeff: float = 0.0
    # Asymmetric PPO clip. ``epsilon_high`` > ``epsilon`` is DAPO's "clip-higher": it loosens
    # the upward probability update so exploratory tokens are not clipped away (mitigates
    # entropy collapse). ``None`` for epsilon_high => symmetric clipping at ``epsilon``.
    epsilon: float = 0.2
    epsilon_high: float | None = 0.28
    # Dr.GRPO / DAPO loss aggregation. "dr_grpo" averages the loss over tokens (unbiased,
    # avoids the length bias of sequence-level "grpo") which matters for long thinking traces.
    grpo_loss_type: Literal["grpo", "bnpo", "dr_grpo"] = "dr_grpo"
    # Whether to divide group advantages by their std. Dr.GRPO recommends False (the std
    # scaling introduces a difficulty bias); leaving the mean-subtraction baseline intact.
    scale_rewards: bool = False
    # Zero-out the loss on completions that hit ``max_completion_length`` (truncated, so their
    # reward is unreliable) — DAPO's overlong masking; reduces reward noise.
    mask_truncated_completions: bool = True
    # GRPO iterations (mu) per batch of rollouts — the number of optimisation passes reusing
    # one set of generations before resampling (TRL ``num_iterations``). 1 = fully on-policy.
    mu: int = 1

    # --- training length ---
    num_epochs: float = 1.0
    # Total optimiser steps. >0 overrides ``num_epochs`` (TRL ``max_steps``); kept under the
    # scaffold's ``num_iterations`` name. 0 => train for ``num_epochs`` over the prompt set.
    num_iterations: int = 0
    warmup_ratio: float = 0.0
    # LR schedule (TRL/HF ``lr_scheduler_type``). The default stayed ``"linear"`` for every run up
    # to run 8, which decays to exactly 0 over the horizon. ``"cosine_with_min_lr"`` plus
    # ``lr_scheduler_min_lr`` is the way to start hot and land on a non-zero floor: run 9 anneals
    # 8e-5 -> 2e-5 so the late steps still move the policy instead of freezing it.
    #
    # NOTE: the horizon the scheduler decays over is HF's ``num_training_steps``, i.e. ``max_steps``
    # when ``num_iterations > 0`` and otherwise the epoch-derived estimate. Set ``num_iterations``
    # explicitly whenever a non-zero floor matters, or the shape of the curve depends on a
    # dataloader-length guess that changes with the rank count.
    lr_scheduler_type: str = "linear"
    # Absolute LR floor for ``cosine_with_min_lr`` (HF ``lr_scheduler_kwargs["min_lr"]``). ``None``
    # leaves the scheduler's own default, which for the cosine variants is a floor of 0.
    lr_scheduler_min_lr: float | None = None
    logging_steps: int = 1
    save_steps: int = 0
    report_to: str = "none"
    # Continue a previous run from a ``save_steps`` checkpoint instead of starting a fresh LoRA.
    # ``"auto"`` picks the newest ``checkpoint-*`` under ``output_dir`` and starts from scratch
    # when there is none; an explicit path resumes exactly that checkpoint; ``None`` (default)
    # never resumes, so existing configs and reruns behave exactly as before.
    #
    # This matters because the QOS wall is 2 days and a GRPO step is ~93 s: a crash or a timeout
    # at step 600 would otherwise throw away ~15 h. HF Trainer restores optimiser state, LR
    # schedule and step count, so a resumed run continues rather than re-doing work.
    resume_from_checkpoint: str | None = None

    # --- held-out evaluation (generalisation / reward gain) ---
    # Withhold this many prompts from training and score them as a held-out set. 0 = train on
    # everything, which is what runs 1-8 did: every reward number they report is an in-sample
    # number on a prompt the policy was actively being trained on, so none of them can distinguish
    # "the policy got better at benchmarking" from "the policy memorised these subjects".
    #
    # The split is a deterministic seeded shuffle of the *fully filtered* prompt list (after
    # corpus/exclude_projects/max_source_chars/limit_prompts), so it is reproducible from the
    # config alone and stable across a resume. The chosen ids are written to
    # ``output_dir/holdout_prompts.json`` at startup.
    holdout_prompts: int = Field(default=0, ge=0)
    # Independent of ``seed`` so the train/holdout split can be held fixed while the sampling seed
    # changes between runs (and vice versa).
    holdout_seed: int = 12345
    # Score the held-out set every N optimiser steps (HF ``eval_steps``). 0 = never, so the prompts
    # are still withheld but only evaluated offline. Each pass costs
    # ``holdout_prompts x num_generations_eval`` rollouts, which is real wall clock: at 100 prompts
    # and G=8 that is 800 rollouts, comparable to ~200 training steps of reward work.
    holdout_eval_every: int = Field(default=0, ge=0)
    # Also evaluate once before the first optimiser step (HF ``eval_on_start``). This is the t=0
    # baseline that makes the later passes a *gain* rather than a level, and it is measured with
    # the identical prompts, sampling params and reward as every later pass.
    holdout_eval_on_start: bool = True
    # Group size for held-out scoring (TRL ``num_generations_eval``). ``None`` = reuse
    # ``group_size`` so a held-out reward is directly comparable to a training reward.
    num_generations_eval: int | None = None
    # Per-rank eval batch. The global eval generation batch is this x num_processes and must be a
    # multiple of the eval group size, so keep it equal to ``gradient_accumulation_steps`` to give
    # each rank the same number of concurrent reward evaluations it handles during training.
    per_device_eval_batch_size: int = 1

    # --- local diagnostics ---
    # Scalar-only reward telemetry is written under ``output_dir/metrics`` by default. It excludes
    # prompts, completions, generated Java source, Maven logs, and mutation verdict lists.
    diagnostics: bool = True
    diagnostics_dir: str | None = None
    diagnostics_failure_sample_limit: int = Field(default=20, ge=0)
    # TRL's completion tables contain complete model outputs and make remote logs unwieldy. The
    # structured diagnostics above provide safe, compact debugging signals instead.
    log_completions: bool = False

    # --- vLLM rollout generation ---
    # vLLM 0.23 supports Gemma 4 + Transformers 5 and is the newest release supported by
    # TRL 1.7. Colocate is the safe default for a single GPU; server mode requires a distinct
    # CUDA device for the rollout server.
    use_vllm: bool = True
    vllm_mode: Literal["colocate", "server"] = "colocate"
    # Colocate only: fraction of GPU memory vLLM reserves for weights + KV cache; the rest is left
    # for the trainer (frozen bf16 base + LoRA + grad-checkpointed activations). ~0.3 is a safe
    # start for an E2B policy on an 80GB card.
    vllm_gpu_memory_utilization: float = 0.3
    # Gemma 4 reserves multimodal soft-token capacity that is not visible in text tokenizer
    # lengths. Keep headroom beyond max_prompt_length + max_completion_length.
    vllm_max_model_length: int = 16384
    vllm_tensor_parallel_size: int = 1
    # Colocate only: release vLLM's weights and KV cache during the optimiser step. This trades
    # some wake-up latency for substantially lower peak memory when training and inference share.
    vllm_enable_sleep_mode: bool = True
    # Server mode: where the ``trl vllm-serve`` / ``vllm serve`` process is reachable. A full
    # ``vllm_server_base_url`` overrides host/port (e.g. a RunPod proxy URL).
    vllm_server_base_url: str | None = None
    vllm_server_host: str = "0.0.0.0"
    vllm_server_port: int = 8000
    vllm_server_timeout: float = 240.0

    # --- verifiable reward (computed synchronously on the training VM) ---
    # First RL run uses compile + runtime + anti_pattern only (rsd/mutation left at 0).
    reward_weights: RewardWeights = Field(
        default_factory=lambda: RewardWeights(compile=0.34, runtime=0.33, anti_pattern=0.33)
    )
    # Anti-pattern backend: ``regex`` (fast static pre-screen) or ``spotjmh`` (SpotJMHBugs
    # bytecode analysis). ``auto`` prefers SpotJMHBugs when ``vendor/spotbugs`` is installed.
    anti_pattern_backend: Literal["auto", "regex", "spotjmh"] = "regex"
    # Compile-and-run each rollout (False => compile-only, cheaper but no runtime signal).
    need_run: bool = True
    # --- telemetry -------------------------------------------------------------------------
    # Capture the generated Java of every rollout in a step, once every N steps (0 = never).
    #
    # OFF BY DEFAULT AND DELIBERATELY BOUNDED. The metrics writer is otherwise scalar-only -- it
    # never persists prompts, completions, Java source or Maven output -- and that is what makes
    # a metrics directory safe to copy around. Turning this on writes generated benchmark source
    # to metrics/completions.jsonl, which is the only way to answer the two questions the scalar
    # telemetry cannot: how a subject's benchmark changes over training, and how the G rollouts
    # of a single step differ from each other. At every 25 steps with G=8 and ~4 KB of Java per
    # rollout, a 1572-step run costs roughly 2 MB.
    capture_completions_every: int = 0
    # Hard cap on stored source per rollout, so one runaway generation cannot bloat the file.
    capture_completions_max_chars: int = 20000
    # Sample GPU utilisation/memory/power into metrics/gpu.csv every N seconds (0 = off).
    # Cheap (one nvidia-smi per interval) and the only way to see, after the fact, how much of a
    # step the cards spent idle waiting on the CPU-side JMH reward.
    gpu_sample_seconds: float = 10.0
    # Score a group's completions on this many threads. 1 keeps the historical serial path.
    #
    # Reward evaluation is ~half of a GRPO step (measured: 23.6 s of a 48.4 s step, and the step
    # pays the *slowest* rank), while using one core per rank out of 96. Each worker gets its own
    # Maven work root and each JMH invocation its own java.io.tmpdir, so workers never share a
    # project directory or a JMH lock.
    #
    # The cost is measurement noise, and it is not hypothetical: the mutation reward is a >=10 %
    # slowdown test at alpha=0.05 from 5x200 ms measurements, so N benchmarks timing themselves
    # on one host perturb exactly the quantity carrying 70 % of the reward weight. Until that is
    # quantified, keep this at or below the number of rollouts one rank scores per step and watch
    # mutation_score_mean against run 7 (0.69 on eligible rollouts).
    reward_workers: int = 1
    # Apply jmhgen.generate.repair before compiling a rollout: add the JMH/TimeUnit imports the
    # file uses but never imported, rewrite static ``Blackhole.consume`` onto the injected
    # instance, and drop stray markdown fence lines. Those three defects were the root cause of
    # ~27 % of run-2's compile failures — mechanical mistakes, not the API skill the reward is
    # meant to teach, and each one a guaranteed zero that also flattens the group advantage.
    # GRPO-only by design: the RFT/eval scoring path is left byte-identical so JMH-Bench numbers
    # stay comparable across runs.
    repair_completions: bool = False
    rsd_good: float = 0.05
    rsd_bad: float = 0.25
    # Drop these projects from the prompt corpus. Use for subjects whose compile rate is so low
    # that every group is all-zero: those groups have no advantage spread, so they cost a full
    # rollout batch of wall-clock and contribute nothing to the gradient.
    exclude_projects: list[str] = Field(default_factory=list)
    runner: RunnerConfig = Field(default_factory=RunnerConfig)
    # Map CodeSnippet.project -> a ``.cp`` file from ``jmh-provision-classpath``. A rollout for a
    # project with no entry compiles against an empty classpath (fine only for self-contained
    # subjects), so every subject library in the prompt corpus must appear here.
    project_classpaths: dict[str, str] = Field(default_factory=dict)
    # Map CodeSnippet.project -> ``mutants.yaml`` when ``reward_weights.mutation`` > 0. For
    # mutation-scored projects the classpath MUST be the patched jar from ``jmh-provision-mutants``.
    project_mutants: dict[str, str] = Field(default_factory=dict)
    mutation: MutationConfig = Field(default_factory=MutationConfig)


class RemoteConfig(YamlModel):
    """Connection + job description for running a training stage on a remote GPU over SSH.

    Provider-agnostic: any reachable host with SSH works (RunPod / Vast.ai / Lambda / bare
    metal). The runner rsyncs the repo, ``uv sync``s the ``train`` extra, launches
    ``jmh-train-<stage>``, and (optionally) rsyncs the produced checkpoints back.
    """

    host: str
    user: str
    port: int = 22
    # Path to the SSH private key; ``None`` falls back to the agent / default identities.
    identity_file: str | None = None
    # Persistent network volume mount (RunPod: ``/workspace``). Model weights, Maven artifacts,
    # logs, the repository, and outputs survive pod redeploys here.
    remote_volume: str | None = None
    # Optional fast ephemeral directory. When set, the venv plus uv/Triton/Inductor/vLLM caches
    # live here while durable assets remain on ``remote_volume``.
    remote_scratch: str | None = None
    remote_dir: str = "~/jmhgen"
    # Selects an isolated profile project under ``profiles/``.  Profile projects carry their own
    # lockfile because CUDA wheel indexes cannot safely be selected by a root-project extra.
    model_profile: Literal["gemma", "qwen"] = "gemma"
    stage: Literal["sft", "rft", "grpo"] = "sft"
    stage_config: str = "configs/sft/default.yaml"
    # Local env vars forwarded to the remote training process (e.g. the HF token).
    env_passthrough: list[str] = Field(default_factory=lambda: ["HF_TOKEN"])
    rsync_excludes: list[str] = Field(
        default_factory=lambda: [
            ".git",
            ".venv",
            "outputs",
            "__pycache__",
            ".mypy_cache",
            ".pytest_cache",
            ".ruff_cache",
            "data/llm2jmh-*",
            "data/rxjava-src",
            "*.pyc",
        ]
    )
    # Shell command run over SSH before the first rsync push (e.g. install rsync on minimal images).
    bootstrap_cmd: str | None = None
    # Bootstrap command run on the remote when ``uv`` is not already on PATH.
    setup_cmd: str = "curl -LsSf https://astral.sh/uv/install.sh | sh"
    pull_outputs: bool = True
