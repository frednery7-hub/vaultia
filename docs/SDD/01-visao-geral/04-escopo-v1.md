# Vaultia – Escopo da v1

## Objetivo do Documento

Este documento define o escopo funcional e técnico da versão 1 do Vaultia Android.

O objetivo é impedir aumento descontrolado de escopo e garantir que a primeira versão seja pequena, segura, auditável e funcional.

## Princípio de Escopo

A v1 deve provar o núcleo do produto:

> Criar um cofre local, proteger dados sensíveis com senha-mestra, criptografar conteúdo em repouso e permitir uso básico sem backend, sem nuvem e sem permissão de internet.

Qualquer funcionalidade fora desse objetivo deve ser adiada.

## Escopo Principal da v1

A v1 será composta pelos seguintes núcleos funcionais:

1. Cofre mínimo
2. Criptografia e armazenamento local
3. Nota segura
4. Hardening mobile básico
5. Senhas
6. Biometria opcional
7. Backup local criptografado
8. Fotos e documentos
9. Auditoria local mínima
10. Release candidate controlado

## Núcleo 1 — Cofre Mínimo

Entra na v1:

- criação de cofre local;
- criação de senha-mestra;
- confirmação explícita de que a senha-mestra não pode ser recuperada;
- desbloqueio do cofre com senha-mestra;
- bloqueio manual do cofre;
- bloqueio automático ao app ir para background;
- estado local de cofre bloqueado/desbloqueado;
- tela inicial bloqueada;
- sessão local do cofre enquanto desbloqueado.

Critério mínimo:

- o usuário consegue criar um cofre;
- fechar o app não deixa o cofre aberto indevidamente;
- reabrir o app exige autenticação novamente quando aplicável.

## Núcleo 2 — Criptografia e Armazenamento Local

Entra na v1:

- criptografia autenticada para dados sensíveis;
- AES-256-GCM como algoritmo preferencial;
- KDF para derivação/proteção de chave;
- salt por cofre;
- nonce/IV único quando aplicável;
- Vault Key;
- versionamento do formato criptográfico;
- Android Keystore quando aplicável;
- armazenamento local protegido;
- teste de senha errada;
- teste de dados corrompidos;
- tratamento seguro de erro criptográfico.

Critério mínimo:

- dados salvos não ficam legíveis no armazenamento local;
- senha errada não desbloqueia o cofre;
- erro criptográfico não vaza conteúdo sensível.

## Núcleo 3 — Nota Segura

A primeira entidade funcional do Vaultia será a nota segura.

Entra na v1:

- criar nota segura;
- editar nota segura;
- excluir nota segura;
- listar notas;
- visualizar nota;
- criptografar conteúdo da nota;
- tratar título da nota como dado sensível;
- descriptografar nota apenas com cofre desbloqueado.

Critério mínimo:

- criar nota;
- fechar o app;
- reabrir;
- desbloquear;
- ler a nota corretamente;
- confirmar que a nota não está em texto claro no armazenamento.

## Núcleo 4 — Hardening Mobile Básico

Entra na v1:

- bloqueio de screenshot em telas sensíveis;
- proteção de conteúdo em tela recente;
- `android:allowBackup="false"`;
- `android:fullBackupContent="false"`;
- revisão de `dataExtractionRules`;
- ausência da permissão `INTERNET`;
- ausência de permissões desnecessárias;
- build release com `debuggable=false`;
- sanitização básica de logs;
- separação entre debug e release.

Critério mínimo:

- app release não permite backup automático do Android;
- app release não contém permissão `INTERNET`;
- telas sensíveis não aparecem em screenshot.

## Núcleo 5 — Senhas

Entra na v1:

- criar item de senha;
- editar item de senha;
- excluir item de senha;
- listar senhas;
- visualizar detalhe de senha;
- campos de título, usuário/e-mail, senha, URL e observações;
- gerador de senha forte;
- medidor simples de força;
- copiar senha manualmente;
- limpar clipboard após tempo definido;
- tratar URL, usuário/e-mail, título e observações como dados sensíveis.

Critério mínimo:

- senha salva não fica em texto claro;
- senha copiada é removida do clipboard;
- senha não aparece em logs.

## Núcleo 6 — Biometria Opcional

Entra na v1:

- ativar biometria;
- desativar biometria;
- desbloqueio conveniente por biometria;
- fallback para senha-mestra;
- exigência de senha-mestra para ações críticas;
- política de revogação em eventos de risco.

Critério mínimo:

- biometria não substitui totalmente a senha-mestra;
- usuário consegue usar senha-mestra mesmo com biometria desativada;
- ações críticas podem exigir senha-mestra novamente.

## Núcleo 7 — Backup Local Criptografado

Entra na v1:

- exportar backup criptografado;
- importar backup criptografado;
- validar integridade do backup;
- rejeitar backup corrompido;
- rejeitar senha incorreta;
- versionar formato de backup;
- exibir aviso antes da exportação;
- impedir exportação aberta em texto claro.

Critério mínimo:

- backup não contém plaintext;
- backup só é restaurado com senha correta;
- backup corrompido é recusado.

## Núcleo 8 — Fotos e Documentos

Entra na v1:

- importar foto privada;
- importar documento;
- criptografar arquivo importado;
- armazenar arquivo em área privada do app;
- visualizar arquivo de forma protegida;
- limpar arquivos temporários;
- validar tamanho;
- validar tipo de arquivo;
- excluir arquivo criptografado.

Critério mínimo:

- arquivo não fica legível no storage;
- arquivo não aparece na galeria por ação do app;
- temporários são limpos após visualização.

## Núcleo 9 — Auditoria Local Mínima

Entra na v1:

- registro local sanitizado de eventos relevantes;
- últimos eventos de segurança;
- tela simples de atividade recente;
- retenção limitada;
- separação entre auditoria local e logs técnicos.

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

Critério mínimo:

- auditoria não contém conteúdo do cofre;
- auditoria não contém senha, chave, URL, título, nome de arquivo ou dado sensível desnecessário.

## Núcleo 10 — Release Candidate Controlado

Entra na v1:

- build interno;
- build release candidate;
- checklist de segurança;
- teste em aparelho real;
- política de privacidade;
- revisão de permissões;
- hash SHA-256 do artefato;
- validação de ausência de `INTERNET`;
- validação de `allowBackup=false`;
- validação de `debuggable=false`.

Critério mínimo:

- APK/AAB release candidate gerado;
- artefato tem hash;
- checklist de segurança preenchido;
- teste manual documentado.

## Ordem de Entrega

A ordem da v1 será:

1. Cofre mínimo
2. Criptografia e armazenamento
3. Nota segura
4. Hardening mobile básico
5. Senhas
6. Biometria
7. Backup local criptografado
8. Fotos e documentos
9. Auditoria local e logs
10. CI/CD e release candidate

Nenhum núcleo deve começar antes da auditoria do núcleo anterior.

## Critérios Gerais de Aceite da v1

A v1 só será considerada aceitável se:

- funcionar sem internet;
- não declarar permissão `INTERNET`;
- não depender de backend;
- não sincronizar dados;
- criar e desbloquear cofre local;
- criptografar dados sensíveis em repouso;
- proteger conteúdo em telas sensíveis;
- impedir backup automático do Android;
- permitir uso básico de notas seguras;
- permitir uso básico de senhas;
- permitir backup criptografado;
- passar por checklist de release;
- passar por teste em aparelho real.

## Status do Documento

- **Documento:** `docs/SDD/01-visao-geral/04-escopo-v1.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Fundação e SDD Essencial
