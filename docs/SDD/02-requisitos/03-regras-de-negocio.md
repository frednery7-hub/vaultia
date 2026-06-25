# Vaultia – Regras de Negócio

## Objetivo do Documento

Este documento define as regras de negócio do Vaultia Android v1.

Regras de negócio determinam comportamentos obrigatórios do produto, independentemente da implementação técnica.

## Princípio Central

O Vaultia é um cofre local de alta responsabilidade.

O app deve priorizar proteção, clareza e previsibilidade acima de conveniência excessiva.

## RN-001 — Cofre Local Único na v1

Na v1, o app deve operar com um cofre local principal por instalação.

Regras:

- não há múltiplas contas;
- não há múltiplos usuários;
- não há login remoto;
- não há perfil em nuvem;
- não há sincronização entre dispositivos;
- o cofre pertence à instalação local do app.

Critérios de aceite:

- app não solicita cadastro;
- app não solicita e-mail;
- app não cria conta;
- app não consulta servidor.

## RN-002 — Criação Obrigatória do Cofre

O usuário só pode usar funcionalidades internas após criar um cofre.

Regras:

- primeiro uso deve levar ao fluxo de criação;
- notas, senhas, arquivos, backup e configurações sensíveis exigem cofre existente;
- app não deve permitir pular criação do cofre;
- app deve informar ausência de recuperação antes da criação.

Critérios de aceite:

- sem cofre, usuário não acessa área interna;
- criação exige Master Password;
- aviso de perda de senha é exibido.

## RN-003 — Master Password Obrigatória

A Master Password é obrigatória para criação e recuperação de acesso ao cofre.

Regras:

- não há cofre sem Master Password;
- não há senha padrão;
- não há senha gerada pelo app sem usuário conhecer;
- não há recuperação remota;
- não há reset por suporte;
- não há pergunta secreta.

Critérios de aceite:

- app bloqueia criação sem senha válida;
- app comunica risco de perda;
- app não oferece recuperação falsa.

## RN-004 — Confirmação de Ausência de Recuperação

Antes de criar o cofre, o usuário deve confirmar que entende a ausência de recuperação.

Regras:

- confirmação deve ser explícita;
- texto deve ser claro;
- não pode ficar escondido em termos longos;
- usuário deve entender que perder a senha pode tornar o cofre inacessível.

Critérios de aceite:

- fluxo de criação contém aviso;
- usuário confirma antes de prosseguir;
- documentação e UI não prometem recuperação.

## RN-005 — Senha Fraca

O app deve lidar com senha fraca de forma segura.

Regras:

- app deve medir ou validar força mínima;
- senha extremamente fraca deve ser rejeitada ou gerar bloqueio conforme política final;
- senha fraca aceitável apenas com aviso forte se a política permitir;
- o app não deve fingir que KDF resolve senha ruim.

Critérios de aceite:

- senha trivial é tratada;
- usuário recebe feedback claro;
- política fica documentada.

## RN-006 — Cofre Bloqueado por Padrão

O cofre deve iniciar bloqueado quando já existir cofre local.

Regras:

- ao abrir app com cofre existente, exibir tela de desbloqueio;
- não exibir conteúdo antes de autenticação;
- sessão anterior não deve restaurar plaintext automaticamente;
- app deve ocultar dados ao background.

Critérios de aceite:

- reiniciar app exige desbloqueio;
- app switcher não mostra conteúdo sensível;
- conteúdo não aparece antes de autenticação.

## RN-007 — Acesso a Dados Nível 3

Dados Nível 3 só podem ser exibidos com cofre desbloqueado.

Regras:

- notas privadas exigem estado `Unlocked`;
- senhas exigem estado `Unlocked`;
- fotos e documentos exigem estado `Unlocked`;
- backup exige estado `Unlocked`;
- configurações críticas exigem estado `Unlocked`.

Critérios de aceite:

- com cofre bloqueado, dados sensíveis não aparecem;
- tentativa inválida redireciona para desbloqueio.

## RN-008 — Bloqueio por Background

Quando o app vai para background, o conteúdo sensível deve ser ocultado.

Regras:

- app deve ocultar conteúdo imediatamente ou em política definida;
- app switcher não deve exibir dados do cofre;
- retorno ao app deve exigir desbloqueio ou revalidação conforme política;
- comportamento deve ser previsível.

Critérios de aceite:

- dados não aparecem no app switcher;
- retorno ao app não expõe conteúdo indevidamente.

## RN-009 — Bloqueio por Inatividade

