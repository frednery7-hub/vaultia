package com.vaultia.app.core.storage.local.repository

import com.vaultia.app.core.storage.local.dao.StorageRecordMetadataDao
import com.vaultia.app.core.storage.local.entity.StorageRecordMetadataEntity
import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata
import com.vaultia.app.core.storage.model.StorageRecordType
import com.vaultia.app.core.storage.repository.StorageRepositoryError
import com.vaultia.app.core.storage.repository.StorageRepositoryResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap

class RoomStorageRepositoryTest {
    class FakeDao : StorageRecordMetadataDao {
        val data = ConcurrentHashMap<String, StorageRecordMetadataEntity>()
        var simulateDbException = false
        override fun insertOrReplace(entity: StorageRecordMetadataEntity) { if (simulateDbException) throw Exception(); data[entity.id] = entity }
        override fun findById(id: String): StorageRecordMetadataEntity? { if (simulateDbException) throw Exception(); return data[id] }
        override fun findAll(): List<StorageRecordMetadataEntity> { if (simulateDbException) throw Exception(); return data.values.sortedByDescending { it.updatedAtEpochMillis } }
        override fun deleteById(id: String) { if (simulateDbException) throw Exception(); data.remove(id) }
    }

    private lateinit var fakeDao: FakeDao
    private lateinit var repository: RoomStorageRepository

    @Before
    fun setUp() { fakeDao = FakeDao(); repository = RoomStorageRepository(fakeDao) }

    private fun createDomainMetadata(id: String) = StorageRecordMetadata(
        id = StorageRecordId.from(id), type = StorageRecordType.ITEM_METADATA,
        payloadPointer = EncryptedPayloadPointer.from("payload_${id}_abcd.enc"),
        createdAtEpochMillis = 1000L, updatedAtEpochMillis = 2000L, formatVersion = 1
    )

    @Test
    fun `save inserts new metadata successfully`() {
        val result = repository.save(createDomainMetadata("record_001"))
        assertTrue(result is StorageRepositoryResult.Success)
    }

    @Test
    fun `save returns DuplicateRecord when id already exists`() {
        val metadata = createDomainMetadata("record_001")
        repository.save(metadata)
        val duplicateResult = repository.save(metadata)
        assertEquals(StorageRepositoryError.DuplicateRecord, (duplicateResult as StorageRepositoryResult.Failure).error)
    }

    @Test
    fun `save returns InvalidOperation when DAO throws exception`() {
        fakeDao.simulateDbException = true
        val result = repository.save(createDomainMetadata("record_001"))
        assertEquals(StorageRepositoryError.InvalidOperation, (result as StorageRepositoryResult.Failure).error)
    }

    @Test
    fun `findById returns correctly mapped metadata`() {
        repository.save(createDomainMetadata("record_001"))
        val result = repository.findById(StorageRecordId.from("record_001"))
        assertEquals("payload_record_001_abcd.enc", (result as StorageRepositoryResult.Success).value?.payloadPointer?.value)
    }

    @Test
    fun `listAll maps all entities correctly`() {
        repository.save(createDomainMetadata("record_001"))
        repository.save(createDomainMetadata("record_002"))
        val result = repository.listAll()
        assertEquals(2, (result as StorageRepositoryResult.Success).value.size)
    }

    @Test
    fun `deleteById successfully removes record`() {
        repository.save(createDomainMetadata("record_001"))
        repository.deleteById(StorageRecordId.from("record_001"))
        assertEquals(null, (repository.findById(StorageRecordId.from("record_001")) as StorageRepositoryResult.Success).value)
    }
}
