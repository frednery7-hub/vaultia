# Vaultia – Android Foundation Phase 7 PLAN

## Objetivo

Criar uma UI bootstrap não sensível para validar abertura do app e comunicação inicial do posicionamento local-only.

## Escopo Permitido

Esta fase permite:

- criar tela inicial simples;
- exibir nome do app;
- exibir aviso local-first;
- exibir aviso sem nuvem/sem conta/sem recuperação remota;
- conectar `MainActivity` à tela bootstrap;
- criar teste de arquitetura contra input/persistência/criptografia indevida;
- rodar testes e check Android.

## Escopo Bloqueado

Esta fase não permite implementar:

- campo de senha;
- Master Password;
- KDF;
- AES-GCM;
- Android Keystore;
- banco local;
- storage criptografado;
- cofre real;
- biometria;
- backup;
- clipboard;
- rede;
- permissão INTERNET;
- login;
- conta;
- recuperação.

## Critérios de Aceite

A fase só pode ser fechada se:

- app compilar;
- testes unitários passarem;
- `./scripts/check-android.sh` passar;
- Manifest continuar sem INTERNET;
- APK binário continuar sem INTERNET;
- Bootstrap UI não contiver input sensível;
- Bootstrap UI não contiver persistência;
- Bootstrap UI não contiver criptografia;
- nenhum arquivo perigoso aparecer no Git status.

## Status

- **Status:** Planejado
- **Próximo passo:** executar testes e auditoria final
