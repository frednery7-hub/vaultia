package com.vaultia.app.core.crypto.vault

import com.vaultia.app.core.crypto.kdf.KdfDerivationOutcome
import com.vaultia.app.core.crypto.kdf.KdfDeriver
import com.vaultia.app.core.security.password.MasterPasswordPolicy

class VaultUnlockService(
    private val kdfDeriver: KdfDeriver,
    private val headerDecoder: (String) -> VaultHeaderSerializationResult = VaultHeaderSerializer::decode,
) {
    fun unlock(request: VaultUnlockRequest): VaultUnlockResult {
        val masterPasswordStr = request.masterPassword.joinToString("")
        val validation = MasterPasswordPolicy.validate(masterPasswordStr)
        if (!validation.isValid) {
            return VaultUnlockResult.Failure(VaultUnlockError.InvalidMasterPassword)
        }
        
        val headerResult = headerDecoder(request.serializedHeader)
        val header = when (headerResult) {
            is VaultHeaderSerializationResult.Success -> headerResult.header
            is VaultHeaderSerializationResult.Failure -> return VaultUnlockResult.Failure(VaultUnlockError.InvalidHeader)
        }

        val kdfOutcome = kdfDeriver.derive(
            password = masterPasswordStr.encodeToByteArray(),
            parameters = header.toKdfParameters(),
        )

        return when (kdfOutcome) {
            is KdfDerivationOutcome.Success -> VaultUnlockResult.Success(kdfOutcome.result)
            is KdfDerivationOutcome.Failure -> VaultUnlockResult.Failure(VaultUnlockError.KdfDerivationFailed)
        }
    }
}
