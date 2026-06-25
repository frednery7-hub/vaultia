# Vaultia – Componentes e Camadas

## Objetivo do Documento

Este documento detalha os principais componentes arquiteturais do Vaultia Android v1 e define a separação de responsabilidades entre camadas.

Ele complementa a visão arquitetural e serve como guia direto para a implementação.

## Princípio Central

Cada camada deve ter responsabilidade clara.

Nenhum componente deve misturar UI, criptografia, storage, regra de negócio e API Android sensível ao mesmo tempo.

## Camadas Principais

O Vaultia Android v1 será organizado nas seguintes camadas:

1. Presentation Layer
2. Application Layer
3. Domain Layer
4. Data Layer
5. Security Layer
6. Platform Layer

## Presentation Layer

Responsável pela interface visual e interação com o usuário.

Componentes esperados:

- Compose Screens;
- Compose Components;
- ViewModels;
- UI State;
- Navigation;
- Dialogs de segurança;
- telas de bloqueio e desbloqueio;
- telas de criação e edição de itens;
- telas de backup;
- telas de configuração.

Responsabilidades:

- renderizar estado;
- capturar ações do usuário;
- chamar ViewModels;
- exibir erros seguros;
- proteger telas sensíveis;
- evitar preview de conteúdo sensível.

Proibido:

- acessar banco diretamente;
- executar criptografia;
- manipular chaves;
- acessar filesystem diretamente;
- usar Android Keystore diretamente;
- logar conteúdo sensível.

## Application Layer

Responsável por orquestrar casos de uso.

Componentes esperados:

- CreateVaultUseCase;
- UnlockVaultUseCase;
- LockVaultUseCase;
- CreateSecureNoteUseCase;
- UpdateSecureNoteUseCase;
- DeleteSecureNoteUseCase;
- CreatePasswordItemUseCase;
- UpdatePasswordItemUseCase;
- DeletePasswordItemUseCase;
- ImportSecureFileUseCase;
- DeleteSecureFileUseCase;
- ExportBackupUseCase;
- ImportBackupUseCase;
- EnableBiometricUseCase;
- DisableBiometricUseCase;
- RecordAuditEventUseCase.

Responsabilidades:

- coordenar fluxos;
- validar pré-condições;
- chamar serviços de domínio;
- chamar serviços de segurança;
- chamar repositórios;
- converter erros internos em erros seguros;
- registrar auditoria sanitizada.

Proibido:

- implementar algoritmo criptográfico;
- acessar API Android diretamente;
- salvar plaintext;
- expor detalhes internos para UI.

## Domain Layer

Responsável por regras de negócio puras.

Componentes esperados:

- Vault;
- VaultState;
- SessionState;
- SecureNote;
- PasswordItem;
- SecureFile;
- VaultBackup;
- SecuritySettings;
- AuditEvent;
- DomainError;
- Repository interfaces.

Responsabilidades:

- representar entidades;
- definir contratos;
- validar regras independentes da plataforma;
- definir estados permitidos;
- impedir transições inválidas;
- manter regras de negócio testáveis.

Proibido:

- depender de Android;
- depender de Compose;
- depender de Room diretamente;
- depender de filesystem;
- acessar Keystore;
- conter segredo hardcoded.

## Data Layer

Responsável pela persistência local.

Componentes esperados:

- VaultRepositoryImpl;
- SecureNoteRepositoryImpl;
- PasswordRepositoryImpl;
- SecureFileRepositoryImpl;
- BackupRepositoryImpl;
- AuditRepositoryImpl;
- LocalDatabase;
- DAO ou equivalente;
- FileStorageDataSource;
- MigrationManager.

Responsabilidades:

- persistir ciphertext;
- recuperar ciphertext;
- persistir metadados protegidos;
- persistir arquivos criptografados;
- executar migrações;
- versionar schema;
- remover registros;
- fornecer dados apenas por contratos.

Proibido:

- armazenar dados Nível 3 em plaintext;
- criar índice plaintext de conteúdo sensível;
- manter cache descriptografado persistente;
- usar storage público para conteúdo privado;
- decidir política criptográfica.

## Security Layer

Responsável por operações sensíveis de segurança.

