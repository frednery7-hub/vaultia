package com.vaultia.app.core.storage.local.repository

import com.vaultia.app.core.storage.local.dao.StorageRecordMetadataDao
import com.vaultia.app.core.storage.local.entity.StorageRecordMetadataEntity
import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata
import com.vaultia.app.core.storage.model.StorageRecordType
import com.vaultia.app.core.storage.repository.StorageRepository
import com.vaultia.app.core.storage.repository.StorageRepositoryError
import com.vaultia.app.core.storage.repository.StorageRepositoryResult

class RoomStorageRepository(
    private val dao: StorageRecordMetadataDao
) : StorageRepository {
    override fun save(metadata: StorageRecordMetadata): StorageRepositoryResult<StorageRecordMetadata> {
        return try {
            if (dao.findById(metadata.id.value) != null) return StorageRepositoryResult.Failure(StorageRepositoryError.DuplicateRecord)
            dao.insertOrReplace(mapToEntity(metadata))
            StorageRepositoryResult.Success(metadata)
        } catch (e: Exception) { StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation) }
    }
    override fun findById(id: StorageRecordId): StorageRepositoryResult<StorageRecordMetadata?> {
        return try { StorageRepositoryResult.Success(dao.findById(id.value)?.let { mapToDomain(it) }) } 
        catch (e: Exception) { StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation) }
    }
    override fun listAll(): StorageRepositoryResult<List<StorageRecordMetadata>> {
        return try { StorageRepositoryResult.Success(dao.findAll().map { mapToDomain(it) }) } 
        catch (e: Exception) { StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation) }
    }
    override fun deleteById(id: StorageRecordId): StorageRepositoryResult<Boolean> {
        return try {
            if (dao.findById(id.value) == null) return StorageRepositoryResult.Success(false)
            dao.deleteById(id.value)
            StorageRepositoryResult.Success(true)
        } catch (e: Exception) { StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation) }
    }
    override fun clear(): StorageRepositoryResult<Unit> {
        return try {
            dao.findAll().forEach { dao.deleteById(it.id) }
            StorageRepositoryResult.Success(Unit)
        } catch (e: Exception) { StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation) }
    }
    private fun mapToEntity(domain: StorageRecordMetadata) = StorageRecordMetadataEntity(
        id = domain.id.value, type = domain.type.name,
        payloadPointer = domain.payloadPointer?.value ?: "", 
        createdAtEpochMillis = domain.createdAtEpochMillis, updatedAtEpochMillis = domain.updatedAtEpochMillis, formatVersion = domain.formatVersion
    )
    private fun mapToDomain(entity: StorageRecordMetadataEntity) = StorageRecordMetadata(
        id = StorageRecordId.from(entity.id), type = StorageRecordType.valueOf(entity.type),
        payloadPointer = if (entity.payloadPointer.isNotEmpty()) EncryptedPayloadPointer.from(entity.payloadPointer) else null,
        createdAtEpochMillis = entity.createdAtEpochMillis, updatedAtEpochMillis = entity.updatedAtEpochMillis, formatVersion = entity.formatVersion
    )
}
