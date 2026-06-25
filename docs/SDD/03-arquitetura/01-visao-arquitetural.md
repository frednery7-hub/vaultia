# Vaultia – Visão Arquitetural

## Objetivo do Documento

Este documento define a visão arquitetural do Vaultia Android v1.

O objetivo é estabelecer a estrutura macro do aplicativo, seus limites, camadas, responsabilidades e restrições técnicas obrigatórias.

Este documento não implementa código. Ele define a arquitetura que a implementação deverá seguir.

## Princípio Arquitetural Central

O Vaultia Android v1 é um aplicativo local-first e offline-only.

A arquitetura deve proteger dados sensíveis no próprio dispositivo, sem backend, sem nuvem, sem sincronização remota e sem permissão de internet.

## Decisões Arquiteturais Fundamentais

A v1 adota as seguintes decisões:

- aplicativo Android nativo;
- Kotlin como linguagem principal;
- Jetpack Compose para UI;
- armazenamento local privado;
- criptografia em repouso;
- Master Password como fator principal;
- biometria opcional como conveniência;
- backup local criptografado;
- ausência de backend;
- ausência de API remota;
- ausência de analytics remoto;
- ausência de crash reporting remoto;
- ausência de permissão INTERNET.

## Estilo Arquitetural

O Vaultia deve seguir uma arquitetura modular em camadas.

Camadas principais:

1. Presentation Layer;
2. Application Layer;
3. Domain Layer;
4. Data Layer;
5. Security Layer;
6. Platform Layer.

A dependência deve fluir de fora para dentro.

A regra principal é: UI não deve conter lógica criptográfica, lógica de storage ou regras de segurança sensíveis.

## Diagrama Conceitual

Fluxo macro:

Usuário
→ UI Jetpack Compose
→ ViewModel / Application Services
→ Use Cases
→ Repositories
→ Crypto Services
→ Storage Local Privado
→ Android Platform Services

Não existe fluxo para internet na v1.

## Presentation Layer

A Presentation Layer é responsável pela interface do usuário.

Responsabilidades:

- renderizar telas;
- receber ações do usuário;
- exibir estados;
- solicitar desbloqueio do cofre;
- exibir avisos de segurança;
- bloquear exposição visual de dados sensíveis;
- não executar criptografia diretamente;
- não acessar storage diretamente.

Componentes esperados:

- telas Compose;
- componentes visuais;
- ViewModels;
- estados de UI;
- navegação local.

Regras:

- não armazenar dados sensíveis em estado persistente de UI;
- evitar preview sensível;
- proteger telas sensíveis;
- não logar conteúdo exibido;
- não expor Nível 3 em notificações ou widgets.

## Application Layer

A Application Layer coordena casos de uso.

Responsabilidades:

- orquestrar criação do cofre;
- orquestrar desbloqueio;
- orquestrar bloqueio;
- criar notas;
- editar notas;
- excluir notas;
- criar itens de senha;
- editar itens de senha;
- importar arquivos;
- exportar backup;
- importar backup;
- ativar ou desativar biometria;
- registrar auditoria local sanitizada.

Regras:

- não implementar primitiva criptográfica diretamente;
- chamar serviços de segurança dedicados;
- validar pré-condições;
- tratar erros de forma segura;
- não vazar detalhe interno para UI.

## Domain Layer

A Domain Layer contém regras de negócio puras.

Responsabilidades:

- representar entidades do cofre;
- definir regras de criação de itens;
- definir estados do cofre;
- definir políticas de sessão;
- definir contratos de repositório;
- definir erros de domínio;
- manter regras independentes da plataforma quando possível.

Entidades conceituais:

- Vault;
- SecureNote;
- PasswordItem;
- SecureFile;
- VaultBackup;
- AuditEvent;
- SecuritySettings;
- SessionState.

Regras:

- não depender diretamente de Android;
- não depender diretamente de bibliotecas de UI;
- não acessar filesystem;
- não acessar banco diretamente;
- não conter segredo hardcoded.

## Data Layer

A Data Layer é responsável por persistência local.

Responsabilidades:

- salvar metadados protegidos;
- salvar registros criptografados;
- salvar arquivos criptografados;
- recuperar registros criptografados;
- remover dados com segurança razoável;
- versionar schema;
- executar migrações locais;
- entregar dados apenas mediante fluxo autorizado.

Opções técnicas a decidir na implementação:

- Room com criptografia por campo/registro;
- SQLCipher;
- abordagem híbrida: banco para metadados criptografados + arquivos encrypted no storage privado.

Regras:

- não salvar dados Nível 3 em texto claro;
- não criar índice plaintext para dados sensíveis;
- não salvar cache descriptografado;
- não usar storage público para conteúdo do cofre;
- não depender de backup automático do Android.

## Security Layer

A Security Layer concentra os mecanismos sensíveis.

Responsabilidades:

- KDF;
- geração de salt;
- geração de nonce ou IV;
- geração e proteção da Vault Key;
- criptografia AEAD;
- descriptografia AEAD;
- integração com Android Keystore;
- validação de integridade;
- wrapping e unwrapping de chave;
- limpeza de sessão;
- proteção de backup;
- validação de formato criptográfico.

Regras:

- não inventar criptografia;
- não usar chave hardcoded;
- não logar segredo;
- não expor chave para UI;
- não aceitar dados corrompidos;
- não descriptografar fora de sessão autorizada;
- não misturar regra visual com regra criptográfica.

## Platform Layer

A Platform Layer encapsula APIs específicas do Android.

Responsabilidades:

