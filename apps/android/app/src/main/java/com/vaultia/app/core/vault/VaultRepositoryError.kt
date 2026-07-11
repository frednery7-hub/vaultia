package com.vaultia.app.core.vault

enum class VaultRepositoryError {
    SerializationFailed,
    EncryptionFailed,
    DecryptionFailed,
    StorageFailed,
    ItemNotFound,
    InvalidKey,
    CorruptedData
}
