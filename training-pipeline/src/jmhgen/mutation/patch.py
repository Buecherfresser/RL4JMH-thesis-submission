"""Apply / revert the mutation patch against a subject source tree.

The patch (``mutations.patch``) is a single unified diff written relative to
``src/main/java``; it adds the ``MutationSwitch`` helper and inserts a dormant
``MutationSwitch.tick(<id>)`` at the entry of each mutated method. We apply it with
``git apply --directory=src/main/java`` (ported from JMH-Bench ``jmhbench/project.py``), so
the same patch applies to any copy of the source tree regardless of where it lives on disk.
"""

from __future__ import annotations

import subprocess
from pathlib import Path


class PatchError(RuntimeError):
    """Raised when applying (or reverting) the mutation patch fails."""


def _git_apply(
    project_dir: Path, patch_path: Path, *, reverse: bool
) -> subprocess.CompletedProcess:
    args = [
        "git",
        "apply",
        "--unsafe-paths",
        "--whitespace=nowarn",
        "--ignore-whitespace",
        "--directory=src/main/java",
    ]
    if reverse:
        args.append("--reverse")
    args.append(str(patch_path.resolve()))
    return subprocess.run(  # noqa: S603 - args built from trusted inputs
        args, cwd=project_dir, capture_output=True, text=True, check=False
    )


def apply_patch(project_dir: str | Path, patch_path: str | Path) -> None:
    """Apply the mutation patch to the SUT under ``project_dir/src/main/java``."""
    project = Path(project_dir)
    patch = Path(patch_path)
    if not patch.exists():
        raise PatchError(f"patch file not found: {patch}")
    result = _git_apply(project, patch, reverse=False)
    if result.returncode != 0:
        raise PatchError(f"failed to apply {patch.name}: {result.stderr.strip()}")


def revert_patch(project_dir: str | Path, patch_path: str | Path) -> None:
    """Best-effort inverse of :func:`apply_patch` (no-op if the patch is absent)."""
    patch = Path(patch_path)
    if not patch.exists():
        return
    _git_apply(Path(project_dir), patch, reverse=True)
