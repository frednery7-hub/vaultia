package com.vaultia.app.core.app

import com.vaultia.app.core.storage.composition.StorageCompositionRoot
import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata
import com.vaultia.app.core.storage.model.StorageRecordType
import com.vaultia.app.core.storage.service.StorageServiceError
import com.vaultia.app.core.storage.service.StorageServiceResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultiaAppContainerStorageIntegrationTest {
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
    fun appContainerExposesStorageServiceFromStorageContainer() {
        val storageContainer = StorageCompositionRoot.createInMemoryContainer()
        val appContainer = VaultiaAppContainer(storageContainer = storageContainer)

        assertSame(storageContainer.storageService, appContainer.storageService)
    }

    @Test
    fun appContainerDefaultStorageServiceCanSaveAndFindMetadata() {
        val appContainer = VaultiaAppContainer()
        val metadata = metadata()

        val saveResult = appContainer.storageService.save(metadata)
        val findResult = appContainer.storageService.findById(metadata.id)

        assertTrue(saveResult is StorageServiceResult.Success)
        assertTrue(findResult is StorageServiceResult.Success)
        assertEquals(metadata, (findResult as StorageServiceResult.Success).value)
    }

    @Test
    fun appContainerDefaultStorageServiceCanListMetadata() {
        val appContainer = VaultiaAppContainer()
        val first = metadata(idValue = "record_111111", pointerValue = "payload_111111.enc")
        val second = metadata(idValue = "record_222222", pointerValue = "payload_222222.enc")

        appContainer.storageService.save(first)
        appContainer.storageService.save(second)

        val listResult = appContainer.storageService.listAll()

        assertTrue(listResult is StorageServiceResult.Success)
        assertEquals(listOf(first, second), (listResult as StorageServiceResult.Success).value)
    }

    @Test
    fun appContainerDefaultStorageServiceCanDeleteMetadata() {
        val appContainer = VaultiaAppContainer()
        val metadata = metadata()

        appContainer.storageService.save(metadata)

        val deleteResult = appContainer.storageService.deleteById(metadata.id)
        val findResult = appContainer.storageService.findById(metadata.id)

        assertTrue(deleteResult is StorageServiceResult.Success)
        assertTrue(findResult is StorageServiceResult.Success)
        assertNull((findResult as StorageServiceResult.Success).value)
    }

    @Test
    fun appContainerStorageServiceMapsMissingDeleteToNotFound() {
        val appContainer = VaultiaAppContainer()

        val result = appContainer.storageService.deleteById(StorageRecordId.from("record_999999"))

        assertTrue(result is StorageServiceResult.Failure)
        assertEquals(
            StorageServiceError.RecordNotFound,
            (result as StorageServiceResult.Failure).error,
        )
    }

    @Test
    fun appContainersHaveIndependentDefaultStorageState() {
        val firstContainer = VaultiaAppContainer()
        val secondContainer = VaultiaAppContainer()
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
