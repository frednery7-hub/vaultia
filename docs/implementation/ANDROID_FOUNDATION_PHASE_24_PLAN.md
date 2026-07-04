# Android Foundation — Phase 24 Plan

## Nome

Argon2id KDF Implementation

## Objetivo

Implementar uma classe concreta isolada para derivação de chave com Argon2id usando `com.lambdapioneer.argon2kt:argon2kt:1.6.0`, respeitando o contrato `KdfDeriver` criado na Phase 23, sem conectar a derivação ao fluxo real de autenticação, storage, UI, `CryptoService`, AES-GCM ou Android Keystore.

A Phase 24 transforma a avaliação de dependência da Phase 22 e os contratos seguros da Phase 23 em uma implementação KDF isolada, testável e auditável.

---

## Decisão Arquitetural

A implementação concreta deve ficar em:

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/Argon2idKdfDeriver.kt
```

Ela deve implementar:

```kotlin
class Argon2idKdfDeriver : KdfDeriver
```

A implementação deve usar apenas o contrato `KdfDeriver`, `KdfParameters`, `KdfResult`, `KdfDerivationOutcome` e `KdfDerivationError` já criados.

---

## Fronteiras de Isolamento

A Phase 24 pode alterar apenas:

```text
core/crypto/kdf
tests de core/crypto/kdf
docs/implementation
```

A Phase 24 não deve alterar:

```text
feature/auth
feature/vault
core/session
core/storage
core/security/contract/CryptoService
MainActivity
BootstrapScreen
AndroidManifest.xml
```

A derivação deve continuar fora do fluxo real do aplicativo.

---

## Comportamento Esperado

### Entrada válida

Quando receber:

- `password: ByteArray` não vazio;
- `parameters: KdfParameters.Argon2id`;
- salt válido;
- output válido;
- iterações válidas;
- memória válida;
- paralelismo válido;

deve retornar:

```text
KdfDerivationOutcome.Success(KdfResult)
```

### Algoritmo incompatível

Quando receber `KdfParameters.Pbkdf2HmacSha256`, deve retornar:

```text
KdfDerivationOutcome.Failure(KdfDerivationError.UnsupportedAlgorithm)
```

A implementação PBKDF2 real fica para fase separada, se necessária.

### Senha vazia

Quando receber `password` vazio, deve retornar:

```text
KdfDerivationOutcome.Failure(KdfDerivationError.InvalidParameters)
```

### Falha nativa

Falhas relacionadas a JNI, ABI, carregamento da biblioteca `.so`, `UnsatisfiedLinkError`, `NoClassDefFoundError` ou problemas equivalentes devem ser mapeadas para:

```text
KdfDerivationOutcome.Failure(KdfDerivationError.NativeLibraryUnavailable)
```

### Falha genérica de execução

Qualquer exceção inesperada durante a derivação deve ser mapeada para:

```text
KdfDerivationOutcome.Failure(KdfDerivationError.DerivationFailed)
```

---

## Parâmetros de Referência

A implementação deve aceitar os parâmetros definidos pelo chamador via `KdfParameters.Argon2id`.

Os parâmetros de referência avaliados na Phase 22 permanecem:

```text
FAST
- memoryCostKiB: 32 * 1024
- iterations: 2
- parallelism: 1
- outputLengthBytes: 32

CONSERVATIVE
- memoryCostKiB: 64 * 1024
- iterations: 2
- parallelism: 1
- outputLengthBytes: 32
```

A Phase 24 não congela parâmetros finais de produção. Parâmetros definitivos ainda dependem de benchmark em aparelho físico real e decisão posterior de release.

---

## Requisitos de Segurança

- A implementação não deve logar senha.
- A implementação não deve logar salt.
- A implementação não deve logar chave derivada.
- A implementação não deve persistir senha.
- A implementação não deve persistir salt.
- A implementação não deve persistir chave derivada.
- A implementação deve usar cópias defensivas já fornecidas pelos modelos da Phase 23.
- A implementação deve retornar `KdfResult` com material derivado protegido por cópia defensiva.
- A implementação não deve alterar permissões Android.
- A implementação não deve adicionar `android.permission.INTERNET`.

Observação: limpeza determinística de memória em JVM/Android é limitada. A Phase 24 deve evitar logs e persistência de material sensível, mas a estratégia completa de lifecycle da chave em memória será tratada em fase posterior de vault/session real.

---

## Escopo

Esta fase cria:

- `Argon2idKdfDeriver`;
- testes unitários da implementação Argon2id;
- testes de erro para senha vazia, algoritmo incompatível e falhas esperadas;
- teste de arquitetura garantindo isolamento do pacote KDF;
- documentação DONE da Phase 24.

---

## Fora de Escopo

Esta fase não implementa:

- PBKDF2 real;
- AES-GCM;
- envelope encryption;
- Android Keystore;
- biometria;
- criação real do cofre;
- desbloqueio real;
- login real;
- tela de senha mestra;
- persistência de parâmetros KDF;
- persistência de salt;
- persistência de chave derivada;
- storage seguro;
- backup/export;
- modo discreto/decoy vault;
- alteração no roadmap formal.

---

## Testes Planejados

Arquivo previsto:

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/Argon2idKdfDeriverTest.kt
```

Cobertura mínima:

- deriva chave Argon2id com parâmetros válidos;
- resultado tem tamanho igual a `outputLengthBytes`;
- duas derivações com mesmos inputs produzem o mesmo resultado;
- duas derivações com salts diferentes produzem resultados diferentes;
- senha vazia retorna `InvalidParameters`;
- parâmetros PBKDF2 retornam `UnsupportedAlgorithm`;
- resultado não expõe array interno mutável;
- implementação não altera storage, UI ou sessão;
- código continua sem permissão `INTERNET`.

A Phase 24 pode usar parâmetros reduzidos nos testes unitários para manter tempo aceitável de execução, desde que a lógica principal também suporte os parâmetros avaliados na Phase 22.

---

## Critérios de Aceite

- `Argon2idKdfDeriver.kt` criado.
- A classe implementa `KdfDeriver`.
- A classe usa `Argon2Mode.ARGON2_ID`.
- Parâmetros Argon2id são mapeados corretamente para a biblioteca.
- PBKDF2 retorna `UnsupportedAlgorithm`.
- Senha vazia retorna `InvalidParameters`.
- Falhas nativas são mapeadas para `NativeLibraryUnavailable`.
- Falhas genéricas são mapeadas para `DerivationFailed`.
- Nenhum fluxo real do app usa KDF ainda.
- Nenhuma UI é alterada.
- Nenhum storage real é criado.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
- Roadmap formal não é alterado nesta fase.
