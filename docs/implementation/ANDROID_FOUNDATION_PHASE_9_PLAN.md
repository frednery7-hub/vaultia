# Android Foundation — Phase 9 Plan

## Nome

Local Session State

## Objetivo

Introduzir o primeiro estado local de sessão do Vaultia, separando o app entre cofre bloqueado e cofre desbloqueado.

## Escopo

Esta fase cria um estado em memória para representar:

- LOCKED
- UNLOCKED

A implementação é temporária e serve apenas para validar fluxo de UI e arquitetura.

## Fora de escopo

Esta fase não implementa:

- Senha real
- PIN real
- Biometria
- KDF
- AES-GCM
- Android Keystore
- SQLite
- Armazenamento de arquivos
- Persistência local
- Recuperação de senha
- Backend
- Rede
- Permissão INTERNET

## Critérios de aceite

- O app inicia bloqueado.
- A UI mostra o estado bloqueado.
- Existe ação explícita de desbloqueio simulado.
- Existe ação explícita de bloqueio.
- O estado é mantido somente em memória.
- Nenhum segredo é persistido.
- Nenhuma permissão nova é adicionada.
- O APK continua sem android.permission.INTERNET.
- Testes automatizados validam o comportamento básico.
