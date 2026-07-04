package com.vaultia.app.core.crypto.kdf

data class KdfProfile(
    val id: String,
    val algorithm: KdfAlgorithm,
    val kdfVersion: KdfVersion,
    val memoryCostKiB: Int,
    val iterations: Int,
    val parallelism: Int,
    val outputLengthBytes: Int,
    val recommendedSaltLengthBytes: Int,
) {
    init {
        require(id.isNotBlank()) {
            "KDF profile id must not be blank."
        }
        require(memoryCostKiB > 0) {
            "KDF profile memory cost must be positive."
        }
        require(iterations > 0) {
            "KDF profile iterations must be positive."
        }
        require(parallelism > 0) {
            "KDF profile parallelism must be positive."
        }
        require(outputLengthBytes > 0) {
            "KDF profile output length must be positive."
        }
        require(recommendedSaltLengthBytes >= KdfParameters.MIN_SALT_LENGTH_BYTES) {
            "KDF profile recommended salt length must be at least ${KdfParameters.MIN_SALT_LENGTH_BYTES} bytes."
        }
    }

    fun toArgon2idParameters(salt: ByteArray): KdfParameters.Argon2id {
        require(algorithm == KdfAlgorithm.ARGON2ID) {
            "KDF profile algorithm must be ARGON2ID to build Argon2id parameters."
        }

        return KdfParameters.Argon2id(
            salt = salt,
            outputLengthBytes = outputLengthBytes,
            iterations = iterations,
            memoryCostKiB = memoryCostKiB,
            parallelism = parallelism,
        )
    }
}
