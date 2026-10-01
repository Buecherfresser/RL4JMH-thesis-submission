"""Build a *patched* subject-under-test jar (with the mutation guards baked in).

The mutation reward needs the generated benchmark to compile and run against a SUT that
carries the dormant ``MutationSwitch.tick(id)`` guards, so arming ``-Djmhbench.mutant=<id>``
actually changes behaviour.

Two build backends:

* **javac** (default for zero-dep libraries): copy ``src/main/java``, apply the patch, plain
  ``javac`` + ``jar``. Fast, but fails for libraries that need Maven/Gradle deps or codegen.
* **native** (maven/gradle): copy the real checkout, inject patched sources into the matching
  module paths, run the project's own build, and collect the resulting jars.
"""

from __future__ import annotations

import shutil
import tempfile
from collections.abc import Iterable, Sequence
from pathlib import Path

from jmhgen.data.classpath import (
    DEFAULT_JAR_GLOBS,
    collect_jars,
    write_classpath_file,
)
from jmhgen.mutation.patch import apply_patch
from jmhgen.utils.logging import get_logger
from jmhgen.utils.subprocess import run_command

logger = get_logger("jmhgen.mutation.provision")

_NON_RUNTIME_JAR_SUFFIXES: tuple[str, ...] = (
    "-sources.jar",
    "-tests.jar",
    "-test-sources.jar",
    "-javadoc.jar",
)


def _java_sources(src_main: Path) -> list[str]:
    """Every ``*.java`` under ``src_main`` except ``module-info`` (breaks a plain javac)."""
    return [
        str(p)
        for p in sorted(src_main.rglob("*.java"))
        if p.name != "module-info.java" and p.name != "package-info.java"
    ]


def build_patched_jar(
    src_main: str | Path,
    patch_path: str | Path,
    out_jar: str | Path,
    *,
    java_release: int = 17,
    dep_classpath: Iterable[str | Path] = (),
    javac_args: Iterable[str] = (),
    javac_executable: str = "javac",
    jar_executable: str = "jar",
    timeout_s: float = 3600.0,
) -> Path:
    """Apply ``patch_path`` to a copy of ``src_main``, compile it, and jar it into ``out_jar``.

    Returns the path to the built jar. Raises :class:`RuntimeError` on any build failure.
    """
    src_root = Path(src_main)
    if not src_root.exists():
        raise FileNotFoundError(f"source root not found: {src_root}")
    out_path = Path(out_jar).resolve()
    out_path.parent.mkdir(parents=True, exist_ok=True)

    tmp = Path(tempfile.mkdtemp(prefix="jmhgen-patched-"))
    try:
        project = tmp / "project"
        build_src = project / "src" / "main" / "java"
        build_src.parent.mkdir(parents=True, exist_ok=True)
        shutil.copytree(src_root, build_src)
        apply_patch(project, patch_path)

        sources = _java_sources(build_src)
        if not sources:
            raise RuntimeError(f"no compilable .java sources under {build_src}")
        classes = project / "classes"
        classes.mkdir(parents=True, exist_ok=True)

        argfile = project / "sources.txt"
        argfile.write_text("\n".join(sources), encoding="utf-8")
        # JDK 8's javac has no --release; use -source/-target there.
        probe = run_command([javac_executable, "-version"], cwd=project, timeout_s=30)
        javac_ver = f"{probe.stdout or ''}{probe.stderr or ''}"
        if "1.8" in javac_ver or "javac 8" in javac_ver:
            release_args = ["-source", "1.8", "-target", "1.8"]
        else:
            release_args = ["--release", str(java_release)]
        javac_cmd = [
            javac_executable,
            *release_args,
            "-encoding",
            "UTF-8",
            "-d",
            str(classes),
        ]
        cp = [str(Path(p).resolve()) for p in dep_classpath]
        if cp:
            import os

            javac_cmd += ["-cp", os.pathsep.join(cp)]
        javac_cmd.extend(javac_args)
        javac_cmd.append(f"@{argfile}")
        logger.info("compiling %d source file(s) with %s", len(sources), javac_executable)
        proc = run_command(javac_cmd, cwd=project, timeout_s=timeout_s)
        if not proc.ok:
            raise RuntimeError(
                f"javac failed (rc={proc.returncode}, timed_out={proc.timed_out}):\n"
                f"{proc.stderr[-4000:]}"
            )

        jar_cmd = [jar_executable, "cf", str(out_path), "-C", str(classes), "."]
        proc = run_command(jar_cmd, cwd=project, timeout_s=timeout_s)
        if not proc.ok:
            raise RuntimeError(f"jar failed (rc={proc.returncode}):\n{proc.stderr[-2000:]}")
        logger.info("built patched jar -> %s", out_path)
        return out_path
    finally:
        shutil.rmtree(tmp, ignore_errors=True)


