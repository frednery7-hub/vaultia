package com.vaultia.app.core.ui.screen

import com.vaultia.app.core.ui.security.SecureUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class SecureScreenPolicyTest {
    @Test
    fun modeCapabilitiesMatchSecurityRequirements() {
        assertFalse(SecureScreenMode.PUBLIC.requiresSecureFlag)
        assertFalse(SecureScreenMode.PUBLIC.canRenderSensitiveContent)
        assertFalse(SecureScreenMode.PUBLIC.redactsContent)

        assertFalse(SecureScreenMode.REDACTED.requiresSecureFlag)
        assertFalse(SecureScreenMode.REDACTED.canRenderSensitiveContent)
        assertTrue(SecureScreenMode.REDACTED.redactsContent)

        assertTrue(SecureScreenMode.SENSITIVE.requiresSecureFlag)
        assertTrue(SecureScreenMode.SENSITIVE.canRenderSensitiveContent)
        assertFalse(SecureScreenMode.SENSITIVE.redactsContent)

        assertFalse(SecureScreenMode.LOCKED.requiresSecureFlag)
        assertFalse(SecureScreenMode.LOCKED.canRenderSensitiveContent)
        assertTrue(SecureScreenMode.LOCKED.redactsContent)
    }

    @Test
    fun uiStatesMapToSafeScreenModes() {
        assertEquals(SecureScreenMode.REDACTED, SecureScreenPolicy.modeForUiState(SecureUiState.BOOTSTRAPPING))
        assertEquals(SecureScreenMode.LOCKED, SecureScreenPolicy.modeForUiState(SecureUiState.LOCKED))
        assertEquals(SecureScreenMode.REDACTED, SecureScreenPolicy.modeForUiState(SecureUiState.AUTHENTICATING))
        assertEquals(SecureScreenMode.REDACTED, SecureScreenPolicy.modeForUiState(SecureUiState.UNLOCKED_REDACTED))
        assertEquals(SecureScreenMode.SENSITIVE, SecureScreenPolicy.modeForUiState(SecureUiState.UNLOCKED_REVEALED))
        assertEquals(SecureScreenMode.LOCKED, SecureScreenPolicy.modeForUiState(SecureUiState.LOCKING))
        assertEquals(SecureScreenMode.REDACTED, SecureScreenPolicy.modeForUiState(SecureUiState.ERROR_REDACTED))
    }

    @Test
    fun onlySensitiveModeRequiresSecureFlag() {
        assertFalse(SecureScreenPolicy.requiresSecureFlag(SecureScreenMode.PUBLIC))
        assertFalse(SecureScreenPolicy.requiresSecureFlag(SecureScreenMode.REDACTED))
        assertTrue(SecureScreenPolicy.requiresSecureFlag(SecureScreenMode.SENSITIVE))
        assertFalse(SecureScreenPolicy.requiresSecureFlag(SecureScreenMode.LOCKED))
    }

    @Test
    fun recentsScreenNeverReceivesSensitiveMode() {
        assertTrue(SecureScreenPolicy.canShowInRecents(SecureScreenMode.PUBLIC))
        assertTrue(SecureScreenPolicy.canShowInRecents(SecureScreenMode.REDACTED))
        assertFalse(SecureScreenPolicy.canShowInRecents(SecureScreenMode.SENSITIVE))
        assertTrue(SecureScreenPolicy.canShowInRecents(SecureScreenMode.LOCKED))
    }

    @Test
    fun decisionCannotRequireAndClearSecureFlagAtSameTime() {
        expectIllegalArgument {
            SecureScreenDecision(
                nextMode = SecureScreenMode.SENSITIVE,
                requireSecureFlag = true,
                clearSecureFlag = true,
                redactContent = false,
                startVisualLock = false,
                allowReveal = true,
            )
        }
    }

    @Test
    fun decisionCannotAllowRevealInNonSensitiveMode() {
        expectIllegalArgument {
            SecureScreenDecision(
                nextMode = SecureScreenMode.REDACTED,
                requireSecureFlag = false,
                clearSecureFlag = true,
                redactContent = true,
                startVisualLock = false,
                allowReveal = true,
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
