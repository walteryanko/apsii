#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
compilar() {
    if command -v javac >/dev/null 2>&1; then
        javac -encoding UTF-8 "$@"
    else
        java com.sun.tools.javac.Main -encoding UTF-8 "$@"
    fi
}
mkdir -p out/original out/refatorado
compilar -d out/original original/Locadora/*.java
compilar -d out/refatorado Locadora/*.java testes/LocadoraTest.java
java -cp out/refatorado LocadoraTest out/original
