# Android Foundation — Phase 27 Done

## Status

Aprovado.

## Nome

Vault Creation Draft Without Persistence

## Objetivo

Criar um fluxo interno em memória para montar um vault inicial combinando política de senha mestra, perfil KDF, salt fornecido, derivação Argon2id e header serializável, sem criar storage real, sem escrever em disco, sem UI, sem AES-GCM, sem Android Keystore e sem persistir senha ou chave derivada.

A Phase 27 conecta os contratos criados nas fases anteriores em um fluxo controlado e testável, ainda sem transformar isso em cofre real persistido no dispositivo.

---

## Arquivos Criados

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

## Implementação Entregue

### VaultCreationRequest

- Request de criação de vault em memória criado.
- Recebe `masterPassword`.
- Recebe `salt`.
- Recebe `createdAtEpochMillis`.
- Protege salt com cópia defensiva.
- Rejeita timestamp inválido.
- Não persiste senha.
- Não persiste salt.

### VaultCreationService

- Serviço de criação de vault em memória criado.
- Valida senha mestra com `MasterPasswordPolicy`.
- Rejeita senha mestra inválida.
- Usa `KdfProfileCatalog.defaultProfile()`.
- Usa perfil padrão `CONSERVATIVE`.
- Cria `VaultHeader` em memória.
- Converte `VaultHeader` para `KdfParameters`.
- Chama `KdfDeriver` por contrato.
- Mapeia falha de KDF para `KdfDerivationFailed`.
- Serializa `VaultHeader` em memória.
- Valida serialização via decode.
- Mapeia falha de serialização para `HeaderSerializationFailed`.
- Retorna `CreatedVaultDraft` apenas em memória.

### CreatedVaultDraft

- Draft em memória de vault criado.
- Contém `header`.
- Contém `serializedHeader`.
- Contém `kdfResult`.
- Não cria payload cifrado.
- Não persiste chave derivada.
- Não escreve em disco.

### VaultCreationResult

- Resultado explícito do fluxo criado.
- `Success(draft: CreatedVaultDraft)` criado.
- `Failure(error: VaultCreationError)` criado.
- Fluxo não usa exceção como caminho normal.

### VaultCreationError

Erros tipados criados:

```text
InvalidMasterPassword
InvalidCreationRequest
InvalidVaultHeader
KdfDerivationFailed
HeaderSerializationFailed
```

Observação: `InvalidVaultHeader` foi reservado para evolução do fluxo, mas nesta implementação inicial erros de header inválido entram como `InvalidCreationRequest` quando derivados do request.

---

## Fluxo Implementado

```text
1. Recebe VaultCreationRequest
2. Valida senha mestra com MasterPasswordPolicy
3. Rejeita senha inválida
4. Obtém KdfProfileCatalog.defaultProfile()
5. Cria VaultHeader a partir do perfil, salt e timestamp
6. Converte VaultHeader para KdfParameters
7. Deriva chave via KdfDeriver
8. Rejeita falha de derivação
9. Serializa VaultHeader
10. Valida round-trip da serialização via decode
11. Retorna CreatedVaultDraft em memória
```

Nenhuma etapa escreve em disco.

---

## Segurança e Isolamento

- Nenhum storage real foi criado.
- Nenhuma escrita em disco foi criada.
- Nenhuma leitura de disco foi criada.
- Nenhum arquivo foi criado pelo app.
- Nenhum SQLite foi criado.
- Nenhum Room foi criado.
- Nenhum DataStore foi criado.
- Nenhum SharedPreferences foi criado.
- Nenhuma UI foi criada.
- Nenhum log foi criado.
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

O resultado contém `KdfResult` apenas em memória para provar o fluxo de criação. A limpeza determinística de memória em JVM/Android permanece fora do escopo desta fase e será tratada em lifecycle de vault/session real.

---

## Incidente Técnico Corrigido

A primeira tentativa de teste falhou porque `KdfDeriver` é uma interface normal, não uma `fun interface`. Portanto Kotlin não aceita `KdfDeriver { ... }`.

Erro observado:

```text
Interface KdfDeriver does not have constructors.
```

Correção aplicada: os testes passaram a usar `object : KdfDeriver { override fun derive(...) }` e um helper `CapturingSuccessfulKdfDeriver` para capturar parâmetros.

Não houve commit quebrado. O script parou antes do commit por causa de `set -euo pipefail`.

---

## Testes Entregues

- Request protege salt com cópia defensiva.
- Request rejeita timestamp inválido.
- Service rejeita senha mestra inválida.
- Service rejeita salt inválido.
- Service usa perfil padrão `CONSERVATIVE`.
- Service cria `VaultHeader` válido.
- Service serializa `VaultHeader`.
- Service chama `KdfDeriver` com parâmetros derivados do header.
- Service retorna `Success` com `CreatedVaultDraft` em memória.
- Draft contém header.
- Draft contém serializedHeader.
- Draft contém `KdfResult` em memória.
- Falha do KDF retorna `KdfDerivationFailed`.
- Erro de serialização retorna `HeaderSerializationFailed`.
- Arquitetura não importa Android UI.
- Arquitetura não importa storage.
- Arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- Arquitetura não importa java.io ou java.nio.
- Arquitetura não usa SecureRandom nesta fase.
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
- APK SHA-256: `f59280f221112b162b3a4e4bd2e2165422c397360e146aa9627e00ba31cf59b7`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 27.

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
- Roadmap formal não foi alterado nesta fase.

---

## Commits

```text
df08df5 docs: add phase 27 plan for in-memory vault creation
92c813d feat: add in-memory vault creation draft
```

## Próxima Fase Recomendada

Phase 28 — Secure Salt Generation Contract

Objetivo futuro: criar contrato e implementação isolada para geração de salt criptograficamente seguro via CSPRNG Android/JVM, ainda sem storage real e sem criação final de vault persistido.
