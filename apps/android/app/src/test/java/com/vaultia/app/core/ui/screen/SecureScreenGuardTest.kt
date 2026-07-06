package com.vaultia.app.core.ui.screen

import com.vaultia.app.core.ui.security.SecureUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureScreenGuardTest {
    @Test
    fun revealedUiStateRequiresSecureFlagAndAllowsReveal() {
        val decision = SecureScreenGuard.decisionForState(SecureUiState.UNLOCKED_REVEALED)

        assertEquals(SecureScreenMode.SENSITIVE, decision.nextMode)
        assertTrue(decision.requireSecureFlag)
        assertFalse(decision.clearSecureFlag)
        assertFalse(decision.redactContent)
        assertFalse(decision.startVisualLock)
        assertTrue(decision.allowReveal)
    }

    @Test
    fun lockedUiStateClearsSecureFlagAndRedactsContent() {
        val decision = SecureScreenGuard.decisionForState(SecureUiState.LOCKED)

        assertEquals(SecureScreenMode.LOCKED, decision.nextMode)
        assertFalse(decision.requireSecureFlag)
        assertTrue(decision.clearSecureFlag)
        assertTrue(decision.redactContent)
        assertFalse(decision.allowReveal)
    }

    @Test
    fun secretRevealedEventRequiresSecureFlag() {
        val decision = SecureScreenGuard.decisionForEvent(
            currentMode = SecureScreenMode.REDACTED,
            event = SecureScreenEvent.SECRET_REVEALED,
        )

        assertEquals(SecureScreenMode.SENSITIVE, decision.nextMode)
        assertTrue(decision.requireSecureFlag)
        assertFalse(decision.clearSecureFlag)
        assertFalse(decision.redactContent)
        assertTrue(decision.allowReveal)
    }

    @Test
    fun secretRedactedEventClearsSecureFlagAndRedactsContent() {
        val decision = SecureScreenGuard.decisionForEvent(
            currentMode = SecureScreenMode.SENSITIVE,
            event = SecureScreenEvent.SECRET_REDACTED,
        )

        assertEquals(SecureScreenMode.REDACTED, decision.nextMode)
        assertFalse(decision.requireSecureFlag)
        assertTrue(decision.clearSecureFlag)
        assertTrue(decision.redactContent)
        assertFalse(decision.allowReveal)
    }

    @Test
    fun backgroundFromSensitiveRedactsInsteadOfPreservingSensitiveMode() {
        val decision = SecureScreenGuard.decisionForEvent(
            currentMode = SecureScreenMode.SENSITIVE,
            event = SecureScreenEvent.APP_BACKGROUND,
        )

        assertEquals(SecureScreenMode.REDACTED, decision.nextMode)
        assertFalse(decision.requireSecureFlag)
        assertTrue(decision.clearSecureFlag)
        assertTrue(decision.redactContent)
        assertFalse(decision.allowReveal)
    }

    @Test
    fun backgroundFromRedactedStartsVisualLock() {
        val decision = SecureScreenGuard.decisionForEvent(
            currentMode = SecureScreenMode.REDACTED,
            event = SecureScreenEvent.APP_BACKGROUND,
        )

        assertEquals(SecureScreenMode.LOCKED, decision.nextMode)
        assertFalse(decision.requireSecureFlag)
        assertTrue(decision.clearSecureFlag)
        assertTrue(decision.redactContent)
        assertTrue(decision.startVisualLock)
    }

    @Test
    fun lockRequestedAlwaysMovesToLockedMode() {
        val decision = SecureScreenGuard.decisionForEvent(
            currentMode = SecureScreenMode.SENSITIVE,
            event = SecureScreenEvent.LOCK_REQUESTED,
        )

        assertEquals(SecureScreenMode.LOCKED, decision.nextMode)
        assertTrue(decision.redactContent)
        assertTrue(decision.startVisualLock)
        assertFalse(decision.allowReveal)
    }

    @Test
    fun pauseAndStopAlsoRedactSensitiveMode() {
        val pauseDecision = SecureScreenGuard.decisionForEvent(
            currentMode = SecureScreenMode.SENSITIVE,
            event = SecureScreenEvent.APP_PAUSE,
        )
        val stopDecision = SecureScreenGuard.decisionForEvent(
            currentMode = SecureScreenMode.SENSITIVE,
            event = SecureScreenEvent.APP_STOP,
        )

        assertEquals(SecureScreenMode.REDACTED, pauseDecision.nextMode)
        assertEquals(SecureScreenMode.REDACTED, stopDecision.nextMode)
        assertTrue(pauseDecision.redactContent)
        assertTrue(stopDecision.redactContent)
    }
}
