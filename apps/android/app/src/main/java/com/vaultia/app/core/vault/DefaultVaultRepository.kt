package com.vaultia.app.core.vault

import com.vaultia.app.core.crypto.encryption.AesGcmAuthenticatedCipher
import com.vaultia.app.core.crypto.encryption.EncryptedPayload
import com.vaultia.app.core.crypto.kdf.KdfResult
import com.vaultia.app.core.crypto.vault.item.EncryptedVaultItemDraftFactory
import com.vaultia.app.core.crypto.vault.item.EncryptedVaultItemDraftResult
import com.vaultia.app.core.crypto.vault.item.EncryptedVaultItemType
import com.vaultia.app.core.crypto.vault.item.VaultItemSerializationResult
import com.vaultia.app.core.crypto.vault.item.VaultItemSerializer
import com.vaultia.app.core.crypto.vault.payload.SerializedEncryptedVaultPayload
import com.vaultia.app.core.crypto.vault.payload.VaultPayloadDecryptionRequest
import com.vaultia.app.core.crypto.vault.payload.VaultPayloadDecryptionResult
import com.vaultia.app.core.crypto.vault.payload.VaultPayloadDecryptionService
import com.vaultia.app.core.crypto.vault.payload.VaultPayloadEncryptionRequest
import com.vaultia.app.core.crypto.vault.payload.VaultPayloadEncryptionResult
import com.vaultia.app.core.crypto.vault.payload.VaultPayloadEncryptionService
import com.vaultia.app.core.crypto.vault.payload.VaultPayloadSerializedFormatVersion
import com.vaultia.app.core.model.payload.NotePayload
import com.vaultia.app.core.model.payload.PasswordPayload
import com.vaultia.app.core.model.payload.VaultItemPayload
import com.vaultia.app.core.model.payload.VaultItemPayloadSerializationResult
import com.vaultia.app.core.model.payload.VaultItemPayloadSerializer
import com.vaultia.app.core.model.vault.VaultItem
import com.vaultia.app.core.model.vault.VaultItemType
import com.vaultia.app.core.storage.composition.StorageContainer
import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.model.StorageRecordId
import com.vaultia.app.core.storage.model.StorageRecordMetadata
import com.vaultia.app.core.storage.model.StorageRecordType
import com.vaultia.app.core.storage.repository.StorageRepositoryResult
import com.vaultia.app.core.storage.service.StorageServiceResult
import java.util.UUID

