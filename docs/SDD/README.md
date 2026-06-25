# Vaultia – Software Design Document (SDD)

## Visão Geral

Bem-vindo ao Software Design Document (SDD) do **Vaultia**.

Este diretório é a fonte única de verdade (*Single Source of Truth*) para todas as decisões arquiteturais, de segurança, de produto e operacionais do projeto.

Nenhum código é escrito, nenhuma dependência é adicionada e nenhuma release é feita sem que a decisão correspondente esteja refletida e aprovada nesta documentação.

O Vaultia é um aplicativo móvel Android projetado para atuar como um cofre pessoal para armazenamento local de dados sensíveis, incluindo senhas, notas privadas, documentos e fotos.

O compromisso primário do produto é com a privacidade, a integridade e a proteção dos dados do usuário.

## Princípios Fundamentais da v1

Qualquer decisão técnica ou de produto deve ser avaliada contra estes três pilares:

### 1. Offline-First / Offline-Only na v1

O aplicativo não possui backend próprio, não sincroniza dados na nuvem e não solicita a permissão `INTERNET` no `AndroidManifest.xml`.

### 2. Security by Design

Segurança não é uma feature adicional; é a fundação do produto.

Se uma funcionalidade de conveniência enfraquece a criptografia, o modelo de ameaça, o armazenamento seguro ou a privacidade do usuário, a funcionalidade é descartada ou adiada.

### 3. Simplicidade Operacional

Ferramentas de engenharia como Docker, Terraform, Postman e CI/CD existem para impor portões de qualidade, segurança e rastreabilidade.

Elas não devem introduzir complexidade desnecessária, tráfego indevido, backend prematuro ou exposição de dados sensíveis.

## Navegação do SDD

A documentação está dividida em blocos lógicos. Cada arquivo responde a perguntas específicas e diretas.

### `/01-visao-geral`

Define o que é o produto, o escopo da v1, as decisões principais e o que explicitamente não será feito.

### `/02-requisitos`

Reúne requisitos funcionais, requisitos não funcionais, regras de negócio e requisitos de privacidade.

### `/03-arquitetura`

Descreve a visão arquitetural em alto nível, fluxo local-first, tecnologias aprovadas e permissões do sistema operacional.

### `/04-design-detalhado`

Detalha modelo de dados local, módulos internos, interfaces, contratos, fluxos e estados do aplicativo.

### `/05-seguranca`

É o núcleo crítico do projeto.

Contém modelagem de ameaças, requisitos de segurança, classificação de dados, arquitetura criptográfica, gerenciamento de chaves, autenticação, armazenamento seguro e política de logs.

### `/06-testes`

Define estratégia de testes, casos de teste, testes de segurança, testes de release e critérios de aceitação.

### `/07-operacional`

Documenta ferramentas aprovadas, uso de Docker, Postman, Terraform, CI/CD, checklist de release, backup, recuperação e distribuição.

### `/08-anexos`

Contém exemplos, glossário, referências externas e decisões em aberto.

## Regras de Manutenção

### Objetividade

Os documentos devem ser curtos, focados e rastreáveis. Evite blocos monolíticos de texto.

### Atualização Contínua

Se um Pull Request altera arquitetura, modelo de dados, criptografia, permissões, dependências, fluxo de autenticação ou política operacional, o PR deve incluir a atualização correspondente no SDD.

### Revisão de Segurança

Qualquer alteração na pasta `/05-seguranca` exige revisão obrigatória do responsável técnico de segurança do projeto.

### Sem Código Antes da Decisão

Funcionalidades que afetem dados sensíveis, criptografia, autenticação, backup, armazenamento ou permissões do Android não podem ser implementadas antes de estarem documentadas e aprovadas no SDD.

## Roadmap de Inicialização

A engenharia só será iniciada após a aprovação dos seguintes documentos, nesta ordem:

- [x] 1. `README.md`
- [x] 2. `01-visao-geral/03-product-decisions.md`
- [x] 3. `01-visao-geral/04-escopo-v1.md`
- [x] 4. `01-visao-geral/05-fora-de-escopo.md`
- [x] 5. `05-seguranca/01-threat-model.md`
- [x] 6. `05-seguranca/03-data-classification.md`
- [x] 7. `05-seguranca/04-crypto-architecture.md`
- [x] 8. `05-seguranca/02-security-requirements.md`
- [x] 9. `03-arquitetura/01-visao-arquitetural.md`
- [x] 10. `07-operacional/06-release-checklist.md`

## Núcleos de Entrega

O Vaultia será desenvolvido por núcleos auditáveis.

Cada núcleo precisa ser implementado, testado e auditado antes do próximo começar.

1. Fundação e SDD Essencial
2. Cofre Mínimo
3. Criptografia e Armazenamento
4. Nota Segura
5. Hardening Mobile Básico
6. Senhas
7. Biometria
8. Backup Local Criptografado
9. Fotos e Documentos
10. Auditoria Local e Logs
11. CI/CD e Release Candidate
12. Operação e Publicação

## Status do Documento

- **Documento:** `docs/SDD/README.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Fundação e SDD Essencial
