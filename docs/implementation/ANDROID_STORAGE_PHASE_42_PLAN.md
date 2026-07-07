# Android Storage — Phase 42 Plan

## Status

Planned.

## Nome

Storage Model Contracts

## Contexto

A Phase 41 definiu a arquitetura de storage, a classificação de dados e as regras de boundary.

A Phase 42 começa a transformar essa arquitetura em contratos Kotlin puros. Esta fase ainda não implementa persistência real.

---

## Objetivo

Criar contratos de modelo para storage local sem escrever dados em disco.

A fase deve definir:

- identificador técnico de registro;
- ponteiro técnico para payload criptografado;
- tipos de registro de storage;
- metadados técnicos permitidos;
- erros de modelo de storage;
- regras de validação defensiva;
- testes unitários para impedir vazamento de plaintext nos modelos.

---

## Arquivos Planejados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/storage/model/StorageRecordId.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/model/EncryptedPayloadPointer.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/model/StorageRecordType.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/model/StorageRecordMetadata.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/model/StorageModelError.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/storage/model/StorageRecordIdTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/storage/model/EncryptedPayloadPointerTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/storage/model/StorageRecordMetadataTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/storage/model/StorageModelArchitectureTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_STORAGE_PHASE_42_PLAN.md
docs/implementation/ANDROID_STORAGE_PHASE_42_DONE.md
```

---

## Regras de Segurança

- Modelos não podem conter plaintext.
- Modelos não podem conter master password.
- Modelos não podem conter derived key.
- Modelos não podem conter vault key em claro.
- Modelos não podem conter título real, username, URL sensível, nota, senha, token, seed phrase, documento ou foto em claro.
- Ponteiros de payload devem ser técnicos, não nomes de usuário nem paths externos.
- IDs devem ser técnicos e validados.
- Nenhuma classe desta fase deve usar Android framework APIs.
- Nenhuma classe desta fase deve usar java.io, kotlin.io, Room, SQLite, DataStore ou SharedPreferences.
- Nenhuma permissão Android deve ser adicionada.
- Nenhum storage real deve ser criado.

---

## Critérios de Aceite

- Contratos Kotlin puros criados.
- Testes unitários cobrindo validação defensiva criados.
- Teste arquitetural impedindo APIs de persistência criado.
- Nenhuma implementação real de storage adicionada.
- Nenhuma permissão Android adicionada.
- Testes unitários passam.
- Check Android passa.
- APK continua sem `android.permission.INTERNET`.
