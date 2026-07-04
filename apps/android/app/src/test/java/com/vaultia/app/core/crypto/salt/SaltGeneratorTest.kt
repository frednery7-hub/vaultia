package com.vaultia.app.core.crypto.salt

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class SaltGeneratorTest {
    @Test
    fun exposesMinimumAndRecommendedSaltLengths() {
        assertEquals(16, SaltGenerator.MIN_SALT_LENGTH_BYTES)
        assertEquals(32, SaltGenerator.RECOMMENDED_SALT_LENGTH_BYTES)
    }

    @Test
    fun successProtectsSaltWithDefensiveCopy() {
        val sourceSalt = ByteArray(32) { index -> index.toByte() }
        val success = SaltGenerationResult.Success(sourceSalt)

        sourceSalt[0] = 99
        val firstCopy = success.saltCopy()
        firstCopy[1] = 88

        assertArrayEquals(ByteArray(32) { index -> index.toByte() }, success.saltCopy())
        assertEquals(32, success.saltLengthBytes)
    }

    @Test
    fun successRejectsShortSalt() {
        try {
            SaltGenerationResult.Success(ByteArray(15))
            fail("Expected IllegalArgumentException.")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }
}
