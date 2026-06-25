# Vaultia – Fora de Escopo da v1

## Objetivo do Documento

Este documento define explicitamente o que está fora do escopo do Vaultia Android v1.

O objetivo é evitar aumento descontrolado de escopo, reduzir superfície de ataque e impedir que funcionalidades prematuras comprometam segurança, privacidade ou simplicidade operacional.

## Princípio de Corte

A v1 do Vaultia deve ser pequena, local, segura e auditável.

Qualquer funcionalidade que exija backend, nuvem, tráfego de rede, identidade remota, recuperação remota, sincronização ou processamento externo fica fora da v1.

## Fora de Escopo Absoluto na v1

As funcionalidades abaixo não serão implementadas na v1:

- backend próprio;
- API remota;
- sincronização em nuvem;
- login remoto;
- conta de usuário;
- OAuth2;
- JWT;
- refresh token;
- login com Google;
- login com Apple;
- login com e-mail;
- login por telefone;
- GraphQL;
- WebSocket;
- notificações push;
- analytics remoto;
- crash reporting remoto;
- telemetria remota;
- backup automático em nuvem;
- recuperação de senha-mestra por e-mail;
- recuperação de senha-mestra por SMS;
- recuperação de senha-mestra por suporte humano;
- compartilhamento de senhas;
- compartilhamento de documentos;
- cofre familiar;
- cofre empresarial;
- painel web;
- extensão de navegador;
- autofill Android;
- passkeys;
- 2FA remoto;
- Google Authenticator;
- SMS 2FA;
- e-mail 2FA;
- integração com IA;
- leitura automática de conteúdo por IA;
- OCR automático de documentos;
- upload de dados sensíveis;
- processamento de dados sensíveis fora do dispositivo.

## Rede e Internet

Fica fora da v1:

- permissão `INTERNET`;
- chamadas HTTP;
- chamadas HTTPS a partir do app;
- APIs externas;
- serviços cloud;
- sync remoto;
- download remoto de configuração;
- feature flags remotas;
- verificação remota de versão;
- envio remoto de logs;
- envio remoto de crash reports;
- envio remoto de métricas.

A ausência da permissão `INTERNET` é uma decisão de produto e de segurança.

Qualquer proposta para adicionar rede ao app exige nova revisão de arquitetura, threat model e product decisions.

## Conta, Login e Identidade Remota

Fica fora da v1:

- cadastro de usuário;
- login com senha;
- login social;
- conta Vaultia;
- autenticação remota;
- token de sessão;
- OAuth2;
- JWT;
- passkeys;
- login sem senha;
- recuperação de conta;
- troca de dispositivo via conta.

Na v1, autenticação significa desbloqueio local do cofre.

## Recuperação de Senha-Mestra

Fica fora da v1:

- recuperação por e-mail;
- recuperação por SMS;
- recuperação por suporte;
- recuperação por pergunta secreta;
- recuperação por documento de identidade;
- reset remoto;
- reset com conta;
- escrow de chave;
- chave de recuperação externa;
- frase de recuperação.

Se o usuário perder a senha-mestra, poderá perder acesso ao cofre.

Essa decisão deve ser comunicada de forma clara no onboarding.

## Sincronização e Multi-dispositivo

Fica fora da v1:

- sincronização entre dispositivos;
- sync cloud;
- sync local via rede;
- sync por QR code;
- sync por Bluetooth;
- sync por Wi-Fi Direct;
- conflito de versões entre dispositivos;
- merge de cofres;
- conta multi-dispositivo.

A v1 protege um cofre local em um dispositivo.

## Compartilhamento

Fica fora da v1:

- compartilhar senha com outro usuário;
- compartilhar pasta;
- compartilhar cofre;
- link público;
- link privado;
- convite;
- permissões por usuário;
- cofre familiar;
- cofre de equipe;
- organização empresarial.

Compartilhamento é funcionalidade de alto risco e depende de modelo criptográfico próprio.

## Exportação Aberta

Fica fora da v1:

- exportar JSON aberto;
- exportar CSV aberto;
- exportar TXT aberto;
- exportar senhas em texto claro;
- exportar notas em texto claro;
- exportar documentos descriptografados em lote;
- exportar fotos descriptografadas em lote.

