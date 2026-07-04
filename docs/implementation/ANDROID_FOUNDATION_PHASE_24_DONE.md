# Android Foundation — Phase 24 Done

## Status

Aprovado após commit corretivo.

## Nome

Argon2id KDF Implementation

## Objetivo

Implementar uma classe concreta isolada para derivação de chave com Argon2id usando `com.lambdapioneer.argon2kt:argon2kt:1.6.0`, respeitando o contrato `KdfDeriver` criado na Phase 23, sem conectar a derivação ao fluxo real de autenticação, storage, UI, `CryptoService`, AES-GCM ou Android Keystore.

---

## Arquivos Criados ou Alterados

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/kdf/Argon2idKdfDeriver.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/Argon2idKdfDeriverTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/kdf/KdfArchitectureTest.kt
```

---

## Implementação Entregue

- `Argon2idKdfDeriver` criado.
- A classe implementa `KdfDeriver`.
- A implementação usa `Argon2Mode.ARGON2_ID`.
- A implementação usa `Argon2Kt().hash(...)`.
- O retorno `Argon2KtResult` é convertido para bytes usando `rawHashAsHexadecimal()` e conversão hexadecimal interna.
- A implementação aceita apenas `KdfParameters.Argon2id`.
- `KdfParameters.Pbkdf2HmacSha256` retorna `KdfDerivationError.UnsupportedAlgorithm`.
- Senha vazia retorna `KdfDerivationError.InvalidParameters`.
- Falhas nativas são mapeadas para `KdfDerivationError.NativeLibraryUnavailable`.
- Falhas genéricas são mapeadas para `KdfDerivationError.DerivationFailed`.
- Resultado bem-sucedido retorna `KdfDerivationOutcome.Success(KdfResult)`.
- O material derivado continua protegido por cópia defensiva via `KdfResult`.

---

## Testes Entregues

- Derivação Argon2id com parâmetros válidos.
- Resultado com tamanho igual a `outputLengthBytes`.
- Mesmos inputs produzem mesmo resultado.
- Salts diferentes produzem resultados diferentes.
- Senha vazia retorna `InvalidParameters`.
- Parâmetros PBKDF2 retornam `UnsupportedAlgorithm`.
- Falha nativa retorna `NativeLibraryUnavailable`.
- Falha genérica retorna `DerivationFailed`.
- Resultado não expõe array interno mutável.
- Teste de arquitetura permite `argon2kt` apenas em `Argon2idKdfDeriver.kt`.
- Teste de arquitetura mantém KDF isolado de Android UI, storage, Room, DataStore e SQLite.

Observação: os testes unitários usam `Argon2idHashFunction` fake/determinística para validar contrato, mapeamento, erros e isolamento sem depender de JNI no JVM test runner. A execução real da biblioteca nativa já havia sido validada em `androidTest` na Phase 22.

---

## Segurança e Isolamento

- Nenhum fluxo real do app usa KDF ainda.
- Nenhuma UI foi alterada.
- `feature/auth` não foi alterado.
- `feature/vault` não foi alterado.
- `core/session` não foi alterado.
- `core/storage` não foi alterado.
- `CryptoService` não foi alterado.
- `MainActivity` não foi alterada.
- `BootstrapScreen` não foi alterado.
- `AndroidManifest.xml` não foi alterado.
- Nenhuma senha foi persistida.
- Nenhum salt foi persistido.
- Nenhuma chave derivada foi persistida.
- Nenhum storage real foi criado.
- Nenhum AES-GCM foi implementado.
- Nenhum Android Keystore foi implementado.
- Nenhuma biometria foi implementada.
- Nenhuma permissão Android foi adicionada.
- APK permanece sem `android.permission.INTERNET`.

Observação: limpeza determinística de memória em JVM/Android é limitada. A Phase 24 evita logs e persistência de material sensível, mas a política completa de lifecycle de chave em memória será tratada em fase posterior de vault/session real.

---

## Incidente Técnico Corrigido

Durante a primeira implementação, o build falhou porque `Argon2Kt().hash(...)` retorna `Argon2KtResult`, não `ByteArray`.

Erro observado:

```text
Return type mismatch: expected ByteArray, actual Argon2KtResult
```

O script de execução continuou após o `BUILD FAILED` e criou o commit inicial quebrado:

```text
34cf227 feat: implement isolated argon2id kdf deriver
```

A correção foi feita sem reescrever histórico, no commit:

```text
4877b7d fix: map argon2kt result to derived key bytes
```

A validação final aprovada é o estado do projeto após o commit `4877b7d`.

A causa foi corrigida convertendo `Argon2KtResult` para bytes via `rawHashAsHexadecimal()` e conversão hexadecimal controlada.

---

## Validações Executadas

### Testes Unitários

Comando:

```bash
./gradlew testDebugUnitTest
```

Resultado final:

```text
BUILD SUCCESSFUL
```

### Check Android Completo

Comando:

```bash
./scripts/check-android.sh
```

Resultado final:

```text
VAULTIA_ANDROID_CHECK_OK
```

Evidências finais:

- Manifest fonte sem `android.permission.INTERNET`.
- APK compilado sem `android.permission.INTERNET`.
- Nenhum arquivo perigoso apareceu no Git status.
- APK SHA-256: `f9a7267b362fc4b2d575bd04a8349ca14e401620e73bc77216a94fd3db889f4b`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 24.

---

## Critérios de Aceite

- `Argon2idKdfDeriver.kt` criado.
- A classe implementa `KdfDeriver`.
- A classe usa `Argon2Mode.ARGON2_ID`.
- Parâmetros Argon2id são mapeados para a biblioteca.
- PBKDF2 retorna `UnsupportedAlgorithm`.
- Senha vazia retorna `InvalidParameters`.
- Falhas nativas são mapeadas para `NativeLibraryUnavailable`.
- Falhas genéricas são mapeadas para `DerivationFailed`.
- Nenhum fluxo real do app usa KDF ainda.
- Nenhuma UI foi alterada.
- Nenhum storage real foi criado.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
- Roadmap formal não foi alterado nesta fase.

---

## Commits

```text
7a1c483 docs: add phase 24 plan for argon2id kdf implementation
34cf227 feat: implement isolated argon2id kdf deriver
4877b7d fix: map argon2kt result to derived key bytes
```

## Próxima Fase Recomendada

Phase 25 — KDF Parameter Profiles and Vault Header Draft

Objetivo futuro: definir perfis nomeados de parâmetros KDF, formato inicial de header criptográfico do vault e estratégia de persistência segura de parâmetros públicos como salt, algoritmo, memória, iterações, paralelismo e versão.
