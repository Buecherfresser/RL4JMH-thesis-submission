"""GRPO corpus selection (original six vs full merged JSONL)."""

from __future__ import annotations

import json
from pathlib import Path

from jmhgen.config.schema import GRPOConfig
from jmhgen.training.grpo import build_prompt_records, original_rl_projects

_ROOT = Path(__file__).resolve().parents[1]


def test_original_rl_projects_match_manifest() -> None:
    projects = original_rl_projects(_ROOT / "configs/grpo/rl-corpus.yaml")
    assert projects == {
        "commons-numbers",
        "commons-statistics",
        "commons-codec",
        "commons-text",
        "jackson-core",
        "roaringbitmap",
    }


def test_corpus_original_filters_merged_jsonl(tmp_path: Path) -> None:
    path = tmp_path / "merged.jsonl"
    rows = [
        {
            "id": "a.A",
            "source": "class A {}",
            "language": "java",
            "project": "commons-codec",
            "path": "A.java",
            "metadata": {},
        },
        {
            "id": "b.B",
            "source": "class B {}",
            "language": "java",
            "project": "guava",
            "path": "B.java",
            "metadata": {},
        },
    ]
    path.write_text("\n".join(json.dumps(r) for r in rows) + "\n", encoding="utf-8")

    original = build_prompt_records(
        GRPOConfig(dataset_path=str(path), corpus="original", max_source_chars=0)
    )
    full = build_prompt_records(
        GRPOConfig(dataset_path=str(path), corpus="full", max_source_chars=0)
    )

    assert [r["project"] for r in original] == ["commons-codec"]
    assert [r["project"] for r in full] == ["commons-codec", "guava"]


def test_qwen_grpo_defaults_to_original_corpus() -> None:
    config = GRPOConfig.from_yaml(_ROOT / "configs/grpo/qwen35-4b.yaml")
    assert config.corpus == "original"


def test_resolve_resume_checkpoint(tmp_path):
    """None never resumes, auto picks the newest checkpoint, an explicit path is honoured."""
    import pytest

    from jmhgen.config.schema import GRPOConfig
    from jmhgen.training.grpo import resolve_resume_checkpoint

    out = tmp_path / "run"
    out.mkdir()
    cfg = GRPOConfig(base_model="m", output_dir=str(out), dataset_path="d.jsonl")

    # Default: never resume, even when checkpoints exist.
    (out / "checkpoint-25").mkdir()
    assert cfg.resume_from_checkpoint is None
    assert resolve_resume_checkpoint(cfg) is None

    # auto with no checkpoints -> start fresh rather than fail.
    empty = GRPOConfig(
        base_model="m", output_dir=str(tmp_path / "empty"), dataset_path="d.jsonl",
        resume_from_checkpoint="auto",
    )
    (tmp_path / "empty").mkdir()
    assert resolve_resume_checkpoint(empty) is None

    # auto picks the highest step, not lexicographic order (100 > 25).
    (out / "checkpoint-100").mkdir()
    (out / "checkpoint-not-a-step").mkdir()
    cfg.resume_from_checkpoint = "auto"
    assert resolve_resume_checkpoint(cfg) == str(out / "checkpoint-100")

    # Explicit path is used verbatim; a bad one fails loudly instead of silently restarting.
    cfg.resume_from_checkpoint = str(out / "checkpoint-25")
    assert resolve_resume_checkpoint(cfg) == str(out / "checkpoint-25")
    cfg.resume_from_checkpoint = str(out / "nope")
    with pytest.raises(SystemExit):
        resolve_resume_checkpoint(cfg)


def _load_bootstrap():
    import importlib.util

    path = Path(__file__).resolve().parents[1] / "scripts" / "bootstrap_rl_corpus.py"
    spec = importlib.util.spec_from_file_location("bootstrap_rl_corpus", path)
    assert spec and spec.loader
    mod = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(mod)
    return mod


def test_merge_module_sources_ignores_the_checkout_s_parent_directories(tmp_path: Path) -> None:
    """Skip tokens must match inside the checkout, not the absolute path.

    ``_SKIP_PATH_PARTS`` contains "/jmh/" and "/it/", which are ordinary directory names on the
    way to a repo. On LRZ the tree sits under .../go68bef2/jmh/JMH_Training_Pipeline, so an
    absolute match dropped every file of every merged project (35 empty source trees).
    """
    boot = _load_bootstrap()
    # Nest the checkout under directories that collide with the skip list.
    repo = tmp_path / "jmh" / "it" / "examples" / "myrepo"
    pkg = repo / "core" / "src" / "main" / "java" / "org" / "acme"
    pkg.mkdir(parents=True)
    (pkg / "Keep.java").write_text("package org.acme; class Keep {}", encoding="utf-8")
    # A genuinely test-scoped file inside the checkout must still be dropped.
    tst = repo / "core" / "src" / "main" / "java" / "org" / "acme" / "benchmarks"
    tst.mkdir(parents=True)
    (tst / "Drop.java").write_text("package org.acme.benchmarks; class Drop {}", encoding="utf-8")

    out = tmp_path / "merged" / "src" / "main" / "java"
    boot._merge_module_sources(repo, out, package_root="org.acme")

    copied = sorted(p.relative_to(out).as_posix() for p in out.rglob("*.java"))
    assert copied == ["org/acme/Keep.java"]
