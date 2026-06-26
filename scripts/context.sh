#!/usr/bin/env bash
set -uo pipefail
REPO="${1:-.}"
cd "$REPO" || { echo "Repo não encontrado: $REPO"; exit 1; }

echo "============================================================"
echo "1. ESTRUTURA GERAL DO PROJETO (3 níveis)"
echo "============================================================"
find . -maxdepth 4 -not -path '*/build/*' -not -path '*/.git/*' -not -path '*/.gradle/*' | sort

echo ""
echo "============================================================"
echo "2. TODOS OS ARQUIVOS .kt"
echo "============================================================"
find . -name "*.kt" -not -path '*/build/*' | sort

echo ""
echo "============================================================"
echo "3. CONTEÚDO DO MainActivity.kt"
echo "============================================================"
find . -iname "MainActivity.kt" -not -path '*/build/*' -exec cat {} \;

echo ""
echo "============================================================"
echo "4. CONTEÚDO do VaultiaAppContainer"
echo "============================================================"
find . -iname "VaultiaAppContainer.kt" -not -path '*/build/*' -exec cat {} \;

echo ""
echo "============================================================"
echo "5. CONTEÚDO do VaultSessionState / InMemorySessionManager"
echo "============================================================"
find . -iname "VaultSessionState.kt" -o -iname "InMemorySessionManager.kt" -not -path '*/build/*' | while read -r f; do
  echo "--- $f ---"
  cat "$f"
done

echo ""
echo "============================================================"
echo "6. CONTEÚDO do VaultItem / VaultItemType"
echo "============================================================"
find . -iname "VaultItem.kt" -o -iname "VaultItemType.kt" -not -path '*/build/*' | while read -r f; do
  echo "--- $f ---"
  cat "$f"
done

echo ""
echo "============================================================"
echo "7. CONTEÚDO do InMemoryVaultRepository"
echo "============================================================"
find . -iname "InMemoryVaultRepository.kt" -not -path '*/build/*' -exec cat {} \;

echo ""
echo "============================================================"
echo "8. CONTEÚDO de telas de cofre"
echo "============================================================"
for name in "LockedVaultScreen.kt" "UnlockedVaultScreen.kt" "VaultHomeShell.kt" "AuthBoundaryNoticeView.kt" "VaultEmptyStateView.kt"; do
  find . -iname "$name" -not -path '*/build/*' | while read -r f; do
    echo "--- $f ---"
    cat "$f"
  done
done

echo ""
echo "============================================================"
echo "9. CONTEÚDO do sistema de navegação"
echo "============================================================"
for name in "VaultDestination.kt" "InMemoryVaultNavigator.kt"; do
  find . -iname "$name" -not -path '*/build/*' | while read -r f; do
    echo "--- $f ---"
    cat "$f"
  done
done

echo ""
echo "============================================================"
echo "10. CONTEÚDO do seed demo"
echo "============================================================"
find . -iname "DemoVaultMetadataSeed.kt" -not -path '*/build/*' -exec cat {} \;

echo ""
echo "============================================================"
echo "11. build.gradle (dependências atuais)"
echo "============================================================"
find . -name "build.gradle" -o -name "build.gradle.kts" | grep -v '/build/' | while read -r f; do
  echo "--- $f ---"
  cat "$f"
done

echo ""
echo "============================================================"
echo "12. AndroidManifest.xml atual"
echo "============================================================"
find . -name "AndroidManifest.xml" -not -path '*/build/*' -exec cat {} \;

echo ""
echo "============================================================"
echo "13. minSdk / targetSdk / compileSdk"
echo "============================================================"
grep -RnE 'minSdk|targetSdk|compileSdk' --include='build.gradle' --include='build.gradle.kts' . 2>/dev/null | grep -v '/build/'

echo ""
echo "============================================================"
echo "14. LISTA DE TESTES EXISTENTES"
echo "============================================================"
find . -path '*/test/*' -name "*.kt" -not -path '*/build/*' | sort

echo ""
echo "============================================================"
echo "15. CONTEÚDO de testes de arquitetura"
echo "============================================================"
find . -path '*/test/*' -iname "*Architecture*" -not -path '*/build/*' | while read -r f; do
  echo "--- $f ---"
  cat "$f"
done

echo ""
echo "============================================================"
echo "16. ROADMAP / DOCUMENTAÇÃO existente"
echo "============================================================"
find . -maxdepth 3 -iname "*.md" -not -path '*/build/*' | sort

echo ""
echo "============================================================"
echo "17. ÚLTIMOS 20 COMMITS"
echo "============================================================"
git log -20 --format="%h | %ad | %s" --date=short 2>/dev/null

echo ""
echo "============================================================"
echo "18. VERSÕES DE FERRAMENTAS"
echo "============================================================"
echo "-- Java --"
java -version 2>&1
echo "-- Gradle wrapper --"
[ -f "./gradlew" ] && ./gradlew --version 2>&1 | head -20 || echo "gradlew não encontrado"

echo ""
echo "============================================================"
echo "FIM DO CONTEXTO"
echo "============================================================"
