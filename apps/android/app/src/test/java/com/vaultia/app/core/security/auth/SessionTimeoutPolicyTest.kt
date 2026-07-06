package com.vaultia.app.core.security.auth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class SessionTimeoutPolicyTest {
    @Test
    fun sessionExpiresAfterConfiguredTimeout() {
        val policy = SessionTimeoutPolicy(sessionTimeoutMillis = 300_000L)

        assertFalse(
            policy.isSessionExpired(
                lastAuthenticatedAtEpochMillis = 1_000L,
                nowEpochMillis = 301_000L,
            ),
        )
        assertTrue(
            policy.isSessionExpired(
                lastAuthenticatedAtEpochMillis = 1_000L,
                nowEpochMillis = 301_001L,
            ),
        )
    }

    @Test
    fun criticalActionFreshnessUsesShorterWindow() {
        val policy = SessionTimeoutPolicy(
            sessionTimeoutMillis = 300_000L,
            criticalActionFreshnessMillis = 30_000L,
        )

        assertTrue(
            policy.isFreshForCriticalAction(
                lastCriticalAuthenticationAtEpochMillis = 1_000L,
                nowEpochMillis = 31_000L,
            ),
        )
        assertFalse(
            policy.isFreshForCriticalAction(
                lastCriticalAuthenticationAtEpochMillis = 1_000L,
                nowEpochMillis = 31_001L,
            ),
        )
    }

    @Test
    fun rejectsInvalidConfigurationAndTimestamps() {
        expectIllegalArgument {
            SessionTimeoutPolicy(sessionTimeoutMillis = 0L)
        }
        expectIllegalArgument {
            SessionTimeoutPolicy(criticalActionFreshnessMillis = 0L)
        }
        expectIllegalArgument {
            SessionTimeoutPolicy(
                sessionTimeoutMillis = 30_000L,
                criticalActionFreshnessMillis = 60_000L,
            )
        }

        val policy = SessionTimeoutPolicy()

        expectIllegalArgument {
            policy.isSessionExpired(
                lastAuthenticatedAtEpochMillis = -1L,
                nowEpochMillis = 0L,
            )
        }
        expectIllegalArgument {
            policy.isSessionExpired(
                lastAuthenticatedAtEpochMillis = 10L,
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
