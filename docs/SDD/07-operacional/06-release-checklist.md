# Vaultia – Release Checklist

## Objetivo do Documento

Este documento define o checklist obrigatório para qualquer release do Vaultia Android v1.

Nenhuma versão APK, AAB, release candidate, build interno ou build público deve ser considerada aprovada sem passar por este checklist.

## Princípio Central

Release não é apenas gerar APK.

Release é provar que o aplicativo continua respeitando as decisões de produto, arquitetura, segurança, privacidade, criptografia e operação definidas no SDD.

## Escopo do Checklist

Este checklist cobre:

- documentação;
- código;
- permissões Android;
- criptografia;
- armazenamento local;
- backup;
- logs;
- UI sensível;
- dependências;
- build release;
- assinatura;
- testes;
- evidências;
- aprovação final.

## Bloqueadores Absolutos

O release deve ser bloqueado imediatamente se qualquer item abaixo for encontrado:

- permissão `INTERNET` declarada;
- `android:allowBackup="true"`;
- build release com `debuggable=true`;
- senha-mestra armazenada;
- chave hardcoded;
- dados Nível 3 em plaintext;
- backup aberto em JSON, CSV ou TXT;
- logs com senha, nota, URL, título, chave ou conteúdo sensível;
- dependência criptográfica não revisada;
- algoritmo criptográfico próprio;
- AES-ECB;
- AES-CBC sem autenticação;
- nonce reutilizado com mesma chave;
- crash reporting remoto;
- analytics remoto;
- SDK remoto não aprovado;
- dados reais em teste;
- screenshot com dados reais;
- release sem hash SHA-256 do artefato.

## RC-001 — Revisão do SDD

Antes do release, os documentos essenciais devem estar aprovados.

Checklist:

- [ ] `README.md` aprovado;
- [ ] `01-visao-geral/03-product-decisions.md` aprovado;
- [ ] `01-visao-geral/04-escopo-v1.md` aprovado;
- [ ] `01-visao-geral/05-fora-de-escopo.md` aprovado;
- [ ] `05-seguranca/01-threat-model.md` aprovado;
- [ ] `05-seguranca/03-data-classification.md` aprovado;
- [ ] `05-seguranca/04-crypto-architecture.md` aprovado;
- [ ] `05-seguranca/02-security-requirements.md` aprovado;
- [ ] `03-arquitetura/01-visao-arquitetural.md` aprovado;
- [ ] `07-operacional/06-release-checklist.md` aprovado.

Critério de aceite:

- nenhum documento essencial permanece com status "Proposto para aprovação".

## RC-002 — Escopo da Versão

A versão deve respeitar o escopo aprovado.

Checklist:

- [ ] versão corresponde ao escopo v1;
- [ ] nenhuma feature fora de escopo foi incluída;
- [ ] não há backend;
- [ ] não há login remoto;
- [ ] não há conta;
- [ ] não há sincronização;
- [ ] não há nuvem;
- [ ] não há recuperação de senha;
- [ ] não há analytics;
- [ ] não há crash reporting remoto;
- [ ] não há permissão de internet.

Critério de aceite:

- o APK/AAB representa apenas o produto offline-only aprovado.

## RC-003 — Manifest Android

O manifesto deve ser revisado antes do release.

Checklist:

- [ ] `INTERNET` ausente;
- [ ] localização ausente;
- [ ] contatos ausente;
- [ ] SMS ausente;
- [ ] áudio ausente;
- [ ] câmera ausente na v1 inicial;
- [ ] notificações ausente na v1 inicial;
- [ ] `USE_BIOMETRIC` presente somente se biometria estiver implementada;
- [ ] `android:allowBackup="false"`;
- [ ] `android:fullBackupContent="false"` ou regra equivalente;
- [ ] `dataExtractionRules` revisado;
- [ ] activities sensíveis revisadas;
- [ ] exported components revisados.

Critério de aceite:

- manifesto contém apenas permissões justificadas no SDD.

## RC-004 — Build Release

O build release deve estar endurecido.

Checklist:

- [ ] build variant release usado;
- [ ] `debuggable=false`;
- [ ] minification/R8 avaliado;
- [ ] logs de debug removidos ou bloqueados;
- [ ] assinatura release aplicada;
- [ ] keystore de assinatura protegido;
- [ ] versionCode atualizado;
- [ ] versionName atualizado;
- [ ] appId correto;
- [ ] ausência de secrets no APK/AAB;
- [ ] ausência de dados de teste no APK/AAB.

