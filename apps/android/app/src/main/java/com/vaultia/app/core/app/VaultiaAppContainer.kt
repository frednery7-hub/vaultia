package com.vaultia.app.core.app

import com.vaultia.app.core.crypto.kdf.Argon2idKdfDeriver
import com.vaultia.app.core.crypto.salt.SecureRandomSaltGenerator
import com.vaultia.app.core.crypto.vault.VaultCreationService
import com.vaultia.app.core.crypto.vault.VaultUnlockService
import com.vaultia.app.core.navigation.InMemoryVaultNavigator
import com.vaultia.app.core.session.InMemorySessionManager
import com.vaultia.app.core.storage.composition.StorageCompositionRoot
import com.vaultia.app.core.storage.composition.StorageContainer
import com.vaultia.app.core.storage.service.StorageService
import com.vaultia.app.core.vault.DefaultVaultRepository
import com.vaultia.app.core.vault.VaultRepository

class VaultiaAppContainer(
    val storageContainer: StorageContainer = StorageCompositionRoot.createInMemoryContainer(),
) {
    val sessionManager: InMemorySessionManager = InMemorySessionManager()
    val vaultNavigator: InMemoryVaultNavigator = InMemoryVaultNavigator()
    
    val storageService: StorageService = storageContainer.storageService
    val payloadRepository = storageContainer.payloadRepository
    val headerRepository = storageContainer.headerRepository

    val kdfDeriver = Argon2idKdfDeriver()
    val saltGenerator = SecureRandomSaltGenerator()

    val vaultCreationService = VaultCreationService(
        kdfDeriver = kdfDeriver,
        saltGenerator = saltGenerator
    )

    val vaultUnlockService = VaultUnlockService(
        kdfDeriver = kdfDeriver
    )

    val vaultRepository: VaultRepository = DefaultVaultRepository(storageContainer)
}
