# Android Storage — Phase 45 Plan

## Status

Planned.

## Nome

Storage Facade / Service Boundary

## Contexto

A Phase 42 criou contratos de modelo para registros técnicos de storage.
A Phase 43 criou o contrato de repositório e uma implementação in-memory.
A Phase 44 criou use cases para save, find, list e delete sobre o `StorageRepository`.

A Phase 45 cria uma fachada única para acesso aos use cases de storage, centralizando a fronteira de serviço sem expor diretamente os use cases para camadas superiores.

Esta fase ainda não implementa storage persistente, crypto/decrypt, Keystore, biometria, backup/export ou UI real.

---

## Objetivo

Criar um boundary de serviço para operações técnicas de storage, mantendo repository e use cases isolados da futura UI e da futura persistência real.

A fase deve definir:

- contrato de facade/service para storage;
- resultado tipado do facade;
- erros do facade mapeados a partir dos use cases;
- implementação default que delega aos use cases;
- testes unitários de save/find/list/delete via facade;
- teste arquitetural impedindo persistência, Android APIs, crypto e UI no pacote de facade.

---

## Arquivos Planejados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/storage/service/StorageService.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/service/StorageServiceResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/service/StorageServiceError.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/service/DefaultStorageService.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/storage/service/DefaultStorageServiceTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/storage/service/StorageServiceArchitectureTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_STORAGE_PHASE_45_PLAN.md
docs/implementation/ANDROID_STORAGE_PHASE_45_DONE.md
```

---

## Regras de Segurança

- O service não pode escrever em disco.
- O service não pode ler arquivos.
- O service não pode usar Room.
- O service não pode usar SQLite.
- O service não pode usar DataStore.
- O service não pode usar SharedPreferences.
- O service não pode usar Android framework APIs.
- O service não pode derivar chaves.
- O service não pode criptografar ou descriptografar payloads.
- O service não pode conter plaintext, senha mestra, derived key ou vault key em claro.
- O service não pode se conectar à UI.
- O service não pode adicionar `android.permission.INTERNET`.

---

## Operações Permitidas na Phase 45

Somente delegação para use cases já existentes:

- save metadata;
- find metadata by id;
- list metadata;
- delete metadata by id.

Nenhuma operação deve persistir dados em disco, acessar payload criptografado real, derivar chaves, descriptografar conteúdo ou renderizar UI.

---

## Critérios de Aceite

- Plano da Phase 45 criado.
- Resultado e erros de service criados.
- Contrato `StorageService` criado.
- Implementação `DefaultStorageService` criada.
- Testes unitários de service criados.
- Teste arquitetural contra persistência, Android APIs, crypto e UI criado.
- Nenhuma implementação real de storage adicionada.
- Nenhuma permissão Android adicionada.
- Testes unitários passam.
- Check Android passa.
- APK continua sem `android.permission.INTERNET`.
