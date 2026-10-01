#!/usr/bin/env python3
"""Move candidate-tier projects that actually provisioned into the training corpus.

A tier YAML (scripts/build_tier_corpus.py) is a list of guesses. After the cluster has tried
them, only some have a planted mutant registry *and* a patched SUT jar whose classpath still
resolves -- those are the ones a GRPO rollout can score. This promotes exactly those into
configs/grpo/rl-corpus.yaml, carrying over the metadata bootstrap discovered (package_root,
source_style, ...) from data/projects/<id>/project.yaml.

Also enforces the class-file ceiling: a jar above major 61 needs a newer javac at rollout time
than some hosts have, so it is reported and skipped unless --allow-any-classfile.

Two ways to take the survivors:

* ``--out-corpus`` writes them to their own corpus file, leaving rl-corpus.yaml alone. This
  keeps the repaired corpus and tier A independently trainable, so the prompt set can be
  built from either or from both -- the default, and what you want while tier A is new.
* no ``--out-corpus`` appends them to rl-corpus.yaml, merging the two for good.

  uv run python scripts/promote_tier_projects.py --dry-run
  uv run python scripts/promote_tier_projects.py \
      --out-corpus configs/grpo/rl-corpus-tierA.verified.yaml
"""

from __future__ import annotations

import argparse
import re
import struct
import zipfile
from pathlib import Path
from typing import Any

import yaml

ROOT = Path(__file__).resolve().parents[1]
CP = ROOT / "data" / "classpaths"
PROJECTS = ROOT / "data" / "projects"
MAIN = ROOT / "configs" / "grpo" / "rl-corpus.yaml"
CEILING = 61


def _max_major(jar: Path) -> int:
    if not jar.exists():
        return 0
    try:
        with zipfile.ZipFile(jar) as z:
            best = 0
            for name in [n for n in z.namelist() if n.endswith(".class")][:40]:
                head = z.read(name)[:8]
                if len(head) >= 8 and head[:4] == b"\xca\xfe\xba\xbe":
                    best = max(best, struct.unpack(">H", head[6:8])[0])
            return best
    except (zipfile.BadZipFile, OSError):
        return -1


def _cp_resolves(cp_file: Path) -> bool:
    if not cp_file.exists():
        return False
    entries = [e.strip() for e in re.split(r"[:\n]", cp_file.read_text(encoding="utf-8")) if e.strip()]
    return bool(entries) and all((CP / e).exists() for e in entries)


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--tier", type=Path, default=ROOT / "configs" / "grpo" / "rl-corpus-tierA.yaml")
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument("--allow-any-classfile", action="store_true")
    ap.add_argument("--out-corpus", type=Path, default=None,
                    help="Write survivors to their own corpus file instead of appending "
                         "them to configs/grpo/rl-corpus.yaml.")
    args = ap.parse_args()

    tier = yaml.safe_load(args.tier.read_text(encoding="utf-8")) or {}
    main_doc = yaml.safe_load(MAIN.read_text(encoding="utf-8")) or {}
    have = {e["id"] for e in (main_doc.get("existing") or []) + (main_doc.get("projects") or [])}

    promote: list[dict[str, Any]] = []
    rejected: dict[str, list[str]] = {
        "no mutant registry": [],
        "no usable mutant classpath": [],
        "above class-file ceiling": [],
        "already in corpus": [],
    }

    for entry in tier.get("projects") or []:
        pid = entry["id"]
        if pid in have:
            rejected["already in corpus"].append(pid)
            continue
        if not (PROJECTS / pid / "mutants.yaml").exists():
            rejected["no mutant registry"].append(pid)
            continue
        if not _cp_resolves(CP / f"{pid}-mutants.cp"):
            rejected["no usable mutant classpath"].append(pid)
            continue
        major = _max_major(CP / f"{pid}-mutants.jar")
        if major > CEILING and not args.allow_any_classfile:
            rejected["above class-file ceiling"].append(f"{pid}={major}")
            continue

        new = {"id": pid, "github": entry["github"]}
        meta_path = PROJECTS / pid / "project.yaml"
        if meta_path.exists():
            meta = yaml.safe_load(meta_path.read_text(encoding="utf-8")) or {}
            # Carry what bootstrap actually discovered, so a re-clone reproduces this tree.
            if meta.get("package_root"):
                new["package_root"] = meta["package_root"]
            tag = str((meta.get("provenance") or {}).get("tag") or "").strip()
            if tag:
                new["ref"] = tag
        for key in ("build", "merge_modules", "source_style", "source_subdir"):
            if entry.get(key) is not None:
                new[key] = entry[key]
        promote.append(new)

    print(f"tier file      : {args.tier.relative_to(ROOT)}")
    print(f"candidates     : {len(tier.get('projects') or [])}")
    print(f"PROMOTABLE     : {len(promote)}")
    for reason, items in rejected.items():
        if items:
            print(f"  rejected {len(items):3d}  {reason}")
    if promote:
        print("\npromoting: " + " ".join(e["id"] for e in promote))

    if args.dry_run or not promote:
        print("\n(dry run -- nothing written)" if args.dry_run else "\nnothing to promote")
        return 0

    if args.out_corpus:
        out = args.out_corpus if args.out_corpus.is_absolute() else ROOT / args.out_corpus
        header = [
            f"# Verified survivors of {args.tier.name} -- generated by",
            "# scripts/promote_tier_projects.py, do not hand-edit.",
            "#",
            "# Every entry here has a planted mutant registry, a patched SUT jar whose classpath",
            f"# still resolves, and class-file major <= {CEILING}. Kept separate from",
            "# configs/grpo/rl-corpus.yaml so the repaired corpus and this one stay independently",
            "# trainable; build a prompt set from either, or from both:",
            "#",
            "#   uv run python scripts/build_mutation_corpus.py \\",
            f"#       --corpus {out.relative_to(ROOT)} \\",
            "#       --out data/snippets/rl-mutation-tierA.good.jsonl",
            "",
        ]
        payload = {"mutants_per_project": tier.get("mutants_per_project", 50), "projects": promote}
        out.parent.mkdir(parents=True, exist_ok=True)
        out.write_text(
            "\n".join(header) + yaml.safe_dump(payload, sort_keys=False, width=100),
            encoding="utf-8",
        )
        print(f"\nwrote {len(promote)} entr(ies) -> {out.relative_to(ROOT)}")
        print(f"next: uv run python scripts/build_mutation_corpus.py --corpus {out.relative_to(ROOT)} \\")
        print("        --out data/snippets/rl-mutation-tierA.good.jsonl")
        return 0

    # Appending merges the tiers permanently; keep the header comments by inserting the new
    # entries as text rather than re-dumping the document.
    lines = MAIN.read_text(encoding="utf-8").rstrip("\n").split("\n")
    rendered = [
        "  - " + yaml.safe_dump(e, default_flow_style=True, sort_keys=False, width=10_000).strip()
        for e in promote
    ]
    MAIN.write_text("\n".join(lines + rendered) + "\n", encoding="utf-8")
    print(f"\nappended {len(promote)} entr(ies) to {MAIN.relative_to(ROOT)}")
    print("next: uv run python scripts/build_mutation_corpus.py && "
          "uv run python scripts/sync_grpo_corpus_configs.py")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
