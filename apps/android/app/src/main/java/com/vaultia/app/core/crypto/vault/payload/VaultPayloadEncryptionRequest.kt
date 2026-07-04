package com.vaultia.app.core.crypto.vault.payload

class VaultPayloadEncryptionRequest(
    key: ByteArray,
    plaintext: ByteArray,
    val createdAtEpochMillis: Long,
) {
    private val keyBytes: ByteArray = key.copyOf()
    private val plaintextBytes: ByteArray = plaintext.copyOf()

    init {
        require(keyBytes.isNotEmpty()) {
            "Vault payload encryption key must not be empty."
        }
        require(plaintextBytes.isNotEmpty()) {
            "Vault payload plaintext must not be empty."
        }
        require(createdAtEpochMillis > 0L) {
            "Vault payload timestamp must be positive."
        }
    }

    val keyLengthBytes: Int
        get() = keyBytes.size

    val plaintextLengthBytes: Int
        get() = plaintextBytes.size

    fun keyCopy(): ByteArray = keyBytes.copyOf()

    fun plaintextCopy(): ByteArray = plaintextBytes.copyOf()
}
