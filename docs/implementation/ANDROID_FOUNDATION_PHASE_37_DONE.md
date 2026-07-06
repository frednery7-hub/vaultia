# Android Foundation — Phase 37 Done

## Status

Aprovado.

## Nome

Secure UI State Contracts

## Objetivo

Criar contratos de estado seguro de UI para o futuro frontend real do Vaultia, sem implementar tela real, sem renderizar secrets reais, sem criar storage e sem conectar decrypt à interface.

A Phase 37 transformou a arquitetura documental da Phase 36 em contratos Kotlin mínimos, testáveis e isolados, para impedir estados visuais inseguros nas próximas fases.

---

## Arquivos Criados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/ui/security/SecureUiState.kt
apps/android/app/src/main/java/com/vaultia/app/core/ui/security/SecureUiAction.kt
apps/android/app/src/main/java/com/vaultia/app/core/ui/security/SensitiveRevealPolicy.kt
apps/android/app/src/main/java/com/vaultia/app/core/ui/security/SecureUiTransition.kt
apps/android/app/src/main/java/com/vaultia/app/core/ui/security/SecureUiTransitionValidator.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/ui/security/SecureUiTransitionValidatorTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/ui/security/SensitiveRevealPolicyTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/ui/security/SecureUiSecurityArchitectureTest.kt
```

---

## Implementação Entregue

### SecureUiState

- Enum de estados visuais seguros criado.
- Estados criados: `BOOTSTRAPPING`, `LOCKED`, `AUTHENTICATING`, `UNLOCKED_REDACTED`, `UNLOCKED_REVEALED`, `LOCKING` e `ERROR_REDACTED`.
- Cada estado define se pode renderizar lista do vault.
- Cada estado define se pode renderizar valor sensível.
- Cada estado define se exige redaction.
- Apenas `UNLOCKED_REVEALED` permite renderização sensível.

### SecureUiAction

- Enum de ações seguras de UI criado.
- Inclui autenticação, reveal, hide, background, foreground, timeout e lock.

### SensitiveRevealPolicy

- Política de reveal criada.
- Timeout precisa ser positivo.
- Timeout padrão definido em 30 segundos.
- Reveal só é permitido a partir de `UNLOCKED_REDACTED`.
- Timeout de reveal redige `UNLOCKED_REVEALED` para `UNLOCKED_REDACTED`.
- Background redige `UNLOCKED_REVEALED`.
- Background envia `UNLOCKED_REDACTED` para `LOCKING`.

### SecureUiTransition

- Modelo de transição segura criado.
- Contém estado de origem, ação e estado de destino.

### SecureUiTransitionValidator

- Validador de transições seguras criado.
- Permite somente transições explicitamente documentadas.
- Rejeita reveal a partir de estados inseguros.
- Rejeita unlock direto para estado revelado.
- Rejeita acesso direto a estados desbloqueados a partir de `LOCKED`.

---

## Segurança e Isolamento

- Nenhuma tela real foi criada.
- `MainActivity` não foi alterada.
- Nenhum decrypt foi conectado à UI.
- Nenhum secret real foi renderizado.
- Nenhum storage real foi criado.
- Nenhuma leitura de disco foi criada pelo app.
- Nenhuma escrita em disco foi criada pelo app.
- Nenhum SQLite foi criado.
- Nenhum Room foi criado.
- Nenhum DataStore foi criado.
- Nenhum SharedPreferences foi criado.
- Nenhuma permissão Android foi adicionada.
- Nenhum `android.permission.INTERNET` foi adicionado.
- Nenhum Android Keystore foi criado.
- Nenhuma biometria foi criada.
- Nenhum backup/export foi criado.
- Nenhum duress password foi criado.
- Nenhuma plausible deniability foi criada.
- Roadmap formal não foi alterado nesta fase.

---

## Testes Entregues

- Transições documentadas são permitidas.
- Reveal é rejeitado a partir de estados inseguros.
- Unlock direto para `UNLOCKED_REVEALED` é rejeitado.
- Acesso direto de `LOCKED` para estados desbloqueados é rejeitado.
- Capacidade de renderização sensível só existe em `UNLOCKED_REVEALED`.
- Timeout de reveal precisa ser positivo.
- Timeout padrão é positivo e conservador.
- Reveal só é permitido em `UNLOCKED_REDACTED`.
- Timeout redige estado revelado.
- Background redige ou inicia locking para estados desbloqueados.
- Arquitetura não importa Android UI.
- Arquitetura não importa storage.
- Arquitetura não importa crypto direta.
- Arquitetura não usa logs.
- Arquitetura não usa persistência.
- Arquitetura não contém campos de senha, key, plaintext, ciphertext, nonce, authentication tag ou payload serializado.

---

## Validações Executadas

### Testes Unitários

```bash
./gradlew testDebugUnitTest
```

Resultado:

```text
BUILD SUCCESSFUL
```

### Check Android Completo

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
- APK SHA-256: `5b371918e61d37c90616be958b40e8920f8b57b9f6c74d35c2f0c01c49e55684`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 37.

---

## Commits

```text
4be2df1 docs: add phase 37 plan for secure ui state contracts
345462f feat: add secure ui state contracts
```

## Próxima Fase Recomendada

Phase 38 — Secure Screen Guard
