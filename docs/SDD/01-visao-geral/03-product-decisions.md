# Vaultia – Product Decisions

## Objetivo do Documento

Este documento registra as decisões centrais de produto para o Vaultia Android v1.

Ele define o que o produto é, quais princípios são inegociáveis e quais escolhas ficam proibidas nesta versão.

## Decisão Principal

O Vaultia v1 será um aplicativo Android de cofre digital pessoal, local-first e offline-only, voltado para armazenamento seguro de dados sensíveis no próprio dispositivo do usuário.

O app não terá backend, não terá sincronização em nuvem e não solicitará permissão `INTERNET` na v1.

## Plataforma da v1

- Plataforma inicial: Android
- Linguagem principal: Kotlin
- UI: Jetpack Compose
- Modelo de dados: local
- Backend: não existe na v1
- Nuvem: não existe na v1
- Internet permission: proibida na v1

## Produto

O Vaultia será um cofre local para proteger:

- notas privadas;
- senhas;
- documentos;
- fotos privadas;
- dados sensíveis gerais.

A primeira entidade funcional do cofre será a **nota segura**.

Senhas, backup, biometria, fotos e documentos serão implementados em núcleos posteriores.

## Promessa do Produto

O Vaultia v1 promete:

- armazenamento local de dados sensíveis;
- criptografia em repouso;
- senha-mestra local;
- bloqueio automático;
- proteção contra exposição básica em tela;
- ausência de backend;
- ausência de sincronização;
- ausência de coleta remota de dados.

## Promessas Proibidas

O Vaultia não deve prometer:

- segurança absoluta;
- proteção impossível de quebrar;
- recuperação garantida de dados;
- proteção total em dispositivo comprometido;
- proteção total contra coerção física;
- proteção total em aparelho com malware/root;
- criptografia “militar” como argumento de marketing;
- anonimato absoluto;
- invulnerabilidade.

## Decisões Iniciais da v1

- O app será offline-only.
- O app não terá backend.
- O app não terá login remoto.
- O app não terá conta de usuário.
- O app não terá OAuth2.
- O app não terá JWT.
- O app não terá GraphQL.
- O app não terá sincronização em nuvem.
- O app não terá analytics remoto.
- O app não terá crash reporting remoto.
- O app não terá permissão `INTERNET`.
- O app não terá recuperação de senha-mestra.
- O app não terá backup automático em nuvem.
- O app não terá compartilhamento entre usuários.
- O app não terá wipe automático após erros na v1.

## Segurança por Design

Toda funcionalidade deve ser avaliada contra:

- impacto na criptografia;
- impacto no modelo de ameaça;
- impacto no armazenamento seguro;
- impacto na privacidade;
- impacto na superfície de ataque;
- impacto na simplicidade operacional.

Se uma funcionalidade aumentar risco sem necessidade, ela deve ser rejeitada ou adiada.

## Senha-Mestra

A senha-mestra será local.

Regras:

- não será armazenada em texto claro;
- não será transmitida;
- não será enviada para servidor;
- não será recuperável;
- será exigida para criar e desbloquear o cofre;
- poderá ser exigida novamente em ações críticas.

Se o usuário perder a senha-mestra, poderá perder acesso aos dados do cofre.

Essa limitação deve ser comunicada no onboarding.

## Biometria

A biometria será opcional.

A biometria será mecanismo de conveniência, não substituto absoluto da senha-mestra.

A senha-mestra continua sendo a raiz de acesso ao cofre.

## Criptografia

O Vaultia v1 deve usar criptografia autenticada para dados sensíveis.

Decisões:

- AES-256-GCM é o algoritmo preferencial.
- Algoritmos próprios são proibidos.
- AES-ECB é proibido.
- AES-CBC sem autenticação é proibido.
- Base64 não é criptografia.
- Nonce/IV deve ser único quando aplicável.
- O formato criptográfico deve ser versionado.
- A arquitetura criptográfica será detalhada em documento próprio.

## Armazenamento

Todo dado sensível deve ser armazenado localmente de forma protegida.

Regras:

- conteúdo do cofre não pode ficar em texto claro;
- metadados sensíveis devem ser tratados como dados restritos;
- arquivos privados devem ser armazenados no storage privado do app;
- storage público deve ser evitado;
- backup automático do Android deve ser desabilitado;
- fotos/documentos não devem permanecer expostos na galeria por decisão do app.

## Logs e Auditoria

O Vaultia v1 não terá logs remotos.

Regras:

- dados sensíveis não podem ser logados;
- senha-mestra não pode ser logada;
- chaves criptográficas não podem ser logadas;
- conteúdo do cofre não pode ser logado;
- logs técnicos devem ser sanitizados;
- auditoria local deve ser mínima e sem conteúdo sensível.

## Ferramentas de Engenharia

Ferramentas aprovadas:

- VS Code;
- Android Studio;
- Docker;
- Python;
- Postman;
- Terraform;
- GitHub Actions.

Restrições:

- Docker não manipula dados reais do cofre.
- Postman só será usado para contratos futuros.
- Terraform só será usado para infraestrutura pública sem dados sensíveis.
- CI/CD será usado como gate de qualidade e segurança.
- Python será usado para ferramentas internas, não como base do app Android.

## Núcleos de Entrega

O desenvolvimento será dividido por núcleos auditáveis:

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

Cada núcleo deve ser auditado antes do próximo começar.

## Critério de Mudança

Qualquer mudança nestas decisões exige atualização deste documento e revisão explícita.

Decisões sobre internet, backend, nuvem, recuperação de senha, criptografia, permissões Android e armazenamento são consideradas decisões críticas.

## Status do Documento

- **Documento:** `docs/SDD/01-visao-geral/03-product-decisions.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Fundação e SDD Essencial
