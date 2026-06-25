package com.vaultia.app.core.model.vault

/**
 * Metadata-only representation of a future vault item.
 *
 * This model intentionally excludes secret payload fields.
 *
 * Rules:
 * - no password value;
 * - no note body;
 * - no document bytes;
 * - no photo bytes;
 * - no decrypted content.
 */
internal data class VaultItem(
    val id: String,
    val type: VaultItemType,
    val title: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
