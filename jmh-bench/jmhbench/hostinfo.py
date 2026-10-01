"""Machine identity, captured at the moment a benchmark run starts.

Bug size is a within-pair ratio on one host, so raw machine speed cancels out.
Three things do not: the host's *noise floor* (bug size is ``1 - U`` of a
confidence interval, so a noisier box yields fewer kills at the same true
effect), the JVM ergonomics the machine selects when nothing is pinned, and
every absolute-time metric. None of that can be attributed to a host after the
fact unless the host was recorded while it ran.

That has already cost one result in this project: a run whose mutation score
moved 0.18 -> 0.12 lists three confounds, and the benchmarking box had also
changed from ``fsc04`` to ``bsc-gcp``. The machine was a fourth confound and
cannot be recovered. This module exists so that cannot happen again
(EVALUATION_ISSUES.md B5).

Everything here is best-effort: a probe that fails records ``None`` for that
field rather than failing the run. ``collect()`` never raises.
"""

from __future__ import annotations

import os
import platform
import re
import shutil
import socket
import subprocess
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

_TIMEOUT = 5


def _sh(cmd: list[str]) -> str | None:
    """Run a probe command, returning stripped stdout or None."""
    if not shutil.which(cmd[0]):
        return None
    try:
        proc = subprocess.run(
            cmd, capture_output=True, text=True, timeout=_TIMEOUT, check=False
        )
    except (OSError, subprocess.SubprocessError):
        return None
    if proc.returncode != 0:
        return None
    out = (proc.stdout or "").strip()
    return out or None


def _read(path: str) -> str | None:
    try:
        return Path(path).read_text(encoding="utf-8", errors="replace").strip() or None
    except OSError:
        return None


def _int(value: Any) -> int | None:
    try:
        return int(str(value).strip())
    except (TypeError, ValueError):
        return None


# ---------------------------------------------------------------------------
# CPU / memory
# ---------------------------------------------------------------------------

def _cpu_linux() -> dict[str, Any]:
    info: dict[str, Any] = {}
    cpuinfo = _read("/proc/cpuinfo") or ""
    for line in cpuinfo.splitlines():
        if line.lower().startswith("model name") and "model" not in info:
            info["model"] = line.split(":", 1)[1].strip()
            break

    lscpu = _sh(["lscpu"]) or ""
    fields = {}
    for line in lscpu.splitlines():
        if ":" in line:
            k, v = line.split(":", 1)
            fields[k.strip()] = v.strip()
    info.setdefault("model", fields.get("Model name"))
    info["sockets"] = _int(fields.get("Socket(s)"))
    info["cores_per_socket"] = _int(fields.get("Core(s) per socket"))
    info["threads_per_core"] = _int(fields.get("Thread(s) per core"))
    info["logical_cpus"] = _int(fields.get("CPU(s)")) or os.cpu_count()
    if info["sockets"] and info["cores_per_socket"]:
        info["physical_cores"] = info["sockets"] * info["cores_per_socket"]
    info["cpu_mhz"] = fields.get("CPU MHz")
    info["cpu_max_mhz"] = fields.get("CPU max MHz")

    # SMT / hyper-threading: /sys is authoritative, lscpu is the fallback.
    smt = _read("/sys/devices/system/cpu/smt/control")
    if smt is None and info.get("threads_per_core") is not None:
        smt = "on" if info["threads_per_core"] > 1 else "off"
    info["smt"] = smt

    # Frequency governor and turbo. Both move the noise floor, and both are
    # per-machine BIOS/OS policy rather than a property of the CPU model.
    info["governor"] = _read(
        "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor"
    )
    no_turbo = _read("/sys/devices/system/cpu/intel_pstate/no_turbo")
    if no_turbo is not None:
        info["turbo"] = "off" if no_turbo == "1" else "on"
    else:
        boost = _read("/sys/devices/system/cpu/cpufreq/boost")
        info["turbo"] = ("on" if boost == "1" else "off") if boost is not None else None
    return info


