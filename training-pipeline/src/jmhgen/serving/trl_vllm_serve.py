"""Text-only adapter for TRL 1.7.1's ``vllm-serve`` command.

TRL's server mode exposes private rollout and weight-synchronisation endpoints
that ``trl.GRPOTrainer`` needs, but its 1.7.1 CLI does not forward vLLM's
``--language-model-only`` option.  This module keeps TRL's server and replaces
only its worker factory so Qwen3.5 can skip multimodal initialisation.

It also forces ``multiprocessing`` start method ``spawn`` before TRL forks
workers. Importing torch/vLLM in the parent and then forking is a common cause
of CUDA hangs on H100 nodes (server stuck at ``Waiting for application startup``).
"""

from __future__ import annotations

import importlib.metadata
import inspect
import json
import multiprocessing as mp
import os
import sys
from collections.abc import Sequence
from typing import Any

_SUPPORTED_TRL_VERSION = "1.7.1"
_LANGUAGE_MODEL_ONLY_FLAG = "--language-model-only"

# Qwen3.5 is a hybrid gated-delta-net model, and vLLM's default GDN prefill backend
# ("auto" -> flashinfer) is **JIT-compiled at engine start**:
#   WARNING [gdn/qwen_gdn_linear_attn.py:235] FlashInfer GDN prefill is JIT-compiled;
#           first run may take a while.
# FlashInfer's JIT shells out to `ninja` and needs nvcc. On hpi2 neither is reliably on the
# serve process's PATH, and the failure is invisible: the EngineCore dies (or spends minutes
# stat-ing GPFS looking for CUDA binaries in humming.utils.cuda.find_all_cuda_paths) while
# TRL's server sits at "Waiting for application startup." forever, never surfacing the child's
# error. That is jobs 2343911, 2362641 and 2384618.
#
# `triton` avoids the JIT entirely and its GDN kernels are already warm in the Triton cache
# (.cache/triton/**/chunk_scaled_dot_kkt_fwd_kernel.cubin). Override with
# JMHGEN_GDN_PREFILL_BACKEND=flashinfer|triton|cutedsl, or empty to leave vLLM's default.
_GDN_PREFILL_BACKEND_ENV = "JMHGEN_GDN_PREFILL_BACKEND"
_DEFAULT_GDN_PREFILL_BACKEND = "triton"


def consume_language_model_only(argv: Sequence[str]) -> tuple[list[str], bool]:
    """Remove the JMHGen-only flag before TRL parses its own arguments."""
    args = list(argv)
    enabled = _LANGUAGE_MODEL_ONLY_FLAG in args
    return [arg for arg in args if arg != _LANGUAGE_MODEL_ONLY_FLAG], enabled


def _force_spawn_start_method() -> None:
    """Prefer spawn so TRL's worker process does not inherit a CUDA-touched parent."""
    try:
        current = mp.get_start_method(allow_none=True)
    except RuntimeError:
        current = None
    if current == "spawn":
        return
    try:
        mp.set_start_method("spawn", force=True)
    except RuntimeError as exc:
        # Already started under another method in this interpreter; log and continue.
        print(
            f"[jmhgen-trl-vllm-serve] warning: could not force spawn (current={current!r}): {exc}",
            file=sys.stderr,
        )


def _assert_compatible_trl() -> None:
    installed = importlib.metadata.version("trl")
    if installed != _SUPPORTED_TRL_VERSION:
        raise RuntimeError(
            "jmhgen's text-only TRL server is pinned to "
            f"TRL {_SUPPORTED_TRL_VERSION}, but {installed} is installed. "
            "Update the adapter and its tests before using another TRL version."
        )


def _assert_vllm_accepts_text_only() -> None:
    from vllm import LLM
    from vllm.engine.arg_utils import EngineArgs

    parameters = inspect.signature(LLM).parameters.values()
    accepts_keywords = any(parameter.kind is parameter.VAR_KEYWORD for parameter in parameters)
    engine_args = inspect.signature(EngineArgs).parameters
    if (
        "language_model_only" not in inspect.signature(LLM).parameters and not accepts_keywords
    ) or "language_model_only" not in engine_args:
        raise RuntimeError(
            "The installed vLLM wheel does not expose language_model_only. "
            "Install the pinned Qwen CUDA-12.8 wheel that supports "
            "--language-model-only."
        )


