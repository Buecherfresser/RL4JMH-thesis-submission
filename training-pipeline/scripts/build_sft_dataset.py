#!/usr/bin/env python3
"""Thin wrapper so the SFT dataset builder can be invoked as a script.

Equivalent to the ``jmh-build-sft`` console entrypoint:
    python scripts/build_sft_dataset.py --config configs/data/sft.yaml
"""

from jmhgen.cli.build_sft import main

if __name__ == "__main__":
    main()
