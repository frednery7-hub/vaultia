package com.vaultia.app.core.crypto.salt

import java.security.SecureRandom

class SecureRandomSaltGenerator(
    private val secureRandom: SecureRandom = SecureRandom(),
) : SaltGenerator {
    override fun generateSalt(lengthBytes: Int): SaltGenerationResult {
        if (lengthBytes < SaltGenerator.MIN_SALT_LENGTH_BYTES) {
            return SaltGenerationResult.Failure(SaltGenerationError.InvalidSaltLength)
        }

        return try {
            val salt = ByteArray(lengthBytes)
            secureRandom.nextBytes(salt)
            SaltGenerationResult.Success(salt)
        } catch (_: Throwable) {
            SaltGenerationResult.Failure(SaltGenerationError.GenerationFailed)
        }
    }
}
