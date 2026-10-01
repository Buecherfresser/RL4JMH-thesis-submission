#!/usr/bin/env python3
"""Generate the fixed performance-mutation set for the Commons Compress track.

This produces three artefacts inside a vendored project directory:

* ``mutations.patch`` - a single unified diff that (a) adds the
  ``MutationSwitch`` helper class and (b) inserts ``MutationSwitch.tick(<id>)``
  as the first statement of N distinct existing methods spread across the
  library's components. Every insertion is a *dormant* performance mutant: it
  does nothing unless that exact id is armed at runtime via
  ``-Djmhbench.mutant=<id>``.
* ``mutants.yaml`` - the hidden ground-truth registry mapping each mutant id to
  its location (file, class, method, signature, line). Never shown to a harness.
* (the helper source is embedded in the patch, not written separately)

The patch is produced with an isolated throwaway git repo so the diff is
byte-exact and applies cleanly with ``git apply --directory=src/main/java``.

Usage::

    python tools/make_compress_mutants.py \
        --project-dir dataset/projects/commons-compress --count 100

Deterministic: the same source tree + count always yields the same mutants.
"""

from __future__ import annotations

import argparse
import re
import shutil
import subprocess
import tempfile
from collections import defaultdict
from dataclasses import dataclass
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
DEFAULT_PROJECT_DIR = REPO_ROOT / "dataset" / "projects" / "commons-compress"

SWITCH_PACKAGE = "org.apache.commons.compress.jmhbench"
SWITCH_REL_PATH = "org/apache/commons/compress/jmhbench/MutationSwitch.java"
SWITCH_CALL = "org.apache.commons.compress.jmhbench.MutationSwitch.tick"

# Java modifiers that may precede a method's return type.
_MODIFIERS = {
    "public",
    "protected",
    "private",
    "static",
    "final",
    "abstract",
    "synchronized",
    "native",
    "default",
    "strictfp",
}

# Method names we never mutate: trivial Object overrides.
_SKIP_NAMES = {"equals", "hashCode", "toString", "clone", "finalize"}

# Per-element / hot-path methods. A mutant here fires Theta(payload-size) times
# per benchmark op, so a ~1ms per-call sleep becomes a multi-second per-op
# regression that blows the JMH time budget instead of a tiny detectable one.
# We deliberately place mutants only at coarse, ~once-per-op method entries.
_HOT_NAMES = {
    "read",
    "write",
    "count",
    "available",
    "skip",
    "flush",
    "update",
    "matches",
    "decode",
    "encode",
    "getLength",
    "getOffset",
    "readByte",
    "writeByte",
    "readBits",
    "writeBits",
    "readBit",
    "writeBit",
    "readBuffer",
    "fill",
    "getNextByte",
    "nextByte",
    "writeRun",
    "copy",
    # Per-byte counters / per-token accessors observed to fire thousands-to-
    # millions of times per op (would make an armed rerun time out, not a tiny
    # regression). Detection still treats any time-out as a kill, but excluding
    # these keeps the mutant set genuinely "coarse, ~once-per-op".
    "getBytesRead",
    "getBytesWritten",
    "getType",
    "getData",
    "isArrayZero",
}

# Matches the body-opening brace of a single-line method signature:
#   ... ) {              or      ... ) throws Foo, Bar {
_BODY_BRACE_RE = re.compile(r"\)\s*(?:throws\s[^{;]*?)?\{")
_IDENT_RE = re.compile(r"[A-Za-z_$][\w$]*")


@dataclass(frozen=True)
class Site:
    rel_path: str          # path relative to src/main/java, e.g. org/.../ZipFile.java
    component: str         # e.g. "archivers/zip"
    class_name: str        # best-effort enclosing top-level class simple name
    method: str            # method name
    signature: str         # trimmed signature line
    line_index: int        # 0-based line index of the signature
    brace_col: int         # column right after the body-opening '{'
    priority: int          # lower = more interesting (stream/entry/file/util)


