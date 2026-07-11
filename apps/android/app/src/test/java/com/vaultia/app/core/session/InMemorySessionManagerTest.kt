package com.vaultia.app.core.session

import com.vaultia.app.core.crypto.kdf.KdfParameters
import com.vaultia.app.core.crypto.kdf.KdfResult
import com.vaultia.app.core.model.vault.VaultSessionState
import org.junit.Assert.assertEquals
import org.junit.Test

class InMemorySessionManagerTest {
    private val mockKey = KdfResult(ByteArray(32) { 0 }, KdfParameters.Argon2id(ByteArray(16) { 0 }, 32, 1, 1024, 1))

    @Test
    fun startsLocked() {
        val sessionManager = InMemorySessionManager()

        assertEquals(VaultSessionState.LOCKED, sessionManager.state())
    }

    @Test
    fun canUnlockForCurrentProcessOnly() {
        val sessionManager = InMemorySessionManager()

        sessionManager.unlockForCurrentProcessOnly(mockKey)

        assertEquals(VaultSessionState.UNLOCKED, sessionManager.state())
    }

    @Test
    fun canLockAgain() {
        val sessionManager = InMemorySessionManager()

        sessionManager.unlockForCurrentProcessOnly(mockKey)
        sessionManager.lock()

        assertEquals(VaultSessionState.LOCKED, sessionManager.state())
    }
}
