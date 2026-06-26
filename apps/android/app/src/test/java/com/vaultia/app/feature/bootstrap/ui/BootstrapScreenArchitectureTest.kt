package com.vaultia.app.feature.bootstrap.ui

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BootstrapScreenArchitectureTest {
    private val bootstrapSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/bootstrap/ui/BootstrapScreen.kt",
    ).toFile().readText()

    @Test
    fun bootstrapScreenDelegatesToLockedAndUnlockedScreens() {
        assertTrue(bootstrapSource.contains("LockedVaultScreen.create"))
        assertTrue(bootstrapSource.contains("UnlockedVaultScreen.create"))
    }

    @Test
    fun bootstrapScreenStillOrchestratesVaultSessionUiState() {
        assertTrue(bootstrapSource.contains("VaultSessionUiState.LOCKED"))
        assertTrue(bootstrapSource.contains("VaultSessionUiState.UNLOCKED"))
        assertTrue(bootstrapSource.contains("toUiState()"))
    }

    @Test
    fun bootstrapScreenCoordinatesLocalNavigatorOnly() {
        assertTrue(bootstrapSource.contains("InMemoryVaultNavigator"))
        assertTrue(bootstrapSource.contains("vaultNavigator.current()"))
        assertTrue(bootstrapSource.contains("vaultNavigator.navigateTo(destination)"))
        assertTrue(bootstrapSource.contains("vaultNavigator.resetToHome()"))
    }

    @Test
    fun bootstrapScreenDoesNotBuildLowLevelVaultUiDirectly() {
        assertFalse(bootstrapSource.contains("TextView(context)"))
        assertFalse(bootstrapSource.contains("Button(context)"))
        assertFalse(bootstrapSource.contains("LinearLayout(context)"))
        assertFalse(bootstrapSource.contains("VaultHomeShell.create"))
    }

    @Test
    fun bootstrapScreenDoesNotUseStorageCryptoOrNetworkApis() {
        assertFalse(bootstrapSource.contains("SharedPreferences"))
        assertFalse(bootstrapSource.contains("DataStore"))
        assertFalse(bootstrapSource.contains("SQLite"))
        assertFalse(bootstrapSource.contains("Room"))
        assertFalse(bootstrapSource.contains("File("))
        assertFalse(bootstrapSource.contains("Cipher"))
        assertFalse(bootstrapSource.contains("Keystore"))
        assertFalse(bootstrapSource.contains("Http"))
        assertFalse(bootstrapSource.contains("Socket"))
        assertFalse(bootstrapSource.contains("Retrofit"))
        assertFalse(bootstrapSource.contains("OkHttp"))
    }
}
