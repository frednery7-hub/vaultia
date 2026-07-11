package com.vaultia.app.core.session

import com.vaultia.app.core.crypto.kdf.KdfResult
import com.vaultia.app.core.model.vault.VaultSessionState

class InMemorySessionManager {
    private var currentState: VaultSessionState = VaultSessionState.LOCKED
    private var sessionKey: KdfResult? = null

    fun state(): VaultSessionState = currentState
    
    fun getSessionKey(): KdfResult? = sessionKey

    fun unlockForCurrentProcessOnly(key: KdfResult) {
        sessionKey = key
        currentState = VaultSessionState.UNLOCKED
    }

    fun lock() {
        sessionKey = null
        currentState = VaultSessionState.LOCKED
    }
}
