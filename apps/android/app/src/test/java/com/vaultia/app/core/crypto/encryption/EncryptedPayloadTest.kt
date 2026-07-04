package com.vaultia.app.core.crypto.encryption

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class EncryptedPayloadTest {
    @Test
    fun rejectsInvalidNonceLength() {
        expectIllegalArgument {
            EncryptedPayload(
                nonce = ByteArray(11),
                ciphertext = byteArrayOf(1),
                authenticationTag = ByteArray(16),
            )
        }
    }

    @Test
    fun rejectsEmptyCiphertext() {
        expectIllegalArgument {
            EncryptedPayload(
                nonce = ByteArray(12),
                ciphertext = ByteArray(0),
                authenticationTag = ByteArray(16),
            )
        }
    }

    @Test
    fun rejectsInvalidAuthenticationTagLength() {
        expectIllegalArgument {
            EncryptedPayload(
                nonce = ByteArray(12),
                ciphertext = byteArrayOf(1),
                authenticationTag = ByteArray(15),
            )
        }
    }

    @Test
    fun protectsArraysWithDefensiveCopies() {
        val nonce = ByteArray(12) { index -> index.toByte() }
        val ciphertext = byteArrayOf(1, 2, 3, 4)
        val tag = ByteArray(16) { index -> (index + 10).toByte() }

        val payload = EncryptedPayload(
            nonce = nonce,
            ciphertext = ciphertext,
            authenticationTag = tag,
        )

        nonce[0] = 99
        ciphertext[0] = 99
        tag[0] = 99

        val nonceCopy = payload.nonceCopy()
        val ciphertextCopy = payload.ciphertextCopy()
        val tagCopy = payload.authenticationTagCopy()

        nonceCopy[1] = 88
        ciphertextCopy[1] = 88
        tagCopy[1] = 88

        assertArrayEquals(ByteArray(12) { index -> index.toByte() }, payload.nonceCopy())
        assertArrayEquals(byteArrayOf(1, 2, 3, 4), payload.ciphertextCopy())
        assertArrayEquals(ByteArray(16) { index -> (index + 10).toByte() }, payload.authenticationTagCopy())
        assertEquals(12, payload.nonceLengthBytes)
        assertEquals(4, payload.ciphertextLengthBytes)
        assertEquals(16, payload.authenticationTagLengthBytes)
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
