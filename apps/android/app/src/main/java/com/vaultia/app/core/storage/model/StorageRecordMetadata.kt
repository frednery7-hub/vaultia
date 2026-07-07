package com.vaultia.app.core.storage.model

data class StorageRecordMetadata(
    val id: StorageRecordId,
    val type: StorageRecordType,
    val payloadPointer: EncryptedPayloadPointer?,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val formatVersion: Int,
) {
    init {
        require(createdAtEpochMillis > 0L) { "Created timestamp must be positive." }
        require(updatedAtEpochMillis >= createdAtEpochMillis) {
            "Updated timestamp must be greater than or equal to created timestamp."
        }
        require(formatVersion > 0) { "Format version must be positive." }

        if (type == StorageRecordType.ITEM_METADATA ||
            type == StorageRecordType.ENCRYPTED_PAYLOAD ||
            type == StorageRecordType.ENCRYPTED_BINARY_OBJECT
        ) {
            require(payloadPointer != null) { "Payload pointer is required for this record type." }
        }

        if (type == StorageRecordType.VAULT_HEADER) {
            require(payloadPointer == null) { "Vault header metadata must not point to an encrypted payload." }
        }
    }
}