Critério de aceite:

- artefato final é release real, não build debug.

## RC-005 — Criptografia

A implementação criptográfica deve respeitar a arquitetura aprovada.

Checklist:

- [ ] AEAD usado;
- [ ] AES-256-GCM usado ou alternativa aprovada;
- [ ] KDF usada;
- [ ] salt por cofre presente;
- [ ] nonce/IV único por operação;
- [ ] senha do usuário não usada diretamente como chave AES;
- [ ] Vault Key não hardcoded;
- [ ] Vault Key não armazenada em texto claro;
- [ ] dados corrompidos são rejeitados;
- [ ] senha errada não retorna plaintext;
- [ ] formato criptográfico versionado.

Critério de aceite:

- storage local não revela conteúdo útil fora do app.

## RC-006 — Armazenamento Local

O armazenamento local deve proteger dados Nível 3.

Checklist:

- [ ] notas não ficam em plaintext;
- [ ] senhas não ficam em plaintext;
- [ ] URLs salvas não ficam em plaintext;
- [ ] títulos sensíveis não ficam em plaintext ou decisão explícita foi registrada;
- [ ] tags não ficam em plaintext ou decisão explícita foi registrada;
- [ ] nomes reais de arquivos não ficam expostos;
- [ ] arquivos privados são criptografados;
- [ ] arquivos ficam no storage privado do app;
- [ ] não há cache persistente descriptografado;
- [ ] não há índice de busca plaintext para conteúdo sensível.

Critério de aceite:

- inspeção manual do storage não encontra dados sensíveis legíveis.

## RC-007 — Backup Local Criptografado

Backup deve ser seguro.

Checklist:

- [ ] backup é criptografado;
- [ ] backup tem integridade verificável;
- [ ] backup tem versão;
- [ ] backup não é JSON aberto;
- [ ] backup não é CSV aberto;
- [ ] backup não é TXT aberto;
- [ ] senha incorreta rejeita importação;
- [ ] backup corrompido é rejeitado;
- [ ] exportação exige confirmação;
- [ ] importação exige confirmação antes de substituir dados;
- [ ] aviso de responsabilidade é exibido ao usuário.

Critério de aceite:

- backup exportado não contém plaintext.

## RC-008 — Tela e Exposição Visual

A interface deve proteger dados sensíveis.

Checklist:

- [ ] telas sensíveis usam `FLAG_SECURE`;
- [ ] screenshots são bloqueados em telas sensíveis;
- [ ] tela recente não mostra conteúdo do cofre;
- [ ] app oculta conteúdo ao background;
- [ ] cofre bloqueia por inatividade;
- [ ] dados Nível 3 não aparecem em notificações;
- [ ] dados Nível 3 não aparecem em widgets;
- [ ] mensagens de erro não expõem detalhes sensíveis.

Critério de aceite:

- conteúdo sensível não é visível fora da sessão desbloqueada.

## RC-009 — Clipboard

Clipboard deve ser controlado.

Checklist:

- [ ] copiar senha exige ação explícita;
- [ ] conteúdo copiado expira;
- [ ] clipboard não é usado automaticamente;
- [ ] conteúdo copiado não aparece em logs;
- [ ] usuário recebe indicação de cópia temporária.

Critério de aceite:

- clipboard não mantém segredo indefinidamente por ação do app.

## RC-010 — Logs

Logs devem ser sanitizados.

Checklist:

- [ ] ausência de `println`;
- [ ] ausência de `printStackTrace`;
- [ ] ausência de `Log.d` não autorizado;
- [ ] logger central usado;
- [ ] logs não contêm senha-mestra;
- [ ] logs não contêm senhas salvas;
- [ ] logs não contêm notas;
- [ ] logs não contêm URLs salvas;
- [ ] logs não contêm títulos de itens;
- [ ] logs não contêm nomes reais de arquivos;
- [ ] logs não contêm chaves;
- [ ] logs não contêm ciphertext;
- [ ] logs não contêm plaintext.

Critério de aceite:

- revisão de Logcat em fluxo real não encontra dados sensíveis.

## RC-011 — Auditoria Local

Auditoria local deve ser mínima e sanitizada.

Checklist:

- [ ] cofre criado registrado sem conteúdo sensível;
- [ ] desbloqueio registrado sem conteúdo sensível;
- [ ] falha de desbloqueio registrada sem detalhe perigoso;
- [ ] bloqueio registrado;
- [ ] criação de item registrada sem título/conteúdo;
- [ ] edição de item registrada sem título/conteúdo;
- [ ] exclusão de item registrada sem título/conteúdo;
- [ ] exportação de backup registrada sem path sensível;
- [ ] importação de backup registrada sem path sensível;
- [ ] retenção local revisada.

