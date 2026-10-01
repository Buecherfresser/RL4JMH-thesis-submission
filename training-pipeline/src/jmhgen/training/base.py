"""Shared utilities and a common entrypoint pattern for the training stages.

Every stage (SFT / RFT / GRPO) is a thin module that defines a ``train(config)`` function
and delegates argument parsing, seeding, and output setup to :func:`stage_main`. This keeps
the stages comparable: they differ only in their training logic, not their plumbing.
"""

from __future__ import annotations

import argparse
import random
from collections.abc import Callable
from pathlib import Path
from typing import TypeVar

from jmhgen.config.schema import YamlModel
from jmhgen.utils.logging import get_logger

logger = get_logger("jmhgen.training")

_C = TypeVar("_C", bound=YamlModel)


def set_seed(seed: int) -> None:
    """Seed Python's RNG (and numpy/torch if installed) for reproducibility."""
    random.seed(seed)
    try:  # numpy/torch are only present with the 'train' extra
        import numpy as np

        np.random.seed(seed)
    except ImportError:
        pass
    try:
        import torch

        torch.manual_seed(seed)
    except ImportError:
        pass


def prepare_output_dir(path: str | Path) -> Path:
    out = Path(path)
    out.mkdir(parents=True, exist_ok=True)
    return out


def stage_main(
    *,
    stage: str,
    config_cls: type[_C],
    default_config_path: str,
    train_fn: Callable[[_C], None],
    configure_parser: Callable[[argparse.ArgumentParser], None] | None = None,
    apply_cli: Callable[[_C, argparse.Namespace], None] | None = None,
) -> None:
    """Parse ``--config``, load+validate it, seed, prepare output, then run ``train_fn``.

    Optional ``configure_parser`` / ``apply_cli`` let a stage add flags (e.g. GRPO
    ``--corpus``) without forking the shared plumbing.

    A :class:`NotImplementedError` from ``train_fn`` is reported as a clean message (no
    traceback) while the stage is still scaffolded.
    """
    parser = argparse.ArgumentParser(description=f"Run the {stage} training stage.")
    parser.add_argument("--config", default=default_config_path, help="Path to a YAML config.")
    if configure_parser is not None:
        configure_parser(parser)
    args = parser.parse_args()

    config = config_cls.from_yaml(args.config)
    if apply_cli is not None:
        apply_cli(config, args)
    set_seed(getattr(config, "seed", 0))
    output_dir = prepare_output_dir(getattr(config, "output_dir", "outputs"))
    logger.info("[%s] loaded config from %s (output_dir=%s)", stage, args.config, output_dir)

    try:
        train_fn(config)
    except NotImplementedError as exc:
        raise SystemExit(f"[{stage}] not implemented yet: {exc}") from None
