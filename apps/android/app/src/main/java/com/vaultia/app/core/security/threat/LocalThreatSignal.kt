package com.vaultia.app.core.security.threat

enum class LocalThreatSignal(
    val defaultSeverity: LocalThreatSeverity,
) {
    ROOT_DETECTED(
        defaultSeverity = LocalThreatSeverity.HIGH,
    ),
    DEBUGGER_ATTACHED(
        defaultSeverity = LocalThreatSeverity.HIGH,
    ),
    EMULATOR_ENVIRONMENT(
        defaultSeverity = LocalThreatSeverity.MEDIUM,
    ),
    HOOKING_FRAMEWORK_INDICATOR(
        defaultSeverity = LocalThreatSeverity.HIGH,
    ),
    APP_DEBUGGABLE(
        defaultSeverity = LocalThreatSeverity.MEDIUM,
    ),
    SIGNATURE_MISMATCH(
        defaultSeverity = LocalThreatSeverity.CRITICAL,
    ),
    OVERLAY_RISK(
        defaultSeverity = LocalThreatSeverity.MEDIUM,
    ),
    ACCESSIBILITY_RISK(
        defaultSeverity = LocalThreatSeverity.MEDIUM,
    ),
    UNKNOWN_INSTALLER(
        defaultSeverity = LocalThreatSeverity.LOW,
    ),
    INTEGRITY_CHECK_FAILED(
        defaultSeverity = LocalThreatSeverity.CRITICAL,
    ),
}
