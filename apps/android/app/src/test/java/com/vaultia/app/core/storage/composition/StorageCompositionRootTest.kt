package com.vaultia.app.core.storage.composition

import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata
import com.vaultia.app.core.storage.model.StorageRecordType
import com.vaultia.app.core.storage.service.StorageService
import com.vaultia.app.core.storage.service.StorageServiceError
import com.vaultia.app.core.storage.service.StorageServiceResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageCompositionRootTest {
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
    fun createsContainerWithStorageServiceBoundary() {
        val container = StorageCompositionRoot.createInMemoryContainer()

        assertTrue(container.storageService is StorageService)
    }

    @Test
    fun containerStorageServiceCanSaveAndFindMetadata() {
        val container = StorageCompositionRoot.createInMemoryContainer()
        val metadata = metadata()

        val saveResult = container.storageService.save(metadata)
        val findResult = container.storageService.findById(metadata.id)

        assertTrue(saveResult is StorageServiceResult.Success)
        assertTrue(findResult is StorageServiceResult.Success)
        assertEquals(metadata, (findResult as StorageServiceResult.Success).value)
    }

    @Test
    fun containerStorageServiceCanListMetadata() {
        val container = StorageCompositionRoot.createInMemoryContainer()
        val first = metadata(idValue = "record_111111", pointerValue = "payload_111111.enc")
        val second = metadata(idValue = "record_222222", pointerValue = "payload_222222.enc")

        container.storageService.save(first)
        container.storageService.save(second)

        val listResult = container.storageService.listAll()

        assertTrue(listResult is StorageServiceResult.Success)
        assertEquals(listOf(first, second), (listResult as StorageServiceResult.Success).value)
    }

    @Test
    fun containerStorageServiceCanDeleteMetadata() {
        val container = StorageCompositionRoot.createInMemoryContainer()
        val metadata = metadata()

        container.storageService.save(metadata)

        val deleteResult = container.storageService.deleteById(metadata.id)
        val findResult = container.storageService.findById(metadata.id)

        assertTrue(deleteResult is StorageServiceResult.Success)
        assertTrue(findResult is StorageServiceResult.Success)
        assertNull((findResult as StorageServiceResult.Success).value)
    }

    @Test
    fun containerStorageServiceMapsMissingDeleteToNotFound() {
        val container = StorageCompositionRoot.createInMemoryContainer()

        val result = container.storageService.deleteById(StorageRecordId.from("record_999999"))

        assertTrue(result is StorageServiceResult.Failure)
        assertEquals(
            StorageServiceError.RecordNotFound,
            (result as StorageServiceResult.Failure).error,
        )
    }

    @Test
    fun eachInMemoryContainerHasIndependentRepositoryState() {
        val firstContainer = StorageCompositionRoot.createInMemoryContainer()
        val secondContainer = StorageCompositionRoot.createInMemoryContainer()
        val metadata = metadata()

        firstContainer.storageService.save(metadata)

        val firstFindResult = firstContainer.storageService.findById(metadata.id)
        val secondFindResult = secondContainer.storageService.findById(metadata.id)

        assertNotSame(firstContainer.storageService, secondContainer.storageService)
        assertTrue(firstFindResult is StorageServiceResult.Success)
        assertTrue(secondFindResult is StorageServiceResult.Success)
        assertEquals(metadata, (firstFindResult as StorageServiceResult.Success).value)
        assertNull((secondFindResult as StorageServiceResult.Success).value)
    }
}
