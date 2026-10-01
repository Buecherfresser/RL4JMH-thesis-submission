"""A dependency-free HTTP dashboard over a GRPO run's metrics directory.

Stdlib ``http.server`` with an inline single-page UI: no framework, no CDN, no build step. That
is a requirement rather than minimalism for its own sake -- this has to start on the training box
over SSH while a run is live, and the box's venvs are pinned CUDA stacks that nobody should be
installing a web framework into.

Read-only by construction: every handler opens files for reading and the process never writes to
the run directory. Safe to point at a live run.

    jmh-dashboard serve --run outputs/grpo-... --port 8090
    ssh -N -L 8090:127.0.0.1:8090 rtx-jonas       # then open http://127.0.0.1:8090
"""

from __future__ import annotations

import csv
import io
import json
import threading
import time
from collections.abc import Iterable
from functools import partial
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from typing import Any
from urllib.parse import parse_qs, urlparse

from jmhgen.dashboard import assets, markdown
from jmhgen.dashboard.load import RunData, discover_runs, load_run
from jmhgen.dashboard.series import (
    build_charts,
    failure_table,
    project_table,
    realised_weights,
    summary,
)
from jmhgen.utils.logging import get_logger

logger = get_logger("jmhgen.dashboard")

_CACHE_SECONDS = 3.0


class RunCache:
    """Reload a run at most every few seconds.

    A live 4-rank run appends to `rollouts.jsonl` continuously and the file reaches hundreds of
    thousands of lines; re-parsing it on every browser poll would make the dashboard the most
    expensive process on the box.
    """

    def __init__(self, ttl: float = _CACHE_SECONDS) -> None:
        self.ttl = ttl
        self._lock = threading.Lock()
        self._entries: dict[str, tuple[float, RunData]] = {}

    def get(self, run_dir: Path) -> RunData:
        key = str(run_dir)
        now = time.monotonic()
        with self._lock:
            hit = self._entries.get(key)
            if hit and now - hit[0] < self.ttl:
                return hit[1]
        data = load_run(run_dir)
        with self._lock:
            self._entries[key] = (now, data)
        return data


def _csv_bytes(rows: Iterable[dict[str, Any]], fields: list[str] | None = None) -> bytes:
    rows = list(rows)
    if not rows:
        return b""
    if not fields:
        known = {key for row in rows for key in row}
        lead = [k for k in ("step", "rank", "index", "t", "gpu", "project", "snippet_id", "kind")
                if k in known]
        fields = lead + sorted(known - set(lead))
    buf = io.StringIO()
    writer = csv.DictWriter(buf, fieldnames=fields, extrasaction="ignore")
    writer.writeheader()
    for row in rows:
        writer.writerow(row)
    return buf.getvalue().encode("utf-8")


