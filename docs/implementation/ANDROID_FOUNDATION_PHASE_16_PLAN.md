# Android Foundation — Phase 16 Plan

## Nome

App Composition Root

## Objetivo

Criar um ponto único de composição das dependências locais do app.

## Escopo

Esta fase move a criação dos objetos principais para um container explícito:

- InMemorySessionManager
- InMemoryVaultRepository
- DemoVaultMetadataSeed

A MainActivity deixa de conhecer detalhes de seed demo e passa a receber dependências prontas do container.

## Fora de escopo

Esta fase não implementa:

- injeção de dependência externa;
- Hilt;
- Dagger;
- Koin;
- senha real;
- PIN real;
- biometria;
- KDF;
- criptografia;
- Android Keystore;
- banco de dados;
- arquivos locais;
- storage criptografado;
- backend;
- rede;
- permissão INTERNET.

## Critérios de aceite

- Existe um container explícito de composição.
- MainActivity não carrega seed demo diretamente.
- Container cria session manager e repository.
- Container carrega apenas metadados demo em memória.
- Nenhum storage real é usado.
- Nenhuma permissão nova é adicionada.
- O APK permanece sem android.permission.INTERNET.