def provision_mutated_classpath(
    src_main: str | Path,
    patch_path: str | Path,
    output_cp: str | Path,
    *,
    jar_path: str | Path | None = None,
    java_release: int = 17,
    dep_classpath: Iterable[str | Path] = (),
    javac_args: Iterable[str] = (),
    javac_executable: str = "javac",
    jar_executable: str = "jar",
) -> Path:
    """Build the patched jar and write a ``.cp`` file (patched jar + any dependency jars).

    Returns the path to the written ``.cp`` file.
    """
    deps = [str(Path(p).resolve()) for p in dep_classpath]
    jar = Path(jar_path) if jar_path is not None else Path(output_cp).with_suffix(".jar")
    built = build_patched_jar(
        src_main,
        patch_path,
        jar,
        java_release=java_release,
        dep_classpath=deps,
        javac_args=javac_args,
        javac_executable=javac_executable,
        jar_executable=jar_executable,
    )
    return write_classpath_file(output_cp, [built, *deps])


_SKIP_MODULE_MARKERS: tuple[str, ...] = (
    "/test-",
    "/tests/",
    "/metrics/",
    "/extras/",
    "/examples/",
    "/example/",
    "/benchmark",
    "/benchmarks/",
    "/samples/",
    "/sample/",
    "/integration-",
    "/android/",
    "/gwt/",
    "/it/",
)


def _src_main_roots(checkout: Path) -> list[Path]:
    return sorted({p for p in checkout.rglob("src/main/java") if p.is_dir()})


def _flat_src_roots(checkout: Path) -> list[Path]:
    """Guava / dsiutils style: ``src/<package>/...`` without ``main/java``."""
    roots: list[Path] = []
    for src_root in sorted(p for p in checkout.rglob("src") if p.is_dir() and p.name == "src"):
        posix = src_root.as_posix()
        if "/test/" in posix or src_root.name == "test":
            continue
        if (src_root / "main").is_dir():
            continue
        if any((src_root / tip).is_dir() for tip in ("com", "org", "net", "io", "javax", "jakarta")):
            roots.append(src_root)
    return roots


def _all_java_roots(checkout: Path) -> list[Path]:
    return _src_main_roots(checkout) + _flat_src_roots(checkout)


def _ancestor_match_depth(root: Path, rel_path: Path) -> int:
    """How many leading package segments of ``rel_path`` already exist under ``root``."""
    for depth in range(len(rel_path.parts) - 1, 0, -1):
        if (root / Path(*rel_path.parts[:depth])).is_dir():
            return depth
    return 0


def _module_name_for_root(root: Path) -> str:
    if root.name == "java" and root.parent.name == "main":
        return root.parent.parent.name
    if root.name == "src":
        return root.parent.name
    return root.parent.name


def _root_rank(root: Path, rel_path: Path) -> tuple[int, int, int]:
    """Prefer primary library modules over extras/metrics/tests when placing new files."""
    posix = f"/{root.as_posix()}/"
    primary = 0 if any(m in posix for m in _SKIP_MODULE_MARKERS) else 1
    pkg_parts = [p for p in rel_path.parts[:-1] if p != "jmhbench"]
    hint = pkg_parts[-1] if pkg_parts else ""
    module_name = _module_name_for_root(root)
    name_match = 1 if hint and (module_name == hint or f"/{hint}/" in posix) else 0
    java_count = sum(1 for _ in root.rglob("*.java"))
    return (primary, name_match, java_count)


