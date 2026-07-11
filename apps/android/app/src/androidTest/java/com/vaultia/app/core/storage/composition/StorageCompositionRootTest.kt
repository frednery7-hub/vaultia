package com.vaultia.app.core.storage.composition

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata
import com.vaultia.app.core.storage.model.StorageRecordType
import com.vaultia.app.core.storage.service.StorageServiceResult
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StorageCompositionRootTest {

    @After
    fun tearDown() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("vaultia_local_metadata.db")
    }

    @Test
    fun createLocalContainerInstantiatesRealDatabaseAndRepository() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = StorageCompositionRoot.createLocalContainer(context)
        
        val metadata = StorageRecordMetadata(
            id = StorageRecordId.from("test_root_123"),
            type = StorageRecordType.ITEM_METADATA,
            payloadPointer = EncryptedPayloadPointer.from("test_payload_abcd.enc"),
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 1000L,
            formatVersion = 1
        )
        
        val saveResult = container.storageService.save(metadata)
        assertTrue(saveResult is StorageServiceResult.Success)
        
        val listResult = container.storageService.listAll()
        assertTrue(listResult is StorageServiceResult.Success)
        assertTrue((listResult as StorageServiceResult.Success).value.any { it.id.value == "test_root_123" })
    }
}
