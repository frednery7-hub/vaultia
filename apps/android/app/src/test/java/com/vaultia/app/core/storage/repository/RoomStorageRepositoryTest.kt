package com.vaultia.app.core.storage.repository

import com.vaultia.app.core.storage.local.dao.StorageRecordMetadataDao
import com.vaultia.app.core.storage.local.entity.StorageRecordMetadataEntity
import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata
import com.vaultia.app.core.storage.model.StorageRecordType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap

class RoomStorageRepositoryTest {

    class FakeDao : StorageRecordMetadataDao {
        val data = ConcurrentHashMap<String, StorageRecordMetadataEntity>()
        var simulateDbException = false

        override fun insertOrReplace(entity: StorageRecordMetadataEntity) {
            if (simulateDbException) throw Exception("Simulated SQLite Exception")
            data[entity.id] = entity
        }

        override fun findById(id: String): StorageRecordMetadataEntity? {
            if (simulateDbException) throw Exception("Simulated SQLite Exception")
            return data[id]
        }

        override fun findAll(): List<StorageRecordMetadataEntity> {
            if (simulateDbException) throw Exception("Simulated SQLite Exception")
            return data.values.sortedByDescending { it.updatedAtEpochMillis }
        }

        override fun deleteById(id: String) {
            if (simulateDbException) throw Exception("Simulated SQLite Exception")
            data.remove(id)
        }
    }

    private lateinit var fakeDao: FakeDao
    private lateinit var repository: RoomStorageRepository

    @Before
    fun setUp() {
        fakeDao = FakeDao()
        repository = RoomStorageRepository(fakeDao)
    }

    private fun createDomainMetadata(id: String): StorageRecordMetadata {
        return StorageRecordMetadata(
            id = StorageRecordId.from(id),
            type = StorageRecordType.ITEM_METADATA,
            payloadPointer = EncryptedPayloadPointer.from("payload_${id}_abcd.enc"),
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 2000L,
            formatVersion = 1
        )
    }

    @Test
    fun `save inserts new metadata successfully`() {
        val metadata = createDomainMetadata("rec_123")
        
        val result = repository.save(metadata)
        
        assertTrue(result is StorageRepositoryResult.Success)
        val savedEntity = fakeDao.data["rec_123"]!!
        assertEquals("rec_123", savedEntity.id)
        assertEquals("ITEM_METADATA", savedEntity.type)
        assertEquals("payload_rec_123_abcd.enc", savedEntity.payloadPointer)
    }

    @Test
    fun `save returns DuplicateRecord when id already exists`() {
        val metadata = createDomainMetadata("rec_123")
        repository.save(metadata)
        
        val duplicateResult = repository.save(metadata)
        
        assertTrue(duplicateResult is StorageRepositoryResult.Failure)
        assertEquals(StorageRepositoryError.DuplicateRecord, (duplicateResult as StorageRepositoryResult.Failure).error)
    }

    @Test
    fun `save returns InvalidOperation when DAO throws exception`() {
        fakeDao.simulateDbException = true
        val metadata = createDomainMetadata("rec_123")
        
        val result = repository.save(metadata)
        
        assertTrue(result is StorageRepositoryResult.Failure)
        assertEquals(StorageRepositoryError.InvalidOperation, (result as StorageRepositoryResult.Failure).error)
    }

    @Test
    fun `findById returns correctly mapped metadata`() {
        val metadata = createDomainMetadata("rec_123")
        repository.save(metadata)
        
        val result = repository.findById(StorageRecordId.from("rec_123"))
        
        assertTrue(result is StorageRepositoryResult.Success)
        val found = (result as StorageRepositoryResult.Success).value
        assertEquals("payload_rec_123_abcd.enc", found?.payloadPointer?.value)
    }

    @Test
    fun `listAll maps all entities correctly`() {
        repository.save(createDomainMetadata("rec_1"))
        repository.save(createDomainMetadata("rec_2"))
        
        val result = repository.listAll()
        
        assertTrue(result is StorageRepositoryResult.Success)
        assertEquals(2, (result as StorageRepositoryResult.Success).value.size)
    }

    @Test
    fun `deleteById successfully removes record`() {
        repository.save(createDomainMetadata("rec_123"))
        
        val deleteResult = repository.deleteById(StorageRecordId.from("rec_123"))
        assertTrue(deleteResult is StorageRepositoryResult.Success)
        assertEquals(true, (deleteResult as StorageRepositoryResult.Success).value)
        
        val findResult = repository.findById(StorageRecordId.from("rec_123"))
        assertEquals(null, (findResult as StorageRepositoryResult.Success).value)
    }
}
