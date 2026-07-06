package com.vaultia.app.core.security.threat

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalThreatAssessmentTest {
    @Test
    fun cleanAssessmentHasZeroRiskAndNormalFlags() {
        val assessment = LocalThreatAssessment.clean()

        assertFalse(assessment.hasFindings)
        assertEquals(0, assessment.riskScore)
        assertEquals(LocalThreatSeverity.LOW, assessment.maximumSeverity)
        assertFalse(assessment.requiresDegradedMode)
        assertFalse(assessment.blocksCriticalActions)
    }

    @Test
    fun assessmentProtectsFindingListFromExternalMutation() {
        val mutableFindings = mutableListOf(
            LocalThreatFinding(LocalThreatSignal.UNKNOWN_INSTALLER),
        )

        val assessment = LocalThreatAssessment(mutableFindings)
        mutableFindings.add(LocalThreatFinding(LocalThreatSignal.SIGNATURE_MISMATCH))

        assertEquals(1, assessment.findings.size)
        assertTrue(assessment.containsSignal(LocalThreatSignal.UNKNOWN_INSTALLER))
        assertFalse(assessment.containsSignal(LocalThreatSignal.SIGNATURE_MISMATCH))
    }

    @Test
    fun maximumSeverityUsesHighestFindingSeverity() {
        val assessment = LocalThreatAssessment(
            listOf(
                LocalThreatFinding(LocalThreatSignal.UNKNOWN_INSTALLER),
                LocalThreatFinding(LocalThreatSignal.OVERLAY_RISK),
                LocalThreatFinding(LocalThreatSignal.ROOT_DETECTED),
            ),
        )

        assertEquals(LocalThreatSeverity.HIGH, assessment.maximumSeverity)
    }

    @Test
    fun riskScoreSumsSeverityPoints() {
        val assessment = LocalThreatAssessment(
            listOf(
                LocalThreatFinding(LocalThreatSignal.UNKNOWN_INSTALLER),
                LocalThreatFinding(LocalThreatSignal.OVERLAY_RISK),
                LocalThreatFinding(LocalThreatSignal.ROOT_DETECTED),
            ),
        )

        assertEquals(10, assessment.riskScore)
    }

    @Test
    fun criticalFindingBlocksCriticalActions() {
        val assessment = LocalThreatAssessment(
            listOf(
                LocalThreatFinding(LocalThreatSignal.INTEGRITY_CHECK_FAILED),
            ),
        )

        assertTrue(assessment.requiresDegradedMode)
        assertTrue(assessment.blocksCriticalActions)
        assertEquals(LocalThreatSeverity.CRITICAL, assessment.maximumSeverity)
    }

    @Test
    fun multipleMediumFindingsElevateRiskToDegradedMode() {
        val assessment = LocalThreatAssessment(
            listOf(
                LocalThreatFinding(LocalThreatSignal.EMULATOR_ENVIRONMENT),
                LocalThreatFinding(LocalThreatSignal.OVERLAY_RISK),
            ),
        )

        assertEquals(6, assessment.riskScore)
        assertTrue(assessment.requiresDegradedMode)
        assertFalse(assessment.blocksCriticalActions)
    }
}
