package com.vaultia.app.core.ui.screen

import com.vaultia.app.core.ui.security.SecureUiState

object SecureScreenPolicy {
    fun modeForUiState(state: SecureUiState): SecureScreenMode {
        return when (state) {
            SecureUiState.BOOTSTRAPPING -> SecureScreenMode.REDACTED
            SecureUiState.LOCKED -> SecureScreenMode.LOCKED
            SecureUiState.AUTHENTICATING -> SecureScreenMode.REDACTED
            SecureUiState.UNLOCKED_REDACTED -> SecureScreenMode.REDACTED
            SecureUiState.UNLOCKED_REVEALED -> SecureScreenMode.SENSITIVE
            SecureUiState.LOCKING -> SecureScreenMode.LOCKED
            SecureUiState.ERROR_REDACTED -> SecureScreenMode.REDACTED
        }
    }

    fun requiresSecureFlag(mode: SecureScreenMode): Boolean {
        return mode.requiresSecureFlag
    }

    fun canShowInRecents(mode: SecureScreenMode): Boolean {
        return mode == SecureScreenMode.PUBLIC || mode == SecureScreenMode.LOCKED || mode == SecureScreenMode.REDACTED
    }

    fun mustRedactForBackground(mode: SecureScreenMode): Boolean {
        return mode == SecureScreenMode.SENSITIVE || mode == SecureScreenMode.REDACTED
    }
}
