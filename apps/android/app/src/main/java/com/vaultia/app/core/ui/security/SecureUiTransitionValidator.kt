package com.vaultia.app.core.ui.security

object SecureUiTransitionValidator {
    private val allowedTransitions = setOf(
        SecureUiTransition(
            from = SecureUiState.BOOTSTRAPPING,
            action = SecureUiAction.LOCK_COMPLETED,
            to = SecureUiState.LOCKED,
        ),
        SecureUiTransition(
            from = SecureUiState.LOCKED,
            action = SecureUiAction.START_AUTHENTICATION,
            to = SecureUiState.AUTHENTICATING,
        ),
        SecureUiTransition(
            from = SecureUiState.AUTHENTICATING,
            action = SecureUiAction.AUTHENTICATION_SUCCEEDED,
            to = SecureUiState.UNLOCKED_REDACTED,
        ),
        SecureUiTransition(
            from = SecureUiState.AUTHENTICATING,
            action = SecureUiAction.AUTHENTICATION_FAILED,
            to = SecureUiState.ERROR_REDACTED,
        ),
        SecureUiTransition(
            from = SecureUiState.ERROR_REDACTED,
            action = SecureUiAction.LOCK_COMPLETED,
            to = SecureUiState.LOCKED,
        ),
        SecureUiTransition(
            from = SecureUiState.UNLOCKED_REDACTED,
            action = SecureUiAction.REQUEST_REVEAL,
            to = SecureUiState.UNLOCKED_REVEALED,
        ),
        SecureUiTransition(
            from = SecureUiState.UNLOCKED_REVEALED,
            action = SecureUiAction.HIDE_SECRET,
            to = SecureUiState.UNLOCKED_REDACTED,
        ),
        SecureUiTransition(
            from = SecureUiState.UNLOCKED_REVEALED,
            action = SecureUiAction.REVEAL_TIMEOUT,
            to = SecureUiState.UNLOCKED_REDACTED,
        ),
        SecureUiTransition(
            from = SecureUiState.UNLOCKED_REDACTED,
            action = SecureUiAction.REQUEST_LOCK,
            to = SecureUiState.LOCKING,
        ),
        SecureUiTransition(
            from = SecureUiState.UNLOCKED_REVEALED,
            action = SecureUiAction.REQUEST_LOCK,
            to = SecureUiState.LOCKING,
        ),
        SecureUiTransition(
            from = SecureUiState.UNLOCKED_REDACTED,
            action = SecureUiAction.APP_BACKGROUND,
            to = SecureUiState.LOCKING,
        ),
        SecureUiTransition(
            from = SecureUiState.UNLOCKED_REVEALED,
            action = SecureUiAction.APP_BACKGROUND,
            to = SecureUiState.UNLOCKED_REDACTED,
        ),
        SecureUiTransition(
            from = SecureUiState.LOCKING,
            action = SecureUiAction.LOCK_COMPLETED,
            to = SecureUiState.LOCKED,
        ),
    )

    fun isAllowed(transition: SecureUiTransition): Boolean {
        return transition in allowedTransitions
    }

    fun isAllowed(
        from: SecureUiState,
        action: SecureUiAction,
        to: SecureUiState,
    ): Boolean {
        return isAllowed(
            SecureUiTransition(
                from = from,
                action = action,
                to = to,
            ),
        )
    }
}
