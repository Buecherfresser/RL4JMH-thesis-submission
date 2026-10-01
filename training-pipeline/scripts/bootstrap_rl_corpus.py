#!/usr/bin/env python3
"""Bootstrap the extended GRPO RL corpus: clone libraries and plant 50 mutants each.

Reads ``configs/grpo/rl-corpus.yaml``, shallow-clones each new project under
``data/external-src/<id>``, optionally merges multi-module ``src/main/java`` trees, runs
heuristic ``jmh-make-mutants --count 50`` (which writes ``mutation_sites.yaml``), and scaffolds
``project.yaml`` + ``good_classes.yaml`` from the planted sites.

Existing corpus projects (the original six) are skipped unless ``--include-existing``.

Examples::

    uv run python scripts/bootstrap_rl_corpus.py
    uv run python scripts/bootstrap_rl_corpus.py --only guava,gson,jsoup
    uv run python scripts/bootstrap_rl_corpus.py --skip-clone   # re-plant from cached checkouts
"""

from __future__ import annotations

import argparse
import os
import re
import shutil
import subprocess
import sys
from collections import Counter
from pathlib import Path
from typing import Any

import yaml

ROOT = Path(__file__).resolve().parents[1]
# Overridable so an unverified candidate tier can be bootstrapped without touching the
# provisioned training corpus (see scripts/build_tier_corpus.py).
CORPUS_PATH = Path(os.environ.get("JMH_RL_CORPUS") or ROOT / "configs" / "grpo" / "rl-corpus.yaml")
if not CORPUS_PATH.is_absolute():
    CORPUS_PATH = ROOT / CORPUS_PATH
EXTERNAL = ROOT / "data" / "external-src"
PROJECTS = ROOT / "data" / "projects"


def _run(cmd: list[str], *, cwd: Path | None = None, check: bool = True) -> subprocess.CompletedProcess[str]:
    print(f"  $ {' '.join(cmd)}", flush=True)
    return subprocess.run(
        cmd,
        cwd=cwd or ROOT,
        check=check,
        text=True,
        capture_output=True,
    )


def _load_corpus() -> dict[str, Any]:
    return yaml.safe_load(CORPUS_PATH.read_text(encoding="utf-8"))


def _display_name(github: str) -> str:
    return github.split("/", 1)[-1]


def _detect_package_root(src_main: Path) -> str | None:
    """Pick the most common 3-segment package prefix under ``src_main``."""
    counts: Counter[str] = Counter()
    for path in src_main.rglob("*.java"):
        rel = path.relative_to(src_main).as_posix()
        if rel.endswith("package-info.java") or rel.endswith("module-info.java"):
            continue
        parts = Path(rel).parts
        if len(parts) < 2:
            continue
        # Prefer 3-segment roots when available (org.apache.commons), else 2.
        if len(parts) >= 4:
            counts[".".join(parts[:3])] += 1
        elif len(parts) >= 3:
            counts[".".join(parts[:2])] += 1
        else:
            counts[parts[0]] += 1
    if not counts:
        return None
    return counts.most_common(1)[0][0]


_SKIP_PATH_PARTS = (
    "/examples/",
    "/example/",
    "/jmh/",
    "/test/",
    "/tests/",
    "/benchmark/",
    "/benchmarks/",
    "/microbench/",
    "/integration/",
    "/it/",
    "/samples/",
    "/demo/",
    "-examples/",
)


def _merge_module_sources(repo: Path, out_java: Path, *, package_root: str | None = None) -> None:
    if out_java.exists() and any(out_java.rglob("*.java")):
        print(f"  cached merged source {out_java}")
        return
    print(f"  merge module sources -> {out_java}")
    out_java.mkdir(parents=True, exist_ok=True)
    prefix = package_root.replace(".", "/") + "/" if package_root else None
    for java in repo.rglob("*.java"):
        posix = java.as_posix()
        if "/src/main/java/" not in posix:
            continue
        # Match the skip tokens against the path *inside the checkout*, never the absolute
        # path: tokens like "/jmh/" and "/it/" are common directory names on the way to a
        # repo. On LRZ the tree lives under .../go68bef2/jmh/JMH_Training_Pipeline, so an
        # absolute match skipped every file of every merged project and produced 35 empty
        # source trees, while the same code worked on hosts whose paths happened not to collide.
        rel_posix = "/" + java.relative_to(repo).as_posix()
        if any(skip in rel_posix for skip in _SKIP_PATH_PARTS):
            continue
        # Drop test-flavoured module directory names even when they use src/main/java.
        lower = rel_posix.lower()
        if any(
            part in lower
            for part in (
                "/metrics/",
                "/jpms_test/",
                "/native_test/",
                "/proto/",
                "/protobuf/",
                "/extras/",
            )
        ):
            continue
        rel = posix.split("/src/main/java/", 1)[1]
        if prefix and not rel.startswith(prefix):
            continue
        dest = out_java / rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        if not dest.exists():
            shutil.copy2(java, dest)


