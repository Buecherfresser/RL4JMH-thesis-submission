"""``jmh-dashboard`` — live view and static export of GRPO training telemetry.

    jmh-dashboard serve  --root outputs --port 8090
    jmh-dashboard export --run outputs/grpo-... --out reports/run8

``serve`` is read-only and safe to point at a run that is still training; ``export`` writes a
markdown report plus the CSVs next to it, for pasting into a thesis chapter or attaching to a
run report.
"""

from __future__ import annotations

import argparse
from pathlib import Path

from jmhgen.dashboard.load import discover_runs, load_run
from jmhgen.dashboard.markdown import render
from jmhgen.dashboard.server import Handler, RunCache, serve

_CSV_TABLES = ("steps", "steps_per_rank", "components", "rollouts", "projects", "failures", "gpu")


def _export(run_dir: Path, out_dir: Path) -> None:
    data = load_run(run_dir)
    out_dir.mkdir(parents=True, exist_ok=True)
    report = out_dir / f"{data.name}-telemetry.md"
    report.write_text(render(data), encoding="utf-8")
    print(f"wrote {report}")

    # Reuse the server's CSV serialisation so the download and the export are byte-identical.
    handler = Handler.__new__(Handler)
    handler.root = run_dir  # type: ignore[attr-defined]
    handler.cache = RunCache()  # type: ignore[attr-defined]
    for table in _CSV_TABLES:
        try:
            body, name = handler._csv_table(data, table)  # noqa: SLF001 - same package
        except ValueError:
            continue
        if not body:
            continue
        path = out_dir / name
        path.write_bytes(body)
        print(f"wrote {path} ({len(body)} bytes)")

    if not data.completions:
        print("note: no generations captured — set capture_completions_every in the GRPO config")
    if not data.gpu:
        print("note: no GPU samples — set gpu_sample_seconds in the GRPO config")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    sub = parser.add_subparsers(dest="command", required=True)

    p_serve = sub.add_parser("serve", help="run the live dashboard")
    p_serve.add_argument("--root", default="outputs",
                         help="directory holding run dirs (default: outputs)")
    p_serve.add_argument("--host", default="127.0.0.1",
                         help="bind address; keep on loopback and use an SSH tunnel")
    p_serve.add_argument("--port", type=int, default=8090)

    p_export = sub.add_parser("export", help="write a markdown report and CSVs")
    p_export.add_argument("--run", required=True, help="a run output dir or its metrics dir")
    p_export.add_argument("--out", default=None,
                          help="destination directory (default: <run>/report)")

    p_list = sub.add_parser("list", help="show discoverable runs")
    p_list.add_argument("--root", default="outputs")

    args = parser.parse_args()
    if args.command == "serve":
        serve(args.root, host=args.host, port=args.port)
    elif args.command == "export":
        run_dir = Path(args.run).expanduser()
        out = Path(args.out) if args.out else run_dir / "report"
        _export(run_dir, out)
    elif args.command == "list":
        runs = discover_runs(args.root)
        if not runs:
            print(f"no runs with metrics under {args.root}")
        for run in runs:
            print(run)


if __name__ == "__main__":
    main()
