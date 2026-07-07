# Android Storage — Phase 46 Plan

## Status

Planned.

## Nome

Storage Composition Root

## Contexto

A Phase 42 criou contratos de modelo para registros técnicos de storage.
A Phase 43 criou o contrato de repositório e uma implementação in-memory.
A Phase 44 criou use cases de storage.
A Phase 45 criou uma fachada de serviço para centralizar o acesso aos use cases.

A Phase 46 cria um composition root para montar os componentes de storage de forma explícita, testável e sem acoplamento com UI ou persistência real.

Esta fase ainda não implementa storage persistente, crypto/decrypt, Keystore, biometria, backup/export ou UI real.

---

## Objetivo

Criar um ponto central de montagem para o track de storage, encapsulando a criação do repository, use cases e service em uma única estrutura de composição.

A fase deve definir:

- container de storage;
- factory para montar o container default;
- exposição de `StorageService` como boundary público do container;
- wiring explícito de `InMemoryStorageRepository`;
- wiring explícito dos use cases de save, find, list e delete;
- wiring explícito de `DefaultStorageService`;
- testes unitários de composição;
- teste arquitetural impedindo persistência, Android APIs, crypto e UI no pacote de composition.

---

## Arquivos Planejados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/storage/composition/StorageContainer.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/composition/StorageCompositionRoot.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/storage/composition/StorageCompositionRootTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/storage/composition/StorageCompositionArchitectureTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_STORAGE_PHASE_46_PLAN.md
docs/implementation/ANDROID_STORAGE_PHASE_46_DONE.md
```

---

## Regras de Segurança

- O composition root não pode escrever em disco.
- O composition root não pode ler arquivos.
- O composition root não pode usar Room.
- O composition root não pode usar SQLite.
- O composition root não pode usar DataStore.
- O composition root não pode usar SharedPreferences.
- O composition root não pode usar Android framework APIs.
- O composition root não pode usar Compose/UI.
- O composition root não pode derivar chaves.
- O composition root não pode criptografar ou descriptografar payloads.
- O composition root não pode conter plaintext, senha mestra, derived key ou vault key em claro.
- O composition root não pode adicionar `android.permission.INTERNET`.

O container pode montar apenas dependências já existentes do track de storage: repository in-memory, use cases e service.

---

## Operações Permitidas na Phase 46

Somente composição de objetos já implementados:

- criar `InMemoryStorageRepository`;
- criar use cases de storage;
- criar `DefaultStorageService`;
- expor `StorageService` pelo container;
- validar que operações via service funcionam pelo container.

Nenhuma operação deve persistir dados em disco, acessar payload criptografado real, derivar chaves, descriptografar conteúdo ou renderizar UI.

---

## Critérios de Aceite

- Plano da Phase 46 criado.
- `StorageContainer` criado.
- `StorageCompositionRoot` criado.
- Container expõe apenas `StorageService` como boundary principal.
- Wiring completo de repository, use cases e service validado.
- Testes unitários de composição criados.
- Teste arquitetural contra persistência, Android APIs, crypto e UI criado.
- Nenhuma implementação real de storage adicionada.
- Nenhuma permissão Android adicionada.
- Testes unitários passam.
- Check Android passa.
- APK continua sem `android.permission.INTERNET`.
