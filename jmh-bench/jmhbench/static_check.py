"""Static anti-pattern checks on a generated JMH benchmark source file.

We implement the five "prevalent bad practices" surfaced by Costa et al.
("What's Wrong With My Benchmark Results?", TSE 2019) as lightweight,
regex-based heuristics. They are deliberately conservative: false negatives
are preferable to false positives so that an honest harness isn't penalised
unfairly.

Five checks (matching the Costa et al. taxonomy adapted to JMH 1.x):

* ``dce_risk`` — at least one ``@Benchmark`` method returns ``void`` without
  taking a ``Blackhole`` parameter (dead-code-elimination risk).
* ``constant_folding`` — a benchmark operates purely on literals / fields
  declared ``static final`` with no ``@State`` and no ``@Param``.
* ``missing_jmh_config`` — neither class nor any benchmark method carries the
  fork / warmup / measurement annotations *and* no defaults are configured.
* ``wrong_mode`` — no ``@BenchmarkMode`` is set anywhere, leaving JMH to
  default to throughput which is often inappropriate.
* ``final_local_input`` — local variables fed into the benchmark are declared
  ``final`` with literal initialisers (forces constant folding even with @State).
"""

from __future__ import annotations

import re
from dataclasses import dataclass, field

ANTI_PATTERNS = [
    "dce_risk",
    "constant_folding",
    "missing_jmh_config",
    "wrong_mode",
    "final_local_input",
]


@dataclass
class StaticCheckResult:
    violations: dict[str, bool] = field(default_factory=lambda: {k: False for k in ANTI_PATTERNS})
    notes: dict[str, str] = field(default_factory=dict)

    @property
    def total(self) -> int:
        return sum(1 for v in self.violations.values() if v)

    @property
    def clean(self) -> bool:
        return self.total == 0

    def to_dict(self) -> dict:
        return {
            "violations": dict(self.violations),
            "notes": dict(self.notes),
            "total": self.total,
        }


_BENCHMARK_METHOD_RE = re.compile(
    r"@Benchmark\b[^\n]*\n+\s*(?:public|private|protected)?\s*([\w<>\[\],\s.]+?)\s+"
    r"(\w+)\s*\(([^)]*)\)",
    re.MULTILINE,
)
_STATE_RE = re.compile(r"@State\b")
_PARAM_RE = re.compile(r"@Param\b")
_BLACKHOLE_RE = re.compile(r"\bBlackhole\b")
_BENCHMARK_MODE_RE = re.compile(r"@BenchmarkMode\b")
_FORK_RE = re.compile(r"@Fork\b")
_WARMUP_RE = re.compile(r"@Warmup\b")
_MEASUREMENT_RE = re.compile(r"@Measurement\b")
_STATIC_FINAL_LITERAL_RE = re.compile(
    r"\b(?:public|private|protected)?\s*static\s+final\s+\w+\s+\w+\s*=\s*[^;]+;"
)
_FINAL_LOCAL_LITERAL_RE = re.compile(
    r"\bfinal\s+(?:int|long|double|float|String|boolean|byte|short|char)\s+\w+\s*=\s*"
    r"(?:\d|\"|true|false|'[^']'|0x)[^;]*;"
)


def _strip_block_comments(src: str) -> str:
    return re.sub(r"/\*.*?\*/", "", src, flags=re.DOTALL)


def check(source: str) -> StaticCheckResult:
    """Run all anti-pattern checks on *source* (one Java file's contents)."""
    src = _strip_block_comments(source)
    result = StaticCheckResult()

    benchmark_methods = list(_BENCHMARK_METHOD_RE.finditer(src))
    if not benchmark_methods:
        result.violations["missing_jmh_config"] = True
        result.notes["missing_jmh_config"] = "No @Benchmark method detected"
        return result

    # ---- dce_risk -------------------------------------------------------
    void_no_bh: list[str] = []
    for m in benchmark_methods:
        ret_type = m.group(1).strip()
        params = m.group(3)
        if ret_type == "void" and "Blackhole" not in params:
            void_no_bh.append(m.group(2))
    if void_no_bh:
        result.violations["dce_risk"] = True
        result.notes["dce_risk"] = (
            f"void benchmark(s) without Blackhole: {', '.join(void_no_bh)}"
        )

    # ---- constant_folding ---------------------------------------------
    has_state = bool(_STATE_RE.search(src))
    has_param = bool(_PARAM_RE.search(src))
    if not has_state and not has_param:
        # Look at how the @Benchmark methods get their inputs. If everything
        # they touch is a static final literal, this is the classic CF trap.
        sfl_count = len(_STATIC_FINAL_LITERAL_RE.findall(src))
        if sfl_count > 0 or _looks_pure_literal(benchmark_methods, src):
            result.violations["constant_folding"] = True
            result.notes["constant_folding"] = (
                f"No @State / @Param and {sfl_count} static-final literal field(s)"
            )

    # ---- missing_jmh_config -------------------------------------------
    if not (_FORK_RE.search(src) and _WARMUP_RE.search(src) and _MEASUREMENT_RE.search(src)):
        result.violations["missing_jmh_config"] = True
        missing = [
            name
            for name, pat in [
                ("@Fork", _FORK_RE),
                ("@Warmup", _WARMUP_RE),
                ("@Measurement", _MEASUREMENT_RE),
            ]
            if not pat.search(src)
        ]
        result.notes["missing_jmh_config"] = "Missing " + ", ".join(missing)

    # ---- wrong_mode ----------------------------------------------------
    if not _BENCHMARK_MODE_RE.search(src):
        result.violations["wrong_mode"] = True
        result.notes["wrong_mode"] = "No @BenchmarkMode declared"

    # ---- final_local_input --------------------------------------------
    if _FINAL_LOCAL_LITERAL_RE.search(src):
        result.violations["final_local_input"] = True
        result.notes["final_local_input"] = (
            "Final local with literal initialiser feeds the @Benchmark body"
        )

    return result


def _looks_pure_literal(method_matches: list[re.Match], src: str) -> bool:
    """Heuristic: a benchmark body whose only operands are numeric/string literals."""
    for m in method_matches:
        start = m.end()
        depth = 0
        body_start = src.find("{", start)
        if body_start < 0:
            continue
        i = body_start
        while i < len(src):
            if src[i] == "{":
                depth += 1
            elif src[i] == "}":
                depth -= 1
                if depth == 0:
                    body = src[body_start + 1 : i]
                    if re.match(r"^\s*return\s+[\d\s+\-*/().]+;\s*$", body):
                        return True
                    break
            i += 1
    return False