def _component_of(rel_path: str) -> str:
    marker = "org/apache/commons/compress/"
    tail = rel_path.split(marker, 1)[-1]
    parts = tail.split("/")
    if len(parts) == 1:
        return "(root)"
    # Group archivers/<fmt> and compressors/<fmt>; otherwise the first segment.
    if parts[0] in ("archivers", "compressors") and len(parts) >= 3:
        return f"{parts[0]}/{parts[1]}"
    return parts[0]


def _class_name_of(rel_path: str) -> str:
    return Path(rel_path).stem


def _is_interesting(class_name: str) -> bool:
    return bool(
        re.search(
            r"(InputStream|OutputStream|File|Utils|Parameters|Archive|Compressor|Entry)",
            class_name,
        )
    )


def _candidate_site(line: str, rel_path: str, idx: int) -> Site | None:
    """Return a :class:`Site` if *line* is a mutable single-line method header."""
    stripped = line.strip()
    # Only accessible (public/protected) methods, so we hit the real API surface.
    if not (stripped.startswith("public ") or stripped.startswith("protected ")):
        return None
    if "abstract " in stripped:
        return None

    m = _BODY_BRACE_RE.search(line)
    if m is None:
        return None
    brace_col = m.end()  # index just after the body '{'

    open_brace = line.find("{")
    # The body brace must be the first '{' on the line (else it's an array
    # initialiser, annotation value, lambda, etc.).
    if open_brace == -1 or open_brace != brace_col - 1:
        return None

    header = line[: line.index("(")] if "(" in line else ""
    if not header or "=" in line[: brace_col] or "@" in line[: brace_col]:
        return None

    tokens = _IDENT_RE.findall(header)
    if len(tokens) < 2:
        return None  # need <modifiers...> <returnType> <name>
    name = tokens[-1]
    before_name = tokens[:-1]
    # Constructor check: nothing but modifiers before the name => no return type.
    if all(tok in _MODIFIERS for tok in before_name):
        return None
    if name in _SKIP_NAMES or name in _HOT_NAMES:
        return None
    if name in _MODIFIERS:
        return None

    class_name = _class_name_of(rel_path)
    return Site(
        rel_path=rel_path,
        component=_component_of(rel_path),
        class_name=class_name,
        method=name,
        signature=stripped,
        line_index=idx,
        brace_col=brace_col,
        priority=0 if _is_interesting(class_name) else 1,
    )


def collect_sites(src_root: Path) -> dict[str, list[Site]]:
    """Group mutable method sites by component, sorted by interest then position."""
    by_component: dict[str, list[Site]] = defaultdict(list)
    for path in sorted(src_root.rglob("*.java")):
        rel = path.relative_to(src_root).as_posix()
        # Only target the public API packages (archivers / compressors / utils /
        # parallel / root); skip internal harmony/pack200 + lz77 plumbing.
        comp = _component_of(rel)
        top = comp.split("/")[0]
        if top not in ("archivers", "compressors", "utils", "parallel", "(root)"):
            continue
        lines = path.read_text(encoding="utf-8", errors="replace").splitlines()
        for idx, line in enumerate(lines):
            site = _candidate_site(line, rel, idx)
            if site is not None:
                by_component[comp].append(site)
    for comp in by_component:
        by_component[comp].sort(key=lambda s: (s.priority, s.rel_path, s.line_index))
    return by_component


def select_sites(by_component: dict[str, list[Site]], count: int) -> list[Site]:
    """Round-robin across components to spread mutants, capping per-file picks."""
    for file_cap in (2, 3, 4, 6, 100):
        selected: list[Site] = []
        per_file: dict[str, int] = defaultdict(int)
        cursors: dict[str, int] = {c: 0 for c in by_component}
        comps = sorted(by_component)
        progress = True
        while len(selected) < count and progress:
            progress = False
            for comp in comps:
                if len(selected) >= count:
                    break
                lst = by_component[comp]
                i = cursors[comp]
                while i < len(lst):
                    site = lst[i]
                    i += 1
                    if per_file[site.rel_path] < file_cap:
                        cursors[comp] = i
                        selected.append(site)
                        per_file[site.rel_path] += 1
                        progress = True
                        break
                else:
                    cursors[comp] = i
        if len(selected) >= count:
            return selected[:count]
    return selected


