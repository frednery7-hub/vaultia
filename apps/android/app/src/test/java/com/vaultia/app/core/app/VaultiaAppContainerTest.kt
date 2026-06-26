package com.vaultia.app.core.app

import com.vaultia.app.core.model.vault.VaultItemType
import com.vaultia.app.core.model.vault.VaultSessionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultiaAppContainerTest {
    @Test
    fun createsLockedSessionManager() {
        val container = VaultiaAppContainer()

        assertEquals(VaultSessionState.LOCKED, container.sessionManager.state())
    }

    @Test
    fun createsRepositoryWithDemoMetadataOnlyItems() {
        val container = VaultiaAppContainer()
        val items = container.vaultRepository.listMetadata()

        assertEquals(3, items.size)
        assertTrue(items.any { item -> item.type == VaultItemType.PASSWORD })
        assertTrue(items.any { item -> item.type == VaultItemType.NOTE })
        assertTrue(items.any { item -> item.type == VaultItemType.DOCUMENT })
        assertTrue(items.none { item -> item.type == VaultItemType.PHOTO })
    }
}