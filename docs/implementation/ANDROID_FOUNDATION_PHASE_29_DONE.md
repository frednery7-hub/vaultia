# Android Foundation — Phase 29 Done

## Status

Aprovado.

## Nome

Vault Creation With Generated Salt

## Objetivo

Integrar o contrato de geração segura de salt ao fluxo de criação em memória do vault, permitindo criar um `CreatedVaultDraft` sem receber salt externo, ainda sem storage real, sem UI, sem AES-GCM, sem Android Keystore e sem persistência.

A Phase 29 conecta a Phase 27 e a Phase 28: o vault continua sendo criado apenas em memória, mas agora o salt pode ser gerado pelo próprio fluxo controlado.

---

## Arquivos Criados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultCreationWithoutExternalSaltRequest.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultCreationWithGeneratedSaltTest.kt
```

## Arquivos Modificados

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultCreationError.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultCreationService.kt
```

---

## Implementação Entregue

### VaultCreationWithoutExternalSaltRequest

- Request de criação sem salt externo criado.
- Recebe `masterPassword`.
- Recebe `createdAtEpochMillis`.
- Rejeita timestamp inválido.
- Não recebe salt externo.
- Não persiste senha.
- Não persiste timestamp.

### VaultCreationError

- Erro `SaltGenerationFailed` adicionado.
- Falhas do `SaltGenerator` agora têm erro tipado no fluxo de criação de vault.

Erros atuais do fluxo:

```text
InvalidMasterPassword
InvalidCreationRequest
InvalidVaultHeader
SaltGenerationFailed
KdfDerivationFailed
HeaderSerializationFailed
```

### VaultCreationService

- `SaltGenerator` injetável adicionado ao service.
- Fluxo existente com salt externo preservado.
- Novo método `createWithGeneratedSalt(...)` criado.
- Senha mestra é validada antes da geração de salt.
- Salt recomendado é solicitado via `SaltGenerator.generateRecommendedSalt()`.
- Falha de geração de salt retorna `SaltGenerationFailed`.
- Ausência de `SaltGenerator` no novo fluxo retorna `SaltGenerationFailed`.
- Salt gerado é usado para criar `VaultCreationRequest` internamente.
- Fluxo existente de criação em memória é reutilizado.
- Header é criado em memória com salt gerado.
- KDF é derivado por contrato.
- Header é serializado em memória.
- `CreatedVaultDraft` é retornado apenas em memória.

---

## Fluxos Suportados

### Fluxo com salt externo

```text
create(request: VaultCreationRequest): VaultCreationResult
```

Finalidade: manter controlabilidade em testes, auditoria e cenários futuros de importação/migração.

### Fluxo com salt gerado

```text
createWithGeneratedSalt(request: VaultCreationWithoutExternalSaltRequest): VaultCreationResult
```

Finalidade: permitir criação de draft de vault em memória sem salt manual.

Fluxo implementado:

```text
1. Recebe senha mestra e timestamp
2. Valida senha mestra com MasterPasswordPolicy
3. Rejeita senha inválida
4. Solicita salt recomendado ao SaltGenerator
5. Rejeita falha de geração de salt
6. Monta VaultCreationRequest com salt gerado
7. Reusa fluxo existente de criação em memória
8. Cria VaultHeader em memória
9. Deriva chave via KdfDeriver
10. Serializa VaultHeader
11. Retorna CreatedVaultDraft em memória
```

Nenhuma etapa escreve em disco.

---

## Segurança e Isolamento

- Nenhuma senha mestra foi persistida.
- Nenhuma chave derivada foi persistida.
- Nenhum salt foi persistido.
- Nenhuma senha foi logada.
- Nenhuma chave derivada foi logada.
- Nenhum salt foi logado.
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
- Nenhum AES-GCM foi implementado.
- Nenhum Android Keystore foi implementado.
- Nenhuma biometria foi implementada.
- Nenhum payload cifrado foi criado.
- Nenhum nonce real foi criado.
- Nenhuma tag de autenticação foi criada.
- Nenhum backup/export foi criado.
- Nenhum modo discreto/decoy vault foi criado.
- Roadmap formal não foi alterado nesta fase.

A limpeza determinística de memória em JVM/Android continua fora do escopo desta fase.

---

## Testes Entregues

- Request sem salt externo rejeita timestamp inválido.
- Service com salt gerado rejeita senha mestra inválida antes de gerar salt.
- Service com salt gerado mapeia ausência de `SaltGenerator` para `SaltGenerationFailed`.
- Service com salt gerado mapeia falha do `SaltGenerator` para `SaltGenerationFailed`.
- Service chama `SaltGenerator.generateRecommendedSalt()`.
- Service usa o salt retornado pelo gerador.
- Header é criado com o salt gerado.
- KDF recebe parâmetros com o salt gerado.
- Header é serializado.
- `CreatedVaultDraft` é retornado em memória.
- Fluxo com salt externo continua funcionando sem `SaltGenerator`.
- Falha de KDF após geração de salt retorna `KdfDerivationFailed`.
- Arquitetura não importa Android UI.
- Arquitetura não importa storage.
- Arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- Arquitetura não importa java.io ou java.nio.
- Arquitetura não usa SecureRandom diretamente no pacote vault.
- Arquitetura não usa Cipher, SecretKey ou KeyStore.
- Arquitetura não usa logs.

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
- APK SHA-256: `3da25e81b718e953bbeadbeeaf8c334536634d8b6fe14d251642ea51790eafc5`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 29.

---

## Critérios de Aceite

- `VaultCreationWithoutExternalSaltRequest.kt` criado.
- `VaultCreationService` integra `SaltGenerator`.
- Fluxo com salt externo continua funcionando.
- Fluxo com salt gerado funciona.
- Falha de geração de salt é mapeada para erro tipado.
- Header é criado em memória com salt gerado.
- Header é serializado em memória.
- KDF é chamado por contrato.
- Nenhum storage real foi criado.
- Nenhuma escrita em disco foi criada.
- Nenhuma leitura de disco foi criada.
- Nenhuma UI foi alterada.
- Nenhum AES-GCM foi criado.
- Nenhum Android Keystore foi criado.
- Nenhuma permissão Android foi adicionada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
- Roadmap formal não foi alterado nesta fase.

---

## Commits

```text
f6508b8 docs: add phase 29 plan for generated salt vault creation
a40c950 feat: create vault draft with generated salt
```

## Próxima Fase Recomendada

Phase 30 — Encryption Contract and AES-GCM Boundary

Objetivo futuro: criar os contratos de criptografia autenticada para payloads do vault, definindo nonce, ciphertext, tag e erros, ainda antes de storage real e UI.