_SWITCH_TEMPLATE = r"""/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.compress.jmhbench;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * JMH-Bench performance-mutation switch (generated; do not edit by hand).
 *
 * <p>Each call site {@code MutationSwitch.tick(id)} is an independent, dormant
 * performance mutant baked into the subject-under-test. Exactly one mutant is
 * armed per JVM via {@code -Djmhbench.mutant=<id>}; when armed, the matching
 * site injects a tiny, fixed latency whose operator and magnitude are chosen by
 * {@code -Djmhbench.mutant.op} / {@code -Djmhbench.mutant.tokens}. With
 * {@code -Djmhbench.record=true} the switch instead counts how many times each
 * mutant id was reached and dumps the counts ({@code <id> <count>} per line) to
 * {@code -Djmhbench.record.file} at JVM shutdown. JMH-Bench uses those counts to
 * build a benchmark coverage map and to prioritise the benchmarks most likely
 * to be on a mutant's measured hot path.</p>
 */
public final class MutationSwitch {

    /** Number of mutants defined in this build. */
    public static final int COUNT = __COUNT__;

    private static final int ARMED = Integer.getInteger("jmhbench.mutant", -1);
    private static final boolean RECORD = Boolean.getBoolean("jmhbench.record");
    private static final long[] HITS = new long[COUNT + 1];

    /** {@code -Djmhbench.mutant.op=spin}: an LCG burn loop, ~1 ns per token. */
    private static final int OP_SPIN = 0;
    /** {@code -Djmhbench.mutant.op=sleep}: {@code Thread.sleep(0, 1)}, ~1.2 ms on Linux. */
    private static final int OP_SLEEP = 1;
    /** {@code -Djmhbench.mutant.op=nanotime}: one {@code System.nanoTime()} read. */
    private static final int OP_NANOTIME = 2;
    /** {@code -Djmhbench.mutant.op=consumecpu}: JMH's {@code Blackhole.consumeCPU}. */
    private static final int OP_CONSUMECPU = 3;

    /**
     * The injected-latency operator. {@code sleep} is the historical default and
     * costs ~1.2 ms per hit on Linux -- four orders of magnitude above the 10%
     * kill threshold, which makes every covered mutant trivially killable. Use
     * {@code consumecpu} (or the older {@code spin}) with a host-calibrated token
     * count for a latency in the nanosecond-to-microsecond range that the benchmark
     * has to actually resolve.
     */
    private static final int OP = parseOp(System.getProperty("jmhbench.mutant.op", "sleep"));

    /** Tokens for {@code op=spin} / {@code op=consumecpu} (see {@code tools/calibrate_mutant_op.py}). */
    private static final long TOKENS = Long.getLong("jmhbench.mutant.tokens", 64L);

    /**
     * Sink for the spin operator. Volatile: the read at the top of the loop plus
     * the never-taken store after it are what stop the JIT from folding the loop
     * away -- the same guard JMH's own {@code Blackhole.consumeCPU} relies on.
     */
    private static volatile long consumed = 0x5DEECE66DL;

    static {
        if (RECORD) {
            final String file = System.getProperty("jmhbench.record.file");
            if (file != null && !file.isEmpty()) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> dump(file)));
            }
        }
    }

    private MutationSwitch() {
    }

    /**
     * Mutation hook. No-op unless this {@code id} is armed (then it slows down)
     * or recording is enabled (then it counts the reach).
     *
     * @param id the mutant id for this call site
     */
    public static void tick(final int id) {
        if (RECORD && id >= 0 && id < HITS.length) {
            HITS[id]++;
        }
        if (id == ARMED) {
            slow();
        }
    }

    private static int parseOp(final String name) {
        if ("spin".equalsIgnoreCase(name)) {
            return OP_SPIN;
        }
        if ("sleep".equalsIgnoreCase(name)) {
            return OP_SLEEP;
        }
        if ("nanotime".equalsIgnoreCase(name)) {
            return OP_NANOTIME;
        }
        if ("consumecpu".equalsIgnoreCase(name)) {
            return OP_CONSUMECPU;
        }
        // Fail loudly rather than quietly measure an operator other than the one
        // the run records in its provenance.
        throw new IllegalArgumentException(
                "jmhbench.mutant.op must be one of spin|sleep|nanotime|consumecpu, got: "
                        + name);
    }

    /**
     * The injected latency. {@code OP} and {@code TOKENS} are static finals read
     * from system properties, so the JIT constant-folds this dispatch and only
     * the selected operator survives on the armed path.
     */
    private static void slow() {
        if (OP == OP_SPIN) {
            spin(TOKENS);
        } else if (OP == OP_CONSUMECPU) {
            consumeCpu(TOKENS);
        } else if (OP == OP_NANOTIME) {
            consumed = System.nanoTime();
        } else {
            sleep();
        }
    }

    /**
     * Burn {@code tokens} steps of a 64-bit LCG: a data-dependent chain the JIT
     * can neither vectorise nor shorten, so the cost is linear in {@code tokens}
     * at roughly a nanosecond each. Calibrate the token count on the measurement
     * host, since a nanosecond per token is a rough figure, not a guarantee.
     *
     * <p>Kept exactly as it was first measured, so a run recorded with
     * {@code op=spin} stays reproducible. New measurements should prefer
     * {@code op=consumecpu}. Do not "simplify" the step below to the plain
     * recurrence {@code t = t * A + B}: that form is affine and has a closed form
     * over k steps, C2 composes it across unrolled iterations -- one multiply-add
     * per two tokens -- and the injected latency halves without the calibration
     * noticing. The extra {@code t +=} is what stops that today.</p>
     */
    private static void spin(final long tokens) {
        long t = consumed;
        for (long i = tokens; i > 0L; i--) {
            t += t * 0x5DEECE66DL + 0xBL;
        }
        if (t == 42L) {
            consumed = t;
        }
    }

    /**
     * Burn {@code tokens} steps of JMH's {@code Blackhole.consumeCPU} (1.37),
     * ported verbatim. Preferred over {@code spin} for new measurements: mixing
     * the loop counter {@code i} into the step makes the recurrence non-affine, so
     * there is no constant for C2 to precompute and the cost per token stops
     * depending on how the loop is unrolled. Measured on one Zen 4c core at
     * 3.7 GHz: 1.894 ns per token, stable to 0.02% across JVMs and to 0.07%
     * between a constant and a runtime token count.
     */
    private static void consumeCpu(final long tokens) {
        long t = consumed;
        for (long i = tokens; i > 0L; i--) {
            t += (t * 0x5DEECE66DL + 0xBL + i) & 0xFFFFFFFFFFFFL;
        }
        if (t == 42L) {
            consumed += t;
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(0, 1);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void dump(final String file) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < HITS.length; i++) {
            if (HITS[i] > 0) {
                sb.append(i).append(' ').append(HITS[i]).append('\n');
            }
        }
        try (Writer w = Files.newBufferedWriter(Paths.get(file), StandardCharsets.UTF_8)) {
            w.write(sb.toString());
        } catch (final IOException e) {
            // best-effort: a failed dump just means no coverage for this run
        }
    }
}
"""


