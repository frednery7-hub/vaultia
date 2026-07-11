# Android Vault Engine — Phase 54 Plan

## Status

Planned.

## Nome

Vault Item Payload Models & Serialization Draft

## Contexto

Após 53 Fases, toda a nossa infraestrutura base de criptografia (AES-GCM, Argon2) e de armazenamento (Room para metadados, File I/O para binários, centralizados no `StorageContainer`) está perfeitamente pronta, testada e isolada. 

Para finalmente começarmos a salvar itens no cofre, precisamos conectar o Domínio da UI com a Fundação. O primeiro passo é modelar o conteúdo secreto real. O modelo `VaultItem` atual serve apenas como um cabeçalho (Título, Data e Tipo). Nós precisamos definir a estrutura dos dados confidenciais (Payloads).

## Objetivo

Criar a hierarquia de domínio dos **Payloads do Cofre** (Senhas, Notas, etc.) de forma agnóstica a frameworks externos, e definir o contrato de como essas classes Kotlin puras serão transformadas em um `ByteArray` (via JSON) para poderem ser criptografadas e enviadas para o nosso recém-criado `PayloadRepository`.

## Arquivos Afetados

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
```

## Regras de Arquitetura e Segurança

1. **Domínio Puro**: As classes de Payload serão puramente Kotlin (`data class`). Nenhuma anotação de bibliotecas de terceiros (como `@Serializable` do Kotlinx ou `@Json` do Moshi) deve poluir esses arquivos para manter a independência de framework (Clean Architecture).
2. **Segurança de Memória (Futura)**: As strings contendo senhas reais estarão sujeitas a ataques de despejo de memória (Memory Dump) no futuro se usarmos apenas `String`. Por ora, validaremos as regras de negócio via `require()`, mas projetando para uma possível migração para arrays de caracteres char.

## Critérios de Aceite

- Plano Phase 54 criado.
- `VaultItemPayload` interface/sealed class estabelecida.
- `PasswordPayload` criado com os campos `username`, `password`, `url` e `additionalNotes`.
- `NotePayload` criado com o campo `text`.
- Validações de tamanho máximo criadas para impedir payloads gigantescos que possam causar estouro de memória no AES-GCM (OutOfMemory).
