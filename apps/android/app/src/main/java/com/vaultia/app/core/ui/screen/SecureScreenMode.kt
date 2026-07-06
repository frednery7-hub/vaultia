package com.vaultia.app.core.ui.screen

enum class SecureScreenMode(
    val requiresSecureFlag: Boolean,
    val canRenderSensitiveContent: Boolean,
    val redactsContent: Boolean,
) {
    PUBLIC(
        requiresSecureFlag = false,
        canRenderSensitiveContent = false,
        redactsContent = false,
    ),
    REDACTED(
        requiresSecureFlag = false,
        canRenderSensitiveContent = false,
        redactsContent = true,
    ),
    SENSITIVE(
        requiresSecureFlag = true,
        canRenderSensitiveContent = true,
        redactsContent = false,
    ),
    LOCKED(
        requiresSecureFlag = false,
        canRenderSensitiveContent = false,
        redactsContent = true,
    ),
}
