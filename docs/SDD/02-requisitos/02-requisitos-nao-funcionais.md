# Vaultia – Requisitos Não Funcionais

## Objetivo do Documento

Este documento define os requisitos não funcionais do Vaultia Android v1.

Requisitos não funcionais descrevem como o aplicativo deve se comportar em termos de segurança, privacidade, desempenho, confiabilidade, usabilidade, manutenção, compatibilidade e operação.

## Princípio Central

No Vaultia, requisitos não funcionais não são detalhes secundários.

Segurança, privacidade e confiabilidade são parte do produto. Uma funcionalidade que funciona, mas viola esses requisitos, não está pronta.

## RNF-001 — Offline-Only

O app deve funcionar integralmente sem internet.

Requisitos:

- não declarar permissão `INTERNET`;
- não depender de servidor;
- não depender de API;
- não depender de conta remota;
- não depender de sincronização;
- não depender de analytics;
- não depender de crash reporting remoto.

Critérios de aceite:

- app funciona em modo avião;
- manifesto não contém `android.permission.INTERNET`;
- fluxos v1 funcionam sem rede.

## RNF-002 — Confidencialidade

Dados Nível 3 devem permanecer confidenciais.

Requisitos:

- dados sensíveis não podem ser armazenados em plaintext;
- dados sensíveis não podem aparecer em logs;
- dados sensíveis não podem aparecer em notificações;
- dados sensíveis não podem aparecer no app switcher;
- dados sensíveis não podem sair do dispositivo na v1.

Critérios de aceite:

- inspeção de storage não encontra plaintext;
- Logcat não revela conteúdo sensível;
- tela recente não exibe conteúdo do cofre.

## RNF-003 — Integridade

O app deve detectar alteração ou corrupção de dados protegidos.

Requisitos:

- usar criptografia autenticada;
- validar integridade antes de aceitar dados;
- rejeitar backup corrompido;
- rejeitar registro local corrompido;
- tratar falhas com erro seguro.

Critérios de aceite:

- dado adulterado não é aceito como válido;
- backup corrompido falha;
- erro não vaza detalhe criptográfico.

## RNF-004 — Segurança Criptográfica

A implementação deve seguir a arquitetura criptográfica aprovada.

Requisitos:

- usar AEAD;
- preferir AES-256-GCM;
- usar KDF;
- usar salt;
- usar nonce ou IV único quando aplicável;
- não usar criptografia própria;
- não usar chave hardcoded;
- não usar senha diretamente como chave AES.

Critérios de aceite:

- testes cobrem senha correta, senha errada e dado corrompido;
- nenhum algoritmo proibido é usado;
- nenhum segredo aparece no código.

## RNF-005 — Proteção da Senha-Mestra

A Master Password deve ser protegida.

Requisitos:

- nunca armazenar senha-mestra;
- nunca logar senha-mestra;
- nunca incluir senha-mestra em backup;
- nunca transmitir senha-mestra;
- exigir aviso de ausência de recuperação;
- aplicar política mínima de força.

Critérios de aceite:

- busca no storage não encontra senha;
- busca nos logs não encontra senha;
- onboarding informa risco de perda.

## RNF-006 — Privacidade por Design

O app deve minimizar coleta e exposição de dados.

Requisitos:

- não coletar dados desnecessários;
- não solicitar e-mail;
- não solicitar telefone;
- não solicitar localização;
- não solicitar contatos;
- não solicitar câmera na v1 inicial;
- não solicitar notificações na v1 inicial;
- não enviar dados para terceiros.

Critérios de aceite:

- app funciona sem dados pessoais de cadastro;
- manifesto contém permissões mínimas;
- nenhuma telemetria remota existe.

## RNF-007 — Proteção Visual

O app deve reduzir exposição visual de dados sensíveis.

Requisitos:

- usar `FLAG_SECURE` em telas sensíveis;
- ocultar conteúdo ao background;
- impedir preview sensível;
- bloquear cofre por inatividade;
- não exibir dados Nível 3 em notificações ou widgets.

Critérios de aceite:

- screenshot é bloqueado em telas sensíveis;
- app switcher não mostra conteúdo do cofre;
- cofre bloqueia conforme política.

## RNF-008 — Desempenho

