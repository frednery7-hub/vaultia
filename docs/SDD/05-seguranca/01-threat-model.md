# Vaultia – Threat Model

## Objetivo do Documento

Este documento define o modelo de ameaças do Vaultia Android v1.

O objetivo é identificar os ativos protegidos, os atores de ameaça, os cenários de ataque relevantes, as mitigações esperadas e os limites explícitos de proteção da v1.

## Escopo do Threat Model

Este threat model cobre:

- aplicativo Android v1;
- cofre local;
- senha-mestra;
- dados sensíveis armazenados no dispositivo;
- criptografia local;
- armazenamento local;
- biometria opcional;
- backup local criptografado;
- logs e auditoria local;
- permissões Android;
- fluxo de release.

Este threat model não cobre backend, nuvem, sincronização, conta remota ou APIs, pois esses componentes não existem na v1.

## Princípio Central

O Vaultia v1 assume que dados sensíveis devem permanecer protegidos mesmo que alguém tenha acesso aos arquivos locais do aplicativo.

O Vaultia v1 não promete proteção total se o sistema operacional estiver totalmente comprometido.

## Ativos Protegidos

### Ativos Críticos

- senha-mestra;
- Vault Key;
- Key Encryption Key;
- dados criptográficos derivados;
- conteúdo de notas;
- senhas salvas;
- documentos;
- fotos privadas;
- backup criptografado;
- material temporariamente descriptografado em memória.

### Ativos Sensíveis

- títulos de itens;
- tags;
- URLs salvas;
- nomes de arquivos;
- metadados de arquivos;
- histórico local de auditoria;
- configurações de segurança;
- preferência de biometria;
- estado bloqueado/desbloqueado;
- dados temporários de visualização.

### Ativos Operacionais

- código-fonte;
- dependências;
- signing key;
- build release;
- pipeline CI/CD;
- secrets do projeto;
- configuração de manifesto Android;
- documentação SDD.

## Atores de Ameaça

### A1 — Pessoa com acesso casual ao celular desbloqueado

Exemplos:

- amigo;
- parceiro;
- familiar;
- colega;
- pessoa que pegou o celular por alguns minutos.

Objetivo do atacante:

- abrir o app;
- visualizar senhas;
- visualizar notas;
- visualizar fotos ou documentos;
- copiar dados sensíveis.

Mitigações:

- cofre bloqueado por padrão;
- senha-mestra;
- auto-lock;
- bloqueio ao background;
- bloqueio manual;
- biometria opcional;
- tela recente sem conteúdo sensível.

## A2 — Ladrão com acesso físico ao celular

Exemplos:

- roubo;
- furto;
- perda do aparelho.

Objetivo do atacante:

- extrair arquivos locais;
- copiar banco de dados;
- copiar arquivos do app;
- tentar ataque offline contra o cofre;
- tentar restaurar backup automático.

Mitigações:

- criptografia em repouso;
- senha-mestra;
- KDF;
- salt por cofre;
- Vault Key protegida;
- Android Keystore quando aplicável;
- backup automático do Android desabilitado;
- ausência de dados sensíveis em texto claro.

## A3 — Malware comum no dispositivo

Exemplos:

- app malicioso sem root;
- app tentando capturar tela;
- app tentando ler clipboard;
- app tentando acessar arquivos externos;
- app tentando abusar de permissões.

Objetivo do atacante:

- capturar senhas;
- capturar telas;
- ler clipboard;
- encontrar arquivos exportados;
- obter documentos ou fotos privadas.

Mitigações:

- `FLAG_SECURE`;
- uso de storage privado;
- ausência de storage público por padrão;
- clipboard temporário;
- limpeza de temporários;
- menor número possível de permissões;
- ausência de permissão `INTERNET`.

## A4 — Malware avançado ou dispositivo comprometido

Exemplos:

- root;
- bootloader destravado;
- hooking;
- keylogger;
- screen reader malicioso;
- instrumentação;
- sistema operacional comprometido.

Objetivo do atacante:

- observar entrada da senha-mestra;
- interceptar dados em memória;
- capturar tela apesar das proteções;
- modificar comportamento do app;
- extrair material criptográfico em uso.

