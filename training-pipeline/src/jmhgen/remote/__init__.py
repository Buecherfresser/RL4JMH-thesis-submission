"""Remote execution of training stages over SSH (rsync code -> uv sync -> run -> pull)."""

from jmhgen.remote.ssh import (
    build_bootstrap_script,
    build_pull_cmd,
    build_push_cmd,
    build_setup_script,
    build_ssh_exec,
    build_train_script,
    resolve_output_dir,
    run_remote,
)

__all__ = [
    "build_bootstrap_script",
    "build_pull_cmd",
    "build_push_cmd",
    "build_setup_script",
    "build_ssh_exec",
    "build_train_script",
    "resolve_output_dir",
    "run_remote",
]
