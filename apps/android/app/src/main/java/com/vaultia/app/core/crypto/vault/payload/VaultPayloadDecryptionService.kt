package com.vaultia.app.core.crypto.vault.payload

import com.vaultia.app.core.crypto.encryption.AuthenticatedCipher
import com.vaultia.app.core.crypto.encryption.DecryptionResult
import com.vaultia.app.core.crypto.encryption.EncryptionError

class VaultPayloadDecryptionService(
    private val authenticatedCipher: AuthenticatedCipher,
) {
    fun decrypt(request: VaultPayloadDecryptionRequest): VaultPayloadDecryptionResult {
        return when (
            val decryptionResult = authenticatedCipher.decrypt(
                key = request.keyCopy(),
                encryptedPayload = request.encryptedPayload,
            )
        ) {
            is DecryptionResult.Success -> VaultPayloadDecryptionResult.Success(
                DecryptedVaultPayloadDraft(
                    plaintextPayload = decryptionResult.plaintextPayload,
                    decryptedAtEpochMillis = request.decryptedAtEpochMillis,
                ),
            )

            is DecryptionResult.Failure -> VaultPayloadDecryptionResult.Failure(
                when (decryptionResult.error) {
                    EncryptionError.AuthenticationFailed -> VaultPayloadDecryptionError.AuthenticationFailed
                    else -> VaultPayloadDecryptionError.DecryptionFailed
                },
            )
        }
    }
}
