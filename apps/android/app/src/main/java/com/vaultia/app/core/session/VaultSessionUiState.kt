package com.vaultia.app.core.session

import com.vaultia.app.core.model.vault.VaultSessionState

enum class VaultSessionUiState {
    LOCKED,
    UNLOCKED,
}

fun VaultSessionState.toUiState(): VaultSessionUiState {
    return when (this) {
        VaultSessionState.NOT_INITIALIZED -> VaultSessionUiState.LOCKED
        VaultSessionState.LOCKED -> VaultSessionUiState.LOCKED
        VaultSessionState.UNLOCKED -> VaultSessionUiState.UNLOCKED
    }
}