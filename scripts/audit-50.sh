#!/usr/bin/env bash
set -uo pipefail
REPO="${1:-.}"
cd "$REPO" || { echo "Repo nao encontrado: $REPO"; exit 1; }
APP="apps/android/app/src/main/java/com/vaultia/app"
PASS=0; WARN=0; FAIL=0
ok()  { echo "PASS - $1"; PASS=$((PASS+1)); }
bad() { echo "FAIL - $1"; FAIL=$((FAIL+1)); }
wrn() { echo "WARN - $1"; WARN=$((WARN+1)); }

echo "== 1. INTERNET permission =="
if grep -rq 'android.permission.INTERNET' apps/android/app/src/main/AndroidManifest.xml 2>/dev/null; then
  bad "INTERNET presente no Manifest"
else
  ok "INTERNET ausente do Manifest"
fi

echo "== 2. allowBackup =="
if grep -q 'android:allowBackup="false"' apps/android/app/src/main/AndroidManifest.xml 2>/dev/null; then
  ok "allowBackup=false"
else
  wrn "allowBackup nao esta false explicitamente"
fi

echo "== 3. Libs de rede nas dependencias =="
if grep -rqE 'retrofit|okhttp|volley|ktor-client' apps/android/app/build.gradle.kts apps/android/build.gradle.kts 2>/dev/null; then
  bad "Lib de rede encontrada nas dependencias"
else
  ok "Nenhuma lib de rede nas dependencias"
fi

echo "== 4. Storage real (SharedPreferences/Room/DataStore/SQLite) no codigo fonte =="
HITS=$(grep -rlE 'SharedPreferences|RoomDatabase|androidx\.room|DataStore|SQLiteOpenHelper' "$APP" --include='*.kt' 2>/dev/null | grep -v '/contract/')
if [[ -n "$HITS" ]]; then
  bad "Storage real encontrado fora dos contratos: $HITS"
else
  ok "Nenhum storage real fora dos contratos vazios"
fi

echo "== 5. Cripto real (Cipher/Keystore/AES) no codigo fonte =="
HITS=$(grep -rlE 'javax\.crypto\.Cipher|AndroidKeyStore|MasterKey' "$APP" --include='*.kt' 2>/dev/null | grep -v '/contract/')
if [[ -n "$HITS" ]]; then
  bad "API de cripto real encontrada fora dos contratos: $HITS"
else
  ok "Nenhuma API de cripto real fora dos contratos vazios"
fi

echo "== 6. Contratos vazios (CryptoService, SecureStorage, SessionManager, VaultRepository) sem metodos =="
for f in "core/security/contract/CryptoService.kt" "core/storage/contract/SecureStorage.kt" "core/session/contract/SessionManager.kt" "core/storage/contract/VaultRepository.kt"; do
  path="$APP/$f"
  if [[ -f "$path" ]]; then
    if grep -qE 'fun |val .*:.*=|var ' "$path"; then
      bad "$f parece ter membro declarado (deveria estar vazio)"
    else
      ok "$f permanece vazio"
    fi
  else
    wrn "$f nao encontrado no path esperado"
  fi
done

echo "== 7. VaultItem sem campos sensiveis prematuros =="
ITEM="$APP/core/model/vault/VaultItem.kt"
if [[ -f "$ITEM" ]]; then
  if grep -qiE 'password|secret|cipher|bytes|\bpath\b|\buri\b' "$ITEM"; then
    bad "VaultItem.kt contem termo sensivel"
  else
    ok "VaultItem.kt sem campos sensiveis"
  fi
else
  wrn "VaultItem.kt nao encontrado no path esperado"
fi

echo "== 8. AuthBoundaryNoticeView usado nas telas locked/unlocked =="
LOCKED="$APP/feature/vault/ui/LockedVaultScreen.kt"
UNLOCKED="$APP/feature/vault/ui/UnlockedVaultScreen.kt"
for f in "$LOCKED" "$UNLOCKED"; do
  if [[ -f "$f" ]]; then
    grep -q 'AuthBoundaryNoticeView' "$f" && ok "$(basename "$f") referencia AuthBoundaryNoticeView" || bad "$(basename "$f") NAO referencia AuthBoundaryNoticeView"
  else
    wrn "$(basename "$f") nao encontrado"
  fi
done

echo "== 9. Sem DI externo (Hilt/Dagger/Koin) =="
if grep -rqE 'Hilt|Dagger|Koin|@Inject|@Module' "$APP" --include='*.kt' 2>/dev/null; then
  bad "Referencia a DI externo encontrada"
else
  ok "Sem DI externo"
fi

echo "== 10. Segredos hardcoded / chaves no codigo =="
HITS=$(grep -rnE '(AKIA[0-9A-Z]{16})|(-----BEGIN [A-Z ]*PRIVATE KEY-----)' "$APP" --include='*.kt' 2>/dev/null)
if [[ -n "$HITS" ]]; then
  bad "Possivel segredo hardcoded: $HITS"
else
  ok "Nenhum segredo hardcoded encontrado"
fi

echo "== 11. Segredos no historico git (todos os branches) =="
HITS=$(git log --all -p 2>/dev/null | grep -E -o '(AKIA[0-9A-Z]{16})|(-----BEGIN [A-Z ]*PRIVATE KEY-----)' | sort -u | head -20)
if [[ -n "$HITS" ]]; then
  bad "Segredo encontrado no historico git: $HITS"
else
  ok "Nenhum segredo encontrado no historico git"
fi

echo "== 12. Working tree limpo =="
if [[ -z "$(git status --porcelain 2>/dev/null)" ]]; then
  ok "Working tree limpo"
else
  wrn "Existem alteracoes nao commitadas"
fi

echo "== 13. gradlew presente =="
if [[ -f "apps/android/gradlew" || -f "gradlew" ]]; then
  ok "gradlew presente"
else
  wrn "gradlew ausente — build nao reproduzivel sem Gradle global"
fi

echo ""
echo "===================================="
echo "RESUMO: PASS=$PASS WARN=$WARN FAIL=$FAIL"
echo "===================================="
