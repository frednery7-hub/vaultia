package com.vaultia.app.core.storage.usecase

sealed class StorageUseCaseError {
    data object DuplicateRecord : StorageUseCaseError()
    data object RecordNotFound : StorageUseCaseError()
    data object InvalidOperation : StorageUseCaseError()
}
