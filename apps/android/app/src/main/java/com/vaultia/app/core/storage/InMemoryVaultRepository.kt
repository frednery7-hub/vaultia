package com.vaultia.app.core.storage

import com.vaultia.app.core.model.vault.VaultItem

class InMemoryVaultRepository {
    private val items = mutableListOf<VaultItem>()

    fun listMetadata(): List<VaultItem> = items.toList()

    fun addMetadataForCurrentProcessOnly(item: VaultItem) {
        require(items.none { existingItem -> existingItem.id == item.id }) {
            "Vault item id must be unique."
        }

        items.add(item)
    }

    fun clearForCurrentProcessOnly() {
        items.clear()
    }
}
