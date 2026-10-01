"""Clear scenario-specific regression hints from task.yaml files.

``expected_input_shapes`` prescribes the exact input regime in which a
particular regression bites and is cleared here so harnesses do not receive
that hand-hold.
"""
from __future__ import annotations

import sys
from pathlib import Path

import yaml

REPO = Path(__file__).resolve().parent.parent
TASK_DIRS = [REPO / "dataset" / "tasks", REPO / "dataset" / "tasks_real"]


def main() -> int:
    edited = 0
    for tdir in TASK_DIRS:
        if not tdir.exists():
            continue
        for yaml_path in sorted(tdir.glob("*/task.yaml")):
            data = yaml.safe_load(yaml_path.read_text())
            before = yaml_path.read_text()

            if data.get("expected_input_shapes"):
                data["expected_input_shapes"] = []

            after = yaml.safe_dump(data, sort_keys=False, default_flow_style=False, width=88)
            if after != before:
                yaml_path.write_text(after)
                edited += 1
                print(f"sanitized: {yaml_path.relative_to(REPO)}")
    print(f"\n{edited} task.yaml files updated.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