def _cpu_darwin() -> dict[str, Any]:
    def sysctl(key: str) -> str | None:
        return _sh(["sysctl", "-n", key])

    threads = _int(sysctl("hw.logicalcpu"))
    cores = _int(sysctl("hw.physicalcpu"))
    return {
        "model": sysctl("machdep.cpu.brand_string"),
        "sockets": _int(sysctl("hw.packages")) or 1,
        "physical_cores": cores,
        "logical_cpus": threads or os.cpu_count(),
        "threads_per_core": (threads // cores) if (threads and cores) else None,
        "smt": ("on" if (threads and cores and threads > cores) else "off"),
        # macOS exposes neither a cpufreq governor nor a turbo switch.
        "governor": None,
        "turbo": None,
        "cpu_max_mhz": sysctl("hw.cpufrequency_max"),
    }


def _memory_bytes() -> int | None:
    if platform.system() == "Linux":
        meminfo = _read("/proc/meminfo") or ""
        m = re.search(r"^MemTotal:\s+(\d+)\s+kB", meminfo, re.MULTILINE)
        if m:
            return int(m.group(1)) * 1024
    if platform.system() == "Darwin":
        return _int(_sh(["sysctl", "-n", "hw.memsize"]))
    try:
        return os.sysconf("SC_PAGE_SIZE") * os.sysconf("SC_PHYS_PAGES")
    except (ValueError, OSError, AttributeError):
        return None


# ---------------------------------------------------------------------------
# JVM
# ---------------------------------------------------------------------------

def _jdk() -> dict[str, Any]:
    """The JDK that will actually run JMH, not whatever is first on PATH."""
    try:
        from jmhbench.build import java_executable, resolve_java_home
        exe = java_executable()
        home = resolve_java_home()
    except Exception:  # noqa: BLE001 -- provenance must never break a run
        exe, home = "java", os.environ.get("JAVA_HOME")

    version = None
    if shutil.which(exe) or Path(exe).exists():
        try:
            proc = subprocess.run(
                [exe, "-version"], capture_output=True, text=True,
                timeout=_TIMEOUT, check=False,
            )
            # `java -version` writes to stderr.
            version = ((proc.stderr or "") + (proc.stdout or "")).strip() or None
        except (OSError, subprocess.SubprocessError):
            version = None
    return {
        "java_home": home,
        "java_executable": exe,
        # The full multi-line build string: vendor, build number and VM name all
        # matter, and "17" alone does not identify a JIT.
        "version_string": version,
        "available_processors": os.cpu_count(),
    }


# ---------------------------------------------------------------------------

def _cpu_affinity() -> dict[str, Any]:
    """Which logical CPUs this process may run on, and how fast each one is.

    Two separate reasons this has to be recorded, not inferred:

    *Affinity* -- the run may be pinned (``taskset``) to a subset of the box.
    ``logical_cpus`` then describes the machine but not the measurement, and
    the JVM sizes ``ParallelGCThreads`` / ``CICompilerCount`` off the *mask*,
    so two runs on one host are not comparable unless the mask matches.

    *Heterogeneity* -- on a hybrid part the cores are not interchangeable. The
    Ryzen 5 PRO 8500GE (Phoenix 2) pairs 2 Zen 4 cores with 4 Zen 4c cores at
    ACPI CPPC ``highest_perf`` 196 vs 144: a 36% spread, against a kill
    threshold of 1.10. A baseline scheduled on a fast core and its mutant on a
    dense one differ by more than three times the effect being tested, in
    whichever direction the scheduler happened to pick. ``perf_classes`` makes
    that visible after the fact; a mask covering only one class rules it out.
    """
    info: dict[str, Any] = {}
    try:
        cpus = sorted(os.sched_getaffinity(0))
        info["affinity"] = cpus
        info["affinity_count"] = len(cpus)
    except (OSError, AttributeError):
        cpus = []
        info["affinity"] = None
        info["affinity_count"] = None

    # ACPI CPPC highest_perf is the per-core speed rating the firmware reports;
    # on a homogeneous part every core carries the same value.
    perf: dict[int, int] = {}
    for cpu in range(os.cpu_count() or 0):
        raw = _read(f"/sys/devices/system/cpu/cpu{cpu}/acpi_cppc/highest_perf")
        if raw is None:
            raw = _read(f"/sys/devices/system/cpu/cpu{cpu}/cpu_capacity")
        val = _int(raw)
        if val is not None:
            perf[cpu] = val
    if perf:
        classes: dict[int, list[int]] = {}
        for cpu, val in sorted(perf.items()):
            classes.setdefault(val, []).append(cpu)
        info["perf_classes"] = {str(k): v for k, v in sorted(classes.items(), reverse=True)}
        info["heterogeneous"] = len(classes) > 1
        if cpus:
            masked = {perf[c] for c in cpus if c in perf}
            # The property that actually matters: every CPU the run can land on
            # has the same rating, so scheduling cannot bias a within-pair ratio.
            info["affinity_uniform_perf"] = len(masked) == 1
            info["affinity_perf_class"] = next(iter(masked)) if len(masked) == 1 else None
    else:
        info["perf_classes"] = None
        info["heterogeneous"] = None
    return info


def collect() -> dict[str, Any]:
    """Everything needed to say which machine produced a number.

    Never raises: any probe that fails contributes ``None``. Call this at the
    *start* of a run -- ``load_average`` is a point-in-time reading and is only
    meaningful if it was taken before the benchmarks loaded the box.
    """
    info: dict[str, Any] = {
        "recorded_at": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
        "hostname": None,
        "fqdn": None,
        "platform": platform.platform(),
        "system": platform.system(),
        "release": platform.release(),
        "kernel": None,
        "machine": platform.machine(),
        "python": platform.python_version(),
    }
    try:
        info["hostname"] = socket.gethostname()
        info["fqdn"] = socket.getfqdn()
    except OSError:
        pass

    info["kernel"] = _sh(["uname", "-a"])

    system = platform.system()
    if system == "Linux":
        info["cpu"] = _cpu_linux()
        info["cpu"].update(_cpu_affinity())
    elif system == "Darwin":
        info["cpu"] = _cpu_darwin()
    else:
        info["cpu"] = {"model": platform.processor() or None, "logical_cpus": os.cpu_count()}

    mem = _memory_bytes()
    info["memory_bytes"] = mem
    info["memory_gib"] = round(mem / (1024 ** 3), 2) if mem else None

    try:
        one, five, fifteen = os.getloadavg()
        info["load_average"] = {"1m": round(one, 2), "5m": round(five, 2), "15m": round(fifteen, 2)}
    except (OSError, AttributeError):
        info["load_average"] = None

    info["jdk"] = _jdk()

    # Co-tenancy: a VM or container shares the box with tenants we cannot see,
    # which is exactly the situation where two "identical" hosts are not.
    info["virtualisation"] = _sh(["systemd-detect-virt"]) or None
    info["container"] = bool(
        Path("/.dockerenv").exists()
        or (_read("/proc/1/cgroup") or "").find("docker") >= 0
    )
    return info


def short_label(info: dict[str, Any] | None) -> str:
    """One-line host identity for report tables."""
    if not info:
        return "not recorded"
    host = info.get("hostname") or "unknown-host"
    cpu = (info.get("cpu") or {}).get("model") or "unknown CPU"
    n = (info.get("cpu") or {}).get("logical_cpus")
    mem = info.get("memory_gib")
    bits = [host, cpu]
    if n:
        bits.append(f"{n} vCPU")
    if mem:
        bits.append(f"{mem:g} GiB")
    cpu_info = info.get("cpu") or {}
    # A pinned run is a different measurement setup from an unpinned one on the
    # same box; the label has to say so or the two look identical in a table.
    pinned = cpu_info.get("affinity_count")
    if pinned and n and pinned != n:
        cls = cpu_info.get("affinity_perf_class")
        bits.append(f"pinned {pinned}/{n}" + (f" @perf{cls}" if cls else ""))
    if cpu_info.get("heterogeneous") and cpu_info.get("affinity_uniform_perf") is False:
        bits.append("MIXED-CORE")
    return " / ".join(bits)


if __name__ == "__main__":  # pragma: no cover
    import json
    print(json.dumps(collect(), indent=2))
