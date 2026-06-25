package com.vaultia.app.core.session

import com.vaultia.app.core.model.vault.VaultSessionState

class InMemorySessionManager {
    private var currentState: VaultSessionState = VaultSessionState.LOCKED

    fun state(): VaultSessionState = currentState

    fun unlockForCurrentProcessOnly() {
        currentState = VaultSessionState.UNLOCKED
    }

    fun lock() {
        currentState = VaultSessionState.LOCKED
    }
}
