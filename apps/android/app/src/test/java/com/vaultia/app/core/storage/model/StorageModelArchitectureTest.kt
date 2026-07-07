package com.vaultia.app.core.storage.model

import java.nio.file.Files
import java.nio.file.Path
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageModelArchitectureTest {
    private fun storageModelSourceRoot(): Path {
        val candidates = listOf(
            Path.of("src/main/java/com/vaultia/app/core/storage/model"),
            Path.of("app/src/main/java/com/vaultia/app/core/storage/model"),
            Path.of("apps/android/app/src/main/java/com/vaultia/app/core/storage/model"),
        )

        return candidates.firstOrNull { Files.exists(it) }
            ?: error("Storage model source root not found. Working directory: ${System.getProperty("user.dir")}")
    }

    private fun storageModelSourceFiles(): List<Path> {
        val sourceRoot = storageModelSourceRoot()

        return Files.walk(sourceRoot).use { paths ->
            paths
                .filter { Files.isRegularFile(it) }
                .filter { it.toString().endsWith(".kt") }
                .toList()
        }
    }

    @Test
    fun storageModelContractsDoNotUseAndroidOrPersistenceApis() {
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

        val sourceFiles = storageModelSourceFiles()

        assertTrue(sourceFiles.isNotEmpty())

        sourceFiles.forEach { file ->
            val text = file.toFile().readText()
            forbiddenTokens.forEach { token ->
                assertFalse("$file contains forbidden token $token", text.contains(token))
            }
        }
    }

    @Test
    fun storageModelContractsDoNotExposeSecretFields() {
        val forbiddenFieldTokens = listOf(
            "masterPassword",
            "derivedKey",
            "vaultKey",
            "plainText",
            "plaintext",
            "passwordValue",
            "secretValue",
            "username",
            "noteBody",
            "seedPhrase",
            "documentBytes",
            "photoBytes",
        )

        val sourceFiles = storageModelSourceFiles()

        assertTrue(sourceFiles.isNotEmpty())

        sourceFiles.forEach { file ->
            val text = file.toFile().readText()
            forbiddenFieldTokens.forEach { token ->
                assertFalse("$file contains forbidden secret field token $token", text.contains(token))
            }
        }
    }
}
