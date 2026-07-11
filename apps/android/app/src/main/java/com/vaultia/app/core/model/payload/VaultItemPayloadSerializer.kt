package com.vaultia.app.core.model.payload

import java.util.Base64

object VaultItemPayloadSerializer {
    private const val MAGIC = "VAULTIA_PAYLOAD_V1"
    private const val FOOTER = "END_VAULTIA_PAYLOAD"
    
    private const val FIELD_TYPE = "type"
    private const val FIELD_VERSION = "version"
    private const val FIELD_TITLE = "title"
    
    private const val TYPE_PASSWORD = "PASSWORD"
    private const val TYPE_NOTE = "NOTE"

    private fun encodeBase64(value: String): String = Base64.getEncoder().encodeToString(value.toByteArray(Charsets.UTF_8))
    private fun decodeBase64(value: String): String = String(Base64.getDecoder().decode(value), Charsets.UTF_8)

    fun encode(payload: VaultItemPayload): ByteArray {
        val lines = mutableListOf<String>()
        lines.add(MAGIC)
        lines.add("$FIELD_VERSION=${payload.version}")
        lines.add("$FIELD_TITLE=${encodeBase64(payload.title)}")
        
        when (payload) {
            is PasswordPayload -> {
                lines.add("$FIELD_TYPE=$TYPE_PASSWORD")
                lines.add("username=${encodeBase64(payload.username)}")
                lines.add("passwordValue=${encodeBase64(payload.passwordValue)}")
                lines.add("url=${encodeBase64(payload.url)}")
                lines.add("additionalNotes=${encodeBase64(payload.additionalNotes)}")
            }
            is NotePayload -> {
                lines.add("$FIELD_TYPE=$TYPE_NOTE")
                lines.add("text=${encodeBase64(payload.text)}")
            }
        }
        lines.add(FOOTER)
        return lines.joinToString(separator = "\n").toByteArray(Charsets.UTF_8)
    }

    fun decode(inputBytes: ByteArray): VaultItemPayloadSerializationResult {
        if (inputBytes.isEmpty()) return VaultItemPayloadSerializationResult.Failure(VaultItemPayloadSerializationError.EmptyInput)
        
        val input = String(inputBytes, Charsets.UTF_8)
        val lines = input.lines().filter { it.isNotBlank() }
        
        if (lines.firstOrNull() != MAGIC || lines.lastOrNull() != FOOTER) {
            return VaultItemPayloadSerializationResult.Failure(VaultItemPayloadSerializationError.InvalidMagic)
        }
        
        val fields = mutableMapOf<String, String>()
        for (line in lines.drop(1).dropLast(1)) {
            val separatorIndex = line.indexOf('=')
            if (separatorIndex <= 0 || separatorIndex == line.lastIndex) {
                return VaultItemPayloadSerializationResult.Failure(VaultItemPayloadSerializationError.InvalidFieldValue)
            }
            val name = line.substring(0, separatorIndex)
            val value = line.substring(separatorIndex + 1)
            fields[name] = value
        }
        
        if (!fields.containsKey(FIELD_TYPE) || !fields.containsKey(FIELD_VERSION) || !fields.containsKey(FIELD_TITLE)) {
            return VaultItemPayloadSerializationResult.Failure(VaultItemPayloadSerializationError.MissingField)
        }
        
        val version = fields[FIELD_VERSION]?.toIntOrNull() ?: return VaultItemPayloadSerializationResult.Failure(VaultItemPayloadSerializationError.InvalidFormatVersion)
        val title = decodeBase64(fields.getValue(FIELD_TITLE))
        
        return try {
            when (fields[FIELD_TYPE]) {
                TYPE_PASSWORD -> {
                    if (!fields.containsKey("username") || !fields.containsKey("passwordValue") || !fields.containsKey("url") || !fields.containsKey("additionalNotes")) {
                        return VaultItemPayloadSerializationResult.Failure(VaultItemPayloadSerializationError.MissingField)
                    }
                    val payload = PasswordPayload(
                        title = title,
                        username = decodeBase64(fields.getValue("username")),
                        passwordValue = decodeBase64(fields.getValue("passwordValue")),
                        url = decodeBase64(fields.getValue("url")),
                        additionalNotes = decodeBase64(fields.getValue("additionalNotes")),
                        version = version
                    )
                    VaultItemPayloadSerializationResult.Success(payload)
                }
                TYPE_NOTE -> {
                    if (!fields.containsKey("text")) return VaultItemPayloadSerializationResult.Failure(VaultItemPayloadSerializationError.MissingField)
                    val payload = NotePayload(
                        title = title,
                        text = decodeBase64(fields.getValue("text")),
                        version = version
                    )
                    VaultItemPayloadSerializationResult.Success(payload)
                }
                else -> VaultItemPayloadSerializationResult.Failure(VaultItemPayloadSerializationError.UnsupportedPayloadType)
            }
        } catch (e: Exception) {
            VaultItemPayloadSerializationResult.Failure(VaultItemPayloadSerializationError.InvalidFieldValue)
        }
    }
}
