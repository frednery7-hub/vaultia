package com.vaultia.app.core.crypto.vault

class VaultCreationWithoutExternalSaltRequest(
    val masterPassword: String,
    val createdAtEpochMillis: Long,
) {
    init {
        require(createdAtEpochMillis > 0L) {
            "Vault creation timestamp must be positive."
        }
    }
}
