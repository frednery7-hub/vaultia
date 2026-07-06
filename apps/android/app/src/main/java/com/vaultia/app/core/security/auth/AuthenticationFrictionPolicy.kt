package com.vaultia.app.core.security.auth

class AuthenticationFrictionPolicy(
    private val delayPolicy: AuthenticationDelayPolicy = AuthenticationDelayPolicy(),
    private val sessionTimeoutPolicy: SessionTimeoutPolicy = SessionTimeoutPolicy(),
) {
    fun decisionForAction(
        action: CriticalAction,
        isVaultUnlocked: Boolean,
        attemptState: AuthenticationAttemptState,
        lastAuthenticatedAtEpochMillis: Long,
        lastCriticalAuthenticationAtEpochMillis: Long,
        nowEpochMillis: Long,
    ): CriticalActionDecision {
        if (delayPolicy.isTemporarilyLocked(attemptState.failedAttempts)) {
            return CriticalActionDecision.blocked(
                action = action,
                reason = CriticalActionBlockReason.TOO_MANY_FAILURES,
            )
        }

        if (delayPolicy.isDelayElapsed(
                failedAttempts = attemptState.failedAttempts,
                lastFailureAtEpochMillis = attemptState.lastFailureAtEpochMillis,
                nowEpochMillis = nowEpochMillis,
            ) == false
        ) {
            return CriticalActionDecision.blocked(
                action = action,
                reason = CriticalActionBlockReason.TOO_MANY_FAILURES,
            )
        }

        if (action == CriticalAction.UNLOCK_VAULT) {
            return CriticalActionDecision.requiresReauthentication(action)
        }

        if (isVaultUnlocked == false) {
            return CriticalActionDecision.blocked(
                action = action,
                reason = CriticalActionBlockReason.SESSION_LOCKED,
            )
        }

        if (sessionTimeoutPolicy.isSessionExpired(
                lastAuthenticatedAtEpochMillis = lastAuthenticatedAtEpochMillis,
                nowEpochMillis = nowEpochMillis,
            )
        ) {
            return CriticalActionDecision.blocked(
                action = action,
                reason = CriticalActionBlockReason.SESSION_TIMEOUT,
            )
        }

        if (
            action.requiresFreshAuthentication &&
            sessionTimeoutPolicy.isFreshForCriticalAction(
                lastCriticalAuthenticationAtEpochMillis = lastCriticalAuthenticationAtEpochMillis,
                nowEpochMillis = nowEpochMillis,
            ) == false
        ) {
            return CriticalActionDecision.requiresReauthentication(action)
        }

        return CriticalActionDecision.allowed(action)
    }
}
