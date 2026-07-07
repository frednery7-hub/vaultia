package com.vaultia.app.core.storage.repository

import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata

class InMemoryStorageRepository : StorageRepository {
    private val records = LinkedHashMap<StorageRecordId, StorageRecordMetadata>()

    override fun save(metadata: StorageRecordMetadata): StorageRepositoryResult<StorageRecordMetadata> {
        if (records.containsKey(metadata.id)) {
            return StorageRepositoryResult.Failure(StorageRepositoryError.DuplicateRecord)
        }

        records[metadata.id] = metadata
        return StorageRepositoryResult.Success(metadata)
    }

    override fun findById(id: StorageRecordId): StorageRepositoryResult<StorageRecordMetadata?> {
        return StorageRepositoryResult.Success(records[id])
    }

    override fun listAll(): StorageRepositoryResult<List<StorageRecordMetadata>> {
        return StorageRepositoryResult.Success(records.values.toList())
    }

    override fun deleteById(id: StorageRecordId): StorageRepositoryResult<Boolean> {
        val removed = records.remove(id) != null
        return StorageRepositoryResult.Success(removed)
    }

    override fun clear(): StorageRepositoryResult<Unit> {
        records.clear()
        return StorageRepositoryResult.Success(Unit)
    }
}
