# Vaultia – Requisitos de Privacidade

## Objetivo do Documento

Este documento define os requisitos de privacidade do Vaultia Android v1.

O objetivo é garantir que o aplicativo minimize coleta de dados, opere localmente, não envie informações a terceiros e comunique limites de forma clara ao usuário.

## Princípio Central

Privacidade no Vaultia não é uma camada posterior.

A v1 deve ser desenhada para coletar o mínimo possível, processar localmente e impedir vazamento por arquitetura.

## RP-001 — Dados Locais por Padrão

Todos os dados do cofre devem permanecer no dispositivo do usuário.

Requisitos:

- notas ficam locais;
- senhas ficam locais;
- fotos ficam locais;
- documentos ficam locais;
- auditoria fica local;
- configurações ficam locais;
- backup é gerado localmente;
- importação de backup ocorre localmente.

Critérios de aceite:

- nenhum fluxo v1 exige servidor;
- nenhum dado do cofre é enviado pela internet;
- app funciona em modo avião.

## RP-002 — Ausência de Conta

A v1 não deve exigir conta de usuário.

Requisitos:

- não solicitar e-mail;
- não solicitar telefone;
- não solicitar nome real;
- não solicitar login social;
- não criar perfil remoto;
- não criar identificador remoto do usuário.

Critérios de aceite:

- primeiro uso cria cofre, não conta;
- app não possui tela de login remoto;
- app não possui fluxo de cadastro.

## RP-003 — Ausência de Telemetria Remota

A v1 não deve enviar telemetria.

Requisitos:

- não enviar eventos de uso;
- não enviar métricas;
- não enviar analytics;
- não enviar crash report remoto;
- não enviar logs;
- não enviar identificadores de dispositivo.

Critérios de aceite:

- não existe SDK remoto de analytics;
- não existe SDK remoto de crash;
- manifesto não contém INTERNET.

## RP-004 — Minimização de Dados

O app deve coletar apenas dados necessários ao funcionamento do cofre.

Requisitos:

- não coletar localização;
- não coletar contatos;
- não coletar SMS;
- não coletar áudio;
- não coletar câmera na v1 inicial;
- não coletar calendário;
- não coletar identificadores publicitários;
- não coletar dados de rede.

Critérios de aceite:

- permissões proibidas não aparecem no manifesto;
- fluxos funcionam sem dados pessoais de cadastro.

## RP-005 — Classificação de Privacidade

Dados do cofre devem ser tratados como dados privados de alta sensibilidade.

Dados privados incluem:

- notas;
- senhas;
- documentos;
- fotos;
- títulos;
- tags;
- URLs;
- nomes de usuário;
- e-mails salvos;
- nomes de arquivos;
- metadados associados a itens;
- backup;
- material criptográfico.

Critérios de aceite:

- dados Nível 3 seguem política de classificação;
- metadados sensíveis não são tratados como públicos.

## RP-006 — Consentimento Informado de Risco

O usuário deve ser informado sobre riscos essenciais antes de criar o cofre.

Informações obrigatórias:

- o cofre é local;
- não há recuperação de senha;
- perda da Master Password pode causar perda de acesso;
- backup é responsabilidade do usuário;
- biometria é conveniência, não recuperação garantida;
- o app não promete proteção absoluta contra aparelho comprometido.

Critérios de aceite:

- onboarding exibe aviso;
- usuário confirma entendimento;
- texto não promete recuperação inexistente.

## RP-007 — Transparência de Funcionamento

O app deve explicar de forma simples como os dados são protegidos.

Requisitos:

- informar que dados ficam no dispositivo;
- informar que backup é local e criptografado;
- informar que não há nuvem na v1;
- informar que não há conta remota;
- informar que o app não envia dados do cofre.

Critérios de aceite:

- tela de informações de segurança existe ou está planejada;
- textos são consistentes com arquitetura.

## RP-008 — Backup sob Controle do Usuário

Backup deve ser uma ação explícita do usuário.

Requisitos:

