package com.vaultia.app.core.storage.service

import java.nio.file.Files
import java.nio.file.Path
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageServiceArchitectureTest {
    private fun serviceSourceRoot(): Path {
        val candidates = listOf(
            Path.of("src/main/java/com/vaultia/app/core/storage/service"),
            Path.of("app/src/main/java/com/vaultia/app/core/storage/service"),
            Path.of("apps/android/app/src/main/java/com/vaultia/app/core/storage/service"),
        )

        return candidates.firstOrNull { Files.exists(it) }
            ?: error("Storage service source root not found. Working directory: ${System.getProperty("user.dir")}")
    }

    private fun serviceSourceFiles(): List<Path> {
        val sourceRoot = serviceSourceRoot()

        return Files.walk(sourceRoot).use { paths ->
            paths
                .filter { Files.isRegularFile(it) }
                .filter { it.toString().endsWith(".kt") }
                .toList()
        }
    }

    @Test
    fun serviceDoesNotUseAndroidPersistenceOrUiApis() {
        val forbiddenTokens = listOf(
            "import android.",
            "import androidx.compose.",
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
            "Composable",
            "Modifier",
        )

        val sourceFiles = serviceSourceFiles()

        assertTrue(sourceFiles.isNotEmpty())

        sourceFiles.forEach { file ->
            val text = file.toFile().readText()
            forbiddenTokens.forEach { token ->
                assertFalse("$file contains forbidden token $token", text.contains(token))
            }
        }
    }

    @Test
    fun serviceDoesNotExposeSecretsOrLowLevelOperations() {
        val forbiddenTokens = listOf(
            "masterPassword",
            "derivedKey",
            "vaultKey",
            "plainText",
            "plaintext",
            "passwordValue",
            "secretValue",
            "documentBytes",
            "photoBytes",
            "decrypt",
            "encrypt",
            "cipher",
            "kdf",
            "argon",
            "keystore",
            "biometric",
        )

        val sourceFiles = serviceSourceFiles()

        assertTrue(sourceFiles.isNotEmpty())

        sourceFiles.forEach { file ->
            val text = file.toFile().readText()
            forbiddenTokens.forEach { token ->
                assertFalse("$file contains forbidden token $token", text.contains(token, ignoreCase = true))
            }
        }
    }
}
