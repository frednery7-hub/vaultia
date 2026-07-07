package com.vaultia.app.core.storage.service

import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata

interface StorageService {
    fun save(metadata: StorageRecordMetadata): StorageServiceResult<StorageRecordMetadata>

    fun findById(id: StorageRecordId): StorageServiceResult<StorageRecordMetadata?>

    fun listAll(): StorageServiceResult<List<StorageRecordMetadata>>

    fun deleteById(id: StorageRecordId): StorageServiceResult<Unit>
}
