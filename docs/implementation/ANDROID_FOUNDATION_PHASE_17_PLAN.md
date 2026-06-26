# Android Foundation — Phase 17 Plan

## Nome

Locked/Unlocked Screen Separation

## Objetivo

Separar a UI do cofre bloqueado e a UI do cofre desbloqueado em componentes próprios.

## Escopo

Esta fase reorganiza a camada visual inicial:

- `LockedVaultScreen`
- `UnlockedVaultScreen`
- `BootstrapScreen` como orquestrador de estado

O `BootstrapScreen` passa a decidir qual tela renderizar com base em `VaultSessionUiState`, mas deixa de construir diretamente a UI de baixo nível.

## Desenho esperado

```text
BootstrapScreen
├── LockedVaultScreen
└── UnlockedVaultScreen
    └── VaultHomeShell