# Vaultia – Fluxo Local-First

## Objetivo do Documento

Este documento define o fluxo local-first e offline-only do Vaultia Android v1.

O objetivo é garantir que todo dado sensível seja criado, processado, criptografado, armazenado, usado, exportado e removido localmente, sem dependência de backend, nuvem, API remota ou permissão de internet.

## Princípio Central

No Vaultia v1, o dispositivo do usuário é o único ambiente operacional do cofre.

O app não deve depender de rede para criar, desbloquear, editar, proteger, exportar ou restaurar o cofre.

## Definição de Local-First

Local-first significa que:

- os dados nascem localmente;
- os dados são processados localmente;
- os dados são criptografados localmente;
- os dados são armazenados localmente;
- os dados são restaurados localmente;
- o usuário mantém controle direto sobre backup;
- o app funciona sem internet.

## Definição de Offline-Only

Offline-only significa que:

- não existe backend na v1;
- não existe API remota na v1;
- não existe sincronização na v1;
- não existe conta remota na v1;
- não existe telemetria remota na v1;
- não existe analytics remoto na v1;
- não existe crash reporting remoto na v1;
- não existe permissão INTERNET na v1.

## Fluxo Macro

Fluxo geral da v1:

1. usuário instala o app;
2. app inicia sem conexão remota;
3. usuário cria cofre local;
4. app gera material criptográfico local;
5. app armazena dados criptografados localmente;
6. usuário desbloqueia cofre localmente;
7. usuário cria, edita e remove itens;
8. app protege dados em repouso;
9. usuário exporta backup local criptografado, se desejar;
10. usuário importa backup local criptografado, se necessário.

Nenhuma etapa exige servidor.

## Primeiro Uso

Fluxo conceitual:

1. app abre em estado `NotInitialized`;
2. app exibe explicação de cofre local;
3. app informa que não existe recuperação de senha;
4. usuário confirma entendimento;
5. usuário cria Master Password;
6. app valida força mínima;
7. app gera salt;
8. app deriva material por KDF;
9. app gera Vault Key;
10. app protege Vault Key;
11. app cria metadados criptográficos versionados;
12. app registra auditoria local sanitizada;
13. app entra em estado `Unlocked`.

Regras:

- não consultar servidor;
- não criar conta;
- não pedir e-mail;
- não pedir telefone;
- não pedir login social;
- não pedir permissão INTERNET;
- não permitir avançar sem aviso de ausência de recuperação.

## Criação de Item

Fluxo conceitual:

1. cofre precisa estar desbloqueado;
2. usuário escolhe tipo de item;
3. usuário informa dados;
4. UI envia intenção para Application Layer;
5. Application Layer valida pré-condições;
6. Security Layer criptografa conteúdo;
7. Data Layer persiste ciphertext;
8. auditoria local registra evento sanitizado;
9. UI atualiza estado.

Tipos de item v1:

- nota segura;
- senha;
- foto;
- documento.

Regras:

- item não deve ser salvo em plaintext;
- metadados sensíveis devem ser protegidos;
- logs não podem conter conteúdo;
- operação não deve depender de rede.

## Leitura de Item

Fluxo conceitual:

1. usuário solicita abrir item;
2. app verifica estado `Unlocked`;
3. Data Layer recupera ciphertext;
4. Security Layer valida integridade;
5. Security Layer descriptografa conteúdo;
6. Application Layer entrega dados para UI;
7. UI exibe conteúdo em tela protegida.

Regras:

- conteúdo só aparece com cofre desbloqueado;
- erro criptográfico deve ser genérico;
- conteúdo não deve ir para logs;
- conteúdo não deve aparecer no app switcher;
- conteúdo não deve persistir em cache aberto.

## Edição de Item

Fluxo conceitual:

1. usuário edita item existente;
2. app valida sessão desbloqueada;
3. app aplica alterações em memória;
4. Security Layer criptografa nova versão;
5. Data Layer substitui registro criptografado;
6. auditoria registra evento sanitizado.

Regras:

- edição não deve deixar cópia antiga em plaintext;
- erro não deve corromper item existente;
- auditoria não deve registrar título, nota, URL, senha ou nome real de arquivo.

## Exclusão de Item

Fluxo conceitual:

1. usuário solicita exclusão;
2. app pede confirmação;
3. app remove registro local;
4. app remove arquivo criptografado associado, quando existir;
5. app registra auditoria sanitizada.

Regras:

- exclusão deve ser local;
- exclusão não depende de servidor;
- app não promete destruição forense absoluta;
- app deve evitar órfãos de arquivos criptografados;
- confirmação deve reduzir exclusão acidental.

## Bloqueio do Cofre

Fluxo conceitual:

1. usuário bloqueia manualmente, ou;
2. app vai para background, ou;
3. app atinge timeout de inatividade;
4. UI oculta conteúdo sensível;
5. sessão local é encerrada;
6. referências sensíveis são descartadas quando possível;
7. app volta ao estado `Locked`.

Regras:

- bloquear não remove dados;
- bloquear não exige internet;
- bloquear não deve corromper storage;
- conteúdo sensível não deve aparecer no app switcher;
- próximo acesso exige Master Password ou biometria habilitada.

## Desbloqueio do Cofre

Fluxo conceitual:

1. usuário informa Master Password ou usa biometria habilitada;
2. app tenta desbloquear material criptográfico;
3. app valida integridade;
4. app abre sessão local;
5. app registra auditoria sanitizada;
6. app entra em estado `Unlocked`.

Regras:

- senha errada falha genericamente;
- dados corrompidos falham genericamente;
- biometria não substitui recuperação por Master Password;
- desbloqueio não consulta servidor;
- erro não deve vazar detalhes internos.

