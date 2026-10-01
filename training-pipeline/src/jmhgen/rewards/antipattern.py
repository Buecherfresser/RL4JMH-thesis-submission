"""Anti-pattern reward (``r_anti``): freedom from JMH benchmarking smells (Costa et al.).

Two tiers, matching JMH-Bench:

* **SpotJMHBugs** (authoritative) — bytecode analysis via the Costa et al. SpotBugs plugin.
  Runs against ``target/classes`` after a successful compile. Catches LOOP, non-void RETU,
  INVO, and other patterns regex cannot see.
* **Regex pre-screen** (fallback) — JMH-Bench ``static_check.py``, used when SpotBugs is not
  installed after a successful compile.

The reward value is graded: ``1 - hits / N`` where ``N`` is five Costa categories (SpotJMH)
or five regex heuristics (fallback). A clean benchmark scores 1.0; each hit costs an equal
fraction.

Install SpotJMHBugs once: ``./tools/install_spotjmhbugs.sh`` (populates ``vendor/spotbugs/``).
"""

from __future__ import annotations

import re
from dataclasses import dataclass, field
from typing import Literal

from jmhgen.rewards.base import RewardResult
from jmhgen.rewards.spotjmhbugs import (
    gate_invo_by_runtime,
    per_op_nanos,
    score_from_result,
)
from jmhgen.rewards.spotjmhbugs import (
    is_available as spotjmh_available,
)
from jmhgen.rewards.spotjmhbugs import (
    run as run_spotjmh,
)
from jmhgen.runner.types import EvaluationResult

Backend = Literal["auto", "regex", "spotjmh"]

# Five regex anti-patterns (JMH-Bench static_check.py), in JMH-Bench order.
ANTI_PATTERNS: tuple[str, ...] = (
    "dce_risk",
    "constant_folding",
    "missing_jmh_config",
    "wrong_mode",
    "final_local_input",
)

COSTA_CATEGORY: dict[str, str | None] = {
    "dce_risk": "RETU",
    "constant_folding": "FINAL",
    "missing_jmh_config": "FORK",
    "wrong_mode": None,
    "final_local_input": "FINAL",
}

_BENCHMARK_METHOD_RE = re.compile(
    r"@Benchmark\b[^\n]*\n+\s*(?:public|private|protected)?\s*([\w<>\[\],\s.]+?)\s+"
    r"(\w+)\s*\(([^)]*)\)",
    re.MULTILINE,
)
_STATE_RE = re.compile(r"@State\b")
_PARAM_RE = re.compile(r"@Param\b")
_FORK_RE = re.compile(r"@Fork\b")
_WARMUP_RE = re.compile(r"@Warmup\b")
_MEASUREMENT_RE = re.compile(r"@Measurement\b")
_BENCHMARK_MODE_RE = re.compile(r"@BenchmarkMode\b")
_STATIC_FINAL_LITERAL_RE = re.compile(
    r"\b(?:public|private|protected)?\s*static\s+final\s+\w+\s+\w+\s*=\s*[^;]+;"
)
_FINAL_LOCAL_LITERAL_RE = re.compile(
    r"\bfinal\s+(?:int|long|double|float|String|boolean|byte|short|char)\s+\w+\s*=\s*"
    r"(?:\d|\"|true|false|'[^']'|0x)[^;]*;"
)
_PURE_LITERAL_BODY_RE = re.compile(r"^\s*return\s+[\d\s+\-*/().]+;\s*$")


@dataclass(frozen=True, slots=True)
class AntiPatternResult:
    """Outcome of the regex anti-pattern scan over one benchmark source."""

    violations: dict[str, bool] = field(default_factory=dict)
    notes: dict[str, str] = field(default_factory=dict)

    @property
    def total(self) -> int:
        return sum(1 for v in self.violations.values() if v)

    @property
    def clean(self) -> bool:
        return self.total == 0

    def to_dict(self) -> dict[str, object]:
        return {
            "violations": dict(self.violations),
            "notes": dict(self.notes),
            "total": self.total,
            "costa": {p: COSTA_CATEGORY.get(p) for p in self.violations if self.violations[p]},
        }


def _strip_block_comments(src: str) -> str:
    return re.sub(r"/\*.*?\*/", "", src, flags=re.DOTALL)


def _looks_pure_literal(method_matches: list[re.Match[str]], src: str) -> bool:
    for m in method_matches:
        body_start = src.find("{", m.end())
        if body_start < 0:
            continue
        depth = 0
        i = body_start
        while i < len(src):
            if src[i] == "{":
                depth += 1
            elif src[i] == "}":
                depth -= 1
                if depth == 0:
                    body = src[body_start + 1 : i]
                    if _PURE_LITERAL_BODY_RE.match(body):
                        return True
                    break
            i += 1
    return False


