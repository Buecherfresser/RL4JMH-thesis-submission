#!/usr/bin/env bash
#
# remote_run_eval.sh — prepare a remote VM and start tools/run_eval.sh there.
#
# Reads SSH settings from .env, syncs the repo when the remote checkout is
# missing or stale, installs Python/JMH-Bench dependencies, then kicks off
# run_eval.sh on the remote with REMOTE=local so generation + benchmarking
# both run on the VM (close your laptop; monitor via SSH).
#
# Usage:
#   tools/remote_run_eval.sh              # prepare + start in background
#   tools/remote_run_eval.sh --sync-only  # prepare only, do not start
#   tools/remote_run_eval.sh --force-sync # always rsync repo before starting
#   tools/remote_run_eval.sh --cache-wheels  # also ship a pre-built linux wheel cache
#   tools/remote_run_eval.sh --foreground # run eval in the foreground (debug)
#
# .env is always rsynced (config/API keys). The repo syncs when the git rev changes
# or with --force-sync. Wheel caching is opt-in (slow); pip retries on the VM by default.
#
# Override eval matrix via .env or the environment (same vars as run_eval.sh):
#   HARNESS, TRACKS, PROJECTS, TEMPS, THINKING, PARALLEL, SKIP_GEN, RUN_DIR, ...
#
set -euo pipefail

REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO"

die() { echo "error: $*" >&2; exit 1; }

SYNC_ONLY=0
FORCE_SYNC=0
FOREGROUND=0
CACHE_WHEELS=0
for arg in "$@"; do
  case "$arg" in
    --sync-only) SYNC_ONLY=1 ;;
    --force-sync) FORCE_SYNC=1 ;;
    --foreground) FOREGROUND=1 ;;
    --cache-wheels) CACHE_WHEELS=1 ;;
    -h|--help)
      sed -n '2,18p' "$0" | sed 's/^# \{0,1\}//'
      exit 0
      ;;
    *) die "unknown arg: $arg (try --help)" ;;
  esac
done

