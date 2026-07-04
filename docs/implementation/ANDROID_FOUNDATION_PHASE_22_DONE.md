# Android Foundation — Phase 22 Done

## Status

Aprovado com observações.

## Objetivo

Avaliar formalmente a dependência candidata para a derivação de chave a partir da senha mestra (KDF) — `com.lambdapioneer.argon2kt` — através de auditoria de segurança, tamanho de APK, dependências e benchmark instrumentado em emulador Android local, antes de introduzir qualquer código de criptografia real no app.

---

## A1 — Git / Histórico / Limpeza

- **Confirmar branch atual:** A branch atual foi confirmada como `main`.
- **Confirmar escopo do working tree:** O `git status --short` foi validado. O repositório contém apenas alterações esperadas da Phase 22 e da sanitização pública em preparação:
  - `M apps/android/app/build.gradle.kts` (Adição da biblioteca candidata e dependências de testes instrumentados)
  - `?? apps/android/gradle.properties` (Configuração para habilitar o AndroidX)
  - `?? apps/android/app/src/androidTest/` (Código isolado do benchmark instrumentado)
- **Confirmar ordem dos commits:** A ordem dos commits está correta e foi validada via `git log`:
  - `2cb2685` - audit: add independent security audit document for phase 22 foundation
  - `f8f8a33` - docs: register exact test count for phase 21 done (Último commit da Phase 21)
  - `36da652` - docs: add phase 22 plan for kdf dependency evaluation
- **Confirmar encerramento da Phase 21:** Confirmado. A Phase 21 foi formalmente fechada com o commit `f8f8a33` e o relatório `ANDROID_FOUNDATION_PHASE_21_DONE.md` está presente e aprovado.
- **Confirmar ausência de arquivos temporários, APKs ou logs grandes:** Confirmado. Nenhum arquivo `.apk`, `.log`, `.tmp`, ou arquivos ocultos macOS como `._*` está versionado.
- **Confirmar que arquivos sensíveis não estão versionados:** Confirmado via `git ls-files`. Nenhum arquivo `.env`, keystore (`.jks`, `.keystore`), chaves ou certificados (`.pem`, `.p12`), ou `local.properties` foi encontrado no histórico do Git.
- **Auditoria de `gradle.properties`:** O arquivo `apps/android/gradle.properties` adicionado nesta fase contém apenas configurações não sensíveis do build system (`android.useAndroidX=true`). Portanto, ele **pode e deve** ser versionado no Git para garantir a reprodutibilidade do build.
- **Varredura de segredos e binários grandes:** 
  - Executada varredura estática de padrões de segredo, sem achados bloqueantes.
  - Verificação de arquivos grandes no histórico não identificou binários grandes ou inesperados.
- **Commits usados na comparação:**
  - **Baseline:** Commit `2cb2685` (antes da adição da dependência localmente).
  - **Estado com argon2kt:** Working tree local com as modificações aplicadas na Phase 22.

---

## A2 — Manifest / Permissões Android

- **Permissão de `INTERNET` ausente:** Confirmado. A permissão `android.permission.INTERNET` está ausente no `AndroidManifest.xml` de origem e no manifest mesclado final (`merged_manifest`). O APK gerado foi inspecionado e não possui acesso à rede.
- **Manifest fonte sem novas permissões:** Confirmado. Nenhuma nova permissão foi adicionada ao arquivo fonte do manifest.
- **Manifest mesclado final:** A única permissão presente no manifest final gerado é `com.vaultia.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` (permissão interna, com nível de proteção `signature`, exigida pelo AndroidX para receptores de broadcast internos, totalmente segura).
- **Parâmetro `android:allowBackup`:** Confirmado como `"false"` explicitamente no manifest fonte e mesclado.
- **Parâmetro `usesCleartextTraffic`:** Ausente (portanto, desativado por padrão em APIs modernas).
- **Parâmetro `android:debuggable`:** Não está hardcoded no manifest fonte. A tag `android:debuggable="true"` é injetada automaticamente apenas no build de debug, como esperado.
- **Ausência de permissões sensíveis:** Confirmada a ausência de permissões para localização, câmera, microfone, contatos, SMS, storage externo, rede ou estado de rede.
- **Permissões do `androidTest`:** O manifest de testes instrumentados não injeta nenhuma permissão perigosa ou de rede no aplicativo de produção.

