package com.vaultia.app.core.crypto.vault.item

import com.vaultia.app.core.crypto.vault.payload.SerializedEncryptedVaultPayload
import com.vaultia.app.core.crypto.vault.payload.VaultPayloadSerializedFormatVersion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class EncryptedVaultItemDraftFactoryTest {
    @Test
    fun itemTypeContainsExpectedTypes() {
        assertTrue(EncryptedVaultItemType.entries.contains(EncryptedVaultItemType.PASSWORD))
        assertTrue(EncryptedVaultItemType.entries.contains(EncryptedVaultItemType.NOTE))
        assertTrue(EncryptedVaultItemType.entries.contains(EncryptedVaultItemType.PHOTO))
        assertTrue(EncryptedVaultItemType.entries.contains(EncryptedVaultItemType.DOCUMENT))
        assertEquals(4, EncryptedVaultItemType.entries.size)
    }

    @Test
    fun itemIdRejectsEmptyOrBlankValues() {
        expectIllegalArgument { EncryptedVaultItemId("") }
        expectIllegalArgument { EncryptedVaultItemId("   ") }
    }

    @Test
    fun itemIdAppliesTrim() {
        val id = EncryptedVaultItemId("  item-123  ")

        assertEquals("item-123", id.value)
    }

    @Test
    fun itemIdRejectsInternalWhitespaceAndPathLikeValues() {
        expectIllegalArgument { EncryptedVaultItemId("item 123") }
        expectIllegalArgument { EncryptedVaultItemId("item/123") }
        expectIllegalArgument { EncryptedVaultItemId("item\\123") }
        expectIllegalArgument { EncryptedVaultItemId("drive:item") }
        expectIllegalArgument { EncryptedVaultItemId("item.txt") }
    }

    @Test
    fun factoryReturnsInvalidIdForInvalidId() {
        val result = EncryptedVaultItemDraftFactory.create(
            id = "item 123",
            type = EncryptedVaultItemType.PASSWORD,
            serializedPayload = validPayload(),
            createdAtEpochMillis = 100L,
            updatedAtEpochMillis = 100L,
        )

        assertTrue(result is EncryptedVaultItemDraftResult.Failure)
        assertEquals(
            EncryptedVaultItemDraftError.InvalidId,
            (result as EncryptedVaultItemDraftResult.Failure).error,
        )
    }

    @Test
    fun factoryReturnsInvalidTimestampForInvalidTimestamp() {
        val zeroCreated = EncryptedVaultItemDraftFactory.create(
            id = "item-123",
            type = EncryptedVaultItemType.NOTE,
            serializedPayload = validPayload(),
            createdAtEpochMillis = 0L,
            updatedAtEpochMillis = 100L,
        )
        val earlierUpdated = EncryptedVaultItemDraftFactory.create(
            id = "item-123",
            type = EncryptedVaultItemType.NOTE,
            serializedPayload = validPayload(),
            createdAtEpochMillis = 100L,
            updatedAtEpochMillis = 99L,
        )

        assertEquals(
            EncryptedVaultItemDraftError.InvalidTimestamp,
            (zeroCreated as EncryptedVaultItemDraftResult.Failure).error,
        )
        assertEquals(
            EncryptedVaultItemDraftError.InvalidTimestamp,
            (earlierUpdated as EncryptedVaultItemDraftResult.Failure).error,
        )
    }

    @Test
    fun factoryReturnsSuccessForValidItem() {
        val result = EncryptedVaultItemDraftFactory.create(
            id = "item-123",
            type = EncryptedVaultItemType.DOCUMENT,
            serializedPayload = validPayload(),
            createdAtEpochMillis = 100L,
            updatedAtEpochMillis = 200L,
        )

        assertTrue(result is EncryptedVaultItemDraftResult.Success)
        val draft = (result as EncryptedVaultItemDraftResult.Success).draft

        assertEquals("item-123", draft.id.value)
        assertEquals(EncryptedVaultItemType.DOCUMENT, draft.type)
        assertEquals(validPayload().nonceHex, draft.serializedPayload.nonceHex)
        assertEquals(validPayload().ciphertextHex, draft.serializedPayload.ciphertextHex)
        assertEquals(validPayload().authenticationTagHex, draft.serializedPayload.authenticationTagHex)
        assertEquals(100L, draft.createdAtEpochMillis)
        assertEquals(200L, draft.updatedAtEpochMillis)
    }

    @Test
    fun draftRejectsInvalidTimestamps() {
        expectIllegalArgument {
            EncryptedVaultItemDraft(
                id = EncryptedVaultItemId("item-123"),
                type = EncryptedVaultItemType.PHOTO,
                serializedPayload = validPayload(),
                createdAtEpochMillis = 100L,
                updatedAtEpochMillis = 99L,
            )
        }
    }

    @Test
    fun draftDoesNotExposeSensitiveMetadataFields() {
        val fieldNames = EncryptedVaultItemDraft::class.java.declaredFields
            .map { field -> field.name.lowercase() }

        assertFalse(fieldNames.any { fieldName -> fieldName.contains("key") })
        assertFalse(fieldNames.any { fieldName -> fieldName.contains("plain") })
        assertFalse(fieldNames.any { fieldName -> fieldName.contains("password") })
        assertFalse(fieldNames.any { fieldName -> fieldName.contains("title") })
        assertFalse(fieldNames.any { fieldName -> fieldName.contains("uri") })
        assertFalse(fieldNames.any { fieldName -> fieldName.contains("path") })
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

    private fun expectIllegalArgument(block: () -> Unit) {
        try {
            block()
            fail("Expected IllegalArgumentException.")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }
}
