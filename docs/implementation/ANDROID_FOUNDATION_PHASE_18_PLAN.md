# Android Foundation — Phase 18 Plan

## Nome

Local Navigation Skeleton

## Objetivo

Criar uma navegação local simples para o cofre desbloqueado, sem persistência, sem Android Navigation e sem storage real.

## Escopo

Esta fase adiciona:

- `VaultDestination`
- `InMemoryVaultNavigator`
- navegação local entre seções do cofre
- placeholders visuais para seções internas

As seções iniciais são:

- Home
- Senhas
- Notas
- Fotos
- Documentos

## Desenho esperado

```text
UnlockedVaultScreen
├── VaultHomeShell
└── navegação local por seção:
    ├── HOME
    ├── PASSWORDS
    ├── NOTES
    ├── PHOTOS
    └── DOCUMENTS
```

## Fora de escopo

Esta fase não implementa:

- CRUD real;
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
- Android Navigation;
- backend;
- rede;
- permissão INTERNET.

## Critérios de aceite

- Existe enum explícito de destinos do cofre.
- Existe navegador local em memória.
- O container cria o navegador local.
- O BootstrapScreen coordena o navegador.
- A tela desbloqueada recebe o destino atual.
- A tela desbloqueada permite navegar entre seções.
- A navegação reseta para Home ao bloquear ou desbloquear.
- Nenhum storage real é usado.
- Nenhuma permissão nova é adicionada.
- O APK permanece sem android.permission.INTERNET.
