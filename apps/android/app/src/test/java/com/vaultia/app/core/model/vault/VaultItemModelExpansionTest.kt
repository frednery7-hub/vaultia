package com.vaultia.app.core.model.vault

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultItemModelExpansionTest {
    @Test
    fun supportsExpectedVaultItemTypes() {
        assertTrue(VaultItemType.entries.contains(VaultItemType.PASSWORD))
        assertTrue(VaultItemType.entries.contains(VaultItemType.NOTE))
        assertTrue(VaultItemType.entries.contains(VaultItemType.PHOTO))
        assertTrue(VaultItemType.entries.contains(VaultItemType.DOCUMENT))
        assertEquals(4, VaultItemType.entries.size)
    }

    @Test
    fun createsMetadataOnlyVaultItem() {
        val item = VaultItem(
            id = "item-1",
            type = VaultItemType.NOTE,
            title = "Documento pessoal",
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 1000L,
        )

        assertEquals("item-1", item.id)
        assertEquals(VaultItemType.NOTE, item.type)
        assertEquals("Documento pessoal", item.title)
        assertEquals(1000L, item.createdAtEpochMillis)
        assertEquals(1000L, item.updatedAtEpochMillis)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsBlankId() {
        VaultItem(
            id = " ",
            type = VaultItemType.DOCUMENT,
            title = "Passaporte",
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 1000L,
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsBlankTitle() {
        VaultItem(
            id = "item-1",
            type = VaultItemType.DOCUMENT,
            title = " ",
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 1000L,
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsInvalidCreationTimestamp() {
        VaultItem(
            id = "item-1",
            type = VaultItemType.PHOTO,
            title = "Foto",
            createdAtEpochMillis = 0L,
            updatedAtEpochMillis = 1000L,
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsUpdateTimestampBeforeCreationTimestamp() {
        VaultItem(
            id = "item-1",
            type = VaultItemType.PASSWORD,
            title = "Conta",
            createdAtEpochMillis = 2000L,
            updatedAtEpochMillis = 1000L,
        )
    }
}
