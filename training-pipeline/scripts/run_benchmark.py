#!/usr/bin/env python3
"""Thin wrapper so the runner CLI can be invoked as a script.

Equivalent to the ``jmh-run`` console entrypoint:
    python scripts/run_benchmark.py path/to/MyBenchmark.java
"""

from jmhgen.cli.run_benchmark import main

if __name__ == "__main__":
    main()
