# Android Vault Engine — Phase 54 Done

## Status

Completed.

## Nome

Vault Item Payload Models & Serialization Draft

## Resultado

A Phase 54 modelou as entidades de núcleo absoluto do Cofre, estabelecendo como uma Senha e uma Nota devem se estruturar na memória do aplicativo de forma totalmente pura e desconectada de frameworks de serialização (Clean Architecture).

Foram criadas restrições (limits) robustas baseadas na heurística de OutOfMemoryError (OOM) para AES-GCM. Ao impedir que um payload de nota tenha mais de 200.000 caracteres, a Fundação garante que a criptografia nunca explodirá a heap de memória do Android por exaustão, que é um vetor de negação de serviço (DoS) comum em cofres móveis.

---

## Arquivos Criados / Modificados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/model/payload/VaultItemPayload.kt
apps/android/app/src/main/java/com/vaultia/app/core/model/payload/PasswordPayload.kt
apps/android/app/src/main/java/com/vaultia/app/core/model/payload/NotePayload.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/model/payload/PayloadModelsTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_VAULT_ENGINE_PHASE_54_PLAN.md
docs/implementation/ANDROID_VAULT_ENGINE_PHASE_54_DONE.md
```

---

## Componentes e Garantias de Segurança

- **Agnosticismo a Frameworks**: Nenhum `@Serializable` ou `@Keep` polui essas classes. Elas são Kotlin puro, prontas para qualquer framework de JSON no futuro via DTOs na camada de adaptação.
- **Circuit Breaker OOM**: O AES requer Arrays contíguos de memória. As validações restritas via `require()` (255 para usernames, 1024 para senhas, 200k para notas) blindam o app na base antes mesmo do processo de criptografia iniciar, eliminando travamentos de buffer overflow.
