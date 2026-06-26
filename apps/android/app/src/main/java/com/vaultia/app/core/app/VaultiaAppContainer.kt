package com.vaultia.app.core.app

import com.vaultia.app.core.session.InMemorySessionManager
import com.vaultia.app.core.storage.InMemoryVaultRepository
import com.vaultia.app.feature.vault.demo.DemoVaultMetadataSeed

class VaultiaAppContainer {
    val sessionManager: InMemorySessionManager = InMemorySessionManager()
    val vaultRepository: InMemoryVaultRepository = InMemoryVaultRepository()

    init {
        vaultRepository.addAllMetadataForCurrentProcessOnly(
            DemoVaultMetadataSeed.metadataOnlyItems(),
        )
    }
}