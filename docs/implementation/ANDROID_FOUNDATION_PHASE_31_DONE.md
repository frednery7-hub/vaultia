# Android Foundation — Phase 31 Done

## Status

Aprovado.

## Nome

Vault Payload Encryption Draft

## Objetivo

Conectar o boundary AES-GCM criado na Phase 30 ao modelo de payload do vault em memória, criando um draft cifrado de payload sem storage real, sem serialização em arquivo, sem UI, sem Android Keystore e sem persistência.

A Phase 31 representa dados do cofre como payload cifrado em memória, mas ainda não cria itens persistidos nem grava nada em disco.

---

## Arquivos Criados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadEncryptionService.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadEncryptionRequest.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadEncryptionResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadEncryptionError.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/EncryptedVaultPayloadDraft.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadEncryptionServiceTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadEncryptionArchitectureTest.kt
```

## Arquivos Modificados

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultHeaderArchitectureTest.kt
```

A alteração no teste antigo foi cirúrgica: o `VaultHeaderArchitectureTest` passou a ignorar o subpacote `payload`, porque a Phase 31 introduziu uma auditoria própria para esse novo pacote.

---

## Implementação Entregue

### VaultPayloadEncryptionRequest

- Request de cifragem de payload em memória criado.
- Recebe `key`.
- Recebe `plaintext`.
- Recebe `createdAtEpochMillis`.
- Rejeita key vazia.
- Rejeita plaintext vazio.
- Rejeita timestamp inválido.
- Protege key com cópia defensiva.
- Protege plaintext com cópia defensiva.
- Não persiste dados.

### VaultPayloadEncryptionService

- Service de cifragem de payload em memória criado.
- Recebe `VaultPayloadEncryptionRequest`.
- Cria `PlaintextPayload` em memória.
- Chama `AuthenticatedCipher.encrypt`.
- Mapeia sucesso para `EncryptedVaultPayloadDraft`.
- Mapeia falha do cipher para `VaultPayloadEncryptionError.EncryptionFailed`.
- Não grava nenhum dado em disco.

### EncryptedVaultPayloadDraft

- Draft de payload cifrado em memória criado.
- Contém apenas `EncryptedPayload` e timestamp.
- Rejeita timestamp inválido.
- Não contém key.
- Não contém plaintext.
- Não persiste ciphertext.

### VaultPayloadEncryptionError

Erros tipados criados:

```text
InvalidRequest
EncryptionFailed
```

### VaultPayloadEncryptionResult

- Resultado tipado criado.
- `Success` carrega `EncryptedVaultPayloadDraft`.
- `Failure` carrega `VaultPayloadEncryptionError`.

---

## Fluxo Implementado

```text
1. Recebe key, plaintext e timestamp
2. Valida request
3. Cria PlaintextPayload em memória
4. Chama AuthenticatedCipher.encrypt
5. Rejeita falha de criptografia
6. Recebe EncryptedPayload
7. Retorna EncryptedVaultPayloadDraft
```

Nenhuma etapa escreve em disco.

---

## Segurança e Isolamento

- Nenhuma key foi persistida.
- Nenhum plaintext foi persistido.
- Nenhum ciphertext foi persistido.
- Nenhum nonce foi persistido.
- Nenhuma authentication tag foi persistida.
- Nenhuma key foi logada.
- Nenhum plaintext foi logado.
- Nenhum ciphertext foi logado.
- Nenhum nonce foi logado.
- Nenhuma authentication tag foi logada.
- Draft não contém key.
- Draft não contém plaintext.
- Nenhum arquivo foi criado pelo app.
- Nenhuma escrita em disco foi criada.
- Nenhuma leitura de disco foi criada.
- Nenhum storage real foi criado.
- Nenhum SQLite foi criado.
- Nenhum Room foi criado.
- Nenhum DataStore foi criado.
- Nenhum SharedPreferences foi criado.
- Nenhuma UI foi alterada.
- Nenhuma permissão Android foi adicionada.
- Nenhum `android.permission.INTERNET` foi adicionado.
- Nenhum Android Keystore foi implementado.
- Nenhuma biometria foi implementada.
- Nenhum backup/export foi criado.
- Nenhum modo discreto/decoy vault foi criado.
- Roadmap formal não foi alterado nesta fase.

A limpeza determinística de memória em JVM/Android continua fora do escopo desta fase.

---

## Testes Entregues

- Request rejeita key vazia.
- Request rejeita plaintext vazio.
- Request rejeita timestamp inválido.
- Request protege key com cópia defensiva.
- Request protege plaintext com cópia defensiva.
- Service chama `AuthenticatedCipher.encrypt`.
- Service envia key correta ao cipher.
- Service envia plaintext correto ao cipher.
- Service retorna `EncryptedVaultPayloadDraft` em sucesso.
- Draft preserva timestamp.
- Draft rejeita timestamp inválido.
- Draft não expõe campos com key.
- Draft não expõe campos com plaintext.
- Falha do cipher retorna `EncryptionFailed`.
- Arquitetura não importa Android UI.
- Arquitetura não importa storage.
- Arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- Arquitetura não importa java.io ou java.nio.
- Arquitetura não usa Android Keystore.
- Arquitetura não usa logs.
- Arquitetura não usa `javax.crypto` diretamente no pacote payload.
- Arquitetura não usa `SecureRandom` diretamente no pacote payload.

---

## Validações Executadas

### Testes Unitários

Comando:

```bash
./gradlew testDebugUnitTest
```

Resultado:

```text
BUILD SUCCESSFUL
```

### Check Android Completo

Comando:

```bash
./scripts/check-android.sh
```

Resultado:

```text
VAULTIA_ANDROID_CHECK_OK
```

Evidências finais:

- Manifest fonte sem `android.permission.INTERNET`.
- APK compilado sem `android.permission.INTERNET`.
- Nenhum arquivo perigoso apareceu no Git status.
- APK SHA-256: `fa2f65971c8f08e02c8613a6d4f97e167137b8c2a13281061779ed1b8418e8a5`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 31.

---

## Critérios de Aceite

- `VaultPayloadEncryptionService.kt` criado.
- `VaultPayloadEncryptionRequest.kt` criado.
- `VaultPayloadEncryptionResult.kt` criado.
- `VaultPayloadEncryptionError.kt` criado.
- `EncryptedVaultPayloadDraft.kt` criado.
- Payload claro é cifrado em memória.
- Draft contém apenas payload cifrado e timestamp.
- Draft não contém key.
- Draft não contém plaintext.
- Nenhum storage real foi criado.
- Nenhuma escrita em disco foi criada.
- Nenhuma leitura de disco foi criada.
- Nenhuma UI foi alterada.
- Nenhum Android Keystore foi criado.
- Nenhuma permissão Android foi adicionada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
- Roadmap formal não foi alterado nesta fase.

---

## Commits

```text
ee2cfca docs: add phase 31 plan for vault payload encryption
be541b8 feat: add vault payload encryption draft
```

## Próxima Fase Recomendada

Phase 32 — Vault Payload Decryption Draft

Objetivo futuro: criar o fluxo inverso de decryption em memória para validar payload cifrado e recuperar plaintext transitório, ainda sem storage real e sem UI.
