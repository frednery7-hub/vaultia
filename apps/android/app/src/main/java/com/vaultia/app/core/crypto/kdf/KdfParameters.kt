package com.vaultia.app.core.crypto.kdf

sealed class KdfParameters(
    val algorithm: KdfAlgorithm,
    salt: ByteArray,
    val outputLengthBytes: Int,
    val iterations: Int,
) {
    private val saltBytes: ByteArray = salt.copyOf()

    init {
        require(saltBytes.size >= MIN_SALT_LENGTH_BYTES) {
            "KDF salt must be at least $MIN_SALT_LENGTH_BYTES bytes."
        }
        require(outputLengthBytes > 0) {
            "KDF output length must be positive."
        }
        require(iterations > 0) {
            "KDF iterations must be positive."
        }
    }

    fun saltCopy(): ByteArray = saltBytes.copyOf()

    class Argon2id(
        salt: ByteArray,
        outputLengthBytes: Int,
        iterations: Int,
        val memoryCostKiB: Int,
        val parallelism: Int,
    ) : KdfParameters(
        algorithm = KdfAlgorithm.ARGON2ID,
        salt = salt,
        outputLengthBytes = outputLengthBytes,
        iterations = iterations,
    ) {
        init {
            require(memoryCostKiB > 0) {
                "Argon2id memory cost must be positive."
            }
            require(parallelism > 0) {
                "Argon2id parallelism must be positive."
            }
        }
    }

    class Pbkdf2HmacSha256(
        salt: ByteArray,
        outputLengthBytes: Int,
        iterations: Int,
    ) : KdfParameters(
        algorithm = KdfAlgorithm.PBKDF2_HMAC_SHA256,
        salt = salt,
        outputLengthBytes = outputLengthBytes,
        iterations = iterations,
    )

    companion object {
        const val MIN_SALT_LENGTH_BYTES: Int = 16
    }
}
