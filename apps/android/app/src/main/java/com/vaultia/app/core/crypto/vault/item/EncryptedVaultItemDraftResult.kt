package com.vaultia.app.core.crypto.vault.item

sealed class EncryptedVaultItemDraftResult {
    class Success(val draft: EncryptedVaultItemDraft) : EncryptedVaultItemDraftResult()
    class Failure(val error: EncryptedVaultItemDraftError) : EncryptedVaultItemDraftResult()
}