Mitigações:

- alerta de risco quando possível;
- reautenticação;
- logs mínimos;
- redução de superfície de ataque;
- ausência de rede na v1;
- não prometer segurança absoluta.

Limite:

- a v1 não garante proteção total contra dispositivo totalmente comprometido.

## A5 — Atacante com acesso a backup criptografado

Exemplos:

- backup exportado para pendrive;
- backup salvo manualmente em cloud pessoal;
- backup compartilhado acidentalmente;
- backup roubado.

Objetivo do atacante:

- restaurar o backup;
- quebrar senha offline;
- extrair conteúdo do cofre;
- analisar metadados.

Mitigações:

- backup sempre criptografado;
- KDF;
- senha-mestra necessária;
- validação de integridade;
- ausência de plaintext;
- formato versionado;
- aviso forte ao exportar.

## A6 — Usuário legítimo descuidado

Exemplos:

- usa senha fraca;
- esquece a senha-mestra;
- exporta backup para local inseguro;
- copia senha e deixa no clipboard;
- mantém aparelho sem bloqueio;
- usa aparelho rooteado;
- ignora avisos.

Objetivo involuntário:

- reduzir a própria segurança;
- perder acesso;
- expor dados;
- criar backup inseguro.

Mitigações:

- medidor de força de senha;
- aviso de não recuperação;
- aviso de risco no backup;
- clipboard temporário;
- configurações de auto-lock;
- mensagens claras;
- UX sem falsa promessa.

## A7 — Desenvolvedor ou processo de engenharia descuidado

Exemplos:

- loga segredo;
- adiciona dependência insegura;
- ativa permissão indevida;
- adiciona `INTERNET`;
- deixa `debuggable=true`;
- esquece `allowBackup=false`;
- commita secrets;
- enfraquece criptografia.

Objetivo ou risco:

- introduzir falha de segurança;
- vazar segredo;
- quebrar decisão offline-only;
- reduzir integridade do produto.

Mitigações:

- SDD obrigatório;
- revisão de segurança;
- CI/CD;
- secret scanning;
- release checklist;
- política de logs;
- revisão de dependências;
- verificação de manifesto;
- gates de release.

## A8 — Atacante com acesso ao repositório ou pipeline

Exemplos:

- token GitHub vazado;
- conta sem MFA;
- CI mal configurado;
- alteração maliciosa em workflow;
- dependência comprometida;
- signing key exposta.

Objetivo do atacante:

- inserir código malicioso;
- capturar segredos;
- adulterar build;
- publicar artefato comprometido.

Mitigações:

- MFA em contas críticas;
- menor privilégio;
- proteção de branch;
- revisão de PR;
- signing key protegida;
- secrets fora do Git;
- CI com escopo mínimo;
- release manual controlado.

## Ameaças Principais

### T1 — Extração de banco local

Descrição:

Um atacante copia arquivos locais do app e tenta ler os dados.

Mitigações:

- criptografia em repouso;
- KDF;
- Vault Key protegida;
- ausência de plaintext;
- backup automático desabilitado.

Status:

- mitigada na v1.

## T2 — Senha-mestra fraca

Descrição:

Usuário define senha-mestra fraca e o atacante tenta força bruta offline.

Mitigações:

- medidor de força;
- aviso de risco;
- KDF;
- política mínima de senha;
- mensagens educativas.

Status:

- parcialmente mitigada.

Risco residual:

- o usuário ainda pode escolher senha ruim se a política permitir.

## T3 — Conteúdo sensível em logs

Descrição:

Conteúdo do cofre aparece em Logcat, arquivo local, crash ou auditoria.

Mitigações:

- logger central;
- proibição de logs sensíveis;
- revisão de código;
- CI/CD para padrões proibidos;
- release checklist.

Status:

- mitigação obrigatória.

## T4 — Screenshot ou preview do app

Descrição:

Tela sensível aparece em screenshot, screen recording ou tela de apps recentes.

Mitigações:

- `FLAG_SECURE`;
- proteção de tela recente;
- bloqueio ao background;
- telas sensíveis protegidas.

Status:

- mitigação obrigatória.

## T5 — Clipboard expõe senha

