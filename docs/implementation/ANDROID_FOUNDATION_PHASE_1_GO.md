# Vaultia – Android Foundation Phase 1 GO

## Decisão

A criação da fundação Android está autorizada.

## Data

2026-06-24

## Escopo Permitido

A fase permite apenas:

- criação do projeto Android;
- Kotlin;
- Jetpack Compose;
- package inicial;
- Manifest mínimo;
- ausência de permissão INTERNET;
- tela inicial neutra;
- estrutura base de diretórios;
- validações de build;
- auditoria inicial de permissões.

## Escopo Bloqueado nesta Fase

Ainda não está permitido implementar:

- cofre real;
- Master Password;
- KDF;
- AES-GCM;
- banco local;
- storage seguro;
- backup;
- biometria;
- clipboard;
- importação de arquivos;
- exportação;
- logs técnicos definitivos;
- auditoria local definitiva.

## Bases Aprovadas

Documentos críticos aprovados antes do GO:

- requisitos funcionais;
- requisitos não funcionais;
- regras de negócio;
- requisitos de privacidade;
- visão arquitetural;
- componentes e camadas;
- tecnologias;
- fluxo local-first;
- permissões Android;
- threat model;
- security requirements;
- data classification;
- crypto architecture;
- key management;
- secure storage;
- logging policy;
- release checklist.

## Condição de Segurança

A fundação Android deve preservar a decisão central:

- sem backend;
- sem cloud;
- sem conta;
- sem analytics;
- sem crash remoto;
- sem permissão INTERNET;
- sem dados reais;
- sem secrets;
- sem plaintext sensível.

## Critério de Aprovação da Fase

A fase só será considerada válida se:

- o projeto compilar;
- o Manifest não declarar INTERNET;
- não houver dependência que injete INTERNET;
- não houver código sensível;
- não houver secrets;
- não houver dados reais;
- a estrutura estiver alinhada ao SDD.

## Status

- **Status:** GO aprovado para Fundação Android Fase 1
- **Próximo passo:** criação do projeto Android no Android Studio
