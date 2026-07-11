package com.vaultia.app.core.app

import java.nio.file.Files
import java.nio.file.Path
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultiaAppContainerArchitectureTest {
    private fun appSourceRoot(): Path {
        val candidates = listOf(
            Path.of("src/main/java/com/vaultia/app/core/app"),
            Path.of("app/src/main/java/com/vaultia/app/core/app"),
            Path.of("apps/android/app/src/main/java/com/vaultia/app/core/app"),
        )

        return candidates.firstOrNull { Files.exists(it) }
            ?: error("Vaultia app source root not found. Working directory: ${System.getProperty("user.dir")}")
    }

    private fun appSourceFiles(): List<Path> {
        val sourceRoot = appSourceRoot()

        return Files.walk(sourceRoot).use { paths ->
            paths
                .filter { Files.isRegularFile(it) }
                .filter { it.toString().endsWith(".kt") }
                .filter { !it.toString().endsWith("VaultiaApp.kt") }
                .toList()
        }
    }

    @Test
    fun appContainerDoesNotUsePersistenceOrUiApis() {
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

        val sourceFiles = appSourceFiles()

        assertTrue(sourceFiles.isNotEmpty())

        sourceFiles.forEach { file ->
            val text = file.toFile().readText()
            forbiddenTokens.forEach { token ->
                assertFalse("$file contains forbidden token $token", text.contains(token))
            }
        }
    }

    @Test
    fun appContainerDoesNotExposeSecrets() {
        val forbiddenTokens = listOf(
            "masterPassword",
            "plainText",
            "plaintext",
            "passwordValue",
            "secretValue",
            "documentBytes",
            "photoBytes",
            "keystore",
            "biometric",
        )

        val sourceFiles = appSourceFiles()

        assertTrue(sourceFiles.isNotEmpty())

        sourceFiles.forEach { file ->
            val text = file.toFile().readText()
            forbiddenTokens.forEach { token ->
                assertFalse("$file contains forbidden token $token", text.contains(token, ignoreCase = true))
            }
        }
    }
}
