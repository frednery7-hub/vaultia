package com.vaultia.app.core.crypto.vault.payload

sealed class VaultPayloadDecryptionError {
    data object InvalidRequest : VaultPayloadDecryptionError()
    data object AuthenticationFailed : VaultPayloadDecryptionError()
    data object DecryptionFailed : VaultPayloadDecryptionError()
}
