package com.vaultia.app.core.storage.local.composition

import android.content.Context
import androidx.room.Room
import com.vaultia.app.core.storage.composition.StorageContainer
import com.vaultia.app.core.storage.local.VaultiaLocalDatabase
import com.vaultia.app.core.storage.local.repository.RoomStorageRepository
import com.vaultia.app.core.storage.service.DefaultStorageService
import com.vaultia.app.core.storage.usecase.DeleteStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.FindStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.ListStorageRecordMetadataUseCase
import com.vaultia.app.core.storage.usecase.SaveStorageRecordMetadataUseCase

object LocalStorageCompositionRoot {
    fun createLocalContainer(context: Context): StorageContainer {
        val database = Room.databaseBuilder(context.applicationContext, VaultiaLocalDatabase::class.java, "vaultia_local_metadata.db").build()
        val repository = RoomStorageRepository(database.storageRecordMetadataDao())
        return StorageContainer(storageService = DefaultStorageService(
            SaveStorageRecordMetadataUseCase(repository), FindStorageRecordMetadataUseCase(repository),
            ListStorageRecordMetadataUseCase(repository), DeleteStorageRecordMetadataUseCase(repository)
        ))
    }
}
