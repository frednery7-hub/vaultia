package com.vaultia.app.core.crypto.vault.payload

enum class VaultPayloadSerializedFormatVersion(
    val encodedValue: Int,
) {
    V1_0(encodedValue = 1),
    ;

    companion object {
        fun fromEncodedValue(encodedValue: Int): VaultPayloadSerializedFormatVersion? {
            return entries.firstOrNull { version -> version.encodedValue == encodedValue }
        }
    }
}
