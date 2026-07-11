package com.vaultia.app.core.storage.local.header

import com.vaultia.app.core.storage.header.VaultHeaderRepository
import java.io.File

class FileVaultHeaderRepository(private val baseDirectory: File) : VaultHeaderRepository {
    private val file = File(baseDirectory, "vault_header.dat")
    init { if (!baseDirectory.exists()) baseDirectory.mkdirs() }
    
    override fun saveHeader(serializedHeader: String): Boolean {
        return try { file.writeText(serializedHeader, Charsets.UTF_8); true } catch (e: Exception) { false }
    }
    override fun readHeader(): String? {
        if (!file.exists()) return null
        return try { file.readText(Charsets.UTF_8) } catch (e: Exception) { null }
    }
    override fun deleteHeader(): Boolean {
        if (!file.exists()) return false
        return file.delete()
    }
}
