package com.vaultia.app.feature.vault.demo

import com.vaultia.app.core.model.vault.VaultItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoVaultMetadataSeedTest {
    @Test
    fun createsExpectedDemoMetadataItems() {
        val items = DemoVaultMetadataSeed.metadataOnlyItems()

        assertEquals(3, items.size)
        assertTrue(items.any { item -> item.type == VaultItemType.PASSWORD })
        assertTrue(items.any { item -> item.type == VaultItemType.NOTE })
        assertTrue(items.any { item -> item.type == VaultItemType.DOCUMENT })
    }

    @Test
    fun demoMetadataUsesUniqueIds() {
        val items = DemoVaultMetadataSeed.metadataOnlyItems()

        assertEquals(items.size, items.map { item -> item.id }.toSet().size)
    }

    @Test
    fun demoMetadataDoesNotUsePhotoYet() {
        val items = DemoVaultMetadataSeed.metadataOnlyItems()

        assertTrue(items.none { item -> item.type == VaultItemType.PHOTO })
    }
}
