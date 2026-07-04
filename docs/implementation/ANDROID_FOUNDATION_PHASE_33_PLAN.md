# Android Foundation — Phase 33 Plan

## Nome

Vault Payload Serialization Contract

## Objetivo

Definir o contrato de serialização de `EncryptedPayload` para um formato persistível, validável e versionado, ainda sem escrita real em disco, sem leitura real de disco, sem storage final, sem UI, sem Android Keystore e sem persistência operacional.

A Phase 33 cria apenas o boundary de transformação entre objeto cifrado em memória e representação textual/estrutural serializada. Ela não cria repositório, não cria arquivos reais e não salva dados do cofre.

---

## Contexto Técnico

A Phase 30 criou o boundary AES-GCM:

- `EncryptedPayload`;
- `PlaintextPayload`;
- `AuthenticatedCipher`;
- `EncryptionResult`;
- `DecryptionResult`;
- `AesGcmAuthenticatedCipher`.

A Phase 31 criou o fluxo de encryption de payload em memória.

A Phase 32 criou o fluxo de decryption de payload em memória.

A Phase 33 deve criar o contrato de serialização do payload cifrado para permitir que fases futuras persistam o conteúdo de forma controlada, sem ainda introduzir storage real.

Dependências conceituais anteriores:

```text
Phase 30 — Encryption Contract and AES-GCM Boundary
Phase 31 — Vault Payload Encryption Draft
Phase 32 — Vault Payload Decryption Draft
75% Audit Gate — Foundation Security Audit
```

---

## Decisão Arquitetural

A serialização do payload cifrado será isolada em um subcontrato do pacote de payload:

```text
com.vaultia.app.core.crypto.vault.payload
```

O contrato deve transformar `EncryptedPayload` em uma representação textual determinística com campos explícitos e versionados.

A serialização deve incluir apenas material cifrado e metadados técnicos mínimos. Não deve incluir plaintext, key, master password, salt KDF ou header de vault.

O formato deve ser simples, auditável e testável sem dependência de Android APIs, storage frameworks ou bibliotecas externas de JSON.

---

## Arquivos Planejados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadSerializedFormatVersion.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/SerializedEncryptedVaultPayload.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadSerializationResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadSerializationError.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadSerializer.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadSerializerTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/payload/VaultPayloadSerializationArchitectureTest.kt
```

---

## Contratos Planejados

### VaultPayloadSerializedFormatVersion

Versão explícita do formato serializado de payload cifrado.

Versão inicial planejada:

```text
V1_0
```

### SerializedEncryptedVaultPayload

Representação serializada em memória de um payload cifrado.

Campos planejados:

```text
formatVersion: VaultPayloadSerializedFormatVersion
nonceHex: String
ciphertextHex: String
authenticationTagHex: String
createdAtEpochMillis: Long
```

Regras:

- `nonceHex` deve representar nonce de 12 bytes.
- `ciphertextHex` não pode ser vazio.
- `authenticationTagHex` deve representar tag de 16 bytes.
- `createdAtEpochMillis` deve ser positivo.
- Não deve conter key.
- Não deve conter plaintext.
- Não deve conter master password.
- Não deve conter salt KDF.

### VaultPayloadSerializer

Objeto responsável por codificar e decodificar payload cifrado sem tocar em disco.

Métodos planejados:

```text
encode(draft: EncryptedVaultPayloadDraft): String
decode(input: String): VaultPayloadSerializationResult
```

Formato textual planejado:

```text
VAULTIA_PAYLOAD_V1
formatVersion=1
nonceHex=<hex>
ciphertextHex=<hex>
authenticationTagHex=<hex>
createdAtEpochMillis=<long>
END_VAULTIA_PAYLOAD
```

A escolha por formato textual key-value é intencional para manter auditabilidade e evitar dependência prematura de JSON ou storage.

### VaultPayloadSerializationResult

Resultado tipado do decode.

Estados planejados:

```text
Success(draft: EncryptedVaultPayloadDraft)
Failure(error: VaultPayloadSerializationError)
```

### VaultPayloadSerializationError

Erros planejados:

```text
EmptyInput
InvalidMagic
MissingField
DuplicateField
InvalidFieldValue
UnsupportedFormatVersion
InvalidNonceEncoding
InvalidCiphertextEncoding
InvalidAuthenticationTagEncoding
InvalidEncryptedPayload
InvalidTimestamp
```

---

## Requisitos de Segurança

- Não persistir key.
- Não persistir plaintext.
- Não serializar key.
- Não serializar plaintext.
- Não serializar master password.
- Não serializar salt KDF.
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

A serialização de nonce, ciphertext e authentication tag é permitida porque esses campos são necessários para persistência futura do payload cifrado. Nesta fase, eles permanecem apenas em memória e em testes.

---

## Escopo

Esta fase cria:

- versão do formato serializado;
- modelo serializado em memória;
- serializer de encode;
- serializer de decode;
- resultado tipado de decode;
- erro tipado de serialização;
- testes de round-trip;
- testes de campos obrigatórios;
- testes de campos duplicados;
- testes de versão incompatível;
- testes de hex inválido;
- testes de ausência de key/plaintext no formato;
- testes de arquitetura;
- relatório DONE.

---

## Fora de Escopo

Esta fase não implementa:

- storage real;
- escrita em disco;
- leitura de disco;
- arquivo final do vault;
- repositório persistente de itens;
- SQLite;
- Room;
- DataStore;
- SharedPreferences;
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

- encode gera magic header correto.
- encode gera footer correto.
- encode inclui formatVersion.
- encode inclui nonceHex.
- encode inclui ciphertextHex.
- encode inclui authenticationTagHex.
- encode inclui createdAtEpochMillis.
- encode não inclui key.
- encode não inclui plaintext.
- decode rejeita input vazio.
- decode rejeita magic inválido.
- decode rejeita campo ausente.
- decode rejeita campo duplicado.
- decode rejeita versão incompatível.
- decode rejeita nonceHex inválido.
- decode rejeita ciphertextHex inválido.
- decode rejeita authenticationTagHex inválido.
- decode rejeita timestamp inválido.
- decode reconstrói `EncryptedVaultPayloadDraft` válido.
- round-trip encode/decode preserva nonce.
- round-trip encode/decode preserva ciphertext.
- round-trip encode/decode preserva authentication tag.
- round-trip encode/decode preserva timestamp.
- arquitetura não importa Android UI.
- arquitetura não importa storage.
- arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- arquitetura não importa java.io ou java.nio.
- arquitetura não usa Android Keystore.
- arquitetura não usa logs.
- arquitetura não usa `javax.crypto` diretamente no pacote serializer.
- arquitetura não usa `SecureRandom` diretamente no pacote serializer.

---

## Critérios de Aceite

- `VaultPayloadSerializedFormatVersion.kt` criado.
- `SerializedEncryptedVaultPayload.kt` criado.
- `VaultPayloadSerializationResult.kt` criado.
- `VaultPayloadSerializationError.kt` criado.
- `VaultPayloadSerializer.kt` criado.
- Payload cifrado pode ser codificado para texto em memória.
- Texto serializado pode ser decodificado para `EncryptedVaultPayloadDraft`.
- Formato serializado é versionado.
- Formato serializado não contém key.
- Formato serializado não contém plaintext.
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
