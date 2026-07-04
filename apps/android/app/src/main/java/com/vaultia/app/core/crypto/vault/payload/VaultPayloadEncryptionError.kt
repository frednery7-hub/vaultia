package com.vaultia.app.core.crypto.vault.payload

sealed class VaultPayloadEncryptionError {
    data object InvalidRequest : VaultPayloadEncryptionError()
    data object EncryptionFailed : VaultPayloadEncryptionError()
}
