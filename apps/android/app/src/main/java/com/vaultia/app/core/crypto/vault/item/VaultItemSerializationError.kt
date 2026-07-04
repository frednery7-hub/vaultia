package com.vaultia.app.core.crypto.vault.item

sealed class VaultItemSerializationError {
    data object EmptyInput : VaultItemSerializationError()
    data object InvalidMagic : VaultItemSerializationError()
    data object MissingField : VaultItemSerializationError()
    data object DuplicateField : VaultItemSerializationError()
    data object InvalidFieldValue : VaultItemSerializationError()
    data object UnsupportedFormatVersion : VaultItemSerializationError()
    data object UnsupportedItemType : VaultItemSerializationError()
    data object InvalidId : VaultItemSerializationError()
    data object InvalidTimestamp : VaultItemSerializationError()
    data object InvalidPayload : VaultItemSerializationError()
    data object InvalidItem : VaultItemSerializationError()
}
