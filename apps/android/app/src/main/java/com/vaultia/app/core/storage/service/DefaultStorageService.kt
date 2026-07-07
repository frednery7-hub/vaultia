package com.vaultia.app.core.storage.service

import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata
import com.vaultia.app.core.storage.usecase.DeleteStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.FindStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.ListStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.SaveStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.StorageUseCaseError
import com.vaultia.app.core.storage.usecase.StorageUseCaseResult

class DefaultStorageService(
    private val saveStorageRecordMetadataUseCase: SaveStorageRecordMetadataUseCase,
    private val findStorageRecordMetadataUseCase: FindStorageRecordMetadataUseCase,
    private val listStorageRecordMetadataUseCase: ListStorageRecordMetadataUseCase,
    private val deleteStorageRecordMetadataUseCase: DeleteStorageRecordMetadataUseCase,
) : StorageService {
    override fun save(metadata: StorageRecordMetadata): StorageServiceResult<StorageRecordMetadata> {
        return when (val result = saveStorageRecordMetadataUseCase.execute(metadata)) {
            is StorageUseCaseResult.Success -> StorageServiceResult.Success(result.value)
            is StorageUseCaseResult.Failure -> StorageServiceResult.Failure(result.error.toServiceError())
        }
    }

    override fun findById(id: StorageRecordId): StorageServiceResult<StorageRecordMetadata?> {
        return when (val result = findStorageRecordMetadataUseCase.execute(id)) {
            is StorageUseCaseResult.Success -> StorageServiceResult.Success(result.value)
            is StorageUseCaseResult.Failure -> StorageServiceResult.Failure(result.error.toServiceError())
        }
    }

    override fun listAll(): StorageServiceResult<List<StorageRecordMetadata>> {
        return when (val result = listStorageRecordMetadataUseCase.execute()) {
            is StorageUseCaseResult.Success -> StorageServiceResult.Success(result.value)
            is StorageUseCaseResult.Failure -> StorageServiceResult.Failure(result.error.toServiceError())
        }
    }

    override fun deleteById(id: StorageRecordId): StorageServiceResult<Unit> {
        return when (val result = deleteStorageRecordMetadataUseCase.execute(id)) {
            is StorageUseCaseResult.Success -> StorageServiceResult.Success(result.value)
            is StorageUseCaseResult.Failure -> StorageServiceResult.Failure(result.error.toServiceError())
        }
    }

    private fun StorageUseCaseError.toServiceError(): StorageServiceError {
        return when (this) {
            StorageUseCaseError.DuplicateRecord -> StorageServiceError.DuplicateRecord
            StorageUseCaseError.RecordNotFound -> StorageServiceError.RecordNotFound
            StorageUseCaseError.InvalidOperation -> StorageServiceError.InvalidOperation
        }
    }
}
