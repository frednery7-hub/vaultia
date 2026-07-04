package com.vaultia.app.core.crypto.vault.payload

import com.vaultia.app.core.crypto.encryption.PlaintextPayload

class DecryptedVaultPayloadDraft(
    plaintextPayload: PlaintextPayload,
    val decryptedAtEpochMillis: Long,
) {
    val plaintextPayload: PlaintextPayload = PlaintextPayload(
        plaintextPayload.plaintextCopy(),
    )

    init {
        require(decryptedAtEpochMillis > 0L) {
            "Decrypted vault payload draft timestamp must be positive."
        }
    }
}