def gdn_prefill_backend() -> str | None:
    """Which GDN prefill backend to force, or ``None`` to leave vLLM's default alone."""
    backend = os.environ.get(_GDN_PREFILL_BACKEND_ENV, _DEFAULT_GDN_PREFILL_BACKEND).strip()
    return backend or None


def llm_worker(
    script_args: Any,
    data_parallel_rank: int,
    master_port: int,
    connection: Any,
) -> None:
    """TRL 1.7.1 worker with the vLLM text-only argument added."""
    from vllm import LLM

    os.environ["VLLM_DP_RANK"] = str(data_parallel_rank)
    os.environ["VLLM_DP_RANK_LOCAL"] = str(data_parallel_rank)
    os.environ["VLLM_DP_SIZE"] = str(script_args.data_parallel_size)
    os.environ["VLLM_DP_MASTER_PORT"] = str(master_port)

    backend = gdn_prefill_backend()
    additional_config: dict[str, Any] = {}
    if backend:
        # vLLM's --gdn-prefill-backend lands in additional_config (see engine/arg_utils.py);
        # TRL 1.7.1's parser cannot forward the flag, so set it here.
        additional_config["gdn_prefill_backend"] = backend
        print(f"[jmhgen-trl-vllm-serve] gdn_prefill_backend={backend}", flush=True)

    llm = LLM(
        model=script_args.model,
        revision=script_args.revision,
        tensor_parallel_size=script_args.tensor_parallel_size,
        gpu_memory_utilization=script_args.gpu_memory_utilization,
        enforce_eager=script_args.enforce_eager,
        dtype=script_args.dtype,
        enable_prefix_caching=script_args.enable_prefix_caching,
        kv_cache_dtype=script_args.kv_cache_dtype,
        max_model_len=script_args.max_model_len,
        worker_extension_cls="trl.scripts.vllm_serve.WeightSyncWorkerExtension",
        trust_remote_code=script_args.trust_remote_code,
        model_impl=script_args.vllm_model_impl,
        distributed_executor_backend=script_args.distributed_executor_backend,
        logprobs_mode="processed_logprobs",
        speculative_config=(
            json.loads(script_args.speculative_config) if script_args.speculative_config else None
        ),
        language_model_only=True,
        **({"additional_config": additional_config} if additional_config else {}),
    )

    connection.send({"status": "ready"})
    while True:
        try:
            command = connection.recv()
        except KeyboardInterrupt:
            llm.collective_rpc(method="close_communicator")
            break

        if command["type"] in ["call", "fire_and_forget"]:
            method = getattr(llm, command["method"])
            result = method(*command.get("args", ()), **command.get("kwargs", {}))
            if command["type"] == "call":
                connection.send(result)
        elif command["type"] == "shutdown":
            break


def main(argv: Sequence[str] | None = None) -> None:
    """Run TRL's server after installing the Qwen text-only worker."""
    from trl.scripts import vllm_serve as trl_vllm_serve

    raw_args = list(sys.argv[1:] if argv is None else argv)
    trl_args, language_model_only = consume_language_model_only(raw_args)
    if not language_model_only:
        raise SystemExit(f"{_LANGUAGE_MODEL_ONLY_FLAG} is required for Qwen3.5 text-only serving.")
    _assert_compatible_trl()
    _assert_vllm_accepts_text_only()
    _force_spawn_start_method()

    if "--help" in trl_args:
        print(
            "\nJMHGen addition:\n"
            f"  {_LANGUAGE_MODEL_ONLY_FLAG}  Required; disables Qwen3.5 multimodal "
            "initialisation.\n"
        )
    sys.argv = [sys.argv[0], *trl_args]
    trl_vllm_serve.llm_worker = llm_worker
    parser = trl_vllm_serve.make_parser(prog="jmhgen-trl-vllm-serve")
    (script_args,) = parser.parse_args_and_config()
    trl_vllm_serve.main(script_args)


if __name__ == "__main__":
    main()