## Backup Local Criptografado

Fluxo conceitual de exportação:

1. usuário solicita backup;
2. app exige cofre desbloqueado;
3. app exibe aviso de responsabilidade;
4. app coleta registros criptografados ou payload preparado;
5. app gera pacote versionado;
6. app protege pacote com criptografia autenticada;
7. usuário escolhe onde salvar;
8. app registra auditoria sanitizada.

Regras:

- backup nunca deve ser aberto;
- backup não deve ser JSON, CSV ou TXT plaintext;
- backup deve ter integridade verificável;
- backup deve ter versão;
- exportação exige ação explícita;
- app não envia backup para nuvem.

## Importação de Backup

Fluxo conceitual:

1. usuário seleciona arquivo de backup;
2. app valida formato;
3. app solicita senha necessária;
4. app tenta verificar integridade;
5. app rejeita senha errada;
6. app rejeita arquivo corrompido;
7. app exibe confirmação antes de substituir dados;
8. app restaura dados localmente;
9. app registra auditoria sanitizada.

Regras:

- importação não consulta servidor;
- erro deve ser seguro;
- backup inválido não deve afetar cofre existente;
- sobrescrita exige confirmação explícita.

## Biometria Local

Fluxo conceitual:

1. usuário cria cofre com Master Password;
2. usuário opta por ativar biometria;
3. app configura desbloqueio local com BiometricPrompt e Keystore quando aplicável;
4. acesso biométrico desbloqueia material local protegido;
5. Master Password continua sendo fallback.

Regras:

- biometria é opcional;
- biometria não recupera cofre perdido;
- alteração biométrica do dispositivo deve ser tratada com segurança quando possível;
- ações críticas podem exigir Master Password.

## Clipboard Local

Fluxo conceitual:

1. usuário solicita copiar senha;
2. app confirma ação;
3. app copia para clipboard;
4. app agenda limpeza;
5. app limpa clipboard quando possível;
6. auditoria pode registrar evento sanitizado.

Regras:

- clipboard só por ação explícita;
- conteúdo copiado não vai para logs;
- clipboard não deve ser usado automaticamente;
- usuário deve ser informado de que cópia é temporária.

## Auditoria Local

Fluxo conceitual:

1. evento de segurança ocorre;
2. Application Layer gera evento sanitizado;
3. auditoria local salva evento sem conteúdo sensível;
4. usuário pode visualizar histórico básico.

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

Regras:

- auditoria não sai do dispositivo;
- auditoria não contém conteúdo do cofre;
- auditoria não contém título, URL, senha, nota ou nome real de arquivo;
- retenção deve ser limitada.

## Erros Locais

Fluxo de erro:

1. erro ocorre;
2. erro interno é capturado;
3. app converte para mensagem segura;
4. logger registra apenas evento sanitizado;
5. UI mostra mensagem genérica.

Exemplos de mensagens aceitáveis:

- "Não foi possível abrir o cofre."
- "Senha incorreta ou dados inválidos."
- "Backup inválido ou corrompido."
- "Não foi possível concluir a operação."

Regras:

- não mostrar stack trace;
- não mostrar chave;
- não mostrar caminho sensível;
- não mostrar plaintext;
- não mostrar metadado sensível.

## Ausência de Fluxos Remotos

Na v1 não existem os seguintes fluxos:

- cadastro remoto;
- login remoto;
- recuperação de senha;
- sincronização cloud;
- upload de backup;
- download de backup;
- telemetry;
- analytics;
- crash reporting remoto;
- push notification;
- verificação remota de licença;
- feature flag remota;
- API externa.

## Estados Locais

Estados principais:

- `NotInitialized`;
- `Locked`;
- `Unlocking`;
- `Unlocked`;
- `BackgroundLocked`;
- `BackupExporting`;
- `BackupImporting`;
- `ErrorSafe`.

Regras:

- `Unlocked` é o único estado que permite exibir Nível 3;
- `Locked` não mostra conteúdo;
- `BackgroundLocked` não mostra conteúdo;
- `ErrorSafe` não mostra detalhe sensível;
- transições devem ser explícitas e testáveis.

## Transições Permitidas

Transições esperadas:

- `NotInitialized` → `Unlocked`, após criação do cofre;
- `Locked` → `Unlocking`, após tentativa de desbloqueio;
- `Unlocking` → `Unlocked`, se sucesso;
- `Unlocking` → `Locked`, se falha;
- `Unlocked` → `Locked`, por ação manual;
- `Unlocked` → `BackgroundLocked`, ao background;
- `BackgroundLocked` → `Locked`, ao retornar;
- qualquer estado → `ErrorSafe`, diante de erro crítico controlado.

## Restrições de Implementação

A implementação deve respeitar:

- sem permissão INTERNET;
- sem backend;
- sem cloud;
- sem SDK remoto;
- sem plaintext persistente;
- sem cache aberto;
- sem logs sensíveis;
- sem backup automático Android;
- sem dependência de rede para fluxos essenciais.

## Critérios de Aceite

Este documento será considerado aceito quando:

- fluxo de primeiro uso estiver definido;
- fluxo de criação de item estiver definido;
- fluxo de leitura estiver definido;
- fluxo de edição estiver definido;
- fluxo de exclusão estiver definido;
- fluxo de bloqueio estiver definido;
- fluxo de desbloqueio estiver definido;
- fluxo de backup estiver definido;
- fluxo de importação estiver definido;
- ausência de fluxos remotos estiver explícita;
- estados locais estiverem definidos;
- documento estiver alinhado com visão arquitetural, tecnologias e permissões Android.

## Status do Documento

- **Documento:** `docs/SDD/03-arquitetura/04-fluxo-local-first.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Arquitetura Detalhada Pré-Implementação
