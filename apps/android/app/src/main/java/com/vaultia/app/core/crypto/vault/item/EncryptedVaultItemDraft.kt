package com.vaultia.app.core.crypto.vault.item

import com.vaultia.app.core.crypto.vault.payload.SerializedEncryptedVaultPayload

class EncryptedVaultItemDraft(
    val id: EncryptedVaultItemId,
    val type: EncryptedVaultItemType,
    val serializedPayload: SerializedEncryptedVaultPayload,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
) {
    init {
        require(createdAtEpochMillis > 0L) {
            "Encrypted vault item creation timestamp must be positive."
        }
        require(updatedAtEpochMillis > 0L) {
            "Encrypted vault item update timestamp must be positive."
        }
        require(updatedAtEpochMillis >= createdAtEpochMillis) {
            "Encrypted vault item update timestamp must not be earlier than creation timestamp."
        }
    }
}
