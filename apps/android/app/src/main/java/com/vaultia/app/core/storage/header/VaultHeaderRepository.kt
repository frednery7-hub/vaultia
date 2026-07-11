package com.vaultia.app.core.storage.header

interface VaultHeaderRepository {
    fun saveHeader(serializedHeader: String): Boolean
    fun readHeader(): String?
    fun deleteHeader(): Boolean
}
