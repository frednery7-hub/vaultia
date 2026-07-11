# Android Vault Engine — Phase 58 Plan

## Status

Planned.

## Nome

Vault Session Engine & DI Wiring

## Contexto

Agora que a Fundação Criptográfica (Core) e o Orquestrador (`DefaultVaultRepository`) estão finalizados, precisamos conectá-los ao Container de Injeção de Dependências (`VaultiaAppContainer`) do App. 
No entanto, o Orquestrador exige uma Sessão Destrancada (com a Chave-Mestra `KdfResult` na memória RAM) para funcionar. Atualmente, o nosso `SessionManager` é apenas um rascunho que guarda um estado binário (Trancado/Destrancado).

## Objetivo

Atualizar o `SessionManager` para armazenar o Estado da Sessão com segurança na memória, injetar o `DefaultVaultRepository` na aplicação substituindo o mock em memória, e plugar os serviços de Criação (`VaultCreationService`) e Destrancamento (`VaultUnlockService`) para que as telas (UI) possam consumi-los.

## Arquivos Afetados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/session/SessionManager.kt
apps/android/app/src/main/java/com/vaultia/app/core/session/InMemorySessionManager.kt
apps/android/app/src/main/java/com/vaultia/app/core/app/VaultiaAppContainer.kt
```

### Documentação

```text
docs/implementation/ANDROID_VAULT_ENGINE_PHASE_58_PLAN.md
```

## Regras de Arquitetura e Segurança

1. **Memória Volátil**: O `KdfResult` (Chave Mestre) que destranca as senhas do usuário só existirá dentro do `SessionManager`. Ele não será gravado em disco (SharedPreferences) em hipótese alguma. Quando o App for fechado pelo Android (OOM), a chave some e o cofre tranca automaticamente.
2. **Injeção Centralizada**: O `VaultiaAppContainer` atuará como um "Dagger/Hilt" manual. Ele deve inicializar os algoritmos do Argon2id uma única vez (Economizando CPU/Bateria).

## Critérios de Aceite

- Plano Phase 58 criado.
- `SessionManager` atualizado para conter `unlock(kdfResult: KdfResult)` e retornar a chave quando necessário para o `VaultRepository`.
- `VaultiaAppContainer` injetando `DefaultVaultRepository` no lugar de `InMemoryVaultRepository`.
- Instâncias do `VaultCreationService` e `VaultUnlockService` providas globalmente pelo Container, consumindo as dependências agnósticas (Argon2, FileHeader, etc).
