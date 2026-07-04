package com.vaultia.app.core.crypto.vault.payload

import com.vaultia.app.core.crypto.encryption.AuthenticatedCipher
import com.vaultia.app.core.crypto.encryption.DecryptionResult
import com.vaultia.app.core.crypto.encryption.EncryptedPayload
import com.vaultia.app.core.crypto.encryption.EncryptionError
import com.vaultia.app.core.crypto.encryption.EncryptionResult
import com.vaultia.app.core.crypto.encryption.PlaintextPayload
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class VaultPayloadDecryptionServiceTest {
    @Test
    fun requestRejectsEmptyKey() {
        expectIllegalArgument {
            VaultPayloadDecryptionRequest(
                key = ByteArray(0),
                encryptedPayload = sampleEncryptedPayload(),
                decryptedAtEpochMillis = 1L,
            )
        }
    }

    @Test
    fun requestRejectsInvalidTimestamp() {
        expectIllegalArgument {
            VaultPayloadDecryptionRequest(
                key = validKey(),
                encryptedPayload = sampleEncryptedPayload(),
                decryptedAtEpochMillis = 0L,
            )
        }
    }

    @Test
    fun requestProtectsKeyWithDefensiveCopy() {
        val key = validKey()
        val request = VaultPayloadDecryptionRequest(
            key = key,
            encryptedPayload = sampleEncryptedPayload(),
            decryptedAtEpochMillis = 123L,
        )

        key[0] = 99
        val keyCopy = request.keyCopy()
        keyCopy[1] = 88

        assertArrayEquals(validKey(), request.keyCopy())
        assertEquals(32, request.keyLengthBytes)
    }

    @Test
    fun requestProtectsEncryptedPayloadWithDefensiveCopy() {
        val encryptedPayload = sampleEncryptedPayload()
        val request = VaultPayloadDecryptionRequest(
            key = validKey(),
            encryptedPayload = encryptedPayload,
            decryptedAtEpochMillis = 123L,
        )

        val nonceCopy = request.encryptedPayload.nonceCopy()
        val ciphertextCopy = request.encryptedPayload.ciphertextCopy()
        val tagCopy = request.encryptedPayload.authenticationTagCopy()

        nonceCopy[0] = 99
        ciphertextCopy[0] = 88
        tagCopy[0] = 77

        assertArrayEquals(ByteArray(12) { index -> index.toByte() }, request.encryptedPayload.nonceCopy())
        assertArrayEquals(byteArrayOf(10, 11, 12), request.encryptedPayload.ciphertextCopy())
        assertArrayEquals(ByteArray(16) { index -> (index + 20).toByte() }, request.encryptedPayload.authenticationTagCopy())
    }

    @Test
    fun serviceCallsAuthenticatedCipherDecrypt() {
        val cipher = RecordingCipher()
        val request = validRequest()

        VaultPayloadDecryptionService(cipher).decrypt(request)

        assertTrue(cipher.decryptCalled)
        assertArrayEquals(validKey(), cipher.receivedKey)
        assertArrayEquals(sampleEncryptedPayload().nonceCopy(), cipher.receivedEncryptedPayload.nonceCopy())
        assertArrayEquals(sampleEncryptedPayload().ciphertextCopy(), cipher.receivedEncryptedPayload.ciphertextCopy())
        assertArrayEquals(sampleEncryptedPayload().authenticationTagCopy(), cipher.receivedEncryptedPayload.authenticationTagCopy())
    }

    @Test
    fun serviceReturnsDecryptedVaultPayloadDraftOnSuccess() {
        val result = VaultPayloadDecryptionService(RecordingCipher()).decrypt(validRequest())

        assertTrue(result is VaultPayloadDecryptionResult.Success)
        val draft = (result as VaultPayloadDecryptionResult.Success).draft

        assertEquals(123456789L, draft.decryptedAtEpochMillis)
        assertArrayEquals(validPlaintext(), draft.plaintextPayload.plaintextCopy())
    }

    @Test
    fun draftRejectsInvalidTimestamp() {
        expectIllegalArgument {
            DecryptedVaultPayloadDraft(
                plaintextPayload = PlaintextPayload(validPlaintext()),
                decryptedAtEpochMillis = 0L,
            )
        }
    }

    @Test
    fun draftDoesNotExposeKeyFields() {
        val fieldNames = DecryptedVaultPayloadDraft::class.java.declaredFields
            .map { field -> field.name.lowercase() }

        assertFalse(fieldNames.any { fieldName -> fieldName.contains("key") })
    }

    @Test
    fun authenticationFailureMapsToAuthenticationFailed() {
        val result = VaultPayloadDecryptionService(
            FailingCipher(EncryptionError.AuthenticationFailed),
        ).decrypt(validRequest())

        assertTrue(result is VaultPayloadDecryptionResult.Failure)
        assertEquals(
            VaultPayloadDecryptionError.AuthenticationFailed,
            (result as VaultPayloadDecryptionResult.Failure).error,
        )
    }

    @Test
    fun genericDecryptionFailureMapsToDecryptionFailed() {
        val result = VaultPayloadDecryptionService(
            FailingCipher(EncryptionError.DecryptionFailed),
        ).decrypt(validRequest())

        assertTrue(result is VaultPayloadDecryptionResult.Failure)
        assertEquals(
            VaultPayloadDecryptionError.DecryptionFailed,
            (result as VaultPayloadDecryptionResult.Failure).error,
        )
    }

    private class RecordingCipher : AuthenticatedCipher {
        var decryptCalled: Boolean = false
        var receivedKey: ByteArray = ByteArray(0)
        var receivedEncryptedPayload: EncryptedPayload = sampleEncryptedPayload()

        override fun encrypt(
            key: ByteArray,
            plaintext: PlaintextPayload,
        ): EncryptionResult {
            return EncryptionResult.Failure(EncryptionError.EncryptionFailed)
        }

        override fun decrypt(
            key: ByteArray,
            encryptedPayload: EncryptedPayload,
        ): DecryptionResult {
            decryptCalled = true
            receivedKey = key.copyOf()
            receivedEncryptedPayload = EncryptedPayload(
                nonce = encryptedPayload.nonceCopy(),
                ciphertext = encryptedPayload.ciphertextCopy(),
                authenticationTag = encryptedPayload.authenticationTagCopy(),
            )
            return DecryptionResult.Success(PlaintextPayload(validPlaintext()))
        }
    }

    private class FailingCipher(
        private val error: EncryptionError,
    ) : AuthenticatedCipher {
        override fun encrypt(
            key: ByteArray,
            plaintext: PlaintextPayload,
        ): EncryptionResult {
            return EncryptionResult.Failure(EncryptionError.EncryptionFailed)
        }

        override fun decrypt(
            key: ByteArray,
            encryptedPayload: EncryptedPayload,
        ): DecryptionResult {
            return DecryptionResult.Failure(error)
        }
    }

    private fun validRequest(): VaultPayloadDecryptionRequest {
        return VaultPayloadDecryptionRequest(
            key = validKey(),
            encryptedPayload = sampleEncryptedPayload(),
            decryptedAtEpochMillis = 123456789L,
        )
    }

    private fun expectIllegalArgument(block: () -> Unit) {
        try {
            block()
            fail("Expected IllegalArgumentException.")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }

    companion object {
        private fun validKey(): ByteArray = ByteArray(32) { index -> (index + 1).toByte() }

        private fun validPlaintext(): ByteArray = "vault decrypted payload".encodeToByteArray()

        private fun sampleEncryptedPayload(): EncryptedPayload {
            return EncryptedPayload(
                nonce = ByteArray(12) { index -> index.toByte() },
                ciphertext = byteArrayOf(10, 11, 12),
                authenticationTag = ByteArray(16) { index -> (index + 20).toByte() },
            )
        }
    }
}
