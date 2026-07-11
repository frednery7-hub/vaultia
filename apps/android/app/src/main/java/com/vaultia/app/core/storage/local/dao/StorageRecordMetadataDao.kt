package com.vaultia.app.core.storage.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vaultia.app.core.storage.local.entity.StorageRecordMetadataEntity

@Dao
interface StorageRecordMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(entity: StorageRecordMetadataEntity)

    @Query("SELECT * FROM storage_record_metadata WHERE id = :id LIMIT 1")
    fun findById(id: String): StorageRecordMetadataEntity?

    @Query("SELECT * FROM storage_record_metadata ORDER BY updated_at DESC")
    fun findAll(): List<StorageRecordMetadataEntity>

    @Query("DELETE FROM storage_record_metadata WHERE id = :id")
    fun deleteById(id: String)
}
