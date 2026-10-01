"""Tests for the telemetry dashboard: loading, aggregation, export, and the HTTP surface."""

from __future__ import annotations

import json
import threading
import urllib.request
from functools import partial
from http.server import ThreadingHTTPServer
from pathlib import Path

import pytest

from jmhgen.dashboard.load import discover_runs, load_run, read_jsonl
from jmhgen.dashboard.markdown import render
from jmhgen.dashboard.series import bin_series, realised_weights, rolling, summary
from jmhgen.dashboard.server import Handler, RunCache

_STEP_HEADER = (
    "step,epoch,reward,reward_std,loss,grad_norm,learning_rate,step_time,clipped_ratio,"
    "zero_std_frac,num_generations,rollouts,parse_fail_frac,compile_frac,run_frac,"
    "mutation_eligible_frac,mutation_score_mean\n"
)


def _make_run(tmp_path: Path, *, ranks: int = 2, steps: int = 4, with_diag: bool = True) -> Path:
    run = tmp_path / "run-x"
    metrics = run / "metrics"
    metrics.mkdir(parents=True)

    rows = [_STEP_HEADER]
    for step in range(1, steps + 1):
        for rank in range(ranks):
            # compile_frac differs per rank so merging has something to average.
            compile_frac = 0.2 + 0.1 * rank
            rows.append(
                f"{step},{0.1 * step},{0.5},{0.2},{0.01},{0.03},{3e-6},{40 + step},"
                f"{0.1},{0.0},1,4,{0.0},{compile_frac},{0.1},{0.1},{0.7}\n"
            )
    (metrics / "step_metrics.csv").write_text("".join(rows), encoding="utf-8")

    (metrics / "reward_components.csv").write_text(
        "step,compile_mean,compile_std,weighted_compile_mean,weighted_compile_std,"
        "mutation_mean,mutation_std,weighted_mutation_mean,weighted_mutation_std\n"
        "1,0.5,0.1,0.05,0.01,0.4,0.1,0.28,0.02\n"
        "2,0.6,0.1,0.06,0.01,0.5,0.1,0.35,0.02\n",
        encoding="utf-8",
    )

    rollouts = []
    for i in range(10):
        row = {
            "project": "commons-codec" if i % 2 else "gson",
            "snippet_id": "org.ex.Foo",
            "parse_ok": True,
            "compiled": i % 2 == 0,
            "ran": i % 4 == 0,
            "reward": 0.5 if i % 4 == 0 else 0.0,
            "duration_s": 1.5,
            "components": {"compile": 1.0},
            "mutation": {"score": 0.7} if i % 4 == 0 else {"error": "no_mutation_score"},
            "compile_diagnostics": [] if i % 2 == 0 else ["cannot find symbol"],
        }
        if with_diag:
            row["run_diagnostics"] = (
                ["ERROR: org.openjdk.jmh.runner.RunnerException: Unable to acquire the JMH lock "
                 "(/tmp/jmh.lock)"] if i == 3 else []
            )
        rollouts.append(row)
    (metrics / "rollouts.jsonl").write_text(
        "\n".join(json.dumps(r) for r in rollouts) + "\n", encoding="utf-8"
    )
    (metrics / "gpu.csv").write_text(
        "t,gpu,util,mem_used_frac,mem_used_mb,power_w,temp_c\n"
        "1786435012.4,0.0,1.0,0.13,12814.0,102.0,70.0\n"
        "1786435012.4,1.0,0.5,0.12,12248.0,101.0,69.0\n",
        encoding="utf-8",
    )
    (metrics / "rollout_summary.jsonl").write_text(
        json.dumps({"step": 1, "trainer_metrics": {"entropy": 0.7,
                                                   "completions/mean_length": 1600.0}}) + "\n",
        encoding="utf-8",
    )
    return run


