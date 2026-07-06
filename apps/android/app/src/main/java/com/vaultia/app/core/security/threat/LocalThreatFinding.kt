package com.vaultia.app.core.security.threat

data class LocalThreatFinding(
    val signal: LocalThreatSignal,
    val severity: LocalThreatSeverity = signal.defaultSeverity,
)
