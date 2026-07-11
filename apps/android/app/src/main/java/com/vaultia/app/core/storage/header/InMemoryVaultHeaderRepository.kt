package com.vaultia.app.core.storage.header

class InMemoryVaultHeaderRepository : VaultHeaderRepository {
    private var header: String? = null
    override fun saveHeader(serializedHeader: String): Boolean {
        header = serializedHeader; return true
    }
    override fun readHeader(): String? = header
    override fun deleteHeader(): Boolean {
        if (header == null) return false
        header = null; return true
    }
}
