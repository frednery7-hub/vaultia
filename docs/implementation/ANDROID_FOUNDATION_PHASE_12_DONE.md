# Android Foundation — Phase 12 Done

## Status

Aprovado.

## Objetivo

Criar um repositório temporário em memória para metadados de itens do cofre.

## Resultado

Foi criado o `InMemoryVaultRepository`, responsável por manter uma lista temporária de `VaultItem` somente durante o processo atual do app.

## Implementação

Arquivos principais:

- `InMemoryVaultRepository.kt`
- `VaultHomeShell.kt`
- `BootstrapScreen.kt`
- `MainActivity.kt`

Operações suportadas:

- listar metadados;
- adicionar metadados no processo atual;
- limpar metadados no processo atual;
- rejeitar IDs duplicados.

## Garantias de segurança desta fase

- Nenhum dado sensível foi salvo.
- Nenhum conteúdo de senha foi salvo.
- Nenhum conteúdo de nota foi salvo.
- Nenhum byte de foto ou documento foi salvo.
- Nenhum caminho local de arquivo foi salvo.
- Nenhum ciphertext foi salvo.
- Nenhuma chave criptográfica foi salva.
- Nenhum banco de dados foi usado.
- Nenhum arquivo local foi usado.
- Nenhuma criptografia foi simulada.
- Nenhuma permissão nova foi adicionada.
- O APK permanece sem android.permission.INTERNET.

## Testes

Foram adicionados e atualizados testes para validar:

- repositório inicia vazio;
- adição de metadados em memória;
- rejeição de IDs duplicados;
- cópia defensiva ao listar metadados;
- limpeza dos metadados em memória;
- ausência de APIs de storage, criptografia ou rede;
- UI calcula contadores por categoria usando metadados.

## Critério de aceite

A Phase 12 é considerada concluída porque o app possui um repositório em memória para metadados, sem persistência real, sem rede e sem conteúdo sensível.
