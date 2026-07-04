# Android Foundation — Phase 34 Done

## Status

Aprovado.

## Nome

Vault Item Encrypted Draft Contract

## Objetivo

Conectar um payload cifrado e serializável a um draft de item de vault, criando um contrato de item criptografado em memória, ainda sem storage real, sem UI, sem Android Keystore e sem persistência operacional.

A Phase 34 não criou repositório persistente, não escreveu arquivo, não leu disco e não conectou o fluxo à interface. Ela definiu apenas como um item do cofre pode carregar metadados mínimos não sensíveis e payload cifrado serializável.

---

## Arquivos Criados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/item/EncryptedVaultItemType.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/item/EncryptedVaultItemId.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/item/EncryptedVaultItemDraft.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/item/EncryptedVaultItemDraftResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/item/EncryptedVaultItemDraftError.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/item/EncryptedVaultItemDraftFactory.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/item/EncryptedVaultItemDraftFactoryTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/item/EncryptedVaultItemArchitectureTest.kt
```

---

## Implementação Entregue

### EncryptedVaultItemType

- Enum técnico de tipos de item criado.
- Tipos suportados: `PASSWORD`, `NOTE`, `PHOTO`, `DOCUMENT`.
- O enum carrega apenas metadado técnico, sem conteúdo sensível.

### EncryptedVaultItemId

- Identificador técnico de item criado.
- Aplica `trim` no valor recebido.
- Rejeita string vazia.
- Rejeita string apenas com espaços.
- Rejeita espaços internos.
- Rejeita `/`.
- Rejeita barra invertida.
- Rejeita `:`.
- Rejeita `.` para evitar aparência de caminho/arquivo.
- Não gera UUID real nesta fase.

### EncryptedVaultItemDraft

- Draft de item criptografado em memória criado.
- Contém `EncryptedVaultItemId`.
- Contém `EncryptedVaultItemType`.
- Contém `SerializedEncryptedVaultPayload`.
- Contém `createdAtEpochMillis`.
- Contém `updatedAtEpochMillis`.
- Rejeita timestamp de criação inválido.
- Rejeita timestamp de atualização inválido.
- Rejeita update anterior à criação.
- Não contém key.
- Não contém plaintext.
- Não contém título real.
- Não contém URI/path.
- Não persiste dados.

### EncryptedVaultItemDraftFactory

- Factory de criação de draft criptografado criada.
- Recebe id, tipo, payload serializado e timestamps.
- Mapeia ID inválido para `InvalidId`.
- Mapeia timestamp inválido para `InvalidTimestamp`.
- Retorna `Success` com `EncryptedVaultItemDraft` válido.
- Mantém contrato público por resultado tipado, não por exceção.

### EncryptedVaultItemDraftError

Erros tipados criados:

```text
InvalidId
InvalidTimestamp
InvalidPayload
InvalidMetadata
```

### EncryptedVaultItemDraftResult

- Resultado tipado criado.
- `Success` carrega `EncryptedVaultItemDraft`.
- `Failure` carrega `EncryptedVaultItemDraftError`.

---

## Segurança e Isolamento

- Nenhuma key foi persistida.
- Nenhum plaintext foi persistido.
- Nenhuma master password foi armazenada.
- Nenhum salt KDF foi armazenado no item.
- Nenhum título real foi armazenado.
- Nenhum nome de usuário foi armazenado.
- Nenhuma senha foi armazenada.
- Nenhuma nota clara foi armazenada.
- Nenhum caminho de foto/documento foi armazenado.
- Nenhuma URI foi armazenada.
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

O draft carrega `SerializedEncryptedVaultPayload`, que contém apenas payload cifrado e metadados técnicos mínimos definidos na Phase 33.

---

## Testes Entregues

- Enum contém `PASSWORD`, `NOTE`, `PHOTO` e `DOCUMENT`.
- ID rejeita string vazia.
- ID rejeita string apenas com espaços.
- ID aplica `trim`.
- ID rejeita espaços internos.
- ID rejeita path separators.
- ID rejeita aparência de caminho de arquivo.
- Factory retorna `InvalidId` para ID inválido.
- Factory retorna `InvalidTimestamp` para timestamp inválido.
- Factory retorna `Success` para item válido.
- Draft preserva id.
- Draft preserva type.
- Draft preserva payload serializado.
- Draft preserva timestamps.
- Draft rejeita timestamps inválidos.
- Draft não expõe key.
- Draft não expõe plaintext.
- Draft não expõe password.
- Draft não expõe title.
- Draft não expõe URI/path.
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
- APK SHA-256: `7e7321e27cfb04d117b38501d2407b7dd25a9ca5929f94244d08291450643ef1`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 34.

---

## Critérios de Aceite

- `EncryptedVaultItemType.kt` criado.
- `EncryptedVaultItemId.kt` criado.
- `EncryptedVaultItemDraft.kt` criado.
- `EncryptedVaultItemDraftResult.kt` criado.
- `EncryptedVaultItemDraftError.kt` criado.
- `EncryptedVaultItemDraftFactory.kt` criado.
- Item draft conecta tipo técnico, ID técnico e payload cifrado serializado.
- Item draft não contém key.
- Item draft não contém plaintext.
- Item draft não contém título real.
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
e4d61f2 docs: add phase 34 plan for encrypted vault item draft
9b92802 feat: add encrypted vault item draft contract
```

## Próxima Fase Recomendada

Phase 35 — Vault Item Serialization Contract

Objetivo futuro: definir um contrato serializado para item criptografado completo, ainda sem storage real, sem escrita em disco, sem UI e sem persistência operacional.
