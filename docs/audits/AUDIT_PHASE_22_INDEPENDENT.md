# Independent Security Audit — Phase 22 Foundation

## Metadados da auditoria

```text
Data:         2026-06-29
Branch:       main
Commit:       f8f8a3309c1987eb94ea15721e3f008bb5d81d6b
Short:        f8f8a33
Commit date:  2026-06-28 17:34:43 -0400
Subject:      docs: register exact test count for phase 21 done
Worktree:     dirty (registrado abaixo)
```

Estado do worktree no momento da auditoria:

```text
 M apps/android/app/build.gradle.kts
?? apps/android/gradle.properties
```

Ambas as mudanças não-commitadas pertencem à Phase 22 em curso (adição da
dependência `argon2kt:1.6.0` para avaliação isolada e criação do
`gradle.properties` com `android.useAndroidX=true`). A auditoria reconhece
explicitamente que não foi rodada sobre um checkout limpo, e considerou as
mudanças não-commitadas no escopo.

## Escopo e método

Esta auditoria é independente da execução da Phase 22 e foi feita em
paralelo a ela. Avalia o estado de segurança do projeto na fase
"Foundation" (50% concluído), antes de qualquer implementação real de
criptografia, KDF, Keystore ou autenticação.

**Limitação honesta de método**: vários itens da checklist clássica de
auditoria de cofre não são reprovados por implementação fraca, e sim por
ainda não existirem. O documento distingue ausência por design de fase
(estado declarado, esperado) de ausência crítica (estado não planejado
que deveria existir).

**Severidades**: classificadas considerando o contexto Phase 22 /
Foundation. **Reavaliação obrigatória antes de qualquer release que
persista dados reais de usuário.** Em release candidate de produção,
itens INFO ligados a criptografia/KDF/auth simulada subiriam para HIGH
ou CRITICAL.

## Ferramentas utilizadas

```text
semgrep scan --config p/security-audit --config p/secrets --config p/kotlin
gitleaks detect
./gradlew testDebugUnitTest
./scripts/audit-50.sh .
OSV API (verificação de vulnerabilidades de dependências)
./gradlew assembleRelease (com observação operacional, ver AUDIT-...-06)
```

### Resultados das ferramentas

```text
semgrep:                0 findings
gitleaks:               0 leaks em 28 commits
testDebugUnitTest:      passou
audit-50.sh:            16 PASS, 1 WARN (worktree dirty, esperado)
OSV (dependências):     0 vulnerabilidades retornadas
assembleRelease:        falhou no volume externo por arquivos AppleDouble
                        ._*; passou em cópia temporária para /tmp (ver
                        AUDIT-...-06)
```

## Tabela de achados

| ID | Achado | Severidade |
|----|--------|------------|
| AUDIT-2026-06-29-01 | Cripto não implementada | INFO |
| AUDIT-2026-06-29-02 | Argon2 empacotado no APK sem integração real | LOW |
| AUDIT-2026-06-29-03 | Auth/bloqueio simulado | INFO |
| AUDIT-2026-06-29-04 | `FLAG_SECURE` ausente | MEDIUM |
| AUDIT-2026-06-29-05 | Release com `isMinifyEnabled=false` / `isShrinkResources=false` | MEDIUM |
| AUDIT-2026-06-29-06 | `._*` quebra `assembleRelease` no volume externo | LOW |
| AUDIT-2026-06-29-07 | Sem rede / `INTERNET` ausente / cert pinning não aplicável nesta fase | INFO |
| AUDIT-2026-06-29-08 | `MainActivity` exportada por necessidade técnica de launcher | INFO |

Distribuição: 4 INFO, 2 MEDIUM, 2 LOW, 0 HIGH, 0 CRITICAL.

## Achados detalhados

### AUDIT-2026-06-29-01 — Cripto não implementada (INFO)

`CryptoService` é contrato vazio (reservado em fase anterior), e
`MasterPasswordPolicy` declara explicitamente que não faz hash, KDF nem
toca em Keystore.

**Fontes**:
- `apps/android/app/src/main/java/com/vaultia/app/core/security/contract/CryptoService.kt`, linha 3
- `apps/android/app/src/main/java/com/vaultia/app/core/security/password/MasterPasswordPolicy.kt`, linha 7
- `docs/SDD/05-seguranca/04-crypto-architecture.md`, linha 109 (arquitetura documentada)