O app deve ser responsivo em operações comuns.

Requisitos:

- abertura de telas não deve travar UI;
- operações criptográficas não devem rodar na thread principal;
- KDF deve ter parâmetros seguros e usáveis;
- arquivos grandes não devem travar UI;
- importação e backup devem indicar progresso quando necessário.

Critérios de aceite:

- app permanece utilizável durante operações pesadas;
- KDF tem tempo aceitável em aparelho real;
- backup não congela a interface de forma indefinida.

## RNF-009 — Confiabilidade

O app deve preservar dados mesmo diante de falhas controladas.

Requisitos:

- falha ao salvar não deve corromper item existente;
- falha ao trocar senha não deve corromper cofre;
- falha ao importar backup não deve destruir cofre atual;
- falha criptográfica deve levar a estado seguro;
- app deve evitar dados parcialmente gravados.

Critérios de aceite:

- teste de erro em importação mantém cofre existente;
- teste de troca de senha falha sem perda de dados;
- erro retorna estado seguro.

## RNF-010 — Recuperação Local

A recuperação deve depender apenas de backup local criptografado e senha correta.

Requisitos:

- não oferecer recuperação remota;
- não oferecer reset de senha;
- não oferecer suporte com desbloqueio;
- não armazenar chave de recuperação externa;
- backup local deve ser criptografado.

Critérios de aceite:

- app deixa claro que não há recuperação sem senha;
- importação exige backup válido e senha correta.

## RNF-011 — Usabilidade Segura

O app deve ser simples sem esconder riscos.

Requisitos:

- mensagens devem ser claras;
- avisos de perda de senha devem ser explícitos;
- erros devem ser compreensíveis e genéricos;
- ações destrutivas exigem confirmação;
- exportação de backup exige aviso;
- importação com sobrescrita exige confirmação.

Critérios de aceite:

- usuário entende riscos principais;
- exclusões pedem confirmação;
- backup exibe aviso adequado.

## RNF-012 — Manutenibilidade

O código deve ser organizado para manutenção segura.

Requisitos:

- separar camadas;
- evitar lógica sensível na UI;
- centralizar criptografia;
- centralizar logging;
- centralizar política de permissões;
- documentar decisões críticas;
- manter SDD atualizado.

Critérios de aceite:

- estrutura segue arquitetura aprovada;
- mudanças críticas atualizam documentos;
- componentes têm responsabilidade clara.

## RNF-013 — Testabilidade

O app deve ser testável.

Requisitos:

- regras de domínio devem ser testáveis sem Android;
- serviços críticos devem possuir testes;
- storage deve ser testado;
- backup deve ser testado;
- criptografia deve ser testada;
- permissões devem ser auditáveis;
- logs devem ser auditáveis.

Critérios de aceite:

- testes automatizados cobrem fluxos críticos;
- auditorias manuais estão documentadas;
- release não ocorre sem testes mínimos.

## RNF-014 — Compatibilidade Android

A v1 deve definir compatibilidade Android de forma consciente.

Requisitos:

- definir minSdk antes da implementação final;
- definir targetSdk de acordo com tooling atual;
- validar comportamento em aparelho real;
- considerar diferenças de Keystore por aparelho;
- considerar disponibilidade de biometria;
- considerar restrições modernas de storage.

Critérios de aceite:

- minSdk e targetSdk registrados no projeto;
- teste real executado;
- fallback de biometria e Keystore considerado.

## RNF-015 — Segurança de Dependências

Dependências devem ser mínimas e revisadas.

Requisitos:

- justificar cada dependência crítica;
- evitar SDKs remotos;
- evitar dependências sem manutenção;
- revisar dependência criptográfica;
- revisar permissões adicionadas por dependência;
- bloquear dependências que violem offline-only.

Critérios de aceite:

- lista de dependências revisada;
- nenhuma dependência adiciona `INTERNET` sem aprovação;
- dependências críticas documentadas.

## RNF-016 — Build Release Seguro

Build release deve ser endurecido.

Requisitos:

- `debuggable=false`;
- ausência de debug logs sensíveis;
- ausência de dados de teste;
- ausência de secrets;
- assinatura release protegida;
- hash SHA-256 gerado;
- checklist de release preenchido.

Critérios de aceite:

- artefato release é rastreável;
- release checklist aprovado;
- APK/AAB não contém bloqueadores absolutos.

## RNF-017 — Observabilidade Local

A v1 deve ter observabilidade local sem telemetria remota.

Requisitos:

- logs técnicos sanitizados;
- auditoria local sanitizada;
- ausência de analytics remoto;
- ausência de crash reporting remoto;
- mensagens de erro seguras.

Critérios de aceite:

- app ajuda diagnóstico local sem expor dados;
- nenhum dado é enviado para terceiros.

## RNF-018 — Resiliência a Uso Incorreto

O app deve reduzir dano por ações acidentais.

Requisitos:

- confirmar exclusões;
- confirmar importação que substitui dados;
- avisar antes de exportar backup;
- avisar sobre perda de senha;
- impedir operações críticas com cofre bloqueado.

Critérios de aceite:

- ações destrutivas exigem confirmação;
- app não permite editar ou exportar com cofre bloqueado.

## RNF-019 — Acessibilidade Básica

A interface deve buscar acessibilidade mínima.

Requisitos:

- textos legíveis;
- botões com rótulos claros;
- contraste razoável;
- fluxo compreensível;
- mensagens não dependem apenas de cor;
- campos de senha têm comportamento previsível.

Critérios de aceite:

- telas principais são utilizáveis;
- ações críticas têm textos claros;
- erros são compreensíveis.

## RNF-020 — Internacionalização Futura

A v1 pode começar em um idioma, mas deve evitar amarrar textos no domínio.

Requisitos:

- textos de UI devem ficar organizados;
- mensagens críticas devem ser fáceis de revisar;
- mensagens de segurança devem ser consistentes;
- regras de negócio não devem depender de string visual.

Critérios de aceite:

- textos não ficam espalhados em lógica sensível;
- mensagens críticas são revisáveis.

## RNF-021 — Escalabilidade Local

A v1 deve suportar crescimento razoável do cofre local.

Requisitos:

- lidar com múltiplas notas;
- lidar com múltiplos itens de senha;
- lidar com múltiplos arquivos;
- evitar carregar todo conteúdo sensível em memória sem necessidade;
- paginar ou filtrar listas quando necessário futuramente.

Critérios de aceite:

- app não depende de lista pequena fixa;
- arquitetura permite crescimento local.

## RNF-022 — Consistência de Estado

Estados do cofre devem ser explícitos e controlados.

Requisitos:

- `NotInitialized`;
- `Locked`;
- `Unlocking`;
- `Unlocked`;
- `BackgroundLocked`;
- `BackupExporting`;
- `BackupImporting`;
- `ErrorSafe`.

Critérios de aceite:

- dados Nível 3 só aparecem em `Unlocked`;
- erro crítico leva a estado seguro;
- background oculta conteúdo.

## RNF-023 — Compatibilidade com Auditoria

O app deve facilitar auditoria técnica.

Requisitos:

- estrutura de projeto clara;
- documentos atualizados;
- comandos de build documentados;
- checklist de release;
- logs sanitizados;
- testes rastreáveis;
- artefatos com hash.

Critérios de aceite:

- auditor consegue rastrear decisão até implementação;
- release gera evidências.

## RNF-024 — Não Prometer Segurança Absoluta

O app deve comunicar limitações honestamente.

Requisitos:

- não prometer invulnerabilidade;
- não prometer proteção contra OS comprometido;
- não prometer recuperação impossível;
- não prometer anonimato absoluto;
- não prometer proteção contra coerção.

Critérios de aceite:

- textos de segurança são honestos;
- documentação declara riscos residuais.

## Critérios de Aceite do Documento

Este documento será considerado aceito quando:

- requisitos de segurança estiverem definidos;
- requisitos de privacidade estiverem definidos;
- requisitos de desempenho estiverem definidos;
- requisitos de confiabilidade estiverem definidos;
- requisitos de usabilidade estiverem definidos;
- requisitos de manutenção estiverem definidos;
- requisitos de release estiverem definidos;
- documento estiver alinhado com requisitos funcionais, arquitetura e security requirements.

## Status do Documento

- **Documento:** `docs/SDD/02-requisitos/02-requisitos-nao-funcionais.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Requisitos Pré-Implementação
