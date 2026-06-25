# Android Foundation — Phase 13 Done

## Status

Aprovado.

## Objetivo

Adicionar um seed demo em memória com metadados falsos para validar a UI com contadores diferentes de zero.

## Resultado

Foi criado o `DemoVaultMetadataSeed`, que fornece itens `VaultItem` metadata-only para uso visual temporário.

Itens demo:

- 1 PASSWORD metadata-only
- 1 NOTE metadata-only
- 1 DOCUMENT metadata-only

## Implementação

Arquivos principais:

- `DemoVaultMetadataSeed.kt`
- `InMemoryVaultRepository.kt`
- `MainActivity.kt`

O seed é carregado no `InMemoryVaultRepository`, que permanece em memória e não persiste dados.

## Garantias de segurança desta fase

- Nenhuma senha real foi incluída.
- Nenhum conteúdo de nota foi incluído.
- Nenhum arquivo real foi incluído.
- Nenhum byte de foto ou documento foi incluído.
- Nenhum caminho local foi incluído.
- Nenhum ciphertext foi incluído.
- Nenhuma chave criptográfica foi incluída.
- Nenhum banco de dados foi usado.
- Nenhum arquivo local foi usado.
- Nenhuma criptografia foi simulada.
- Nenhuma permissão nova foi adicionada.
- O APK permanece sem android.permission.INTERNET.

## Testes

Foram adicionados e atualizados testes para validar:

- seed contém somente metadados;
- IDs demo são únicos;
- ausência de payload sensível;
- ausência de APIs de storage, criptografia ou rede;
- seed é carregado somente no repositório em memória;
- repositório aceita adição em lote mantendo validação contra duplicidade.

## Critério de aceite

A Phase 13 é considerada concluída porque a UI pode ser validada com metadados demo sem persistência, sem conteúdo sensível e sem rede.
