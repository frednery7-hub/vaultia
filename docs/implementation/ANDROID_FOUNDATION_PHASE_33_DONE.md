# Android Foundation — Phase 33 Done

## Status

Aprovado.

## Nome

Vault Payload Serialization Contract

## Objetivo

Definir o contrato de serialização de `EncryptedPayload` para um formato persistível, validável e versionado, ainda sem escrita real em disco, sem leitura real de disco, sem storage final, sem UI, sem Android Keystore e sem persistência operacional.

A Phase 33 criou apenas o boundary de transformação entre objeto cifrado em memória e representação textual serializada. Ela não criou repositório, não criou arquivos reais e não salvou dados do cofre.

---

## Arquivos Criados

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

## Arquivos Modificados

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultHeaderSerializationArchitectureTest.kt
```

A alteração no teste antigo foi cirúrgica: o `VaultHeaderSerializationArchitectureTest` passou a ignorar o subpacote `payload`, porque a Phase 33 introduziu uma auditoria própria para serialização de payload.

---

## Implementação Entregue

### VaultPayloadSerializedFormatVersion

- Enum de versão do formato serializado criado.
- Versão inicial: `V1_0`.
- Valor codificado: `1`.
- Método de resolução por valor codificado criado.

### SerializedEncryptedVaultPayload

- Modelo serializado em memória criado.
- Contém `formatVersion`.
- Contém `nonceHex`.
- Contém `ciphertextHex`.
- Contém `authenticationTagHex`.
- Contém `createdAtEpochMillis`.
- Valida nonce de 12 bytes em hex.
- Valida ciphertext hex não vazio.
- Valida authentication tag de 16 bytes em hex.
- Valida timestamp positivo.
- Não contém key.
- Não contém plaintext.

### VaultPayloadSerializer

- Serializer de payload cifrado criado.
- `encode` transforma `EncryptedVaultPayloadDraft` em texto key-value.
- `decode` transforma texto key-value em `EncryptedVaultPayloadDraft`.
- Formato inclui magic header `VAULTIA_PAYLOAD_V1`.
- Formato inclui footer `END_VAULTIA_PAYLOAD`.
- Formato inclui `formatVersion=1`.
- Formato inclui nonce, ciphertext e tag em hex.
- Formato inclui timestamp de criação.
- Decode valida campo ausente.
- Decode valida campo duplicado.
- Decode valida versão incompatível.
- Decode valida hex inválido.
- Decode valida timestamp inválido.
- Decode reconstrói `EncryptedPayload` e `EncryptedVaultPayloadDraft`.
- Não toca em disco.

### VaultPayloadSerializationError

Erros tipados criados:

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

### VaultPayloadSerializationResult

- Resultado tipado de decode criado.
- `Success` carrega `EncryptedVaultPayloadDraft`.
- `Failure` carrega `VaultPayloadSerializationError`.

---

## Formato Implementado

```text
VAULTIA_PAYLOAD_V1
formatVersion=1
nonceHex=<hex>
ciphertextHex=<hex>
authenticationTagHex=<hex>
createdAtEpochMillis=<long>
END_VAULTIA_PAYLOAD
```

O formato é textual, determinístico, versionado e testável sem dependência de Android APIs, storage frameworks ou biblioteca externa de JSON.

---

## Segurança e Isolamento

- Nenhuma key foi persistida.
- Nenhum plaintext foi persistido.
- Nenhuma key foi serializada.
- Nenhum plaintext foi serializado.
- Nenhuma master password foi serializada.
- Nenhum salt KDF foi serializado.
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

A serialização de nonce, ciphertext e authentication tag é permitida porque esses campos são necessários para persistência futura do payload cifrado. Nesta fase, eles permanecem apenas em memória e em testes.

---

## Testes Entregues

- Versão do formato mapeia valor codificado.
- Modelo serializado rejeita nonce inválido.
- Modelo serializado rejeita ciphertext vazio.
- Modelo serializado rejeita tag inválida.
- Modelo serializado rejeita timestamp inválido.
- Encode gera magic header correto.
- Encode gera footer correto.
- Encode inclui formatVersion.
- Encode inclui nonceHex.
- Encode inclui ciphertextHex.
- Encode inclui authenticationTagHex.
- Encode inclui createdAtEpochMillis.
- Encode não inclui key.
- Encode não inclui plaintext.
- Encode não inclui password.
- Encode não inclui salt.
- Decode rejeita input vazio.
- Decode rejeita magic inválido.
- Decode rejeita campo ausente.
- Decode rejeita campo duplicado.
- Decode rejeita versão incompatível.
- Decode rejeita nonceHex inválido.
- Decode rejeita ciphertextHex inválido.
- Decode rejeita authenticationTagHex inválido.
- Decode rejeita timestamp inválido.
- Decode rejeita tamanho inválido de nonce/tag.
- Round-trip encode/decode preserva nonce.
- Round-trip encode/decode preserva ciphertext.
- Round-trip encode/decode preserva authentication tag.
- Round-trip encode/decode preserva timestamp.
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
- APK SHA-256: `18b9941d6bb9751a16540516a83da4fd12df8d935e60a6fa7f6bf9f72cd2c5e4`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 33.

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
f9b4530 docs: add phase 33 plan for vault payload serialization
4ef7413 feat: add vault payload serialization contract
```

## Próxima Fase Recomendada

Phase 34 — Vault Item Encrypted Draft Contract

Objetivo futuro: conectar payload cifrado serializável a um draft de item de vault, ainda sem storage real, sem UI e sem persistência operacional.
