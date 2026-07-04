package com.vaultia.app.core.crypto.salt

sealed class SaltGenerationResult {
    class Success(salt: ByteArray) : SaltGenerationResult() {
        private val saltBytes: ByteArray = salt.copyOf()

        init {
            require(saltBytes.size >= SaltGenerator.MIN_SALT_LENGTH_BYTES) {
                "Salt must be at least ${SaltGenerator.MIN_SALT_LENGTH_BYTES} bytes."
            }
        }

        val saltLengthBytes: Int
            get() = saltBytes.size

        fun saltCopy(): ByteArray = saltBytes.copyOf()
    }

    class Failure(val error: SaltGenerationError) : SaltGenerationResult()
}