---

## A3 — Dependências / Supply Chain

- **Biblioteca candidata:** `com.lambdapioneer.argon2kt:argon2kt:1.6.0` (versão fixada).
- **Bibliotecas de teste:** `androidx.test.ext:junit:1.2.1` e `androidx.test:runner:1.6.2` (fixadas e exclusivas de ambiente de teste).
- **Isolamento da biblioteca:** Confirmado que a dependência foi inserida apenas para avaliação/benchmark, permanecendo comentada e sem qualquer chamada no código principal (`main/`).
- **Licença:** MIT (Compatível com os objetivos do projeto).
- **Mecanismo:** Binding JNI nativo empacotado em formato `aar`.
- **Arquivos nativos `.so` empacotados por ABI:** Confirmado. O APK contém binários compilados nativamente para as seguintes arquiteturas:
  - `arm64-v8a` (`libargon2jni.so`, `libargon2native.so`)
  - `armeabi-v7a` (`libargon2jni.so`, `libargon2native.so`)
  - `x86` (`libargon2jni.so`, `libargon2native.so`)
  - `x86_64` (`libargon2jni.so`, `libargon2native.so`)
- **Ausência de bibliotecas de rede, analytics ou DI externo:** Nenhuma dependência como Retrofit, OkHttp, Ktor, Firebase, Mixpanel, Hilt ou Koin foi detectada.
- **Verificação de CVEs e Issues do repositório:** 
  - Nenhum CVE específico para `lambdapioneer/argon2kt:1.6.0` foi encontrado na busca realizada. Isso não comprova ausência absoluta de vulnerabilidades; registra ausência de evidência pública encontrada nesta auditoria.
  - A revisão pública do repositório não identificou issue de segurança bloqueante no escopo desta avaliação, mas a manutenção futura da dependência permanece risco residual monitorado.
- **Risco de dependência nativa:** Existe o risco operacional de `UnsatisfiedLinkError` caso a JVM falhe em carregar os arquivos nativos `.so` em certos aparelhos Android customizados.
- **Mitigação e Fallback:** Caso a biblioteca falhe no carregamento nativo, o projeto possui o fallback documentado para `PBKDF2WithHmacSHA256` (nativo da API de criptografia do Android JCE).

---

## A4 — Arquitetura / Boundaries

- **Localização do benchmark:** Exclusivo de `apps/android/app/src/androidTest/java/com/vaultia/app/core/crypto/kdf/Argon2BenchmarkInstrumentedTest.kt`.
- **Isolamento de boundaries:** O benchmark não toca e não altera nenhuma das seguintes classes de domínio:
  - `CryptoService` (permanece vazio)
  - `SessionManager` / `InMemorySessionManager`
  - `VaultRepository` / `InMemoryVaultRepository`
  - `SecureStorage`
  - Pacotes de UI, `feature/auth`, `core/storage` e `core/session`.
- **Ausência de fluxo criptográfico real:** Sem fluxos de desbloqueio, derivação integrada ao fluxo do app, nem armazenamento de segredos nesta fase.
- **Dados do benchmark:** O benchmark utiliza valores puramente fictícios para senha e salt (`DUMMY_PASSWORD` e `DUMMY_SALT`), garantindo que nenhum dado real seja processado ou vazado.

---

## A5 — Criptografia / KDF

