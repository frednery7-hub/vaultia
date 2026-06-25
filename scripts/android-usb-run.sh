#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export ANDROID_USER_HOME="$ROOT_DIR/.android-home"
export ANDROID_AVD_HOME="$ROOT_DIR/.android-avd"
export GRADLE_USER_HOME="$ROOT_DIR/.gradle-user-home"
export TMPDIR="$ROOT_DIR/.tmp"

mkdir -p "$ANDROID_USER_HOME"
mkdir -p "$ANDROID_AVD_HOME"
mkdir -p "$GRADLE_USER_HOME"
mkdir -p "$TMPDIR"

if [ "$#" -eq 0 ]; then
  echo "ANDROID_HOME=$ANDROID_HOME"
  echo "ANDROID_SDK_ROOT=$ANDROID_SDK_ROOT"
  echo "ANDROID_USER_HOME=$ANDROID_USER_HOME"
  echo "ANDROID_AVD_HOME=$ANDROID_AVD_HOME"
  echo "GRADLE_USER_HOME=$GRADLE_USER_HOME"
  echo "TMPDIR=$TMPDIR"
  exit 0
fi

exec "$@"
