"""Generate a fixed performance-mutation set for a subject library.

Ported and generalised from JMH-Bench ``tools/make_compress_mutants.py``: instead of the
hard-coded ``org.apache.commons.compress`` root, the package root is a parameter, so the same
generator plants mutants in any Java library (Commons Lang, fastutil, ...).

For a source tree it produces:

* ``mutations.patch`` — one unified diff that (a) adds a ``MutationSwitch`` helper class and
  (b) inserts ``MutationSwitch.tick(<id>)`` as the first statement of ``count`` distinct public
  methods spread across the library's components. Each insertion is a *dormant* performance
  mutant: a no-op unless that exact id is armed at run time via ``-Djmhbench.mutant=<id>``
  (which then injects a tiny, fixed ~1 ms latency).
* ``mutants.yaml`` — the hidden ground-truth registry mapping each id to its location.

The patch is byte-exact and deterministic (same source tree + count => same mutants); it is
built through an isolated throwaway git repo so it applies cleanly with
``git apply --directory=src/main/java``.
"""

from __future__ import annotations

import re
import shutil
import subprocess
import tempfile
from collections import defaultdict
from dataclasses import dataclass
from pathlib import Path

# Primary top-level type declaration, used to reject non-benchmarkable subjects (interfaces,
# annotations, abstract bases) that the policy cannot instantiate to drive a mutant.
_TYPE_DECL_RE = re.compile(
    r"(?m)^[ \t]*(?:public\s+)?"
    r"(?P<mods>(?:final\s+|abstract\s+|sealed\s+|non-sealed\s+|strictfp\s+|static\s+)*)"
    r"(?P<kind>@interface|interface|class|enum|record)\s+(?P<name>[A-Za-z_$][\w$]*)"
)

