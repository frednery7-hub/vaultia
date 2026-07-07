package com.vaultia.app.core.storage.usecase

import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata
import com.vaultia.app.core.storage.repository.StorageRepository
import com.vaultia.app.core.storage.repository.StorageRepositoryError
import com.vaultia.app.core.storage.repository.StorageRepositoryResult

class FindStorageRecordMetadataUseCase(
    private val repository: StorageRepository,
) {
    fun execute(id: StorageRecordId): StorageUseCaseResult<StorageRecordMetadata?> {
        return when (val result = repository.findById(id)) {
            is StorageRepositoryResult.Success -> StorageUseCaseResult.Success(result.value)
            is StorageRepositoryResult.Failure -> StorageUseCaseResult.Failure(result.error.toUseCaseError())
        }
    }

    private fun StorageRepositoryError.toUseCaseError(): StorageUseCaseError {
        return when (this) {
            StorageRepositoryError.DuplicateRecord -> StorageUseCaseError.DuplicateRecord
            StorageRepositoryError.RecordNotFound -> StorageUseCaseError.RecordNotFound
            StorageRepositoryError.InvalidOperation -> StorageUseCaseError.InvalidOperation
        }
    }
}