def _merge_ejml_sources(repo: Path, out_java: Path) -> None:
    """EJML uses ``main/<module>/src/org/...`` (no ``src/main/java``)."""
    if out_java.exists() and any(out_java.rglob("*.java")):
        print(f"  cached merged source {out_java}")
        return
    print(f"  merge ejml sources -> {out_java}")
    out_java.mkdir(parents=True, exist_ok=True)
    for java in (repo / "main").rglob("*.java"):
        posix = java.as_posix()
        # Relative, for the same reason as _merge_module_sources: an absolute match makes the
        # result depend on what the checkout happens to be nested under.
        rel_posix = "/" + java.relative_to(repo).as_posix()
        if "/test/" in rel_posix or "/benchmarks/" in rel_posix or "/examples/" in rel_posix:
            continue
        if "/src/org/" not in posix:
            continue
        rel = posix.split("/src/", 1)[1]
        dest = out_java / rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        if not dest.exists():
            shutil.copy2(java, dest)


def _copy_src_root(repo: Path, out_java: Path, *, package_root: str | None) -> None:
    """Guava/dsiutils style: sources live under ``src/<package>/...`` (no main/java)."""
    if out_java.exists() and any(out_java.rglob("*.java")):
        print(f"  cached merged source {out_java}")
        return
    src = repo / "src"
    if not src.exists():
        raise FileNotFoundError(f"no src/ under {repo}")
    print(f"  copy src-root sources -> {out_java}")
    out_java.mkdir(parents=True, exist_ok=True)
    prefix = package_root.replace(".", "/") + "/" if package_root else None
    for java in src.rglob("*.java"):
        rel = java.relative_to(src).as_posix()
        if prefix and not rel.startswith(prefix):
            continue
        dest = out_java / rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        if not dest.exists():
            shutil.copy2(java, dest)


def _find_jdk_src_zip() -> Path:
    """Locate JDK ``src.zip`` on macOS or Linux."""
    import os

    candidates: list[Path] = []
    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        candidates.append(Path(java_home) / "lib" / "src.zip")
    java_home_bin = Path("/usr/libexec/java_home")
    if java_home_bin.exists():
        try:
            home = subprocess.check_output([str(java_home_bin)], text=True).strip()
            candidates.append(Path(home) / "lib" / "src.zip")
        except subprocess.CalledProcessError:
            pass
    jvm_root = Path("/usr/lib/jvm")
    if jvm_root.exists():
        candidates.extend(sorted(jvm_root.glob("*/lib/src.zip")))
        candidates.extend(sorted(jvm_root.glob("*/src.zip")))
    for path in candidates:
        if path.exists():
            return path
    raise FileNotFoundError(
        "JDK src.zip not found (set JAVA_HOME or install a JDK with sources)"
    )


def _extract_jdk_concurrent(out_java: Path) -> None:
    """Plant from the local JDK ``src.zip`` ``java.util.concurrent`` sources (jsr166 stand-in)."""
    if out_java.exists() and any(out_java.rglob("*.java")):
        print(f"  cached jdk concurrent sources {out_java}")
        return
    import zipfile

    src_zip = _find_jdk_src_zip()
    print(f"  extract java.util.concurrent from {src_zip}")
    out_java.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(src_zip) as zf:
        names = zf.namelist()
        # Layouts differ: java.base/java/util/concurrent/... or java/util/concurrent/...
        prefixes = (
            "java.base/java/util/concurrent/",
            "java/util/concurrent/",
        )
        written = 0
        for name in names:
            if not name.endswith(".java"):
                continue
            rel = None
            for prefix in prefixes:
                if name.startswith(prefix):
                    rel = "java/util/concurrent/" + name[len(prefix) :]
                    break
            if rel is None:
                continue
            dest = out_java / rel
            dest.parent.mkdir(parents=True, exist_ok=True)
            dest.write_bytes(zf.read(name))
            written += 1
    if written == 0:
        raise RuntimeError(f"no java.util.concurrent sources inside {src_zip}")


