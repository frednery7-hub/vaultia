package com.vaultia.app.core.security.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class AuthenticationDelayPolicyTest {
    @Test
    fun progressiveDelayMatchesPolicy() {
        val policy = AuthenticationDelayPolicy()

        assertEquals(0L, policy.delayMillisForFailures(0))
        assertEquals(0L, policy.delayMillisForFailures(1))
        assertEquals(30_000L, policy.delayMillisForFailures(2))
        assertEquals(120_000L, policy.delayMillisForFailures(3))
        assertEquals(600_000L, policy.delayMillisForFailures(4))
        assertEquals(1_800_000L, policy.delayMillisForFailures(5))
        assertEquals(1_800_000L, policy.delayMillisForFailures(9))
    }

    @Test
    fun temporaryLockStartsAtFiveFailures() {
        val policy = AuthenticationDelayPolicy()

        assertFalse(policy.isTemporarilyLocked(0))
        assertFalse(policy.isTemporarilyLocked(1))
        assertFalse(policy.isTemporarilyLocked(2))
        assertFalse(policy.isTemporarilyLocked(3))
        assertFalse(policy.isTemporarilyLocked(4))
        assertTrue(policy.isTemporarilyLocked(5))
    }

    @Test
    fun delayElapsedUsesLastFailureTimestamp() {
        val policy = AuthenticationDelayPolicy()

        assertFalse(
            policy.isDelayElapsed(
                failedAttempts = 2,
                lastFailureAtEpochMillis = 1_000L,
                nowEpochMillis = 20_000L,
            ),
        )
        assertTrue(
            policy.isDelayElapsed(
                failedAttempts = 2,
                lastFailureAtEpochMillis = 1_000L,
                nowEpochMillis = 31_000L,
            ),
        )
    }

    @Test
    fun rejectsInvalidInputs() {
        val policy = AuthenticationDelayPolicy()

        expectIllegalArgument {
            AuthenticationDelayPolicy(strongTemporaryLockMillis = 0L)
        }
        expectIllegalArgument {
            policy.delayMillisForFailures(-1)
        }
        expectIllegalArgument {
            policy.isTemporarilyLocked(-1)
        }
        expectIllegalArgument {
            policy.isDelayElapsed(
                failedAttempts = 1,
                lastFailureAtEpochMillis = -1L,
                nowEpochMillis = 0L,
            )
        }
        expectIllegalArgument {
            policy.isDelayElapsed(
                failedAttempts = 1,
                lastFailureAtEpochMillis = 10L,
                nowEpochMillis = 9L,
            )
        }
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
