# Android Foundation — Phase 40 Plan

## Nome

Local Threat Detection Contract

## Objetivo

Criar contratos puros para classificação de risco local do ambiente onde o Vaultia roda, cobrindo sinais de root, debugger, emulador, hooking, tampering, app debuggable, overlay/accessibility risk e integridade local.

A Phase 40 fecha a fundação com modelo de threat detection, scoring e modo degradado, sem implementar detecção nativa agressiva real e sem adicionar internet, backend, storage, Keystore ou biometria.

---

## Contexto

A Phase 37 definiu estados seguros de UI.
A Phase 38 definiu proteção visual de tela.
A Phase 39 definiu fricção de autenticação e ações críticas.

A Phase 40 define como o app deve classificar ambiente hostil e decidir modo degradado, sem ainda acoplar APIs Android reais ou heurísticas invasivas.

---

## Arquivos Planejados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/security/threat/LocalThreatSignal.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/threat/LocalThreatSeverity.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/threat/LocalThreatFinding.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/threat/LocalThreatAssessment.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/threat/LocalThreatPolicy.kt
apps/android/app/src/main/java/com/vaultia/app/core/security/threat/DegradedModeDecision.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/security/threat/LocalThreatPolicyTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/security/threat/LocalThreatAssessmentTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/security/threat/LocalThreatArchitectureTest.kt
```

---

## Contratos Planejados

### LocalThreatSignal

Sinais planejados:

- root detected;
- debugger attached;
- emulator environment;
- hooking framework indicator;
- app debuggable;
- signature mismatch;
- overlay risk;
- accessibility risk;
- unknown installer;
- integrity check failed.

### LocalThreatSeverity

Severidades planejadas:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

### LocalThreatAssessment

Avaliação planejada:

- lista de findings;
- severidade máxima;
- risk score;
- indicador de modo degradado;
- indicador de bloqueio de ações críticas.

### DegradedModeDecision

Decisões planejadas:

- permitir operação normal;
- exigir reautenticação;
- bloquear reveal;
- bloquear exportação;
- manter UI redigida;
- exibir aviso forte;
- bloquear ações críticas.

### LocalThreatPolicy

Política mínima:

- sinais críticos sempre ativam modo degradado;
- sinais high ativam modo degradado;
- múltiplos sinais medium elevam risco;
- modo degradado exige reautenticação;
- modo degradado bloqueia exportação;
- modo degradado mantém reveal bloqueado ou altamente restrito;
- ausência de sinais mantém operação normal.

---

## Requisitos de Segurança

- Não criar detecção Android real nesta fase.
- Não usar APIs Android diretas nesta fase.
- Não alterar `MainActivity`.
- Não criar tela real.
- Não adicionar internet.
- Não adicionar backend.
- Não adicionar Play Integrity nesta fase.
- Não criar storage real.
- Não ler disco.
- Não escrever em disco.
- Não criar SQLite, Room, DataStore ou SharedPreferences.
- Não implementar Android Keystore.
- Não implementar biometria.
- Não conectar decrypt à UI.
- Não renderizar secrets reais.
- Não criar backup/export real.
- Não implementar duress password.
- Não implementar plausible deniability.
- Não alterar roadmap formal nesta fase.

---

## Testes Planejados

- Validar severidade máxima.
- Validar risk score.
- Validar assessment sem findings como operação normal.
- Validar sinal crítico ativa modo degradado.
- Validar root/debugger/hooking como alto risco.
- Validar múltiplos sinais medium elevando risco.
- Validar modo degradado exige reautenticação.
- Validar modo degradado bloqueia exportação.
- Validar modo degradado mantém UI redigida.
- Validar arquitetura sem Android API direta, storage, logs, crypto direta ou persistência.
- Rodar `./gradlew testDebugUnitTest`.
- Rodar `./scripts/check-android.sh`.
- Confirmar APK sem `android.permission.INTERNET`.

---

## Critérios de Aceite

- Plan criado.
- Contratos de threat detection criados.
- Política de risk scoring criada.
- Decisão de modo degradado criada.
- Testes unitários criados.
- Nenhuma detecção Android real criada.
- Nenhuma tela real criada.
- Nenhum storage criado.
- Nenhum secret renderizado.
- Nenhum decrypt conectado à UI.
- Nenhuma permissão Android adicionada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
