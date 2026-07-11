package com.vaultia.app.core.storage.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vaultia.app.core.storage.local.dao.StorageRecordMetadataDao
import com.vaultia.app.core.storage.local.entity.StorageRecordMetadataEntity

@Database(
    entities = [StorageRecordMetadataEntity::class],
    version = 1,
    exportSchema = false
)
abstract class VaultiaLocalDatabase : RoomDatabase() {
    abstract fun storageRecordMetadataDao(): StorageRecordMetadataDao
}
