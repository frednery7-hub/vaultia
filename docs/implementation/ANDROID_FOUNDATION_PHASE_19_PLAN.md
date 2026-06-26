# Android Foundation — Phase 19 Plan

## Nome

Empty State & Section UX Hardening

## Objetivo

Melhorar a experiência das seções internas do cofre sem implementar CRUD real, storage, senha ou criptografia.

## Escopo

Esta fase adiciona um componente reutilizável de estado vazio:

- `VaultEmptyStateView`

E atualiza a tela desbloqueada para usar estados vazios explícitos nas seções:

- Senhas
- Notas
- Fotos
- Documentos

Cada seção deve comunicar claramente que ainda não armazena dados reais e que recursos sensíveis dependem de autenticação, KDF, criptografia e storage seguro em fases futuras.

## Fora de escopo

Esta fase não implementa:

- CRUD real;
- criação de senha;
- criação de nota;
- importação de foto;
- importação de documento;
- senha real;
- PIN real;
- biometria;
- KDF;
- criptografia;
- Android Keystore;
- banco de dados;
- arquivos locais;
- storage criptografado;
- backup;
- import/export;
- backend;
- rede;
- permissão INTERNET.

## Critérios de aceite

- Existe componente reutilizável para estado vazio.
- `UnlockedVaultScreen` usa `VaultEmptyStateView` nas seções internas.
- As mensagens deixam claro que não há dados reais nesta fase.
- As mensagens citam limites de segurança antes de qualquer conteúdo sensível.
- Nenhum storage real é usado.
- Nenhuma permissão nova é adicionada.
- O APK permanece sem android.permission.INTERNET.
- O roadmap não é atualizado nesta fase.