def _switch_source(count: int) -> str:
    return _SWITCH_TEMPLATE.replace("__COUNT__", str(count))


def _apply_insertions(work_root: Path, selected: list[Site]) -> None:
    """Insert the tick() call into each selected method (in the working copy)."""
    by_file: dict[str, list[tuple[int, int, int]]] = defaultdict(list)
    for mutant_id, site in enumerate(selected, start=1):
        by_file[site.rel_path].append((site.line_index, site.brace_col, mutant_id))

    for rel_path, edits in by_file.items():
        path = work_root / rel_path
        lines = path.read_text(encoding="utf-8").splitlines(keepends=True)
        for line_index, brace_col, mutant_id in edits:
            line = lines[line_index]
            newline = ""
            if line.endswith("\r\n"):
                body, newline = line[:-2], "\r\n"
            elif line.endswith("\n"):
                body, newline = line[:-1], "\n"
            else:
                body = line
            insert = f" {SWITCH_CALL}({mutant_id});"
            lines[line_index] = body[:brace_col] + insert + body[brace_col:] + newline
        path.write_text("".join(lines), encoding="utf-8")


def _git(args: list[str], cwd: Path) -> subprocess.CompletedProcess:
    return subprocess.run(
        ["git", "-c", "user.name=jmhbench", "-c", "user.email=jmhbench@local", *args],
        cwd=cwd,
        capture_output=True,
        text=True,
        check=False,
    )


