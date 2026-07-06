package com.vaultia.app.core.ui.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class SensitiveRevealPolicyTest {
    @Test
    fun rejectsNonPositiveRevealTimeout() {
        expectIllegalArgument {
            SensitiveRevealPolicy(0L)
        }
        expectIllegalArgument {
            SensitiveRevealPolicy(-1L)
        }
    }

    @Test
    fun defaultTimeoutIsPositiveAndConservative() {
        val policy = SensitiveRevealPolicy.default()

        assertEquals(30_000L, policy.revealTimeoutMillis)
        assertTrue(policy.revealTimeoutMillis > 0L)
    }

    @Test
    fun revealIsAllowedOnlyFromUnlockedRedacted() {
        val policy = SensitiveRevealPolicy.default()

        assertFalse(policy.canRevealFrom(SecureUiState.BOOTSTRAPPING))
        assertFalse(policy.canRevealFrom(SecureUiState.LOCKED))
        assertFalse(policy.canRevealFrom(SecureUiState.AUTHENTICATING))
        assertTrue(policy.canRevealFrom(SecureUiState.UNLOCKED_REDACTED))
        assertFalse(policy.canRevealFrom(SecureUiState.UNLOCKED_REVEALED))
        assertFalse(policy.canRevealFrom(SecureUiState.LOCKING))
        assertFalse(policy.canRevealFrom(SecureUiState.ERROR_REDACTED))
    }

    @Test
    fun revealTimeoutRedactsRevealedStateOnly() {
        val policy = SensitiveRevealPolicy.default()

        assertEquals(SecureUiState.UNLOCKED_REDACTED, policy.stateAfterRevealTimeout(SecureUiState.UNLOCKED_REVEALED))
        assertEquals(SecureUiState.UNLOCKED_REDACTED, policy.stateAfterRevealTimeout(SecureUiState.UNLOCKED_REDACTED))
        assertEquals(SecureUiState.LOCKED, policy.stateAfterRevealTimeout(SecureUiState.LOCKED))
    }

    @Test
    fun backgroundRedactsOrLocksUnlockedStates() {
        val policy = SensitiveRevealPolicy.default()

        assertEquals(SecureUiState.UNLOCKED_REDACTED, policy.stateAfterBackground(SecureUiState.UNLOCKED_REVEALED))
        assertEquals(SecureUiState.LOCKING, policy.stateAfterBackground(SecureUiState.UNLOCKED_REDACTED))
        assertEquals(SecureUiState.LOCKED, policy.stateAfterBackground(SecureUiState.LOCKED))
        assertEquals(SecureUiState.AUTHENTICATING, policy.stateAfterBackground(SecureUiState.AUTHENTICATING))
    }

    private fun expectIllegalArgument(block: () -> Unit) {
        try {
            block()
            fail("Expected IllegalArgumentException.")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }
}
