package com.vaultia.app.core.model.vault

data class VaultItem(
    val id: String,
    val type: VaultItemType,
    val title: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
) {
    init {
        require(id.isNotBlank()) {
            "Vault item id must not be blank."
        }

        require(title.isNotBlank()) {
            "Vault item title must not be blank."
        }

        require(createdAtEpochMillis > 0L) {
            "Vault item creation timestamp must be positive."
        }

        require(updatedAtEpochMillis >= createdAtEpochMillis) {
            "Vault item update timestamp must be greater than or equal to creation timestamp."
        }
    }
}
