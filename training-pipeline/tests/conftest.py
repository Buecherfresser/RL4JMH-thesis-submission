"""Shared pytest fixtures and helpers."""

from __future__ import annotations

from pathlib import Path

import pytest

RESOURCES = Path(__file__).parent / "resources"


@pytest.fixture(scope="session")
def hello_benchmark_source() -> str:
    return (RESOURCES / "HelloBenchmark.java").read_text(encoding="utf-8")