Descrição:

Senha copiada fica disponível para outros apps ou para o usuário por tempo excessivo.

Mitigações:

- copiar apenas por ação explícita;
- limpeza automática do clipboard;
- aviso visual;
- não copiar automaticamente.

Status:

- mitigação obrigatória no núcleo de senhas.

## T6 — Backup aberto ou mal protegido

Descrição:

Usuário exporta dados em formato legível ou backup é roubado.

Mitigações:

- proibir exportação aberta na v1;
- backup criptografado;
- integridade;
- aviso forte;
- senha necessária para importar.

Status:

- mitigação obrigatória no núcleo de backup.

## T7 — Adição acidental de permissão INTERNET

Descrição:

Uma dependência ou alteração adiciona permissão `INTERNET`.

Mitigações:

- decisão de produto;
- verificação manual;
- CI/CD;
- release checklist;
- revisão de manifesto.

Status:

- mitigação obrigatória.

## T8 — Android backup automático expõe dados

Descrição:

O sistema operacional ou ferramenta de backup copia dados do app.

Mitigações:

- `android:allowBackup="false"`;
- `android:fullBackupContent="false"`;
- revisão de `dataExtractionRules`.

Status:

- mitigação obrigatória.

## T9 — Dependência vulnerável ou maliciosa

Descrição:

Biblioteca externa introduz falha, coleta dados ou adiciona comportamento indevido.

Mitigações:

- dependências mínimas;
- justificativa para dependência;
- dependency audit;
- revisão de permissões;
- preferência por APIs nativas.

Status:

- mitigação obrigatória.

## T10 — Dispositivo comprometido

Descrição:

Atacante controla o sistema operacional ou tem root avançado.

Mitigações:

- alerta de risco;
- redução de superfície;
- ausência de rede;
- reautenticação;
- mensagens claras.

Status:

- risco residual aceito.

Limite:

- não há promessa de proteção total nesse cenário.

## Ameaças Fora do Escopo

Ficam fora do escopo de proteção garantida da v1:

- sistema operacional totalmente comprometido;
- malware com controle privilegiado;
- coerção física extrema;
- análise forense avançada com aparelho desbloqueado;
- vulnerabilidades desconhecidas do Android;
- falhas de hardware;
- ataque físico avançado contra chip;
- engenharia social fora do app;
- usuário salvando senha-mestra em local inseguro;
- usuário exportando backup e compartilhando voluntariamente;
- comprometimento total da conta Google do aparelho;
- comprometimento do teclado usado para digitar senha-mestra.

## Riscos Residuais Aceitos

A v1 aceita os seguintes riscos residuais:

- usuário pode escolher senha fraca;
- usuário pode perder a senha-mestra;
- usuário pode armazenar backup em local inseguro;
- dispositivo rooteado reduz garantias;
- malware avançado pode observar dados durante uso;
- dados descriptografados existem temporariamente em memória;
- biometria depende da segurança do dispositivo;
- logs locais mínimos ainda podem revelar padrões de uso;
- arquivo exportado depende de guarda correta pelo usuário.

## Requisitos Derivados

Este threat model gera os seguintes requisitos:

- senha-mestra obrigatória;
- aviso de não recuperação;
- KDF obrigatório;
- criptografia autenticada;
- backup criptografado;
- ausência de `INTERNET`;
- `allowBackup=false`;
- `FLAG_SECURE`;
- clipboard temporário;
- logs sanitizados;
- logger central;
- auditoria local mínima;
- dependências mínimas;
- release checklist;
- revisão de manifesto;
- teste em aparelho real.

## Critérios de Aceite do Threat Model

Este threat model será considerado aceito quando:

- ativos críticos estiverem listados;
- atores de ameaça estiverem definidos;
- ameaças principais estiverem mapeadas;
- mitigações estiverem associadas;
- riscos residuais estiverem declarados;
- ameaças fora do escopo estiverem explícitas;
- requisitos derivados estiverem claros;
- o documento estiver alinhado com product decisions e escopo v1.

## Status do Documento

- **Documento:** `docs/SDD/05-seguranca/01-threat-model.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Fundação e SDD Essencial
