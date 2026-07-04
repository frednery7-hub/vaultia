# Android Foundation — Phase 32 Done

## Status

Aprovado.

## Nome

Vault Payload Decryption Draft

## Objetivo

Criar o fluxo inverso da Phase 31: receber key e `EncryptedPayload` em memória, usar `AuthenticatedCipher.decrypt`, validar falhas de autenticação/decryption e retornar um draft de plaintext transitório, ainda sem storage real, sem UI, sem Android Keystore e sem persistência.

A Phase 32 valida o caminho de decryption em memória. Ela não cria leitura de arquivo, não recupera item persistido e não grava plaintext em disco.

---

## Arquivos Criados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadDecryptionService.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadDecryptionRequest.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadDecryptionResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadDecryptionError.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/DecryptedVaultPayloadDraft.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadDecryptionServiceTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadDecryptionArchitectureTest.kt
```

---

## Implementação Entregue

### VaultPayloadDecryptionRequest

- Request de decryption de payload em memória criado.
- Recebe `key`.
- Recebe `EncryptedPayload`.
- Recebe `decryptedAtEpochMillis`.
- Rejeita key vazia.
- Rejeita timestamp inválido.
- Protege key com cópia defensiva.
- Recria `EncryptedPayload` internamente com cópias defensivas.
- Não persiste dados.

### VaultPayloadDecryptionService

- Service de decryption de payload em memória criado.
- Recebe `VaultPayloadDecryptionRequest`.
- Chama `AuthenticatedCipher.decrypt`.
- Mapeia sucesso para `DecryptedVaultPayloadDraft`.
- Mapeia `EncryptionError.AuthenticationFailed` para `VaultPayloadDecryptionError.AuthenticationFailed`.
- Mapeia demais falhas para `VaultPayloadDecryptionError.DecryptionFailed`.
- Não lê disco.
- Não grava disco.

### DecryptedVaultPayloadDraft

- Draft de payload decifrado transitório em memória criado.
- Contém `PlaintextPayload`.
- Contém timestamp de decryption.
- Rejeita timestamp inválido.
- Recria `PlaintextPayload` internamente com cópia defensiva.
- Não contém key.
- Não persiste plaintext.

### VaultPayloadDecryptionError

Erros tipados criados:

```text
InvalidRequest
AuthenticationFailed
DecryptionFailed
```

### VaultPayloadDecryptionResult

- Resultado tipado criado.
- `Success` carrega `DecryptedVaultPayloadDraft`.
- `Failure` carrega `VaultPayloadDecryptionError`.

---

## Fluxo Implementado

```text
1. Recebe key, EncryptedPayload e timestamp
2. Valida request
3. Chama AuthenticatedCipher.decrypt
4. Rejeita falha de autenticação/decryption
5. Recebe PlaintextPayload
6. Retorna DecryptedVaultPayloadDraft em memória
```

Nenhuma etapa lê ou escreve em disco.

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
- Draft contém plaintext apenas como resultado transitório em memória.
- Draft não contém key.
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
- Request rejeita timestamp inválido.
- Request protege key com cópia defensiva.
- Request protege `EncryptedPayload` com cópia defensiva.
- Service chama `AuthenticatedCipher.decrypt`.
- Service envia key correta ao cipher.
- Service envia `EncryptedPayload` correto ao cipher.
- Service retorna `DecryptedVaultPayloadDraft` em sucesso.
- Draft preserva timestamp.
- Draft contém `PlaintextPayload` transitório.
- Draft rejeita timestamp inválido.
- Draft não expõe campos com key.
- Falha de autenticação retorna `AuthenticationFailed`.
- Falha genérica de decryption retorna `DecryptionFailed`.
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
- APK SHA-256: `367805cf1c5ed314e96461af8c24eccafae6dcc0c2540ef306a18ff6e8bc364c`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 32.

---

## Critérios de Aceite

- `VaultPayloadDecryptionService.kt` criado.
- `VaultPayloadDecryptionRequest.kt` criado.
- `VaultPayloadDecryptionResult.kt` criado.
- `VaultPayloadDecryptionError.kt` criado.
- `DecryptedVaultPayloadDraft.kt` criado.
- Payload cifrado é decifrado em memória.
- Draft contém plaintext transitório e timestamp.
- Draft não contém key.
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
91166b0 docs: add phase 32 plan for vault payload decryption
b2751fb feat: add vault payload decryption draft
```

## Próxima Fase Recomendada

Phase 33 — Vault Payload Serialization Contract

Objetivo futuro: definir contrato de serialização do payload cifrado para formato persistível, ainda sem escrita real em disco e sem storage final.
