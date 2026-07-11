package com.vaultia.app.core.model.payload

sealed interface VaultItemPayloadSerializationResult {
    data class Success(val payload: VaultItemPayload) : VaultItemPayloadSerializationResult
    data class Failure(val error: VaultItemPayloadSerializationError) : VaultItemPayloadSerializationResult
}
