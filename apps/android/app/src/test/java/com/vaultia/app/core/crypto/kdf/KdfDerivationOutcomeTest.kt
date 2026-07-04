package com.vaultia.app.core.crypto.kdf

import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class KdfDerivationOutcomeTest {
    @Test
    fun representsSuccessfulDerivation() {
        val result = KdfResult(
            derivedKey = ByteArray(32) { it.toByte() },
            parameters = validParameters(),
        )

        val outcome: KdfDerivationOutcome = KdfDerivationOutcome.Success(result)

        assertTrue(outcome is KdfDerivationOutcome.Success)
        val success = outcome as KdfDerivationOutcome.Success
        assertSame(result, success.result)
    }

    @Test
    fun representsFailedDerivation() {
        val outcome: KdfDerivationOutcome = KdfDerivationOutcome.Failure(
            KdfDerivationError.NativeLibraryUnavailable,
        )

        assertTrue(outcome is KdfDerivationOutcome.Failure)
        val failure = outcome as KdfDerivationOutcome.Failure
        assertSame(KdfDerivationError.NativeLibraryUnavailable, failure.error)
    }

    private fun validParameters(): KdfParameters = KdfParameters.Pbkdf2HmacSha256(
        salt = ByteArray(16) { it.toByte() },
        outputLengthBytes = 32,
        iterations = 100_000,
    )
}
