package com.vaultia.app.core.crypto.vault.payload

import com.vaultia.app.core.crypto.encryption.EncryptedPayload

class VaultPayloadDecryptionRequest(
    key: ByteArray,
    encryptedPayload: EncryptedPayload,
    val decryptedAtEpochMillis: Long,
) {
    private val keyBytes: ByteArray = key.copyOf()
    val encryptedPayload: EncryptedPayload = EncryptedPayload(
        nonce = encryptedPayload.nonceCopy(),
        ciphertext = encryptedPayload.ciphertextCopy(),
        authenticationTag = encryptedPayload.authenticationTagCopy(),
    )

    init {
        require(keyBytes.isNotEmpty()) {
            "Vault payload decryption key must not be empty."
        }
        require(decryptedAtEpochMillis > 0L) {
            "Vault payload decryption timestamp must be positive."
        }
    }

    val keyLengthBytes: Int
        get() = keyBytes.size

    fun keyCopy(): ByteArray = keyBytes.copyOf()
}
