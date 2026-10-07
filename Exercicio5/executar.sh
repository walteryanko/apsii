#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p out/refatorado
if command -v javac >/dev/null 2>&1; then
    javac -encoding UTF-8 -d out/refatorado Locadora/*.java
else
    java com.sun.tools.javac.Main -encoding UTF-8 -d out/refatorado Locadora/*.java
fi
java -cp out/refatorado Locadora
