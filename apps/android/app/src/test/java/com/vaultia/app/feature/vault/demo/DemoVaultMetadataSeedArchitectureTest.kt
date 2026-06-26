package com.vaultia.app.feature.vault.demo

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoVaultMetadataSeedArchitectureTest {
    private val seedSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/vault/demo/DemoVaultMetadataSeed.kt",
    ).toFile().readText()

    private val appContainerSource = Paths.get(
        "src/main/java/com/vaultia/app/core/app/VaultiaAppContainer.kt",
    ).toFile().readText()

    private val mainActivitySource = Paths.get(
        "src/main/java/com/vaultia/app/MainActivity.kt",
    ).toFile().readText()

    @Test
    fun seedIsExplicitlyMetadataOnly() {
        assertTrue(seedSource.contains("metadataOnlyItems"))
        assertTrue(seedSource.contains("VaultItem"))
        assertTrue(seedSource.contains("Demo senha"))
        assertTrue(seedSource.contains("Demo nota"))
        assertTrue(seedSource.contains("Demo documento"))
    }

    @Test
    fun seedDoesNotContainSensitivePayloadFields() {
        assertFalse(seedSource.contains("passwordValue"))
        assertFalse(seedSource.contains("noteBody"))
        assertFalse(seedSource.contains("secret"))
        assertFalse(seedSource.contains("payload"))
        assertFalse(seedSource.contains("content"))
        assertFalse(seedSource.contains("cipher"))
        assertFalse(seedSource.contains("bytes"))
        assertFalse(seedSource.contains("path"))
        assertFalse(seedSource.contains("uri"))
        assertFalse(seedSource.contains("key"))
        assertFalse(seedSource.contains("fileName"))
        assertFalse(seedSource.contains("mimeType"))
    }

    @Test
    fun seedDoesNotUseStorageCryptoOrNetworkApis() {
        assertFalse(seedSource.contains("SharedPreferences"))
        assertFalse(seedSource.contains("DataStore"))
        assertFalse(seedSource.contains("SQLite"))
        assertFalse(seedSource.contains("Room"))
        assertFalse(seedSource.contains("File("))
        assertFalse(seedSource.contains("Cipher"))
        assertFalse(seedSource.contains("Keystore"))
        assertFalse(seedSource.contains("Http"))
        assertFalse(seedSource.contains("Socket"))
    }

    @Test
    fun appContainerLoadsDemoSeedIntoInMemoryRepositoryOnly() {
        assertTrue(appContainerSource.contains("DemoVaultMetadataSeed.metadataOnlyItems()"))
        assertTrue(appContainerSource.contains("addAllMetadataForCurrentProcessOnly"))
        assertTrue(appContainerSource.contains("InMemoryVaultRepository"))
    }

    @Test
    fun mainActivityDoesNotLoadDemoSeedDirectly() {
        assertTrue(mainActivitySource.contains("VaultiaAppContainer"))
        assertFalse(mainActivitySource.contains("DemoVaultMetadataSeed"))
        assertFalse(mainActivitySource.contains("metadataOnlyItems"))
        assertFalse(mainActivitySource.contains("addAllMetadataForCurrentProcessOnly"))
    }
}