package com.vaultia.app.core.crypto.vault.payload

import com.vaultia.app.core.crypto.encryption.EncryptedPayload

class EncryptedVaultPayloadDraft(
    val encryptedPayload: EncryptedPayload,
    val createdAtEpochMillis: Long,
) {
    init {
        require(createdAtEpochMillis > 0L) {
            "Encrypted vault payload draft timestamp must be positive."
        }
    }
}
