package com.vaultia.app.feature.vault

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultHomeShellArchitectureTest {
    private val vaultHomeShellSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/vault/ui/VaultHomeShell.kt",
    ).toFile().readText()

    private val bootstrapScreenSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/bootstrap/ui/BootstrapScreen.kt",
    ).toFile().readText()

    @Test
    fun vaultHomeShellShowsCategories() {
        assertTrue(vaultHomeShellSource.contains("Senhas"))
        assertTrue(vaultHomeShellSource.contains("Notas"))
        assertTrue(vaultHomeShellSource.contains("Fotos"))
        assertTrue(vaultHomeShellSource.contains("Documentos"))
        assertTrue(vaultHomeShellSource.contains("Nenhum item salvo nesta fase."))
    }

    @Test
    fun vaultHomeShellUsesMetadataOnlyInput() {
        assertTrue(vaultHomeShellSource.contains("items: List<VaultItem>"))
        assertTrue(vaultHomeShellSource.contains("VaultItemType"))
        assertTrue(vaultHomeShellSource.contains("countItems"))
    }

    @Test
    fun vaultHomeShellDoesNotUseStorageOrCryptoApis() {
        assertFalse(vaultHomeShellSource.contains("SharedPreferences"))
        assertFalse(vaultHomeShellSource.contains("DataStore"))
        assertFalse(vaultHomeShellSource.contains("SQLite"))
        assertFalse(vaultHomeShellSource.contains("Room"))
        assertFalse(vaultHomeShellSource.contains("File("))
        assertFalse(vaultHomeShellSource.contains("Cipher"))
        assertFalse(vaultHomeShellSource.contains("AES"))
        assertFalse(vaultHomeShellSource.contains("Keystore"))
    }

    @Test
    fun vaultHomeShellDoesNotUseNetworkApis() {
        assertFalse(vaultHomeShellSource.contains("Http"))
        assertFalse(vaultHomeShellSource.contains("URL"))
        assertFalse(vaultHomeShellSource.contains("Socket"))
        assertFalse(vaultHomeShellSource.contains("Retrofit"))
        assertFalse(vaultHomeShellSource.contains("OkHttp"))
    }

    @Test
    fun bootstrapScreenMountsVaultHomeShellOnlyWhenUnlocked() {
        assertTrue(bootstrapScreenSource.contains("VaultHomeShell.create(context, vaultRepository.listMetadata())"))
        assertTrue(bootstrapScreenSource.contains("if (isUnlocked)"))
    }
}
