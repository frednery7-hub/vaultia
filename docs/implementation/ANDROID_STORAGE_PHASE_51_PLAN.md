# Android Storage — Phase 51 Plan

## Status

Planned.

## Nome

Encrypted Payload Repository Contract

## Contexto

Na Phase 50, concluímos a infraestrutura do banco de dados relacional (SQLite/Room) responsável exclusivamente pela indexação rápida dos *metadados* do cofre (IDs, timestamps e os ponteiros de arquivo).

Agora precisamos construir a segunda metade do nosso "Hybrid Storage Model" (desenhado na Phase 41): a persistência física dos **arquivos criptografados reais** (`.enc`) que contêm as senhas, notas e dados confidenciais do usuário. 

A Phase 51 estabelece a ponte arquitetural limpa para a escrita e leitura desses arquivos.

## Objetivo

Definir a interface genérica `EncryptedPayloadRepository` e criar sua implementação em memória (`InMemoryEncryptedPayloadRepository`), permitindo que a camada de domínio possa salvar bytes puros sem depender do `java.io.File` real ainda.

## Arquivos Afetados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/storage/repository/EncryptedPayloadRepository.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/repository/InMemoryEncryptedPayloadRepository.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/storage/repository/InMemoryEncryptedPayloadRepositoryTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_STORAGE_PHASE_51_PLAN.md
```

## Regras de Arquitetura e Segurança

1. **Blindagem de File I/O**: A camada de Domínio/Service **NUNCA** chamará `java.io.File` ou APIs do sistema operacional. Ela falará estritamente com a nova interface do Repositório.
2. **Contrato Type-Safe**: Os métodos devem usar **apenas** o tipo de segurança `EncryptedPayloadPointer` (e não strings primitivas) para garantir validação rígida de caminhos (`.enc`).
3. **Agnosticismo de Criptografia**: O repositório só enxerga `ByteArray`. Ele não sabe nem deve saber decifrar o conteúdo, tratar AES ou ler o Header. É uma classe burra de I/O.
4. **Erros Funcionais**: Assim como o banco de dados, falhas de leitura/gravação devem retornar falhas via `StorageRepositoryResult`.

## Critérios de Aceite

- Plano Phase 51 criado no controle de versão.
- Interface `EncryptedPayloadRepository` desenhada com operações de leitura, gravação e deleção.
- Implementação `InMemoryEncryptedPayloadRepository` construída sobre um hashmap seguro para threads.
- Teste unitário certificando leitura, sobrescrita, busca de arquivo inexistente e deleção de payloads in-memory.
