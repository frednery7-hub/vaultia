package com.vaultia.app.core.model.vault

/**
 * Supported Vaultia item categories planned for v1.
 *
 * These are metadata-level categories only.
 * No secret payload is represented in Foundation Phase 5.
 */
internal enum class VaultItemType {
    PASSWORD,
    SECURE_NOTE,
    PRIVATE_PHOTO,
    PRIVATE_DOCUMENT
}
