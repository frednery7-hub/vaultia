package com.vaultia.app.core.storage.composition

import com.vaultia.app.core.storage.service.StorageService

data class StorageContainer(
    val storageService: StorageService,
)
