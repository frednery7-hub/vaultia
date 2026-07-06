# Android Foundation — Phase 39 Plan

## Nome

Authentication Friction and Critical Actions

## Objetivo

Criar contratos de segurança para reautenticação, ações críticas, timeout de sessão, delay progressivo e bloqueio temporário após falhas de autenticação.

A Phase 39 não implementa biometria real, não cria tela real, não cria storage, não conecta decrypt à UI e não persiste tentativas de login.

---

## Contexto

A Phase 37 criou contratos seguros de estado de UI.
A Phase 38 criou contratos puros de proteção de tela.

A Phase 39 cria a camada de decisão para impedir que um unlock simples libere ações críticas sem nova fricção de segurança.

---

## Arquivos Planejados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/security/auth/AuthenticationFrictionPolicy.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/auth/CriticalAction.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/auth/CriticalActionDecision.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/auth/AuthenticationAttemptState.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/auth/AuthenticationDelayPolicy.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/auth/SessionTimeoutPolicy.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/security/auth/AuthenticationFrictionPolicyTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/security/auth/AuthenticationDelayPolicyTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/security/auth/SessionTimeoutPolicyTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/security/auth/AuthenticationFrictionArchitectureTest.kt
```

---

## Contratos Planejados

### CriticalAction

Ações críticas planejadas:

- reveal secret;
- copy secret;
- edit item;
- delete item;
- export backup;
- change master password;
- disable biometric convenience;
- unlock vault.

### CriticalActionDecision

Decisões planejadas:

- permitido sem reautenticação;
- exige reautenticação;
- bloqueado por timeout;
- bloqueado por falhas repetidas;
- bloqueado por sessão travada.

### AuthenticationDelayPolicy

Delay progressivo planejado:

```text
0 falhas -> 0s
1 falha  -> 0s
2 falhas -> 30s
3 falhas -> 2min
4 falhas -> 10min
5+ falhas -> bloqueio temporário forte
```

### SessionTimeoutPolicy

Timeouts planejados:

- timeout padrão de sessão curta;
- timeout de reveal separado;
- sessão expirada exige reautenticação;
- ações críticas podem exigir reautenticação mesmo com sessão desbloqueada.

---

## Requisitos de Segurança

- Não criar tela real.
- Não alterar `MainActivity`.
- Não implementar biometria real.
- Não implementar Android Keystore.
- Não persistir falhas de autenticação nesta fase.
- Não criar storage real.
- Não ler disco.
- Não escrever em disco.
- Não criar SQLite, Room, DataStore ou SharedPreferences.
- Não conectar decrypt à UI.
- Não renderizar secrets reais.
- Não adicionar permissão Android.
- Não adicionar `android.permission.INTERNET`.
- Não criar backup/export real.
- Não implementar duress password.
- Não implementar plausible deniability.
- Não alterar roadmap formal nesta fase.

---

## Testes Planejados

- Validar que reveal exige reautenticação quando política exigir.
- Validar que copy secret exige reautenticação.
- Validar que export backup exige reautenticação.
- Validar que trocar senha-mestra exige reautenticação.
- Validar delay progressivo por quantidade de falhas.
- Validar bloqueio temporário após muitas falhas.
- Validar timeout de sessão.
- Validar arquitetura sem Android API direta, storage, logs, crypto direta ou persistência.
- Rodar `./gradlew testDebugUnitTest`.
- Rodar `./scripts/check-android.sh`.
- Confirmar APK sem `android.permission.INTERNET`.

---

## Critérios de Aceite

- Plan criado.
- Contratos de ações críticas criados.
- Política de reautenticação criada.
- Política de delay progressivo criada.
- Política de timeout criada.
- Testes unitários criados.
- Nenhuma tela real criada.
- Nenhuma biometria real criada.
- Nenhum storage criado.
- Nenhum secret renderizado.
- Nenhum decrypt conectado à UI.
- Nenhuma permissão Android adicionada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
