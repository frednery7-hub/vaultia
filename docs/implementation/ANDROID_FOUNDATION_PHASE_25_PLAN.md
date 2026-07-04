# Android Foundation — Phase 25 Plan

## Nome

KDF Profiles and Public Vault Header Contract

## Objetivo

Definir perfis internos de parâmetros KDF e o contrato público inicial do header criptográfico do vault, sem criar storage real, sem serialização definitiva, sem AES-GCM, sem Android Keystore, sem criação real de cofre e sem fluxo real de desbloqueio.

A Phase 25 estabelece como o Vaultia vai representar, validar e preservar os parâmetros públicos necessários para reabrir um vault no futuro, mantendo senha, chave derivada e payload cifrado fora do header.

---

## Decisão Arquitetural

Perfis KDF existem apenas como catálogo em código.

O `VaultHeader` deve guardar parâmetros concretos e públicos, não apenas o nome amigável do perfil.

Motivo: se o catálogo de perfis mudar no futuro, vaults antigos continuam abrindo porque o header contém os parâmetros efetivamente usados no momento da criação.

---

## Arquivos Planejados

### KDF

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/KdfProfile.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/KdfProfileCatalog.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/KdfVersion.kt
```

### Vault Header

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultHeader.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultHeaderVersion.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfProfileTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfProfileCatalogTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfVersionTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultHeaderTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultHeaderArchitectureTest.kt
```

---

## Contratos Planejados

### KdfVersion

Representa a versão do algoritmo KDF usada pelo vault.

Para Argon2id, a versão planejada é:

```text
ARGON2_VERSION_13
```

A versão deve ser registrada separadamente do algoritmo para evitar ambiguidade futura se a biblioteca ou o padrão Argon2 evoluir.

### KdfProfile

Representa um perfil interno de parâmetros KDF.

Campos planejados:

```text
id
algorithm
kdfVersion
memoryCostKiB
iterations
parallelism
outputLengthBytes
recommendedSaltLengthBytes
```

O perfil não deve conter salt real, senha, chave derivada, ciphertext, nonce, tag ou caminho de storage.

### KdfProfileCatalog

Catálogo interno de perfis suportados.

Perfis iniciais planejados:

```text
FAST
- algorithm: ARGON2ID
- kdfVersion: ARGON2_VERSION_13
- memoryCostKiB: 32 * 1024
- iterations: 2
- parallelism: 1
- outputLengthBytes: 32
- recommendedSaltLengthBytes: 32

CONSERVATIVE
- algorithm: ARGON2ID
- kdfVersion: ARGON2_VERSION_13
- memoryCostKiB: 64 * 1024
- iterations: 2
- parallelism: 1
- outputLengthBytes: 32
- recommendedSaltLengthBytes: 32
```

Esses nomes são internos. O header futuro deve guardar os parâmetros concretos, não depender do nome do perfil.

### VaultHeaderVersion

Representa a versão do formato público do header.

Versão inicial planejada:

```text
V1_0
encodedValue: 0x00010000
major: 1
minor: 0
```

Regra futura de leitura: leitores v1 devem aceitar headers v1.x compatíveis e rejeitar major version futura.

### VaultHeader

Contrato público inicial do header do vault.

Campos planejados:

```text
formatVersion
kdfAlgorithm
kdfVersion
salt
memoryCostKiB
iterations
parallelism
outputLengthBytes
createdAtEpochMillis
```

O header deve conter apenas informações públicas necessárias para derivar novamente a chave no futuro.

O header não deve conter:

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
profileName como dependência obrigatória de leitura
```

---

## Regras de Validação

### Salt

- Salt mínimo: 16 bytes.
- Salt recomendado por perfil: 32 bytes.
- Salt deve usar cópia defensiva.
- Salt não é segredo.
- Salt deve ser único por vault no futuro.
- Geração real por CSPRNG fica para fase posterior.

### Parâmetros KDF

- `memoryCostKiB` deve ser maior que zero.
- `iterations` deve ser maior que zero.
- `parallelism` deve ser maior que zero.
- `outputLengthBytes` deve ser maior que zero.
- `createdAtEpochMillis` deve ser maior que zero.

### Header

- Header deve ser metadata-only.
- Header deve falhar cedo para valores inválidos.
- Header deve proteger salt com cópia defensiva.
- Header deve poder ser construído a partir de um `KdfProfile` e salt real fornecido.
- Header não deve gerar salt sozinho nesta fase.
- Header não deve acessar `SecureRandom` nesta fase.

---

## Escopo

Esta fase cria:

- perfis KDF internos;
- catálogo de perfis KDF;
- versão explícita de KDF;
- versão explícita de header;
- modelo público de Vault Header;
- validações de parâmetros públicos;
- testes unitários;
- testes de arquitetura;
- relatório DONE.

---

## Fora de Escopo

Esta fase não implementa:

- storage real;
- serialização definitiva;
- JSON definitivo;
- CBOR definitivo;
- Protobuf definitivo;
- escrita em disco;
- leitura de disco;
- geração real de salt por CSPRNG;
- AES-GCM;
- envelope encryption;
- payload cifrado;
- nonce;
- tag de autenticação;
- Android Keystore;
- biometria;
- criação real de vault;
- desbloqueio real;
- rotação de KDF params;
- rekeying;
- migração de vault;
- modo discreto/decoy vault;
- alteração no roadmap formal.

---

## Testes Planejados

Cobertura mínima:

- cria perfil FAST válido;
- cria perfil CONSERVATIVE válido;
- catálogo contém perfis únicos;
- catálogo retorna perfil padrão;
- `KdfVersion.ARGON2_VERSION_13` existe;
- `VaultHeaderVersion.V1_0` expõe major, minor e encoded value;
- cria `VaultHeader` válido a partir de perfil e salt;
- header guarda parâmetros concretos do perfil;
- header não guarda o nome do perfil como dependência obrigatória;
- header protege salt com cópia defensiva;
- rejeita salt menor que 16 bytes;
- rejeita timestamps inválidos;
- rejeita parâmetros KDF inválidos;
- testes garantem que header não contém campos de senha;
- testes garantem que header não contém campos de chave derivada;
- testes garantem que header não contém campos de payload cifrado;
- testes garantem isolamento de UI, storage, Room, DataStore, SQLite e Android framework.

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
- Nenhum storage real é criado.
- Nenhuma serialização definitiva é criada.
- Nenhum AES-GCM é criado.
- Nenhuma UI é alterada.
- Nenhuma permissão Android é adicionada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
- Roadmap formal não é alterado nesta fase.
