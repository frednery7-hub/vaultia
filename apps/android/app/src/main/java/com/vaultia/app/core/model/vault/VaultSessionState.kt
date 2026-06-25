package com.vaultia.app.core.model.vault

/**
 * Local-only vault session state.
 *
 * This state does not represent authentication with a server.
 */
internal enum class VaultSessionState {
    NOT_INITIALIZED,
    LOCKED,
    UNLOCKED
}