O app deve possuir política de bloqueio por inatividade.

Regras:

- usuário pode configurar tempo, se a feature estiver habilitada;
- tempo padrão deve ser conservador;
- ações do usuário renovam sessão;
- expiração bloqueia cofre;
- operação crítica pode exigir reautenticação.

Critérios de aceite:

- cofre bloqueia após tempo configurado;
- app não mantém sessão indefinidamente sem política.

## RN-010 — Biometria é Opcional

Biometria não é requisito obrigatório para usar o app.

Regras:

- usuário pode usar app apenas com Master Password;
- biometria só pode ser ativada por escolha explícita;
- usuário pode desativar biometria;
- app deve continuar funcionando se biometria não existir no aparelho;
- app deve continuar funcionando se biometria falhar.

Critérios de aceite:

- fluxo sem biometria funciona;
- biometria não bloqueia usuário legítimo que possui Master Password.

## RN-011 — Biometria Não Recupera Cofre

Biometria não pode ser apresentada como recuperação de cofre perdido.

Regras:

- se Master Password for perdida, biometria não deve ser comunicada como solução garantida;
- ações críticas podem exigir Master Password;
- alteração biométrica do dispositivo pode invalidar acesso biométrico;
- app deve comunicar limites de biometria.

Critérios de aceite:

- UI não promete recuperação via biometria;
- documentação declara biometria como conveniência.

## RN-012 — Notas Seguras

O app deve permitir gerenciar notas seguras na v1.

Regras:

- criar nota;
- visualizar nota;
- editar nota;
- excluir nota;
- exigir cofre desbloqueado;
- proteger título e conteúdo;
- registrar auditoria sanitizada.

Critérios de aceite:

- CRUD de notas funciona offline;
- nota não aparece em plaintext fora do app;
- auditoria não contém conteúdo.

## RN-013 — Senhas

O app deve permitir gerenciar itens de senha na v1.

Regras:

- criar item;
- visualizar item;
- editar item;
- excluir item;
- copiar senha por ação explícita;
- proteger todos os campos sensíveis;
- registrar auditoria sanitizada.

Critérios de aceite:

- CRUD de senhas funciona offline;
- senha não aparece em plaintext persistente;
- clipboard é temporário conforme política.

## RN-014 — Fotos e Documentos

O app deve permitir importar, visualizar e excluir fotos/documentos.

Regras:

- importar por seletor seguro do sistema;
- criptografar antes de persistir;
- armazenar no storage privado;
- não salvar cópia permanente na galeria por ação própria;
- excluir registro e arquivo associado;
- registrar auditoria sanitizada.

Critérios de aceite:

- arquivo importado é acessível pelo app;
- arquivo não é legível fora do app;
- exclusão remove associação.

## RN-015 — Backup Local Criptografado

O app deve permitir exportar backup local criptografado.

Regras:

- backup exige cofre desbloqueado;
- backup exige confirmação;
- backup deve ser criptografado;
- backup deve ter integridade verificável;
- backup deve ser versionado;
- backup não deve ser plaintext;
- usuário escolhe onde salvar.

Critérios de aceite:

- backup exportado não é legível;
- exportação funciona sem internet;
- app exibe aviso de responsabilidade.

## RN-016 — Importação de Backup

O app deve permitir importar backup local criptografado.

Regras:

- usuário seleciona arquivo;
- app valida formato;
- app solicita senha correta;
- app rejeita senha incorreta;
- app rejeita backup corrompido;
- app confirma antes de substituir cofre existente;
- restauração ocorre localmente.

Critérios de aceite:

- backup válido restaura dados;
- senha errada falha;
- arquivo corrompido falha;
- cofre existente não é sobrescrito sem confirmação.

## RN-017 — Sem Sincronização

A v1 não deve sincronizar dados.

Regras:

- não há sync cloud;
- não há sync entre aparelhos;
- não há servidor intermediário;
- não há upload automático;
- não há download automático;
- backup é responsabilidade explícita do usuário.

Critérios de aceite:

- app não possui fluxo de sync;
- app não possui permissão INTERNET;
- UI não promete sincronização.

## RN-018 — Sem Recuperação de Senha

A v1 não deve oferecer recuperação de senha.

Regras:

- não há e-mail de recuperação;
- não há telefone de recuperação;
- não há suporte capaz de desbloquear;
- não há chave guardada pelo fornecedor;
- não há pergunta secreta;
- não há reset remoto.

Critérios de aceite:

- UI não exibe "esqueci minha senha" como recuperação real;
- documentação declara limitação.

