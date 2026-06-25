# Vaultia – Requisitos Funcionais

## Objetivo do Documento

Este documento define os requisitos funcionais do Vaultia Android v1.

Requisitos funcionais descrevem o que o aplicativo deve fazer do ponto de vista do usuário e do produto.

## Princípio Central

A v1 deve entregar um cofre local funcional, seguro e simples.

Nenhuma funcionalidade pode depender de backend, internet, conta remota ou sincronização em nuvem.

## RF-001 — Primeiro Uso

O app deve apresentar um fluxo inicial para criação do cofre local.

Requisitos:

- exibir tela de boas-vindas;
- explicar que o cofre é local;
- explicar que não existe recuperação de senha;
- solicitar confirmação explícita do usuário;
- permitir criação da Master Password;
- solicitar confirmação da Master Password;
- validar força mínima da senha;
- criar o cofre local;
- iniciar sessão desbloqueada após criação bem-sucedida.

Critérios de aceite:

- usuário consegue criar cofre sem internet;
- app não solicita e-mail;
- app não solicita telefone;
- app não cria conta remota;
- app informa claramente o risco de perda da senha.

## RF-002 — Desbloquear Cofre

O app deve permitir desbloquear o cofre local.

Requisitos:

- solicitar Master Password;
- validar a senha localmente;
- desbloquear dados se a senha estiver correta;
- rejeitar senha incorreta;
- exibir erro seguro;
- registrar auditoria local sanitizada.

Critérios de aceite:

- senha correta desbloqueia;
- senha errada não desbloqueia;
- erro não revela detalhe criptográfico;
- desbloqueio funciona sem internet.

## RF-003 — Bloquear Cofre

O app deve permitir bloquear o cofre.

Requisitos:

- permitir bloqueio manual;
- bloquear ao ir para background, conforme política;
- bloquear por inatividade, conforme política;
- ocultar conteúdo sensível;
- encerrar sessão local;
- registrar auditoria local sanitizada.

Critérios de aceite:

- conteúdo some ao bloquear;
- app não mostra conteúdo no app switcher;
- reabertura exige novo desbloqueio.

## RF-004 — Criar Nota Segura

O app deve permitir criar nota segura.

Requisitos:

- cofre precisa estar desbloqueado;
- usuário informa título;
- usuário informa conteúdo;
- app salva nota de forma protegida;
- app lista a nota criada;
- app registra auditoria sanitizada.

Critérios de aceite:

- nota criada aparece na lista;
- nota não fica em plaintext no storage;
- título e conteúdo não aparecem em logs.

## RF-005 — Visualizar Nota Segura

O app deve permitir visualizar nota segura.

Requisitos:

- cofre precisa estar desbloqueado;
- usuário seleciona uma nota;
- app abre detalhe da nota;
- conteúdo aparece apenas em tela protegida;
- erro de leitura deve ser seguro.

Critérios de aceite:

- nota correta é exibida;
- nota não aparece com cofre bloqueado;
- screenshot é bloqueado em tela sensível, quando política aplicada.

## RF-006 — Editar Nota Segura

O app deve permitir editar nota segura.

Requisitos:

- cofre precisa estar desbloqueado;
- usuário altera título e/ou conteúdo;
- app salva nova versão protegida;
- app mantém integridade do item;
- app registra auditoria sanitizada.

Critérios de aceite:

- alteração persiste após fechar e abrir o app;
- conteúdo antigo não aparece em plaintext;
- erro não corrompe item existente.

## RF-007 — Excluir Nota Segura

O app deve permitir excluir nota segura.

Requisitos:

- cofre precisa estar desbloqueado;
- usuário solicita exclusão;
- app pede confirmação;
- app remove a nota;
- app registra auditoria sanitizada.

Critérios de aceite:

- nota excluída desaparece da lista;
- exclusão não depende de servidor;
- auditoria não contém título nem conteúdo.

## RF-008 — Criar Item de Senha

O app deve permitir criar item de senha.

Campos v1:

- título;
- usuário ou e-mail;
- senha;
- URL opcional;
- observações opcionais.

Requisitos:

- cofre precisa estar desbloqueado;
- senha deve ser protegida;
- campos sensíveis devem ser tratados como Nível 3;
- app salva item localmente;
- app registra auditoria sanitizada.

Critérios de aceite:

- item aparece na lista;
- senha não fica em plaintext;
- URL e usuário não aparecem em logs.

## RF-009 — Visualizar Item de Senha

O app deve permitir visualizar item de senha.

