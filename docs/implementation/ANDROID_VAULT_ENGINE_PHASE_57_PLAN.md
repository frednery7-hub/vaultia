# Android Vault Engine — Phase 57 Plan

## Status

Planned.

## Nome

Vault Engine: The Grand Orchestrator

## Contexto

Toda a fundação criptográfica, serialização determinística e infraestrutura agnóstica de persistência foram construídas em absoluto isolamento. Agora precisamos da engrenagem final, a classe suprema de Domínio que ficará exposta para os "Casos de Uso" e para a "UI" do Vaultia: O **Repositório do Cofre** (`VaultRepository`).

## Objetivo

Criar o Orquestrador (`DefaultVaultRepository`) que receberá Modelos de Domínio (`VaultItemPayload`) em Plaintext, e regerá a sinfonia de Serialização, Criptografia, Geração de Ponteiros (`EncryptedPayloadPointer`) e Persistência no File System + SQLite de forma atômica e limpa.

## Arquivos Afetados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/vault/VaultRepositoryError.kt
apps/android/app/src/main/java/com/vaultia/app/core/vault/VaultRepositoryResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/vault/VaultRepository.kt
apps/android/app/src/main/java/com/vaultia/app/core/vault/DefaultVaultRepository.kt
```

### Documentação

```text
docs/implementation/ANDROID_VAULT_ENGINE_PHASE_57_PLAN.md
```

## Regras de Arquitetura e Segurança

1. **Agnosticismo Pleno**: O `VaultRepository` pertencerá a `core.vault`, consumindo a trindade via Injeção de Dependência (`StorageContainer`, `VaultPayloadEncryptionService`, `VaultPayloadDecryptionService`).
2. **Separação de Identidade**: O SQLite Index armazenará o `StorageRecordMetadata` (para listagens rápidas sem descriptografar nada). O Arquivo Físico (`PayloadRepository`) guardará o `EncryptedVaultItemDraft` serializado.
3. **Atomicidade Simplificada**: Se falhar ao gravar o Payload no arquivo, não gravamos o SQLite. Se a gravação SQLite falhar, tentamos excluir o payload huérfano.

## Critérios de Aceite

- Plano Phase 57 criado.
- `VaultRepository` desenhado com `saveItem`, `readItem` e `deleteItem`.
- `DefaultVaultRepository` construído unindo Serializers, EncryptionServices e o `StorageContainer`.
- Teste de Integração/Unitário validando o fluxo de ponta a ponta (Plaintext Domain Object -> AesGcm -> FileSystem -> SQLite -> Read -> Decrypt -> Plaintext Domain Object).
