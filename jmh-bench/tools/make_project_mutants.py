#!/usr/bin/env python3
"""Generate the performance-mutation set for any vendored project-track subject.

This is the project-agnostic generalisation of ``tools/make_compress_mutants.py``
(which stays as-is so the Commons Compress track keeps reproducing byte-for-byte).
Everything project-specific — the switch package, which packages to mutate, which
class names are "interesting", which method names are too hot to touch — is read
from the project's own ``project.yaml`` or passed on the command line.

It produces two artefacts inside a vendored project directory:

* ``mutations.patch`` - a single unified diff that (a) adds the ``MutationSwitch``
  helper class and (b) inserts ``MutationSwitch.tick(<id>)`` as the first
  statement of N distinct existing methods spread across the library's
  components. Every insertion is a *dormant* performance mutant: it does nothing
  unless that exact id is armed at runtime via ``-Djmhbench.mutant=<id>``, and
  what it then injects is chosen by ``-Djmhbench.mutant.op``.

  Regenerating renumbers and relocates mutants, which breaks comparability with
  every scorecard already measured. To change only the switch class -- the
  machinery an armed tick runs -- use ``tools/refresh_mutation_switch.py``, which
  rewrites that hunk in place and leaves the call sites alone.
* ``mutants.yaml`` - the hidden ground-truth registry mapping each mutant id to
  its location (file, class, method, signature, line). Never shown to a harness.

The patch is produced with an isolated throwaway git repo so the diff is
byte-exact and applies cleanly with ``git apply --directory=src/main/java``.

Usage::

    python tools/make_project_mutants.py --project-dir dataset/projects/snakeyaml
    python tools/make_project_mutants.py --project-dir dataset/projects/hppc --count 50

Configuration is taken from ``<project-dir>/project.yaml``:

    source_dir:      where the pristine sources live (default src/main/java)
    mutation.switch_class:  FQCN of the generated MutationSwitch
    mutation.count:  how many mutants to bake in (``--count`` wins)
    harness_input.include_packages: component allow-list (empty = all)
    harness_input.name_filter:      regex marking "interesting" class names,
                                    which are mutated first
    mutation.skip_methods:  extra per-project method names never to mutate

Deterministic: the same source tree + count always yields the same mutants.
"""

from __future__ import annotations

import argparse
import re
import shutil
import subprocess
import sys
import tempfile
from collections import defaultdict
from dataclasses import dataclass
from pathlib import Path

import yaml

REPO_ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(REPO_ROOT))

from jmhbench.project_layout import base_package, component_of  # noqa: E402

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

# Per-element / hot-path method names shared across libraries. A mutant here
# fires Theta(payload-size) times per benchmark op, so the ~1ms per-call latency
# becomes a multi-second per-op regression that blows the JMH time budget
# instead of a small, realistically detectable one. We deliberately place
# mutants only at coarse, ~once-per-op method entries. Projects add their own
# hot names via ``mutation.skip_methods`` in project.yaml.
_HOT_NAMES = {
    # stream / codec per-byte plumbing
    "read",
    "write",
    "skip",
    "flush",
    "available",
    "readByte",
    "writeByte",
    "readBit",
    "writeBit",
    "readBits",
    "writeBits",
    "readBuffer",
    "fill",
    "nextByte",
    "getNextByte",
    "peek",
    "poll",
    "advance",
    "next",
    "hasNext",
    "nextToken",
    "nextChar",
    "getChar",
    "charAt",
    "forward",
    # per-element collection / hashing plumbing
    "get",
    "set",
    "put",
    "add",
    "remove",
    "contains",
    "containsKey",
    "indexOf",
    "hash",
    "hashKey",
    "mix",
    "compare",
    "compareTo",
    "iterator",
    "size",
    "length",
    "isEmpty",
    "count",
    "update",
    "apply",
    "test",
    "accept",
    "value",
    "key",
    "index",
    "getType",
    "getData",
    "getLength",
    "getOffset",
    "getBytesRead",
    "getBytesWritten",
    "encode",
    "decode",
    "matches",
    "copy",
}

# Matches the body-opening brace of a single-line method signature:
#   ... ) {              or      ... ) throws Foo, Bar {
_BODY_BRACE_RE = re.compile(r"\)\s*(?:throws\s[^{;]*?)?\{")
_IDENT_RE = re.compile(r"[A-Za-z_$][\w$]*")
_PACKAGE_RE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)


