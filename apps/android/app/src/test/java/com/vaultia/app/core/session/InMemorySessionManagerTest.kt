package com.vaultia.app.core.session

import com.vaultia.app.core.model.vault.VaultSessionState
import org.junit.Assert.assertEquals
import org.junit.Test

class InMemorySessionManagerTest {
    @Test
    fun startsLocked() {
        val sessionManager = InMemorySessionManager()

        assertEquals(VaultSessionState.LOCKED, sessionManager.state())
    }

    @Test
    fun canUnlockForCurrentProcessOnly() {
        val sessionManager = InMemorySessionManager()

        sessionManager.unlockForCurrentProcessOnly()

        assertEquals(VaultSessionState.UNLOCKED, sessionManager.state())
    }

    @Test
    fun canLockAgain() {
        val sessionManager = InMemorySessionManager()

        sessionManager.unlockForCurrentProcessOnly()
        sessionManager.lock()

        assertEquals(VaultSessionState.LOCKED, sessionManager.state())
    }
}
