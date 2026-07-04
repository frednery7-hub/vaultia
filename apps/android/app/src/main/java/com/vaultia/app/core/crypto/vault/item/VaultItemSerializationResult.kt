package com.vaultia.app.core.crypto.vault.item

sealed class VaultItemSerializationResult {
    class Success(val draft: EncryptedVaultItemDraft) : VaultItemSerializationResult()
    class Failure(val error: VaultItemSerializationError) : VaultItemSerializationResult()
}
