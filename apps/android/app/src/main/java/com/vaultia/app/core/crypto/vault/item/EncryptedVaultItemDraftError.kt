package com.vaultia.app.core.crypto.vault.item

sealed class EncryptedVaultItemDraftError {
    data object InvalidId : EncryptedVaultItemDraftError()
    data object InvalidTimestamp : EncryptedVaultItemDraftError()
    data object InvalidPayload : EncryptedVaultItemDraftError()
    data object InvalidMetadata : EncryptedVaultItemDraftError()
}