Componentes esperados:

- CryptoService;
- KdfService;
- KeyManager;
- VaultKeyManager;
- BackupCryptoService;
- PasswordPolicyService;
- SecureClipboardService;
- SessionSecurityManager;
- CryptoMetadataValidator.

Responsabilidades:

- derivar material criptográfico;
- gerar salt;
- gerar nonce ou IV;
- gerar Vault Key;
- proteger Vault Key;
- criptografar dados;
- descriptografar dados;
- validar integridade;
- proteger backup;
- rejeitar dados inválidos;
- limpar sessão quando possível.

Proibido:

- renderizar UI;
- acessar componentes Compose;
- registrar segredo em logs;
- criar algoritmo próprio;
- aceitar dados corrompidos;
- expor chaves fora dos contratos necessários.

## Platform Layer

Responsável por encapsular APIs Android.

Componentes esperados:

- AndroidKeystoreProvider;
- BiometricPromptAdapter;
- SecureWindowManager;
- AppLifecycleObserver;
- PrivateFileStorage;
- ClipboardAdapter;
- ManifestPermissionPolicy;
- DeviceSecurityInfoProvider.

Responsabilidades:

- interagir com Android Keystore;
- interagir com biometria;
- aplicar FLAG_SECURE;
- observar background/foreground;
- acessar storage privado;
- controlar clipboard;
- expor informações de plataforma de forma segura.

Proibido:

- conter regra de negócio;
- decidir escopo de produto;
- armazenar conteúdo sensível sem Security Layer;
- expor APIs Android diretamente para UI.

## Logger Central

O app deve possuir um logger central sanitizado.

Responsabilidades:

- registrar eventos técnicos permitidos;
- impedir logs de conteúdo sensível;
- bloquear padrões proibidos;
- separar erro técnico de conteúdo do usuário.

Proibido:

- logar senha;
- logar nota;
- logar URL salva;
- logar título;
- logar nome real de arquivo;
- logar chaves;
- logar ciphertext;
- logar plaintext.

## Auditoria Local

A auditoria local é diferente de log técnico.

Responsabilidades:

- registrar eventos de segurança compreensíveis ao usuário;
- não registrar conteúdo sensível;
- não sair do dispositivo;
- ter retenção limitada.

Eventos esperados:

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

## Dependências Entre Camadas

Regra de dependência:

- Presentation pode depender de Application;
- Application pode depender de Domain;
- Application pode depender de interfaces de Security e Data;
- Domain não depende de camadas externas;
- Data implementa interfaces do Domain;
- Security implementa contratos usados pela Application;
- Platform implementa abstrações usadas por Security e Application.

Fluxo proibido:

- UI → Data diretamente;
- UI → Keystore diretamente;
- UI → Crypto diretamente;
- Domain → Android;
- Domain → Room;
- Data → Compose;
- Security → Compose.

## Módulos Conceituais

A implementação pode ser organizada em módulos ou pacotes conceituais:

- app;
- feature-vault;
- feature-notes;
- feature-passwords;
- feature-files;
- feature-backup;
- core-domain;
- core-data;
- core-security;
- core-platform;
- core-ui;
- core-logging.

A divisão física final será definida na criação do projeto Android.

## Estados Compartilhados

Estados globais permitidos devem ser mínimos.

Estados esperados:

- estado de sessão;
- estado do cofre;
- configuração de segurança;
- estado de biometria;
- estado de operação de backup.

Proibido:

- estado global com plaintext persistente;
- singleton com Vault Key exposta;
- cache global descriptografado;
- estado de UI contendo segredo além do necessário.

## Critérios de Aceite

Este documento será considerado aceito quando:

- todas as camadas estiverem descritas;
- os principais componentes estiverem listados;
- responsabilidades estiverem claras;
- proibições por camada estiverem explícitas;
- dependências permitidas estiverem definidas;
- fluxos proibidos estiverem definidos;
- logger e auditoria local estiverem separados;
- documento estiver alinhado com a visão arquitetural e security requirements.

## Status do Documento

- **Documento:** `docs/SDD/03-arquitetura/02-componentes-camadas.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Arquitetura Detalhada Pré-Implementação
