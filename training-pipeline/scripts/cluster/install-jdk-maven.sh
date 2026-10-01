#!/usr/bin/env bash
# Install a user-local JDK 17 (Temurin) + Maven under $JMH_ROOT/tools/.
# Run on the HPI login node (rx01 / hpi2), NOT on a GPU compute node.
#
#   export JMH_ROOT=/sc/scratch/zongxiong.chen/jonas
#   ./scripts/cluster/install-jdk-maven.sh
#
# Idempotent: skips download/extract if jdk-17/ and maven/ already exist.
set -euo pipefail

JMH_ROOT="${JMH_ROOT:-/sc/scratch/zongxiong.chen/jonas}"
TOOLS="${JMH_ROOT}/tools"
JDK_DIR="${TOOLS}/jdk-17"
MAVEN_DIR="${TOOLS}/maven"
MAVEN_VERSION="${MAVEN_VERSION:-3.9.16}"

mkdir -p "${TOOLS}"
cd "${TOOLS}"

if [[ -x "${JDK_DIR}/bin/java" ]]; then
  echo "JDK already installed at ${JDK_DIR}"
else
  echo "==> downloading Temurin JDK 17 (linux x64)"
  curl -fL \
    "https://api.adoptium.net/v3/binary/latest/17/ga/linux/x64/jdk/hotspot/normal/eclipse?project=jdk" \
    -o temurin17.tar.gz
  rm -rf "${TOOLS}/.jdk-extract"
  mkdir -p "${TOOLS}/.jdk-extract"
  tar -xzf temurin17.tar.gz -C "${TOOLS}/.jdk-extract"
  rm -f temurin17.tar.gz
  extracted="$(find "${TOOLS}/.jdk-extract" -maxdepth 1 -type d -name 'jdk-17*' | head -1)"
  if [[ -z "${extracted}" ]]; then
    echo "error: could not find extracted jdk-17* directory" >&2
    exit 1
  fi
  rm -rf "${JDK_DIR}"
  mv "${extracted}" "${JDK_DIR}"
  rmdir "${TOOLS}/.jdk-extract" 2>/dev/null || rm -rf "${TOOLS}/.jdk-extract"
  # GPFS/DSS drops execute bits on tar extract; restore them across the WHOLE JDK, not just
  # bin/. lib/jspawnhelper matters as much as bin/java: posix_spawn uses it for every
  # subprocess, so without +x any Java fork fails with
  #   Cannot run program ".../bin/java": error=13, Permission denied
  # -- naming bin/java, not the helper. JMH forks a JVM per benchmark, so the symptom is that
  # compile succeeds while runtime/rsd/mutation are silently 0 forever (seen on LRZ, 2026-07-29).
  chmod -R u+x "${JDK_DIR}/bin" "${JDK_DIR}/lib"
  echo "installed JDK -> ${JDK_DIR}"
fi

# Repair an existing install too: jspawnhelper is the one that bites, and bin/java being
# executable says nothing about it (that was exactly the LRZ failure).
if [[ ! -x "${JDK_DIR}/bin/java" || ! -x "${JDK_DIR}/lib/jspawnhelper" ]]; then
  chmod -R u+x "${JDK_DIR}/bin" "${JDK_DIR}/lib"
fi

if [[ -x "${MAVEN_DIR}/bin/mvn" ]]; then
  echo "Maven already installed at ${MAVEN_DIR}"
else
  echo "==> downloading Apache Maven ${MAVEN_VERSION}"
  curl -fL \
    "https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" \
    -o "apache-maven-${MAVEN_VERSION}-bin.tar.gz"
  rm -rf "${TOOLS}/.maven-extract"
  mkdir -p "${TOOLS}/.maven-extract"
  tar -xzf "apache-maven-${MAVEN_VERSION}-bin.tar.gz" -C "${TOOLS}/.maven-extract"
  rm -f "apache-maven-${MAVEN_VERSION}-bin.tar.gz"
  extracted="$(find "${TOOLS}/.maven-extract" -maxdepth 1 -type d -name 'apache-maven-*' | head -1)"
  if [[ -z "${extracted}" ]]; then
    echo "error: could not find extracted apache-maven-* directory" >&2
    exit 1
  fi
  rm -rf "${MAVEN_DIR}"
  mv "${extracted}" "${MAVEN_DIR}"
  rmdir "${TOOLS}/.maven-extract" 2>/dev/null || rm -rf "${TOOLS}/.maven-extract"
  chmod -R u+x "${MAVEN_DIR}/bin"
  echo "installed Maven -> ${MAVEN_DIR}"
fi

if [[ ! -x "${MAVEN_DIR}/bin/mvn" ]]; then
  chmod -R u+x "${MAVEN_DIR}/bin"
fi

export JAVA_HOME="${JDK_DIR}"
export PATH="${JAVA_HOME}/bin:${MAVEN_DIR}/bin:${PATH}"
export MAVEN_OPTS="-Dmaven.repo.local=${JMH_ROOT}/.m2/repository"

echo ""
echo "Verify:"
java -version
mvn -version
echo ""
echo "Add to your job script or source scripts/cluster/env.hpi.sh:"
echo "  export JMH_ROOT=${JMH_ROOT}"
echo "  export JAVA_HOME=${JDK_DIR}"
echo "  export PATH=\"\${JAVA_HOME}/bin:${MAVEN_DIR}/bin:\${HOME}/.local/bin:\${PATH}\""
echo "  export MAVEN_OPTS=\"-Dmaven.repo.local=${JMH_ROOT}/.m2/repository\""
