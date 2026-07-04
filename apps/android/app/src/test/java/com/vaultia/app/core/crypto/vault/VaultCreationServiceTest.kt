package com.vaultia.app.core.crypto.vault

import com.vaultia.app.core.crypto.kdf.KdfAlgorithm
import com.vaultia.app.core.crypto.kdf.KdfDerivationError
import com.vaultia.app.core.crypto.kdf.KdfDerivationOutcome
import com.vaultia.app.core.crypto.kdf.KdfDeriver
import com.vaultia.app.core.crypto.kdf.KdfParameters
import com.vaultia.app.core.crypto.kdf.KdfResult
import com.vaultia.app.core.crypto.kdf.KdfVersion
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class VaultCreationServiceTest {
    @Test
    fun requestProtectsSaltWithDefensiveCopy() {
        val salt = validSalt()
        val request = VaultCreationRequest(
            masterPassword = validPassword(),
            salt = salt,
            createdAtEpochMillis = 1000L,
        )

        salt[0] = 99
        val firstCopy = request.saltCopy()
        firstCopy[1] = 88

        assertArrayEquals(validSalt(), request.saltCopy())
    }

    @Test
    fun requestRejectsInvalidTimestamp() {
        expectIllegalArgument {
            VaultCreationRequest(
                masterPassword = validPassword(),
                salt = validSalt(),
                createdAtEpochMillis = 0L,
            )
        }
    }

    @Test
    fun rejectsInvalidMasterPassword() {
        val service = VaultCreationService(kdfDeriver = successfulKdfDeriver())

        val outcome = service.create(
            VaultCreationRequest(
                masterPassword = "short",
                salt = validSalt(),
                createdAtEpochMillis = 1000L,
            ),
        )

        assertFailure(outcome, VaultCreationError.InvalidMasterPassword)
    }

    @Test
    fun rejectsInvalidSalt() {
        val service = VaultCreationService(kdfDeriver = successfulKdfDeriver())

        val outcome = service.create(
            VaultCreationRequest(
                masterPassword = validPassword(),
                salt = ByteArray(15),
                createdAtEpochMillis = 1000L,
            ),
        )

        assertFailure(outcome, VaultCreationError.InvalidCreationRequest)
    }

    @Test
    fun createsVaultDraftInMemoryUsingDefaultConservativeProfile() {
        val capturingKdfDeriver = CapturingSuccessfulKdfDeriver()
        val service = VaultCreationService(kdfDeriver = capturingKdfDeriver)

        val outcome = service.create(validRequest())

        assertTrue(outcome is VaultCreationResult.Success)
        val draft = (outcome as VaultCreationResult.Success).draft

        assertEquals(KdfAlgorithm.ARGON2ID, draft.header.kdfAlgorithm)
        assertEquals(KdfVersion.ARGON2_VERSION_13, draft.header.kdfVersion)
        assertEquals(64 * 1024, draft.header.memoryCostKiB)
        assertEquals(2, draft.header.iterations)
        assertEquals(1, draft.header.parallelism)
        assertEquals(32, draft.header.outputLengthBytes)
        assertEquals(1000L, draft.header.createdAtEpochMillis)
        assertTrue(draft.serializedHeader.startsWith("VAULTIA_HEADER_V1"))
        assertTrue(draft.serializedHeader.endsWith("END_VAULTIA_HEADER"))
        assertEquals(32, draft.kdfResult.derivedKeyLengthBytes)

        val argon2Parameters = capturingKdfDeriver.capturedParameters as KdfParameters.Argon2id
        assertEquals(64 * 1024, argon2Parameters.memoryCostKiB)
        assertEquals(2, argon2Parameters.iterations)
        assertEquals(1, argon2Parameters.parallelism)
        assertEquals(32, argon2Parameters.outputLengthBytes)
        assertArrayEquals(validSalt(), argon2Parameters.saltCopy())
    }

    @Test
    fun mapsKdfFailureToKdfDerivationFailed() {
        val service = VaultCreationService(kdfDeriver = failingKdfDeriver())

        val outcome = service.create(validRequest())

        assertFailure(outcome, VaultCreationError.KdfDerivationFailed)
    }

    @Test
    fun mapsHeaderSerializationFailure() {
        val service = VaultCreationService(
            kdfDeriver = successfulKdfDeriver(),
            headerEncoder = { _ -> "broken-header" },
        )

        val outcome = service.create(validRequest())

        assertFailure(outcome, VaultCreationError.HeaderSerializationFailed)
    }

    private class CapturingSuccessfulKdfDeriver : KdfDeriver {
        var capturedParameters: KdfParameters? = null

        override fun derive(
            password: ByteArray,
            parameters: KdfParameters,
        ): KdfDerivationOutcome {
            capturedParameters = parameters
            return KdfDerivationOutcome.Success(
                KdfResult(
                    derivedKey = ByteArray(parameters.outputLengthBytes) { index ->
                        (password[index % password.size].toInt() xor index).toByte()
                    },
                    parameters = parameters,
                ),
            )
        }
    }

    private fun validRequest(): VaultCreationRequest = VaultCreationRequest(
        masterPassword = validPassword(),
        salt = validSalt(),
        createdAtEpochMillis = 1000L,
    )

    private fun validPassword(): String = "correct horse battery vault"

    private fun validSalt(): ByteArray = ByteArray(32) { index -> index.toByte() }

    private fun successfulKdfDeriver(): KdfDeriver = object : KdfDeriver {
        override fun derive(
            password: ByteArray,
            parameters: KdfParameters,
        ): KdfDerivationOutcome {
            return KdfDerivationOutcome.Success(
                KdfResult(
                    derivedKey = ByteArray(parameters.outputLengthBytes) { 7 },
                    parameters = parameters,
                ),
            )
        }
    }

    private fun failingKdfDeriver(): KdfDeriver = object : KdfDeriver {
        override fun derive(
            password: ByteArray,
            parameters: KdfParameters,
        ): KdfDerivationOutcome {
            return KdfDerivationOutcome.Failure(KdfDerivationError.DerivationFailed)
        }
    }

    private fun assertFailure(
        outcome: VaultCreationResult,
        expectedError: VaultCreationError,
    ) {
        assertTrue(outcome is VaultCreationResult.Failure)
        val failure = outcome as VaultCreationResult.Failure
        assertEquals(expectedError, failure.error)
    }

    private fun expectIllegalArgument(block: () -> Unit) {
        try {
            block()
            fail("Expected IllegalArgumentException.")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }
}
