package com.vaultia.app.core.storage.model

class StorageRecordId private constructor(
    val value: String,
) {
    init {
        require(isValid(value)) { "Invalid storage record id." }
    }

    override fun equals(other: Any?): Boolean {
        return other is StorageRecordId && value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "StorageRecordId(value=$value)"
    }

    companion object {
        private const val MIN_LENGTH = 8
        private const val MAX_LENGTH = 128
        private val allowedPattern = Regex("^[A-Za-z0-9][A-Za-z0-9_-]{7,127}$")

        fun from(value: String): StorageRecordId {
            return StorageRecordId(value)
        }

        fun isValid(value: String): Boolean {
            if (value.length !in MIN_LENGTH..MAX_LENGTH) return false
            if (value.any { it.isWhitespace() }) return false
            if (value.contains("/")) return false
            if (value.contains("\\")) return false
            if (value.contains("..")) return false
            if (value.contains(":")) return false
            if (value.contains("~")) return false
            return allowedPattern.matches(value)
        }
    }
}
