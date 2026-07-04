# Android Foundation — Phase 27 Plan

## Nome

Vault Creation Draft Without Persistence

## Objetivo

Criar um fluxo interno em memória para montar um vault inicial combinando política de senha mestra, perfil KDF, salt fornecido, derivação Argon2id e header serializável, sem criar storage real, sem escrever em disco, sem UI, sem AES-GCM, sem Android Keystore e sem persistir senha ou chave derivada.

A Phase 27 conecta os contratos criados nas fases anteriores em um fluxo controlado e testável, ainda sem transformar isso em cofre real persistido no dispositivo.

---

## Contexto Técnico

A Phase 27 depende das entregas anteriores:

```text
Phase 21 — Master Password Policy Contract
Phase 23 — KDF Contract and Safe Models
Phase 24 — Argon2id KDF Implementation
Phase 25 — KDF Profiles and Public Vault Header Contract
Phase 26 — Vault Header Serialization Contract
```

Esta fase deve apenas orquestrar componentes já existentes, mantendo o limite de segurança: nada é salvo, nada é exibido em UI e nenhum payload sensível real é criado.

---

## Decisão Arquitetural

A criação de vault será representada por um serviço puro de aplicação/core, sem Android framework, sem storage e sem UI.

O fluxo deve receber explicitamente:

```text
masterPassword
salt
createdAtEpochMillis
```

A fase não deve gerar salt sozinha, pois a geração por CSPRNG será tratada em fase posterior.

A fase não deve persistir a chave derivada. O resultado pode conter material derivado apenas em memória para provar a montagem do fluxo, mantendo as cópias defensivas já existentes em `KdfResult`.

---

## Arquivos Planejados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultCreationService.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultCreationRequest.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultCreationResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultCreationError.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/CreatedVaultDraft.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultCreationServiceTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultCreationArchitectureTest.kt
```

---

## Fluxo Planejado

O fluxo de criação em memória deve seguir esta ordem:

```text
1. Receber VaultCreationRequest
2. Validar senha mestra com MasterPasswordPolicy
3. Rejeitar senha inválida
4. Obter KdfProfileCatalog.defaultProfile()
5. Validar salt fornecido
6. Criar VaultHeader a partir do perfil, salt e timestamp
7. Converter VaultHeader para KdfParameters
8. Derivar chave com KdfDeriver
9. Rejeitar falha de derivação
10. Serializar VaultHeader com VaultHeaderSerializer
11. Retornar CreatedVaultDraft em memória
```

Nenhuma etapa deve escrever em disco.

---

## Contratos Planejados

### VaultCreationRequest

Entrada explícita do fluxo.

Campos planejados:

```text
masterPassword: String
salt: ByteArray
createdAtEpochMillis: Long
```

Regras:

- `masterPassword` não deve ser persistida.
- `salt` deve usar cópia defensiva.
- `createdAtEpochMillis` deve ser positivo.

### VaultCreationService

Serviço responsável pela orquestração da criação em memória.

Dependências planejadas:

```text
MasterPasswordPolicy
KdfDeriver
KdfProfileCatalog
VaultHeader
VaultHeaderSerializer
```

### CreatedVaultDraft

Resultado em memória de uma criação bem-sucedida.

Campos planejados:

```text
header: VaultHeader
serializedHeader: String
kdfResult: KdfResult
```

Observação: `kdfResult` contém material derivado apenas em memória. Esta fase não persiste esse material e não cria payload cifrado.

### VaultCreationResult

Resultado explícito do fluxo:

```text
Success(draft: CreatedVaultDraft)
Failure(error: VaultCreationError)
```

### VaultCreationError

Erros planejados:

```text
InvalidMasterPassword
InvalidCreationRequest
InvalidVaultHeader
KdfDerivationFailed
HeaderSerializationFailed
```

O fluxo não deve lançar exceção como caminho normal.

---

## Requisitos de Segurança

- Não persistir senha mestra.
- Não persistir chave derivada.
- Não persistir salt.
- Não criar arquivo.
- Não escrever em disco.
- Não ler disco.
- Não criar storage real.
- Não criar SQLite.
- Não criar Room.
- Não criar DataStore.
- Não criar SharedPreferences.
- Não criar UI.
- Não logar senha.
- Não logar chave derivada.
- Não logar salt.
- Não adicionar permissão Android.
- Não adicionar `android.permission.INTERNET`.
- Não implementar AES-GCM.
- Não implementar Android Keystore.
- Não implementar biometria.

A limpeza determinística de memória em JVM/Android continua fora do escopo desta fase e será tratada em lifecycle de vault/session real.

---

## Escopo

Esta fase cria:

- request de criação de vault em memória;
- service de criação de vault em memória;
- result tipado de criação;
- erros tipados de criação;
- draft em memória de vault criado;
- testes de sucesso do fluxo;
- testes de erro do fluxo;
- testes de isolamento/arquitetura;
- relatório DONE.

---

## Fora de Escopo

Esta fase não implementa:

- storage real;
- escrita em disco;
- leitura de disco;
- criação real de vault persistido;
- desbloqueio real;
- login real;
- integração com UI;
- integração com SessionManager;
- integração com VaultRepository;
- geração real de salt por CSPRNG;
- AES-GCM;
- envelope encryption;
- payload cifrado;
- nonce real;
- tag de autenticação;
- Android Keystore;
- biometria;
- backup/export;
- modo discreto/decoy vault;
- alteração no roadmap formal.

---

## Testes Planejados

Cobertura mínima:

- request protege salt com cópia defensiva.
- request rejeita timestamp inválido.
- service rejeita senha mestra inválida.
- service rejeita salt inválido.
- service usa perfil padrão `CONSERVATIVE`.
- service cria `VaultHeader` válido.
- service serializa `VaultHeader`.
- service chama `KdfDeriver` com parâmetros derivados do header.
- service retorna `Success` com `CreatedVaultDraft` em memória.
- draft contém header.
- draft contém serializedHeader.
- draft contém `KdfResult` em memória.
- falha do KDF retorna `KdfDerivationFailed`.
- erro de serialização retorna `HeaderSerializationFailed` se aplicável.
- arquitetura não importa Android UI.
- arquitetura não importa storage.
- arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- arquitetura não importa java.io ou java.nio.
- arquitetura não usa SecureRandom nesta fase.
- arquitetura não usa Cipher, SecretKey ou KeyStore.
- arquitetura não usa logs.

---

## Critérios de Aceite

- `VaultCreationService.kt` criado.
- `VaultCreationRequest.kt` criado.
- `VaultCreationResult.kt` criado.
- `VaultCreationError.kt` criado.
- `CreatedVaultDraft.kt` criado.
- Fluxo de criação em memória implementado.
- Senha mestra inválida é rejeitada.
- Salt inválido é rejeitado.
- Header é criado em memória.
- Header é serializado em memória.
- KDF é chamado por contrato.
- Nenhum storage real é criado.
- Nenhuma escrita em disco é criada.
- Nenhuma leitura de disco é criada.
- Nenhuma UI é alterada.
- Nenhum AES-GCM é criado.
- Nenhum Android Keystore é criado.
- Nenhuma permissão Android é adicionada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
- Roadmap formal não é alterado nesta fase.
