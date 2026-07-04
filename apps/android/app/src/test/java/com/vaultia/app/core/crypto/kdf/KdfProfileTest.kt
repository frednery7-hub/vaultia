package com.vaultia.app.core.crypto.kdf

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class KdfProfileTest {
    @Test
    fun createsValidArgon2idProfile() {
        val profile = validProfile()

        assertEquals("argon2id-test-profile", profile.id)
        assertEquals(KdfAlgorithm.ARGON2ID, profile.algorithm)
        assertEquals(KdfVersion.ARGON2_VERSION_13, profile.kdfVersion)
        assertEquals(32 * 1024, profile.memoryCostKiB)
        assertEquals(2, profile.iterations)
        assertEquals(1, profile.parallelism)
        assertEquals(32, profile.outputLengthBytes)
        assertEquals(32, profile.recommendedSaltLengthBytes)
    }

    @Test
    fun buildsArgon2idParametersFromProfileAndSalt() {
        val parameters = validProfile().toArgon2idParameters(salt = validSalt())

        assertEquals(KdfAlgorithm.ARGON2ID, parameters.algorithm)
        assertEquals(32, parameters.outputLengthBytes)
        assertEquals(2, parameters.iterations)
        assertEquals(32 * 1024, parameters.memoryCostKiB)
        assertEquals(1, parameters.parallelism)
    }

    @Test
    fun rejectsInvalidProfileValues() {
        expectIllegalArgument { validProfile(id = " ") }
        expectIllegalArgument { validProfile(memoryCostKiB = 0) }
        expectIllegalArgument { validProfile(iterations = 0) }
        expectIllegalArgument { validProfile(parallelism = 0) }
        expectIllegalArgument { validProfile(outputLengthBytes = 0) }
        expectIllegalArgument { validProfile(recommendedSaltLengthBytes = 15) }
    }

    private fun validProfile(
        id: String = "argon2id-test-profile",
        memoryCostKiB: Int = 32 * 1024,
        iterations: Int = 2,
        parallelism: Int = 1,
        outputLengthBytes: Int = 32,
        recommendedSaltLengthBytes: Int = 32,
    ): KdfProfile = KdfProfile(
        id = id,
        algorithm = KdfAlgorithm.ARGON2ID,
        kdfVersion = KdfVersion.ARGON2_VERSION_13,
        memoryCostKiB = memoryCostKiB,
        iterations = iterations,
        parallelism = parallelism,
        outputLengthBytes = outputLengthBytes,
        recommendedSaltLengthBytes = recommendedSaltLengthBytes,
    )

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