@dataclass(frozen=True)
class Site:
    rel_path: str          # path relative to the source root, e.g. org/.../Yaml.java
    component: str         # e.g. "constructor" (package segment below the base)
    class_name: str        # best-effort enclosing top-level class simple name
    method: str            # method name
    signature: str         # trimmed signature line
    line_index: int        # 0-based line index of the signature
    brace_col: int         # column right after the body-opening '{'
    priority: int          # lower = more interesting (matches name_filter)


@dataclass(frozen=True)
class Config:
    """Everything project-specific the generator needs."""

    src_root: Path
    switch_fqcn: str
    count: int
    include_packages: tuple[str, ...]     # empty = every component
    component_groups: tuple[str, ...]     # top segments labelled two-deep
    name_filter: str | None
    skip_methods: frozenset[str]

    @property
    def switch_package(self) -> str:
        return self.switch_fqcn.rsplit(".", 1)[0]

    @property
    def switch_rel_path(self) -> str:
        return self.switch_fqcn.replace(".", "/") + ".java"

    @property
    def switch_call(self) -> str:
        return f"{self.switch_fqcn}.tick"


_THROW_RE = re.compile(r"^\s*throw\b")


def _is_throw_stub(lines: list[str], idx: int) -> bool:
    """True if the method starting at *idx* does nothing but throw.

    Read-only views and unsupported-operation stubs (HPPC's
    ``SortedIteration*HashMap.release()``, for instance) are public methods that
    can never appear on a benchmark's measured path, so a mutant there is dead
    weight: it would never be killed by any suite, however thorough.
    """
    for line in lines[idx + 1 : idx + 3]:
        stripped = line.strip()
        if not stripped:
            continue
        return bool(_THROW_RE.match(stripped))
    return False


def _candidate_site(
    line: str,
    rel_path: str,
    idx: int,
    cfg: Config,
    base: str,
    lines: list[str],
) -> Site | None:
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
    if not header or "=" in line[:brace_col] or "@" in line[:brace_col]:
        return None

    tokens = _IDENT_RE.findall(header)
    if len(tokens) < 2:
        return None  # need <modifiers...> <returnType> <name>
    name = tokens[-1]
    before_name = tokens[:-1]
    # Constructor check: nothing but modifiers before the name => no return type.
    if all(tok in _MODIFIERS for tok in before_name):
        return None
    if name in _SKIP_NAMES or name in _HOT_NAMES or name in cfg.skip_methods:
        return None
    if name in _MODIFIERS:
        return None
    if _is_throw_stub(lines, idx):
        return None

    class_name = Path(rel_path).stem
    interesting = bool(re.search(cfg.name_filter, class_name)) if cfg.name_filter else True
    return Site(
        rel_path=rel_path,
        component=component_of(rel_path, base, cfg.component_groups),
        class_name=class_name,
        method=name,
        signature=stripped,
        line_index=idx,
        brace_col=brace_col,
        priority=0 if interesting else 1,
    )


def collect_sites(cfg: Config, base: str) -> dict[str, list[Site]]:
    """Group mutable method sites by component, sorted by interest then position."""
    by_component: dict[str, list[Site]] = defaultdict(list)
    for path in sorted(cfg.src_root.rglob("*.java")):
        rel = path.relative_to(cfg.src_root).as_posix()
        if rel == cfg.switch_rel_path or Path(rel).stem in ("package-info", "module-info"):
            continue
        comp = component_of(rel, base, cfg.component_groups)
        if cfg.include_packages and comp not in cfg.include_packages:
            continue
        lines = path.read_text(encoding="utf-8", errors="replace").splitlines()
        for idx, line in enumerate(lines):
            site = _candidate_site(line, rel, idx, cfg, base, lines)
            if site is not None:
                by_component[comp].append(site)
    for comp in by_component:
        by_component[comp].sort(key=lambda s: (s.priority, s.rel_path, s.line_index))
    return by_component


