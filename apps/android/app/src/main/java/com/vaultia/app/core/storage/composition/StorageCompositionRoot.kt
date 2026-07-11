package com.vaultia.app.core.storage.composition

import com.vaultia.app.core.storage.header.InMemoryVaultHeaderRepository
import com.vaultia.app.core.storage.payload.InMemoryPayloadRepository
import com.vaultia.app.core.storage.repository.InMemoryStorageRepository
import com.vaultia.app.core.storage.service.DefaultStorageService
import com.vaultia.app.core.storage.usecase.DeleteStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.FindStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.ListStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.SaveStorageRecordMetadataUseCase

object StorageCompositionRoot {
    fun createInMemoryContainer(): StorageContainer {
        val repository = InMemoryStorageRepository()
        val storageService = DefaultStorageService(
            SaveStorageRecordMetadataUseCase(repository), FindStorageRecordMetadataUseCase(repository),
            ListStorageRecordMetadataUseCase(repository), DeleteStorageRecordMetadataUseCase(repository)
        )
        return StorageContainer(
            storageService = storageService,
            payloadRepository = InMemoryPayloadRepository(),
            headerRepository = InMemoryVaultHeaderRepository()
        )
    }
}
