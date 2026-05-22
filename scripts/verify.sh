#!/usr/bin/env bash
set -euo pipefail

echo "Running IgorBot verification pipeline..."
echo "[1/2] Running tests"
mvn -B -ntp test

echo "[2/2] Running package build"
mvn -B -ntp clean package

echo "Verification completed successfully."