# Java modifiers that may precede a method's return type.
_MODIFIERS = frozenset(
    {
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
)

# Method names we never mutate: trivial Object overrides.
_SKIP_NAMES = frozenset({"equals", "hashCode", "toString", "clone", "finalize"})

# Per-element / hot-path methods. A mutant here fires Theta(payload-size) times per benchmark
# op, so a ~1 ms per-call sleep becomes a multi-second per-op regression that blows the JMH
# time budget instead of a tiny detectable one. We deliberately place mutants only at coarse,
# ~once-per-op method entries.
_HOT_NAMES = frozenset(
    {
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
        "get",
        "set",
        "put",
        "add",
        "remove",
        "contains",
        "next",
        "hasNext",
        "compare",
        "getByte",
        "getInt",
        "getLong",
        "getData",
        "getType",
    }
)

# Broad "interesting subject" heuristic: only affects ordering within a component so the more
# benchmark-worthy classes are picked first when a count cap bites.
_INTERESTING_RE = re.compile(
    r"(Utils|Util|List|Set|Map|Buffer|String|Array|Math|Number|Builder|Range|Parser|"
    r"Encoder|Decoder|Stream|Function|Comparator)"
)

# Matches the body-opening brace of a single-line method signature:
#   ... ) {              or      ... ) throws Foo, Bar {
_BODY_BRACE_RE = re.compile(r"\)\s*(?:throws\s[^{;]*?)?\{")
_IDENT_RE = re.compile(r"[A-Za-z_$][\w$]*")


@dataclass(frozen=True)
class Site:
    """A mutable single-line public-method header found in the source tree."""

    rel_path: str  # path relative to src/main/java, e.g. org/.../StringUtils.java
    component: str  # subpackage bucket, e.g. "text" or "(root)"
    class_name: str  # enclosing top-level class simple name (best-effort)
    method: str  # method name
    signature: str  # trimmed signature line
    line_index: int  # 0-based line index of the signature
    brace_col: int  # column right after the body-opening '{'
    priority: int  # lower = more interesting


@dataclass(frozen=True)
class MutantPlan:
    """The outcome of planning a mutation set for a source tree."""

    selected: list[Site]
    patch: str
    registry_yaml: str
    distribution: dict[str, int]
    total_candidates: int


def _package_prefix(package_root: str) -> str:
    return package_root.replace(".", "/") + "/"


def _component_of(rel_path: str, package_root: str) -> str:
    marker = _package_prefix(package_root)
    if marker not in rel_path:
        return "(root)"
    tail = rel_path.split(marker, 1)[-1]
    parts = tail.split("/")
    if len(parts) == 1:
        return "(root)"
    return parts[0]


def _class_name_of(rel_path: str) -> str:
    return Path(rel_path).stem


def is_benchmarkable_type(text: str, simple_name: str) -> bool:
    """Whether ``simple_name``'s top-level type is a concrete, instantiable subject.

    Interfaces, annotations, and ``abstract`` classes cannot be exercised by a generated
    benchmark, so a mutant planted there can never be killed. Enums, records, and concrete
    classes are benchmarkable. Unknown/unparseable declarations are treated as benchmarkable
    (permissive) since the automatic scanner already restricts to public method headers.
    """
    for match in _TYPE_DECL_RE.finditer(text):
        if match.group("name") != simple_name:
            continue
        kind = match.group("kind")
        if kind in {"interface", "@interface"}:
            return False
        return "abstract" not in (match.group("mods") or "")
    return True


def _fqcn(rel_path: str) -> str:
    no_ext = rel_path[:-5] if rel_path.endswith(".java") else rel_path
    return no_ext.replace("/", ".")


def _candidate_site(line: str, rel_path: str, idx: int, package_root: str) -> Site | None:
    """Return a :class:`Site` if *line* is a mutable single-line public-method header."""
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
    # The body brace must be the first '{' on the line (else it's an array initialiser,
    # annotation value, lambda, etc.).
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
    if name in _SKIP_NAMES or name in _HOT_NAMES or name in _MODIFIERS:
        return None

    class_name = _class_name_of(rel_path)
    return Site(
        rel_path=rel_path,
        component=_component_of(rel_path, package_root),
        class_name=class_name,
        method=name,
        signature=stripped,
        line_index=idx,
        brace_col=brace_col,
        priority=0 if _INTERESTING_RE.search(class_name) else 1,
    )


def _candidate_site_multiline(
    lines: list[str], rel_path: str, decl_idx: int, package_root: str
) -> Site | None:
    """Like :func:`_candidate_site` but resolves a body brace that may be on a later line."""
    line = lines[decl_idx]
    stripped = line.strip()
    if not (stripped.startswith("public ") or stripped.startswith("protected ")):
        return None
    if "abstract " in stripped:
        return None
    if "(" not in line:
        return None
    # Skip fields / assignments and annotation declarations on the header line.
    if "=" in line.split("(", 1)[0] or stripped.startswith("@"):
        return None

    header = line[: line.index("(")]
    tokens = _IDENT_RE.findall(header)
    if len(tokens) < 2:
        return None
    name = tokens[-1]
    before_name = tokens[:-1]
    if all(tok in _MODIFIERS for tok in before_name):
        return None
    if name in _SKIP_NAMES or name in _HOT_NAMES or name in _MODIFIERS:
        return None

    brace = _resolve_body_brace(lines, decl_idx)
    if brace is None:
        return None
    brace_line, brace_col = brace
    class_name = _class_name_of(rel_path)
    return Site(
        rel_path=rel_path,
        component=_component_of(rel_path, package_root),
        class_name=class_name,
        method=name,
        signature=stripped,
        line_index=brace_line,
        brace_col=brace_col,
        priority=0 if _INTERESTING_RE.search(class_name) else 1,
    )


def collect_sites(
    src_root: Path,
    package_root: str,
    *,
    include_subpackages: tuple[str, ...] = (),
    exclude_subpackages: tuple[str, ...] = (),
) -> dict[str, list[Site]]:
    """Group mutable method sites by component, sorted by interest then position."""
    prefix = _package_prefix(package_root)
    by_component: dict[str, list[Site]] = defaultdict(list)
    for path in sorted(src_root.rglob("*.java")):
        rel = path.relative_to(src_root).as_posix()
        if prefix not in f"{rel}":
            continue
        if rel.endswith("package-info.java") or rel.endswith("module-info.java"):
            continue
        comp = _component_of(rel, package_root)
        if include_subpackages and comp not in include_subpackages:
            continue
        if exclude_subpackages and comp in exclude_subpackages:
            continue
        text = path.read_text(encoding="utf-8", errors="replace")
        if not is_benchmarkable_type(text, _class_name_of(rel)):
            continue
        lines = text.splitlines()
        seen_lines: set[int] = set()
        for idx, line in enumerate(lines):
            site = _candidate_site(line, rel, idx, package_root)
            if site is None:
                site = _candidate_site_multiline(lines, rel, idx, package_root)
            if site is not None and site.line_index not in seen_lines:
                seen_lines.add(site.line_index)
                by_component[comp].append(site)
    for comp in by_component:
        by_component[comp].sort(key=lambda s: (s.priority, s.rel_path, s.line_index))
    return by_component


def select_sites(by_component: dict[str, list[Site]], count: int) -> list[Site]:
    """Round-robin across components to spread mutants, capping per-file picks."""
    for file_cap in (2, 3, 4, 6, 100):
        selected: list[Site] = []
        per_file: dict[str, int] = defaultdict(int)
        cursors: dict[str, int] = dict.fromkeys(by_component, 0)
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
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Generated by jmhgen (jmh-make-mutants); do not edit by hand.
 *
 * JMH-Bench-style performance-mutation switch baked into a copy of the subject-under-test.
 * Each call site {@code MutationSwitch.tick(id)} is an independent, dormant performance
 * mutant. Exactly one mutant is armed per JVM via {@code -Djmhbench.mutant=<id>}; when armed,
 * the matching site injects a tiny, fixed, reliably measurable latency. With
 * {@code -Djmhbench.record=true} the switch instead counts how many times each mutant id was
 * reached and dumps the counts to {@code -Djmhbench.record.file} at JVM shutdown.
 */
package __PACKAGE__;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class MutationSwitch {

    /** Number of mutants defined in this build. */
    public static final int COUNT = __COUNT__;

    private static final int ARMED = Integer.getInteger("jmhbench.mutant", -1);
    private static final boolean RECORD = Boolean.getBoolean("jmhbench.record");
    private static final long[] HITS = new long[COUNT + 1];

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
     * Mutation hook. No-op unless this {@code id} is armed (then it slows down) or recording is
     * enabled (then it counts the reach).
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

    private static void slow() {
        try {
            // Heavyweight operator: a tiny, fixed latency (see the JMH-Bench plan).
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


def _switch_source(package_root: str, count: int) -> str:
    switch_package = f"{package_root}.jmhbench"
    return _SWITCH_TEMPLATE.replace("__PACKAGE__", switch_package).replace("__COUNT__", str(count))


def _apply_insertions(work_root: Path, selected: list[Site], switch_call: str) -> None:
    """Insert the ``tick()`` call into each selected method (in the working copy)."""
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
            insert = f" {switch_call}({mutant_id});"
            lines[line_index] = body[:brace_col] + insert + body[brace_col:] + newline
        path.write_text("".join(lines), encoding="utf-8")


def _git(args: list[str], cwd: Path) -> subprocess.CompletedProcess:
    return subprocess.run(  # noqa: S603 - args built from trusted inputs
        ["git", "-c", "user.name=jmhgen", "-c", "user.email=jmhgen@local", *args],
        cwd=cwd,
        capture_output=True,
        text=True,
        check=False,
    )


def _build_patch(src_root: Path, selected: list[Site], package_root: str, count: int) -> str:
    """Produce the unified diff via an isolated throwaway git repo."""
    switch_rel_path = f"{package_root.replace('.', '/')}/jmhbench/MutationSwitch.java"
    switch_call = f"{package_root}.jmhbench.MutationSwitch.tick"
    tmp = Path(tempfile.mkdtemp(prefix="jmhgen-mutants-"))
    try:
        work = tmp / "java"
        shutil.copytree(src_root, work)
        _git(["init", "-q"], work)
        _git(["add", "-A"], work)

        _apply_insertions(work, selected, switch_call)

        switch_path = work / switch_rel_path
        switch_path.parent.mkdir(parents=True, exist_ok=True)
        switch_path.write_text(_switch_source(package_root, count), encoding="utf-8")
        _git(["add", "-N", switch_rel_path], work)

        diff = _git(["diff"], work)
        if diff.returncode != 0:
            raise RuntimeError(f"git diff failed: {diff.stderr}")
        return diff.stdout
    finally:
        shutil.rmtree(tmp, ignore_errors=True)


def _registry_yaml(selected: list[Site], package_root: str) -> str:
    lines = [
        "# Hidden ground-truth registry for the mutation reward.",
        "# Generated by jmhgen (jmh-make-mutants) - do not edit by hand.",
        "# NEVER show this file to the policy: it reveals where the mutants are.",
        f"package_root: {package_root}",
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


def _match_from_signature(signature: str) -> str:
    """Derive a ``mutation_sites.yaml`` ``match`` substring from a method header line."""
    if "(" not in signature:
        return "()"
    start = signature.index("(")
    end = signature.rfind("{")
    chunk = signature[start:end] if end != -1 else signature[start:]
    return chunk.strip()


def sites_yaml(selected: list[Site], package_root: str, *, project: str | None = None) -> str:
    """Serialize selected sites into a curated ``mutation_sites.yaml`` (re-plantable)."""
    header = [
        f"# Curated performance-mutation sites for {project or package_root} (GRPO RL corpus).",
        "#",
        "# Concrete classes and hot ~once-per-call public methods. Several methods per class",
        "# => killed / in-class mutants rewards benchmark coverage of the subject's core API.",
        "#",
        "# Regenerate with:",
        f"#   jmh-make-mutants {project or '<id>'} --source data/external-src/<checkout> \\",
        f"#       --sites data/projects/{project or '<id>'}/mutation_sites.yaml",
        "",
        f"package_root: {package_root}",
        "",
        "sites:",
    ]
    lines = list(header)
    for site in selected:
        fqcn = _fqcn(site.rel_path)
        match = _match_from_signature(site.signature).replace('"', '\\"')
        note = f"{site.class_name}.{site.method}"
        lines.append(
            f'  - {{class: {fqcn}, method: {site.method}, match: "{match}", note: "{note}"}}'
        )
    return "\n".join(lines) + "\n"


def generate_mutants(
    src_root: str | Path,
    package_root: str,
    *,
    count: int = 100,
    include_subpackages: tuple[str, ...] = (),
    exclude_subpackages: tuple[str, ...] = (),
    allow_partial: bool = False,
) -> MutantPlan:
    """Plan a mutation set for ``src_root`` and return the patch + registry text.

    ``src_root`` is the ``src/main/java`` root; ``package_root`` is the library's base package
    (e.g. ``org.apache.commons.lang3``). Raises if fewer than ``count`` mutable sites exist,
    unless ``allow_partial`` is set (then any non-empty selection is accepted).
    """
    root = Path(src_root)
    if not root.exists():
        raise FileNotFoundError(f"source root not found: {root}")

    by_component = collect_sites(
        root,
        package_root,
        include_subpackages=include_subpackages,
        exclude_subpackages=exclude_subpackages,
    )
    total_candidates = sum(len(v) for v in by_component.values())
    selected = select_sites(by_component, count)
    if len(selected) < count and not allow_partial:
        raise ValueError(
            f"only found {len(selected)} mutable sites (< {count}); "
            f"total candidates={total_candidates}"
        )
    if not selected:
        raise ValueError(
            f"no mutable sites under {package_root}; total candidates={total_candidates}"
        )

    patch = _build_patch(root, selected, package_root, len(selected))
    registry_yaml = _registry_yaml(selected, package_root)
    distribution: dict[str, int] = defaultdict(int)
    for site in selected:
        distribution[site.component] += 1
    return MutantPlan(
        selected=selected,
        patch=patch,
        registry_yaml=registry_yaml,
        distribution=dict(sorted(distribution.items())),
        total_candidates=total_candidates,
    )


# --- curated placement ----------------------------------------------------------------------
# The heuristic planter (above) spreads mutants by name/pattern; curated placement instead takes
# an explicit, human-authored list of (class, method) sites so mutants land on genuinely
# benchmark-worthy methods, several per class, spread across distinct methods -- which is what
# makes ``killed / in-class mutants`` a real per-class *coverage* reward.


@dataclass(frozen=True)
class SiteSpec:
    """A hand-picked mutation site: a method to guard, addressed by class + name (+ overload)."""

    fqcn: str  # fully-qualified subject class, e.g. org.apache.commons.lang3.StringUtils
    method: str  # method name to guard
    match: str | None = None  # optional substring of the header to disambiguate overloads
    note: str | None = None  # rationale (curation aid; not emitted)


def _resolve_body_brace(
    lines: list[str], decl_idx: int, *, max_span: int = 16
) -> tuple[int, int] | None:
    """From a declaration line, find the (line_index, col-after) of the body-opening ``{``.

    Scans forward across up to ``max_span`` lines tracking parenthesis depth, so it handles
    multi-line signatures. Returns ``None`` for an abstract/interface method (``;`` before any
    body brace at paren-depth 0).
    """
    depth = 0
    started = False
    for line_index in range(decl_idx, min(decl_idx + max_span, len(lines))):
        for col, ch in enumerate(lines[line_index]):
            if ch == "(":
                depth += 1
                started = True
            elif ch == ")":
                depth = max(0, depth - 1)
            elif ch == "{" and started and depth == 0:
                return line_index, col + 1
            elif ch == ";" and started and depth == 0:
                return None
    return None


def _is_constructor_header(header_before_paren: str, method_name: str) -> bool:
    """True when the header is a constructor (no return type before the name)."""
    tokens = _IDENT_RE.findall(header_before_paren)
    if not tokens or tokens[-1] != method_name:
        return False
    before_name = tokens[:-1]
    return bool(before_name) and all(tok in _MODIFIERS for tok in before_name)


def _resolve_one_site(
    lines: list[str], rel_path: str, package_root: str, spec: SiteSpec
) -> Site | None:
    """Locate ``spec`` in an already-read source file, or ``None`` if not found."""
    header_re = re.compile(
        rf"^[ \t]*(?:public|protected)[\w\s.\[\]<>,?&]*?\b{re.escape(spec.method)}\s*\("
    )
    for idx, line in enumerate(lines):
        if header_re.search(line) is None:
            continue
        brace = _resolve_body_brace(lines, idx)
        if brace is None:
            continue
        brace_line, brace_col = brace
        if spec.match is not None:
            header_text = " ".join(part.strip() for part in lines[idx : brace_line + 1])
            if spec.match not in header_text:
                continue
        header = line[: line.index("(")] if "(" in line else line
        # Constructors cannot host ``tick()`` as the first body statement — ``super()``/
        # ``this()`` must come first — so curated constructor sites are skipped.
        if _is_constructor_header(header, spec.method):
            return None
        # Also skip if the body already starts with super()/this().
        body_line = lines[brace_line][brace_col:].lstrip()
        if body_line.startswith(("super(", "this(")) or (
            brace_line + 1 < len(lines)
            and lines[brace_line + 1].lstrip().startswith(("super(", "this("))
        ):
            return None
        return Site(
            rel_path=rel_path,
            component=_component_of(rel_path, package_root),
            class_name=_class_name_of(rel_path),
            method=spec.method,
            signature=lines[idx].strip(),
            line_index=brace_line,
            brace_col=brace_col,
            priority=0,
        )
    return None


def resolve_sites(src_root: str | Path, package_root: str, specs: list[SiteSpec]) -> list[Site]:
    """Resolve curated ``specs`` to concrete :class:`Site` objects (order preserved).

    Raises ``ValueError`` on any unresolved/invalid site so curation mistakes surface loudly
    rather than silently dropping a mutant. Constructor sites are skipped (not an error).
    """
    root = Path(src_root)
    sites: list[Site] = []
    skipped_ctors = 0
    for spec in specs:
        rel = spec.fqcn.replace(".", "/") + ".java"
        path = root / rel
        if not path.exists():
            raise ValueError(f"curated site class not found: {spec.fqcn} ({path})")
        text = path.read_text(encoding="utf-8", errors="replace")
        if not is_benchmarkable_type(text, spec.fqcn.rsplit(".", 1)[-1]):
            raise ValueError(
                f"curated site class is not benchmarkable "
                f"(interface/annotation/abstract): {spec.fqcn}"
            )
        site = _resolve_one_site(text.splitlines(), rel, package_root, spec)
        if site is None:
            # Distinguish constructor skips from genuine misses by re-checking the header.
            lines = text.splitlines()
            header_re = re.compile(
                rf"^[ \t]*(?:public|protected)[\w\s.\[\]<>,?&]*?\b{re.escape(spec.method)}\s*\("
            )
            was_ctor = False
            for idx, line in enumerate(lines):
                if header_re.search(line) is None:
                    continue
                header = line[: line.index("(")] if "(" in line else line
                if _is_constructor_header(header, spec.method):
                    was_ctor = True
                    break
            if was_ctor:
                skipped_ctors += 1
                continue
            hint = f" matching {spec.match!r}" if spec.match else ""
            raise ValueError(f"could not resolve method {spec.method!r} in {spec.fqcn}{hint}")
        sites.append(site)
    if not sites:
        raise ValueError(
            f"no plantable curated sites under {package_root} "
            f"(skipped_constructors={skipped_ctors})"
        )
    return sites


def generate_from_sites(
    src_root: str | Path, package_root: str, specs: list[SiteSpec]
) -> MutantPlan:
    """Plan a mutation set from an explicit curated site list (see :func:`resolve_sites`)."""
    root = Path(src_root)
    if not root.exists():
        raise FileNotFoundError(f"source root not found: {root}")
    if not specs:
        raise ValueError("no curated sites provided")
    selected = resolve_sites(root, package_root, specs)
    count = len(selected)
    patch = _build_patch(root, selected, package_root, count)
    registry_yaml = _registry_yaml(selected, package_root)
    distribution: dict[str, int] = defaultdict(int)
    for site in selected:
        distribution[site.component] += 1
    return MutantPlan(
        selected=selected,
        patch=patch,
        registry_yaml=registry_yaml,
        distribution=dict(sorted(distribution.items())),
        total_candidates=count,
    )
