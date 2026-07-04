# Android Foundation — Phase 28 Done

## Status

Aprovado.

## Nome

Secure Salt Generation Contract

## Objetivo

Criar contrato e implementação isolada para geração de salt criptograficamente seguro, usando CSPRNG, sem criar storage real, sem escrever em disco, sem UI, sem criação final de vault persistido, sem AES-GCM e sem Android Keystore.

A Phase 28 remove a dependência de salt fornecido manualmente nos testes/fluxos futuros e introduz uma peça controlada para geração segura de salt em memória.

---

## Arquivos Criados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/salt/SaltGenerationError.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/salt/SaltGenerationResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/salt/SaltGenerator.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/salt/SecureRandomSaltGenerator.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/salt/SaltGeneratorTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/salt/SecureRandomSaltGeneratorTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/salt/SaltGenerationArchitectureTest.kt
```

---

## Implementação Entregue

### SaltGenerator

- Contrato de geração de salt criado.
- `generateSalt(lengthBytes: Int)` criado.
- `generateRecommendedSalt()` criado.
- Salt mínimo definido como 16 bytes.
- Salt recomendado definido como 32 bytes.
- O contrato não depende de Android UI.
- O contrato não depende de storage.

### SaltGenerationResult

- Resultado explícito de geração criado.
- `Success(salt: ByteArray)` criado.
- `Failure(error: SaltGenerationError)` criado.
- `Success` protege salt com cópia defensiva.
- `Success` expõe `saltLengthBytes`.
- `Success` rejeita salt menor que 16 bytes.
- Salt não é exposto por referência mutável interna.

### SaltGenerationError

Erros tipados criados:

```text
InvalidSaltLength
GenerationFailed
```

### SecureRandomSaltGenerator

- Implementação concreta criada.
- Usa `java.security.SecureRandom`.
- Gera bytes em memória.
- Rejeita tamanho menor que 16 bytes.
- Gera salt recomendado de 32 bytes.
- Mapeia falha interna para `GenerationFailed`.
- Não escreve em disco.
- Não lê disco.
- Não acessa Android Keystore.
- Não implementa AES-GCM.
- Não cria storage real.

---

## Segurança e Isolamento

- Salt mínimo: 16 bytes.
- Salt recomendado: 32 bytes.
- Salt gerado por CSPRNG.
- Salt não é tratado como segredo.
- Salt usa cópia defensiva.
- Nenhum salt é persistido.
- Nenhum salt é logado.
- Nenhum storage real foi criado.
- Nenhuma escrita em disco foi criada.
- Nenhuma leitura de disco foi criada.
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

Observação: esta fase não comprova unicidade matemática absoluta por vault, porque ainda não existe storage real nem registro persistido de salts. Ela garante geração via CSPRNG, tamanho correto e defensive copy. A política de não reutilização será reforçada quando existir vault persistido.

---

## Testes Entregues

- Constantes de salt mínimo e recomendado validadas.
- `Success` protege salt com cópia defensiva.
- `Success` rejeita salt menor que 16 bytes.
- Gerador rejeita tamanho menor que 16 bytes.
- Gerador gera salt de 16 bytes.
- Gerador gera salt recomendado de 32 bytes.
- Chamadas sucessivas retornam arrays independentes.
- Chamadas sucessivas não retornaram o mesmo conteúdo no teste probabilístico simples.
- Falha interna de geração retorna `GenerationFailed`.
- Arquitetura não importa Android UI.
- Arquitetura não importa storage.
- Arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- Arquitetura não usa java.io ou java.nio.
- Arquitetura não usa Cipher, SecretKey ou KeyStore.
- Arquitetura não usa logs.
- Arquitetura permite `java.security.SecureRandom` apenas no gerador concreto.

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
- APK SHA-256: `3cf81ef9ebe607f42573e9b87d3f03c9e9ea0c4273885b6248d9a9056486dba1`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 28.

---

## Critérios de Aceite

- `SaltGenerationError.kt` criado.
- `SaltGenerationResult.kt` criado.
- `SaltGenerator.kt` criado.
- `SecureRandomSaltGenerator.kt` criado.
- Geração de salt mínimo validada.
- Geração de salt recomendado implementada.
- Resultado protege salt com cópia defensiva.
- Implementação usa CSPRNG.
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
45150d3 docs: add phase 28 plan for secure salt generation
db36614 feat: add secure salt generation contract
```

## Próxima Fase Recomendada

Phase 29 — Vault Creation With Generated Salt

Objetivo futuro: integrar o `SecureRandomSaltGenerator` ao fluxo de criação em memória do vault, removendo a necessidade de fornecer salt manualmente, ainda sem storage real e sem UI.
