#!/usr/bin/env python3
"""Publish a trained adapter (or a merged model) to the Hugging Face Hub.

Repos are created **private** by default -- pass ``--public`` deliberately. The token is read
from ``$HF_TOKEN`` and never printed.

  uv run python scripts/upload_to_hf.py <local_dir> --repo user/name [--card card.md] [--public]
"""

from __future__ import annotations

import argparse
import os
import sys
from pathlib import Path

from huggingface_hub import HfApi

# Training/runtime artefacts that should not end up in a model repo.
IGNORE = [
    "checkpoint-*/*",
    "metrics/*",
    "*.out",
    "*.status",
    "analysis.json",
    "__pycache__/*",
]


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("local_dir", type=Path)
    ap.add_argument("--repo", required=True, help="e.g. bookxd/gemma4-e2b-jmh-grpo")
    ap.add_argument("--card", type=Path, default=None, help="Markdown file to upload as README.md")
    ap.add_argument("--public", action="store_true", help="Create a PUBLIC repo (default private)")
    ap.add_argument("--commit-message", default="Upload JMH GRPO model")
    args = ap.parse_args()

    token = os.environ.get("HF_TOKEN")
    if not token:
        print("FATAL: HF_TOKEN not set", file=sys.stderr)
        return 2
    if not args.local_dir.is_dir():
        print(f"FATAL: {args.local_dir} is not a directory", file=sys.stderr)
        return 2

    api = HfApi(token=token)
    api.create_repo(args.repo, repo_type="model", private=not args.public, exist_ok=True)
    print(f"repo {args.repo} ready (private={not args.public})")

    if args.card and args.card.is_file():
        api.upload_file(
            path_or_fileobj=str(args.card),
            path_in_repo="README.md",
            repo_id=args.repo,
            repo_type="model",
            commit_message="Add model card",
        )
        print("uploaded model card")

    api.upload_folder(
        folder_path=str(args.local_dir),
        repo_id=args.repo,
        repo_type="model",
        ignore_patterns=IGNORE,
        commit_message=args.commit_message,
    )
    print(f"uploaded {args.local_dir} -> https://huggingface.co/{args.repo}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
