# Android Foundation — Phase 37 Plan

## Nome

Secure UI State Contracts

## Objetivo

Criar contratos de estado seguro de UI para o futuro frontend real do Vaultia, sem implementar tela real, sem renderizar secrets reais, sem criar storage e sem conectar decrypt à interface.

A Phase 37 transforma a arquitetura documental da Phase 36 em contratos Kotlin mínimos, testáveis e isolados, para impedir estados visuais inseguros nas próximas fases.

---

## Contexto

A Phase 36 definiu a arquitetura segura do frontend e documentou três documentos principais:

- `SECURE_FRONTEND_ARCHITECTURE.md`.
- `SECURE_FRONTEND_RENDERING_RULES.md`.
- `SECURE_FRONTEND_STATE_MODEL.md`.

Também foi registrada a decisão de que Vaultia v1 não implementa duress password, decoy vault, hidden vault ou plausible deniability.

A Phase 37 cria contratos de estado de UI, mas ainda não cria telas reais de cofre.

---

## Arquivos Planejados

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

## Contratos Planejados

### SecureUiState

Estados permitidos:

```text
BOOTSTRAPPING
LOCKED
AUTHENTICATING
UNLOCKED_REDACTED
UNLOCKED_REVEALED
LOCKING
ERROR_REDACTED
```

Regras:

- `LOCKED` não pode exibir conteúdo sensível.
- `AUTHENTICATING` não pode listar itens.
- `UNLOCKED_REDACTED` é o estado padrão após unlock futuro.
- `UNLOCKED_REVEALED` é temporário.
- `LOCKING` limpa visualmente a sessão.
- `ERROR_REDACTED` não pode expor detalhes sensíveis.

### SecureUiAction

Ações planejadas:

- iniciar autenticação;
- autenticação concluída;
- autenticação falhou;
- revelar segredo;
- esconder segredo;
- app foi para background;
- app voltou para foreground;
- timeout de reveal;
- lock solicitado;
- lock concluído.

### SensitiveRevealPolicy

Política mínima:

- reveal precisa ser explícito.
- reveal precisa ter timeout positivo.
- reveal não pode ser permitido em `LOCKED`.
- reveal não pode ser permitido em `AUTHENTICATING`.
- reveal não pode ser permitido em `ERROR_REDACTED`.
- background deve forçar redaction ou lock.

### SecureUiTransitionValidator

Validador de transições permitidas entre estados seguros.

Transições permitidas:

```text
BOOTSTRAPPING -> LOCKED
LOCKED -> AUTHENTICATING
AUTHENTICATING -> UNLOCKED_REDACTED
AUTHENTICATING -> ERROR_REDACTED
ERROR_REDACTED -> LOCKED
UNLOCKED_REDACTED -> UNLOCKED_REVEALED
UNLOCKED_REVEALED -> UNLOCKED_REDACTED
UNLOCKED_REDACTED -> LOCKING
UNLOCKED_REVEALED -> LOCKING
LOCKING -> LOCKED
```

---

## Requisitos de Segurança

- Não criar tela real.
- Não alterar `MainActivity`.
- Não conectar decrypt à UI.
- Não renderizar secrets reais.
- Não criar storage real.
- Não ler disco.
- Não escrever em disco.
- Não criar SQLite.
- Não criar Room.
- Não criar DataStore.
- Não criar SharedPreferences.
- Não adicionar permissão Android.
- Não adicionar `android.permission.INTERNET`.
- Não criar Android Keystore.
- Não criar biometria.
- Não criar backup/export.
- Não implementar duress password.
- Não implementar plausible deniability.
- Não alterar roadmap formal nesta fase.

---

## Testes Planejados

- Validar transições permitidas.
- Rejeitar transições inseguras.
- Validar que reveal exige estado permitido.
- Validar que reveal exige timeout positivo.
- Validar que background força estado redacted/locking.
- Validar arquitetura sem Android UI, storage, logs, crypto direta ou framework de persistência.
- Rodar `./gradlew testDebugUnitTest`.
- Rodar `./scripts/check-android.sh`.
- Confirmar APK sem `android.permission.INTERNET`.

---

## Critérios de Aceite

- Plan criado.
- Contratos Kotlin de estado seguro criados.
- Validador de transição criado.
- Política de reveal criada.
- Testes unitários criados.
- Nenhuma tela real criada.
- Nenhum secret renderizado.
- Nenhum decrypt conectado à UI.
- Nenhum storage criado.
- Nenhuma permissão Android adicionada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
