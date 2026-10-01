"""Tests for scalar-only GRPO telemetry and diagnostics summaries."""

from __future__ import annotations

import json

import pytest

from jmhgen.cli.plot_grpo_metrics import _plot, diagnostics_summary, read_csv_rows
from jmhgen.training.grpo_metrics import GrpoMetricsCollector


def _components() -> dict[str, object]:
    return {
        "compile": {"value": 1.0, "weighted": 0.2, "detail": {"success": True}},
        "runtime": {"value": 1.0, "weighted": 0.2, "detail": {"success": True}},
        "mutation": {
            "value": 0.5,
            "weighted": 0.1,
            "detail": {
                "score": 0.5,
                "killed": 1,
                "total": 2,
                "attempted": 2,
                "sampled_kill_rate": 0.5,
                "coverage_rate": 1.0,
                "conditional_kill_rate": 0.5,
                "not_covered": 0,
                "covered_not_killed": 1,
                "killed_timeout": 0,
                "killed_statistical": 1,
                "verdicts": [{"id": 1, "killed": True}],
                "source": "must-not-be-logged",
            },
        },
    }


def test_collector_writes_sanitized_rollouts_and_step_metrics(tmp_path) -> None:
    collector = GrpoMetricsCollector(tmp_path, failure_sample_limit=1)
    collector.record_rollout(
        project="example",
        snippet_id="com.example.Foo",
        parse_ok=True,
        compiled=True,
        ran=True,
        compile_error_kind="none",
        run_error_kind="none",
        reward=0.5,
        reward_components=_components(),
        duration_s=1.25,
    )
    collector.record_rollout(
        project="example",
        snippet_id="com.example.Bar",
        parse_ok=False,
        compiled=False,
        ran=False,
        compile_error_kind=None,
        run_error_kind=None,
        reward=0.0,
        reward_components=None,
        duration_s=0.01,
    )
    collector.finish_reward_batch(2)
    collector.flush_step(7, {"loss": 0.1, "reward": 0.25, "reward_std": 0.25})
    collector.finish(7)

    rollouts = [
        json.loads(line) for line in (tmp_path / "rollouts.jsonl").read_text().splitlines()
    ]
    assert len(rollouts) == 2
    assert rollouts[0]["mutation"] == {
        "attempted": 2,
        "conditional_kill_rate": 0.5,
        "coverage_rate": 1.0,
        "covered_not_killed": 1,
        "killed": 1,
        "killed_statistical": 1,
        "killed_timeout": 0,
        "not_covered": 0,
        "sampled_kill_rate": 0.5,
        "score": 0.5,
        "total": 2,
    }
    assert "verdicts" not in (tmp_path / "rollouts.jsonl").read_text()
    assert "must-not-be-logged" not in (tmp_path / "rollouts.jsonl").read_text()

    steps = read_csv_rows(tmp_path / "step_metrics.csv")
    components = read_csv_rows(tmp_path / "reward_components.csv")
    assert steps[0]["step"] == 7.0
    assert steps[0]["parse_fail_frac"] == 0.5
    assert steps[0]["mutation_coverage_rate_mean"] == 1.0
    assert steps[0]["mutation_statistical_kill_frac"] == 1.0
    assert components[0]["mutation_mean"] == 0.5
    assert (tmp_path / "failure_samples.jsonl").exists()
    assert json.loads((tmp_path / "report.json").read_text())["steps"] == 1


def test_diagnostics_summary_reports_scalar_health() -> None:
    summary = diagnostics_summary(
        [
            {
                "reward": 0.4,
                "reward_std": 0.2,
                "zero_std_frac": 0.0,
                "parse_fail_frac": 0.1,
                "compile_frac": 0.8,
                "run_frac": 0.7,
            }
        ],
        [{"compile_mean": 0.8, "mutation_mean": 0.3}],
    )
    assert summary["steps"] == 1
    assert summary["component_means"] == {"compile": 0.8, "mutation": 0.3}


def test_plot_suite_writes_expected_artifacts_when_viz_is_installed(tmp_path) -> None:
    pytest.importorskip("matplotlib")
    collector = GrpoMetricsCollector(tmp_path / "metrics")
    collector.record_rollout(
        project="example",
        snippet_id="com.example.Foo",
        parse_ok=True,
        compiled=True,
        ran=True,
        compile_error_kind="none",
        run_error_kind="none",
        reward=0.5,
        reward_components=_components(),
        duration_s=1.0,
    )
    collector.flush_step(1, {"reward": 0.5, "reward_std": 0.0, "step_time": 1.0})
    paths = _plot(tmp_path / "metrics", tmp_path / "plots")
    assert {path.name for path in paths} == {
        "training_metrics.png",
        "component_progress.png",
        "reward_distribution.png",
        "mutation_progress.png",
    }
    assert all(path.exists() for path in paths)