**Severidade INFO**: estado declarado por design das Fases 1-20.
Documentado nos PLANs e DONEs. Implementação real planejada para Fases
23-26.

### AUDIT-2026-06-29-02 — Argon2 empacotado no APK sem integração real (LOW)

A dependência `com.lambdapioneer.argon2kt:argon2kt:1.6.0` foi adicionada
ao `build.gradle.kts` apenas para avaliação (medição de impacto no APK e
benchmark isolado), com comentário explícito de escopo. O APK debug
inclui binários nativos Argon2 (`libargon2jni.so`, `libargon2native.so`)
em todas as ABIs suportadas. Isso aumenta a superfície de supply chain
e a dependência transitiva (incluindo `androidx.appcompat:1.7.0` e toda
a cadeia Jetpack relacionada) antes do uso real.

**Fontes**:
- `apps/android/app/build.gradle.kts`, linha 53

**Severidade LOW**: risco residual real e aceito durante a fase. A
classificação é LOW (não INFO) porque há ação esperada quando a Fase 22
fechar — aprovar a dependência para uso na Fase 23, ou descartá-la e
acionar fallback PBKDF2. INFO subentende "registrado para conhecimento,
sem ação"; LOW captura melhor o caráter de "aceito como risco com
decisão pendente".

### AUDIT-2026-06-29-03 — Auth/bloqueio simulado (INFO)

`InMemorySessionManager.unlockForCurrentProcessOnly()` só troca estado
em memória, sem senha, sem KDF, sem biometria, sem timeout real. O
`AuthBoundaryNoticeView` na UI deixa o caráter simulado explícito para
o usuário em tempo de execução.

**Fontes**:
- `apps/android/app/src/main/java/com/vaultia/app/core/session/InMemorySessionManager.kt`, linha 5

**Severidade INFO**: estado declarado por design, com UI honesta sobre o
que é. Implementação real planejada para Fases 28-29.

### AUDIT-2026-06-29-04 — `FLAG_SECURE` ausente (MEDIUM)

`MainActivity` não aplica `WindowManager.LayoutParams.FLAG_SECURE`.
Consequência: screenshots manuais e preview de multitarefa não estão
bloqueados. Para um cofre de senhas, isso permite captura visual do
conteúdo desbloqueado por screenshots e thumbnails do recents screen.

**Fontes**:
- `apps/android/app/src/main/java/com/vaultia/app/MainActivity.kt`, linha 11

**Severidade MEDIUM**: achado novo, não está coberto por nenhuma fase do
roadmap atual. Deve virar fase própria de hardening de UI antes de
qualquer release com dados reais. Não é HIGH porque o app ainda não
exibe dados sensíveis reais (vault está em metadata-only).

### AUDIT-2026-06-29-05 — Release com `isMinifyEnabled=false` / `isShrinkResources=false` (MEDIUM)

Tipo de build `release` está com:

```text
isMinifyEnabled = false
isShrinkResources = false
```

Para um app de cofre, ofuscação/minificação são esperadas no release
final. Há complicação técnica conhecida: ProGuard/R8 com biblioteca
nativa (`argon2kt`) exige regras de `keep` cuidadosas para não quebrar
o binding JNI; isso não é trivial e merece fase própria.

**Fontes**:
- `apps/android/app/build.gradle.kts`, linha 32

**Severidade MEDIUM**: para release final é bloqueador, mas o projeto
está pré-release. Não é HIGH porque não há release público planejado
neste momento. Vira HIGH no momento em que release candidato real for
considerado.

### AUDIT-2026-06-29-06 — `._*` quebra `assembleRelease` no volume externo (LOW)

`./gradlew assembleRelease` falhou quando rodado diretamente do
pendrive (`/Volumes/KINGSTON/vaultia`) por causa de arquivos AppleDouble
`._*` que o macOS cria automaticamente em volumes formatados como
HFS+/exFAT. O mesmo comando passou quando rodado em cópia temporária
do projeto em `/tmp`.

**Severidade LOW**: não é vulnerabilidade do app — é risco de
reprodutibilidade/build no ambiente auditado. Tem workaround conhecido
(rodar build em volume nativo). Vale planejar resolução estável (script
de limpeza de `._*` em pré-build, ou mudança de fluxo de build) antes
de qualquer release.

