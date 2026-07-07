# Android Storage — Phase 44 Plan

## Status

Planned.

## Nome

Storage Use Case Contracts

## Contexto

A Phase 42 criou contratos de modelo para registros técnicos de storage.
A Phase 43 criou um contrato de repositório e uma implementação in-memory para operações sobre `StorageRecordMetadata`.

A Phase 44 cria uma camada de use cases sobre o `StorageRepository`, mantendo a separação entre domínio, repositório e futura persistência real.

Esta fase ainda não implementa storage persistente.

---

## Objetivo

Criar contratos de use case para operações de storage técnico sem expor detalhes de repositório diretamente para camadas superiores.

A fase deve definir:

- use case para salvar metadados técnicos;
- use case para buscar metadados por ID;
- use case para listar metadados;
- use case para deletar metadados;
- resultado tipado de use case;
- erros de use case mapeados a partir do repository;
- testes unitários cobrindo os fluxos principais;
- teste arquitetural impedindo persistência, Android APIs e crypto na camada de use case.

---

## Arquivos Planejados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/storage/usecase/StorageUseCaseResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/usecase/StorageUseCaseError.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/usecase/SaveStorageRecordMetadataUseCase.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/usecase/FindStorageRecordMetadataUseCase.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/usecase/ListStorageRecordMetadataUseCase.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/usecase/DeleteStorageRecordMetadataUseCase.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/storage/usecase/StorageUseCaseTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/storage/usecase/StorageUseCaseArchitectureTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_STORAGE_PHASE_44_PLAN.md
docs/implementation/ANDROID_STORAGE_PHASE_44_DONE.md
```

---

## Regras de Segurança

- Use cases não podem escrever em disco.
- Use cases não podem ler arquivos.
- Use cases não podem usar Room.
- Use cases não podem usar SQLite.
- Use cases não podem usar DataStore.
- Use cases não podem usar SharedPreferences.
- Use cases não podem usar Android framework APIs.
- Use cases não podem derivar chaves.
- Use cases não podem criptografar ou descriptografar payloads.
- Use cases não podem conter plaintext, senha mestra, derived key ou vault key em claro.
- Use cases não podem se conectar à UI.
- Use cases não podem adicionar `android.permission.INTERNET`.

---

## Operações Permitidas na Phase 44

Somente orquestração de operações já expostas pelo `StorageRepository`:

- save metadata;
- find metadata by id;
- list metadata;
- delete metadata by id.

Nenhuma operação deve persistir dados em disco ou acessar payload criptografado real.

---

## Critérios de Aceite

- Plano da Phase 44 criado.
- Resultado e erros de use case criados.
- Use cases de save/find/list/delete criados.
- Testes unitários de use case criados.
- Teste arquitetural contra persistência, Android APIs e crypto criado.
- Nenhuma implementação real de storage adicionada.
- Nenhuma permissão Android adicionada.
- Testes unitários passam.
- Check Android passa.
- APK continua sem `android.permission.INTERNET`.
