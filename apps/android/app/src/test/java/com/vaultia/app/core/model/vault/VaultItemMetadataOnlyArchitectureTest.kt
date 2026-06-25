package com.vaultia.app.core.model.vault

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultItemMetadataOnlyArchitectureTest {
    private val vaultItemSource = Paths.get(
        "src/main/java/com/vaultia/app/core/model/vault/VaultItem.kt",
    ).toFile().readText()

    @Test
    fun vaultItemContainsOnlyAllowedMetadataFields() {
        assertTrue(vaultItemSource.contains("val id: String"))
        assertTrue(vaultItemSource.contains("val type: VaultItemType"))
        assertTrue(vaultItemSource.contains("val title: String"))
        assertTrue(vaultItemSource.contains("val createdAtEpochMillis: Long"))
        assertTrue(vaultItemSource.contains("val updatedAtEpochMillis: Long"))
    }

    @Test
    fun vaultItemDoesNotContainSensitivePayloadFields() {
        assertFalse(vaultItemSource.contains("secret"))
        assertFalse(vaultItemSource.contains("payload"))
        assertFalse(vaultItemSource.contains("content"))
        assertFalse(vaultItemSource.contains("plain"))
        assertFalse(vaultItemSource.contains("cipher"))
        assertFalse(vaultItemSource.contains("bytes"))
        assertFalse(vaultItemSource.contains("path"))
        assertFalse(vaultItemSource.contains("uri"))
        assertFalse(vaultItemSource.contains("key"))
        assertFalse(vaultItemSource.contains("passwordValue"))
        assertFalse(vaultItemSource.contains("noteBody"))
        assertFalse(vaultItemSource.contains("fileName"))
        assertFalse(vaultItemSource.contains("mimeType"))
    }

    @Test
    fun vaultItemDoesNotUseStorageCryptoOrNetworkApis() {
        assertFalse(vaultItemSource.contains("SharedPreferences"))
        assertFalse(vaultItemSource.contains("DataStore"))
        assertFalse(vaultItemSource.contains("SQLite"))
        assertFalse(vaultItemSource.contains("Room"))
        assertFalse(vaultItemSource.contains("File("))
        assertFalse(vaultItemSource.contains("Cipher"))
        assertFalse(vaultItemSource.contains("Keystore"))
        assertFalse(vaultItemSource.contains("Http"))
        assertFalse(vaultItemSource.contains("Socket"))
    }
}