def build_patch(src_root: Path, selected: list[Site], count: int) -> str:
    """Produce the unified diff via an isolated throwaway git repo."""
    tmp = Path(tempfile.mkdtemp(prefix="cc-mutants-"))
    try:
        work = tmp / "java"
        shutil.copytree(src_root, work)
        _git(["init", "-q"], work)
        _git(["add", "-A"], work)

        _apply_insertions(work, selected)

        switch_path = work / SWITCH_REL_PATH
        switch_path.parent.mkdir(parents=True, exist_ok=True)
        switch_path.write_text(_switch_source(count), encoding="utf-8")
        _git(["add", "-N", SWITCH_REL_PATH], work)

        diff = _git(["diff"], work)
        if diff.returncode != 0:
            raise RuntimeError(f"git diff failed: {diff.stderr}")
        return diff.stdout
    finally:
        shutil.rmtree(tmp, ignore_errors=True)


def _mutants_yaml(selected: list[Site]) -> str:
    lines = [
        "# Hidden ground-truth registry for the Commons Compress mutation track.",
        "# Generated by tools/make_compress_mutants.py - do not edit by hand.",
        "# NEVER show this file to a harness: it reveals where the mutants are.",
        f"count: {len(selected)}",
        "mutants:",
    ]
    for mutant_id, site in enumerate(selected, start=1):
        sig = site.signature.replace('"', '\\"')
        lines.append(f"  - id: {mutant_id}")
        lines.append(f"    file: {site.rel_path}")
        lines.append(f"    fqcn: {_fqcn(site.rel_path)}")
        lines.append(f"    component: {site.component}")
        lines.append(f"    method: {site.method}")
        lines.append(f'    signature: "{sig}"')
        lines.append(f"    line: {site.line_index + 1}")
    return "\n".join(lines) + "\n"


def _fqcn(rel_path: str) -> str:
    no_ext = rel_path[:-5] if rel_path.endswith(".java") else rel_path
    return no_ext.replace("/", ".")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--project-dir", type=Path, default=DEFAULT_PROJECT_DIR)
    parser.add_argument("--count", type=int, default=100)
    args = parser.parse_args()

    project_dir: Path = args.project_dir.resolve()
    src_root = project_dir / "src" / "main" / "java"
    if not src_root.exists():
        raise SystemExit(f"source root not found: {src_root}")

    by_component = collect_sites(src_root)
    total_candidates = sum(len(v) for v in by_component.values())
    selected = select_sites(by_component, args.count)
    if len(selected) < args.count:
        raise SystemExit(
            f"only found {len(selected)} mutable sites (< {args.count}); "
            f"total candidates={total_candidates}"
        )

    patch = build_patch(src_root, selected, args.count)
    (project_dir / "mutations.patch").write_text(patch, encoding="utf-8")
    (project_dir / "mutants.yaml").write_text(_mutants_yaml(selected), encoding="utf-8")

    dist: dict[str, int] = defaultdict(int)
    for s in selected:
        dist[s.component] += 1
    print(f"Selected {len(selected)} mutants from {total_candidates} candidates.")
    print("Distribution by component:")
    for comp in sorted(dist):
        print(f"  {comp:28s} {dist[comp]}")
    print(f"\nWrote: {project_dir / 'mutations.patch'}")
    print(f"Wrote: {project_dir / 'mutants.yaml'}")


if __name__ == "__main__":
    main()
