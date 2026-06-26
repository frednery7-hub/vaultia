# Android Foundation — Phase 20 Plan

## Nome

Pre-Auth UX Boundary

## Objetivo

Deixar explícito na UI que o desbloqueio atual ainda é simulado e que dados reais só serão permitidos depois de autenticação local real, KDF, criptografia e storage seguro.

## Escopo

Esta fase adiciona um componente reutilizável de aviso pré-autenticação:

- `AuthBoundaryNoticeView`

E aplica esse aviso em:

- `LockedVaultScreen`
- `UnlockedVaultScreen`

O objetivo é impedir interpretação errada de que o app já protege dados reais nesta etapa.

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
- CRUD real;
- backup;
- import/export;
- backend;
- rede;
- permissão INTERNET.

## Critérios de aceite

- Existe componente explícito de aviso pré-autenticação.
- A tela bloqueada exibe o aviso.
- A tela desbloqueada exibe o aviso.
- O aviso declara que o desbloqueio é simulado.
- O aviso declara que dados reais dependem de autenticação local real, KDF, criptografia e storage seguro.
- Nenhum storage real é usado.
- Nenhuma permissão nova é adicionada.
- O APK permanece sem android.permission.INTERNET.
- O roadmap não é atualizado nesta fase.