- não criar backup automático em nuvem;
- não enviar backup para servidor;
- não fazer upload automático;
- backup deve ser criptografado;
- usuário escolhe destino;
- usuário recebe aviso sobre guarda do arquivo.

Critérios de aceite:

- exportação exige ação explícita;
- backup não contém plaintext;
- backup não é enviado automaticamente.

## RP-009 — Importação de Backup com Segurança

Importação de backup deve proteger o cofre existente e a privacidade do usuário.

Requisitos:

- validar formato;
- rejeitar arquivo inválido;
- rejeitar senha incorreta;
- rejeitar backup corrompido;
- confirmar antes de substituir cofre existente;
- não enviar backup a servidor;
- não logar caminho sensível.

Critérios de aceite:

- backup inválido não expõe conteúdo;
- backup inválido não corrompe cofre existente;
- importação é local.

## RP-010 — Logs Privados e Sanitizados

Logs técnicos não podem expor dados privados.

Proibido em logs:

- senha-mestra;
- senhas salvas;
- notas;
- URLs;
- títulos;
- tags;
- nomes de arquivos;
- conteúdo de documentos;
- conteúdo de fotos;
- chaves;
- plaintext;
- ciphertext;
- caminhos sensíveis.

Critérios de aceite:

- revisão de Logcat não encontra dados privados;
- logger central aplica sanitização.

## RP-011 — Auditoria Local Sanitizada

Auditoria local deve ajudar o usuário sem vazar conteúdo.

Requisitos:

- registrar apenas eventos genéricos;
- não registrar conteúdo de item;
- não registrar título;
- não registrar URL;
- não registrar nome real de arquivo;
- não registrar senha;
- manter auditoria local;
- aplicar retenção limitada.

Critérios de aceite:

- auditoria não contém dados Nível 3;
- auditoria não sai do dispositivo.

## RP-012 — Proteção contra Exposição Visual

O app deve reduzir risco de exposição visual de dados privados.

Requisitos:

- proteger telas sensíveis;
- bloquear screenshots quando aplicável;
- ocultar conteúdo ao background;
- impedir preview sensível no app switcher;
- não exibir dados privados em notificações;
- não exibir dados privados em widgets.

Critérios de aceite:

- app switcher não mostra dados do cofre;
- screenshot é bloqueado em tela sensível;
- notificações com dados Nível 3 não existem.

## RP-013 — Clipboard com Privacidade

Clipboard deve ser tratado como área de risco.

Requisitos:

- copiar senha apenas por ação explícita;
- avisar usuário;
- limpar clipboard após tempo definido, quando possível;
- não copiar automaticamente;
- não logar conteúdo copiado.

Critérios de aceite:

- conteúdo copiado expira;
- logs não contêm senha copiada.

## RP-014 — Privacidade em Arquivos

Fotos e documentos importados devem permanecer privados.

Requisitos:

- armazenar em área privada do app;
- criptografar arquivo antes de persistir;
- evitar manter cópia descriptografada;
- limpar temporários;
- não salvar cópia permanente na galeria por ação do app;
- tratar nome original como sensível.

Critérios de aceite:

- arquivo não fica legível fora do app;
- nome real não é exposto sem necessidade;
- temporários são controlados.

## RP-015 — Ausência de Compartilhamento Automático

O app não deve compartilhar dados do cofre automaticamente.

Requisitos:

- não compartilhar notas;
- não compartilhar senhas;
- não compartilhar arquivos;
- não compartilhar backup;
- não compartilhar auditoria;
- qualquer exportação exige ação explícita.

Critérios de aceite:

- não há envio automático;
- não há integração social;
- não há upload automático.

## RP-016 — Dados de Teste e Evidências

Dados reais não devem ser usados em testes, prints ou evidências públicas.

Requisitos:

- não usar senhas reais;
- não usar documentos reais;
- não usar fotos privadas reais;
- não usar backup real;
- não usar nomes reais sensíveis;
- sanitizar screenshots;
- usar exemplos fictícios.

Critérios de aceite:

- repositório não contém dados reais;
- evidências públicas são fictícias ou sanitizadas.

