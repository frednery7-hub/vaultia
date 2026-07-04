# Android Foundation — Phase 30 Done

## Status

Aprovado.

## Nome

Encryption Contract and AES-GCM Boundary

## Objetivo

Criar contratos de criptografia autenticada para payloads do vault, definindo plaintext, ciphertext, nonce, authentication tag, erros tipados e interface de encrypt/decrypt, preparando o boundary para AES-GCM sem criar storage real, sem UI, sem Android Keystore e sem persistência.

A Phase 30 cria a camada contratual e testável para criptografia autenticada antes de qualquer escrita em disco.

---

## Arquivos Criados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/AuthenticatedCipher.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/EncryptionError.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/EncryptionResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/DecryptionResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/EncryptedPayload.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/PlaintextPayload.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/AesGcmAuthenticatedCipher.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/encryption/EncryptedPayloadTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/encryption/PlaintextPayloadTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/encryption/AesGcmAuthenticatedCipherTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/encryption/EncryptionArchitectureTest.kt
```

---

## Implementação Entregue

### AuthenticatedCipher

- Interface de criptografia autenticada criada.
- Método `encrypt(key, plaintext)` criado.
- Método `decrypt(key, encryptedPayload)` criado.
- Opera sobre bytes em memória.
- Retorna resultados tipados.
- Não usa exceção como caminho normal.

### PlaintextPayload

- Modelo de payload claro criado.
- Rejeita payload vazio.
- Protege bytes com cópia defensiva.
- Expõe `plaintextLengthBytes`.
- Não persiste conteúdo.

### EncryptedPayload

- Modelo de payload cifrado autenticado criado.
- Contém `nonce`.
- Contém `ciphertext`.
- Contém `authenticationTag`.
- Nonce fixado em 12 bytes.
- Authentication tag fixada em 16 bytes / 128 bits.
- Rejeita ciphertext vazio.
- Protege arrays com cópia defensiva.
- Não contém plaintext.

### EncryptionError

Erros tipados criados:

```text
InvalidKey
InvalidPlaintext
InvalidEncryptedPayload
EncryptionFailed
AuthenticationFailed
DecryptionFailed
NonceGenerationFailed
```

### AesGcmAuthenticatedCipher

- Implementação concreta AES-GCM criada.
- Usa `AES/GCM/NoPadding`.
- Usa `GCMParameterSpec`.
- Usa `SecretKeySpec`.
- Usa `SecureRandom` para geração de nonce.
- Gera nonce novo por operação de encrypt.
- Usa nonce de 12 bytes.
- Usa tag de 128 bits.
- Separa ciphertext e authentication tag no modelo.
- Chave aceita: 16, 24 ou 32 bytes.
- Falha de autenticação retorna `AuthenticationFailed`.
- Falha de geração de nonce retorna `NonceGenerationFailed`.
- Não usa Android Keystore.
- Não usa storage real.

---

## Segurança e Isolamento

- Nonce novo por operação de encryption.
- Nonce de 12 bytes.
- Tag de autenticação de 16 bytes / 128 bits.
- Plaintext vazio é rejeitado.
- Ciphertext vazio é rejeitado.
- Chave inválida é rejeitada.
- Dados adulterados falham na autenticação.
- Nonce adulterado falha na autenticação.
- Tag adulterada falha na autenticação.
- Arrays internos não são expostos por referência.
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

- `PlaintextPayload` rejeita bytes vazios.
- `PlaintextPayload` protege bytes com cópia defensiva.
- `EncryptedPayload` rejeita nonce inválido.
- `EncryptedPayload` rejeita ciphertext vazio.
- `EncryptedPayload` rejeita tag inválida.
- `EncryptedPayload` protege arrays com cópia defensiva.
- AES-GCM rejeita chave inválida no encrypt.
- AES-GCM rejeita chave inválida no decrypt.
- AES-GCM mapeia falha de nonce para `NonceGenerationFailed`.
- AES-GCM encrypt gera nonce de 12 bytes.
- AES-GCM encrypt gera tag de 16 bytes.
- AES-GCM encrypt não retorna plaintext como ciphertext.
- AES-GCM decrypt recupera plaintext original.
- AES-GCM decrypt falha com ciphertext adulterado.
- AES-GCM decrypt falha com tag adulterada.
- AES-GCM decrypt falha com nonce adulterado.
- Duas chamadas de encrypt com mesma chave e plaintext geram nonces diferentes.
- Arquitetura não importa Android UI.
- Arquitetura não importa storage.
- Arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- Arquitetura não importa java.io ou java.nio.
- Arquitetura não usa Android Keystore.
- Arquitetura não usa logs.
- Arquitetura confirma uso esperado de `AES/GCM/NoPadding`, `GCMParameterSpec`, `SecureRandom` e `AEADBadTagException`.

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
- APK SHA-256: `8a020a896fe6c1226d7cfd75d014996f4fcab2f1a70732006a137f6735e214b8`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 30.

---

## Critérios de Aceite

- `AuthenticatedCipher.kt` criado.
- `PlaintextPayload.kt` criado.
- `EncryptedPayload.kt` criado.
- `EncryptionError.kt` criado.
- `EncryptionResult.kt` criado.
- `DecryptionResult.kt` criado.
- `AesGcmAuthenticatedCipher.kt` criado.
- AES-GCM usa nonce de 12 bytes.
- AES-GCM usa tag de 128 bits.
- Encrypt/decrypt round-trip funciona.
- Dados adulterados falham.
- Arrays usam cópia defensiva.
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
8a01aa6 docs: add phase 30 plan for encryption boundary
6a68da5 feat: add aes-gcm encryption boundary
```

## Próxima Fase Recomendada

Phase 31 — Vault Payload Encryption Draft

Objetivo futuro: conectar o boundary AES-GCM ao modelo de payload do vault em memória, ainda sem storage real e sem UI.
