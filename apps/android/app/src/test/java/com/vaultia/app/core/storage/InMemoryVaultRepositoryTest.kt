package com.vaultia.app.core.storage

import com.vaultia.app.core.model.vault.VaultItem
import com.vaultia.app.core.model.vault.VaultItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryVaultRepositoryTest {
    @Test
    fun startsEmpty() {
        val repository = InMemoryVaultRepository()

        assertTrue(repository.listMetadata().isEmpty())
    }

    @Test
    fun addsMetadataForCurrentProcessOnly() {
        val repository = InMemoryVaultRepository()
        val item = vaultItem(id = "item-1", type = VaultItemType.NOTE)

        repository.addMetadataForCurrentProcessOnly(item)

        assertEquals(listOf(item), repository.listMetadata())
    }

    @Test
    fun addsAllMetadataForCurrentProcessOnly() {
        val repository = InMemoryVaultRepository()
        val firstItem = vaultItem(id = "item-1", type = VaultItemType.NOTE)
        val secondItem = vaultItem(id = "item-2", type = VaultItemType.DOCUMENT)

        repository.addAllMetadataForCurrentProcessOnly(listOf(firstItem, secondItem))

        assertEquals(listOf(firstItem, secondItem), repository.listMetadata())
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsDuplicateIds() {
        val repository = InMemoryVaultRepository()
        val firstItem = vaultItem(id = "item-1", type = VaultItemType.NOTE)
        val secondItem = vaultItem(id = "item-1", type = VaultItemType.DOCUMENT)

        repository.addMetadataForCurrentProcessOnly(firstItem)
        repository.addMetadataForCurrentProcessOnly(secondItem)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsDuplicateIdsWhenAddingAll() {
        val repository = InMemoryVaultRepository()
        val firstItem = vaultItem(id = "item-1", type = VaultItemType.NOTE)
        val secondItem = vaultItem(id = "item-1", type = VaultItemType.DOCUMENT)

        repository.addAllMetadataForCurrentProcessOnly(listOf(firstItem, secondItem))
    }

    @Test
    fun exposesDefensiveCopy() {
        val repository = InMemoryVaultRepository()
        val item = vaultItem(id = "item-1", type = VaultItemType.PHOTO)

        repository.addMetadataForCurrentProcessOnly(item)

        val listedItems = repository.listMetadata().toMutableList()
        listedItems.clear()

        assertEquals(1, repository.listMetadata().size)
    }

    @Test
    fun clearsMetadataForCurrentProcessOnly() {
        val repository = InMemoryVaultRepository()

        repository.addMetadataForCurrentProcessOnly(vaultItem(id = "item-1", type = VaultItemType.PASSWORD))
        repository.clearForCurrentProcessOnly()

        assertTrue(repository.listMetadata().isEmpty())
    }

    private fun vaultItem(
        id: String,
        type: VaultItemType,
    ): VaultItem {
        return VaultItem(
            id = id,
            type = type,
            title = "Item $id",
            createdAtEpochMillis = 1000L,
            updatedAtEpochMillis = 1000L,
        )
    }
}
