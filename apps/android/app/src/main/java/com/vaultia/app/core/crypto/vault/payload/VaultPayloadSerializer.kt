package com.vaultia.app.core.crypto.vault.payload

import com.vaultia.app.core.crypto.encryption.EncryptedPayload

object VaultPayloadSerializer {
    private const val MAGIC = "VAULTIA_PAYLOAD_V1"
    private const val FOOTER = "END_VAULTIA_PAYLOAD"
    private const val FIELD_FORMAT_VERSION = "formatVersion"
    private const val FIELD_NONCE_HEX = "nonceHex"
    private const val FIELD_CIPHERTEXT_HEX = "ciphertextHex"
    private const val FIELD_AUTHENTICATION_TAG_HEX = "authenticationTagHex"
    private const val FIELD_CREATED_AT_EPOCH_MILLIS = "createdAtEpochMillis"

    private val requiredFields = listOf(
        FIELD_FORMAT_VERSION,
        FIELD_NONCE_HEX,
        FIELD_CIPHERTEXT_HEX,
        FIELD_AUTHENTICATION_TAG_HEX,
        FIELD_CREATED_AT_EPOCH_MILLIS,
    )

    fun encode(draft: EncryptedVaultPayloadDraft): String {
        val encryptedPayload = draft.encryptedPayload
        return listOf(
            MAGIC,
            "$FIELD_FORMAT_VERSION=${VaultPayloadSerializedFormatVersion.V1_0.encodedValue}",
            "$FIELD_NONCE_HEX=${encryptedPayload.nonceCopy().toHex()}",
            "$FIELD_CIPHERTEXT_HEX=${encryptedPayload.ciphertextCopy().toHex()}",
            "$FIELD_AUTHENTICATION_TAG_HEX=${encryptedPayload.authenticationTagCopy().toHex()}",
            "$FIELD_CREATED_AT_EPOCH_MILLIS=${draft.createdAtEpochMillis}",
            FOOTER,
        ).joinToString(separator = "\n")
    }

    fun decode(input: String): VaultPayloadSerializationResult {
        if (input.isBlank()) {
            return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.EmptyInput)
        }

        val lines = input.lines().filter { line -> line.isNotBlank() }
        if (lines.firstOrNull() != MAGIC || lines.lastOrNull() != FOOTER) {
            return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.InvalidMagic)
        }

        val fields = mutableMapOf<String, String>()
        for (line in lines.drop(1).dropLast(1)) {
            val separatorIndex = line.indexOf('=')
            if (separatorIndex <= 0 || separatorIndex == line.lastIndex) {
                return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.InvalidFieldValue)
            }
            val name = line.substring(0, separatorIndex)
            val value = line.substring(separatorIndex + 1)
            if (fields.containsKey(name)) {
                return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.DuplicateField)
            }
            fields[name] = value
        }

        if (!requiredFields.all { field -> fields.containsKey(field) }) {
            return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.MissingField)
        }

        val formatVersionValue = fields.getValue(FIELD_FORMAT_VERSION).toIntOrNull()
            ?: return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.InvalidFieldValue)
        val formatVersion = VaultPayloadSerializedFormatVersion.fromEncodedValue(formatVersionValue)
            ?: return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.UnsupportedFormatVersion)

        val createdAtEpochMillis = fields.getValue(FIELD_CREATED_AT_EPOCH_MILLIS).toLongOrNull()
            ?: return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.InvalidTimestamp)
        if (createdAtEpochMillis <= 0L) {
            return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.InvalidTimestamp)
        }

        val nonce = fields.getValue(FIELD_NONCE_HEX).hexToByteArrayOrNull()
            ?: return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.InvalidNonceEncoding)
        val ciphertext = fields.getValue(FIELD_CIPHERTEXT_HEX).hexToByteArrayOrNull()
            ?: return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.InvalidCiphertextEncoding)
        val authenticationTag = fields.getValue(FIELD_AUTHENTICATION_TAG_HEX).hexToByteArrayOrNull()
            ?: return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.InvalidAuthenticationTagEncoding)

        val serializedPayload = try {
            SerializedEncryptedVaultPayload(
                formatVersion = formatVersion,
                nonceHex = fields.getValue(FIELD_NONCE_HEX),
                ciphertextHex = fields.getValue(FIELD_CIPHERTEXT_HEX),
                authenticationTagHex = fields.getValue(FIELD_AUTHENTICATION_TAG_HEX),
                createdAtEpochMillis = createdAtEpochMillis,
            )
        } catch (_: IllegalArgumentException) {
            return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.InvalidFieldValue)
        }

        val encryptedPayload = try {
            EncryptedPayload(
                nonce = nonce,
                ciphertext = ciphertext,
                authenticationTag = authenticationTag,
            )
        } catch (_: IllegalArgumentException) {
            return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.InvalidEncryptedPayload)
        }

        val draft = try {
            EncryptedVaultPayloadDraft(
                encryptedPayload = encryptedPayload,
                createdAtEpochMillis = serializedPayload.createdAtEpochMillis,
            )
        } catch (_: IllegalArgumentException) {
            return VaultPayloadSerializationResult.Failure(VaultPayloadSerializationError.InvalidEncryptedPayload)
        }

        return VaultPayloadSerializationResult.Success(draft)
    }

    private fun ByteArray.toHex(): String {
        val result = StringBuilder(size * 2)
        forEach { byte ->
            val value = byte.toInt() and 0xff
            result.append(HEX_CHARS[value ushr 4])
            result.append(HEX_CHARS[value and 0x0f])
        }
        return result.toString()
    }

    private fun String.hexToByteArrayOrNull(): ByteArray? {
        if (isEmpty() || length % 2 != 0) {
            return null
        }
        val output = ByteArray(length / 2)
        for (index in output.indices) {
            val high = this[index * 2].hexValueOrNull() ?: return null
            val low = this[index * 2 + 1].hexValueOrNull() ?: return null
            output[index] = ((high shl 4) or low).toByte()
        }
        return output
    }

    private fun Char.hexValueOrNull(): Int? {
        return when (this) {
            in '0'..'9' -> this - '0'
            in 'a'..'f' -> this - 'a' + 10
            in 'A'..'F' -> this - 'A' + 10
            else -> null
        }
    }

    private val HEX_CHARS = "0123456789abcdef".toCharArray()
}
