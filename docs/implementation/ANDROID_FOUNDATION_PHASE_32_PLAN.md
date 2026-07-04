# Android Foundation — Phase 32 Plan

## Nome

Vault Payload Decryption Draft

## Objetivo

Criar o fluxo inverso da Phase 31: receber key e `EncryptedPayload` em memória, usar `AuthenticatedCipher.decrypt`, validar falhas de autenticação/decryption e retornar um draft de plaintext transitório, ainda sem storage real, sem UI, sem Android Keystore e sem persistência.

A Phase 32 não cria leitura de arquivo, não recupera item persistido e não grava plaintext em disco. Ela apenas valida o caminho de decryption em memória.

---

## Contexto Técnico

A Phase 30 criou o boundary AES-GCM:

- `AuthenticatedCipher`;
- `PlaintextPayload`;
- `EncryptedPayload`;
- `EncryptionResult`;
- `DecryptionResult`;
- `AesGcmAuthenticatedCipher`.

A Phase 31 criou o draft de encryption de payload:

- `VaultPayloadEncryptionRequest`;
- `VaultPayloadEncryptionService`;
- `EncryptedVaultPayloadDraft`;
- `VaultPayloadEncryptionResult`;
- `VaultPayloadEncryptionError`.

A Phase 32 deve criar a camada complementar de decryption em memória, mantendo o mesmo isolamento arquitetural.

Dependências conceituais anteriores:

```text
Phase 30 — Encryption Contract and AES-GCM Boundary
Phase 31 — Vault Payload Encryption Draft
75% Audit Gate — Foundation Security Audit
```

---

## Decisão Arquitetural

A decryption de payload será implementada no mesmo pacote da Phase 31:

```text
com.vaultia.app.core.crypto.vault.payload
```

O serviço deve depender apenas do contrato `AuthenticatedCipher`, não da implementação concreta AES-GCM.

O plaintext retornado deve ser tratado como transitório. Ele pode existir no resultado da operação para permitir testes e fluxos futuros, mas não deve ser persistido, logado ou conectado à UI nesta fase.

---

## Arquivos Planejados

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

## Contratos Planejados

### VaultPayloadDecryptionRequest

Entrada do fluxo de decryption de payload em memória.

Campos planejados:

```text
key: ByteArray
encryptedPayload: EncryptedPayload
decryptedAtEpochMillis: Long
```

Regras:

- key deve usar cópia defensiva;
- key vazia deve ser rejeitada;
- timestamp deve ser positivo;
- request não deve persistir dados;
- request não deve expor key por referência.

### VaultPayloadDecryptionService

Serviço responsável por decifrar payloads de vault em memória.

Método planejado:

```text
decrypt(request: VaultPayloadDecryptionRequest): VaultPayloadDecryptionResult
```

Fluxo planejado:

```text
1. Recebe key, EncryptedPayload e timestamp
2. Valida request
3. Chama AuthenticatedCipher.decrypt
4. Rejeita falha de autenticação/decryption
5. Recebe PlaintextPayload
6. Retorna DecryptedVaultPayloadDraft em memória
```

Nenhuma etapa deve ler ou escrever disco.

### DecryptedVaultPayloadDraft

Draft de payload decifrado transitório em memória.

Campos planejados:

```text
plaintextPayload: PlaintextPayload
decryptedAtEpochMillis: Long
```

Regras:

- contém plaintext apenas de forma transitória;
- não deve conter key;
- não deve persistir plaintext;
- timestamp deve ser positivo.

### VaultPayloadDecryptionError

Erros planejados:

```text
InvalidRequest
AuthenticationFailed
DecryptionFailed
```

O service deve mapear falhas de `AuthenticatedCipher` sem vazar key, plaintext, ciphertext, nonce ou tag.

---

## Requisitos de Segurança

- Não persistir key.
- Não persistir plaintext.
- Não persistir ciphertext.
- Não logar key.
- Não logar plaintext.
- Não logar ciphertext.
- Não logar nonce.
- Não logar authentication tag.
- Não criar arquivo.
- Não escrever em disco.
- Não ler disco.
- Não criar storage real.
- Não criar SQLite.
- Não criar Room.
- Não criar DataStore.
- Não criar SharedPreferences.
- Não criar UI.
- Não adicionar permissão Android.
- Não adicionar `android.permission.INTERNET`.
- Não implementar Android Keystore.
- Não implementar biometria.

O plaintext decifrado é sensível e deve permanecer transitório. A limpeza determinística de memória em JVM/Android continua fora do escopo desta fase.

---

## Escopo

Esta fase cria:

- request de decryption de payload em memória;
- service de decryption de payload em memória;
- resultado tipado;
- erro tipado;
- draft de plaintext transitório em memória;
- testes de sucesso;
- testes de falha de autenticação;
- testes de falha genérica de decryption;
- testes de validação do request;
- testes de ausência de key no draft;
- testes de arquitetura;
- relatório DONE.

---

## Fora de Escopo

Esta fase não implementa:

- storage real;
- escrita em disco;
- leitura de disco;
- serialização final de payload cifrado;
- criação final de vault persistido;
- repositório persistente de itens;
- UI;
- onboarding;
- login real;
- desbloqueio real;
- Android Keystore;
- biometria;
- backup/export;
- modo discreto/decoy vault;
- alteração no roadmap formal.

---

## Testes Planejados

Cobertura mínima:

- request rejeita key vazia.
- request rejeita timestamp inválido.
- request protege key com cópia defensiva.
- service chama `AuthenticatedCipher.decrypt`.
- service retorna `DecryptedVaultPayloadDraft` em sucesso.
- draft contém `PlaintextPayload` transitório.
- draft não contém key.
- falha de autenticação retorna `AuthenticationFailed`.
- falha genérica de decryption retorna `DecryptionFailed`.
- arquitetura não importa Android UI.
- arquitetura não importa storage.
- arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- arquitetura não importa java.io ou java.nio.
- arquitetura não usa Android Keystore.
- arquitetura não usa logs.
- arquitetura não usa `javax.crypto` diretamente no pacote payload.
- arquitetura não usa `SecureRandom` diretamente no pacote payload.

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
- Nenhum storage real é criado.
- Nenhuma escrita em disco é criada.
- Nenhuma leitura de disco é criada.
- Nenhuma UI é alterada.
- Nenhum Android Keystore é criado.
- Nenhuma permissão Android é adicionada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
- Roadmap formal não é alterado nesta fase.
