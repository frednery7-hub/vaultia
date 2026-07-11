package com.vaultia.app.core.vault

sealed interface VaultRepositoryResult<out T> {
    data class Success<T>(val data: T) : VaultRepositoryResult<T>
    data class Failure(val error: VaultRepositoryError) : VaultRepositoryResult<Nothing>
}
