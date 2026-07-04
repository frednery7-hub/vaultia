package com.vaultia.app.core.crypto.vault.item

import com.vaultia.app.core.crypto.vault.payload.SerializedEncryptedVaultPayload
import com.vaultia.app.core.crypto.vault.payload.VaultPayloadSerializedFormatVersion

object VaultItemSerializer {
    private const val MAGIC = "VAULTIA_ITEM_V1"
    private const val FOOTER = "END_VAULTIA_ITEM"
    private const val FIELD_FORMAT_VERSION = "formatVersion"
    private const val FIELD_ID = "id"
    private const val FIELD_TYPE = "type"
    private const val FIELD_CREATED_AT_EPOCH_MILLIS = "createdAtEpochMillis"
    private const val FIELD_UPDATED_AT_EPOCH_MILLIS = "updatedAtEpochMillis"
    private const val FIELD_PAYLOAD_FORMAT_VERSION = "payloadFormatVersion"
    private const val FIELD_PAYLOAD_NONCE_HEX = "payloadNonceHex"
    private const val FIELD_PAYLOAD_CIPHERTEXT_HEX = "payloadCiphertextHex"
    private const val FIELD_PAYLOAD_AUTHENTICATION_TAG_HEX = "payloadAuthenticationTagHex"
    private const val FIELD_PAYLOAD_CREATED_AT_EPOCH_MILLIS = "payloadCreatedAtEpochMillis"

    private val requiredFields = listOf(
        FIELD_FORMAT_VERSION,
        FIELD_ID,
        FIELD_TYPE,
        FIELD_CREATED_AT_EPOCH_MILLIS,
        FIELD_UPDATED_AT_EPOCH_MILLIS,
        FIELD_PAYLOAD_FORMAT_VERSION,
        FIELD_PAYLOAD_NONCE_HEX,
        FIELD_PAYLOAD_CIPHERTEXT_HEX,
        FIELD_PAYLOAD_AUTHENTICATION_TAG_HEX,
        FIELD_PAYLOAD_CREATED_AT_EPOCH_MILLIS,
    )

    fun encode(draft: EncryptedVaultItemDraft): String {
        val payload = draft.serializedPayload
        return listOf(
            MAGIC,
            "$FIELD_FORMAT_VERSION=${VaultItemSerializedFormatVersion.V1_0.encodedValue}",
            "$FIELD_ID=${draft.id.value}",
            "$FIELD_TYPE=${draft.type.name}",
            "$FIELD_CREATED_AT_EPOCH_MILLIS=${draft.createdAtEpochMillis}",
            "$FIELD_UPDATED_AT_EPOCH_MILLIS=${draft.updatedAtEpochMillis}",
            "$FIELD_PAYLOAD_FORMAT_VERSION=${payload.formatVersion.encodedValue}",
            "$FIELD_PAYLOAD_NONCE_HEX=${payload.nonceHex}",
            "$FIELD_PAYLOAD_CIPHERTEXT_HEX=${payload.ciphertextHex}",
            "$FIELD_PAYLOAD_AUTHENTICATION_TAG_HEX=${payload.authenticationTagHex}",
            "$FIELD_PAYLOAD_CREATED_AT_EPOCH_MILLIS=${payload.createdAtEpochMillis}",
            FOOTER,
        ).joinToString(separator = "\n")
    }

