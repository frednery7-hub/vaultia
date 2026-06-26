package com.vaultia.app.feature.vault.ui

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultScreenArchitectureTest {
    private val lockedScreenSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/vault/ui/LockedVaultScreen.kt",
    ).toFile().readText()

    private val unlockedScreenSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/vault/ui/UnlockedVaultScreen.kt",
    ).toFile().readText()

    @Test
    fun lockedScreenContainsOnlyLockedUiConcern() {
        assertTrue(lockedScreenSource.contains("object LockedVaultScreen"))
        assertTrue(lockedScreenSource.contains("onUnlockRequested"))
        assertTrue(lockedScreenSource.contains("Estado: cofre bloqueado"))
        assertFalse(lockedScreenSource.contains("VaultHomeShell"))
        assertFalse(lockedScreenSource.contains("InMemorySessionManager"))
        assertFalse(lockedScreenSource.contains("InMemoryVaultRepository"))
    }

    @Test
    fun unlockedScreenContainsOnlyUnlockedUiConcern() {
        assertTrue(unlockedScreenSource.contains("object UnlockedVaultScreen"))
        assertTrue(unlockedScreenSource.contains("VaultHomeShell.create"))
        assertTrue(unlockedScreenSource.contains("onLockRequested"))
        assertFalse(unlockedScreenSource.contains("InMemorySessionManager"))
        assertFalse(unlockedScreenSource.contains("InMemoryVaultRepository"))
    }

    @Test
    fun separatedVaultScreensDoNotUseStorageCryptoOrNetworkApis() {
        val combinedSource = lockedScreenSource + unlockedScreenSource

        assertFalse(combinedSource.contains("SharedPreferences"))
        assertFalse(combinedSource.contains("DataStore"))
        assertFalse(combinedSource.contains("SQLite"))
        assertFalse(combinedSource.contains("Room"))
        assertFalse(combinedSource.contains("File("))
        assertFalse(combinedSource.contains("Cipher"))
        assertFalse(combinedSource.contains("Keystore"))
        assertFalse(combinedSource.contains("Http"))
        assertFalse(combinedSource.contains("Socket"))
        assertFalse(combinedSource.contains("Retrofit"))
        assertFalse(combinedSource.contains("OkHttp"))
    }
}