Requisitos:

- cofre precisa estar desbloqueado;
- usuário seleciona item;
- app exibe dados do item;
- senha pode estar oculta por padrão;
- usuário pode revelar senha por ação explícita.

Critérios de aceite:

- senha não aparece sem ação do usuário, se essa política for adotada;
- dados não aparecem com cofre bloqueado;
- tela sensível é protegida.

## RF-010 — Copiar Senha

O app deve permitir copiar senha para clipboard.

Requisitos:

- cofre precisa estar desbloqueado;
- cópia exige ação explícita;
- app informa que a cópia é temporária;
- app limpa clipboard após tempo definido, quando possível;
- conteúdo copiado não deve ser logado.

Critérios de aceite:

- senha é copiada manualmente;
- clipboard é limpo conforme política;
- logs não contêm senha.

## RF-011 — Editar Item de Senha

O app deve permitir editar item de senha.

Requisitos:

- cofre precisa estar desbloqueado;
- usuário altera campos;
- app salva nova versão protegida;
- app registra auditoria sanitizada.

Critérios de aceite:

- alteração persiste;
- campos sensíveis não aparecem em plaintext;
- auditoria não contém conteúdo.

## RF-012 — Excluir Item de Senha

O app deve permitir excluir item de senha.

Requisitos:

- cofre precisa estar desbloqueado;
- usuário solicita exclusão;
- app pede confirmação;
- app remove o item;
- app registra auditoria sanitizada.

Critérios de aceite:

- item excluído desaparece da lista;
- exclusão não depende de servidor;
- auditoria não contém título, URL, usuário ou senha.

## RF-013 — Importar Foto ou Documento

O app deve permitir importar foto ou documento para o cofre.

Requisitos:

- cofre precisa estar desbloqueado;
- usuário seleciona arquivo por mecanismo seguro do sistema;
- app valida arquivo;
- app criptografa antes de persistir;
- app armazena em storage privado;
- app registra auditoria sanitizada.

Critérios de aceite:

- arquivo é importado;
- arquivo não fica legível fora do app;
- nome real do arquivo não é exposto sem necessidade;
- app não salva cópia permanente na galeria por ação própria.

## RF-014 — Visualizar Foto ou Documento

O app deve permitir visualizar foto ou documento importado.

Requisitos:

- cofre precisa estar desbloqueado;
- usuário seleciona arquivo;
- app valida integridade;
- app exibe conteúdo em tela protegida;
- temporários devem ser evitados ou limpos.

Critérios de aceite:

- arquivo correto é exibido;
- conteúdo não aparece com cofre bloqueado;
- app não mantém cópia aberta persistente.

## RF-015 — Excluir Foto ou Documento

O app deve permitir excluir foto ou documento.

Requisitos:

- cofre precisa estar desbloqueado;
- usuário solicita exclusão;
- app pede confirmação;
- app remove registro;
- app remove arquivo criptografado associado;
- app registra auditoria sanitizada.

Critérios de aceite:

- arquivo excluído desaparece;
- não ficam órfãos óbvios;
- auditoria não contém nome real do arquivo.

## RF-016 — Backup Local Criptografado

O app deve permitir exportar backup local criptografado.

Requisitos:

- cofre precisa estar desbloqueado;
- usuário solicita backup;
- app exibe aviso de responsabilidade;
- app gera pacote versionado;
- app criptografa backup;
- app garante integridade verificável;
- usuário escolhe local de destino;
- app registra auditoria sanitizada.

Critérios de aceite:

- backup é gerado;
- backup não contém plaintext;
- backup não é JSON, CSV ou TXT aberto;
- exportação funciona sem internet.

## RF-017 — Importar Backup Local

O app deve permitir importar backup local criptografado.

Requisitos:

- usuário seleciona arquivo de backup;
- app valida formato;
- app solicita senha necessária;
- app rejeita senha incorreta;
- app rejeita backup corrompido;
- app confirma antes de substituir cofre existente;
- app restaura dados localmente;
- app registra auditoria sanitizada.

Critérios de aceite:

- backup válido restaura dados;
- senha errada falha;
- backup corrompido falha;
- restauração não depende de internet.

## RF-018 — Configurar Auto-Lock

O app deve permitir configurar bloqueio automático.

Requisitos:

- permitir escolher tempo de inatividade;
- salvar preferência localmente;
- aplicar política durante uso;
- bloquear cofre ao atingir tempo configurado.

Critérios de aceite:

- configuração persiste;
- cofre bloqueia após tempo definido;
- configuração não expõe conteúdo sensível.