## RN-019 — Exportação Plaintext Fora da v1

A v1 não deve exportar dados do cofre em plaintext.

Regras:

- não exportar CSV aberto;
- não exportar JSON aberto;
- não exportar TXT aberto;
- não exportar ZIP aberto;
- backup deve ser criptografado.

Critérios de aceite:

- nenhum fluxo v1 gera backup aberto;
- release checklist bloqueia exportação plaintext.

## RN-020 — Auditoria Local Sanitizada

O app deve registrar eventos locais de segurança sem conteúdo sensível.

Regras:

- auditoria fica no dispositivo;
- auditoria não sai pela internet;
- auditoria não contém nota;
- auditoria não contém senha;
- auditoria não contém URL;
- auditoria não contém título;
- auditoria não contém nome real de arquivo;
- auditoria possui retenção limitada.

Critérios de aceite:

- auditoria mostra eventos úteis;
- auditoria não vaza dados Nível 3.

## RN-021 — Logs Técnicos Sanitizados

Logs técnicos não podem conter dados sensíveis.

Regras:

- logger central;
- sem `println`;
- sem `printStackTrace`;
- sem `Log.d` livre;
- sem plaintext;
- sem ciphertext;
- sem senha;
- sem chave;
- sem URL salva;
- sem título;
- sem nome real de arquivo.

Critérios de aceite:

- revisão de Logcat não encontra dados sensíveis.

## RN-022 — Permissões Mínimas

O app deve usar permissões mínimas.

Regras:

- `INTERNET` proibida;
- localização proibida;
- câmera proibida na v1 inicial;
- notificações proibidas na v1 inicial;
- contatos proibidos;
- SMS proibido;
- microfone proibido;
- biometria permitida somente se implementada;
- storage amplo deve ser evitado.

Critérios de aceite:

- manifesto respeita documento de permissões;
- release checklist valida permissões.

## RN-023 — Ações Destrutivas Exigem Confirmação

Ações destrutivas devem exigir confirmação explícita.

Ações:

- excluir nota;
- excluir senha;
- excluir arquivo;
- importar backup substituindo cofre existente;
- limpar cofre, se feature existir futuramente;
- desativar recurso crítico, quando aplicável.

Critérios de aceite:

- usuário não perde dados por toque acidental simples;
- confirmação é clara.

## RN-024 — Erros Seguros

Erros devem ser tratados de forma segura.

Regras:

- não expor stack trace;
- não expor path sensível;
- não expor chave;
- não expor detalhe criptográfico;
- não exibir conteúdo parcial;
- mensagens devem ser genéricas e compreensíveis.

Critérios de aceite:

- falha de senha, backup ou storage não vaza dados.

## RN-025 — Informação Honesta ao Usuário

O app deve comunicar limites reais.

Regras:

- não prometer segurança absoluta;
- não prometer recuperação inexistente;
- não prometer proteção contra aparelho comprometido;
- não prometer proteção contra coerção;
- explicar que backup e senha são responsabilidade do usuário.

Critérios de aceite:

- telas de segurança são honestas;
- textos não vendem garantias falsas.

## RN-026 — Dados de Teste

Dados reais do usuário não podem ser usados como teste.

Regras:

- não versionar backup real;
- não usar senha real;
- não usar documento real;
- não usar foto privada;
- não usar screenshots com dados reais;
- exemplos devem ser fictícios.

Critérios de aceite:

- repositório não contém dados reais;
- evidências públicas são sanitizadas.

## RN-027 — Release Controlado

Toda versão deve passar por checklist.

Regras:

- release exige checklist;
- release exige hash SHA-256;
- release exige teste em aparelho real;
- release exige revisão de permissões;
- release exige revisão de logs;
- release exige revisão de storage;
- release exige revisão de backup.

Critérios de aceite:

- nenhum APK/AAB é considerado aprovado sem checklist.

## Critérios de Aceite do Documento

Este documento será considerado aceito quando:

- regras de criação e acesso ao cofre estiverem definidas;
- regras de senha-mestra estiverem definidas;
- regras de bloqueio estiverem definidas;
- regras de notas, senhas e arquivos estiverem definidas;
- regras de backup estiverem definidas;
- regras de auditoria e logs estiverem definidas;
- regras de permissões estiverem definidas;
- restrições de negócio estiverem claras;
- documento estiver alinhado com requisitos funcionais e não funcionais.

## Status do Documento

- **Documento:** `docs/SDD/02-requisitos/03-regras-de-negocio.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Requisitos Pré-Implementação
