package com.vaultia.app.core.crypto.encryption

sealed class EncryptionError {
    data object InvalidKey : EncryptionError()
    data object InvalidPlaintext : EncryptionError()
    data object InvalidEncryptedPayload : EncryptionError()
    data object EncryptionFailed : EncryptionError()
    data object AuthenticationFailed : EncryptionError()
    data object DecryptionFailed : EncryptionError()
    data object NonceGenerationFailed : EncryptionError()
}
