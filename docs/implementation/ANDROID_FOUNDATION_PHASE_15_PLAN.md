# Android Foundation — Phase 15 Plan

## Nome

UI State Hardening

## Objetivo

Criar uma camada explícita de estado de UI para evitar que a interface interprete diretamente estados brutos de sessão.

## Escopo

Esta fase cria um mapeamento seguro:

- NOT_INITIALIZED -> LOCKED
- LOCKED -> LOCKED
- UNLOCKED -> UNLOCKED

A UI passa a depender de `VaultSessionUiState`.

## Fora de escopo

Esta fase não implementa:

- senha real;
- PIN real;
- biometria;
- KDF;
- criptografia;
- Android Keystore;
- banco de dados;
- arquivos locais;
- storage criptografado;
- backend;
- rede;
- permissão INTERNET.

## Critérios de aceite

- Existe estado explícito de UI.
- NOT_INITIALIZED é tratado como bloqueado.
- BootstrapScreen usa o estado de UI.
- Nenhuma permissão nova é adicionada.
- O APK permanece sem android.permission.INTERNET.
- Testes validam o mapeamento.