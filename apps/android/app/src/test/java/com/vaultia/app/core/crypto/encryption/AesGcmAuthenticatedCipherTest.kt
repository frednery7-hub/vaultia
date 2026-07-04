package com.vaultia.app.core.crypto.encryption

import java.security.SecureRandom
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AesGcmAuthenticatedCipherTest {
    @Test
    fun rejectsInvalidKeyOnEncrypt() {
        val result = AesGcmAuthenticatedCipher().encrypt(
            key = ByteArray(15),
            plaintext = PlaintextPayload(byteArrayOf(1, 2, 3)),
        )

        assertEncryptionFailure(result, EncryptionError.InvalidKey)
    }

    @Test
    fun rejectsInvalidKeyOnDecrypt() {
        val encryptedPayload = EncryptedPayload(
            nonce = ByteArray(12),
            ciphertext = byteArrayOf(1, 2, 3),
            authenticationTag = ByteArray(16),
        )

        val result = AesGcmAuthenticatedCipher().decrypt(
            key = ByteArray(15),
            encryptedPayload = encryptedPayload,
        )

        assertDecryptionFailure(result, EncryptionError.InvalidKey)
    }

    @Test
    fun mapsNonceGenerationFailure() {
        val failingSecureRandom = object : SecureRandom() {
            override fun nextBytes(bytes: ByteArray) {
                throw IllegalStateException("Forced failure.")
            }
        }

        val result = AesGcmAuthenticatedCipher(failingSecureRandom).encrypt(
            key = validKey(),
            plaintext = validPlaintext(),
        )

        assertEncryptionFailure(result, EncryptionError.NonceGenerationFailed)
    }

    @Test
    fun encryptProducesNonceCiphertextAndAuthenticationTagWithoutPlaintextLeak() {
        val result = AesGcmAuthenticatedCipher().encrypt(
            key = validKey(),
            plaintext = PlaintextPayload("vault-secret".encodeToByteArray()),
        )

        assertTrue(result is EncryptionResult.Success)
        val encryptedPayload = (result as EncryptionResult.Success).encryptedPayload

        assertEquals(12, encryptedPayload.nonceLengthBytes)
        assertEquals(16, encryptedPayload.authenticationTagLengthBytes)
        assertTrue(encryptedPayload.ciphertextLengthBytes > 0)
        assertFalse(
            encryptedPayload.ciphertextCopy().contentEquals("vault-secret".encodeToByteArray()),
        )
    }

    @Test
    fun encryptAndDecryptRoundTripRecoversOriginalPlaintext() {
        val cipher = AesGcmAuthenticatedCipher()
        val plaintext = "vaultia authenticated encryption payload".encodeToByteArray()

        val encrypted = cipher.encrypt(
            key = validKey(),
            plaintext = PlaintextPayload(plaintext),
        ) as EncryptionResult.Success

        val decrypted = cipher.decrypt(
            key = validKey(),
            encryptedPayload = encrypted.encryptedPayload,
        )

        assertTrue(decrypted is DecryptionResult.Success)
        assertArrayEquals(
            plaintext,
            (decrypted as DecryptionResult.Success).plaintextPayload.plaintextCopy(),
        )
    }

    @Test
    fun decryptFailsWhenCiphertextIsTampered() {
        val encryptedPayload = encryptedPayload()
        val tamperedCiphertext = encryptedPayload.ciphertextCopy()
        tamperedCiphertext[0] = (tamperedCiphertext[0].toInt() xor 1).toByte()

        val result = AesGcmAuthenticatedCipher().decrypt(
            key = validKey(),
            encryptedPayload = EncryptedPayload(
                nonce = encryptedPayload.nonceCopy(),
                ciphertext = tamperedCiphertext,
                authenticationTag = encryptedPayload.authenticationTagCopy(),
            ),
        )

        assertDecryptionFailure(result, EncryptionError.AuthenticationFailed)
    }

    @Test
    fun decryptFailsWhenAuthenticationTagIsTampered() {
        val encryptedPayload = encryptedPayload()
        val tamperedTag = encryptedPayload.authenticationTagCopy()
        tamperedTag[0] = (tamperedTag[0].toInt() xor 1).toByte()

        val result = AesGcmAuthenticatedCipher().decrypt(
            key = validKey(),
            encryptedPayload = EncryptedPayload(
                nonce = encryptedPayload.nonceCopy(),
                ciphertext = encryptedPayload.ciphertextCopy(),
                authenticationTag = tamperedTag,
            ),
        )

        assertDecryptionFailure(result, EncryptionError.AuthenticationFailed)
    }

    @Test
    fun decryptFailsWhenNonceIsTampered() {
        val encryptedPayload = encryptedPayload()
        val tamperedNonce = encryptedPayload.nonceCopy()
        tamperedNonce[0] = (tamperedNonce[0].toInt() xor 1).toByte()

        val result = AesGcmAuthenticatedCipher().decrypt(
            key = validKey(),
            encryptedPayload = EncryptedPayload(
                nonce = tamperedNonce,
                ciphertext = encryptedPayload.ciphertextCopy(),
                authenticationTag = encryptedPayload.authenticationTagCopy(),
            ),
        )

        assertDecryptionFailure(result, EncryptionError.AuthenticationFailed)
    }

    @Test
    fun twoEncryptCallsWithSameKeyAndPlaintextGenerateDifferentNonces() {
        val cipher = AesGcmAuthenticatedCipher()
        val plaintext = validPlaintext()

        val first = cipher.encrypt(validKey(), plaintext) as EncryptionResult.Success
        val second = cipher.encrypt(validKey(), plaintext) as EncryptionResult.Success

        assertFalse(
            first.encryptedPayload.nonceCopy().contentEquals(second.encryptedPayload.nonceCopy()),
        )
    }

    private fun encryptedPayload(): EncryptedPayload {
        val result = AesGcmAuthenticatedCipher().encrypt(
            key = validKey(),
            plaintext = validPlaintext(),
        )

        return (result as EncryptionResult.Success).encryptedPayload
    }

    private fun validKey(): ByteArray = ByteArray(32) { index -> (index + 1).toByte() }

    private fun validPlaintext(): PlaintextPayload =
        PlaintextPayload("vaultia payload".encodeToByteArray())

    private fun assertEncryptionFailure(
        result: EncryptionResult,
        expectedError: EncryptionError,
    ) {
        assertTrue(result is EncryptionResult.Failure)
        assertEquals(expectedError, (result as EncryptionResult.Failure).error)
    }

    private fun assertDecryptionFailure(
        result: DecryptionResult,
        expectedError: EncryptionError,
    ) {
        assertTrue(result is DecryptionResult.Failure)
        assertEquals(expectedError, (result as DecryptionResult.Failure).error)
    }
}
