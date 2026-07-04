package com.vaultia.app.core.crypto.vault

import com.vaultia.app.core.crypto.kdf.KdfResult

class CreatedVaultDraft(
    val header: VaultHeader,
    val serializedHeader: String,
    val kdfResult: KdfResult,
)
