# Android Foundation — Phase 28 Plan

## Nome

Secure Salt Generation Contract

## Objetivo

Criar contrato e implementação isolada para geração de salt criptograficamente seguro, usando CSPRNG, sem criar storage real, sem escrever em disco, sem UI, sem criação final de vault persistido, sem AES-GCM e sem Android Keystore.

A Phase 28 remove a dependência de salt fornecido manualmente nos testes/fluxos futuros e introduz uma peça controlada para geração segura de salt em memória.

---

## Contexto Técnico

A Phase 27 criou o fluxo `VaultCreationService`, mas ele ainda recebe o salt externamente.

A Phase 28 cria a peça responsável por gerar esse salt com uma fonte criptograficamente adequada.

Dependências conceituais anteriores:

```text
Phase 23 — KDF Contract and Safe Models
Phase 25 — KDF Profiles and Public Vault Header Contract
Phase 27 — Vault Creation Draft Without Persistence
```

O salt não é segredo, mas deve ser único por vault e gerado com entropia adequada.

---

## Decisão Arquitetural

A geração de salt será isolada em um contrato próprio, separado de `VaultCreationService`.

Motivos:

- manter o fluxo de criação testável;
- permitir injeção de geradores determinísticos em teste;
- evitar acoplamento prematuro entre criação de vault, storage e fonte de entropia;
- permitir auditoria específica da geração de salt.

O contrato deve expor geração de salt por tamanho explícito e um helper para o tamanho recomendado pelo perfil KDF.

---

## Arquivos Planejados

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

## Contratos Planejados

### SaltGenerator

Interface para geração de salt.

Métodos planejados:

```text
generateSalt(lengthBytes: Int): SaltGenerationResult
generateRecommendedSalt(): SaltGenerationResult
```

Regras:

- `lengthBytes` deve ser pelo menos 16 bytes.
- tamanho recomendado deve ser 32 bytes.
- resultado deve usar cópia defensiva.
- falhas devem ser retornadas como resultado tipado.
- não deve lançar exceção como caminho normal.

### SecureRandomSaltGenerator

Implementação baseada em `java.security.SecureRandom`.

Regras:

- usar CSPRNG via `SecureRandom`;
- gerar bytes em memória;
- não escrever em disco;
- não ler disco;
- não usar Android Keystore;
- não usar AES-GCM;
- não criar storage real.

### SaltGenerationResult

Resultado explícito:

```text
Success(salt: ByteArray)
Failure(error: SaltGenerationError)
```

A classe `Success` deve proteger o salt com cópia defensiva.

### SaltGenerationError

Erros planejados:

```text
InvalidSaltLength
GenerationFailed
```

---

## Regras de Segurança

- Salt mínimo: 16 bytes.
- Salt recomendado: 32 bytes.
- Salt deve ser gerado por CSPRNG.
- Salt não é segredo.
- Salt deve ser único por vault.
- Salt deve usar cópia defensiva.
- Gerador não deve persistir salt.
- Gerador não deve logar salt.
- Gerador não deve acessar storage.
- Gerador não deve acessar UI.
- Gerador não deve adicionar permissão Android.
- Gerador não deve adicionar `android.permission.INTERNET`.

Observação: esta fase não comprova unicidade matemática absoluta. Ela garante geração via CSPRNG e valida tamanho/defensive copy. A política de não reutilização por vault será reforçada quando houver storage real.

---

## Escopo

Esta fase cria:

- contrato de geração de salt;
- resultado tipado de geração;
- erros tipados de geração;
- implementação com `SecureRandom`;
- helper de salt recomendado de 32 bytes;
- testes unitários;
- testes de cópia defensiva;
- testes de arquitetura;
- relatório DONE.

---

## Fora de Escopo

Esta fase não implementa:

- storage real;
- escrita em disco;
- leitura de disco;
- criação final de vault persistido;
- integração automática com `VaultCreationService`;
- UI;
- onboarding;
- login real;
- desbloqueio real;
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

- rejeita tamanho menor que 16 bytes.
- gera salt de 16 bytes.
- gera salt recomendado de 32 bytes.
- resultado `Success` protege salt com cópia defensiva.
- chamadas sucessivas retornam arrays independentes.
- chamadas sucessivas não devem retornar o mesmo conteúdo em teste probabilístico simples.
- erro interno de geração retorna `GenerationFailed`.
- arquitetura não importa Android UI.
- arquitetura não importa storage.
- arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- arquitetura não usa java.io ou java.nio.
- arquitetura não usa Cipher, SecretKey ou KeyStore.
- arquitetura não usa logs.
- arquitetura permite `java.security.SecureRandom` apenas no gerador concreto.

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