### AUDIT-2026-06-29-07 — Sem rede / `INTERNET` ausente / cert pinning não aplicável nesta fase (INFO — positivo por ausência)

Verificações cruzadas confirmam:

- Manifest-fonte não declara `android.permission.INTERNET`.
- Manifest mesclado do APK debug (verificado via `aapt dump permissions`
  em ambos os APKs: baseline e com argon2kt) também não declara INTERNET.
- Nenhuma biblioteca de rede no código (sem Retrofit, OkHttp, WebView,
  HttpURLConnection em uso).
- Nenhuma API de sync, telemetria ou logging remoto.

Consequência: não há camada de rede para fazer MitM, e cert pinning não
é aplicável nesta fase porque não há certificado a ser pinado. Esta é
uma garantia positiva da arquitetura local-only do Vaultia.

**Severidade INFO (positiva)**: registrar para conhecimento e como
invariante a ser preservado. Qualquer fase futura que introduza camada
de rede deve revisitar este item.

### AUDIT-2026-06-29-08 — `MainActivity` exportada por necessidade técnica de launcher (INFO)

`MainActivity` está declarada com `android:exported="true"`. Isso é
**necessário**: é a Activity de launch com `<intent-filter>` para
`MAIN` + `LAUNCHER`, e sem `exported=true` o launcher do sistema
operacional não consegue iniciá-la.

Verificação cruzada: não há outras Activities, Services ou
BroadcastReceivers exportados no manifest.

**Severidade INFO**: registrado por completude, não por risco. Cofre
real eventualmente vai querer revisar se o ponto de entrada via
launcher deve exibir tela bloqueada ou pré-bloqueio, mas isso é decisão
de UX/segurança, não de configuração de manifest.

## Pontos positivos confirmados

Verificados diretamente nos arquivos do repo durante a auditoria:

- `android:allowBackup="false"` em `AndroidManifest.xml`.
- `android:fullBackupContent="false"` em `AndroidManifest.xml`.
- `android:dataExtractionRules="@xml/data_extraction_rules"` apontando
  para regras que excluem `path="."` em `root` tanto de `cloud-backup`
  quanto de `device-transfer` — efetivamente bloqueando Auto Backup e
  device transfer.
- Sem permissões perigosas declaradas. Única permissão presente nos APKs
  compilados é a técnica interna do AndroidX
  (`com.vaultia.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`,
  signature-level, bound ao próprio applicationId — emula
  `RECEIVER_NOT_EXPORTED` em APIs antigas).
- Sem storage real fora dos contratos vazios.
- Sem secrets encontrados no código ou no histórico Git (28 commits
  varridos pelo gitleaks).

## Conclusão

No estado atual (Phase 22/Foundation), o Vaultia passa bem como **base
controlada**: sem rede, sem backup Android, sem secrets, sem
permissões sensíveis. Os itens críticos de **cofre real ainda ficam
como não implementados** — criptografia, KDF real, Keystore/biometria,
limpeza de memória, auto-lock, anti-screenshot (`FLAG_SECURE`),
detecção de root/jailbreak, autofill seguro, cert pinning (quando
fizer sentido), ofuscação release.

**O app não pode ser declarado como cofre seguro em produção hoje.**
Esta auditoria considera o projeto adequado para sua fase atual
(Foundation), com pendências mapeadas e classificadas para tratamento
nas próximas fases via backlog em `docs/audits/BACKLOG_FROM_AUDITS.md`.

## Próximos passos rastreáveis

Os 8 achados deste documento entram para `BACKLOG_FROM_AUDITS.md` com
seus IDs estáveis. Mapeamento esperado:

- 3 achados (01, 03, 07) já estão cobertos por fases planejadas do
  roadmap original — serão referenciados nos DONEs dessas fases.
- 1 achado (02) será resolvido no fechamento da própria Phase 22.
- 1 achado (08) é INFO de completude, não exige fase própria.
- 2 achados novos (04, 05) merecem fases próprias de hardening, a serem
  numeradas no bloco 75% → 100% do roadmap.
- 1 achado (06) é operacional, vira issue de processo de build, não
  fase.
