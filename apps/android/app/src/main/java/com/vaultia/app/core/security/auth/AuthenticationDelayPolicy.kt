package com.vaultia.app.core.security.auth

class AuthenticationDelayPolicy(
    val strongTemporaryLockMillis: Long = DEFAULT_STRONG_TEMPORARY_LOCK_MILLIS,
) {
    init {
        require(strongTemporaryLockMillis > 0L) {
            "Strong temporary lock duration must be positive."
        }
    }

    fun delayMillisForFailures(failedAttempts: Int): Long {
        require(failedAttempts >= 0) {
            "Failed attempts cannot be negative."
        }

        return when (failedAttempts) {
            0 -> 0L
            1 -> 0L
            2 -> 30_000L
            3 -> 120_000L
            4 -> 600_000L
            else -> strongTemporaryLockMillis
        }
    }

    fun isTemporarilyLocked(failedAttempts: Int): Boolean {
        require(failedAttempts >= 0) {
            "Failed attempts cannot be negative."
        }

        return failedAttempts >= STRONG_LOCK_FAILURE_THRESHOLD
    }

    fun isDelayElapsed(
        failedAttempts: Int,
        lastFailureAtEpochMillis: Long,
        nowEpochMillis: Long,
    ): Boolean {
        require(lastFailureAtEpochMillis >= 0L) {
            "Last failure timestamp cannot be negative."
        }
        require(nowEpochMillis >= lastFailureAtEpochMillis) {
            "Now timestamp cannot be before the last failure timestamp."
        }

        val delayMillis = delayMillisForFailures(failedAttempts)
        return nowEpochMillis - lastFailureAtEpochMillis >= delayMillis
    }

    companion object {
        const val STRONG_LOCK_FAILURE_THRESHOLD: Int = 5
        const val DEFAULT_STRONG_TEMPORARY_LOCK_MILLIS: Long = 1_800_000L
    }
}
