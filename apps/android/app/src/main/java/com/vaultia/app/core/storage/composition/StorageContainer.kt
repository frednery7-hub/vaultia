package com.vaultia.app.core.storage.composition

import com.vaultia.app.core.storage.header.VaultHeaderRepository
import com.vaultia.app.core.storage.payload.PayloadRepository
import com.vaultia.app.core.storage.service.StorageService

data class StorageContainer(
    val storageService: StorageService,
    val payloadRepository: PayloadRepository,
    val headerRepository: VaultHeaderRepository,
)
