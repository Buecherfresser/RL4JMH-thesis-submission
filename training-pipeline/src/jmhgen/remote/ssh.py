"""``jmh-train-remote`` — run a training stage on a rented GPU box over SSH.

A deliberately small, dependency-free (stdlib ``subprocess`` + ``rsync``/``ssh``) runner so
local training is not required when the model is too large for the dev machine. The flow:

1. optionally run ``bootstrap_cmd`` over SSH (e.g. install ``rsync`` on minimal GPU images);
2. ``rsync`` the working tree to ``remote_dir`` (excluding caches, the local venv, and the
   heavy distilled/source data — but **including** the small ``data/sft`` dataset);
3. ensure ``uv`` is installed remotely, then sync the selected isolated GPU profile;
4. ``uv run jmh-train-<stage> --config <stage_config>`` with forwarded env (e.g. ``HF_TOKEN``),
   streaming the remote logs back live;
5. optionally ``rsync`` the produced checkpoints (``output_dir`` from the stage config) back.

The command-building helpers are pure functions (no I/O) so they can be unit-tested without a
network. This is the "basic version" extension point; a richer runner (provisioning, async
log tailing, multi-GPU launchers) can grow from here.
"""

from __future__ import annotations

import shlex
import subprocess
from pathlib import Path
from typing import Annotated

import typer
import yaml

from jmhgen.config.schema import RemoteConfig
from jmhgen.utils.logging import get_logger

logger = get_logger("jmhgen.remote")

# Prepend the locations uv may install itself to so a freshly bootstrapped uv is found.
_REMOTE_PATH = 'export PATH="$HOME/.local/bin:$HOME/.cargo/bin:$PATH"'
_PROFILE_PROJECTS = {
    "gemma": "profiles/gemma-cu130",
    "qwen": "profiles/qwen-cu128",
}


def profile_project(config: RemoteConfig) -> str:
    """Return the project directory containing this run's CUDA-specific lockfile."""
    return _PROFILE_PROJECTS[config.model_profile]


def profile_venv_name(config: RemoteConfig) -> str:
    """Stable per-profile venv/cache suffix used on remote scratch storage."""
    return f"{config.model_profile}-cu{'130' if config.model_profile == 'gemma' else '128'}"


def _ssh_transport(config: RemoteConfig) -> str:
    """The ``-e`` transport string rsync hands to ssh (host added separately by rsync)."""
    parts = ["ssh", "-p", str(config.port)]
    if config.identity_file:
        parts += ["-i", config.identity_file]
    return " ".join(parts)


def _ssh_prefix(config: RemoteConfig) -> list[str]:
    prefix = ["ssh", "-p", str(config.port)]
    if config.identity_file:
        prefix += ["-i", config.identity_file]
    return prefix


def _target(config: RemoteConfig) -> str:
    return f"{config.user}@{config.host}"


def build_push_cmd(config: RemoteConfig) -> list[str]:
    """rsync the local working tree up to ``remote_dir`` (mirrors, deletes stale files)."""
    excludes = [f"--exclude={pattern}" for pattern in config.rsync_excludes]
    return [
        "rsync",
        "-az",
        "--delete",
        "--no-owner",
        "--no-group",
        "-e",
        _ssh_transport(config),
        *excludes,
        "./",
        f"{_target(config)}:{config.remote_dir}/",
    ]


def build_pull_cmd(config: RemoteConfig, output_dir: str) -> list[str]:
    """rsync the remote ``output_dir`` back down into the same relative local path."""
    return [
        "rsync",
        "-az",
        "-e",
        _ssh_transport(config),
        f"{_target(config)}:{config.remote_dir}/{output_dir}/",
        f"{output_dir}/",
    ]


def build_ssh_exec(config: RemoteConfig, script: str) -> list[str]:
    """An ssh command that runs ``script`` through the remote shell."""
    return [*_ssh_prefix(config), _target(config), script]


