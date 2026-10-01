"""Training stages: thin, comparable entrypoints sharing the same core library.

Each of :mod:`jmhgen.training.sft`, :mod:`jmhgen.training.rft`, and
:mod:`jmhgen.training.grpo` exposes a ``train(config)`` and a ``main()`` console entrypoint.
"""

from jmhgen.training.base import prepare_output_dir, set_seed, stage_main

__all__ = ["prepare_output_dir", "set_seed", "stage_main"]
