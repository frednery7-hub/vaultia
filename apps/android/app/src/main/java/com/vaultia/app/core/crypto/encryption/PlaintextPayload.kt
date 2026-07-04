package com.vaultia.app.core.crypto.encryption

class PlaintextPayload(
    plaintext: ByteArray,
) {
    private val plaintextBytes: ByteArray = plaintext.copyOf()

    init {
        require(plaintextBytes.isNotEmpty()) {
            "Plaintext payload must not be empty."
        }
    }

    val plaintextLengthBytes: Int
        get() = plaintextBytes.size

    fun plaintextCopy(): ByteArray = plaintextBytes.copyOf()
}
