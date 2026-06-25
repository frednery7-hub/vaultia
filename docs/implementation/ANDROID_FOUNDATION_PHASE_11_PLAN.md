# Android Foundation — Phase 11 Plan

## Nome

Vault Item Model Expansion

## Objetivo

Consolidar o modelo de item do cofre mantendo somente metadados seguros.

## Escopo

Esta fase define os tipos e campos permitidos para representar um item do cofre sem armazenar conteúdo sensível.

Tipos suportados:

- PASSWORD
- NOTE
- PHOTO
- DOCUMENT

Campos permitidos:

- id
- type
- title
- createdAtEpochMillis
- updatedAtEpochMillis

## Fora de escopo

Esta fase não implementa:

- Conteúdo de senha
- Conteúdo de nota
- Bytes de foto
- Bytes de documento
- Caminho local de arquivo
- Ciphertext
- Chaves criptográficas
- Banco de dados
- Arquivos locais
- Criptografia
- Android Keystore
- Backend
- Rede
- Permissão INTERNET

## Critérios de aceite

- O modelo VaultItem contém somente metadados.
- O enum VaultItemType contém os quatro tipos previstos.
- O modelo valida título não vazio.
- O modelo valida timestamps coerentes.
- Testes automatizados garantem ausência de campos sensíveis.
- O APK permanece sem android.permission.INTERNET.
