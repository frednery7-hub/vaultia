package com.vaultia.app.core.crypto.kdf

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class KdfParametersTest {
    @Test
    fun rejectsEmptySalt() {
        expectIllegalArgument {
            KdfParameters.Argon2id(
                salt = ByteArray(0),
                outputLengthBytes = 32,
                iterations = 2,
                memoryCostKiB = 32 * 1024,
                parallelism = 1,
            )
        }
    }

    @Test
    fun rejectsSaltShorterThanSixteenBytes() {
        expectIllegalArgument {
            KdfParameters.Pbkdf2HmacSha256(
                salt = ByteArray(15),
                outputLengthBytes = 32,
                iterations = 100_000,
            )
        }
    }

    @Test
    fun rejectsNonPositiveOutputLength() {
        expectIllegalArgument {
            KdfParameters.Argon2id(
                salt = validSalt(),
                outputLengthBytes = 0,
                iterations = 2,
                memoryCostKiB = 32 * 1024,
                parallelism = 1,
            )
        }
    }

    @Test
    fun rejectsNonPositiveIterations() {
        expectIllegalArgument {
            KdfParameters.Pbkdf2HmacSha256(
                salt = validSalt(),
                outputLengthBytes = 32,
                iterations = 0,
            )
        }
    }

    @Test
    fun rejectsNonPositiveArgon2idMemoryCost() {
        expectIllegalArgument {
            KdfParameters.Argon2id(
                salt = validSalt(),
                outputLengthBytes = 32,
                iterations = 2,
                memoryCostKiB = 0,
                parallelism = 1,
            )
        }
    }

    @Test
    fun rejectsNonPositiveArgon2idParallelism() {
        expectIllegalArgument {
            KdfParameters.Argon2id(
                salt = validSalt(),
                outputLengthBytes = 32,
                iterations = 2,
                memoryCostKiB = 32 * 1024,
                parallelism = 0,
            )
        }
    }

    @Test
    fun acceptsValidArgon2idParameters() {
        val parameters = KdfParameters.Argon2id(
            salt = validSalt(),
            outputLengthBytes = 32,
            iterations = 2,
            memoryCostKiB = 64 * 1024,
            parallelism = 1,
        )

        assertEquals(KdfAlgorithm.ARGON2ID, parameters.algorithm)
        assertEquals(32, parameters.outputLengthBytes)
        assertEquals(2, parameters.iterations)
        assertEquals(64 * 1024, parameters.memoryCostKiB)
        assertEquals(1, parameters.parallelism)
    }

    @Test
    fun acceptsValidPbkdf2Parameters() {
        val parameters = KdfParameters.Pbkdf2HmacSha256(
            salt = validSalt(),
            outputLengthBytes = 32,
            iterations = 100_000,
        )

        assertEquals(KdfAlgorithm.PBKDF2_HMAC_SHA256, parameters.algorithm)
        assertEquals(32, parameters.outputLengthBytes)
        assertEquals(100_000, parameters.iterations)
    }

    @Test
    fun protectsSaltWithDefensiveCopy() {
        val salt = validSalt()
        val parameters = KdfParameters.Pbkdf2HmacSha256(
            salt = salt,
            outputLengthBytes = 32,
            iterations = 100_000,
        )

        salt[0] = 99
        val firstCopy = parameters.saltCopy()
        firstCopy[1] = 88

        assertArrayEquals(ByteArray(16) { it.toByte() }, parameters.saltCopy())
    }

    private fun validSalt(): ByteArray = ByteArray(16) { it.toByte() }

    private fun expectIllegalArgument(block: () -> Unit) {
        try {
            block()
            fail("Expected IllegalArgumentException.")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }
}
