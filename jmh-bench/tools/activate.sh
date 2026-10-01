#!/usr/bin/env bash
# Activate the JMH-Bench venv and ensure `jmhbench` is on PATH.
#
# Usage (must be sourced, not executed):
#   source tools/activate.sh
#   # or from anywhere in the repo:
#   source "$(git rev-parse --show-toplevel)/tools/activate.sh"

_jmhbench_activate() {
  local repo
  if repo="$(git rev-parse --show-toplevel 2>/dev/null)"; then
    :
  else
    repo="$(cd "$(dirname "${BASH_SOURCE[0]:-$0}")/.." && pwd)"
  fi

  local venv_activate="${repo}/.venv/bin/activate"
  if [[ ! -f "${venv_activate}" ]]; then
    echo "jmhbench: no .venv at ${repo}/.venv" >&2
    echo "Create it with: python3 -m venv .venv && pip install -e '.[openai]'" >&2
    return 1
  fi

  # shellcheck source=/dev/null
  source "${venv_activate}"

  if ! command -v jmhbench >/dev/null 2>&1 || ! jmhbench --help >/dev/null 2>&1; then
    "${repo}/tools/fix_install.sh" >/dev/null
  fi
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  echo "Source this script instead of running it:" >&2
  echo "  source tools/activate.sh" >&2
  exit 1
fi

_jmhbench_activate
