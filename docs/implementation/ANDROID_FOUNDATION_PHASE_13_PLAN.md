# Android Foundation — Phase 13 Plan

## Nome

Demo Metadata Seed for UI Only

## Objetivo

Adicionar um seed temporário em memória com metadados falsos para validar visualmente a home do cofre com contadores diferentes de zero.

## Escopo

Esta fase adiciona metadados demo para:

- 1 senha fake metadata-only
- 1 nota fake metadata-only
- 1 documento fake metadata-only

Os itens existem apenas em memória e apenas durante o processo atual do app.

## Fora de escopo

Esta fase não implementa:

- senha real
- conteúdo de nota
- arquivo real
- foto real
- bytes
- path local
- URI
- ciphertext
- chave criptográfica
- SQLite
- Room
- DataStore
- SharedPreferences
- storage criptografado
- criptografia
- Android Keystore
- backend
- rede
- permissão INTERNET

## Critérios de aceite

- O seed cria apenas `VaultItem`.
- O seed usa somente metadados.
- O seed não contém payload sensível.
- A home mostra contadores vindos do repositório.
- Nenhum dado é persistido.
- Nenhuma permissão nova é adicionada.
- O APK permanece sem android.permission.INTERNET.
