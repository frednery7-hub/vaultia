package com.vaultia.app.core.crypto.kdf

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class KdfResultTest {
    @Test
    fun rejectsEmptyDerivedKey() {
        expectIllegalArgument {
            KdfResult(
                derivedKey = ByteArray(0),
                parameters = validParameters(),
            )
        }
    }

    @Test
    fun exposesDerivedKeyLengthWithoutExposingInternalArray() {
        val result = KdfResult(
            derivedKey = ByteArray(32) { it.toByte() },
            parameters = validParameters(),
        )

        assertEquals(32, result.derivedKeyLengthBytes)
    }

    @Test
    fun protectsDerivedKeyWithDefensiveCopy() {
        val derivedKey = ByteArray(32) { it.toByte() }
        val result = KdfResult(
            derivedKey = derivedKey,
            parameters = validParameters(),
        )

        derivedKey[0] = 99
        val firstCopy = result.derivedKeyCopy()
        firstCopy[1] = 88

        assertArrayEquals(ByteArray(32) { it.toByte() }, result.derivedKeyCopy())
    }

    private fun validParameters(): KdfParameters = KdfParameters.Argon2id(
        salt = ByteArray(16) { it.toByte() },
        outputLengthBytes = 32,
        iterations = 2,
        memoryCostKiB = 64 * 1024,
        parallelism = 1,
    )

    private fun expectIllegalArgument(block: () -> Unit) {
        try {
            block()
            fail("Expected IllegalArgumentException.")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }
}
