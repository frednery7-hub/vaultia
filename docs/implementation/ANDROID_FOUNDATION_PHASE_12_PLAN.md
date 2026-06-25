# Android Foundation — Phase 12 Plan

## Nome

In-Memory Vault Repository

## Objetivo

Criar um repositório temporário em memória para metadados de itens do cofre.

## Escopo

Esta fase adiciona um repositório local em memória para operar somente com `VaultItem`.

O repositório deve permitir:

- listar metadados;
- adicionar metadados apenas no processo atual;
- limpar metadados apenas no processo atual;
- rejeitar IDs duplicados.

A UI interna do cofre passa a ler os contadores por categoria a partir do repositório.

## Fora de escopo

Esta fase não implementa:

- SQLite
- Room
- DataStore
- SharedPreferences
- arquivos locais
- storage criptografado
- bytes de foto/documento
- caminho local de arquivo
- ciphertext
- chave criptográfica
- senha real
- conteúdo de nota
- backend
- rede
- permissão INTERNET

## Critérios de aceite

- O repositório mantém dados somente em memória.
- A lista exposta pelo repositório é cópia defensiva.
- IDs duplicados são rejeitados.
- A UI calcula contadores por categoria usando metadados.
- Nenhum storage real é usado.
- Nenhuma permissão nova é adicionada.
- O APK permanece sem android.permission.INTERNET.
