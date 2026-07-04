package com.vaultia.app.core.crypto.vault.item

import com.vaultia.app.core.crypto.vault.payload.SerializedEncryptedVaultPayload

object EncryptedVaultItemDraftFactory {
    fun create(
        id: String,
        type: EncryptedVaultItemType,
        serializedPayload: SerializedEncryptedVaultPayload,
        createdAtEpochMillis: Long,
        updatedAtEpochMillis: Long,
    ): EncryptedVaultItemDraftResult {
        val itemId = try {
            EncryptedVaultItemId(id)
        } catch (_: IllegalArgumentException) {
            return EncryptedVaultItemDraftResult.Failure(EncryptedVaultItemDraftError.InvalidId)
        }

        if (createdAtEpochMillis <= 0L || updatedAtEpochMillis <= 0L || updatedAtEpochMillis < createdAtEpochMillis) {
            return EncryptedVaultItemDraftResult.Failure(EncryptedVaultItemDraftError.InvalidTimestamp)
        }

        val draft = try {
            EncryptedVaultItemDraft(
                id = itemId,
                type = type,
                serializedPayload = serializedPayload,
                createdAtEpochMillis = createdAtEpochMillis,
                updatedAtEpochMillis = updatedAtEpochMillis,
            )
        } catch (_: IllegalArgumentException) {
            return EncryptedVaultItemDraftResult.Failure(EncryptedVaultItemDraftError.InvalidMetadata)
        }

        return EncryptedVaultItemDraftResult.Success(draft)
    }
}
