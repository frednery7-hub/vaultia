package com.vaultia.app.core.security.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class AuthenticationFrictionPolicyTest {
    private val policy = AuthenticationFrictionPolicy(
        delayPolicy = AuthenticationDelayPolicy(),
        sessionTimeoutPolicy = SessionTimeoutPolicy(
            sessionTimeoutMillis = 300_000L,
            criticalActionFreshnessMillis = 30_000L,
        ),
    )

    @Test
    fun unlockVaultRequiresReauthentication() {
        val decision = policy.decisionForAction(
            action = CriticalAction.UNLOCK_VAULT,
            isVaultUnlocked = false,
            attemptState = AuthenticationAttemptState.clean(),
            lastAuthenticatedAtEpochMillis = 1_000L,
            lastCriticalAuthenticationAtEpochMillis = 1_000L,
            nowEpochMillis = 2_000L,
        )

        assertFalse(decision.allowed)
        assertTrue(decision.requiresReauthentication)
        assertEquals(CriticalActionBlockReason.NONE, decision.blockReason)
    }

    @Test
    fun revealSecretRequiresFreshAuthenticationWhenFreshnessExpired() {
        val decision = policy.decisionForAction(
            action = CriticalAction.REVEAL_SECRET,
            isVaultUnlocked = true,
            attemptState = AuthenticationAttemptState.clean(),
            lastAuthenticatedAtEpochMillis = 1_000L,
            lastCriticalAuthenticationAtEpochMillis = 1_000L,
            nowEpochMillis = 40_000L,
        )

        assertFalse(decision.allowed)
        assertTrue(decision.requiresReauthentication)
        assertEquals(CriticalActionBlockReason.NONE, decision.blockReason)
    }

    @Test
    fun copySecretRequiresFreshAuthenticationWhenFreshnessExpired() {
        val decision = policy.decisionForAction(
            action = CriticalAction.COPY_SECRET,
            isVaultUnlocked = true,
            attemptState = AuthenticationAttemptState.clean(),
            lastAuthenticatedAtEpochMillis = 1_000L,
            lastCriticalAuthenticationAtEpochMillis = 1_000L,
            nowEpochMillis = 40_000L,
        )

        assertFalse(decision.allowed)
        assertTrue(decision.requiresReauthentication)
    }

    @Test
    fun exportBackupRequiresFreshAuthenticationWhenFreshnessExpired() {
        val decision = policy.decisionForAction(
            action = CriticalAction.EXPORT_BACKUP,
            isVaultUnlocked = true,
            attemptState = AuthenticationAttemptState.clean(),
            lastAuthenticatedAtEpochMillis = 1_000L,
            lastCriticalAuthenticationAtEpochMillis = 1_000L,
            nowEpochMillis = 40_000L,
        )

        assertFalse(decision.allowed)
        assertTrue(decision.requiresReauthentication)
    }

    @Test
    fun changeMasterPasswordRequiresFreshAuthenticationWhenFreshnessExpired() {
        val decision = policy.decisionForAction(
            action = CriticalAction.CHANGE_MASTER_PASSWORD,
            isVaultUnlocked = true,
            attemptState = AuthenticationAttemptState.clean(),
            lastAuthenticatedAtEpochMillis = 1_000L,
            lastCriticalAuthenticationAtEpochMillis = 1_000L,
            nowEpochMillis = 40_000L,
        )

        assertFalse(decision.allowed)
        assertTrue(decision.requiresReauthentication)
    }

    @Test
    fun actionIsAllowedWhenSessionAndCriticalFreshnessAreValid() {
        val decision = policy.decisionForAction(
            action = CriticalAction.REVEAL_SECRET,
            isVaultUnlocked = true,
            attemptState = AuthenticationAttemptState.clean(),
            lastAuthenticatedAtEpochMillis = 1_000L,
            lastCriticalAuthenticationAtEpochMillis = 20_000L,
            nowEpochMillis = 40_000L,
        )

        assertTrue(decision.allowed)
        assertFalse(decision.requiresReauthentication)
        assertEquals(CriticalActionBlockReason.NONE, decision.blockReason)
    }

    @Test
    fun lockedSessionBlocksCriticalActions() {
        val decision = policy.decisionForAction(
            action = CriticalAction.REVEAL_SECRET,
            isVaultUnlocked = false,
            attemptState = AuthenticationAttemptState.clean(),
            lastAuthenticatedAtEpochMillis = 1_000L,
            lastCriticalAuthenticationAtEpochMillis = 1_000L,
            nowEpochMillis = 2_000L,
        )

        assertFalse(decision.allowed)
        assertFalse(decision.requiresReauthentication)
        assertEquals(CriticalActionBlockReason.SESSION_LOCKED, decision.blockReason)
    }

    @Test
    fun expiredSessionBlocksCriticalActions() {
        val decision = policy.decisionForAction(
            action = CriticalAction.REVEAL_SECRET,
            isVaultUnlocked = true,
            attemptState = AuthenticationAttemptState.clean(),
            lastAuthenticatedAtEpochMillis = 1_000L,
            lastCriticalAuthenticationAtEpochMillis = 290_000L,
            nowEpochMillis = 400_001L,
        )

        assertFalse(decision.allowed)
        assertFalse(decision.requiresReauthentication)
        assertEquals(CriticalActionBlockReason.SESSION_TIMEOUT, decision.blockReason)
    }

    @Test
    fun tooManyFailuresBlockActions() {
        val decision = policy.decisionForAction(
            action = CriticalAction.REVEAL_SECRET,
            isVaultUnlocked = true,
            attemptState = AuthenticationAttemptState(
                failedAttempts = 5,
                lastFailureAtEpochMillis = 1_000L,
            ),
            lastAuthenticatedAtEpochMillis = 1_000L,
            lastCriticalAuthenticationAtEpochMillis = 1_000L,
            nowEpochMillis = 2_000L,
        )

        assertFalse(decision.allowed)
        assertFalse(decision.requiresReauthentication)
        assertEquals(CriticalActionBlockReason.TOO_MANY_FAILURES, decision.blockReason)
    }

    @Test
    fun progressiveDelayBlocksBeforeDelayElapsed() {
        val decision = policy.decisionForAction(
            action = CriticalAction.REVEAL_SECRET,
            isVaultUnlocked = true,
            attemptState = AuthenticationAttemptState(
                failedAttempts = 2,
                lastFailureAtEpochMillis = 1_000L,
            ),
            lastAuthenticatedAtEpochMillis = 1_000L,
            lastCriticalAuthenticationAtEpochMillis = 1_000L,
            nowEpochMillis = 20_000L,
        )

        assertFalse(decision.allowed)
        assertFalse(decision.requiresReauthentication)
        assertEquals(CriticalActionBlockReason.TOO_MANY_FAILURES, decision.blockReason)
    }

    @Test
    fun decisionRejectsContradictoryState() {
        expectIllegalArgument {
            CriticalActionDecision(
                action = CriticalAction.REVEAL_SECRET,
                allowed = true,
                requiresReauthentication = true,
                blockReason = CriticalActionBlockReason.NONE,
            )
        }
        expectIllegalArgument {
            CriticalActionDecision(
                action = CriticalAction.REVEAL_SECRET,
                allowed = true,
                requiresReauthentication = false,
                blockReason = CriticalActionBlockReason.SESSION_LOCKED,
            )
        }
        expectIllegalArgument {
            CriticalActionDecision.blocked(
                action = CriticalAction.REVEAL_SECRET,
                reason = CriticalActionBlockReason.NONE,
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
