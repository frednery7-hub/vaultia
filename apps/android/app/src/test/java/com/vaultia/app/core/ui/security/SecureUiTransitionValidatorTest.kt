package com.vaultia.app.core.ui.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureUiTransitionValidatorTest {
    @Test
    fun allowsDocumentedSafeTransitions() {
        assertAllowed(SecureUiState.BOOTSTRAPPING, SecureUiAction.LOCK_COMPLETED, SecureUiState.LOCKED)
        assertAllowed(SecureUiState.LOCKED, SecureUiAction.START_AUTHENTICATION, SecureUiState.AUTHENTICATING)
        assertAllowed(SecureUiState.AUTHENTICATING, SecureUiAction.AUTHENTICATION_SUCCEEDED, SecureUiState.UNLOCKED_REDACTED)
        assertAllowed(SecureUiState.AUTHENTICATING, SecureUiAction.AUTHENTICATION_FAILED, SecureUiState.ERROR_REDACTED)
        assertAllowed(SecureUiState.ERROR_REDACTED, SecureUiAction.LOCK_COMPLETED, SecureUiState.LOCKED)
        assertAllowed(SecureUiState.UNLOCKED_REDACTED, SecureUiAction.REQUEST_REVEAL, SecureUiState.UNLOCKED_REVEALED)
        assertAllowed(SecureUiState.UNLOCKED_REVEALED, SecureUiAction.HIDE_SECRET, SecureUiState.UNLOCKED_REDACTED)
        assertAllowed(SecureUiState.UNLOCKED_REVEALED, SecureUiAction.REVEAL_TIMEOUT, SecureUiState.UNLOCKED_REDACTED)
        assertAllowed(SecureUiState.UNLOCKED_REDACTED, SecureUiAction.REQUEST_LOCK, SecureUiState.LOCKING)
        assertAllowed(SecureUiState.UNLOCKED_REVEALED, SecureUiAction.REQUEST_LOCK, SecureUiState.LOCKING)
        assertAllowed(SecureUiState.UNLOCKED_REDACTED, SecureUiAction.APP_BACKGROUND, SecureUiState.LOCKING)
        assertAllowed(SecureUiState.UNLOCKED_REVEALED, SecureUiAction.APP_BACKGROUND, SecureUiState.UNLOCKED_REDACTED)
        assertAllowed(SecureUiState.LOCKING, SecureUiAction.LOCK_COMPLETED, SecureUiState.LOCKED)
    }

    @Test
    fun rejectsRevealFromUnsafeStates() {
        assertRejected(SecureUiState.BOOTSTRAPPING, SecureUiAction.REQUEST_REVEAL, SecureUiState.UNLOCKED_REVEALED)
        assertRejected(SecureUiState.LOCKED, SecureUiAction.REQUEST_REVEAL, SecureUiState.UNLOCKED_REVEALED)
        assertRejected(SecureUiState.AUTHENTICATING, SecureUiAction.REQUEST_REVEAL, SecureUiState.UNLOCKED_REVEALED)
        assertRejected(SecureUiState.ERROR_REDACTED, SecureUiAction.REQUEST_REVEAL, SecureUiState.UNLOCKED_REVEALED)
        assertRejected(SecureUiState.LOCKING, SecureUiAction.REQUEST_REVEAL, SecureUiState.UNLOCKED_REVEALED)
    }

    @Test
    fun rejectsDirectUnlockToRevealedState() {
        assertRejected(SecureUiState.AUTHENTICATING, SecureUiAction.AUTHENTICATION_SUCCEEDED, SecureUiState.UNLOCKED_REVEALED)
        assertRejected(SecureUiState.LOCKED, SecureUiAction.AUTHENTICATION_SUCCEEDED, SecureUiState.UNLOCKED_REVEALED)
    }

    @Test
    fun rejectsDirectAccessFromLockedToVaultStates() {
        assertRejected(SecureUiState.LOCKED, SecureUiAction.APP_FOREGROUND, SecureUiState.UNLOCKED_REDACTED)
        assertRejected(SecureUiState.LOCKED, SecureUiAction.APP_FOREGROUND, SecureUiState.UNLOCKED_REVEALED)
    }

    @Test
    fun stateCapabilitiesStayRedactedExceptExplicitReveal() {
        assertFalse(SecureUiState.BOOTSTRAPPING.canRenderVaultList)
        assertFalse(SecureUiState.BOOTSTRAPPING.canRenderSensitiveValue)
        assertTrue(SecureUiState.BOOTSTRAPPING.requiresRedaction)

        assertFalse(SecureUiState.LOCKED.canRenderVaultList)
        assertFalse(SecureUiState.LOCKED.canRenderSensitiveValue)
        assertTrue(SecureUiState.LOCKED.requiresRedaction)

        assertFalse(SecureUiState.AUTHENTICATING.canRenderVaultList)
        assertFalse(SecureUiState.AUTHENTICATING.canRenderSensitiveValue)
        assertTrue(SecureUiState.AUTHENTICATING.requiresRedaction)

        assertTrue(SecureUiState.UNLOCKED_REDACTED.canRenderVaultList)
        assertFalse(SecureUiState.UNLOCKED_REDACTED.canRenderSensitiveValue)
        assertTrue(SecureUiState.UNLOCKED_REDACTED.requiresRedaction)

        assertTrue(SecureUiState.UNLOCKED_REVEALED.canRenderVaultList)
        assertTrue(SecureUiState.UNLOCKED_REVEALED.canRenderSensitiveValue)
        assertFalse(SecureUiState.UNLOCKED_REVEALED.requiresRedaction)

        assertFalse(SecureUiState.LOCKING.canRenderVaultList)
        assertFalse(SecureUiState.LOCKING.canRenderSensitiveValue)
        assertTrue(SecureUiState.LOCKING.requiresRedaction)

        assertFalse(SecureUiState.ERROR_REDACTED.canRenderVaultList)
        assertFalse(SecureUiState.ERROR_REDACTED.canRenderSensitiveValue)
        assertTrue(SecureUiState.ERROR_REDACTED.requiresRedaction)
    }

    private fun assertAllowed(
        from: SecureUiState,
        action: SecureUiAction,
        to: SecureUiState,
    ) {
        assertTrue(SecureUiTransitionValidator.isAllowed(from, action, to))
    }

    private fun assertRejected(
        from: SecureUiState,
        action: SecureUiAction,
        to: SecureUiState,
    ) {
        assertFalse(SecureUiTransitionValidator.isAllowed(from, action, to))
    }
}
