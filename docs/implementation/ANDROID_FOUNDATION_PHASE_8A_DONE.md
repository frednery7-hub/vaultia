# Vaultia – Android Foundation Phase 8A DONE

## Decisão

A Fundação Android Fase 8A foi concluída com sucesso.

## Data

2026-06-24

## Objetivo

Preparar o projeto para operar com caches e futuro emulador/AVD no pen drive, reduzindo uso do armazenamento interno do computador.

## Resultado

Foram criados:

- `.android-home/`
- `.android-avd/`
- `.gradle-user-home/`
- `tools/android/`

Foram criados os scripts:

- `scripts/android-usb-env.sh`
- `scripts/android-usb-run.sh`

## Ambiente USB

O runner configura:

- `ANDROID_USER_HOME` no pen drive;
- `ANDROID_AVD_HOME` no pen drive;
- `GRADLE_USER_HOME` no pen drive;
- `ANDROID_HOME` apontando para o SDK já instalado.

## Observação

O Android SDK principal ainda está no armazenamento interno do Mac.

O AVD/emulador do projeto deverá ser criado na próxima fase usando:

- `ANDROID_AVD_HOME=<local-android-avd>`

## Validações Executadas

Foi executado:

- `./scripts/android-usb-run.sh ./scripts/check-android.sh`

Resultado esperado:

- `VAULTIA_ANDROID_CHECK_OK`

## Segurança Mantida

A fase confirmou:

- Manifest fonte sem `android.permission.INTERNET`;
- APK binário sem `android.permission.INTERNET`;
- diretórios locais USB fora do Git;
- nenhum arquivo perigoso no Git status.

## Escopo Não Implementado

Esta fase não implementou:

- cofre real;
- Master Password;
- KDF;
- AES-GCM;
- Android Keystore;
- banco local;
- storage criptografado;
- biometria;
- backup;
- rede;
- permissão INTERNET.

## Status

- **Status:** Concluído
- **Próximo passo:** criar AVD no pen drive e realizar smoke test visual
