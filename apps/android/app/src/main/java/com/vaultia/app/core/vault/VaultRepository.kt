package com.vaultia.app.core.vault

import com.vaultia.app.core.crypto.kdf.KdfResult
import com.vaultia.app.core.model.payload.VaultItemPayload
import com.vaultia.app.core.model.vault.VaultItem

interface VaultRepository {
    fun saveItem(id: String?, payload: VaultItemPayload, sessionKey: KdfResult): VaultRepositoryResult<VaultItem>
    fun readItem(id: String, sessionKey: KdfResult): VaultRepositoryResult<Pair<VaultItem, VaultItemPayload>>
    fun listItems(sessionKey: KdfResult): VaultRepositoryResult<List<VaultItem>>
    fun deleteItem(id: String): VaultRepositoryResult<Boolean>
}
