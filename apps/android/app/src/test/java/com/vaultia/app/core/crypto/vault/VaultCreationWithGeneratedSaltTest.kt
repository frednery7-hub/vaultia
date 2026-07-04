package com.vaultia.app.core.crypto.vault

import com.vaultia.app.core.crypto.kdf.KdfDerivationError
import com.vaultia.app.core.crypto.kdf.KdfDerivationOutcome
import com.vaultia.app.core.crypto.kdf.KdfDeriver
import com.vaultia.app.core.crypto.kdf.KdfParameters
import com.vaultia.app.core.crypto.kdf.KdfResult
import com.vaultia.app.core.crypto.salt.SaltGenerationError
import com.vaultia.app.core.crypto.salt.SaltGenerationResult
import com.vaultia.app.core.crypto.salt.SaltGenerator
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class VaultCreationWithGeneratedSaltTest {
    @Test
    fun requestRejectsInvalidTimestamp() {
        expectIllegalArgument {
            VaultCreationWithoutExternalSaltRequest(
                masterPassword = validPassword(),
                createdAtEpochMillis = 0L,
            )
        }
    }

    @Test
    fun rejectsInvalidMasterPasswordBeforeGeneratingSalt() {
        val saltGenerator = CapturingSaltGenerator(validSalt())
        val service = VaultCreationService(
            kdfDeriver = successfulKdfDeriver(),
            saltGenerator = saltGenerator,
        )

        val outcome = service.createWithGeneratedSalt(
            VaultCreationWithoutExternalSaltRequest(
                masterPassword = "short",
                createdAtEpochMillis = 1000L,
            ),
        )

        assertFailure(outcome, VaultCreationError.InvalidMasterPassword)
        assertEquals(0, saltGenerator.generateSaltCalls)
    }

    @Test
    fun mapsMissingSaltGeneratorToSaltGenerationFailed() {
        val service = VaultCreationService(kdfDeriver = successfulKdfDeriver())

        val outcome = service.createWithGeneratedSalt(validGeneratedSaltRequest())

        assertFailure(outcome, VaultCreationError.SaltGenerationFailed)
    }

    @Test
    fun mapsSaltGenerationFailureToSaltGenerationFailed() {
        val service = VaultCreationService(
            kdfDeriver = successfulKdfDeriver(),
            saltGenerator = failingSaltGenerator(),
        )

        val outcome = service.createWithGeneratedSalt(validGeneratedSaltRequest())

        assertFailure(outcome, VaultCreationError.SaltGenerationFailed)
    }

    @Test
    fun createsVaultDraftUsingGeneratedRecommendedSalt() {
        val generatedSalt = validSalt()
        val saltGenerator = CapturingSaltGenerator(generatedSalt)
        val kdfDeriver = CapturingSuccessfulKdfDeriver()
        val service = VaultCreationService(
            kdfDeriver = kdfDeriver,
            saltGenerator = saltGenerator,
        )

        val outcome = service.createWithGeneratedSalt(validGeneratedSaltRequest())

        assertTrue(outcome is VaultCreationResult.Success)
        val draft = (outcome as VaultCreationResult.Success).draft

        assertEquals(1, saltGenerator.generateSaltCalls)
        assertEquals(SaltGenerator.RECOMMENDED_SALT_LENGTH_BYTES, saltGenerator.requestedLengthBytes)
        assertArrayEquals(generatedSalt, draft.header.saltCopy())
        assertArrayEquals(generatedSalt, (kdfDeriver.capturedParameters as KdfParameters.Argon2id).saltCopy())
        assertEquals(32, draft.kdfResult.derivedKeyLengthBytes)
        assertTrue(draft.serializedHeader.startsWith("VAULTIA_HEADER_V1"))
        assertTrue(draft.serializedHeader.endsWith("END_VAULTIA_HEADER"))
    }

    @Test
    fun externalSaltFlowStillWorksWithoutSaltGenerator() {
        val service = VaultCreationService(kdfDeriver = successfulKdfDeriver())

        val outcome = service.create(
            VaultCreationRequest(
                masterPassword = validPassword(),
                salt = validSalt(),
                createdAtEpochMillis = 1000L,
            ),
        )

        assertTrue(outcome is VaultCreationResult.Success)
    }

    @Test
    fun mapsKdfFailureAfterSaltGeneration() {
        val service = VaultCreationService(
            kdfDeriver = failingKdfDeriver(),
            saltGenerator = CapturingSaltGenerator(validSalt()),
        )

        val outcome = service.createWithGeneratedSalt(validGeneratedSaltRequest())

        assertFailure(outcome, VaultCreationError.KdfDerivationFailed)
    }

    private class CapturingSaltGenerator(
        private val salt: ByteArray,
    ) : SaltGenerator {
        var generateSaltCalls: Int = 0
        var requestedLengthBytes: Int = 0

        override fun generateSalt(lengthBytes: Int): SaltGenerationResult {
            generateSaltCalls += 1
            requestedLengthBytes = lengthBytes
            return SaltGenerationResult.Success(salt)
        }
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
                    derivedKey = ByteArray(parameters.outputLengthBytes) { 9 },
                    parameters = parameters,
                ),
            )
        }
    }

    private fun validGeneratedSaltRequest(): VaultCreationWithoutExternalSaltRequest {
        return VaultCreationWithoutExternalSaltRequest(
            masterPassword = validPassword(),
            createdAtEpochMillis = 1000L,
        )
    }

    private fun validPassword(): String = "correct horse battery vault"

    private fun validSalt(): ByteArray = ByteArray(32) { index -> (index + 1).toByte() }

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

    private fun failingSaltGenerator(): SaltGenerator = object : SaltGenerator {
        override fun generateSalt(lengthBytes: Int): SaltGenerationResult {
            return SaltGenerationResult.Failure(SaltGenerationError.GenerationFailed)
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
