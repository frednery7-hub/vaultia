package com.vaultia.app.core.storage.usecase

import java.nio.file.Files
import java.nio.file.Path
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageUseCaseArchitectureTest {
    private fun useCaseSourceRoot(): Path {
        val candidates = listOf(
            Path.of("src/main/java/com/vaultia/app/core/storage/usecase"),
            Path.of("app/src/main/java/com/vaultia/app/core/storage/usecase"),
            Path.of("apps/android/app/src/main/java/com/vaultia/app/core/storage/usecase"),
        )

        return candidates.firstOrNull { Files.exists(it) }
            ?: error("Storage use case source root not found. Working directory: ${System.getProperty("user.dir")}")
    }

    private fun useCaseSourceFiles(): List<Path> {
        val sourceRoot = useCaseSourceRoot()

        return Files.walk(sourceRoot).use { paths ->
            paths
                .filter { Files.isRegularFile(it) }
                .filter { it.toString().endsWith(".kt") }
                .toList()
        }
    }

    @Test
    fun useCasesDoNotUseAndroidOrPersistenceApis() {
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

        val sourceFiles = useCaseSourceFiles()

        assertTrue(sourceFiles.isNotEmpty())

        sourceFiles.forEach { file ->
            val text = file.toFile().readText()
            forbiddenTokens.forEach { token ->
                assertFalse("$file contains forbidden token $token", text.contains(token))
            }
        }
    }

    @Test
    fun useCasesDoNotExposeSecretsOrLowLevelOperations() {
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

        val sourceFiles = useCaseSourceFiles()

        assertTrue(sourceFiles.isNotEmpty())

        sourceFiles.forEach { file ->
            val text = file.toFile().readText()
            forbiddenTokens.forEach { token ->
                assertFalse("$file contains forbidden token $token", text.contains(token, ignoreCase = true))
            }
        }
    }
}
