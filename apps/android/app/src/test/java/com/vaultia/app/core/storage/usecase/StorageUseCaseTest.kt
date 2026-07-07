package com.vaultia.app.core.storage.usecase

import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata
import com.vaultia.app.core.storage.model.StorageRecordType
import com.vaultia.app.core.storage.repository.InMemoryStorageRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageUseCaseTest {
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
    fun saveUseCaseStoresMetadata() {
        val repository = InMemoryStorageRepository()
        val saveUseCase = SaveStorageRecordMetadataUseCase(repository)
        val findUseCase = FindStorageRecordMetadataUseCase(repository)
        val metadata = metadata()

        val saveResult = saveUseCase.execute(metadata)
        val findResult = findUseCase.execute(metadata.id)

        assertTrue(saveResult is StorageUseCaseResult.Success)
        assertTrue(findResult is StorageUseCaseResult.Success)
        assertEquals(metadata, (findResult as StorageUseCaseResult.Success).value)
    }

    @Test
    fun saveUseCaseMapsDuplicateRecordError() {
        val repository = InMemoryStorageRepository()
        val saveUseCase = SaveStorageRecordMetadataUseCase(repository)
        val metadata = metadata()

        val first = saveUseCase.execute(metadata)
        val second = saveUseCase.execute(metadata)

        assertTrue(first is StorageUseCaseResult.Success)
        assertTrue(second is StorageUseCaseResult.Failure)
        assertEquals(
            StorageUseCaseError.DuplicateRecord,
            (second as StorageUseCaseResult.Failure).error,
        )
    }

    @Test
    fun findUseCaseReturnsNullWhenMetadataDoesNotExist() {
        val repository = InMemoryStorageRepository()
        val findUseCase = FindStorageRecordMetadataUseCase(repository)

        val result = findUseCase.execute(StorageRecordId.from("record_999999"))

        assertTrue(result is StorageUseCaseResult.Success)
        assertNull((result as StorageUseCaseResult.Success).value)
    }

    @Test
    fun listUseCaseReturnsMetadataInRepositoryOrder() {
        val repository = InMemoryStorageRepository()
        val saveUseCase = SaveStorageRecordMetadataUseCase(repository)
        val listUseCase = ListStorageRecordMetadataUseCase(repository)
        val first = metadata(idValue = "record_111111", pointerValue = "payload_111111.enc")
        val second = metadata(idValue = "record_222222", pointerValue = "payload_222222.enc")

        saveUseCase.execute(first)
        saveUseCase.execute(second)

        val result = listUseCase.execute()

        assertTrue(result is StorageUseCaseResult.Success)
        assertEquals(listOf(first, second), (result as StorageUseCaseResult.Success).value)
    }

    @Test
    fun deleteUseCaseRemovesExistingMetadata() {
        val repository = InMemoryStorageRepository()
        val saveUseCase = SaveStorageRecordMetadataUseCase(repository)
        val findUseCase = FindStorageRecordMetadataUseCase(repository)
        val deleteUseCase = DeleteStorageRecordMetadataUseCase(repository)
        val metadata = metadata()

        saveUseCase.execute(metadata)

        val deleteResult = deleteUseCase.execute(metadata.id)
        val findResult = findUseCase.execute(metadata.id)

        assertTrue(deleteResult is StorageUseCaseResult.Success)
        assertTrue(findResult is StorageUseCaseResult.Success)
        assertNull((findResult as StorageUseCaseResult.Success).value)
    }

    @Test
    fun deleteUseCaseReturnsNotFoundWhenMetadataDoesNotExist() {
        val repository = InMemoryStorageRepository()
        val deleteUseCase = DeleteStorageRecordMetadataUseCase(repository)

        val result = deleteUseCase.execute(StorageRecordId.from("record_999999"))

        assertTrue(result is StorageUseCaseResult.Failure)
        assertEquals(
            StorageUseCaseError.RecordNotFound,
            (result as StorageUseCaseResult.Failure).error,
        )
    }
}
