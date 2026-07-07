package com.vaultia.app.core.storage.model

class EncryptedPayloadPointer private constructor(
    val value: String,
) {
    init {
        require(isValid(value)) { "Invalid encrypted payload pointer." }
    }

    override fun equals(other: Any?): Boolean {
        return other is EncryptedPayloadPointer && value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "EncryptedPayloadPointer(value=$value)"
    }

    companion object {
        private const val MIN_LENGTH = 12
        private const val MAX_LENGTH = 192
        private val allowedPattern = Regex("^[A-Za-z0-9][A-Za-z0-9_.-]{7,187}\\.enc$")

        fun from(value: String): EncryptedPayloadPointer {
            return EncryptedPayloadPointer(value)
        }

        fun isValid(value: String): Boolean {
            if (value.length !in MIN_LENGTH..MAX_LENGTH) return false
            if (value.any { it.isWhitespace() }) return false
            if (value.contains("/")) return false
            if (value.contains("\\")) return false
            if (value.contains("..")) return false
            if (value.contains(":")) return false
            if (value.contains("~")) return false
            if (!value.endsWith(".enc")) return false
            return allowedPattern.matches(value)
        }
    }
}
