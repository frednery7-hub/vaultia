package com.vaultia.app.core.crypto.vault.payload

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Test

class VaultPayloadSerializationArchitectureTest {
    @Test
    fun payloadSerializationSourcesDoNotImportAndroidStorageIoCryptoOrLoggingFrameworks() {
        val sourceFiles = File("src/main/java/com/vaultia/app/core/crypto/vault/payload")
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
            "KeyStore",
            "javax.crypto.",
            "SecureRandom",
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