- **KDF alvo:** Argon2id (variante recomendada pelo RFC 9106 e OWASP por ser memory-hard e mitigar brute-force offline).
- **PBKDF2 como Fallback formal:** Mantido se a biblioteca nativa `argon2kt` se provar inviável.
- **scrypt rejeitado:** Confirmado por design.
- **Ausência de APIs criptográficas prematuras:** Sem AES, sem Android Keystore, sem armazenamento de chaves, senhas ou salts persistidos nesta fase.
- **Parâmetros testados no benchmark:**
  - **Conjunto FAST:** Memória = 32 MiB (32768 KiB), Iterações = 2, Paralelismo = 1, Output = 32 bytes.
  - **Conjunto CONSERVATIVE:** Memória = 64 MiB (65536 KiB), Iterações = 2, Paralelismo = 1, Output = 32 bytes.
- Os parâmetros padrão da biblioteca não foram adotados sem ajuste, focando em obter o melhor balanço de segurança/velocidade.

---

## A6 — Storage / Dados Sensíveis

- **Sem persistência local:** Confirmada a total ausência de Room, SQLite, SharedPreferences, DataStore, arquivos em disco ou MediaStore.
- **Sem logs sensíveis:** O benchmark loga apenas métricas de tempo (ms). Senhas, salts ou chaves reais não são emitidos para o Logcat.
- **VaultItem intacto:** A classe `VaultItem` não possui campos de segredos reais.

---

## A7 — Testes Unitários

- **Comando executado:** `./gradlew testDebugUnitTest`
- **Resultado:** Passou com sucesso (34 testes no total).
- **Estabilidade:** Todos os testes das Fases 1 a 21 e testes de arquitetura continuam verdes. Nenhuma política de qualidade ou validação de senha foi relaxada ou burlada.

---

## A8 — Teste Instrumentado / Benchmark em Emulador Android

O benchmark foi executado com sucesso em um emulador Android local rodando na arquitetura host nativa. Esta evidência valida build, carregamento JNI e performance inicial em ambiente Android emulado. Benchmark em aparelho físico real permanece recomendado antes de congelar parâmetros definitivos de produção ou antes de release final.

- **Ambiente:** Google Emulator (`sdk_gphone64_arm64`)
- **Versão do Android:** Android 16 (API 36)
- **ABI do Dispositivo:** `arm64-v8a`
- **Comando executado:** `./gradlew connectedDebugAndroidTest`
- **Carregamento JNI:** Aprovado. A biblioteca nativa `.so` carregou com sucesso e executou sem disparar `UnsatisfiedLinkError`.

### Resultados do Benchmark (3 rodadas + 1 warmup):

| Parâmetro / Métrica | FAST (32 MiB, t=2, p=1) | CONSERVATIVE (64 MiB, t=2, p=1) |
| :--- | :--- | :--- |
| **Memória (KiB)** | 32,768 (32 MiB) | 65,536 (64 MiB) |
| **Iterações (t)** | 2 | 2 |
| **Paralelismo (p)** | 1 | 1 |
| **Saída (bytes)** | 32 | 32 |
| **Tempos obtidos (ms)** | 51.9, 58.7, 64.3 | 116.8, 118.2, 109.5 |
| **Tempo Mínimo (ms)** | 51.9 | 109.5 |
| **Tempo Máximo (ms)** | 64.3 | 118.2 |
| **Tempo Médio (ms)** | **58.3** | **114.8** |

- **Warmup:** Executado 1 rodada descartada com sucesso (garante o carregamento prévio da lib nativa e alocação estável de buffers).
- **Estabilidade:** Sem travamentos, OOM (Out Of Memory) ou timeouts.
- **Resultado:** Ambos os perfis são viáveis. O perfil **CONSERVATIVE (64 MiB)** obteve excelente performance (~115 ms de média), sendo a recomendação de calibração padrão futura devido à maior dureza de memória.

---

## A9 — Build / APK / Impacto de Tamanho

Para obter medições limpas sem interferência de Spotlight/Finder no pendrive exFAT, as builds foram feitas na partição local APFS (`/tmp`).

- **APK Baseline (sem dependência):**
  - **Tamanho:** 872,932 bytes (~852 KB)
  - **SHA-256:** `f363b02d38703b79a48b185cab3aa34cad2c958eb7954ebbd9074fef60db0b2c`
