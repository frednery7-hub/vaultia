package com.vaultia.app.core.security.auth

data class CriticalActionDecision(
    val action: CriticalAction,
    val allowed: Boolean,
    val requiresReauthentication: Boolean,
    val blockReason: CriticalActionBlockReason,
) {
    init {
        require((allowed && requiresReauthentication) == false) {
            "Action cannot be allowed while requiring reauthentication."
        }
        require((allowed && blockReason != CriticalActionBlockReason.NONE) == false) {
            "Allowed action cannot have a block reason."
        }
        require((requiresReauthentication && blockReason != CriticalActionBlockReason.NONE) == false) {
            "Reauthentication request cannot also be blocked."
        }
    }

    companion object {
        fun allowed(action: CriticalAction): CriticalActionDecision {
            return CriticalActionDecision(
                action = action,
                allowed = true,
                requiresReauthentication = false,
                blockReason = CriticalActionBlockReason.NONE,
            )
        }

        fun requiresReauthentication(action: CriticalAction): CriticalActionDecision {
            return CriticalActionDecision(
                action = action,
                allowed = false,
                requiresReauthentication = true,
                blockReason = CriticalActionBlockReason.NONE,
            )
        }

        fun blocked(
            action: CriticalAction,
            reason: CriticalActionBlockReason,
        ): CriticalActionDecision {
            require(reason != CriticalActionBlockReason.NONE) {
                "Blocked action requires a concrete block reason."
            }
            return CriticalActionDecision(
                action = action,
                allowed = false,
                requiresReauthentication = false,
                blockReason = reason,
            )
        }
    }
}

enum class CriticalActionBlockReason {
    NONE,
    SESSION_LOCKED,
    SESSION_TIMEOUT,
    TOO_MANY_FAILURES,
}
