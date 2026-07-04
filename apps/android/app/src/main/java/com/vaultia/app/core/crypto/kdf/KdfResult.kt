package com.vaultia.app.core.crypto.kdf

class KdfResult(
    derivedKey: ByteArray,
    val parameters: KdfParameters,
) {
    private val derivedKeyBytes: ByteArray = derivedKey.copyOf()

    init {
        require(derivedKeyBytes.isNotEmpty()) {
            "Derived key must not be empty."
        }
    }

    val derivedKeyLengthBytes: Int
        get() = derivedKeyBytes.size

    fun derivedKeyCopy(): ByteArray = derivedKeyBytes.copyOf()
}
