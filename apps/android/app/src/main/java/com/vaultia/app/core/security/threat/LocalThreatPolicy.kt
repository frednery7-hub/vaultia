package com.vaultia.app.core.security.threat

object LocalThreatPolicy {
    fun assess(findings: List<LocalThreatFinding>): LocalThreatAssessment {
        return LocalThreatAssessment(findings)
    }

    fun decisionForAssessment(assessment: LocalThreatAssessment): DegradedModeDecision {
        return if (assessment.requiresDegradedMode) {
            DegradedModeDecision.degraded(
                blockCriticalActions = assessment.blocksCriticalActions,
            )
        } else {
            DegradedModeDecision.normal()
        }
    }

    fun decisionForFindings(findings: List<LocalThreatFinding>): DegradedModeDecision {
        return decisionForAssessment(assess(findings))
    }
}
