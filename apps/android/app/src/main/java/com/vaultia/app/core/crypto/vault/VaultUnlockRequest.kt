package com.vaultia.app.core.crypto.vault

data class VaultUnlockRequest(
    val masterPassword: CharArray,
    val serializedHeader: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VaultUnlockRequest
        if (!masterPassword.contentEquals(other.masterPassword)) return false
        return serializedHeader == other.serializedHeader
    }
    override fun hashCode(): Int {
        var result = masterPassword.contentHashCode()
        result = 31 * result + serializedHeader.hashCode()
        return result
    }
}
