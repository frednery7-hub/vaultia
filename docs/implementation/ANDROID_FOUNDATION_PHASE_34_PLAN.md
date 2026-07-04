# Android Foundation — Phase 34 Plan

## Nome

Vault Item Encrypted Draft Contract

## Objetivo

Conectar um payload cifrado e serializável a um draft de item de vault, criando um contrato de item criptografado em memória, ainda sem storage real, sem UI, sem Android Keystore e sem persistência operacional.

A Phase 34 não cria repositório persistente, não escreve arquivo, não lê disco e não conecta o fluxo à interface. Ela define apenas como um item do cofre pode carregar metadados mínimos não sensíveis e payload cifrado serializável.

---

## Contexto Técnico

As fases anteriores criaram a base criptográfica e de payload:

- Phase 30 — `EncryptedPayload`, `PlaintextPayload` e `AuthenticatedCipher`.
- Phase 31 — encryption de payload em memória.
- Phase 32 — decryption de payload em memória.
- Phase 33 — serialização textual versionada de payload cifrado.

A Phase 34 deve criar o próximo nível de contrato: um draft de item criptografado que aponta para o payload cifrado serializável e mantém somente metadados seguros.

Dependências conceituais anteriores:

```text
Phase 30 — Encryption Contract and AES-GCM Boundary
Phase 31 — Vault Payload Encryption Draft
Phase 32 — Vault Payload Decryption Draft
Phase 33 — Vault Payload Serialization Contract
75% Audit Gate — Foundation Security Audit
```

---

## Decisão Arquitetural

O draft de item criptografado será criado em um pacote específico de contrato de item, separado do pacote de payload:

```text
com.vaultia.app.core.crypto.vault.item
```

O item deve referenciar `SerializedEncryptedVaultPayload`, não plaintext e não key.

Metadados permitidos nesta fase:

- identificador técnico do item;
- tipo do item;
- timestamps;
- payload cifrado serializado.

Metadados sensíveis, como título real, usuário, senha, nota, caminho de foto ou nome de documento, não devem ser adicionados nesta fase. Quando existirem no produto final, deverão ser tratados como dados sensíveis e preferencialmente ficar dentro do payload cifrado.

---

## Arquivos Planejados

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

## Contratos Planejados

### EncryptedVaultItemType

Enum técnico dos tipos de item suportados pelo cofre.

Tipos planejados:

```text
PASSWORD
NOTE
PHOTO
DOCUMENT
```

Esses tipos são metadados técnicos. Não devem carregar conteúdo sensível.

### EncryptedVaultItemId

Identificador técnico do draft de item.

Regras planejadas:

- não pode ser vazio;
- deve ser normalizado com `trim`;
- deve rejeitar espaços internos;
- deve rejeitar path separators;
- deve rejeitar valores com aparência de caminho de arquivo;
- deve aceitar formato UUID/string técnica futura.

A Phase 34 não precisa gerar UUID real. Ela apenas valida o contrato do ID recebido.

### EncryptedVaultItemDraft

Draft de item criptografado em memória.

Campos planejados:

```text
id: EncryptedVaultItemId
type: EncryptedVaultItemType
serializedPayload: SerializedEncryptedVaultPayload
createdAtEpochMillis: Long
updatedAtEpochMillis: Long
```

Regras planejadas:

- timestamp de criação deve ser positivo;
- timestamp de atualização deve ser positivo;
- `updatedAtEpochMillis` não pode ser menor que `createdAtEpochMillis`;
- não deve conter key;
- não deve conter plaintext;
- não deve conter título real nesta fase;
- não deve conter caminho de arquivo;
- não deve conter URI;
- não deve persistir dados.

### EncryptedVaultItemDraftFactory

Factory para construir o draft de item criptografado com validação centralizada.

Método planejado:

```text
create(id: String, type: EncryptedVaultItemType, serializedPayload: SerializedEncryptedVaultPayload, createdAtEpochMillis: Long, updatedAtEpochMillis: Long): EncryptedVaultItemDraftResult
```

A factory deve retornar resultado tipado, não exceção pública.

### EncryptedVaultItemDraftError

Erros planejados:

```text
InvalidId
InvalidTimestamp
InvalidPayload
InvalidMetadata
```

### EncryptedVaultItemDraftResult

Resultado tipado da criação do draft.

Estados planejados:

```text
Success(draft: EncryptedVaultItemDraft)
Failure(error: EncryptedVaultItemDraftError)
```

---

## Requisitos de Segurança

- Não persistir key.
- Não persistir plaintext.
- Não armazenar master password.
- Não armazenar salt KDF no item.
- Não armazenar título real nesta fase.
- Não armazenar nome de usuário.
- Não armazenar senha.
- Não armazenar nota clara.
- Não armazenar caminho de foto/documento.
- Não armazenar URI.
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

O draft pode carregar `SerializedEncryptedVaultPayload`, porque esse objeto contém apenas payload cifrado e metadados técnicos mínimos criados na Phase 33.

---

## Escopo

Esta fase cria:

- tipo técnico de item criptografado;
- identificador técnico validado;
- draft de item criptografado em memória;
- factory com resultado tipado;
- erro tipado de criação;
- testes de ID inválido;
- testes de timestamp inválido;
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
- geração real de UUID;
- alteração no roadmap formal.

---

## Testes Planejados

Cobertura mínima:

- enum contém PASSWORD, NOTE, PHOTO e DOCUMENT.
- ID rejeita string vazia.
- ID rejeita string apenas com espaços.
- ID aplica trim.
- ID rejeita espaços internos.
- ID rejeita path separators.
- ID rejeita aparência de caminho de arquivo.
- factory retorna `InvalidId` para ID inválido.
- factory retorna `InvalidTimestamp` para timestamp inválido.
- factory retorna `Success` para item válido.
- draft preserva id.
- draft preserva type.
- draft preserva payload serializado.
- draft preserva timestamps.
- draft não expõe key.
- draft não expõe plaintext.
- draft não expõe password.
- draft não expõe title.
- draft não expõe URI/path.
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
