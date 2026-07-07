package com.vaultia.app.core.storage.repository

sealed class StorageRepositoryError {
    data object DuplicateRecord : StorageRepositoryError()
    data object RecordNotFound : StorageRepositoryError()
    data object InvalidOperation : StorageRepositoryError()
}