class TestLoad:
    def test_merges_per_rank_rows_into_one_row_per_step(self, tmp_path: Path) -> None:
        data = load_run(_make_run(tmp_path, ranks=2, steps=4))
        assert len(data.steps) == 8
        merged = data.merged_steps()
        assert len(merged) == 4
        # compile_frac is averaged across ranks (0.2 and 0.3), rollouts is summed (4 + 4).
        assert merged[0]["compile_frac"] == pytest.approx(0.25)
        assert merged[0]["rollouts"] == 8
        assert merged[0]["ranks"] == 2

    def test_truncated_final_line_is_skipped_not_raised(self, tmp_path: Path) -> None:
        """A live run is mid-write when the dashboard polls; that must never be an error."""
        path = tmp_path / "partial.jsonl"
        path.write_text('{"a": 1}\n{"b": 2}\n{"c": 3', encoding="utf-8")
        assert list(read_jsonl(path)) == [{"a": 1}, {"b": 2}]

    def test_missing_files_yield_empty_not_error(self, tmp_path: Path) -> None:
        empty = tmp_path / "nothing"
        empty.mkdir()
        data = load_run(empty)
        assert data.steps == [] and data.rollouts == []
        assert summary(data)["steps"] == 0

    def test_gpu_csv_is_loaded_despite_having_no_step_column(self, tmp_path: Path) -> None:
        """gpu.csv is keyed by wall-clock `t`; a step-keyed guard silently dropped every row."""
        data = load_run(_make_run(tmp_path))
        assert len(data.gpu) == 2
        assert data.gpu[0]["util"] == pytest.approx(1.0)
        assert data.gpu[1]["gpu"] == pytest.approx(1.0)

    def test_blank_csv_rows_are_still_skipped(self, tmp_path: Path) -> None:
        path = tmp_path / "partial.csv"
        path.write_text("step,reward\n1,0.5\n,\n", encoding="utf-8")
        from jmhgen.dashboard.load import read_csv
        assert len(read_csv(path)) == 1

    def test_discover_finds_run_dirs(self, tmp_path: Path) -> None:
        _make_run(tmp_path)
        assert [p.name for p in discover_runs(tmp_path)] == ["run-x"]


class TestSeries:
    def test_bin_series_downsamples_and_drops_nulls(self) -> None:
        xs = list(range(1000))
        ys: list[float | None] = [float(i) for i in xs]
        ys[5] = None
        out = bin_series(xs, ys, bins=10)
        assert len(out) <= 10
        assert all(len(p) == 2 for p in out)

    def test_bin_series_preserves_short_series(self) -> None:
        assert bin_series([1, 2], [3.0, 4.0], bins=100) == [[1, 3.0], [2, 4.0]]

    def test_rolling_ignores_nulls_but_keeps_length(self) -> None:
        out = rolling([1.0, None, 3.0], window=2)
        assert len(out) == 3
        assert out[0] == pytest.approx(1.0)
        assert out[2] == pytest.approx(2.0)

    def test_summary_counts_the_funnel(self, tmp_path: Path) -> None:
        s = summary(load_run(_make_run(tmp_path)))
        assert s["rollouts"] == 10
        assert s["compile_frac"] == pytest.approx(0.5)
        assert s["run_frac"] == pytest.approx(0.3)
        assert s["run_over_compile"] == pytest.approx(0.6)
        assert s["mutation_score_mean"] == pytest.approx(0.7)

    def test_lock_losses_are_counted_when_recorded(self, tmp_path: Path) -> None:
        s = summary(load_run(_make_run(tmp_path, with_diag=True)))
        assert s["jmh_lock_recorded"] is True
        assert s["jmh_lock_losses"] == 1

    def test_lock_losses_are_unknown_for_older_runs(self, tmp_path: Path) -> None:
        """Run 7 has no run_diagnostics; reporting 0 would be a reassuring lie."""
        s = summary(load_run(_make_run(tmp_path, with_diag=False)))
        assert s["jmh_lock_recorded"] is False
        assert s["jmh_lock_losses"] is None

    def test_realised_weights_sum_to_one(self, tmp_path: Path) -> None:
        shares = [
            r["realised_share"] for r in realised_weights(load_run(_make_run(tmp_path)))
            if r["realised_share"]
        ]
        assert sum(shares) == pytest.approx(1.0)


class TestMarkdown:
    def test_report_contains_health_and_trend(self, tmp_path: Path) -> None:
        md = render(load_run(_make_run(tmp_path)))
        assert "# GRPO telemetry" in md
        assert "ran / compiled" in md
        assert "Reward composition" in md
        assert "cannot find symbol" in md

    def test_report_flags_an_unmeasurable_lock(self, tmp_path: Path) -> None:
        md = render(load_run(_make_run(tmp_path, with_diag=False)))
        assert "not recorded" in md

    def test_report_on_an_empty_run_does_not_raise(self, tmp_path: Path) -> None:
        empty = tmp_path / "blank"
        empty.mkdir()
        assert "GRPO telemetry" in render(load_run(empty))


