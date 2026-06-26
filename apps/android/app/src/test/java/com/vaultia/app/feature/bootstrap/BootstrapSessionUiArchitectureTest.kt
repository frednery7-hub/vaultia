package com.vaultia.app.feature.bootstrap

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BootstrapSessionUiArchitectureTest {
    private val bootstrapSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/bootstrap/ui/BootstrapScreen.kt",
    ).toFile().readText()

    private val lockedScreenSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/vault/ui/LockedVaultScreen.kt",
    ).toFile().readText()

    private val unlockedScreenSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/vault/ui/UnlockedVaultScreen.kt",
    ).toFile().readText()

    @Test
    fun bootstrapDelegatesLockedAndUnlockedStatesToDedicatedScreens() {
        assertTrue(bootstrapSource.contains("VaultSessionUiState.LOCKED"))
        assertTrue(bootstrapSource.contains("VaultSessionUiState.UNLOCKED"))
        assertTrue(bootstrapSource.contains("LockedVaultScreen.create"))
        assertTrue(bootstrapSource.contains("UnlockedVaultScreen.create"))
    }

    @Test
    fun lockedScreenKeepsSimulatedUnlockActionExplicit() {
        assertTrue(lockedScreenSource.contains("Desbloquear simulado"))
        assertTrue(lockedScreenSource.contains("onUnlockRequested"))
        assertTrue(lockedScreenSource.contains("Estado: cofre bloqueado"))
    }

    @Test
    fun unlockedScreenKeepsLockActionExplicit() {
        assertTrue(unlockedScreenSource.contains("Bloquear"))
        assertTrue(unlockedScreenSource.contains("onLockRequested"))
    }

    @Test
    fun bootstrapDoesNotContainLowLevelSessionLabelsAnymore() {
        assertFalse(bootstrapSource.contains("Desbloquear simulado"))
        assertFalse(bootstrapSource.contains("Estado: cofre bloqueado"))
        assertFalse(bootstrapSource.contains("Bloquear"))
    }
}