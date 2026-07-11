package com.vaultia.app.core.model.payload

enum class VaultItemPayloadSerializationError {
    EmptyInput,
    InvalidMagic,
    InvalidPayloadType,
    InvalidFormatVersion,
    MissingField,
    InvalidFieldValue,
    UnsupportedPayloadType
}
