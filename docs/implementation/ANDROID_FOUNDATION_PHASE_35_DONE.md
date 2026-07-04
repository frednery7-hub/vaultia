# Android Foundation — Phase 35 Done

## Status

Aprovado.

## Nome

Vault Item Serialization Contract

## Objetivo

Definir um contrato serializado para o item criptografado completo, transformando `EncryptedVaultItemDraft` em uma representação textual, versionada, validável e persistível em fases futuras, ainda sem storage real, sem escrita em disco, sem leitura de disco, sem UI, sem Android Keystore e sem persistência operacional.

A Phase 35 criou apenas o boundary de serialização do item criptografado completo em memória. Ela não criou arquivo final de vault, não criou repositório persistente e não conectou o fluxo à interface.

---

## Arquivos Criados

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

## Arquivos Modificados

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultHeaderArchitectureTest.kt
```

A alteração no teste antigo foi cirúrgica: o `VaultHeaderArchitectureTest` passou a ignorar o subpacote `item`, porque a Phase 35 introduziu uma auditoria própria para serialização de item.

---

## Implementação Entregue

### VaultItemSerializedFormatVersion

- Enum de versão do formato serializado de item criado.
- Versão inicial: `V1_0`.
- Valor codificado: `1`.
- Método de resolução por valor codificado criado.

### SerializedEncryptedVaultItem

- Modelo serializado em memória criado.
- Contém `formatVersion`.
- Contém `id` técnico.
- Contém `type` técnico.
- Contém `createdAtEpochMillis`.
- Contém `updatedAtEpochMillis`.
- Contém `SerializedEncryptedVaultPayload`.
- Valida ID usando `EncryptedVaultItemId`.
- Valida timestamps positivos.
- Valida que update não é anterior à criação.
- Não contém key.
- Não contém plaintext.
- Não contém título real.
- Não contém URI/path.

### VaultItemSerializer

- Serializer de item criptografado criado.
- `encode` transforma `EncryptedVaultItemDraft` em texto key-value.
- `decode` transforma texto key-value em `EncryptedVaultItemDraft`.
- Formato inclui magic header `VAULTIA_ITEM_V1`.
- Formato inclui footer `END_VAULTIA_ITEM`.
- Formato inclui `formatVersion=1`.
- Formato inclui id técnico.
- Formato inclui tipo técnico.
- Formato inclui timestamps do item.
- Formato inclui payload cifrado serializado com campos prefixados.
- Decode valida campo ausente.
- Decode valida campo duplicado.
- Decode valida versão incompatível.
- Decode valida tipo incompatível.
- Decode valida ID inválido.
- Decode valida timestamp inválido.
- Decode valida payload inválido.
- Decode reconstrói `EncryptedVaultItemDraft`.
- Não toca em disco.

### VaultItemSerializationError

Erros tipados criados:

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

### VaultItemSerializationResult

- Resultado tipado de decode criado.
- `Success` carrega `EncryptedVaultItemDraft`.
- `Failure` carrega `VaultItemSerializationError`.

---

## Formato Implementado

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

O formato é textual, determinístico, versionado e testável sem dependência de Android APIs, storage frameworks ou biblioteca externa de JSON.

---

## Segurança e Isolamento

- Nenhuma key foi persistida.
- Nenhum plaintext foi persistido.
- Nenhuma key foi serializada.
- Nenhum plaintext foi serializado.
- Nenhuma master password foi serializada.
- Nenhum salt KDF foi serializado.
- Nenhum título real foi serializado.
- Nenhum nome de usuário foi serializado.
- Nenhuma senha foi serializada.
- Nenhuma nota clara foi serializada.
- Nenhum URI/path foi serializado.
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
- Modelo serializado rejeita ID inválido.
- Modelo serializado rejeita timestamp inválido.
- Encode gera magic header correto.
- Encode gera footer correto.
- Encode inclui formatVersion.
- Encode inclui id técnico.
- Encode inclui tipo técnico.
- Encode inclui timestamps.
- Encode inclui payload cifrado serializado com prefixo.
- Encode não inclui key.
- Encode não inclui plaintext.
- Encode não inclui password.
- Encode não inclui title.
- Decode rejeita input vazio.
- Decode rejeita magic inválido.
- Decode rejeita campo ausente.
- Decode rejeita campo duplicado.
- Decode rejeita versão incompatível.
- Decode rejeita tipo incompatível.
- Decode rejeita ID inválido.
- Decode rejeita timestamp inválido.
- Decode rejeita payload inválido.
- Round-trip encode/decode preserva id.
- Round-trip encode/decode preserva tipo.
- Round-trip encode/decode preserva timestamps.
- Round-trip encode/decode preserva payload serializado.
- Arquitetura não importa Android UI.
- Arquitetura não importa storage.
- Arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- Arquitetura não importa java.io ou java.nio.
- Arquitetura não usa Android Keystore.
- Arquitetura não usa logs.
- Arquitetura não usa `javax.crypto` diretamente no pacote item.
- Arquitetura não usa `SecureRandom` diretamente no pacote item.

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
- APK SHA-256: `046736f2d0283efc10945f7e42b0cfec261918e8f981c19d00ed93f479065694`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 35.

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
7727a18 docs: add phase 35 plan for vault item serialization
06606fe feat: add vault item serialization contract
```

## Próxima Fase Recomendada

Phase 36 — Secure Frontend Architecture Pause

Objetivo futuro: pausar antes de implementar frontend real e definir a arquitetura segura da interface, evitando exposição indevida de secrets, logs, previews, screenshots, navegação insegura e estados visuais inconsistentes.
