"""Structured, local diagnostics for online GRPO reward execution.

The TRL trainer records aggregate policy metrics, but it cannot see why a JMH
reward was high or low.  This module records only bounded scalar outcomes:
never prompts, completions, Java source, Maven output, or mutation verdict lists.
"""

from __future__ import annotations

import csv
import json
import math
import os
import threading
import time
from collections import Counter
from collections.abc import Sequence
from pathlib import Path
from statistics import fmean, pstdev
from typing import Any

_COMPONENTS = ("compile", "runtime", "anti_pattern", "rsd", "mutation")
_STEP_FIELDS = (
    "step",
    "epoch",
    "reward",
    "reward_std",
    "loss",
    "grad_norm",
    "learning_rate",
    "step_time",
    "clipped_ratio",
    "zero_std_frac",
    "num_generations",
    "rollouts",
    "parse_fail_frac",
    "compile_frac",
    "run_frac",
    "mutation_eligible_frac",
    "mutation_score_mean",
    "mutation_attempted_mean",
    "mutation_killed_mean",
    "mutation_coverage_rate_mean",
    "mutation_sampled_kill_rate_mean",
    "mutation_not_covered_frac",
    "mutation_covered_not_killed_frac",
    "mutation_timeout_kill_frac",
    "mutation_statistical_kill_frac",
)
_COMPONENT_FIELDS = tuple(
    f"{prefix}{name}_{stat}"
    for prefix in ("", "weighted_")
    for name in _COMPONENTS
    for stat in ("mean", "std")
)


def _number(value: Any) -> float | None:
    """Return a finite float or ``None`` for absent/non-scalar data."""
    if isinstance(value, bool):
        return float(value)
    if not isinstance(value, (int, float)):
        return None
    result = float(value)
    return result if math.isfinite(result) else None


def _mean(values: list[float]) -> float | None:
    return fmean(values) if values else None


def _std(values: list[float]) -> float | None:
    return pstdev(values) if len(values) > 1 else (0.0 if values else None)