# Read a single KEY=value from .env without bash expanding ~ to the local home.
read_env_var() {
  local name="$1" file="$2" line raw
  line="$(grep -E "^${name}=" "$file" 2>/dev/null | head -1 || true)"
  [[ -n "$line" ]] || return 1
  raw="${line#*=}"
  raw="${raw%$'\r'}"
  case "$raw" in
    \"*\") raw="${raw:1:${#raw}-2}" ;;
    \'*\') raw="${raw:1:${#raw}-2}" ;;
  esac
  printf '%s' "$raw"
}

# Turn REMOTE_BASE into an absolute path on the remote host.
resolve_remote_base() {
  local base="$1" remote_home="$2" local_home="$3"
  # Sourcing unquoted "~/…" in .env expands to the laptop path — remap to remote $HOME.
  if [[ -n "$local_home" && "$base" == "$local_home"/* ]]; then
    printf '%s' "$remote_home/${base#$local_home/}"
    return 0
  fi
  if [[ "$base" == "~" ]]; then
    printf '%s' "$remote_home"
  elif [[ "$base" == "~/"* ]]; then
    printf '%s' "${remote_home}${base#\~}"
  elif [[ "$base" == /* ]]; then
    printf '%s' "$base"
  else
    printf '%s' "$remote_home/$base"
  fi
}

# ---- load local .env -------------------------------------------------------
if [[ -f "$REPO/.env" ]]; then
  set -a
  # shellcheck source=/dev/null
  source "$REPO/.env"
  set +a
else
  die "no .env found — copy .env.example and set REMOTE (and API keys)"
fi

REMOTE="${REMOTE:-}"
# Prefer the raw .env value so ~/ stays ~/ and is resolved on the remote.
if raw_base="$(read_env_var REMOTE_BASE "$REPO/.env" 2>/dev/null)"; then
  REMOTE_BASE="$raw_base"
else
  REMOTE_BASE="${REMOTE_BASE:-jmhbench-remote}"
fi
JAVA_HOME_HINT="${JAVA_HOME_HINT:-}"   # optional override; auto-detected on remote if unset
SSH_OPTS="${SSH_OPTS:-}"

[[ -n "$REMOTE" ]] || die "REMOTE is not set in .env (ssh host alias or user@host)"
[[ "$REMOTE" != "local" ]] || die "REMOTE=local belongs on the remote box — set REMOTE to your ssh host here"

log() { echo "[$(date -Iseconds)] $*"; }

ssh_monitor_inv() {
  if [[ -n "$SSH_OPTS" ]]; then
    # shellcheck disable=SC2086
    printf 'ssh %s %s' "$SSH_OPTS" "$REMOTE"
  else
    printf 'ssh %s' "$REMOTE"
  fi
}

print_monitor_cmds() {
  local launcher_log="$1"
  local ssh_inv
  ssh_inv="$(ssh_monitor_inv)"
  echo ""
  log "monitor:"
  log "  ${ssh_inv} tail -f ${launcher_log}"
  local run_log
  run_log="$(ssh_cmd "ls -td '$REMOTE_BASE'/reports/eval_*/run.log 2>/dev/null | head -1" || true)"
  if [[ -n "$run_log" ]]; then
    log "  ${ssh_inv} tail -f ${run_log}"
  fi
}

ssh_cmd() {
  if [[ -n "$SSH_OPTS" ]]; then
    # shellcheck disable=SC2086
    ssh $SSH_OPTS "$REMOTE" "$@"
  else
    ssh "$REMOTE" "$@"
  fi
}
rsync_cmd() {
  if [[ -n "$SSH_OPTS" ]]; then
    # shellcheck disable=SC2086
    rsync -e "ssh $SSH_OPTS" "$@"
  else
    rsync "$@"
  fi
}

# Resolve REMOTE_BASE against the remote $HOME (never the local Mac path).
REMOTE_HOME="$(ssh_cmd 'echo "$HOME"')"
if [[ -n "${HOME:-}" && "$REMOTE_BASE" == "$HOME"/* ]]; then
  log "warn: REMOTE_BASE expanded to local path — remapping to remote"
fi
REMOTE_BASE="$(resolve_remote_base "$REMOTE_BASE" "$REMOTE_HOME" "$HOME")"

log "remote : $REMOTE"
log "path   : $REMOTE_BASE"

# ---- verify remote OS packages before sync / pip / SpotJMHBugs bootstrap -------
check_remote_deps() {
  log "checking remote prerequisites on $REMOTE"
  local report
  report="$(ssh_cmd "REMOTE_BASE='$REMOTE_BASE' bash -s" <<'REMOTE_CHECK' || true
set -uo pipefail

missing=()
apt_pkgs=()

add_missing() {
  missing+=("$1")
  if [[ -n "${2:-}" ]]; then
    apt_pkgs+=("$2")
  fi
}

# python3 + venv (ensurepip)
if ! command -v python3 >/dev/null 2>&1; then
  add_missing "python3" python3
else
  py_mm="$(python3 -c 'import sys; print(f"{sys.version_info.major}.{sys.version_info.minor}")')"
  if ! python3 -c 'import ensurepip' >/dev/null 2>&1; then
    add_missing "python3 venv (python${py_mm}-venv)" "python${py_mm}-venv"
  fi
fi

# JDK 17+ with javac (Maven compile-check + JMH)
java_major=""
if command -v java >/dev/null 2>&1; then
  java_major="$(java -version 2>&1 | sed -n 's/.*version "\([0-9]*\).*/\1/p' | head -1)"
fi
javac_ok=0
if command -v javac >/dev/null 2>&1; then
  javac_ok=1
else
  for candidate in \
    "${JAVA_HOME:-}" \
    /usr/lib/jvm/java-21-openjdk-amd64 /usr/lib/jvm/java-21-openjdk \
    /usr/lib/jvm/java-17-openjdk-amd64 /usr/lib/jvm/java-17-openjdk; do
    [[ -n "$candidate" && -x "$candidate/bin/javac" ]] && javac_ok=1 && break
  done
fi
if [[ -z "$java_major" || "$java_major" -lt 17 ]]; then
  add_missing "JDK 17+ (java)" openjdk-21-jdk
elif [[ "$javac_ok" -eq 0 ]]; then
  add_missing "JDK 17+ with javac (not just a JRE)" openjdk-21-jdk
fi

if ! command -v mvn >/dev/null 2>&1; then
  add_missing "Maven (mvn)" maven
fi

# SpotJMHBugs is bootstrapped on the remote on first run with mvn present.
spotbugs_bin="$REMOTE_BASE/vendor/spotbugs/spotbugs-4.9.6/bin/spotbugs"
plugin_jar="$REMOTE_BASE/vendor/spotbugs/spotbugs-4.9.6/plugin/spotJMHbugs.jar"
needs_spotbugs=0
if [[ ! -x "$spotbugs_bin" || ! -f "$plugin_jar" ]]; then
  needs_spotbugs=1
fi
if [[ "$needs_spotbugs" -eq 1 ]]; then
  if ! command -v curl >/dev/null 2>&1; then
    add_missing "curl (SpotBugs download)" curl
  fi
  if ! command -v git >/dev/null 2>&1; then
    add_missing "git (SpotJMHBugs plugin build)" git
  fi
fi

if [[ ${#missing[@]} -eq 0 ]]; then
  echo "__OK__"
  exit 0
fi

echo "__MISSING__"
printf '%s\n' "${missing[@]}"
if [[ ${#apt_pkgs[@]} -gt 0 ]]; then
  pkgs="$(printf '%s\n' "${apt_pkgs[@]}" | awk '!seen[$0]++' | tr '\n' ' ')"
  if [[ -f /etc/debian_version ]]; then
    echo "__APT__ sudo apt install -y ${pkgs% }"
  else
    echo "__APT__ install: ${pkgs% } (use your distro package manager)"
  fi
fi
REMOTE_CHECK
)"

  if [[ "$report" == *__OK__* ]]; then
    log "remote prerequisites ok"
    return 0
  fi

  {
    echo "error: missing prerequisites on $REMOTE:" >&2
    while IFS= read -r line; do
      [[ "$line" == __*__ ]] && continue
      [[ "$line" == __APT__* ]] && continue
      echo "  - $line" >&2
    done <<< "$report"
    while IFS= read -r line; do
      [[ "$line" == __APT__* ]] || continue
      echo "" >&2
      echo "${line#__APT__ }" >&2
    done <<< "$report"
  }
  die "install the packages above on $REMOTE, then re-run"
}

check_remote_deps

# ---- detect whether a sync is needed ---------------------------------------
LOCAL_HEAD="$(git -C "$REPO" rev-parse HEAD)"
REMOTE_REV="$(ssh_cmd "cat '$REMOTE_BASE/.sync-rev' 2>/dev/null" || true)"
REMOTE_HAS_SCRIPT="$(ssh_cmd "test -f '$REMOTE_BASE/tools/run_eval.sh' && echo yes || echo no")"

needs_sync() {
  [[ "$FORCE_SYNC" -eq 1 ]] && return 0
  [[ "$REMOTE_HAS_SCRIPT" != "yes" ]] && return 0
  [[ "$REMOTE_REV" != "$LOCAL_HEAD" ]] && return 0
  return 1
}

# ---- rsync repo to remote --------------------------------------------------
sync_repo() {
  log "syncing $REPO -> $REMOTE:$REMOTE_BASE (rev ${LOCAL_HEAD:0:12})"
  ssh_cmd "mkdir -p '$REMOTE_BASE'"
  # Ship sources + .env; skip heavy/regenerable trees. .env is gitignored locally
  # but required on the remote for API keys and eval config.
  # "P" filters keep remote-only dirs (venv, reports, …) from being deleted by
  # --delete even though they are excluded from the transfer.
  rsync_cmd -az --delete \
    --filter 'P wheels/' \
    --filter 'P .venv/' \
    --filter 'P reports/' \
    --filter 'P tmp/' \
    --filter 'P vendor/' \
    --filter 'P .java_home' \
    --filter 'P .sync-rev' \
    --filter 'P remote_run_eval.pid' \
    --filter 'P remote_run_eval.launcher.log' \
    --exclude .git/ \
    --exclude .venv/ \
    --exclude __pycache__/ \
    --exclude .pytest_cache/ \
    --exclude .mypy_cache/ \
    --exclude .ruff_cache/ \
    --exclude reports/ \
    --exclude tmp/ \
    --exclude vendor/ \
    --exclude target/ \
    --exclude .jmhbench-cache/ \
    --exclude '*.log' \
    --exclude .DS_Store \
    "$REPO/" "$REMOTE:$REMOTE_BASE/"
  ssh_cmd "echo '$LOCAL_HEAD' > '$REMOTE_BASE/.sync-rev'"
  log "sync complete"
}

# ---- always sync .env (config + API keys change independently of git) ------
sync_env() {
  [[ -f "$REPO/.env" ]] || die "no .env found"
  log "syncing .env -> $REMOTE:$REMOTE_BASE/.env"
  ssh_cmd "mkdir -p '$REMOTE_BASE'"
  rsync_cmd -az "$REPO/.env" "$REMOTE:$REMOTE_BASE/.env"
}

# ---- optional: ship a pre-built linux wheel cache (slow; run once locally) --
WHEEL_CACHE="${REMOTE_WHEEL_DIR:-$REPO/.remote-wheels/py312}"

sync_wheels() {
  if [[ ! -d "$WHEEL_CACHE" ]] || [[ ! -f "$WHEEL_CACHE/.stamp" ]]; then
    log "building linux wheel cache locally (one-time; can take several minutes)"
    "$REPO/tools/cache_remote_wheels.sh"
  fi
  log "shipping wheels -> $REMOTE:$REMOTE_BASE/wheels/"
  ssh_cmd "mkdir -p '$REMOTE_BASE/wheels'"
  rsync_cmd -az "$WHEEL_CACHE/" "$REMOTE:$REMOTE_BASE/wheels/"
}

# ---- install / refresh dependencies on the remote --------------------------
ensure_deps() {
  log "ensuring remote dependencies"
  ssh_cmd "REMOTE_BASE='$REMOTE_BASE' JAVA_HOME_HINT='$JAVA_HOME_HINT' bash -s" <<'REMOTE_SETUP'
set -euo pipefail
cd "$REMOTE_BASE"

if ! command -v python3 >/dev/null; then
  echo "error: python3 not found on remote" >&2
  exit 1
fi

if [[ ! -f .venv/bin/activate ]]; then
  echo ">> creating venv"
  rm -rf .venv
  if ! python3 -m venv .venv; then
    echo "error: python3 -m venv failed — on Debian/Ubuntu run: sudo apt install python3-venv" >&2
    exit 1
  fi
fi
# shellcheck source=/dev/null
source .venv/bin/activate

echo ">> pip install -e '.[openai]'"
export PIP_DEFAULT_TIMEOUT="${PIP_DEFAULT_TIMEOUT:-600}"
pip_install_with_retry() {
  local -a opts=(--timeout "${PIP_DEFAULT_TIMEOUT}" --retries 15)
  if [[ -d wheels && -n "$(ls -A wheels 2>/dev/null)" ]]; then
    opts+=(--find-links wheels)
    echo ">> using cached wheels/ ($(ls wheels | wc -l) files)"
  fi
  local attempt
  for attempt in 1 2 3; do
    if pip install -q "${opts[@]}" -e '.[openai]'; then
      return 0
    fi
    echo ">> pip install failed (attempt $attempt/3), retrying in 30s ..." >&2
    sleep 30
  done
  echo "error: pip install failed after 3 attempts — VM may lack PyPI access" >&2
  return 1
}
pip install -q -U pip --timeout "${PIP_DEFAULT_TIMEOUT}" --retries 15
pip_install_with_retry
tools/fix_install.sh >/dev/null

resolve_java_home() {
  if [[ -n "${JAVA_HOME_HINT:-}" && -d "$JAVA_HOME_HINT" ]]; then
    echo "$JAVA_HOME_HINT"
    return 0
  fi
  if [[ -n "${JAVA_HOME:-}" && -d "$JAVA_HOME" ]]; then
    echo "$JAVA_HOME"
    return 0
  fi
  if command -v java >/dev/null; then
    local java_bin java_home
    java_bin="$(command -v java)"
    if [[ -L "$java_bin" ]]; then
      java_bin="$(readlink -f "$java_bin" 2>/dev/null || readlink "$java_bin")"
    fi
    java_home="$(cd "$(dirname "$java_bin")/.." && pwd)"
    if [[ -x "$java_home/bin/java" ]]; then
      echo "$java_home"
      return 0
    fi
  fi
  if command -v /usr/libexec/java_home >/dev/null 2>&1; then
    /usr/libexec/java_home 2>/dev/null && return 0
  fi
  local candidate
  for candidate in /usr/lib/jvm/java-21-openjdk-amd64 /usr/lib/jvm/java-21-openjdk \
                   /usr/lib/jvm/java-17-openjdk-amd64 /usr/lib/jvm/java-17-openjdk; do
    if [[ -d "$candidate" ]]; then
      echo "$candidate"
      return 0
    fi
  done
  return 1
}

if resolved="$(resolve_java_home)"; then
  export JAVA_HOME="$resolved"
  export PATH="$JAVA_HOME/bin:$PATH"
  echo "$resolved" > .java_home
  echo ">> JAVA_HOME=$JAVA_HOME"
elif [[ -n "${JAVA_HOME_HINT:-}" ]]; then
  echo "warn: JAVA_HOME_HINT=$JAVA_HOME_HINT not found — install a JDK or set JAVA_HOME" >&2
fi

if command -v java >/dev/null; then
  echo ">> $(java -version 2>&1 | head -1)"
else
  echo "warn: java not on PATH — install JDK 17+ for benchmarking" >&2
fi

if command -v mvn >/dev/null; then
  echo ">> $(mvn -version 2>&1 | head -1)"
  if [[ ! -x vendor/spotbugs/spotbugs-4.9.6/bin/spotbugs ]]; then
    echo ">> installing SpotJMHBugs (first run only)"
    tools/install_spotjmhbugs.sh
  fi
else
  echo "warn: mvn not on PATH — install Maven for benchmarking" >&2
fi

mkdir -p tmp
chmod +x tools/*.sh
REMOTE_SETUP
  log "remote dependencies ready"
}

# ---- prepare ----------------------------------------------------------------
if needs_sync; then
  sync_repo
else
  log "remote checkout up to date (${LOCAL_HEAD:0:12}) — skipping repo sync"
fi
sync_env
if [[ "$CACHE_WHEELS" -eq 1 ]]; then
  sync_wheels
fi
ensure_deps

if [[ -z "$JAVA_HOME_HINT" ]]; then
  JAVA_HOME_HINT="$(ssh_cmd "cat '$REMOTE_BASE/.java_home' 2>/dev/null" || true)"
fi
if [[ -n "$JAVA_HOME_HINT" ]]; then
  log "java   : $JAVA_HOME_HINT"
fi

if [[ "$SYNC_ONLY" -eq 1 ]]; then
  log "sync-only — remote is ready; not starting run_eval.sh"
  exit 0
fi

# ---- start run_eval.sh on the remote ---------------------------------------
# Pass through eval config from the local environment / .env. Force REMOTE=local
# so phase 2 does not try to ssh back out.
RUN_ENV=(
  REMOTE=local
  REMOTE_BASE="$REMOTE_BASE"
)
if [[ -n "$JAVA_HOME_HINT" ]]; then
  RUN_ENV+=(JAVA_HOME_HINT="$JAVA_HOME_HINT")
fi
for var in HARNESS TRACKS PROJECTS TEMPS THINKING PARALLEL MAX_TOKENS COMPILE_CHECK \
           SKIP_GEN SKIP_BENCH POLL_SECS MAX_WAIT_SECS RUN_DIR TMPDIR_OVERRIDE; do
  if [[ -n "${!var:-}" ]]; then
    RUN_ENV+=("$var=${!var}")
  fi
done

# Build a single quoted env prefix for the remote shell.
REMOTE_ENV=""
for kv in "${RUN_ENV[@]}"; do
  REMOTE_ENV+="$(printf '%q ' "$kv")"
done

LAUNCHER_LOG="$REMOTE_BASE/remote_run_eval.launcher.log"
PID_FILE="$REMOTE_BASE/remote_run_eval.pid"

if [[ "$FOREGROUND" -eq 1 ]]; then
  log "starting run_eval.sh in foreground on $REMOTE"
  ssh_cmd "cd '$REMOTE_BASE' && ${REMOTE_ENV}exec tools/run_eval.sh"
  exit $?
fi

log "starting run_eval.sh in background on $REMOTE"
# Subshell around nohup … & is required: without it, OpenSSH keeps the session open
# until the background job exits even though stdout/stderr are redirected.
ssh_cmd "cd '$REMOTE_BASE' && \
  ( nohup bash -c '${REMOTE_ENV}exec tools/run_eval.sh' \
      >>'$LAUNCHER_LOG' 2>&1 </dev/null & \
    echo \$! > '$PID_FILE' )"

PID="$(ssh_cmd "cat '$PID_FILE'")"
log "started (pid $PID on $REMOTE)"
print_monitor_cmds "$LAUNCHER_LOG"
