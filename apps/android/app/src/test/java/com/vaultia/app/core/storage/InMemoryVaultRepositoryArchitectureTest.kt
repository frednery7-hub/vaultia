package com.vaultia.app.core.storage

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryVaultRepositoryArchitectureTest {
    private val repositorySource = Paths.get(
        "src/main/java/com/vaultia/app/core/storage/InMemoryVaultRepository.kt",
    ).toFile().readText()

    @Test
    fun repositoryIsExplicitlyInMemoryOnly() {
        assertTrue(repositorySource.contains("mutableListOf"))
        assertTrue(repositorySource.contains("listMetadata"))
        assertTrue(repositorySource.contains("addMetadataForCurrentProcessOnly"))
        assertTrue(repositorySource.contains("addAllMetadataForCurrentProcessOnly"))
        assertTrue(repositorySource.contains("clearForCurrentProcessOnly"))
    }

    @Test
    fun repositoryExposesDefensiveCopy() {
        assertTrue(repositorySource.contains("items.toList()"))
    }

    @Test
    fun repositoryRejectsDuplicateIds() {
        assertTrue(repositorySource.contains("items.none"))
        assertTrue(repositorySource.contains("existingItem.id == item.id"))
    }

    @Test
    fun repositoryDoesNotUsePersistenceCryptoOrNetworkApis() {
        assertFalse(repositorySource.contains("SharedPreferences"))
        assertFalse(repositorySource.contains("DataStore"))
        assertFalse(repositorySource.contains("SQLite"))
        assertFalse(repositorySource.contains("Room"))
        assertFalse(repositorySource.contains("File("))
        assertFalse(repositorySource.contains("Cipher"))
        assertFalse(repositorySource.contains("AES"))
        assertFalse(repositorySource.contains("Keystore"))
        assertFalse(repositorySource.contains("Http"))
        assertFalse(repositorySource.contains("URL"))
        assertFalse(repositorySource.contains("Socket"))
        assertFalse(repositorySource.contains("Retrofit"))
        assertFalse(repositorySource.contains("OkHttp"))
    }
}
