package com.vaultia.app.core.crypto.vault.payload

sealed class VaultPayloadEncryptionResult {
    class Success(val draft: EncryptedVaultPayloadDraft) : VaultPayloadEncryptionResult()
    class Failure(val error: VaultPayloadEncryptionError) : VaultPayloadEncryptionResult()
}
