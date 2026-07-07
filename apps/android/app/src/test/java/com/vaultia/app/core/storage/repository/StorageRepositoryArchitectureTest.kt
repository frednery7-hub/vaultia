package com.vaultia.app.core.storage.repository

import java.nio.file.Files
import java.nio.file.Path
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageRepositoryArchitectureTest {
    private fun repositorySourceRoot(): Path {
        val candidates = listOf(
            Path.of("src/main/java/com/vaultia/app/core/storage/repository"),
            Path.of("app/src/main/java/com/vaultia/app/core/storage/repository"),
            Path.of("apps/android/app/src/main/java/com/vaultia/app/core/storage/repository"),
        )

        return candidates.firstOrNull { Files.exists(it) }
            ?: error("Storage repository source root not found. Working directory: ${System.getProperty("user.dir")}")
    }

    private fun repositorySourceFiles(): List<Path> {
        val sourceRoot = repositorySourceRoot()

        return Files.walk(sourceRoot).use { paths ->
            paths
                .filter { Files.isRegularFile(it) }
                .filter { it.toString().endsWith(".kt") }
                .toList()
        }
    }

    @Test
    fun repositoryDoesNotUseAndroidOrPersistenceApis() {
        val forbiddenTokens = listOf(
            "import android.",
            "import androidx.room.",
            "import java.io.",
            "import kotlin.io.",
            "SQLite",
            "Room",
            "DataStore",
            "SharedPreferences",
            "File(",
            "Files.",
            "Path.",
        )

        val sourceFiles = repositorySourceFiles()

        assertTrue(sourceFiles.isNotEmpty())

        sourceFiles.forEach { file ->
            val text = file.toFile().readText()
            forbiddenTokens.forEach { token ->
                assertFalse("$file contains forbidden token $token", text.contains(token))
            }
        }
    }

    @Test
    fun repositoryDoesNotExposeSecretsOrCryptoOperations() {
        val forbiddenTokens = listOf(
            "masterPassword",
            "derivedKey",
            "vaultKey",
            "plainText",
            "plaintext",
            "passwordValue",
            "secretValue",
            "decrypt",
            "encrypt",
            "cipher",
            "kdf",
            "argon",
            "keystore",
            "biometric",
        )

        val sourceFiles = repositorySourceFiles()

        assertTrue(sourceFiles.isNotEmpty())

        sourceFiles.forEach { file ->
            val text = file.toFile().readText()
            forbiddenTokens.forEach { token ->
                assertFalse("$file contains forbidden token $token", text.contains(token, ignoreCase = true))
            }
        }
    }
}
