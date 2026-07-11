package com.vaultia.app.core.model.payload

sealed interface VaultItemPayload {
    val title: String
    val version: Int
}
