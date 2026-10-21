#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/backend"
if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven is not installed or not on PATH. Install Maven 3.9+ and Java 17+, then retry." >&2
  exit 1
fi
exec mvn spring-boot:run
