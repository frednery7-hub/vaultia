package com.vaultia.app.core.storage.payload

import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.repository.StorageRepositoryResult

interface PayloadRepository {
    fun savePayload(pointer: EncryptedPayloadPointer, data: ByteArray): StorageRepositoryResult<Unit>
    fun readPayload(pointer: EncryptedPayloadPointer): StorageRepositoryResult<ByteArray>
    fun deletePayload(pointer: EncryptedPayloadPointer): StorageRepositoryResult<Boolean>
}
