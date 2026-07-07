package com.vaultia.app.core.storage.usecase

sealed class StorageUseCaseResult<out T> {
    data class Success<T>(
        val value: T,
    ) : StorageUseCaseResult<T>()

    data class Failure(
        val error: StorageUseCaseError,
    ) : StorageUseCaseResult<Nothing>()
}
