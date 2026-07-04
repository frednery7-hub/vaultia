package com.vaultia.app.core.crypto.vault.payload

sealed class VaultPayloadSerializationResult {
    class Success(val draft: EncryptedVaultPayloadDraft) : VaultPayloadSerializationResult()
    class Failure(val error: VaultPayloadSerializationError) : VaultPayloadSerializationResult()
}
