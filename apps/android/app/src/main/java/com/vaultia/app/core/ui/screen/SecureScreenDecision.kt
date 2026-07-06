package com.vaultia.app.core.ui.screen

data class SecureScreenDecision(
    val nextMode: SecureScreenMode,
    val requireSecureFlag: Boolean,
    val clearSecureFlag: Boolean,
    val redactContent: Boolean,
    val startVisualLock: Boolean,
    val allowReveal: Boolean,
) {
    init {
        require(!(requireSecureFlag && clearSecureFlag)) {
            "Secure flag cannot be required and cleared at the same time."
        }
        require(!(allowReveal && nextMode.canRenderSensitiveContent == false)) {
            "Reveal cannot be allowed when the next mode cannot render sensitive content."
        }
    }
}
