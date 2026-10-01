#!/usr/bin/env bash
# Bootstrap SpotJMHBugs (Costa et al., TSE 2019) into vendor/spotbugs/.
#
# Re-runs are no-ops once the vendor tree exists. Required for bytecode-level r_anti
# (SpotJMHBugs); without it the reward falls back to the regex pre-screen.
#
# Layout produced:
#   vendor/spotbugs/spotbugs-<ver>/bin/spotbugs
#   vendor/spotbugs/spotbugs-<ver>/plugin/spotJMHbugs.jar
set -euo pipefail

REPO="$(cd "$(dirname "$0")/.." && pwd)"
VENDOR="$REPO/vendor/spotbugs"
SPOTBUGS_VER="4.9.6"
SPOTBUGS_DIR="$VENDOR/spotbugs-$SPOTBUGS_VER"
SPOTBUGS_TGZ_URL="https://github.com/spotbugs/spotbugs/releases/download/$SPOTBUGS_VER/spotbugs-$SPOTBUGS_VER.tgz"
PLUGIN_REPO="https://github.com/DiegoEliasCosta/spotjmhbugs.git"

mkdir -p "$VENDOR"

if [ ! -x "$SPOTBUGS_DIR/bin/spotbugs" ]; then
  echo "[1/3] downloading SpotBugs $SPOTBUGS_VER ..."
  curl -fsSL "$SPOTBUGS_TGZ_URL" | tar xz -C "$VENDOR"
  chmod +x "$SPOTBUGS_DIR/bin/spotbugs"
else
  echo "[1/3] SpotBugs already present at $SPOTBUGS_DIR"
fi

PLUGIN_JAR="$SPOTBUGS_DIR/plugin/spotJMHbugs.jar"
if [ ! -f "$PLUGIN_JAR" ]; then
  echo "[2/3] building SpotJMHBugs plugin from source ..."
  WORK="$(mktemp -d)"
  trap 'rm -rf "$WORK"' EXIT
  git clone --depth=1 "$PLUGIN_REPO" "$WORK/spotjmhbugs" >/dev/null 2>&1
  # The Maven wrapper is not reliably executable after a clone onto GPFS/DSS (same class of
  # problem as the JDK's lib/jspawnhelper), which fails with "./mvnw: Permission denied".
  # Restore the bit, and invoke through bash so a stripped one cannot break the build again.
  chmod +x "$WORK/spotjmhbugs/mvnw" 2>/dev/null || true
  ( cd "$WORK/spotjmhbugs" && bash ./mvnw -q -DskipTests package >/dev/null )
  mkdir -p "$SPOTBUGS_DIR/plugin"
  cp "$WORK/spotjmhbugs/target/spotJMHbugs-"*.jar "$PLUGIN_JAR"
else
  echo "[2/3] SpotJMHBugs plugin already present at $PLUGIN_JAR"
fi

echo "[3/3] verifying plugin is loaded ..."
if "$SPOTBUGS_DIR/bin/spotbugs" -textui -showPlugins 2>&1 | grep -q "performance-tests-checker"; then
  echo "ok: SpotJMHBugs is available."
else
  echo "error: plugin built but SpotBugs didn't load it." >&2
  exit 1
fi