class TestServer:
    @pytest.fixture()
    def base_url(self, tmp_path: Path):
        _make_run(tmp_path)
        handler = partial(Handler, root=tmp_path, cache=RunCache(ttl=0))
        httpd = ThreadingHTTPServer(("127.0.0.1", 0), handler)
        thread = threading.Thread(target=httpd.serve_forever, daemon=True)
        thread.start()
        yield f"http://127.0.0.1:{httpd.server_address[1]}"
        httpd.shutdown()
        httpd.server_close()

    @staticmethod
    def _get(url: str) -> tuple[int, bytes]:
        try:
            with urllib.request.urlopen(url, timeout=10) as response:  # noqa: S310 - localhost
                return response.status, response.read()
        except urllib.error.HTTPError as exc:
            return exc.code, exc.read()

    def test_index_serves_the_page(self, base_url: str) -> None:
        status, body = self._get(base_url + "/")
        assert status == 200
        assert b"GRPO telemetry" in body

    def test_overview_has_every_section(self, base_url: str) -> None:
        status, body = self._get(base_url + "/api/overview")
        assert status == 200
        payload = json.loads(body)
        assert set(payload) == {"summary", "realised_weights", "charts", "tables"}
        assert payload["summary"]["rollouts"] == 10

    def test_csv_export_leads_with_step(self, base_url: str) -> None:
        status, body = self._get(base_url + "/export/csv?table=steps")
        assert status == 200
        assert body.split(b"\n")[0].startswith(b"step,")

    def test_gpu_csv_export_is_not_empty(self, base_url: str) -> None:
        status, body = self._get(base_url + "/export/csv?table=gpu")
        assert status == 200
        assert body.split(b"\n")[0].startswith(b"t,")
        assert len(body.strip().split(b"\n")) == 3

    def test_markdown_export_is_a_download(self, base_url: str) -> None:
        status, body = self._get(base_url + "/export/markdown")
        assert status == 200
        assert body.startswith(b"# GRPO telemetry")

    def test_unknown_csv_table_is_a_bad_request(self, base_url: str) -> None:
        status, _ = self._get(base_url + "/export/csv?table=evil")
        assert status == 400

    def test_unknown_route_is_404(self, base_url: str) -> None:
        status, _ = self._get(base_url + "/nope")
        assert status == 404

    def test_run_parameter_is_not_a_path_traversal(self, base_url: str) -> None:
        """?run= is matched against discovered runs, never joined onto the filesystem."""
        status, body = self._get(base_url + "/api/overview?run=../../etc")
        assert status == 200
        assert json.loads(body)["summary"]["run"] == "run-x"


class TestCompletionCapture:
    def test_capture_writes_sources_and_keeps_rollouts_scalar(self, tmp_path: Path) -> None:
        from jmhgen.training.grpo_metrics import GrpoMetricsCollector

        metrics = tmp_path / "metrics"
        collector = GrpoMetricsCollector(
            metrics, capture_completions_every=2, capture_completions_max_chars=40
        )
        for step in (1, 2):
            for _ in range(3):
                collector.record_rollout(
                    project="p", snippet_id="org.ex.Foo", parse_ok=True, compiled=True, ran=True,
                    compile_error_kind="none", run_error_kind="none", reward=0.5,
                    reward_components=None, duration_s=1.0, source="X" * 500,
                )
            collector.finish_reward_batch(3)
            collector.flush_step(step, {})

        text = (metrics / "completions.jsonl").read_text()
        captured = [json.loads(line) for line in text.splitlines()]
        assert {row["step"] for row in captured} == {2}, "only every 2nd step is captured"
        assert len(captured) == 3
        assert len(captured[0]["source"]) == 40, "source is truncated to the configured cap"

        rollout_text = (metrics / "rollouts.jsonl").read_text()
        rollouts = [json.loads(line) for line in rollout_text.splitlines()]
        assert len(rollouts) == 6
        assert not any("source" in r or "_source" in r for r in rollouts), (
            "the scalar-only rollouts file must never carry generated source"
        )

    def test_capture_disabled_writes_nothing(self, tmp_path: Path) -> None:
        from jmhgen.training.grpo_metrics import GrpoMetricsCollector

        metrics = tmp_path / "metrics"
        collector = GrpoMetricsCollector(metrics)
        collector.record_rollout(
            project="p", snippet_id="s", parse_ok=True, compiled=True, ran=True,
            compile_error_kind="none", run_error_kind="none", reward=0.5,
            reward_components=None, duration_s=1.0, source="public class B {}",
        )
        collector.flush_step(1, {})
        assert not (metrics / "completions.jsonl").exists()
