package com.vaultia.app.core.crypto.encryption

class EncryptedPayload(
    nonce: ByteArray,
    ciphertext: ByteArray,
    authenticationTag: ByteArray,
) {
    private val nonceBytes: ByteArray = nonce.copyOf()
    private val ciphertextBytes: ByteArray = ciphertext.copyOf()
    private val authenticationTagBytes: ByteArray = authenticationTag.copyOf()

    init {
        require(nonceBytes.size == NONCE_LENGTH_BYTES) {
            "AES-GCM nonce must be $NONCE_LENGTH_BYTES bytes."
        }
        require(ciphertextBytes.isNotEmpty()) {
            "Ciphertext must not be empty."
        }
        require(authenticationTagBytes.size == AUTHENTICATION_TAG_LENGTH_BYTES) {
            "AES-GCM authentication tag must be $AUTHENTICATION_TAG_LENGTH_BYTES bytes."
        }
    }

    val nonceLengthBytes: Int
        get() = nonceBytes.size

    val ciphertextLengthBytes: Int
        get() = ciphertextBytes.size

    val authenticationTagLengthBytes: Int
        get() = authenticationTagBytes.size

    fun nonceCopy(): ByteArray = nonceBytes.copyOf()

    fun ciphertextCopy(): ByteArray = ciphertextBytes.copyOf()

    fun authenticationTagCopy(): ByteArray = authenticationTagBytes.copyOf()

    companion object {
        const val NONCE_LENGTH_BYTES: Int = 12
        const val AUTHENTICATION_TAG_LENGTH_BYTES: Int = 16
    }
}
