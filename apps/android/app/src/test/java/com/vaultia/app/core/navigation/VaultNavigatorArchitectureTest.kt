package com.vaultia.app.core.navigation

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultNavigatorArchitectureTest {
    private val navigatorSource = Paths.get(
        "src/main/java/com/vaultia/app/core/navigation/InMemoryVaultNavigator.kt",
    ).toFile().readText()

    private val destinationSource = Paths.get(
        "src/main/java/com/vaultia/app/core/navigation/VaultDestination.kt",
    ).toFile().readText()

    @Test
    fun navigatorIsExplicitlyInMemoryOnly() {
        assertTrue(navigatorSource.contains("class InMemoryVaultNavigator"))
        assertTrue(navigatorSource.contains("private var currentDestination"))
        assertTrue(navigatorSource.contains("VaultDestination.HOME"))
    }

    @Test
    fun destinationContainsVaultSectionsOnly() {
        assertTrue(destinationSource.contains("HOME"))
        assertTrue(destinationSource.contains("PASSWORDS"))
        assertTrue(destinationSource.contains("NOTES"))
        assertTrue(destinationSource.contains("PHOTOS"))
        assertTrue(destinationSource.contains("DOCUMENTS"))
    }

    @Test
    fun navigationDoesNotUseStorageCryptoOrNetworkApis() {
        val combinedSource = navigatorSource + destinationSource

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
