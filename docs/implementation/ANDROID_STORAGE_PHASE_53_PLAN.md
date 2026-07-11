# Android Storage — Phase 53 Plan

## Status

Planned.

## Nome

Payload Repository Container Integration

## Contexto

Temos o `StorageRepository` (via SQLite/Room) para armazenar metadados velozes, e agora criamos o `EncryptedPayloadRepository` (via File I/O) para armazenar os bytes cifrados no disco do Android.

Para que a aplicação (coração do Vaultia) consiga coordenar as operações de salvar um cofre inteiro, ela precisa ter acesso a esses dois serviços através de um ponto unificado de injeção de dependências: o `StorageContainer`.

## Objetivo

Atualizar o `StorageContainer` para fornecer acesso direto ao `EncryptedPayloadRepository`. Em seguida, atualizar as fábricas de composição (`StorageCompositionRoot` e `LocalStorageCompositionRoot`) para instanciarem os repositórios (em-memória e em-disco, respectivamente) e os injetarem no container.

## Arquivos Afetados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/storage/composition/StorageContainer.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/composition/StorageCompositionRoot.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/local/composition/LocalStorageCompositionRoot.kt
```

### Testes

```text
apps/android/app/src/androidTest/java/com/vaultia/app/core/storage/local/composition/LocalStorageCompositionRootTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_STORAGE_PHASE_53_PLAN.md
```

## Regras de Arquitetura e Segurança

1. **Plug & Play Transparente**: A camada de Aplicação só deve receber o `EncryptedPayloadRepository` genérico de dentro do Container, sem saber se a execução por baixo está escrevendo na Memória (`InMemory`) ou no Android Storage (`File`). O encapsulamento se manterá absoluto.
2. **Path Resolution Segura**: Ao inicializar o `FileEncryptedPayloadRepository` dentro do `LocalStorageCompositionRoot`, nós usaremos um diretório travado dentro do `Context.filesDir` instanciado rigidamente, prevenindo injeções.

## Critérios de Aceite

- Plano Phase 53 registrado.
- Propriedade `payloadRepository` adicionada à data class `StorageContainer`.
- `StorageCompositionRoot` (In-Memory) instanciando e provendo o `InMemoryEncryptedPayloadRepository`.
- `LocalStorageCompositionRoot` (Room+File) instanciando e provendo o `FileEncryptedPayloadRepository` roteado para o `context.filesDir/vaultia_payloads`.
- Build, testes e `check-android.sh` retornando sucesso (TUDO VERDE).
