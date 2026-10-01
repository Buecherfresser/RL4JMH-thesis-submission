"""The timeout-as-kill policy and its baseline precondition.

An armed run that blows its wall-clock budget counts as a kill only when that
benchmark's baseline completed. With a baseline the armed run exceeded
``5 x baseline_wall + 60s`` -- a >5x slowdown against a 1.10 kill threshold.
Without one, nothing separates a mutant-induced slowdown from a benchmark that
never fit the cap, so it stays an error.
"""
from __future__ import annotations

import importlib.util
import sys
from pathlib import Path

from jmhbench.project_bench import BenchmarkVerdict

_RESCORE = Path(__file__).resolve().parents[1] / "tools" / "campaign-rerun" / "rescore.py"
_spec = importlib.util.spec_from_file_location("rescore", _RESCORE)
rescore_mod = importlib.util.module_from_spec(_spec)
sys.modules["rescore"] = rescore_mod
_spec.loader.exec_module(rescore_mod)


def test_verdict_defaults_to_not_a_timeout_kill():
    assert BenchmarkVerdict(benchmark="b", method="m").timeout_kill is False


def test_timeout_with_measured_baseline_is_attributable():
    det = {"timed_out": True,
           "error": "armed run exceeded its 900s budget (baseline 122s)"}
    assert rescore_mod.timeout_attributable(det) is True


def test_timeout_without_baseline_is_not_attributable():
    det = {"timed_out": True, "error": "armed run exceeded its 900s budget"}
    assert rescore_mod.timeout_attributable(det) is False


def test_plain_error_is_not_attributable():
    assert rescore_mod.timeout_attributable(
        {"timed_out": False, "error": "JMH exited with code 1"}) is False


def _card(dets, status="error", n=10):
    return {"mutant_count": n,
            "mutant_results": [{"id": 1, "status": status, "detections": dets}]}


def test_errored_mutant_with_attributable_timeout_is_promoted():
    r = _card([{"timed_out": True,
                "error": "armed run exceeded its 900s budget (baseline 122s)"}])
    out = rescore_mod.rescore(r)
    assert out["promoted"] == [1]
    assert r["mutant_results"][0]["status"] == "killed"
    # promoted mutants must enter the denominator's covered set too
    assert r["mutants_covered"] == 1
    assert r["mutants_killed_by_timeout"] == 1
    assert r["mutation_score"] == 0.1


def test_errored_mutant_without_baseline_stays_an_error():
    r = _card([{"timed_out": True, "error": "armed run exceeded its 900s budget"}])
    out = rescore_mod.rescore(r)
    assert out["promoted"] == []
    assert r["mutant_results"][0]["status"] == "error"
    assert r["mutants_killed"] == 0
    assert r["mutants_covered"] == 0


def test_already_killed_mutant_is_untouched_and_uncounted():
    r = _card([{"timed_out": False, "error": None}], status="killed")
    rescore_mod.rescore(r)
    assert r["mutants_killed"] == 1
    # it was killed on a real effect size, so it is not a timeout kill
    assert r["mutants_killed_by_timeout"] == 0


def test_covered_not_killed_is_never_promoted():
    r = _card([{"timed_out": False, "error": None}], status="covered_not_killed")
    rescore_mod.rescore(r)
    assert r["mutants_killed"] == 0
    assert r["mutants_covered"] == 1


def test_errors_only_score_is_recoverable():
    """mutants_killed - mutants_killed_by_timeout must give the strict score."""
    r = {"mutant_count": 4, "mutant_results": [
        {"id": 1, "status": "killed", "detections": [{"timed_out": False}]},
        {"id": 2, "status": "error", "detections": [
            {"timed_out": True, "error": "armed run exceeded its 900s budget (baseline 5s)"}]},
        {"id": 3, "status": "error", "detections": [
            {"timed_out": True, "error": "armed run exceeded its 900s budget"}]},
        {"id": 4, "status": "covered_not_killed", "detections": [{"timed_out": False}]},
    ]}
    rescore_mod.rescore(r)
    assert r["mutants_killed"] == 2
    assert r["mutants_killed_by_timeout"] == 1
    assert r["mutants_killed"] - r["mutants_killed_by_timeout"] == 1
    assert r["mutants_errored"] == 1
