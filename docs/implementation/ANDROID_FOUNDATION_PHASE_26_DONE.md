# Android Foundation — Phase 26 Done

## Status

Aprovado.

## Nome

Vault Header Serialization Contract

## Objetivo

Definir e implementar uma serialização reversível do `VaultHeader` público criado na Phase 25, permitindo converter o header entre objeto em memória e representação textual determinística, sem criar storage real, sem escrever em disco, sem payload cifrado, sem AES-GCM, sem Android Keystore e sem fluxo real de criação/desbloqueio de vault.

A Phase 26 prepara o formato persistível futuro do header, mas ainda não persiste nada.

---

## Arquivos Criados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultHeaderSerializer.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultHeaderSerializationError.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultHeaderSerializationResult.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultHeaderSerializerTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultHeaderSerializationArchitectureTest.kt
```

---

## Implementação Entregue

### VaultHeaderSerializer

- Serializador textual do `VaultHeader` criado.
- Desserializador textual do `VaultHeader` criado.
- Encode determinístico implementado.
- Decode tipado implementado.
- Round-trip preserva campos públicos do header.
- Formato textual interno criado com magic header e footer.
- Helpers internos de hexadecimal criados.
- Nenhuma dependência externa de JSON, CBOR ou Protobuf foi adicionada.
- Nenhum storage real foi criado.
- Nenhum I/O foi criado.
- Nenhum log foi criado.

Formato implementado:

```text
VAULTIA_HEADER_V1
formatVersion=<int>
kdfAlgorithm=<string>
kdfVersion=<int>
saltHex=<hex>
memoryCostKiB=<int>
iterations=<int>
parallelism=<int>
outputLengthBytes=<int>
createdAtEpochMillis=<long>
END_VAULTIA_HEADER
```

### VaultHeaderSerializationResult

- Resultado explícito de decode criado.
- `Success(header: VaultHeader)` criado.
- `Failure(error: VaultHeaderSerializationError)` criado.
- Decode não usa exceção como fluxo normal.

### VaultHeaderSerializationError

Erros tipados criados:

```text
EmptyInput
InvalidMagic
MissingField
DuplicateField
InvalidFieldValue
UnsupportedFormatVersion
UnsupportedKdfAlgorithm
UnsupportedKdfVersion
InvalidSaltEncoding
InvalidHeader
```

---

## Campos Permitidos na Serialização

A serialização contém apenas:

```text
formatVersion
kdfAlgorithm
kdfVersion
saltHex
memoryCostKiB
iterations
parallelism
outputLengthBytes
createdAtEpochMillis
```

A serialização não contém:

```text
password
passwordHash
derivedKey
vaultKey
encryptedVaultKey
ciphertext
nonce
tag
payload
filePath
storagePath
biometricKeyAlias
profileName
profileId
profile
```

---

## Segurança e Isolamento

- Nenhum storage real foi criado.
- Nenhuma escrita em disco foi criada.
- Nenhuma leitura de disco foi criada.
- Nenhum JSON definitivo foi criado.
- Nenhum CBOR definitivo foi criado.
- Nenhum Protobuf definitivo foi criado.
- Nenhum payload cifrado foi criado.
- Nenhum AES-GCM foi criado.
- Nenhum envelope encryption foi criado.
- Nenhum nonce criptográfico real foi criado.
- Nenhuma tag de autenticação foi criada.
- Nenhum Android Keystore foi implementado.
- Nenhuma biometria foi implementada.
- Nenhuma criação real de vault foi implementada.
- Nenhum desbloqueio real foi implementado.
- Nenhuma integração com UI foi criada.
- Nenhuma integração com SessionManager foi criada.
- Nenhuma integração com VaultRepository foi criada.
- Nenhuma geração real de salt por CSPRNG foi criada.
- Nenhuma permissão Android foi adicionada.
- Roadmap formal não foi alterado nesta fase.

O serializer é puro e opera apenas em memória sobre metadados públicos.

---

## Testes Entregues

- Encode de header válido contém magic inicial.
- Encode de header válido contém footer final.
- Encode de header válido contém campos permitidos.
- Encode é determinístico para o mesmo header.
- Decode de encode válido retorna `Success`.
- Round-trip preserva `formatVersion`.
- Round-trip preserva `kdfAlgorithm`.
- Round-trip preserva `kdfVersion`.
- Round-trip preserva `salt`.
- Round-trip preserva `memoryCostKiB`.
- Round-trip preserva `iterations`.
- Round-trip preserva `parallelism`.
- Round-trip preserva `outputLengthBytes`.
- Round-trip preserva `createdAtEpochMillis`.
- Input vazio retorna `EmptyInput`.
- Magic inválido retorna `InvalidMagic`.
- Campo ausente retorna `MissingField`.
- Campo duplicado retorna `DuplicateField`.
- Valor numérico inválido retorna `InvalidFieldValue`.
- Versão futura de header retorna `UnsupportedFormatVersion`.
- Algoritmo KDF desconhecido retorna `UnsupportedKdfAlgorithm`.
- Versão KDF desconhecida retorna `UnsupportedKdfVersion`.
- Salt hex inválido retorna `InvalidSaltEncoding`.
- Salt curto retorna `InvalidHeader`.
- Teste garante ausência de senha/chave/payload na serialização.
- Teste de arquitetura garante ausência de Android, storage, I/O, Room, DataStore, SQLite, Cipher, SecretKey, KeyStore, Argon2Kt, println e Log no serializer.

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
- APK SHA-256: `74bb90070d10f6f48a59e1ca128a7bcc2e8e759d5126a78eaa9b369f540fd812`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 26.

---

## Critérios de Aceite

- `VaultHeaderSerializer.kt` criado.
- `VaultHeaderSerializationError.kt` criado.
- `VaultHeaderSerializationResult.kt` criado.
- Encode determinístico implementado.
- Decode tipado implementado.
- Round-trip preserva todos os campos públicos.
- Campos secretos não aparecem na serialização.
- Erros de decode são tipados.
- Nenhum storage real foi criado.
- Nenhuma escrita em disco foi criada.
- Nenhuma leitura de disco foi criada.
- Nenhum AES-GCM foi criado.
- Nenhuma UI foi alterada.
- Nenhuma permissão Android foi adicionada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
- Roadmap formal não foi alterado nesta fase.

---

## Commits

```text
46f5d2e docs: add phase 26 plan for vault header serialization
1f5914f feat: add vault header serialization contract
```

## Próxima Fase Recomendada

Phase 27 — Vault Creation Draft Without Persistence

Objetivo futuro: criar o fluxo interno em memória para montar um vault inicial combinando política de senha mestra, perfil KDF, salt fornecido, derivação Argon2id e header serializável, ainda sem storage real e sem UI.
