package com.vaultia.app.core.storage.local.payload

import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.payload.EncryptedPayloadRepository
import com.vaultia.app.core.storage.repository.StorageRepositoryError
import com.vaultia.app.core.storage.repository.StorageRepositoryResult
import java.io.File
import java.io.IOException

class FileEncryptedPayloadRepository(
    private val baseDirectory: File
) : EncryptedPayloadRepository {

    init {
        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs()
        }
    }

    private fun getFile(pointer: EncryptedPayloadPointer): File {
        return File(baseDirectory, pointer.value)
    }

    override fun savePayload(pointer: EncryptedPayloadPointer, data: ByteArray): StorageRepositoryResult<Unit> {
        return try {
            val file = getFile(pointer)
            file.writeBytes(data)
            StorageRepositoryResult.Success(Unit)
        } catch (e: IOException) {
            StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation)
        } catch (e: SecurityException) {
            StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation)
        }
    }

    override fun readPayload(pointer: EncryptedPayloadPointer): StorageRepositoryResult<ByteArray> {
        return try {
            val file = getFile(pointer)
            if (!file.exists()) {
                return StorageRepositoryResult.Failure(StorageRepositoryError.RecordNotFound)
            }
            val data = file.readBytes()
            StorageRepositoryResult.Success(data)
        } catch (e: IOException) {
            StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation)
        } catch (e: SecurityException) {
            StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation)
        }
    }

    override fun deletePayload(pointer: EncryptedPayloadPointer): StorageRepositoryResult<Boolean> {
        return try {
            val file = getFile(pointer)
            if (!file.exists()) {
                return StorageRepositoryResult.Success(false)
            }
            val deleted = file.delete()
            StorageRepositoryResult.Success(deleted)
        } catch (e: SecurityException) {
            StorageRepositoryResult.Failure(StorageRepositoryError.InvalidOperation)
        }
    }
}
