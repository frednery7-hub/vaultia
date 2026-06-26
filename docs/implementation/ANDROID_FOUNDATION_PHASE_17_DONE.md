# Android Foundation — Phase 17 Done

## Status

Aprovado.

## Objetivo

Separar a UI do cofre bloqueado e a UI do cofre desbloqueado em componentes próprios.

## Resultado

Foram criadas duas telas dedicadas:

- `LockedVaultScreen`
- `UnlockedVaultScreen`

O `BootstrapScreen` agora atua como orquestrador visual:

```text
BootstrapScreen
├── LockedVaultScreen
└── UnlockedVaultScreen
    └── VaultHomeShell