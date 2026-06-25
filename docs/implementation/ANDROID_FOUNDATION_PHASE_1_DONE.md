# Vaultia – Android Foundation Phase 1 DONE

## Decisão

A Fundação Android Fase 1 foi concluída com sucesso.

## Data

2026-06-24

## Resultado

A base Android mínima do Vaultia foi criada manualmente via VS Code/terminal, sem uso do Android Studio para geração do projeto.

Android Studio permanece reservado para emulador e SDK.

## Estrutura Criada

Projeto Android:

- `apps/android`

Arquivos principais:

- `settings.gradle.kts`
- `build.gradle.kts`
- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`
- `local.properties`
- `app/build.gradle.kts`
- `app/proguard-rules.pro`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/vaultia/app/MainActivity.kt`
- `app/src/main/java/com/vaultia/app/VaultiaApp.kt`

## Stack Técnica Inicial

- Kotlin
- Android Gradle Plugin
- Gradle Wrapper
- Java 17
- Android SDK 35
- minSdk 26
- targetSdk 35
- package `com.vaultia.app`

## Artefato Gerado

APK debug:

- `apps/android/app/build/outputs/apk/debug/app-debug.apk`

SHA-256:

- `2420130796f8ca442e78fe9fd4b376a9d1c78ff7fa66a5b36741e3aac5ad3094`

## Auditorias Executadas

A fase foi validada com:

- build debug bem-sucedido;
- APK debug gerado;
- Manifest fonte sem `uses-permission`;
- Manifest fonte sem `android.permission.INTERNET`;
- APK binário auditado com `aapt`;
- APK binário sem `android.permission.INTERNET`;
- package correto `com.vaultia.app`;
- versão inicial `0.1.0`;
- código Kotlin mínimo validado.

## Escopo Implementado

Implementado nesta fase:

- fundação Android mínima;
- app module;
- MainActivity neutra;
- Application class mínima;
- Manifest mínimo;
- bloqueio de Android backup automático;
- ausência de permissão INTERNET;
- build debug funcional.

## Escopo Não Implementado

Não foi implementado nesta fase:

- cofre real;
- Master Password;
- KDF;
- AES-GCM;
- Android Keystore;
- banco local;
- storage seguro;
- backup;
- biometria;
- clipboard;
- importação de arquivos;
- exportação;
- auditoria local;
- logs técnicos definitivos;
- UI sensível.

## Observações Técnicas

Durante a criação, foram corrigidos:

- incompatibilidade entre Gradle 9.6.0 e Android Gradle Plugin 8.x;
- wrapper ajustado para Gradle 9.5.0;
- referências a ícones inexistentes removidas do Manifest;
- incompatibilidade JVM entre Java e Kotlin corrigida para Java 17/Kotlin 17.

Warnings existentes:

- `kotlinOptions` está deprecado e deverá ser migrado futuramente para `compilerOptions`.

Este warning não bloqueia a Fundação Android Fase 1.

## Status

- **Status:** Concluído
- **Próximo passo:** Fundação Android Fase 2 — higiene de projeto, gitignore, auditoria de generated files e abertura no VS Code
