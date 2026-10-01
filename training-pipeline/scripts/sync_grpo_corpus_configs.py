#!/usr/bin/env python3
"""Sync GRPO YAML ``project_classpaths`` / ``project_mutants`` from the RL corpus registry.

Updates every configs/grpo/jmh-rl*.yaml (and qwen35-4b.yaml) so each corpus project id maps to
``data/classpaths/<id>.cp`` (base) or ``data/classpaths/<id>-mutants.cp`` + mutants.yaml
(mutation configs).

Corpus-scoped, because data/classpaths holds jars for every corpus at once. Pass the corpora
a config set should train on; they are unioned. This is what keeps the repaired corpus and
tier A selectable rather than silently merged::

  uv run python scripts/sync_grpo_corpus_configs.py            # repaired corpus
  uv run python scripts/sync_grpo_corpus_configs.py \
      --corpus configs/grpo/rl-corpus.yaml \
      --corpus configs/grpo/rl-corpus-tierA.verified.yaml \
      --config configs/grpo/mutation70-gemma-all.yaml
"""

from __future__ import annotations

import argparse
import re
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_CORPUS = ROOT / "configs" / "grpo" / "rl-corpus.yaml"
CORPORA: list[Path] = [DEFAULT_CORPUS]
GRPO_DIR = ROOT / "configs" / "grpo"

# Configs that use unpatched classpaths (no mutation reward).
BASE_CONFIGS = ("jmh-rl.yaml", "qwen35-4b.yaml")
# Configs that use patched mutant jars + registries. The mutation70-* set is globbed rather
# than listed: those are the configs training actually runs, and a newly provisioned project
# that never reaches them is invisible to the run no matter how well it built.
MUTATION_CONFIGS = (
    "jmh-rl-full.yaml",
    "jmh-rl-mutation.yaml",
    "jmh-rl-coverage-mutation.yaml",
)
MUTATION_CONFIG_GLOBS = ("mutation70-*.yaml",)


def _all_ids() -> list[str]:
    ids: list[str] = []
    for corpus in CORPORA:
        data = yaml.safe_load(corpus.read_text(encoding="utf-8"))
        for entry in list(data.get("existing") or []) + list(data.get("projects") or []):
            if entry["id"] not in ids:
                ids.append(entry["id"])
    return ids


def _ids_with_sites() -> list[str]:
    ids = []
    for project_id in _all_ids():
        if (ROOT / "data" / "projects" / project_id / "mutation_sites.yaml").exists():
            ids.append(project_id)
    return ids


def _ids_with_base_cp() -> list[str]:
    cp_dir = ROOT / "data" / "classpaths"
    return [
        project_id
        for project_id in _all_ids()
        if (cp_dir / f"{project_id}.cp").exists()
    ]


def _mutant_cp_resolves(cp_dir: Path, project_id: str) -> bool:
    """True when every entry of ``<id>-mutants.cp`` still exists on disk.

    A native build can leave absolute paths into a since-deleted /tmp scratch checkout behind
    (commons-csv did). The file exists, so a bare ``.exists()`` check wires the project into
    training, where every rollout then silently fails to compile. This mirrors the check in
    scripts/build_mutation_corpus.py so the two cannot disagree about what is scorable.
    """
    cp = cp_dir / f"{project_id}-mutants.cp"
    entries = [e.strip() for e in re.split(r"[:\n]", cp.read_text(encoding="utf-8")) if e.strip()]
    return bool(entries) and all((cp_dir / e).exists() for e in entries)


def _ids_with_mutant_cp() -> list[str]:
    cp_dir = ROOT / "data" / "classpaths"
    ids = []
    for project_id in _all_ids():
        if not (cp_dir / f"{project_id}-mutants.cp").exists():
            continue
        if not (cp_dir / f"{project_id}-mutants.jar").exists():
            continue
        if not (ROOT / "data" / "projects" / project_id / "mutants.yaml").exists():
            continue
        if not _mutant_cp_resolves(cp_dir, project_id):
            print(f"WARNING skipping {project_id}: unresolved entry in its mutant classpath")
            continue
        ids.append(project_id)
    return ids


def _replace_block(text: str, key: str, block: str) -> str:
    """Replace a top-level ``key:`` mapping block with ``block`` (including the key line)."""
    pattern = re.compile(
        rf"(?m)^{re.escape(key)}:\n(?:^[ \t].+\n|^[ \t]*\n)*",
    )
    if not pattern.search(text):
        if not text.endswith("\n"):
            text += "\n"
        return text + "\n" + block
    return pattern.sub(block, text, count=1)


def _cp_block(ids: list[str], *, mutants: bool) -> str:
    lines = ["project_classpaths:"]
    for project_id in ids:
        suffix = "-mutants.cp" if mutants else ".cp"
        lines.append(f"  {project_id}: data/classpaths/{project_id}{suffix}")
    return "\n".join(lines) + "\n"


def _mutants_block(ids: list[str]) -> str:
    lines = ["project_mutants:"]
    for project_id in ids:
        lines.append(f"  {project_id}: data/projects/{project_id}/mutants.yaml")
    return "\n".join(lines) + "\n"


def sync_file(path: Path, *, mutants: bool, ids: list[str]) -> None:
    text = path.read_text(encoding="utf-8")
    text = _replace_block(text, "project_classpaths", _cp_block(ids, mutants=mutants))
    if mutants:
        text = _replace_block(text, "project_mutants", _mutants_block(ids))
    path.write_text(text, encoding="utf-8")
    print(f"updated {path.relative_to(ROOT)} ({len(ids)} projects, mutants={mutants})")


def main() -> None:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--corpus", action="append", type=Path, default=None,
                    help="Corpus YAML to wire in; repeatable. Default: the main RL corpus.")
    ap.add_argument("--config", action="append", type=Path, default=None,
                    help="Only sync these configs (mutation-style). Default: the usual set.")
    args = ap.parse_args()

    global CORPORA
    CORPORA = [c if c.is_absolute() else ROOT / c for c in (args.corpus or [DEFAULT_CORPUS])]
    print("corpora: " + ", ".join(c.name for c in CORPORA))

    # Wire configs only to projects that actually have classpaths on disk, so training
    # does not schedule prompts whose SUT jar is missing.
    base_ids = _ids_with_base_cp() or _ids_with_sites()[:6]
    mutant_ids = _ids_with_mutant_cp() or base_ids[:6]

    if args.config:
        # Explicit target list: only these, and always as mutation configs.
        for raw in args.config:
            path = raw if raw.is_absolute() else ROOT / raw
            if path.exists():
                sync_file(path, mutants=True, ids=mutant_ids)
            else:
                print(f"skipping missing config {path}")
        return

    for name in BASE_CONFIGS:
        path = GRPO_DIR / name
        if path.exists():
            sync_file(path, mutants=False, ids=base_ids)
    mutation_paths = [GRPO_DIR / name for name in MUTATION_CONFIGS]
    for pattern in MUTATION_CONFIG_GLOBS:
        mutation_paths.extend(sorted(GRPO_DIR.glob(pattern)))
    seen: set[Path] = set()
    for path in mutation_paths:
        if path in seen or not path.exists():
            continue
        seen.add(path)
        sync_file(path, mutants=True, ids=mutant_ids)


if __name__ == "__main__":
    main()
