package com.vaultia.app.core.storage.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class StorageRecordMetadataTest {
    private val id = StorageRecordId.from("record_123456")
    private val pointer = EncryptedPayloadPointer.from("payload_123456.enc")

    @Test
    fun acceptsVaultHeaderWithoutPayloadPointer() {
        val metadata = StorageRecordMetadata(
            id = id,
            type = StorageRecordType.VAULT_HEADER,
            payloadPointer = null,
            createdAtEpochMillis = 1_000L,
            updatedAtEpochMillis = 1_000L,
            formatVersion = 1,
        )

        assertEquals(StorageRecordType.VAULT_HEADER, metadata.type)
        assertNull(metadata.payloadPointer)
    }

    @Test
    fun acceptsItemMetadataWithEncryptedPayloadPointer() {
        val metadata = StorageRecordMetadata(
            id = id,
            type = StorageRecordType.ITEM_METADATA,
            payloadPointer = pointer,
            createdAtEpochMillis = 1_000L,
            updatedAtEpochMillis = 2_000L,
            formatVersion = 1,
        )

        assertEquals(pointer, metadata.payloadPointer)
    }

    @Test
    fun rejectsPayloadRecordsWithoutPointer() {
        assertThrows(IllegalArgumentException::class.java) {
            StorageRecordMetadata(
                id = id,
                type = StorageRecordType.ENCRYPTED_PAYLOAD,
                payloadPointer = null,
                createdAtEpochMillis = 1_000L,
                updatedAtEpochMillis = 1_000L,
                formatVersion = 1,
            )
        }
    }

    @Test
    fun rejectsVaultHeaderWithPayloadPointer() {
        assertThrows(IllegalArgumentException::class.java) {
            StorageRecordMetadata(
                id = id,
                type = StorageRecordType.VAULT_HEADER,
                payloadPointer = pointer,
                createdAtEpochMillis = 1_000L,
                updatedAtEpochMillis = 1_000L,
                formatVersion = 1,
            )
        }
    }

    @Test
    fun rejectsInvalidTimestampsAndFormatVersion() {
        assertThrows(IllegalArgumentException::class.java) {
            StorageRecordMetadata(
                id = id,
                type = StorageRecordType.ITEM_METADATA,
                payloadPointer = pointer,
                createdAtEpochMillis = 2_000L,
                updatedAtEpochMillis = 1_000L,
                formatVersion = 1,
            )
        }

        assertThrows(IllegalArgumentException::class.java) {
            StorageRecordMetadata(
                id = id,
                type = StorageRecordType.ITEM_METADATA,
                payloadPointer = pointer,
                createdAtEpochMillis = 1_000L,
                updatedAtEpochMillis = 1_000L,
                formatVersion = 0,
            )
        }
    }
}
