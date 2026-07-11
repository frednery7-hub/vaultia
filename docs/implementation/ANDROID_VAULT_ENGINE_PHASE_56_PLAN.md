# Android Vault Engine — Phase 56 Plan

## Status

Planned.

## Nome

Vault Header Storage & Unlock Engine

## Contexto

Para que um usuário possa salvar ou ler suas senhas criptografadas, ele precisa antes "Destrancar o Cofre". 
Para destrancar o cofre, precisamos do `VaultUnlockService`, que pega a "Master Password" digitada pelo usuário e a processa no poderoso algoritmo **Argon2** para derivar a chave mestre do AES-GCM. 

No entanto, o Argon2 exige um "Salt" aleatório e os parâmetros de custo de memória que foram utilizados no dia em que o cofre foi criado. Esses parâmetros ficam armazenados no `VaultHeader`.

## Objetivo

Criar a ponte que permite ao usuário inicializar o Cofre (salvando o `VaultHeader` no disco do Android) e posteriormente Destrancá-lo, processando o Argon2 e devolvendo a Chave Criptográfica Suprema (`AuthenticatedCipher`) para a sessão do app.

## Arquivos Afetados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultUnlockService.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/header/VaultHeaderRepository.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/header/InMemoryVaultHeaderRepository.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/local/header/FileVaultHeaderRepository.kt
```

### Documentação

```text
docs/implementation/ANDROID_VAULT_ENGINE_PHASE_56_PLAN.md
```

## Regras de Arquitetura e Segurança

1. **Separação Rigorosa**: O `VaultHeader` (Metadados Criptográficos Públicos do Cofre) não é um segredo, mas é vital. Ele viverá em um arquivo chamado `vault_header.dat` diretamente no `Context.filesDir` ao lado dos payloads, mantendo o App-Specific Storage como única fonte da verdade física.
2. **KDF Intenso**: O `VaultUnlockService` consumirá recursos (memória e tempo de CPU) usando a classe `Argon2idKdfDeriver` da fundação. Esse cálculo é lento propositalmente para mitigar ataques de força-bruta (Brute Force/Dictionary Attacks).
3. **Resiliência a Corrupção**: Se o `VaultHeader` for corrompido ou sumir, o cofre é matematicamente irrecuperável. O `VaultHeaderRepository` será desenhado como a peça mais resiliente do Storage.

## Critérios de Aceite

- Plano Phase 56 criado.
- `VaultHeaderRepository` desenhado (Agnostic).
- `FileVaultHeaderRepository` implementado para salvar e ler `vault_header.dat` via Android Storage.
- `VaultUnlockService` construído para receber `masterPassword` e `VaultHeader`, acionar a derivação pesada via Argon2, instanciar a chave derivada em `AesGcmAuthenticatedCipher` e retorná-la para a sessão.