## RF-019 — Biometria Opcional

O app deve permitir ativar biometria, se implementada na v1.

Requisitos:

- biometria só pode ser ativada após criação do cofre;
- usuário deve optar explicitamente;
- Master Password permanece fallback;
- usuário pode desativar biometria;
- ações críticas podem exigir Master Password.

Critérios de aceite:

- app funciona sem biometria;
- biometria não recupera cofre sem senha;
- desativação funciona.

## RF-020 — Auditoria Local

O app deve permitir visualizar eventos locais mínimos.

Eventos permitidos:

- cofre criado;
- cofre desbloqueado;
- falha de desbloqueio;
- cofre bloqueado;
- item criado;
- item editado;
- item excluído;
- backup exportado;
- backup importado;
- biometria ativada;
- biometria desativada.

Requisitos:

- auditoria é local;
- auditoria é sanitizada;
- auditoria não contém conteúdo do cofre;
- auditoria possui retenção limitada.

Critérios de aceite:

- usuário vê eventos básicos;
- eventos não contêm dados Nível 3;
- eventos não saem do dispositivo.

## RF-021 — Configurações Locais

O app deve possuir tela de configurações locais.

Configurações esperadas:

- alterar Master Password;
- auto-lock;
- biometria;
- exportar backup;
- importar backup;
- visualizar auditoria local;
- informações de segurança;
- versão do app.

Critérios de aceite:

- configurações funcionam offline;
- nenhuma configuração exige conta;
- nenhuma configuração exige internet.

## RF-022 — Alterar Master Password

O app deve permitir alterar Master Password.

Requisitos:

- exigir senha atual;
- exigir nova senha;
- confirmar nova senha;
- validar força mínima;
- reproteger Vault Key;
- registrar auditoria sanitizada;
- falha não deve corromper cofre.

Critérios de aceite:

- senha antiga deixa de desbloquear;
- senha nova desbloqueia;
- dados permanecem acessíveis;
- erro não corrompe cofre.

## RF-023 — Estado Seguro de Erro

O app deve possuir estado seguro de erro.

Requisitos:

- exibir mensagem genérica;
- não expor stack trace;
- não expor path sensível;
- não expor detalhe criptográfico;
- não exibir conteúdo parcial;
- permitir retorno seguro ao estado bloqueado quando possível.

Critérios de aceite:

- erros não vazam dados sensíveis;
- app não fica exibindo conteúdo após falha.

## RF-024 — Informações de Segurança

O app deve exibir informações básicas de segurança ao usuário.

Conteúdo esperado:

- cofre local;
- ausência de recuperação;
- backup local criptografado;
- biometria como conveniência;
- responsabilidade sobre senha e backup;
- ausência de nuvem na v1.

Critérios de aceite:

- usuário consegue entender as limitações;
- app não promete segurança absoluta;
- app não promete recuperação inexistente.

## RF-025 — Funcionamento Sem Internet

O app deve funcionar sem internet em todos os fluxos v1.

Fluxos obrigatórios offline:

- criar cofre;
- desbloquear;
- criar nota;
- editar nota;
- excluir nota;
- criar senha;
- editar senha;
- excluir senha;
- importar arquivo;
- visualizar arquivo;
- exportar backup;
- importar backup;
- configurar auto-lock;
- usar biometria local, quando habilitada.

Critérios de aceite:

- app não possui permissão INTERNET;
- app funciona em modo avião;
- nenhum fluxo v1 tenta conexão remota.

## Funcionalidades Fora da v1

Fora da v1:

- login remoto;
- cadastro remoto;
- sincronização;
- nuvem;
- backend;
- recuperação de senha;
- compartilhamento entre usuários;
- extensão de navegador;
- autofill;
- OCR;
- IA;
- captura direta por câmera;
- notificações;
- analytics;
- crash reporting remoto.

## Critérios de Aceite do Documento

Este documento será considerado aceito quando:

- requisitos funcionais principais estiverem definidos;
- fluxos de cofre estiverem cobertos;
- notas estiverem cobertas;
- senhas estiverem cobertas;
- arquivos estiverem cobertos;
- backup estiver coberto;
- biometria estiver delimitada;
- auditoria local estiver definida;
- funcionamento offline estiver explícito;
- documento estiver alinhado com escopo v1, arquitetura e security requirements.

## Status do Documento

- **Documento:** `docs/SDD/02-requisitos/01-requisitos-funcionais.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Requisitos Pré-Implementação
