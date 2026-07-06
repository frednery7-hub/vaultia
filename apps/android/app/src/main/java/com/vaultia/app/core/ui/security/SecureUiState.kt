package com.vaultia.app.core.ui.security

enum class SecureUiState(
    val canRenderVaultList: Boolean,
    val canRenderSensitiveValue: Boolean,
    val requiresRedaction: Boolean,
) {
    BOOTSTRAPPING(
        canRenderVaultList = false,
        canRenderSensitiveValue = false,
        requiresRedaction = true,
    ),
    LOCKED(
        canRenderVaultList = false,
        canRenderSensitiveValue = false,
        requiresRedaction = true,
    ),
    AUTHENTICATING(
        canRenderVaultList = false,
        canRenderSensitiveValue = false,
        requiresRedaction = true,
    ),
    UNLOCKED_REDACTED(
        canRenderVaultList = true,
        canRenderSensitiveValue = false,
        requiresRedaction = true,
    ),
    UNLOCKED_REVEALED(
        canRenderVaultList = true,
        canRenderSensitiveValue = true,
        requiresRedaction = false,
    ),
    LOCKING(
        canRenderVaultList = false,
        canRenderSensitiveValue = false,
        requiresRedaction = true,
    ),
    ERROR_REDACTED(
        canRenderVaultList = false,
        canRenderSensitiveValue = false,
        requiresRedaction = true,
    ),
}