def build_volume_layout_script(config: RemoteConfig) -> str:
    """Create durable volume dirs and optional fast scratch-cache dirs."""
    vol = shlex.quote(config.remote_volume or "")
    script = (
        f"mkdir -p {vol}/.uv-cache {vol}/.cache/huggingface {vol}/.m2 {vol}/logs; "
        f"mkdir -p /root/.cache; "
        f"ln -sfn {vol}/.m2 /root/.m2; "
        f"ln -sfn {vol}/.cache/huggingface /root/.cache/huggingface"
    )
    if config.remote_scratch:
        scratch = shlex.quote(config.remote_scratch)
        profile = profile_venv_name(config)
        script += (
            f"; mkdir -p {scratch}/.uv-cache {scratch}/.cache/{profile}/triton "
            f"{scratch}/.cache/{profile}/torchinductor {scratch}/.cache/{profile}/vllm "
            f"{scratch}/tmp"
        )
    else:
        script += f"; ln -sfn {vol}/.uv-cache /root/.cache/uv"
    return script


def build_volume_env(config: RemoteConfig) -> str:
    """Export persistent asset paths and optional local scratch paths."""
    vol = config.remote_volume or ""
    scratch = config.remote_scratch or vol
    profile = profile_venv_name(config)
    env = (
        f"export UV_CACHE_DIR={shlex.quote(f'{scratch}/.uv-cache')}; "
        f"export UV_LINK_MODE=copy; "
        f"export HF_HOME={shlex.quote(f'{vol}/.cache/huggingface')}; "
        f"export XDG_CACHE_HOME={shlex.quote(f'{scratch}/.cache/{profile}')}; "
        f"export UV_PROJECT_ENVIRONMENT={shlex.quote(f'{scratch}/.venv-{profile}')}; "
        f"export JMHGEN_MODEL_PROFILE={shlex.quote(config.model_profile)}; "
        f"export JMHGEN_VOLUME={shlex.quote(vol)}; "
    )
    env += (
        f"export TRITON_CACHE_DIR={shlex.quote(f'{scratch}/.cache/{profile}/triton')}; "
        f"export TORCHINDUCTOR_CACHE_DIR="
        f"{shlex.quote(f'{scratch}/.cache/{profile}/torchinductor')}; "
        f"export VLLM_CACHE_ROOT={shlex.quote(f'{scratch}/.cache/{profile}/vllm')}; "
        f"export TMPDIR={shlex.quote(f'{scratch}/tmp')}; "
    )
    return env


def build_bootstrap_script(config: RemoteConfig) -> str | None:
    """Remote shell script run before rsync push (volume layout + optional bootstrap_cmd)."""
    parts: list[str] = []
    if config.remote_volume:
        parts.append(build_volume_layout_script(config))
    if config.bootstrap_cmd:
        parts.append(config.bootstrap_cmd)
    if not parts:
        return None
    return f"set -e; {'; '.join(parts)}"


def build_setup_script(config: RemoteConfig) -> str:
    """Remote shell script: ensure uv exists, then sync the training environment."""
    volume_env = build_volume_env(config) if config.remote_volume else ""
    return (
        f"set -e; cd {config.remote_dir}; "
        f"{_REMOTE_PATH}; "
        f"{volume_env}"
        f"command -v uv >/dev/null 2>&1 || {{ {config.setup_cmd}; }}; "
        f"{_REMOTE_PATH}; "
        f"{volume_env}"
        f"uv sync --project {shlex.quote(profile_project(config))} --locked"
    )


def build_train_script(config: RemoteConfig, env: dict[str, str]) -> str:
    """Remote shell script: launch the training stage with forwarded env vars."""
    volume_env = build_volume_env(config) if config.remote_volume else ""
    env_prefix = "".join(f"{key}={shlex.quote(value)} " for key, value in env.items())
    return (
        f"set -e; cd {config.remote_dir}; "
        f"{_REMOTE_PATH}; "
        f"{volume_env}"
        f"{env_prefix}uv run jmh-train-{config.stage} --config {shlex.quote(config.stage_config)}"
    )