def _spread(items: list) -> list:
    """Reorder *items* so that a prefix of the result is spread evenly over it.

    Walking a sorted list front-to-back concentrates picks at one end of the
    alphabet, which matters for libraries whose classes are generated
    specialisations: taking the first 50 of HPPC's containers yields
    ``ByteArrayList`` through ``FloatStack`` and never reaches ``IntHashMap`` or
    ``ObjectObjectHashMap``. This visits midpoints recursively (the order a
    binary search would probe), so any prefix samples the whole range.
    Deterministic and dependency-free.
    """
    order: list = []
    queue = [(0, len(items) - 1)]
    while queue:
        lo, hi = queue.pop(0)
        if lo > hi:
            continue
        mid = (lo + hi) // 2
        order.append(items[mid])
        queue.append((lo, mid - 1))
        queue.append((mid + 1, hi))
    return order


def _file_order(sites: list[Site]) -> list[list[Site]]:
    """Group a component's sites per file, ordered interesting-first then spread.

    Files whose class name matches the project's ``name_filter`` (priority 0)
    come first; within each priority tier both the files and the sites inside a
    file are spread with :func:`_spread`, so mutants land across the whole
    component and across the whole class rather than clustering.
    """
    by_file: dict[str, list[Site]] = defaultdict(list)
    for site in sites:
        by_file[site.rel_path].append(site)

    tiers: dict[int, list[str]] = defaultdict(list)
    for rel_path, group in by_file.items():
        tiers[min(s.priority for s in group)].append(rel_path)

    ordered: list[list[Site]] = []
    for priority in sorted(tiers):
        for rel_path in _spread(sorted(tiers[priority])):
            group = sorted(by_file[rel_path], key=lambda s: s.line_index)
            ordered.append(_spread(group))
    return ordered


def select_sites(by_component: dict[str, list[Site]], count: int) -> list[Site]:
    """Round-robin across components to spread mutants, capping per-file picks.

    Each turn takes one site from a component, walking that component's files in
    spread order. ``file_cap`` starts at one mutant per file — maximum spread —
    and only rises when the project has too few files to reach *count*.
    """
    comp_files = {comp: _file_order(sites) for comp, sites in by_component.items()}
    comps = sorted(comp_files)

    for file_cap in (1, 2, 3, 4, 6, 100):
        selected: list[Site] = []
        # component -> [index of current file, index of next site within it]
        cursors: dict[str, list[int]] = {c: [0, 0] for c in comps}
        progress = True
        while len(selected) < count and progress:
            progress = False
            for comp in comps:
                if len(selected) >= count:
                    break
                files = comp_files[comp]
                file_i, site_i = cursors[comp]
                while file_i < len(files):
                    group = files[file_i]
                    if site_i < min(file_cap, len(group)):
                        selected.append(group[site_i])
                        cursors[comp] = [file_i, site_i + 1]
                        progress = True
                        break
                    file_i, site_i = file_i + 1, 0
                else:
                    cursors[comp] = [file_i, site_i]
        if len(selected) >= count:
            return selected[:count]
    return selected


