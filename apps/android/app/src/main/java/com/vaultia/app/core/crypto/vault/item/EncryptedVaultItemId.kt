package com.vaultia.app.core.crypto.vault.item

class EncryptedVaultItemId(
    rawValue: String,
) {
    val value: String = rawValue.trim()

    init {
        require(value.isNotEmpty()) {
            "Encrypted vault item id must not be empty."
        }
        require(value.none { character -> character.isWhitespace() }) {
            "Encrypted vault item id must not contain whitespace."
        }
        require(!value.contains("/")) {
            "Encrypted vault item id must not contain slash separators."
        }
        require(!value.contains("\\")) {
            "Encrypted vault item id must not contain backslash separators."
        }
        require(!value.contains(":")) {
            "Encrypted vault item id must not contain path-like separators."
        }
        require(!value.contains(".")) {
            "Encrypted vault item id must not look like a file path."
        }
    }
}
