# Android Storage — Phase 43 Plan

## Status

Planned.

## Nome

In-Memory Storage Repository Contract

## Contexto

A Phase 42 criou contratos Kotlin puros para o modelo de storage local: IDs técnicos, ponteiros de payload criptografado, tipos de registro, metadados técnicos e erros de modelo.

A Phase 43 cria um contrato de repositório e uma implementação em memória para validar operações de storage sem persistir dados em disco.

Esta fase ainda não implementa storage real.

---

## Objetivo

Criar uma fronteira de repositório para registros técnicos de storage, com implementação in-memory usada apenas para testes e contrato de domínio.

A fase deve definir:

- contrato de repository para metadados de storage;
- resultado de operações de storage;
- erros de repository;
- implementação in-memory sem persistência;
- testes de create/read/list/delete;
- teste arquitetural impedindo File I/O, Room, SQLite, DataStore e SharedPreferences;
- preservação da fronteira offline-only e sem `android.permission.INTERNET`.

---

## Arquivos Planejados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/storage/repository/StorageRepository.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/repository/StorageRepositoryResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/repository/StorageRepositoryError.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/repository/InMemoryStorageRepository.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/storage/repository/InMemoryStorageRepositoryTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/storage/repository/StorageRepositoryArchitectureTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_STORAGE_PHASE_43_PLAN.md
docs/implementation/ANDROID_STORAGE_PHASE_43_DONE.md
```

---

## Regras de Segurança

- O repositório não pode escrever em disco.
- O repositório não pode ler arquivos.
- O repositório não pode usar Room.
- O repositório não pode usar SQLite.
- O repositório não pode usar DataStore.
- O repositório não pode usar SharedPreferences.
- O repositório não pode usar Android framework APIs.
- O repositório não pode derivar chaves.
- O repositório não pode criptografar ou descriptografar payloads.
- O repositório não pode conter plaintext, senha mestra, derived key ou vault key em claro.
- O repositório não pode se conectar à UI.
- O repositório não pode adicionar `android.permission.INTERNET`.

---

## Operações Permitidas na Phase 43

Somente operações in-memory sobre `StorageRecordMetadata`:

- save;
- find by id;
- list all;
- delete by id;
- clear for tests.

Nenhuma dessas operações deve sobreviver ao processo.

---

## Critérios de Aceite

- Plano da Phase 43 criado.
- Contrato `StorageRepository` criado.
- Resultado e erros de repository criados.
- Implementação `InMemoryStorageRepository` criada.
- Testes unitários de repository criados.
- Teste arquitetural contra persistência real criado.
- Nenhuma implementação real de storage adicionada.
- Nenhuma permissão Android adicionada.
- Testes unitários passam.
- Check Android passa.
- APK continua sem `android.permission.INTERNET`.
