package com.vaultia.app.core.storage.repository

import com.vaultia.app.core.storage.local.dao.StorageRecordMetadataDao
import com.vaultia.app.core.storage.local.entity.StorageRecordMetadataEntity
import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata
import com.vaultia.app.core.storage.model.StorageRecordType

class RoomStorageRepository(
    private val dao: StorageRecordMetadataDao
) : StorageRepository {

    override fun save(metadata: StorageRecordMetadata): StorageRepositoryResult<StorageRecordMetadata> {
        return try {
            val existing = dao.findById(metadata.id.value)
            if (existing != null) {
                return StorageRepositoryResult.Failure(StorageRepositoryError.DuplicateRecord)
            }
            dao.insertOrReplace(mapToEntity(metadata))
            StorageRepositoryResult.Success(metadata)
        } catch (e: Exception) {
            StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation)
        }
    }

    override fun findById(id: StorageRecordId): StorageRepositoryResult<StorageRecordMetadata?> {
        return try {
            val entity = dao.findById(id.value)
            StorageRepositoryResult.Success(entity?.let { mapToDomain(it) })
        } catch (e: Exception) {
            StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation)
        }
    }

    override fun listAll(): StorageRepositoryResult<List<StorageRecordMetadata>> {
        return try {
            val entities = dao.findAll()
            StorageRepositoryResult.Success(entities.map { mapToDomain(it) })
        } catch (e: Exception) {
            StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation)
        }
    }

    override fun deleteById(id: StorageRecordId): StorageRepositoryResult<Boolean> {
        return try {
            val existing = dao.findById(id.value)
            if (existing == null) {
                return StorageRepositoryResult.Success(false)
            }
            dao.deleteById(id.value)
            StorageRepositoryResult.Success(true)
        } catch (e: Exception) {
            StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation)
        }
    }

    override fun clear(): StorageRepositoryResult<Unit> {
        return try {
            val all = dao.findAll()
            all.forEach { dao.deleteById(it.id) }
            StorageRepositoryResult.Success(Unit)
        } catch (e: Exception) {
            StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation)
        }
    }

    private fun mapToEntity(domain: StorageRecordMetadata): StorageRecordMetadataEntity {
        return StorageRecordMetadataEntity(
            id = domain.id.value,
            type = domain.type.name,
            payloadPointer = domain.payloadPointer?.value ?: "", 
            createdAtEpochMillis = domain.createdAtEpochMillis,
            updatedAtEpochMillis = domain.updatedAtEpochMillis,
            formatVersion = domain.formatVersion
        )
    }

    private fun mapToDomain(entity: StorageRecordMetadataEntity): StorageRecordMetadata {
        return StorageRecordMetadata(
            id = StorageRecordId.from(entity.id),
            type = StorageRecordType.valueOf(entity.type),
            payloadPointer = if (entity.payloadPointer.isNotEmpty()) EncryptedPayloadPointer.from(entity.payloadPointer) else null,
            createdAtEpochMillis = entity.createdAtEpochMillis,
            updatedAtEpochMillis = entity.updatedAtEpochMillis,
            formatVersion = entity.formatVersion
        )
    }
}
