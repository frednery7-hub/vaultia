package com.vaultia.app.core.storage.repository

sealed class StorageRepositoryResult<out T> {
    data class Success<T>(
        val value: T,
    ) : StorageRepositoryResult<T>()

    data class Failure(
        val error: StorageRepositoryError,
    ) : StorageRepositoryResult<Nothing>()
}
