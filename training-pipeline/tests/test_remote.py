"""Tests for the remote SSH runner: pure command-building (no network, no subprocess).

These pin the rsync/ssh argv strings the runner would execute, plus the env passthrough and
secret redaction, so the "basic version" stays honest without needing a real GPU box.
"""

from __future__ import annotations

from jmhgen.config.schema import RemoteConfig
from jmhgen.remote.ssh import (
    _display,
    build_bootstrap_script,
    build_pull_cmd,
    build_push_cmd,
    build_setup_script,
    build_ssh_exec,
    build_train_script,
    build_volume_env,
    build_volume_layout_script,
    collect_env,
    profile_project,
    resolve_output_dir,
)


def _cfg(**overrides: object) -> RemoteConfig:
    base: dict[str, object] = {"host": "gpu.example", "user": "ubuntu", "remote_dir": "~/jmhgen"}
    base.update(overrides)
    return RemoteConfig(**base)  # type: ignore[arg-type]


class TestPushPull:
    def test_push_targets_remote_dir_and_excludes(self) -> None:
        cmd = build_push_cmd(_cfg(rsync_excludes=[".git", "outputs"]))
        assert cmd[0] == "rsync"
        assert "--delete" in cmd
        assert "--no-owner" in cmd
        assert "--no-group" in cmd
        assert "--exclude=.git" in cmd
        assert "--exclude=outputs" in cmd
        assert cmd[-2:] == ["./", "ubuntu@gpu.example:~/jmhgen/"]

    def test_push_transport_includes_port_and_key(self) -> None:
        cmd = build_push_cmd(_cfg(port=2222, identity_file="/keys/id_ed25519"))
        transport = cmd[cmd.index("-e") + 1]
        assert transport == "ssh -p 2222 -i /keys/id_ed25519"

    def test_pull_mirrors_output_dir(self) -> None:
        cmd = build_pull_cmd(_cfg(), "outputs/sft")
        assert cmd[-2:] == ["ubuntu@gpu.example:~/jmhgen/outputs/sft/", "outputs/sft/"]


class TestRemoteScripts:
    def test_bootstrap_script_omitted_when_unset(self) -> None:
        assert build_bootstrap_script(_cfg()) is None

    def test_bootstrap_script_includes_volume_layout(self) -> None:
        script = build_bootstrap_script(_cfg(remote_volume="/workspace", bootstrap_cmd="true"))
        assert script is not None
        assert "/workspace/.uv-cache" in script
        assert "ln -sfn /workspace/.m2 /root/.m2" in script
        assert "true" in script

    def test_volume_env_exports_caches_on_volume(self) -> None:
        env = build_volume_env(_cfg(remote_volume="/workspace"))
        assert "UV_CACHE_DIR=/workspace/.uv-cache" in env
        assert "UV_LINK_MODE=copy" in env
        assert "HF_HOME=/workspace/.cache/huggingface" in env

    def test_volume_env_uses_scratch_for_hot_caches_and_venv(self) -> None:
        cfg = _cfg(remote_volume="/workspace", remote_scratch="/root/jmhgen-scratch")
        layout = build_volume_layout_script(cfg)
        env = build_volume_env(cfg)
        assert "mkdir -p /root/jmhgen-scratch/.uv-cache" in layout
        assert "/root/jmhgen-scratch/.cache/gemma-cu130/triton" in layout
        assert "UV_CACHE_DIR=/root/jmhgen-scratch/.uv-cache" in env
        assert "UV_PROJECT_ENVIRONMENT=/root/jmhgen-scratch/.venv-gemma-cu130" in env
        assert "TRITON_CACHE_DIR=/root/jmhgen-scratch/.cache/gemma-cu130/triton" in env
        assert "JMHGEN_MODEL_PROFILE=gemma" in env
        assert "HF_HOME=/workspace/.cache/huggingface" in env

    def test_setup_script_bootstraps_uv_then_syncs(self) -> None:
        script = build_setup_script(_cfg())
        assert "cd ~/jmhgen" in script
        assert "command -v uv" in script
        assert "uv sync --project profiles/gemma-cu130 --locked" in script

    def test_setup_script_uses_volume_caches_when_configured(self) -> None:
        script = build_setup_script(_cfg(remote_volume="/workspace", model_profile="qwen"))
        assert "UV_CACHE_DIR=/workspace/.uv-cache" in script
        assert "uv sync --project profiles/qwen-cu128 --locked" in script

    def test_qwen_profile_uses_its_own_environment_and_project(self) -> None:
        cfg = _cfg(remote_volume="/workspace", model_profile="qwen")
        env = build_volume_env(cfg)
        assert profile_project(cfg) == "profiles/qwen-cu128"
        assert "UV_PROJECT_ENVIRONMENT=/workspace/.venv-qwen-cu128" in env
        assert "JMHGEN_MODEL_PROFILE=qwen" in env

    def test_train_script_forwards_env_and_runs_stage(self) -> None:
        script = build_train_script(_cfg(stage="sft"), {"HF_TOKEN": "secret-123"})
        expected = "HF_TOKEN=secret-123 uv run jmh-train-sft --config configs/sft/default.yaml"
        assert expected in script

    def test_train_script_uses_volume_caches_when_configured(self) -> None:
        script = build_train_script(
            _cfg(stage="grpo", remote_volume="/workspace"),
            {"HF_TOKEN": "secret-123"},
        )
        assert "UV_CACHE_DIR=/workspace/.uv-cache" in script
        assert "HF_TOKEN=secret-123" in script

    def test_ssh_exec_wraps_script_for_host(self) -> None:
        cmd = build_ssh_exec(_cfg(port=22), "echo hi")
        assert cmd[:3] == ["ssh", "-p", "22"]
        assert cmd[-2:] == ["ubuntu@gpu.example", "echo hi"]


class TestEnvAndOutput:
    def test_collect_env_only_keeps_set_passthrough_vars(self) -> None:
        cfg = _cfg(env_passthrough=["HF_TOKEN", "WANDB_API_KEY"])
        env = collect_env(cfg, {"HF_TOKEN": "abc", "OTHER": "x"})
        assert env == {"HF_TOKEN": "abc"}

    def test_resolve_output_dir_falls_back_for_missing_config(self) -> None:
        cfg = _cfg(stage="grpo", stage_config="does/not/exist.yaml")
        assert resolve_output_dir(cfg) == "outputs/grpo"

    def test_resolve_output_dir_reads_stage_config(self) -> None:
        cfg = _cfg(stage="sft", stage_config="configs/sft/default.yaml")
        assert resolve_output_dir(cfg) == "outputs/sft"

    def test_display_redacts_secrets(self) -> None:
        cmd = ["ssh", "host", "HF_TOKEN=secret-123 uv run x"]
        assert "secret-123" not in _display(cmd, ("secret-123",))
        assert "***" in _display(cmd, ("secret-123",))
