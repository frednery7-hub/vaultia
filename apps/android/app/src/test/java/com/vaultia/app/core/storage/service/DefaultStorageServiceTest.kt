package com.vaultia.app.core.storage.service

import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata
import com.vaultia.app.core.storage.model.StorageRecordType
import com.vaultia.app.core.storage.repository.InMemoryStorageRepository
import com.vaultia.app.core.storage.usecase.DeleteStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.FindStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.ListStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.SaveStorageRecordMetadataUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultStorageServiceTest {
    private fun service(): StorageService {
        val repository = InMemoryStorageRepository()

        return DefaultStorageService(
            saveStorageRecordMetadataUseCase = SaveStorageRecordMetadataUseCase(repository),
            findStorageRecordMetadataUseCase = FindStorageRecordMetadataUseCase(repository),
            listStorageRecordMetadataUseCase = ListStorageRecordMetadataUseCase(repository),
            deleteStorageRecordMetadataUseCase = DeleteStorageRecordMetadataUseCase(repository),
        )
    }

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
    fun serviceSavesAndFindsMetadata() {
        val service = service()
        val metadata = metadata()

        val saveResult = service.save(metadata)
        val findResult = service.findById(metadata.id)

        assertTrue(saveResult is StorageServiceResult.Success)
        assertTrue(findResult is StorageServiceResult.Success)
        assertEquals(metadata, (findResult as StorageServiceResult.Success).value)
    }

    @Test
    fun serviceMapsDuplicateRecordError() {
        val service = service()
        val metadata = metadata()

        val first = service.save(metadata)
        val second = service.save(metadata)

        assertTrue(first is StorageServiceResult.Success)
        assertTrue(second is StorageServiceResult.Failure)
        assertEquals(
            StorageServiceError.DuplicateRecord,
            (second as StorageServiceResult.Failure).error,
        )
    }

    @Test
    fun serviceReturnsNullWhenMetadataDoesNotExist() {
        val service = service()

        val result = service.findById(StorageRecordId.from("record_999999"))

        assertTrue(result is StorageServiceResult.Success)
        assertNull((result as StorageServiceResult.Success).value)
    }

    @Test
    fun serviceListsMetadataInRepositoryOrder() {
        val service = service()
        val first = metadata(idValue = "record_111111", pointerValue = "payload_111111.enc")
        val second = metadata(idValue = "record_222222", pointerValue = "payload_222222.enc")

        service.save(first)
        service.save(second)

        val result = service.listAll()

        assertTrue(result is StorageServiceResult.Success)
        assertEquals(listOf(first, second), (result as StorageServiceResult.Success).value)
    }

    @Test
    fun serviceDeletesExistingMetadata() {
        val service = service()
        val metadata = metadata()

        service.save(metadata)

        val deleteResult = service.deleteById(metadata.id)
        val findResult = service.findById(metadata.id)

        assertTrue(deleteResult is StorageServiceResult.Success)
        assertTrue(findResult is StorageServiceResult.Success)
        assertNull((findResult as StorageServiceResult.Success).value)
    }

    @Test
    fun serviceMapsDeleteMissingMetadataToRecordNotFound() {
        val service = service()

        val result = service.deleteById(StorageRecordId.from("record_999999"))

        assertTrue(result is StorageServiceResult.Failure)
        assertEquals(
            StorageServiceError.RecordNotFound,
            (result as StorageServiceResult.Failure).error,
        )
    }
}
