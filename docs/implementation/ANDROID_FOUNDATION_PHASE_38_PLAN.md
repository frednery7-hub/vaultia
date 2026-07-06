# Android Foundation — Phase 38 Plan

## Nome

Secure Screen Guard

## Objetivo

Criar contratos de proteção de tela sensível para o futuro frontend real do Vaultia, cobrindo screenshot blocking, screen recording mitigation, recents screen redaction, background redaction e lifecycle guard, ainda sem implementar tela real do cofre e sem renderizar secrets reais.

A Phase 38 transforma a arquitetura segura de frontend e os contratos de estado da Phase 37 em uma camada de decisão para proteção visual.

---

## Contexto

A Phase 37 criou contratos seguros de UI:

- `SecureUiState`.
- `SecureUiAction`.
- `SensitiveRevealPolicy`.
- `SecureUiTransition`.
- `SecureUiTransitionValidator`.

A Phase 38 adiciona uma camada de política de proteção visual, sem conectar ainda a `MainActivity`, sem alterar Compose real, sem storage e sem decrypt.

---

## Arquivos Planejados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/ui/screen/SecureScreenPolicy.kt
apps/android/app/src/main/java/com/vaultia/app/core/ui/screen/SecureScreenMode.kt
apps/android/app/src/main/java/com/vaultia/app/core/ui/screen/SecureScreenDecision.kt
apps/android/app/src/main/java/com/vaultia/app/core/ui/screen/SecureScreenEvent.kt
apps/android/app/src/main/java/com/vaultia/app/core/ui/screen/SecureScreenGuard.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/ui/screen/SecureScreenGuardTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/ui/screen/SecureScreenPolicyTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/ui/screen/SecureScreenArchitectureTest.kt
```

---

## Contratos Planejados

### SecureScreenMode

Modos planejados:

```text
PUBLIC
REDACTED
SENSITIVE
LOCKED
```

Regras:

- `PUBLIC` não exige `FLAG_SECURE`.
- `REDACTED` deve ocultar conteúdo sensível.
- `SENSITIVE` deve exigir `FLAG_SECURE`.
- `LOCKED` deve redigir e bloquear conteúdo do vault.

### SecureScreenEvent

Eventos planejados:

- app foi para foreground;
- app foi para background;
- app entrou em pause;
- app entrou em stop;
- tela sensível aberta;
- tela sensível fechada;
- secret revelado;
- secret redigido;
- lock solicitado.

### SecureScreenDecision

Decisões planejadas:

- exigir secure flag;
- limpar secure flag;
- redigir conteúdo;
- iniciar lock visual;
- manter estado atual;
- bloquear reveal.

### SecureScreenPolicy

Política mínima:

- telas sensíveis exigem secure flag;
- conteúdo revelado exige secure flag;
- background deve redigir ou iniciar lock;
- recents screen não deve receber conteúdo sensível;
- tela locked deve ser redigida;
- tela pública não deve forçar secure flag sem necessidade.

### SecureScreenGuard

Guard puro de decisão, sem Android API direta nesta fase.

A implementação real de `WindowManager.LayoutParams.FLAG_SECURE` deve ficar para integração posterior. Nesta fase, o guard apenas decide quando a flag deve ser exigida.

---

## Requisitos de Segurança

- Não criar tela real.
- Não alterar `MainActivity` nesta fase.
- Não usar Android `Window` diretamente nesta fase.
- Não chamar `WindowManager.LayoutParams.FLAG_SECURE` diretamente nesta fase.
- Não conectar decrypt à UI.
- Não renderizar secrets reais.
- Não criar storage real.
- Não ler disco.
- Não escrever em disco.
- Não criar SQLite, Room, DataStore ou SharedPreferences.
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

- Validar que tela sensível exige secure flag.
- Validar que conteúdo revelado exige secure flag.
- Validar que background redige ou inicia lock.
- Validar que tela pública não exige secure flag.
- Validar que locked não permite conteúdo sensível.
- Validar que recents/background não preservam estado sensível.
- Validar arquitetura sem Android API direta, storage, logs, crypto direta ou persistência.
- Rodar `./gradlew testDebugUnitTest`.
- Rodar `./scripts/check-android.sh`.
- Confirmar APK sem `android.permission.INTERNET`.

---

## Critérios de Aceite

- Plan criado.
- Contratos de screen guard criados.
- Política de tela sensível criada.
- Guard de decisão criado.
- Testes unitários criados.
- Nenhuma tela real criada.
- Nenhum Android `Window` usado diretamente.
- Nenhum secret renderizado.
- Nenhum decrypt conectado à UI.
- Nenhum storage criado.
- Nenhuma permissão Android adicionada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
