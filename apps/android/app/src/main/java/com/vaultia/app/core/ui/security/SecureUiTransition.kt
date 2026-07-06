package com.vaultia.app.core.ui.security

data class SecureUiTransition(
    val from: SecureUiState,
    val action: SecureUiAction,
    val to: SecureUiState,
)
