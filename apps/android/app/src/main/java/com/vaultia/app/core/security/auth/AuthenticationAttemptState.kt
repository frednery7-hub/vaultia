package com.vaultia.app.core.security.auth

data class AuthenticationAttemptState(
    val failedAttempts: Int,
    val lastFailureAtEpochMillis: Long,
) {
    init {
        require(failedAttempts >= 0) {
            "Failed attempts cannot be negative."
        }
        require(lastFailureAtEpochMillis >= 0L) {
            "Last failure timestamp cannot be negative."
        }
    }

    companion object {
        fun clean(): AuthenticationAttemptState {
            return AuthenticationAttemptState(
                failedAttempts = 0,
                lastFailureAtEpochMillis = 0L,
            )
        }
    }
}
