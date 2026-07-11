package com.vaultia.app.core.app

import com.vaultia.app.core.navigation.InMemoryVaultNavigator
import com.vaultia.app.core.session.InMemorySessionManager
import com.vaultia.app.core.storage.InMemoryVaultRepository
import com.vaultia.app.core.storage.composition.StorageCompositionRoot
import com.vaultia.app.core.storage.composition.StorageContainer
import com.vaultia.app.core.storage.service.StorageService
import com.vaultia.app.feature.vault.demo.DemoVaultMetadataSeed

class VaultiaAppContainer(
    val storageContainer: StorageContainer = StorageCompositionRoot.createInMemoryContainer(),
) {
    val sessionManager: InMemorySessionManager = InMemorySessionManager()
    val vaultRepository: InMemoryVaultRepository = InMemoryVaultRepository()
    val vaultNavigator: InMemoryVaultNavigator = InMemoryVaultNavigator()
    val storageService: StorageService = storageContainer.storageService
    val payloadRepository = storageContainer.payloadRepository
    val headerRepository = storageContainer.headerRepository

    init {
        vaultRepository.addAllMetadataForCurrentProcessOnly(
            DemoVaultMetadataSeed.metadataOnlyItems(),
        )
    }
}
