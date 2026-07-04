package com.vaultia.app.core.crypto.salt

interface SaltGenerator {
    fun generateSalt(lengthBytes: Int): SaltGenerationResult

    fun generateRecommendedSalt(): SaltGenerationResult {
        return generateSalt(RECOMMENDED_SALT_LENGTH_BYTES)
    }

    companion object {
        const val MIN_SALT_LENGTH_BYTES: Int = 16
        const val RECOMMENDED_SALT_LENGTH_BYTES: Int = 32
    }
}
