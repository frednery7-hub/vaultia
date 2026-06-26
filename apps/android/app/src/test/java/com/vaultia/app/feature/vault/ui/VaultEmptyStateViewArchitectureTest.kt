package com.vaultia.app.feature.vault.ui

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultEmptyStateViewArchitectureTest {
    private val emptyStateSource = Paths.get(
        "src/main/java/com/vaultia/app/feature/vault/ui/VaultEmptyStateView.kt",
    ).toFile().readText()

    @Test
    fun emptyStateViewIsReusableAndExplicit() {
        assertTrue(emptyStateSource.contains("object VaultEmptyStateView"))
        assertTrue(emptyStateSource.contains("title: String"))
        assertTrue(emptyStateSource.contains("description: String"))
        assertTrue(emptyStateSource.contains("securityNote: String"))
    }

    @Test
    fun emptyStateViewDoesNotKnowVaultSectionsOrRepositories() {
        assertFalse(emptyStateSource.contains("VaultDestination"))
        assertFalse(emptyStateSource.contains("VaultItem"))
        assertFalse(emptyStateSource.contains("InMemoryVaultRepository"))
        assertFalse(emptyStateSource.contains("InMemoryVaultNavigator"))
        assertFalse(emptyStateSource.contains("InMemorySessionManager"))
    }

    @Test
    fun emptyStateViewDoesNotUseStorageCryptoOrNetworkApis() {
        assertFalse(emptyStateSource.contains("SharedPreferences"))
        assertFalse(emptyStateSource.contains("DataStore"))
        assertFalse(emptyStateSource.contains("SQLite"))
        assertFalse(emptyStateSource.contains("Room"))
        assertFalse(emptyStateSource.contains("File("))
        assertFalse(emptyStateSource.contains("Cipher"))
        assertFalse(emptyStateSource.contains("Keystore"))
        assertFalse(emptyStateSource.contains("Http"))
        assertFalse(emptyStateSource.contains("Socket"))
        assertFalse(emptyStateSource.contains("Retrofit"))
        assertFalse(emptyStateSource.contains("OkHttp"))
    }
}