def _resolve_src_main(entry: dict[str, Any], checkout: Path) -> Path:
    """Return the java source root used for planting (checkout or merged tree)."""
    style = entry.get("source_style")
    if style == "jdk_srczip":
        merged = EXTERNAL / f"{entry['id']}-merged" / "src" / "main" / "java"
        _extract_jdk_concurrent(merged)
        return merged

    sub = entry.get("source_subdir")
    base = checkout / sub if sub else checkout
    # source_subdir may itself be a nested path (e.g. ObjectLayout).
    if sub and not base.exists() and "/" in str(sub):
        base = checkout.joinpath(*str(sub).split("/"))

    if style == "ejml":
        merged = EXTERNAL / f"{entry['id']}-merged" / "src" / "main" / "java"
        _merge_ejml_sources(base, merged)
        return merged
    if style == "src_root":
        merged = EXTERNAL / f"{entry['id']}-merged" / "src" / "main" / "java"
        _copy_src_root(base, merged, package_root=entry.get("package_root"))
        return merged

    top = base / "src" / "main" / "java"
    module_mains = [
        p
        for p in base.rglob("src/main/java")
        if p.is_dir() and "src/test" not in p.as_posix()
    ]
    want_merge = bool(entry.get("merge_modules")) or (not top.exists() and len(module_mains) > 0)
    if want_merge:
        merged = EXTERNAL / f"{entry['id']}-merged" / "src" / "main" / "java"
        if merged.exists() and entry.get("_force_remerge"):
            shutil.rmtree(merged.parent)
        _merge_module_sources(base, merged, package_root=entry.get("package_root"))
        return merged
    if top.exists():
        return top
    # Some repos put sources at the root or under java/.
    for alt in (base / "java" / "src" / "main" / "java", base / "java", base / "src"):
        if alt.exists() and any(alt.rglob("*.java")):
            return alt
    if any(base.rglob("*.java")):
        return base
    raise FileNotFoundError(f"no Java sources under {base}")


_SHA_RE = re.compile(r"^[0-9a-f]{40}$")


def _pinned_commit(project_id: str) -> str | None:
    """The commit ``data/projects/<id>/project.yaml`` recorded when the mutants were planted.

    Most corpus entries carry no ``ref``, so a plain ``git clone --depth 1`` takes whatever the
    default branch points at *today*. ``mutations.patch`` was generated against the tree at
    bootstrap time, so on a host with no existing checkout (a fresh cluster) every moved
    project would fail with "patch does not apply" — and the mutant registry would silently
    stop describing the source. Pinning to the recorded commit makes the corpus reproducible
    on any host.
    """
    meta = PROJECTS / project_id / "project.yaml"
    if not meta.exists():
        return None
    data = yaml.safe_load(meta.read_text(encoding="utf-8")) or {}
    tag = str((data.get("provenance") or {}).get("tag") or "").strip()
    return tag or None


def _clone_at_commit(url: str, dest: Path, sha: str) -> None:
    """Fetch exactly ``sha`` — ``git clone --branch`` cannot take a bare commit id."""
    dest.mkdir(parents=True, exist_ok=True)
    _run(["git", "init", "-q"], cwd=dest)
    _run(["git", "remote", "add", "origin", url], cwd=dest, check=False)
    fetched = _run(
        ["git", "fetch", "--depth", "1", "--filter=blob:none", "origin", sha],
        cwd=dest,
        check=False,
    )
    if fetched.returncode != 0:
        # Some servers refuse fetch-by-sha (uploadpack.allowReachableSHA1InWant off).
        print(f"  shallow fetch of {sha[:12]} refused; falling back to a full clone")
        _run(["git", "fetch", "--filter=blob:none", "origin"], cwd=dest)
    _run(["git", "checkout", "-q", "--detach", sha], cwd=dest)


