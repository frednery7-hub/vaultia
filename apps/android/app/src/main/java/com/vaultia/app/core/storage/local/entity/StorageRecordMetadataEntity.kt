package com.vaultia.app.core.storage.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "storage_record_metadata")
data class StorageRecordMetadataEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "payload_pointer")
    val payloadPointer: String,

    @ColumnInfo(name = "created_at")
    val createdAtEpochMillis: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAtEpochMillis: Long,

    @ColumnInfo(name = "format_version")
    val formatVersion: Int
)