def _dest_for_rel(checkout: Path, rel: str) -> Path:
    """Map a path relative to ``src/main/java`` onto a file inside ``checkout``."""
    rel_path = Path(rel)
    roots = _all_java_roots(checkout)

    existing: list[tuple[tuple[int, int, int], Path]] = []
    for root in roots:
        cand = root / rel_path
        if cand.is_file():
            existing.append((_root_rank(root, rel_path), cand))
    if existing:
        existing.sort(key=lambda item: item[0], reverse=True)
        return existing[0][1]

    # New files (e.g. MutationSwitch): deepest existing package ancestor, primary module first.
    best: tuple[tuple[int, int, int, int], Path] | None = None
    for root in roots:
        depth = _ancestor_match_depth(root, rel_path)
        if depth <= 0:
            continue
        key = (depth, *_root_rank(root, rel_path))
        if best is None or key > best[0]:
            best = (key, root / rel_path)
    if best is not None:
        return best[1]

    root = roots[0] if roots else checkout / "src" / "main" / "java"
    root.mkdir(parents=True, exist_ok=True)
    return root / rel_path


def inject_patched_sources(checkout: Path, patched_src_main: Path) -> int:
    """Overwrite checkout sources with the patched tree (including ``MutationSwitch``)."""
    count = 0
    for src in patched_src_main.rglob("*.java"):
        rel = src.relative_to(patched_src_main).as_posix()
        dest = _dest_for_rel(checkout, rel)
        dest.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(src, dest)
        count += 1
    return count


def detect_native_build(checkout: Path) -> tuple[str, list[str], tuple[str, ...]] | None:
    """Return ``(kind, build_cmd, jar_globs)`` for a checkout, or ``None`` if unknown."""
    if (checkout / "pom.xml").exists():
        return (
            "maven",
            [
                "mvn",
                "-q",
                "-DskipTests",
                "-Drat.skip=true",
                "-Dcheckstyle.skip=true",
                "-Dspotbugs.skip=true",
                "-Dpmd.skip=true",
                "-Dcpd.skip=true",
                "-Djacoco.skip=true",
                "-Dcoverage.skip=true",
                "-Denforcer.skip=true",
                "-Dlicense.skip=true",
                "-DskipITs=true",
                "-Dmaven.antrun.skip=true",
                "-Dspotless.check.skip=true",
                "-Dspotless.skip=true",
                "-Dfmt.skip=true",
                "-Danimal.sniffer.skip=true",
                "package",
                "dependency:copy-dependencies",
                "-DincludeScope=runtime",
            ],
            ("**/target/*.jar", "**/target/dependency/*.jar"),
        )
    if any(
        (checkout / name).exists()
        for name in (
            "build.gradle",
            "build.gradle.kts",
            "settings.gradle",
            "settings.gradle.kts",
        )
    ):
        gradlew = checkout / "gradlew"
        cmd = (
            ["./gradlew", "--no-daemon", "jar", "-x", "test"]
            if gradlew.exists()
            else ["gradle", "--no-daemon", "jar", "-x", "test"]
        )
        return (
            "gradle",
            cmd,
            (
                "**/build/libs/*.jar",
                "**/output/**/*.jar",
                "**/dist/**/*.jar",
                "**/build/distributions/*.jar",
            ),
        )
    return None


def _filter_runtime_jars(jars: Sequence[Path]) -> list[Path]:
    kept: list[Path] = []
    for jar in jars:
        name = jar.name
        if any(name.endswith(suf) for suf in _NON_RUNTIME_JAR_SUFFIXES):
            continue
        if jar.stat().st_size < 32:
            continue
        kept.append(jar)
    return kept


def _ensure_git_metadata(work: Path) -> None:
    """Some Gradle plugins (e.g. palantir git-version) require a ``.git`` directory."""
    if (work / ".git").exists():
        return
    run_command(["git", "init"], cwd=work, timeout_s=60)
    run_command(
        ["git", "-c", "user.name=jmhgen", "-c", "user.email=jmhgen@local", "add", "-A"],
        cwd=work,
        timeout_s=120,
    )
    run_command(
        [
            "git",
            "-c",
            "user.name=jmhgen",
            "-c",
            "user.email=jmhgen@local",
            "commit",
            "-m",
            "jmhgen native mutants workspace",
            "--allow-empty",
        ],
        cwd=work,
        timeout_s=120,
    )