- Android Keystore;
- BiometricPrompt;
- Encrypted storage quando aplicável;
- filesystem privado;
- clipboard;
- proteção de tela;
- ciclo de vida do app;
- detecção de background;
- controle de permissões;
- integração com build Android.

Regras:

- isolar APIs Android atrás de interfaces;
- permitir testes com abstrações;
- não espalhar chamadas sensíveis pela UI;
- manter comportamento seguro em background;
- manter manifesto mínimo.

## Fluxo de Criação do Cofre

Fluxo conceitual:

1. usuário abre o app pela primeira vez;
2. app apresenta aviso de ausência de recuperação;
3. usuário cria Master Password;
4. app valida força mínima;
5. app gera salt;
6. app deriva material com KDF;
7. app gera Vault Key;
8. app protege Vault Key;
9. app cria metadados criptográficos versionados;
10. app cria estado inicial do cofre;
11. app registra auditoria local sanitizada.

Regras:

- não criar cofre sem confirmação do risco;
- não salvar Master Password;
- não salvar Vault Key aberta;
- não exigir internet;
- não registrar dados sensíveis.

## Fluxo de Desbloqueio

Fluxo conceitual:

1. usuário informa Master Password ou usa biometria já habilitada;
2. app tenta desbloquear material criptográfico;
3. app valida integridade;
4. app abre sessão local;
5. app permite acesso aos dados;
6. app registra auditoria sanitizada.

Regras:

- senha errada deve falhar genericamente;
- dados corrompidos devem falhar genericamente;
- erro não deve vazar detalhe criptográfico;
- sessão deve expirar por política definida;
- app deve bloquear ao background.

## Fluxo de Bloqueio

Fluxo conceitual:

1. app detecta inatividade, background ou ação manual;
2. app oculta conteúdo sensível;
3. app descarta referências temporárias quando possível;
4. app retorna ao estado bloqueado;
5. app registra auditoria sanitizada.

Regras:

- bloquear sem depender de rede;
- não persistir plaintext;
- não mostrar preview sensível;
- não destruir dados do cofre por bloqueio normal.

## Fluxo de Item Seguro

Fluxo conceitual para nota, senha, foto ou documento:

1. usuário cria ou importa item;
2. app valida entrada;
3. Application Layer chama Security Layer;
4. Security Layer criptografa conteúdo;
5. Data Layer persiste ciphertext e metadados protegidos;
6. auditoria local registra evento sanitizado.

Regras:

- item não deve ser salvo em plaintext;
- título e metadados sensíveis devem receber proteção;
- logs não podem conter conteúdo;
- UI não deve reter cópias desnecessárias.

## Fluxo de Backup Local

Fluxo conceitual:

1. usuário solicita exportação;
2. app exige cofre desbloqueado;
3. app exibe aviso de responsabilidade;
4. app gera pacote versionado;
5. app criptografa payload;
6. app autentica integridade;
7. app permite salvar arquivo exportado;
8. app registra auditoria sanitizada.

Regras:

- backup nunca aberto;
- backup nunca automático em nuvem pela v1;
- backup corrompido deve ser rejeitado;
- senha errada deve falhar;
- importação deve proteger contra sobrescrita acidental.

## Estado do Cofre

Estados principais:

- NotInitialized;
- Locked;
- Unlocking;
- Unlocked;
- BackgroundLocked;
- ErrorSafe;
- BackupExporting;
- BackupImporting.

Regras:

- dados Nível 3 só podem ser exibidos em Unlocked;
- BackgroundLocked não pode exibir conteúdo;
- ErrorSafe não pode vazar dados;
- transições devem ser explícitas.

## Ausência de Backend

A v1 não possui backend.

Consequências:

- não há login remoto;
- não há conta;
- não há JWT;
- não há refresh token;
- não há API REST;
- não há GraphQL;
- não há sincronização;
- não há painel web;
- não há recuperação remota;
- não há telemetria remota;
- não há Terraform obrigatório para operação da v1.

## Ausência de Internet

A v1 não deve declarar permissão INTERNET.

Consequências:

- dependências devem ser revisadas;
- SDKs remotos são proibidos;
- analytics remoto é proibido;
- crash remoto é proibido;
- feature flag remota é proibida;
- atualização de conteúdo remoto é proibida.

## Ferramentas de Engenharia

Ferramentas permitidas, desde que não violem offline-only:

- VS Code;
- Android Studio;
- Docker para tooling local;
- Python para scripts internos;
- GitHub Actions para CI;
- Terraform apenas para infraestrutura pública futura, sem dados do cofre;
- Postman apenas para contratos futuros, não para v1 offline-only.

Regras:

- ferramentas não podem receber dados reais do cofre;
- CI não pode armazenar secrets indevidos;
- screenshots não podem conter dados reais;
- scripts devem usar dados fictícios.

## Limites Arquiteturais

São limites obrigatórios:

- UI não acessa banco diretamente;
- UI não executa criptografia diretamente;
- Domain não depende de Android;
- Security Layer não depende de UI;
- Data Layer não decide política de segurança;
- Platform Layer não contém regra de negócio;
- logs passam por logger central;
- permissões passam por revisão.

## Critérios de Aceite

Este documento será considerado aceito quando:

- camadas principais estiverem definidas;
- responsabilidades estiverem separadas;
- ausência de backend estiver explícita;
- ausência de internet estiver explícita;
- fluxo de criação do cofre estiver definido;
- fluxo de desbloqueio estiver definido;
- fluxo de bloqueio estiver definido;
- fluxo de backup estiver definido;
- limites arquiteturais estiverem definidos;
- documento estiver alinhado com security requirements.

## Status do Documento

- **Documento:** `docs/SDD/03-arquitetura/01-visao-arquitetural.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Fundação e SDD Essencial
