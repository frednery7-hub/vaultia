# Android Storage — Phase 49 Plan

## Status

Planned.

## Nome

Local Storage Repository Implementation

## Contexto

Na Phase 48 criamos a camada mais baixa de infraestrutura do SQLite (Room Database, Entity e DAO). No entanto, o núcleo do aplicativo não conhece o Room; ele conversa apenas com a interface limpa `StorageRepository`.

A Phase 49 é o "elo" entre esses dois mundos. Ela criará a implementação real (`RoomStorageRepository`) que consumirá o DAO, fará o mapeamento bidirecional de dados (Domain Model <-> Room Entity) e fará o encapsulamento seguro de exceções do SQLite para o modelo funcional `StorageRepositoryResult`.

## Objetivo

Implementar o `RoomStorageRepository` atendendo o contrato da interface `StorageRepository` e testá-lo isoladamente. 

## Arquivos Afetados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/storage/repository/RoomStorageRepository.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/storage/repository/RoomStorageRepositoryTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_STORAGE_PHASE_49_PLAN.md
```

## Regras de Arquitetura e Segurança

1. **Mapeamento Explícito**: O repositório deve transformar `StorageRecordMetadata` em `StorageRecordMetadataEntity` antes de salvar no DAO, e fazer o inverso ao ler.
2. **Encapsulamento de Erros**: Exceções lançadas pelo Room (ex: ConstraintExceptions no caso de ID duplicado) não podem vazar para o core. Elas devem ser capturadas e transformadas em `StorageRepositoryResult.Failure(StorageRepositoryError)`.
3. **Isolamento Constante**: O `VaultiaAppContainer` ainda **NÃO** deve ser alterado para usar o RoomStorageRepository. Manteremos a injeção do `InMemoryStorageRepository` no AppContainer para assegurar que testes sistêmicos rodem rápido antes de ativarmos a persistência global.
4. **Sem Arquivos Físicos**: O repositório trata apenas metadados técnicos do banco. Ele não lerá/escreverá o payload cifrado no disco (isso será em uma fase futura).

## Critérios de Aceite

- Plano Phase 49 criado.
- Classe `RoomStorageRepository` implementada.
- Tratamento de exceções (DuplicateRecord) validado.
- Testes unitários do `RoomStorageRepository` desenvolvidos usando MockK para o DAO.
- `check-android.sh` executado com sucesso.