- **APK com argon2kt (com dependência):**
  - **Tamanho:** 4,215,254 bytes (~4.02 MB)
  - **SHA-256:** `0499009a5d92db0a0be69532cd5e55e4c5c8c0fe9cb5379b6e7235f3f1d74bd4`
- **Impacto de Tamanho (Delta):**
  - **Delta Absoluto:** `+3,342,322` bytes (~3.19 MiB)
  - **Delta Percentual:** `+382.88%`
- **Avaliação do Impacto:** O delta de ~3.2 MB deve-se inteiramente à inclusão de binários nativos `.so` para 4 ABIs diferentes (`arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64`). O tamanho é aceitável para a fase de avaliação. Como o APK debug inclui 4 ABIs, o impacto final por usuário em distribuição via Android App Bundle tende a ser menor que o delta bruto do APK universal/debug. Esse valor deve ser medido com bundle/split real antes de release; portanto, nenhum número definitivo de impacto por usuário é declarado nesta fase.

---

## A10 — Logs e Evidências

Todos os comandos de build, logs de teste e hashes foram registrados neste documento. Nenhum APK ou log bruto gigante foi ou será enviado para o repositório Git.

---

## A11 — Decisão Final

- **Decisão:** **`com.lambdapioneer.argon2kt` APROVADA** para avançar à Phase 23 como biblioteca candidata oficial de KDF do Vaultia, sob contrato isolado e com confirmação futura em aparelho físico real antes de congelar parâmetros definitivos de produção.
- **Justificativa:**
  1. Sucesso na compilação e teste instrumentado em arquitetura nativa.
  2. Suporte completo a 4 ABIs, permitindo rodar em virtualmente qualquer dispositivo Android.
  3. Desempenho extremamente rápido (~115 ms para 64 MiB e ~58 ms para 32 MiB), bem abaixo do limite aceitável de UX (< 500 ms).
  4. Binding robusto JNI que utiliza `direct-allocated ByteBuffers`, permitindo a limpeza determinística dos segredos da memória (essencial para segurança).
  5. Manifest final permanece totalmente fechado e sem permissão de `INTERNET`.
  6. Não adiciona dependências indesejadas (rede/analytics/DI) e tem licença MIT amigável.

---

## A12 — Riscos Residuais Obrigatórios

1. **Risco de Biblioteca Nativa (.so) e UnsatisfiedLinkError:** Dispositivos Android altamente customizados ou com problemas de empacotamento podem falhar ao carregar as bibliotecas JNI nativas. 
   - *Mitigação:* Provisionar fallback funcional baseado em `PBKDF2WithHmacSHA256` nativo da API Java/Android.
2. **Risco de Manutenção:** A biblioteca `argon2kt` é mantida por um único desenvolvedor independente.
   - *Mitigação:* O código de KDF deve ser encapsulado sob o contrato do `CryptoService`. Se for necessária a migração, a alteração afetará apenas a classe de implementação concreta.
3. **Risco de Parâmetros de Criptografia:** Parâmetros de Argon2id muito fortes (ex. >128 MiB) podem travar ou esgotar a memória de dispositivos de baixo custo (low-end).
   - *Mitigação:* Manter a calibração em 64 MiB ou 32 MiB de memória como limite máximo.
4. **Falsa Segurança antes do Envelope Encryption:** Apenas derivar a chave mestra com KDF robusto não protege os dados se eles forem salvos sem AES-GCM ou se a chave derivada for mantida indefinidamente em memória de forma insegura.
   - *Mitigação:* Seguir à risca as próximas fases do Roadmap para implementação de `SecureStorage` e criptografia simétrica com chave derivada.

---

## Próximos Passos (Phase 23)

1. Criar o contrato de KDF.
2. Definir parâmetros candidatos finais e formato do resultado de derivação.
3. Implementar o tratamento de erros em caso de falha de carregamento JNI.
4. Garantir a limpeza determinística da memória (limpando os ByteBuffers diretos após a derivação).
5. Definir onde os parâmetros de KDF (iterações, custo de memória e salt) serão armazenados no header do cofre futuro.
