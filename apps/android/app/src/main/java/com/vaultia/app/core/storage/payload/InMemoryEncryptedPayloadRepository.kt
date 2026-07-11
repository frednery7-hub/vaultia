package com.vaultia.app.core.storage.payload

import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.repository.StorageRepositoryError
import com.vaultia.app.core.storage.repository.StorageRepositoryResult
import java.util.concurrent.ConcurrentHashMap

class InMemoryEncryptedPayloadRepository : EncryptedPayloadRepository {
    private val payloads = ConcurrentHashMap<String, ByteArray>()
    override fun savePayload(pointer: EncryptedPayloadPointer, data: ByteArray): StorageRepositoryResult<Unit> {
        payloads[pointer.value] = data.copyOf(); return StorageRepositoryResult.Success(Unit)
    }
    override fun readPayload(pointer: EncryptedPayloadPointer): StorageRepositoryResult<ByteArray> {
        val data = payloads[pointer.value]
        return if (data != null) StorageRepositoryResult.Success(data.copyOf()) else StorageRepositoryResult.Failure(StorageRepositoryError.RecordNotFound)
    }
    override fun deletePayload(pointer: EncryptedPayloadPointer): StorageRepositoryResult<Boolean> = StorageRepositoryResult.Success(payloads.remove(pointer.value) != null)
}
