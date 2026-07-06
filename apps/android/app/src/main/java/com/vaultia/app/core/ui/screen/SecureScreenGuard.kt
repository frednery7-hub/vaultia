package com.vaultia.app.core.ui.screen

import com.vaultia.app.core.ui.security.SecureUiState

object SecureScreenGuard {
    fun decisionForState(state: SecureUiState): SecureScreenDecision {
        val mode = SecureScreenPolicy.modeForUiState(state)
        return decisionForMode(mode = mode, allowReveal = mode == SecureScreenMode.SENSITIVE)
    }

    fun decisionForEvent(
        currentMode: SecureScreenMode,
        event: SecureScreenEvent,
    ): SecureScreenDecision {
        return when (event) {
            SecureScreenEvent.APP_FOREGROUND -> decisionForMode(currentMode)
            SecureScreenEvent.APP_BACKGROUND -> decisionForBackground(currentMode)
            SecureScreenEvent.APP_PAUSE -> decisionForBackground(currentMode)
            SecureScreenEvent.APP_STOP -> decisionForBackground(currentMode)
            SecureScreenEvent.SENSITIVE_SCREEN_OPENED -> decisionForMode(SecureScreenMode.REDACTED)
            SecureScreenEvent.SENSITIVE_SCREEN_CLOSED -> decisionForMode(SecureScreenMode.REDACTED)
            SecureScreenEvent.SECRET_REVEALED -> decisionForMode(SecureScreenMode.SENSITIVE, allowReveal = true)
            SecureScreenEvent.SECRET_REDACTED -> decisionForMode(SecureScreenMode.REDACTED)
            SecureScreenEvent.LOCK_REQUESTED -> decisionForMode(SecureScreenMode.LOCKED, startVisualLock = true)
        }
    }

    private fun decisionForBackground(currentMode: SecureScreenMode): SecureScreenDecision {
        return when (currentMode) {
            SecureScreenMode.SENSITIVE -> decisionForMode(SecureScreenMode.REDACTED)
            SecureScreenMode.REDACTED -> decisionForMode(SecureScreenMode.LOCKED, startVisualLock = true)
            SecureScreenMode.PUBLIC -> decisionForMode(SecureScreenMode.PUBLIC)
            SecureScreenMode.LOCKED -> decisionForMode(SecureScreenMode.LOCKED)
        }
    }

    private fun decisionForMode(
        mode: SecureScreenMode,
        allowReveal: Boolean = false,
        startVisualLock: Boolean = false,
    ): SecureScreenDecision {
        return SecureScreenDecision(
            nextMode = mode,
            requireSecureFlag = mode.requiresSecureFlag,
            clearSecureFlag = mode.requiresSecureFlag == false,
            redactContent = mode.redactsContent,
            startVisualLock = startVisualLock,
            allowReveal = allowReveal,
        )
    }
}
