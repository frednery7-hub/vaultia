package com.vaultia.app.core.crypto.vault.item

enum class VaultItemSerializedFormatVersion(
    val encodedValue: Int,
) {
    V1_0(encodedValue = 1),
    ;

    companion object {
        fun fromEncodedValue(encodedValue: Int): VaultItemSerializedFormatVersion? {
            return entries.firstOrNull { version -> version.encodedValue == encodedValue }
        }
    }
}
