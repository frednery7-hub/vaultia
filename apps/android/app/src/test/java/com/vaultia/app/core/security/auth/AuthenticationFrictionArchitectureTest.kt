package com.vaultia.app.core.security.auth

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthenticationFrictionArchitectureTest {
    @Test
    fun authenticationFrictionContractsDoNotUseAndroidStorageCryptoOrLogging() {
        val sourceFiles = File("src/main/java/com/vaultia/app/core/security/auth")
            .walkTopDown()
            .filter { file -> file.isFile && file.extension == "kt" }
            .toList()

        assertTrue(sourceFiles.isNotEmpty())

        val forbiddenPatterns = listOf(
            "import android.",
            "import androidx.",
            "import com.vaultia.app.feature.",
            "import com.vaultia.app.core.crypto",
            "import com.vaultia.app.core.storage",
            "java.io",
            "java.nio",
            "SQLite",
            "Room",
            "DataStore",
            "SharedPreferences",
            "KeyStore",
            "javax.crypto",
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

    @Test
    fun authenticationFrictionContractsDoNotCarrySensitiveValues() {
        val sourceFiles = File("src/main/java/com/vaultia/app/core/security/auth")
            .walkTopDown()
            .filter { file -> file.isFile && file.extension == "kt" }
            .toList()

        val forbiddenPatterns = listOf(
            "masterPasswordValue",
            "derivedKey",
            "vaultKey",
            "plaintext",
            "ciphertext",
            "authenticationTag",
            "nonce",
            "serializedPayload",
            "secretValue",
            "passwordValue",
        )

        for (file in sourceFiles) {
            val text = file.readText()
            forbiddenPatterns.forEach { forbiddenPattern ->
                assertFalse(
                    "Sensitive term $forbiddenPattern found in ${file.path}",
                    text.contains(forbiddenPattern),
                )
            }
        }
    }
}
