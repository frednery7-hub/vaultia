package com.vaultia.app.core.app

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultiaAppContainerArchitectureTest {
    private val appContainerSource = Paths.get(
        "src/main/java/com/vaultia/app/core/app/VaultiaAppContainer.kt",
    ).toFile().readText()

    @Test
    fun appContainerOwnsLocalDependencies() {
        assertTrue(appContainerSource.contains("InMemorySessionManager"))
        assertTrue(appContainerSource.contains("InMemoryVaultRepository"))
        assertTrue(appContainerSource.contains("InMemoryVaultNavigator"))
        assertTrue(appContainerSource.contains("DemoVaultMetadataSeed"))
    }

    @Test
    fun appContainerDoesNotUseExternalDependencyInjectionFrameworks() {
        assertFalse(appContainerSource.contains("Hilt"))
        assertFalse(appContainerSource.contains("Dagger"))
        assertFalse(appContainerSource.contains("Koin"))
        assertFalse(appContainerSource.contains("@Inject"))
        assertFalse(appContainerSource.contains("@Module"))
    }

    @Test
    fun appContainerDoesNotUseStorageCryptoOrNetworkApis() {
        assertFalse(appContainerSource.contains("SharedPreferences"))
        assertFalse(appContainerSource.contains("DataStore"))
        assertFalse(appContainerSource.contains("SQLite"))
        assertFalse(appContainerSource.contains("Room"))
        assertFalse(appContainerSource.contains("File("))
        assertFalse(appContainerSource.contains("Cipher"))
        assertFalse(appContainerSource.contains("Keystore"))
        assertFalse(appContainerSource.contains("Http"))
        assertFalse(appContainerSource.contains("Socket"))
        assertFalse(appContainerSource.contains("Retrofit"))
        assertFalse(appContainerSource.contains("OkHttp"))
    }
}
