package com.vaultia.app.core.crypto.vault.payload

import com.vaultia.app.core.crypto.encryption.AuthenticatedCipher
import com.vaultia.app.core.crypto.encryption.EncryptionResult
import com.vaultia.app.core.crypto.encryption.PlaintextPayload

class VaultPayloadEncryptionService(
    private val authenticatedCipher: AuthenticatedCipher,
) {
    fun encrypt(request: VaultPayloadEncryptionRequest): VaultPayloadEncryptionResult {
        val plaintextPayload = try {
            PlaintextPayload(request.plaintextCopy())
        } catch (_: IllegalArgumentException) {
            return VaultPayloadEncryptionResult.Failure(
                VaultPayloadEncryptionError.InvalidRequest,
            )
        }

        return when (
            val encryptionResult = authenticatedCipher.encrypt(
                key = request.keyCopy(),
                plaintext = plaintextPayload,
            )
        ) {
            is EncryptionResult.Success -> VaultPayloadEncryptionResult.Success(
                EncryptedVaultPayloadDraft(
                    encryptedPayload = encryptionResult.encryptedPayload,
                    createdAtEpochMillis = request.createdAtEpochMillis,
                ),
            )

            is EncryptionResult.Failure -> VaultPayloadEncryptionResult.Failure(
                VaultPayloadEncryptionError.EncryptionFailed,
            )
        }
    }
}
