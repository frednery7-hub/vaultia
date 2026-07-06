package com.vaultia.app.core.ui.security

class SensitiveRevealPolicy(
    val revealTimeoutMillis: Long,
) {
    init {
        require(revealTimeoutMillis > 0L) {
            "Reveal timeout must be positive."
        }
    }

    fun canRevealFrom(state: SecureUiState): Boolean {
        return state == SecureUiState.UNLOCKED_REDACTED
    }

    fun stateAfterRevealTimeout(state: SecureUiState): SecureUiState {
        return when (state) {
            SecureUiState.UNLOCKED_REVEALED -> SecureUiState.UNLOCKED_REDACTED
            else -> state
        }
    }

    fun stateAfterBackground(state: SecureUiState): SecureUiState {
        return when (state) {
            SecureUiState.UNLOCKED_REVEALED -> SecureUiState.UNLOCKED_REDACTED
            SecureUiState.UNLOCKED_REDACTED -> SecureUiState.LOCKING
            else -> state
        }
    }

    companion object {
        const val DEFAULT_REVEAL_TIMEOUT_MILLIS: Long = 30_000L

        fun default(): SensitiveRevealPolicy {
            return SensitiveRevealPolicy(DEFAULT_REVEAL_TIMEOUT_MILLIS)
        }
    }
}
