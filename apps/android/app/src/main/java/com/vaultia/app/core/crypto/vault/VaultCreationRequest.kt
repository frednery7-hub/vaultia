package com.vaultia.app.core.crypto.vault

class VaultCreationRequest(
    val masterPassword: String,
    salt: ByteArray,
    val createdAtEpochMillis: Long,
) {
    private val saltBytes: ByteArray = salt.copyOf()

    init {
        require(createdAtEpochMillis > 0L) {
            "Vault creation timestamp must be positive."
        }
    }

    fun saltCopy(): ByteArray = saltBytes.copyOf()
}
