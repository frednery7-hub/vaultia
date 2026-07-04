package com.vaultia.app.core.crypto.vault

import com.vaultia.app.core.crypto.kdf.KdfDerivationOutcome
import com.vaultia.app.core.crypto.kdf.KdfDeriver
import com.vaultia.app.core.crypto.kdf.KdfProfileCatalog
import com.vaultia.app.core.crypto.salt.SaltGenerationResult
import com.vaultia.app.core.crypto.salt.SaltGenerator
import com.vaultia.app.core.security.password.MasterPasswordPolicy

class VaultCreationService(
    private val kdfDeriver: KdfDeriver,
    private val saltGenerator: SaltGenerator? = null,
    private val headerEncoder: (VaultHeader) -> String = VaultHeaderSerializer::encode,
    private val headerDecoder: (String) -> VaultHeaderSerializationResult = VaultHeaderSerializer::decode,
) {
    fun create(request: VaultCreationRequest): VaultCreationResult {
        val passwordValidation = MasterPasswordPolicy.validate(request.masterPassword)
        if (!passwordValidation.isValid) {
            return VaultCreationResult.Failure(VaultCreationError.InvalidMasterPassword)
        }

        val profile = KdfProfileCatalog.defaultProfile()

        val header = try {
            VaultHeader.fromProfile(
                profile = profile,
                salt = request.saltCopy(),
                createdAtEpochMillis = request.createdAtEpochMillis,
            )
        } catch (_: IllegalArgumentException) {
            return VaultCreationResult.Failure(VaultCreationError.InvalidCreationRequest)
        }

        val kdfOutcome = kdfDeriver.derive(
            password = request.masterPassword.encodeToByteArray(),
            parameters = header.toKdfParameters(),
        )

        val kdfResult = when (kdfOutcome) {
            is KdfDerivationOutcome.Success -> kdfOutcome.result
            is KdfDerivationOutcome.Failure -> {
                return VaultCreationResult.Failure(VaultCreationError.KdfDerivationFailed)
            }
        }

        val serializedHeader = try {
            headerEncoder(header)
        } catch (_: Throwable) {
            return VaultCreationResult.Failure(VaultCreationError.HeaderSerializationFailed)
        }

        val decodedHeader = headerDecoder(serializedHeader)
        if (decodedHeader !is VaultHeaderSerializationResult.Success) {
            return VaultCreationResult.Failure(VaultCreationError.HeaderSerializationFailed)
        }

        return VaultCreationResult.Success(
            CreatedVaultDraft(
                header = header,
                serializedHeader = serializedHeader,
                kdfResult = kdfResult,
            ),
        )
    }

    fun createWithGeneratedSalt(
        request: VaultCreationWithoutExternalSaltRequest,
    ): VaultCreationResult {
        val passwordValidation = MasterPasswordPolicy.validate(request.masterPassword)
        if (!passwordValidation.isValid) {
            return VaultCreationResult.Failure(VaultCreationError.InvalidMasterPassword)
        }

        val generator = saltGenerator
            ?: return VaultCreationResult.Failure(VaultCreationError.SaltGenerationFailed)

        val saltResult = generator.generateRecommendedSalt()
        val salt = when (saltResult) {
            is SaltGenerationResult.Success -> saltResult.saltCopy()
            is SaltGenerationResult.Failure -> {
                return VaultCreationResult.Failure(VaultCreationError.SaltGenerationFailed)
            }
        }

        return create(
            VaultCreationRequest(
                masterPassword = request.masterPassword,
                salt = salt,
                createdAtEpochMillis = request.createdAtEpochMillis,
            ),
        )
    }
}
