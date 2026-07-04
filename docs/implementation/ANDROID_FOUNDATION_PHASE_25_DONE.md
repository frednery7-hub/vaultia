# Android Foundation — Phase 25 Done

## Status

Aprovado.

## Nome

KDF Profiles and Public Vault Header Contract

## Objetivo

Definir perfis internos de parâmetros KDF e o contrato público inicial do header criptográfico do vault, sem criar storage real, sem serialização definitiva, sem AES-GCM, sem Android Keystore, sem criação real de cofre e sem fluxo real de desbloqueio.

A Phase 25 estabelece como o Vaultia representa parâmetros públicos necessários para reabrir um vault no futuro, mantendo senha, chave derivada e payload cifrado fora do header.

---

## Arquivos Criados

### KDF

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/KdfVersion.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/KdfProfile.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/KdfProfileCatalog.kt
```

### Vault Header

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultHeader.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultHeaderVersion.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfVersionTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfProfileTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfProfileCatalogTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultHeaderVersionTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultHeaderTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultHeaderArchitectureTest.kt
```

---

## Implementação Entregue

### KdfVersion

- `KdfVersion.ARGON2_VERSION_13` criado.
- `encodedValue = 0x13` definido.
- A versão KDF fica separada do algoritmo para evitar ambiguidade futura.

### KdfProfile

- Modelo interno de perfil KDF criado.
- Campos públicos e não secretos definidos.
- Validação de `id` não vazio.
- Validação de `memoryCostKiB > 0`.
- Validação de `iterations > 0`.
- Validação de `parallelism > 0`.
- Validação de `outputLengthBytes > 0`.
- Validação de `recommendedSaltLengthBytes >= 16`.
- Conversão para `KdfParameters.Argon2id` a partir de salt fornecido.
- Nenhum salt real é armazenado no perfil.
- Nenhuma senha é armazenada no perfil.
- Nenhuma chave derivada é armazenada no perfil.

### KdfProfileCatalog

- Catálogo interno de perfis KDF criado.
- Perfil `FAST` criado com id estável `argon2id-v1-profile-a`.
- Perfil `CONSERVATIVE` criado com id estável `argon2id-v1-profile-b`.
- `FAST`: 32 MiB, 2 iterações, paralelismo 1, saída 32 bytes, salt recomendado 32 bytes.
- `CONSERVATIVE`: 64 MiB, 2 iterações, paralelismo 1, saída 32 bytes, salt recomendado 32 bytes.
- Perfil padrão definido como `CONSERVATIVE`.
- Catálogo retorna lista de perfis suportados.

### VaultHeaderVersion

- `VaultHeaderVersion.V1_0` criado.
- `encodedValue = 0x00010000`.
- `major = 1`.
- `minor = 0`.

### VaultHeader

- Contrato público inicial do header criado.
- Header guarda `formatVersion`.
- Header guarda `kdfAlgorithm`.
- Header guarda `kdfVersion`.
- Header guarda salt público com cópia defensiva.
- Header guarda `memoryCostKiB`.
- Header guarda `iterations`.
- Header guarda `parallelism`.
- Header guarda `outputLengthBytes`.
- Header guarda `createdAtEpochMillis`.
- Header pode ser construído a partir de `KdfProfile` e salt fornecido.
- Header pode reconstruir `KdfParameters`.
- Header guarda parâmetros concretos, não depende de nome de perfil.
- Header rejeita valores inválidos no construtor.
- Header não gera salt.
- Header não acessa `SecureRandom`.

---

## Segurança e Isolamento

- Nenhum storage real foi criado.
- Nenhuma serialização definitiva foi criada.
- Nenhum JSON definitivo foi criado.
- Nenhum CBOR definitivo foi criado.
- Nenhum Protobuf definitivo foi criado.
- Nenhuma escrita em disco foi criada.
- Nenhuma leitura de disco foi criada.
- Nenhuma geração real de salt por CSPRNG foi criada.
- Nenhum AES-GCM foi implementado.
- Nenhum envelope encryption foi implementado.
- Nenhum payload cifrado foi criado.
- Nenhum nonce foi criado.
- Nenhuma tag de autenticação foi criada.
- Nenhum Android Keystore foi implementado.
- Nenhuma biometria foi implementada.
- Nenhuma criação real de vault foi implementada.
- Nenhum desbloqueio real foi implementado.
- Nenhuma rotação de KDF params foi implementada.
- Nenhum rekeying foi implementado.
- Nenhuma migração de vault foi implementada.
- Nenhuma UI foi alterada.
- Nenhuma permissão Android foi adicionada.
- Roadmap formal não foi alterado nesta fase.

O header é metadata-only e contém apenas parâmetros públicos necessários para futura derivação de chave.

---

## Dados Explicitamente Ausentes do Header

O `VaultHeader` não contém campos para:

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

A ausência de `profileName`, `profileId` e `profile` é intencional: o header deve persistir parâmetros concretos para evitar dependência futura do catálogo de perfis.

---

## Testes Entregues

- `KdfVersion.ARGON2_VERSION_13` expõe `0x13`.
- `KdfProfile` cria perfil Argon2id válido.
- `KdfProfile` converte perfil em `KdfParameters.Argon2id`.
- `KdfProfile` rejeita valores inválidos.
- `KdfProfileCatalog` contém `FAST`.
- `KdfProfileCatalog` contém `CONSERVATIVE`.
- `KdfProfileCatalog` contém perfis com ids únicos.
- `KdfProfileCatalog` retorna `CONSERVATIVE` como padrão.
- `VaultHeaderVersion.V1_0` expõe encoded value, major e minor.
- `VaultHeader` é criado a partir de perfil e salt.
- `VaultHeader` guarda parâmetros concretos do perfil.
- `VaultHeader` não guarda nome/id/objeto de perfil.
- `VaultHeader` protege salt com cópia defensiva.
- `VaultHeader` converte header de volta para `KdfParameters`.
- `VaultHeader` rejeita salt menor que 16 bytes.
- `VaultHeader` rejeita parâmetros inválidos.
- `VaultHeader` rejeita timestamp inválido.
- Teste garante ausência de nomes de campos secretos.
- Teste de arquitetura garante isolamento de Android UI, storage, Room, DataStore, SQLite, SecureRandom, Argon2Kt, Cipher, SecretKey e KeyStore no pacote vault.

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
- APK SHA-256: `3e4e14b7e3a6839c930cc4e54bb04f2c80222a315a5177f5e2fc15473a335c99`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 25.

---

## Critérios de Aceite

- `KdfProfile.kt` criado.
- `KdfProfileCatalog.kt` criado.
- `KdfVersion.kt` criado.
- `VaultHeader.kt` criado.
- `VaultHeaderVersion.kt` criado.
- Perfis internos criados sem dados secretos.
- Header criado com parâmetros públicos concretos.
- Header não contém senha.
- Header não contém chave derivada.
- Header não contém payload cifrado.
- Header protege salt com cópia defensiva.
- Nenhum storage real foi criado.
- Nenhuma serialização definitiva foi criada.
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
691b642 docs: add phase 25 plan for kdf profiles and vault header
afd8355 feat: add kdf profiles and public vault header contract
```

## Próxima Fase Recomendada

Phase 26 — Vault Header Serialization Contract

Objetivo futuro: definir serialização reversível do header público do vault, sem storage real e sem payload cifrado, permitindo round-trip controlado para futuras fases de criação e abertura do cofre.
