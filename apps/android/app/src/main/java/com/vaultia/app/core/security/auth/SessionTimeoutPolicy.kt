package com.vaultia.app.core.security.auth

class SessionTimeoutPolicy(
    val sessionTimeoutMillis: Long = DEFAULT_SESSION_TIMEOUT_MILLIS,
    val criticalActionFreshnessMillis: Long = DEFAULT_CRITICAL_ACTION_FRESHNESS_MILLIS,
) {
    init {
        require(sessionTimeoutMillis > 0L) {
            "Session timeout must be positive."
        }
        require(criticalActionFreshnessMillis > 0L) {
            "Critical action freshness window must be positive."
        }
        require(criticalActionFreshnessMillis <= sessionTimeoutMillis) {
            "Critical action freshness window cannot be longer than the session timeout."
        }
    }

    fun isSessionExpired(
        lastAuthenticatedAtEpochMillis: Long,
        nowEpochMillis: Long,
    ): Boolean {
        validateTimeWindow(
            earlierEpochMillis = lastAuthenticatedAtEpochMillis,
            nowEpochMillis = nowEpochMillis,
        )

        return nowEpochMillis - lastAuthenticatedAtEpochMillis > sessionTimeoutMillis
    }

    fun isFreshForCriticalAction(
        lastCriticalAuthenticationAtEpochMillis: Long,
        nowEpochMillis: Long,
    ): Boolean {
        validateTimeWindow(
            earlierEpochMillis = lastCriticalAuthenticationAtEpochMillis,
            nowEpochMillis = nowEpochMillis,
        )

        return nowEpochMillis - lastCriticalAuthenticationAtEpochMillis <= criticalActionFreshnessMillis
    }

    private fun validateTimeWindow(
        earlierEpochMillis: Long,
        nowEpochMillis: Long,
    ) {
        require(earlierEpochMillis >= 0L) {
            "Earlier timestamp cannot be negative."
        }
        require(nowEpochMillis >= earlierEpochMillis) {
            "Now timestamp cannot be before the earlier timestamp."
        }
    }

    companion object {
        const val DEFAULT_SESSION_TIMEOUT_MILLIS: Long = 300_000L
        const val DEFAULT_CRITICAL_ACTION_FRESHNESS_MILLIS: Long = 30_000L
    }
}