class GrpoMetricsCollector:
    """Append sanitized rollout metrics and aggregate them at trainer log boundaries."""

    def __init__(
        self,
        metrics_dir: str | Path,
        *,
        failure_sample_limit: int = 20,
        capture_completions_every: int = 0,
        capture_completions_max_chars: int = 20000,
    ) -> None:
        self.metrics_dir = Path(metrics_dir)
        self.metrics_dir.mkdir(parents=True, exist_ok=True)
        self.failure_sample_limit = failure_sample_limit
        # Source capture is opt-in; everything else this class writes stays scalar-only.
        self.capture_completions_every = max(0, int(capture_completions_every))
        self.capture_completions_max_chars = max(0, int(capture_completions_max_chars))
        self._lock = threading.Lock()
        self._pending_batches: list[list[dict[str, Any]]] = []
        self._all_step_rows: list[dict[str, Any]] = []
        self._failure_samples_written = 0
        # Held-out scoring reuses the training reward path, so rollouts have to be segregated by
        # mode or the two curves end up in one file. See ``set_eval_mode``.
        self._eval_mode = False
        self._stashed_batches: list[list[dict[str, Any]]] = []

        self.rollouts_path = self.metrics_dir / "rollouts.jsonl"
        self.rollout_summary_path = self.metrics_dir / "rollout_summary.jsonl"
        self.failure_samples_path = self.metrics_dir / "failure_samples.jsonl"
        self.completions_path = self.metrics_dir / "completions.jsonl"
        self.step_metrics_path = self.metrics_dir / "step_metrics.csv"
        self.component_metrics_path = self.metrics_dir / "reward_components.csv"
        self.holdout_metrics_path = self.metrics_dir / "holdout_eval.csv"
        self.report_path = self.metrics_dir / "report.json"
        self._write_csv_header(self.step_metrics_path, _STEP_FIELDS)
        self._write_csv_header(self.component_metrics_path, ("step", *_COMPONENT_FIELDS))
        self._write_csv_header(self.holdout_metrics_path, _STEP_FIELDS)

    @staticmethod
    def _write_csv_header(path: Path, fields: tuple[str, ...]) -> None:
        if path.exists() and path.stat().st_size > 0:
            return
        with path.open("w", newline="", encoding="utf-8") as handle:
            csv.DictWriter(handle, fieldnames=fields).writeheader()

    @staticmethod
    def _append_jsonl(path: Path, row: dict[str, Any]) -> None:
        with path.open("a", encoding="utf-8") as handle:
            handle.write(json.dumps(row, sort_keys=True, allow_nan=False))
            handle.write("\n")

    @staticmethod
    def _append_csv(path: Path, fields: tuple[str, ...], row: dict[str, Any]) -> None:
        with path.open("a", newline="", encoding="utf-8") as handle:
            csv.DictWriter(handle, fieldnames=fields).writerow(row)

    def set_eval_mode(self, enabled: bool) -> None:
        """Route subsequent rollouts to the held-out file instead of the training one.

        Any batch still pending when evaluation starts belongs to training, so it is stashed and
        restored afterwards rather than flushed under an eval row. With ``logging_steps: 1`` there
        is normally nothing pending; the stash matters for coarser logging, where a training batch
        would otherwise be attributed to the held-out curve.
        """
        with self._lock:
            if enabled == self._eval_mode:
                return
            if enabled:
                self._stashed_batches = self._pending_batches
                self._pending_batches = []
            else:
                # Drop anything the eval loop left unflushed; it was never associated with a step.
                self._pending_batches = self._stashed_batches
                self._stashed_batches = []
            self._eval_mode = enabled

    def record_rollout(
        self,
        *,
        project: str | None,
        snippet_id: str | None,
        parse_ok: bool,
        compiled: bool,
        ran: bool,
        compile_error_kind: str | None,
        run_error_kind: str | None,
        reward: float,
        reward_components: dict[str, Any] | None,
        duration_s: float,
        repairs: Sequence[str] = (),
        compile_diagnostics: Sequence[str] = (),
        run_diagnostics: Sequence[str] = (),
        source: str | None = None,
    ) -> None:
        """Record one scalar-only rollout outcome and queue it for step aggregation."""
        components = reward_components or {}
        component_values: dict[str, float] = {}
        weighted_values: dict[str, float] = {}
        mutation: dict[str, Any] = {}
        anti_pattern_backend: str | None = None
        for name in _COMPONENTS:
            component = components.get(name)
            if not isinstance(component, dict):
                continue
            value = _number(component.get("value"))
            weighted = _number(component.get("weighted"))
            if value is not None:
                component_values[name] = value
            if weighted is not None:
                weighted_values[name] = weighted
            detail = component.get("detail")
            if name == "mutation" and isinstance(detail, dict):
                mutation = {
                    key: detail.get(key)
                    for key in (
                        "score",
                        "killed",
                        "total",
                        "attempted",
                        "sampled_kill_rate",
                        "coverage_rate",
                        "conditional_kill_rate",
                        "not_covered",
                        "covered_not_killed",
                        "killed_timeout",
                        "killed_statistical",
                        "mode",
                        "error",
                    )
                    if detail.get(key) is not None
                }
            if name == "anti_pattern" and isinstance(detail, dict):
                source = detail.get("source")
                anti_pattern_backend = str(source) if source is not None else None

        event = {
            "timestamp_s": round(time.time(), 3),
            "project": project or "",
            "snippet_id": snippet_id or "",
            "parse_ok": parse_ok,
            "compiled": compiled,
            "ran": ran,
            "compile_error_kind": compile_error_kind,
            "run_error_kind": run_error_kind,
            "reward": reward,
            "duration_s": round(duration_s, 6),
            "components": component_values,
            "weighted_components": weighted_values,
            "mutation": mutation,
            "anti_pattern_backend": anti_pattern_backend,
            "repairs": list(repairs),
            "compile_diagnostics": list(compile_diagnostics),
            "run_diagnostics": list(run_diagnostics),
        }
        # The source rides along on the in-memory event so flush_step can attach the step number
        # to it, but it is stripped before anything reaches rollouts.jsonl.
        captured = None
        if self.capture_completions_every and source:
            captured = source[: self.capture_completions_max_chars]
        with self._lock:
            self._append_jsonl(self.rollouts_path, event)
            if (
                self._failure_samples_written < self.failure_sample_limit
                and (not parse_ok or not compiled or not ran)
            ):
                self._append_jsonl(self.failure_samples_path, event)
                self._failure_samples_written += 1
            self._pending_batches.append([{**event, "_source": captured}])

    def finish_reward_batch(self, count: int) -> None:
        """Merge the one-rollout provisional batches emitted during one reward-function call."""
        if count <= 1:
            return
        with self._lock:
            if len(self._pending_batches) < count:
                return
            events = [batch[0] for batch in self._pending_batches[-count:]]
            del self._pending_batches[-count:]
            self._pending_batches.append(events)

    @staticmethod
    def _batch_summary(events: list[dict[str, Any]]) -> dict[str, Any]:
        rewards = [float(event["reward"]) for event in events]
        component_means: dict[str, float | None] = {}
        component_stds: dict[str, float | None] = {}
        weighted_component_means: dict[str, float | None] = {}
        weighted_component_stds: dict[str, float | None] = {}
        mutation_stats: dict[str, float | None] = {}
        for name in _COMPONENTS:
            values = [
                value
                for event in events
                if (value := _number(event["components"].get(name))) is not None
            ]
            weighted_values = [
                value
                for event in events
                if (value := _number(event["weighted_components"].get(name))) is not None
            ]
            component_means[name] = _mean(values)
            component_stds[name] = _std(values)
            weighted_component_means[name] = _mean(weighted_values)
            weighted_component_stds[name] = _std(weighted_values)
        for name in (
            "killed",
            "total",
            "attempted",
            "score",
            "sampled_kill_rate",
            "coverage_rate",
            "conditional_kill_rate",
        ):
            values = [
                value
                for event in events
                if isinstance(event["mutation"], dict)
                and (value := _number(event["mutation"].get(name))) is not None
            ]
            mutation_stats[f"mutation_{name}_mean"] = _mean(values)
        mutation_status_totals = {
            name: sum(
                int(value)
                for event in events
                if isinstance(event["mutation"], dict)
                and (value := _number(event["mutation"].get(name))) is not None
            )
            for name in (
                "not_covered",
                "covered_not_killed",
                "killed_timeout",
                "killed_statistical",
            )
        }
        attempted_total = sum(
            int(value)
            for event in events
            if isinstance(event["mutation"], dict)
            and (value := _number(event["mutation"].get("attempted"))) is not None
        )
        killed_total = mutation_status_totals["killed_timeout"] + mutation_status_totals[
            "killed_statistical"
        ]

        error_kinds = Counter(
            kind
            for event in events
            for kind in (event["compile_error_kind"], event["run_error_kind"])
            if kind and kind != "none"
        )
        eligible = sum(
            bool(isinstance(event["mutation"], dict) and event["mutation"].get("total"))
            for event in events
        )
        return {
            "n_completions": len(events),
            "reward_mean": _mean(rewards),
            "reward_std": _std(rewards),
            "parse_failures": sum(not event["parse_ok"] for event in events),
            "compiled": sum(event["compiled"] for event in events),
            "ran": sum(event["ran"] for event in events),
            "mutation_eligible": eligible,
            "component_means": component_means,
            "component_stds": component_stds,
            "weighted_component_means": weighted_component_means,
            "weighted_component_stds": weighted_component_stds,
            "error_kinds": dict(sorted(error_kinds.items())),
            "mutation_status_totals": mutation_status_totals,
            "mutation_attempted_total": attempted_total,
            "mutation_killed_total": killed_total,
            **mutation_stats,
        }

    def _write_completions(self, step: int, events: list[dict[str, Any]]) -> None:
        """Persist the generated Java for one step, when capture is on and this step qualifies.

        Two questions motivate the schema, and both need more than the source text:
        *how does a subject's benchmark change over training* (group by ``snippet_id`` across
        steps) and *how do the G rollouts of one step differ* (group by ``snippet_id`` within a
        step, ordered by ``index``). Each row therefore carries the outcome it earned, so a
        side-by-side can show why one sibling scored and another did not.

        All four ranks append to this file, so ``rank`` is what disambiguates ``index``.
        """
        if not self.capture_completions_every:
            return
        if step % self.capture_completions_every:
            return
        rank = os.environ.get("RANK") or os.environ.get("LOCAL_RANK") or "0"
        rows = []
        for index, event in enumerate(events):
            source = event.get("_source")
            if not source:
                continue
            rows.append({
                "step": step,
                "rank": rank,
                "index": index,
                "project": event.get("project"),
                "snippet_id": event.get("snippet_id"),
                "reward": event.get("reward"),
                "compiled": event.get("compiled"),
                "ran": event.get("ran"),
                "parse_ok": event.get("parse_ok"),
                "components": event.get("components"),
                "mutation": event.get("mutation"),
                "compile_diagnostics": event.get("compile_diagnostics"),
                "run_diagnostics": event.get("run_diagnostics"),
                "duration_s": event.get("duration_s"),
                "source": source,
            })
        if not rows:
            return
        with self._lock:
            for row in rows:
                self._append_jsonl(self.completions_path, row)

    def flush_step(self, step: int, trainer_logs: dict[str, Any]) -> None:
        """Associate all reward batches pending at an optimizer log boundary with ``step``."""
        with self._lock:
            if not self._pending_batches:
                return
            batches = self._pending_batches
            self._pending_batches = []
            is_eval = self._eval_mode

        def log_value(key: str) -> Any:
            """Read a trainer metric, preferring TRL's ``eval_``-prefixed key during evaluation."""
            if is_eval and (prefixed := trainer_logs.get(f"eval_{key}")) is not None:
                return prefixed
            return trainer_logs.get(key)

        events = [event for batch in batches for event in batch]
        if not is_eval:
            self._write_completions(step, events)
        summary = self._batch_summary(events)
        clipped_ratio = _number(
            log_value("completions/clipped_ratio")
            if log_value("completions/clipped_ratio") is not None
            else log_value("clipped_ratio")
        )
        step_row = {
            "step": step,
            "epoch": _number(log_value("epoch")),
            "reward": _number(
                value if (value := log_value("reward")) is not None else summary["reward_mean"]
            ),
            "reward_std": _number(
                value
                if (value := log_value("reward_std")) is not None
                else summary["reward_std"]
            ),
            "loss": _number(log_value("loss")),
            "grad_norm": _number(log_value("grad_norm")),
            "learning_rate": _number(log_value("learning_rate")),
            "step_time": _number(log_value("step_time")),
            "clipped_ratio": clipped_ratio,
            "zero_std_frac": float(summary["reward_std"] == 0.0),
            "num_generations": len(batches),
            "rollouts": len(events),
            "parse_fail_frac": summary["parse_failures"] / max(1, len(events)),
            "compile_frac": summary["compiled"] / max(1, len(events)),
            "run_frac": summary["ran"] / max(1, len(events)),
            "mutation_eligible_frac": summary["mutation_eligible"] / max(1, len(events)),
            "mutation_score_mean": summary["mutation_score_mean"],
            "mutation_attempted_mean": summary["mutation_attempted_mean"],
            "mutation_killed_mean": summary["mutation_killed_mean"],
            "mutation_coverage_rate_mean": summary["mutation_coverage_rate_mean"],
            "mutation_sampled_kill_rate_mean": summary["mutation_sampled_kill_rate_mean"],
            "mutation_not_covered_frac": (
                summary["mutation_status_totals"]["not_covered"]
                / summary["mutation_attempted_total"]
                if summary["mutation_attempted_total"]
                else None
            ),
            "mutation_covered_not_killed_frac": (
                summary["mutation_status_totals"]["covered_not_killed"]
                / summary["mutation_attempted_total"]
                if summary["mutation_attempted_total"]
                else None
            ),
            "mutation_timeout_kill_frac": (
                summary["mutation_status_totals"]["killed_timeout"]
                / summary["mutation_killed_total"]
                if summary["mutation_killed_total"]
                else None
            ),
            "mutation_statistical_kill_frac": (
                summary["mutation_status_totals"]["killed_statistical"]
                / summary["mutation_killed_total"]
                if summary["mutation_killed_total"]
                else None
            ),
        }
        # Held-out rows go to their own file and are deliberately kept out of the training
        # aggregates: step_metrics.csv, reward_components.csv, rollout_summary.jsonl and the
        # run-level report all describe the *training* distribution.
        if is_eval:
            with self._lock:
                self._append_csv(self.holdout_metrics_path, _STEP_FIELDS, step_row)
            return

        component_row: dict[str, Any] = {"step": step}
        for name in _COMPONENTS:
            component_row[f"{name}_mean"] = summary["component_means"][name]
            component_row[f"{name}_std"] = summary["component_stds"][name]
            component_row[f"weighted_{name}_mean"] = summary["weighted_component_means"][name]
            component_row[f"weighted_{name}_std"] = summary["weighted_component_stds"][name]
        with self._lock:
            self._append_csv(self.step_metrics_path, _STEP_FIELDS, step_row)
            self._append_csv(
                self.component_metrics_path, ("step", *_COMPONENT_FIELDS), component_row
            )
            scalar_trainer_metrics = {
                key: value
                for key, raw_value in trainer_logs.items()
                if (value := _number(raw_value)) is not None
            }
            self._append_jsonl(
                self.rollout_summary_path,
                {"step": step, "trainer_metrics": scalar_trainer_metrics, **summary},
            )
            self._all_step_rows.append(step_row)

    def finish(self, step: int) -> None:
        """Flush remaining data and write a compact, run-level summary."""
        self.flush_step(step, {})
        with self._lock:
            rows = list(self._all_step_rows)
        report = {
            "steps": len(rows),
            "mean_reward": _mean(
                [value for row in rows if (value := _number(row.get("reward"))) is not None]
            ),
            "mean_reward_std": _mean(
                [value for row in rows if (value := _number(row.get("reward_std"))) is not None]
            ),
            "zero_std_step_frac": _mean(
                [
                    float(row["zero_std_frac"])
                    for row in rows
                    if row.get("zero_std_frac") is not None
                ]
            ),
            "metrics_files": {
                "step_metrics": self.step_metrics_path.name,
                "reward_components": self.component_metrics_path.name,
                "rollouts": self.rollouts_path.name,
                "rollout_summary": self.rollout_summary_path.name,
            },
        }
        self.report_path.write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")


def build_grpo_metrics_callback(collector: GrpoMetricsCollector) -> Any:
    """Build a lazy Transformers callback so CPU-only reward tests need no train extra."""
    from transformers import TrainerCallback

    class _GrpoMetricsCallback(TrainerCallback):
        def on_log(
            self,
            args: Any,
            state: Any,
            control: Any,
            logs: dict[str, Any] | None = None,
            **kwargs: Any,
        ) -> Any:
            del args, kwargs
            collector.flush_step(int(state.global_step), dict(logs or {}))
            return control

        def on_train_end(self, args: Any, state: Any, control: Any, **kwargs: Any) -> Any:
            del args, kwargs
            collector.finish(int(state.global_step))
            return control

    return _GrpoMetricsCallback()
