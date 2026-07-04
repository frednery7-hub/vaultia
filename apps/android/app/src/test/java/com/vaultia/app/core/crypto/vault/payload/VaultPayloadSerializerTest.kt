package com.vaultia.app.core.crypto.vault.payload

import com.vaultia.app.core.crypto.encryption.EncryptedPayload
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class VaultPayloadSerializerTest {
    @Test
    fun formatVersionMapsEncodedValue() {
        assertEquals(1, VaultPayloadSerializedFormatVersion.V1_0.encodedValue)
        assertEquals(
            VaultPayloadSerializedFormatVersion.V1_0,
            VaultPayloadSerializedFormatVersion.fromEncodedValue(1),
        )
        assertEquals(null, VaultPayloadSerializedFormatVersion.fromEncodedValue(99))
    }

    @Test
    fun serializedPayloadRejectsInvalidFields() {
        expectIllegalArgument {
            SerializedEncryptedVaultPayload(
                formatVersion = VaultPayloadSerializedFormatVersion.V1_0,
                nonceHex = "00",
                ciphertextHex = "0a0b0c",
                authenticationTagHex = validTagHex(),
                createdAtEpochMillis = 1L,
            )
        }

        expectIllegalArgument {
            SerializedEncryptedVaultPayload(
                formatVersion = VaultPayloadSerializedFormatVersion.V1_0,
                nonceHex = validNonceHex(),
                ciphertextHex = "",
                authenticationTagHex = validTagHex(),
                createdAtEpochMillis = 1L,
            )
        }

        expectIllegalArgument {
            SerializedEncryptedVaultPayload(
                formatVersion = VaultPayloadSerializedFormatVersion.V1_0,
                nonceHex = validNonceHex(),
                ciphertextHex = "0a0b0c",
                authenticationTagHex = "00",
                createdAtEpochMillis = 1L,
            )
        }

        expectIllegalArgument {
            SerializedEncryptedVaultPayload(
                formatVersion = VaultPayloadSerializedFormatVersion.V1_0,
                nonceHex = validNonceHex(),
                ciphertextHex = "0a0b0c",
                authenticationTagHex = validTagHex(),
                createdAtEpochMillis = 0L,
            )
        }
    }

    @Test
    fun encodeCreatesExpectedTextFormatWithoutKeyOrPlaintext() {
        val encoded = VaultPayloadSerializer.encode(sampleDraft())

        assertTrue(encoded.startsWith("VAULTIA_PAYLOAD_V1\n"))
        assertTrue(encoded.endsWith("\nEND_VAULTIA_PAYLOAD"))
        assertTrue(encoded.contains("formatVersion=1"))
        assertTrue(encoded.contains("nonceHex=${validNonceHex()}"))
        assertTrue(encoded.contains("ciphertextHex=0a0b0c"))
        assertTrue(encoded.contains("authenticationTagHex=${validTagHex()}"))
        assertTrue(encoded.contains("createdAtEpochMillis=123456789"))
        assertFalse(encoded.lowercase().contains("key"))
        assertFalse(encoded.lowercase().contains("plaintext"))
        assertFalse(encoded.lowercase().contains("password"))
        assertFalse(encoded.lowercase().contains("salt"))
    }

    @Test
    fun decodeRejectsEmptyInput() {
        assertFailure("", VaultPayloadSerializationError.EmptyInput)
    }

    @Test
    fun decodeRejectsInvalidMagic() {
        assertFailure(
            validSerializedText().replace("VAULTIA_PAYLOAD_V1", "WRONG"),
            VaultPayloadSerializationError.InvalidMagic,
        )
    }

    @Test
    fun decodeRejectsMissingField() {
        val input = validSerializedText()
            .lines()
            .filterNot { line -> line.startsWith("nonceHex=") }
            .joinToString("\n")

        assertFailure(input, VaultPayloadSerializationError.MissingField)
    }

    @Test
    fun decodeRejectsDuplicateField() {
        val input = validSerializedText().replace(
            "ciphertextHex=0a0b0c",
            "ciphertextHex=0a0b0c\nciphertextHex=0d0e0f",
        )

        assertFailure(input, VaultPayloadSerializationError.DuplicateField)
    }

    @Test
    fun decodeRejectsUnsupportedFormatVersion() {
        assertFailure(
            validSerializedText().replace("formatVersion=1", "formatVersion=99"),
            VaultPayloadSerializationError.UnsupportedFormatVersion,
        )
    }

    @Test
    fun decodeRejectsInvalidHexFields() {
        assertFailure(
            validSerializedText().replace("nonceHex=${validNonceHex()}", "nonceHex=nothex"),
            VaultPayloadSerializationError.InvalidNonceEncoding,
        )
        assertFailure(
            validSerializedText().replace("ciphertextHex=0a0b0c", "ciphertextHex=0x"),
            VaultPayloadSerializationError.InvalidCiphertextEncoding,
        )
        assertFailure(
            validSerializedText().replace("authenticationTagHex=${validTagHex()}", "authenticationTagHex=zz"),
            VaultPayloadSerializationError.InvalidAuthenticationTagEncoding,
        )
    }

    @Test
    fun decodeRejectsInvalidTimestamp() {
        assertFailure(
            validSerializedText().replace("createdAtEpochMillis=123456789", "createdAtEpochMillis=0"),
            VaultPayloadSerializationError.InvalidTimestamp,
        )
    }

    @Test
    fun decodeRejectsInvalidEncryptedPayloadLengths() {
        assertFailure(
            validSerializedText().replace("nonceHex=${validNonceHex()}", "nonceHex=0000"),
            VaultPayloadSerializationError.InvalidFieldValue,
        )
        assertFailure(
            validSerializedText().replace("authenticationTagHex=${validTagHex()}", "authenticationTagHex=0000"),
            VaultPayloadSerializationError.InvalidFieldValue,
        )
    }

    @Test
    fun roundTripPreservesEncryptedPayloadAndTimestamp() {
        val result = VaultPayloadSerializer.decode(VaultPayloadSerializer.encode(sampleDraft()))

        assertTrue(result is VaultPayloadSerializationResult.Success)
        val decodedDraft = (result as VaultPayloadSerializationResult.Success).draft

        assertEquals(123456789L, decodedDraft.createdAtEpochMillis)
        assertArrayEquals(sampleDraft().encryptedPayload.nonceCopy(), decodedDraft.encryptedPayload.nonceCopy())
        assertArrayEquals(sampleDraft().encryptedPayload.ciphertextCopy(), decodedDraft.encryptedPayload.ciphertextCopy())
        assertArrayEquals(
            sampleDraft().encryptedPayload.authenticationTagCopy(),
            decodedDraft.encryptedPayload.authenticationTagCopy(),
        )
    }

    private fun assertFailure(input: String, expectedError: VaultPayloadSerializationError) {
        val result = VaultPayloadSerializer.decode(input)
        assertTrue(result is VaultPayloadSerializationResult.Failure)
        assertEquals(expectedError, (result as VaultPayloadSerializationResult.Failure).error)
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
        private fun sampleDraft(): EncryptedVaultPayloadDraft {
            return EncryptedVaultPayloadDraft(
                encryptedPayload = EncryptedPayload(
                    nonce = ByteArray(12) { index -> index.toByte() },
                    ciphertext = byteArrayOf(10, 11, 12),
                    authenticationTag = ByteArray(16) { index -> (index + 20).toByte() },
                ),
                createdAtEpochMillis = 123456789L,
            )
        }

        private fun validSerializedText(): String = VaultPayloadSerializer.encode(sampleDraft())

        private fun validNonceHex(): String = "000102030405060708090a0b"

        private fun validTagHex(): String = "1415161718191a1b1c1d1e1f20212223"
    }
}
