package com.vaultia.app.core.crypto.vault

sealed class VaultHeaderSerializationResult {
    class Success(val header: VaultHeader) : VaultHeaderSerializationResult()
    class Failure(val error: VaultHeaderSerializationError) : VaultHeaderSerializationResult()
}
