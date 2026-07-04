package com.vaultia.app.core.crypto.vault.payload

sealed class VaultPayloadSerializationError {
    data object EmptyInput : VaultPayloadSerializationError()
    data object InvalidMagic : VaultPayloadSerializationError()
    data object MissingField : VaultPayloadSerializationError()
    data object DuplicateField : VaultPayloadSerializationError()
    data object InvalidFieldValue : VaultPayloadSerializationError()
    data object UnsupportedFormatVersion : VaultPayloadSerializationError()
    data object InvalidNonceEncoding : VaultPayloadSerializationError()
    data object InvalidCiphertextEncoding : VaultPayloadSerializationError()
    data object InvalidAuthenticationTagEncoding : VaultPayloadSerializationError()
    data object InvalidEncryptedPayload : VaultPayloadSerializationError()
    data object InvalidTimestamp : VaultPayloadSerializationError()
}
