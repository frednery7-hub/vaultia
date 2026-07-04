# Android Foundation — Phase 23 Plan

## Nome

KDF Contract and Safe Models

## Objetivo

Definir os contratos internos, modelos seguros e representação de erros para derivação de chaves a partir de senha dentro do pacote `core/crypto/kdf`, sem implementar derivação real, sem integrar ao fluxo de autenticação, sem persistir parâmetros e sem introduzir criptografia simétrica nesta fase.

A Phase 23 prepara a fronteira arquitetural para que a futura implementação Argon2id seja isolada, testável, auditável e substituível por fallback documentado se necessário.

---

## Decisão Arquitetural

Os contratos KDF devem ficar em:

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/
```

Arquivos planejados:

```text
KdfAlgorithm.kt             -> enum dos algoritmos permitidos
KdfParameters.kt            -> parâmetros seguros por algoritmo
KdfResult.kt                -> wrapper seguro para material derivado
KdfDerivationError.kt       -> erros tipados de derivação
KdfDerivationOutcome.kt     -> sucesso/falha sem depender de exceção como fluxo
KdfDeriver.kt               -> interface contratual da derivação
```

A Phase 23 não deve importar `com.lambdapioneer.argon2kt` no código `main`. A implementação concreta baseada em Argon2id fica para fase posterior.

---

## Fronteiras de Isolamento

```text
feature/auth
    sem alteração
    ainda não realiza login ou desbloqueio real

core/security/contract/CryptoService
    sem alteração
    não recebe KDF real nesta fase

core/session
    sem alteração

core/storage
    sem alteração

core/crypto/kdf
    declara apenas contratos, parâmetros, resultados e erros
```

---

## Contratos Planejados

### KdfAlgorithm

Enumeração dos algoritmos aceitos pelo contrato:

```text
ARGON2ID
PBKDF2_HMAC_SHA256
```

ARGON2ID é o candidato principal resultante da Phase 22. PBKDF2_HMAC_SHA256 permanece como fallback documentado.

### KdfParameters

Modelo seguro de parâmetros por algoritmo.

Regras obrigatórias:

- `salt` deve ter no mínimo 16 bytes;
- `salt` deve ser protegido por cópia defensiva;
- `outputLengthBytes` deve ser maior que zero;
- `iterations` deve ser maior que zero;
- `memoryCostKiB` deve ser maior que zero para Argon2id;
- `parallelism` deve ser maior que zero para Argon2id;
- parâmetros inválidos devem falhar cedo no momento da construção.

### KdfResult

Wrapper seguro para o material derivado.

Regras obrigatórias:

- não deve ser `data class`;
- não deve expor `ByteArray` interno diretamente;
- deve rejeitar chave derivada vazia;
- deve proteger o material derivado por cópia defensiva;
- deve expor apenas cópia do material derivado quando necessário.

Mesmo sem derivação real nesta fase, o contrato deve nascer com tratamento correto para material sensível.

### KdfDerivationError

Erros tipados previstos:

```text
InvalidParameters
UnsupportedAlgorithm
NativeLibraryUnavailable
DerivationFailed
```

`NativeLibraryUnavailable` cobre falhas de ABI, JNI, carregamento de `.so` ou runtime nativo.

### KdfDerivationOutcome

Representação explícita de sucesso/falha:

```text
Success(result: KdfResult)
Failure(error: KdfDerivationError)
```

Essa estrutura evita usar `Result<KdfResult>` como fluxo de segurança e mantém os erros auditáveis.

### KdfDeriver

Interface contratual:

```kotlin
interface KdfDeriver {
    fun derive(
        password: ByteArray,
        parameters: KdfParameters,
    ): KdfDerivationOutcome
}
```

A interface não deve conter implementação real nesta fase.

---

## Escopo

Esta fase cria:

- contratos KDF em `core/crypto/kdf`;
- modelos seguros de parâmetros;
- wrapper seguro de resultado derivado;
- erros tipados;
- outcome explícito de sucesso/falha;
- testes unitários dos modelos;
- teste de arquitetura para evitar dependências indevidas.

---

## Fora de Escopo

Esta fase não implementa:

- derivação real com Argon2id;
- derivação real com PBKDF2;
- integração com `CryptoService`;
- integração com `SessionManager`;
- integração com UI;
- login real;
- desbloqueio real;
- persistência de senha;
- persistência de salt;
- persistência de parâmetros KDF;
- persistência de chave derivada;
- AES-GCM;
- envelope encryption;
- Android Keystore;
- biometria;
- storage seguro;
- vault real.

---

## Testes Planejados

Arquivos previstos:

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfParametersTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfResultTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfDerivationOutcomeTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfArchitectureTest.kt
```

Cobertura mínima:

- rejeitar salt vazio;
- rejeitar salt menor que 16 bytes;
- rejeitar `outputLengthBytes <= 0`;
- rejeitar `iterations <= 0`;
- rejeitar `memoryCostKiB <= 0` para Argon2id;
- rejeitar `parallelism <= 0` para Argon2id;
- aceitar parâmetros Argon2id válidos;
- aceitar parâmetros PBKDF2 válidos;
- proteger `salt` por cópia defensiva;
- rejeitar `derivedKey` vazio;
- proteger `derivedKey` por cópia defensiva;
- representar sucesso com `KdfDerivationOutcome.Success`;
- representar falha com `KdfDerivationOutcome.Failure`;
- garantir que `core/crypto/kdf` não importa `android.*`;
- garantir que `core/crypto/kdf` não importa UI;
- garantir que `core/crypto/kdf` não importa storage;
- garantir que `core/crypto/kdf` não importa Room, DataStore ou SQLite;
- garantir que código `main` da Phase 23 não importa `com.lambdapioneer.argon2kt`.

---

## Critérios de Aceite

- `KdfAlgorithm.kt` criado em `core/crypto/kdf`;
- `KdfParameters.kt` criado em `core/crypto/kdf`;
- `KdfResult.kt` criado em `core/crypto/kdf`;
- `KdfDerivationError.kt` criado em `core/crypto/kdf`;
- `KdfDerivationOutcome.kt` criado em `core/crypto/kdf`;
- `KdfDeriver.kt` criado em `core/crypto/kdf`;
- `KdfResult` não é `data class`;
- `salt` e `derivedKey` usam cópia defensiva;
- testes unitários passam;
- teste de arquitetura passa;
- `./gradlew testDebugUnitTest` passa;
- `scripts/check-android.sh` passa;
- APK permanece sem `android.permission.INTERNET`;
- nenhum storage real é criado;
- nenhum fluxo real de autenticação é alterado;
- roadmap formal não é alterado nesta fase.
