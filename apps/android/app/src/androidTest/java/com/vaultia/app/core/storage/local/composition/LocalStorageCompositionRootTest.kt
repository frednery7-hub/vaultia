package com.vaultia.app.core.storage.local.composition

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
class LocalStorageCompositionRootTest {
    @After fun tearDown() { InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase("vaultia_local_metadata.db") }
    @Test fun containerWorks() {
        val container = LocalStorageCompositionRoot.createLocalContainer(InstrumentationRegistry.getInstrumentation().targetContext)
        val metadata = StorageRecordMetadata(StorageRecordId.from("test_root_123"), StorageRecordType.ITEM_METADATA, EncryptedPayloadPointer.from("test_payload_abcd.enc"), 1000L, 1000L, 1)
        assertTrue(container.storageService.save(metadata) is StorageServiceResult.Success)
    }
}
