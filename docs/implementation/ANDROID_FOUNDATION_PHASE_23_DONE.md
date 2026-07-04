# Android Foundation — Phase 23 Done

## Status

Aprovado.

## Nome

KDF Contract and Safe Models

## Objetivo

Criar os contratos internos, modelos seguros e representação tipada de erros para derivação de chaves a partir de senha dentro do pacote `core/crypto/kdf`, sem implementar derivação real e sem integrar o KDF ao fluxo de autenticação, storage, UI ou criptografia simétrica.

---

## Arquivos Criados

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/KdfAlgorithm.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/KdfParameters.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/KdfResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/KdfDerivationError.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/KdfDerivationOutcome.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/KdfDeriver.kt
```

Testes criados:

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfParametersTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfResultTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfDerivationOutcomeTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfArchitectureTest.kt
```

---

## Contratos Entregues

### KdfAlgorithm

- `ARGON2ID` definido como algoritmo principal.
- `PBKDF2_HMAC_SHA256` definido como fallback documentado.

### KdfParameters

- Modelo separado por algoritmo.
- `Argon2id` valida salt, output, iterações, memória e paralelismo.
- `Pbkdf2HmacSha256` valida salt, output e iterações.
- `salt` protegido por cópia defensiva.
- Salt mínimo definido em 16 bytes.
- Parâmetros inválidos falham cedo no construtor.

### KdfResult

- Implementado como classe comum, não `data class`.
- Rejeita chave derivada vazia.
- Protege `derivedKey` por cópia defensiva.
- Expõe apenas cópia do material derivado.
- Expõe o tamanho da chave derivada sem expor o array interno.

### KdfDerivationError

Erros tipados criados:

```text
InvalidParameters
UnsupportedAlgorithm
NativeLibraryUnavailable
DerivationFailed
```

### KdfDerivationOutcome

- `Success(result: KdfResult)`
- `Failure(error: KdfDerivationError)`

O contrato evita usar `Result<KdfResult>` como fluxo de segurança e mantém falhas explicitamente auditáveis.

### KdfDeriver

Interface criada sem implementação real:

```kotlin
interface KdfDeriver {
    fun derive(
        password: ByteArray,
        parameters: KdfParameters,
    ): KdfDerivationOutcome
}
```

---

## Segurança e Isolamento

- Nenhuma derivação real foi implementada.
- Nenhum fluxo real de autenticação foi alterado.
- Nenhuma senha foi persistida.
- Nenhum salt foi persistido.
- Nenhuma chave derivada foi persistida.
- Nenhum storage real foi criado.
- Nenhuma UI foi alterada.
- `CryptoService` não foi alterado.
- `SessionManager` não foi alterado.
- `VaultRepository` não foi alterado.
- Nenhuma integração com AES-GCM foi criada.
- Nenhuma integração com Android Keystore foi criada.
- Nenhuma integração com biometria foi criada.
- O pacote `main` da Phase 23 não importa `com.lambdapioneer.argon2kt`.

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

Evidências do check:

- Manifest fonte sem `android.permission.INTERNET`.
- APK compilado sem `android.permission.INTERNET`.
- Nenhum arquivo perigoso apareceu no Git status.
- APK SHA-256: `e689d4fcda9cef52c9cfc1b8ebb86d4dbee4344aae1f95c65fb30ecd08507268`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 23.

---

## Critérios de Aceite

- `KdfAlgorithm.kt` criado.
- `KdfParameters.kt` criado.
- `KdfResult.kt` criado.
- `KdfDerivationError.kt` criado.
- `KdfDerivationOutcome.kt` criado.
- `KdfDeriver.kt` criado.
- `KdfResult` não é `data class`.
- `salt` usa cópia defensiva.
- `derivedKey` usa cópia defensiva.
- Testes unitários passam.
- Teste de arquitetura passa.
- Check Android passa.
- APK permanece sem `android.permission.INTERNET`.
- Nenhum storage real foi criado.
- Nenhum fluxo real de autenticação foi alterado.
- Roadmap formal não foi alterado nesta fase.

---

## Commit

```text
fcd2773 feat: add kdf contract models for phase 23
```

## Próxima Fase Recomendada

Phase 24 — Argon2id KDF Implementation

Objetivo futuro: implementar uma classe concreta isolada para Argon2id usando `argon2kt`, respeitando o contrato `KdfDeriver`, sem ainda conectar a derivação ao fluxo real de login ou storage.
