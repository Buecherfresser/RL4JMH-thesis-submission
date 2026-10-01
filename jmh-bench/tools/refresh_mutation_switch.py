#!/usr/bin/env python3
"""Rewrite the ``MutationSwitch.java`` hunk inside every vendored ``mutations.patch``.

The mutants themselves are the ``MutationSwitch.tick(<id>)`` call sites baked
into the patch; the switch class is only the machinery that decides what an
armed ``tick`` *does*. Changing that machinery -- adding a latency operator, say
-- must not renumber or move a single mutant, or every previously measured
scorecard stops being comparable to the new ones.

So this tool does not regenerate the patches (``make_project_mutants.py`` would,
and would reshuffle site selection). It replaces exactly the added-file hunk for
``MutationSwitch.java`` with the current template from that generator, reusing
the package and mutant count read back out of the patch it is rewriting. Every
other byte of the patch is left alone.

    uv run python tools/refresh_mutation_switch.py            # rewrite + verify
    uv run python tools/refresh_mutation_switch.py --check    # verify only
"""

from __future__ import annotations

import argparse
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from make_compress_mutants import _SWITCH_TEMPLATE as _COMPRESS_TEMPLATE  # noqa: E402
from make_project_mutants import _SWITCH_TEMPLATE  # noqa: E402

REPO = Path(__file__).resolve().parent.parent
PROJECTS = REPO / "dataset" / "projects"


#: commons-compress predates the generic generator: its switch is emitted by
#: make_compress_mutants.py, which hardcodes the package and carries the ASF
#: license header the file needs to sit inside an Apache-licensed SUT.
_TEMPLATES = {"commons-compress": _COMPRESS_TEMPLATE}


def _switch_source(project: str, package: str, count: int) -> str:
    template = _TEMPLATES.get(project, _SWITCH_TEMPLATE)
    return template.replace("__PACKAGE__", package).replace("__COUNT__", str(count))


def _split_hunk(patch: str) -> tuple[str, str, str]:
    """Return (prefix, switch-hunk, suffix) for the MutationSwitch added-file hunk."""
    starts = [
        m.start()
        for m in re.finditer(r"^diff --git ", patch, re.MULTILINE)
    ]
    for i, start in enumerate(starts):
        end = starts[i + 1] if i + 1 < len(starts) else len(patch)
        block = patch[start:end]
        if "MutationSwitch.java" in block.splitlines()[0]:
            return patch[:start], block, patch[end:]
    raise SystemExit("no MutationSwitch.java hunk found in patch")


def _rebuild_hunk(project: str, old_block: str) -> str:
    """Rebuild the added-file hunk, keeping its path/package/mutant count."""
    header, _, body = old_block.partition("@@")
    if not body:
        raise SystemExit("MutationSwitch hunk has no @@ header")
    added = [ln[1:] for ln in old_block.splitlines() if ln.startswith("+++") is False and ln.startswith("+")]
    old_src = "\n".join(added)
    m = re.search(r"^package ([\w.]+);", old_src, re.MULTILINE)
    if not m:
        raise SystemExit("cannot read the package out of the existing MutationSwitch")
    package = m.group(1)
    m = re.search(r"COUNT = (\d+);", old_src)
    if not m:
        raise SystemExit("cannot read COUNT out of the existing MutationSwitch")
    count = int(m.group(1))

    new_src = _switch_source(project, package, count)
    lines = new_src.split("\n")
    if lines and lines[-1] == "":       # the template ends with a newline
        lines.pop()
    plus = "\n".join("+" + ln for ln in lines)
    # `index <blob>..<blob>` is informational for `git apply` (we never use
    # --3way), and the blob id of a file we just rewrote is not knowable here.
    header = re.sub(r"^index [0-9a-f]+\.\.[0-9a-f]+\n", "", header, flags=re.MULTILINE)
    return f"{header}@@ -0,0 +1,{len(lines)} @@\n{plus}\n"


def _verify(project_dir: Path, patch_path: Path) -> None:
    """Apply the patch to a throwaway copy of the SUT and compile the switch."""
    src_root = project_dir / "src" / "main" / "java"
    if not src_root.is_dir():
        raise SystemExit(f"{project_dir.name}: no src/main/java root to verify against")
    tmp = Path(tempfile.mkdtemp(prefix="jmhbench-switch-verify-"))
    try:
        work = tmp / "project" / "src" / "main" / "java"
        work.parent.mkdir(parents=True)
        shutil.copytree(src_root, work)
        r = subprocess.run(
            ["git", "apply", "--unsafe-paths", "--whitespace=nowarn",
             "--directory=src/main/java", str(patch_path.resolve())],
            cwd=tmp / "project", capture_output=True, text=True,
        )
        if r.returncode != 0:
            raise SystemExit(f"{project_dir.name}: git apply failed: {r.stderr.strip()}")
        switch = next(work.rglob("jmhbench/MutationSwitch.java"), None)
        if switch is None:
            raise SystemExit(f"{project_dir.name}: MutationSwitch.java missing after apply")
        out = tmp / "classes"
        out.mkdir()
        r = subprocess.run(
            ["javac", "-nowarn", "-d", str(out), str(switch)],
            capture_output=True, text=True,
        )
        if r.returncode != 0:
            raise SystemExit(f"{project_dir.name}: javac failed:\n{r.stderr.strip()}")
        print(f"  verified: git apply + javac ok ({project_dir.name})")
    finally:
        shutil.rmtree(tmp, ignore_errors=True)


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--check", action="store_true",
                    help="report whether each patch is already up to date; write nothing")
    args = ap.parse_args()

    stale = []
    for patch_path in sorted(PROJECTS.glob("*/mutations.patch")):
        prefix, block, suffix = _split_hunk(patch_path.read_text(encoding="utf-8"))
        new_block = _rebuild_hunk(patch_path.parent.name, block)
        if new_block == block:
            print(f"{patch_path.parent.name}: up to date")
            continue
        stale.append(patch_path.parent.name)
        if args.check:
            print(f"{patch_path.parent.name}: STALE")
            continue
        patch_path.write_text(prefix + new_block + suffix, encoding="utf-8")
        print(f"{patch_path.parent.name}: rewrote MutationSwitch hunk")
        _verify(patch_path.parent, patch_path)

    if args.check and stale:
        print(f"\n{len(stale)} patch(es) stale: {', '.join(stale)}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
