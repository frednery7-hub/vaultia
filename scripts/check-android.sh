#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ANDROID_DIR="$ROOT_DIR/apps/android"

echo ""
echo "=== Vaultia Android Check ==="

cd "$ANDROID_DIR"

echo ""
echo "1) Gradle version:"
./gradlew --version | head -20

echo ""
echo "2) Clean build:"
./gradlew clean :app:assembleDebug

echo ""
echo "3) Manifest source INTERNET audit:"
cd "$ROOT_DIR"

if grep -R "android.permission.INTERNET" apps/android/app/src/main/AndroidManifest.xml; then
  echo "ERRO: Manifest contém android.permission.INTERNET"
  exit 1
else
  echo "OK: Manifest sem android.permission.INTERNET"
fi

echo ""
echo "4) APK binary INTERNET audit:"
APK="apps/android/app/build/outputs/apk/debug/app-debug.apk"
AAPT="$(find "$ANDROID_HOME/build-tools" -type f -name aapt | sort | tail -1)"

if [ -z "$AAPT" ]; then
  echo "ERRO: aapt não encontrado"
  exit 1
fi

if "$AAPT" dump permissions "$APK" | grep -i "android.permission.INTERNET"; then
  echo "ERRO: APK contém android.permission.INTERNET"
  exit 1
else
  echo "OK: APK sem android.permission.INTERNET"
fi

echo ""
echo "5) Git dangerous files audit:"
if git status --short | grep -E "local.properties|\.apk$|\.aab$|\.apks$|\.idsig$|\.jks$|\.keystore$|/build/|\.gradle/|\.env"; then
  echo "ERRO: arquivo perigoso apareceu no Git status"
  exit 1
else
  echo "OK: nenhum arquivo perigoso apareceu no Git status"
fi

echo ""
echo "6) APK SHA-256:"
shasum -a 256 "$APK"

echo ""
echo "VAULTIA_ANDROID_CHECK_OK"
