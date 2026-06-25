# Vaultia – Android Foundation Phase 4 PLAN

## Objetivo

Estabelecer uma baseline mínima de qualidade para o projeto Android antes de qualquer implementação sensível.

## Escopo Permitido

Esta fase permite:

- remover warnings próprios de configuração Gradle/Kotlin;
- criar script de verificação reproduzível;
- validar build limpo;
- auditar Manifest sem INTERNET;
- auditar APK binário sem INTERNET;
- auditar Git status contra arquivos perigosos.

## Escopo Bloqueado

Esta fase não permite implementar:

- cofre real;
- Master Password;
- KDF;
- AES-GCM;
- Android Keystore;
- banco local;
- storage criptografado;
- biometria;
- backup;
- clipboard;
- importação;
- exportação;
- UI sensível.

## Arquivos Alterados/Criados

- `apps/android/app/build.gradle.kts`
- `scripts/check-android.sh`
- `docs/implementation/ANDROID_FOUNDATION_PHASE_4_PLAN.md`

## Critérios de Aceite

A fase só pode ser fechada se:

- `./scripts/check-android.sh` passar;
- build debug passar;
- Manifest permanecer sem `android.permission.INTERNET`;
- APK binário permanecer sem `android.permission.INTERNET`;
- Git status não listar arquivos perigosos;
- working tree conter apenas arquivos esperados da Fase 4.

## Status

- **Status:** Planejado
- **Próximo passo:** executar script de verificação
