package com.vaultia.app.core.storage.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vaultia.app.core.storage.local.dao.StorageRecordMetadataDao
import com.vaultia.app.core.storage.local.entity.StorageRecordMetadataEntity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class VaultiaLocalDatabaseTest {
    private lateinit var db: VaultiaLocalDatabase
    private lateinit var dao: StorageRecordMetadataDao

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, VaultiaLocalDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.storageRecordMetadataDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeMetadataAndReadInList() {
        val metadata = StorageRecordMetadataEntity(
            id = "record_123",
            type = "ITEM_METADATA",
            payloadPointer = "payload_123.enc",
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 1000L,
            formatVersion = 1
        )
        dao.insertOrReplace(metadata)

        val byId = dao.findById("record_123")
        assertNotNull(byId)
        assertEquals("payload_123.enc", byId?.payloadPointer)
        
        val all = dao.findAll()
        assertEquals(1, all.size)
        assertEquals("record_123", all[0].id)
        
        dao.deleteById("record_123")
        assertNull(dao.findById("record_123"))
    }
}
