package com.vaultia.app.core.crypto.salt

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SaltGenerationArchitectureTest {
    @Test
    fun saltSourcesDoNotImportAndroidStorageIoCryptoOrLoggingFrameworks() {
        val sourceFiles = File("src/main/java/com/vaultia/app/core/crypto/salt")
            .walkTopDown()
            .filter { file -> file.isFile && file.extension == "kt" }
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
            "SharedPreferences",
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

    @Test
    fun secureRandomIsUsedOnlyByConcreteSaltGenerator() {
        val sourceFiles = File("src/main/java/com/vaultia/app/core/crypto/salt")
            .walkTopDown()
            .filter { file -> file.isFile && file.extension == "kt" }
            .toList()

        for (file in sourceFiles) {
            val text = file.readText()
            if (file.name == "SecureRandomSaltGenerator.kt") {
                assertTrue(text.contains("import java.security.SecureRandom"))
                assertTrue(text.contains("secureRandom.nextBytes"))
            } else {
                assertFalse(
                    "SecureRandom must only be used by SecureRandomSaltGenerator.kt",
                    text.contains("SecureRandom"),
                )
            }
        }
    }
}
