package com.vaultia.app.feature.vault

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultHomeShellArchitectureTest {
    private val vaultHomeShellSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/vault/ui/VaultHomeShell.kt",
    ).toFile().readText()

    private val bootstrapSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/bootstrap/ui/BootstrapScreen.kt",
    ).toFile().readText()

    private val unlockedScreenSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/vault/ui/UnlockedVaultScreen.kt",
    ).toFile().readText()

    @Test
    fun vaultHomeShellUsesMetadataOnlyItems() {
        assertTrue(vaultHomeShellSource.contains("List<VaultItem>"))
        assertTrue(vaultHomeShellSource.contains("VaultItemType.PASSWORD"))
        assertTrue(vaultHomeShellSource.contains("VaultItemType.NOTE"))
        assertTrue(vaultHomeShellSource.contains("VaultItemType.PHOTO"))
        assertTrue(vaultHomeShellSource.contains("VaultItemType.DOCUMENT"))
    }

    @Test
    fun vaultHomeShellDoesNotUseSensitivePayloadFields() {
        assertFalse(vaultHomeShellSource.contains("passwordValue"))
        assertFalse(vaultHomeShellSource.contains("noteBody"))
        assertFalse(vaultHomeShellSource.contains("secret"))
        assertFalse(vaultHomeShellSource.contains("payload"))
        assertFalse(vaultHomeShellSource.contains("cipher"))
        assertFalse(vaultHomeShellSource.contains("bytes"))
        assertFalse(vaultHomeShellSource.contains("fileName"))
        assertFalse(vaultHomeShellSource.contains("mimeType"))
    }

    @Test
    fun unlockedScreenMountsVaultHomeShell() {
        assertTrue(unlockedScreenSource.contains("VaultHomeShell.create"))
        assertTrue(unlockedScreenSource.contains("items = items"))
    }

    @Test
    fun bootstrapScreenDoesNotMountVaultHomeShellDirectly() {
        assertFalse(bootstrapSource.contains("VaultHomeShell.create"))
        assertTrue(bootstrapSource.contains("UnlockedVaultScreen.create"))
    }

    @Test
    fun vaultHomeShellDoesNotUseStorageCryptoOrNetworkApis() {
        assertFalse(vaultHomeShellSource.contains("SharedPreferences"))
        assertFalse(vaultHomeShellSource.contains("DataStore"))
        assertFalse(vaultHomeShellSource.contains("SQLite"))
        assertFalse(vaultHomeShellSource.contains("Room"))
        assertFalse(vaultHomeShellSource.contains("File("))
        assertFalse(vaultHomeShellSource.contains("Cipher"))
        assertFalse(vaultHomeShellSource.contains("Keystore"))
        assertFalse(vaultHomeShellSource.contains("Http"))
        assertFalse(vaultHomeShellSource.contains("Socket"))
        assertFalse(vaultHomeShellSource.contains("Retrofit"))
        assertFalse(vaultHomeShellSource.contains("OkHttp"))
    }
}