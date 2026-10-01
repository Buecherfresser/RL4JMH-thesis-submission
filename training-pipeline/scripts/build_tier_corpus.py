#!/usr/bin/env python3
"""Turn a tier of the candidate-project spreadsheet into a bootstrappable corpus YAML.

The catalogue (JMH_RL_candidate_projects_v3.xlsx) holds ~1290 harvested repos. None of the
harvested rows has been cloned or built -- tier is a *paper* judgement from the build file and
git tree. This script converts one tier into the same entry shape ``bootstrap_rl_corpus.py``
already consumes, so the cluster can find out which of them are real.

Deliberately kept separate from configs/grpo/rl-corpus.yaml: those 99 projects are provisioned
and feed training today, and sync_grpo_corpus_configs.py wires every corpus id into the GRPO
configs. Dropping 282 unbuilt projects in there would poison the training configs on the next
sync. Promote entries into the main corpus only once they have a usable mutant classpath.

  uv run --with openpyxl python scripts/build_tier_corpus.py --tier A --category GRPO
  uv run --with openpyxl python scripts/build_tier_corpus.py --tier A --category EVALUATION \
      --out configs/grpo/rl-corpus-tierA-eval.yaml
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path
from typing import Any

import yaml

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_XLSX = Path.home() / "BSc_Thesis" / "JMH_RL_candidate_projects_v3.xlsx"
MAIN_CORPUS = ROOT / "configs" / "grpo" / "rl-corpus.yaml"
HEADER_ROW = 2  # 0-indexed: rows 0-1 are the title and the legend blurb.


def _slug(name: str) -> str:
    s = re.sub(r"[^a-z0-9]+", "-", str(name).strip().lower()).strip("-")
    return re.sub(r"-{2,}", "-", s)


def _existing() -> tuple[set[str], set[str]]:
    """(ids, lowercased owner/repo) already in the provisioned corpus."""
    if not MAIN_CORPUS.exists():
        return set(), set()
    c = yaml.safe_load(MAIN_CORPUS.read_text(encoding="utf-8")) or {}
    entries = list(c.get("existing") or []) + list(c.get("projects") or [])
    return (
        {e["id"] for e in entries},
        {str(e.get("github", "")).lower() for e in entries},
    )


def _rows(xlsx: Path) -> list[dict[str, Any]]:
    try:
        import openpyxl
    except ModuleNotFoundError:
        sys.exit("openpyxl missing -- run with: uv run --with openpyxl python " + __file__)
    ws = openpyxl.load_workbook(xlsx, data_only=True)["Candidate Projects"]
    raw = list(ws.iter_rows(values_only=True))
    hdr = list(raw[HEADER_ROW])
    return [dict(zip(hdr, r)) for r in raw[HEADER_ROW + 1 :] if r and r[1]]


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--xlsx", type=Path, default=DEFAULT_XLSX)
    ap.add_argument("--tier", default="A")
    ap.add_argument("--category", default="GRPO", help="Pipeline role column; '' for any.")
    ap.add_argument("--out", type=Path, default=None)
    ap.add_argument("--mutants-per-project", type=int, default=50)
    ap.add_argument("--max-classes", type=int, default=1200)
    ap.add_argument("--min-classes", type=int, default=20)
    args = ap.parse_args()

    out = args.out or ROOT / "configs" / "grpo" / f"rl-corpus-tier{args.tier}.yaml"
    rows = _rows(args.xlsx)
    known_ids, known_repos = _existing()

    picked: list[dict[str, Any]] = []
    skipped: dict[str, list[str]] = {"already-in-corpus": [], "no-repo": [], "size": [], "dup-id": []}
    seen: set[str] = set()

    for r in rows:
        if str(r.get("Tier") or "").strip() != args.tier:
            continue
        if args.category and str(r.get("Category") or "").strip() != args.category:
            continue
        repo = str(r.get("Owner / Repo") or "").strip().strip("/")
        if not repo or repo.count("/") != 1:
            skipped["no-repo"].append(str(r.get("Project")))
            continue
        if repo.lower() in known_repos:
            skipped["already-in-corpus"].append(repo)
            continue
        n_cls = r.get("Main-source classes") or 0
        if not (args.min_classes <= int(n_cls) <= args.max_classes):
            skipped["size"].append(f"{repo}({n_cls})")
            continue

        pid = _slug(repo.split("/")[1])
        if pid in known_ids or pid in seen:
            pid = _slug(repo.replace("/", "-"))  # disambiguate with the owner
        if pid in known_ids or pid in seen:
            skipped["dup-id"].append(repo)
            continue
        seen.add(pid)

        entry: dict[str, Any] = {"id": pid, "github": repo}
        # detect_build() sniffs pom.xml/build.gradle from the checkout; only record the hint
        # when the catalogue says Gradle, since that is the case worth forcing.
        if str(r.get("Build") or "").strip().lower() == "gradle":
            entry["build"] = "gradle"
        entry["_classes"] = int(n_cls)
        entry["_modules"] = int(r.get("Modules") or 1)
        picked.append(entry)

    # Cheapest first: an overnight run that cannot finish everything should finish the most
    # projects, and small single-module libraries are both quicker and likelier to work.
    picked.sort(key=lambda e: (e["_modules"], e["_classes"], e["id"]))
    for e in picked:
        e.pop("_classes")
        e.pop("_modules")

    doc_lines = [
        f"# Tier {args.tier} candidates ({args.category or 'all categories'}) from",
        f"# {args.xlsx.name}, generated by scripts/build_tier_corpus.py -- do not hand-edit.",
        "#",
        "# NOT the training corpus. Every entry here is unverified: the tier is a paper",
        "# judgement from the build file and git tree, nothing has been cloned or built. Keep it",
        "# out of configs/grpo/rl-corpus.yaml until an entry has a working mutant classpath,",
        "# because sync_grpo_corpus_configs.py wires every corpus id straight into the GRPO",
        "# training configs.",
        "#",
        "# Ordered cheapest-first (modules, then class count) so a truncated run still",
        "# provisions the largest number of projects.",
        "#",
        "#   JMH_RL_CORPUS=" + str(out.relative_to(ROOT)) + " \\",
        "#     uv run python scripts/bootstrap_rl_corpus.py",
        "",
    ]
    payload = {"mutants_per_project": args.mutants_per_project, "projects": picked}
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(
        "\n".join(doc_lines) + yaml.safe_dump(payload, sort_keys=False, width=100),
        encoding="utf-8",
    )

    print(f"wrote {len(picked)} entr(ies) -> {out.relative_to(ROOT)}")
    for reason, items in skipped.items():
        if items:
            print(f"  skipped {len(items):3d}  {reason}: {' '.join(items[:6])}{' ...' if len(items) > 6 else ''}")
    gradle = sum(1 for e in picked if e.get("build") == "gradle")
    print(f"  build: maven={len(picked) - gradle} gradle={gradle}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