class Handler(BaseHTTPRequestHandler):
    server_version = "jmh-dashboard"

    def __init__(self, *args: Any, root: Path, cache: RunCache, **kwargs: Any) -> None:
        self.root = root
        self.cache = cache
        super().__init__(*args, **kwargs)

    # -- plumbing ---------------------------------------------------------------------------

    def log_message(self, fmt: str, *args: Any) -> None:  # noqa: A003 - stdlib signature
        return  # a polling UI would otherwise spam the console the run is printing to

    def _send(self, body: bytes, content_type: str, *, filename: str | None = None) -> None:
        self.send_response(200)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(len(body)))
        self.send_header("Cache-Control", "no-store")
        if filename:
            self.send_header("Content-Disposition", f'attachment; filename="{filename}"')
        self.end_headers()
        self.wfile.write(body)

    def _json(self, payload: Any) -> None:
        self._send(json.dumps(payload, allow_nan=False, default=str).encode("utf-8"),
                   "application/json; charset=utf-8")

    def _error(self, code: int, message: str) -> None:
        body = json.dumps({"error": message}).encode("utf-8")
        self.send_response(code)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def _runs(self) -> list[Path]:
        return discover_runs(self.root)

    def _resolve(self, query: dict[str, list[str]]) -> Path | None:
        """Resolve ?run= to a discovered run dir. Never trusts the parameter as a path."""
        runs = self._runs()
        if not runs:
            return None
        wanted = (query.get("run") or [""])[0]
        for run in runs:
            if run.name == wanted or str(run) == wanted:
                return run
        return runs[-1]

    # -- routes -----------------------------------------------------------------------------

    def do_GET(self) -> None:  # noqa: N802 - stdlib signature
        parsed = urlparse(self.path)
        query = parse_qs(parsed.query)
        route = parsed.path.rstrip("/") or "/"
        try:
            self._route(route, query)
        except BrokenPipeError:
            pass
        except Exception as exc:  # noqa: BLE001 - a viewer must not die on one bad request
            logger.warning("dashboard %s failed: %s", route, exc)
            self._error(500, f"{type(exc).__name__}: {exc}")

    def _route(self, route: str, query: dict[str, list[str]]) -> None:
        if route == "/":
            self._send(assets.INDEX_HTML.encode("utf-8"), "text/html; charset=utf-8")
            return
        if route == "/api/runs":
            self._json({"runs": [r.name for r in self._runs()], "root": str(self.root)})
            return

        run_dir = self._resolve(query)
        if run_dir is None:
            self._error(404, f"no run directories with metrics found under {self.root}")
            return
        data = self.cache.get(run_dir)

        if route == "/api/overview":
            self._json({
                "summary": summary(data),
                "realised_weights": realised_weights(data),
                "charts": build_charts(data),
                "tables": {
                    "projects": project_table(data),
                    "failures": failure_table(data),
                },
            })
            return
        if route == "/api/completions":
            self._json(self._completions_index(data))
            return
        if route == "/api/completion":
            self._json(self._completion_detail(data, query))
            return
        if route == "/export/markdown":
            body = markdown.render(data).encode("utf-8")
            self._send(body, "text/markdown; charset=utf-8",
                       filename=f"{data.name}-telemetry.md")
            return
        if route == "/export/csv":
            table = (query.get("table") or ["steps"])[0]
            try:
                body, name = self._csv_table(data, table)
            except ValueError as exc:
                self._error(400, str(exc))
                return
            self._send(body, "text/csv; charset=utf-8", filename=name)
            return
        self._error(404, f"unknown route {route}")

    # -- generations ------------------------------------------------------------------------

    def _completions_index(self, data: RunData) -> dict[str, Any]:
        """What was captured: which steps, and which subjects appear in more than one step.

        The `across_steps` list is the interesting one -- those are the subjects whose benchmark
        can be diffed over training.
        """
        steps: dict[Any, int] = {}
        by_snippet: dict[str, set[Any]] = {}
        for row in data.completions:
            steps[row.get("step")] = steps.get(row.get("step"), 0) + 1
            by_snippet.setdefault(str(row.get("snippet_id")), set()).add(row.get("step"))
        across = sorted(
            ({"snippet_id": k, "steps": sorted(v, key=lambda s: (s is None, s))}
             for k, v in by_snippet.items() if len(v) > 1),
            key=lambda r: -len(r["steps"]),
        )
        return {
            "captured": bool(data.completions),
            "rows": len(data.completions),
            "steps": [{"step": s, "rollouts": n} for s, n in
                      sorted(steps.items(), key=lambda kv: (kv[0] is None, kv[0]))],
            "snippets": sorted(by_snippet),
            "across_steps": across[:200],
        }

    def _completion_detail(self, data: RunData, query: dict[str, list[str]]) -> dict[str, Any]:
        """Rollouts filtered by step and/or snippet, ordered so siblings sit next to each other."""
        step = (query.get("step") or [""])[0]
        snippet = (query.get("snippet") or [""])[0]
        rows = []
        for row in data.completions:
            if step and str(row.get("step")) != step:
                continue
            if snippet and str(row.get("snippet_id")) != snippet:
                continue
            rows.append(row)
        rows.sort(key=lambda r: (r.get("step") or 0, str(r.get("rank")), r.get("index") or 0))
        return {"rows": rows[:64], "total": len(rows)}

    # -- csv --------------------------------------------------------------------------------

    def _csv_table(self, data: RunData, table: str) -> tuple[bytes, str]:
        name = f"{data.name}-{table}.csv"
        if table == "steps":
            return _csv_bytes(data.merged_steps()), name
        if table == "steps_per_rank":
            return _csv_bytes(data.steps), name
        if table == "components":
            return _csv_bytes(data.components), name
        if table == "gpu":
            return _csv_bytes(data.gpu), name
        if table == "projects":
            return _csv_bytes(project_table(data)), name
        if table == "failures":
            failures = failure_table(data, limit=1000)
            rows = ([{"kind": "compile", **r} for r in failures["compile"]]
                    + [{"kind": "run", **r} for r in failures["run"]])
            return _csv_bytes(rows), name
        if table == "rollouts":
            flat = []
            for row in data.rollouts:
                components = row.get("components") or {}
                mutation = row.get("mutation") if isinstance(row.get("mutation"), dict) else {}
                flat.append({
                    "project": row.get("project"),
                    "snippet_id": row.get("snippet_id"),
                    "parse_ok": row.get("parse_ok"),
                    "compiled": row.get("compiled"),
                    "ran": row.get("ran"),
                    "reward": row.get("reward"),
                    "duration_s": row.get("duration_s"),
                    "compile_error_kind": row.get("compile_error_kind"),
                    "run_error_kind": row.get("run_error_kind"),
                    "mutation_score": mutation.get("score"),
                    **{f"component_{k}": v for k, v in components.items()},
                    "compile_diagnostics": " | ".join(row.get("compile_diagnostics") or []),
                    "run_diagnostics": " | ".join(row.get("run_diagnostics") or []),
                })
            return _csv_bytes(flat), name
        raise ValueError(f"unknown csv table {table!r}")


def serve(root: str | Path, host: str = "127.0.0.1", port: int = 8090) -> None:
    root_path = Path(root).expanduser().resolve()
    handler = partial(Handler, root=root_path, cache=RunCache())
    httpd = ThreadingHTTPServer((host, port), handler)
    runs = discover_runs(root_path)
    logger.info("dashboard on http://%s:%d for %s (%d run(s): %s)", host, port, root_path,
                len(runs), ", ".join(r.name for r in runs) or "none yet")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        logger.info("dashboard stopped")
    finally:
        httpd.server_close()
