package com.vaultia.app.core.security.threat

data class DegradedModeDecision(
    val allowNormalOperation: Boolean,
    val requireReauthentication: Boolean,
    val blockReveal: Boolean,
    val blockExport: Boolean,
    val keepUiRedacted: Boolean,
    val showStrongWarning: Boolean,
    val blockCriticalActions: Boolean,
) {
    init {
        require((allowNormalOperation && requireReauthentication) == false) {
            "Normal operation cannot require reauthentication."
        }
        require((allowNormalOperation && blockReveal) == false) {
            "Normal operation cannot block reveal."
        }
        require((allowNormalOperation && blockExport) == false) {
            "Normal operation cannot block export."
        }
        require((allowNormalOperation && keepUiRedacted) == false) {
            "Normal operation cannot force redacted UI."
        }
        require((allowNormalOperation && showStrongWarning) == false) {
            "Normal operation cannot show strong warning."
        }
        require((allowNormalOperation && blockCriticalActions) == false) {
            "Normal operation cannot block critical actions."
        }
    }

    companion object {
        fun normal(): DegradedModeDecision {
            return DegradedModeDecision(
                allowNormalOperation = true,
                requireReauthentication = false,
                blockReveal = false,
                blockExport = false,
                keepUiRedacted = false,
                showStrongWarning = false,
                blockCriticalActions = false,
            )
        }

        fun degraded(blockCriticalActions: Boolean): DegradedModeDecision {
            return DegradedModeDecision(
                allowNormalOperation = false,
                requireReauthentication = true,
                blockReveal = true,
                blockExport = true,
                keepUiRedacted = true,
                showStrongWarning = true,
                blockCriticalActions = blockCriticalActions,
            )
        }
    }
}