def check_anti_patterns(source: str) -> AntiPatternResult:
    """Run all five regex anti-pattern checks on one benchmark's ``source``."""
    src = _strip_block_comments(source)
    violations = dict.fromkeys(ANTI_PATTERNS, False)
    notes: dict[str, str] = {}

    benchmark_methods = list(_BENCHMARK_METHOD_RE.finditer(src))
    if not benchmark_methods:
        violations["missing_jmh_config"] = True
        notes["missing_jmh_config"] = "No @Benchmark method detected"
        return AntiPatternResult(violations=violations, notes=notes)

    void_no_bh = [
        m.group(2)
        for m in benchmark_methods
        if m.group(1).strip() == "void" and "Blackhole" not in m.group(3)
    ]
    if void_no_bh:
        violations["dce_risk"] = True
        notes["dce_risk"] = f"void benchmark(s) without Blackhole: {', '.join(void_no_bh)}"

    if not _STATE_RE.search(src) and not _PARAM_RE.search(src):
        sfl_count = len(_STATIC_FINAL_LITERAL_RE.findall(src))
        if sfl_count > 0 or _looks_pure_literal(benchmark_methods, src):
            violations["constant_folding"] = True
            notes["constant_folding"] = (
                f"No @State / @Param and {sfl_count} static-final literal field(s)"
            )

    missing = [
        name
        for name, pat in (
            ("@Fork", _FORK_RE),
            ("@Warmup", _WARMUP_RE),
            ("@Measurement", _MEASUREMENT_RE),
        )
        if not pat.search(src)
    ]
    if missing:
        violations["missing_jmh_config"] = True
        notes["missing_jmh_config"] = "Missing " + ", ".join(missing)

    if not _BENCHMARK_MODE_RE.search(src):
        violations["wrong_mode"] = True
        notes["wrong_mode"] = "No @BenchmarkMode declared"

    if _FINAL_LOCAL_LITERAL_RE.search(src):
        violations["final_local_input"] = True
        notes["final_local_input"] = "Final local with literal initialiser feeds the @Benchmark"

    return AntiPatternResult(violations=violations, notes=notes)


def _best_per_op_ns(evaluation: EvaluationResult) -> float | None:
    """Slowest measured op across @Benchmark methods (conservative INVO gating input)."""
    run = evaluation.run
    if run is None:
        return None
    per_ops = [
        ns
        for s in run.stats
        if (ns := per_op_nanos(s.score, s.unit, s.mode)) is not None
    ]
    return max(per_ops) if per_ops else None


class AntiPatternReward:
    """``r_anti``: rewards benchmarks free of Costa et al. anti-patterns.

    Prefers SpotJMHBugs bytecode analysis when ``backend`` is ``auto`` or ``spotjmh`` and a
    compiled ``project_dir`` is available; otherwise falls back to the regex pre-screen.
    """

    name = "anti_pattern"

    def __init__(
        self,
        on_missing_source: float = 0.0,
        backend: Backend = "auto",
    ) -> None:
        self.on_missing_source = on_missing_source
        self.backend = backend

    def score_source(self, source: str) -> RewardResult:
        if not source.strip():
            return RewardResult(
                name=self.name,
                value=self.on_missing_source,
                detail={"error": "no_source", "source": "regex"},
            )
        result = check_anti_patterns(source)
        value = 1.0 - result.total / len(ANTI_PATTERNS)
        return RewardResult(
            name=self.name,
            value=value,
            detail={**result.to_dict(), "source": "regex"},
        )

    def _score_spotjmh(self, evaluation: EvaluationResult) -> RewardResult | None:
        meta = evaluation.metadata or {}
        project_dir = evaluation.compile.project_dir or meta.get("project_dir")
        if not project_dir or not evaluation.compiled:
            return None

        extra = tuple(str(p) for p in (meta.get("extra_classpath") or ()))
        sj = run_spotjmh(project_dir, extra_classpath=extra)
        if not sj.available:
            return None
        if sj.error:
            return RewardResult(
                name=self.name,
                value=self.on_missing_source,
                detail={**sj.to_dict(), "source": "spotjmhbugs", "error": sj.error},
            )

        detail = sj.to_dict()
        detail["source"] = "spotjmhbugs"
        if evaluation.ran:
            detail = gate_invo_by_runtime(detail, _best_per_op_ns(evaluation))
        value = score_from_result(detail)
        return RewardResult(name=self.name, value=value, detail=detail)

    def __call__(self, evaluation: EvaluationResult) -> RewardResult:
        # A clean-looking source is not a useful benchmark if it cannot compile. Keeping this
        # hard gate avoids rewarding static formatting while compile/runtime/mutation are zero.
        if not evaluation.compiled:
            return RewardResult(
                name=self.name,
                value=self.on_missing_source,
                detail={"error": "not_compiled", "source": "gated"},
            )
        use_spotjmh = self.backend in ("auto", "spotjmh") and (
            self.backend == "spotjmh" or spotjmh_available()
        )
        if use_spotjmh:
            spot = self._score_spotjmh(evaluation)
            if spot is not None:
                return spot
            if self.backend == "spotjmh":
                return RewardResult(
                    name=self.name,
                    value=self.on_missing_source,
                    detail={"error": "spotjmh_unavailable", "source": "spotjmhbugs"},
                )

        source = str((evaluation.metadata or {}).get("source") or "")
        return self.score_source(source)
