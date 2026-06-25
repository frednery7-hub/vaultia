# Android Foundation — Phase 10 Plan

## Nome

Vault Home Shell

## Objetivo

Criar a primeira tela interna do cofre para quando a sessão estiver desbloqueada.

## Escopo

A fase adiciona uma home visual interna com categorias vazias:

- Senhas
- Notas
- Fotos
- Documentos

A tela é apenas shell visual. Ela não cria, lê, edita, salva ou criptografa dados.

## Fora de escopo

Esta fase não implementa:

- Cadastro de item
- Edição de item
- Listagem real
- Banco de dados
- Arquivos locais
- Criptografia
- Senha real
- PIN real
- Biometria
- Android Keystore
- Backend
- Rede
- Permissão INTERNET

## Critérios de aceite

- O app continua iniciando em estado bloqueado.
- Ao usar desbloqueio simulado, a UI mostra a área interna do cofre.
- A área interna possui categorias vazias.
- A ação de bloqueio continua funcionando.
- Nenhum dado sensível é salvo.
- Nenhuma permissão nova é adicionada.
- O APK permanece sem android.permission.INTERNET.
- Testes automatizados validam que a home é apenas shell visual.
