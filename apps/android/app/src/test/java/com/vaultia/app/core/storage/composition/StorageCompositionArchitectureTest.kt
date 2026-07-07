package com.vaultia.app.core.storage.composition

import java.nio.file.Files
import java.nio.file.Path
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageCompositionArchitectureTest {
    private fun compositionSourceRoot(): Path {
        val candidates = listOf(
            Path.of("src/main/java/com/vaultia/app/core/storage/composition"),
            Path.of("app/src/main/java/com/vaultia/app/core/storage/composition"),
            Path.of("apps/android/app/src/main/java/com/vaultia/app/core/storage/composition"),
        )

        return candidates.firstOrNull { Files.exists(it) }
            ?: error("Storage composition source root not found. Working directory: ${System.getProperty("user.dir")}")
    }

    private fun compositionSourceFiles(): List<Path> {
        val sourceRoot = compositionSourceRoot()

        return Files.walk(sourceRoot).use { paths ->
            paths
                .filter { Files.isRegularFile(it) }
                .filter { it.toString().endsWith(".kt") }
                .toList()
        }
    }

    @Test
    fun compositionDoesNotUseAndroidPersistenceOrUiApis() {
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

        val sourceFiles = compositionSourceFiles()

        assertTrue(sourceFiles.isNotEmpty())

        sourceFiles.forEach { file ->
            val text = file.toFile().readText()
            forbiddenTokens.forEach { token ->
                assertFalse("$file contains forbidden token $token", text.contains(token))
            }
        }
    }

    @Test
    fun compositionDoesNotExposeSecretsOrLowLevelOperations() {
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

        val sourceFiles = compositionSourceFiles()

        assertTrue(sourceFiles.isNotEmpty())

        sourceFiles.forEach { file ->
            val text = file.toFile().readText()
            forbiddenTokens.forEach { token ->
                assertFalse("$file contains forbidden token $token", text.contains(token, ignoreCase = true))
            }
        }
    }
}
