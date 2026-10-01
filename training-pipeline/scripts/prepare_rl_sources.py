#!/usr/bin/env python3
"""Ensure every GRPO corpus project has a checkout + the source tree project.yaml expects.

The first bsc-gcp pass failed because:
* ``existing`` (original six) were never cloned
* ``*-merged`` trees were built only on the laptop and never recreated remotely
* ``project.yaml`` still pointed at those missing merged paths

This script clones every corpus entry (existing + projects) and rebuilds whatever
``data/projects/<id>/project.yaml`` ``source_dir`` needs (plain, merged, src_root, ejml, jdk).

  uv run python scripts/prepare_rl_sources.py
  uv run python scripts/prepare_rl_sources.py --only commons-codec,gson,guava
"""

from __future__ import annotations

import argparse
import importlib.util
import sys
from pathlib import Path
from typing import Any

import yaml

ROOT = Path(__file__).resolve().parents[1]


def _load_bootstrap() -> Any:
    path = ROOT / "scripts" / "bootstrap_rl_corpus.py"
    spec = importlib.util.spec_from_file_location("bootstrap_rl_corpus", path)
    if spec is None or spec.loader is None:
        raise RuntimeError(f"cannot load {path}")
    mod = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(mod)
    return mod


def _all_entries(corpus: dict[str, Any]) -> list[dict[str, Any]]:
    return list(corpus.get("existing") or []) + list(corpus.get("projects") or [])


def prepare_one(boot: Any, entry: dict[str, Any], *, force_remerge: bool) -> dict[str, Any]:
    project_id = entry["id"]
    print(f"\n== [{project_id}] prepare sources ==", flush=True)
    meta_path = boot.PROJECTS / project_id / "project.yaml"
    expected: Path | None = None
    if meta_path.exists():
        meta = yaml.safe_load(meta_path.read_text(encoding="utf-8")) or {}
        src = meta.get("source_dir")
        if src:
            expected = (boot.ROOT / src).resolve() if not Path(src).is_absolute() else Path(src)

    if force_remerge and expected is not None and "merged" in str(expected):
        merged_root = expected
        # climb to .../<id>-merged
        for parent in [expected, *expected.parents]:
            if parent.name.endswith("-merged"):
                if parent.exists():
                    import shutil

                    print(f"  removing stale {parent}")
                    shutil.rmtree(parent)
                break

    checkout = boot._clone(entry)
    entry = dict(entry)
    if force_remerge:
        entry["_force_remerge"] = True
    resolved = boot._resolve_src_main(entry, checkout or boot.EXTERNAL / project_id)
    resolved = resolved.resolve()

    if expected is not None and expected != resolved:
        # project.yaml may point at a merged path; recreate that exact path if needed.
        if not expected.exists():
            if "merged" in expected.as_posix() or entry.get("merge_modules") or entry.get(
                "source_style"
            ) in {"ejml", "src_root", "jdk_srczip"}:
                # Ensure parent and copy/symlink resolved tree into expected location.
                expected.parent.mkdir(parents=True, exist_ok=True)
                if resolved.exists() and not expected.exists():
                    import shutil

                    print(f"  aligning source_dir: {resolved} -> {expected}")
                    if expected.parent.exists() and expected.parent != resolved.parent:
                        # expected is .../merged/src/main/java
                        shutil.copytree(resolved, expected, dirs_exist_ok=True)
            else:
                raise FileNotFoundError(
                    f"{project_id}: project.yaml source_dir missing: {expected} "
                    f"(resolved {resolved})"
                )
        check = expected if expected.exists() else resolved
    else:
        check = resolved

    n_java = sum(1 for _ in check.rglob("*.java"))
    if n_java == 0:
        raise RuntimeError(f"{project_id}: no .java files under {check}")
    print(f"  ok {check} ({n_java} java files)")
    return {"id": project_id, "status": "ok", "source": str(check), "java_files": n_java}


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--only", default="", help="Comma-separated project ids.")
    parser.add_argument(
        "--force-remerge",
        action="store_true",
        help="Delete and rebuild *-merged trees.",
    )
    args = parser.parse_args()

    boot = _load_bootstrap()
    corpus = boot._load_corpus()
    entries = _all_entries(corpus)
    only = {x.strip() for x in args.only.split(",") if x.strip()}
    if only:
        entries = [e for e in entries if e["id"] in only]

    ok: list[str] = []
    failed: list[str] = []
    for entry in entries:
        try:
            prepare_one(boot, entry, force_remerge=args.force_remerge)
            ok.append(entry["id"])
        except Exception as exc:  # noqa: BLE001
            print(f"  ERROR: {exc}", file=sys.stderr, flush=True)
            failed.append(entry["id"])

    print("\n== summary ==")
    print(f"  ok={len(ok)} failed={len(failed)} total={len(entries)}")
    if failed:
        print("  failures: " + ", ".join(failed))
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