_SWITCH_TEMPLATE = r"""/*
 * Copyright (c) JMH-Bench. Generated file - do not edit by hand.
 *
 * This class is not part of the upstream library; it is injected by JMH-Bench's
 * project-mutation track (tools/make_project_mutants.py) and is licensed under
 * the same terms as the vendored subject-under-test it is compiled into.
 */
package __PACKAGE__;

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


def _switch_source(cfg: Config) -> str:
    return _SWITCH_TEMPLATE.replace("__PACKAGE__", cfg.switch_package).replace(
        "__COUNT__", str(cfg.count)
    )


def _apply_insertions(work_root: Path, selected: list[Site], cfg: Config) -> None:
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
            insert = f" {cfg.switch_call}({mutant_id});"
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


def build_patch(cfg: Config, selected: list[Site]) -> str:
    """Produce the unified diff via an isolated throwaway git repo."""
    tmp = Path(tempfile.mkdtemp(prefix="jmhbench-mutants-"))
    try:
        work = tmp / "java"
        shutil.copytree(cfg.src_root, work)
        _git(["init", "-q"], work)
        _git(["add", "-A"], work)

        _apply_insertions(work, selected, cfg)

        switch_path = work / cfg.switch_rel_path
        switch_path.parent.mkdir(parents=True, exist_ok=True)
        switch_path.write_text(_switch_source(cfg), encoding="utf-8")
        _git(["add", "-N", cfg.switch_rel_path], work)

        diff = _git(["diff"], work)
        if diff.returncode != 0:
            raise RuntimeError(f"git diff failed: {diff.stderr}")
        return diff.stdout
    finally:
        shutil.rmtree(tmp, ignore_errors=True)


def _fqcn(rel_path: str) -> str:
    no_ext = rel_path[:-5] if rel_path.endswith(".java") else rel_path
    return no_ext.replace("/", ".")


def _mutants_yaml(display_name: str, selected: list[Site]) -> str:
    lines = [
        f"# Hidden ground-truth registry for the {display_name} mutation track.",
        "# Generated by tools/make_project_mutants.py - do not edit by hand.",
        "# NEVER show this file to a harness: it reveals where the mutants are.",
        f"count: {len(selected)}",
        "mutants:",
    ]
    for mutant_id, site in enumerate(selected, start=1):
        sig = site.signature.replace("\\", "\\\\").replace('"', '\\"')
        lines.append(f"  - id: {mutant_id}")
        lines.append(f"    file: {site.rel_path}")
        lines.append(f"    fqcn: {_fqcn(site.rel_path)}")
        lines.append(f"    component: {site.component}")
        lines.append(f"    method: {site.method}")
        lines.append(f'    signature: "{sig}"')
        lines.append(f"    line: {site.line_index + 1}")
    return "\n".join(lines) + "\n"


def load_config(project_dir: Path, count_override: int | None) -> tuple[Config, str]:
    spec_file = project_dir / "project.yaml"
    if not spec_file.exists():
        raise SystemExit(f"missing project.yaml in {project_dir}")
    data = yaml.safe_load(spec_file.read_text()) or {}

    src_root = project_dir / data.get("source_dir", "src/main/java")
    if not src_root.exists():
        raise SystemExit(f"source root not found: {src_root}")

    mutation = data.get("mutation") or {}
    switch_fqcn = mutation.get("switch_class")
    if not switch_fqcn:
        raise SystemExit(f"{spec_file}: mutation.switch_class is required")

    harness_input = data.get("harness_input") or {}
    cfg = Config(
        src_root=src_root,
        switch_fqcn=switch_fqcn,
        count=count_override or int(mutation.get("count", 50)),
        include_packages=tuple(harness_input.get("include_packages") or ()),
        component_groups=tuple(harness_input.get("component_groups") or ()),
        name_filter=harness_input.get("name_filter"),
        skip_methods=frozenset(mutation.get("skip_methods") or ()),
    )
    return cfg, data.get("display_name", project_dir.name)


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--project-dir", type=Path, required=True)
    parser.add_argument(
        "--count",
        type=int,
        default=None,
        help="Number of mutants (default: mutation.count from project.yaml).",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Report candidate/selected counts without writing any files.",
    )
    args = parser.parse_args()

    project_dir: Path = args.project_dir.resolve()
    cfg, display_name = load_config(project_dir, args.count)

    base = base_package(cfg.src_root)
    by_component = collect_sites(cfg, base)
    total_candidates = sum(len(v) for v in by_component.values())
    selected = select_sites(by_component, cfg.count)
    if len(selected) < cfg.count:
        raise SystemExit(
            f"only found {len(selected)} mutable sites (< {cfg.count}); "
            f"total candidates={total_candidates}"
        )

    dist: dict[str, int] = defaultdict(int)
    for s in selected:
        dist[s.component] += 1
    print(f"{display_name}: base package {base or '(none)'}")
    print(f"Selected {len(selected)} mutants from {total_candidates} candidates.")
    print("Distribution by component:")
    for comp in sorted(dist):
        print(f"  {comp:28s} {dist[comp]}")
    if args.dry_run:
        return

    patch = build_patch(cfg, selected)
    (project_dir / "mutations.patch").write_text(patch, encoding="utf-8")
    (project_dir / "mutants.yaml").write_text(
        _mutants_yaml(display_name, selected), encoding="utf-8"
    )
    print(f"\nWrote: {project_dir / 'mutations.patch'}")
    print(f"Wrote: {project_dir / 'mutants.yaml'}")


if __name__ == "__main__":
    main()