class DefaultVaultRepository(
    private val storageContainer: StorageContainer,
) : VaultRepository {

    private fun ByteArray.toHex(): String {
        val result = java.lang.StringBuilder(size * 2)
        val hexChars = "0123456789abcdef".toCharArray()
        forEach { byte ->
            val value = byte.toInt() and 0xff
            result.append(hexChars[value ushr 4])
            result.append(hexChars[value and 0x0f])
        }
        return result.toString()
    }

    private fun String.hexToByteArray(): ByteArray {
        val output = ByteArray(length / 2)
        for (i in output.indices) {
            val high = this[i * 2].digitToInt(16)
            val low = this[i * 2 + 1].digitToInt(16)
            output[i] = ((high shl 4) or low).toByte()
        }
        return output
    }

    override fun saveItem(id: String?, payload: VaultItemPayload, sessionKey: KdfResult): VaultRepositoryResult<VaultItem> {
        val itemId = id ?: UUID.randomUUID().toString().replace("-", "")
        val now = System.currentTimeMillis()
        
        // 1. Serialize Payload
        val rawBytes = VaultItemPayloadSerializer.encode(payload)
        
        // 2. Encrypt Bytes
        val encryptionService = VaultPayloadEncryptionService(AesGcmAuthenticatedCipher())
        val encryptResult = encryptionService.encrypt(
            VaultPayloadEncryptionRequest(key = sessionKey.derivedKeyCopy(), plaintext = rawBytes, createdAtEpochMillis = now)
        )
        val encryptedPayloadDraft = when (encryptResult) {
            is VaultPayloadEncryptionResult.Success -> encryptResult.draft
            is VaultPayloadEncryptionResult.Failure -> return VaultRepositoryResult.Failure(VaultRepositoryError.EncryptionFailed)
        }
        
        // 3. Serialize Encrypted Payload
        val serializedEncryptedPayload = SerializedEncryptedVaultPayload(
            formatVersion = VaultPayloadSerializedFormatVersion.V1_0,
            nonceHex = encryptedPayloadDraft.encryptedPayload.nonceCopy().toHex(),
            ciphertextHex = encryptedPayloadDraft.encryptedPayload.ciphertextCopy().toHex(),
            authenticationTagHex = encryptedPayloadDraft.encryptedPayload.authenticationTagCopy().toHex(),
            createdAtEpochMillis = encryptedPayloadDraft.createdAtEpochMillis
        )
        
        // 4. Draft EncryptedVaultItem
        val type = when (payload) {
            is PasswordPayload -> EncryptedVaultItemType.PASSWORD
            is NotePayload -> EncryptedVaultItemType.NOTE
            else -> throw IllegalArgumentException("Unknown payload type")
        }
        val itemDraftResult = EncryptedVaultItemDraftFactory.create(
            id = itemId, type = type, serializedPayload = serializedEncryptedPayload, createdAtEpochMillis = now, updatedAtEpochMillis = now
        )
        val itemDraft = when (itemDraftResult) {
            is EncryptedVaultItemDraftResult.Success -> itemDraftResult.draft
            is EncryptedVaultItemDraftResult.Failure -> return VaultRepositoryResult.Failure(VaultRepositoryError.SerializationFailed)
        }
        
        // 5. Serialize full shell
        val vaultItemShellBytes = VaultItemSerializer.encode(itemDraft).toByteArray(Charsets.UTF_8)
        
        // 6. Save to Disk
        val pointer = EncryptedPayloadPointer.from("${itemId}.enc")
        val savePayloadResult = storageContainer.payloadRepository.savePayload(pointer, vaultItemShellBytes)
        if (savePayloadResult !is StorageRepositoryResult.Success<*>) {
            return VaultRepositoryResult.Failure(VaultRepositoryError.StorageFailed)
        }
        
        // 7. Save to SQLite
        val metadata = StorageRecordMetadata(
            id = StorageRecordId.from(itemId),
            type = StorageRecordType.ITEM_METADATA,
            payloadPointer = pointer,
            createdAtEpochMillis = now,
            updatedAtEpochMillis = now,
            formatVersion = 1
        )
        val saveSqliteResult = storageContainer.storageService.save(metadata)
        if (saveSqliteResult !is StorageServiceResult.Success<*>) {
            storageContainer.payloadRepository.deletePayload(pointer)
            return VaultRepositoryResult.Failure(VaultRepositoryError.StorageFailed)
        }
        
        return VaultRepositoryResult.Success(
            VaultItem(id = itemId, type = if (type == EncryptedVaultItemType.PASSWORD) VaultItemType.PASSWORD else VaultItemType.NOTE, title = payload.title, createdAtEpochMillis = now, updatedAtEpochMillis = now)
        )
    }

    override fun readItem(id: String, sessionKey: KdfResult): VaultRepositoryResult<Pair<VaultItem, VaultItemPayload>> {
        val pointer = EncryptedPayloadPointer.from("${id}.enc")
        val now = System.currentTimeMillis()
        
        // 1. Read Bytes from Disk
        val readPayloadResult = storageContainer.payloadRepository.readPayload(pointer)
        val vaultItemShellBytes = when (readPayloadResult) {
            is StorageRepositoryResult.Success -> readPayloadResult.value
            is StorageRepositoryResult.Failure -> return VaultRepositoryResult.Failure(VaultRepositoryError.ItemNotFound)
        }
        
        // 2. Deserialize Shell
        val shellString = String(vaultItemShellBytes, Charsets.UTF_8)
        val shellResult = VaultItemSerializer.decode(shellString)
        val shell = when (shellResult) {
            is VaultItemSerializationResult.Success -> shellResult.draft
            is VaultItemSerializationResult.Failure -> return VaultRepositoryResult.Failure(VaultRepositoryError.SerializationFailed)
        }
        
        // 3. Reconstruct EncryptedPayload
        val encryptedPayload = EncryptedPayload(
            nonce = shell.serializedPayload.nonceHex.hexToByteArray(),
            ciphertext = shell.serializedPayload.ciphertextHex.hexToByteArray(),
            authenticationTag = shell.serializedPayload.authenticationTagHex.hexToByteArray(),
        )
        
        // 4. Decrypt Payload
        val decryptionService = VaultPayloadDecryptionService(AesGcmAuthenticatedCipher())
        val decryptResult = decryptionService.decrypt(
            VaultPayloadDecryptionRequest(key = sessionKey.derivedKeyCopy(), encryptedPayload = encryptedPayload, decryptedAtEpochMillis = now)
        )
        val plaintextPayload = when (decryptResult) {
            is VaultPayloadDecryptionResult.Success -> decryptResult.draft.plaintextPayload
            is VaultPayloadDecryptionResult.Failure -> return VaultRepositoryResult.Failure(VaultRepositoryError.DecryptionFailed)
        }
        
        // 5. Deserialize Plaintext
        val payloadResult = VaultItemPayloadSerializer.decode(plaintextPayload.plaintextCopy())
        val vaultItemPayload = when (payloadResult) {
            is VaultItemPayloadSerializationResult.Success -> payloadResult.payload
            is VaultItemPayloadSerializationResult.Failure -> return VaultRepositoryResult.Failure(VaultRepositoryError.SerializationFailed)
        }
        
        val vaultItem = VaultItem(
            id = shell.id.value,
            type = if (shell.type == EncryptedVaultItemType.PASSWORD) VaultItemType.PASSWORD else VaultItemType.NOTE,
            title = vaultItemPayload.title,
            createdAtEpochMillis = shell.createdAtEpochMillis,
            updatedAtEpochMillis = shell.updatedAtEpochMillis
        )
        
        return VaultRepositoryResult.Success(Pair(vaultItem, vaultItemPayload))
    }

    override fun listItems(sessionKey: KdfResult): VaultRepositoryResult<List<VaultItem>> {
        val listResult = storageContainer.storageService.listAll()
        val records = when (listResult) {
            is StorageServiceResult.Success -> listResult.value
            is StorageServiceResult.Failure -> return VaultRepositoryResult.Failure(VaultRepositoryError.StorageFailed)
        }
        
        val items = mutableListOf<VaultItem>()
        for (record in records) {
            if (record.type == StorageRecordType.ITEM_METADATA) {
                val readResult = readItem(record.id.value, sessionKey)
                if (readResult is VaultRepositoryResult.Success) {
                    items.add(readResult.data.first)
                }
            }
        }
        return VaultRepositoryResult.Success(items)
    }

    override fun deleteItem(id: String): VaultRepositoryResult<Boolean> {
        val pointer = EncryptedPayloadPointer.from("${id}.enc")
        storageContainer.payloadRepository.deletePayload(pointer)
        storageContainer.storageService.deleteById(StorageRecordId.from(id))
        return VaultRepositoryResult.Success(true)
    }
}
