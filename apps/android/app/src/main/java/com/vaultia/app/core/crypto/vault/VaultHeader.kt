package com.vaultia.app.core.crypto.vault

import com.vaultia.app.core.crypto.kdf.KdfAlgorithm
import com.vaultia.app.core.crypto.kdf.KdfParameters
import com.vaultia.app.core.crypto.kdf.KdfProfile
import com.vaultia.app.core.crypto.kdf.KdfVersion

class VaultHeader(
    val formatVersion: VaultHeaderVersion,
    val kdfAlgorithm: KdfAlgorithm,
    val kdfVersion: KdfVersion,
    salt: ByteArray,
    val memoryCostKiB: Int,
    val iterations: Int,
    val parallelism: Int,
    val outputLengthBytes: Int,
    val createdAtEpochMillis: Long,
) {
    private val saltBytes: ByteArray = salt.copyOf()

    init {
        require(saltBytes.size >= KdfParameters.MIN_SALT_LENGTH_BYTES) {
            "Vault header salt must be at least ${KdfParameters.MIN_SALT_LENGTH_BYTES} bytes."
        }
        require(memoryCostKiB > 0) {
            "Vault header memory cost must be positive."
        }
        require(iterations > 0) {
            "Vault header iterations must be positive."
        }
        require(parallelism > 0) {
            "Vault header parallelism must be positive."
        }
        require(outputLengthBytes > 0) {
            "Vault header output length must be positive."
        }
        require(createdAtEpochMillis > 0L) {
            "Vault header creation timestamp must be positive."
        }
    }

    fun saltCopy(): ByteArray = saltBytes.copyOf()

    fun toKdfParameters(): KdfParameters {
        return when (kdfAlgorithm) {
            KdfAlgorithm.ARGON2ID -> KdfParameters.Argon2id(
                salt = saltCopy(),
                outputLengthBytes = outputLengthBytes,
                iterations = iterations,
                memoryCostKiB = memoryCostKiB,
                parallelism = parallelism,
            )

            KdfAlgorithm.PBKDF2_HMAC_SHA256 -> KdfParameters.Pbkdf2HmacSha256(
                salt = saltCopy(),
                outputLengthBytes = outputLengthBytes,
                iterations = iterations,
            )
        }
    }

    companion object {
        fun fromProfile(
            profile: KdfProfile,
            salt: ByteArray,
            createdAtEpochMillis: Long,
        ): VaultHeader {
            return VaultHeader(
                formatVersion = VaultHeaderVersion.V1_0,
                kdfAlgorithm = profile.algorithm,
                kdfVersion = profile.kdfVersion,
                salt = salt,
                memoryCostKiB = profile.memoryCostKiB,
                iterations = profile.iterations,
                parallelism = profile.parallelism,
                outputLengthBytes = profile.outputLengthBytes,
                createdAtEpochMillis = createdAtEpochMillis,
            )
        }
    }
}
