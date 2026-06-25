# Vaultia – Android Foundation Phase 3 PLAN

## Objetivo

Organizar a estrutura interna mínima de pacotes Android antes da implementação de qualquer funcionalidade sensível.

## Escopo Permitido

Esta fase permite apenas:

- criação de pacotes;
- criação de marcadores internos de pacote;
- documentação de fronteiras arquiteturais;
- validação de build;
- validação de ausência de INTERNET.

## Escopo Bloqueado

Esta fase não permite implementar:

- cofre real;
- Master Password;
- KDF;
- AES-GCM;
- Android Keystore;
- banco local;
- storage criptografado;
- biometria;
- backup;
- clipboard;
- importação de arquivos;
- exportação;
- auditoria local sensível.

## Pacotes Criados

- `core.common`
- `core.security`
- `core.storage`
- `core.model`
- `feature.bootstrap`

## Regras

Nenhum pacote desta fase deve armazenar dado sensível.

Nenhum pacote desta fase deve conter criptografia real.

Nenhum pacote desta fase deve introduzir permissão de internet.

Nenhum pacote desta fase deve introduzir dependência externa nova sem aprovação.

## Status

- **Status:** Planejado
- **Próximo passo:** validar build e auditoria de permissões após criação dos pacotes
