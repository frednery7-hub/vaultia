package com.vaultia.app.core.crypto.vault

sealed class VaultCreationResult {
    class Success(val draft: CreatedVaultDraft) : VaultCreationResult()
    class Failure(val error: VaultCreationError) : VaultCreationResult()
}
