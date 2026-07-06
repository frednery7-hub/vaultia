# Android Foundation — Phase 39 Done

## Status

Aprovado.

## Nome

Authentication Friction and Critical Actions

## Objetivo

Criar contratos de segurança para reautenticação, ações críticas, timeout de sessão, delay progressivo e bloqueio temporário após falhas de autenticação.

A Phase 39 criou contratos puros e testáveis. Não implementou biometria real, não criou tela real, não criou storage, não conectou decrypt à UI e não persistiu tentativas de login.

---

## Arquivos Criados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/security/auth/CriticalAction.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/auth/CriticalActionDecision.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/auth/AuthenticationAttemptState.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/auth/AuthenticationDelayPolicy.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/auth/SessionTimeoutPolicy.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/auth/AuthenticationFrictionPolicy.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/security/auth/AuthenticationDelayPolicyTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/security/auth/SessionTimeoutPolicyTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/security/auth/AuthenticationFrictionPolicyTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/security/auth/AuthenticationFrictionArchitectureTest.kt
```

---

## Implementação Entregue

### CriticalAction

- Enum de ações críticas criado.
- Ações criadas: `REVEAL_SECRET`, `COPY_SECRET`, `EDIT_ITEM`, `DELETE_ITEM`, `EXPORT_BACKUP`, `CHANGE_MASTER_PASSWORD`, `DISABLE_BIOMETRIC_CONVENIENCE` e `UNLOCK_VAULT`.
- Todas as ações críticas exigem autenticação recente.

### CriticalActionDecision

- Modelo de decisão criado.
- Decisões suportam permitido, exige reautenticação e bloqueado.
- Razões de bloqueio: `SESSION_LOCKED`, `SESSION_TIMEOUT` e `TOO_MANY_FAILURES`.
- Estados contraditórios são rejeitados.

### AuthenticationAttemptState

- Estado transitório de tentativas criado.
- Registra quantidade de falhas e timestamp da última falha.
- Não persiste dados em disco.

### AuthenticationDelayPolicy

- Política de delay progressivo criada.
- 0 falhas: 0 segundos.
- 1 falha: 0 segundos.
- 2 falhas: 30 segundos.
- 3 falhas: 2 minutos.
- 4 falhas: 10 minutos.
- 5 ou mais falhas: bloqueio temporário forte de 30 minutos.
- Rejeita entradas inválidas.

### SessionTimeoutPolicy

- Política de timeout de sessão criada.
- Timeout padrão de sessão: 5 minutos.
- Janela padrão de autenticação recente para ação crítica: 30 segundos.
- A janela de ação crítica não pode ser maior que o timeout da sessão.
- Rejeita timestamps inválidos.

### AuthenticationFrictionPolicy

- Política central de decisão criada.
- Unlock exige reautenticação.
- Ações críticas bloqueiam quando a sessão está travada.
- Ações críticas bloqueiam quando a sessão expirou.
- Ações críticas exigem reautenticação quando a autenticação recente expirou.
- Falhas repetidas bloqueiam ações.
- Delay progressivo bloqueia ações antes do tempo necessário.

---

## Segurança e Isolamento

- Nenhuma tela real foi criada.
- `MainActivity` não foi alterada.
- Nenhuma biometria real foi criada.
- Nenhum Android Keystore foi criado.
- Nenhuma falha de autenticação foi persistida.
- Nenhum storage real foi criado.
- Nenhuma leitura de disco foi criada pelo app.
- Nenhuma escrita em disco foi criada pelo app.
- Nenhum SQLite, Room, DataStore ou SharedPreferences foi criado.
- Nenhum decrypt foi conectado à UI.
- Nenhum secret real foi renderizado.
- Nenhuma permissão Android foi adicionada.
- Nenhum `android.permission.INTERNET` foi adicionado.
- Nenhum backup/export real foi criado.
- Nenhum duress password foi criado.
- Nenhuma plausible deniability foi criada.
- Roadmap formal não foi alterado nesta fase.

---

## Testes Entregues

- Delay progressivo segue a política definida.
- Bloqueio temporário começa a partir de 5 falhas.
- Delay usa timestamp da última falha.
- Configurações inválidas são rejeitadas.
- Sessão expira após timeout configurado.
- Janela de ação crítica usa timeout mais curto.
- Unlock exige reautenticação.
- Reveal secret exige autenticação recente quando a janela expirou.
- Copy secret exige autenticação recente quando a janela expirou.
- Export backup exige autenticação recente quando a janela expirou.
- Trocar senha-mestra exige autenticação recente quando a janela expirou.
- Ação é permitida quando sessão e autenticação crítica estão válidas.
- Sessão travada bloqueia ações críticas.
- Sessão expirada bloqueia ações críticas.
- Muitas falhas bloqueiam ações.
- Delay progressivo bloqueia antes do tempo necessário.
- Estados contraditórios de decisão são rejeitados.
- Arquitetura não usa Android APIs diretas.
- Arquitetura não usa storage.
- Arquitetura não usa crypto direta.
- Arquitetura não usa logs.
- Arquitetura não carrega termos sensíveis como senha valor, key, plaintext, ciphertext, nonce, authentication tag ou payload serializado.

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
- APK SHA-256: `fb5695b8f327bdbd7a534ff4a59fadd96defaed2172bdebcaaeaf569d7634a42`.

Observação: o build informou que `libargon2jni.so` e `libargon2native.so` não foram stripadas e foram empacotadas como estão. Isso é compatível com a dependência nativa avaliada na Phase 22 e não é bloqueador da Phase 39.

---

## Commits

```text
01defbd docs: add phase 39 plan for authentication friction
4b868d3 feat: add authentication friction contracts
```

## Próxima Fase Recomendada

Phase 40 — Local Threat Detection Contract
