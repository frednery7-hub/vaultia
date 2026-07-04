package com.vaultia.app.core.crypto.vault

sealed class VaultCreationError {
    data object InvalidMasterPassword : VaultCreationError()
    data object InvalidCreationRequest : VaultCreationError()
    data object InvalidVaultHeader : VaultCreationError()
    data object SaltGenerationFailed : VaultCreationError()
    data object KdfDerivationFailed : VaultCreationError()
    data object HeaderSerializationFailed : VaultCreationError()
}
