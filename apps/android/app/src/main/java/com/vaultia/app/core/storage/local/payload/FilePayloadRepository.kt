package com.vaultia.app.core.storage.local.payload

import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.payload.PayloadRepository
import com.vaultia.app.core.storage.repository.StorageRepositoryError
import com.vaultia.app.core.storage.repository.StorageRepositoryResult
import java.io.File
import java.io.IOException

class FilePayloadRepository(private val baseDirectory: File) : PayloadRepository {
    init { if (!baseDirectory.exists()) baseDirectory.mkdirs() }
    private fun getFile(pointer: EncryptedPayloadPointer): File = File(baseDirectory, pointer.value)
    override fun savePayload(pointer: EncryptedPayloadPointer, data: ByteArray): StorageRepositoryResult<Unit> {
        return try { getFile(pointer).writeBytes(data); StorageRepositoryResult.Success(Unit) }
        catch (e: Exception) { StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation) }
    }
    override fun readPayload(pointer: EncryptedPayloadPointer): StorageRepositoryResult<ByteArray> {
        return try {
            val file = getFile(pointer)
            if (!file.exists()) return StorageRepositoryResult.Failure(StorageRepositoryError.RecordNotFound)
            StorageRepositoryResult.Success(file.readBytes())
        } catch (e: Exception) { StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation) }
    }
    override fun deletePayload(pointer: EncryptedPayloadPointer): StorageRepositoryResult<Boolean> {
        return try {
            val file = getFile(pointer)
            if (!file.exists()) StorageRepositoryResult.Success(false) else StorageRepositoryResult.Success(file.delete())
        } catch (e: Exception) { StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation) }
    }
}
