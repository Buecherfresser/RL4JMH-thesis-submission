#!/usr/bin/env bash
# Workaround for the macOS + Cursor + Python 3.14 editable-install clash:
#
#   * Cursor's file watcher marks files inside `.venv/` with UF_HIDDEN.
#   * CPython 3.14's site.py was changed to silently skip UF_HIDDEN .pth
#     files (see Lib/site.py lines 183-186 in 3.14.3).
#   * Result: setuptools' editable install (a `.pth` file) never loads,
#     and `jmhbench` raises ModuleNotFoundError.
#
# This script replaces `.venv/bin/jmhbench` with a self-locating shim that
# adds the repo root to sys.path before importing the package. Run after
# every `pip install -e .` in this venv.
set -euo pipefail

REPO="$(cd "$(dirname "$0")/.." && pwd)"
PYTHON="$REPO/.venv/bin/python"
SCRIPT="$REPO/.venv/bin/jmhbench"

if [ ! -x "$PYTHON" ]; then
  echo "error: $PYTHON not found. Create the venv first: python3 -m venv .venv" >&2
  exit 1
fi

# Find the canonical python interpreter path used by the venv.
PY_REAL="$($PYTHON -c 'import sys; print(sys.executable)')"

cat > "$SCRIPT" <<PYEOF
#!${PY_REAL}
# Self-healing shim — see tools/fix_install.sh for the rationale.
import os
import sys

_REPO = os.path.dirname(os.path.dirname(os.path.dirname(os.path.realpath(__file__))))
if os.path.isfile(os.path.join(_REPO, "jmhbench", "__init__.py")) and _REPO not in sys.path:
    sys.path.insert(0, _REPO)

from jmhbench.cli import main  # noqa: E402

if __name__ == "__main__":
    sys.argv[0] = sys.argv[0].removesuffix(".exe")
    sys.exit(main())
PYEOF

chmod +x "$SCRIPT"
echo "patched: $SCRIPT"

# Belt-and-braces: also clear UF_HIDDEN on any .pth files so that, if Cursor
# isn't actively running, the regular editable mechanism keeps working.
if [ "$(uname)" = "Darwin" ]; then
  chflags nohidden "$REPO/.venv/lib/"python*/site-packages/*.pth 2>/dev/null || true
fi

echo "verifying..."
"$SCRIPT" --help >/dev/null && echo "ok: jmhbench --help succeeded"