def _clone(entry: dict[str, Any]) -> Path | None:
    """Clone the project checkout, or return None when sources come from elsewhere."""
    if entry.get("source_style") == "jdk_srczip":
        return None
    project_id = entry["id"]
    github = entry["github"]
    dest = EXTERNAL / project_id
    url = f"https://github.com/{github}.git"
    # The recorded commit wins over the corpus ref: it is what mutations.patch was built
    # against. Falls back to `ref` for entries bootstrapped before provenance was written.
    pin = _pinned_commit(project_id) or entry.get("ref")
    pin = str(pin) if pin else None
    pinned_sha = bool(pin and _SHA_RE.match(pin))

    if dest.exists() and any(dest.iterdir()):
        if pin and (dest / ".git").exists():
            if pinned_sha:
                current = (
                    _run(["git", "rev-parse", "HEAD"], cwd=dest, check=False).stdout or ""
                ).strip()
                matches = current == pin
            else:
                head = _run(["git", "rev-parse", "--abbrev-ref", "HEAD"], cwd=dest, check=False)
                describe = _run(
                    ["git", "describe", "--tags", "--exact-match"], cwd=dest, check=False
                )
                current = (describe.stdout or head.stdout or "").strip()
                matches = current == pin
            if not matches:
                print(f"  refreshing checkout for {pin[:12]} (was {current[:12] or 'unknown'})")
                shutil.rmtree(dest)
            else:
                print(f"  cached checkout {dest} @ {current[:12]}")
                return dest
        else:
            print(f"  cached checkout {dest}")
            return dest
    if dest.exists():
        shutil.rmtree(dest)
    dest.parent.mkdir(parents=True, exist_ok=True)
    if pinned_sha:
        print(f"  cloning {github} @ {pin[:12]} (pinned)")
        _clone_at_commit(url, dest, pin)
    elif pin:
        _run(["git", "clone", "--depth", "1", "--branch", pin, url, str(dest)])
    else:
        print(f"  WARNING {project_id}: no recorded commit; cloning default-branch tip")
        _run(["git", "clone", "--depth", "1", url, str(dest)])
    return dest


def _git_head(checkout: Path | None) -> str:
    if checkout is None:
        return "local-jdk-srczip"
    proc = _run(["git", "rev-parse", "HEAD"], cwd=checkout)
    return proc.stdout.strip()


def _write_project_yaml(
    entry: dict[str, Any],
    *,
    package_root: str,
    source_dir: Path,
    tag: str,
    mutant_count: int,
) -> None:
    project_id = entry["id"]
    github = entry["github"]
    out = PROJECTS / project_id
    out.mkdir(parents=True, exist_ok=True)
    rel_source = source_dir.relative_to(ROOT).as_posix()
    switch = f"{package_root}.jmhbench.MutationSwitch"
    text = f"""# Mutation-track metadata for {_display_name(github)} (GRPO RL corpus).
name: {project_id}
display_name: {_display_name(github)}
version: "{tag}"
package_root: {package_root}

source_dir: {rel_source}

mutations_patch: mutations.patch
mutants_registry: mutants.yaml
snippets: snippets.jsonl

java_release: 17
dependencies: []

mutation:
  switch_class: {switch}
  arm_property: jmhbench.mutant
  record_property: jmhbench.record

provenance:
  project: {_display_name(github)}
  url: https://github.com/{github}
  tag: "{tag}"
  mutants_planted: {mutant_count}
"""
    (out / "project.yaml").write_text(text, encoding="utf-8")


def _write_good_classes(project_id: str, fqcns: list[str]) -> None:
    out = PROJECTS / project_id
    out.mkdir(parents=True, exist_ok=True)
    lines = [
        f"# Curated benchmarkable classes for {project_id} (GRPO prompt corpus).",
        "# Derived from planted mutation sites (unique FQCNs).",
        "",
        f"project: {project_id}",
        "",
        "classes:",
    ]
    for fqcn in sorted(set(fqcns)):
        lines.append(f"  - {fqcn}")
    (out / "good_classes.yaml").write_text("\n".join(lines) + "\n", encoding="utf-8")


def _plant(entry: dict[str, Any], src_main: Path, *, count: int) -> int:
    project_id = entry["id"]
    package_root = entry.get("package_root") or _detect_package_root(src_main)
    if not package_root:
        raise RuntimeError(f"could not detect package_root for {project_id}")
    entry["package_root"] = package_root
    cmd = [
        "uv",
        "run",
        "jmh-make-mutants",
        project_id,
        "--source",
        str(src_main),
        "--package-root",
        package_root,
        "--count",
        str(count),
        "--allow-partial",
    ]
    proc = _run(cmd, check=False)
    sys.stdout.write(proc.stdout)
    sys.stderr.write(proc.stderr)
    if proc.returncode != 0:
        raise RuntimeError(f"jmh-make-mutants failed for {project_id}")
    registry = yaml.safe_load((PROJECTS / project_id / "mutants.yaml").read_text(encoding="utf-8"))
    return int(registry.get("count") or 0)


