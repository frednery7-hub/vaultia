package com.vaultia.app.core.crypto.kdf

object KdfProfileCatalog {
    val FAST: KdfProfile = KdfProfile(
        id = "argon2id-v1-profile-a",
        algorithm = KdfAlgorithm.ARGON2ID,
        kdfVersion = KdfVersion.ARGON2_VERSION_13,
        memoryCostKiB = 32 * 1024,
        iterations = 2,
        parallelism = 1,
        outputLengthBytes = 32,
        recommendedSaltLengthBytes = 32,
    )

    val CONSERVATIVE: KdfProfile = KdfProfile(
        id = "argon2id-v1-profile-b",
        algorithm = KdfAlgorithm.ARGON2ID,
        kdfVersion = KdfVersion.ARGON2_VERSION_13,
        memoryCostKiB = 64 * 1024,
        iterations = 2,
        parallelism = 1,
        outputLengthBytes = 32,
        recommendedSaltLengthBytes = 32,
    )

    fun all(): List<KdfProfile> = listOf(
        FAST,
        CONSERVATIVE,
    )

    fun defaultProfile(): KdfProfile = CONSERVATIVE
}
