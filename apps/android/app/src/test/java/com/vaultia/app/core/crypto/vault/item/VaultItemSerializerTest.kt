package com.vaultia.app.core.crypto.vault.item

import com.vaultia.app.core.crypto.vault.payload.SerializedEncryptedVaultPayload
import com.vaultia.app.core.crypto.vault.payload.VaultPayloadSerializedFormatVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class VaultItemSerializerTest {
    @Test
    fun formatVersionMapsEncodedValue() {
        assertEquals(1, VaultItemSerializedFormatVersion.V1_0.encodedValue)
        assertEquals(VaultItemSerializedFormatVersion.V1_0, VaultItemSerializedFormatVersion.fromEncodedValue(1))
        assertEquals(null, VaultItemSerializedFormatVersion.fromEncodedValue(99))
    }

    @Test
    fun serializedItemRejectsInvalidIdAndTimestamp() {
        expectIllegalArgument {
            SerializedEncryptedVaultItem(
                formatVersion = VaultItemSerializedFormatVersion.V1_0,
                id = "bad id",
                type = EncryptedVaultItemType.NOTE,
                createdAtEpochMillis = 100L,
                updatedAtEpochMillis = 100L,
                serializedPayload = validPayload(),
            )
        }
        expectIllegalArgument {
            SerializedEncryptedVaultItem(
                formatVersion = VaultItemSerializedFormatVersion.V1_0,
                id = "item-123",
                type = EncryptedVaultItemType.NOTE,
                createdAtEpochMillis = 100L,
                updatedAtEpochMillis = 99L,
                serializedPayload = validPayload(),
            )
        }
    }

    @Test
    fun encodeCreatesExpectedTextFormatWithoutSensitiveMetadata() {
        val encoded = VaultItemSerializer.encode(validDraft())

        assertTrue(encoded.startsWith("VAULTIA_ITEM_V1\n"))
        assertTrue(encoded.endsWith("\nEND_VAULTIA_ITEM"))
        assertTrue(encoded.contains("formatVersion=1"))
        assertTrue(encoded.contains("id=item-123"))
        assertTrue(encoded.contains("type=PASSWORD"))
        assertTrue(encoded.contains("createdAtEpochMillis=100"))
        assertTrue(encoded.contains("updatedAtEpochMillis=200"))
        assertTrue(encoded.contains("payloadFormatVersion=1"))
        assertTrue(encoded.contains("payloadNonceHex=000102030405060708090a0b"))
        assertTrue(encoded.contains("payloadCiphertextHex=0a0b0c"))
        assertTrue(encoded.contains("payloadAuthenticationTagHex=1415161718191a1b1c1d1e1f20212223"))
        assertTrue(encoded.contains("payloadCreatedAtEpochMillis=100"))
        assertFalse(encoded.lowercase().contains("key"))
        assertFalse(encoded.lowercase().contains("plaintext"))
        assertFalse(encoded.lowercase().contains("password="))
        assertFalse(encoded.lowercase().contains("title"))
        assertFalse(encoded.lowercase().contains("uri"))
        assertFalse(encoded.lowercase().contains("path"))
    }

    @Test
    fun decodeRejectsEmptyInput() {
        assertFailure("", VaultItemSerializationError.EmptyInput)
    }

    @Test
    fun decodeRejectsInvalidMagic() {
        assertFailure(validSerializedText().replace("VAULTIA_ITEM_V1", "WRONG"), VaultItemSerializationError.InvalidMagic)
    }

    @Test
    fun decodeRejectsMissingField() {
        val input = validSerializedText().lines().filterNot { line -> line.startsWith("id=") }.joinToString("\n")

        assertFailure(input, VaultItemSerializationError.MissingField)
    }

    @Test
    fun decodeRejectsDuplicateField() {
        val input = validSerializedText().replace("type=PASSWORD", "type=PASSWORD\ntype=NOTE")

        assertFailure(input, VaultItemSerializationError.DuplicateField)
    }

    @Test
    fun decodeRejectsUnsupportedFormatVersion() {
        assertFailure(
            validSerializedText().replace("formatVersion=1", "formatVersion=99"),
            VaultItemSerializationError.UnsupportedFormatVersion,
        )
    }

    @Test
    fun decodeRejectsUnsupportedItemType() {
        assertFailure(
            validSerializedText().replace("type=PASSWORD", "type=SECRET"),
            VaultItemSerializationError.UnsupportedItemType,
        )
    }

    @Test
    fun decodeRejectsInvalidId() {
        assertFailure(
            validSerializedText().replace("id=item-123", "id=bad id"),
            VaultItemSerializationError.InvalidId,
        )
    }

    @Test
    fun decodeRejectsInvalidTimestamp() {
        assertFailure(
            validSerializedText().replace("updatedAtEpochMillis=200", "updatedAtEpochMillis=99"),
            VaultItemSerializationError.InvalidTimestamp,
        )
    }

    @Test
    fun decodeRejectsInvalidPayload() {
        assertFailure(
            validSerializedText().replace("payloadNonceHex=000102030405060708090a0b", "payloadNonceHex=nothex"),
            VaultItemSerializationError.InvalidPayload,
        )
        assertFailure(
            validSerializedText().replace("payloadFormatVersion=1", "payloadFormatVersion=99"),
            VaultItemSerializationError.InvalidPayload,
        )
    }

    @Test
    fun roundTripPreservesItemAndPayload() {
        val result = VaultItemSerializer.decode(VaultItemSerializer.encode(validDraft()))

        assertTrue(result is VaultItemSerializationResult.Success)
        val decodedDraft = (result as VaultItemSerializationResult.Success).draft

        assertEquals("item-123", decodedDraft.id.value)
        assertEquals(EncryptedVaultItemType.PASSWORD, decodedDraft.type)
        assertEquals(100L, decodedDraft.createdAtEpochMillis)
        assertEquals(200L, decodedDraft.updatedAtEpochMillis)
        assertEquals(validPayload().nonceHex, decodedDraft.serializedPayload.nonceHex)
        assertEquals(validPayload().ciphertextHex, decodedDraft.serializedPayload.ciphertextHex)
        assertEquals(validPayload().authenticationTagHex, decodedDraft.serializedPayload.authenticationTagHex)
        assertEquals(validPayload().createdAtEpochMillis, decodedDraft.serializedPayload.createdAtEpochMillis)
    }

    private fun assertFailure(input: String, expectedError: VaultItemSerializationError) {
        val result = VaultItemSerializer.decode(input)
        assertTrue(result is VaultItemSerializationResult.Failure)
        assertEquals(expectedError, (result as VaultItemSerializationResult.Failure).error)
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
        private fun validDraft(): EncryptedVaultItemDraft {
            return EncryptedVaultItemDraft(
                id = EncryptedVaultItemId("item-123"),
                type = EncryptedVaultItemType.PASSWORD,
                serializedPayload = validPayload(),
                createdAtEpochMillis = 100L,
                updatedAtEpochMillis = 200L,
            )
        }

        private fun validPayload(): SerializedEncryptedVaultPayload {
            return SerializedEncryptedVaultPayload(
                formatVersion = VaultPayloadSerializedFormatVersion.V1_0,
                nonceHex = "000102030405060708090a0b",
                ciphertextHex = "0a0b0c",
                authenticationTagHex = "1415161718191a1b1c1d1e1f20212223",
                createdAtEpochMillis = 100L,
            )
        }

        private fun validSerializedText(): String = VaultItemSerializer.encode(validDraft())
    }
}
