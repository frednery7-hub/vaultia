package com.vaultia.app.core.security.threat

class LocalThreatAssessment(
    findings: List<LocalThreatFinding>,
) {
    val findings: List<LocalThreatFinding> = findings.toList()

    val riskScore: Int = this.findings.sumOf { finding ->
        finding.severity.riskPoints
    }

    val maximumSeverity: LocalThreatSeverity = this.findings
        .maxByOrNull { finding -> finding.severity.riskPoints }
        ?.severity
        ?: LocalThreatSeverity.LOW

    val hasFindings: Boolean = this.findings.isNotEmpty()

    val requiresDegradedMode: Boolean =
        this.findings.any { finding -> finding.severity == LocalThreatSeverity.HIGH } ||
            this.findings.any { finding -> finding.severity == LocalThreatSeverity.CRITICAL } ||
            riskScore >= DEGRADED_MODE_RISK_SCORE_THRESHOLD

    val blocksCriticalActions: Boolean =
        this.findings.any { finding -> finding.severity == LocalThreatSeverity.CRITICAL } ||
            riskScore >= CRITICAL_ACTION_BLOCK_RISK_SCORE_THRESHOLD

    fun containsSignal(signal: LocalThreatSignal): Boolean {
        return findings.any { finding -> finding.signal == signal }
    }

    companion object {
        const val DEGRADED_MODE_RISK_SCORE_THRESHOLD: Int = 6
        const val CRITICAL_ACTION_BLOCK_RISK_SCORE_THRESHOLD: Int = 10

        fun clean(): LocalThreatAssessment {
            return LocalThreatAssessment(emptyList())
        }
    }
}