    fun decode(input: String): VaultItemSerializationResult {
        if (input.isBlank()) {
            return VaultItemSerializationResult.Failure(VaultItemSerializationError.EmptyInput)
        }

        val lines = input.lines().filter { line -> line.isNotBlank() }
        if (lines.firstOrNull() != MAGIC || lines.lastOrNull() != FOOTER) {
            return VaultItemSerializationResult.Failure(VaultItemSerializationError.InvalidMagic)
        }

        val fields = mutableMapOf<String, String>()
        for (line in lines.drop(1).dropLast(1)) {
            val separatorIndex = line.indexOf('=')
            if (separatorIndex <= 0 || separatorIndex == line.lastIndex) {
                return VaultItemSerializationResult.Failure(VaultItemSerializationError.InvalidFieldValue)
            }
            val name = line.substring(0, separatorIndex)
            val value = line.substring(separatorIndex + 1)
            if (fields.containsKey(name)) {
                return VaultItemSerializationResult.Failure(VaultItemSerializationError.DuplicateField)
            }
            fields[name] = value
        }

        if (!requiredFields.all { field -> fields.containsKey(field) }) {
            return VaultItemSerializationResult.Failure(VaultItemSerializationError.MissingField)
        }

        val itemFormatVersionValue = fields.getValue(FIELD_FORMAT_VERSION).toIntOrNull()
            ?: return VaultItemSerializationResult.Failure(VaultItemSerializationError.InvalidFieldValue)
        val itemFormatVersion = VaultItemSerializedFormatVersion.fromEncodedValue(itemFormatVersionValue)
            ?: return VaultItemSerializationResult.Failure(VaultItemSerializationError.UnsupportedFormatVersion)

        val id = fields.getValue(FIELD_ID)
        try {
            EncryptedVaultItemId(id)
        } catch (_: IllegalArgumentException) {
            return VaultItemSerializationResult.Failure(VaultItemSerializationError.InvalidId)
        }

        val type = try {
            EncryptedVaultItemType.valueOf(fields.getValue(FIELD_TYPE))
        } catch (_: IllegalArgumentException) {
            return VaultItemSerializationResult.Failure(VaultItemSerializationError.UnsupportedItemType)
        }

        val createdAtEpochMillis = fields.getValue(FIELD_CREATED_AT_EPOCH_MILLIS).toLongOrNull()
            ?: return VaultItemSerializationResult.Failure(VaultItemSerializationError.InvalidTimestamp)
        val updatedAtEpochMillis = fields.getValue(FIELD_UPDATED_AT_EPOCH_MILLIS).toLongOrNull()
            ?: return VaultItemSerializationResult.Failure(VaultItemSerializationError.InvalidTimestamp)
        if (createdAtEpochMillis <= 0L || updatedAtEpochMillis <= 0L || updatedAtEpochMillis < createdAtEpochMillis) {
            return VaultItemSerializationResult.Failure(VaultItemSerializationError.InvalidTimestamp)
        }

        val payloadFormatVersionValue = fields.getValue(FIELD_PAYLOAD_FORMAT_VERSION).toIntOrNull()
            ?: return VaultItemSerializationResult.Failure(VaultItemSerializationError.InvalidPayload)
        val payloadFormatVersion = VaultPayloadSerializedFormatVersion.fromEncodedValue(payloadFormatVersionValue)
            ?: return VaultItemSerializationResult.Failure(VaultItemSerializationError.InvalidPayload)
        val payloadCreatedAtEpochMillis = fields.getValue(FIELD_PAYLOAD_CREATED_AT_EPOCH_MILLIS).toLongOrNull()
            ?: return VaultItemSerializationResult.Failure(VaultItemSerializationError.InvalidPayload)

        val serializedPayload = try {
            SerializedEncryptedVaultPayload(
                formatVersion = payloadFormatVersion,
                nonceHex = fields.getValue(FIELD_PAYLOAD_NONCE_HEX),
                ciphertextHex = fields.getValue(FIELD_PAYLOAD_CIPHERTEXT_HEX),
                authenticationTagHex = fields.getValue(FIELD_PAYLOAD_AUTHENTICATION_TAG_HEX),
                createdAtEpochMillis = payloadCreatedAtEpochMillis,
            )
        } catch (_: IllegalArgumentException) {
            return VaultItemSerializationResult.Failure(VaultItemSerializationError.InvalidPayload)
        }

        try {
            SerializedEncryptedVaultItem(
                formatVersion = itemFormatVersion,
                id = id,
                type = type,
                createdAtEpochMillis = createdAtEpochMillis,
                updatedAtEpochMillis = updatedAtEpochMillis,
                serializedPayload = serializedPayload,
            )
        } catch (_: IllegalArgumentException) {
            return VaultItemSerializationResult.Failure(VaultItemSerializationError.InvalidItem)
        }

        val draftResult = EncryptedVaultItemDraftFactory.create(
            id = id,
            type = type,
            serializedPayload = serializedPayload,
            createdAtEpochMillis = createdAtEpochMillis,
            updatedAtEpochMillis = updatedAtEpochMillis,
        )

        return when (draftResult) {
            is EncryptedVaultItemDraftResult.Success -> VaultItemSerializationResult.Success(draftResult.draft)
            is EncryptedVaultItemDraftResult.Failure -> VaultItemSerializationResult.Failure(draftResult.error.toSerializationError())
        }
    }

    private fun EncryptedVaultItemDraftError.toSerializationError(): VaultItemSerializationError {
        return when (this) {
            EncryptedVaultItemDraftError.InvalidId -> VaultItemSerializationError.InvalidId
            EncryptedVaultItemDraftError.InvalidTimestamp -> VaultItemSerializationError.InvalidTimestamp
            EncryptedVaultItemDraftError.InvalidPayload -> VaultItemSerializationError.InvalidPayload
            EncryptedVaultItemDraftError.InvalidMetadata -> VaultItemSerializationError.InvalidItem
        }
    }
}
