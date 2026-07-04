package com.vaultia.app.core.crypto.vault.item

import com.vaultia.app.core.crypto.vault.payload.SerializedEncryptedVaultPayload

class SerializedEncryptedVaultItem(
    val formatVersion: VaultItemSerializedFormatVersion,
    val id: String,
    val type: EncryptedVaultItemType,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val serializedPayload: SerializedEncryptedVaultPayload,
) {
    init {
        EncryptedVaultItemId(id)
        require(createdAtEpochMillis > 0L) {
            "Serialized encrypted vault item creation timestamp must be positive."
        }
        require(updatedAtEpochMillis > 0L) {
            "Serialized encrypted vault item update timestamp must be positive."
        }
        require(updatedAtEpochMillis >= createdAtEpochMillis) {
            "Serialized encrypted vault item update timestamp must not be earlier than creation timestamp."
        }
    }
}
