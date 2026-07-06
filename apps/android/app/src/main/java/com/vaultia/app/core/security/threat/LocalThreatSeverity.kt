package com.vaultia.app.core.security.threat

enum class LocalThreatSeverity(
    val riskPoints: Int,
) {
    LOW(
        riskPoints = 1,
    ),
    MEDIUM(
        riskPoints = 3,
    ),
    HIGH(
        riskPoints = 6,
    ),
    CRITICAL(
        riskPoints = 10,
    ),
}
