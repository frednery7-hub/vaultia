package com.vaultia.app.core.storage.model

sealed class StorageModelError {
    data object InvalidRecordId : StorageModelError()
    data object InvalidPayloadPointer : StorageModelError()
    data object InvalidTimestamps : StorageModelError()
    data object InvalidFormatVersion : StorageModelError()
    data object ForbiddenCleartextField : StorageModelError()
}
