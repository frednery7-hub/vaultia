package com.vaultia.app.core.crypto.vault

import com.vaultia.app.core.crypto.kdf.KdfResult

sealed interface VaultUnlockResult {
    data class Success(val kdfResult: KdfResult) : VaultUnlockResult
    data class Failure(val error: VaultUnlockError) : VaultUnlockResult
}
