package com.vaultia.app.core.crypto.vault

enum class VaultHeaderVersion(
    val encodedValue: Int,
    val major: Int,
    val minor: Int,
) {
    V1_0(
        encodedValue = 0x00010000,
        major = 1,
        minor = 0,
    ),
}
