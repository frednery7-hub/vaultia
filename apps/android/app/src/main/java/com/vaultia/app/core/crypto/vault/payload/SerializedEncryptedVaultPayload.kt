package com.vaultia.app.core.crypto.vault.payload

class SerializedEncryptedVaultPayload(
    val formatVersion: VaultPayloadSerializedFormatVersion,
    val nonceHex: String,
    val ciphertextHex: String,
    val authenticationTagHex: String,
    val createdAtEpochMillis: Long,
) {
    init {
        require(nonceHex.isValidHexWithByteLength(NONCE_LENGTH_BYTES)) {
            "Serialized vault payload nonce must be valid 12-byte hex."
        }
        require(ciphertextHex.isValidNonEmptyHex()) {
            "Serialized vault payload ciphertext must be non-empty valid hex."
        }
        require(authenticationTagHex.isValidHexWithByteLength(AUTHENTICATION_TAG_LENGTH_BYTES)) {
            "Serialized vault payload authentication tag must be valid 16-byte hex."
        }
        require(createdAtEpochMillis > 0L) {
            "Serialized vault payload timestamp must be positive."
        }
    }

    companion object {
        const val NONCE_LENGTH_BYTES: Int = 12
        const val AUTHENTICATION_TAG_LENGTH_BYTES: Int = 16

        private fun String.isValidHexWithByteLength(byteLength: Int): Boolean {
            return length == byteLength * 2 && isValidNonEmptyHex()
        }

        private fun String.isValidNonEmptyHex(): Boolean {
            return isNotEmpty() && length % 2 == 0 && all { character -> character.isHexCharacter() }
        }

        private fun Char.isHexCharacter(): Boolean {
            return this in '0'..'9' || this in 'a'..'f' || this in 'A'..'F'
        }
    }
}
