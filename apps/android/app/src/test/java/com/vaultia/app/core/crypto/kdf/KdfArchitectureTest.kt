package com.vaultia.app.core.crypto.kdf

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Test

class KdfArchitectureTest {
    @Test
    fun kdfMainSourcesDoNotImportAndroidUiStorageOrArgon2Implementation() {
        val kdfSourceFiles = File("src/main/java/com/vaultia/app/core/crypto/kdf")
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
            "com.lambdapioneer.argon2kt",
            "Argon2Kt",
        )

        for (file in kdfSourceFiles) {
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
