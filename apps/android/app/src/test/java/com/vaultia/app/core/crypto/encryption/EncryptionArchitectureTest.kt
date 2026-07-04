package com.vaultia.app.core.crypto.encryption

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EncryptionArchitectureTest {
    @Test
    fun encryptionSourcesDoNotImportAndroidStorageIoKeystoreOrLoggingFrameworks() {
        val sourceFiles = File("src/main/java/com/vaultia/app/core/crypto/encryption")
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
    fun aesGcmImplementationUsesExpectedCipherBoundary() {
        val source = File(
            "src/main/java/com/vaultia/app/core/crypto/encryption/AesGcmAuthenticatedCipher.kt",
        ).readText()

        assertTrue(source.contains("AES/GCM/NoPadding"))
        assertTrue(source.contains("GCMParameterSpec"))
        assertTrue(source.contains("SecureRandom"))
        assertTrue(source.contains("AEADBadTagException"))
    }
}