def _soften_build_strictness(work: Path) -> None:
    """Relax fail-on-warning / Error Prone so injected mutants can compile."""
    import re

    for pom in work.rglob("pom.xml"):
        text = pom.read_text(encoding="utf-8")
        new = (
            text.replace("<failOnWarning>true</failOnWarning>", "<failOnWarning>false</failOnWarning>")
            .replace("<failOnWarnings>true</failOnWarnings>", "<failOnWarnings>false</failOnWarnings>")
            .replace("-Werror", "-Wno-error")
        )
        # Ancient Maven projects pin source/target 1.5 which modern JDKs reject.
        new = re.sub(r"<source>1\.[1-5]</source>", "<source>1.8</source>", new)
        new = re.sub(r"<target>1\.[1-5]</target>", "<target>1.8</target>", new)
        new = re.sub(
            r"<maven\.compiler\.source>1\.[1-5]</maven\.compiler\.source>",
            "<maven.compiler.source>1.8</maven.compiler.source>",
            new,
        )
        new = re.sub(
            r"<maven\.compiler\.target>1\.[1-5]</maven\.compiler\.target>",
            "<maven.compiler.target>1.8</maven.compiler.target>",
            new,
        )
        if new != text:
            pom.write_text(new, encoding="utf-8")
        text2 = pom.read_text(encoding="utf-8")
        new2 = text2
        if "jacoco" in text2.lower() or "enforcer" in text2.lower():
            if "<jacoco.skip>false</jacoco.skip>" in new2:
                new2 = new2.replace("<jacoco.skip>false</jacoco.skip>", "<jacoco.skip>true</jacoco.skip>")
            if "<enforcer.skip>false</enforcer.skip>" in new2:
                new2 = new2.replace(
                    "<enforcer.skip>false</enforcer.skip>", "<enforcer.skip>true</enforcer.skip>"
                )
            if "<properties>" in new2 and "jacoco.skip" not in new2:
                new2 = new2.replace(
                    "<properties>",
                    "<properties>\n    <jacoco.skip>true</jacoco.skip>\n"
                    "    <enforcer.skip>true</enforcer.skip>",
                    1,
                )
        # streamex: jacoco coverage check via antrun — drop the fail threshold.
        if "LIMIT" in new2 and "jacoco" in new2.lower():
            new2 = re.sub(
                r"<limit>\s*<counter>[^<]+</counter>\s*<value>COVEREDRATIO</value>\s*"
                r"<minimum>[^<]+</minimum>\s*</limit>",
                "",
                new2,
                flags=re.I | re.S,
            )
        if new2 != text2:
            pom.write_text(new2, encoding="utf-8")
    for gradle in list(work.rglob("build.gradle")) + list(work.rglob("build.gradle.kts")):
        text = gradle.read_text(encoding="utf-8")
        new = text.replace("-Werror", "-Wno-error")
        if "gitVersion()" in new and "jmhgen" not in new:
            new = new.replace("gitVersion()", "'0.0.0-jmhgen'")
        if new != text:
            gradle.write_text(new, encoding="utf-8")


def _maven_module_rel(work: Path, file: Path) -> str | None:
    """Return the Maven ``-pl`` path for ``file``, or ``None`` if it is the reactor root."""
    cur = file.parent
    while cur != work and cur != cur.parent:
        if (cur / "pom.xml").is_file():
            if cur == work:
                return None
            return cur.relative_to(work).as_posix()
        cur = cur.parent
    return None


def _reactor_modules(work: Path) -> set[str]:
    """Module paths declared in the root aggregator pom (best-effort)."""
    root_pom = work / "pom.xml"
    if not root_pom.is_file():
        return set()
    mods: set[str] = set()
    for line in root_pom.read_text(encoding="utf-8").splitlines():
        s = line.strip()
        if s.startswith("<module>") and s.endswith("</module>"):
            mods.add(s[len("<module>") : -len("</module>")].strip())
    return mods


def _scope_maven_cmd(work: Path, build_cmd: Sequence[str]) -> list[str]:
    """Prefer building only the module that received ``MutationSwitch`` (plus deps)."""
    cmd = list(build_cmd)
    if not cmd or Path(cmd[0]).name not in {"mvn", "mvn.cmd"}:
        return cmd
    if any(a == "-pl" or a.startswith("-pl=") for a in cmd):
        return cmd
    reactor = _reactor_modules(work)
    switches = list(work.rglob("**/jmhbench/MutationSwitch.java"))
    modules: list[str] = []
    for switch in switches:
        mod = _maven_module_rel(work, switch)
        if not mod:
            continue
        if reactor and mod not in reactor and mod.split("/")[0] not in reactor:
            continue
        if mod not in modules:
            modules.append(mod)
    if len(modules) == 1:
        insert_at = 1
        while insert_at < len(cmd) and cmd[insert_at].startswith("-"):
            insert_at += 1
        cmd[insert_at:insert_at] = ["-pl", modules[0], "-am"]
    return cmd