def resolve_output_dir(config: RemoteConfig) -> str:
    """Read ``output_dir`` from the stage config YAML (falls back to ``outputs/<stage>``)."""
    fallback = f"outputs/{config.stage}"
    try:
        data = yaml.safe_load(Path(config.stage_config).read_text(encoding="utf-8")) or {}
    except OSError:
        return fallback
    value = data.get("output_dir")
    return str(value) if value else fallback


def collect_env(config: RemoteConfig, environ: dict[str, str]) -> dict[str, str]:
    """Pick the configured passthrough vars that are actually set in ``environ``."""
    return {name: environ[name] for name in config.env_passthrough if environ.get(name)}


def _display(cmd: list[str], secrets: tuple[str, ...] = ()) -> str:
    shown = " ".join(shlex.quote(part) for part in cmd)
    for secret in secrets:
        if secret:
            shown = shown.replace(secret, "***")
    return shown


def run_remote(
    config: RemoteConfig,
    *,
    dry_run: bool = False,
    sync_only: bool = False,
    env: dict[str, str] | None = None,
) -> None:
    """Push code, sync the env, run the stage, and pull outputs back (each step over SSH)."""
    if env is None:
        import os

        env = collect_env(config, dict(os.environ))
    secrets = tuple(env.values())
    output_dir = resolve_output_dir(config)

    steps: list[tuple[str, list[str], tuple[str, ...]]] = []
    bootstrap = build_bootstrap_script(config)
    if bootstrap is not None:
        steps.append(("bootstrap remote", build_ssh_exec(config, bootstrap), ()))
    steps.extend(
        [
            ("push code", build_push_cmd(config), ()),
            ("setup env", build_ssh_exec(config, build_setup_script(config)), ()),
        ]
    )
    if not sync_only:
        steps.append(("train", build_ssh_exec(config, build_train_script(config, env)), secrets))
        if config.pull_outputs:
            steps.append(("pull outputs", build_pull_cmd(config, output_dir), ()))

    for label, cmd, step_secrets in steps:
        logger.info("[remote] %s: %s", label, _display(cmd, step_secrets))
        if dry_run:
            continue
        if label == "pull outputs":
            Path(output_dir).mkdir(parents=True, exist_ok=True)
        # Args are an explicit argv list (no shell=True), so there is no shell-injection seam.
        subprocess.run(cmd, check=True)

    if dry_run:
        logger.info("[remote] dry run: no commands executed")


def run(
    config: Annotated[
        Path, typer.Option(help="RemoteConfig YAML with the SSH connection + job.")
    ] = Path("configs/remote/default.yaml"),
    host: Annotated[str | None, typer.Option(help="Override the SSH host.")] = None,
    user: Annotated[str | None, typer.Option(help="Override the SSH user.")] = None,
    stage: Annotated[str | None, typer.Option(help="Override the stage (sft/rft/grpo).")] = None,
    stage_config: Annotated[
        str | None, typer.Option(help="Override the stage config path used on the remote.")
    ] = None,
    dry_run: Annotated[
        bool, typer.Option("--dry-run", help="Print the rsync/ssh commands without running.")
    ] = False,
    sync_only: Annotated[
        bool,
        typer.Option(
            "--sync-only",
            help="Push code and run uv sync on the remote; skip training and pull.",
        ),
    ] = False,
) -> None:
    """Run a training stage on a remote GPU over SSH."""
    cfg = RemoteConfig.from_yaml(config)
    updates: dict[str, str] = {}
    if host is not None:
        updates["host"] = host
    if user is not None:
        updates["user"] = user
    if stage is not None:
        updates["stage"] = stage
    if stage_config is not None:
        updates["stage_config"] = stage_config
    if updates:
        cfg = cfg.model_copy(update=updates)

    try:
        run_remote(cfg, dry_run=dry_run, sync_only=sync_only)
    except subprocess.CalledProcessError as exc:
        raise typer.Exit(code=exc.returncode or 1) from None


def main() -> None:
    typer.run(run)


if __name__ == "__main__":
    main()
