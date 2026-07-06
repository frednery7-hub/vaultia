package com.vaultia.app.core.security.threat

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class LocalThreatPolicyTest {
    @Test
    fun noFindingsAllowNormalOperation() {
        val decision = LocalThreatPolicy.decisionForFindings(emptyList())

        assertTrue(decision.allowNormalOperation)
        assertFalse(decision.requireReauthentication)
        assertFalse(decision.blockReveal)
        assertFalse(decision.blockExport)
        assertFalse(decision.keepUiRedacted)
        assertFalse(decision.showStrongWarning)
        assertFalse(decision.blockCriticalActions)
    }

    @Test
    fun rootFindingActivatesDegradedMode() {
        val decision = LocalThreatPolicy.decisionForFindings(
            listOf(
                LocalThreatFinding(LocalThreatSignal.ROOT_DETECTED),
            ),
        )

        assertFalse(decision.allowNormalOperation)
        assertTrue(decision.requireReauthentication)
        assertTrue(decision.blockReveal)
        assertTrue(decision.blockExport)
        assertTrue(decision.keepUiRedacted)
        assertTrue(decision.showStrongWarning)
        assertFalse(decision.blockCriticalActions)
    }

    @Test
    fun debuggerFindingActivatesDegradedMode() {
        val decision = LocalThreatPolicy.decisionForFindings(
            listOf(
                LocalThreatFinding(LocalThreatSignal.DEBUGGER_ATTACHED),
            ),
        )

        assertFalse(decision.allowNormalOperation)
        assertTrue(decision.requireReauthentication)
        assertTrue(decision.blockReveal)
    }

    @Test
    fun hookingFindingActivatesDegradedMode() {
        val decision = LocalThreatPolicy.decisionForFindings(
            listOf(
                LocalThreatFinding(LocalThreatSignal.HOOKING_FRAMEWORK_INDICATOR),
            ),
        )

        assertFalse(decision.allowNormalOperation)
        assertTrue(decision.requireReauthentication)
        assertTrue(decision.keepUiRedacted)
    }

    @Test
    fun criticalFindingBlocksCriticalActions() {
        val decision = LocalThreatPolicy.decisionForFindings(
            listOf(
                LocalThreatFinding(LocalThreatSignal.SIGNATURE_MISMATCH),
            ),
        )

        assertFalse(decision.allowNormalOperation)
        assertTrue(decision.requireReauthentication)
        assertTrue(decision.blockReveal)
        assertTrue(decision.blockExport)
        assertTrue(decision.keepUiRedacted)
        assertTrue(decision.showStrongWarning)
        assertTrue(decision.blockCriticalActions)
    }

    @Test
    fun lowFindingDoesNotActivateDegradedMode() {
        val decision = LocalThreatPolicy.decisionForFindings(
            listOf(
                LocalThreatFinding(LocalThreatSignal.UNKNOWN_INSTALLER),
            ),
        )

        assertTrue(decision.allowNormalOperation)
        assertFalse(decision.requireReauthentication)
        assertFalse(decision.blockExport)
    }

    @Test
    fun degradedDecisionRejectsContradictoryNormalOperation() {
        expectIllegalArgument {
            DegradedModeDecision(
                allowNormalOperation = true,
                requireReauthentication = true,
                blockReveal = false,
                blockExport = false,
                keepUiRedacted = false,
                showStrongWarning = false,
                blockCriticalActions = false,
            )
        }
        expectIllegalArgument {
            DegradedModeDecision(
                allowNormalOperation = true,
                requireReauthentication = false,
                blockReveal = true,
                blockExport = false,
                keepUiRedacted = false,
                showStrongWarning = false,
                blockCriticalActions = false,
            )
        }
        expectIllegalArgument {
            DegradedModeDecision(
                allowNormalOperation = true,
                requireReauthentication = false,
                blockReveal = false,
                blockExport = true,
                keepUiRedacted = false,
                showStrongWarning = false,
                blockCriticalActions = false,
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
