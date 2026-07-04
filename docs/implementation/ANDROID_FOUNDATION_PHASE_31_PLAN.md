# Android Foundation — Phase 31 Plan

## Nome

Vault Payload Encryption Draft

## Objetivo

Conectar o boundary AES-GCM criado na Phase 30 ao modelo de payload do vault em memória, criando um draft cifrado de payload sem storage real, sem serialização em arquivo, sem UI, sem Android Keystore e sem persistência.

A Phase 31 começa a representar dados reais do cofre como payload cifrado em memória, mas ainda não cria itens persistidos nem grava nada em disco.

---

## Contexto Técnico

A Phase 30 criou:

- `AuthenticatedCipher`;
- `PlaintextPayload`;
- `EncryptedPayload`;
- `EncryptionResult`;
- `DecryptionResult`;
- `EncryptionError`;
- `AesGcmAuthenticatedCipher`.

A Phase 31 deve criar uma camada de draft de payload cifrado em memória, conectando esse boundary ao domínio do vault sem ainda introduzir storage.

Dependências conceituais anteriores:

```text
Phase 23 — KDF Contract and Safe Models
Phase 27 — Vault Creation Draft Without Persistence
Phase 29 — Vault Creation With Generated Salt
Phase 30 — Encryption Contract and AES-GCM Boundary
75% Audit Gate — Foundation Security Audit
```

---

## Decisão Arquitetural

A criação de payload cifrado será isolada em um pacote próprio dentro do core crypto/vault, sem acoplar a storage, UI ou Android APIs.

A Phase 31 deve aceitar bytes claros em memória, cifrar usando `AuthenticatedCipher` e retornar um draft contendo somente o payload cifrado e metadados mínimos de operação.

O plaintext deve existir apenas como entrada transitória e não deve ser armazenado no objeto de saída.

---

## Arquivos Planejados

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

---

## Contratos Planejados

### VaultPayloadEncryptionRequest

Entrada do fluxo de cifragem de payload em memória.

Campos planejados:

```text
key: ByteArray
plaintext: ByteArray
createdAtEpochMillis: Long
```

Regras:

- key deve usar cópia defensiva;
- plaintext deve usar cópia defensiva;
- plaintext vazio deve ser rejeitado;
- timestamp deve ser positivo;
- request não deve persistir dados.

### VaultPayloadEncryptionService

Serviço responsável por cifrar payloads de vault em memória.

Método planejado:

```text
encrypt(request: VaultPayloadEncryptionRequest): VaultPayloadEncryptionResult
```

Fluxo planejado:

```text
1. Recebe key, plaintext e timestamp
2. Valida request
3. Cria PlaintextPayload em memória
4. Chama AuthenticatedCipher.encrypt
5. Rejeita falha de criptografia
6. Recebe EncryptedPayload
7. Retorna EncryptedVaultPayloadDraft
```

Nenhuma etapa deve escrever em disco.

### EncryptedVaultPayloadDraft

Draft de payload cifrado em memória.

Campos planejados:

```text
encryptedPayload: EncryptedPayload
createdAtEpochMillis: Long
```

Regras:

- não deve conter plaintext;
- não deve conter key;
- não deve persistir ciphertext;
- deve manter apenas payload cifrado em memória.

### VaultPayloadEncryptionError

Erros planejados:

```text
InvalidRequest
EncryptionFailed
```

O service deve mapear falhas de `AuthenticatedCipher` para `EncryptionFailed` sem vazar detalhes sensíveis.

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

A limpeza determinística de memória em JVM/Android continua fora do escopo desta fase, mas o tempo de vida do plaintext deve ser minimizado.

---

## Escopo

Esta fase cria:

- request de cifragem de payload em memória;
- service de cifragem de payload em memória;
- resultado tipado;
- erro tipado;
- draft de payload cifrado em memória;
- testes de sucesso;
- testes de falha do cipher;
- testes de validação do request;
- testes de ausência de plaintext/key no draft;
- testes de arquitetura;
- relatório DONE.

---

## Fora de Escopo

Esta fase não implementa:

- storage real;
- escrita em disco;
- leitura de disco;
- serialização final de payload cifrado em arquivo;
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

- request rejeita plaintext vazio.
- request rejeita timestamp inválido.
- request protege key com cópia defensiva.
- request protege plaintext com cópia defensiva.
- service chama `AuthenticatedCipher.encrypt`.
- service retorna `EncryptedVaultPayloadDraft` em sucesso.
- draft não contém key.
- draft não contém plaintext.
- falha do cipher retorna `EncryptionFailed`.
- arquitetura não importa Android UI.
- arquitetura não importa storage.
- arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- arquitetura não importa java.io ou java.nio.
- arquitetura não usa Android Keystore.
- arquitetura não usa logs.

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