def provision_mutated_classpath_native(
    checkout: str | Path,
    src_main: str | Path,
    patch_path: str | Path,
    output_cp: str | Path,
    *,
    build_cmd: Sequence[str] | None = None,
    jar_globs: Iterable[str] | None = None,
    jar_path: str | Path | None = None,
    timeout_s: float = 3600.0,
) -> Path:
    """Patch sources inside a throwaway checkout copy and build with Maven/Gradle.

    ``src_main`` is the (possibly merged) tree the patch was generated against.
    ``checkout`` is the real project root that owns ``pom.xml`` / ``build.gradle``.
    """
    checkout_root = Path(checkout)
    src_root = Path(src_main)
    patch = Path(patch_path)
    if not checkout_root.exists():
        raise FileNotFoundError(f"checkout not found: {checkout_root}")
    if not src_root.exists():
        raise FileNotFoundError(f"source root not found: {src_root}")
    if not patch.exists():
        raise FileNotFoundError(f"patch not found: {patch}")

    detected = detect_native_build(checkout_root)
    if build_cmd is None:
        if detected is None:
            raise RuntimeError(f"no Maven/Gradle build files under {checkout_root}")
        build_cmd = detected[1]
        jar_globs = jar_globs or detected[2]
    else:
        jar_globs = jar_globs or DEFAULT_JAR_GLOBS

    out_cp = Path(output_cp)
    out_cp.parent.mkdir(parents=True, exist_ok=True)
    out_jar = Path(jar_path) if jar_path is not None else out_cp.with_suffix(".jar")

    tmp = Path(tempfile.mkdtemp(prefix="jmhgen-native-mutants-"))
    try:
        work = tmp / "checkout"
        shutil.copytree(
            checkout_root,
            work,
            symlinks=True,
            ignore=shutil.ignore_patterns(
                ".git",
                "build",
                "target",
                ".gradle",
                ".idea",
                "*.iml",
                "node_modules",
            ),
        )
        _ensure_git_metadata(work)

        patch_proj = tmp / "patchproj"
        build_src = patch_proj / "src" / "main" / "java"
        build_src.parent.mkdir(parents=True, exist_ok=True)
        shutil.copytree(src_root, build_src)
        apply_patch(patch_proj, patch)

        injected = inject_patched_sources(work, build_src)
        _soften_build_strictness(work)
        cmd = _scope_maven_cmd(work, build_cmd)
        logger.info(
            "injected %d patched source(s) into %s; running: %s",
            injected,
            work,
            " ".join(cmd),
        )
        if (work / "gradlew").exists():
            mode = (work / "gradlew").stat().st_mode
            (work / "gradlew").chmod(mode | 0o111)

        proc = run_command(cmd, cwd=work, timeout_s=timeout_s)
        if not proc.ok:
            detail = (proc.stderr or "")[-3000:] + "\n" + (proc.stdout or "")[-3000:]
            raise RuntimeError(
                f"native build failed (rc={proc.returncode}, timed_out={proc.timed_out}):\n"
                f"{detail.strip()}"
            )

        jars = _filter_runtime_jars(collect_jars([work], jar_globs))
        if not jars:
            raise RuntimeError(
                f"native build produced no runtime jars under {work} "
                f"(globs={list(jar_globs)})"
            )

        primary = max(
            (j for j in jars if "/dependency/" not in j.as_posix()),
            key=lambda p: p.stat().st_size,
            default=jars[0],
        )
        shutil.copy2(primary, out_jar)
        # Persist deps before the temp checkout is deleted (CLI bundling runs after return).
        lib_dir = out_cp.parent / "lib" / out_cp.stem
        lib_dir.mkdir(parents=True, exist_ok=True)
        stable: list[Path] = [out_jar]
        for jar in jars:
            if jar.resolve() == primary.resolve():
                continue
            dest = lib_dir / jar.name
            shutil.copy2(jar, dest)
            stable.append(dest)
        logger.info("built patched SUT via native build -> %s (%d jars)", out_jar, len(stable))
        return write_classpath_file(out_cp, stable)
    finally:
        shutil.rmtree(tmp, ignore_errors=True)
