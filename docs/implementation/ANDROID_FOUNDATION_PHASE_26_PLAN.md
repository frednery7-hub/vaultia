# Android Foundation — Phase 26 Plan

## Nome

Vault Header Serialization Contract

## Objetivo

Definir e implementar uma serialização reversível do `VaultHeader` público criado na Phase 25, permitindo converter o header entre objeto em memória e representação textual determinística, sem criar storage real, sem escrever em disco, sem payload cifrado, sem AES-GCM, sem Android Keystore e sem fluxo real de criação/desbloqueio de vault.

A Phase 26 prepara o formato persistível futuro do header, mas ainda não persiste nada.

---

## Decisão Arquitetural

A serialização do header deve ser determinística, reversível e restrita a metadados públicos.

Nesta fase, o formato deve ser um contrato interno simples, sem dependência de bibliotecas externas de JSON, CBOR ou Protobuf.

Motivo: o projeto ainda não implementa storage real nem payload cifrado. O foco desta fase é garantir que o header público possa fazer round-trip controlado e validado.

---

## Arquivos Planejados

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

## Formato Inicial Planejado

O formato inicial será textual, versionado e determinístico.

Formato lógico:

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

O formato não é uma decisão final de storage. Ele é um contrato interno de serialização reversível para preparar a fase posterior de persistência.

---

## Campos Permitidos

A serialização pode conter apenas:

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

A serialização não deve conter:

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

## Contratos Planejados

### VaultHeaderSerializer

Responsável por converter:

```text
VaultHeader -> String
String -> VaultHeaderSerializationResult
```

O serializer deve ser puro, sem Android framework, sem storage, sem I/O, sem logs e sem dependências externas.

### VaultHeaderSerializationResult

Resultado explícito de decode:

```text
Success(header: VaultHeader)
Failure(error: VaultHeaderSerializationError)
```

### VaultHeaderSerializationError

Erros planejados:

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

O decode não deve lançar exceção como fluxo normal. Deve retornar `Failure` tipado.

---

## Regras de Validação

- Input vazio deve falhar com `EmptyInput`.
- Magic header ausente ou incorreto deve falhar com `InvalidMagic`.
- Campo obrigatório ausente deve falhar com `MissingField`.
- Campo duplicado deve falhar com `DuplicateField`.
- Valor numérico inválido deve falhar com `InvalidFieldValue`.
- Versão de formato incompatível deve falhar com `UnsupportedFormatVersion`.
- Algoritmo KDF desconhecido deve falhar com `UnsupportedKdfAlgorithm`.
- Versão KDF desconhecida deve falhar com `UnsupportedKdfVersion`.
- Salt hexadecimal inválido deve falhar com `InvalidSaltEncoding`.
- Header reconstruído com parâmetros inválidos deve falhar com `InvalidHeader`.
- Salt decodificado deve respeitar mínimo de 16 bytes.
- Ordem de encode deve ser fixa e determinística.
- Decode deve reconstruir os mesmos parâmetros públicos do header original.

---

## Escopo

Esta fase cria:

- serializador textual do `VaultHeader`;
- desserializador textual do `VaultHeader`;
- resultado tipado de decode;
- erros tipados de serialização/desserialização;
- helpers internos para hex encode/decode;
- testes de round-trip;
- testes de corrupção/erro;
- testes de ausência de campos secretos;
- testes de arquitetura;
- relatório DONE.

---

## Fora de Escopo

Esta fase não implementa:

- storage real;
- escrita em disco;
- leitura de disco;
- JSON definitivo;
- CBOR definitivo;
- Protobuf definitivo;
- payload cifrado;
- AES-GCM;
- envelope encryption;
- nonce criptográfico real;
- tag de autenticação;
- Android Keystore;
- biometria;
- criação real de vault;
- desbloqueio real;
- integração com UI;
- integração com SessionManager;
- integração com VaultRepository;
- geração real de salt por CSPRNG;
- alteração no roadmap formal.

---

## Testes Planejados

Cobertura mínima:

- encode de header válido contém magic inicial.
- encode de header válido contém footer final.
- encode de header válido contém apenas campos permitidos.
- encode é determinístico para o mesmo header.
- decode de encode válido retorna `Success`.
- round-trip preserva `formatVersion`.
- round-trip preserva `kdfAlgorithm`.
- round-trip preserva `kdfVersion`.
- round-trip preserva `salt`.
- round-trip preserva `memoryCostKiB`.
- round-trip preserva `iterations`.
- round-trip preserva `parallelism`.
- round-trip preserva `outputLengthBytes`.
- round-trip preserva `createdAtEpochMillis`.
- input vazio retorna `EmptyInput`.
- magic inválido retorna `InvalidMagic`.
- campo ausente retorna `MissingField`.
- campo duplicado retorna `DuplicateField`.
- valor inválido retorna `InvalidFieldValue`.
- versão futura de header retorna `UnsupportedFormatVersion`.
- algoritmo KDF desconhecido retorna `UnsupportedKdfAlgorithm`.
- versão KDF desconhecida retorna `UnsupportedKdfVersion`.
- salt hex inválido retorna `InvalidSaltEncoding`.
- salt curto retorna `InvalidHeader`.
- teste garante ausência de senha/chave/payload.
- teste de arquitetura garante ausência de Android, storage, Room, DataStore, SQLite, Cipher, SecretKey, KeyStore e Argon2Kt.

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
- Nenhum storage real é criado.
- Nenhuma escrita em disco é criada.
- Nenhuma leitura de disco é criada.
- Nenhum AES-GCM é criado.
- Nenhuma UI é alterada.
- Nenhuma permissão Android é adicionada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
- Roadmap formal não é alterado nesta fase.
