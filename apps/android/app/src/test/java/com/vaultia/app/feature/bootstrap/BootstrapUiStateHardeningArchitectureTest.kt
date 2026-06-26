package com.vaultia.app.feature.bootstrap

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BootstrapUiStateHardeningArchitectureTest {
    private val bootstrapScreenSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/bootstrap/ui/BootstrapScreen.kt",
    ).toFile().readText()

    private val uiStateSource = Paths.get(
        "src/main/java/com/vaultia/app/core/session/VaultSessionUiState.kt",
    ).toFile().readText()

    @Test
    fun bootstrapScreenUsesUiStateMapping() {
        assertTrue(bootstrapScreenSource.contains("toUiState()"))
        assertTrue(bootstrapScreenSource.contains("VaultSessionUiState.UNLOCKED"))
    }

    @Test
    fun bootstrapScreenDoesNotHandleNotInitializedDirectly() {
        assertFalse(bootstrapScreenSource.contains("NOT_INITIALIZED"))
    }

    @Test
    fun uiStateMapsNotInitializedAsLocked() {
        assertTrue(uiStateSource.contains("VaultSessionState.NOT_INITIALIZED -> VaultSessionUiState.LOCKED"))
    }

    @Test
    fun uiStateDoesNotUseStorageCryptoOrNetworkApis() {
        assertFalse(uiStateSource.contains("SharedPreferences"))
        assertFalse(uiStateSource.contains("DataStore"))
        assertFalse(uiStateSource.contains("SQLite"))
        assertFalse(uiStateSource.contains("Room"))
        assertFalse(uiStateSource.contains("File("))
        assertFalse(uiStateSource.contains("Cipher"))
        assertFalse(uiStateSource.contains("Keystore"))
        assertFalse(uiStateSource.contains("Http"))
        assertFalse(uiStateSource.contains("Socket"))
    }
}