#!/usr/bin/env python3
"""Run selected RFT phases (default: generate, verify, build — no GPU train step)."""

from __future__ import annotations

import argparse

from jmhgen.config.mutation_manifest import load_mutation_manifest
from jmhgen.config.schema import RFTConfig
from jmhgen.training.manifest_job import run_manifest, run_phases


def _parse_phases(raw: str) -> tuple[str, ...]:
    phases = tuple(part.strip() for part in raw.split(",") if part.strip())
    if not phases:
        raise SystemExit("[RFT] --phases must list at least one phase")
    return phases


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Run one or more RFT phases for a single config or a mutation manifest."
    )
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument("--config", help="Path to a single RFT YAML config.")
    group.add_argument(
        "--manifest",
        default=None,
        help=(
            "Unified mutation manifest (runs every project in one job; manifest train "
            "merges project SFT artifacts when the manifest defines train.config)."
        ),
    )
    parser.add_argument(
        "--phases",
        default="generate,verify,build",
        help="Comma-separated phases: generate, verify, build, train (default: no train).",
    )
    parser.add_argument(
        "--bsc-gcp",
        action="store_true",
        help="With --manifest, use each project's bsc_gcp_config instead of config.",
    )
    parser.add_argument(
        "--order",
        choices=("cross-project", "per-project"),
        default="cross-project",
        help=(
            "Manifest scheduling: cross-project runs each phase for all projects before "
            "the next phase (generate all, then verify all — default); per-project runs "
            "generate+verify+build for one corpus before starting the next."
        ),
    )
    args = parser.parse_args()
    phases = _parse_phases(args.phases)

    if args.manifest:
        manifest = load_mutation_manifest(args.manifest)
        run_manifest(manifest, phases, bsc_gcp=args.bsc_gcp, order=args.order)
        return

    config = RFTConfig.from_yaml(args.config)
    run_phases(config, phases)


if __name__ == "__main__":
    main()
