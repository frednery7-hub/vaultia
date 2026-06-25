# Vaultia – Android Foundation Phase 8A PLAN

## Objetivo

Preparar o projeto para usar armazenamento local no pen drive durante builds, checks e futura execução de emulador.

## Contexto

O computador possui pouco espaço livre no armazenamento interno. Por isso, caches e AVDs do projeto devem ser mantidos no pen drive sempre que possível.

## Escopo Permitido

Esta fase permite:

- criar diretórios locais USB para Android runtime;
- criar diretório USB para AVDs;
- criar diretório USB para cache Gradle do projeto;
- criar scripts de ambiente;
- atualizar `.gitignore`;
- validar build/check sem INTERNET.

## Escopo Bloqueado

Esta fase não cria:

- cofre real;
- Master Password;
- KDF;
- criptografia;
- banco local;
- storage seguro;
- biometria;
- backup;
- rede;
- permissão INTERNET.

## Diretórios USB

- `.android-home/`
- `.android-avd/`
- `.gradle-user-home/`
- `tools/android/`

Todos devem permanecer fora do Git.

## Scripts

- `scripts/android-usb-env.sh`
- `scripts/android-usb-run.sh`

## Critérios de Aceite

A fase só pode ser fechada se:

- diretórios USB forem criados;
- diretórios USB forem ignorados pelo Git;
- `./scripts/android-usb-run.sh ./scripts/check-android.sh` passar;
- Manifest continuar sem INTERNET;
- APK binário continuar sem INTERNET;
- nenhum cache/AVD/artefato local aparecer no Git status.

## Status

- **Status:** Planejado
- **Próximo passo:** finalizar e commitar preparação USB
