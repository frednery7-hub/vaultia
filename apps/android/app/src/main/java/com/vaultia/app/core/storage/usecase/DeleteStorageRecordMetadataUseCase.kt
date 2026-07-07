package com.vaultia.app.core.storage.usecase

import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.repository.StorageRepository
import com.vaultia.app.core.storage.repository.StorageRepositoryError
import com.vaultia.app.core.storage.repository.StorageRepositoryResult

class DeleteStorageRecordMetadataUseCase(
    private val repository: StorageRepository,
) {
    fun execute(id: StorageRecordId): StorageUseCaseResult<Unit> {
        return when (val result = repository.deleteById(id)) {
            is StorageRepositoryResult.Success -> {
                if (result.value) {
                    StorageUseCaseResult.Success(Unit)
                } else {
                    StorageUseCaseResult.Failure(StorageUseCaseError.RecordNotFound)
                }
            }
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
