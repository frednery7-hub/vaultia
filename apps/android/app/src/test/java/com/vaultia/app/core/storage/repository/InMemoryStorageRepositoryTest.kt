package com.vaultia.app.core.storage.repository

import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata
import com.vaultia.app.core.storage.model.StorageRecordType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryStorageRepositoryTest {
    private fun metadata(
        idValue: String = "record_123456",
        pointerValue: String = "payload_123456.enc",
    ): StorageRecordMetadata {
        return StorageRecordMetadata(
            id = StorageRecordId.from(idValue),
            type = StorageRecordType.ITEM_METADATA,
            payloadPointer = EncryptedPayloadPointer.from(pointerValue),
            createdAtEpochMillis = 1_000L,
            updatedAtEpochMillis = 1_000L,
            formatVersion = 1,
        )
    }

    @Test
    fun savesAndFindsRecordById() {
        val repository = InMemoryStorageRepository()
        val metadata = metadata()

        val saveResult = repository.save(metadata)
        val findResult = repository.findById(metadata.id)

        assertTrue(saveResult is StorageRepositoryResult.Success)
        assertTrue(findResult is StorageRepositoryResult.Success)
        assertEquals(metadata, (findResult as StorageRepositoryResult.Success).value)
    }

    @Test
    fun returnsNullWhenRecordIsNotFound() {
        val repository = InMemoryStorageRepository()
        val result = repository.findById(StorageRecordId.from("record_999999"))

        assertTrue(result is StorageRepositoryResult.Success)
        assertNull((result as StorageRepositoryResult.Success).value)
    }

    @Test
    fun rejectsDuplicateRecordId() {
        val repository = InMemoryStorageRepository()
        val metadata = metadata()

        val first = repository.save(metadata)
        val second = repository.save(metadata)

        assertTrue(first is StorageRepositoryResult.Success)
        assertTrue(second is StorageRepositoryResult.Failure)
        assertEquals(
            StorageRepositoryError.DuplicateRecord,
            (second as StorageRepositoryResult.Failure).error,
        )
    }

    @Test
    fun listsRecordsInInsertionOrder() {
        val repository = InMemoryStorageRepository()
        val first = metadata(idValue = "record_111111", pointerValue = "payload_111111.enc")
        val second = metadata(idValue = "record_222222", pointerValue = "payload_222222.enc")

        repository.save(first)
        repository.save(second)

        val result = repository.listAll()

        assertTrue(result is StorageRepositoryResult.Success)
        assertEquals(listOf(first, second), (result as StorageRepositoryResult.Success).value)
    }

    @Test
    fun deletesRecordById() {
        val repository = InMemoryStorageRepository()
        val metadata = metadata()

        repository.save(metadata)

        val deleteResult = repository.deleteById(metadata.id)
        val findResult = repository.findById(metadata.id)

        assertTrue(deleteResult is StorageRepositoryResult.Success)
        assertTrue((deleteResult as StorageRepositoryResult.Success).value)
        assertTrue(findResult is StorageRepositoryResult.Success)
        assertNull((findResult as StorageRepositoryResult.Success).value)
    }

    @Test
    fun deleteReturnsFalseWhenRecordDoesNotExist() {
        val repository = InMemoryStorageRepository()

        val deleteResult = repository.deleteById(StorageRecordId.from("record_999999"))

        assertTrue(deleteResult is StorageRepositoryResult.Success)
        assertFalse((deleteResult as StorageRepositoryResult.Success).value)
    }

    @Test
    fun clearRemovesAllRecords() {
        val repository = InMemoryStorageRepository()

        repository.save(metadata(idValue = "record_111111", pointerValue = "payload_111111.enc"))
        repository.save(metadata(idValue = "record_222222", pointerValue = "payload_222222.enc"))

        val clearResult = repository.clear()
        val listResult = repository.listAll()

        assertTrue(clearResult is StorageRepositoryResult.Success)
        assertTrue(listResult is StorageRepositoryResult.Success)
        assertTrue((listResult as StorageRepositoryResult.Success).value.isEmpty())
    }
}
