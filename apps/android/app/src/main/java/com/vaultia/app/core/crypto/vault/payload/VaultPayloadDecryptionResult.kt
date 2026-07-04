package com.vaultia.app.core.crypto.vault.payload

sealed class VaultPayloadDecryptionResult {
    class Success(val draft: DecryptedVaultPayloadDraft) : VaultPayloadDecryptionResult()
    class Failure(val error: VaultPayloadDecryptionError) : VaultPayloadDecryptionResult()
}
