package com.vaultia.app.core.crypto.vault

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Test

class VaultHeaderArchitectureTest {
    @Test
    fun vaultHeaderSourcesDoNotImportAndroidUiStorageOrCryptoImplementations() {
        val sourceFiles = File("src/main/java/com/vaultia/app/core/crypto/vault")
            .walkTopDown()
            .filter { file -> file.isFile && file.extension == "kt" }
            .toList()

        val forbiddenPatterns = listOf(
            "import android.",
            "import androidx.",
            "import com.vaultia.app.feature.",
            "import com.vaultia.app.core.storage",
            "import androidx.room",
            "import androidx.datastore",
            "SQLite",
            "Room",
            "DataStore",
            "SecureRandom",
            "Argon2Kt",
            "Argon2Mode",
            "Cipher",
            "SecretKey",
            "KeyStore",
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
