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

class VaultPayloadEncryptionServiceTest {
    @Test
    fun requestRejectsEmptyKey() {
        expectIllegalArgument {
            VaultPayloadEncryptionRequest(
                key = ByteArray(0),
                plaintext = byteArrayOf(1),
                createdAtEpochMillis = 1L,
            )
        }
    }

    @Test
    fun requestRejectsEmptyPlaintext() {
        expectIllegalArgument {
            VaultPayloadEncryptionRequest(
                key = validKey(),
                plaintext = ByteArray(0),
                createdAtEpochMillis = 1L,
            )
        }
    }

    @Test
    fun requestRejectsInvalidTimestamp() {
        expectIllegalArgument {
            VaultPayloadEncryptionRequest(
                key = validKey(),
                plaintext = validPlaintext(),
                createdAtEpochMillis = 0L,
            )
        }
    }

    @Test
    fun requestProtectsKeyAndPlaintextWithDefensiveCopies() {
        val key = validKey()
        val plaintext = validPlaintext()
        val request = VaultPayloadEncryptionRequest(
            key = key,
            plaintext = plaintext,
            createdAtEpochMillis = 123L,
        )

        key[0] = 99
        plaintext[0] = 88

        val keyCopy = request.keyCopy()
        val plaintextCopy = request.plaintextCopy()

        keyCopy[1] = 77
        plaintextCopy[1] = 66

        assertArrayEquals(validKey(), request.keyCopy())
        assertArrayEquals(validPlaintext(), request.plaintextCopy())
        assertEquals(32, request.keyLengthBytes)
        assertEquals(validPlaintext().size, request.plaintextLengthBytes)
    }

    @Test
    fun serviceCallsAuthenticatedCipherEncrypt() {
        val cipher = RecordingCipher()
        val request = validRequest()

        VaultPayloadEncryptionService(cipher).encrypt(request)

        assertTrue(cipher.encryptCalled)
        assertArrayEquals(validKey(), cipher.receivedKey)
        assertArrayEquals(validPlaintext(), cipher.receivedPlaintext)
    }

    @Test
    fun serviceReturnsEncryptedVaultPayloadDraftOnSuccess() {
        val result = VaultPayloadEncryptionService(RecordingCipher()).encrypt(validRequest())

        assertTrue(result is VaultPayloadEncryptionResult.Success)
        val draft = (result as VaultPayloadEncryptionResult.Success).draft

        assertEquals(123456789L, draft.createdAtEpochMillis)
        assertArrayEquals(ByteArray(12) { index -> index.toByte() }, draft.encryptedPayload.nonceCopy())
        assertArrayEquals(byteArrayOf(10, 11, 12), draft.encryptedPayload.ciphertextCopy())
        assertArrayEquals(ByteArray(16) { index -> (index + 20).toByte() }, draft.encryptedPayload.authenticationTagCopy())
    }

    @Test
    fun draftRejectsInvalidTimestamp() {
        expectIllegalArgument {
            EncryptedVaultPayloadDraft(
                encryptedPayload = sampleEncryptedPayload(),
                createdAtEpochMillis = 0L,
            )
        }
    }

    @Test
    fun draftDoesNotExposeKeyOrPlaintextFields() {
        val fieldNames = EncryptedVaultPayloadDraft::class.java.declaredFields
            .map { field -> field.name.lowercase() }

        assertFalse(fieldNames.any { fieldName -> fieldName.contains("key") })
        assertFalse(fieldNames.any { fieldName -> fieldName.contains("plain") })
    }

    @Test
    fun cipherFailureMapsToEncryptionFailed() {
        val result = VaultPayloadEncryptionService(FailingCipher()).encrypt(validRequest())

        assertTrue(result is VaultPayloadEncryptionResult.Failure)
        assertEquals(
            VaultPayloadEncryptionError.EncryptionFailed,
            (result as VaultPayloadEncryptionResult.Failure).error,
        )
    }

    private class RecordingCipher : AuthenticatedCipher {
        var encryptCalled: Boolean = false
        var receivedKey: ByteArray = ByteArray(0)
        var receivedPlaintext: ByteArray = ByteArray(0)

        override fun encrypt(
            key: ByteArray,
            plaintext: PlaintextPayload,
        ): EncryptionResult {
            encryptCalled = true
            receivedKey = key.copyOf()
            receivedPlaintext = plaintext.plaintextCopy()
            return EncryptionResult.Success(sampleEncryptedPayload())
        }

        override fun decrypt(
            key: ByteArray,
            encryptedPayload: EncryptedPayload,
        ): DecryptionResult {
            return DecryptionResult.Failure(EncryptionError.DecryptionFailed)
        }
    }

    private class FailingCipher : AuthenticatedCipher {
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
            return DecryptionResult.Failure(EncryptionError.DecryptionFailed)
        }
    }

    private fun validRequest(): VaultPayloadEncryptionRequest {
        return VaultPayloadEncryptionRequest(
            key = validKey(),
            plaintext = validPlaintext(),
            createdAtEpochMillis = 123456789L,
        )
    }

    private fun validKey(): ByteArray = ByteArray(32) { index -> (index + 1).toByte() }

    private fun validPlaintext(): ByteArray = "vault secret payload".encodeToByteArray()

    private fun expectIllegalArgument(block: () -> Unit) {
        try {
            block()
            fail("Expected IllegalArgumentException.")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }

    companion object {
        private fun sampleEncryptedPayload(): EncryptedPayload {
            return EncryptedPayload(
                nonce = ByteArray(12) { index -> index.toByte() },
                ciphertext = byteArrayOf(10, 11, 12),
                authenticationTag = ByteArray(16) { index -> (index + 20).toByte() },
            )
        }
    }
}
