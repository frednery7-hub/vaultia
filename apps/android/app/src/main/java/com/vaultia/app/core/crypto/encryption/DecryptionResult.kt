package com.vaultia.app.core.crypto.encryption

sealed class DecryptionResult {
    class Success(val plaintextPayload: PlaintextPayload) : DecryptionResult()
    class Failure(val error: EncryptionError) : DecryptionResult()
}
