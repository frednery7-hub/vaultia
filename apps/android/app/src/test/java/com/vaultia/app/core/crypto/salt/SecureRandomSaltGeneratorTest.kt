package com.vaultia.app.core.crypto.salt

import java.security.SecureRandom
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureRandomSaltGeneratorTest {
    @Test
    fun rejectsSaltLengthBelowMinimum() {
        val result = SecureRandomSaltGenerator().generateSalt(15)

        assertTrue(result is SaltGenerationResult.Failure)
        val failure = result as SaltGenerationResult.Failure
        assertEquals(SaltGenerationError.InvalidSaltLength, failure.error)
    }

    @Test
    fun generatesMinimumSaltLength() {
        val result = SecureRandomSaltGenerator().generateSalt(16)

        assertTrue(result is SaltGenerationResult.Success)
        val success = result as SaltGenerationResult.Success
        assertEquals(16, success.saltLengthBytes)
        assertEquals(16, success.saltCopy().size)
    }

    @Test
    fun generatesRecommendedSaltLength() {
        val result = SecureRandomSaltGenerator().generateRecommendedSalt()

        assertTrue(result is SaltGenerationResult.Success)
        val success = result as SaltGenerationResult.Success
        assertEquals(32, success.saltLengthBytes)
        assertEquals(32, success.saltCopy().size)
    }

    @Test
    fun successiveCallsReturnIndependentArraysAndDifferentContent() {
        val generator = SecureRandomSaltGenerator()

        val first = generator.generateRecommendedSalt() as SaltGenerationResult.Success
        val second = generator.generateRecommendedSalt() as SaltGenerationResult.Success

        val firstSalt = first.saltCopy()
        val secondSalt = second.saltCopy()

        assertFalse(firstSalt === secondSalt)
        assertFalse(firstSalt.contentEquals(secondSalt))
    }

    @Test
    fun mapsInternalGenerationFailureToGenerationFailed() {
        val failingSecureRandom = object : SecureRandom() {
            override fun nextBytes(bytes: ByteArray) {
                throw IllegalStateException("Forced failure.")
            }
        }

        val result = SecureRandomSaltGenerator(failingSecureRandom).generateRecommendedSalt()

        assertTrue(result is SaltGenerationResult.Failure)
        val failure = result as SaltGenerationResult.Failure
        assertEquals(SaltGenerationError.GenerationFailed, failure.error)
    }
}
