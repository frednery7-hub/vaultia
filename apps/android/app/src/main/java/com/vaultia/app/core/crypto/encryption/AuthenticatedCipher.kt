package com.vaultia.app.core.crypto.encryption

interface AuthenticatedCipher {
    fun encrypt(
        key: ByteArray,
        plaintext: PlaintextPayload,
    ): EncryptionResult

    fun decrypt(
        key: ByteArray,
        encryptedPayload: EncryptedPayload,
    ): DecryptionResult
}
