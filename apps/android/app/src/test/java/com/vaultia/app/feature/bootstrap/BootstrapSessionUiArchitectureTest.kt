package com.vaultia.app.feature.bootstrap

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BootstrapSessionUiArchitectureTest {
    private val bootstrapScreenSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/bootstrap/ui/BootstrapScreen.kt",
    ).toFile().readText()

    private val sessionManagerSource = Paths.get(
        "src/main/java/com/vaultia/app/core/session/InMemorySessionManager.kt",
    ).toFile().readText()

    @Test
    fun bootstrapUiUsesExplicitLockedAndUnlockedLabels() {
        assertTrue(bootstrapScreenSource.contains("cofre bloqueado"))
        assertTrue(bootstrapScreenSource.contains("cofre desbloqueado"))
    }

    @Test
    fun unlockActionIsMarkedAsSimulated() {
        assertTrue(bootstrapScreenSource.contains("Desbloquear simulado"))
    }

    @Test
    fun sessionManagerDoesNotUsePersistenceApis() {
        assertFalse(sessionManagerSource.contains("SharedPreferences"))
        assertFalse(sessionManagerSource.contains("DataStore"))
        assertFalse(sessionManagerSource.contains("SQLite"))
        assertFalse(sessionManagerSource.contains("Room"))
        assertFalse(sessionManagerSource.contains("File("))
    }

    @Test
    fun sessionManagerDoesNotClaimRealAuthenticationOrCryptography() {
        assertFalse(sessionManagerSource.contains("password"))
        assertFalse(sessionManagerSource.contains("senha"))
        assertFalse(sessionManagerSource.contains("biometric"))
        assertFalse(sessionManagerSource.contains("AES"))
        assertFalse(sessionManagerSource.contains("Keystore"))
    }
}
