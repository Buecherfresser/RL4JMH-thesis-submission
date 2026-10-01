#!/usr/bin/env python3
"""Filter the merged RL corpus down to the projects the mutation reward can actually score.

``corpus: original`` restricts GRPO to the six hand-curated libraries (113 prompts). The
mutation reward, however, works for every project that has *both* a patched classpath
(``data/classpaths/<id>-mutants.cp`` + jar) and a hidden registry
(``data/projects/<id>/mutants.yaml``). Prompts from any other project compile against nothing
and score 0, so they are dead weight rather than extra signal.

This writes the largest prompt set with a live mutation signal, for use with ``corpus: full``.

Scoped to one or more corpus files, because data/classpaths holds jars for every corpus at
once: without ``--corpus`` the tier-A projects would be folded silently into the main
training set. Name the corpora you want and they are unioned, which is how the repaired
corpus and tier A stay separately trainable::

  uv run python scripts/build_mutation_corpus.py                      # repaired corpus
  uv run python scripts/build_mutation_corpus.py \
      --corpus configs/grpo/rl-corpus-tierA.verified.yaml \
      --out data/snippets/rl-mutation-tierA.good.jsonl
  uv run python scripts/build_mutation_corpus.py \
      --corpus configs/grpo/rl-corpus.yaml \
      --corpus configs/grpo/rl-corpus-tierA.verified.yaml \
      --out data/snippets/rl-mutation-all.good.jsonl
"""

from __future__ import annotations

import argparse
import json
import re
from collections import Counter
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[1]
CLASSPATHS = ROOT / "data" / "classpaths"
PROJECTS = ROOT / "data" / "projects"
SNIPPETS = ROOT / "data" / "snippets"
DEFAULT_CORPUS = ROOT / "configs" / "grpo" / "rl-corpus.yaml"
DEFAULT_DEST = SNIPPETS / "rl-mutation.good.jsonl"


def unresolved_classpath_entries(project_id: str) -> list[str]:
    """Entries of ``<id>-mutants.cp`` that do not exist under data/classpaths.

    Provisioning writes these files as ':'- or newline-separated lists of paths relative to
    data/classpaths. A native Maven/Gradle build can leak an absolute path into a scratch
    directory that is gone by training time (commons-csv did), which silently costs every
    rollout for that project its compile — so treat any unresolved entry as disqualifying.
    """
    cp = CLASSPATHS / f"{project_id}-mutants.cp"
    entries = [e.strip() for e in re.split(r"[:\n]", cp.read_text(encoding="utf-8")) if e.strip()]
    return [e for e in entries if not (CLASSPATHS / e).exists()]


def corpus_ids(paths: list[Path]) -> set[str]:
    """Every project id declared by the given corpus files."""
    ids: set[str] = set()
    for path in paths:
        doc = yaml.safe_load(path.read_text(encoding="utf-8")) or {}
        for entry in list(doc.get("existing") or []) + list(doc.get("projects") or []):
            ids.add(entry["id"])
    return ids


def scorable_projects(within: set[str]) -> set[str]:
    """Ids in ``within`` with a fully resolvable mutant classpath and a mutants registry."""
    ids = set()
    for cp in sorted(CLASSPATHS.glob("*-mutants.cp")):
        project_id = cp.name.removesuffix("-mutants.cp")
        if project_id not in within:
            continue
        if not (CLASSPATHS / f"{project_id}-mutants.jar").exists():
            continue
        if not (PROJECTS / project_id / "mutants.yaml").exists():
            continue
        if unresolved_classpath_entries(project_id):
            continue
        ids.add(project_id)
    return ids


def main() -> None:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--corpus", action="append", type=Path, default=None,
                    help="Corpus YAML to include; repeatable. Default: the main RL corpus.")
    ap.add_argument("--out", type=Path, default=DEFAULT_DEST)
    args = ap.parse_args()

    corpora = args.corpus or [DEFAULT_CORPUS]
    corpora = [c if c.is_absolute() else ROOT / c for c in corpora]
    declared = corpus_ids(corpora)
    allowed = scorable_projects(declared)

    for project_id in sorted(declared):
        cp = CLASSPATHS / f"{project_id}-mutants.cp"
        if project_id in allowed or not cp.exists():
            continue
        broken = unresolved_classpath_entries(project_id)
        if broken:
            print(f"WARNING excluding {project_id}: {len(broken)} unresolved classpath entry/ies")
            print(f"         first: {broken[0]}")

    # Read each project's curated file directly rather than a pre-merged blob: the merged file
    # is only ever a concatenation of these, and a stale one silently drops whole projects.
    kept: list[str] = []
    missing: list[str] = []
    for project_id in sorted(allowed):
        good = SNIPPETS / f"{project_id}.good.jsonl"
        if not good.exists():
            missing.append(project_id)
            continue
        kept.extend(ln for ln in good.read_text(encoding="utf-8").splitlines() if ln.strip())

    out = args.out if args.out.is_absolute() else ROOT / args.out
    out.write_text("\n".join(kept) + "\n", encoding="utf-8")
    per_project: Counter[str] = Counter(json.loads(line)["project"] for line in kept)
    print("corpora: " + ", ".join(c.name for c in corpora))
    print(f"{len(declared)} declared, {len(allowed)} can be mutation-scored")
    print(f"kept {len(kept)} prompt(s) from {len(per_project)} project(s) -> {out.relative_to(ROOT)}")
    if missing:
        print(f"scorable but no curated prompts ({len(missing)}): {' '.join(missing[:10])}")
    unscorable = sorted(declared - allowed)
    print(f"unscorable: {len(unscorable)}")


if __name__ == "__main__":
    main()