Critério de aceite:

- auditoria ajuda o usuário sem vazar conteúdo do cofre.

## RC-012 — Dependências

Dependências devem ser revisadas.

Checklist:

- [ ] lista de dependências revisada;
- [ ] dependências criptográficas justificadas;
- [ ] dependências com tráfego remoto ausentes;
- [ ] SDKs de analytics ausentes;
- [ ] SDKs de crash remoto ausentes;
- [ ] dependências não adicionam permissão `INTERNET`;
- [ ] dependências não adicionam permissões indevidas;
- [ ] lockfile revisado;
- [ ] vulnerabilidades conhecidas avaliadas.

Critério de aceite:

- dependências não quebram o modelo offline-only.

## RC-013 — Testes Funcionais

Fluxos principais devem ser testados.

Checklist:

- [ ] primeira abertura;
- [ ] criação do cofre;
- [ ] aviso sem recuperação;
- [ ] desbloqueio com senha correta;
- [ ] rejeição de senha incorreta;
- [ ] bloqueio manual;
- [ ] bloqueio por background;
- [ ] bloqueio por inatividade;
- [ ] criação de nota;
- [ ] edição de nota;
- [ ] exclusão de nota;
- [ ] criação de senha;
- [ ] cópia temporária de senha;
- [ ] importação de arquivo;
- [ ] visualização de arquivo;
- [ ] exclusão de arquivo;
- [ ] exportação de backup;
- [ ] importação de backup.

Critério de aceite:

- todos os fluxos v1 funcionam sem internet.

## RC-014 — Testes de Segurança

Testes mínimos de segurança devem ser executados.

Checklist:

- [ ] tentar abrir storage fora do app;
- [ ] buscar plaintext no banco;
- [ ] buscar plaintext em arquivos;
- [ ] buscar senha em logs;
- [ ] buscar nota em logs;
- [ ] buscar URL em logs;
- [ ] testar backup com senha errada;
- [ ] testar backup corrompido;
- [ ] testar screenshot em tela sensível;
- [ ] testar app switcher;
- [ ] testar clipboard;
- [ ] testar ausência de internet;
- [ ] testar manifesto;
- [ ] testar `allowBackup=false`.

Critério de aceite:

- nenhum teste encontra vazamento óbvio de dado Nível 3.

## RC-015 — Teste em Aparelho Real

Release precisa ser validado em dispositivo real.

Checklist:

- [ ] instalar APK/AAB em aparelho real;
- [ ] criar cofre;
- [ ] bloquear e desbloquear;
- [ ] criar itens;
- [ ] reiniciar app;
- [ ] validar persistência;
- [ ] testar background;
- [ ] testar screenshot;
- [ ] testar backup;
- [ ] testar importação;
- [ ] validar desempenho aceitável de KDF;
- [ ] validar UX sem internet.

Critério de aceite:

- app funciona em aparelho real sem crash bloqueante.

## RC-016 — Evidências

Release deve gerar evidências mínimas.

Checklist:

- [ ] versão do app registrada;
- [ ] commit hash registrado;
- [ ] data do build registrada;
- [ ] ambiente registrado;
- [ ] comando de build registrado;
- [ ] resultado dos testes registrado;
- [ ] APK/AAB localizado;
- [ ] tamanho do artefato registrado;
- [ ] SHA-256 do artefato registrado;
- [ ] checklist preenchido;
- [ ] pendências conhecidas registradas.

Critério de aceite:

- release é rastreável.

## RC-017 — Aprovação Final

A aprovação final deve ser explícita.

Checklist:

- [ ] produto aprovado;
- [ ] segurança aprovada;
- [ ] arquitetura aprovada;
- [ ] checklist de release aprovado;
- [ ] riscos residuais aceitos;
- [ ] versão marcada;
- [ ] artefato preservado;
- [ ] changelog preparado.

Critério de aceite:

- release só ocorre após aprovação consciente, não por acidente.

## Resultado do Release

Campos obrigatórios no momento do release:

- Versão:
- Data:
- Commit:
- Ambiente:
- Tipo de artefato:
- Caminho do artefato:
- SHA-256:
- Status:
- Aprovador:
- Observações:

## Status do Documento

- **Documento:** `docs/SDD/07-operacional/06-release-checklist.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Fundação e SDD Essencial
