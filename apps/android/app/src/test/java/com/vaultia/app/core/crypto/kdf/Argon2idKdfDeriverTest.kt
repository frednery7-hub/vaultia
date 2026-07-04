package com.vaultia.app.core.crypto.kdf

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Argon2idKdfDeriverTest {
    @Test
    fun derivesArgon2idKeyWithValidParameters() {
        val deriver = Argon2idKdfDeriver(hashFunction = deterministicHashFunction())

        val outcome = deriver.derive(
            password = validPassword(),
            parameters = validArgon2idParameters(),
        )

        assertTrue(outcome is KdfDerivationOutcome.Success)
        val success = outcome as KdfDerivationOutcome.Success
        assertEquals(32, success.result.derivedKeyLengthBytes)
    }

    @Test
    fun passesArgon2idParametersToHashFunction() {
        var capturedPassword: ByteArray? = null
        var capturedSalt: ByteArray? = null
        var capturedIterations: Int? = null
        var capturedMemoryCostKiB: Int? = null
        var capturedParallelism: Int? = null
        var capturedOutputLengthBytes: Int? = null

        val deriver = Argon2idKdfDeriver(
            hashFunction = Argon2idHashFunction {
                    password,
                    salt,
                    iterations,
                    memoryCostKiB,
                    parallelism,
                    outputLengthBytes,
                ->
                capturedPassword = password.copyOf()
                capturedSalt = salt.copyOf()
                capturedIterations = iterations
                capturedMemoryCostKiB = memoryCostKiB
                capturedParallelism = parallelism
                capturedOutputLengthBytes = outputLengthBytes
                ByteArray(outputLengthBytes) { 7 }
            },
        )

        val parameters = KdfParameters.Argon2id(
            salt = validSalt(seed = 3),
            outputLengthBytes = 32,
            iterations = 2,
            memoryCostKiB = 64 * 1024,
            parallelism = 1,
        )

        deriver.derive(
            password = validPassword(),
            parameters = parameters,
        )

        assertArrayEquals(validPassword(), capturedPassword)
        assertArrayEquals(validSalt(seed = 3), capturedSalt)
        assertEquals(2, capturedIterations)
        assertEquals(64 * 1024, capturedMemoryCostKiB)
        assertEquals(1, capturedParallelism)
        assertEquals(32, capturedOutputLengthBytes)
    }

    @Test
    fun repeatedDerivationsWithSameInputsProduceSameResult() {
        val deriver = Argon2idKdfDeriver(hashFunction = deterministicHashFunction())
        val parameters = validArgon2idParameters()

        val first = deriver.derive(validPassword(), parameters) as KdfDerivationOutcome.Success
        val second = deriver.derive(validPassword(), parameters) as KdfDerivationOutcome.Success

        assertArrayEquals(first.result.derivedKeyCopy(), second.result.derivedKeyCopy())
    }

    @Test
    fun differentSaltsProduceDifferentResults() {
        val deriver = Argon2idKdfDeriver(hashFunction = deterministicHashFunction())

        val first = deriver.derive(
            password = validPassword(),
            parameters = validArgon2idParameters(salt = validSalt(seed = 1)),
        ) as KdfDerivationOutcome.Success

        val second = deriver.derive(
            password = validPassword(),
            parameters = validArgon2idParameters(salt = validSalt(seed = 2)),
        ) as KdfDerivationOutcome.Success

        assertFalse(first.result.derivedKeyCopy().contentEquals(second.result.derivedKeyCopy()))
    }

    @Test
    fun returnsInvalidParametersForEmptyPassword() {
        val deriver = Argon2idKdfDeriver(hashFunction = deterministicHashFunction())

        val outcome = deriver.derive(
            password = ByteArray(0),
            parameters = validArgon2idParameters(),
        )

        assertFailure(outcome, KdfDerivationError.InvalidParameters)
    }

    @Test
    fun returnsUnsupportedAlgorithmForPbkdf2Parameters() {
        val deriver = Argon2idKdfDeriver(hashFunction = deterministicHashFunction())

        val outcome = deriver.derive(
            password = validPassword(),
            parameters = KdfParameters.Pbkdf2HmacSha256(
                salt = validSalt(seed = 4),
                outputLengthBytes = 32,
                iterations = 100_000,
            ),
        )

        assertFailure(outcome, KdfDerivationError.UnsupportedAlgorithm)
    }

    @Test
    fun mapsNativeLibraryFailures() {
        val deriver = Argon2idKdfDeriver(
            hashFunction = Argon2idHashFunction { _, _, _, _, _, _ ->
                throw UnsatisfiedLinkError("native library unavailable")
            },
        )

        val outcome = deriver.derive(
            password = validPassword(),
            parameters = validArgon2idParameters(),
        )

        assertFailure(outcome, KdfDerivationError.NativeLibraryUnavailable)
    }

    @Test
    fun mapsGenericExecutionFailures() {
        val deriver = Argon2idKdfDeriver(
            hashFunction = Argon2idHashFunction { _, _, _, _, _, _ ->
                throw IllegalStateException("unexpected failure")
            },
        )

        val outcome = deriver.derive(
            password = validPassword(),
            parameters = validArgon2idParameters(),
        )

        assertFailure(outcome, KdfDerivationError.DerivationFailed)
    }

    @Test
    fun resultDoesNotExposeMutableInternalArray() {
        val deriver = Argon2idKdfDeriver(hashFunction = deterministicHashFunction())
        val outcome = deriver.derive(
            password = validPassword(),
            parameters = validArgon2idParameters(),
        ) as KdfDerivationOutcome.Success

        val firstCopy = outcome.result.derivedKeyCopy()
        firstCopy[0] = 99

        val secondCopy = outcome.result.derivedKeyCopy()
        assertFalse(firstCopy.contentEquals(secondCopy))
    }

    private fun deterministicHashFunction(): Argon2idHashFunction =
        Argon2idHashFunction { password, salt, iterations, memoryCostKiB, parallelism, outputLengthBytes ->
            ByteArray(outputLengthBytes) { index ->
                val passwordByte = password[index % password.size].toInt()
                val saltByte = salt[index % salt.size].toInt()
                (passwordByte xor saltByte xor iterations xor memoryCostKiB xor parallelism xor index).toByte()
            }
        }

    private fun validArgon2idParameters(
        salt: ByteArray = validSalt(seed = 1),
    ): KdfParameters.Argon2id = KdfParameters.Argon2id(
        salt = salt,
        outputLengthBytes = 32,
        iterations = 2,
        memoryCostKiB = 32 * 1024,
        parallelism = 1,
    )

    private fun validPassword(): ByteArray = "phase-24-test-password".toByteArray()

    private fun validSalt(seed: Int): ByteArray = ByteArray(16) { index ->
        (seed + index).toByte()
    }

    private fun assertFailure(
        outcome: KdfDerivationOutcome,
        expectedError: KdfDerivationError,
    ) {
        assertTrue(outcome is KdfDerivationOutcome.Failure)
        val failure = outcome as KdfDerivationOutcome.Failure
        assertEquals(expectedError, failure.error)
    }
}
