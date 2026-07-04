package com.vaultia.app.core.crypto.vault

sealed class VaultHeaderSerializationError {
    data object EmptyInput : VaultHeaderSerializationError()
    data object InvalidMagic : VaultHeaderSerializationError()
    data object MissingField : VaultHeaderSerializationError()
    data object DuplicateField : VaultHeaderSerializationError()
    data object InvalidFieldValue : VaultHeaderSerializationError()
    data object UnsupportedFormatVersion : VaultHeaderSerializationError()
    data object UnsupportedKdfAlgorithm : VaultHeaderSerializationError()
    data object UnsupportedKdfVersion : VaultHeaderSerializationError()
    data object InvalidSaltEncoding : VaultHeaderSerializationError()
    data object InvalidHeader : VaultHeaderSerializationError()
}
