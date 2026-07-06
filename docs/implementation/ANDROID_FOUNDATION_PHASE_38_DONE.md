# Android Foundation — Phase 38 Done

## Status

Aprovado.

## Nome

Secure Screen Guard

## Objetivo

Criar contratos de proteção de tela sensível para o futuro frontend real do Vaultia, cobrindo screenshot blocking, screen recording mitigation, recents screen redaction, background redaction e lifecycle guard, ainda sem implementar tela real do cofre e sem renderizar secrets reais.

A Phase 38 transformou os contratos de estado da Phase 37 em uma camada pura de decisão para proteção visual.

---

## Arquivos Criados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/ui/screen/SecureScreenMode.kt
apps/android/app/src/main/java/com/vaultia/app/core/ui/screen/SecureScreenEvent.kt
apps/android/app/src/main/java/com/vaultia/app/core/ui/screen/SecureScreenDecision.kt
apps/android/app/src/main/java/com/vaultia/app/core/ui/screen/SecureScreenPolicy.kt
apps/android/app/src/main/java/com/vaultia/app/core/ui/screen/SecureScreenGuard.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/ui/screen/SecureScreenPolicyTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/ui/screen/SecureScreenGuardTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/ui/screen/SecureScreenArchitectureTest.kt
```

---

## Implementação Entregue

### SecureScreenMode

- Enum de modos de proteção visual criado.
- Modos criados: `PUBLIC`, `REDACTED`, `SENSITIVE` e `LOCKED`.
- Cada modo define se exige secure flag.
- Cada modo define se pode renderizar conteúdo sensível.
- Cada modo define se redige conteúdo.
- Apenas `SENSITIVE` permite conteúdo sensível e exige secure flag.

### SecureScreenEvent

- Enum de eventos de tela criado.
- Inclui foreground, background, pause, stop, abertura/fechamento de tela sensível, reveal, redaction e lock.

### SecureScreenDecision

- Modelo de decisão visual criado.
- Impede exigir e limpar secure flag ao mesmo tempo.
- Impede permitir reveal quando o modo de destino não pode renderizar conteúdo sensível.

### SecureScreenPolicy

- Política de mapeamento entre `SecureUiState` e `SecureScreenMode` criada.
- `UNLOCKED_REVEALED` mapeia para `SENSITIVE`.
- `LOCKED` e `LOCKING` mapeiam para `LOCKED`.
- Estados de boot, autenticação, unlocked redacted e erro mapeiam para `REDACTED`.
- Recents screen não aceita modo `SENSITIVE`.

### SecureScreenGuard

- Guard puro de decisão criado.
- Estado revelado exige secure flag.
- Evento de secret revelado exige secure flag.
- Evento de secret redigido limpa secure flag e redige conteúdo.
- Background, pause e stop removem modo sensível.
- Background a partir de `REDACTED` inicia lock visual.
- Lock solicitado move para modo `LOCKED`.

---

## Segurança e Isolamento

- Nenhuma tela real foi criada.
- `MainActivity` não foi alterada.
- Nenhum Android `Window` foi usado diretamente.
- Nenhum `WindowManager.LayoutParams.FLAG_SECURE` foi chamado diretamente.
- Nenhum decrypt foi conectado à UI.
- Nenhum secret real foi renderizado.
- Nenhum storage real foi criado.
- Nenhuma leitura de disco foi criada pelo app.
- Nenhuma escrita em disco foi criada pelo app.
- Nenhum SQLite, Room, DataStore ou SharedPreferences foi criado.
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

- Modos de tela possuem capacidades compatíveis com os requisitos de segurança.
- Estados seguros de UI mapeiam para modos seguros de tela.
- Apenas modo `SENSITIVE` exige secure flag.
- Recents screen não recebe modo `SENSITIVE`.
- Decisão não pode exigir e limpar secure flag ao mesmo tempo.
- Decisão não pode permitir reveal em modo não sensível.
- Estado revelado exige secure flag e permite reveal.
- Estado locked limpa secure flag e redige conteúdo.
- Evento de secret revelado exige secure flag.
- Evento de secret redigido limpa secure flag e redige conteúdo.
- Background a partir de modo sensível redige conteúdo.
- Background a partir de modo redigido inicia lock visual.
- Lock solicitado move para modo locked.
- Pause e stop também redigem modo sensível.
- Arquitetura não usa Android APIs diretas.
- Arquitetura não usa storage.
- Arquitetura não usa crypto direta.
- Arquitetura não usa logs.
- Arquitetura não carrega termos sensíveis como senha, key, plaintext, ciphertext, nonce, authentication tag ou payload serializado.

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
- APK SHA-256: `c1ab7fa26de8603a18a28ac07674838bff60b3dea0ef3e8210a0f9e6a5a32e11`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 38.

---

## Commits

```text
064aca4 docs: add phase 38 plan for secure screen guard
ef33f39 feat: add secure screen guard contracts
```

## Próxima Fase Recomendada

Phase 39 — Authentication Friction and Critical Actions
