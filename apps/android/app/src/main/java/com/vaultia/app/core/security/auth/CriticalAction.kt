package com.vaultia.app.core.security.auth

enum class CriticalAction(
    val requiresFreshAuthentication: Boolean,
) {
    REVEAL_SECRET(
        requiresFreshAuthentication = true,
    ),
    COPY_SECRET(
        requiresFreshAuthentication = true,
    ),
    EDIT_ITEM(
        requiresFreshAuthentication = true,
    ),
    DELETE_ITEM(
        requiresFreshAuthentication = true,
    ),
    EXPORT_BACKUP(
        requiresFreshAuthentication = true,
    ),
    CHANGE_MASTER_PASSWORD(
        requiresFreshAuthentication = true,
    ),
    DISABLE_BIOMETRIC_CONVENIENCE(
        requiresFreshAuthentication = true,
    ),
    UNLOCK_VAULT(
        requiresFreshAuthentication = true,
    ),
}