## RP-017 — Privacidade por Permissões

Permissões Android devem ser mínimas.

Requisitos:

- `INTERNET` proibida;
- localização proibida;
- contatos proibidos;
- SMS proibido;
- microfone proibido;
- câmera proibida na v1 inicial;
- notificações proibidas na v1 inicial;
- storage amplo deve ser evitado.

Critérios de aceite:

- manifesto segue política de permissões;
- release checklist valida permissões.

## RP-018 — Privacidade por Arquitetura

Privacidade deve ser garantida por desenho, não por promessa.

Requisitos:

- ausência de backend;
- ausência de internet;
- ausência de conta;
- ausência de telemetria;
- criptografia local;
- storage privado;
- backup local criptografado;
- logs sanitizados.

Critérios de aceite:

- arquitetura implementada corresponde ao SDD;
- fluxos v1 não possuem saída remota.

## RP-019 — Retenção Local

O app deve evitar retenção desnecessária.

Requisitos:

- não manter plaintext persistente;
- não manter cache aberto;
- limitar auditoria;
- limpar temporários;
- limpar clipboard quando possível;
- remover arquivos associados ao excluir item.

Critérios de aceite:

- exclusão remove dados associados;
- temporários não ficam abandonados;
- auditoria tem política de retenção.

## RP-020 — Exclusão Local

O usuário deve poder excluir itens locais.

Requisitos:

- excluir nota;
- excluir senha;
- excluir documento;
- excluir foto;
- excluir arquivo associado;
- excluir metadados associados;
- registrar auditoria sanitizada.

Critérios de aceite:

- item excluído desaparece;
- arquivo associado não fica órfão óbvio;
- app não promete destruição forense absoluta.

## RP-021 — Linguagem Clara

Textos de privacidade devem ser compreensíveis.

Requisitos:

- evitar promessas absolutas;
- explicar ausência de recuperação;
- explicar backup local;
- explicar ausência de nuvem;
- explicar responsabilidade do usuário;
- usar linguagem direta.

Critérios de aceite:

- usuário entende limitações principais;
- textos não escondem riscos.

## RP-022 — Sem Monetização por Dados

A v1 não deve monetizar dados do usuário.

Requisitos:

- sem ads;
- sem tracking;
- sem venda de dados;
- sem analytics comercial;
- sem SDK publicitário;
- sem perfilamento.

Critérios de aceite:

- dependências não incluem ads/tracking;
- política de privacidade não promete algo diferente do app.

## RP-023 — Requisitos para Política de Privacidade Pública

A política de privacidade pública deve refletir o comportamento real da v1.

Deve declarar:

- dados ficam localmente;
- não há conta;
- não há backend na v1;
- não há analytics remoto;
- não há crash reporting remoto;
- não há recuperação de senha;
- backup local é responsabilidade do usuário;
- limitações de segurança.

Critérios de aceite:

- política pública não contradiz o SDD;
- política não promete sincronização ou recuperação.

## RP-024 — Mudanças Futuras de Privacidade

Qualquer mudança que altere privacidade exige revisão.

Mudanças críticas:

- adicionar internet;
- adicionar backend;
- adicionar sync;
- adicionar analytics;
- adicionar crash remoto;
- adicionar login;
- adicionar cloud backup;
- adicionar compartilhamento;
- adicionar autofill;
- adicionar câmera;
- adicionar notificações.

Critérios de aceite:

- mudança futura atualiza threat model, requisitos e política pública;
- mudança não entra silenciosamente.

## Critérios de Aceite do Documento

Este documento será considerado aceito quando:

- dados locais estiverem definidos;
- ausência de conta estiver definida;
- ausência de telemetria estiver definida;
- minimização de dados estiver definida;
- backup local estiver definido;
- logs e auditoria estiverem tratados;
- permissões estiverem alinhadas;
- política pública futura estiver orientada;
- documento estiver alinhado com data classification, security requirements e regras de negócio.

## Status do Documento

- **Documento:** `docs/SDD/02-requisitos/04-requisitos-de-privacidade.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Requisitos Pré-Implementação
