# Android Foundation — Phase 35 Plan

## Nome

Vault Item Serialization Contract

## Objetivo

Definir um contrato serializado para o item criptografado completo, transformando `EncryptedVaultItemDraft` em uma representação textual, versionada, validável e persistível em fases futuras, ainda sem storage real, sem escrita em disco, sem leitura de disco, sem UI, sem Android Keystore e sem persistência operacional.

A Phase 35 não cria arquivo final de vault e não cria repositório persistente. Ela cria apenas o boundary de serialização do item criptografado completo em memória.

---

## Contexto Técnico

As fases anteriores criaram:

- Phase 30 — boundary AES-GCM.
- Phase 31 — encryption de payload em memória.
- Phase 32 — decryption de payload em memória.
- Phase 33 — serialização textual versionada de payload cifrado.
- Phase 34 — draft de item criptografado em memória.

A Phase 35 deve criar o próximo contrato: serializar o item completo, preservando ID técnico, tipo técnico, timestamps e payload cifrado serializado, sem introduzir armazenamento real.

Dependências conceituais anteriores:

```text
Phase 33 — Vault Payload Serialization Contract
Phase 34 — Vault Item Encrypted Draft Contract
75% Audit Gate — Foundation Security Audit
```

---

## Decisão Arquitetural

A serialização do item criptografado será implementada no pacote:

```text
com.vaultia.app.core.crypto.vault.item
```

O contrato deve produzir texto determinístico, versionado e auditável. O serializer deve depender do contrato já criado na Phase 34 e do payload serializado criado na Phase 33.

O item serializado deve conter apenas:

- versão do formato do item;
- id técnico do item;
- tipo técnico do item;
- timestamps;
- payload cifrado serializado.

O item serializado não deve conter key, plaintext, master password, salt KDF, título real, senha, nota clara, URI ou caminho de arquivo.

---

## Arquivos Planejados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/item/VaultItemSerializedFormatVersion.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/item/SerializedEncryptedVaultItem.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/item/VaultItemSerializationError.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/item/VaultItemSerializationResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/item/VaultItemSerializer.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/item/VaultItemSerializerTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/item/VaultItemSerializationArchitectureTest.kt
```

---

## Contratos Planejados

### VaultItemSerializedFormatVersion

Versão explícita do formato serializado de item criptografado.

Versão inicial planejada:

```text
V1_0
```

### SerializedEncryptedVaultItem

Representação serializada em memória de um item criptografado completo.

Campos planejados:

```text
formatVersion: VaultItemSerializedFormatVersion
id: String
type: EncryptedVaultItemType
createdAtEpochMillis: Long
updatedAtEpochMillis: Long
serializedPayload: SerializedEncryptedVaultPayload
```

Regras:

- id deve obedecer `EncryptedVaultItemId`.
- type deve mapear para tipo técnico conhecido.
- timestamps devem ser positivos.
- update não pode ser anterior à criação.
- payload serializado deve ser válido conforme Phase 33.
- não deve conter key.
- não deve conter plaintext.
- não deve conter título real.
- não deve conter URI/path.

### VaultItemSerializer

Objeto responsável por codificar e decodificar item criptografado sem tocar em disco.

Métodos planejados:

```text
encode(draft: EncryptedVaultItemDraft): String
decode(input: String): VaultItemSerializationResult
```

Formato textual planejado:

```text
VAULTIA_ITEM_V1
formatVersion=1
id=<technical-id>
type=<PASSWORD|NOTE|PHOTO|DOCUMENT>
createdAtEpochMillis=<long>
updatedAtEpochMillis=<long>
payloadFormatVersion=1
payloadNonceHex=<hex>
payloadCiphertextHex=<hex>
payloadAuthenticationTagHex=<hex>
payloadCreatedAtEpochMillis=<long>
END_VAULTIA_ITEM
```

A escolha por campos de payload prefixados evita aninhar texto multiline dentro do item e reduz ambiguidade de parsing.

### VaultItemSerializationResult

Resultado tipado do decode.

Estados planejados:

```text
Success(draft: EncryptedVaultItemDraft)
Failure(error: VaultItemSerializationError)
```

### VaultItemSerializationError

Erros planejados:

```text
EmptyInput
InvalidMagic
MissingField
DuplicateField
InvalidFieldValue
UnsupportedFormatVersion
UnsupportedItemType
InvalidId
InvalidTimestamp
InvalidPayload
InvalidItem
```

---

## Requisitos de Segurança

- Não persistir key.
- Não persistir plaintext.
- Não serializar key.
- Não serializar plaintext.
- Não serializar master password.
- Não serializar salt KDF.
- Não serializar título real.
- Não serializar nome de usuário.
- Não serializar senha.
- Não serializar nota clara.
- Não serializar URI/path de arquivo.
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

A serialização de ciphertext, nonce e authentication tag é permitida porque esses campos compõem o payload cifrado. Ainda assim, eles não devem ser logados e não devem ser escritos em arquivo nesta fase.

---

## Escopo

Esta fase cria:

- versão do formato serializado de item;
- modelo serializado em memória;
- serializer de encode;
- serializer de decode;
- resultado tipado de decode;
- erro tipado de serialização;
- testes de round-trip;
- testes de campos obrigatórios;
- testes de campos duplicados;
- testes de versão incompatível;
- testes de tipo incompatível;
- testes de ID inválido;
- testes de payload inválido;
- testes de ausência de key/plaintext/metadados sensíveis;
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

- versão do formato mapeia valor codificado.
- modelo serializado rejeita ID inválido.
- modelo serializado rejeita timestamp inválido.
- encode gera magic header correto.
- encode gera footer correto.
- encode inclui formatVersion.
- encode inclui id técnico.
- encode inclui tipo técnico.
- encode inclui timestamps.
- encode inclui payload cifrado serializado com prefixo.
- encode não inclui key.
- encode não inclui plaintext.
- encode não inclui password.
- encode não inclui title.
- decode rejeita input vazio.
- decode rejeita magic inválido.
- decode rejeita campo ausente.
- decode rejeita campo duplicado.
- decode rejeita versão incompatível.
- decode rejeita tipo incompatível.
- decode rejeita ID inválido.
- decode rejeita timestamp inválido.
- decode rejeita payload inválido.
- round-trip encode/decode preserva id.
- round-trip encode/decode preserva tipo.
- round-trip encode/decode preserva timestamps.
- round-trip encode/decode preserva payload serializado.
- arquitetura não importa Android UI.
- arquitetura não importa storage.
- arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- arquitetura não importa java.io ou java.nio.
- arquitetura não usa Android Keystore.
- arquitetura não usa logs.
- arquitetura não usa `javax.crypto` diretamente no pacote item.
- arquitetura não usa `SecureRandom` diretamente no pacote item.

---

## Critérios de Aceite

- `VaultItemSerializedFormatVersion.kt` criado.
- `SerializedEncryptedVaultItem.kt` criado.
- `VaultItemSerializationError.kt` criado.
- `VaultItemSerializationResult.kt` criado.
- `VaultItemSerializer.kt` criado.
- Item criptografado pode ser codificado para texto em memória.
- Texto serializado pode ser decodificado para `EncryptedVaultItemDraft`.
- Formato serializado é versionado.
- Formato serializado não contém key.
- Formato serializado não contém plaintext.
- Formato serializado não contém título real.
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
