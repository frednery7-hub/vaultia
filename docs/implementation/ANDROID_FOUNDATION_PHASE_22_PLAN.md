# Android Foundation — Phase 22 Plan

## Nome

KDF Dependency Evaluation

## Objetivo

Avaliar formalmente a dependência candidata para derivação de chave a partir
da senha mestra (KDF), antes de qualquer linha de código de criptografia
entrar no projeto. Esta fase é puramente de avaliação e decisão documentada;
não implementa derivação real.

## Decisão de KDF (já fechada antes desta fase)

```text
KDF alvo:        Argon2id
Biblioteca:       lambdapioneer/argon2kt (candidata principal)
Fallback técnico: PBKDF2WithHmacSHA256 (nativo do JCE Android),
                  apenas se Argon2id for inviável por supply chain,
                  manutenção, performance ou compatibilidade.
scrypt:           rejeitado para este projeto.
```

Justificativa: Argon2id é memory-hard, é a recomendação atual da OWASP para
derivação/hash de senha, e é a variante recomendada pelo RFC 9106. Para um
cofre 100% local sem MFA e sem recuperação, o cenário crítico é brute force
offline — exatamente o que Argon2id mitiga melhor que PBKDF2 (não
memory-hard) e melhor se posiciona que scrypt na recomendação atual.

## Pesquisa preliminar sobre `lambdapioneer/argon2kt` (base para a candidatura)

Os pontos abaixo já foram levantados como pesquisa preliminar, com fonte, e
servem de base para `argon2kt` ser a candidata principal desta fase. Eles
**não substituem** o trabalho formal de avaliação que a Phase 22 ainda
precisa produzir (revisão de issues abertas, benchmark físico, medição de
impacto no APK) — listado na seção "Escopo", abaixo, como confirmação ainda
pendente de quem implementa.

- **Última versão publicada**: 1.6.0, em Maven Central/Sonatype, com
  empacotamento `aar` (dependência Android nativa, não lib JVM pura). A
  página de releases do GitHub mostra um release datado de 6 de setembro de
  2024, referente à migração do destino de publicação para Maven Central e
  atualização de dependências Gradle/Android, sem mudança de API.
- **Licença**: MIT, confirmada tanto no arquivo `LICENSE` do repositório
  quanto na metadata publicada no Maven.
- **Autor/mantenedor**: Daniel Hugenroth, pesquisador de segurança
  computacional (pós-doutorado, University of Cambridge).
- **Mecanismo**: binding JNI para a implementação Argon2 em C; usa
  ByteBuffers de alocação direta para permitir apagar segredos da memória de
  forma determinística (vantagem real para um cofre de senhas).
- **Parâmetros default da biblioteca** (não são necessariamente os
  parâmetros do Vaultia): 1 iteração, 65536 KiB (64 MiB) de custo de memória,
  paralelismo de 2, saída de hash de 32 bytes, versão Argon2 V13. Estes
  defaults exigem calibração própria via benchmark — não devem ser adotados
  sem ajuste.
- **Risco de dependência nativa**: por ser uma biblioteca com componente
  nativo (binding JNI sobre código C), existe risco operacional de
  carregamento do arquivo `.so` em determinados dispositivos, ABIs, formatos
  de distribuição ou configurações de empacotamento — `UnsatisfiedLinkError`
  é uma classe comum de falha associada a bibliotecas nativas no Android em
  geral, não um problema confirmado e específico do `argon2kt` por issue
  própria e atual. A documentação da biblioteca oferece um mecanismo de
  carregamento alternativo (SoLoader plugável, ex.: ReLinker) para mitigar
  esse risco. A avaliação desta fase deve incluir teste físico em pelo menos
  um aparelho real e revisão do mecanismo de carregamento nativo, em vez de
  assumir o risco como já materializado ou já descartado.
- **CVEs**: nenhum CVE específico para `lambdapioneer/argon2kt` foi
  encontrado nesta busca. Isso não é confirmação de ausência — é ausência de
  evidência encontrada. Verificação manual da aba "Security" / Issues do
  repositório no GitHub continua sendo critério de aceite desta fase, não
  dispensada pela busca já feita.

## Escopo

Esta fase produz:

- Este documento de avaliação (`PHASE_22_PLAN.md` + `PHASE_22_DONE.md`).
- Confirmação manual, por quem implementa, dos seguintes pontos antes de
  aprovar a entrada da dependência:
  - versão exata a ser fixada no `build.gradle.kts` (`1.6.0`, salvo achado
    de versão mais recente no momento da implementação);
  - licença compatível (MIT — compatível);
  - ausência de issues de segurança abertas e não resolvidas no repositório
    no momento da adoção;
  - impacto no tamanho do APK (a lib inclui binário nativo `.so` por ABI);
  - confirmação de que nenhuma permissão nova é necessária;
  - benchmark do tempo de derivação em pelo menos um aparelho físico Android
    real, com parâmetros candidatos (não os defaults da lib sem ajuste).
- Documentação explícita do fallback: PBKDF2WithHmacSHA256, condição de
  acionamento, e onde essa decisão fica registrada para consulta futura.

## Fora de escopo

Esta fase não implementa:

- adição da dependência `argon2kt` ao `build.gradle.kts`;
- qualquer código de derivação de chave real;
- `CryptoService` (contrato já existe, vazio; implementação fica para fase
  posterior, após esta avaliação ser aprovada);
- escolha final dos parâmetros de produção (iterações, custo de memória,
  paralelismo) — isso depende do resultado do benchmark, que é o critério de
  aceite desta fase, não sua implementação;
- envelope encryption, AES-GCM, formato de header criptográfico, ou qualquer
  item da arquitetura criptográfica mais ampla — isso é fase futura,
  posterior à confirmação do KDF;
- Android Keystore;
- qualquer mudança em `core/session`, `core/storage` ou `feature/auth`.

## Critérios de aceite

- O documento de avaliação está completo, com fonte para cada afirmação
  factual sobre a biblioteca (versão, licença, autor, mecanismo, riscos
  documentados).
- A verificação manual de issues de segurança abertas no repositório foi
  feita e registrada (aprovado, ou motivo de reprovação).
- O benchmark de tempo de derivação em aparelho físico foi executado e
  registrado, com pelo menos dois conjuntos de parâmetros candidatos
  comparados (ex.: um mais rápido e um mais conservador).
- O impacto no tamanho do APK (delta antes/depois de adicionar a lib, sem
  ainda usá-la em código) foi medido e registrado.
- A decisão final desta fase é uma de duas: (a) `argon2kt` aprovado para uso
  na fase de implementação do KDF, com parâmetros candidatos definidos; ou
  (b) `argon2kt` reprovado, com fallback para PBKDF2WithHmacSHA256 formalmente
  ativado e justificativa registrada.
- Nenhuma integração com `CryptoService` ou com qualquer fluxo real do app é
  escrita nesta fase. Código isolado de benchmark pode existir apenas em
  teste, script, ou módulo experimental claramente separado, sem ser chamado
  pela aplicação em execução normal — o erro a evitar é integrar a
  derivação ao produto, não a existência de um harness de benchmark
  controlado.
- O APK permanece sem `android.permission.INTERNET`.
- O roadmap formal não é atualizado nesta fase (próxima atualização em 75%).
