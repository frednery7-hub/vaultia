package com.vaultia.app.core.storage.service

sealed class StorageServiceError {
    data object DuplicateRecord : StorageServiceError()
    data object RecordNotFound : StorageServiceError()
    data object InvalidOperation : StorageServiceError()
}
