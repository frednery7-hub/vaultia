# Vaultia – Android Foundation Phase 5 PLAN

## Objetivo

Criar contratos internos mínimos para a arquitetura futura do Vaultia sem implementar funcionalidade sensível.

## Escopo Permitido

Esta fase permite criar:

- interfaces vazias de contrato;
- modelos de domínio sem payload sensível;
- fronteiras arquiteturais para criptografia, storage, repositório e sessão;
- documentação de regras de segurança;
- validação de build;
- validação do check Android.

## Escopo Bloqueado

Esta fase não permite implementar:

- criptografia real;
- KDF;
- AES-GCM;
- Android Keystore;
- Master Password;
- banco local;
- storage criptografado;
- cofre real;
- leitura de fotos/documentos;
- biometria;
- backup;
- clipboard;
- rede;
- qualquer permissão nova.

## Contratos Planejados

- `CryptoService`
- `SecureStorage`
- `VaultRepository`
- `SessionManager`

## Modelos Permitidos

- `VaultItemType`
- `VaultItem`
- `VaultSessionState`

## Regra Crítica

`VaultItem` nesta fase deve representar apenas metadados.

Ele não pode conter:

- senha;
- corpo de nota;
- bytes de arquivo;
- bytes de foto;
- conteúdo descriptografado;
- chave criptográfica;
- salt;
- nonce;
- tag de autenticação.

## Critérios de Aceite

A fase só pode ser fechada se:

- build passar;
- `scripts/check-android.sh` passar;
- nenhum arquivo perigoso aparecer no Git status;
- Manifest continuar sem INTERNET;
- APK binário continuar sem INTERNET;
- não houver implementação concreta de criptografia/storage/sessão.

## Status

- **Status:** Planejado
- **Próximo passo:** validar contratos e build
