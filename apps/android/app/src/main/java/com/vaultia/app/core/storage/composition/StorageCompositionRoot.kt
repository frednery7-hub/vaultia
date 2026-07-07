package com.vaultia.app.core.storage.composition

import com.vaultia.app.core.storage.repository.InMemoryStorageRepository
import com.vaultia.app.core.storage.service.DefaultStorageService
import com.vaultia.app.core.storage.usecase.DeleteStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.FindStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.ListStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.SaveStorageRecordMetadataUseCase

object StorageCompositionRoot {
    fun createInMemoryContainer(): StorageContainer {
        val repository = InMemoryStorageRepository()

        val saveStorageRecordMetadataUseCase = SaveStorageRecordMetadataUseCase(repository)
        val findStorageRecordMetadataUseCase = FindStorageRecordMetadataUseCase(repository)
        val listStorageRecordMetadataUseCase = ListStorageRecordMetadataUseCase(repository)
        val deleteStorageRecordMetadataUseCase = DeleteStorageRecordMetadataUseCase(repository)

        val storageService = DefaultStorageService(
            saveStorageRecordMetadataUseCase = saveStorageRecordMetadataUseCase,
            findStorageRecordMetadataUseCase = findStorageRecordMetadataUseCase,
            listStorageRecordMetadataUseCase = listStorageRecordMetadataUseCase,
            deleteStorageRecordMetadataUseCase = deleteStorageRecordMetadataUseCase,
        )

        return StorageContainer(
            storageService = storageService,
        )
    }
}
