package com.vaultia.app.core.crypto.encryption

sealed class EncryptionResult {
    class Success(val encryptedPayload: EncryptedPayload) : EncryptionResult()
    class Failure(val error: EncryptionError) : EncryptionResult()
}
