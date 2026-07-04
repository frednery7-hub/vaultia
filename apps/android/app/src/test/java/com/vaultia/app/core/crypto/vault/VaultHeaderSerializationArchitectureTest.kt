package com.vaultia.app.core.crypto.vault

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Test

class VaultHeaderSerializationArchitectureTest {
    @Test
    fun vaultHeaderSerializationSourcesDoNotImportAndroidStorageIoOrCryptoFrameworks() {
        val sourceFiles = File("src/main/java/com/vaultia/app/core/crypto/vault")
            .walkTopDown()
            .filter { file -> file.isFile && file.extension == "kt" }
            .filter { file -> file.name.contains("Serialization") || file.name == "VaultHeaderSerializer.kt" }
            .toList()

        val forbiddenPatterns = listOf(
            "import android.",
            "import androidx.",
            "import com.vaultia.app.feature.",
            "import com.vaultia.app.core.storage",
            "import java.io.",
            "import java.nio.",
            "Room",
            "DataStore",
            "SQLite",
            "SecureRandom",
            "Argon2Kt",
            "Argon2Mode",
            "Cipher",
            "SecretKey",
            "KeyStore",
            "File(",
            "println",
            "Log.",
        )

        for (file in sourceFiles) {
            val text = file.readText()
            forbiddenPatterns.forEach { forbiddenPattern ->
                assertFalse(
                    "Forbidden dependency $forbiddenPattern found in ${file.path}",
                    text.contains(forbiddenPattern),
                )
            }
        }
    }
}