def _fqcns_from_sites(project_id: str) -> list[str]:
    path = PROJECTS / project_id / "mutation_sites.yaml"
    data = yaml.safe_load(path.read_text(encoding="utf-8")) or {}
    return [str(s["class"]) for s in data.get("sites") or [] if s.get("class")]


def bootstrap_one(
    entry: dict[str, Any],
    *,
    count: int,
    skip_clone: bool,
    force: bool,
) -> dict[str, Any]:
    project_id = entry["id"]
    print(f"\n== [{project_id}] {entry['github']} ==", flush=True)
    sites_path = PROJECTS / project_id / "mutation_sites.yaml"
    if sites_path.exists() and not force:
        n = len((yaml.safe_load(sites_path.read_text(encoding="utf-8")) or {}).get("sites") or [])
        print(f"  sites already planted ({n}); ensuring checkout exists")
        if not skip_clone and entry.get("source_style") != "jdk_srczip":
            try:
                _clone(entry)
            except Exception as exc:  # noqa: BLE001
                print(f"  WARNING: checkout failed: {exc}")
        return {"id": project_id, "status": "skipped", "mutants": n}

    if skip_clone:
        if entry.get("source_style") == "jdk_srczip":
            checkout = None
        else:
            checkout = EXTERNAL / project_id
            if not checkout.exists():
                raise FileNotFoundError(f"missing checkout {checkout} (omit --skip-clone)")
    else:
        checkout = _clone(entry)

    if force:
        entry = dict(entry)
        entry["_force_remerge"] = True
        merged = EXTERNAL / f"{project_id}-merged"
        if merged.exists():
            shutil.rmtree(merged)

    src_main = _resolve_src_main(entry, checkout or EXTERNAL / project_id)
    tag = str(entry.get("ref") or _git_head(checkout))
    planted = _plant(entry, src_main, count=count)
    package_root = entry["package_root"]
    # project.yaml source_dir should point at the java root used for planting.
    _write_project_yaml(
        entry,
        package_root=package_root,
        source_dir=src_main,
        tag=tag,
        mutant_count=planted,
    )
    _write_good_classes(project_id, _fqcns_from_sites(project_id))
    print(f"  planted {planted} mutants under {package_root}")
    return {"id": project_id, "status": "ok", "mutants": planted, "package_root": package_root}


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--only",
        default="",
        help="Comma-separated project ids to bootstrap (default: all new projects).",
    )
    parser.add_argument(
        "--include-existing",
        action="store_true",
        help="Also re-bootstrap the original six GRPO projects.",
    )
    parser.add_argument("--skip-clone", action="store_true", help="Reuse data/external-src checkouts.")
    parser.add_argument("--force", action="store_true", help="Re-plant even if mutation_sites.yaml exists.")
    parser.add_argument("--count", type=int, default=0, help="Override mutants_per_project.")
    args = parser.parse_args()

    corpus = _load_corpus()
    count = args.count or int(corpus.get("mutants_per_project") or 50)
    entries: list[dict[str, Any]] = list(corpus.get("projects") or [])
    if args.include_existing:
        entries = list(corpus.get("existing") or []) + entries

    only = {x.strip() for x in args.only.split(",") if x.strip()}
    if only:
        entries = [e for e in entries if e["id"] in only]

    results: list[dict[str, Any]] = []
    failures: list[str] = []
    for entry in entries:
        try:
            results.append(
                bootstrap_one(
                    entry,
                    count=count,
                    skip_clone=args.skip_clone,
                    force=args.force,
                )
            )
        except Exception as exc:  # noqa: BLE001 — collect per-project failures
            print(f"  ERROR: {exc}", file=sys.stderr, flush=True)
            failures.append(entry["id"])
            results.append({"id": entry["id"], "status": "error", "error": str(exc)})

    ok = [r for r in results if r["status"] == "ok"]
    skipped = [r for r in results if r["status"] == "skipped"]
    print("\n== summary ==")
    print(f"  ok={len(ok)} skipped={len(skipped)} failed={len(failures)} total={len(results)}")
    if failures:
        print("  failures: " + ", ".join(failures))
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
