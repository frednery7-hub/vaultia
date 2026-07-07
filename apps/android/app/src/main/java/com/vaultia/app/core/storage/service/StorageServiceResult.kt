package com.vaultia.app.core.storage.service

sealed class StorageServiceResult<out T> {
    data class Success<T>(
        val value: T,
    ) : StorageServiceResult<T>()

    data class Failure(
        val error: StorageServiceError,
    ) : StorageServiceResult<Nothing>()
}
