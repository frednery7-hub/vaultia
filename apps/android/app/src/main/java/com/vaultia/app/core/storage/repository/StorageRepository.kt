package com.vaultia.app.core.storage.repository

import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata

interface StorageRepository {
    fun save(metadata: StorageRecordMetadata): StorageRepositoryResult<StorageRecordMetadata>

    fun findById(id: StorageRecordId): StorageRepositoryResult<StorageRecordMetadata?>

    fun listAll(): StorageRepositoryResult<List<StorageRecordMetadata>>

    fun deleteById(id: StorageRecordId): StorageRepositoryResult<Boolean>

    fun clear(): StorageRepositoryResult<Unit>
}
