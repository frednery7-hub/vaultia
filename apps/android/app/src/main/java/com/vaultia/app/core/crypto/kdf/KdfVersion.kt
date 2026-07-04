package com.vaultia.app.core.crypto.kdf

enum class KdfVersion(
    val encodedValue: Int,
) {
    ARGON2_VERSION_13(encodedValue = 0x13),
}