A v1 só permite backup criptografado.

## Wipe Automático

Fica fora da v1:

- apagar cofre automaticamente após N erros;
- destruição automática de dados por tentativa falha;
- wipe remoto;
- wipe por comando externo;
- wipe por geolocalização;
- wipe por tempo sem uso.

Motivo: wipe automático pode ser usado como ataque de negação de serviço contra o usuário.

A v1 pode implementar bloqueio temporário e delay progressivo após falhas.

## Dispositivo Comprometido

A v1 não promete proteção total contra:

- sistema operacional comprometido;
- malware com privilégios elevados;
- root avançado;
- bootloader destravado;
- keylogger no dispositivo;
- screen reader malicioso;
- hooking;
- framework de instrumentação;
- atacante com controle físico prolongado e ferramentas forenses avançadas.

A v1 pode alertar sobre risco, mas não deve prometer proteção absoluta.

## Coerção Física

Fica fora da v1:

- modo pânico;
- cofre falso;
- senha de coerção;
- destruição por senha alternativa;
- alerta silencioso;
- notificação a terceiros;
- localização de emergência.

Essas funções podem aumentar risco real para o usuário se forem mal desenhadas.

## Cloud e Infraestrutura

Fica fora da v1 do app:

- banco de dados remoto;
- storage remoto de backups;
- API de sincronização;
- servidores de autenticação;
- funções serverless;
- logs remotos;
- dashboards de analytics;
- filas cloud;
- mensageria remota.

Terraform pode ser usado futuramente apenas para infraestrutura pública sem dados sensíveis, como site de privacidade e página de segurança.

## Postman e APIs

Fica fora da v1:

- API real;
- contrato obrigatório de sync;
- coleção com tokens reais;
- coleção com dados reais;
- testes com backup real;
- mocks usando conteúdo sensível.

Postman pode ser usado apenas para documentação futura e mocks sem dados reais.

## ISO 27001 e Governança Avançada

Fica fora do bloqueio inicial de implementação:

- certificação ISO 27001;
- auditoria formal externa;
- SoA completo;
- governança corporativa avançada;
- processo formal de offboarding;
- revisão complexa de IAM.

A v1 pode manter controles inspirados em ISO 27001 de forma leve, sem alegar certificação.

## Funcionalidades Mobile Fora da v1 Inicial

Fica fora da primeira etapa da v1:

- autofill Android;
- integração com teclado;
- extensão de navegador;
- câmera interna;
- scanner de documento;
- OCR;
- leitura automática de QR code;
- importação de grandes lotes;
- suporte a vídeo;
- suporte a áudio;
- edição avançada de imagem;
- organização avançada por pastas;
- tags complexas;
- busca avançada com índice em texto claro.

Algumas dessas funções podem ser reavaliadas em versões futuras.

## Permissões Android Fora da v1

Ficam fora da v1:

- `INTERNET`;
- localização;
- notificações;
- gravação de áudio;
- câmera;
- leitura ampla de armazenamento externo;
- escrita em armazenamento externo;
- contatos;
- calendário;
- SMS;
- telefone.

Qualquer nova permissão precisa de justificativa no SDD.

## Promessas Proibidas

O Vaultia não deve prometer:

- segurança absoluta;
- invulnerabilidade;
- proteção militar;
- recuperação garantida;
- proteção contra todo malware;
- proteção contra todo ataque forense;
- proteção contra coerção física;
- anonimato absoluto;
- substituição completa de boas práticas do usuário;
- garantia de proteção em aparelho comprometido.

## Critério para Trazer Algo de Volta ao Escopo

Uma funcionalidade fora de escopo só poderá ser reavaliada se houver:

- justificativa clara de produto;
- análise de risco;
- atualização do threat model;
- atualização da arquitetura;
- impacto de privacidade;
- impacto em permissões Android;
- impacto em criptografia;
- critérios de teste;
- aprovação explícita no SDD.

## Status do Documento

- **Documento:** `docs/SDD/01-visao-geral/05-fora-de-escopo.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Fundação e SDD Essencial
