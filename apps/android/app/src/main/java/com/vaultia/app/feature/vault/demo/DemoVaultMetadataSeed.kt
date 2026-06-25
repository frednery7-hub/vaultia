package com.vaultia.app.feature.vault.demo

import com.vaultia.app.core.model.vault.VaultItem
import com.vaultia.app.core.model.vault.VaultItemType

object DemoVaultMetadataSeed {
    fun metadataOnlyItems(): List<VaultItem> {
        val now = 1000L

        return listOf(
            VaultItem(
                id = "demo-password-metadata-1",
                type = VaultItemType.PASSWORD,
                title = "Demo senha",
                createdAtEpochMillis = now,
                updatedAtEpochMillis = now,
            ),
            VaultItem(
                id = "demo-note-metadata-1",
                type = VaultItemType.NOTE,
                title = "Demo nota",
                createdAtEpochMillis = now,
                updatedAtEpochMillis = now,
            ),
            VaultItem(
                id = "demo-document-metadata-1",
                type = VaultItemType.DOCUMENT,
                title = "Demo documento",
                createdAtEpochMillis = now,
                updatedAtEpochMillis = now,
            ),
        )
    }
}
